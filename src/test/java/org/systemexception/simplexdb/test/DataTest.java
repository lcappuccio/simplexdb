package org.systemexception.simplexdb.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.systemexception.simplexdb.domain.Data;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author leo
 * @date 27/12/15 19:14
 */
public class DataTest {

	private Data sut;
	private final byte[] bytes = new byte[256];

	@BeforeEach
	public void setUp() {
		String dataName = "TEST_DATA";
		sut = new Data(dataName, bytes);
	}

	@Test
	public void data_size_is_correct() {
        assertEquals(256L, (long) sut.getSize());
	}
}