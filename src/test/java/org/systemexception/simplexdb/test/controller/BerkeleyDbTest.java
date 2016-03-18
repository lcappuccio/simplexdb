package org.systemexception.simplexdb.test.controller;

import com.sleepycat.je.DatabaseException;
import org.junit.AfterClass;
import org.junit.Before;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.systemexception.simplexdb.controller.SimplexDbController;
import org.systemexception.simplexdb.database.impl.BerkeleyDbService;
import org.systemexception.simplexdb.domain.Data;
import org.systemexception.simplexdb.service.StorageService;

import java.io.File;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author leo
 * @date 28/02/16 17:29
 */
public class BerkeleyDbTest extends AbstractControllerTest {

	@Before
	public void setUp() throws DatabaseException {
		TEST_DATABASE_FILENAME = "target" + File.separator + "test_map.db";
		mockData = mock(Data.class);
		when(mockData.getInternalId()).thenReturn("123");
		when(mockData.getName()).thenReturn("123");
		when(mockData.getContent()).thenReturn("123".getBytes());
		databaseService = mock(BerkeleyDbService.class);
		storageService = mock(StorageService.class);
		when(databaseService.findById(mockData.getName())).thenReturn(Optional.of(mockData));
		when(databaseService.delete(mockData.getName())).thenReturn(true);
		when(databaseService.save(any())).thenReturn(true);
		simplexDbController = new SimplexDbController();
		MockitoAnnotations.initMocks(this);
		sut = MockMvcBuilders.standaloneSetup(simplexDbController).build();
	}

	@AfterClass
	public static void tearDownSut() {
		File databaseFile = new File(TEST_DATABASE_FILENAME);
		if (databaseFile.exists()) {
			databaseFile.delete();
		}
		assertFalse(databaseFile.exists());
	}
}
