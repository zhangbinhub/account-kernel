package OLink.bpm.core.workflow.notification.ejb;

import OLink.bpm.base.ejb.IRunTimeProcess;
import OLink.bpm.core.dynaform.pending.ejb.PendingVO;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiVO;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.user.action.WebUser;

public interface NotificationProcess extends IRunTimeProcess<Notification> {

	/**
	 * 通知当前审批者
	 * 
	 * @param doc
	 *            文档(Document)对象
	 * @param pending
	 *            待办对象
	 * @param flow
	 *            流程对象
	 * @throws Exception
	 */
	void notifyCurrentAuditors(Document doc, PendingVO pending, BillDefiVO flow) throws Exception;

	/**
	 * 通知超期审批者
	 * 
	 * @throws Exception
	 */
	void notifyOverDueAuditors() throws Exception;

	/**
	 * 通知被回退者
	 * 
	 * @param doc
	 *            文档(Document)对象
	 * @param flow
	 *            流程对象
	 * @throws Exception
	 */
	void notifyRejectees(Document doc, PendingVO pending, BillDefiVO flow) throws Exception;

	/**
	 * 通知流程送出人
	 * 
	 * @param doc
	 *            文档(Document)对象
	 * @param pending
	 *            待办对象
	 * @param flow
	 *            流程对象
	 * @param user
	 *            web用户
	 * @throws Exception
	 */
	void notifySender(Document doc, BillDefiVO flow, WebUser user) throws Exception;

}
