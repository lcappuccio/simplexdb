package org.systemexception.simplexdb.database;

import org.systemexception.simplexdb.domain.Data;
import org.systemexception.simplexdb.domain.DataId;

import java.util.List;
import java.util.Optional;

/**
 * @author leo
 * @date 05/12/15 00:32
 */
public interface DatabaseApi {

	/**
	 * Adds a record to the database
	 */
	boolean save(Data data);

	/**
	 * List all records on database
	 */
	List<DataId> findAll();

	/**
	 * List single record on database
	 */
	Optional<Data> findById(DataId dataId);

	/**
	 * List records matching string
	 */
	List<DataId> findByFilename(String match);

	/**
	 * Remove record from database
	 */
	boolean delete(DataId dataId);

	/**
	 * Close database
	 */
	boolean close();
}
