package pers.acp.communications.server.http.main.init;

import pers.acp.communications.server.socket.base.BaseSocketHandle;
import pers.acp.communications.server.socket.config.ListenConfig;
import pers.acp.communications.server.socket.config.TcpConfig;
import pers.acp.communications.server.socket.tcp.TcpServer;
import pers.acp.tools.common.CommonTools;
import org.apache.log4j.Logger;

import java.lang.reflect.Constructor;
import java.util.List;

public final class InitTcpServer {

    private static Logger log = Logger.getLogger(InitTcpServer.class);// 日志对象

    public static void startTcpServer() {
        log.info("start tcp listen service...");
        try {
            TcpConfig tcpConfig = TcpConfig.getInstance();
            if (tcpConfig != null) {
                List<ListenConfig> listens = tcpConfig.getListen();
                if (listens != null) {
                    for (ListenConfig listen : listens) {
                        if (listen.isEnabled()) {
                            String classname = listen.getResponseClass();
                            if (!CommonTools.isNullStr(classname)) {
                                Class<?> cls = Class.forName(classname);
                                Class<?>[] parameterTypes = {ListenConfig.class};
                                Constructor<?> constructor = cls.getConstructor(parameterTypes);
                                Object[] parameters = {listen};
                                BaseSocketHandle tcpResponse = (BaseSocketHandle) constructor.newInstance(parameters);
                                int port = listen.getPort();
                                TcpServer server = new TcpServer(port, listen, tcpResponse);
                                Thread sub = new Thread(server);
                                sub.setDaemon(true);
                                sub.start();
                                log.info("start tcp listen service success [" + listen.getName() + "] , port:" + listen.getPort());
                            }
                        } else {
                            log.info("tcp listen service is disabled [" + listen.getName() + "]");
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
