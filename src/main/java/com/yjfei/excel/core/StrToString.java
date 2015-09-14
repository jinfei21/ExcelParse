package com.yjfei.excel.core;

import com.yjfei.excel.common.AbstractConvert;

public class StrToString extends AbstractConvert<String, String> {

    @Override
    public String convert(String source) {

        if (source != null) {
            return source;
        } else {
            return String.valueOf(meta.getDefaultValue());
        }
    }

}
