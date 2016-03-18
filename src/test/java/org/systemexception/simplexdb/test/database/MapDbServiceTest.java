package org.systemexception.simplexdb.test.database;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.systemexception.simplexdb.database.impl.MapDbService;

import java.io.File;

import static org.junit.Assert.assertFalse;

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

	@After
	public void tearDown() {
		sut.close();
	}

}