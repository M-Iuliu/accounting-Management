package com.accounting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AccountingAndManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountingAndManagementSystemApplication.class, args);
	}

}
