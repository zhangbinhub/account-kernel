package pers.acp.communications.tools;

import pers.acp.communications.client.http.ParamTools;
import pers.acp.communications.client.soap.SoapType;
import pers.acp.tools.common.DBConTools;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

public final class CommunicationTools {

    private static Logger log = Logger.getLogger(CommunicationTools.class);

    /**
     * 初始化文件工具类
     */
    public static void InitTools() {

    }

    /**
     * url编码
     *
     * @param url      url字符串
     * @param encoding 字符编码
     * @return 结果字符串
     */
    public static String urlEncoding(String url, String encoding) {
        try {
            return URLEncoder.encode(url, encoding);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }

    /**
     * url解码
     *
     * @param url      url字符串
     * @param encoding 字符编码
     * @return 结果字符串
     */
    public static String urlDecoding(String url, String encoding) {
        try {
            return URLDecoder.decode(url, encoding);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }

    /**
     * 构建XML，只有一级子节点
     *
     * @param params        参数
     * @param rootName      null则使用默认“xml”
     * @param clientCharset 字符集
     * @param isIndent      是否自动格式化
     * @return xml字符串
     */
    public static String convertToXML(Map<String, String> params, String rootName, String clientCharset, boolean isIndent) {
        return ParamTools.buildPostXMLParam(params, rootName, clientCharset, isIndent);
    }

    /**
     * json对象转换为xml字符串
     *
     * @param json          {tagname:{value:string,isCDATA:boolean,children:[{},{}]}}
     * @param clientCharset 字符集
     * @param isIndent      是否进行格式化
     * @return xml字符串
     */
    public static String jsonToXML(JSONObject json, String clientCharset, boolean isIndent) {
        return ParamTools.jsonToXML(json, clientCharset, isIndent);
    }

    /**
     * 解析XML获取Map对象
     *
     * @param xml 根节点下只有一级子节点
     * @return 结果
     */
    public static Map<String, String> parseXML(String xml) {
        return ParamTools.parseXML(xml);
    }

    /**
     * xml字符串转换为json对象
     *
     * @param xml xml字符串
     * @return {tagname:{value:string,isCDATA:boolean,children:[{},{}]}}
     */
    public static JSONObject parseXMLToJson(String xml) {
        return ParamTools.parseXMLToJson(xml);
    }

    /**
     * 构建POST请求SOAP报文
     *
     * @param namespace  命名空间
     * @param methodName 方法名
     * @param params     参数
     * @return xml-soap字符串
     */
    public static String buildPostSOAPParam(String namespace, String methodName, Map<String, String> params) {
        return buildPostSOAPParam(null, namespace, methodName, params);
    }

    /**
     * 构建POST请求SOAP报文
     *
     * @param soapType   null则为SoapType.SOAP_1_2
     * @param namespace  命名空间
     * @param methodName 方法名
     * @param params     参数
     * @return xml-soap字符串
     */
    public static String buildPostSOAPParam(SoapType soapType, String namespace, String methodName, Map<String, String> params) {
        return ParamTools.buildPostSOAPParam(soapType, namespace, methodName, params);
    }

    /**
     * 从WebService返回报文中获取结果字符串
     *
     * @param responseString xml-soap响应字符串
     * @return 结果
     */
    public static String getSOAPReturnString(String responseString) {
        return getSOAPReturnString(null, responseString);
    }

    /**
     * 从WebService返回报文中获取结果字符串
     *
     * @param soapType       null则为SoapType.SOAP_1_2
     * @param responseString xml-soap响应字符串
     * @return 结果
     */
    public static String getSOAPReturnString(SoapType soapType, String responseString) {
        return getSOAPReturnString(soapType, null, responseString);
    }

    /**
     * 从WebService返回报文中获取结果字符串
     *
     * @param soapType       null则为SoapType.SOAP_1_2
     * @param returnName     返回名称
     * @param responseString xml-soap响应字符串
     * @return 结果
     */
    public static String getSOAPReturnString(SoapType soapType, String returnName, String responseString) {
        return getSOAPReturnString(soapType, returnName, responseString, null);
    }

    /**
     * 从WebService返回报文中获取结果字符串
     *
     * @param soapType       null则为SoapType.SOAP_1_2
     * @param returnName     返回名称
     * @param responseString xml-soap响应字符串
     * @param charset        字符集
     * @return 结果
     */
    public static String getSOAPReturnString(SoapType soapType, String returnName, String responseString, String charset) {
        return ParamTools.getSOAPReturnString(soapType, returnName, responseString, charset);
    }

    /**
     * 校验权限
     *
     * @param appid          应用ID
     * @param userid         用户ID
     * @param permissionCode 编码
     * @return 校验是否通过
     */
    public static boolean validatePermissions(String appid, String userid, String permissionCode) {
        DBConTools dbconTools = new DBConTools();
        String func_sql = "select mf.islog,mf.moduleid from t_Module_Func mf,t_Role_Module_Func_Set rmf,t_User_Role_Set ur where mf.id=rmf.funcid and rmf.roleid=ur.roleid and ur.userid='" +
                userid +
                "' and mf.code='" +
                permissionCode + "' and mf.appid='" + appid + "'";
        List<Map<String, Object>> func = dbconTools.getDataListBySql(func_sql);
        String module_sql = "select m.parentid,case when m.parentid in (select id from t_module) then 'false' else 'true' end as istop from t_Module m,t_Role_Module_Set rm,t_User_Role_Set ur where m.id=rm.moduleid and rm.roleid=ur.roleid and ur.userid='" +
                userid +
                "' and m.code='" +
                permissionCode + "' and m.appid='" + appid + "'";
        List<Map<String, Object>> module = dbconTools.getDataListBySql(module_sql);
        if (func.size() > 0) {
            boolean result = validateModulePermissions(appid, userid, String.valueOf(func.get(0).get("moduleid".toUpperCase())));
            if (result) {
                if ("1".equals(func.get(0).get("islog".toUpperCase()))) {
                    log.info("validate permission:" + permissionCode);
                }
            }
            return result;
        } else {
            return module.size() > 0 && ("true".equals(module.get(0).get("istop".toUpperCase())) || validateModulePermissions(appid, userid, String.valueOf(module.get(0).get("parentid".toUpperCase()))));
        }
    }

    /**
     * 校验系统模块权限
     *
     * @param appid    应用ID
     * @param userid   用户ID
     * @param moduleid 模块ID
     * @return 校验是否通过
     */
    private static boolean validateModulePermissions(String appid, String userid, String moduleid) {
        DBConTools dbconTools = new DBConTools();
        String module_sql = "select m.parentid,case when m.parentid in (select id from t_module) then 'false' else 'true' end as istop from t_Module m,t_Role_Module_Set rm,t_User_Role_Set ur where m.id=rm.moduleid and rm.roleid=ur.roleid and ur.userid='"
                + userid
                + "' and m.id='"
                + moduleid + "' and m.appid='" + appid + "'";
        List<Map<String, Object>> module = dbconTools
                .getDataListBySql(module_sql);
        return module.size() > 0 && ("true".equals(module.get(0).get("istop".toUpperCase())) || validateModulePermissions(appid, userid, String.valueOf(module.get(0).get("parentid".toUpperCase()))));
    }

    /**
     * 构造json错误信息
     *
     * @param errorMessage 错误信息
     * @return json对象
     */
    public static JSONObject buildJSONError(String errorMessage) {
        JSONObject json = new JSONObject();
        json.put("errmsg", errorMessage);
        return json;
    }

    /**
     * 构造json错误信息
     *
     * @param errorMessage 错误信息
     * @return json字符串
     */
    public static String buildJSONErrorStr(String errorMessage) {
        return buildJSONError(errorMessage).toString();
    }
}
