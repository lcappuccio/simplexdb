package org.systemexception.simplexdb.test.database;

import org.junit.Before;
import org.junit.Test;
import org.systemexception.simplexdb.database.impl.BerkeleyDbService;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * @author leo
 * @date 28/02/16 11:56
 */
public class BerkeleyDbServiceTest extends AbstractDbTest {

	@Before
	public void setUp() throws FileNotFoundException {
		TEST_DATABASE_FILENAME = "target" + File.separator + "test_berkeley.db";
		File databaseFile = new File(TEST_DATABASE_FILENAME);
		if (databaseFile.exists()) {
			databaseFile.delete();
		}
		databaseFile.mkdir();
		sut = new BerkeleyDbService(storageServiceApi, TEST_DATABASE_FILENAME, 1000L);
	}

	@Test(expected = FileNotFoundException.class)
	public void dont_create_bad_dir() throws FileNotFoundException {
		sut = new BerkeleyDbService(storageServiceApi, "//\\|/", 1000L);
	}
}