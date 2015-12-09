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
import org.systemexception.simplexdb.domain.DataId;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
	private final HTreeMap<DataId, byte[]> databaseMap;
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
		logger.info(LogMessages.SAVE + data.getDataId().getDataId());
		if (databaseMap.containsKey(data.getDataId())) {
			logger.info(LogMessages.SAVE_CONFLICT + data.getDataId().getDataId());
			return false;
		} else {
			databaseMap.put(data.getDataId(), data.getDataData());
			logger.info(LogMessages.SAVED + data.getDataId().getDataId());
			newData = true;
			return true;
		}
	}

	@Override
	public List<DataId> findAll() {
		logger.info(LogMessages.FIND_ALL_IDS.toString());
		List<DataId> dataIds = new ArrayList<>();
		for (DataId dataId : databaseMap.keySet()) {
			dataIds.add(dataId);
		}
		logger.info(LogMessages.FOUND_ID.toString() + dataIds.size());
		return dataIds;
	}

	@Override
	public Optional<Data> findById(DataId dataId) {
		logger.info(LogMessages.FIND_ID + dataId.getDataId());
		if (databaseMap.containsKey(dataId)) {
			logger.info(LogMessages.FOUND_ID + dataId.getDataId());
			return Optional.of(new Data(dataId, databaseMap.get(dataId)));
		} else {
			logger.info(LogMessages.FOUND_NOT_ID + dataId.getDataId());
			return Optional.empty();
		}
	}

	@Override
	public List<DataId> findByFilename(final String match) {
		logger.info(LogMessages.FIND_MATCH + match);
		ArrayList<DataId> foundItems = new ArrayList<>();
		for (DataId dataId : databaseMap.keySet()) {
			if (dataId.getDataId().contains(match)) {
				foundItems.add(dataId);
			}
		}
		logger.info(LogMessages.FOUND_MATCHING.toString() + foundItems.size());
		return foundItems;
	}

	@Override
	public boolean delete(DataId dataId) {
		logger.info(LogMessages.DELETE + dataId.getDataId());
		if (databaseMap.containsKey(dataId)) {
			databaseMap.remove(dataId);
			database.delete(dataId.getDataId());
			newData = true;
			logger.info(LogMessages.DELETED + dataId.getDataId());
			return true;
		} else {
			logger.info(LogMessages.FOUND_NOT_ID + dataId.getDataId());
			return false;
		}
	}

	@Override
	public void export() {
		throw new NotImplementedException();
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
