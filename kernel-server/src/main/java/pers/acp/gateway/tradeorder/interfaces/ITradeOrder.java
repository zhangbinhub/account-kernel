package pers.acp.gateway.tradeorder.interfaces;

import pers.acp.tools.dbconnection.ConnectionFactory;

public interface ITradeOrder {

    /**
     * 创建订单之前执行的函数
     *
     * @return 成功或失败
     */
    boolean beforeCreateOrder();

    /**
     * 创建订单之前执行的函数
     *
     * @param dbcon 数据库链接实例
     * @return 成功或失败
     */
    boolean beforeCreateOrder(ConnectionFactory dbcon);

    /**
     * 创建订单
     *
     * @return 成功或失败
     */
    boolean doCreateOrder();

    /**
     * 创建订单
     *
     * @param dbcon 数据库链接实例
     * @return 成功或失败
     */
    boolean doCreateOrder(ConnectionFactory dbcon);

    /**
     * 创建订单之后执行的函数
     */
    void afterCreateOrder();

    /**
     * 创建订单之后执行的函数
     *
     * @param dbcon 数据库链接实例
     */
    void afterCreateOrder(ConnectionFactory dbcon);

    /**
     * 更新订单之前执行的函数
     *
     * @return 成功或失败
     */
    boolean beforeUpdateOrder();

    /**
     * 更新订单之前执行的函数
     *
     * @param dbcon 数据库链接实例
     * @return 成功或失败
     */
    boolean beforeUpdateOrder(ConnectionFactory dbcon);

    /**
     * 更新订单
     *
     * @return 成功或失败
     */
    boolean doUpdateOrder();

    /**
     * 更新订单
     *
     * @param dbcon 数据库链接实例
     * @return 成功或失败
     */
    boolean doUpdateOrder(ConnectionFactory dbcon);

    /**
     * 更新订单之后执行的函数
     */
    void afterUpdateOrder();

    /**
     * 更新订单之后执行的函数
     *
     * @param dbcon 数据库链接实例
     */
    void afterUpdateOrder(ConnectionFactory dbcon);

    /**
     * 删除订单之前执行的函数
     *
     * @return 成功或失败
     */
    boolean beforeDeleteOrder();

    /**
     * 删除订单之前执行的函数
     *
     * @param dbcon 数据库链接实例
     * @return 成功或失败
     */
    boolean beforeDeleteOrder(ConnectionFactory dbcon);

    /**
     * 删除订单
     *
     * @return 成功或失败
     */
    boolean doDeleteOrder();

    /**
     * 删除订单
     *
     * @param dbcon 数据库链接实例
     * @return 成功或失败
     */
    boolean doDeleteOrder(ConnectionFactory dbcon);

    /**
     * 删除订单之后执行的函数
     */
    void afterDeleteOrder();

    /**
     * 删除订单之后执行的函数
     *
     * @param dbcon 数据库链接实例
     */
    void afterDeleteOrder(ConnectionFactory dbcon);

}
