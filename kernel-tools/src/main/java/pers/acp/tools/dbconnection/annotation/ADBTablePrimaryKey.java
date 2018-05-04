package pers.acp.tools.dbconnection.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import pers.acp.tools.dbconnection.entity.DBTablePrimaryKeyType;

/**
 * 主键
 *
 * @author zhang
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ADBTablePrimaryKey {

    /**
     * 字段名（大小写不敏感）
     */
    String name();

    /**
     * 主键数据类型
     */
    DBTablePrimaryKeyType pKeyType() default DBTablePrimaryKeyType.Uuid;

}
