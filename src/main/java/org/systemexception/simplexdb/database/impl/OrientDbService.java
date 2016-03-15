package org.systemexception.simplexdb.database.impl;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import org.systemexception.simplexdb.constants.LogMessages;
import org.systemexception.simplexdb.database.AbstractDbService;
import org.systemexception.simplexdb.service.StorageServiceApi;

import java.io.File;

/**
 * @author cappuccio
 * @date 15/03/16 09:59
 */
public class OrientDbService extends AbstractDbService {

	public OrientDbService(final StorageServiceApi storageService, final String databaseName,
	                       final Long maxMemoryOccupation) {

		this.databaseName = databaseName;
		logger.info(LogMessages.CREATE_DATABASE + databaseName);
		String dbPath = System.getProperty("user.dir") + File.separator + databaseName;
		ODatabaseDocumentTx database = new ODatabaseDocumentTx("plocal:" + dbPath).create();
	}
}
