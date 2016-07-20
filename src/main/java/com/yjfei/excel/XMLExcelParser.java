package com.yjfei.excel;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.yjfei.excel.common.AbstractExcelTemplate;
import com.yjfei.excel.common.Col;
import com.yjfei.excel.common.ISheetParseHandler;
import com.yjfei.excel.common.ITemplateFactory;
import com.yjfei.excel.common.ParseContext;
import com.yjfei.excel.common.Row;
import com.yjfei.excel.core.ColumnInfo;
import com.yjfei.excel.core.ConvertInfo;
import com.yjfei.excel.core.DefaultTemplateFactory;
import com.yjfei.excel.core.ExcelReader;
import com.yjfei.excel.util.ReflectUtil;

public class XMLExcelParser<T> {
	private static Logger logger = LoggerFactory.getLogger(ExcelParser.class);
	private final static ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
	private final static int DATA_NUM = 200;
	@Resource
	private static ITemplateFactory templateFactory;
	private static ExecutorService threadPool = Executors.newCachedThreadPool();

	private static ITemplateFactory getTemplateFactory() {
		if (templateFactory == null) {
			synchronized (ITemplateFactory.class) {
				if (templateFactory == null) {
					templateFactory = new DefaultTemplateFactory();
				}
			}
		}
		return templateFactory;
	}

	public static <T> ExcelResult<T> parse(InputStream input, int sheetIndex,
			Class<? extends AbstractExcelTemplate> templateClazz, Class<? extends T> targetClazz) {
		ExcelResult<T> result = new ExcelResult<T>();
		try {
			ExcelReader reader = new ExcelReader(input);
			synConvertAndValidate(reader, sheetIndex, templateClazz, targetClazz, result);
			result.setSuccess(true);
		} catch (Throwable e) {
			logger.error("parse excel pojo {} error!", e);
			result.setSuccess(false);
			result.setErrorMsg(e.getMessage());
		} finally {
			try {
				if (null != input) {
					input.close();
				}
			} catch (Exception e) {
				logger.error("鍏抽棴娴佸け璐ャ��", e);
			}
		}
		return result;
	}

