package com.example.grpc.server.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.example.grpc.server.service.CalculatorService;

import io.grpc.ServerBuilder;
import jakarta.annotation.PostConstruct;

@Configuration
public class GrpcServerConfig {

	@Value("${calculator.grpc.server.port}")
	private int calculatorGrpcServerPort;

	@Autowired
	private CalculatorService calculatorService;

	@PostConstruct
	public void startGrpcServer() {
		try {
			ServerBuilder.forPort(calculatorGrpcServerPort).addService(calculatorService).build().start();
			System.out.println("Calculator GRPC Server started on: " + calculatorGrpcServerPort);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
