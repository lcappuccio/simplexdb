package org.systemexception.simplexdb.test;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.systemexception.simplexdb.domain.Data;
import org.systemexception.simplexdb.service.StorageService;
import org.systemexception.simplexdb.test.database.AbstractDbTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author leo
 * @date 08/12/15 22:09
 */
class StorageServiceTest {

    private static StorageService sut;
    private final static String STORAGE_FOLDER = AbstractDbTest.TARGET_FOLDER + "test_output";
    private final Data testData = new Data("TEST", "TEST".getBytes());

	@BeforeAll
	public static void setSut() throws IOException {
		File toRemove = new File(STORAGE_FOLDER);
        if (toRemove.exists()) {
            String[] files = toRemove.list();
            for (String file : files) {
                new File(STORAGE_FOLDER + File.separator + file).delete();
            }
        }
        FileUtils.deleteQuietly(toRemove);

        assertFalse(toRemove.exists());
        sut = new StorageService(STORAGE_FOLDER);
    }

	@AfterAll
	public static void tearDownSut() {
		File toRemove = new File(STORAGE_FOLDER);
		if (toRemove.exists()) {
			String[] files = toRemove.list();
			for (String file : files) {
				new File(STORAGE_FOLDER + File.separator + file).deleteOnExit();
			}
		}
		FileUtils.deleteQuietly(toRemove);
	}

	@Test
	void outputFolderExists() {
		assertTrue(new File(STORAGE_FOLDER).exists());
	}

	@Test
	void saveDataExists() throws IOException {
		sut.saveFile(testData);
		File testDataFile = new File(STORAGE_FOLDER + File.separator + testData.getName());

		assertTrue(testDataFile.exists());
	}

	@Test
	void historify() throws IOException {
		sut.saveFile(testData);
		File testDataFile = new File(STORAGE_FOLDER + File.separator + testData.getName());
		BasicFileAttributes attrs = Files.readAttributes(testDataFile.toPath(), BasicFileAttributes.class);
		sut.saveFile(testData);
		assertTrue(new File(STORAGE_FOLDER + File.separator + convertTime(attrs.creationTime().toMillis()) + "_" +
				testDataFile.getName()).exists());
	}

	private String convertTime(long time) {
		Date date = new Date(time);
		Format format = new SimpleDateFormat(StorageService.DATE_TIME_FORMAT);
		return format.format(date);
	}
}