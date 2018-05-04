package pers.acp.gateway.common;

import pers.acp.communications.client.http.ParamTools;
import pers.acp.communications.tools.CommunicationTools;
import pers.acp.gateway.client.GateWayClientConfig;
import pers.acp.gateway.client.GateWayResult;
import pers.acp.tools.common.CommonTools;
import pers.acp.tools.exceptions.ConfigException;
import pers.acp.tools.security.MD5Utils;
import pers.acp.tools.security.SHA1Utils;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

public class GateWayTools {

    /**
     * 日志对象
     */
    private static Logger log = Logger.getLogger(GateWayTools.class);

    /**
     * 初始化工具类
     */
    public static void InitTools() throws ConfigException {
        GateWayClientConfig.getInstance();
    }

    /**
     * MAP的键值转换为小写，同时按照自然顺序排序
     *
     * @param param 参数
     * @return 排序后的Map
     */
    private static SortedMap<String, String> keyToLowerCaseAndSort(Map<String, String> param) {
        SortedMap<String, String> sortParam = new TreeMap<>();
        for (Entry<String, String> entry : param.entrySet()) {
            sortParam.put(entry.getKey().toLowerCase(), entry.getValue());
        }
        return sortParam;
    }

    /**
     * 校验签名
     *
     * @param param    参数
     * @param tradeKey 签名密钥，为空字符串或null表示不进行校验，直接返回true
     * @return 是否校验通过
     */
    public static boolean validateSign(Map<String, String> param, String tradeKey) {
        if (CommonTools.isNullStr(tradeKey)) {
            return true;
        }
        SortedMap<String, String> sortParam = keyToLowerCaseAndSort(param);
        String validateStr = "";
        String sign = sortParam.get("sign");
        String sign_type = sortParam.get("sign_type").toUpperCase();
        for (Entry<String, String> entry : sortParam.entrySet()) {
            String key = entry.getKey();
            if (!key.equals("sign") && !key.equals("sign_type")) {
                if (!CommonTools.isNullStr(validateStr)) {
                    validateStr += "&";
                }
                validateStr += key + "=" + entry.getValue();
            }
        }
        if (!CommonTools.isNullStr(validateStr)) {
            validateStr += "&";
        }
        validateStr += tradeKey;
        String localsign;
        if (sign_type.equals(GateWaySignType.MD5.getName())) {
            localsign = MD5Utils.encrypt(validateStr).toUpperCase();
        } else if (sign_type.equals(GateWaySignType.SHA1.getName())) {
            localsign = SHA1Utils.encrypt(validateStr).toUpperCase();
        } else {
            localsign = MD5Utils.encrypt(validateStr).toUpperCase();
        }
        log.info("remote sign:" + sign);
        log.info("local validate string:" + validateStr);
        log.info("local sign:" + localsign);
        return sign.equals(localsign);
    }

    /**
     * 构建返回信息
     *
     * @param param    参数
     * @param signType 签名类型
     * @param tradeKey 交易密钥，为空字符串或null表示不进行签名
     * @return 返回信息
     */
    public static String buildReturnMessage(Map<String, String> param, GateWaySignType signType, String tradeKey) {
        SortedMap<String, String> sortParam = keyToLowerCaseAndSort(param);
        if (sortParam.containsKey("status")) {
            sortParam.remove("status");
        }
        if (sortParam.containsKey("message")) {
            sortParam.remove("message");
        }
        sortParam.put("status", "0");
        return ParamTools.buildPostXMLParam(buildSign(sortParam, signType, tradeKey), "xml", CommonTools.getDefaultCharset());
    }

    /**
     * 构建错误返回信息
     *
     * @param errorMessage 错误信息
     * @param signType     签名类型
     * @param tradeKey     交易密钥，为空字符串或null表示不进行签名
     * @return 错误信息
     */
    public static String buildErrorMessage(String errorMessage, GateWaySignType signType, String tradeKey) {
        Map<String, String> param = new HashMap<>();
        param.put("status", "-1");
        param.put("message", errorMessage);
        return ParamTools.buildPostXMLParam(buildSign(keyToLowerCaseAndSort(param), signType, tradeKey), "xml", CommonTools.getDefaultCharset());
    }

    /**
     * 构建请求参数
     *
     * @param param    参数
     * @param signType 签名类型
     * @param tradeKey 交易密钥，为空字符串或null表示不进行签名
     * @return 请求报文
     */
    public static String buildRequestParam(Map<String, String> param, GateWaySignType signType, String tradeKey) {
        return ParamTools.buildPostXMLParam(buildSign(keyToLowerCaseAndSort(param), signType, tradeKey), "xml", CommonTools.getDefaultCharset());
    }

    /**
     * 获取网关返回信息
     *
     * @param responsStr 响应信息
     * @param tradeKey   签名密钥，为空字符串或null表示不进行校验
     * @return 交易结果对象
     */
    public static GateWayResult getResult(String responsStr, String tradeKey) {
        Map<String, String> response = CommunicationTools.parseXML(responsStr);
        GateWayResult result = new GateWayResult();
        if (validateSign(response, tradeKey)) {
            result.setStatus(Integer.valueOf(response.get("status")));
            if (response.containsKey("message")) {
                result.setErrorMessage(response.get("message"));
            }
            result.setInfo(response);
        } else {
            result.setStatus(-1);
            result.setErrorMessage("response string validate sign is faild!");
        }
        return result;
    }

    /**
     * 构建签名
     *
     * @param param    参数
     * @param signType 签名类型
     * @param tradeKey 签名密钥，为空字符串或null表示不进行签名
     * @return 参数
     */
    private static Map<String, String> buildSign(Map<String, String> param, GateWaySignType signType, String tradeKey) {
        if (!CommonTools.isNullStr(tradeKey)) {
            if (param.containsKey("sign")) {
                param.remove("sign");
            }
            if (param.containsKey("sign_type")) {
                param.remove("sign_type");
            }
            String validateStr = "";
            String sign;
            for (Entry<String, String> entry : param.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (!CommonTools.isNullStr(value)) {
                    if (!CommonTools.isNullStr(validateStr)) {
                        validateStr += "&";
                    }
                    validateStr += key + "=" + value;
                }
            }
            if (!CommonTools.isNullStr(validateStr)) {
                validateStr += "&";
            }
            validateStr += tradeKey;
            log.info("validate string:" + validateStr);
            if (GateWaySignType.MD5.equals(signType)) {
                param.put("sign_type", GateWaySignType.MD5.getName());
                sign = MD5Utils.encrypt(validateStr).toUpperCase();
                log.info("sign_type:" + GateWaySignType.MD5.getName());
            } else if (GateWaySignType.SHA1.equals(signType)) {
                param.put("sign_type", GateWaySignType.SHA1.getName());
                sign = SHA1Utils.encrypt(validateStr).toUpperCase();
                log.info("sign_type:" + GateWaySignType.SHA1.getName());
            } else {
                param.put("sign_type", GateWaySignType.MD5.getName());
                sign = MD5Utils.encrypt(validateStr).toUpperCase();
                log.info("sign_type:" + GateWaySignType.MD5.getName());
            }
            param.put("sign", sign);
            log.info("sign:" + sign);
        }
        return param;
    }

}
