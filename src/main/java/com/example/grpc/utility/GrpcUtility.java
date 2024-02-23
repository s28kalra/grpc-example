package com.example.grpc.utility;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.JavaType;
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

//	some modifications are required
	public static <T extends Message.Builder> T mapToProto(Map<String, Object> map, T builder) {
		Class<?> builderClass = builder.getClass();

		for (Map.Entry<String, Object> entry : map.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();

			try {
				// Get the setter method for the field
				Method setter = getSetterMethod(builderClass, key);
				if (setter != null) {
					// Convert value if necessary
					Object convertedValue = convertToFieldType(setter.getParameterTypes()[0], value);
					// Invoke the setter method to set the field
					setter.invoke(builder, convertedValue);
				}
			} catch (IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace(); // Handle exceptions properly
			}
		}

		return builder;
	}

	// Retrieve the setter method for the field
	private static Method getSetterMethod(Class<?> clazz, String fieldName) {
		String setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		for (Method method : clazz.getMethods()) {
			if (method.getName().equals(setterName) && method.getParameterCount() == 1) {
				return method;
			}
		}
		return null;
	}

	// Convert value to the type expected by the setter method
	private static Object convertToFieldType(Class fieldType, Object value) {
		if (fieldType == String.class) {
			return value.toString();
		} else if (fieldType == Integer.class || fieldType == int.class) {
			return Integer.parseInt(value.toString());
		} else if (fieldType == Long.class || fieldType == long.class) {
			return Long.parseLong(value.toString());
		} else if (fieldType == Double.class || fieldType == double.class) {
			return Double.parseDouble(value.toString());
		} else if (fieldType.isEnum()) {
			return Enum.valueOf(fieldType, value.toString());
		} else if (fieldType == Integer.class || fieldType == int.class) {
			return Integer.parseInt(value.toString());
		}
		// Add more conversions as needed
		return value;
	}

}
