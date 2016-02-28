package org.systemexception.simplexdb.test.database;

import com.sleepycat.je.DatabaseException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.systemexception.simplexdb.database.MapDbService;

import java.io.File;

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
		sut = new MapDbService(TEST_DATABASE_FILENAME);
	}

	@AfterClass
	public static void tearDownSut() {
		File databaseFile = new File(TEST_DATABASE_FILENAME);
		if (databaseFile.exists()) {
			databaseFile.delete();
		}
		assert(!databaseFile.exists());
	}

	@After
	public void tearDown() throws DatabaseException {
		sut.close();
	}

}