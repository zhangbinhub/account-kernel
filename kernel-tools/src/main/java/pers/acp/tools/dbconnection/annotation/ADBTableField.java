package pers.acp.tools.dbconnection.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import pers.acp.tools.dbconnection.entity.DBTableFieldType;

/**
 * 表字段
 *
 * @author zhang
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ADBTableField {

    /**
     * 字段名（大小写不敏感）
     */
    String name();

    /**
     * 字段类型
     */
    DBTableFieldType fieldType();

    /**
     * 字段是否允许空值，默认true
     */
    boolean allowNull() default true;

}
