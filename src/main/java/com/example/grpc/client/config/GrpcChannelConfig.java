package com.example.grpc.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

@Configuration
public class GrpcChannelConfig {

	@Value("${calculator.grpc.server.port}")
	private int calculatorGrpcServerPort;

	@Bean("calculatorChannel")
	public ManagedChannel getCalculatorChannel() {
		return ManagedChannelBuilder.forAddress(null, calculatorGrpcServerPort).build();
	}

}
