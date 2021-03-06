package com.yjfei.excel.common;

import com.yjfei.excel.core.ConvertInfo;

public interface IConvert<S, T> {
	T convert(S source);

	Class<T> targetType();

	Class<S> sourceType();

	void setConvertInfo(ConvertInfo convert);
}