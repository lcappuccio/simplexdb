package org.systemexception.simplexdb.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

/**
 * @author leo
 * @date 05/12/15 00:58
 */
public class Data implements Serializable {

	private String internalId;
	private String name;
	private Long size;
	private Long date;
	@JsonIgnore
	private byte[] content;

	public Data() {
	}

	public Data(final String name, final byte[] content) {
		this.internalId = UUID.randomUUID().toString();
		this.name = name;
		this.content = content;
		this.size = (long) content.length;
		this.date = System.currentTimeMillis();
	}

	public Data(final String internalId, final String name, final Long date, final byte[] content) {
		this.internalId = internalId;
		this.name = name;
		this.content = content;
		this.size = (long) content.length;
		this.date = date;
	}

	public String getInternalId() {
		return internalId;
	}

	public void setInternalId(String internalId) {
		this.internalId = internalId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public Long getDate() {
		return date;
	}

	public void setDate(Long date) {
		this.date = date;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
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

    @Override
    public int hashCode() {
        int result = Objects.hash(internalId, name, size, date);
        result = 31 * result + Arrays.hashCode(content);
        return result;
    }
}
