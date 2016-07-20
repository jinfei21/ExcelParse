package com.yjfei.excel.example;

import java.io.Serializable;

public enum SexEnum implements Serializable {
	M("man"), F("female");
	private String name;

	SexEnum(String name) {
		this.name = name;
	}

	public SexEnum getName(String name) {      
		if ("man".equals(name)) {        
			return M;      
		} else {        
			return F;      
		}   
	}

	public static String getName(Enum sex) {
		if (sex != null) {
			return ((SexEnum) sex).name;
		} else {
			return "nan";
		}
	}
}