/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */
package io.litmusblox.server;

import io.litmusblox.server.security.JwtConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Main application class
 *
 * @author : Shital Raval
 * Date : 26/6/19
 * Time : 11:51 AM
 * Class Name : ServerApplication
 * Project Name : server
 */

@SpringBootApplication
public class ServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}

	@Bean
	public JwtConfig jwtConfig() {
		return new JwtConfig();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
