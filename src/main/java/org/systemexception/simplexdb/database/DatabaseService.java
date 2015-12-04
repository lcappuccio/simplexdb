package org.systemexception.simplexdb.database;

import org.springframework.stereotype.Component;

/**
 * @author leo
 * @date 05/12/15 00:45
 */
@Component
public class DatabaseService implements DatabaseApi {

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
