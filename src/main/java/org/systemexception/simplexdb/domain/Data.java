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

	private final String internalId;
	private final String name;
	private final Long size;
	private final Long date;
	@JsonIgnore
	private final byte[] content;

	public Data(final String name, final byte[] content) {
		this.internalId = UUID.randomUUID().toString();
		this.name = name;
		this.content = content;
		this.size = Long.valueOf(content.length);
		this.date = System.currentTimeMillis();
	}

	public Data(final String internalId, final String name, final Long date, final byte[] content) {
		this.internalId = internalId;
		this.name = name;
		this.content = content;
		this.size = Long.valueOf(content.length);
		this.date = date;
	}

	public String getInternalId() {
		return internalId;
	}

	public String getName() {
		return name;
	}

	public byte[] getContent() {
		return content;
	}

	public Long getSize() {
		return size;
	}

	public Long getDate() {
		return date;
	}

	private String calculateSize() {
		float Kb = 1024;
		float Mb = 1024 * Kb;
		float size = (float) content.length;
		if (size < Kb) {
			return content.length + " Byte";
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

		if (name != null ? !name.equals(data.name) : data.name != null) return false;
		if (size != null ? !size.equals(data.size) : data.size != null) return false;
		return Arrays.equals(content, data.content);

	}
}
