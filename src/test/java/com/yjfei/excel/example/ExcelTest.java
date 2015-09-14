package com.yjfei.excel.example;

import java.io.InputStream;

import com.google.gson.Gson;
import com.yjfei.excel.ExcelParser;
import com.yjfei.excel.ExcelResult;

public class ExcelTest {

    public static void main(String args[]) throws Throwable {

        InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("1.xlsx");
        long start = System.currentTimeMillis();
        ExcelResult<DetailStudent> result = ExcelParser.parse(input, 0, StudentTemplate.class, DetailStudent.class);
        long cost = System.currentTimeMillis() - start;
        System.out.println("总耗时:" + cost);
        Gson gson = new Gson();

        System.out.println(gson.toJson(result));

        System.out.println("----------------");
        input = Thread.currentThread().getContextClassLoader().getResourceAsStream("1.xlsx");
        start = System.currentTimeMillis();
        result = ExcelParser.parse(input, 0, StudentTemplate.class, DetailStudent.class);
        cost = System.currentTimeMillis() - start;
        System.out.println("总耗时:" + cost);
        System.out.println(gson.toJson(result));
    }
}
