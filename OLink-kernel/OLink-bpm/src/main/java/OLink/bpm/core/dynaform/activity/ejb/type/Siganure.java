package OLink.bpm.core.dynaform.activity.ejb.type;

import OLink.bpm.core.dynaform.activity.ejb.Activity;
import OLink.bpm.core.dynaform.activity.ejb.ActivityType;

public class Siganure extends ActivityType {

	public Siganure(Activity act) {
		super(act);
		
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public String getAfterAction() {
		
		return  DOCUMENT_JSP_NAMESPACE + "/content.jsp";
	}


	public String getBackAction() {
		
		return  DOCUMENT_JSP_NAMESPACE + "/content.jsp";
	}

	
	public String getBeforeAction() {
		
		return  DOCUMENT_JSP_NAMESPACE + "/content.jsp";
	}

	
	public String getButtonId() {
		
		return DOCUMENT_BUTTON_ID;
	}

	
	public String getDefaultClass() {
		
		return DOCUMENT_BUTTON_CLASS;
	}

	public String getDefaultOnClass() {
		
		return DOCUMENT_BUTTON_ON_CLASS;
	}


	public String getOnClickFunction() {
		
		return "DoSignature()";
	}
//	protected void addButton(String innerText, String function, String className) {
//		htmlBuilder.append("<span class='" + className + "'><a href=\"###\"");
//		htmlBuilder.append(" name='" + getButtonId() + "'");
//		htmlBuilder.append(" title='" + act.getName() + "'");
//		htmlBuilder.append(" onclick=\"" + function + "\"");
//		htmlBuilder.append(" onmouseover='this.className=\"" + getButtonOnClass() + "\"'");
//		htmlBuilder.append(" onmouseout='this.className=\"" + getButtonClass() + "\"'");
//		htmlBuilder.append(" >");
//		htmlBuilder.append("<span>");
//		htmlBuilder.append("<img style='border:0px solid blue;vertical-align:middle;' src='../../../resource/imgv2/front/act/act_"
//						+ act.getType() + ".gif' />&nbsp;");
//		htmlBuilder.append(innerText);
//		Environment env = Environment.getInstance();
//		HttpServletRequest request=ServletActionContext.getRequest();
//		htmlBuilder.append("<OBJECT id=\"SignatureControl\"  classid=\"clsid:D85C89BE-263C-472D-9B6B-5264CD85B36E\" codebase=\"iSignatureHTML.cab#version=7,1,0,180\" width=0 height=0 VIEWASTEXT>");
//		htmlBuilder.append("<param name=\"ServiceUrl\" value=\"http://"+request.getServerName()+":"+request.getServerPort()+env.getContextPath()+"/core/dynaform/mysignature/doCommand.action?" +
//				           "FormID="+act.getOnActionForm()+
//				           "\"/>");
//		htmlBuilder.append("<param name=\"WebAutoSign\" value=\"0\"/> ");
//		htmlBuilder.append("<param name=\"PrintControlType\" value=\"2\"/> ");
//		htmlBuilder.append("</OBJECT>");
//		htmlBuilder.append("</span></a></span>");
//
//		
//	}

}
