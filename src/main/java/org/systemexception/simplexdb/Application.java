package org.systemexception.simplexdb;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.systemexception.simplexdb.database.DatabaseService;

/**
 * @author leo
 * @date 04/12/15 23:27
 */
@ComponentScan
@EnableAutoConfiguration
public class Application {

	@Value("${database.filename}")
	private String databaseFilename;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	DatabaseService databaseService() {
		return new DatabaseService(databaseFilename);
	};
}
