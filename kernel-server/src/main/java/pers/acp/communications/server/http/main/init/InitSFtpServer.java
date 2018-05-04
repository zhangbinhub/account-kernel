package pers.acp.communications.server.http.main.init;

import pers.acp.communications.server.file.config.SFTPConfig;
import pers.acp.communications.server.file.sftp.SFTPServer;
import pers.acp.communications.server.file.user.UserFactory;
import pers.acp.tools.common.CommonTools;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by zhangbin on 2016/12/21.
 * 启动SFTP服务
 */
public class InitSFtpServer {

    /**
     * 日志对象
     */
    private static Logger log = Logger.getLogger(InitFtpServer.class);

    public static void startSFtpServer() {
        log.info("start sftp servers...");
        try {
            SFTPConfig sftpConfig = SFTPConfig.getInstance();
            if (sftpConfig != null) {
                List<SFTPConfig.Listen> listens = sftpConfig.getListens();
                if (listens != null) {
                    for (SFTPConfig.Listen listen : listens) {
                        if (listen.isEnabled()) {
                            String classname = listen.getUserFactoryClass();
                            if (!CommonTools.isNullStr(classname)) {
                                Class<?> cls = Class.forName(classname);
                                UserFactory userFactory = (UserFactory) cls.newInstance();
                                SFTPServer ftpServer = new SFTPServer(userFactory.generateSFtpUserList(), listen);
                                Thread sub = new Thread(ftpServer);
                                sub.setDaemon(true);
                                sub.start();
                                log.info("start sftp server success [" + listen.getName() + "] port:" + listen.getPort());
                            } else {
                                log.info("start sftp server failed [" + listen.getName() + "] : user factory class is null");
                            }
                        } else {
                            log.info("sftp server is disabled [" + listen.getName() + "]");
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
