package com.joo.eBook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class EBookApplication {

	public static void main(String[] args) {
		SpringApplication.run(EBookApplication.class, args);
	}

}
