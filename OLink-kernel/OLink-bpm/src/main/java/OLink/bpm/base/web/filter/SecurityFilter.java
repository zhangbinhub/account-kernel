package OLink.bpm.base.web.filter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import OLink.bpm.constans.Environment;
import OLink.bpm.constans.Web;
import OLink.bpm.core.sso.SSOUtil;
import OLink.bpm.core.superuser.ejb.SuperUserProcess;
import OLink.bpm.core.superuser.ejb.SuperUserVO;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.http.CookieUtil;
import OLink.bpm.util.property.DefaultProperty;
import OLink.bpm.util.property.PropertyUtil;
import OLink.bpm.util.StringUtil;
import org.apache.log4j.Logger;

import eWAP.core.ResourcePool;

/**
 * @author Nicholas
 */
public class SecurityFilter extends HttpServlet implements Filter {

    private final static Logger LOG = Logger.getLogger(SecurityFilter.class);

    private static final long serialVersionUID = -853305800678372152L;

    private static boolean ACCESS_ADMIN = false;

    private static SecurityFilterConfig filterConfig = null;

    static {
        try {
            ACCESS_ADMIN = Boolean.parseBoolean(DefaultProperty
                    .getProperty("ACCESS_ADMIN"));
            filterConfig = SecurityFilterConfig.getInstance();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest hreq = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String uri = hreq.getRequestURI();
        if (ResourcePool.getRunAs() < 2.0) {
            chain.doFilter(hreq, resp);
            return;
        }

        Environment evt = Environment.getInstance();
        if (StringUtil.isBlank(evt.getBaseUrl())) {
            String scheme = hreq.getScheme();
            String serverAddr = hreq.getLocalAddr();
            int port = hreq.getServerPort();
            evt.setBaseUrl(scheme + "://" + serverAddr + ":" + port);
        }

        String queryString = hreq.getQueryString();
        if (queryString != null) {
            if ((queryString.toUpperCase().contains("<SCRIPT>") && queryString
                    .toUpperCase().contains("</SCRIPT>"))
                    || (queryString.toUpperCase().contains("%3CSCRIPT%3E") && queryString
                    .toUpperCase().contains("%3C%2FSCRIPT%3E"))) {
                hreq.setAttribute("error", "{*[Path_Name]*}{*[Error]*}");
                RequestDispatcher dispatcher = hreq
                        .getRequestDispatcher(filterConfig.getErrorPage());
                dispatcher.forward(hreq, resp);
                return;
            }
        }

        if (isExcludeURI(uri)) {
            chain.doFilter(request, response);
        } else {
            HttpSession session = hreq.getSession();
            WebUser user = (WebUser) session
                    .getAttribute(Web.SESSION_ATTRIBUTE_FRONT_USER);
            WebUser admin = (WebUser) session
                    .getAttribute(Web.SESSION_ATTRIBUTE_USER);
            if (uri.contains(".dwr") && admin != null) {
                chain.doFilter(request, response);
                return;
            }
            if (isForegroundURI(uri)) { // 是否为前台
                try {
                    SSOUtil ssoUtil = new SSOUtil();
                    // 前台SSO模式
                    if (Web.AUTHENTICATION_TYPE_SSO.equals(PropertyUtil
                            .get(Web.AUTHENTICATION_TYPE))) {
                        user = ssoUtil.checkSSO(hreq, resp);
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                    LOG.warn(e1);
                }

                // 检查URI权限
                if (user != null) {
                    chain.doFilter(request, response);
                } else { // 用户不存在
                    LOG.warn(uri);
                    /**
                     * 如果是单点登录,则直接跳转到登陆页面, 避免用户配置的跳转页面有误导致timeOut.jsp页面无限循环
                     */
                    if (Web.AUTHENTICATION_TYPE_SSO.equals(PropertyUtil
                            .get(Web.AUTHENTICATION_TYPE))) {
                        String msg = hreq.getHeader("Authorization");
                        /**
                         * 如果Authorization的信息不为null或者不是AD单点登录,则重定向
                         */
                        if (!"ADUserSSO".equals(PropertyUtil
                                .get(Web.SSO_IMPLEMENTATION))
                                || msg != null) {
                            resp.sendRedirect(hreq.getContextPath()
                                    + filterConfig.getForeground().getLoginpage());
                        }
                    } else {
                        resp.sendRedirect(hreq.getContextPath()
                                + filterConfig.getForeground().getTimeoutpage());
                    }
                }
            } else {
                Map<String, String> info = CookieUtil.getSSO(hreq);
                // 根据URL参数登录
                String account = info.get("loginno");
                String password = info.get("password");
                if (ACCESS_ADMIN) {
                    if (admin == null && account != null) {
                        admin = loginAdmin(account, password, hreq);
                    }
                    if (admin != null) {
                        chain.doFilter(request, response);
                    } else {
                        LOG.warn(uri);
                        resp.sendRedirect(hreq.getContextPath()
                                + filterConfig.getBackground().getTimeoutpage());
                    }
                } else {
                    LOG.warn(uri);
                    resp.sendRedirect(hreq.getContextPath()
                            + filterConfig.getBackground().getTimeoutpage());
                }
            }
        }
    }

    /**
     * 是否不作检验的URI
     *
     * @param uri 请求地址
     * @return 是返回true, 否则返回false
     */
    private boolean isExcludeURI(String uri) {
        if (uri.contains(filterConfig.getErrorPage())
                || uri.contains(filterConfig.getForeground().getLoginpage())
                || uri.contains(filterConfig.getForeground().getTimeoutpage())
                || uri.contains(filterConfig.getBackground().getTimeoutpage())) {
            return true;
        } else {
            /**白名单*/
            List<String> notcheckURIs = filterConfig.getNotcheckURI().getKeywords();
            for (String notcheckuri:notcheckURIs
                 ) {
                if(uri.contains(notcheckuri)){
                    return true;
                }
            }
            /**黑名单*/
            List<String> needcheckURIs = filterConfig.getNeedcheckURI().getKeywords();
            for (String needcheckuri:needcheckURIs
                 ) {
                if(uri.contains(needcheckuri)){
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * 是否前台URI
     *
     * @param uri 请求地址
     * @return 是返回true, 否则返回false
     */
    private boolean isForegroundURI(String uri) {
        List<String> foregroundURIs = filterConfig.getForegroundURI().getKeywords();
        for (String foregroundURI : foregroundURIs
                ) {
            if (uri.contains(foregroundURI)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 登录后台管理
     *
     * @return webUser
     */
    private WebUser loginAdmin(String username, String password,
                               HttpServletRequest request) {
        try {
            HttpSession session = request.getSession();
            SuperUserProcess sprocess = (SuperUserProcess) ProcessFactory
                    .createProcess(SuperUserProcess.class);

            SuperUserVO user = sprocess.login(username, password);

            if (user != null && user.getStatus() == 1) {
                WebUser webUser = new WebUser(user);
                session.setAttribute(Web.SESSION_ATTRIBUTE_USER, webUser);
                return webUser;
            }
        } catch (Exception e) {
            LOG.warn(request.getRequestURI(), e);
        }

        return null;
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void destroy() {

    }

}
