package com.yjfei.excel;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

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

public class ExcelParser<T> {
    private static Logger                 logger  = LoggerFactory.getLogger(ExcelParser.class);

    private final static ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

    @Resource
    private static ITemplateFactory       templateFactory;

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

    public static <T> ExcelResult<T> parse(InputStream input, Class<? extends AbstractExcelTemplate> templateClazz,
                                           Class<? extends T> targetClazz) {
        return parse(input, 0, templateClazz, targetClazz);
    }

    public static <T> ExcelResult<T> parse(InputStream input, int sheetIndex,
                                           Class<? extends AbstractExcelTemplate> templateClazz,
                                           Class<? extends T> targetClazz) {

        ExcelResult<T> result = new ExcelResult<T>();
        try {
            Workbook workBook = WorkbookFactory.create(input);
            convertAndValidate(workBook, sheetIndex, templateClazz, targetClazz, result);
            result.setSuccess(true);
        } catch (Throwable e) {
            logger.error("parse excel pojo {} error!", e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }

        return result;
    }

    private static <T> void convertAndValidate(Workbook workBook, int sheetIndex,
                                               Class<? extends AbstractExcelTemplate> templateClazz,
                                               Class<? extends T> targetClazz, ExcelResult<T> result) {

        Sheet sheet = workBook.getSheetAt(sheetIndex);
        int columnNum = 0;
        if (sheet.getRow(0) != null) {
            columnNum = sheet.getRow(0).getLastCellNum() - sheet.getRow(0).getFirstCellNum();
        }
        Map<String, ColumnInfo> columns = getTemplateFactory().getColumns(templateClazz);
        if (columns.size() > columnNum) {
            throw new RuntimeException("列数不正确：" + templateClazz.getName() + ",列数=" + columns.size() + ",文档列数="
                    + columnNum);
        }

        int rowNum = sheet.getLastRowNum() + 1;//总行数
        AbstractExcelTemplate template = getTemplateFactory().getTemplate(templateClazz);

        if (rowNum == 0 || template.getDataIndex() > rowNum) {

            throw new RuntimeException("行数不正确：" + templateClazz.getName() + ",数据起始行=" + template.getDataIndex()
                    + ",文档总行数=" + rowNum);
        }

        if (checkTitle(columns, sheet, template)) {
            result.setTotal(rowNum);
            for (int i = template.getDataIndex(); i < rowNum; i++) {
                Row dataRow = sheet.getRow(i);
                if (null == dataRow) {
                    result.incrementErrorCount();
                    if (template.isIgnoreError()) {
                        result.getErrorMap().put(i, "数据为空！");
                        continue;
                    } else {

                        throw new RuntimeException(new String("数据行" + i + "为空！"));
                    }
                }
                T dataPojo = ReflectUtil.newInstance(targetClazz, true);

                AbstractExcelTemplate temPojo = ReflectUtil.newInstance(templateClazz, true);

                Map<String, Field> targetFieldMap = getTemplateFactory().getClassField(targetClazz);
                //解析数据列
                boolean paserSuccess = true;
                StringBuilder sb = new StringBuilder();
                for (Entry<String, ColumnInfo> entry : columns.entrySet()) {
                    ColumnInfo columnInfo = entry.getValue();
                    Cell cell = dataRow.getCell(columnInfo.getIndex());
                    String strVal = getCellValue(cell);
                    ConvertInfo convertInfo = columnInfo.getConvert();
                    if (convertInfo != null) {

                        try {
                            Object val = convertInfo.getConvertor().convert(strVal);

                            columnInfo.getField().set(temPojo, val);
                            Field targetField = targetFieldMap.get(entry.getKey());
                            if (targetField != null) {
                                targetField.set(dataPojo, val);
                            } else {
                                if (template.isIgnoreError()) {
                                    sb.append("第" + i + "行").append(columnInfo.getDisplayName() + "解析报错:")
                                            .append("目标field为null").append("\r\n");
                                } else {
                                    throw new RuntimeException(targetClazz.getName() + "的" + entry.getKey() + "为空！");
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            paserSuccess = false;
                            if (template.isIgnoreError()) {
                                sb.append("第" + i + "行").append(columnInfo.getDisplayName() + "解析报错:")
                                        .append(e.getMessage()).append("\r\n");
                            } else {
                                throw new RuntimeException(e);
                            }
                        }
                    } else {

                        paserSuccess = false;
                        if (template.isIgnoreError()) {
                            sb.append(columnInfo.getDisplayName() + "解析报错:").append("转化器报错").append("\r\n");
                        } else {
                            throw new RuntimeException("转化器报错");
                        }
                    }

                }

                if (!paserSuccess) {
                    result.incrementErrorCount();
                    result.getErrorMap().put(i, sb.toString());
                    continue;
                }

                //验证数据列
                Validator validator = factory.getValidator();
                paserSuccess = true;
                Set<ConstraintViolation<AbstractExcelTemplate>> constratint = validator.validate(temPojo);
                if (constratint != null && constratint.size() > 0) {
                    for (ConstraintViolation<AbstractExcelTemplate> cv : constratint) {
                        String propName = cv.getPropertyPath().toString();
                        ColumnInfo cInfo = columns.get(propName);
                        if (cInfo != null) {
                            sb.append(cInfo.getDisplayName()).append("[").append(cv.getMessage()).append("]")
                                    .append("\n");
                            paserSuccess = false;
                        }
                    }
                }

                if (paserSuccess) {
                    result.getSuccessList().add(dataPojo);
                } else {
                    result.getErrorMap().put(i, sb.toString());
                    result.incrementErrorCount();
                }
            }
        }

    }

    private static boolean checkTitle(Map<String, ColumnInfo> columns, Sheet sheet, AbstractExcelTemplate template) {
        boolean success = true;
        StringBuilder sb = new StringBuilder("标题错误：");
        if (template.isCheckTitle()) {
            Row titleRow = sheet.getRow(template.getTitleIndex());

            for (Entry<String, ColumnInfo> entry : columns.entrySet()) {
                Cell cell = titleRow.getCell(entry.getValue().getIndex(), Row.CREATE_NULL_AS_BLANK);
                String titleName = getCellValue(cell);
                if (!entry.getValue().getTitle().equals(titleName)) {

                    sb.append(entry.getValue().getDisplayName()).append("名称不符合").append("\r\n");
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
            //数值  
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
                    // 则转换为BigDecimal类型的字符串 
                    if (temp.indexOf("e") > -1 || temp.indexOf("E") > -1) { //科学计数法处理
                        DecimalFormat decFormat = new DecimalFormat("0");
                        cellValue = decFormat.format(Double.valueOf(temp));
                    } else if (temp.indexOf(".") > -1) {
                        cellValue = String.valueOf(new Double(temp)).trim();
                    } else {
                        cellValue = temp.trim();
                    }
                    cell.setCellValue(cellValue); //设置value为字符串内容，否则影响公式解析
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

    //去除结尾的非间断空格 及其他插件空格，如制表符/enter
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
