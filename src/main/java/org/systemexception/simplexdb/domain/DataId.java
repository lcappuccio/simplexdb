package org.systemexception.simplexdb.domain;

import java.io.Serializable;

/**
 * @author leo
 * @date 05/12/15 01:29
 */
public class DataId implements Serializable {

	private final String dataId;

	public DataId(final String dataId) {
		this.dataId = dataId;
	}

	public String getDataId() {
		return dataId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DataId dataId1 = (DataId) o;

		return !(dataId != null ? !dataId.equals(dataId1.dataId) : dataId1.dataId != null);

	}

	@Override
	public int hashCode() {
		return dataId != null ? dataId.hashCode() : 0;
	}
}
