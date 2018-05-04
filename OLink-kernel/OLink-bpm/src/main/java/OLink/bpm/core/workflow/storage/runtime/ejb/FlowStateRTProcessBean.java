package OLink.bpm.core.workflow.storage.runtime.ejb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.constans.Environment;
import OLink.bpm.core.dynaform.document.ejb.DocumentProcess;
import OLink.bpm.core.dynaform.form.ejb.ValidateMessage;
import OLink.bpm.core.dynaform.pending.ejb.PendingProcess;
import OLink.bpm.core.dynaform.pending.ejb.PendingVO;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.macro.runner.JavaScriptFactory;
import OLink.bpm.core.workflow.FlowType;
import OLink.bpm.core.workflow.element.Node;
import OLink.bpm.core.workflow.engine.StateMachine;
import OLink.bpm.core.workflow.notification.ejb.NotificationProcess;
import OLink.bpm.core.workflow.notification.ejb.NotificationProcessBean;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiVO;
import OLink.bpm.core.workflow.storage.runtime.dao.FlowStateRTDAO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.RuntimeDaoManager;
import OLink.bpm.core.workflow.element.SubFlow;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiProcess;
import OLink.bpm.base.dao.IRuntimeDAO;
import OLink.bpm.util.StringUtil;
import org.jfree.util.Log;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.ejb.AbstractRunTimeProcessBean;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.pending.ejb.PendingProcessBean;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.workflow.WorkflowException;
import OLink.bpm.core.workflow.element.FlowDiagram;
import OLink.bpm.core.workflow.engine.AutoAuditJobManager;
import eWAP.core.Tools;

