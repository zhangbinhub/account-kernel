package pers.acp.gateway.tradeorder.base;

import pers.acp.gateway.tradeorder.interfaces.ITradeOrder;
import pers.acp.gateway.tradeorder.interfaces.ITradeOrderNumberFactory;

/**
 * 逻辑订单（非数据库操作）
 * 
 * @author zhang
 * 
 */
public abstract class LogicOrder implements ITradeOrder,
        ITradeOrderNumberFactory {

	private String orderNo;

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

}
