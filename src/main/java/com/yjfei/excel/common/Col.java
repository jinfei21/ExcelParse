package com.yjfei.excel.common;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.DateUtil;

public class Col {

    public static String ERROR_STR = "#VALUE!";
    private int          colNo;
    private ColType      type;
    private String       strVal;
    private String       formula;
    private String       tAttr;

    public int getColNo() {
        return colNo;
    }

    public void setColNo(int colNo) {
        this.colNo = colNo;
    }

    public ColType getType() {
        if (type == null)
            type = ColType.NUMBER;
        return type;
    }

    public void setType(ColType type) {
        this.type = type;
    }

    public String getStrVal() {
        return strVal;
    }

    public void setStrVal(String strVal) {
        this.strVal = strVal;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String gettAttr() {
        return tAttr;
    }

    public void settAttr(String tAttr) {
        this.tAttr = tAttr;
    }

    public Date getAsDate() {
        if (getType() == ColType.NUMBER && StringUtils.isNotEmpty(strVal)) {
            return DateUtil.getJavaDate(Integer.parseInt(strVal));
        }
        return null;
    }

    @Override
    public String toString() {
        return "Col [colNo=" + colNo + ", type=" + type + ", strVal=" + strVal + ", formula=" + formula + ", tAttr="
                + tAttr + "]";
    }

}
