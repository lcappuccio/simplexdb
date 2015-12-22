package org.systemexception.simplexdb.database;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
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
@Service
public class DatabaseService implements DatabaseApi {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final DB database;
	private final HTreeMap<String, Data> databaseMap;
	private final String databaseName;
	private Boolean newData = false;

	public DatabaseService(final String databaseName) {
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
		logger.info(LogMessages.SAVE + data.getDataName());
		if (databaseMap.containsKey(data.getDataInternalId())) {
			logger.info(LogMessages.SAVE_CONFLICT + data.getDataName());
			return false;
		} else {
			databaseMap.put(data.getDataInternalId(), data);
			logger.info(LogMessages.SAVED + data.getDataName());
			newData = true;
			return true;
		}
	}

	@Override
	public List<Data> findAll() {
		logger.info(LogMessages.FIND_ALL_IDS.toString());
		List<Data> foundData = new ArrayList<>();
		for (String dataId : databaseMap.keySet()) {
			foundData.add(new Data(dataId, databaseMap.get(dataId).getDataName(), databaseMap.get(dataId).getDataData()));
		}
		logger.info(LogMessages.FOUND_ID.toString() + foundData.size());
		return foundData;
	}

	@Override
	public Optional<Data> findById(String dataId) {
		logger.info(LogMessages.FIND_ID + dataId);
		if (databaseMap.containsKey(dataId)) {
			logger.info(LogMessages.FOUND_ID + dataId);
			return Optional.of(new Data(dataId, databaseMap.get(dataId).getDataName(),
					databaseMap.get(dataId).getDataData()));
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
			if (databaseMap.get(dataId).getDataName().contains(match)) {
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
			newData = true;
			logger.info(LogMessages.DELETED + dataId);
			return true;
		} else {
			logger.info(LogMessages.FOUND_NOT_ID + dataId);
			return false;
		}
	}

	@Scheduled(cron = "${database.commit.frequency}")
	public void commit() {
		if (newData) {
			database.commit();
			logger.info(LogMessages.SCHEDULED_COMMIT.toString());
			newData = false;
		}
	}

	@PreDestroy
	@Override
	public void close() {
		database.commit();
		database.close();
		logger.info(LogMessages.CLOSE_DATABASE + databaseName);
	}
}
