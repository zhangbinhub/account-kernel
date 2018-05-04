package pers.acp.communications.client.file;

import pers.acp.communications.client.exceptions.SFTPException;
import pers.acp.tools.common.CommonTools;
import pers.acp.tools.exceptions.EnumValueUndefinedException;
import com.jcraft.jsch.*;
import org.apache.log4j.Logger;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Properties;

public class SFTPServerClient extends BaseClient {

    private Logger log = Logger.getLogger(this.getClass());

    private String serverCharset = "GBK";

    private int channelMode = ChannelSftp.RESUME;

    private Channel channel = null;

    private ChannelSftp sftp = null;

    private Session session = null;

    /**
     * 密钥文件的路径
     */
    private String keyFilePath;

    /**
     * 密钥口令
     */
    private String passphrase;

    /**
     * 构造基于密码认证的sftp对象
     *
     * @param hostname 远程主机地址
     * @param port     端口号
     * @param username 用户名
     * @param password 密码
     */
    public SFTPServerClient(String hostname, int port, String username, String password) {
        super(hostname, port, username, password);
        this.keyFilePath = "";
        this.passphrase = "";
    }

    @Override
    public String getServerCharset() {
        return serverCharset;
    }

    @Override
    public void setServerCharset(String serverCharset) {
        this.serverCharset = serverCharset;
    }

    /**
     * 构造基于秘钥认证的sftp对象
     *
     * @param keyFilePath 密钥文件绝对路径
     * @param passphrase  密钥口令
     * @param hostname    远程主机地址
     * @param port        端口号
     * @param username    用户名
     */
    public SFTPServerClient(String keyFilePath, String passphrase, String hostname, int port, String username) {
        super(hostname, port, username, "");
        this.keyFilePath = keyFilePath;
        this.passphrase = passphrase;
    }

    private void rebuildSftp() throws NoSuchFieldException, IllegalAccessException {
        if (sftp != null) {
            Field field = sftp.getClass().getDeclaredField("server_version");
            field.setAccessible(true);
            field.set(sftp, 2);
        }
    }

    /**
     * 连接sftp服务器
     */
    private void connect() throws SFTPException {
        try {
            JSch jsch = new JSch();
            if (!CommonTools.isNullStr(keyFilePath)) {
                // 设置密钥
                if (CommonTools.isNullStr(passphrase)) {
                    jsch.addIdentity(keyFilePath);
                } else {
                    jsch.addIdentity(keyFilePath, passphrase);
                }
                log.info("sftp connect,path of private key file：{" + keyFilePath + "}");
            }
            session = jsch.getSession(username, hostname, port);
            if (!CommonTools.isNullStr(password)) {
                session.setPassword(password);
            }
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setDaemonThread(true);
            session.connect();
            channel = session.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp) channel;
            rebuildSftp();
            sftp.setFilenameEncoding(serverCharset);
            log.info(String.format("sftp server hostname:[%s] port:[%s] is connect successfull", hostname, port));
        } catch (Exception e) {
            log.error("Cannot connect to specified sftp server : {" + hostname + "}:{" + port + "} \n Exception message is: {" + e.getMessage() + "}");
            throw new SFTPException(e.getMessage());
        }
    }

    /**
     * 递归创建目录
     *
     * @param remotePath 远程路径
     */
    private void createDirecroty(String remotePath) throws Exception {
        try {
            if (!CommonTools.isNullStr(remotePath)) {
                sftp.cd(remotePath);
            }
        } catch (SftpException e) {
            String[] tmpfold = parseCurrAndSubFold(remotePath);
            String fold = tmpfold[0];
            String subFold = tmpfold[1];
            if (!CommonTools.isNullStr(fold)) {
                try {
                    sftp.cd(fold);
                } catch (SftpException e1) {
                    sftp.mkdir(fold);
                    sftp.cd(fold);
                }
            }
            createDirecroty(subFold);
        }
    }

    /**
     * 关闭连接 server
     */
    private void finalyFunc() {
        if (sftp != null) {
            if (sftp.isConnected()) {
                sftp.disconnect();
            }
        }
        if (channel != null) {
            if (channel.isConnected()) {
                channel.disconnect();
            }
        }
        if (session != null) {
            if (session.isConnected()) {
                session.disconnect();
            }
        }
    }

    /**
     * 文件下载
     *
     * @return 本地文件绝对路径
     */
    public String doDownLoadForSFTP() {
        try {
            if (CommonTools.isNullStr(localPath)) {
                throw new SFTPException("localPath is null");
            } else {
                localPath = localPath.replace("\\", File.separator).replace("/", File.separator);
            }
            if (CommonTools.isNullStr(fileName)) {
                throw new SFTPException("fileName is null");
            }
            connect();
            formartRemotePath();
            String remoteFile = remotePath + fileName;
            String localFile = localPath + File.separator + fileName + ".tmp";
            String localRealFile = localPath + File.separator + fileName;
            File realFile = new File(localRealFile);
            if (realFile.exists()) {
                log.info("sftp download successfull: " + localRealFile);
                return localRealFile;
            }
            File file = new File(localFile);
            sftp.get(remoteFile, localFile, null, channelMode);
            if (file.renameTo(realFile)) {
                log.info("sftp download successfull: " + localRealFile);
                return localRealFile;
            } else {
                return "";
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("sftp download faild!");
            return "";
        } finally {
            finalyFunc();
        }
    }

    /**
     * 上传文件
     */
    public void doUploadForSFTP(File localFile) {
        try {
            if (localFile == null) {
                throw new SFTPException("localFile is null");
            }
            if (CommonTools.isNullStr(fileName)) {
                throw new SFTPException("fileName is null");
            }
            connect();
            formartRemotePath();
            try {
                sftp.cd(remotePath);
            } catch (SftpException e) {
                if (remotePath.startsWith("/")) {
                    remotePath = remotePath.substring(1);
                }
                createDirecroty(remotePath);
            }
            sftp.put(localFile.getAbsolutePath(), fileName, ChannelSftp.RESUME);
            log.info("file:{" + localFile.getAbsolutePath() + "} is upload successful");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            finalyFunc();
        }
    }

    /**
     * 删除目录
     */
    public boolean doDeleteDirForSFTP() {
        try {
            if (CommonTools.isNullStr(fileName)) {
                throw new SFTPException("fileName is null");
            }
            connect();
            formartRemotePath();
            sftp.cd(remotePath);
            sftp.rmdir(fileName);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        } finally {
            finalyFunc();
        }
    }

    /**
     * 删除文件
     */
    public boolean doDeleteFileForSFTP() {
        try {
            if (CommonTools.isNullStr(fileName)) {
                throw new SFTPException("fileName is null");
            }
            connect();
            formartRemotePath();
            sftp.cd(remotePath);
            sftp.rm(fileName);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        } finally {
            finalyFunc();
        }
    }

    public SFTPChannelMode getChannelMode() throws EnumValueUndefinedException {
        return SFTPChannelMode.getEnum(channelMode);
    }

    public void setChannelMode(SFTPChannelMode channelMode) {
        this.channelMode = channelMode.getValue();
    }

}
