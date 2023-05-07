package org.systemexception.simplexdb.test.controller;

import com.sleepycat.je.DatabaseException;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.systemexception.simplexdb.controller.SimplexDbController;
import org.systemexception.simplexdb.database.impl.BerkeleyDbService;
import org.systemexception.simplexdb.domain.Data;
import org.systemexception.simplexdb.service.StorageService;
import org.systemexception.simplexdb.test.database.AbstractDbTest;
import org.systemexception.simplexdb.test.database.BerkeleyDbServiceTest;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author leo
 * @date 28/02/16 17:29
 */
class BerkeleyDbTest extends AbstractControllerTest {

	@BeforeEach
	void setUp() throws DatabaseException, IOException, ClassNotFoundException {
		AbstractControllerTest.TEST_DATABASE_FULLPATH = AbstractDbTest.TARGET_FOLDER + File.separator +
				BerkeleyDbServiceTest.TEST_DATABASE_FILENAME;
		testData = new Data();
		testData.setInternalId("123");
		testData.setName("123");
		testData.setContent("123".getBytes());
		databaseService = mock(BerkeleyDbService.class);
		storageService = mock(StorageService.class);
		when(databaseService.findById(testData.getInternalId())).thenReturn(Optional.of(testData));
		when(databaseService.delete(testData.getName())).thenReturn(true);
		when(databaseService.save(any())).thenReturn(true);
		simplexDbController = new SimplexDbController(databaseService);
		MockitoAnnotations.initMocks(this);
		sut = MockMvcBuilders.standaloneSetup(simplexDbController).build();
	}
}
