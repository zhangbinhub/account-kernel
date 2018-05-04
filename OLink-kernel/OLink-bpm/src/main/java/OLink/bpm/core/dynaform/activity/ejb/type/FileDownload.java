package OLink.bpm.core.dynaform.activity.ejb.type;

import OLink.bpm.core.dynaform.activity.ejb.ActivityType;
import OLink.bpm.core.dynaform.activity.ejb.Activity;

public class FileDownload extends ActivityType {

	public FileDownload(Activity act) {
		super(act);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -4618802972127624357L;

	public String getDefaultClass() {
		return DOCUMENT_BUTTON_CLASS;
	}

	public String getButtonId() {
		return DOCUMENT_BUTTON_ID;
	}

	public String getAfterAction() {
		return DOCUMENT_JSP_NAMESPACE + "/content.jsp";
	}

	public String getBackAction() {
		return DOCUMENT_JSP_NAMESPACE + "/content.jsp";
	}

	public String getBeforeAction() {
		return DOCUMENT_NAMESPACE + "/fileDownload.action";
	}

	public String getDefaultOnClass() {

		return DOCUMENT_BUTTON_ON_CLASS;
	}

	public String getOnClickFunction() {
		return "doFileDonwload('" + act.getId() + "')";
	}
	
//	protected void addButton(String innerText, String function, String className) {
//		htmlBuilder.append("<span class='" + className + "'><a href=\"###\"");
//		htmlBuilder.append(" id='" + getButtonId() + "'");
//		htmlBuilder.append(" title='" + act.getName() + "'");
//		htmlBuilder.append(" onclick=\"" + function + "\"");
//		htmlBuilder.append(" onmouseover='this.className=\"" + getButtonOnClass() + "\"'");
//		htmlBuilder.append(" onmouseout='this.className=\"" + getButtonClass() + "\"'");
//		htmlBuilder.append(" >");
//		htmlBuilder.append("<span>");
//		htmlBuilder
//				.append("<img style='border:0px solid blue;vertical-align:middle;' src='../../../resource/imgv2/front/act/act_"
//						+ act.getType() + ".gif' />&nbsp;");
//		htmlBuilder.append(innerText);
//		htmlBuilder.append("</span></a></span>");
//	}

}
