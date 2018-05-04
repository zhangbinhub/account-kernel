package OLink.bpm.core.workflow.engine;

import java.util.ArrayList;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.dynaform.document.action.DocumentAction;
import OLink.bpm.core.dynaform.form.action.ImpropriateException;
import OLink.bpm.core.dynaform.form.ejb.ValidateMessage;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.macro.runner.JavaScriptFactory;
import OLink.bpm.core.workflow.FlowType;
import OLink.bpm.core.workflow.element.FlowDiagram;
import OLink.bpm.core.workflow.element.Node;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.cache.MemoryCacheUtil;
import OLink.bpm.core.workflow.FlowState;
import OLink.bpm.core.workflow.element.ManualNode;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiVO;
import OLink.bpm.core.workflow.storage.runtime.ejb.NodeRT;
import com.opensymphony.xwork.Action;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.document.ejb.DocumentProcess;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.workflow.WorkflowException;

public class StateMachineAction extends DocumentAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7970496758364995341L;

	/**
	 * 下一个节点数组
	 */
	private String[] _nextids;

	/**
	 * 当前节点id
	 */
	private String _currid;

	private String _flowType;

	private String _attitude;

	/*
	 * 用户所选择地提交对象{node0:u1;u2,node1:u1;u3}
	 */
	private String submitTo;

	public String getSubmitTo() {
		return submitTo;
	}

	public void setSubmitTo(String submitTo) {
		this.submitTo = submitTo;
	}

	public String get_attitude() {
		return _attitude;
	}

	public void set_attitude(String _attitude) {
		this._attitude = _attitude;
	}

	public String get_flowType() {
		return _flowType;
	}

	public void set_flowType(String type) {
		_flowType = type;
	}

	public String get_currid() {
		return _currid;
	}

	public void set_currid(String _currid) {
		this._currid = _currid;
	}

	public String[] get_nextids() {
		return _nextids;
	}

	public void set_nextids(String[] _nextids) {
		this._nextids = _nextids;
	}

	public StateMachineAction() throws Exception {

	}

	public String doViewFlow() throws Exception {
		return Action.SUCCESS;
	}

	/**
	 * 流程提交
	 * 
	 * @return SUCCESS or INPUT
	 * @throws Exception
	 */
	public String doFlow() throws Exception {
		WebUser user = this.getUser();

		// synchronized (user) {
		if (user.getStatus() == 1) {
			try {
				ParamsTable params = getParams();
				Document doc = (Document) getContent();
				doc = rebuildDocument(doc, params);
				DocumentProcess proxy = (DocumentProcess) ProcessFactory.createRuntimeProcess(DocumentProcess.class,doc.getApplicationid());
				doc = proxy.doFlow(doc, params, get_currid(), get_nextids(), get_flowType(),
						get_attitude(), user);
				setContent(doc);
				MemoryCacheUtil.putToPrivateSpace(doc.getId(), doc, getUser());
				set_attitude("");// 将remarks清空
			} catch (Exception e) {
				this.addFieldError("System Error", e.getMessage());
				if((e instanceof ImpropriateException)){
					//加载数据库中最新的文档到上下文环境
					setContent(getProcess().doView(getContent().getId()));
					MemoryCacheUtil.putToPrivateSpace(getContent().getId(), getContent(), getUser());
				}
				if(!(e instanceof WorkflowException) && !(e instanceof ImpropriateException)){
					e.printStackTrace();
				}
				return Action.INPUT;
			}
			return Action.SUCCESS;
		} else {
			this.addFieldError("System Error",
					"{*[core.flow.intervention.table.notexist]*}  please update the application");
			return Action.INPUT;
		}
	}

	/**
	 * 流程回撤
	 * 
	 * @return
	 * @throws Exception
	 * @author Happy
	 */
	public String doRetracement() throws Exception {
		WebUser user = this.getUser();

		if (user.getStatus() == 1) {
			try {
				ParamsTable params = getParams();
				Document doc = (Document) getContent();
				doc = rebuildDocument(doc, params);
				DocumentProcess proxy = (DocumentProcess) ProcessFactory.createRuntimeProcess(DocumentProcess.class,doc.getApplicationid());
				BillDefiVO flowVO = doc.getState().getFlowVO();
				FlowDiagram fd = flowVO.toFlowDiagram();
				NodeRT nodert = doc.getState().getNoderts().iterator().next();
				Node currNode = (Node) fd.getElementByID(nodert.getNodeid());
				Node nextNode = StateMachine.getBackNodeByHis(doc, flowVO, currNode.id, user, FlowState.RUNNING);
				if (nextNode != null) {

					IRunner runner = JavaScriptFactory.getInstance(params.getSessionid(), application);
					runner.initBSFManager(doc, params, user, new ArrayList<ValidateMessage>());

					boolean allowRetracement = false;
					if (((ManualNode) nextNode).retracementEditMode == 0 && ((ManualNode) nextNode).cRetracement) {
						allowRetracement = true;
					} else if (((ManualNode) nextNode).retracementEditMode == 1
							&& ((ManualNode) nextNode).retracementScript != null
							&& (((ManualNode) nextNode).retracementScript).trim().length() > 0) {
						StringBuffer label = new StringBuffer();
						label.append(doc.getFormname()).append(".Activity(").append(params.getParameter("_activityId"))
								.append("流程回撤").append(".retracementScript");
						Object result = runner.run(label.toString(), ((ManualNode) nextNode).retracementScript);
						if (result != null && result instanceof Boolean) {
							if (((Boolean) result).booleanValue())
								allowRetracement = true;
						}
					}

					if (allowRetracement) {
						// 指的审批人
						String submitTo = "[{\"nodeid\":'" + nextNode.id + "',\"isToPerson\":'true',\"userids\":\"["
								+ user.getId() + "]\"},]";
						params.setParameter("submitTo", submitTo);
						params.setParameter("doRetracement", "true");

						String[] nextids = { nextNode.id };
						proxy.doFlow(doc, params, currNode.id, nextids,
								FlowType.RUNNING2RUNNING_RETRACEMENT, get_attitude(), user);
						// doc.setReadusers("");
//						setContent(doc);
						proxy.doUpdate(doc, true);
						MemoryCacheUtil.putToPrivateSpace(doc.getId(), doc, getUser());
						set_attitude("");// 将remarks清空
					} else {
						this.addFieldError("System Error", "此流程状态下不允许回撤");
						return Action.INPUT;
					}
				} else {
					this.addFieldError("System Error", "您没有回撤的权限");
					return Action.INPUT;
				}
			} catch (Exception e) {
				this.addFieldError("System Error", e.getMessage());
				e.printStackTrace();
				return Action.INPUT;
			}
			return Action.SUCCESS;
		} else {
			this.addFieldError("System Error", "{*[core.user.noeffectived]*}");
			return Action.INPUT;
		}

	}

}
