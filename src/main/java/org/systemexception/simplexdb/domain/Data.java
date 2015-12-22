package org.systemexception.simplexdb.domain;

import java.io.Serializable;
import java.util.Arrays;
import java.util.UUID;

/**
 * @author leo
 * @date 05/12/15 00:58
 */
public class Data implements Serializable{

	private final String dataInternalId;
	private final DataId dataId;
	private final byte[] dataData;


	public Data(final DataId dataId, final byte[] dataData) {
		this.dataInternalId = UUID.randomUUID().toString();

		this.dataId = dataId;
		this.dataData = dataData;
	}

	public String getDataInternalId() {
		return dataInternalId;
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

		if (dataInternalId != null ? !dataInternalId.equals(data.dataInternalId) : data.dataInternalId != null)
			return false;
		if (dataId != null ? !dataId.equals(data.dataId) : data.dataId != null) return false;
		return Arrays.equals(dataData, data.dataData);

	}

	@Override
	public int hashCode() {
		int result = dataInternalId != null ? dataInternalId.hashCode() : 0;
		result = 31 * result + (dataId != null ? dataId.hashCode() : 0);
		result = 31 * result + Arrays.hashCode(dataData);
		return result;
	}
}
