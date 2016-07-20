package com.yjfei.excel.common;

import java.lang.reflect.Field;
import java.util.Map;
import com.yjfei.excel.core.ColumnInfo;

public interface ITemplateFactory {
	Map<String, ColumnInfo> getColumns(Class<?> clz);

	AbstractExcelTemplate getTemplate(Class<?> clz);

	Map<String, Field> getClassField(Class<?> clz);

	String getColumnAlpha(int index);
}