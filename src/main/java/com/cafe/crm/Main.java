package com.cafe.crm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


@SpringBootApplication
@EnableAsync
public class Main extends WebMvcConfigurerAdapter {
	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}
}
