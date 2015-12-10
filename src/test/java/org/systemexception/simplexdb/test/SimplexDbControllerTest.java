package org.systemexception.simplexdb.test;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.systemexception.simplexdb.Application;
import org.systemexception.simplexdb.constants.Endpoints;
import org.systemexception.simplexdb.controller.SimplexDbController;
import org.systemexception.simplexdb.database.DatabaseService;
import org.systemexception.simplexdb.domain.Data;
import org.systemexception.simplexdb.domain.DataId;
import org.systemexception.simplexdb.service.StorageService;

import java.io.File;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * @author leo
 * @date 05/12/15 21:53
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@TestPropertySource(locations = "classpath:application.properties")
public class SimplexDbControllerTest {

	private final static String TEST_DATABASE_FILENAME = "target" + File.separator + "test.db";
	private DatabaseService databaseService;
	private StorageService storageService;
	@InjectMocks
	@Autowired
	private SimplexDbController simplexDbController;
	private MockMvc sut;
	private final static String ENDPOINT = "/simplexdb/";
	private Data mockData;

	@Before
	public void setUp() {
		mockData = mock(Data.class);
		when(mockData.getDataId()).thenReturn(new DataId("123"));
		when(mockData.getDataData()).thenReturn("123".getBytes());
		databaseService = mock(DatabaseService.class);
		storageService = mock(StorageService.class);
		when(databaseService.findAll()).thenReturn(null);
		when(databaseService.findById(mockData.getDataId())).thenReturn(Optional.of(mockData));
		when(databaseService.delete(mockData.getDataId())).thenReturn(true);
		simplexDbController = new SimplexDbController(databaseService);
		MockitoAnnotations.initMocks(this);
		sut = MockMvcBuilders.standaloneSetup(simplexDbController).build();
	}

	@AfterClass
	public static void tearDownSut() {
		File databaseFile = new File(TEST_DATABASE_FILENAME);
		if (databaseFile.exists()) {
			databaseFile.delete();
		}
		assert (!databaseFile.exists());
	}

	@Test
	public void find_all() throws Exception {
		sut.perform(MockMvcRequestBuilders.get(ENDPOINT + Endpoints.FINDALL));
		verify(databaseService).findAll();
	}

	@Test
	public void find_id_and_save() throws Exception {
		sut.perform(MockMvcRequestBuilders.get(ENDPOINT + Endpoints.FINDBYID + "/" + mockData.getDataId().getDataId()))
				.andExpect(status().is(HttpStatus.FOUND.value()));
		verify(databaseService).findById(mockData.getDataId());
		verify(storageService).saveFile(mockData);
	}

	@Test
	public void dont_find_id() throws Exception {
		when(databaseService.findById(mockData.getDataId())).thenReturn(Optional.empty());
		sut.perform(MockMvcRequestBuilders.get(ENDPOINT + Endpoints.FINDBYID + "/" + mockData.getDataId().getDataId()))
				.andExpect(status().is(HttpStatus.NOT_FOUND.value()));
		verify(databaseService).findById(mockData.getDataId());
	}

	@Test
	public void find_match() throws Exception {
		sut.perform(MockMvcRequestBuilders.get(ENDPOINT + Endpoints.FINDBYNAME + "/" + any()));
		verify(databaseService).findByFilename(any());
	}

	@Test
	public void delete_existing() throws Exception {
		sut.perform(MockMvcRequestBuilders.delete(ENDPOINT + Endpoints.DELETE + "/" + mockData.getDataId().getDataId
				()))
				.andExpect(status().is(HttpStatus.OK.value()));
		verify(databaseService).delete(mockData.getDataId());
	}

	@Test
	public void delete_not_existing() throws Exception {
		when(databaseService.delete(mockData.getDataId())).thenReturn(false);
		sut.perform(MockMvcRequestBuilders.delete(ENDPOINT + Endpoints.DELETE + "/" + mockData.getDataId().getDataId
				())).andExpect(status().is(HttpStatus.NOT_FOUND.value()));
		verify(databaseService).delete(mockData.getDataId());
	}

	@Test
	public void export() throws Exception {
		when(databaseService.findAll()).thenReturn(any());
		sut.perform(MockMvcRequestBuilders.get(ENDPOINT + Endpoints.EXPORT)).andExpect(status()
				.is(HttpStatus.OK.value()));
		verify(databaseService).findAll();
		verify(storageService).saveFile(any());
	}
}