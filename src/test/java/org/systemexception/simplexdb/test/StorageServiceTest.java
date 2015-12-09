package org.systemexception.simplexdb.test;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.systemexception.simplexdb.domain.Data;
import org.systemexception.simplexdb.domain.DataId;
import org.systemexception.simplexdb.service.StorageService;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * @author leo
 * @date 08/12/15 22:09
 */
public class StorageServiceTest {

	private final static String STORAGE_FOLDER = "target" + File.separator + "test_output";
	private final Data testData = new Data(new DataId("TEST"), "TEST".getBytes());
	StorageService sut;

	@BeforeClass
	public static void setSut() {
		File toRemove = new File(STORAGE_FOLDER);
		if (toRemove.exists()) {
			String[] files = toRemove.list();
			for (String file: files) {
				new File(STORAGE_FOLDER + File.separator + file).delete();
			}
		}
		toRemove.delete();
		assert(!toRemove.exists());
	}

	@AfterClass
	public static void tearDownSut() {
		File toRemove = new File(STORAGE_FOLDER);
		if (toRemove.exists()) {
			String[] files = toRemove.list();
			for (String file: files) {
				new File(STORAGE_FOLDER + File.separator + file).delete();
			}
		}
		toRemove.delete();
		assert(!toRemove.exists());
	}

	@Before
	public void setUp() throws IOException {
		sut = new StorageService(STORAGE_FOLDER);
	}

	@Test
	public void outputFolderExists() {
		assertTrue(new File(STORAGE_FOLDER).exists());
	}

	@Test
	public void saveDataExists() {
		sut.saveFile(testData);
		File testDataFile = new File(STORAGE_FOLDER + File.separator + testData.getDataId().getDataId());
		assertTrue(testDataFile.exists());
	}
}