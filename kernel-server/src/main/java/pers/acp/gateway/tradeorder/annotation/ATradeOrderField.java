package pers.acp.gateway.tradeorder.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 订单字段
 * 
 * @author zhang
 * 
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ATradeOrderField {

	/**
	 * 字段名（大小写不敏感）
	 * 
	 * @return
	 */
	String name();

}
