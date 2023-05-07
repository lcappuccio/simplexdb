package org.systemexception.simplexdb.test.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.systemexception.simplexdb.database.AbstractDbService;
import org.systemexception.simplexdb.database.DatabaseApi;
import org.systemexception.simplexdb.database.impl.BerkeleyDbService;
import org.systemexception.simplexdb.domain.Data;
import org.systemexception.simplexdb.test.controller.AbstractControllerTest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author leo
 * @date 28/02/16 11:56
 */
public class BerkeleyDbServiceTest extends AbstractDbTest {

	public static final String TEST_DATABASE_FILENAME = "test_berkeley.db";

	@BeforeEach
	void setUp() throws IOException, ClassNotFoundException {
		AbstractControllerTest.TEST_DATABASE_FULLPATH = AbstractDbTest.TARGET_FOLDER + File.separator + TEST_DATABASE_FILENAME;
		File databaseFile = new File(AbstractControllerTest.TEST_DATABASE_FULLPATH);
		if (databaseFile.exists()) {
			databaseFile.delete();
		}
		databaseFile.mkdir();
		sut = new BerkeleyDbService(STORAGE_SERVICE_API, AbstractControllerTest.TEST_DATABASE_FULLPATH, 1000L);
	}

	@Test
	void dont_create_bad_dir() throws IOException, ClassNotFoundException {
        assertThrows(FileNotFoundException.class, () -> {
            sut = new BerkeleyDbService(STORAGE_SERVICE_API, "//\\|/", 1000L);
        });

	}

	@Test
	void rebuild_index() throws IOException, ClassNotFoundException {
		sut.save(getDataForDatabase(AbstractDbTest.TEST_DATABASE_ID));
		sut.rebuildIndex();
	}

	@Test
	void limit_memory() throws IOException, ClassNotFoundException {
		DatabaseApi innerSut;
		innerSut = new BerkeleyDbService(STORAGE_SERVICE_API, AbstractDbTest.TARGET_FOLDER + File.separator +
				"low_mem_berkeley_test_db", 1L);
		innerSut.save(getDataForDatabase(AbstractDbTest.TEST_DATABASE_ID));
		List<Data> dataId = innerSut.findByFilename(AbstractDbTest.TEST_DATABASE_ID);

        assertEquals(1, dataId.size());
        assertEquals(AbstractDbService.WARNING_MESSAGE_MEMORY_OCCUPATION, dataId.get(0).getInternalId());
	}

	@Test
	void limit_memory_find_all() throws IOException, ClassNotFoundException {
		DatabaseApi innerSut;
		innerSut = new BerkeleyDbService(STORAGE_SERVICE_API, AbstractDbTest.TARGET_FOLDER + File.separator +
				"low_mem_test_db", 1L);
		innerSut.save(getDataForDatabase(AbstractDbTest.TEST_DATABASE_ID));
		List<Data> dataId = innerSut.findAll();

        assertEquals(1, dataId.size());
	}
}