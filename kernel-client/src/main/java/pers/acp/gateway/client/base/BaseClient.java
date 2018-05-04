package pers.acp.gateway.client.base;

import pers.acp.communications.client.http.HttpServerClient;
import pers.acp.communications.client.http.HttpsServerClient;
import pers.acp.gateway.client.GateWayClientConfig;
import pers.acp.gateway.client.GateWayResult;
import pers.acp.gateway.client.annotation.ATrade;
import pers.acp.gateway.client.annotation.ATradeField;
import pers.acp.gateway.common.GateWaySignType;
import pers.acp.gateway.common.GateWayTools;
import pers.acp.tools.common.CommonTools;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseClient {

    /**
     * 日志对象
     */
    private Logger log = Logger.getLogger(this.getClass());

    private int serverNo;

    private String encode;

    private String paramEncoding;

    private GateWaySignType signType;

    private boolean isHttps;

    private boolean isNeedResponse;

    private boolean isDebug;

    /**
     * 获取请求参数
     *
     * @return 参数Map
     */
    private Map<String, String> getParams() throws IllegalArgumentException, IllegalAccessException {
        Map<String, String> params = new HashMap<>();
        if (this.getClass().isAnnotationPresent(ATrade.class)) {
            ATrade aTrade = this.getClass().getAnnotation(ATrade.class);
            encode = aTrade.charset();
            if (CommonTools.isNullStr(encode)) {
                encode = CommonTools.getDefaultCharset();
            }
            paramEncoding = aTrade.paramEncoding();
            if (CommonTools.isNullStr(paramEncoding)) {
                paramEncoding = CommonTools.getDefaultCharset();
            }
            serverNo = aTrade.serverNo();
            signType = aTrade.signType();
            isHttps = aTrade.isHttps();
            isNeedResponse = aTrade.isNeedResponse();
            isDebug = aTrade.isDebug();
            params.put("servername", aTrade.servername());
            Class<?> cls = this.getClass();
            while (cls != null && cls.isAnnotationPresent(ATrade.class)) {
                Field[] fields = cls.getDeclaredFields();
                for (Field field : fields) {
                    if (field.isAnnotationPresent(ATradeField.class)) {
                        ATradeField aTradeField = field.getAnnotation(ATradeField.class);
                        if (aTradeField.isParam()) {
                            params.put(aTradeField.paramName(), String.valueOf(field.get(this)));
                        }
                    }
                }
                cls = cls.getSuperclass();
            }
        }
        return params;
    }

    /**
     * 执行请求
     *
     * @return 结果对象
     */
    public GateWayResult doRequest() {
        GateWayResult result;
        try {
            if (beforeRequest()) {
                List<GateWayClientConfig.Server> clients = GateWayClientConfig.getInstance().getServers();
                GateWayClientConfig.Server clientConfig = null;
                for (GateWayClientConfig.Server config : clients) {
                    if (config.getServerNo() == serverNo) {
                        clientConfig = config;
                    }
                }
                if (clientConfig != null) {
                    Map<String, String> param = getParams();
                    String paramStr = GateWayTools.buildRequestParam(param, signType, clientConfig.getTradeKey());
                    if (isDebug) {
                        log.info("gate way request:");
                        log.info(paramStr);
                    }
                    String responsStr;
                    if (isHttps) {
                        HttpsServerClient client = new HttpsServerClient();
                        client.setUrl(clientConfig.getUrl());
                        client.setTimeout(clientConfig.getTimeout());
                        client.setClientCharset(encode);
                        responsStr = client.doHttpsPostBytes(paramStr.getBytes(paramEncoding));
                    } else {
                        HttpServerClient client = new HttpServerClient();
                        client.setUrl(clientConfig.getUrl());
                        client.setTimeout(clientConfig.getTimeout());
                        client.setClientCharset(encode);
                        responsStr = client.doHttpPostBytes(paramStr.getBytes(paramEncoding));
                    }
                    if (isNeedResponse) {
                        result = GateWayTools.getResult(responsStr, clientConfig.getTradeKey());
                    } else {
                        result = new GateWayResult();
                        result.setStatus(0);
                        result.setErrorMessage("request is success!");
                    }
                    if (isDebug) {
                        log.info("gate way respons:");
                        log.info(responsStr);
                    }
                } else {
                    result = new GateWayResult();
                    result.setStatus(-1);
                    result.setErrorMessage("serverNo " + serverNo + " is not find!");
                }
            } else {
                result = new GateWayResult();
                result.setStatus(-2);
                result.setErrorMessage("beforeRequest is faild!");
            }
            afterRequest(result);
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result = new GateWayResult();
            result.setStatus(-3);
            result.setErrorMessage(e.getMessage());
            return result;
        }
    }

    /**
     * 执行请求前调用的方法
     *
     * @return 成功或失败
     */
    public abstract boolean beforeRequest();

    /**
     * 执行请求后调用的方法
     *
     * @param result 结果对象
     */
    public abstract void afterRequest(GateWayResult result);

}
