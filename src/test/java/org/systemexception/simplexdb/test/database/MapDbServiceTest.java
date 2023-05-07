package org.systemexception.simplexdb.test.database;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.systemexception.simplexdb.database.AbstractDbService;
import org.systemexception.simplexdb.database.DatabaseApi;
import org.systemexception.simplexdb.database.impl.MapDbService;
import org.systemexception.simplexdb.domain.Data;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * @author leo
 * @date 05/12/15 01:58
 */
public class MapDbServiceTest extends AbstractDbTest {

	public static final String TEST_DATABASE_FILENAME = "test_map.db";

	@Before
	public void setUp() {
		TEST_DATABASE_FULLPATH = AbstractDbTest.TARGET_FOLDER + "/" + TEST_DATABASE_FILENAME;
		File databaseFile = new File(TEST_DATABASE_FILENAME);
		if (databaseFile.exists()) {
			databaseFile.delete();
		}
		sut = new MapDbService(storageServiceApi, TEST_DATABASE_FILENAME, 1000L);
	}

	@AfterClass
	public static void tearDownSut() {
		File databaseFile = new File(TEST_DATABASE_FILENAME);
		if (databaseFile.exists()) {
			databaseFile.deleteOnExit();
		}
	}

	@Test
	public void limit_memory() throws IOException, ClassNotFoundException {
		DatabaseApi innerSut;
		innerSut = new MapDbService(storageServiceApi, AbstractDbTest.TARGET_FOLDER + "/" +
				"low_mem_mapdb_test_db_1", 1L);
		innerSut.save(getDataForDatabase(AbstractDbTest.TEST_DATABASE_ID));
		List<Data> dataId = innerSut.findByFilename(AbstractDbTest.TEST_DATABASE_ID);

		assertTrue(dataId.size() == 1);
		assertTrue(AbstractDbService.WARNING_MESSAGE_MEMORY_OCCUPATION.equals(dataId.get(0).getInternalId()));
	}

	@Test
	public void limit_memory_findall() throws IOException, ClassNotFoundException {
		DatabaseApi innerSut;
		innerSut = new MapDbService(storageServiceApi, AbstractDbTest.TARGET_FOLDER + "/" +
				"low_mem_mapdb_test_db_2", 1L);
		innerSut.save(getDataForDatabase(AbstractDbTest.TEST_DATABASE_ID));
		List<Data> dataId = innerSut.findAll();

		assertTrue(dataId.size() == 1);
	}

	@After
	public void tearDown() {
		sut.close();
	}

}