package org.systemexception.simplexdb.database.impl;

import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.systemexception.simplexdb.constants.LogMessages;
import org.systemexception.simplexdb.database.AbstractDbService;
import org.systemexception.simplexdb.domain.Data;
import org.systemexception.simplexdb.service.StorageServiceApi;

import javax.annotation.PreDestroy;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author cappuccio
 * @date 15/03/16 09:59
 */
public class OrientDbService extends AbstractDbService {

	private final OObjectDatabaseTx database;

	public OrientDbService(final StorageServiceApi storageService, final String databaseName,
	                       final Long maxMemoryOccupation) {

		this.databaseName = databaseName;
		this.maxMemoryOccupation = maxMemoryOccupation;
		this.storageService = storageService;
		logger.info(LogMessages.CREATE_DATABASE + databaseName);
		String dbPath = System.getProperty("user.dir") + File.separator + databaseName;
		File dbFile = new File(dbPath);
		Orient.instance().startup();
		if (dbFile.exists()) {
			database = new OObjectDatabaseTx("plocal:" + dbPath).open("admin", "admin");
		} else {
			database = new OObjectDatabaseTx("plocal:" + dbPath).create();
		}
		database.getEntityManager().registerEntityClasses("org.systemexception.simplexdb.domain");
		database.getMetadata().getSchema().save();
		database.activateOnCurrentThread();
	}

	@Override
	public boolean save(Data data) {
		database.activateOnCurrentThread();
		database.getEntityManager().registerEntityClass(Data.class);
		for (Data innerData : database.browseClass(Data.class)) {
			if (innerData.getInternalId().equals(data.getInternalId())) {
				return false;
			}
		}
		database.save(data);
		return true;
	}

	@Override
	public List<Data> findAll() {
		database.activateOnCurrentThread();
		List<Data> dataList = new ArrayList<>();
		for (Data data : database.browseClass(Data.class)) {
			Data outData = new Data();
			outData.setInternalId(data.getInternalId());
			outData.setContent(data.getContent());
			outData.setDate(data.getDate());
			outData.setName(data.getName());
			outData.setSize(data.getSize());
			dataList.add(outData);
		}
		return dataList;
	}

	@Override
	public Optional<Data> findById(String dataId) {
		database.activateOnCurrentThread();
		database.getEntityManager().registerEntityClass(Data.class);
		for (Data data : database.browseClass(Data.class)) {
			if (dataId.equals(data.getInternalId())) {
				Data outData = new Data();
				outData.setInternalId(data.getInternalId());
				outData.setContent(data.getContent());
				outData.setDate(data.getDate());
				outData.setName(data.getName());
				outData.setSize(data.getSize());
				return Optional.of(outData);
			}
		}
		return Optional.empty();
	}

	@Override
	public List<Data> findByFilename(final String match) {
		database.activateOnCurrentThread();
		List<Data> foundData = new ArrayList<>();
		for (Data data : database.browseClass(Data.class)) {
			if (data.getName().contains(match)) {
				Data outData = new Data();
				outData.setInternalId(data.getInternalId());
				outData.setContent(data.getContent());
				outData.setDate(data.getDate());
				outData.setName(data.getName());
				outData.setSize(data.getSize());
				foundData.add(outData);
			}
		}
		return foundData;
	}

	@Override
	public boolean delete(String dataId) {
		database.activateOnCurrentThread();
		database.getEntityManager().registerEntityClass(Data.class);
		for (Data data : database.browseClass(Data.class)) {
			if (dataId.equals(data.getInternalId())) {
				database.delete(data);
				return true;
			}
		}
		return false;
	}

	@Override
	public void commit() {
		database.activateOnCurrentThread();
		database.commit();
		logger.info(LogMessages.COMMIT_MESSAGE.toString());
	}

	@PreDestroy
	@Override
	public void close() {
		database.activateOnCurrentThread();
		database.commit();
		database.close();
		Orient.instance().shutdown();
		logger.info(LogMessages.CLOSE_DATABASE + databaseName);
	}
}
