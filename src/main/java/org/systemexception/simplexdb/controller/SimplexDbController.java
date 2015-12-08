package org.systemexception.simplexdb.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.systemexception.simplexdb.constants.Endpoints;
import org.systemexception.simplexdb.constants.LogMessages;
import org.systemexception.simplexdb.database.DatabaseService;
import org.systemexception.simplexdb.domain.Data;
import org.systemexception.simplexdb.domain.DataId;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

/**
 * @author leo
 * @date 05/12/15 00:55
 */
@RestController
@RequestMapping(value = Endpoints.CONTEXT)
public class SimplexDbController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final DatabaseService databaseService;

	@Autowired
	public SimplexDbController(final DatabaseService databaseService) {
		this.databaseService = databaseService;
	}

	@RequestMapping(value = Endpoints.SAVE, method = RequestMethod.POST)
	HttpStatus save(@RequestParam("file") final File dataFile) throws IOException {
		DataId dataId = new DataId(dataFile.getName());
		Data data = new Data(dataId, Files.readAllBytes(dataFile.toPath()));
		logger.info(LogMessages.SAVE + dataId.getDataId());
		boolean saved = databaseService.save(data);
		if (saved) {
			return HttpStatus.CREATED;
		} else {
			return HttpStatus.CONFLICT;
		}
	}

	@RequestMapping(value = Endpoints.FINDALL, method = RequestMethod.GET)
	List<DataId> findAll() {
		logger.info(LogMessages.FIND_ALL_IDS.toString());
		return databaseService.findAll();
	}

	// TODO collaborator for saving files
	@RequestMapping(value = Endpoints.FINDBYID + Endpoints.ID_WITH_EXTENSTION, method = RequestMethod.GET)
	ResponseEntity<HttpStatus> findById(@PathVariable("id") final String id) {
		logger.info(LogMessages.FIND_ID + id);
		DataId dataId = new DataId(id);
		Optional<Data> data = databaseService.findById(dataId);

		if (data.equals(Optional.empty())) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			File dataFile = new File(dataId.getDataId());
			try (FileOutputStream fos = new FileOutputStream(dataFile)) {
				fos.write(data.get().getDataData());
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			return new ResponseEntity<>(HttpStatus.FOUND);
		}
	}

	// TODO behaviour is inconsistent, findById saves files, this returns a list
	@RequestMapping(value = Endpoints.FINDBYNAME + Endpoints.ID_WITH_EXTENSTION, method = RequestMethod.GET)
	List<DataId> findByFilename(@PathVariable("id") final String match) {
		logger.info(LogMessages.FIND_MATCH + match);
		return databaseService.findByFilename(match);
	}

	@RequestMapping(value = Endpoints.DELETE + Endpoints.ID_WITH_EXTENSTION, method = RequestMethod.DELETE)
	ResponseEntity<HttpStatus> delete(@PathVariable("id") final String id) {
		logger.info(LogMessages.DELETE + id);
		boolean deleted = databaseService.delete(new DataId(id));
		if (deleted) {
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}
