package org.systemexception.simplexdb.test.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.systemexception.simplexdb.Application;
import org.systemexception.simplexdb.constants.Endpoints;
import org.systemexception.simplexdb.controller.SimplexDbController;
import org.systemexception.simplexdb.database.DatabaseApi;
import org.systemexception.simplexdb.domain.Data;
import org.systemexception.simplexdb.service.StorageService;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author leo
 * @date 28/02/16 17:22
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {Application.class})
@TestPropertySource(locations = "classpath:application.properties")
public abstract class AbstractControllerTest {

	public static String TEST_DATABASE_FULLPATH;
	protected DatabaseApi databaseService;
	protected StorageService storageService;
	@InjectMocks
	@Autowired
	protected SimplexDbController simplexDbController;
	protected MockMvc sut;
	private final static String ENDPOINT = Endpoints.CONTEXT, REQUEST_PARAM = Endpoints.FILE_TO_UPLOAD,
			FILE_TEXT_FORMAT = "text/plain", FILE_TEXT_DATA = "some data in the file", URL_SEPARATOR = "/";
	protected Data testData;

	@Test
	void save() throws Exception {
		MockMultipartFile dataFile = new MockMultipartFile(REQUEST_PARAM, UUID.randomUUID().toString(),
				FILE_TEXT_FORMAT, FILE_TEXT_DATA.getBytes());
		sut.perform(MockMvcRequestBuilders.fileUpload(ENDPOINT + Endpoints.SAVE).file(dataFile))
				.andExpect(status().is(HttpStatus.CREATED.value()));
		String dataId = dataFile.getOriginalFilename();
		Data data = new Data(dataId, dataFile.getBytes());
		verify(databaseService).save(data);
	}

	@Test
	void save_conflict() throws Exception {
		when(databaseService.save(any())).thenReturn(false);
		MockMultipartFile dataFile = new MockMultipartFile(REQUEST_PARAM, UUID.randomUUID().toString(),
				FILE_TEXT_FORMAT, FILE_TEXT_DATA.getBytes());
		sut.perform(MockMvcRequestBuilders.fileUpload(ENDPOINT + Endpoints.SAVE).file(dataFile))
				.andExpect(status().is(HttpStatus.CONFLICT.value()));
		String dataId = dataFile.getOriginalFilename();
		Data data = new Data(dataId, dataFile.getBytes());
		verify(databaseService).save(data);
	}

	@Test
	void find_all() throws Exception {
		sut.perform(MockMvcRequestBuilders.get(ENDPOINT + Endpoints.FIND)).andExpect(status()
				.is(HttpStatus.OK.value()));
		verify(databaseService).findAll();
	}

	@Test
	void view_all() throws Exception {
		sut.perform(MockMvcRequestBuilders.get(ENDPOINT + Endpoints.VIEW)).andExpect(status()
				.is(HttpStatus.OK.value()));
		verify(databaseService).findAll();
	}

	@Test
	void find_id_and_save() throws Exception {
		sut.perform(MockMvcRequestBuilders.get(ENDPOINT + Endpoints.FIND + URL_SEPARATOR + testData.getInternalId()))
				.andExpect(status().is(HttpStatus.OK.value()));
		verify(databaseService).findById(testData.getName());
	}

	@Test
	void dont_find_id() throws Exception {
		when(databaseService.findById(testData.getName())).thenReturn(Optional.empty());
		sut.perform(MockMvcRequestBuilders.get(ENDPOINT + Endpoints.FIND + URL_SEPARATOR + testData.getInternalId()))
				.andExpect(status().is(HttpStatus.NOT_FOUND.value()));
		verify(databaseService).findById(testData.getName());
	}

	@Test
	void find_match() throws Exception {
		sut.perform(MockMvcRequestBuilders.get(ENDPOINT + Endpoints.FINDBYNAME + URL_SEPARATOR + any()));
		verify(databaseService).findByFilename(any());
	}

	@Test
	void delete_existing() throws Exception {
		sut.perform(MockMvcRequestBuilders.delete(ENDPOINT + Endpoints.DELETE + URL_SEPARATOR + testData.getName()))
				.andExpect(status().is(HttpStatus.OK.value()));
		verify(databaseService).delete(testData.getName());
	}

	@Test
	void delete_not_existing() throws Exception {
		when(databaseService.delete(testData.getName())).thenReturn(false);
		sut.perform(MockMvcRequestBuilders.delete(ENDPOINT + Endpoints.DELETE + URL_SEPARATOR + testData.getName()))
				.andExpect(status().is(HttpStatus.NOT_FOUND.value()));
		verify(databaseService).delete(testData.getName());
	}
}
