package pers.acp.communications.server.http.main.init;

import pers.acp.communications.server.file.config.FTPConfig;
import pers.acp.communications.server.file.ftp.FTPServer;
import pers.acp.communications.server.file.user.UserFactory;
import pers.acp.tools.common.CommonTools;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by zhangbin on 2016/12/20.
 * 初始化FTP服务器
 */
public class InitFtpServer {

    /**
     * 日志对象
     */
    private static Logger log = Logger.getLogger(InitFtpServer.class);

    public static void startFtpServer() {
        log.info("start ftp servers...");
        try {
            FTPConfig ftpConfig = FTPConfig.getInstance();
            if (ftpConfig != null) {
                List<FTPConfig.Listen> listens = ftpConfig.getListens();
                if (listens != null) {
                    for (FTPConfig.Listen listen : listens) {
                        if (listen.isEnabled()) {
                            String classname = listen.getUserFactoryClass();
                            if (!CommonTools.isNullStr(classname)) {
                                Class<?> cls = Class.forName(classname);
                                UserFactory userFactory = (UserFactory) cls.newInstance();
                                FTPServer ftpServer = new FTPServer(userFactory.generateFtpUserList(), listen);
                                Thread sub = new Thread(ftpServer);
                                sub.setDaemon(true);
                                sub.start();
                                log.info("start ftp server success [" + listen.getName() + "] port:" + listen.getPort());
                            } else {
                                log.info("start ftp server failed [" + listen.getName() + "] : user factory class is null");
                            }
                        } else {
                            log.info("ftp server is disabled [" + listen.getName() + "]");
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
