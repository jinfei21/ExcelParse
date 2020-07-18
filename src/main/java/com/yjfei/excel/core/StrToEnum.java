package com.yjfei.excel.core;

import java.lang.reflect.Method;
import com.yjfei.excel.common.AbstractConvert;
import com.yjfei.excel.util.StringUtil;

public class StrToEnum extends AbstractConvert<String, Enum> {
	private Method method = null;

	@Override
	public Enum convert(String source) {
		if (source == null) {
			if ("".equals(meta.getDefaultValue())) {
				return null;
			} else {
				source = String.valueOf(meta.getDefaultValue());
			}
		}
		try {
			if (method == null) {
				return (Enum) Enum.valueOf((Class<? extends Enum>) meta.getTargetType(), source);
			} else {
				return (Enum) method.invoke(meta.getTargetType().newInstance(), source);
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
				method = meta.getTargetType().getMethod(meta.getFormat(), String.class);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			throw new RuntimeException(
					String.format("can not find the transfer method at enum %s with string parameter %s.",
							meta.getTargetType(), meta.getFormat()));
		}
	}
}