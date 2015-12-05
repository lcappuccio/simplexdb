package org.systemexception.simplexdb.domain;

import java.util.Arrays;

/**
 * @author leo
 * @date 05/12/15 00:58
 */
public class Data {

	private final DataId dataId;
	private final byte[] dataData;

	public Data(final DataId dataId, final byte[] dataData) {
		this.dataId = dataId;
		this.dataData = dataData;
	}

	public DataId getDataId() {
		return dataId;
	}

	public byte[] getDataData() {
		return dataData;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Data data = (Data) o;

		if (dataId != null ? !dataId.equals(data.dataId) : data.dataId != null) return false;
		return Arrays.equals(dataData, data.dataData);

	}

	@Override
	public int hashCode() {
		int result = dataId != null ? dataId.hashCode() : 0;
		result = 31 * result + Arrays.hashCode(dataData);
		return result;
	}
}
