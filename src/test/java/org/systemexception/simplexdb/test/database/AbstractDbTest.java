package org.systemexception.simplexdb.test.database;

import com.sleepycat.je.DatabaseException;

/**
 * @author leo
 * @date 28/02/16 15:05
 */
public interface AbstractDbTest {

	void databaseCreated();

	void addRecord() throws DatabaseException;

	void dontAddDuplicateRecord() throws DatabaseException;

	void getDataIdList() throws DatabaseException;

	void deleteExistingData() throws DatabaseException;

	void dontDeleteNonExistingData() throws DatabaseException;

	void findExistingData() throws DatabaseException;

	void dontFindNonExistingData() throws DatabaseException;

	void findMatches() throws DatabaseException;

}
