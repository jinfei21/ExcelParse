package com.yjfei.excel.common;

import java.util.HashMap;
import java.util.Map;

public class Row {
	private int rowNo;
	Map<Integer, Col> cols = new HashMap<Integer, Col>();

	public int getRowNo() {
		return rowNo;
	}

	public void setRowNo(int rowNo) {
		this.rowNo = rowNo;
	}

	public Map<Integer, Col> getCols() {
		return cols;
	}

	public void setCols(Map<Integer, Col> cols) {
		this.cols = cols;
	}

	public Col getCol(int colIdx) {
		return cols.get(colIdx);
	}

	public Col putCol(Col col) {
		return cols.put(col.getColNo(), col);
	}
}