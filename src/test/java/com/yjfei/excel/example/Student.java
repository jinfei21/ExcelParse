package com.yjfei.excel.example;

import java.util.Date;

public class Student {
    private String name;
    private SexEnum sex;
    private Date birth;
    private String email;

    public Student() {
    }

    public Student(String name, SexEnum sex, Date birth, String email) {
        this.name = name;
        this.sex = sex;
        this.birth = birth;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SexEnum getSex() {
        return sex;
    }

    public void setSex(SexEnum sex) {
        this.sex = sex;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}