package org.systemexception.simplexdb.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.systemexception.simplexdb.constants.Endpoints;
import org.systemexception.simplexdb.database.DatabaseService;
import org.systemexception.simplexdb.domain.Data;
import org.systemexception.simplexdb.domain.DataId;

import javax.servlet.http.HttpServletResponse;
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
@RequestMapping(value = Endpoints.CONTEXT)
public class SimplexDbController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final DatabaseService databaseService;

	@Autowired
	public SimplexDbController(final DatabaseService databaseService) {
		this.databaseService = databaseService;
	}

	@RequestMapping(value = Endpoints.SAVE, method = RequestMethod.POST)
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

	@RequestMapping(value = Endpoints.FINDALL, method = RequestMethod.GET)
	List<DataId> findAll() {
		logger.info("Find all ids");
		return databaseService.findAll();
	}

	// TODO collaborator for saving files
	@RequestMapping(value = Endpoints.FINDBYID + Endpoints.ID_WITH_EXTENSTION, method = RequestMethod.GET)
	ResponseEntity<HttpStatus> findById(@PathVariable("id") final String id, HttpServletResponse response) {
		logger.info("Find " + id);
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
		logger.info("Find matching " + match);
		return databaseService.findByFilename(match);
	}

	@RequestMapping(value = Endpoints.DELETE + Endpoints.ID_WITH_EXTENSTION, method = RequestMethod.DELETE)
	ResponseEntity<HttpStatus> delete(@PathVariable("id") final String id) {
		logger.info("Delete " + id);
		boolean deleted = databaseService.delete(new DataId(id));
		if (deleted) {
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}
