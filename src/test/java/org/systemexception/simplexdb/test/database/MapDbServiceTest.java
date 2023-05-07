package org.systemexception.simplexdb.test.database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.systemexception.simplexdb.database.AbstractDbService;
import org.systemexception.simplexdb.database.DatabaseApi;
import org.systemexception.simplexdb.database.impl.MapDbService;
import org.systemexception.simplexdb.domain.Data;
import org.systemexception.simplexdb.test.controller.AbstractControllerTest;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * @author leo
 * @date 05/12/15 01:58
 */
public class MapDbServiceTest extends AbstractDbTest {

	public static final String TEST_DATABASE_FILENAME = "target/test_map.db";

	@BeforeEach
	public void setUp() {
		AbstractControllerTest.TEST_DATABASE_FULLPATH = AbstractDbTest.TARGET_FOLDER + "/" + TEST_DATABASE_FILENAME;
		File databaseFile = new File(TEST_DATABASE_FILENAME);
		if (databaseFile.exists()) {
			databaseFile.delete();
		}
		sut = new MapDbService(STORAGE_SERVICE_API, TEST_DATABASE_FILENAME, 1000L);
	}

    @AfterEach
    public void tearDown() {
        sut.close();
    }

	@Test
	void limit_memory() throws IOException, ClassNotFoundException {
		DatabaseApi innerSut;
		innerSut = new MapDbService(STORAGE_SERVICE_API, AbstractDbTest.TARGET_FOLDER + "/" +
				"low_mem_mapdb_test_db_1", 1L);
		innerSut.save(getDataForDatabase(AbstractDbTest.TEST_DATABASE_ID));
		List<Data> dataId = innerSut.findByFilename(AbstractDbTest.TEST_DATABASE_ID);

        assertEquals(1, dataId.size());
        assertEquals(AbstractDbService.WARNING_MESSAGE_MEMORY_OCCUPATION, dataId.get(0).getInternalId());
	}

	@Test
	void limit_memory_findall() throws IOException, ClassNotFoundException {
		DatabaseApi innerSut;
		innerSut = new MapDbService(STORAGE_SERVICE_API, AbstractDbTest.TARGET_FOLDER + "/" +
				"low_mem_mapdb_test_db_2", 1L);
		innerSut.save(getDataForDatabase(AbstractDbTest.TEST_DATABASE_ID));
		List<Data> dataId = innerSut.findAll();

        assertEquals(1, dataId.size());
	}
}