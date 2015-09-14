package com.yjfei.excel.example;

import java.io.Serializable;

public enum SexEnum implements Serializable {

    男("男"),
    女("女");

    private String name;

    SexEnum(String name) {
        this.name = name;
    }

    public SexEnum getName(String name) {
        if ("男人".equals(name)) {
            return 男;
        } else {
            return 女;
        }
    }
}
