package pers.acp.communications.client.file;

import pers.acp.tools.common.CommonTools;

/**
 * Created by zhangbin on 2016/12/20.
 * 文件传输基类
 */
abstract class BaseClient {

    String charset = CommonTools.getDefaultCharset();

    /**
     * FTP 登录用户名
     */
    String username;

    /**
     * FTP 登录密码
     */
    String password;

    /**
     * FTP 服务器地址IP地址
     */
    String hostname;

    /**
     * FTP 端口
     */
    int port;

    String remotePath;

    String fileName;

    String localPath;

    BaseClient(String hostname, int port, String username, String password) {
        this.username = username;
        this.password = password;
        this.hostname = hostname;
        this.port = port;
    }

    void formartRemotePath() {
        if (CommonTools.isNullStr(remotePath)) {
            remotePath = "/";
        } else {
            remotePath = remotePath.replace("\\", "/") + "/";
            if (remotePath.contains("../")) {
                remotePath = remotePath.substring(remotePath.lastIndexOf("../") + 3);
            }
            if (remotePath.contains("./")) {
                remotePath = remotePath.substring(remotePath.lastIndexOf("../") + 2);
            }
            if (!remotePath.startsWith("/")) {
                remotePath = "/" + remotePath;
            }
        }
    }

    String[] parseCurrAndSubFold(String remotePath) {
        String fold;
        String subFold;
        if (remotePath.startsWith("/")) {
            fold = "";
            subFold = remotePath.substring(1);
        } else {
            if (remotePath.contains("/")) {
                int index = remotePath.indexOf("/");
                fold = remotePath.substring(0, index);
                subFold = remotePath.substring(index + 1);
            } else {
                fold = remotePath;
                subFold = "";
            }
        }
        return new String[]{fold, subFold};
    }

    public abstract String getServerCharset();

    public abstract void setServerCharset(String serverCharset);

    public String getRemotePath() {
        return remotePath;
    }

    public void setRemotePath(String remotePath) {
        this.remotePath = remotePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

}
