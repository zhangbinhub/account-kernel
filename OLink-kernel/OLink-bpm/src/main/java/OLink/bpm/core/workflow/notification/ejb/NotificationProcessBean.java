package OLink.bpm.core.workflow.notification.ejb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.core.workflow.storage.runtime.ejb.RelationHISProcessBean;
import OLink.bpm.base.dao.IRuntimeDAO;
import OLink.bpm.base.ejb.AbstractRunTimeProcessBean;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.document.ejb.DocumentProcess;
import OLink.bpm.core.dynaform.pending.ejb.PendingVO;
import OLink.bpm.core.dynaform.summary.ejb.SummaryCfgProcess;
import OLink.bpm.core.dynaform.summary.ejb.SummaryCfgVO;
import OLink.bpm.core.pushlet.PublishAble;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.user.ejb.BaseUser;
import OLink.bpm.core.workflow.element.Node;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiProcess;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiVO;
import OLink.bpm.core.workflow.storage.runtime.proxy.ejb.WorkflowProxyProcessBean;
import nl.justobjects.pushlet.core.Dispatcher;
import nl.justobjects.pushlet.core.Event;
import OLink.bpm.core.workflow.notification.dao.NotificationDAO;
import OLink.bpm.core.workflow.storage.runtime.ejb.ActorRT;
import OLink.bpm.core.workflow.storage.runtime.ejb.ActorRTProcess;
import OLink.bpm.core.workflow.storage.runtime.ejb.ActorRTProcessBean;
import OLink.bpm.core.workflow.storage.runtime.ejb.FlowStateRT;
import OLink.bpm.core.workflow.storage.runtime.ejb.NodeRT;
import OLink.bpm.core.workflow.storage.runtime.ejb.RelationHIS;
import OLink.bpm.core.workflow.storage.runtime.ejb.RelationHISProcess;
import OLink.bpm.core.workflow.storage.runtime.proxy.ejb.WorkflowProxyProcess;
import OLink.bpm.util.CreateProcessException;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.RuntimeDaoManager;

public class NotificationProcessBean extends AbstractRunTimeProcessBean<Notification> implements NotificationProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9137619464810557702L;

	private BillDefiProcess billDefiProcess;

	private ActorRTProcess actorRTProcess;

	private DocumentProcess documentProcess;

//	private ReminderProcess reminderProcess;
	
