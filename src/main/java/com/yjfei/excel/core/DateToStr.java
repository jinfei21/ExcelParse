package com.yjfei.excel.core;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.yjfei.excel.common.AbstractConvert;
import com.yjfei.excel.util.StringUtil;

public class DateToStr extends AbstractConvert<Date, String> {
	private static String DEFAULT_DATE_FORMAT = "yyyy-dd-MM HH:mm:ss";

	@Override
	public String convert(Date source) {
		if (source == null) {
			return String.valueOf(meta.getDefaultValue());
		}
		return new SimpleDateFormat(meta.getFormat()).format(source);
	}

	public void setConvertInfo(ConvertInfo convert) {
		this.meta = convert;
		if (StringUtil.isBlank(meta.getFormat())) {
			meta.setFormat(DEFAULT_DATE_FORMAT);
		}
	}
}