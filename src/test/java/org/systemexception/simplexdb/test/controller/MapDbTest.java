package org.systemexception.simplexdb.test.controller;

import com.sleepycat.je.DatabaseException;
import org.junit.Before;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.systemexception.simplexdb.controller.SimplexDbController;
import org.systemexception.simplexdb.database.impl.MapDbService;
import org.systemexception.simplexdb.domain.Data;
import org.systemexception.simplexdb.service.StorageService;
import org.systemexception.simplexdb.test.database.AbstractDbTest;
import org.systemexception.simplexdb.test.database.MapDbServiceTest;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static org.mockito.Mockito.*;

/**
 * @author leo
 * @date 05/12/15 21:53
 */
public class MapDbTest extends AbstractControllerTest {

	@Before
	public void setUp() throws DatabaseException, IOException, ClassNotFoundException {
		TEST_DATABASE_FULLPATH = AbstractDbTest.TARGET_FOLDER + File.separator +
				MapDbServiceTest.TEST_DATABASE_FILENAME;
		mockData = mock(Data.class);
		when(mockData.getInternalId()).thenReturn("123");
		when(mockData.getName()).thenReturn("123");
		when(mockData.getContent()).thenReturn("123".getBytes());
		databaseService = mock(MapDbService.class);
		storageService = mock(StorageService.class);
		when(databaseService.findById(mockData.getName())).thenReturn(Optional.of(mockData));
		when(databaseService.delete(mockData.getName())).thenReturn(true);
		when(databaseService.save(any())).thenReturn(true);
		simplexDbController = new SimplexDbController(databaseService);
		MockitoAnnotations.initMocks(this);
		sut = MockMvcBuilders.standaloneSetup(simplexDbController).build();
	}
}