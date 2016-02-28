package org.systemexception.simplexdb.test.database;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.systemexception.simplexdb.database.MapDbService;
import org.systemexception.simplexdb.domain.Data;

import java.io.File;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

/**
 * @author leo
 * @date 05/12/15 01:58
 */
public class MapDbServiceTest implements AbstractDbTest {

	private final static String TEST_DATABASE_FILENAME = "target" + File.separator + "test_map.db";
	private MapDbService sut;

	@Before
	public void setUp() {
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
	public void tearDown() {
		sut.close();
	}

	@Test
	public void databaseCreated() {
		File databaseFile = new File(TEST_DATABASE_FILENAME);
		assertTrue(databaseFile.exists());
	}

	@Test
	public void addRecord() {
		Data data = getDataForDatabase("id");
		boolean saved = sut.save(data);
		sut.commit();
		assertTrue(saved);
	}

	@Test
	public void dontAddDuplicateRecord() {
		Data data = getDataForDatabase("id");
		boolean saved = sut.save(data);
		assertTrue(saved);
		boolean notSaved = sut.save(data);
		assertFalse(notSaved);
	}

	@Test
	public void getDataIdList() {
		int dataToAdd = 10;
		for (int i = 0; i < dataToAdd; i ++) {
			Data data = getDataForDatabase(String.valueOf(i));
			sut.save(data);
		}
		assertTrue(sut.findAll().size() == dataToAdd);
	}

	@Test
	public void deleteExistingData() {
		Data data = getDataForDatabase("id");
		sut.save(data);
		assertTrue(sut.delete(data.getDataInternalId()));
	}

	@Test
	public void dontDeleteNonExistingData() {
		Data data = getDataForDatabase("id");
		assertFalse(sut.delete(data.getDataName()));
	}

	@Test
	public void findExistingData() {
		Data data = getDataForDatabase("id");
		sut.save(data);
		Data foundData = sut.findById(data.getDataInternalId()).get();
		assertEquals(foundData, data);
		assertEquals(data.getDataSize(), foundData.getDataSize());
	}

	@Test(expected = NoSuchElementException.class)
	public void dontFindNonExistingData() {
		String nonExistingId = "nonExistingId";
		Data emptyData = sut.findById(nonExistingId).get();
		assertTrue(null == emptyData);
	}

	@Test
	public void findMatches() {
		int dataToAdd = 5;
		for (int i = 0; i < dataToAdd; i ++) {
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

	private Data getDataForDatabase(String id) {
		byte[] dataContent = ("data" + id).getBytes();
		String dataId = "data" + id;
		return new Data(dataId, dataId, dataContent);
	}
}