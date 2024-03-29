package org.systemexception.simplexdb.database;

import org.systemexception.simplexdb.domain.Data;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * @author leo
 * @date 05/12/15 00:32
 */
public interface DatabaseApi {

	/**
	 * Adds a record to the database
	 *
	 * @param data the data object to save to database
	 */
	boolean save(Data data) throws IOException;

	/**
	 * List all records on database
	 */
	List<Data> findAll() throws IOException, ClassNotFoundException;

	/**
	 * List and eventually save single record on database
	 *
	 * @param dataId the internal data id of the object to search and eventually extract to file
	 */
	Optional<Data> findById(String dataId) throws IOException, ClassNotFoundException;

	/**
	 * List records matching string
	 *
	 * @param match string to search in database (see org.systemexception.simplexdb.domain.Data#dataName)
	 */
	List<Data> findByFilename(String match) throws IOException, ClassNotFoundException;

	/**
	 * Remove record from database
	 *
	 * @param dataId the internal data id of the object to delete
	 */
	boolean delete(String dataId);

	/**
	 * Close database
	 */
	void close();

	/**
	 * Rebuild index
	 */
	void rebuildIndex() throws IOException, ClassNotFoundException;
}
