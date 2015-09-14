package com.yjfei.excel.common;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.yjfei.excel.core.StrToString;

@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConvertMeta {

    /**
     * format格式,如日期，decimal，正则表达式,对于枚举性则为转换的方法名。
     * 
     * @return
     */
    String param() default "";

    Class<? extends IConvert> convert() default StrToString.class;

    /**
     * default
     */
    String defaultVaule() default "";

}
