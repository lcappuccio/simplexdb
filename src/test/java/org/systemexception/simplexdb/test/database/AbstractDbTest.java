package org.systemexception.simplexdb.test.database;

import com.sleepycat.je.DatabaseException;
import org.junit.Test;
import org.systemexception.simplexdb.database.DatabaseApi;
import org.systemexception.simplexdb.domain.Data;

import java.io.File;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

/**
 * @author leo
 * @date 28/02/16 15:05
 */
public abstract class AbstractDbTest {

	protected DatabaseApi sut;
	protected static String TEST_DATABASE_FILENAME;

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
		Data data = getDataForDatabase("id");
		sut.save(data);
		assertTrue(sut.delete(data.getInternalId()));
	}

	@Test
	public void dontDeleteNonExistingData() throws DatabaseException {
		Data data = getDataForDatabase("id");
		assertFalse(sut.delete(data.getName()));
	}

	@Test
	public void findExistingData() throws DatabaseException {
		Data data = getDataForDatabase("id");
		sut.save(data);
		Data foundData = sut.findById(data.getInternalId()).get();
		assertEquals(foundData, data);
		assertEquals(data.getSize(), foundData.getSize());
	}

	@Test(expected = NoSuchElementException.class)
	public void dontFindNonExistingData() throws DatabaseException {
		String nonExistingId = "nonExistingId";
		Data emptyData = sut.findById(nonExistingId).get();
		assertTrue(null == emptyData);
	}

	@Test
	public void findMatches() throws DatabaseException {
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

	protected Data getDataForDatabase(String id) {
		byte[] dataContent = ("data" + id).getBytes();
		String dataId = "data" + id;
		return new Data(dataId, dataId, System.currentTimeMillis(), dataContent);
	}

}
