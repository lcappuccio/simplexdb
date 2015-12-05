package org.systemexception.simplexdb.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.systemexception.simplexdb.database.DatabaseService;
import org.systemexception.simplexdb.domain.Data;
import org.systemexception.simplexdb.domain.DataId;

import java.io.IOException;
import java.util.List;

/**
 * @author leo
 * @date 05/12/15 00:55
 */
@RestController
@RequestMapping(value = "/simplexdb")
public class SimplexDbController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final DatabaseService databaseService;

	@Autowired
	public SimplexDbController(final DatabaseService databaseService) {
		this.databaseService = databaseService;
	}

	@RequestMapping(method = RequestMethod.POST)
	HttpStatus save(@RequestParam("file") MultipartFile dataFile) throws IOException {
		DataId dataId = new DataId(dataFile.getOriginalFilename());
		Data data = new Data(dataId, dataFile.getBytes());
		logger.info("Save " + dataId.getDataId());
		boolean saved = databaseService.save(data);
		if (saved) {
			return HttpStatus.CREATED;
		} else {
			return HttpStatus.CONFLICT;
		}
	}

	@RequestMapping(method = RequestMethod.GET)
	List<DataId> findAll() {
		logger.info("Find all ids");
		return databaseService.findAll();
	}
}
