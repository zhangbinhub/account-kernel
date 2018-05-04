package pers.acp.communications.server.http.main.init;

import org.apache.log4j.Logger;

import pers.acp.communications.server.http.online.OnlineService;
import pers.acp.communications.server.http.config.HttpConfig;

public final class InitOnlineServer {

    private static Logger log = Logger.getLogger(InitOnlineServer.class);// 日志对象

    /**
     * 启动在线用户清理服务
     */
    public static void startOnline(HttpConfig.OnlineServer config) {
        if (config.isEnabled()) {
            OnlineService.getInstance(config.getSpacingTime()).start();
            log.info("init [online user clearup service] success,spacing:" + config.getSpacingTime() + " millisecond");
        } else {
            log.info("[online user clearup service] is disabled!");
        }
    }
}
