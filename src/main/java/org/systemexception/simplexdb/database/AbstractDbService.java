package org.systemexception.simplexdb.database;

import com.sleepycat.je.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.systemexception.simplexdb.domain.Data;
import org.systemexception.simplexdb.service.StorageServiceApi;

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
	public boolean save(Data data) throws DatabaseException {
		return false;
	}

	@Override
	public List<Data> findAll() throws DatabaseException {
		return null;
	}

	@Override
	public Optional<Data> findById(String dataId) throws DatabaseException {
		return null;
	}

	@Override
	public List<Data> findByFilename(String match) throws DatabaseException {
		return null;
	}

	@Override
	public boolean delete(String dataId) throws DatabaseException {
		return false;
	}

	@Override
	public void close() throws DatabaseException {

	}

	@Override
	public void commit() {

	}
}
