package org.systemexception.simplexdb.controller;

import com.sleepycat.je.DatabaseException;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author leo
 * @date 05/12/15 00:55
 */
@Controller
@RequestMapping(value = Endpoints.CONTEXT)
//@EnableSwagger2
//@Api(basePath = Endpoints.CONTEXT, description = "SimplexDB REST API")
public class SimplexDbController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SimplexDbController.class.getName());
	private final DatabaseApi databaseService;

	@Autowired
	public SimplexDbController(final DatabaseApi databaseService) {
		this.databaseService = databaseService;
	}

	@PostMapping(value = Endpoints.SAVE, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<HttpStatus> save(@RequestParam(Endpoints.FILE_TO_UPLOAD) final MultipartFile dataFile)
			throws IOException {
		String dataId = dataFile.getOriginalFilename();
		Data data = new Data(dataId, dataFile.getBytes());
		LOGGER.info(LogMessages.SAVE + dataId);
		boolean saved = databaseService.save(data);
		if (saved) {
			return new ResponseEntity<>(HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
	}

	@GetMapping(value = Endpoints.VIEW)
	public String viewAll(Model model) throws DatabaseException, IOException, ClassNotFoundException {
		model.addAttribute("datalist", databaseService.findAll());
		return "index";
	}

	@GetMapping(value = {Endpoints.FIND, Endpoints.FIND + Endpoints.ID_WITH_EXTENSION}, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<List<Data>> find(@PathVariable(value = "id") final Optional<String> id)
			throws IOException, ClassNotFoundException {

		List<Data> dataList = new ArrayList<>();
		if (id.isPresent()) {
			String idToFind = id.get();
			LOGGER.info(LogMessages.FIND_ID + idToFind);
			Optional<Data> data = databaseService.findById(idToFind);
			if (data.isPresent()) {
				dataList.add(data.get());
			}
		} else {
			dataList.addAll(databaseService.findAll());
		}

		if (dataList.isEmpty() && id.isPresent()) {
			return new ResponseEntity<>(dataList, HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(dataList, HttpStatus.OK);
		}
	}

	@GetMapping(value = Endpoints.FINDBYNAME + Endpoints.ID_WITH_EXTENSION, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<List<Data>> findByFilename(@PathVariable("id") final String match) throws IOException,
			ClassNotFoundException {
		LOGGER.info(LogMessages.FIND_MATCH + match);
		return new ResponseEntity<>(databaseService.findByFilename(match), HttpStatus.OK);
	}

	@DeleteMapping(value = Endpoints.DELETE + Endpoints.ID_WITH_EXTENSION, produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody
	public ResponseEntity<HttpStatus> delete(@PathVariable("id") final String id) {
		LOGGER.info(LogMessages.DELETE + id);
		boolean deleted = databaseService.delete(id);
		if (deleted) {
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}
