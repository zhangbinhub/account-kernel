package pers.acp.communications.server.http.main.init;

import pers.acp.communications.server.file.config.FTPConfig;
import pers.acp.communications.server.file.config.SFTPConfig;
import pers.acp.communications.server.http.config.HttpConfig;
import pers.acp.communications.server.http.config.InitFunctionConfig;
import pers.acp.communications.server.http.servlet.config.ServletConfig;
import pers.acp.communications.server.soap.webservice.config.WSConfig;
import pers.acp.communications.server.socket.config.TcpConfig;
import pers.acp.communications.server.socket.config.UdpConfig;
import pers.acp.gateway.server.GateWayServerConfig;
import pers.acp.tools.exceptions.ConfigException;
import org.apache.log4j.Logger;

public final class InitConfig {

    private static Logger log = Logger.getLogger(InitConfig.class);// 日志对象

    /**
     * 初始化配置实例
     */
    public static void load() throws ConfigException {
        log.info("start load config service...");
        InitFunctionConfig.getInstance();
        HttpConfig.getInstance();
        ServletConfig.getInstance();
        TcpConfig.getInstance();
        UdpConfig.getInstance();
        WSConfig.getInstance();
        FTPConfig.getInstance();
        SFTPConfig.getInstance();
        GateWayServerConfig.getInstance();
    }
}
