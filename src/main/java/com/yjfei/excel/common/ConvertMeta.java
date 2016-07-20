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
	/** * format鏍煎紡,濡傛棩鏈燂紝decimal锛屾鍒欒〃杈惧紡,瀵逛簬鏋氫妇鎬у垯涓鸿浆鎹㈢殑鏂规硶鍚嶃�� * * @return */
	String param() default "";

	Class<? extends IConvert> convert() default StrToString.class;

	/** * default */
	String defaultVaule() default "";
}