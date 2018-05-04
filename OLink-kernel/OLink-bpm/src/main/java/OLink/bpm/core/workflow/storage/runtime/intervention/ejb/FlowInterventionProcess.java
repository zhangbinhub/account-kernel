package OLink.bpm.core.workflow.storage.runtime.intervention.ejb;

import OLink.bpm.base.ejb.IRunTimeProcess;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.user.action.WebUser;

/**
 * @author Happy
 *
 */
public interface FlowInterventionProcess extends IRunTimeProcess<FlowInterventionVO> {

	/**
	 * 根据文档创建记录
	 * @param doc
	 */
	void doCreateByDocument(Document doc, WebUser user) throws Exception;
	
	void doUpdateByDocument(Document doc, WebUser user) throws Exception;
	
}
