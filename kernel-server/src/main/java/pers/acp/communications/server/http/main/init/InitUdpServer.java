package pers.acp.communications.server.http.main.init;

import pers.acp.communications.server.socket.base.BaseSocketHandle;
import pers.acp.communications.server.socket.config.ListenConfig;
import pers.acp.communications.server.socket.config.UdpConfig;
import pers.acp.communications.server.socket.udp.UdpServer;
import pers.acp.tools.common.CommonTools;
import org.apache.log4j.Logger;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

public final class InitUdpServer {

    private static Logger log = Logger.getLogger(InitUdpServer.class);// 日志对象

    public static void startUdpServer() {
        log.info("start udp listen service...");
        try {
            UdpConfig udpConfig = UdpConfig.getInstance();
            if (udpConfig != null) {
                ArrayList<ListenConfig> listens = (ArrayList<ListenConfig>) udpConfig.getListen();
                if (listens != null) {
                    for (ListenConfig listen : listens) {
                        if (listen.isEnabled()) {
                            String classname = listen.getResponseClass();
                            if (!CommonTools.isNullStr(classname)) {
                                Class<?> cls = Class.forName(classname);
                                Class<?>[] parameterTypes = {ListenConfig.class};
                                Constructor<?> constructor = cls.getConstructor(parameterTypes);
                                Object[] parameters = {listen};
                                BaseSocketHandle udpResponse = (BaseSocketHandle) constructor.newInstance(parameters);
                                int port = listen.getPort();
                                UdpServer server = new UdpServer(port, listen, udpResponse);
                                Thread sub = new Thread(server);
                                sub.setDaemon(true);
                                sub.start();
                                log.info("start udp listen service success [" + listen.getName() + "] , port:" + listen.getPort());
                            }
                        } else {
                            log.info("udp listen service is disabled [" + listen.getName() + "]");
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
