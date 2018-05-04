package pers.acp.communications.client.socket.tcp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import org.apache.log4j.Logger;

import pers.acp.tools.common.CommonTools;

public final class TcpServerClient {

    private static int MAX_TIMEOUT = 3600000;

    private Logger log = Logger.getLogger(this.getClass());

    private String serverIp = "";

    private int port;

    private int timeout;

    private String charset = null;

    private boolean isHex = false;

    /**
     * 创建socket发送客户端
     *
     * @param serverIp 发送IP
     * @param port     发送端口
     * @param timeout  接收超时时间
     */
    public TcpServerClient(String serverIp, int port, int timeout) {
        this.serverIp = serverIp;
        this.port = port;
        if (timeout < MAX_TIMEOUT) {
            this.timeout = timeout;
        } else {
            this.timeout = MAX_TIMEOUT;
        }
    }

    /**
     * 发送报文
     *
     * @param mess     报文字符串
     * @param needRead 是否需要接收返回信息
     * @return 响应报文
     */
    public String doSend(final String mess, boolean needRead) {
        InputStream in = null;
        OutputStream out = null;
        BufferedReader br = null;
        BufferedWriter bw = null;
        Socket socket = null;
        try {
            socket = new Socket(serverIp, port);
            socket.setSoTimeout(timeout);
            log.debug("connect tcp server[" + serverIp + ":port] timeout:" + timeout);
            if (isHex) {
                in = socket.getInputStream();
                out = socket.getOutputStream();
                byte[] messhex = CommonTools.hex2byte(mess);
                out.write(messhex);
                out.flush();
            } else {
                if (CommonTools.isNullStr(charset)) {
                    charset = CommonTools.getDefaultCharset();
                }
                br = new BufferedReader(new InputStreamReader(
                        socket.getInputStream(), charset));
                bw = new BufferedWriter(new OutputStreamWriter(
                        socket.getOutputStream(), charset));
                bw.write(mess);
                bw.flush();
            }
            log.debug("tcp send:" + mess);
            if (needRead) {
                String recvStr = "";
                if (isHex) {
                    byte[] revB = new byte[2048];
                    int size = in.read(revB);
                    for (int i = 0; i < size; i++) {
                        byte[] tmp = {revB[i]};
                        recvStr += CommonTools.byte2hex(tmp);
                    }
                } else {
                    char[] revC = new char[2048];
                    int size = br.read(revC);
                    for (int i = 0; i < size; i++) {
                        recvStr += revC[i];
                    }
                }
                log.debug("tcp receive:" + recvStr);
                return recvStr;
            } else {
                return "";
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "";
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                if (br != null) {
                    br.close();
                }
                if (bw != null) {
                    bw.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public String getCharset() {
        return charset;
    }

    /**
     * 默认使用系统字符集
     *
     * @param charset 字符集
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }

    /**
     * 是否十六进制
     *
     * @return 是否十六进制
     */
    public boolean isHex() {
        return isHex;
    }

    /**
     * 是否以十六进制进行通讯，默认false
     *
     * @param isHex 是否十六进制
     */
    public void setHex(boolean isHex) {
        this.isHex = isHex;
    }
}
