package com.yjfei.excel.example;

import java.util.Date;

import com.yjfei.excel.common.AbstractExcelTemplate;
import com.yjfei.excel.common.ColumnMeta;
import com.yjfei.excel.common.ConvertMeta;
import com.yjfei.excel.core.StrToDate;
import com.yjfei.excel.core.StrToEnum;
import com.yjfei.excel.core.StrToString;

public class StudentTemplate extends AbstractExcelTemplate {

    @ColumnMeta(index = 3, title = "姓名*")
    @ConvertMeta(convert = StrToString.class)
    private String  name;

    @ColumnMeta(index = 4, title = "性别*")
    @ConvertMeta(convert = StrToEnum.class, param = "getName")
    private SexEnum sex;

    @ColumnMeta(index = 5, title = "出生日期*")
    @ConvertMeta(convert = StrToDate.class, param = "yyyy/dd/MM")
    private Date    birth;

    @ColumnMeta(index = 6, title = "学校班级*")
    @ConvertMeta(convert = StrToEnum.class, param = "getName")
    private String  email;

    public StudentTemplate() {
        super(2, 3);
    }

}
