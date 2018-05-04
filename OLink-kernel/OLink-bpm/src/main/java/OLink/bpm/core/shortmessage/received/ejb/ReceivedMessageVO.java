package OLink.bpm.core.shortmessage.received.ejb;

import java.io.Serializable;
import java.util.Date;

import OLink.bpm.base.dao.ValueObject;

public class ReceivedMessageVO extends ValueObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String sender;

	private String receiver;

	private String content;

	private String receiveDate;
	
	private String parent;
	
	private String docid;
	
	private int status;
	
	private Date created;

	/**
	 * 接收到回复信息时，创建记录的时间
	 * @return 创建时间
	 */
	public Date getCreated() {
		return created;
	}

	/**
	 * 设置创建时间
	 * @param created 时间，日期型
	 */
	public void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * 回复者电话号码
	 * @return 回复者电话号码
	 */
	public String getSender() {
		return sender;
	}
	/**
	 * 返回发送者电话号码
	 * @return 发送者电话号码
	 */
	public String getReceiver() {
		return receiver;
	}

	/**
	 * 返回回复内容
	 * @return 回复内容
	 */
	public String getContent() {
		return content;
	}

	/**
	 * 接收到回复信息的时间
	 * @return 回复时间
	 */
	public String getReceiveDate() {
		return receiveDate;
	}

	/**
	 * 设置回复者电话号码
	 * @param sender 回复者电话号码
	 */
	public void setSender(String sender) {
		this.sender = sender;
	}

	/**
	 * 设置发送者电话号码
	 * @param receiver 发送者电话号码
	 */
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	/**
	 * 设置回复记录回复内容
	 * @param content 回复记录回复内容
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * 设置回复时间
	 * @param receiveDate 回复时间
	 */
	public void setReceiveDate(String receiveDate) {
		this.receiveDate = receiveDate;
	}

	/**
	 * 返回记录状态,0:未读,1:已读
	 * @return 记录状态
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * 设置记录状态,0:未读,1:已读
	 * @param status 记录状态
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * 返回关联的发送信息记录ID
	 * @return 关联的发送信息记录ID
	 */
	public String getParent() {
		return parent;
	}

	/**
	 * 设置关联的发送信息记录ID
	 * @param parent 关联的发送信息记录ID
	 */
	public void setParent(String parent) {
		this.parent = parent;
	}

	/**
	 * 返回关联的表单数据记录ID
	 * @return 关联的表单数据记录ID
	 */
	public String getDocid() {
		return docid;
	}

	/**
	 * 设置关联的表单数据记录ID
	 * @param docid 关联的表单数据记录ID
	 */
	public void setDocid(String docid) {
		this.docid = docid;
	}
	
}
