package OLink.bpm.core.dynaform.activity.ejb.type;

import OLink.bpm.core.dynaform.activity.ejb.ActivityType;
import OLink.bpm.core.dynaform.activity.ejb.Activity;

/**
 * 编辑文档审批人
 * 
 * @author Administrator
 * 
 */
public class DocumentEditAuditor extends ActivityType {

	public DocumentEditAuditor(Activity act) {
		super(act);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 532359891396821593L;

	public String getAfterAction() {
		return DOCUMENT_JSP_NAMESPACE + "/content.jsp";
	}

	public String getBackAction() {
		return DOCUMENT_JSP_NAMESPACE + "/content.jsp";
	}

	public String getBeforeAction() {
		return DOCUMENT_NAMESPACE + "/changeAuditor.action";
	}

	public String getButtonId() {
		return DOCUMENT_BUTTON_ID;
	}

	public String getDefaultClass() {
		return DOCUMENT_BUTTON_CLASS;
	}

	public String getOnClickFunction() {
		return "changeAuditor("+act.getType()+", '" + act.getId() + "','userlist','auditorList')";
	}

	public String getDefaultOnClass() {
		return DOCUMENT_BUTTON_ON_CLASS;
	}

}
