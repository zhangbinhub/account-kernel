package pers.acp.communications.server.http.servlet;

import pers.acp.communications.server.ctrl.CommunicationCtrl;
import pers.acp.communications.server.exceptions.ExcuteServletException;
import pers.acp.communications.server.http.file.DownLoadFile;
import pers.acp.communications.server.http.file.UpLoadFile;
import pers.acp.communications.server.http.param.ParamBuild;
import pers.acp.communications.server.http.security.Security;
import pers.acp.communications.server.http.servlet.config.ServletConfig;
import pers.acp.communications.server.http.servlet.config.ServletConfig.Param;
import pers.acp.communications.server.http.servlet.config.ServletConfig.Server;
import pers.acp.communications.server.http.servlet.handle.HttpServletRequestAcp;
import pers.acp.communications.server.http.servlet.handle.HttpServletResponseAcp;
import pers.acp.communications.server.http.servlet.tools.IpTools;
import pers.acp.tools.common.CommonTools;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

public final class MainServlet extends HttpServlet {

    private static final long serialVersionUID = -6081090304231087777L;

    private Logger log = Logger.getLogger(this.getClass());// 日志对象

    public MainServlet() {
        super();
    }

    public void service(HttpServletRequest req, HttpServletResponse resp) {
        HttpServletRequestAcp request = (HttpServletRequestAcp) req;
        HttpServletResponseAcp response = (HttpServletResponseAcp) resp;
        try {
            if (CommunicationCtrl.isEnabled()) {
                String uri = request.getRequestURI();
                String reststr = uri.substring(uri.lastIndexOf("/ctrl/") + 6).replaceAll("^/+", "").replaceAll("/+$", "");
                String serverName = reststr;
                if (reststr.contains("/")) {
                    serverName = reststr.substring(0, reststr.indexOf("/"));
                    reststr = reststr.substring(reststr.indexOf("/") + 1);
                }
                request.setAttribute("_rest", reststr);
                if (serverName.equals("getPk")) {
                    Security se = new Security(request, response);
                    se.getPublicKey();
                } else if (serverName.equals("download")) {
                    DownLoadFile dn = new DownLoadFile(request, response);
                    dn.doDownLoad();
                } else if (serverName.equals("upload")) {
                    UpLoadFile up = new UpLoadFile(request, response);
                    up.doUpLoad();
                } else if (!CommonTools.isNullStr(serverName)) {
                    ServletConfig servletConfig = ServletConfig.getInstance();
                    List<Server> servers = servletConfig.getServers();
                    if (servers != null) {
                        for (int i = 0; i < servers.size(); i++) {
                            Server server = servers.get(i);
                            String name = server.getName();
                            if (serverName.equals(name)) {
                                String classname = server.getClassname();
                                String methodname = server.getMethod();
                                List<Param> params = server.getParam();
                                Class<?> cls = Class.forName(classname);
                                Class<?>[] parameterTypes = {HttpServletRequestAcp.class, HttpServletResponseAcp.class};
                                Constructor<?> constructor = cls.getConstructor(parameterTypes);
                                Object[] parameters = {request, response};
                                Object instance = constructor.newInstance(parameters);
                                Method method = cls.getMethod(methodname, buildClass(params));
                                log.debug("remote:" + IpTools.getRemoteIP(request) + " call:name=" + name);
                                log.debug("remote real:" + IpTools.getRemoteRealIP(request) + " call:name=" + name);
                                method.invoke(instance, buildValue(params));
                                break;
                            }
                            if (i == servers.size() - 1) {
                                throw new ExcuteServletException("request name [" + serverName + "] is not service");
                            }
                        }
                    } else {
                        throw new ExcuteServletException("request name [" + serverName + "] is not service");
                    }
                }
            } else {
                response.doReturnError("the server is close");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            response.doReturnError(e.getMessage());
        }
    }

    private Class[] buildClass(List<Param> params) {
        if (params == null) {
            return null;
        }
        Class[] result = new Class[params.size()];
        for (int i = 0; i < params.size(); i++) {
            result[i] = ParamBuild.getParamClass(params.get(i).getType());
        }
        return result;
    }

    private Object[] buildValue(List<Param> params) {
        if (params == null) {
            return null;
        }
        Object[] result = new Object[params.size()];
        for (int i = 0; i < params.size(); i++) {
            result[i] = ParamBuild.getParamValue(params.get(i).getType(), params.get(i).getValue());
        }
        return result;
    }
}
