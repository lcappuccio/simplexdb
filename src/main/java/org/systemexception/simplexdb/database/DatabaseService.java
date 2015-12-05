package org.systemexception.simplexdb.database;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

	private final DB database;
	private HTreeMap<DataId, byte[]> databaseMap;

	@Autowired
	public DatabaseService(@Value("${database.filename}") final String databaseName) {
		database = DBMaker.fileDB(new File(databaseName)).transactionDisable().closeOnJvmShutdown().make();
		databaseMap = database.hashMap("dataCollection");
	}

	@Override
	public boolean save(Data data) {
		if (databaseMap.containsKey(data.getDataId())) {
			return false;
		} else {
			databaseMap.put(data.getDataId(), data.getDataData());
			database.commit();
			return true;
		}
	}

	@Override
	public List<DataId> findAll() {
		List<DataId> dataIds = new ArrayList<>();
		for (DataId dataId: databaseMap.keySet()) {
			dataIds.add(dataId);
		}
		return dataIds;
	}

	@Override
	public Optional<Data> findById(DataId dataId) {
		if (databaseMap.containsKey(dataId)) {
			return Optional.of(new Data(dataId, databaseMap.get(dataId)));
		} else {
			return Optional.empty();
		}
	}

	@Override
	public boolean delete(DataId dataId) {
		if (databaseMap.containsKey(dataId)) {
			databaseMap.remove(dataId);
			database.delete(dataId.getDataId());
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean close() {
		database.commit();
		database.compact();
		database.close();
		return database.isClosed();
	}
}
