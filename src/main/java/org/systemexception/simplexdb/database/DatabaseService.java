package org.systemexception.simplexdb.database;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author leo
 * @date 05/12/15 00:45
 */
@Service
public class DatabaseService implements DatabaseApi {

	@Value("${my.test}")
	public String myValues;

	@Override
	public void save() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void findAll() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void findById() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() {
		throw new UnsupportedOperationException();
	}
}
