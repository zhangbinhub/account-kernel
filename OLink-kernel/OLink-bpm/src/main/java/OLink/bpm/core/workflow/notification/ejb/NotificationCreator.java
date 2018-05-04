package OLink.bpm.core.workflow.notification.ejb;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.core.user.ejb.BaseUser;
import org.apache.log4j.Logger;

import OLink.bpm.core.workflow.element.FlowDiagram;
import OLink.bpm.core.workflow.element.ManualNode;
import OLink.bpm.core.workflow.element.Node;
import OLink.bpm.util.ObjectUtil;

public class NotificationCreator {
	private final static Logger LOG = Logger
			.getLogger(NotificationCreator.class);

	private Map<String, Object> notificationStrategyMap = new HashMap<String, Object>();

	public NotificationCreator(Node node) {
		if (node != null && node instanceof ManualNode) {
			notificationStrategyMap = ((ManualNode) node)
					.getNotificationStrategyMap();
		}
	}

	/**
	 * 创建发送通知
	 * @SuppressWarnings notificationStrategyMapde value值不确定
	 * @param applicationid
	 *            应用标识
	 * @return 发送对象
	 * @throws NotificationException
	 */
	@SuppressWarnings("unchecked")
	public Notification createSendNotification(BaseUser submitter,
			String applicationid) throws NotificationException {
		Map<Object, Object> notificationStrategy = (Map<Object, Object>) notificationStrategyMap
				.get(NotificationConstant.STRTAGERY_SEND);
		if (notificationStrategy == null) {
			LOG.debug("No send notification strategy defined");
			return new NullNotification();
		}

		try {
			SendNotification notification = new SendNotification(applicationid);
			ObjectUtil.copyProperties(notification, notificationStrategy);

			notification.setSubmitter(submitter);
			notification.setSubject("已送出通知");
			notification.setApplicationid(applicationid);

			return notification;
		} catch (IllegalAccessException e) {
			throw new NotificationException(e);
		} catch (InvocationTargetException e) {
			throw new NotificationException(e);
		}
	}

	/**
	 * 创建到达提醒
	 * @SuppressWarnings notificationStrategyMapde value值不确定
	 * @param applicationid
	 *            应用标识
	 * @return 发送对象
	 * @throws NotificationException
	 */
	@SuppressWarnings("unchecked")
	public Notification createArriveNotification(String applicationid)
			throws NotificationException {
		Map<Object, Object> notificationStrategy = (Map<Object, Object>) notificationStrategyMap
				.get(NotificationConstant.STRTAGERY_ARRIVE);
		if (notificationStrategy == null) {
			LOG.debug("No arrive notification strategy defined");
			return new NullNotification();
		}

		try {
			Notification notification = new Notification(applicationid);
			ObjectUtil.copyProperties(notification, notificationStrategy);
			notification.setSubject("待办通知");
			notification.setApplicationid(applicationid);

			return notification;
		} catch (IllegalAccessException e) {
			throw new NotificationException(e);
		} catch (InvocationTargetException e) {
			throw new NotificationException(e);
		}
	}

	/**
	 * 创建超期提醒
	 * @SuppressWarnings notificationStrategyMapde value值不确定
	 * @param curDate
	 *            创建日期
	 * @param deadline
	 *            过期日期
	 * @param applicationid
	 *            应用标识
	 * @return 发送对象
	 * @throws NotificationException
	 */
	@SuppressWarnings("unchecked")
	public Notification createOverDueNotification(Date curDate, Date deadline,
			String applicationid) throws NotificationException {
		Map<Object, Object> notificationStrategy = (Map<Object, Object>) notificationStrategyMap
				.get(NotificationConstant.STRTAGERY_OVERDUE);
		if (notificationStrategy == null) {
			LOG.debug("No overdue notification strategy defined");
			return new NullNotification();
		}

		try {
			OverDueNotification notification = new OverDueNotification(
					applicationid);
			notification.setSubject("待办超期通知");
			// notification.setSubject("Pending has been expired");
			ObjectUtil.copyProperties(notification, notificationStrategy);

			// 设置超限属性
			notification.setCurDate(curDate);
			notification.setDeadline(deadline);
			notification.setApplicationid(applicationid);
			return notification;
		} catch (IllegalAccessException e) {
			throw new NotificationException(e);
		} catch (InvocationTargetException e) {
			throw new NotificationException(e);
		}
	}

	/**
	 * 创建回退提醒
	 * @SuppressWarnings notificationStrategyMapde value值不确定
	 * @param applicationid
	 *            应用标识
	 * @return 发送对象
	 * @throws NotificationException
	 */
	@SuppressWarnings("unchecked")
	public Notification createRejectNotification(UserVO author,
			String applicationid) throws NotificationException {
		Map<Object, Object> notificationStrategy = (Map<Object, Object>) notificationStrategyMap
				.get(NotificationConstant.STRTAGERY_REJECT);
		if (notificationStrategy == null) {
			LOG.debug("No reject notification strategy defined");
			return new NullNotification();
		}

		try {
			RejectNotification notification = new RejectNotification(
					applicationid);
			notification.setSubject("回退通知");
			// notification.setSubject("Submission has been rejected");
			notification.setApplicationid(applicationid);
			ObjectUtil.copyProperties(notification, notificationStrategy);
			return notification;
		} catch (IllegalAccessException e) {
			throw new NotificationException(e);
		} catch (InvocationTargetException e) {
			throw new NotificationException(e);
		}
	}

	public static void main(String[] args) throws NotificationException {
		ManualNode node = new ManualNode(new FlowDiagram());
		// Mock Datas
		node.notificationStrategyJSON = "{"
				+ "arrive: {sendModeCodes:[0, 1]}, "
				+ "overdue: {sendModeCodes:[0, 1], limittimecount:12, timeunit:0, isnotifysuperior:true},"
				+ "reject: {sendModeCodes:[0, 1], responsibleType: 1}" + "}";
		// node.notificationStrategyJSON = "";
		// NotificationCreator creator = new NotificationCreator(node);
		// Notification notification =
		// creator.createArriveNotification("app001");
	}
}
