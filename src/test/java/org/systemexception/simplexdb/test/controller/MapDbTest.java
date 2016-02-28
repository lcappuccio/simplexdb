package org.systemexception.simplexdb.test.controller;

import com.sleepycat.je.DatabaseException;
import org.junit.AfterClass;
import org.junit.Before;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.systemexception.simplexdb.controller.SimplexDbController;
import org.systemexception.simplexdb.database.MapDbService;
import org.systemexception.simplexdb.domain.Data;
import org.systemexception.simplexdb.service.StorageService;

import java.io.File;
import java.util.Optional;

import static org.mockito.Mockito.*;

/**
 * @author leo
 * @date 05/12/15 21:53
 */
public class MapDbTest extends AbstractControllerTest {

	@Before
	public void setUp() throws DatabaseException {
		TEST_DATABASE_FILENAME = "target" + File.separator + "test_berkeley.db";
		mockData = mock(Data.class);
		when(mockData.getDataInternalId()).thenReturn("123");
		when(mockData.getDataName()).thenReturn("123");
		when(mockData.getDataData()).thenReturn("123".getBytes());
		databaseService = mock(MapDbService.class);
		storageService = mock(StorageService.class);
		when(databaseService.findById(mockData.getDataName())).thenReturn(Optional.of(mockData));
		when(databaseService.delete(mockData.getDataName())).thenReturn(true);
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
		assert (!databaseFile.exists());
	}
}