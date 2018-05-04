package pers.acp.communications.server.socket.base;

import pers.acp.communications.server.base.BaseCommunication;
import pers.acp.communications.server.socket.config.ListenConfig;
import pers.acp.tools.exceptions.ConfigException;

public abstract class BaseSocketHandle extends BaseCommunication {

    /**
     * socket监听服务配置类
     */
    private ListenConfig listenConfig = null;

    /**
     * 构造函数
     *
     * @param listenConfig 监听配置信息对象
     */
    public BaseSocketHandle(ListenConfig listenConfig) throws ConfigException {
        super();
        this.listenConfig = listenConfig;
    }

    /**
     * 对接收到的报文进行处理
     *
     * @param recvStr 接收到的报文
     * @return 返回报文
     */
    public abstract String doResponse(String recvStr);

    /**
     * 获取监听服务配置信息类
     *
     * @return 监听配置对象
     */
    public ListenConfig getListenConfig() {
        return listenConfig;
    }

}
