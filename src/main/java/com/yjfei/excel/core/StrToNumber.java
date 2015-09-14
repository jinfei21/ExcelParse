package com.yjfei.excel.core;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;

import com.yjfei.excel.common.AbstractConvert;

public class StrToNumber extends AbstractConvert<String, Number> {
    private DecimalFormat df = new DecimalFormat();

    @Override
    public Number convert(String source) {
        if (source == null) {
            if ("".equals(meta.getDefaultValue())) {
                return null;
            } else {
                source = String.valueOf(meta.getDefaultValue());
            }
        }
        try {
            Number number = df.parse(source);
            return toTarget(number);
        } catch (ParseException e) {
            throw new RuntimeException(String.format("can not parse %s to date with pattern %s", source,
                    meta.getFormat()));
        }
    }

    private Number toTarget(Number number) {

        if (meta == null) {
            return number;
        }
        Class<?> clazz = meta.getTargetType();
        if (clazz == null) {
            return number;
        }

        if (clazz == int.class || clazz == Integer.class) {
            return number.intValue();
        } else if (clazz == long.class || clazz == Long.class) {
            return number.longValue();
        } else if (clazz == short.class || clazz == Short.class) {
            return number.shortValue();
        } else if (clazz == byte.class || clazz == Byte.class) {
            return number.byteValue();
        } else if (clazz == float.class || clazz == Float.class) {
            return number.floatValue();
        } else if (clazz == double.class || clazz == Double.class) {
            return number.doubleValue();
        } else if (clazz == BigDecimal.class) {
            return new BigDecimal(number.doubleValue());
        } else {
            return number;
        }
    }
}
