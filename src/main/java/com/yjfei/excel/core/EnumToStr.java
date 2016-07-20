package com.yjfei.excel.core;

import java.lang.reflect.Method;
import com.yjfei.excel.common.AbstractConvert;
import com.yjfei.excel.util.StringUtil;

public class EnumToStr extends AbstractConvert<Enum, String> {
	private Method method = null;

	@Override
	public String convert(Enum source) {
		if (source == null) {
			if ("".equals(meta.getDefaultValue())) {
				return String.valueOf(meta.getDefaultValue());
			}
		}
		try {
			if (method == null) {
				return String.valueOf(meta.getDefaultValue());
			} else {
				return String.valueOf(method.invoke(null, source));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(String.format("can not convert %s to enum %s by method %s.", source,
					meta.getTargetType(), method.getName()));
		}
	}

	public void setConvertInfo(ConvertInfo convert) {
		this.meta = convert;
		try {
			if (StringUtil.isNotBlank(meta.getFormat())) {
				method = meta.getTargetType().getMethod(meta.getFormat(), Enum.class);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			throw new RuntimeException(
					String.format("can not find the transfer method at enum %s with string parameter %s.",
							meta.getTargetType(), meta.getFormat()));
		}
	}
}