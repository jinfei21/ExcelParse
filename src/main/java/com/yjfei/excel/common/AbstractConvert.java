package com.yjfei.excel.common;

import com.yjfei.excel.core.ConvertInfo;

public abstract class AbstractConvert<S, T> implements IConvert<S, T> {

    protected ConvertInfo meta;

    public Class<T> targetType() {
        if (meta != null) {
            return (Class<T>) meta.getTargetType();
        }
        return null;
    }

    public Class<S> sourceType() {
        if (meta != null) {
            return (Class<S>) meta.getSourceType();
        }
        return null;
    }

    public void setConvertInfo(ConvertInfo convert) {
        this.meta = convert;
    }
}
