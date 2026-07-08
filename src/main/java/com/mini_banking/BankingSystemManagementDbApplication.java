package com.mini_banking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BankingSystemManagementDbApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankingSystemManagementDbApplication.class, args);
	}

}
