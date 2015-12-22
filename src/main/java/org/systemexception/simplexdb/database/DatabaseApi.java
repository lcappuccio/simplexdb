package org.systemexception.simplexdb.database;

import org.springframework.stereotype.Service;
import org.systemexception.simplexdb.domain.Data;

import java.util.List;
import java.util.Optional;

/**
 * @author leo
 * @date 05/12/15 00:32
 */
@Service
public interface DatabaseApi {

	/**
	 * Adds a record to the database
	 */
	boolean save(Data data);

	/**
	 * List all records on database
	 */
	List<Data> findAll();

	/**
	 * List single record on database
	 */
	Optional<Data> findById(String dataId);

	/**
	 * List records matching string
	 */
	List<Data> findByFilename(String match);

	/**
	 * Remove record from database
	 */
	boolean delete(String dataId);

	/**
	 * Close database
	 */
	void close();
}