	private static <T> void synConvertAndValidate(ExcelReader reader, int sheetIndex,
			Class<? extends AbstractExcelTemplate> templateClazz, final Class<? extends T> targetClazz,
			final ExcelResult<T> result) {
		try {
			final AbstractExcelTemplate template = getTemplateFactory().getTemplate(templateClazz);
			final Map<String, ColumnInfo> columns = getTemplateFactory().getColumns(templateClazz);
			final AtomicInteger total = new AtomicInteger(0);
			reader.parse(sheetIndex, new ISheetParseHandler() {
				@Override
				public void startRow(Row row, ParseContext context) {
				}

				@Override
				public void start(ParseContext context) {
				}

				@Override
				public void endRow(Row row, ParseContext context) {
					if (row.getRowNo() == template.getTitleIndex() + 1) {
						checkTitle(columns, row, template);
					} else if (row.getRowNo() > (template.getTitleIndex() + 1)) {
						total.incrementAndGet();
						parseSheetRowData(row, template, columns, targetClazz, result);
					}
				}

				@Override
				public void end(ParseContext context) {
					result.setTotal(total.get());
				}
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static boolean checkTitle(Map<String, ColumnInfo> columns, Row row, AbstractExcelTemplate template) {
		boolean success = true;
		StringBuilder sb = new StringBuilder("鏍囬閿欒锛�");
		if (template.isCheckTitle()) {
			for (Entry<String, ColumnInfo> entry : columns.entrySet()) {
				String titleName = row.getCols().get(entry.getValue().getIndex() + 1).getStrVal();
				if (!entry.getValue().getTitle().equals(titleName)) {
					sb.append(entry.getValue().getDisplayName()).append("鍚嶇О涓嶇鍚�").append("\r\n");
					success = false;
				}
			}
		}
		if (!success) {
			throw new RuntimeException(sb.toString());
		}
		return success;
	}

	

	private static <T> void parseSheetRowData(Row row, AbstractExcelTemplate template, Map<String, ColumnInfo> columns,
			Class<? extends T> targetClazz, ExcelResult<T> result) { // 瑙ｆ瀽鏁版嵁鍒�
		StringBuilder sb = new StringBuilder();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		boolean paserSuccess = convertToTemplateObj(row, dataMap, template, columns, sb); // 楠岃瘉鏁版嵁鍒�
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<AbstractExcelTemplate>> constratint = validator.validate(template);
		if (constratint != null && constratint.size() > 0) {
			for (ConstraintViolation<AbstractExcelTemplate> cv : constratint) {
				String propName = cv.getPropertyPath().toString();
				ColumnInfo cInfo = columns.get(propName);
				if (cInfo != null) {
					sb.append(cInfo.getDisplayName()).append("[").append(cv.getMessage()).append("]").append("\r\n");
					paserSuccess = false;
				}
			}
		}
		if (paserSuccess) {
			T dataPojo = convertToTargetObj(dataMap, targetClazz, columns, template, sb);
			if (dataPojo != null) {
				result.getSuccessList().add(dataPojo);
				return;
			}
		}
		result.getErrorMap().put(row.getRowNo(), sb.toString());
		result.incrementErrorCount();
	}

	private static <T> T convertToTargetObj(Map<String, Object> map, Class<? extends T> targetClazz,
			Map<String, ColumnInfo> columns, AbstractExcelTemplate template, StringBuilder sb) {
		T dataPojo = ReflectUtil.newInstance(targetClazz, true);
		Map<String, Field> targetFieldMap = getTemplateFactory().getClassField(targetClazz);
		boolean paserSuccess = true;
		for (Entry<String, Object> entry : map.entrySet()) {
			Field targetField = targetFieldMap.get(entry.getKey());
			if (targetField != null) {
				try {
					targetField.set(dataPojo, entry.getValue());
				} catch (Throwable e) {
					paserSuccess = false;
					if (template.isIgnoreError()) {
						ColumnInfo columnInfo = columns.get(entry.getKey());
						sb.append(columnInfo.getDisplayName() + "瑙ｆ瀽鎶ラ敊:").append(e.getMessage()).append("\r\n");
					} else {
						throw new RuntimeException(targetClazz.getName() + "鐨�" + entry.getKey() + "涓虹┖锛�");
					}
				}
			} else {
				paserSuccess = false;
				if (template.isIgnoreError()) {
					ColumnInfo columnInfo = columns.get(entry.getKey());
					sb.append(columnInfo.getDisplayName() + "瑙ｆ瀽鎶ラ敊:").append("鐩爣field涓簄ull").append("\r\n");
				} else {
					throw new RuntimeException(targetClazz.getName() + "鐨�" + entry.getKey() + "涓虹┖锛�");
				}
			}
		}
		if (paserSuccess) {
			return dataPojo;
		} else {
			return null;
		}
	}

	private static boolean convertToTemplateObj(Row row, Map<String, Object> dstMap, AbstractExcelTemplate template,
			Map<String, ColumnInfo> columns, StringBuilder sb) {
		boolean paserSuccess = true;
		for (Entry<String, ColumnInfo> entry : columns.entrySet()) {
			ColumnInfo columnInfo = entry.getValue();
			ConvertInfo convertInfo = columnInfo.getConvert();
			if (convertInfo != null) {
				Object val = null;
				try {
					Col col = row.getCol(columnInfo.getIndex() + 1);
					val = convertInfo.getConvertor().convert(col.getStrVal());
					columnInfo.getField().set(template, val);
					dstMap.put(entry.getKey(), val);
				} catch (Throwable e) {
					e.printStackTrace();
					paserSuccess = false;
					if (template.isIgnoreError()) {
						sb.append(columnInfo.getDisplayName() + "瑙ｆ瀽鎶ラ敊:").append(e.getMessage()).append("\r\n");
					} else {
						throw new RuntimeException(e);
					}
				}
			} else {
				paserSuccess = false;
				if (template.isIgnoreError()) {
					sb.append(columnInfo.getDisplayName() + "瑙ｆ瀽鎶ラ敊:").append("娌℃湁閰嶇疆杞寲鍣�").append("\r\n");
				} else {
					throw new RuntimeException("杞寲鍣ㄦ姤閿�");
				}
			}
		}
		return paserSuccess;
	}
}