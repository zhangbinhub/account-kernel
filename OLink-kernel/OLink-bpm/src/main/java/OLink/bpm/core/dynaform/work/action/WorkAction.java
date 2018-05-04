package OLink.bpm.core.dynaform.work.action;


import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.dynaform.document.action.DocumentAction;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.document.ejb.DocumentProcess;
import OLink.bpm.core.dynaform.work.ejb.WorkVO;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.workflow.engine.StateMachine;
import com.opensymphony.xwork.Action;

/**
 * @author Happy
 * 
 */
public class WorkAction extends DocumentAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6517074447281803556L;

	private DataPackage<WorkVO> workDatas;

//	private String isWorkManager;
	
	private String _flowId;

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

	/**
	 * Get the DataPackage with WorkVO
	 * 
	 * @return
	 */
	public DataPackage<WorkVO> getWorkDatas() {
		return workDatas;
	}

	/**
	 * @param workDatas
	 */
	public void setWorkDatas(DataPackage<WorkVO> workDatas) {
		this.workDatas = workDatas;
	}
/*
 * 
	public String getIsWorkManager() {
		try{
			if(isWorkManager ==null || isWorkManager.trim().length()<=0){
				for (Iterator<RoleVO> iter = getUser().getRoles().iterator(); iter
				.hasNext();) {
					if (RoleVO.WORK_MANAGER_TRUE.equals(iter.next().getIsWorkManager())) {
						return RoleVO.WORK_MANAGER_TRUE;
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return isWorkManager;
	}

	public void setIsWorkManager(String isWorkManager) {
		this.isWorkManager = isWorkManager;
	}
*/	
	

	public String get_flowId() {
		return _flowId;
	}

	public void set_flowId(String flowId) {
		_flowId = flowId;
	}

	public WorkAction() throws ClassNotFoundException {
		super();
	}

	/**
	 * 查询出符合条件的工作列表
	 * 
	 * @return
	 */
	public String doWorkList() {

		try {
			this.setWorkDatas(((DocumentProcess) getProcess()).queryWorks(getParams(), getUser()));
			return Action.SUCCESS;
		} catch (Exception e) {
			addFieldError("1", e.getMessage());
			e.printStackTrace();
			return Action.INPUT;
		}

	}

	/**
	 * 流程提交
	 * 
	 * @return SUCCESS or INPUT
	 * @throws Exception
	 */
	public String doFlow() throws Exception {
		WebUser user = this.getUser();

		if (user.getStatus() == 1) {
			try {
				ParamsTable params = getParams();
				Document doc = (Document) getProcess().doView(
						params.getParameterAsString("_docid"));
				((DocumentProcess) getProcess()).doFlow(doc, params,
						get_currid(), get_nextids(), get_flowType(),
						get_attitude(), user);
				getProcess().doUpdate(doc);
				set_attitude("");// 将remarks清空
			} catch (Exception e) {
				this.addFieldError("System Error", e.getMessage());
				e.printStackTrace();
				return Action.INPUT;
			}
			this.setWorkDatas(((DocumentProcess) getProcess()).queryWorks(
					getParams(), getUser()));// 刷新列表
			return Action.SUCCESS;
		} else {
			this.addFieldError("System Error", "{*[core.user.noeffectived]*}");
			return Action.INPUT;
		}
	}

	public String doCommissionedWork() throws Exception {
		try {
			ParamsTable params = getParams();
			Document doc = (Document) getProcess().doView(
					params.getParameterAsString("_docid"));
			StateMachine.commissionedWork(doc, params, getUser());
		} catch (Exception e) {
			this.addFieldError("System Error", e.getMessage());
			e.printStackTrace();
			return Action.INPUT;
		}
		this.setWorkDatas(((DocumentProcess) getProcess()).queryWorks(
				getParams(), getUser()));// 刷新列表
		return Action.SUCCESS;
	}

	public String doRemoveWork() throws Exception {
		try {
			getProcess().doRemove(getParams().getParameterAsString("_docid"));
		} catch (Exception e) {
			this.addFieldError("System Error", e.getMessage());
			e.printStackTrace();
			return Action.INPUT;
		}
		this.setWorkDatas(((DocumentProcess) getProcess()).queryWorks(
				getParams(), getUser()));// 刷新列表
		return Action.SUCCESS;
	}
}
