package pers.acp.communications.client.http;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class HttpsServerClient {

    private Logger log = Logger.getLogger(this.getClass());

    private static int MAXPER_ROUTE = 100;

    private boolean isPost = false;

    private String url;

    private Map<String, String> params;

    private String clientCharset;

    private int timeout;

    private boolean sendXML = false;

    private String rootNameXML = null;

    private boolean sendJSONStr = false;

    private String JSONString;

    private boolean sendBytes = false;

    private byte[] bytes;

    private boolean sendSOAP = false;

    private ClientCommon client = null;

    private Map<String, String> header = new HashMap<>();

    /**
     * 默认构造函数
     */
    public HttpsServerClient() {
        super();
    }

    /**
     * 默认构造函数
     *
     * @param maxper_route 连接池最大连接数，默认100，最大100
     */
    public HttpsServerClient(int maxper_route) {
        if (maxper_route < MAXPER_ROUTE) {
            MAXPER_ROUTE = maxper_route;
        }
    }

    /**
     * GET请求
     *
     * @return 响应信息
     */
    public String doHttpsGet() {
        return doHttpsGet(null);
    }

    /**
     * GET请求
     *
     * @return 响应信息
     */
    public String doHttpsGet(Map<String, String> params) {
        try {
            this.isPost = false;
            this.params = params;
            return doHttpsRequest();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }

    /**
     * POST请求
     *
     * @return 响应信息
     */
    public String doHttpsPost(Map<String, String> params) {
        try {
            this.isPost = true;
            this.sendXML = false;
            this.sendJSONStr = false;
            this.sendBytes = false;
            this.sendSOAP = false;
            this.params = params;
            return doHttpsRequest();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }

    /**
     * POST请求发送XML
     *
     * @return 响应信息
     */
    public String doHttpsPostXML(Map<String, String> params) {
        return doHttpsPostXML(null, params);
    }

    /**
     * POST请求发送XML
     *
     * @return 响应信息
     */
    public String doHttpsPostXML(String rootNameXML, Map<String, String> params) {
        try {
            this.isPost = true;
            this.sendXML = true;
            this.sendJSONStr = false;
            this.sendBytes = false;
            this.sendSOAP = false;
            this.rootNameXML = rootNameXML;
            this.params = params;
            return doHttpsRequest();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }

    /**
     * POST请求发送JSON字符串
     *
     * @return 响应信息
     */
    public String doHttpsPostJSONStr(String jSONString) {
        try {
            this.isPost = true;
            this.sendXML = false;
            this.sendJSONStr = true;
            this.sendBytes = false;
            this.sendSOAP = false;
            this.JSONString = jSONString;
            return doHttpsRequest();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }

    /**
     * POST请求发送字节数组数据
     *
     * @return 响应信息
     */
    public String doHttpsPostBytes(byte[] bytes) {
        try {
            this.isPost = true;
            this.sendXML = false;
            this.sendJSONStr = false;
            this.sendBytes = true;
            this.sendSOAP = false;
            this.bytes = bytes;
            return doHttpsRequest();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }

    /**
     * POST请求发送字节数组数据
     *
     * @return 响应信息
     */
    public String doHttpsPostSOAP(byte[] bytes) {
        try {
            this.isPost = true;
            this.sendXML = false;
            this.sendJSONStr = false;
            this.sendBytes = false;
            this.sendSOAP = true;
            this.bytes = bytes;
            return doHttpsRequest();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }

    /**
     * 执行HTTPs请求
     *
     * @return 响应信息
     */
    private String doHttpsRequest() throws Exception {
        if (client == null) {
            client = new ClientCommon(MAXPER_ROUTE);
        }
        return client.doRequest(timeout, isPost, url, params, clientCharset, header, true,
                sendXML, rootNameXML, sendJSONStr, JSONString, sendBytes, bytes, sendSOAP);
    }

    public boolean isPost() {
        return isPost;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public String getClientCharset() {
        return clientCharset;
    }

    public void setClientCharset(String clientCharset) {
        this.clientCharset = clientCharset;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getUserAgent() {
        return header.get("User-Agent");
    }

    public void setUserAgent(String userAgent) {
        header.put("User-Agent", userAgent);
    }

    public String getCookie() {
        return header.get("Cookie");
    }

    public void setCookie(String cookie) {
        header.put("Cookie", cookie);
    }

    /**
     * 伪装跳转地址
     *
     * @return 跳转地址
     */
    public String getRefer() {
        return header.get("Referer");
    }

    /**
     * 伪装跳转地址
     *
     * @param refer 跳转地址
     */
    public void setRefer(String refer) {
        header.put("Referer", refer);
    }

    public boolean isSendXML() {
        return sendXML;
    }

    public String getRootNameXML() {
        return rootNameXML;
    }

    public boolean isSendJSONStr() {
        return sendJSONStr;
    }

    public String getJSONString() {
        return JSONString;
    }

    public boolean isSendBytes() {
        return sendBytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public boolean isSendSOAP() {
        return sendSOAP;
    }

    public Map<String, String> getHeaders() {
        return header;
    }

    public String getHeader(String name) {
        for (Map.Entry<String, String> entry : header.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(name)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public void addHeader(String name, String value) {
        this.header.put(name, value);
    }

    public void close() {
        if (client != null) {
            client.close();
        }
    }

}
