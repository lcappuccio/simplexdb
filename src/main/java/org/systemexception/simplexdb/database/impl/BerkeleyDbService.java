package org.systemexception.simplexdb.database.impl;

import com.sleepycat.je.*;
import org.systemexception.simplexdb.database.AbstractDbService;
import org.systemexception.simplexdb.domain.Data;
import org.systemexception.simplexdb.service.StorageServiceApi;

import java.io.*;
import java.util.*;

import static com.sleepycat.je.LockMode.READ_UNCOMMITTED;

/**
 * @author leo
 * @date 28/02/16 11:55
 */
public class BerkeleyDbService extends AbstractDbService {

	private final Environment environment;
	private final Database database;
	private final HashMap<String, String> indexFileNames = new HashMap<>();

	public BerkeleyDbService(final StorageServiceApi storageService, final String databaseName,
	                         final Long maxMemoryOccupation) throws IOException, ClassNotFoundException {
		this.databaseName = databaseName;
		this.maxMemoryOccupation = maxMemoryOccupation;
		this.storageService = storageService;
		LOGGER.info("Open database: {}", databaseName);
		EnvironmentConfig envConfig = new EnvironmentConfig();
		envConfig.setConfigParam("je.log.fileMax", "256000000");
		envConfig.setTransactional(true);
		envConfig.setAllowCreate(true);
		File productionDatabaseFile = new File(databaseName);
		if (!productionDatabaseFile.exists()) {
			boolean mkdir = productionDatabaseFile.mkdir();
			if (!mkdir) {
				String errorMessage = "Database directory creation failed";
				LOGGER.error(errorMessage);
				throw new FileNotFoundException(errorMessage);
			}
		}
		environment = new Environment(new File(databaseName), envConfig);
		DatabaseConfig databaseConfig = new DatabaseConfig();
		databaseConfig.setAllowCreate(true);
		databaseConfig.setTransactional(true);
		database = environment.openDatabase(null, databaseName, databaseConfig);
		rebuildIndex();
	}

