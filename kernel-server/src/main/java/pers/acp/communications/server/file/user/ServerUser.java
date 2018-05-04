package pers.acp.communications.server.file.user;

/**
 * Created by zhangbin on 2016/12/20.
 * FTP/SFTP 用户
 */
public abstract class ServerUser {

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHomedirectory() {
        return homedirectory;
    }

    public void setHomedirectory(String homedirectory) {
        this.homedirectory = homedirectory;
    }

    public boolean isEnableflag() {
        return enableflag;
    }

    public void setEnableflag(boolean enableflag) {
        this.enableflag = enableflag;
    }

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 可访问路径
     */
    private String homedirectory;

    /**
     * 是否启用
     */
    private boolean enableflag;

}
