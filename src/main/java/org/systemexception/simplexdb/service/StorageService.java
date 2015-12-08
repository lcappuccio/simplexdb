package org.systemexception.simplexdb.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.systemexception.simplexdb.constants.LogMessages;
import org.systemexception.simplexdb.domain.Data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

/**
 * @author leo
 * @date 08/12/15 22:00
 */
@Service
public class StorageService  implements StorageServiceApi {

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
		// TODO if file exists save historify previous one and save new
		File dataFile = new File(data.getDataId().getDataId());
		try (FileOutputStream fos = new FileOutputStream(storageFolder + File.separator + dataFile)) {
			fos.write(data.getDataData());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
}
