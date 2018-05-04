package pers.acp.tools.dbconnection.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据库表
 *
 * @author zhang
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ADBTable {

    /**
     * 表名（大小写不敏感）
     */
    String tablename() default "";

    /**
     * 父类中的字段是否分别存储在不同的表，默认false
     */
    boolean isSeparate() default false;

    /**
     * 是否是虚拟表，如果是虚拟表，该类中的字段全部归属于第一个非虚拟表的子孙类，不进行单独存储，默认false
     *
     * @return true-虚拟表，false-实体表
     */
    boolean isVirtual() default false;

}
