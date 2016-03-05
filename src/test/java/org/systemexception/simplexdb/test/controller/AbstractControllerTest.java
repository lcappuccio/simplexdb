package org.systemexception.simplexdb.test.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.systemexception.simplexdb.Application;
import org.systemexception.simplexdb.constants.Endpoints;
import org.systemexception.simplexdb.controller.SimplexDbController;
import org.systemexception.simplexdb.database.DatabaseApi;
import org.systemexception.simplexdb.domain.Data;
import org.systemexception.simplexdb.service.StorageService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author leo
 * @date 28/02/16 17:22
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@TestPropertySource(locations = "classpath:application.properties")
public abstract class AbstractControllerTest {

	protected static String TEST_DATABASE_FILENAME;
	protected DatabaseApi databaseService;
	protected StorageService storageService;
	@InjectMocks
	@Autowired
	protected SimplexDbController simplexDbController;
	protected MockMvc sut;
	protected final static String ENDPOINT = "/simplexdb/", REQUEST_PARAM = "fileToUpload";
	protected Data mockData;

	@Test
	public void save() throws Exception {
		MockMultipartFile dataFile = new MockMultipartFile(REQUEST_PARAM, UUID.randomUUID().toString(), "text/plain",
				"some data".getBytes());
		sut.perform(MockMvcRequestBuilders.fileUpload(ENDPOINT + Endpoints.SAVE).file(dataFile))
				.andExpect(status().is(HttpStatus.CREATED.value()));
		String dataId = dataFile.getOriginalFilename();
		Data data = new Data(dataId, dataFile.getBytes());
		verify(databaseService).save(data);
	}

	@Test
	public void save_conflict() throws Exception {
		when(databaseService.save(any())).thenReturn(false);
		MockMultipartFile dataFile = new MockMultipartFile(REQUEST_PARAM, UUID.randomUUID().toString(), "text/plain",
				"some data".getBytes());
		sut.perform(MockMvcRequestBuilders.fileUpload(ENDPOINT + Endpoints.SAVE).file(dataFile))
				.andExpect(status().is(HttpStatus.CONFLICT.value()));
		String dataId = dataFile.getOriginalFilename();
		Data data = new Data(dataId, dataFile.getBytes());
		verify(databaseService).save(data);
	}

	@Test
	public void find_all() throws Exception {
		sut.perform(MockMvcRequestBuilders.get(ENDPOINT + Endpoints.FINDALL)).andExpect(status()
				.is(HttpStatus.OK.value()));
		verify(databaseService).findAll();
	}

	@Test
	public void view_all() throws Exception {
		sut.perform(MockMvcRequestBuilders.get(ENDPOINT + Endpoints.VIEW)).andExpect(status()
				.is(HttpStatus.OK.value()));
		verify(databaseService).findAll();
	}

	@Test
	public void find_id_and_save() throws Exception {
		sut.perform(MockMvcRequestBuilders.get(ENDPOINT + Endpoints.FINDBYID + "/" + mockData.getDataName()))
				.andExpect(status().is(HttpStatus.FOUND.value()));
		verify(databaseService).findById(mockData.getDataName());
		verify(storageService).saveFile(mockData);
	}

	@Test
	public void dont_find_id() throws Exception {
		when(databaseService.findById(mockData.getDataName())).thenReturn(Optional.empty());
		sut.perform(MockMvcRequestBuilders.get(ENDPOINT + Endpoints.FINDBYID + "/" + mockData.getDataName()))
				.andExpect(status().is(HttpStatus.NOT_FOUND.value()));
		verify(databaseService).findById(mockData.getDataName());
	}

	@Test
	public void find_match() throws Exception {
		sut.perform(MockMvcRequestBuilders.get(ENDPOINT + Endpoints.FINDBYNAME + "/" + any()));
		verify(databaseService).findByFilename(any());
	}

	@Test
	public void delete_existing() throws Exception {
		sut.perform(MockMvcRequestBuilders.delete(ENDPOINT + Endpoints.DELETE + "/" + mockData.getDataName()))
				.andExpect(status().is(HttpStatus.OK.value()));
		verify(databaseService).delete(mockData.getDataName());
	}

	@Test
	public void delete_not_existing() throws Exception {
		when(databaseService.delete(mockData.getDataName())).thenReturn(false);
		sut.perform(MockMvcRequestBuilders.delete(ENDPOINT + Endpoints.DELETE + "/" + mockData.getDataName()))
				.andExpect(status().is(HttpStatus.NOT_FOUND.value()));
		verify(databaseService).delete(mockData.getDataName());
	}

	@Test
	public void export() throws Exception {
		List<Data> dataIdList = new ArrayList<>();
		dataIdList.add(mockData);
		when(databaseService.findAll()).thenReturn(dataIdList);
		sut.perform(MockMvcRequestBuilders.get(ENDPOINT + Endpoints.EXPORT)).andExpect(status()
				.is(HttpStatus.OK.value()));
		verify(databaseService).findAll();
		verify(storageService).saveFile(any());
	}
}
