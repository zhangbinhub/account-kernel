package pers.acp.communications.client.http;

import pers.acp.communications.client.soap.SoapType;
import pers.acp.communications.client.soap.WebServiceClient;
import pers.acp.tools.common.CommonTools;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.*;
import java.util.Map.Entry;

public final class ParamTools {

    /**
     * 日志对象
     */
    private static Logger log = Logger.getLogger(ParamTools.class);

    /**
     * 构建GET请求参数
     *
     * @param url           url字符串
     * @param params        参数
     * @param clientCharset 字符集
     * @return url字符串
     */
    public static String buildGetParam(String url, Map<String, String> params, String clientCharset) {
        try {
            if (params != null) {
                for (Entry<String, String> stringStringEntry : params.entrySet()) {
                    String sepStr;
                    if (!url.contains("?")) {
                        sepStr = "?";
                    } else {
                        sepStr = "&";
                    }
                    String key = stringStringEntry.getKey();
                    String val = stringStringEntry.getValue();
                    url += sepStr + key + "=" + URLEncoder.encode(val, clientCharset);
                }
            }
            return url;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }

    /**
     * 构建POST请求参数
     *
     * @param params 参数
     * @return 参数List
     */
    public static List<NameValuePair> buildPostParam(Map<String, String> params) {
        try {
            if (params != null) {
                List<NameValuePair> pairList = new ArrayList<>(params.size());
                for (Entry<String, String> entry : params.entrySet()) {
                    NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue());
                    pairList.add(pair);
                }
                return pairList;
            } else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * 构建POST请求XML
     *
     * @param params        参数
     * @param rootName      null则使用默认“xml”
     * @param clientCharset 字符集
     * @return xml字符串
     */
    public static String buildPostXMLParam(Map<String, String> params, String rootName, String clientCharset) {
        return buildPostXMLParam(params, rootName, clientCharset, false);
    }

    /**
     * 构建POST请求XML
     *
     * @param params        参数
     * @param rootName      null则使用默认“xml”
     * @param clientCharset 字符集
     * @param isIndent      是否自动格式化
     * @return xml字符串
     */
    public static String buildPostXMLParam(Map<String, String> params, String rootName, String clientCharset, boolean isIndent) {
        JSONObject json = new JSONObject();
        String root = "xml";
        if (!CommonTools.isNullStr(rootName)) {
            root = rootName;
        }
        JSONObject info = new JSONObject();
        JSONArray children = new JSONArray();
        for (Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            JSONObject einfo = new JSONObject();
            einfo.put("value", value);
            einfo.put("isdoc", true);
            JSONObject element = new JSONObject();
            element.put(key, einfo);
            children.add(element);
        }
        info.put("children", children);
        json.put(root, info);
        return jsonToXML(json, clientCharset, isIndent);
    }

    /**
     * 解析XML获取Map对象
     *
     * @param xml 根节点下只有一级子节点
     * @return 参数Map
     */
    public static Map<String, String> parseXML(String xml) {
        try {
            Map<String, String> result = new HashMap<>();
            JSONObject json = parseXMLToJson(xml);
            Iterator<?> iKey = json.keys();
            while (iKey.hasNext()) {
                String rootKey = (String) iKey.next();
                JSONObject rootInfo = json.getJSONObject(rootKey);
                JSONArray tags = rootInfo.getJSONArray("children");
                for (int i = 0; i < tags.size(); i++) {
                    JSONObject element = tags.getJSONObject(i);
                    Iterator<?> ieKey = element.keys();
                    while (ieKey.hasNext()) {
                        String key = (String) ieKey.next();
                        JSONObject info = element.getJSONObject(key);
                        result.put(key, info.getString("value"));
                    }
                }
            }
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new HashMap<>();
        }
    }

    /**
     * xml字符串转换为json对象
     *
     * @param xml xml字符串
     * @return {tagname:{value:string,isCDATA:boolean,children:[{},{}]}}
     */
    public static JSONObject parseXMLToJson(String xml) {
        try {
            SAXReader sax = new SAXReader();
            Document document = sax.read(new ByteArrayInputStream(xml.getBytes()));
            Element root = document.getRootElement();
            return generateJSONByXML(root);
        } catch (DocumentException e) {
            log.error(e.getMessage(), e);
            return new JSONObject();
        }
    }

    /**
     * json对象转换为xml字符串
     *
     * @param json          {tagname:{value:string,isCDATA:boolean,children:[{},{}]}}
     * @param clientCharset 字符集
     * @param isIndent      是否自动格式化
     * @return xml字符串
     */
    public static String jsonToXML(JSONObject json, String clientCharset, boolean isIndent) {
        try {
            if (json == null || json.isEmpty()) {
                throw new Exception("json object is null or empty");
            }
            if (json.keySet().size() > 1) {
                throw new Exception("only also allow one root tag");
            }
            Document document = DocumentHelper.createDocument();
            Iterator<?> iKey = json.keys();
            while (iKey.hasNext()) {
                String key = (String) iKey.next();
                JSONObject info = json.getJSONObject(key);
                Element root = document.addElement(key);
                generateXMLElementByJSON(root, info);
            }
            OutputFormat format = OutputFormat.createCompactFormat();
            format.setEncoding(clientCharset);
            format.setNewlines(isIndent);
            format.setIndent(isIndent);
            format.setIndent("    ");
            StringWriter writer = new StringWriter();
            XMLWriter output = new XMLWriter(writer, format);
            output.write(document);
            writer.close();
            output.close();
            return writer.toString();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "";
        }
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
        WebServiceClient wsclient = new WebServiceClient(null);
        if (soapType == null) {
            soapType = SoapType.SOAP_1_2;
        }
        wsclient.setSoapType(soapType);
        wsclient.setNamespace(namespace);
        wsclient.setMethodName(methodName);
        wsclient.setPatameterMap(params);
        return wsclient.getSOAPMessageString();
    }

    /**
     * 从WebService返回报文中获取结果字符串
     *
     * @param soapType       null则为SoapType.SOAP_1_2
     * @param returnName     结果名
     * @param responseString 响应字符串
     * @param charset        字符集
     * @return 结果值
     */
    public static String getSOAPReturnString(SoapType soapType, String returnName, String responseString, String charset) {
        WebServiceClient wsclient = new WebServiceClient(null);
        if (soapType == null) {
            soapType = SoapType.SOAP_1_2;
        }
        wsclient.setSoapType(soapType);
        if (!CommonTools.isNullStr(returnName)) {
            wsclient.setReturnName(returnName);
        }
        return wsclient.getReturnString(responseString, charset);
    }

    /**
     * 通过json生成xml节点
     *
     * @param parent 父节点
     * @param obj    {tagname:{value:string,isCDATA:boolean,children:[{},{}]}}
     */
    private static void generateXMLElementByJSON(Element parent, JSONObject obj) throws Exception {
        String value = "";
        boolean isdoc = true;
        JSONArray children;
        if (obj.containsKey("children")) {
            if (obj.get("children") instanceof JSONArray) {
                children = obj.getJSONArray("children");
                for (int i = 0; i < children.size(); i++) {
                    JSONObject json = children.getJSONObject(i);
                    Iterator<?> iKey = json.keys();
                    while (iKey.hasNext()) {
                        String key = (String) iKey.next();
                        JSONObject info = json.getJSONObject(key);
                        Element root = parent.addElement(key);
                        generateXMLElementByJSON(root, info);
                    }
                }
            } else {
                throw new Exception("children need jsonArray");
            }
        } else {
            if (obj.containsKey("value")) {
                value = obj.getString("value");
            }
            if (obj.containsKey("isCDATA")) {
                isdoc = obj.getBoolean("isCDATA");
            }
            if (isdoc) {
                parent.addCDATA(value);
            } else {
                parent.addText(value);
            }
        }
    }

    /**
     * 生成json对象
     *
     * @param element xml元素对象
     * @return {tagname:{value:string,isCDATA:boolean,children:[{},{}]}}
     */
    private static JSONObject generateJSONByXML(Element element) {
        JSONObject result = new JSONObject();
        JSONObject info = new JSONObject();
        if (!element.isTextOnly()) {
            JSONArray jsonChildren = new JSONArray();
            List<?> children = element.elements();
            for (Object aChildren : children) {
                Element child = (Element) aChildren;
                jsonChildren.add(generateJSONByXML(child));
            }
            info.put("children", jsonChildren);
        } else {
            info.put("value", element.getTextTrim());
            if (isCDATA(element)) {
                info.put("isCDATA", true);
            } else {
                info.put("isCDATA", false);
            }
        }
        result.put(element.getName(), info);
        return result;
    }

    /**
     * 判断节点文本是否是CDATA类型
     *
     * @param node xml节点对象
     * @return 是否是CDATA类型
     */
    private static boolean isCDATA(Node node) {
        if (!node.hasContent())
            return false;
        for (Object o : ((Branch) node).content()) {
            Node n = (Node) o;
            if (Node.CDATA_SECTION_NODE == n.getNodeType()) {
                return true;
            }
        }
        return false;
    }

}
