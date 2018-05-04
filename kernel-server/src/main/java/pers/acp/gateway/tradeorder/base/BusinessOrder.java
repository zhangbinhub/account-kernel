package pers.acp.gateway.tradeorder.base;

import pers.acp.gateway.tradeorder.interfaces.ITradeOrder;
import pers.acp.gateway.tradeorder.interfaces.ITradeOrderNumberFactory;
import pers.acp.tools.dbconnection.ConnectionFactory;
import pers.acp.tools.dbconnection.annotation.ADBTable;
import pers.acp.tools.dbconnection.annotation.ADBTablePrimaryKey;
import pers.acp.tools.dbconnection.entity.DBTable;
import pers.acp.tools.dbconnection.entity.DBTablePrimaryKeyType;

import java.util.Map;

/**
 * 业务订单（数据库操作）
 *
 * @author zhang
 */
@ADBTable(isVirtual = true)
public abstract class BusinessOrder extends DBTable implements ITradeOrder,
        ITradeOrderNumberFactory {

    @ADBTablePrimaryKey(name = "orderNo", pKeyType = DBTablePrimaryKeyType.String)
    private String orderNo;

    /**
     * 获取记录实例(获取满足条件的实例)
     *
     * @param pKey 主键值
     * @param cls  类
     * @param obj  实例对象
     * @return 实例
     */
    public static DBTable doView(Object pKey, Class<? extends DBTable> cls, DBTable obj) {
        return DBTable.getInstance(pKey, cls, obj);
    }

    /**
     * 获取记录实例(获取满足条件的实例)
     *
     * @param whereValues 查询条件
     * @param cls         类
     * @param obj         实例对象
     * @return 实例
     */
    public static DBTable doView(Map<String, Object> whereValues, Class<? extends DBTable> cls, DBTable obj) {
        return DBTable.getInstance(whereValues, cls, obj, new ConnectionFactory());
    }

    /**
     * 获取记录实例(获取满足条件的实例)
     *
     * @param pKey  主键值
     * @param cls   类
     * @param obj   实例对象
     * @param dbcon 数据库连接实例
     * @return 实例
     */
    public static DBTable doView(Object pKey, Class<? extends DBTable> cls, DBTable obj, ConnectionFactory dbcon) {
        return DBTable.getInstance(pKey, cls, obj, dbcon);
    }

    /**
     * 获取记录实例(获取满足条件的实例)
     *
     * @param whereValues 查询条件
     * @param cls         类
     * @param obj         实例对象
     * @param dbcon       数据库连接实例
     * @return 实例
     */
    public static DBTable doView(Map<String, Object> whereValues, Class<? extends DBTable> cls, DBTable obj, ConnectionFactory dbcon) {
        return DBTable.getInstance(whereValues, cls, obj, dbcon);
    }

    /**
     * 获取记录实例(获取满足条件的实例)（查询后进行加锁）
     *
     * @param pKey  主键值
     * @param cls   类
     * @param obj   实例对象
     * @param dbcon 数据库连接实例
     * @return 实例
     */
    public static DBTable doViewByLock(Object pKey, Class<? extends DBTable> cls, DBTable obj, ConnectionFactory dbcon) {
        return DBTable.getInstanceByLock(pKey, cls, obj, dbcon);
    }

    /**
     * 获取记录实例(获取满足条件的实例)（查询后进行加锁）
     *
     * @param whereValues 查询条件
     * @param cls         类
     * @param obj         实例对象
     * @param dbcon       数据库连接实例
     * @return 实例
     */
    public static DBTable doViewByLock(Map<String, Object> whereValues, Class<? extends DBTable> cls, DBTable obj, ConnectionFactory dbcon) {
        return DBTable.getInstanceByLock(whereValues, cls, obj, dbcon);
    }

    @Override
    public boolean doCreateOrder() {
        if (this.beforeCreateOrder()) {
            this.orderNo = this.generateOrderNo();
            boolean result = super.doCreate();
            this.afterCreateOrder();
            return result;
        } else {
            return false;
        }
    }

    @Override
    public boolean doCreateOrder(ConnectionFactory dbcon) {
        if (this.beforeCreateOrder(dbcon)) {
            this.orderNo = this.generateOrderNo(dbcon);
            boolean result = super.doCreate(dbcon);
            this.afterCreateOrder(dbcon);
            return result;
        } else {
            return false;
        }
    }

    @Override
    public boolean doUpdateOrder() {
        if (this.beforeUpdateOrder()) {
            boolean result = super.doUpdate();
            this.afterUpdateOrder();
            return result;
        } else {
            return false;
        }
    }

    @Override
    public boolean doUpdateOrder(ConnectionFactory dbcon) {
        if (this.beforeUpdateOrder(dbcon)) {
            boolean result = super.doUpdate(dbcon);
            this.afterUpdateOrder(dbcon);
            return result;
        } else {
            return false;
        }
    }

    @Override
    public boolean doDeleteOrder() {
        if (this.beforeDeleteOrder()) {
            boolean result = super.doDelete();
            this.afterDeleteOrder();
            return result;
        } else {
            return false;
        }
    }

    @Override
    public boolean doDeleteOrder(ConnectionFactory dbcon) {
        if (this.beforeDeleteOrder(dbcon)) {
            boolean result = super.doDelete(dbcon);
            this.afterDeleteOrder(dbcon);
            return result;
        } else {
            return false;
        }
    }

    /**
     * 获取订单号
     *
     * @return 订单号
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * 设置订单号
     *
     * @param orderNo 订单号
     */
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

}
