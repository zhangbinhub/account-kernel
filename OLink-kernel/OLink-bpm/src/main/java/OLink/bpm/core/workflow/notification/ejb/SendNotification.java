package OLink.bpm.core.workflow.notification.ejb;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import OLink.bpm.core.user.ejb.BaseUser;

public class SendNotification extends Notification {
	private int[] receiverTypes;

	private BaseUser submitter;

	/**
	 * 构造方法
	 * 
	 * @param applicationid
	 *            应用标识
	 */
	public SendNotification(String applicationid) {
		this.applicationid = applicationid;
	}

	/**
	 * 获取接收类型
	 * 
	 * @return 接收类型
	 */
	public int[] getReceiverTypes() {
		return receiverTypes;
	}

	/**
	 * 设置接收类型
	 * 
	 * @param receiverTypes
	 *            接收类型
	 */
	public void setReceiverTypes(int[] receiverTypes) {
		this.receiverTypes = receiverTypes;
	}

	/**
	 * 发送信息给用户
	 * 
	 * @param responsible
	 * @throws Exception
	 */
	public void send() throws Exception {
		Map<String, BaseUser> responsibles = new HashMap<String, BaseUser>();

		for (int i = 0; i < receiverTypes.length; i++) {
			switch (receiverTypes[i]) {
			case NotificationConstant.RESPONSIBLE_TYPE_SUBMITTER:
				responsibles.put(submitter.getId(), submitter);
				break;
			case NotificationConstant.RESPONSIBLE_TYPE_AUTHOR:
				if (document != null && document.getAuthor() != null) {
					responsibles.put(document.getAuthor().getId(), document.getAuthor());
				}
				break;
			case NotificationConstant.RESPONSIBLE_TYPE_ALL:
				for (Iterator<BaseUser> iterator = this.responsibles.iterator(); iterator.hasNext();) {
					BaseUser user = iterator.next();
					if(user == null) continue;
					responsibles.put(user.getId(), user);
				}
				break;

			default:
				break;
			}
		}

		setResponsibles(responsibles.values());
		super.send();
	}

	/**
	 * 获取提交者
	 * 
	 * @return 提交者
	 */
	public BaseUser getSubmitter() {
		return submitter;
	}

	/**
	 * 设置提交者
	 * 
	 * @param submitter
	 *            提交者
	 */
	public void setSubmitter(BaseUser submitter) {
		this.submitter = submitter;
	}

}
