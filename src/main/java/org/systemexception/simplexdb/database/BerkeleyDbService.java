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

import static com.sleepycat.je.LockMode.DEFAULT;
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

	public BerkeleyDbService(final String databaseName) throws DatabaseException {
		this.databaseName = databaseName;
		logger.info(LogMessages.CREATE_DATABASE + databaseName);
		EnvironmentConfig envConfig = new EnvironmentConfig();
		envConfig.setTransactional(true);
		envConfig.setAllowCreate(true);
		File productionDatabaseFile = new File(databaseName);
		if (!productionDatabaseFile.exists()) {
			productionDatabaseFile.mkdir();
		}
		environment = new Environment(new File(databaseName), envConfig);
		DatabaseConfig databaseConfig = new DatabaseConfig();
		databaseConfig.setAllowCreate(true);
		databaseConfig.setTransactional(true);
		database = environment.openDatabase(null, databaseName, databaseConfig);
	}

	@Override
	public boolean save(Data data) throws DatabaseException {
		logger.info(LogMessages.SAVE + data.getDataName());
		DatabaseEntry dbKey = new DatabaseEntry(data.getDataInternalId().getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream os;
		try {
			os = new ObjectOutputStream(out);
			os.writeObject(data);
			out.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		DatabaseEntry dbData = new DatabaseEntry(out.toByteArray());
		OperationStatus operationStatus = database.get(null, dbKey, dbData, READ_UNCOMMITTED);
		if (!operationStatus.equals(OperationStatus.NOTFOUND)) {
			return false;
		} else {
			database.put(null, dbKey, dbData);
			logger.info(LogMessages.SAVED + data.getDataName());
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
		while (databaseCursor.getNext(dbKey, dbData, DEFAULT).equals(OperationStatus.SUCCESS)) {
			Data data;
			try {
				ByteArrayInputStream in = new ByteArrayInputStream(dbData.getData());
				ObjectInputStream is = new ObjectInputStream(in);
				data = (Data) is.readObject();
				foundData.add(new Data(data.getDataInternalId(), data.getDataName(), dbData.getData()));
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		databaseCursor.close();
		logger.info(LogMessages.FOUND_ID.toString() + foundData.size());
		return foundData;
	}

	@Override
	public Optional<Data> findById(String dataId) throws DatabaseException {
		logger.info(LogMessages.FIND_ID + dataId);
		List<Data> allData = findAll();
		for (Data data : allData) {
			if (dataId.equals(data.getDataInternalId())) {
				logger.info(LogMessages.FOUND_ID + dataId);
				Data serializedData = null;
				try {
					ByteArrayInputStream in = new ByteArrayInputStream(data.getDataData());
					ObjectInputStream is = new ObjectInputStream(in);
					data = (Data) is.readObject();
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
				}
				return Optional.of(data);
			}
		}
		logger.info(LogMessages.FOUND_NOT_ID + dataId);
		return Optional.empty();
	}

	@Override
	public List<Data> findByFilename(String match) throws DatabaseException {
		logger.info(LogMessages.FIND_MATCH + match);
		List<Data> allData = findAll();
		ArrayList<Data> foundItems = new ArrayList<>();
		for (Data data : allData) {
			if (data.getDataName().contains(match)) {
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
		environment.close();
		logger.info(LogMessages.CLOSE_DATABASE + databaseName);
	}

	@Override
	public void commit() {
		// Do nothing;
	}
}
