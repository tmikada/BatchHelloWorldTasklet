package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BatchHelloWorldTaskletApplication {

	public static void main(String[] args) {
		try {
			SpringApplication.run(BatchHelloWorldTaskletApplication.class, args);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(100);
		}
	}

}
