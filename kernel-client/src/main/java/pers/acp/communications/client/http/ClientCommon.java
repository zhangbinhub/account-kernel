package pers.acp.communications.client.http;

import pers.acp.communications.client.exceptions.HttpException;
import pers.acp.tools.common.CommonTools;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class ClientCommon {

    private Logger log = Logger.getLogger(this.getClass());

    private static int MAXPER_ROUTE = 100;

    private static TrustManager manager = new X509TrustManager() {

        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1)
                throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

    };

    private CloseableHttpClient client = null;

    /**
     * 构造函数
     */
    ClientCommon() {
        super();
    }

    /**
     * 构造函数
     *
     * @param maxper_route 连接池最大连接数，默认100，最大100
     */
    ClientCommon(int maxper_route) {
        if (maxper_route < MAXPER_ROUTE) {
            MAXPER_ROUTE = maxper_route;
        }
    }

    /**
     * 执行请求
     *
     * @param timeout       超时时间
     * @param isPost        是否post请求
     * @param url           请求url
     * @param params        参数
     * @param clientCharset 字符集
     * @param header        请求头信息
     * @param isHttps       是否https安全链接
     * @param sendXML       是否发送xml报文
     * @param rootName      xml报文根节点名称
     * @param sendJSONStr   是否发送json报文
     * @param JSONString    json字符串
     * @param sendBytes     是否发送字节数组
     * @param bytes         字节数据
     * @param sendSOAP      是否发送SOAP报文
     * @return 响应信息
     */
    String doRequest(int timeout, boolean isPost, String url, Map<String, String> params, String clientCharset, Map<String, String> header, boolean isHttps, boolean sendXML, String rootName, boolean sendJSONStr, String JSONString, boolean sendBytes, byte[] bytes, boolean sendSOAP) throws Exception {
        if (CommonTools.isNullStr(clientCharset)) {
            clientCharset = CommonTools.getDefaultCharset();
        }
        if (!CommonTools.isNullStr(url)) {
            HttpRequestBase request = null;
            CloseableHttpResponse response = null;
            try {
                if (client == null) {
                    RequestConfig.Builder configBuilder = RequestConfig.custom();
                    int MAX_TIMEOUT = 3600000;
                    if (timeout < MAX_TIMEOUT) {
                        configBuilder.setConnectTimeout(timeout);
                        configBuilder.setSocketTimeout(timeout);
                        configBuilder.setConnectionRequestTimeout(timeout);
                    } else {
                        configBuilder.setConnectTimeout(MAX_TIMEOUT);
                        configBuilder.setSocketTimeout(MAX_TIMEOUT);
                        configBuilder.setConnectionRequestTimeout(MAX_TIMEOUT);
                    }
                    configBuilder.setCookieSpec(CookieSpecs.STANDARD_STRICT);
                    configBuilder.setContentCompressionEnabled(true);
                    BasicCookieStore cookieStore = new BasicCookieStore();
                    if (isHttps) {
                        configBuilder.setExpectContinueEnabled(true);
                        configBuilder.setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST));
                        configBuilder.setProxyPreferredAuthSchemes(Collections.singletonList(AuthSchemes.BASIC));
                        SSLContext context = SSLContext.getInstance("TLSv1");
                        context.init(null, new TrustManager[]{manager}, new SecureRandom());
                        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(context, NoopHostnameVerifier.INSTANCE);
                        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.INSTANCE).register("https", socketFactory).build();
                        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
                        connectionManager.setMaxTotal(MAXPER_ROUTE);
                        connectionManager.setDefaultMaxPerRoute(connectionManager.getMaxTotal());
                        client = HttpClients.custom().setDefaultCookieStore(cookieStore).setConnectionManager(connectionManager).setDefaultRequestConfig(configBuilder.build()).build();
                    } else {
                        client = HttpClients.custom().setDefaultCookieStore(cookieStore).setDefaultRequestConfig(configBuilder.build()).build();
                    }
                }
                if (isPost) {
                    request = new HttpPost(url);
                    request = initHeader(request, header);
                    if (sendXML) {
                        if (params == null || params.isEmpty()) {
                            log.error("model is sendXML,but params is null!");
                            throw new HttpException("model is sendXML,but params is null!");
                        }
                        request.addHeader("content-Type", "application/xml;charset=" + clientCharset);
                        request.addHeader("Accept-Charset", clientCharset);
                        String xmlData = ParamTools.buildPostXMLParam(params, rootName, clientCharset);
                        ((HttpPost) request).setEntity(new StringEntity(xmlData, clientCharset));
                    } else if (sendJSONStr) {
                        if (CommonTools.isNullStr(JSONString)) {
                            log.error("model is sendJSONStr,but JSONString is null!");
                            throw new HttpException("model is sendJSONStr,but JSONString is null!");
                        }
                        request.addHeader("content-Type", "application/json;charset=" + clientCharset);
                        request.addHeader("Accept-Charset", clientCharset);
                        ((HttpPost) request).setEntity(new StringEntity(JSONString, clientCharset));
                    } else if (sendBytes) {
                        if (bytes == null || bytes.length == 0) {
                            log.error("model is sendBytes,but bytes is null!");
                            throw new HttpException("model is sendBytes,but bytes is null!");
                        }
                        request.addHeader("content-Type", "application/octet-stream;charset=" + clientCharset);
                        request.addHeader("Accept-Charset", clientCharset);
                        ((HttpPost) request).setEntity(new ByteArrayEntity(bytes));
                    } else if (sendSOAP) {
                        if (bytes == null || bytes.length == 0) {
                            log.error("model is sendSOAP,but bytes is null!");
                            throw new HttpException("model is sendSOAP,but bytes is null!");
                        }
                        request.addHeader("content-Type", "application/soap+xml;charset=" + clientCharset);
                        request.addHeader("Accept-Charset", clientCharset);
                        ((HttpPost) request).setEntity(new ByteArrayEntity(bytes));
                    } else {
                        List<NameValuePair> pairList = ParamTools.buildPostParam(params);
                        request.addHeader("content-Type", "application/x-www-form-urlencoded;charset=" + clientCharset);
                        request.addHeader("Accept-Charset", clientCharset);
                        ((HttpPost) request).setEntity(new UrlEncodedFormEntity(pairList, clientCharset));
                    }
                    response = client.execute(request);
                } else {
                    url = ParamTools.buildGetParam(url, params, clientCharset);
                    request = new HttpGet(url);
                    request = initHeader(request, header);
                    request.addHeader("content-Type", "text/html; charset=" + clientCharset);
                    response = client.execute(request);
                }
                int code = response.getStatusLine().getStatusCode();
                if (code != 200) {
                    if (code == 301 || code == 302) {
                        Header[] headers = response.getHeaders("Location");
                        if (headers.length > 0) {
                            return doRequest(timeout, isPost, headers[0].getValue(), params, clientCharset, header, isHttps,
                                    sendXML, rootName, sendJSONStr, JSONString, sendBytes, bytes, sendSOAP);
                        } else {
                            return "";
                        }
                    }
                }
                String charset = getContentCharset(response);
                HttpEntity ety = response.getEntity();
                String recvStr = EntityUtils.toString(ety, charset);
                EntityUtils.consume(ety);
                return recvStr;
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                log.error(e.getMessage(), e);
                throw new HttpException("Http Client doRequest Exception:" + e.getMessage());
            } finally {
                if (request != null) {
                    request.abort();
                }
                if (response != null) {
                    response.close();
                }
            }
        } else {
            throw new HttpException("Http Client doRequest Exception: The param url is null or empty!");
        }
    }

    /**
     * 设置请求报文头信息
     *
     * @param requestBase 请求对象
     * @param header      头信息
     * @return 请求对象
     */
    private HttpRequestBase initHeader(HttpRequestBase requestBase, Map<String, String> header) {
        header.entrySet().stream().filter(entry -> !CommonTools.isNullStr(entry.getValue())).forEach(entry -> requestBase.addHeader(entry.getKey(), entry.getValue()));
        return requestBase;
    }

    /**
     * 获取服务器响应字符编码
     *
     * @param response 响应对象
     * @return 响应字符编码
     */
    private String getContentCharset(CloseableHttpResponse response) {
        String charset = CommonTools.getDefaultCharset();
        Header header = response.getEntity().getContentType();
        if (header != null) {
            String s = header.getValue();
            if (matcher(s, "(charset)\\s?=\\s?(utf-?8)")) {
                charset = "utf-8";
            } else if (matcher(s, "(charset)\\s?=\\s?(gbk)")) {
                charset = "gbk";
            } else if (matcher(s, "(charset)\\s?=\\s?(gb2312)")) {
                charset = "gb2312";
            }
        }
        if (CommonTools.isNullStr(charset)) {
            charset = "ISO-8859-1";
        }
        return charset;
    }

    private boolean matcher(String s, String pattern) {
        Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE
                + Pattern.UNICODE_CASE);
        Matcher matcher = p.matcher(s);
        return matcher.find();
    }

    void close() {
        try {
            if (client != null) {
                client.close();
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
