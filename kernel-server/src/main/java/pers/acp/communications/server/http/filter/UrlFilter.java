package pers.acp.communications.server.http.filter;

import pers.acp.communications.server.http.config.HttpConfig;
import pers.acp.communications.server.http.config.HttpConfig.UrlKeyWord;
import pers.acp.communications.server.http.servlet.handle.HttpServletRequestAcp;
import pers.acp.communications.server.http.servlet.handle.HttpServletResponseAcp;
import pers.acp.tools.common.CommonTools;
import pers.acp.tools.exceptions.ConfigException;
import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class UrlFilter implements Filter {

    private Logger log = Logger.getLogger(this.getClass());// 日志对象

    private static String INDEX_PAGE = "/index.jsp";

    private static String ERROR_PAGE = "/error.jsp";

    private static String encode;

    @Override
    public void init(FilterConfig filterConfig) {
        encode = CommonTools.getDefaultCharset();
        if (CommonTools.isNullStr(encode)) {
            String charset = filterConfig.getInitParameter("encode");
            if (!CommonTools.isNullStr(charset)) {
                encode = charset;
            }
        }
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpServletRequest req = (HttpServletRequest) request;
        String uri = req.getRequestURI();
        if (uri.equals(req.getContextPath() + ERROR_PAGE)) {
            uri = ERROR_PAGE;
        }
        if (uri.equals(req.getContextPath() + INDEX_PAGE)) {
            uri = INDEX_PAGE;
        }
        String oldCharset = req.getCharacterEncoding();
        if (CommonTools.isNullStr(oldCharset)) {
            oldCharset = encode;
        }
        resp.setContentType("text/html;charset=" + encode);
        resp.setCharacterEncoding(encode);

        HttpServletRequestAcp aRequest = new HttpServletRequestAcp(oldCharset, req);
        HttpServletResponseAcp aResponse = new HttpServletResponseAcp(oldCharset, resp);
        try {
            if (isNeedFilter(uri)) {
                log.error("Request Filter：" + uri);
                aResponse.sendRedirect(req.getContextPath() + ERROR_PAGE);
            } else {
                chain.doFilter(aRequest, aResponse);
            }
        } catch (ConfigException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 是否需要拦截
     *
     * @param uri 待检查的uri
     * @return 是否进行拦截
     */
    private boolean isNeedFilter(String uri) throws ConfigException {
        HttpConfig httpConfig = HttpConfig.getInstance();
        List<UrlKeyWord> urlKeyWords = httpConfig.getNoFilters().getUrlKeyWords();
        for (UrlKeyWord urlKeyWord : urlKeyWords) {
            if (uri.contains(urlKeyWord.getValue())) {
                return false;
            }
        }
        return !(uri.equals(INDEX_PAGE) || uri.equals(ERROR_PAGE));
    }
}
