package OLink.bpm.core.xmpp;

import java.util.ArrayList;
import java.util.Collection;

import OLink.bpm.core.domain.ejb.DomainVO;
import OLink.bpm.core.user.ejb.BaseUser;
import org.jivesoftware.smack.packet.IQ;

public abstract class XMPPNotification extends IQ {
	public final static String NODENAME = "notification";

	protected DomainVO domain;
	protected Collection<BaseUser> receivers;
	protected BaseUser sender;


	public BaseUser getSender() {
		return sender;
	}

	public void setSender(BaseUser sender) {
		this.sender = sender;
	}

	public XMPPNotification() {
		this.receivers = new ArrayList<BaseUser>();
	}
	
	public String getChildElementXML() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<notification xmlns=\"obpm:iq:notification\">");
		buffer.append(getInnerXML());
		buffer.append("</notification>");

		return buffer.toString();
	}
	
	/**
	 * 添加接收人
	 * @param userVO
	 */
	public void addReceiver(BaseUser userVO){
		this.receivers.add(userVO);
	}
	
	public Collection<BaseUser> getReceivers() {
		return receivers;
	}

	/**
	 * 获取提醒内容
	 * @return
	 */
	public abstract String getInnerXML();

	@Override
	public abstract Object clone();
}
