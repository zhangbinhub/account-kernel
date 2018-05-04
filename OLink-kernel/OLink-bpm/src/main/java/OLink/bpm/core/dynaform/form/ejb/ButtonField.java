package OLink.bpm.core.dynaform.form.ejb;

import OLink.bpm.core.dynaform.PermissionType;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.property.MultiLanguageProperty;
import OLink.bpm.core.dynaform.activity.ejb.Activity;
import OLink.bpm.core.dynaform.activity.ejb.ActivityType;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.table.constants.MobileConstant;
import OLink.bpm.core.workflow.engine.StateMachineHelper;
import OLink.bpm.util.StringUtil;
import eWAP.core.Tools;

public class ButtonField extends FormField {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2243912010678637603L;

	private int actType;
	private String actionView;
	private String actionForm;
	private String actionFlow;
	public String getActionView() {
		return actionView;
	}

	public void setActionView(String actionView) {
		this.actionView = actionView;
	}

	public String getActionForm() {
		return actionForm;
	}

	public void setActionForm(String actionForm) {
		this.actionForm = actionForm;
	}

	public String getActionFlow() {
		return actionFlow;
	}

	public void setActionFlow(String actionFlow) {
		this.actionFlow = actionFlow;
	}

	public String getActionPrint() {
		return actionPrint;
	}

	public void setActionPrint(String actionPrint) {
		this.actionPrint = actionPrint;
	}

	private String actionPrint;
	private String fileNameScript;
	private String impmappingconfigid;
	private String stateToShow;
	private String approveLimit;
	private String beforeActionScript;
	private String afterActionScript;

	public String toMbXMLText(Document doc, IRunner runner, WebUser webUser) throws Exception {
		Activity act = getActivity();
		StringBuffer template = new StringBuffer();
		if (act.getType() != ActivityType.PRINT && act.getType() != ActivityType.PRINT_WITHFLOWHIS) {
			StateMachineHelper helper = new StateMachineHelper(doc);
			template.append("<").append(MobileConstant.TAG_ACTION).append(" ").append(MobileConstant.ATT_ID).append(
					"='");
			template.append(act.getId());
			template.append("' ").append(MobileConstant.ATT_NAME).append("='");
			String ntemp = act.getName();
			String actname = MultiLanguageProperty.getProperty(MultiLanguageProperty.getName(2), ntemp, ntemp);
			actname = actname.replaceAll("&", "&amp;");
			template.append(actname);
			template.append("' ").append(MobileConstant.ATT_TYPE).append("='");
			template.append(act.getType() + "'");

			StringBuffer label = new StringBuffer();
			label.append("Form(").append(getId()).append(")." + getName()).append(".Activity.HiddenScript");
			Object result = runner.run(label.toString(), act.getHiddenScript());
			if (result != null && result instanceof Boolean) {
				if (((Boolean) result).booleanValue()) {
					template.append(" ").append(MobileConstant.ATT_HIDDEN).append(" = 'true' ");
				}
			}
			template.append(">");
			if (act.getType() == ActivityType.WORKFLOW_PROCESS) {

				template.append(helper.toFlowXMLText(doc, webUser));

			}
			template.append("</").append(MobileConstant.TAG_ACTION).append(">");
			if (act.getType() == ActivityType.WORKFLOW_PROCESS) {
				template.append("<").append(MobileConstant.TAG_HIDDENFIELD).append(" ").append(MobileConstant.ATT_NAME)
						.append("='_flowid'>");
				template.append(act.getOnActionFlow());
				template.append("</").append(MobileConstant.TAG_HIDDENFIELD).append(">");

				if (doc.getFlowid() != null) {
					template.append("<").append(MobileConstant.TAG_ACTION).append(" ").append(MobileConstant.ATT_ID)
							.append(" = '");
					template.append(Tools.getSequence());
					template.append("' ").append(MobileConstant.ATT_NAME).append("='{*[Flow]*}{*[Diagram]*}' ").append(
							MobileConstant.ATT_TYPE).append(" = '");
					template.append(ActivityType.DOCUEMNT_VIEWFLOWIMAGE);
					template.append("'>");
					template.append("</").append(MobileConstant.TAG_ACTION).append(">");
					if (helper.isShowHis(doc.getFlowid(), doc.getId(), doc.getApplicationid())) {
						template.append("<").append(MobileConstant.TAG_ACTION).append(" ")
								.append(MobileConstant.ATT_ID).append(" = '");
						template.append(Tools.getSequence());
						template.append("' ").append(MobileConstant.ATT_NAME).append("='{*[Flow]*}{*[History]*}' ").append(
								MobileConstant.ATT_TYPE).append(" = '");
						template.append("824");
						template.append("'>");
						template.append("<").append(MobileConstant.TAG_PARAMETER).append(" ").append(
								MobileConstant.ATT_NAME).append("='_docid'>" + doc.getId() + "</").append(
								MobileConstant.TAG_PARAMETER).append(">");
						template.append("</").append(MobileConstant.TAG_ACTION).append(">");
					}
				}
			}
		}
		return template.toString();
	}

