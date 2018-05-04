package pers.acp.gateway.client.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 网关请求参数
 *
 * @author zhang
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ATradeField {

    /**
     * 参数名称（不区分大小写，通讯过程中强制转换为小写）
     */
    String paramName();

    /**
     * 此属性是否以参数形式传递，并参与签名
     */
    boolean isParam() default true;

}
