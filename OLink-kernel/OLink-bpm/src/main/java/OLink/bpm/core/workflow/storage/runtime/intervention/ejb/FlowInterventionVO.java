package OLink.bpm.core.workflow.storage.runtime.intervention.ejb;

import java.util.Date;

import OLink.bpm.base.dao.ValueObject;

/**
 * 工作流干预对象
 * @author Happy
 *
 */
public class FlowInterventionVO extends ValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1869526076159582039L;
	
	/**
	 * 文档摘要
	 */
	private String summary;
	
	/**
	 * 流程名称
	 */
	private String flowName;
	
	/**
	 * 流程状态
	 */
	private String stateLabel;
	
	/**
	 * 发起人名字
	 */
	private String initiator;
	
	/**
	 * 最后处理人名字
	 */
	private String lastAuditor;
	
	/**
	 * 流程开始时间
	 */
	private Date firstProcessTime;
	
	/**
	 * 最后处理时间
	 */
	private Date lastProcessTime;
	
	private String flowId;
	
	private String formId;
	
	private String docId;

	/**
	 * 获取文档摘要
	 * @return
	 * 		文档摘要
	 */
	public String getSummary() {
		return summary;
	}

	/**
	 * 设置文档摘要
	 * @param summary
	 * 		文档摘要
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}

	/**
	 * 获取流程名称
	 * @return
	 * 		流程名称
	 */
	public String getFlowName() {
		return flowName;
	}

	/**
	 * 设置流程名称
	 * @param flowName
	 * 		流程名称
	 */
	public void setFlowName(String flowName) {
		this.flowName = flowName;
	}

	/**
	 * 获取流程状态标签
	 * @return
	 * 		流程状态标签
	 */
	public String getStateLabel() {
		return stateLabel;
	}

	/**
	 * 设置流程状态标签
	 * @param stateLabel
	 * 		流程状态标签
	 */
	public void setStateLabel(String stateLabel) {
		this.stateLabel = stateLabel;
	}

	/**
	 * 获取流程发起人姓名
	 * @return
	 * 		流程发起人姓名
	 */
	public String getInitiator() {
		return initiator;
	}

	/**
	 * 设置流程发起人姓名
	 * @param initiator
	 * 		流程发起人姓名
	 */
	public void setInitiator(String initiator) {
		this.initiator = initiator;
	}

	/**
	 * 获取流程最后处理人姓名
	 * @return
	 * 		流程最后处理人姓名
	 */
	public String getLastAuditor() {
		return lastAuditor;
	}

	/**
	 * 设置流程最后处理人姓名
	 * @param lastAuditor
	 * 		流程最后处理人姓名
	 */
	public void setLastAuditor(String lastAuditor) {
		this.lastAuditor = lastAuditor;
	}

	/**
	 * 获取流程开始时间
	 * @return
	 * 		流程开始时间
	 */
	public Date getFirstProcessTime() {
		return firstProcessTime;
	}

	/**
	 * 设置流程开始时间
	 * @param firstProcessTime
	 * 		流程开始时间
	 */
	public void setFirstProcessTime(Date firstProcessTime) {
		this.firstProcessTime = firstProcessTime;
	}

	/**
	 * 获取流程最后处理时间
	 * @return
	 * 		流程最后处理时间
	 */
	public Date getLastProcessTime() {
		return lastProcessTime;
	}

	/**
	 * 设置流程最后处理时间
	 * @param lastProcessTime
	 * 		流程最后处理时间
	 */
	public void setLastProcessTime(Date lastProcessTime) {
		this.lastProcessTime = lastProcessTime;
	}

	/**
	 * 获取关联的流程Id
	 * @return
	 */
	public String getFlowId() {
		return flowId;
	}

	/**
	 * 设置关联的流程ID
	 * @param flowId
	 */
	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}

	/**
	 * 获取关联的文档ID
	 * @return
	 */
	public String getDocId() {
		return docId;
	}

	/**
	 * 设置关联的文档ID
	 * @param docId
	 */
	public void setDocId(String docId) {
		this.docId = docId;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}
	
	
	
	
	

}
