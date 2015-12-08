package org.systemexception.simplexdb.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.systemexception.simplexdb.constants.LogMessages;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * @author leo
 * @date 08/12/15 22:00
 */
@Service
public class StorageService {

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
}
