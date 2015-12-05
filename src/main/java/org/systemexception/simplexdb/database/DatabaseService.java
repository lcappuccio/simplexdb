package org.systemexception.simplexdb.database;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.systemexception.simplexdb.domain.Data;
import org.systemexception.simplexdb.domain.DataId;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author leo
 * @date 05/12/15 00:45
 */
@Component
public class DatabaseService implements DatabaseApi {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final DB database;
	private HTreeMap<DataId, byte[]> databaseMap;

	public DatabaseService(final String databaseName) {
		logger.info("Creating database " + databaseName);
		database = DBMaker.fileDB(new File(databaseName)).transactionDisable().closeOnJvmShutdown().make();
		databaseMap = database.hashMap("dataCollection");
	}

	@Override
	public boolean save(Data data) {
		logger.info("Save " + data.getDataId().getDataId());
		if (databaseMap.containsKey(data.getDataId())) {
			logger.info(data.getDataId().getDataId() + " already exists");
			return false;
		} else {
			databaseMap.put(data.getDataId(), data.getDataData());
			database.commit();
			logger.info(data.getDataId().getDataId() + " saved");
			return true;
		}
	}

	@Override
	public List<DataId> findAll() {
		logger.info("Find all ids");
		List<DataId> dataIds = new ArrayList<>();
		for (DataId dataId: databaseMap.keySet()) {
			dataIds.add(dataId);
		}
		logger.info("Found " + dataIds.size());
		return dataIds;
	}

	@Override
	public Optional<Data> findById(DataId dataId) {
		logger.info("Find " + dataId);
		if (databaseMap.containsKey(dataId)) {
			logger.info(dataId.getDataId() + " found");
			return Optional.of(new Data(dataId, databaseMap.get(dataId)));
		} else {
			logger.info(dataId.getDataId() + " not found");
			return Optional.empty();
		}
	}

	@Override
	public boolean delete(DataId dataId) {
		logger.info("Delete " + dataId.getDataId());
		if (databaseMap.containsKey(dataId)) {
			databaseMap.remove(dataId);
			database.delete(dataId.getDataId());
			database.commit();
			logger.info(dataId.getDataId() + " removed");
			return true;
		} else {
			logger.info(dataId.getDataId() + " not found");
			return false;
		}
	}

	@Override
	public boolean closeDatabase() {
		database.commit();
		database.compact();
		database.close();
		logger.info("Database closed");
		return database.isClosed();
	}
}
