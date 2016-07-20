package com.yjfei.excel.core;

import com.yjfei.excel.common.IConvert;

public class ConvertInfo {
	String format;
	Class<?> targetType;
	Class<?> convertType;
	Class<?> sourceType;
	Object defaultValue;
	IConvert convertor;

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public Class<?> getTargetType() {
		return targetType;
	}

	public void setTargetType(Class<?> targetType) {
		this.targetType = targetType;
	}

	public Class<?> getConvertType() {
		return convertType;
	}

	public void setConvertType(Class<?> convertType) {
		this.convertType = convertType;
	}

	public Class<?> getSourceType() {
		return sourceType;
	}

	public void setSourceType(Class<?> sourceType) {
		this.sourceType = sourceType;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}

	public IConvert getConvertor() {
		return convertor;
	}

	public void setConvertor(IConvert convertor) {
		this.convertor = convertor;
	}
}