package org.systemexception.simplexdb.test;

import org.junit.Before;
import org.junit.Test;
import org.systemexception.simplexdb.domain.Data;

import static org.junit.Assert.assertTrue;

/**
 * @author leo
 * @date 27/12/15 19:14
 */
public class DataTest {

	private Data sut;
	private final byte[] bytes = new byte[256];

	@Before
	public void setUp() {
		String dataName = "TEST_DATA";
		sut = new Data(dataName, bytes);
	}

	@Test
	public void data_size_is_correct() {
		assertTrue(256L == sut.getSize());
	}
}