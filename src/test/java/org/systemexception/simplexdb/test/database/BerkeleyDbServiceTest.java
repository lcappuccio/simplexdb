package org.systemexception.simplexdb.test.database;

import com.sleepycat.je.DatabaseException;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.systemexception.simplexdb.database.BerkeleyDbService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * @author leo
 * @date 28/02/16 11:56
 */
public class BerkeleyDbServiceTest extends AbstractDbTest {

	@Before
	public void setUp() throws FileNotFoundException {
		TEST_DATABASE_FILENAME = "target" + File.separator + "test_berkeley.db";
		File databaseFile = new File(TEST_DATABASE_FILENAME);
		if (databaseFile.exists()) {
			databaseFile.delete();
		}
		databaseFile.mkdir();
		sut = new BerkeleyDbService(storageServiceApi, TEST_DATABASE_FILENAME, 1000L);
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

	@Test(expected = FileNotFoundException.class)
	public void dont_create_bad_dir() throws FileNotFoundException {
		sut = new BerkeleyDbService(storageServiceApi, "//\\|/", 1000L);
	}
}