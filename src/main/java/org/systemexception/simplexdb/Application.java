package org.systemexception.simplexdb;

import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.systemexception.simplexdb.database.DatabaseApi;
import org.systemexception.simplexdb.database.impl.BerkeleyDbService;
import org.systemexception.simplexdb.database.impl.MapDbService;
import org.systemexception.simplexdb.service.StorageService;
import org.systemexception.simplexdb.service.StorageServiceApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.io.IOException;

/**
 * @author leo
 * @date 04/12/15 23:27
 */
@SpringBootApplication
public class Application {
	
	@Value("${database.filename}")
	private String databaseFilename;

	@Value("${storage.folder}")
	private String storageFolder;

	@Value("${database.type}")
	private String databaseType;

	@Value("${database.memory.occupation}")
	private Long maxMemoryOccupation;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public DatabaseApi databaseService() throws IOException, ClassNotFoundException {
		if ("mapdb".equals(databaseType)) {
			return new MapDbService(storageService(), databaseFilename, maxMemoryOccupation);
		}
		if ("berkeleydb".equals(databaseType)) {
			return new BerkeleyDbService(storageService(), databaseFilename, maxMemoryOccupation);
		}
		throw new InvalidPropertyException(DatabaseApi.class, "database.type", "Database configuration missing");
	}

	@Bean
	public StorageServiceApi storageService() throws IOException {
		return new StorageService(storageFolder);
	}

	@Bean
	public Docket restfulApi() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("restful-api").select().build().apiInfo(apiInfo());
	}

	private ApiInfo apiInfo() {
		return new ApiInfo(
				"SimplexDB",
				"REST API with embedded Database",
				null,
				null,
				"leo@systemexception.org",
				"GPL v3",
				"https://github.com/lcappuccio/simplexdb/blob/master/LICENSE"
		);
	}
}
