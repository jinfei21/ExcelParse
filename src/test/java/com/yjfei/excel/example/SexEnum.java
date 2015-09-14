package com.yjfei.excel.example;

public enum SexEnum {

    Man("男人"),
    Woman("女人");

    private String name;

    SexEnum(String name) {
        this.name = name;
    }

    public static SexEnum getName(String name) {
        if ("男人".equals(name)) {
            return Man;
        } else {
            return Woman;
        }
    }
}
