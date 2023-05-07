package org.systemexception.simplexdb.database.impl;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;
import org.systemexception.simplexdb.constants.LogMessages;
import org.systemexception.simplexdb.database.AbstractDbService;
import org.systemexception.simplexdb.domain.Data;
import org.systemexception.simplexdb.service.StorageServiceApi;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author leo
 * @date 05/12/15 00:45
 */
public class MapDbService extends AbstractDbService {

	private final DB database;
	private final HTreeMap<String, Data> databaseMap;

	public MapDbService(final StorageServiceApi storageService, final String databaseName,
	                    final Long maxMemoryOccupation) {
		this.databaseName = databaseName;
		this.maxMemoryOccupation = maxMemoryOccupation;
		this.storageService = storageService;
		logger.info(LogMessages.CREATE_DATABASE + databaseName);
		database = makeDatabase();
		databaseMap = database.hashMap("dataCollection").keySerializer(Serializer.STRING)
				.valueSerializer(Serializer.JAVA).createOrOpen();
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
			database.commit();
			logger.info(LogMessages.COMMIT_MESSAGE.toString());
			return true;
		}
	}

	@Override
	public List<Data> findAll() {
		logger.info(LogMessages.FIND_ALL_IDS.toString());
		List<Data> foundData = new ArrayList<>();
		long usedMemory = 0L;
		Iterator<String> iterator = databaseMap.keySet().iterator();
		while (iterator.hasNext() && usedMemory < maxMemoryOccupation) {
			String next = iterator.next();
			Data data = databaseMap.get(next);
			foundData.add(data);
			usedMemory += data != null ? data.getContent().length : 0;
		}
		if (usedMemory > maxMemoryOccupation) {
			logger.warn(LogMessages.MEMORY_OCCUPATION_HIT.toString());
		}
		logger.info(LogMessages.FOUND_ID.toString() + foundData.size());
		return foundData;
	}

	@Override
	public Optional<Data> findById(String dataId) throws IOException {
		logger.info(LogMessages.FIND_ID + dataId);
		if (databaseMap.containsKey(dataId)) {
			logger.info(LogMessages.FOUND_ID + dataId);
			Data data = databaseMap.get(dataId);
			storageService.saveFile(data);
			return Optional.ofNullable(data);
		} else {
			logger.info(LogMessages.FOUND_NOT_ID + dataId);
			return Optional.empty();
		}
	}

	@Override
	public List<Data> findByFilename(final String match) {
		logger.info(LogMessages.FIND_MATCH + match);
		ArrayList<Data> foundData = new ArrayList<>();
		long usedMemory = 0L;
        for (Map.Entry<String, Data> dataEntry : databaseMap.entrySet()) {
			if (databaseMap.get(dataEntry.getKey()).getName().contains(match)) {
				Data data = databaseMap.get(dataEntry.getKey());
				usedMemory += data != null ? data.getContent().length : 0;
				foundData.add(data);
			}
			if (usedMemory > maxMemoryOccupation) {
				return memoryOccupationHit(foundData);
			}
		}
		logger.info(LogMessages.FOUND_MATCHING.toString() + foundData.size());
		return foundData;
	}

	@Override
	public boolean delete(String dataId) {
		logger.info(LogMessages.DELETE + dataId);
		if (databaseMap.containsKey(dataId)) {
			databaseMap.remove(dataId);
			logger.info(LogMessages.DELETED + dataId);
			database.commit();
			logger.info(LogMessages.COMMIT_MESSAGE.toString());
			return true;
		} else {
			logger.info(LogMessages.FOUND_NOT_ID + dataId);
			return false;
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
