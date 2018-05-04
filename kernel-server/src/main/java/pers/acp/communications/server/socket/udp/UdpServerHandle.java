package pers.acp.communications.server.socket.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.apache.log4j.Logger;

import pers.acp.communications.server.socket.base.BaseSocketHandle;
import pers.acp.communications.server.socket.config.ListenConfig;
import pers.acp.tools.common.CommonTools;

/**
 * 报文处理类
 *
 * @author zhang
 */
public final class UdpServerHandle implements Runnable {

    private Logger log = Logger.getLogger(this.getClass());

    private DatagramSocket sSocket;

    private DatagramPacket packet;

    private ListenConfig listenConfig;

    private BaseSocketHandle socketResponse;

    public UdpServerHandle(DatagramSocket sSocket, DatagramPacket packet, ListenConfig listenConfig, BaseSocketHandle socketResponse) {
        super();
        this.sSocket = sSocket;
        this.packet = packet;
        this.listenConfig = listenConfig;
        this.socketResponse = socketResponse;
    }

    public void run() {
        try {
            /* 接收数据 start */
            String recvStr = "";
            if (listenConfig.isHex()) {
                byte[] revB = packet.getData();
                for (int i = 0; i < packet.getLength(); i++) {
                    byte[] tmp = {revB[i]};
                    recvStr += CommonTools.byte2hex(tmp);
                }
            } else {
                String defaultCharset = CommonTools.getDefaultCharset();
                String charset = listenConfig.getCharset();
                if (!CommonTools.isNullStr(charset)) {
                    recvStr += new String(packet.getData(), 0, packet.getLength(), charset);
                } else if (!CommonTools.isNullStr(defaultCharset)) {
                    recvStr += new String(packet.getData(), 0, packet.getLength(), defaultCharset);
                } else {
                    recvStr += new String(packet.getData(), 0, packet.getLength());
                }
            }
            log.debug("udp receive:" + recvStr);
            /* 接收数据 end */
            String responseStr = this.socketResponse.doResponse(recvStr);
            log.debug("udp return:" + responseStr);
            /* 返回数据 start */
            if (listenConfig.isHex()) {
                byte[] returnBytes = CommonTools.hex2byte(responseStr);
                DatagramPacket rePacket = new DatagramPacket(returnBytes, returnBytes.length, packet.getAddress(), packet.getPort());
                rePacket.setData(returnBytes);
                sSocket.send(rePacket);
            } else {
                byte[] returnBytes;
                String defaultCharset = CommonTools.getDefaultCharset();
                String charset = listenConfig.getCharset();
                if (!CommonTools.isNullStr(charset)) {
                    returnBytes = responseStr.getBytes(charset);
                } else if (!CommonTools.isNullStr(defaultCharset)) {
                    returnBytes = responseStr.getBytes(defaultCharset);
                } else {
                    returnBytes = responseStr.getBytes();
                }
                DatagramPacket rePacket = new DatagramPacket(returnBytes, returnBytes.length, packet.getAddress(), packet.getPort());
                rePacket.setData(returnBytes);
                sSocket.send(rePacket);
            }
            /* 返回数据 end */
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
