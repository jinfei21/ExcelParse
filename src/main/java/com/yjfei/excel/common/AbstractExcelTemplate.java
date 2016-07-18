package com.yjfei.excel.common;

public abstract class AbstractExcelTemplate {

    private int     titleIndex; //�������ڵ���
    private int     dataIndex;  //�������ڵ���
    private boolean checkTitle; //�Ƿ���֤����
    private boolean ignoreError; //�Ƿ���Դ���
    private int     maxRow;     //�����������

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