//	private SummaryCfgProcess summaryCfgProcess;

	private Map<String, BillDefiVO> flowTemp;

	public NotificationProcessBean(String applicationId) {
		super(applicationId);
		try {
			flowTemp = new HashMap<String, BillDefiVO>();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected IRuntimeDAO getDAO() throws Exception {
		return new RuntimeDaoManager().getNotificationDAO(getConnection(), getApplicationId());
	}

	/**
	 * 通知流程送出人
	 * 
	 * @param doc
	 * @param pending
	 * @param flow
	 * @param user
	 * @throws Exception
	 */
	public void notifySender(Document doc, BillDefiVO flow, WebUser user) throws Exception {
		FlowStateRT stateRT = doc.getState();

		if (stateRT != null) {
			Collection<NodeRT> nodeRTs = stateRT.getNoderts();
			WorkflowProxyProcess proxyProcess = new WorkflowProxyProcessBean(flow.getApplicationid());
			for (Iterator<NodeRT> iterator = nodeRTs.iterator(); iterator.hasNext();) {
				NodeRT nodeRT = iterator.next();
				Collection<BaseUser> userList = nodeRT.getUserList();

				if (userList.isEmpty()) {
					continue;
				}
				userList.addAll(proxyProcess.getAgentsByOwners(userList));

				// 创建发送通知
				Node el = flow.toFlowDiagram().getNodeByID(nodeRT.getNodeid());
				NotificationCreator creator = new NotificationCreator(el);
				Notification notification = creator.createSendNotification(user, getApplicationId());

				notification.setResponsibles(userList);
				notification.setDocument(doc);
				notification.send();
			}
		}
	}

	/**
	 * 流程审批后通知当前审批者
	 */
	public void notifyCurrentAuditors(Document doc, PendingVO pending, BillDefiVO flow) throws Exception {
		FlowStateRT stateRT = doc.getState();

		if (stateRT != null && pending != null) {
			Collection<NodeRT> nodeRTs = stateRT.getNoderts();
			WorkflowProxyProcess proxyProcess = new WorkflowProxyProcessBean(doc.getApplicationid());
			for (Iterator<NodeRT> iterator = nodeRTs.iterator(); iterator.hasNext();) {
				NodeRT nodeRT = iterator.next();
				Collection<BaseUser> userList = nodeRT.getUserList();

				if (userList.isEmpty()) {
					continue;
				}
				userList.addAll(proxyProcess.getAgentsByOwners(userList));
				// 创建到达通知
				Node el = flow.toFlowDiagram().getNodeByID(nodeRT.getNodeid());
				NotificationCreator creator = new NotificationCreator(el);
				Notification notification = creator.createArriveNotification(getApplicationId());
				
				notification.setResponsibles(userList);
				notification.setDocument(doc);
				notification.send();
				createPendingPushletEvent(doc, pending, flow, userList);
			}
		}
	}

	/**
	 * 通知被回退者
	 * 
	 * @param doc
	 * @param flow
	 * @throws Exception
	 */
	public void notifyRejectees(Document doc, PendingVO pending, BillDefiVO flow) throws Exception {
		FlowStateRT stateRT = doc.getState();

		if (stateRT != null) {
			Collection<NodeRT> nodeRTs = stateRT.getNoderts();
			WorkflowProxyProcess proxyProcess = new WorkflowProxyProcessBean(doc.getApplicationid());
			for (Iterator<NodeRT> iterator = nodeRTs.iterator(); iterator.hasNext();) {
				NodeRT nodeRT = iterator.next();
				Collection<BaseUser> userList = new ArrayList<BaseUser>();
				for (Iterator<ActorRT> iterator2 = nodeRT.getPendingActorRTList().iterator(); iterator2.hasNext();) {
					ActorRT actorRT = iterator2.next();
					userList.addAll(actorRT.getAllUser());
				}

				if (userList.isEmpty()) {
					continue;
				}
				userList.addAll(proxyProcess.getAgentsByOwners(userList));

				// 创建回退通知
				Node el = flow.toFlowDiagram().getNodeByID(nodeRT.getNodeid());
				NotificationCreator creator = new NotificationCreator(el);

				Notification notification = creator.createRejectNotification((UserVO) doc.getAuthor(),
						getApplicationId());
				notification.setResponsibles(userList);
				notification.setDocument(doc);
				notification.send();
				createPendingPushletEvent(doc, pending, flow, userList);
			}
		}
	}

	public void notifyOverDueAuditors() throws Exception {
		HashSet<String> unique = new HashSet<String>();

		Collection<Notification> notifications = getOverDueNotifications();
		for (Iterator<Notification> iterator = notifications.iterator(); iterator.hasNext();) {
			Notification notification = iterator.next();
			notification.send();

			if (notification.getDocument() != null) {
				// 对于同一个文档和流程，提醒次数只增加一次
				String docId = notification.getDocument().getId();
				String flowId = notification.getDocument().getFlowid();
				String key = docId + flowId;
				if (notification.isSended() && unique.add(key)) {
					addReminderCount(docId, flowId);
				}
			}
		}
	}

	/**
	 * 创建有提醒对象的待办广播消息事件
	 * 
	 * @param doc
	 *            文档
	 * @param pending
	 *            待办
	 * @param flow
	 *            流程
	 * @param users
	 *            广播消息订阅用户
	 */
	private void createPendingPushletEvent(Document doc, PendingVO pending, BillDefiVO flow, Collection<BaseUser> users) {
		try {
//			ReminderProcess rp = (ReminderProcess) ProcessFactory.createProcess(ReminderProcess.class);
//			Reminder reminder = rp.doViewByForm(doc.getFormid(), doc.getApplicationid());
			SummaryCfgProcess summaryCfgProcess = (SummaryCfgProcess) ProcessFactory.createProcess(SummaryCfgProcess.class);
			SummaryCfgVO summary = summaryCfgProcess.doViewByFormIdAndScope(doc.getFormid(), SummaryCfgVO.SCOPE_NOTIFY);
			if (summary != null && users != null) {
				for (Iterator<BaseUser> it = users.iterator(); it.hasNext();) {
					UserVO user = (UserVO) it.next();
					if(user == null) continue;
					Event anEvent = Event.createDataEvent(PublishAble.SUBJECT_TYPE_PENDING);
					anEvent.setField("p_user", user.getId()); // 指定广播接收人
					anEvent.setField("p_appid", doc.getApplicationid());
					anEvent.setField("p_pendingid", pending.getId());
					anEvent.setField("p_formid", doc.getFormid());
					anEvent.setField("p_reminderid", doc.getFormid());
					anEvent.setField("p_content", "(" + summary.getTitle() + ")" + pending.getSummary());
					Dispatcher.getInstance().multicast(anEvent);
				}
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	/**
	 * 在最后一条历史记录中增加提醒次数，以标记当前节点超时提醒次数
	 * 
	 * @throws Exception
	 */
	public void addReminderCount(String docid, String flowid) throws Exception {
		RelationHISProcess relationProcess = new RelationHISProcessBean(getApplicationId());
		RelationHIS relationHIS = relationProcess.doViewLast(docid, flowid);
		relationHIS.setReminderCount(relationHIS.getReminderCount() + 1);
		relationProcess.doUpdate(relationHIS);
	}

	/**
	 * 获取超时的通知
	 * 
	 * @return 超时通知集合
	 * @throws Exception
	 */
	private Collection<Notification> getOverDueNotifications() throws Exception {
		List<Notification> notifications = new ArrayList<Notification>();

		Date curDate = new Date(); // 当前日期

		// 测试用的伪代码
		// Calendar calendar = Calendar.getInstance();
		// calendar.setTime(DateUtil.parseDateTime("2009-05-05 13:00:44"));
		// curDate = calendar.getTime();

		Collection<Map<String, Object>> pendingInfo = ((NotificationDAO) getDAO()).queryOverDuePending(
				new java.sql.Timestamp(curDate.getTime()), getApplicationId());
		for (Iterator<Map<String, Object>> iterator = pendingInfo.iterator(); iterator.hasNext();) {
			Map<String, Object> info = iterator.next();
			Collection<BaseUser> userList = getUserList(info);
			// 创建超期通知
			Notification notification = createOverDueNotification(info, curDate);
			notification.setResponsibles(userList);
			notifications.add(notification);
		}

		return notifications;
	}

	/**
	 * 获取当前审批用户列表
	 * 
	 * @param info
	 *            待办信息
	 * @return 文档
	 * @throws Exception
	 */
	private List<BaseUser> getUserList(Map<String, Object> info) throws Exception {
		String id = (String) info.get("actorrtid");
		ActorRT actorrt = (ActorRT) getActorRTProcess().doView(id);
		if (actorrt != null) {
			return actorrt.getAllUser();
		}

		return new ArrayList<BaseUser>();
	}

	/**
	 * 创建超时通知
	 * 
	 * @param info
	 *            待办信息
	 * @param curDate
	 *            当前日期
	 * @return
	 * @throws Exception
	 */
	private Notification createOverDueNotification(Map<String, Object> info, Date curDate) throws Exception {
		String nodeid = (String) info.get("nodeid");
		String flowid = (String) info.get("flowid");
		String docid = (String) info.get("id");
		Date deadline = (Date) info.get("deadline");

		BillDefiVO flow = flowTemp.get(flowid);
		if (!flowTemp.containsKey(flowid)) {
			flow = (BillDefiVO) getBillDefiProcess().doView(flowid);
			//统一从document里获取，可能有种情况是前台手动调整流程
			DocumentProcess dp = (DocumentProcess) ProcessFactory.createRuntimeProcess(DocumentProcess.class,flow.getApplicationid());
			Document doc = (Document)dp.doView(docid);
			if(doc != null && flow ==null){
				flow = doc.getFlowVO();
			}
		}
		
		return createOverDueNotification(flow, docid, nodeid, curDate, deadline);
	}

	/**
	 * 创建超期通知
	 * 
	 * @param flow
	 *            流程
	 * @param docid
	 *            文档ID
	 * @param nodeid
	 *            节点ID
	 * @param curDate
	 *            当前日期
	 * @param deadline
	 *            最后限期
	 * @return
	 * @throws Exception
	 */
	private Notification createOverDueNotification(BillDefiVO flow, String docid, String nodeid, Date curDate,
			Date deadline) throws Exception {
		DocumentProcess dp = getDocumentProcess();

		if (flow != null) {
			Node el = flow.toFlowDiagram().getNodeByID(nodeid);
			NotificationCreator creator = new NotificationCreator(el);
			Notification notification = creator.createOverDueNotification(curDate, deadline, getApplicationId());
			Document document = (Document) dp.doView(docid); // 性能问题
			notification.setDocument(document);

			return notification;
		}

		return new NullNotification();
	}

	public DocumentProcess getDocumentProcess() {
		if (documentProcess == null) {
			try {
				documentProcess = (DocumentProcess) ProcessFactory.createRuntimeProcess(DocumentProcess.class,getApplicationId());
			} catch (CreateProcessException e) {
				e.printStackTrace();
			}
		}
		return documentProcess;
	}
/*
	public ReminderProcess getReminderProcess() {
		if (reminderProcess == null) {
			reminderProcess = new ReminderProcessBean();
		}
		return reminderProcess;
	}
*/
	public BillDefiProcess getBillDefiProcess() throws Exception {
		if (billDefiProcess == null) {
			billDefiProcess = (BillDefiProcess) ProcessFactory.createProcess(BillDefiProcess.class);
		}
		return billDefiProcess;
	}

	public ActorRTProcess getActorRTProcess() {
		if (actorRTProcess == null) {
			actorRTProcess = new ActorRTProcessBean(getApplicationId());
		}
		return actorRTProcess;
	}
}
