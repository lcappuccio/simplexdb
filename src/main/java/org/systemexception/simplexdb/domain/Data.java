package org.systemexception.simplexdb.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Arrays;
import java.util.UUID;

/**
 * @author leo
 * @date 05/12/15 00:58
 */
public class Data implements Serializable {

	private final String dataInternalId;
	private final String dataName;
	@JsonIgnore
	private final byte[] dataData;


	public Data(final String dataName, final byte[] dataData) {
		this.dataInternalId = UUID.randomUUID().toString();
		this.dataName = dataName;
		this.dataData = dataData;
	}

	public Data(final String dataInternalId, final String dataName, final byte[] dataData) {
		this.dataInternalId = dataInternalId;
		this.dataName = dataName;
		this.dataData = dataData;
	}

	public String getDataInternalId() {
		return dataInternalId;
	}

	public String getDataName() {
		return dataName;
	}

	public byte[] getDataData() {
		return dataData;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Data data = (Data) o;

		if (dataName != null ? !dataName.equals(data.dataName) : data.dataName != null) return false;
		return Arrays.equals(dataData, data.dataData);

	}

	@Override
	public int hashCode() {
		int result = dataName != null ? dataName.hashCode() : 0;
		result = 31 * result + Arrays.hashCode(dataData);
		return result;
	}
}
