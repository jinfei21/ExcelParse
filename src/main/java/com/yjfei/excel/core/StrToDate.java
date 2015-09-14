package com.yjfei.excel.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.yjfei.excel.common.AbstractConvert;

public class StrToDate extends AbstractConvert<String, Date> {
    private static SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss");
    private SimpleDateFormat        df;

    @Override
    public Date convert(String source) {
        if (source == null) {
            if ("".equals(meta.getDefaultValue())) {
                return null;
            } else {
                source = String.valueOf(meta.getDefaultValue());
            }
        }
        try {
            if (df != null) {
                return df.parse(source);
            } else {
                return DEFAULT_DATE_FORMAT.parse(source);
            }
        } catch (ParseException e) {
            throw new RuntimeException(String.format("can not parse %s to date with pattern %s", source,
                    meta.getFormat()));
        }

    }

    public void setConvertInfo(ConvertInfo convert) {
        this.meta = convert;
        this.df = new SimpleDateFormat(meta.getFormat());
    }
}
