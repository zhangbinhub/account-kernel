package OLink.bpm.core.dynaform.document.action;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;


import OLink.bpm.core.workflow.storage.runtime.ejb.FlowHistory;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiProcess;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiVO;
import OLink.bpm.core.workflow.storage.runtime.ejb.FlowStateRT;
import OLink.bpm.core.dynaform.document.ejb.DocumentProcess;
import OLink.bpm.core.workflow.storage.runtime.ejb.RelationHIS;
import OLink.bpm.core.workflow.storage.runtime.ejb.RelationHISProcess;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import OLink.bpm.core.workflow.storage.runtime.ejb.FlowStateRTProcess;

public class DocumentHelper {
	/**
	 * 根据文档主键,返回相应文档
	 * 
	 * @param id
	 *            文档主键
	 * @return 文档
	 * @throws Exception
	 */

	public static Document getDocumentById(String id, String applicationid) throws Exception {
		DocumentProcess dp = (DocumentProcess) ProcessFactory.createRuntimeProcess(DocumentProcess.class,applicationid);
		return (Document) dp.doView(id);
	}
	
	public static Document getDocumentByFlow(String id, String flowid) throws Exception {
		BillDefiProcess billDefiProcess = (BillDefiProcess) ProcessFactory.createProcess(BillDefiProcess.class);
		BillDefiVO flowVO = (BillDefiVO) billDefiProcess.doView(flowid);
		
		DocumentProcess dp = (DocumentProcess) ProcessFactory.createRuntimeProcess(DocumentProcess.class,flowVO.getApplicationid());
		return (Document) dp.doView(id);
	}
	
	public static String toHistoryHtml(HttpServletRequest request) throws Exception{
		String docid = request.getParameter("_docid");
		String flowStateId = request.getParameter("flowStateId");
		String applicationId = request.getParameter("application");
		if (!StringUtil.isBlank(flowStateId) && !StringUtil.isBlank(applicationId) && !StringUtil.isBlank(docid)) {
			FlowStateRTProcess process = (FlowStateRTProcess) ProcessFactory.createRuntimeProcess(FlowStateRTProcess.class, applicationId);
			FlowStateRT instance = (FlowStateRT) process.doView(flowStateId);
			if(instance ==null) return null;
			RelationHISProcess hisProcess = (RelationHISProcess) ProcessFactory.createRuntimeProcess(RelationHISProcess.class, applicationId);
			Collection<RelationHIS> colls = hisProcess.doQueryByDocIdAndFlowStateId(docid, flowStateId);
			FlowHistory his = new FlowHistory();
			his.addAllHis(colls);

			return his.toTextHtml();
		}
		return "";
	}
}
