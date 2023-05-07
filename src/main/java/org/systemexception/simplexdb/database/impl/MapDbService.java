package org.systemexception.simplexdb.database.impl;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;
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
		LOGGER.info("Open database: {}", databaseName);
		database = makeDatabase();
		databaseMap = database.hashMap("dataCollection").keySerializer(Serializer.STRING)
				.valueSerializer(Serializer.JAVA).createOrOpen();
	}

	private DB makeDatabase() {
		return DBMaker.fileDB(new File(databaseName)).make();
	}

	@Override
	public boolean save(Data data) {
		LOGGER.info("Save: {}", data.getName());
		if (databaseMap.containsKey(data.getInternalId())) {
			LOGGER.warn("Save conflict: {}", data.getName());
			return false;
		} else {
			databaseMap.put(data.getInternalId(), data);
			LOGGER.info("Saved: {}", data.getName());
			database.commit();
			LOGGER.info("Commit");
			return true;
		}
	}

	@Override
	public List<Data> findAll() {
		LOGGER.info("Find all ids");
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
            LOGGER.warn("Memory occupation limit");
		}
		LOGGER.info("Found ids: {}", foundData.size());
		return foundData;
	}

	@Override
	public Optional<Data> findById(String dataId) throws IOException {
		LOGGER.info("Find id: {}", dataId);
		if (databaseMap.containsKey(dataId)) {
			LOGGER.info("Found id: {}", dataId);
			Data data = databaseMap.get(dataId);
			storageService.saveFile(data);
			return Optional.ofNullable(data);
		} else {
            LOGGER.info("Not found id: {}", dataId);
			return Optional.empty();
		}
	}

	@Override
	public List<Data> findByFilename(final String match) {
		LOGGER.info("Find matching: {}", match);
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
		LOGGER.info("Found matching: {}", foundData.size());
		return foundData;
	}

	@Override
	public boolean delete(String dataId) {
		LOGGER.info("Delete id: {}", dataId);
		if (databaseMap.containsKey(dataId)) {
			databaseMap.remove(dataId);
			LOGGER.info("Deleted id: {}", dataId);
			database.commit();
			LOGGER.info("Committing");
			return true;
		} else {
			LOGGER.info("Not found id: {}", dataId);
			return false;
		}
	}

	@PreDestroy
	@Override
	public void close() {
		database.commit();
		database.close();
		LOGGER.info("Close database {}", databaseName);
	}
}
