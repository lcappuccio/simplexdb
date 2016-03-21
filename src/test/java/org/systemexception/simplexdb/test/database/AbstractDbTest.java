package org.systemexception.simplexdb.test.database;

import com.sleepycat.je.DatabaseException;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import org.systemexception.simplexdb.database.DatabaseApi;
import org.systemexception.simplexdb.domain.Data;
import org.systemexception.simplexdb.service.StorageService;
import org.systemexception.simplexdb.service.StorageServiceApi;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * @author leo
 * @date 28/02/16 15:05
 */
public abstract class AbstractDbTest {

	protected DatabaseApi sut;
	protected static String TEST_DATABASE_FILENAME;
	protected final StorageServiceApi storageServiceApi = mock(StorageService.class);

	@After
	public void tearDown() throws IOException {
		sut.close();
		File databaseFile = new File(TEST_DATABASE_FILENAME);
		Stream<Path> walk = Files.walk(Paths.get(TEST_DATABASE_FILENAME), FileVisitOption.FOLLOW_LINKS);
		walk.forEach(item -> item.toFile().delete());
		FileUtils.deleteDirectory(new File(TEST_DATABASE_FILENAME));

		assertFalse(databaseFile.exists());
	}

	@AfterClass
	public static void destroySut() throws IOException {
		File databaseFile = new File(TEST_DATABASE_FILENAME);
		if (databaseFile.exists()) {
			Stream<Path> walk = Files.walk(Paths.get(TEST_DATABASE_FILENAME), FileVisitOption.FOLLOW_LINKS);
			walk.forEach(item -> item.toFile().delete());
			FileUtils.deleteDirectory(new File(TEST_DATABASE_FILENAME));

			assertFalse(databaseFile.exists());
		}
	}

	@Test
	public void database_created() {
		File databaseFile = new File(TEST_DATABASE_FILENAME);

		assertTrue(databaseFile.exists());
	}

	@Test
	public void add_record() throws DatabaseException, IOException {
		Data data = getDataForDatabase("id");
		boolean saved = sut.save(data);

		assertTrue(saved);
	}

	@Test
	public void dont_add_duplicate_record() throws DatabaseException, IOException {
		Data data = getDataForDatabase("id");
		boolean saved = sut.save(data);
		assertTrue(saved);
		boolean notSaved = sut.save(data);

		assertFalse(notSaved);
	}

	@Test
	public void get_data_id_list() throws DatabaseException, IOException, ClassNotFoundException {
		int dataToAdd = 10;
		for (int i = 0; i < dataToAdd; i++) {
			Data data = getDataForDatabase(String.valueOf(i));
			sut.save(data);
		}
		assertTrue(sut.findAll().size() == dataToAdd);
	}

	@Test
	public void delete_existing_data() throws DatabaseException, IOException {
		Data data = getDataForDatabase("id");
		sut.save(data);

		assertTrue(sut.delete(data.getInternalId()));
	}

	@Test
	public void dont_delete_non_existing_data() throws DatabaseException {
		Data data = getDataForDatabase("id");

		assertFalse(sut.delete(data.getName()));
	}

	@Test
	public void find_existing_data() throws DatabaseException, IOException, ClassNotFoundException {
		Data data = getDataForDatabase("id");
		sut.save(data);
		Data foundData = sut.findById(data.getInternalId()).get();

		assertEquals(foundData, data);
		assertEquals(data.getSize(), foundData.getSize());
	}

	@Test(expected = NoSuchElementException.class)
	public void dont_find_non_existing_data() throws DatabaseException, IOException, ClassNotFoundException {
		String nonExistingId = "nonExistingId";
		Data emptyData = sut.findById(nonExistingId).get();

		assertTrue(null == emptyData);
	}

	@Test
	public void find_matches() throws DatabaseException, IOException, ClassNotFoundException {
		int dataToAdd = 5;
		for (int i = 0; i < dataToAdd; i++) {
			Data data = getDataForDatabase(String.valueOf(i));
			sut.save(data);
		}

		List<Data> foundItems = sut.findByFilename("1");
		assertTrue(foundItems.size() == 1);

		foundItems = sut.findByFilename("data");
		assertTrue(foundItems.size() == 5);

		foundItems = sut.findByFilename("NON_EXISTING_ID");
		assertTrue(foundItems.size() == 0);
	}

	@Test
	public void data_integrity() throws IOException, ClassNotFoundException {
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
