package org.systemexception.simplexdb.test.database;

import com.sleepycat.je.DatabaseException;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.systemexception.simplexdb.database.BerkeleyDbService;
import org.systemexception.simplexdb.domain.Data;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author leo
 * @date 28/02/16 11:56
 */
public class BerkeleyDbServiceTest implements AbstractDbTest {

	private final static String TEST_DATABASE_FILENAME = "target" + File.separator + "test_berkeley.db";
	private BerkeleyDbService sut;

	@Before
	public void setUp() throws DatabaseException {
		File databaseFile = new File(TEST_DATABASE_FILENAME);
		if (databaseFile.exists()) {
			databaseFile.delete();
		}
		databaseFile.mkdir();
		sut = new BerkeleyDbService(TEST_DATABASE_FILENAME);
	}

	@After
	public void tearDown() throws DatabaseException, IOException {
		sut.close();
		File databaseFile = new File(TEST_DATABASE_FILENAME);
		Stream<Path> walk = Files.walk(Paths.get(TEST_DATABASE_FILENAME), FileVisitOption.FOLLOW_LINKS);
		walk.forEach(item -> item.toFile().delete());
		FileUtils.deleteDirectory(new File(TEST_DATABASE_FILENAME));
		assert(!databaseFile.exists());
	}

	@Test
	public void databaseCreated() {
		File databaseFile = new File(TEST_DATABASE_FILENAME);
		assertTrue(databaseFile.exists());
	}

	@Test
	public void addRecord() throws DatabaseException {
		Data data = getDataForDatabase("id");
		boolean saved = sut.save(data);
		sut.commit();
		assertTrue(saved);
	}

	@Test
	public void dontAddDuplicateRecord() throws DatabaseException {
		Data data = getDataForDatabase("id");
		boolean saved = sut.save(data);
		assertTrue(saved);
		boolean notSaved = sut.save(data);
		assertFalse(notSaved);
	}

	@Test
	public void getDataIdList() throws DatabaseException {
		int dataToAdd = 10;
		for (int i = 0; i < dataToAdd; i ++) {
			Data data = getDataForDatabase(String.valueOf(i));
			sut.save(data);
		}
		assertTrue(sut.findAll().size() == dataToAdd);
	}

	@Test
	public void deleteExistingData() throws DatabaseException {
	}

	@Test
	public void dontDeleteNonExistingData() {

	}

	@Test
	public void findExistingData() throws DatabaseException {
		Data data = getDataForDatabase("id");
		sut.save(data);
		Data foundData = sut.findById(data.getDataInternalId()).get();
		assertEquals(foundData, data);
		assertEquals(data.getDataSize(), foundData.getDataSize());
	}

	@Test(expected = NoSuchElementException.class)
	public void dontFindNonExistingData() throws DatabaseException {
		String nonExistingId = "nonExistingId";
		Data emptyData = sut.findById(nonExistingId).get();
		assertTrue(null == emptyData);
	}

	@Test
	public void findMatches() {

	}

	private Data getDataForDatabase(String id) {
		byte[] dataContent = ("data" + id).getBytes();
		String dataId = "data" + id;
		return new Data(dataId, dataId, dataContent);
	}
}