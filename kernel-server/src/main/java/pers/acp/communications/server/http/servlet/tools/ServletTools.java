package pers.acp.communications.server.http.servlet.tools;

import pers.acp.communications.server.http.security.Security;
import pers.acp.communications.server.http.servlet.handle.HttpServletRequestAcp;
import pers.acp.tools.common.CommonTools;
import org.apache.log4j.Logger;

import javax.servlet.ServletInputStream;
import java.io.IOException;

public final class ServletTools {

    private static Logger log = Logger.getLogger(ServletTools.class);// 日志对象

    /**
     * 解密前端密文
     *
     * @param encryptedStr {"traitid":"随机的唯一标识符",
     *                     "key":"加密过的AES对称加密密钥"，"encryptedstr":"AES加密后的文件路径密文"}
     * @return 明文
     */
    public static String decryptFromFront(String encryptedStr) {
        return Security.doDecrypt(CommonTools.getJsonObjectFromStr(encryptedStr));
    }

    /**
     * 通过request获取项目webroot路径
     *
     * @return 项目webroot路径
     */
    public static String getWebRootPath(HttpServletRequestAcp request) {
        String webroot = request.getContextPath();
        if (webroot.equals("/")) {
            return "";
        } else {
            return webroot;
        }
    }

    /**
     * 获取客户端发送的内容（xml或json）字符串
     *
     * @param request 请求对象
     * @return 请求内容
     */
    public static String getRequestContent(HttpServletRequestAcp request) {
        ServletInputStream sis = null;
        try {
            sis = request.getInputStream();
            int size = request.getContentLength();
            if (size <= 0) {
                return "";
            }
            byte[] buffer = new byte[size];
            byte[] dataByte = new byte[size];
            int count = 0;
            int rbyte;
            while (count < size) {
                rbyte = sis.read(buffer);
                if (rbyte > 0) {
                    System.arraycopy(buffer, 0, dataByte, count, rbyte);
                    count += rbyte;
                } else {
                    break;
                }
            }
            if (count <= 0) {
                return "";
            }
            return new String(dataByte, request.getOldCharset());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return "";
        } finally {
            if (sis != null) {
                try {
                    sis.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

}
