package com.yjfei.excel.example;

import java.util.Date;
import javax.validation.constraints.NotNull;

import org.apache.bval.constraints.Email;
import org.apache.bval.constraints.NotEmpty;
import com.yjfei.excel.common.AbstractExcelTemplate;
import com.yjfei.excel.common.ColumnMeta;
import com.yjfei.excel.common.ConvertMeta;
import com.yjfei.excel.core.DateToStr;
import com.yjfei.excel.core.EnumToStr;
import com.yjfei.excel.core.NumberToStr;
import com.yjfei.excel.core.StrToString;
import com.yjfei.excel.rule.Money;

public class ExportTemplate extends AbstractExcelTemplate {
    @ColumnMeta(index = 3, title = "濮撳悕*")
    @ConvertMeta(convert = StrToString.class)
    @NotEmpty
    @NotNull
    private String name;
    @ColumnMeta(index = 4, title = "鎬у埆*")
    @ConvertMeta(convert = EnumToStr.class, param = "getName")
    private SexEnum sex;
    @ColumnMeta(index = 5, title = "鍑虹敓鏃ユ湡*")
    @ConvertMeta(convert = DateToStr.class, param = "yyyy-MM-dd")
    private Date birth;
    @ColumnMeta(index = 6, title = "瀛︽牎鐝骇*")
    @ConvertMeta(convert = EnumToStr.class, param = "getName")
    @Email
    @Money(message = "涓嶆槸閽�")
    private String email;
    @ColumnMeta(index = 1, title = "濮撳悕*")
    @ConvertMeta(convert = NumberToStr.class)
    private int age;

    public ExportTemplate() {
        super(2, 3);
    }
}