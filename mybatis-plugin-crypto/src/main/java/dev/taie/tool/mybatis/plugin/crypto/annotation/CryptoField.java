package dev.taie.tool.mybatis.plugin.crypto.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface CryptoField {
    String fieldName();

    /**
     * 结果是否脱敏
     */
    boolean desensitize() default false;

    /**
     * 参数中含有该字符串被认为是脱敏后的信息
     */
    String desensitizationMake() default "****";

    /**
     * 自定义字段业务类型
     */
    String customType() default "";
}
