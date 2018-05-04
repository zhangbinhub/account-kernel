package pers.acp.communications.server.http.main.init;

import java.util.ArrayList;

import javax.xml.ws.Endpoint;

import org.apache.log4j.Logger;

import pers.acp.communications.server.soap.webservice.base.IWebService;
import pers.acp.communications.server.soap.webservice.config.WSConfig;

public class InitWebService {

    private static Logger log = Logger.getLogger(InitWebService.class);// 日志对象

    public static void publishWebService() {
        try {
            WSConfig wsConfig = WSConfig.getInstance();
            if (wsConfig != null) {
                ArrayList<WSConfig.Server> servers = (ArrayList<WSConfig.Server>) wsConfig.getServer();
                if (servers != null) {
                    for (WSConfig.Server server : servers) {
                        String classname = server.getClassName();
                        String href = server.getHref();
                        Class<?> cls = Class.forName(classname);
                        Object instance = cls.newInstance();
                        IWebService ws = (IWebService) instance;
                        String name = ws.getServiceName();
                        href = href + "/" + name;
                        Endpoint.publish(href, instance);
                        log.info("publish webservice [" + name + "] success:[" + classname + "] [" + href + "]");
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
