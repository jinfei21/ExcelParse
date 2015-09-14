package com.yjfei.excel.core;

import java.lang.reflect.Method;

import com.yjfei.excel.common.AbstractConvert;

public class StrToEnum extends AbstractConvert<String, Enum> {

    private Method method;

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
            return (Enum) method.invoke(null, source);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(String.format("can not convert %s to enum %s by method %s.", source,
                    meta.getTargetType(), method.getName()));
        }
    }

    public void setConvertInfo(ConvertInfo convert) {
        this.meta = convert;

        try {
            method = meta.getTargetType().getMethod(meta.getFormat(), String.class);
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(String.format(
                    "can not find the transfer method at enum %s with string parameter %s.", meta.getTargetType(),
                    meta.getFormat()));
        }
    }
}
