package OLink.bpm.core.workflow.storage.runtime.proxy.ejb;

import OLink.bpm.base.dao.ValueObject;

/**
 * 流程代理信息
 * @author Happy
 *
 */
public class WorkflowProxyVO extends ValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5240925136126789458L;
	
	public static final String PROXY_STATE_AVAILABLE = "1";
	
	public static final String PROXY_STATE_UNAVAILABLE = "0";
	
	
	private String flowName;
	
	private String flowId;
	
	private String description;
	
	private String state;
	
	private String agents;
	
	private String agentsName;
	
	private String owner;

	public String getFlowName() {
		return flowName;
	}

	public void setFlowName(String flowName) {
		this.flowName = flowName;
	}

	
	public String getFlowId() {
		return flowId;
	}

	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getAgents() {
		return agents;
	}
	
	

	public String getAgentsName() {
		return agentsName;
	}

	public void setAgentsName(String agentsName) {
		this.agentsName = agentsName;
	}

	public void setAgents(String agents) {
		this.agents = agents;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	
	
	

}
