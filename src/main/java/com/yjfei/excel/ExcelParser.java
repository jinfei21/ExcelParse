package com.yjfei.excel;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.yjfei.excel.common.AbstractExcelTemplate;
import com.yjfei.excel.common.ITemplateFactory;
import com.yjfei.excel.core.ColumnInfo;
import com.yjfei.excel.core.ConvertInfo;
import com.yjfei.excel.core.DefaultTemplateFactory;
import com.yjfei.excel.util.ReflectUtil;
import com.yjfei.excel.util.StringUtil;

public class ExcelParser<T> {
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

    public static <T> OutputStream export(Class<? extends AbstractExcelTemplate> templateClazz, Collection<T> datas,OutputStream output)
            throws IOException, IntrospectionException {

        ExcelResult<T> result = new ExcelResult<T>();
        Map<String, ColumnInfo> columns = getTemplateFactory().getColumns(templateClazz);
        if (columns.size() == 0) {
            throw new RuntimeException("导出数据为空");
        }
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("data");
        sheet.setDefaultColumnWidth((short) 15);
        HSSFCellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
        titleStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        titleStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        titleStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        titleStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        titleStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        HSSFFont font = workbook.createFont();
        font.setColor(HSSFColor.VIOLET.index);
        font.setFontHeightInPoints((short) 12);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        titleStyle.setFont(font);
        HSSFCellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
        dataStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        dataStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        dataStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        dataStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        dataStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        dataStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        dataStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        HSSFFont font2 = workbook.createFont();
        font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        dataStyle.setFont(font2);
        AbstractExcelTemplate template = getTemplateFactory().getTemplate(templateClazz);
        HSSFRow titleRow = sheet.createRow(template.getTitleIndex());
        for (ColumnInfo column : columns.values()) {
            HSSFCell cell = titleRow.createCell(column.getIndex());
            cell.setCellStyle(titleStyle);
            HSSFRichTextString text = new HSSFRichTextString(column.getTitle());
            cell.setCellValue(text);
        }
        PropertyDescriptor[] props = null;
        int dataIndex = template.getTitleIndex() + 1;
        Iterator<T> it = datas.iterator();
        while (it.hasNext()) {
            T obj = it.next();
            if (props == null) {
                props = ReflectUtil.getPropertyDescriptor(obj.getClass());
            }
            HSSFRow dataRow = sheet.createRow(dataIndex++);
            for (ColumnInfo column : columns.values()) {
                try {
                    String name = column.getField().getName();
                    String c = name.substring(0, 1).toUpperCase(Locale.US);
                    String methName = "get" + c + name.substring(1);
                    Method method = ReflectUtil.getGetMethod(methName, props);
                    Object value = method.invoke(obj, null);
                    ConvertInfo convertInfo = column.getConvert();
                    if (convertInfo != null) {
                        Object val = null;
                        try {
                            val = convertInfo.getConvertor().convert(value);
                            HSSFCell cell = dataRow.createCell(column.getIndex());
                            cell.setCellStyle(dataStyle);
                            cell.setCellValue(String.valueOf(val));
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    } else {
                        HSSFCell cell = dataRow.createCell(column.getIndex());
                        cell.setCellStyle(dataStyle);
                        cell.setCellValue(String.valueOf(value));
                    }
                } catch (Throwable t) {
                    logger.error("parse data pojo {} error!", t);
                }
            }
        }

        workbook.write(output);
        return output;
    }

    public static <T> ExcelResult<T> parse(InputStream input, Class<? extends AbstractExcelTemplate> templateClazz,
                                           Class<? extends T> targetClazz) {
        return parse(input, 0, templateClazz, targetClazz);
    }

    public static <T> ExcelResult<T> parse(InputStream input, int sheetIndex,
                                           Class<? extends AbstractExcelTemplate> templateClazz, Class<? extends T> targetClazz) {
        ExcelResult<T> result = new ExcelResult<T>();
        try {
            Workbook workBook = WorkbookFactory.create(input);
            synConvertAndValidate(workBook, sheetIndex, templateClazz, targetClazz, result);
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
                logger.error("关闭文件流错误", e);
            }
        }
        return result;
    }

    public static <T> ExcelResult<T> quickParse(InputStream input, Class<? extends AbstractExcelTemplate> templateClazz,
                                                Class<? extends T> targetClazz) {
        return quickParse(input, 0, templateClazz, targetClazz);
    }

