package pers.acp.communications.server.socket.udp;

import pers.acp.communications.server.ctrl.CommunicationCtrl;
import pers.acp.communications.server.socket.base.BaseSocketHandle;
import pers.acp.communications.server.socket.config.ListenConfig;
import org.apache.log4j.Logger;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public final class UdpServer implements Runnable {

    private Logger log = Logger.getLogger(this.getClass());

    private int port;

    private ListenConfig listenConfig = null;

    private BaseSocketHandle socketResponse = null;

    /**
     * 构造函数
     *
     * @param port         端口
     * @param listenConfig 监听服务配置
     */
    public UdpServer(int port, ListenConfig listenConfig) {
        this.port = port;
        this.listenConfig = listenConfig;
    }

    /**
     * 构造函数
     *
     * @param port           端口
     * @param listenConfig   监听服务配置
     * @param socketResponse 接收报文处理对象
     */
    public UdpServer(int port, ListenConfig listenConfig, BaseSocketHandle socketResponse) {
        this(port, listenConfig);
        this.socketResponse = socketResponse;
    }

    /**
     * 设置接收报文处理对象
     *
     * @param socketResponse 接收报文处理对象
     */
    public void setSocketResponse(BaseSocketHandle socketResponse) {
        this.socketResponse = socketResponse;
    }

    @Override
    public void run() {
        while (true) {
            if (this.socketResponse != null) {
                DatagramSocket sSocket = null;
                try {
                    if (CommunicationCtrl.isEnabled()) {
                        sSocket = new DatagramSocket(port);
                        while (CommunicationCtrl.isEnabled()) {
                            byte[] recvBuf = new byte[1024];
                            DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                            sSocket.receive(packet);
                            UdpServerHandle handle = new UdpServerHandle(sSocket, packet, listenConfig, this.socketResponse);
                            new Thread(handle).start();
                        }
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                } finally {
                    try {
                        if (sSocket != null) {
                            sSocket.close();
                        }
                        Thread.sleep(3000);
                    } catch (Exception e) {
                        log.error("udp server:" + e.getMessage(), e);
                    }
                }
            } else {
                log.error("udp listen server is stop,case by:response object is null[BaseUdpHandle]");
                break;
            }
        }
    }

    public int getPort() {
        return port;
    }

}
