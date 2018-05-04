package pers.acp.communications.client.socket.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.apache.log4j.Logger;

import pers.acp.tools.common.CommonTools;

public final class UdpServerClient {

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
    public UdpServerClient(String serverIp, int port, int timeout) {
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
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(timeout);
            byte[] messhex;
            if (isHex) {
                messhex = CommonTools.hex2byte(mess);
                DatagramPacket packet = new DatagramPacket(messhex,
                        messhex.length, InetAddress.getByName(serverIp), port);
                socket.send(packet);
            } else {
                if (CommonTools.isNullStr(charset)) {
                    charset = CommonTools.getDefaultCharset();
                }
                messhex = mess.getBytes(charset);
            }
            DatagramPacket packet = new DatagramPacket(messhex, messhex.length,
                    InetAddress.getByName(serverIp), port);
            socket.send(packet);
            log.debug("udp send:" + mess);
            if (needRead) {
                byte[] revB = new byte[1024];
                DatagramPacket rePacket = new DatagramPacket(revB, revB.length);
                socket.receive(rePacket);
                String recvStr = "";
                if (isHex) {
                    for (int i = 0; i < rePacket.getLength(); i++) {
                        byte[] tmp = {revB[i]};
                        recvStr += CommonTools.byte2hex(tmp);
                    }
                } else {
                    recvStr += new String(rePacket.getData(), 0,
                            rePacket.getLength(), charset);
                }
                log.debug("udp receive:" + recvStr);
                return recvStr;
            } else {
                return "";
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "";
        } finally {
            if (socket != null) {
                socket.close();
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
