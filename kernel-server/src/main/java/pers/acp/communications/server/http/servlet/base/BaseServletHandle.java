package pers.acp.communications.server.http.servlet.base;

import pers.acp.communications.server.base.BaseCommunication;
import pers.acp.communications.server.http.config.HttpConfig;
import pers.acp.communications.server.http.servlet.handle.HttpServletRequestAcp;
import pers.acp.communications.server.http.servlet.handle.HttpServletResponseAcp;
import pers.acp.tools.common.CommonTools;
import pers.acp.tools.exceptions.ConfigException;

public abstract class BaseServletHandle extends BaseCommunication {

    /**
     * http服务端配置类
     */
    private HttpConfig httpConfig = null;

    /**
     * servlet 请求
     */
    protected HttpServletRequestAcp request;

    /**
     * servlet 响应
     */
    protected HttpServletResponseAcp response;

    /**
     * servlet 构造函数
     *
     * @param request  请求对象
     * @param response 响应对象
     */
    public BaseServletHandle(HttpServletRequestAcp request, HttpServletResponseAcp response) throws ConfigException {
        super();
        this.request = request;
        this.response = response;
        this.httpConfig = HttpConfig.getInstance();
        if (this.httpConfig.getAppIdName() != null) {
            this.appid = request.getParameter(this.httpConfig.getAppIdName());
        }
        if (this.httpConfig.getDbnoName() != null) {
            if (!CommonTools.isNullStr(request.getParameter(this.httpConfig.getDbnoName()))) {
                this.dbno = Integer.valueOf(request.getParameter(this.httpConfig.getDbnoName()));
            }
        }
        if (this.httpConfig.getOperatorIdName() != null) {
            this.operatorId = request.getParameter(this.httpConfig.getOperatorIdName());
        }
    }

    /**
     * 获取http请求配置类
     *
     * @return 配置对象
     */
    protected HttpConfig getHttpConfig() {
        return httpConfig;
    }

}
