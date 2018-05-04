package pers.acp.communications.server.file.user;

import pers.acp.communications.server.file.ftp.FTPServerUser;
import pers.acp.communications.server.file.sftp.SFTPServerUser;

import java.util.List;

/**
 * Created by zhangbin on 2016/12/20.
 * FTP/SFTP 服务用户接口
 */
public interface UserFactory {

    List<FTPServerUser> generateFtpUserList();

    List<SFTPServerUser> generateSFtpUserList();

}
