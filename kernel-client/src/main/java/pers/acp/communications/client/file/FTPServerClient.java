package pers.acp.communications.client.file;

import pers.acp.communications.client.exceptions.FTPException;
import pers.acp.tools.common.CommonTools;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

import java.io.*;

public class FTPServerClient extends BaseClient {

    private Logger log = Logger.getLogger(this.getClass());

    private String serverCharset = "ISO-8859-1";

    private FTPClient ftpClient = new FTPClient();

    private OutputStream out = null;

    private InputStream in = null;

    private RandomAccessFile raf = null;

    /**
     * 构造函数
     *
     * @param hostname 远程主机地址
     * @param port     端口号
     * @param username 用户名
     * @param password 密码
     */
    public FTPServerClient(String hostname, int port, String username, String password) {
        super(hostname, port, username, password);
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
     * 连接到FTP服务器
     *
     * @return true|false
     */
    private boolean connect() throws Exception {
        try {
            int reply;
            ftpClient.connect(hostname, port);
            reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                log.error("ftp connect error,hostname=" + hostname + " port:" + port);
                return false;
            }
            ftpClient.login(username, password);
            reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                log.error("ftp login error,username=" + username + " password=" + password);
                return false;
            }
            //设置以二进制方式传输
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            log.info("ftp server by hostname:{" + hostname + "} username:{" + username + "} is connect successfull");
            return true;
        } catch (Exception e) {
            throw new Exception("ftp connect faild!");
        }
    }

    /**
     * 递归创建目录
     *
     * @param remotePath 远程路径
     */
    private void createDirecroty(String remotePath) throws Exception {
        if (!remotePath.equals("/") && !ftpClient.changeWorkingDirectory(remotePath)) {
            String[] tmpfold = parseCurrAndSubFold(remotePath);
            String fold = tmpfold[0];
            String subFold = tmpfold[1];
            if (!CommonTools.isNullStr(fold)) {
                if (!ftpClient.changeWorkingDirectory(fold)) {
                    if (ftpClient.makeDirectory(fold)) {
                        ftpClient.changeWorkingDirectory(fold);
                    } else {
                        throw new Exception("create remote fold is failed!");
                    }
                }
            }
            createDirecroty(subFold);
        }
    }

    /**
     * 上传文件
     *
     * @param remoteFile 远程文件路径
     * @param localFile  本地文件
     * @return true|false
     */
    private boolean uploadFile(String remoteFile, File localFile) throws Exception {
        in = null;
        long remoteSize;
        //设置被动模式
        ftpClient.enterLocalPassiveMode();
        //检查远程是否存在文件
        FTPFile[] files = ftpClient.listFiles(remoteFile);
        if (files.length == 1) {
            remoteSize = files[0].getSize();
            long localSize = localFile.length();
            if (remoteSize >= localSize) {
                if (!ftpClient.deleteFile(remoteFile)) {
                    throw new Exception("delete remote file is failed!");
                }
                remoteSize = 0;
            }
        } else {
            remoteSize = 0;
        }
        raf = new RandomAccessFile(localFile, "r");
        out = ftpClient.appendFileStream(remoteFile);
        if (remoteSize > 0) {
            ftpClient.setRestartOffset(remoteSize);
            raf.seek(remoteSize);
        }
        byte[] bytes = new byte[1024];
        int c;
        while ((c = raf.read(bytes)) != -1) {
            out.write(bytes, 0, c);
        }
        out.flush();
        raf.close();
        out.close();
        return ftpClient.completePendingCommand();
    }

    /**
     * 关闭连接 server
     */
    private void finalyFunc() {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (raf != null) {
                raf.close();
            }
            if (ftpClient.isConnected()) {
                ftpClient.disconnect();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 从FTP下载文件
     *
     * @return 本地文件绝对路径
     */
    public String doDownLoadForFTP() {
        out = null;
        in = null;
        try {
            if (CommonTools.isNullStr(localPath)) {
                throw new FTPException("localPath is null");
            } else {
                localPath = localPath.replace("\\", File.separator).replace("/", File.separator);
            }
            if (CommonTools.isNullStr(fileName)) {
                throw new FTPException("fileName is null");
            }
            if (!connect()) {
                throw new FTPException("ftp server login faild!");
            }
            formartRemotePath();
            String remoteFile = new String((remotePath + fileName).getBytes(charset), serverCharset);
            String localFile = localPath + File.separator + fileName + ".tmp";
            String localRealFile = localPath + File.separator + fileName;
            //设置被动模式
            ftpClient.enterLocalPassiveMode();
            //检查远程文件是否存在
            FTPFile[] files = ftpClient.listFiles(remoteFile);
            if (files.length != 1) {
                throw new FTPException("remote file is not find!");
            }
            long lRemoteSize = files[0].getSize();
            File realFile = new File(localRealFile);
            if (realFile.exists()) {
                log.info("ftp download successfull: " + localRealFile);
                return localRealFile;
            }
            File file = new File(localFile);
            //本地存在文件，进行断点续传
            if (file.exists()) {
                long localSize = file.length();
                if (localSize == lRemoteSize) {
                    if (file.renameTo(realFile)) {
                        log.info("ftp download successfull: " + localRealFile);
                        return localRealFile;
                    } else {
                        return "";
                    }
                }
                //进行断点续传，并记录状态
                out = new FileOutputStream(file, true);
                ftpClient.setRestartOffset(localSize);
            } else {
                out = new FileOutputStream(file);
            }
            //设置被动模式
            ftpClient.enterLocalPassiveMode();
            in = ftpClient.retrieveFileStream(remoteFile);
            byte[] bytes = new byte[1024];
            int c;
            while ((c = in.read(bytes)) != -1) {
                out.write(bytes, 0, c);
            }
            in.close();
            out.close();
            boolean isDo = ftpClient.completePendingCommand();
            ftpClient.logout();
            ftpClient.disconnect();
            if (isDo) {
                if (file.renameTo(realFile)) {
                    log.info("ftp download successfull: " + localRealFile);
                    return localRealFile;
                } else {
                    return "";
                }
            } else {
                log.error("ftp download faild!");
                return "";
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("ftp download faild!");
            return "";
        } finally {
            finalyFunc();
        }
    }

    /**
     * 上传文件
     *
     * @param localFile 本地文件
     * @return 成功或失败
     */
    public boolean doUpLoadForFTP(File localFile) {
        try {
            if (localFile == null) {
                throw new FTPException("localFile is null");
            }
            if (CommonTools.isNullStr(fileName)) {
                throw new FTPException("fileName is null");
            }
            if (!connect()) {
                throw new FTPException("ftp server login faild!");
            }
            formartRemotePath();
            ftpClient.setControlEncoding(charset);
            remotePath = new String(remotePath.getBytes(charset), serverCharset);
            fileName = new String(fileName.getBytes(charset), serverCharset);
            if (!ftpClient.changeWorkingDirectory(remotePath)) {
                if (remotePath.startsWith("/")) {
                    remotePath = remotePath.substring(1);
                }
                createDirecroty(remotePath);
            }
            boolean uploadResult;
            uploadResult = uploadFile(fileName, localFile);
            ftpClient.logout();
            ftpClient.disconnect();
            if (uploadResult) {
                log.info("ftp download successfull{" + localFile.getName() + "}: " + localFile.getAbsolutePath());
            }
            return uploadResult;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        } finally {
            finalyFunc();
        }
    }

    /**
     * 删除文件
     *
     * @return 成功或失败
     */
    public boolean doDeleteForFTP() {
        try {
            if (CommonTools.isNullStr(fileName)) {
                throw new FTPException("fileName is null");
            }
            if (!connect()) {
                throw new FTPException("ftp server login faild!");
            }
            formartRemotePath();
            remotePath = new String(remotePath.getBytes(charset), serverCharset);
            fileName = new String(fileName.getBytes(charset), serverCharset);
            ftpClient.changeWorkingDirectory(remotePath);

            boolean result = true;
            FTPFile[] files = ftpClient.listFiles(fileName);
            if (files.length == 1) {
                result = ftpClient.deleteFile(fileName);
            }
            ftpClient.logout();
            ftpClient.disconnect();
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        } finally {
            finalyFunc();
        }
    }

}
