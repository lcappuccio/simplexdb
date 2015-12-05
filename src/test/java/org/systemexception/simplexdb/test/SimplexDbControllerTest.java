package org.systemexception.simplexdb.test;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.systemexception.simplexdb.Application;
import org.systemexception.simplexdb.controller.SimplexDbController;
import org.systemexception.simplexdb.database.DatabaseService;

import java.util.Optional;

import static org.mockito.Mockito.*;


/**
 * @author leo
 * @date 05/12/15 21:53
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@TestPropertySource(locations="classpath:application.properties")
public class SimplexDbControllerTest {

	private DatabaseService databaseService;
	@InjectMocks
	@Autowired
	private SimplexDbController simplexDbController;
	private MockMvc sut;
	private final static String ENDPOINT = "/simplexdb/";

	@Before
	public void setUp() {
		databaseService = mock(DatabaseService.class);
		when(databaseService.findAll()).thenReturn(null);
		when(databaseService.findById(any())).thenReturn(Optional.empty());
		simplexDbController = new SimplexDbController(databaseService);
		MockitoAnnotations.initMocks(this);
		sut = MockMvcBuilders.standaloneSetup(simplexDbController).build();
	}

	@Test
	public void find_all() throws Exception {
		sut.perform(MockMvcRequestBuilders.get(ENDPOINT));
		verify(databaseService).findAll();
	}

	@Test
	public void find_id() throws Exception {
		sut.perform(MockMvcRequestBuilders.get(ENDPOINT + "/" + any()));
		verify(databaseService).findById(any());
	}

	@Test
	public void delete() throws Exception {
		sut.perform(MockMvcRequestBuilders.delete(ENDPOINT + "/" + any()));
		verify(databaseService).delete(any());
	}
}