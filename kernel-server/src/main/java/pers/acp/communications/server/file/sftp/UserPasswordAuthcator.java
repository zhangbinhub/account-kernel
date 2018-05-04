package pers.acp.communications.server.file.sftp;

import org.apache.log4j.Logger;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.session.ServerSession;

import java.util.List;

/**
 * Created by zhangbin on 2016/12/21.
 * 用户认证类
 */
class UserPasswordAuthcator implements PasswordAuthenticator {

    private Logger log = Logger.getLogger(this.getClass());

    private List<SFTPServerUser> userList = null;

    private boolean needAuth;

    UserPasswordAuthcator(List<SFTPServerUser> userList, boolean needAuth) {
        this.userList = userList;
        this.needAuth = needAuth;
    }

    @Override
    public boolean authenticate(String username, String password, ServerSession serverSession) {
        boolean result = false;
        if (needAuth) {
            boolean isexist = false;
            for (SFTPServerUser sftpServerUser : userList) {
                if (sftpServerUser.isEnableflag()) {
                    if (sftpServerUser.getUsername().equals(username)) {
                        isexist = true;
                        if (sftpServerUser.getPassword().equals(password)) {
                            result = true;
                            log.info("sftp user [" + username + "] password authentication successfull");
                            break;
                        } else {
                            log.error("sftp user [" + username + "] password authentication failed : password error");
                        }
                    }
                }
            }
            if (!isexist) {
                log.error("sftp user [" + username + "] password authentication failed : user is not existence");
            }
        } else {
            log.error("sftp server password authentication is not available");
        }
        return result;
    }

}
