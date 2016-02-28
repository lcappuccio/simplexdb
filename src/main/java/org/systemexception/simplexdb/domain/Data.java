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
	private final String dataSize;
	@JsonIgnore
	private final byte[] dataData;

	public Data(final String dataName, final byte[] dataData) {
		this.dataInternalId = UUID.randomUUID().toString();
		this.dataName = dataName;
		this.dataData = dataData;
		this.dataSize = calculateSize();
	}

	public Data(final String dataInternalId, final String dataName, final byte[] dataData) {
		this.dataInternalId = dataInternalId;
		this.dataName = dataName;
		this.dataData = dataData;
		this.dataSize = calculateSize();
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

	public String getDataSize() {
		return dataSize;
	}

	private String calculateSize() {
		float Kb = 1024;
		float Mb = 1024 * Kb;
		float size = (float) dataData.length;
		if (size < Kb) {
			return dataData.length + " Byte";
		}
		if (size < Mb) {
			return (size / Kb) + " KB";
		}
		return (size / Mb) + " MB";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Data data = (Data) o;

		if (dataName != null ? !dataName.equals(data.dataName) : data.dataName != null) return false;
		return Arrays.equals(dataData, data.dataData);

	}

}
