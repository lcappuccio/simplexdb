package org.systemexception.simplexdb.test.database;

import com.sleepycat.je.DatabaseException;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.systemexception.simplexdb.database.DatabaseApi;
import org.systemexception.simplexdb.database.impl.BerkeleyDbService;
import org.systemexception.simplexdb.domain.Data;
import org.systemexception.simplexdb.service.StorageService;
import org.systemexception.simplexdb.service.StorageServiceApi;
import org.systemexception.simplexdb.test.controller.AbstractControllerTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * @author leo
 * @date 28/02/16 15:05
 */
public abstract class AbstractDbTest {

    protected DatabaseApi sut;

    public static final String TARGET_FOLDER = "target";
    public static final String TEST_DATABASE_ID = "dataId";

    protected static final StorageServiceApi STORAGE_SERVICE_API = mock(StorageService.class);

	@AfterEach
	void tearDown() throws IOException {
        if (sut instanceof BerkeleyDbService) {
            sut.close();
        }
        File databaseFile = new File(AbstractControllerTest.TEST_DATABASE_FULLPATH);
        Stream<Path> walk = Files.walk(Paths.get(AbstractControllerTest.TEST_DATABASE_FULLPATH), FileVisitOption.FOLLOW_LINKS);
        walk.forEach(item -> item.toFile().delete());
        FileUtils.deleteDirectory(new File(AbstractControllerTest.TEST_DATABASE_FULLPATH));
        assertFalse(databaseFile.exists());
    }

    /*
    @AfterAll
    public static void destroySut() throws IOException {
        File databaseFile = new File(TEST_DATABASE_FULLPATH);
        if (databaseFile.exists()) {
            Stream<Path> walk = Files.walk(Paths.get(TEST_DATABASE_FULLPATH), FileVisitOption.FOLLOW_LINKS);
            walk.forEach(item -> item.toFile().delete());
            databaseFile.deleteOnExit();
        }
    }

     */

	@Test
	void add_record() throws DatabaseException, IOException {
		Data data = getDataForDatabase("id");
		boolean saved = sut.save(data);

		assertTrue(saved);
	}

	@Test
	void dont_add_duplicate_record() throws DatabaseException, IOException {
		Data data = getDataForDatabase("id");
		boolean saved = sut.save(data);
		assertTrue(saved);
		boolean notSaved = sut.save(data);

		assertFalse(notSaved);
	}

	@Test
	void get_data_id_list() throws DatabaseException, IOException, ClassNotFoundException {
		int dataToAdd = 10;
		for (int i = 0; i < dataToAdd; i++) {
			Data data = getDataForDatabase(String.valueOf(i));
			sut.save(data);
		}
        assertEquals(sut.findAll().size(), dataToAdd);
	}

	@Test
	void delete_existing_data() throws DatabaseException, IOException {
		Data data = getDataForDatabase("id");
		sut.save(data);

		assertTrue(sut.delete(data.getName()));
	}

	@Test
	void dont_delete_non_existing_data() throws DatabaseException {
		Data data = new Data("internalId", "someName", System.currentTimeMillis(), "SomeData".getBytes());

		assertFalse(sut.delete(data.getName()));
	}

	@Test
	void find_existing_data() throws DatabaseException, IOException, ClassNotFoundException {
		Data data = getDataForDatabase("id");
		sut.save(data);
		Data foundData = sut.findById(data.getInternalId()).get();

		assertEquals(foundData, data);
		assertEquals(data.getSize(), foundData.getSize());
	}

	@Test
	void dont_find_non_existing_data() throws DatabaseException, IOException, ClassNotFoundException {
		String nonExistingId = "nonExistingId";
        assertThrows(NoSuchElementException.class, () -> {
            Data emptyData = sut.findById(nonExistingId).get();
        });
	}

	@Test
	void find_matches() throws DatabaseException, IOException, ClassNotFoundException {
		int dataToAdd = 5;
		for (int i = 0; i < dataToAdd; i++) {
			Data data = getDataForDatabase(String.valueOf(i));
			sut.save(data);
		}

		List<Data> foundItems = sut.findByFilename("1");
        assertEquals(1, foundItems.size());

		foundItems = sut.findByFilename("data");
        assertEquals(5, foundItems.size());

		foundItems = sut.findByFilename("NON_EXISTING_ID");
        assertEquals(0, foundItems.size());
	}

	@Test
	void data_integrity() throws IOException, ClassNotFoundException {
		Data data = new Data("123","dataName",123456L,"dataContent".getBytes());
		sut.save(data);
		Data dataFech = sut.findById("123").get();
		Data dataCopy = new Data();
		dataCopy.setInternalId(dataFech.getInternalId());
		dataCopy.setName(dataFech.getName());
		dataCopy.setDate(dataFech.getDate());
		dataCopy.setSize(dataFech.getSize());
		dataCopy.setContent(dataFech.getContent());

		assertEquals(data, dataCopy);
	}

	protected Data getDataForDatabase(String id) {
		byte[] dataContent = ("data" + id).getBytes();
		String dataId = "data" + id;
		return new Data(dataId, dataId, System.currentTimeMillis(), dataContent);
	}

}
