package org.systemexception.simplexdb.test.database;

import org.junit.Before;
import org.systemexception.simplexdb.database.impl.OrientDbService;

import java.io.File;

/**
 * @author cappuccio
 * @date 15/03/16 10:08
 */
public class OrientDbServiceTest extends AbstractDbTest {

	@Before
	public void setUp() {
		TEST_DATABASE_FILENAME = "target" + File.separator + "test_orient.db";
		File databaseFile = new File(TEST_DATABASE_FILENAME);
		if (databaseFile.exists()) {
			databaseFile.delete();
		}
		sut = new OrientDbService(storageServiceApi, TEST_DATABASE_FILENAME, 1000L);
	}
}