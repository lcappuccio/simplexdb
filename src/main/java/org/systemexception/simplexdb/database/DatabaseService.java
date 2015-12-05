package org.systemexception.simplexdb.database;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.systemexception.simplexdb.domain.DataId;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author leo
 * @date 05/12/15 00:45
 */
@Component
public class DatabaseService implements DatabaseApi {

	@Value("${database.filename}")
	private String databaseName;
	private final DB database;
	private final HTreeMap<DataId, byte[]> databaseMap;

	public DatabaseService() {
		System.out.println(databaseName);
		database = DBMaker.fileDB(new File(databaseName)).closeOnJvmShutdown().make();
		databaseMap = database.hashMap("dataCollection");
	}

	@Override
	public void save() {
		throw new UnsupportedOperationException();
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
	public void findById() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() {
		throw new UnsupportedOperationException();
	}
}
