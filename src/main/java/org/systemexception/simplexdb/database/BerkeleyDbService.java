package org.systemexception.simplexdb.database;

import org.systemexception.simplexdb.domain.Data;

import java.util.List;
import java.util.Optional;

/**
 * @author leo
 * @date 28/02/16 11:55
 */
public class BerkeleyDbService implements Api {

	@Override
	public boolean save(Data data) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Data> findAll() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<Data> findById(String dataId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Data> findByFilename(String match) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean delete(String dataId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void commit() {
		throw new UnsupportedOperationException();
	}
}
