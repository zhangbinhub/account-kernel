package pers.acp.gateway.client.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import pers.acp.gateway.common.GateWaySignType;

/**
 * 网关请求类
 *
 * @author zhang
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ATrade {

    /**
     * 网关服务端序号，与配置文件对应
     */
    int serverNo() default 0;

    /**
     * 网关服务名，与网关服务端服务名对应
     */
    String servername();

    /**
     * 请求报文头编码
     */
    String charset() default "utf-8";

    /**
     * 参数字符集
     */
    String paramEncoding() default "utf-8";

    /**
     * 签名算法
     */
    GateWaySignType signType() default GateWaySignType.MD5;

    /**
     * 是否使用https协议
     */
    boolean isHttps() default false;

    /**
     * 是否等待服务端返回
     */
    boolean isNeedResponse() default true;

    /**
     * 日志是否输出通讯xml报文信息
     */
    boolean isDebug() default true;

}
