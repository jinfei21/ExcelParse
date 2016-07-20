package com.yjfei.excel.core;

import com.yjfei.excel.common.IConvert;

public class SelfConvert<T> implements IConvert<T, T> {
	@Override
	public T convert(T source) {
		return source;
	}

	@Override
	public Class<T> targetType() {
		return null;
	}

	@Override
	public Class<T> sourceType() {
		return null;
	}

	@Override
	public void setConvertInfo(ConvertInfo convert) {
	}
}