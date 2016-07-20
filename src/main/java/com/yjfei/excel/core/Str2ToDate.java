package com.yjfei.excel.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.poi.ss.usermodel.DateUtil;
import com.yjfei.excel.common.AbstractConvert;
import com.yjfei.excel.util.StringUtil;

public class Str2ToDate extends AbstractConvert<String, Date> {
	private static String DEFAULT_DATE_FORMAT = "yyyy-dd-MM HH:mm:ss";

	@Override
	public Date convert(String source) {
		if (source == null) {
			if (StringUtil.isBlank(String.valueOf(meta.getDefaultValue()))) {
				return null;
			} else {
				source = String.valueOf(meta.getDefaultValue());
			}
		}
		try {
			Date date = DateUtil.getJavaDate(Integer.parseInt(source));
			SimpleDateFormat format = new SimpleDateFormat(meta.getFormat());
			return format.parse(format.format(date));
		} catch (ParseException e) {
			throw new RuntimeException(
					String.format("can not parse %s to date with pattern %s", source, meta.getFormat()));
		}
	}

	public void setConvertInfo(ConvertInfo convert) {
		this.meta = convert;
		if (StringUtil.isBlank(meta.getFormat())) {
			meta.setFormat(DEFAULT_DATE_FORMAT);
		}
	}
}