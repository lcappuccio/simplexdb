package org.systemexception.simplexdb.test.database;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.systemexception.simplexdb.database.DatabaseApi;
import org.systemexception.simplexdb.database.impl.MapDbService;
import org.systemexception.simplexdb.domain.Data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author leo
 * @date 05/12/15 01:58
 */
public class MapDbServiceTest extends AbstractDbTest {

	@Before
	public void setUp() {
		TEST_DATABASE_FILENAME = "target" + File.separator + "test_map.db";
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
			databaseFile.delete();
		}
		assertFalse(databaseFile.exists());
	}

	@Test
	public void limit_memory() throws FileNotFoundException {
		DatabaseApi innerSut;
		innerSut = new MapDbService(storageServiceApi, "target" + File.separator + "low_mem_mapdb_test_db", 1L);
		innerSut.save(getDataForDatabase("dataId"));
		List<Data> dataId = innerSut.findByFilename("dataId");

		assertTrue(dataId.size() == 1);
		assertTrue("WARNING".equals(dataId.get(0).getInternalId()));
	}

	@After
	public void tearDown() {
		sut.close();
	}

}