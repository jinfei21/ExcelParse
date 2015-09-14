package com.yjfei.excel.example;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.apache.bval.constraints.Email;
import org.apache.bval.constraints.NotEmpty;

import com.yjfei.excel.common.AbstractExcelTemplate;
import com.yjfei.excel.common.ColumnMeta;
import com.yjfei.excel.common.ConvertMeta;
import com.yjfei.excel.core.StrToDate;
import com.yjfei.excel.core.StrToEnum;
import com.yjfei.excel.core.StrToNumber;
import com.yjfei.excel.core.StrToString;

public class StudentTemplate extends AbstractExcelTemplate {

    @ColumnMeta(index = 3, title = "姓名*")
    @ConvertMeta(convert = StrToString.class)
    @NotEmpty
    @NotNull
    private String  name;

    @ColumnMeta(index = 4, title = "性别*")
    @ConvertMeta(convert = StrToEnum.class)
    private SexEnum sex;

    @ColumnMeta(index = 5, title = "出生日期*")
    @ConvertMeta(convert = StrToDate.class, param = "yyyy/dd/MM")
    private Date    birth;

    @ColumnMeta(index = 6, title = "学校班级*")
    @ConvertMeta(convert = StrToEnum.class, param = "getName")
    @Email
    private String  email;

    @ColumnMeta(index = 1, title = "姓名*")
    @ConvertMeta(convert = StrToNumber.class)
    private int     age;

    public StudentTemplate() {
        super(2, 3);
    }

}
