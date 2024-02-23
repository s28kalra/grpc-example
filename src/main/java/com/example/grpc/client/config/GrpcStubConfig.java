package com.example.grpc.client.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.kotlin.grpc.config.CalculatorServiceGrpc;
import com.example.kotlin.grpc.config.CalculatorServiceGrpc.CalculatorServiceBlockingStub;
import com.example.kotlin.grpc.config.CalculatorServiceGrpc.CalculatorServiceStub;

import io.grpc.ManagedChannel;

@Configuration
public class GrpcStubConfig {

	@Autowired
	private ManagedChannel calculatorChannel;

	@Bean("calculatorServiceBlockingStub")
	public CalculatorServiceBlockingStub getCalculatorServiceBlockingStub() {
		return CalculatorServiceGrpc.newBlockingStub(calculatorChannel);
	}

	@Bean("calculatorServiceStub")
	public CalculatorServiceStub getCalculatorServiceStub() {
		return CalculatorServiceGrpc.newStub(calculatorChannel);
	}

}
