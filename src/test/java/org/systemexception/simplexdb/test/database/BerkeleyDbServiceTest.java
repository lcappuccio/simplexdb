package org.systemexception.simplexdb.test.database;

import org.junit.Before;
import org.junit.Test;
import org.systemexception.simplexdb.database.AbstractDbService;
import org.systemexception.simplexdb.database.DatabaseApi;
import org.systemexception.simplexdb.database.impl.BerkeleyDbService;
import org.systemexception.simplexdb.domain.Data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * @author leo
 * @date 28/02/16 11:56
 */
public class BerkeleyDbServiceTest extends AbstractDbTest {

	public static final String TEST_DATABASE_FILENAME = "test_berkeley.db";

	@Before
	public void setUp() throws IOException, ClassNotFoundException {
		TEST_DATABASE_FULLPATH = AbstractDbTest.TARGET_FOLDER + File.separator + TEST_DATABASE_FILENAME;
		File databaseFile = new File(TEST_DATABASE_FULLPATH);
		if (databaseFile.exists()) {
			databaseFile.delete();
		}
		databaseFile.mkdir();
		sut = new BerkeleyDbService(storageServiceApi, TEST_DATABASE_FULLPATH, 1000L);
	}

	@Test(expected = FileNotFoundException.class)
	public void dont_create_bad_dir() throws IOException, ClassNotFoundException {
		sut = new BerkeleyDbService(storageServiceApi, "//\\|/", 1000L);
	}

	@Test
	public void rebuild_index() throws IOException, ClassNotFoundException {
		sut.save(getDataForDatabase(AbstractDbTest.TEST_DATABASE_ID));
		sut.rebuildIndex();
	}

	@Test
	public void limit_memory() throws IOException, ClassNotFoundException {
		DatabaseApi innerSut;
		innerSut = new BerkeleyDbService(storageServiceApi, AbstractDbTest.TARGET_FOLDER + File.separator +
				"low_mem_berkeley_test_db", 1L);
		innerSut.save(getDataForDatabase(AbstractDbTest.TEST_DATABASE_ID));
		List<Data> dataId = innerSut.findByFilename(AbstractDbTest.TEST_DATABASE_ID);

		assertTrue(dataId.size() == 1);
		assertTrue(AbstractDbService.WARNING_MESSAGE_MEMORY_OCCUPATION.equals(dataId.get(0).getInternalId()));
	}

	@Test
	public void limit_memory_find_all() throws IOException, ClassNotFoundException {
		DatabaseApi innerSut;
		innerSut = new BerkeleyDbService(storageServiceApi, AbstractDbTest.TARGET_FOLDER + File.separator +
				"low_mem_test_db", 1L);
		innerSut.save(getDataForDatabase(AbstractDbTest.TEST_DATABASE_ID));
		List<Data> dataId = innerSut.findAll();

		assertTrue(dataId.size() == 1);
	}
}