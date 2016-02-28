package org.systemexception.simplexdb;

import com.sleepycat.je.DatabaseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.systemexception.simplexdb.database.BerkeleyDbService;
import org.systemexception.simplexdb.database.DatabaseApi;
import org.systemexception.simplexdb.database.MapDbService;
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
public class Application {

	@Value("${database.filename}")
	private String databaseFilename;

	@Value("${storage.folder}")
	private String storageFolder;

	@Value("${database.type}")
	private String databaseType;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public DatabaseApi databaseService() throws DatabaseException {
		if ("mapdb".equals(databaseType)) {
			return new MapDbService(databaseFilename);
		}
		if ("berkeleydb".equals(databaseType)) {
			return new BerkeleyDbService(databaseFilename);
		}
		// Use a default
		return new MapDbService(databaseFilename);
	}

	@Bean
	public StorageServiceApi storageService() throws IOException {
		return new StorageService(storageFolder);
	}
}
