package com.example.grpc.client.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.grpc.utility.GrpcUtility;
import com.example.kotlin.grpc.config.CalculatorRequest;
import com.example.kotlin.grpc.config.CalculatorResponse;
import com.example.kotlin.grpc.config.CalculatorServiceGrpc.CalculatorServiceBlockingStub;
import com.example.kotlin.grpc.config.CalculatorServiceGrpc.CalculatorServiceStub;
import com.example.kotlin.grpc.config.DoubleCalculatorResponse;
import com.example.kotlin.grpc.config.OneNumberRequest;
import com.example.kotlin.grpc.config.StreamCalculatorResponse;

import io.grpc.stub.StreamObserver;

@Service
public class ClientCalculatorService {

	@Autowired
	private CalculatorServiceBlockingStub calculatorServiceBlockingStub;

	@Autowired
	private CalculatorServiceStub calculatorServiceStub;

//	Unidirectional
	public void add(int a, int b) {
		CalculatorRequest request = getRequest(a, b);
		CalculatorResponse response = calculatorServiceBlockingStub.add(request);
		System.out.println(GrpcUtility.getJsonMap(response));
	}

//	Unidirectional
	public void subtract(int a, int b) {
		CalculatorRequest request = getRequest(a, b);
		CalculatorResponse response = calculatorServiceBlockingStub.subtract(request);
		System.out.println(GrpcUtility.getJsonMap(response));
	}

//	Unidirectional
	public void multiply(int a, int b) {
		CalculatorRequest request = getRequest(a, b);
		CalculatorResponse response = calculatorServiceBlockingStub.multiply(request);
		System.out.println(GrpcUtility.getJsonMap(response));
	}

//	Unidirectional
	public void divide(int a, int b) {
		CalculatorRequest request = getRequest(a, b);
		CalculatorResponse response = calculatorServiceBlockingStub.divide(request);
		System.out.println(GrpcUtility.getJsonMap(response));
	}

	private CalculatorRequest getRequest(int a, int b) {
		return CalculatorRequest.newBuilder().setFirst(a).setSecond(b).build();
	}

//	ServerStreaming
	public void getStreamOfPrimeNumbers(int num) {
		StreamObserver<StreamCalculatorResponse> streamObserver = new StreamObserver<StreamCalculatorResponse>() {

			@Override
			public void onNext(StreamCalculatorResponse value) {
				System.out.println("Server Response: " + GrpcUtility.getJsonMap(value));
			}

			@Override
			public void onError(Throwable t) {

			}

			@Override
			public void onCompleted() {

			}
		};
		OneNumberRequest request = OneNumberRequest.newBuilder().setNum(num).build();
		calculatorServiceStub.getStreamOfPrimeNumbers(request, streamObserver);
	}

//	ClientStreaming
	public void calculateAverage(Collection<Integer> listOfNumbers) {
		StreamObserver<OneNumberRequest> requests = calculatorServiceStub
				.calculateAverage(new StreamObserver<DoubleCalculatorResponse>() {

					@Override
					public void onNext(DoubleCalculatorResponse value) {
						System.out.println("Average Response: " + GrpcUtility.getJsonMap(value));
					}

					@Override
					public void onError(Throwable t) {

					}

					@Override
					public void onCompleted() {

					}
				});

		for (Integer i : listOfNumbers)
			requests.onNext(OneNumberRequest.newBuilder().setNum(i).build());
		requests.onCompleted();
	}

//	BiDirectionalStreaming
	public void squares(Collection<Integer> listOfNumbers) {
		StreamObserver<OneNumberRequest> requests = calculatorServiceStub
				.squares(new StreamObserver<StreamCalculatorResponse>() {

					@Override
					public void onNext(StreamCalculatorResponse value) {
						System.out.println("Squares Response: " + GrpcUtility.getJsonMap(value));
					}

					@Override
					public void onError(Throwable t) {

					}

					@Override
					public void onCompleted() {

					}
				});

		for (Integer i : listOfNumbers)
			requests.onNext(OneNumberRequest.newBuilder().setNum(i).build());
		requests.onCompleted();
	}

}
