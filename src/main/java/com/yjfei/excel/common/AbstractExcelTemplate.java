package com.yjfei.excel.common;

public abstract class AbstractExcelTemplate {

    private int     titleIndex; //标题所在的行
    private int     dataIndex;  //数据所在的行
    private boolean checkTitle; //是否验证标题
    private boolean ignoreError; //是否忽略错误

    public AbstractExcelTemplate(int titleIndex, int dataIndex, boolean checkTitle, boolean ignoreError) {
        this.titleIndex = titleIndex;
        this.dataIndex = dataIndex;
        this.checkTitle = checkTitle;
        this.ignoreError = ignoreError;
    }

    public AbstractExcelTemplate(int titleIndex, int dataIndex) {
        this(titleIndex, dataIndex, true, true);
    }

    public int getTitleIndex() {
        return titleIndex;
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
