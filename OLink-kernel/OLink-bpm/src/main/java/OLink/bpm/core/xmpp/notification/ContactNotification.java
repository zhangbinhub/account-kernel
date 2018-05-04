package OLink.bpm.core.xmpp.notification;

import java.lang.reflect.InvocationTargetException;

import OLink.bpm.core.xmpp.XMPPNotification;
import OLink.bpm.util.ObjectUtil;

/**
 * IQ格式： <notification xmlns="obpm:iq:notification"> <contact
 * xmlns="obpm:iq:notification:contact"> <action>update</action> </contact>
 * </notification>
 * 
 * @author keezzm
 * 
 * @last modified by keezzm
 * 
 */
public class ContactNotification extends XMPPNotification {

	public final static String ACTION_CREATE = "create";
	public final static String ACTION_UPDATE = "update";
	public final static String ACTION_REMOVE = "remove";

	private String action;

	public ContactNotification() {
	}

	public ContactNotification(String action) {
		this.action = action;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	@Override
	public Object clone() {
		ContactNotification clone = new ContactNotification(this.action);
		try {
			ObjectUtil.copyProperties(clone, this);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return clone;
	}

	@Override
	public String getInnerXML() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<contact xmlns=\"obpm:iq:notification:contact\">");
		buffer.append("<action>" + this.action + "</action>");
		buffer.append("</contact>");

		return buffer.toString();
	}

	public static ContactNotification newInstance() {
		return new ContactNotification();
	}

	public static ContactNotification newInstance(String action) {
		return new ContactNotification(action);
	}
}
