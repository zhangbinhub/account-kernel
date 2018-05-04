package pers.acp.communications.server.file.sftp;

import pers.acp.tools.security.key.KeyManagement;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import org.apache.log4j.Logger;
import org.apache.sshd.server.PublickeyAuthenticator;
import org.apache.sshd.server.session.ServerSession;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

/**
 * Created by zhangbin on 2016/12/21.
 * 用户认证类
 */
class UserPublicKeyAuthcator implements PublickeyAuthenticator {

    private Logger log = Logger.getLogger(this.getClass());

    private List<SFTPServerUser> userList = null;

    private boolean needAuth;

    private String keyAuthMode;

    private String keyAuthType;

    UserPublicKeyAuthcator(List<SFTPServerUser> userList, boolean needAuth, String keyAuthMode, String keyAuthType) {
        this.userList = userList;
        this.needAuth = needAuth;
        this.keyAuthMode = keyAuthMode;
        this.keyAuthType = keyAuthType;
    }

    @Override
    public boolean authenticate(String username, PublicKey publicKey, ServerSession serverSession) {
        boolean result = false;
        if (needAuth) {
            boolean isexist = false;
            for (SFTPServerUser sftpServerUser : userList) {
                if (sftpServerUser.isEnableflag()) {
                    if (sftpServerUser.getUsername().equals(username)) {
                        isexist = true;
                        try {
                            PublicKey userPublicKey = getUserPublicKey(sftpServerUser.getPublicKey());
                            if (publicKey.equals(userPublicKey)) {
                                result = true;
                                log.info("sftp user [" + username + "] certificate authentication successfull");
                            } else {
                                result = false;
                                log.error("sftp user [" + username + "] certificate authentication failed : certificate is invalid");
                            }
                        } catch (Exception e) {
                            log.error("certificate authentication exception : " + e.getMessage(), e);
                            result = false;
                        }
                        break;
                    }
                }
            }
            if (!isexist) {
                log.error("sftp user [" + username + "] certificate authentication failed : user is not existence");
            }
        } else {
            log.error("sftp server certificate authentication is not available");
        }
        return result;
    }

    private PublicKey getUserPublicKey(String publicKey) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException, Base64DecodingException {
        PublicKey userPublicKey;
        switch (keyAuthType) {
            case "der":
                if (keyAuthMode.equals("DSA")) {
                    userPublicKey = KeyManagement.getDSAPublicKeyForDER(publicKey);
                } else {
                    userPublicKey = KeyManagement.getRSAPublicKeyForDER(publicKey);
                }
                break;
            case "pem":
                if (keyAuthMode.equals("DSA")) {
                    userPublicKey = KeyManagement.getDSAPublicKeyForPEM(publicKey);
                } else {
                    userPublicKey = KeyManagement.getRSAPublicKeyForPEM(publicKey);
                }
                break;
            case "ssh":
                if (keyAuthMode.equals("DSA")) {
                    userPublicKey = KeyManagement.getDSAPublicKeyForSSH(publicKey);
                } else {
                    userPublicKey = KeyManagement.getRSAPublicKeyForSSH(publicKey);
                }
                break;
            default:
                userPublicKey = null;
        }
        return userPublicKey;
    }

}
