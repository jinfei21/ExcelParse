package com.yjfei.excel.example;

import com.google.gson.Gson;
import com.yjfei.excel.ExcelParser;
import com.yjfei.excel.ExcelResult;
import com.yjfei.excel.XMLExcelParser;

import java.io.InputStream;

public class ExcelTest {

    public static void main(String args[]) throws Throwable {
        Gson gson = new Gson();
        InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("1.xlsx");
        long start = System.currentTimeMillis();
        ExcelResult<DetailStudent> result = XMLExcelParser.parse(input, 1, StudentTemplate.class, DetailStudent.class);
        long cost = System.currentTimeMillis() - start;
        System.out.println("总耗时:" + cost);
        System.out.println(gson.toJson(result));
        System.out.println(result.getTotal());
        System.out.println(result.getErrorCount());
        System.out.println(result.getErrorMap().toString());
        System.out.println("----------------");
        input = Thread.currentThread().getContextClassLoader().getResourceAsStream("1.xlsx");
        start = System.currentTimeMillis();
        result = XMLExcelParser.parse(input, 1, StudentTemplate.class, DetailStudent.class);
        cost = System.currentTimeMillis() - start;
        System.out.println("总耗时:" + cost);
        System.out.println(gson.toJson(result));
        System.out.println(result.getTotal());
        System.out.println(result.getErrorCount());
        System.out.println(result.getErrorMap().toString());
        System.out.println("----------------");

        input = Thread.currentThread().getContextClassLoader().getResourceAsStream("1.xlsx");
        start = System.currentTimeMillis();
        result = ExcelParser.parse(input, 0, StudentTemplate.class, DetailStudent.class);
        cost = System.currentTimeMillis() - start;
        System.out.println("总耗时:" + cost);
        System.out.println(gson.toJson(result));
        System.out.println(result.getTotal());
        System.out.println(result.getErrorCount());
        System.out.println(result.getErrorMap().toString());

        System.out.println("----------------");
        input = Thread.currentThread().getContextClassLoader().getResourceAsStream("1.xlsx");
        start = System.currentTimeMillis();
        result = ExcelParser.quickParse(input, 0, StudentTemplate.class, DetailStudent.class);
        cost = System.currentTimeMillis() - start;
        System.out.println("总耗时:" + cost);
        System.out.println(result.getTotal());
        System.out.println(result.getErrorCount());
        System.out.println(result.getErrorMap().toString());
        System.out.println(gson.toJson(result));
        System.out.println("----------------");
        input = Thread.currentThread().getContextClassLoader().getResourceAsStream("1.xlsx");
        start = System.currentTimeMillis();
        result = ExcelParser.quickParse(input, 0, StudentTemplate.class, DetailStudent.class);
        cost = System.currentTimeMillis() - start;
        System.out.println("总耗时:" + cost);
        System.out.println(result.getTotal());
        System.out.println(result.getErrorCount());
        System.out.println(result.getErrorMap().toString());
    }


}