package org.systemexception.simplexdb.test.database;

import org.junit.Before;
import org.junit.Test;
import org.systemexception.simplexdb.database.DatabaseApi;
import org.systemexception.simplexdb.database.impl.BerkeleyDbService;
import org.systemexception.simplexdb.domain.Data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import static org.junit.Assert.assertTrue;

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

	@Test
	public void rebuild_index() {
		sut.save(getDataForDatabase("dataId"));
		sut.rebuildIndex();
	}

	@Test
	public void limit_memory() throws FileNotFoundException {
		DatabaseApi innerSut;
		innerSut = new BerkeleyDbService(storageServiceApi, "target" + File.separator + "low_mem_berkeley_test_db", 1L);
		innerSut.save(getDataForDatabase("dataId"));
		List<Data> dataId = innerSut.findByFilename("dataId");

		assertTrue(dataId.size() == 1);
		assertTrue("WARNING".equals(dataId.get(0).getInternalId()));
	}

	@Test
	public void limit_memory_find_all() throws FileNotFoundException {
		DatabaseApi innerSut;
		innerSut = new BerkeleyDbService(storageServiceApi, "target" + File.separator + "low_mem_test_db", 1L);
		innerSut.save(getDataForDatabase("dataId"));
		List<Data> dataId = innerSut.findAll();

		assertTrue(dataId.size() == 1);
	}
}