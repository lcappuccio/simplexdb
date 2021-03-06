package org.systemexception.simplexdb.controller;

import com.sleepycat.je.DatabaseException;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.systemexception.simplexdb.constants.Endpoints;
import org.systemexception.simplexdb.constants.LogMessages;
import org.systemexception.simplexdb.database.DatabaseApi;
import org.systemexception.simplexdb.domain.Data;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * @author leo
 * @date 05/12/15 00:55
 */
@Controller
@RequestMapping(value = Endpoints.CONTEXT)
@EnableSwagger2
@Api(basePath = Endpoints.CONTEXT, description = "SimplexDB REST API")
public class SimplexDbController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final DatabaseApi databaseService;

	@Autowired
	public SimplexDbController(final DatabaseApi databaseService) {
		this.databaseService = databaseService;
	}

	@RequestMapping(value = Endpoints.SAVE, method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<HttpStatus> save(@RequestParam(Endpoints.FILE_TO_UPLOAD) final MultipartFile dataFile)
			throws IOException {
		String dataId = dataFile.getOriginalFilename();
		Data data = new Data(dataId, dataFile.getBytes());
		logger.info(LogMessages.SAVE + dataId);
		boolean saved = databaseService.save(data);
		if (saved) {
			return new ResponseEntity<>(HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
	}

	@RequestMapping(value = Endpoints.FINDALL, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<List<Data>> findAll() throws DatabaseException, IOException, ClassNotFoundException {
		logger.info(LogMessages.FIND_ALL_IDS.toString());
		return new ResponseEntity<>(databaseService.findAll(), HttpStatus.OK);
	}

	@RequestMapping(value = Endpoints.VIEW, method = RequestMethod.GET)
	public String viewAll(Model model) throws DatabaseException, IOException, ClassNotFoundException {
		model.addAttribute("datalist", databaseService.findAll());
		return "index";
	}

	@RequestMapping(value = Endpoints.FINDBYID + Endpoints.ID_WITH_EXTENSION, method = RequestMethod.GET,
			produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody
	public ResponseEntity<HttpStatus> findById(@PathVariable("id") final String id) throws IOException, ClassNotFoundException {
		logger.info(LogMessages.FIND_ID + id);
		Optional<Data> data = databaseService.findById(id);
		if (data.equals(Optional.empty())) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(HttpStatus.FOUND);
		}
	}

	@RequestMapping(value = Endpoints.FINDBYNAME + Endpoints.ID_WITH_EXTENSION, method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<List<Data>> findByFilename(@PathVariable("id") final String match) throws IOException, ClassNotFoundException {
		logger.info(LogMessages.FIND_MATCH + match);
		return new ResponseEntity<>(databaseService.findByFilename(match), HttpStatus.OK);
	}

	@RequestMapping(value = Endpoints.DELETE + Endpoints.ID_WITH_EXTENSION, method = RequestMethod.DELETE,
			produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody
	public ResponseEntity<HttpStatus> delete(@PathVariable("id") final String id) {
		logger.info(LogMessages.DELETE + id);
		boolean deleted = databaseService.delete(id);
		if (deleted) {
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}
