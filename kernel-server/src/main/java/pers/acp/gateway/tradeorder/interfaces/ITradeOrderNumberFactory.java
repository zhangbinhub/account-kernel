package pers.acp.gateway.tradeorder.interfaces;

import pers.acp.tools.dbconnection.ConnectionFactory;

public interface ITradeOrderNumberFactory {

    /**
     * 生成订单号
     *
     * @return 订单号
     */
    String generateOrderNo();

    /**
     * 生成订单号
     *
     * @param dbcon 数据库链接实例
     * @return 订单号
     */
    String generateOrderNo(ConnectionFactory dbcon);

}
