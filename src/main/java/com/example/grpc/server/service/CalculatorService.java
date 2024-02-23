package com.example.grpc.server.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.kotlin.grpc.config.CalculatorRequest;
import com.example.kotlin.grpc.config.CalculatorResponse;
import com.example.kotlin.grpc.config.CalculatorServiceGrpc.CalculatorServiceImplBase;
import com.example.kotlin.grpc.config.DoubleCalculatorResponse;
import com.example.kotlin.grpc.config.OneNumberRequest;
import com.example.kotlin.grpc.config.Status;
import com.example.kotlin.grpc.config.StreamCalculatorResponse;

import io.grpc.stub.StreamObserver;

@Service
public class CalculatorService extends CalculatorServiceImplBase {

	private static final String SUCCESS = "SUCCESS";

	@Override
	public void add(CalculatorRequest request, StreamObserver<CalculatorResponse> responseObserver) {
		CalculatorResponse.Builder response = CalculatorResponse.newBuilder();
		response.setResult(request.getFirst() + request.getSecond());
		response.setStatus(Status.SUCCESS);
		response.setMessage(SUCCESS);
		responseObserver.onNext(response.build());
		responseObserver.onCompleted();
	}

	@Override
	public void subtract(CalculatorRequest request, StreamObserver<CalculatorResponse> responseObserver) {
		CalculatorResponse.Builder response = CalculatorResponse.newBuilder();
		response.setResult(request.getFirst() - request.getSecond());
		response.setStatus(Status.SUCCESS);
		response.setMessage(SUCCESS);
		responseObserver.onNext(response.build());
		responseObserver.onCompleted();
	}

	@Override
	public void multiply(CalculatorRequest request, StreamObserver<CalculatorResponse> responseObserver) {
		CalculatorResponse.Builder response = CalculatorResponse.newBuilder();
		response.setResult(request.getFirst() - request.getSecond());
		response.setStatus(Status.SUCCESS);
		response.setMessage(SUCCESS);
		responseObserver.onNext(response.build());
		responseObserver.onCompleted();
	}

	@Override
	public void divide(CalculatorRequest request, StreamObserver<CalculatorResponse> responseObserver) {
		CalculatorResponse.Builder response = CalculatorResponse.newBuilder();
		try {
			response.setResult(request.getFirst() / request.getSecond());
			response.setStatus(Status.SUCCESS);
			response.setMessage(SUCCESS);
		} catch (Exception e) {
			response.setStatus(Status.FAILURE);
			response.setMessage(e.getMessage());
		}
		responseObserver.onNext(response.build());
		responseObserver.onCompleted();
	}

	private boolean isPrime(int x) {
		int i = 2;
		while (i * i <= x) {
			if (x % i == 0)
				return false;
			i++;
		}
		return true;
	}

	@Override
	public void getStreamOfPrimeNumbers(OneNumberRequest request,
			StreamObserver<StreamCalculatorResponse> responseObserver) {
		for (int i = 2; i <= request.getNum(); i++) {
			if (isPrime(i)) {
				responseObserver.onNext(StreamCalculatorResponse.newBuilder().setResult(i)
						.setTime(LocalDateTime.now().toString()).build());
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		responseObserver.onCompleted();
	}

	@Override
	public StreamObserver<OneNumberRequest> calculateAverage(
			StreamObserver<DoubleCalculatorResponse> responseObserver) {

		StreamObserver<OneNumberRequest> streamObserver = new StreamObserver<OneNumberRequest>() {
			double totalSum = 0.0;
			int totalSize = 0;

			@Override
			public void onNext(OneNumberRequest value) {
				totalSum += value.getNum();
				totalSize++;
			}

			@Override
			public void onError(Throwable t) {
				responseObserver.onError(t);
			}

			@Override
			public void onCompleted() {
				responseObserver.onCompleted();
			}
		};

		return streamObserver;
	}

	@Override
	public StreamObserver<OneNumberRequest> squares(StreamObserver<StreamCalculatorResponse> responseObserver) {

		StreamObserver<OneNumberRequest> streamObserver = new StreamObserver<OneNumberRequest>() {

			@Override
			public void onNext(OneNumberRequest value) {
				double result = ((double) (value.getNum()) * value.getNum());
				responseObserver.onNext(StreamCalculatorResponse.newBuilder().setResult(result)
						.setTime(LocalDateTime.now().toString()).build());
			}

			@Override
			public void onError(Throwable t) {
				responseObserver.onError(t);

			}

			@Override
			public void onCompleted() {
				responseObserver.onCompleted();
			}
		};

		return streamObserver;
	}

}
