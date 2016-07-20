package com.yjfei.excel.example;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.yjfei.excel.ExcelParser;

public class ExportTest {
	public static void main(String[] args) throws IOException, IntrospectionException {   
		List<DetailStudent> list = new ArrayList<DetailStudent>();      
		list.add(new DetailStudent("1", SexEnum.M, new Date(), "gets"));      
		list.add(new DetailStudent("1", SexEnum.M, new Date(), "gets"));       
		list.add(new DetailStudent("1", SexEnum.M, new Date(), "gets"));     
		OutputStream output = ExcelParser.export(ExportTemplate.class, list);  
		output.flush();    
		output.close();    }
}