    public static <T> ExcelResult<T> quickParse(InputStream input, int sheetIndex,
                                                Class<? extends AbstractExcelTemplate> templateClazz, Class<? extends T> targetClazz) {
        ExcelResult<T> result = new ExcelResult<T>();
        try {
            Workbook workBook = WorkbookFactory.create(input);
            result = asyConvertAndValidate(workBook, sheetIndex, templateClazz, targetClazz);
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
                logger.error("关闭文件流错误", e);
            }
        }
        return result;
    }

    private static <T> void synConvertAndValidate(Workbook workBook, int sheetIndex,
                                                  Class<? extends AbstractExcelTemplate> templateClazz, Class<? extends T> targetClazz,
                                                  ExcelResult<T> result) {
        Sheet sheet = workBook.getSheetAt(sheetIndex);
        int columnNum = 0;
        if (sheet.getRow(0) != null) {
            columnNum = sheet.getRow(0).getLastCellNum() - sheet.getRow(0).getFirstCellNum();
        }
        Map<String, ColumnInfo> columns = getTemplateFactory().getColumns(templateClazz);
        if (columns.size() > columnNum) {
            throw new RuntimeException(
                    "解析模版" + templateClazz.getName() + "总列数=" + columns.size() + "超过实际列数=" + columnNum);
        }
        int rowNum = sheet.getLastRowNum() + 1;//
        AbstractExcelTemplate template = getTemplateFactory().getTemplate(templateClazz);
        if (rowNum > template.getMaxRow()) {
            throw new RuntimeException("解析模版：" + templateClazz.getName() + ",模版行数=" + template.getMaxRow()
                    + ",小实际行数=" + rowNum);
        }
        if (rowNum == 0 || template.getDataIndex() > rowNum) {
            throw new RuntimeException("解析模版" + templateClazz.getName() + ",数据行数=" + template.getDataIndex()
                    + ",大于实际行数=" + rowNum);
        }
        if (checkTitle(columns, sheet, template)) {
            result.setTotal(rowNum - template.getDataIndex());
            parseSheetRowData(sheet, template.getDataIndex(), rowNum, template, columns, targetClazz, result);
        }
    }

    private static <T> ExcelResult<T> asyConvertAndValidate(Workbook workBook, int sheetIndex,
                                                            final Class<? extends AbstractExcelTemplate> templateClazz, final Class<? extends T> targetClazz) {
        ExcelResult<T> result = new ExcelResult<T>();
        Sheet sheet = workBook.getSheetAt(sheetIndex);
        int columnNum = 0;
        if (sheet.getRow(0) != null) {
            columnNum = sheet.getRow(0).getLastCellNum() - sheet.getRow(0).getFirstCellNum();
        }
        Map<String, ColumnInfo> columns = getTemplateFactory().getColumns(templateClazz);
        if (columns.size() > columnNum) {
            throw new RuntimeException(
                    "解析模版" + templateClazz.getName() + ",解析列数" + columns.size() + ",大于实际列数" + columnNum);
        }
        int rowNum = sheet.getLastRowNum() + 1;// 鎬昏鏁�
        AbstractExcelTemplate template = getTemplateFactory().getTemplate(templateClazz);
        if (rowNum > template.getMaxRow()) {
            throw new RuntimeException("解析模版" + templateClazz.getName() + ",解析最大行数=" + template.getMaxRow()
                    + ",小于实际函数=" + rowNum);
        }
        if (rowNum == 0 || template.getDataIndex() > rowNum) {
            throw new RuntimeException("解析模版" + templateClazz.getName() + ",解析起始行数=" + template.getDataIndex()
                    + ",大于实际行数" + rowNum);
        }
        if (checkTitle(columns, sheet, template)) {
            result.setTotal(rowNum - template.getDataIndex());
            List<Future<ExcelResult<T>>> list = new ArrayList<Future<ExcelResult<T>>>();
            for (int i = template.getDataIndex(); i < rowNum; ) {
                int end = i + DATA_NUM;
                if (end > rowNum) {
                    end = rowNum;
                }
                Future<ExcelResult<T>> future = threadPool
                        .submit(new ParseTask<T>(sheet, i, end, templateClazz, columns, targetClazz));
                list.add(future);
                i = end;
            }
            for (Future<ExcelResult<T>> future : list) {
                try {
                    ExcelResult<T> fResult = future.get();
                    result.getSuccessList().addAll(fResult.getSuccessList());
                    result.getErrorMap().putAll(fResult.getErrorMap());
                    result.addErrorCount(fResult.getErrorCount());
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private static class ParseTask<T> implements Callable<ExcelResult<T>> {
        private int start;
        private int end;
        private Sheet sheet;
        private Class<? extends AbstractExcelTemplate> templateClazz;
        private Map<String, ColumnInfo> columns;
        private Class<? extends T> targetClazz;

        public ParseTask(Sheet sheet, int start, int end, Class<? extends AbstractExcelTemplate> templateClazz,
                         Map<String, ColumnInfo> columns, Class<? extends T> targetClazz) {
            this.start = start;
            this.end = end;
            this.sheet = sheet;
            this.templateClazz = templateClazz;
            this.columns = columns;
            this.targetClazz = targetClazz;
        }

        @Override
        public ExcelResult<T> call() throws Exception {
            ExcelResult<T> result = new ExcelResult<T>();
            AbstractExcelTemplate template = ReflectUtil.newInstance(templateClazz, true);
            parseSheetRowData(sheet, start, end, template, columns, targetClazz, result);
            return result;
        }
    }

    private static <T> void parseSheetRowData(Sheet sheet, int start, int end, AbstractExcelTemplate template,
                                              Map<String, ColumnInfo> columns, Class<? extends T> targetClazz, ExcelResult<T> result) {
        for (int i = start; i < end; i++) {
            Row dataRow = sheet.getRow(i);
            if (null == dataRow) {
                result.incrementErrorCount();
                if (template.isIgnoreError()) {
                    result.getErrorMap().put(i, "数据为空");
                    continue;
                } else {
                    throw new RuntimeException("数据第" + i + "行为空");
                }
            }
            Map<String, String> rawMap = convertToRawMap(dataRow, columns);
            if (rawMap == null) {
                continue;
            }
            StringBuilder sb = new StringBuilder();
            Map<String, Object> dataMap = new HashMap<String, Object>();
            boolean paserSuccess = convertToTemplateObj(rawMap, dataMap, template, columns, sb);
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<AbstractExcelTemplate>> constratint = validator.validate(template);
            if (constratint != null && constratint.size() > 0) {
                for (ConstraintViolation<AbstractExcelTemplate> cv : constratint) {
                    String propName = cv.getPropertyPath().toString();
                    ColumnInfo cInfo = columns.get(propName);
                    if (cInfo != null) {
                        sb.append(cInfo.getDisplayName()).append("[").append(cv.getMessage()).append("]")
                                .append("\r\n");
                        paserSuccess = false;
                    }
                }
            }
            if (paserSuccess) {
                T dataPojo = convertToTargetObj(dataMap, targetClazz, columns, template, sb);
                if (dataPojo != null) {
                    result.getSuccessList().add(dataPojo);
                    continue;
                }
            }
            result.getErrorMap().put(i, sb.toString());
            result.incrementErrorCount();
        }
    }

    private static Map<String, String> convertToRawMap(Row dataRow, Map<String, ColumnInfo> columns) {
        Map<String, String> rawMap = new HashMap<String, String>();
        boolean isIncludeRow = false;
        for (Entry<String, ColumnInfo> entry : columns.entrySet()) {
            ColumnInfo columnInfo = entry.getValue();
            Cell cell = dataRow.getCell(columnInfo.getIndex());
            String strVal = getCellValue(cell);
            if (StringUtil.isNotBlank(strVal)) {
                isIncludeRow = true;
                rawMap.put(entry.getKey(), strVal);
            }
        }
        if (isIncludeRow) {
            return rawMap;
        } else {
            return null;
        }
    }

    private static boolean convertToTemplateObj(Map<String, String> srcMap, Map<String, Object> dstMap,
                                                AbstractExcelTemplate template, Map<String, ColumnInfo> columns, StringBuilder sb) {
        boolean paserSuccess = true;
        for (Entry<String, ColumnInfo> entry : columns.entrySet()) {
            ColumnInfo columnInfo = entry.getValue();
            ConvertInfo convertInfo = columnInfo.getConvert();
            if (convertInfo != null) {
                Object val = null;
                try {
                    val = convertInfo.getConvertor().convert(srcMap.get(entry.getKey()));
                    columnInfo.getField().set(template, val);
                    dstMap.put(entry.getKey(), val);
                } catch (Throwable e) {
                    e.printStackTrace();
                    paserSuccess = false;
                    if (template.isIgnoreError()) {
                        sb.append(columnInfo.getDisplayName() + "解析错误:").append(e.getMessage()).append("\r\n");
                    } else {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                paserSuccess = false;
                if (template.isIgnoreError()) {
                    sb.append(columnInfo.getDisplayName() + "解析错误:").append("\r\n");
                } else {
                    throw new RuntimeException("解析错误");
                }
            }
        }
        return paserSuccess;
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
                        sb.append(columnInfo.getDisplayName() + "解析错误:").append(e.getMessage()).append("\r\n");
                    } else {
                        throw new RuntimeException(targetClazz.getName() + "解析" + entry.getKey() + "发生错误");
                    }
                }
            } else {
                paserSuccess = false;
                if (template.isIgnoreError()) {
                    ColumnInfo columnInfo = columns.get(entry.getKey());
                    sb.append(columnInfo.getDisplayName() + "解析错误field为null").append("\r\n");
                } else {
                    throw new RuntimeException(targetClazz.getName() + "解析" + entry.getKey() + "发生错误");
                }
            }
        }
        if (paserSuccess) {
            return dataPojo;
        } else {
            return null;
        }
    }

    private static boolean checkTitle(Map<String, ColumnInfo> columns, Sheet sheet, AbstractExcelTemplate template) {
        boolean success = true;
        StringBuilder sb = new StringBuilder("标题检查错误");
        if (template.isCheckTitle()) {
            Row titleRow = sheet.getRow(template.getTitleIndex());
            for (Entry<String, ColumnInfo> entry : columns.entrySet()) {
                Cell cell = titleRow.getCell(entry.getValue().getIndex(), Row.CREATE_NULL_AS_BLANK);
                String titleName = getCellValue(cell);
                if (!entry.getValue().getTitle().equals(titleName)) {
                    sb.append(entry.getValue().getDisplayName()).append("名称不对").append("\r\n");
                    success = false;
                }
            }
        }
        if (!success) {
            throw new RuntimeException(sb.toString());
        }
        return success;
    }

    private static String getCellValue(Cell cell) {
        if (cell == null)
            return null;
        String cellValue = "";
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BLANK:
                cellValue = "";
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                cellValue = Boolean.toString(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date cellDateValue = cell.getDateCellValue();
                    SimpleDateFormat myFormat = new SimpleDateFormat("yyyy/MM/dd");
                    cellValue = myFormat.format(cellDateValue);
                } else {
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    String temp = cell.getStringCellValue();
                    if (null == temp) {
                        temp = "";
                    }
                    if (temp.indexOf("e") > -1 || temp.indexOf("E") > -1) {
                        DecimalFormat decFormat = new DecimalFormat("0");
                        cellValue = decFormat.format(Double.valueOf(temp));
                    } else if (temp.indexOf(".") > -1) {
                        cellValue = String.valueOf(new Double(temp)).trim();
                    } else {
                        cellValue = temp.trim();
                    }
                    cell.setCellValue(cellValue);
                }
                break;
            case Cell.CELL_TYPE_STRING:
                cellValue = trimEndSpecialBlank(cell.getStringCellValue().trim());
                break;
            case Cell.CELL_TYPE_ERROR:
                cellValue = "";
                break;
            case Cell.CELL_TYPE_FORMULA:
                Workbook wb = cell.getSheet().getWorkbook();
                CreationHelper crateHelper = wb.getCreationHelper();
                FormulaEvaluator evaluator = crateHelper.createFormulaEvaluator();
                cellValue = getCellValue(evaluator.evaluateInCell(cell));
                break;
            default:
                cellValue = "";
                break;
        }
        return cellValue;
    }

    public static String trimEndSpecialBlank(String x) {
        char[] charArray = x.toCharArray();
        for (int i = (charArray.length - 1); i > -1; i--) {

            if (((int) charArray[i]) == 160 || Character.isWhitespace(charArray[i])) {
                x = String.valueOf(charArray, 0, i);
            } else {
                break;
            }
        }
        return x;
    }
}
