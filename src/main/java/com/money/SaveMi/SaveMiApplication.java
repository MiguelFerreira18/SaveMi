package com.money.SaveMi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SaveMiApplication {
	@Value("${app.profile:profile}")
	private static String profile;

	public static void main(String[] args) {
		SpringApplication.run(SaveMiApplication.class, args);

		System.out.println("-------------------PROFILE----------------------");
		System.out.println(profile);
		System.out.println("------------------------------------------------");
	}

}
