package org.systemexception.simplexdb.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.systemexception.simplexdb.constants.LogMessages;
import org.systemexception.simplexdb.domain.Data;
import org.systemexception.simplexdb.service.StorageServiceApi;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * @author cappuccio
 * @date 15/03/16 09:50
 */
public class AbstractDbService implements DatabaseApi {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	protected String databaseName;
	protected Long maxMemoryOccupation;
	protected StorageServiceApi storageService;

	@Override
	public boolean save(Data data) {
		return false;
	}

	@Override
	public List<Data> findAll() {
		return null;
	}

	@Override
	public Optional<Data> findById(String dataId) throws IOException {
		return null;
	}

	@Override
	public List<Data> findByFilename(String match) {
		return null;
	}

	@Override
	public boolean delete(String dataId) {
		return false;
	}

	@Override
	public void close() {
		// See implementation
	}

	@Override
	public void rebuildIndex() {
		// See implementation
	}

	/**
	 * Checks if memory occupation limit has been hit when searching for filename
	 *
	 * @param dataList will be cleared and a single warning item will be returned
	 * @return
	 */
	protected List<Data> memoryOccupationHit(List<Data> dataList) {
		logger.warn(LogMessages.MEMORY_OCCUPATION_HIT.toString());
		dataList.clear();
		Data warningData = new Data();
		warningData.setInternalId("WARNING");
		warningData.setName("Please narrow your search");
		dataList.add(warningData);
		return dataList;
	}
}
