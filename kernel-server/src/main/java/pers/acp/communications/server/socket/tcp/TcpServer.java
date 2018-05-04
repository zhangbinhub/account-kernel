package pers.acp.communications.server.socket.tcp;

import pers.acp.communications.server.ctrl.CommunicationCtrl;
import pers.acp.communications.server.socket.base.BaseSocketHandle;
import pers.acp.communications.server.socket.config.ListenConfig;
import org.apache.log4j.Logger;

import java.net.ServerSocket;
import java.net.Socket;

public final class TcpServer implements Runnable {

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
    public TcpServer(int port, ListenConfig listenConfig) {
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
    public TcpServer(int port, ListenConfig listenConfig, BaseSocketHandle socketResponse) {
        this(port, listenConfig);
        this.socketResponse = socketResponse;
    }

    /**
     * 设置接收报文处理对象
     *
     * @param socketResponse 响应对象
     */
    public void setSocketResponse(BaseSocketHandle socketResponse) {
        this.socketResponse = socketResponse;
    }

    @Override
    public void run() {
        while (true) {
            if (this.socketResponse != null) {
                Socket clientSocket;
                ServerSocket sSocket = null;
                try {
                    if (CommunicationCtrl.isEnabled()) {
                        sSocket = new ServerSocket(port);
                        while (CommunicationCtrl.isEnabled()) {
                            clientSocket = sSocket.accept();
                            TcpServerHandle handle = new TcpServerHandle(clientSocket, listenConfig, this.socketResponse);
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
                        log.error("tcp server:" + e.getMessage(), e);
                    }
                }
            } else {
                log.error("tcp listen service is stop,case by:response object is null[BaseSocketHandle]");
                break;
            }
        }
    }

    public int getPort() {
        return port;
    }
}
