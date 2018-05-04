package OLink.bpm.core.workflow.storage.runtime.proxy.action;

import java.util.Map;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.base.ejb.IRunTimeProcess;
import OLink.bpm.core.workflow.storage.runtime.proxy.ejb.WorkflowProxyProcessBean;
import OLink.bpm.core.workflow.storage.runtime.proxy.ejb.WorkflowProxyVO;
import OLink.bpm.base.action.AbstractRunTimeAction;
import OLink.bpm.core.workflow.storage.runtime.proxy.ejb.WorkflowProxyProcess;

/**
 * @author Happy
 *
 */
public class WorkflowProxyAction extends AbstractRunTimeAction<WorkflowProxyVO> {
	
	

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 6343125074617302029L;
	
	private String isRefresh;
	
	
	

	public String getIsRefresh() {
		return isRefresh;
	}


	public void setIsRefresh(String isRefresh) {
		this.isRefresh = isRefresh;
	}


	public WorkflowProxyAction() {
		this.setContent(new WorkflowProxyVO());
		
	}


	@Override
	public IRunTimeProcess<WorkflowProxyVO> getProcess() {
		return new WorkflowProxyProcessBean(getApplication());
	}
	
	
	public String doNew() throws Exception {
		//this.setContent(new WorkflowProxyVO());
		return SUCCESS;
	}
	
	public String doSave() throws Exception {
		try{
		WorkflowProxyVO vo = (WorkflowProxyVO) getContent();
		vo.setDomainid(getUser().getDomainid());
		vo.setApplicationid(getApplication());
		if(vo.getOwner()==null || vo.getOwner().trim().length()<=0){
			vo.setOwner(getUser().getId());//设置流程代理配置的所有者为当前登陆用户
		}
		
		if(vo.getId() ==null || vo.getId().trim().length()<=0){
			if(!((WorkflowProxyProcess)getProcess()).onlyCheckOnFlow(vo)){//流程配置的唯一校验
				this.addFieldError("System Error", "该流程的代理配置信息已存在");
				return INPUT;
			}
			getProcess().doCreate(vo);
		}else{
			getProcess().doUpdate(vo);
		}
		}catch (Exception e) {
			this.addFieldError("System Error", e.getMessage());
			e.printStackTrace();
			return INPUT;
		}
		this.setIsRefresh("true");
		return SUCCESS;
		
	}
	
	
	public String doList() throws Exception {
		try{
			this.setDatas(this.getProcess().doQuery(getParams(), getUser()));
		}catch (Exception e) {
			this.addFieldError("System Error", e.getMessage());
			return INPUT;
			}
		return SUCCESS;
	}
	
	public String doView() throws Exception {
		Map<?, ?> params = getContext().getParameters();

		String[] ids = (String[]) (params.get("id"));
		String id = (ids != null && ids.length > 0) ? ids[0] : null;

		ValueObject contentVO = this.getProcess().doView(id);
		setContent(contentVO);

		return SUCCESS;
	}
	
	public String doDelete() throws Exception {
		try {
			if (_selects != null) {
				((WorkflowProxyProcess)getProcess()).doRemove(_selects);
			}
		} catch (Exception e) {
			addFieldError("", e.getMessage());
			e.printStackTrace();
		}
		doList();
		return SUCCESS;
	}

	
	
	

}
