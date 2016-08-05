package org.systemexception.simplexdb.database.impl;

import com.sleepycat.je.*;
import org.systemexception.simplexdb.constants.LogMessages;
import org.systemexception.simplexdb.database.AbstractDbService;
import org.systemexception.simplexdb.domain.Data;
import org.systemexception.simplexdb.service.StorageServiceApi;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

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
		logger.info(LogMessages.CREATE_DATABASE + databaseName);
		EnvironmentConfig envConfig = new EnvironmentConfig();
		envConfig.setConfigParam("je.log.fileMax", "256000000");
		envConfig.setTransactional(true);
		envConfig.setAllowCreate(true);
		File productionDatabaseFile = new File(databaseName);
		if (!productionDatabaseFile.exists()) {
			boolean mkdir = productionDatabaseFile.mkdir();
			if (!mkdir) {
				String errorMessage = "Database directory creation failed";
				logger.error(errorMessage);
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
		logger.info(LogMessages.SAVE + data.getName());
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
			logger.info(LogMessages.SAVED + data.getName());
			return true;
		}
	}

	@Override
	public List<Data> findAll() throws DatabaseException, IOException, ClassNotFoundException {
		logger.info(LogMessages.FIND_ALL_IDS.toString());
		Cursor databaseCursor = database.openCursor(null, null);
		List<Data> foundData = new ArrayList<>();
		DatabaseEntry dbKey = new DatabaseEntry();
		DatabaseEntry dbData = new DatabaseEntry();
		Long usedMemory = 0L;
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
			logger.warn(LogMessages.MEMORY_OCCUPATION_HIT.toString());
		}
		databaseCursor.close();
		logger.info(LogMessages.FOUND_ID.toString() + foundData.size());
		return foundData;
	}

	@Override
	public Optional<Data> findById(String dataId) throws DatabaseException, IOException, ClassNotFoundException {
		logger.info(LogMessages.FIND_ID + dataId);
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
			logger.info(LogMessages.FOUND_ID + dataId);
			return Optional.of(new Data(data.getInternalId(), data.getName(), data.getDate(), data.getContent()));
		}
		logger.info(LogMessages.FOUND_NOT_ID + dataId);
		return Optional.empty();
	}

	@Override
	public List<Data> findByFilename(String match) throws DatabaseException, IOException, ClassNotFoundException {
		logger.info(LogMessages.FIND_MATCH + match);
		List<Data> foundData = new ArrayList<>();
		Long usedMemory = 0L;
		for (String dataId : indexFileNames.keySet()) {
			if (indexFileNames.get(dataId).contains(match)) {
				DatabaseEntry dbKey = new DatabaseEntry(dataId.getBytes());
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
		logger.info(LogMessages.FOUND_MATCHING.toString() + foundData.size());
		return foundData;
	}

	@Override
	public boolean delete(String dataId) throws DatabaseException {
		logger.info(LogMessages.DELETE + dataId);
		if (indexFileNames.containsKey(dataId)) {
			DatabaseEntry dbKey = new DatabaseEntry(dataId.getBytes());
			database.delete(null, dbKey);
			logger.info(LogMessages.DELETED + dataId);
			return true;
		} else {
			logger.info(LogMessages.FOUND_NOT_ID + dataId);
			return false;
		}
	}

	@Override
	public void close() throws DatabaseException {
		database.close();
		environment.cleanLog();
		environment.close();
		logger.info(LogMessages.CLOSE_DATABASE + databaseName);
	}

	@Override
	public void rebuildIndex() throws IOException, ClassNotFoundException {
		logger.info(LogMessages.INDEX_BUILD_START.toString());
		Cursor databaseCursor = database.openCursor(null, null);
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
		databaseCursor.close();
		logger.info(LogMessages.INDEX_BUILD_END.toString());
	}
}
