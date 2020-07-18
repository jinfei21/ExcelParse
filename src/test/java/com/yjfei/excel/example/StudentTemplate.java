package com.yjfei.excel.example;

import java.util.Date;
import javax.validation.constraints.NotNull;

import com.yjfei.excel.core.*;
import org.apache.bval.constraints.Email;
import org.apache.bval.constraints.NotEmpty;
import com.yjfei.excel.common.AbstractExcelTemplate;
import com.yjfei.excel.common.ColumnMeta;
import com.yjfei.excel.common.ConvertMeta;


public class StudentTemplate extends AbstractExcelTemplate {
    @ColumnMeta(index = 3, title = "姓名*")
    @ConvertMeta(convert = StrToString.class)
    @NotEmpty
    @NotNull
    private String name;

//    @ColumnMeta(index = 4, title = "性别*")
//    @ConvertMeta(convert = StrToEnum.class, param = "getName")
//    private SexEnum sex;

    @ColumnMeta(index = 5, title = "出生日期*")
    @ConvertMeta(convert = Str2ToDate.class, param = "yyyy/MM/dd")
    private Date birth;

    @ColumnMeta(index = 6, title = "学校班级*")
    @ConvertMeta(convert = StrToString.class)
    @Email
    private String email;

    @ColumnMeta(index = 1, title = "姓名*")
    @ConvertMeta(convert = StrToNumber.class)
    private int age;

    public StudentTemplate() {
        super(2, 3);
    }
}
