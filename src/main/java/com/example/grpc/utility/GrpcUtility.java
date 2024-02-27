package com.example.grpc.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.JavaType;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.DynamicMessage.Builder;
import com.google.protobuf.Message;

public class GrpcUtility {

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getJsonMap(Message protoObject) {
		Map<String, Object> map = new LinkedHashMap<>();

		List<FieldDescriptor> descriptors = protoObject.getDescriptorForType().getFields();

		for (FieldDescriptor descriptor : descriptors) {
			String fieldName = descriptor.getName();
			Object value = null;
			if (descriptor.isRepeated()) {
				List<Object> list = (List<Object>) protoObject.getField(descriptor);
				List<Object> newJsonList = new ArrayList<>();
				for (Object o : list) {
					if (o instanceof Message x) {
						newJsonList.add(getJsonMap(x));
					} else {
						newJsonList.add(o);
					}
					value = newJsonList;
				}
			} else if (descriptor.getJavaType() == JavaType.MESSAGE) {
				value = getJsonMap((Message) (protoObject.getField(descriptor)));
			} else if (descriptor.getJavaType() == JavaType.ENUM) {
				value = protoObject.getField(descriptor).toString();
			} else {
				value = protoObject.getField(descriptor);
			}
			map.put(fieldName, value);
		}
		return map;
	}

	public static <T extends Message.Builder> T mapToProto(Map<String, Object> map, T builder) {
		List<FieldDescriptor> descriptors = builder.getDescriptorForType().getFields();
		for (FieldDescriptor descriptor : descriptors) {
			try {
				Object value = map.get(descriptor.getName());
				if (descriptor.isRepeated() && value instanceof Collection collection) {
					for (Object o : collection) {
						builder.addRepeatedField(descriptor, convertToFieldType(descriptor, o));
					}

				} else {
					builder.setField(descriptor, convertToFieldType(descriptor, value));
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return builder;
	}

	@SuppressWarnings({ "unchecked" })
	private static Object convertToFieldType(FieldDescriptor descriptor, Object value) {
		switch (descriptor.getJavaType()) {
		case INT:
			return Integer.valueOf(value.toString());
		case LONG:
			return Long.valueOf(value.toString());
		case FLOAT:
			return Float.valueOf(value.toString());
		case DOUBLE:
			return Double.valueOf(value.toString());
		case BOOLEAN:
			return Boolean.valueOf(value.toString());
		case STRING:
			return value.toString();
		case ENUM: {
			return descriptor.getEnumType().findValueByName(value.toString());
		}
		case MESSAGE: {
			if (value instanceof Map map) {
				Builder builder = DynamicMessage.newBuilder(descriptor.getMessageType());
				return mapToProto(map, builder).build();
			}
		}

		default:
			return null;
		}
	}

}
