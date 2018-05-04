package pers.acp.communications.server.http.file;

import pers.acp.communications.server.exceptions.ExcuteServletException;
import pers.acp.communications.server.http.servlet.base.BaseServletHandle;
import pers.acp.communications.server.http.servlet.handle.HttpServletRequestAcp;
import pers.acp.communications.server.http.servlet.handle.HttpServletResponseAcp;
import pers.acp.tools.common.CommonTools;
import pers.acp.tools.exceptions.ConfigException;
import pers.acp.tools.file.FileTools;
import net.sf.json.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.List;

public final class UpLoadFile extends BaseServletHandle {

    private Logger log = Logger.getLogger(this.getClass());

    public UpLoadFile(HttpServletRequestAcp request,
                      HttpServletResponseAcp response) throws ConfigException {
        super(request, response);
    }

    public void doUpLoad() {
        try {
            String data = request.getParameter("data");
            JSONObject parame = CommonTools.getJsonObjectFromStr(data);
            String path = parame.getString("path");
            String filename = parame.getString("filename");
            upload(path, filename);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            response.doReturnError(e.getMessage());
        }
    }

    private HttpServletResponseAcp upload(String path, String filename)
            throws ExcuteServletException {
        File saveFile = null;
        try {
            String webrootpath = FileTools.getWebRootAbsPath();
            String savePath = path.replace("\\", File.separator).replace("/", File.separator);
            if (!savePath.contains(webrootpath)) {
                savePath = webrootpath + File.separator + "upload" + savePath;
            }
            File fold = new File(savePath);
            if (!fold.exists()) {
                fold.mkdir();
            }
            saveFile = new File(savePath + File.separator + CommonTools.getDateTimeString(null, "yyyyMMddHHmmssSSS") + filename);
            DiskFileItemFactory fac = new DiskFileItemFactory();
            fac.setRepository(new File(webrootpath));
            fac.setSizeThreshold(1024 * 1024);
            ServletFileUpload upload = new ServletFileUpload(fac);
            upload.setHeaderEncoding(request.getOldCharset());
            List<?> fileList = upload.parseRequest(request);
            for (Object aFileList : fileList) {
                FileItem item = (FileItem) aFileList;
                if (!item.isFormField()) {
                    if (CommonTools.isNullStr(item.getName()) || item.getName().trim().equals("null")) {
                        continue;
                    }
                    item.write(saveFile);
                }
            }
            JSONObject result = new JSONObject();
            result.put("filePathName", saveFile.getAbsolutePath().replace(webrootpath, "").replace(File.separator, "/"));
            response.doReturn(result.toString());
        } catch (Exception e) {
            log.error("upload file Exception:" + e.getMessage(), e);
            if (saveFile != null) {
                saveFile.delete();
            }
            throw new ExcuteServletException("upload file Exception:" + e.getMessage());
        }
        return response;
    }
}
