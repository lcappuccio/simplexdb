package org.systemexception.simplexdb.service;

import org.systemexception.simplexdb.domain.Data;

/**
 * @author leo
 * @date 08/12/15 22:15
 */
public interface StorageServiceApi {

	/**
	 * Saves domain object to disk
	 *
	 * @param data
	 */
	void saveFile(Data data);
}
