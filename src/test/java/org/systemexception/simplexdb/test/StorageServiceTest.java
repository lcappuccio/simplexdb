package org.systemexception.simplexdb.test;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.systemexception.simplexdb.domain.Data;
import org.systemexception.simplexdb.service.StorageService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author leo
 * @date 08/12/15 22:09
 */
public class StorageServiceTest {

	private final static String STORAGE_FOLDER = "target" + File.separator + "test_output";
	private final Data testData = new Data("TEST", "TEST".getBytes());
	private StorageService sut;

	@BeforeClass
	public static void setSut() {
		File toRemove = new File(STORAGE_FOLDER);
		if (toRemove.exists()) {
			String[] files = toRemove.list();
			for (String file : files) {
				new File(STORAGE_FOLDER + File.separator + file).delete();
			}
		}
		toRemove.delete();

		assertFalse(toRemove.exists());
	}

	@AfterClass
	public static void tearDownSut() {
		File toRemove = new File(STORAGE_FOLDER);
		if (toRemove.exists()) {
			String[] files = toRemove.list();
			for (String file : files) {
				new File(STORAGE_FOLDER + File.separator + file).delete();
			}
		}
		toRemove.delete();

		assertFalse(toRemove.exists());
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
		File testDataFile = new File(STORAGE_FOLDER + File.separator + testData.getName());

		assertTrue(testDataFile.exists());
	}

	@Test
	public void historify() throws IOException {
		sut.saveFile(testData);
		File testDataFile = new File(STORAGE_FOLDER + File.separator + testData.getName());
		BasicFileAttributes attrs = Files.readAttributes(testDataFile.toPath(), BasicFileAttributes.class);
		sut.saveFile(testData);
		assertTrue(new File(STORAGE_FOLDER + File.separator + convertTime(attrs.creationTime().toMillis()) + "_" +
				testDataFile.getName()).exists());
	}

	private String convertTime(long time) {
		Date date = new Date(time);
		Format format = new SimpleDateFormat("yyyyMMddHHmmss");
		return format.format(date);
	}
}