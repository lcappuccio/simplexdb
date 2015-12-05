package org.systemexception.simplexdb.database;

import org.systemexception.simplexdb.domain.DataId;

import java.util.List;

/**
 * @author leo
 * @date 05/12/15 00:32
 */
public interface DatabaseApi {

	/**
	 * Adds a record to the database
	 */
	void save();

	/**
	 * List all records on database
	 */
	List<DataId> findAll();

	/**
	 * List single record on database
	 */
	void findById();

	/**
	 * Remove record from database
	 */
	void delete();

	/**
	 * Close database
	 */
	void close();
}
