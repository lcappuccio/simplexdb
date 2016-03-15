package org.systemexception.simplexdb.database.impl;

import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.systemexception.simplexdb.constants.LogMessages;
import org.systemexception.simplexdb.database.AbstractDbService;
import org.systemexception.simplexdb.domain.Data;
import org.systemexception.simplexdb.service.StorageServiceApi;

import javax.annotation.PreDestroy;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author cappuccio
 * @date 15/03/16 09:59
 */
public class OrientDbService extends AbstractDbService {

	private final ODatabaseDocumentTx database;
	private final OIndex idxInternalId, idxName;

	public OrientDbService(final StorageServiceApi storageService, final String databaseName,
	                       final Long maxMemoryOccupation) {

		this.databaseName = databaseName;
		this.maxMemoryOccupation = maxMemoryOccupation;
		this.storageService = storageService;
		logger.info(LogMessages.CREATE_DATABASE + databaseName);
		String dbPath = System.getProperty("user.dir") + File.separator + databaseName;
		File dbFile = new File(dbPath);
		Orient.instance().startup();
		if (dbFile.exists()) {
			database = new ODatabaseDocumentTx("plocal:" + dbPath).open("admin", "admin");
		} else {
			database = new ODatabaseDocumentTx("plocal:" + dbPath).create();
		}

		OSchema schema = database.getMetadata().getSchema();
		OClass oClass = schema.createClass(Data.class.getSimpleName());
		oClass.createProperty("internalId", OType.STRING);
		oClass.createProperty("name", OType.STRING);
		idxInternalId = oClass.createIndex(Data.class.getSimpleName() + ".internalId", "UNIQUE", new String[]{"internalId"});
		idxName = oClass.createIndex(Data.class.getSimpleName() + ".name", "FULLTEXT", new String[]{"name"});
		database.getMetadata().getSchema().save();
		ODatabaseRecordThreadLocal.INSTANCE.set(database);
	}

	@Override
	public boolean save(Data data) {
		if (!idxInternalId.contains(data.getInternalId())) {
			ODocument dataDocument = new ODocument(Data.class.getSimpleName());
			dataDocument.field("internalId", data.getInternalId());
			dataDocument.field("name", data.getName());
			dataDocument.field("size", data.getSize());
			dataDocument.field("date", data.getDate());
			dataDocument.field("content", data.getContent());
			dataDocument.save();
			idxInternalId.put(data.getInternalId(), dataDocument);
			idxName.put(data.getName(), dataDocument);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<Data> findAll() {
		List<Data> dataList = new ArrayList<>();
		for (ODocument oDocument : database.browseClass(Data.class.getSimpleName())) {
			String internalId = oDocument.field("internalId");
			String name = oDocument.field("name");
			Long date = oDocument.field("date");
			byte[] content = oDocument.field("content");
			Data data = new Data(internalId, name, date, content);
			dataList.add(data);
		}
		return dataList;
	}

	@Override
	public Optional<Data> findById(String dataId) {
		if (idxInternalId.contains(dataId)) {
			OIdentifiable object = (OIdentifiable) idxInternalId.get(dataId);
			ORecord record = database.getRecord(object);
			Data data = (Data) record;
			return Optional.of(data);
		} else {
			return Optional.empty();
		}
	}

	@Override
	public List<Data> findByFilename(final String match) {
		List<Data> foundData = new ArrayList<>();
		for (ODocument oDocument : database.browseClass(Data.class.getSimpleName())) {
			String name = oDocument.field("name");
			if (name.contains(match)) {
				String internalId = oDocument.field("internalId");
				Long date = oDocument.field("date");
				byte[] content = oDocument.field("content");
				Data data = new Data(internalId, name, date, content);
				foundData.add(data);
			}
		}
		return foundData;
	}

	@Override
	public boolean delete(String dataId) {
		if (idxInternalId.contains(dataId)) {
			OIdentifiable object = (OIdentifiable) idxInternalId.get(dataId);
			object.getRecord().delete();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void commit() {
		database.commit();
		logger.info(LogMessages.COMMIT_MESSAGE.toString());
	}

	@PreDestroy
	@Override
	public void close() {
		database.commit();
		database.close();
		Orient.instance().shutdown();
		logger.info(LogMessages.CLOSE_DATABASE + databaseName);
	}
}
