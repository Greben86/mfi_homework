package com.mfi_homework.mfi_homework;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class MfiHomeworkApplication {

	public static void main(String[] args) {
		SpringApplication.run(MfiHomeworkApplication.class, args);
	}

}
