package com.yjfei.excel.core;

import java.lang.reflect.Field;

public class ColumnInfo {
	private int index;
	private String displayName;
	private Class<?> type;
	private Field field;
	private ConvertInfo convert;
	private String title;

	public ConvertInfo getConvert() {
		return convert;
	}

	public void setConvert(ConvertInfo convert) {
		this.convert = convert;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "ColumnInfo [index=" + index + ", displayName=" + displayName + ", type=" + type + ", field=" + field
				+ "]";
	}
}