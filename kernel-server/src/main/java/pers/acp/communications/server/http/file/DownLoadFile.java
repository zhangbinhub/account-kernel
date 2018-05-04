package pers.acp.communications.server.http.file;

import pers.acp.communications.server.exceptions.ExcuteServletException;
import pers.acp.communications.server.http.servlet.base.BaseServletHandle;
import pers.acp.communications.server.http.servlet.handle.HttpServletRequestAcp;
import pers.acp.communications.server.http.servlet.handle.HttpServletResponseAcp;
import pers.acp.communications.server.http.servlet.tools.ServletTools;
import pers.acp.tools.exceptions.ConfigException;
import pers.acp.tools.file.FileTools;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.URLEncoder;

public final class DownLoadFile extends BaseServletHandle {

    private Logger log = Logger.getLogger(this.getClass());

    public DownLoadFile(HttpServletRequestAcp request, HttpServletResponseAcp response) throws ConfigException {
        super(request, response);
    }

    public void doDownLoad() {
        try {
            String path = request.getParameter("filename");
            String issec = request.getParameter("issec");
            if (issec != null && "true".equals(issec)) {
                path = ServletTools.decryptFromFront(path);
            }
            if (pathFilter(path)) {
                download(path);
            } else {
                throw new ExcuteServletException(
                        "download file faild,the file path is not correct");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            response.doReturnError(e.getMessage());
        }
    }

    /**
     * 文件路径过滤
     *
     * @param path 路径
     * @return true-允许下载 false-不允许下载
     */
    private boolean pathFilter(String path) {
        return path.startsWith("/files/tmp/") || path.startsWith("/files/upload/")
                || path.startsWith("/files/download/");
    }

    private HttpServletResponseAcp download(String path) throws Exception {
        File file = new File(FileTools.getWebRootAbsPath()
                + path.replace("/", File.separator).replace("\\",
                File.separator));
        String filename = file.getName();
        InputStream fis = new BufferedInputStream(new FileInputStream(file));
        byte[] buffer = new byte[fis.available()];
        fis.read(buffer);
        fis.close();
        response.reset();
        response.setContentType("application/octet-stream");
        response.addHeader("Content-Disposition", "attachment;filename="
                + URLEncoder.encode(filename, response.getOldCharset()));
        response.setContentLength(Integer.valueOf(String.valueOf(file.length())));
        OutputStream toClient = new BufferedOutputStream(
                response.getOutputStream());
        toClient.write(buffer);
        toClient.flush();
        toClient.close();
        boolean isdelete = Boolean.valueOf(request.getParameter("isdelete"));
        log.debug("download file success:" + filename);
        if (isdelete) {
            FileTools.doDelete(file, true);
        }
        return response;
    }
}
