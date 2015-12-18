package org.systemexception.simplexdb;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.systemexception.simplexdb.database.DatabaseApi;
import org.systemexception.simplexdb.database.DatabaseService;
import org.systemexception.simplexdb.service.StorageService;
import org.systemexception.simplexdb.service.StorageServiceApi;

import java.io.IOException;

/**
 * @author leo
 * @date 04/12/15 23:27
 */
@ComponentScan
@EnableAutoConfiguration
@SpringBootApplication
@EnableScheduling
public class Application {

	@Value("${database.filename}")
	private String databaseFilename;

	@Value("${storage.folder}")
	private String storageFolder;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	DatabaseApi databaseService() {
		return new DatabaseService(databaseFilename);
	}

	@Bean
	StorageServiceApi storageService() throws IOException {
		return new StorageService(storageFolder);
	}
}
