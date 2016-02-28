package org.systemexception.simplexdb;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.systemexception.simplexdb.database.Api;
import org.systemexception.simplexdb.database.MapDbService;
import org.systemexception.simplexdb.service.StorageService;
import org.systemexception.simplexdb.service.StorageServiceApi;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
	public Api databaseService() {
		if ("mapdb".equals(databaseType)) {
			return new MapDbService(databaseFilename);
		}
		if ("berkeleydb".equals(databaseType)) {
			throw new NotImplementedException();
		}
		return null;
	}

	@Bean
	public StorageServiceApi storageService() throws IOException {
		return new StorageService(storageFolder);
	}
}
