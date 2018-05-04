package pers.acp.communications.server.http.servlet.handle;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import pers.acp.communications.tools.CommunicationTools;
import org.apache.log4j.Logger;

public class HttpServletResponseAcp extends HttpServletResponseWrapper {

    private Logger log = Logger.getLogger(this.getClass());// 日志对象

    private String oldCharset = null;

    public HttpServletResponseAcp(String oldCharset, HttpServletResponse response) {
        super(response);
        this.oldCharset = oldCharset;
    }

    /**
     * 返回请求信息，自动转换为客户端指定字符编码
     *
     * @param returnMessage 返回信息
     */
    public void doReturn(String returnMessage) {
        PrintWriter writer = null;
        try {
            String returnStr = new String(returnMessage.getBytes(this.getCharacterEncoding()), this.oldCharset);
            this.setContentType("text/html;charset=" + this.oldCharset);
            this.setCharacterEncoding(this.oldCharset);
            writer = this.getWriter();
            writer.write(returnStr);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    /**
     * 返回错误信息（json格式：{"errmsg":""}），自动转换为客户端指定字符编码
     *
     * @param errorMessage 错误信息
     */
    public void doReturnError(String errorMessage) {
        PrintWriter writer = null;
        try {
            String returnStr = new String(CommunicationTools.buildJSONErrorStr(errorMessage).getBytes(this.getCharacterEncoding()), this.oldCharset);
            this.setCharacterEncoding(this.oldCharset);
            this.setContentType("text/html;charset=" + this.oldCharset);
            writer = this.getWriter();
            writer.write(returnStr);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    /**
     * 获取客户端请求时指定的字符集
     *
     * @return 字符集
     */
    public String getOldCharset() {
        return this.oldCharset;
    }
}
