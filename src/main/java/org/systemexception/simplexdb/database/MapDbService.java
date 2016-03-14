package org.systemexception.simplexdb.database;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.systemexception.simplexdb.constants.LogMessages;
import org.systemexception.simplexdb.domain.Data;
import org.systemexception.simplexdb.service.StorageServiceApi;

import javax.annotation.PreDestroy;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
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
	private final Long maxMemoryOccupation;
	private final StorageServiceApi storageService;

	public MapDbService(final StorageServiceApi storageService, final String databaseName,
	                    final Long maxMemoryOccupation) {
		this.databaseName = databaseName;
		logger.info(LogMessages.CREATE_DATABASE + databaseName);
		database = makeDatabase();
		databaseMap = database.hashMap("dataCollection");
		this.maxMemoryOccupation = maxMemoryOccupation;
		this.storageService = storageService;
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
		Long usedMemory = 0L;
		Iterator<String> iterator = databaseMap.keySet().iterator();
		while (iterator.hasNext() && usedMemory < maxMemoryOccupation) {
			String next = iterator.next();
			Data data = databaseMap.get(next);
			foundData.add(data);
			usedMemory += data.getContent().length;
		}
		if (usedMemory > maxMemoryOccupation) {
			logger.warn(LogMessages.MEMORY_OCCUPATION_HIT.toString());
		}
		logger.info(LogMessages.FOUND_ID.toString() + foundData.size());
		return foundData;
	}

	@Override
	public Optional<Data> findById(String dataId) {
		logger.info(LogMessages.FIND_ID + dataId);
		if (databaseMap.containsKey(dataId)) {
			logger.info(LogMessages.FOUND_ID + dataId);
			Data data = databaseMap.get(dataId);
			storageService.saveFile(data);
			return Optional.of(data);
		} else {
			logger.info(LogMessages.FOUND_NOT_ID + dataId);
			return Optional.empty();
		}
	}

	@Override
	// TODO LC Add memory occupation checks
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
