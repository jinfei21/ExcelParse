package com.yjfei.excel.core;

import com.yjfei.excel.common.AbstractConvert;
import com.yjfei.excel.util.StringUtil;

public class StrToString extends AbstractConvert<String, String> {

    @Override
    public String convert(String source) {

        if (source != null || StringUtil.isBlank(String.valueOf(meta.getDefaultValue()))) {
            return source;
        } else {
            return String.valueOf(meta.getDefaultValue());
        }
    }

}
