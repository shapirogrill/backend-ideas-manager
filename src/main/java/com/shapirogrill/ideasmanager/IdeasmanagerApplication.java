package com.shapirogrill.ideasmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class IdeasmanagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(IdeasmanagerApplication.class, args);
	}

}