	public String toTemplate() {
		StringBuffer template = new StringBuffer();
		template.append("<input type='button'");
		template.append(" className='" + this.getClass().getName() + "'");
		template.append(" id='" + getId() + "'");
		template.append(" name='" + getName() + "'");
		template.append(" formid='" + getFormid() + "'");
		template.append(" discript='" + getDiscript() + "'");
		template.append(" hiddenScript='" + getHiddenScript() + "'");
		template.append(" hiddenPrintScript='" + getHiddenPrintScript() + "'");
		template.append(" refreshOnChanged='" + isRefreshOnChanged() + "'");
		template.append(" validateRule='" + getValidateRule() + "'");
		template.append(" valueScript='" + getValueScript() + "'");
		template.append("/>");
		return template.toString();
	}

	public String toHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		Activity act = getActivity();
		int displayType = getDisplayType(doc, runner, webUser);
		if (displayType == PermissionType.HIDDEN) {
			return this.getHiddenValue();
		}

		return act.toButtonHtml(displayType);
	}

	public Activity getActivity() {
		Activity act = new Activity();
		act.setName(name);
		act.setId(id);
		act.setAfterActionScript(StringUtil.dencodeHTML(afterActionScript));
		act.setApplicationid(get_form().getApplicationid());
		act.setApproveLimit(approveLimit);
		act.setBeforeActionScript(StringUtil.dencodeHTML(beforeActionScript));
		// act.setDomainid(webUser.getDomainid());
		act.setFileNameScript(StringUtil.dencodeHTML(fileNameScript));
		act.setHiddenScript(StringUtil.dencodeHTML(hiddenScript));
		act.setImpmappingconfigid(impmappingconfigid);
		act.setOnActionFlow(actionFlow);
		act.setOnActionPrint(actionPrint);
		act.setOnActionForm(actionForm);
		act.setOnActionView(actionView);
		act.setParentForm(getFormid());
		act.setStateToShow(stateToShow);
		act.setType(actType);

		return act;
	}

	public String toPrintHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		if (doc != null) {
			int displayType = getPrintDisplayType(doc, runner, webUser);
			//如果按钮为"打印时隐藏",则隐藏
			if (displayType == PermissionType.HIDDEN) {
				return this.getPrintHiddenValue();
			}
			return toHtmlTxt(doc, runner, webUser);
		}
		return "";
	}

	public int getActType() {
		return actType;
	}

	public void setActType(int actType) {
		this.actType = actType;
	}

	public String getFileNameScript() {
		return fileNameScript;
	}

	public void setFileNameScript(String fileNameScript) {
		this.fileNameScript = fileNameScript;
	}

	public String getImpmappingconfigid() {
		return impmappingconfigid;
	}

	public void setImpmappingconfigid(String impmappingconfigid) {
		this.impmappingconfigid = impmappingconfigid;
	}

	public String getStateToShow() {
		return stateToShow;
	}

	public void setStateToShow(String stateToShow) {
		this.stateToShow = stateToShow;
	}

	public String getApproveLimit() {
		return approveLimit;
	}

	public void setApproveLimit(String approveLimit) {
		this.approveLimit = approveLimit;
	}

	public String getBeforeActionScript() {
		return beforeActionScript;
	}

	public void setBeforeActionScript(String beforeActionScript) {
		this.beforeActionScript = beforeActionScript;
	}

	public String getAfterActionScript() {
		return afterActionScript;
	}

	public void setAfterActionScript(String afterActionScript) {
		this.afterActionScript = afterActionScript;
	}

}
