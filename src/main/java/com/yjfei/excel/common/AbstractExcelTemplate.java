package com.yjfei.excel.common;

public abstract class AbstractExcelTemplate {

    private int titleIndex; //标题起始行
    private int dataIndex;  //数据起始行
    private boolean checkTitle; //是否检查标题
    private boolean ignoreError; //是否忽略错误
    private int maxRow;     //最大行

    public AbstractExcelTemplate(int titleIndex, int dataIndex, boolean checkTitle, boolean ignoreError, int maxRow) {
        this.titleIndex = titleIndex;
        this.dataIndex = dataIndex;
        this.checkTitle = checkTitle;
        this.ignoreError = ignoreError;
        this.maxRow = maxRow;
    }

    public AbstractExcelTemplate(int titleIndex, int dataIndex) {
        this(titleIndex, dataIndex, true, true, Integer.MAX_VALUE);
    }

    public AbstractExcelTemplate(int titleIndex, int dataIndex, int maxRow) {
        this(titleIndex, dataIndex, true, true, maxRow);
    }

    public int getTitleIndex() {
        return titleIndex;
    }

    public int getMaxRow() {
        return this.maxRow;
    }

    public void setTitleIndex(int titleIndex) {
        this.titleIndex = titleIndex;
    }

    public boolean isCheckTitle() {
        return checkTitle;
    }

    public void setCheckTitle(boolean checkTitle) {
        this.checkTitle = checkTitle;
    }

    public boolean isIgnoreError() {
        return ignoreError;
    }

    public void setIgnoreError(boolean ignoreError) {
        this.ignoreError = ignoreError;
    }

    public int getDataIndex() {
        return dataIndex;
    }

    public void setDataIndex(int dataIndex) {
        this.dataIndex = dataIndex;
    }

}
