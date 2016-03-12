package org.systemexception.simplexdb.database;

import com.sleepycat.je.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.systemexception.simplexdb.constants.LogMessages;
import org.systemexception.simplexdb.domain.Data;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.sleepycat.je.LockMode.READ_COMMITTED;
import static com.sleepycat.je.LockMode.READ_UNCOMMITTED;

/**
 * @author leo
 * @date 28/02/16 11:55
 */
public class BerkeleyDbService implements DatabaseApi {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final Environment environment;
	private final Database database;
	private final String databaseName;

	public BerkeleyDbService(final String databaseName) {
		this.databaseName = databaseName;
		logger.info(LogMessages.CREATE_DATABASE + databaseName);
		EnvironmentConfig envConfig = new EnvironmentConfig();
		envConfig.setConfigParam("je.log.fileMax", "256000000");
		envConfig.setTransactional(true);
		envConfig.setAllowCreate(true);
		File productionDatabaseFile = new File(databaseName);
		if (!productionDatabaseFile.exists()) {
			boolean mkdir = productionDatabaseFile.mkdir();
			if (!mkdir) {
				logger.error("Database directory creation failed");
			}
		}
		environment = new Environment(new File(databaseName), envConfig);
		DatabaseConfig databaseConfig = new DatabaseConfig();
		databaseConfig.setAllowCreate(true);
		databaseConfig.setTransactional(true);
		database = environment.openDatabase(null, databaseName, databaseConfig);
	}

	@Override
	public boolean save(Data data) throws DatabaseException {
		logger.info(LogMessages.SAVE + data.getName());
		DatabaseEntry dbKey = new DatabaseEntry(data.getInternalId().getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			ObjectOutputStream os = new ObjectOutputStream(out);
			os.writeObject(data);
			out.toByteArray();
			os.close();
			out.close();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		DatabaseEntry dbData = new DatabaseEntry(out.toByteArray());
		OperationStatus operationStatus = database.get(null, dbKey, dbData, READ_UNCOMMITTED);
		if (!operationStatus.equals(OperationStatus.NOTFOUND)) {
			return false;
		} else {
			database.put(null, dbKey, dbData);
			logger.info(LogMessages.SAVED + data.getName());
			return true;
		}
	}

	@Override
	public List<Data> findAll() throws DatabaseException {
		logger.info(LogMessages.FIND_ALL_IDS.toString());
		Cursor databaseCursor = database.openCursor(null, null);
		List<Data> foundData = new ArrayList<>();
		DatabaseEntry dbKey = new DatabaseEntry();
		DatabaseEntry dbData = new DatabaseEntry();
		// TODO LC Heap Space error here, the cursor goes to memory, the data goes to memory, everything goes to memory
		while (databaseCursor.getNext(dbKey, dbData, READ_UNCOMMITTED).equals(OperationStatus.SUCCESS)) {
			try {
				ByteArrayInputStream in = new ByteArrayInputStream(dbData.getData());
				ObjectInputStream is = new ObjectInputStream(in);
				Data data = (Data) is.readObject();
				foundData.add(new Data(data.getInternalId(), data.getName(), data.getDate(), "0".getBytes()));
				is.close();
				in.close();
			} catch (IOException | ClassNotFoundException e) {
				logger.error(e.getMessage());
			}
		}
		databaseCursor.close();
		logger.info(LogMessages.FOUND_ID.toString() + foundData.size());
		return foundData;
	}

	@Override
	public Optional<Data> findById(String dataId) throws DatabaseException {
		logger.info(LogMessages.FIND_ID + dataId);
		Cursor databaseCursor = database.openCursor(null, null);
		DatabaseEntry dbKey = new DatabaseEntry(dataId.getBytes());
		DatabaseEntry dbData = new DatabaseEntry();
		OperationStatus operationStatus = database.get(null, dbKey, dbData, READ_COMMITTED);
		if (operationStatus.equals(OperationStatus.SUCCESS)) {
			try {
				ByteArrayInputStream in = new ByteArrayInputStream(dbData.getData());
				ObjectInputStream is = new ObjectInputStream(in);
				Data data = (Data) is.readObject();
				is.close();
				in.close();
				return Optional.of(new Data(data.getInternalId(), data.getName(), data.getDate(), data.getContent()));
			} catch (IOException | ClassNotFoundException e) {
				logger.error(e.getMessage());
			} finally {
				databaseCursor.close();
			}
		}
		databaseCursor.close();
		logger.info(LogMessages.FOUND_NOT_ID + dataId);
		return Optional.empty();
	}

	@Override
	public List<Data> findByFilename(String match) throws DatabaseException {
		logger.info(LogMessages.FIND_MATCH + match);
		List<Data> allData = findAll();
		ArrayList<Data> foundItems = new ArrayList<>();
		for (final Data data : allData) {
			if (data.getName().contains(match)) {
				foundItems.add(data);
			}
		}
		logger.info(LogMessages.FOUND_MATCHING.toString() + foundItems.size());
		return foundItems;
	}

	@Override
	public boolean delete(String dataId) throws DatabaseException {
		logger.info(LogMessages.DELETE + dataId);
		Optional<Data> dataById = findById(dataId);
		if (dataById.isPresent()) {
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
	public void commit() {
		// Do nothing;
	}
}
