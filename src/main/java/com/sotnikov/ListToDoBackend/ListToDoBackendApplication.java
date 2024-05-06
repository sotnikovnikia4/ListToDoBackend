package com.sotnikov.ListToDoBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class ListToDoBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ListToDoBackendApplication.class, args);
	}

}
