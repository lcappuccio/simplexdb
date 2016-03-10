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
	public void saveFile(Data data) {
		File dataFile = new File(storageFolder + File.separator + data.getName());
		historifyFile(dataFile);
		try (FileOutputStream fos = new FileOutputStream(dataFile)) {
			fos.write(data.getContent());
			logger.info(data.getName() + LogMessages.STORAGE_SAVE);
		} catch (IOException e) {
			logger.error(data.getName() + LogMessages.STORAGE_SAVE_FAILED + e.getMessage());
		}
	}

	private void historifyFile(File file) {
		if (file.exists()) {
			BasicFileAttributes attrs;
			try {
				attrs = Files.readAttributes(file.getAbsoluteFile().toPath(), BasicFileAttributes.class);
				long fileTime = attrs.creationTime().toMillis();
				String historifiedFilename = File.separator + convertTime(fileTime) + "_" + file.getName();
				boolean renamedOk = file.renameTo(new File(storageFolder + File.separator + historifiedFilename));
				if (renamedOk) {
					logger.info(file.getName() + LogMessages.STORAGE_RENAME + historifiedFilename);
				} else {
					logger.error(file.getName() + LogMessages.STORAGE_RENAME_FAILED);
				}
			} catch (IOException e) {
				logger.error(file.getName() + LogMessages.STORAGE_RENAME_EXCEPTION + e.getMessage());
			}
		}
	}

	private String convertTime(long time) {
		Date date = new Date(time);
		Format format = new SimpleDateFormat("yyyyMMddHHmmss");
		return format.format(date);
	}
}
