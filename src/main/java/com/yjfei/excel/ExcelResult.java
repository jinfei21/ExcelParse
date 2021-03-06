package com.yjfei.excel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ExcelResult<T> implements Serializable {
	private Boolean success = false;
	private List<T> successList = new ArrayList<T>();
	private int total = 0;
	private AtomicInteger errorCount = new AtomicInteger(0);
	private Map<Integer, String> errorMap = new HashMap<Integer, String>();
	private String errorMsg;

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public List<T> getSuccessList() {
		return successList;
	}

	public void incrementErrorCount() {
		errorCount.incrementAndGet();
	}

	public int getErrorCount() {
		return this.errorCount.get();
	}

	public void addErrorCount(int delta) {
		errorCount.addAndGet(delta);
	}

	public void setSuccessList(List<T> successList) {
		this.successList = successList;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public Map<Integer, String> getErrorMap() {
		return errorMap;
	}

	public void setErrorMap(Map<Integer, String> errorMap) {
		this.errorMap = errorMap;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
}