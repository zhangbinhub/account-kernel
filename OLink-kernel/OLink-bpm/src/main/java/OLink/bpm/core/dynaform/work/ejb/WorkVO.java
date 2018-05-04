package OLink.bpm.core.dynaform.work.ejb;

import java.io.Serializable;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.dynaform.document.ejb.DocumentProcess;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.user.ejb.BaseUser;
import OLink.bpm.util.ProcessFactory;

/**
 * 
 * @author Happy
 * 
 */
public class WorkVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5273894768950344743L;

	private String applicationId;

	private String docId;

	private String formId;

	private String flowId;

	private String flowName;

	private String stateLabel;

	private String auditorNames;

	private String auditorList;

	private String subject;

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public String getFlowId() {
		return flowId;
	}

	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}

	public String getFlowName() {
		return flowName;
	}

	public void setFlowName(String flowName) {
		this.flowName = flowName;
	}

	public String getStateLabel() {
		return stateLabel;
	}

	public void setStateLabel(String stateLabel) {
		this.stateLabel = stateLabel;
	}

	public String getAuditorNames() {
		return auditorNames;
	}

	public void setAuditorNames(String auditorNames) {
		this.auditorNames = auditorNames;
	}

	public String getAuditorList() {
		return auditorList;
	}

	public void setAuditorList(String auditorList) {
		this.auditorList = auditorList;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		if (subject == null) {
			this.subject = "";
		} else {
			this.subject = subject.replaceAll("&#160;", " ");
		}
	}

	public static void main(String[] args) throws Exception {
		ParamsTable params = new ParamsTable();
		params.setParameter("_currpage", 1);
		params.setParameter("_pagelines", 1);
		// params.setParameter("_actorid", "");
		params.setParameter("_processType", "processing");
		WebUser user = new WebUser(new BaseUser());
		user.setId("11e0-463e-37537d91-8d25-d362e82b2291");
		user.setDomainid("11de-c138-782d2f26-9a62-8bacb70a86e1");
		DocumentProcess process = (DocumentProcess) ProcessFactory
				.createRuntimeProcess(DocumentProcess.class,
						"11e0-4638-a6848426-8d25-d362e82b2291");

		DataPackage<WorkVO> dp = process.queryWorks(params, user);
		System.out.println(dp.toString());
	}

}
