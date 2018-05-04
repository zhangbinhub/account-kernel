package OLink.bpm.core.workflow.storage.runtime.intervention.action;

import java.util.Collection;
import java.util.Map;

import OLink.bpm.base.action.AbstractRunTimeAction;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.base.ejb.IRunTimeProcess;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.document.ejb.DocumentProcess;
import OLink.bpm.core.workflow.FlowType;
import OLink.bpm.core.workflow.storage.runtime.ejb.NodeRT;
import OLink.bpm.core.workflow.storage.runtime.intervention.ejb.FlowInterventionVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.constans.Web;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.workflow.storage.runtime.intervention.ejb.FlowInterventionProcessBean;

public class FlowInterventionAction extends AbstractRunTimeAction<FlowInterventionVO> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4072644497765893046L;
	
	private String nextNodeId;
	
	
	
	public String getNextNodeId() {
		return nextNodeId;
	}


	public void setNextNodeId(String nextNodeId) {
		this.nextNodeId = nextNodeId;
	}


	@Override
	public IRunTimeProcess<FlowInterventionVO> getProcess() {
		return new FlowInterventionProcessBean(getApplication());
	}
	
	
	@SuppressWarnings("static-access")
	public String doFlow() throws Exception {
		try{
			
			ParamsTable params = getParams();
			String[] ids = params.getParameterAsArray("id");
			String id = (ids != null && ids.length > 0) ? ids[0] : null;
			DocumentProcess proxy = (DocumentProcess) ProcessFactory.createRuntimeProcess(DocumentProcess.class,getApplication());
			Document doc =  (Document) proxy.doView(id);
			WebUser user = (WebUser) this.getContext().getSession().get(Web.SESSION_ATTRIBUTE_USER);
			//user.setApplicationid(getApplication());
			if(doc !=null){
				Collection<NodeRT> noderts = doc.getState().getNoderts();
				for(NodeRT nodert : noderts){
					proxy.doFlow(doc, params, nodert.getNodeid(),new String[]{getNextNodeId()}, FlowType.RUNNING2RUNNING_INTERVENTION, "",user );
				}
				
				doList();
			}else{
				throw new Exception("Doucment should not be null");
			}
			
			
			
		}catch (Exception e) {
			addFieldError("System Error", e.getMessage());
			e.printStackTrace();
			return INPUT;
		}
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



	
	

}