	@Override
	public boolean save(Data data) throws DatabaseException, IOException {
		LOGGER.info("Save: {}", data.getName());
		DatabaseEntry dbKey = new DatabaseEntry(data.getInternalId().getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(out);
		os.writeObject(data);
		out.toByteArray();
		os.close();
		out.close();
		DatabaseEntry dbData = new DatabaseEntry(out.toByteArray());
		OperationStatus operationStatus = database.get(null, dbKey, dbData, READ_UNCOMMITTED);
		if (operationStatus.equals(OperationStatus.SUCCESS)) {
			return false;
		} else {
			database.put(null, dbKey, dbData);
			indexFileNames.put(data.getInternalId(), data.getName());
			LOGGER.info("Saved: {}", data.getName());
			return true;
		}
	}

	@Override
	public List<Data> findAll() throws DatabaseException, IOException, ClassNotFoundException {
        LOGGER.info("Find all ids");
        try (Cursor databaseCursor = database.openCursor(null, null)) {
            List<Data> foundData = new ArrayList<>();
            DatabaseEntry dbKey = new DatabaseEntry();
            DatabaseEntry dbData = new DatabaseEntry();
            long usedMemory = 0L;
            while (databaseCursor.getNext(dbKey, dbData, READ_UNCOMMITTED).equals(OperationStatus.SUCCESS) &&
                    usedMemory < maxMemoryOccupation) {
                ByteArrayInputStream in = new ByteArrayInputStream(dbData.getData());
                ObjectInputStream is = new ObjectInputStream(in);
                Data data = (Data) is.readObject();
                foundData.add(new Data(data.getInternalId(), data.getName(), data.getDate(), data.getContent()));
                is.close();
                in.close();
                usedMemory += data.getContent().length;
            }
            if (usedMemory > maxMemoryOccupation) {
                LOGGER.warn("Memory occupation limit");
            }
            LOGGER.info("Found ids: {}", foundData.size());
            return foundData;
        }
	}

	@Override
	public Optional<Data> findById(String dataId) throws DatabaseException, IOException, ClassNotFoundException {
        LOGGER.info("Find id: {}", dataId);
		DatabaseEntry dbKey = new DatabaseEntry(dataId.getBytes());
		DatabaseEntry dbData = new DatabaseEntry();
		OperationStatus operationStatus = database.get(null, dbKey, dbData, READ_UNCOMMITTED);
		if (operationStatus.equals(OperationStatus.SUCCESS)) {
			ByteArrayInputStream in = new ByteArrayInputStream(dbData.getData());
			ObjectInputStream is = new ObjectInputStream(in);
			Data data = (Data) is.readObject();
			is.close();
			in.close();
			storageService.saveFile(data);
            LOGGER.info("Found id: {}", dataId);
			return Optional.of(new Data(data.getInternalId(), data.getName(), data.getDate(), data.getContent()));
		}
        LOGGER.info("Not found id: {}", dataId);
		return Optional.empty();
	}

	@Override
	public List<Data> findByFilename(String match) throws DatabaseException, IOException, ClassNotFoundException {
        LOGGER.info("Find matching: {}", match);
		List<Data> foundData = new ArrayList<>();
		long usedMemory = 0L;
		for (Map.Entry<String, String> dataEntry : indexFileNames.entrySet()) {
			if (indexFileNames.get(dataEntry.getKey()).contains(match)) {
				DatabaseEntry dbKey = new DatabaseEntry(dataEntry.getKey().getBytes());
				DatabaseEntry dbData = new DatabaseEntry();
				OperationStatus operationStatus = database.get(null, dbKey, dbData, READ_UNCOMMITTED);
				if (operationStatus.equals(OperationStatus.SUCCESS)) {
					ByteArrayInputStream in = new ByteArrayInputStream(dbData.getData());
					ObjectInputStream is = new ObjectInputStream(in);
					Data data = (Data) is.readObject();
					usedMemory += data.getContent().length;
					if (data.getName().contains(match)) {
						foundData.add(new Data(data.getInternalId(), data.getName(), data.getDate(),
								data.getContent()));
					}
					is.close();
					in.close();
				}
			}
			if (usedMemory > maxMemoryOccupation) {
				return memoryOccupationHit(foundData);
			}
		}
        LOGGER.info("Found matching: {}", foundData.size());
		return foundData;
	}

	@Override
	public boolean delete(String dataId) throws DatabaseException {
        LOGGER.info("Delete id: {}", dataId);
		if (indexFileNames.containsKey(dataId)) {
			DatabaseEntry dbKey = new DatabaseEntry(dataId.getBytes());
			database.delete(null, dbKey);
            LOGGER.info("Deleted id: {}", dataId);
			return true;
		} else {
            LOGGER.info("Not found id: {}", dataId);
			return false;
		}
	}

	@Override
	public void close() throws DatabaseException {
		database.close();
		environment.cleanLog();
		environment.close();
        LOGGER.info("Close database {}", databaseName);
	}

	@Override
	public void rebuildIndex() throws IOException, ClassNotFoundException {
		LOGGER.info("Building indexes START");
        try (Cursor databaseCursor = database.openCursor(null, null)) {
            DatabaseEntry dbKey = new DatabaseEntry();
            DatabaseEntry dbData = new DatabaseEntry();
            while (databaseCursor.getNext(dbKey, dbData, READ_UNCOMMITTED).equals(OperationStatus.SUCCESS)) {
                ByteArrayInputStream in = new ByteArrayInputStream(dbData.getData());
                ObjectInputStream is = new ObjectInputStream(in);
                Data data = (Data) is.readObject();
                indexFileNames.put(new String(dbKey.getData()), data.getName());
                is.close();
                in.close();
            }
        }
        LOGGER.info("Building indexes END");
	}
}
