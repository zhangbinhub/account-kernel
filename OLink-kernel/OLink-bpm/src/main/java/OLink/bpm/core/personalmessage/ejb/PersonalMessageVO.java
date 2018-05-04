package OLink.bpm.core.personalmessage.ejb;

import java.io.Serializable;
import java.util.Date;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.pushlet.PublishAble;
import OLink.bpm.util.StringUtil;
import nl.justobjects.pushlet.core.Dispatcher;
import nl.justobjects.pushlet.core.Event;

/**
 * @hibernate.class table="T_MESSAGE" batch-size="10" lazy="true"
 */
public class PersonalMessageVO extends ValueObject implements Serializable, PublishAble {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8549419681060378811L;

	private MessageBody body;

	/**
	 * 接收者标识
	 */
	private String receiverId;

	private String senderId;

	/**
	 * 是否已读
	 */
	private boolean read;

	/**
	 * 回收箱
	 */
	private boolean trash;

	private boolean inbox;
	private boolean outbox;

	/** 信息所有者 */
	private String ownerId;

	private Date sendDate;

	/**
	 * 获取接收者标识
	 * 
	 * @return 接收者标识
	 */
	public String getReceiverId() {
		return receiverId;
	}

	/**
	 * 设置接收者标识
	 * 
	 * @param receiverId
	 *            接收者标识
	 */
	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}

	/**
	 * 获取是否已读
	 * 
	 * @return 是否已读
	 */
	public boolean isRead() {
		return read;
	}

	/**
	 * 设置是否已读
	 * 
	 * @param read
	 *            是否已读
	 */
	public void setRead(boolean read) {
		this.read = read;
	}

	/**
	 * 获取是否是回收箱
	 * 
	 * @return 回收箱
	 */
	public boolean isTrash() {
		return trash;
	}

	/**
	 * 设置是否是回收箱
	 * 
	 * @param trash
	 *            回收箱
	 */
	public void setTrash(boolean trash) {
		this.trash = trash;
	}

	public MessageBody getBody() {
		return body;
	}

	public void setBody(MessageBody body) {
		this.body = body;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public Date getSendDate() {
		return sendDate;
	}

	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

	public boolean isInbox() {
		return inbox;
	}

	public void setInbox(boolean inbox) {
		this.inbox = inbox;
	}

	public boolean isOutbox() {
		return outbox;
	}

	public void setOutbox(boolean outbox) {
		this.outbox = outbox;
	}

	/**
	 * 发布给指定订阅用户
	 */
	public void publish() {
		// 只提示收件箱
		if (inbox) {
			Event anEvent = Event.createDataEvent(SUBJECT_TYPE_PERSONALMESSAGE);
			if (body != null) {
				anEvent.setField("p_title", body.getTitle());
				anEvent.setField("p_content", body.getContent());
			}
			if (!StringUtil.isBlank(getReceiverId())) {
				// 广播
				anEvent.setField("p_user", getReceiverId()); // 指定广播接收人
				Dispatcher.getInstance().multicast(anEvent);
			}
		}
	}
}