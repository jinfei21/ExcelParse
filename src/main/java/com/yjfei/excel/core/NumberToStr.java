package com.yjfei.excel.core;

import com.yjfei.excel.common.AbstractConvert;

public class NumberToStr extends AbstractConvert<Number, String> {
	@Override
	public String convert(Number source) {
		if (source == null) {
			return String.valueOf(meta.getDefaultValue());
		}
		return String.valueOf(source);
	}
}