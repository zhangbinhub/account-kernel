package pers.acp.communications.server.http.servlet.handle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class HttpServletRequestAcp extends HttpServletRequestWrapper {

    private String oldCharset = null;

    public HttpServletRequestAcp(String oldCharset, HttpServletRequest request) {
        super(request);
        this.oldCharset = oldCharset;
    }

    /**
     * 获取客户端请求时使用的字符集，默认为utf-8
     *
     * @return 字符集
     */
    public String getOldCharset() {
        return this.oldCharset;
    }

    /**
     * 获取REST连接地址
     *
     * @return rest url
     */
    public String getRestUrl() {
        return (String) this.getAttribute("_rest");
    }

}
