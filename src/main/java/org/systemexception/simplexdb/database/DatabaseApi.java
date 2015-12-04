package org.systemexception.simplexdb.database;

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
	void findAll();

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
