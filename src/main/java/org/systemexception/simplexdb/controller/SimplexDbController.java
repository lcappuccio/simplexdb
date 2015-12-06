package org.systemexception.simplexdb.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.systemexception.simplexdb.database.DatabaseService;
import org.systemexception.simplexdb.domain.Data;
import org.systemexception.simplexdb.domain.DataId;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

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

	@RequestMapping(value = "save", method = RequestMethod.POST)
	HttpStatus save(@RequestParam("file") final MultipartFile dataFile) throws IOException {
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

	@RequestMapping(value = "findall", method = RequestMethod.GET)
	List<DataId> findAll() {
		logger.info("Find all ids");
		return databaseService.findAll();
	}

	// TODO collaborator for saving files
	@RequestMapping(value = "findbyid/{id:.+}", method = RequestMethod.GET)
	HttpStatus findById(@PathVariable("id") final String id) {
		logger.info("Find " + id);
		DataId dataId = new DataId(id);
		Optional<Data> data = databaseService.findById(dataId);

		if (data.equals(Optional.empty())) {
			return HttpStatus.NOT_FOUND;
		} else {
			File dataFile = new File(dataId.getDataId());
			try (FileOutputStream fos = new FileOutputStream(dataFile)) {
				fos.write(data.get().getDataData());
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			return HttpStatus.FOUND;
		}
	}

	// TODO behaviour is inconsistent, findById saves files, this returns a list
	@RequestMapping(value = "findbyname/{match:.+}", method = RequestMethod.GET)
	List<DataId> findByFilename(@PathVariable("match") final String match) {
		logger.info("Find matching " + match);
		return databaseService.findByFilename(match);
	}

	@RequestMapping(value = "delete/{id:.+}", method = RequestMethod.DELETE)
	HttpStatus delete(@PathVariable("id") final String id) {
		logger.info("Delete " + id);
		boolean deleted = databaseService.delete(new DataId(id));
		if (deleted) {
			return HttpStatus.OK;
		} else {
			return HttpStatus.NOT_FOUND;
		}
	}
}
