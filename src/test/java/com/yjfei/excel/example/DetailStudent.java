package com.yjfei.excel.example;

import java.util.Date;

public class DetailStudent extends Student {
    private String school;
    private int age;

    public DetailStudent() {
    }

    public DetailStudent(String name, SexEnum sex, Date birth, String email) {
        super(name, sex, birth, email);
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}