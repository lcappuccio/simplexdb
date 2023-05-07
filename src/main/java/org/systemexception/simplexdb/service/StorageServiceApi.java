package org.systemexception.simplexdb.service;

import org.systemexception.simplexdb.domain.Data;

import java.io.IOException;

/**
 * @author leo
 * @date 08/12/15 22:15
 */
public interface StorageServiceApi {

	/**
	 * Saves domain object to disk
	 *
	 * @param data the Data domain object
	 */
	void saveFile(Data data) throws IOException;
}