public class FlowStateRTProcessBean extends AbstractRunTimeProcessBean<FlowStateRT> implements FlowStateRTProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1242577083144357588L;

	public FlowStateRTProcessBean(String applicationId) {
		super(applicationId);
	}

	protected IRuntimeDAO getDAO() throws Exception {
		return new RuntimeDaoManager().getFlowStateRTDAO(getConnection(), getApplicationId());
	}

	/**
	 * 根据文档Id获取相关的流程实例集合
	 * 
	 * @param docId
	 * @return
	 * @throws Exception
	 */
	public Collection<FlowStateRT> getFlowStateRTsByDocId(String docId) throws Exception {

		return ((FlowStateRTDAO) getDAO()).getFlowStateRTsByDocId(docId);
	}

	public void doCreate(ValueObject vo) throws Exception {
		FlowStateRT state = (FlowStateRT) vo;
		state.setTemp(false);
		super.doCreate(state);
	}

	/**
	 * 根据文档ID删除关联的流程实例
	 * 
	 * @param docId
	 * @throws Exception
	 */
	public void doRemoveByDocId(String docId) throws Exception {
		((FlowStateRTDAO) getDAO()).doRemoveByDocId(docId);
	}

	/**
	 * 获取当前文档流程状态
	 * 
	 * @param docid
	 *            Document id
	 * @param flowid
	 *            文档流程 id
	 * @return 当前文档流程状态
	 * @throws Exception
	 */
	public FlowStateRT findFlowStateRTByDocidAndFlowid(String docid, String flowid) throws Exception {
		return ((FlowStateRTDAO) getDAO()).findFlowStateRTByDocidAndFlowid(docid, flowid);
	}

	public void doUpdate(ValueObject vo) throws Exception {
		super.doUpdate(vo);
	}

	public Collection<FlowStateRT> getSubStates(String parent) throws Exception {
		return ((FlowStateRTDAO) getDAO()).queryByParent(parent);
	}

	public FlowStateRT createTransientFlowStateRT(Document doc, String flowId, WebUser user) throws Exception {
		BillDefiProcess process = (BillDefiProcess) ProcessFactory.createProcess(BillDefiProcess.class);
		BillDefiVO flowVO = (BillDefiVO) process.doView(flowId);
		if (flowVO != null) {
			return createTransientFlowStateRT(doc, flowVO, user);
		}
		return null;
	}

	public FlowStateRT createTransientFlowStateRT(Document doc, BillDefiVO flowVO, WebUser user) throws Exception {
		FlowStateRT state = new FlowStateRT();
		state.setId(Tools.getSequence());
		state.setApplicationid(doc.getApplicationid());
		state.setFlowName(flowVO.getSubject());
		state.setLastModified(new Date());
		state.setLastModifierId(user.getId());
		state.setInitiator(user.getName());
		state.setAudituser(user.getId());
		state.setDocument(doc);
		state.setFlowVO(flowVO);
		state.setTemp(true);
		state.setPosition(0);
		return state;
	}

	public FlowStateRT createTransientSubFlowStateRT(Document subFlowDoc, BillDefiVO subFlowVO,
													 FlowStateRT parentInstance, String token, SubFlow node, WebUser user) throws Exception {
		FlowStateRT state = new FlowStateRT();
		state.setId(Tools.getSequence());
		state.setApplicationid(subFlowDoc.getApplicationid());
		state.setFlowName(subFlowVO.getSubject());
		state.setLastModified(new Date());
		state.setLastModifierId(user.getId());
		state.setInitiator(user.getName());
		state.setAudituser(user.getId());
		state.setDocument(subFlowDoc);
		state.setFlowVO(subFlowVO);
		state.setSubFlowNodeId(node.id);
		state.setParent(parentInstance);
		state.setToken(token);
		state.setCallback(node.callback);
		state.setTemp(true);
		return state;
	}

	public void startFlow(FlowStateRT instance, Document doc, ParamsTable params) throws Exception {
	}

	public boolean isAllSubFlowStateRTComplete(FlowStateRT subFlowInstance) throws Exception {
		return ((FlowStateRTDAO) getDAO()).isAllSubFlowStateRTComplete(subFlowInstance);
	}

	public void callBack(FlowStateRT subFlowInstance, IRunner runner, String script, ParamsTable params, WebUser user)
			throws Exception {
	}

	public FlowStateRT getCurrFlowStateRT(Document doc, WebUser user, String currFlowStateId) throws Exception {

		return ((FlowStateRTDAO) getDAO()).getCurrFlowStateRT(doc, user, currFlowStateId);
	}

	public boolean isMultiFlowState(Document doc, WebUser user) throws Exception {
		return ((FlowStateRTDAO) getDAO()).isMultiFlowState(doc, user);
	}

	public boolean isMultiFlowState(Document doc) throws Exception {
		return ((FlowStateRTDAO) getDAO()).isMultiFlowState(doc);
	}

	public void asynchronous2Next(String instanceId, String subFlowNodeId, ParamsTable params, WebUser user)
			throws Exception {
		FlowStateRT instance = (FlowStateRT) doView(instanceId);
		doApprove(instance, subFlowNodeId, params, user);
	}

	public void asynchronous2Next(FlowStateRT instance, String subFlowNodeid, ParamsTable params, WebUser user)
			throws Exception {

		doApprove(instance, subFlowNodeid, params, user);
	}

	public void doParentFlow2Next(FlowStateRT subFlowInstance, ParamsTable params, WebUser user) throws Exception {
		FlowStateRT instance = subFlowInstance.getParent();
		doApprove(instance, subFlowInstance.getSubFlowNodeId(), params, user, false);
	}

	public void doApprove(FlowStateRT instance, String currNodeId, ParamsTable params, WebUser user) throws Exception {
		doApprove(instance, currNodeId, params, user, true);
	}

	public void doApprove(ParamsTable params, FlowStateRT instance, String currNodeId, String[] nextNodeIds,
						  String flowOption, String comment, Environment evt, WebUser user) throws Exception {
		doApprove(params, instance, currNodeId, nextNodeIds, flowOption, comment, evt, user, true);
	}

	/**
	 * 只提交流程，不更新文档状态
	 * 
	 * @param instance
	 * @param currNodeId
	 * @param params
	 * @param user
	 * @param updateDocument
	 * @throws Exception
	 */
	private void doApprove(FlowStateRT instance, String currNodeId, ParamsTable params, WebUser user,
			boolean updateDocument) throws Exception {
		FlowDiagram fd = instance.getFlowVO().toFlowDiagram();
		Collection<Node> nextNodeList = fd.getNextNodeList(currNodeId);

		if (nextNodeList == null || nextNodeList.isEmpty()) {
			throw new Exception("没有找到流程的下一步骤");
		}

		StringBuffer nextNodeIds = new StringBuffer();
		for (Iterator<Node> it = nextNodeList.iterator(); it.hasNext();) {
			Node n = it.next();
			nextNodeIds.append(n.id).append(",");
		}
		nextNodeIds.setLength(nextNodeIds.length() - 1);

		doApprove(params, instance, currNodeId, nextNodeIds.toString().split(","), FlowType.RUNNING2RUNNING_NEXT, "",
				Environment.getInstance(), user, updateDocument);
	}

	private void doApprove(ParamsTable params, FlowStateRT instance, String currNodeId, String[] nextNodeIds,
			String flowOption, String comment, Environment evt, WebUser user, boolean updateDocument) throws Exception {
		NotificationProcess notificationProcess = new NotificationProcessBean(getApplicationId());
		PendingProcess pendingProcess = new PendingProcessBean(getApplicationId());
		DocumentProcess docProcess = (DocumentProcess) ProcessFactory.createRuntimeProcess(DocumentProcess.class,
				getApplicationId());
		try {
			Log.info("FlowStateRTProcessBean:beginTransaction() start");
			beginTransaction();
			Log.info("FlowStateRTProcessBean:beginTransaction() end");
			// Document doc = instance.getDocument();
			Document origDoc = (Document) instance.getDocument().clone();

			Log.info("FlowStateRTProcessBean: 出错后要处理 start");
			// 出错后要处理
			instance.getDocument().setAuditdate(new Date());
			instance.setAuditdate(new Date());
			instance.getDocument().setAudituser(user.getId());
			instance.setAudituser(user.getId());
			instance.getDocument().setLastFlowOperation(flowOption);
			instance.setLastFlowOperation(flowOption);
			instance.getDocument().setLastmodifier(user.getId());
			Log.info("FlowStateRTProcessBean: 出错后要处理 end");

			Log.info("FlowStateRTProcessBean: 为批量审批时添加审批备注 start");
			// 为批量审批时添加审批备注_attitude。 add by by dolly 2011-3-10
			if (comment == null || comment.equals("")) {
				comment = params.getParameterAsString("_attitude");
			}
			Log.info("FlowStateRTProcessBean: 为批量审批时添加审批备注 end");
			
			Log.info("FlowStateRTProcessBean: 处理提交或回退提醒 start");			
			// 处理提交或回退提醒
			if (!flowOption.equals(FlowType.START2RUNNING) && !flowOption.equals(FlowType.SUSPEND2RUNNING)
					&& !flowOption.equals(FlowType.AUTO2RUNNING)) {
				notificationProcess.notifySender(origDoc, instance.getFlowVO(), user); // 送出提醒
			}
			Log.info("FlowStateRTProcessBean: 处理提交或回退提醒 end");
			
			Log.info("FlowStateRTProcessBean: StateMachine.doFlow start");
			StateMachine.doFlow(params, instance, currNodeId, nextNodeIds, user, flowOption, comment, evt);
			Log.info("FlowStateRTProcessBean: StateMachine.doFlow end");
			
			if (updateDocument) {
				Log.info("FlowStateRTProcessBean: if (updateDocument) start");
				docProcess.doCreateOrUpdate(instance.getDocument(), user);
				PendingVO pending = (PendingVO) pendingProcess.doView(instance.getDocument().getId());

				if (flowOption.equals(FlowType.RUNNING2RUNNING_NEXT) || flowOption.equals(FlowType.START2RUNNING)
						|| flowOption.equals(FlowType.AUTO2RUNNING)) {
					notificationProcess.notifyCurrentAuditors(instance.getDocument(), pending, instance.getFlowVO()); // 到达提醒
				} else if (flowOption.equals(FlowType.RUNNING2RUNNING_BACK)) {
					notificationProcess.notifyRejectees(instance.getDocument(), pending, instance.getFlowVO()); // 回退提醒
				}
				Log.info("FlowStateRTProcessBean: if (updateDocument) end");
			}

			Log.info("FlowStateRTProcessBean:commitTransaction() start");
			commitTransaction();
			Log.info("FlowStateRTProcessBean:commitTransaction() end");
			// 启动自动审批任务
			AutoAuditJobManager.startJobByDoc(instance.getDocument());
			if (!StringUtil.isBlank(instance.getAsyncSubFlowNodeId())) {
				asynchronous2Next(instance.getId(), instance.getAsyncSubFlowNodeId(), params, user);
			}
		} catch (Exception e) {
			Log.info("FlowStateRTProcessBean:Exception rollbackTransaction() start");
			rollbackTransaction();
			Log.info("FlowStateRTProcessBean:Exception rollbackTransaction() end");
			Log.error("FlowStateRTProcessBean:Exception info", e);
			if(!(e instanceof WorkflowException)){
				e.printStackTrace();
			}
			
			throw e;
		}
	}

	public void runCallbackScript(ParamsTable params, FlowStateRT instance, WebUser user) throws Exception {
		IRunner runner = JavaScriptFactory.getInstance(params.getSessionid(), instance.getApplicationid());
		if (instance.isSubFlowStete()) {
			runner.initBSFManager(instance.getParent().getDocument(), params, user, new ArrayList<ValidateMessage>());
		} else {
			runner.initBSFManager(instance.getDocument(), params, user, new ArrayList<ValidateMessage>());
		}
		if(instance.getParent() ==null) return;
		BillDefiVO pflow = instance.getParent().getFlowVO();
		SubFlow subFlowNode = (SubFlow) pflow.toFlowDiagram().getElementByID(instance.getSubFlowNodeId());
		if (subFlowNode != null && !StringUtil.isBlank(subFlowNode.callbackScript)) {
			runner.run("callback Script:", StringUtil.dencodeHTML(subFlowNode.callbackScript));
		}
	}

	public Collection<FlowStateRT> doQueryBySQL(String sql) throws Exception {
		return ((FlowStateRTDAO) getDAO()).queryBySQL(sql);
	}

}
