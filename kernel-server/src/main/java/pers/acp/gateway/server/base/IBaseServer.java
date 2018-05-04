package pers.acp.gateway.server.base;

import java.util.Map;

import pers.acp.gateway.tradeorder.interfaces.ITradeOrder;
import pers.acp.gateway.server.GateWayServerConfig;

public interface IBaseServer {

    /**
     * 处理网关请求
     *
     * @param tradeOrder   订单对象
     * @param serverConfig 网关服务配置类
     * @return 返回数据集，键值自动强制小写
     */
    Map<String, String> doServer(ITradeOrder tradeOrder, GateWayServerConfig.Server serverConfig);

    /**
     * 获取交易密钥
     *
     * @param tradeOrder 订单对象
     * @return 交易密钥
     */
    String getTradeKey(ITradeOrder tradeOrder);

}
