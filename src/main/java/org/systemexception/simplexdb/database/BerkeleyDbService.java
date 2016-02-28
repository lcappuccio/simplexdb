package org.systemexception.simplexdb.database;

import com.sleepycat.je.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.systemexception.simplexdb.constants.LogMessages;
import org.systemexception.simplexdb.domain.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.sleepycat.je.LockMode.*;
import static com.sleepycat.je.LockMode.DEFAULT;

/**
 * @author leo
 * @date 28/02/16 11:55
 */
public class BerkeleyDbService implements Api {

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
		environment = new Environment(new File(databaseName), envConfig);
		DatabaseConfig databaseConfig = new DatabaseConfig();
		databaseConfig.setAllowCreate(true);
		databaseConfig.setTransactional(true);
		database = environment.openDatabase(null, databaseName, databaseConfig);
	}

	@Override
	public boolean save(Data data) throws DatabaseException {
		logger.info(LogMessages.SAVE + data.getDataName());
		DatabaseEntry dbKey = new DatabaseEntry(data.getDataName().getBytes());
		DatabaseEntry dbData = new DatabaseEntry(data.getDataData());
		OperationStatus operationStatus = database.get(null, dbKey, dbData, READ_UNCOMMITTED);
		if (!operationStatus.equals(OperationStatus.NOTFOUND)) {
			return false;
		} else {
			OperationStatus result = database.put(null, dbKey, dbData);
			if (result.equals(OperationStatus.SUCCESS)) {
				logger.info(LogMessages.SAVED + data.getDataName());
				return true;
			} else {
				return false;
			}
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
			String key = new String(dbKey.getData());
			foundData.add(new Data(key, dbData.getData()));
		}
		databaseCursor.close();
		logger.info(LogMessages.FOUND_ID.toString() + foundData.size());
		return foundData;
	}

	@Override
	public Optional<Data> findById(String dataId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Data> findByFilename(String match) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean delete(String dataId) {
		throw new UnsupportedOperationException();
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
