package com.yjfei.excel.util;
public class ParseUtils {
    public static int colNo(String rc) {
        int i = 0;
        int colNo = 0;
        int len = rc.length();
        /** 绗竴涓暟瀛� */
        while (i < len) {
            char c = rc.charAt(i);
            if (Character.isDigit(c))
                break;
            colNo = (colNo) * 26 + (c - 'A') + 1;
            i++;
        }
        if (i == 0)
            return -1;
        return colNo;
    }
    public static int rowNo(String rc) {        int len = rc.length();        int i = len - 1;        /** 绗竴涓暟瀛� */        while (i >= 0 && Character.isDigit(rc.charAt(i))) {            i--;        }        if (i == len - 1)            return -1;        return Integer.parseInt(rc.substring(i + 1));    }    public static void main(String[] args) {        String pair = "A";        System.out.println(colNo(pair));        System.out.println(rowNo(pair));    }}