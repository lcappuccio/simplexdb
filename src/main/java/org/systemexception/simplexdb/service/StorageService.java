package org.systemexception.simplexdb.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.systemexception.simplexdb.constants.LogMessages;
import org.systemexception.simplexdb.domain.Data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author leo
 * @date 08/12/15 22:00
 */
public class StorageService implements StorageServiceApi {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final String storageFolder;
	public static final String DATE_TIME_FORMAT = "yyyyMMddHHmmss";

	public StorageService(final String storageFolder) throws IOException {
		this.storageFolder = storageFolder;
		createStorageFolder();
	}

	private void createStorageFolder() throws IOException {
		File storageFolderFile = new File(storageFolder);
		if (!storageFolderFile.exists()) {
			Files.createDirectory(storageFolderFile.toPath());
			logger.info(LogMessages.STORAGE_FOLDER + storageFolder);
		}
	}

	@Override
	public void saveFile(Data data) throws IOException {
		File dataFile = new File(storageFolder + File.separator + data.getName());
		historifyFile(dataFile);
		FileOutputStream fos = new FileOutputStream(dataFile);
		fos.write(data.getContent());
		logger.info(data.getName() + LogMessages.STORAGE_SAVE);
	}

	private void historifyFile(File file) throws IOException {
		if (file.exists()) {
			BasicFileAttributes attrs;
			attrs = Files.readAttributes(file.getAbsoluteFile().toPath(), BasicFileAttributes.class);
			long fileTime = attrs.creationTime().toMillis();
			String historifiedFilename = File.separator + convertTime(fileTime) + "_" + file.getName();
			file.renameTo(new File(storageFolder + File.separator + historifiedFilename));
			logger.info(file.getName() + LogMessages.STORAGE_RENAME + historifiedFilename);
		}
	}

	private String convertTime(long time) {
		Date date = new Date(time);
		Format format = new SimpleDateFormat(DATE_TIME_FORMAT);
		return format.format(date);
	}
}
