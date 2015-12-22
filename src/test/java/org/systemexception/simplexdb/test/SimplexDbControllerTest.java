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
import org.springframework.mock.web.MockMultipartFile;
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
import org.systemexception.simplexdb.service.StorageService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
		when(mockData.getDataInternalId()).thenReturn("123");
		when(mockData.getDataName()).thenReturn("123");
		when(mockData.getDataData()).thenReturn("123".getBytes());
		databaseService = mock(DatabaseService.class);
		storageService = mock(StorageService.class);
		when(databaseService.findById(mockData.getDataName())).thenReturn(Optional.of(mockData));
		when(databaseService.delete(mockData.getDataName())).thenReturn(true);
		simplexDbController = new SimplexDbController();
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
	public void save() throws Exception {
		MockMultipartFile dataFile = new MockMultipartFile("file", "filename.txt", "text/plain",
				"some xml".getBytes());
		sut.perform(MockMvcRequestBuilders.fileUpload(ENDPOINT + Endpoints.SAVE).file(dataFile));
		String dataId = dataFile.getOriginalFilename();
		Data data = new Data(dataId, dataFile.getBytes());
		verify(databaseService).save(data);
	}

	@Test
	public void find_all() throws Exception {
		sut.perform(MockMvcRequestBuilders.get(ENDPOINT + Endpoints.FINDALL));
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