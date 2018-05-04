package pers.acp.communications.server.file.ftp;

import pers.acp.communications.server.file.user.ServerUser;

/**
 * Created by zhangbin on 2016/12/20.
 * FTP 用户
 */
public class FTPServerUser extends ServerUser {

    public void setWritepermission(boolean writepermission) {
        this.writepermission = writepermission;
    }

    public boolean isWritepermission() {
        return writepermission;
    }

    public int getMaxloginnumber() {
        return maxloginnumber;
    }

    public void setMaxloginnumber(int maxloginnumber) {
        this.maxloginnumber = maxloginnumber;
    }

    public int getMaxloginperip() {
        return maxloginperip;
    }

    public void setMaxloginperip(int maxloginperip) {
        this.maxloginperip = maxloginperip;
    }

    public int getIdletime() {
        return idletime;
    }

    public void setIdletime(int idletime) {
        this.idletime = idletime;
    }

    public int getUploadrate() {
        return uploadrate;
    }

    public void setUploadrate(int uploadrate) {
        this.uploadrate = uploadrate;
    }

    public int getDownloadrate() {
        return downloadrate;
    }

    public void setDownloadrate(int downloadrate) {
        this.downloadrate = downloadrate;
    }

    /**
     * 是否有写权限
     */
    private boolean writepermission;

    private int maxloginnumber = 0;

    private int maxloginperip = 0;

    private int idletime = 0;

    private int uploadrate = 0;

    private int downloadrate = 0;

}
