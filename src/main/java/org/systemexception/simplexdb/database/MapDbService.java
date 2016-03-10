package org.systemexception.simplexdb.database;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.systemexception.simplexdb.constants.LogMessages;
import org.systemexception.simplexdb.domain.Data;

import javax.annotation.PreDestroy;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author leo
 * @date 05/12/15 00:45
 */
public class MapDbService implements DatabaseApi {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final DB database;
	private final HTreeMap<String, Data> databaseMap;
	private final String databaseName;

	public MapDbService(final String databaseName) {
		this.databaseName = databaseName;
		logger.info(LogMessages.CREATE_DATABASE + databaseName);
		database = makeDatabase();
		databaseMap = database.hashMap("dataCollection");
	}

	private DB makeDatabase() {
		return DBMaker.fileDB(new File(databaseName)).make();
	}

	@Override
	public boolean save(Data data) {
		logger.info(LogMessages.SAVE + data.getName());
		if (databaseMap.containsKey(data.getInternalId())) {
			logger.info(LogMessages.SAVE_CONFLICT + data.getName());
			return false;
		} else {
			databaseMap.put(data.getInternalId(), data);
			logger.info(LogMessages.SAVED + data.getName());
			return true;
		}
	}

	@Override
	public List<Data> findAll() {
		logger.info(LogMessages.FIND_ALL_IDS.toString());
		List<Data> foundData = new ArrayList<>();
		for (String dataId : databaseMap.keySet()) {
			foundData.add(new Data(dataId, databaseMap.get(dataId).getName(),
					databaseMap.get(dataId).getContent()));
		}
		logger.info(LogMessages.FOUND_ID.toString() + foundData.size());
		return foundData;
	}

	@Override
	public Optional<Data> findById(String dataId) {
		logger.info(LogMessages.FIND_ID + dataId);
		if (databaseMap.containsKey(dataId)) {
			logger.info(LogMessages.FOUND_ID + dataId);
			return Optional.of(new Data(dataId, databaseMap.get(dataId).getName(),
					databaseMap.get(dataId).getContent()));
		} else {
			logger.info(LogMessages.FOUND_NOT_ID + dataId);
			return Optional.empty();
		}
	}

	@Override
	public List<Data> findByFilename(final String match) {
		logger.info(LogMessages.FIND_MATCH + match);
		ArrayList<Data> foundItems = new ArrayList<>();
		for (String dataId : databaseMap.keySet()) {
			if (databaseMap.get(dataId).getName().contains(match)) {
				foundItems.add(databaseMap.get(dataId));
			}
		}
		logger.info(LogMessages.FOUND_MATCHING.toString() + foundItems.size());
		return foundItems;
	}

	@Override
	public boolean delete(String dataId) {
		logger.info(LogMessages.DELETE + dataId);
		if (databaseMap.containsKey(dataId)) {
			databaseMap.remove(dataId);
			database.delete(dataId);
			logger.info(LogMessages.DELETED + dataId);
			return true;
		} else {
			logger.info(LogMessages.FOUND_NOT_ID + dataId);
			return false;
		}
	}

	@Override
	public void commit() {
		database.commit();
		logger.info(LogMessages.COMMIT_MESSAGE.toString());
	}

	@PreDestroy
	@Override
	public void close() {
		database.commit();
		database.close();
		logger.info(LogMessages.CLOSE_DATABASE + databaseName);
	}
}
