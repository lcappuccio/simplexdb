package org.systemexception.simplexdb.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.systemexception.simplexdb.database.DatabaseApi;
import org.systemexception.simplexdb.database.DatabaseService;
import org.systemexception.simplexdb.domain.Data;
import org.systemexception.simplexdb.domain.DataId;

import java.io.File;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author leo
 * @date 05/12/15 01:58
 */
public class DatabaseServiceTest {

	private final static String TEST_DATABASE_FILENAME = "target" + File.separator + "test.db";
	private DatabaseApi sut;

	@Before
	public void setUp() {
		File databaseFile = new File(TEST_DATABASE_FILENAME);
		if (databaseFile.exists()) {
			databaseFile.delete();
		}
		sut = new DatabaseService(TEST_DATABASE_FILENAME);
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
		assertTrue(sut.delete(data.getDataId()));
	}

	@Test
	public void dontDeleteNonExistingData() {
		Data data = getDataForDatabase("id");
		assertFalse(sut.delete(data.getDataId()));
	}

	@Test
	public void findExistingData() {
		Data data = getDataForDatabase("id");
		sut.save(data);
		Data foundData = sut.findById(data.getDataId()).get();
		assertEquals(foundData, data);
	}

	@Test(expected = NoSuchElementException.class)
	public void dontFindNonExistingData() {
		DataId nonExistingId = new DataId("nonExistingId");
		Data emptyData = sut.findById(nonExistingId).get();
	}

	private Data getDataForDatabase(String id) {
		byte[] dataContent = "TestData".getBytes();
		DataId dataId = new DataId(id);
		return new Data(dataId, dataContent);
	}
}