package com.yjfei.excel.common;

import java.util.HashMap;

public class ParseContext extends HashMap<Object, Object> {
	private static final long serialVersionUID = 1L;
	private int minColNo = 1;
	private int maxColNo;
	private int minRowNo;
	private int maxRowNo;
	private int sheetNo;
	private String sheetName;
	private int rowCount;

	public int getMinColNo() {
		return minColNo;
	}

	public void setMinColNo(int minColNo) {
		this.minColNo = minColNo;
	}

	public int getMaxColNo() {
		return maxColNo;
	}

	public void setMaxColNo(int maxColNo) {
		this.maxColNo = maxColNo;
	}

	public int getMinRowNo() {
		return minRowNo;
	}

	public void setMinRowNo(int minRowNo) {
		this.minRowNo = minRowNo;
	}

	public int getMaxRowNo() {
		return maxRowNo;
	}

	public void setMaxRowNo(int maxRowNo) {
		this.maxRowNo = maxRowNo;
	}

	public int getSheetNo() {
		return sheetNo;
	}

	public void setSheetNo(int sheetNo) {
		this.sheetNo = sheetNo;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public int getRowCount() {
		return rowCount;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	public void incrRow() {
		this.rowCount++;
	}
}