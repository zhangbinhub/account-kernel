package OLink.bpm.core.dynaform.pending.ejb;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.IRuntimeDAO;
import OLink.bpm.base.ejb.AbstractRunTimeProcessBean;
import OLink.bpm.core.dynaform.pending.dao.PendingDAO;
import OLink.bpm.core.dynaform.summary.ejb.SummaryCfgProcess;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.workflow.storage.runtime.dao.FlowStateRTDAO;
import OLink.bpm.core.xmpp.XMPPNotification;
import OLink.bpm.core.xmpp.XMPPSender;
import OLink.bpm.core.xmpp.notification.PendingNotification;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.util.ProcessFactory;
import org.apache.log4j.Logger;

import OLink.bpm.core.dynaform.summary.ejb.SummaryCfgVO;
import OLink.bpm.util.ObjectUtil;
import OLink.bpm.util.RuntimeDaoManager;

public class PendingProcessBean extends AbstractRunTimeProcessBean<PendingVO> implements PendingProcess {
	public final static Logger LOG = Logger.getLogger(PendingProcessBean.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 7961013990579740564L;
	// private ReminderProcess reminderProcess;

	private SummaryCfgProcess summaryCfgProcess;

	public PendingProcessBean(String applicationId) {
		super(applicationId);
		try {
			// reminderProcess = (ReminderProcess)
			// ProcessFactory.createProcess(ReminderProcess.class);
			summaryCfgProcess = (SummaryCfgProcess) ProcessFactory.createProcess(SummaryCfgProcess.class);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	protected IRuntimeDAO getDAO() throws Exception {
		RuntimeDaoManager runtimeDao = new RuntimeDaoManager();
		PendingDAO pendingDAO = (PendingDAO) runtimeDao.getPendingDAO(getConnection(), getApplicationId());
		pendingDAO.setStateRTDAO((FlowStateRTDAO) runtimeDao.getFlowStateRTDAO(getConnection(), getApplicationId()));
		return pendingDAO;
	}

	/**
	 * Create or remove pending for document
	 */
	public PendingVO doCreateOrRemoveByDocument(Document doc, WebUser user) throws Exception {
		try {

			this.doRemove(doc.getId());
			// createRemovePendingPushletEvent(doc);

			PendingVO pendingVO = new PendingVO();
			ObjectUtil.copyProperties(pendingVO, doc);
			pendingVO.setSummary(getSummay(doc,user));
			this.doCreate(pendingVO);

			// 发送XMPP信息
			sendNotification(pendingVO);

			return pendingVO;
		} catch (Exception e) {
			rollbackTransaction();
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 发送更新信息
	 * 
	 * @param pendingVO
	 */
	private void sendNotification(PendingVO pendingVO) throws Exception {
		try {
			// 发送XMPP信息
			XMPPNotification notification = new PendingNotification(pendingVO, PendingNotification.ACTION_UPDATE);
			XMPPSender.getInstance().processNotification(notification);
		} catch (Exception e) {
			LOG.warn("XMPP Notification Error: " + e.getMessage());
			throw e;
		}
	}

	public void doUpdateByDocument(Document doc,WebUser user) throws Exception {
		PendingVO pendingVO = (PendingVO) getDAO().find(doc.getId());
		if (pendingVO != null) {
			ObjectUtil.copyProperties(pendingVO, doc);
			pendingVO.setSummary(getSummay(doc,user));
			this.doUpdate(pendingVO);
			// 增加发送xmpp消息(桌面程序客户端待办列表更新)
			try {
				// 发送XMPP信息
				sendNotification(pendingVO);
			} catch (Exception e) {
				LOG.warn("XMPP Notification Error", e);
			}
		}
	}

	private String getSummay(Document doc,WebUser user) throws Exception {
		try {
			/*
			 * Reminder reminder = reminderProcess.doViewByForm(doc.getFormid(),
			 * doc.getApplicationid()); return reminder.toSummay(doc);
			 */
			SummaryCfgVO summaryCfg = summaryCfgProcess.doViewByFormIdAndScope(doc.getFormid(),
					SummaryCfgVO.SCOPE_PENDING);
			if (summaryCfg != null)
				return summaryCfg.toSummay(doc,user);
			return "";
		} catch (Exception ex) {
			return "";
		}
	}

	public DataPackage<PendingVO> doQueryByFilter(ParamsTable params, WebUser user) throws Exception {
		PendingDAO pendingDAO = (PendingDAO) getDAO();
		return pendingDAO.queryByFilter(params, user);
	}

	public long conutByFilter(ParamsTable params, WebUser user) throws Exception {
		PendingDAO pendingDAO = (PendingDAO) getDAO();
		return pendingDAO.countByFilter(params, user);
	}
}
