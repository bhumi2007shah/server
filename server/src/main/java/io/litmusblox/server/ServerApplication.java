/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */
package io.litmusblox.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class
 *
 * @author : Shital Raval
 * Date : 26/6/19
 * Time : 11:51 AM
 * Class Name : ServerApplication
 * Project Name : server
 */
@PropertySource("classpath:appConfig.properties")
@EnableConfigurationProperties
@EnableScheduling
@SpringBootApplication
public class ServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}

}
