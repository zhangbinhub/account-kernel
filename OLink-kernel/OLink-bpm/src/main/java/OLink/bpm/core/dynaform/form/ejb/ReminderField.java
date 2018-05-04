package OLink.bpm.core.dynaform.form.ejb;

import OLink.bpm.constans.Environment;
import OLink.bpm.core.domain.ejb.DomainVO;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.domain.ejb.DomainProcess;
import OLink.bpm.core.dynaform.PermissionType;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.summary.ejb.SummaryCfgProcess;
import OLink.bpm.core.dynaform.summary.ejb.SummaryCfgVO;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.ProcessFactory;

public class ReminderField extends FormField implements ValueStoreField{
	
	private static final long serialVersionUID = 3011283097413156840L;
	
	protected String reminderid;//提醒编号
	
	public String getReminderid() {
		return reminderid;
	}

	public void setReminderid(String reminderid) {
		this.reminderid = reminderid;
	}

	@Override
	public String toMbXMLText(Document doc, IRunner runner, WebUser webUser)
			throws Exception {
		return null;
	}

	@Override
	public String toTemplate() {
		StringBuffer template = new StringBuffer();
		template.append("<input type='text'");
		template.append(" className='" + this.getClass().getName() + "'");
		template.append(" id='" + getId() + "'");
		template.append(" name='" + getName() + "'");
		template.append(" formid='" + getFormid() + "'");
		template.append(" discript='" + getDiscript() + "'");
		template.append(" hiddenScript='" + getHiddenScript() + "'");
		template.append(" hiddenPrintScript='" + getHiddenPrintScript() + "'");
		template.append(">");
		return template.toString();
	}

	/**
	 * 以网格的形式显示
	 */
	public String toGridHtmlText(Document doc, IRunner runner, WebUser webUser)
			throws Exception {
		return toHtmlTxt(doc,runner,webUser);
	}

	/**
	 * 以一般网页的形式显示
	 */
	public String toHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		StringBuffer htmlBuilder = new StringBuffer();
		int displayType = getDisplayType(doc, runner, webUser);
		String skinType = "";

		//获取皮肤参数
		if(webUser.getUserSetup() != null){
			skinType = webUser.getUserSetup().getUserSkin();
		}else{
			DomainProcess domPro=(DomainProcess) ProcessFactory.createProcess(DomainProcess.class);
			DomainVO domainVO = (DomainVO) domPro.doView(webUser.getDomainid());
			skinType = domainVO.getSkinType();
		}
		
		if (displayType == PermissionType.HIDDEN) {
			return this.getHiddenValue();
		}else if(displayType == PermissionType.MODIFY){
		
			SummaryCfgProcess summaryCfgProcess = (SummaryCfgProcess) ProcessFactory.createProcess(SummaryCfgProcess.class);
			SummaryCfgVO summaryCfg = (SummaryCfgVO) summaryCfgProcess.doView(getReminderid());
			if(summaryCfg!=null){
			String contextPath = Environment.getInstance().getContextPath();
			htmlBuilder.append("<div class='ElementDiv'");
			htmlBuilder.append(" src='" + contextPath);

			if (skinType != null && !skinType.equals("") && (skinType.equals("fresh")||skinType.equals("blue"))) {
				htmlBuilder.append("/portal/dynaform/document/pendinglist-fresh.action?formid=");
			}else{
				htmlBuilder.append("/portal/dynaform/document/pendinglist-nogray.action?formid=");
			}
			htmlBuilder.append(summaryCfg.getFormId());
			htmlBuilder.append("&application="+get_form().getApplicationid());
			htmlBuilder.append("&_pagelines=10&summaryCfgId=" + summaryCfg.getId()+ "&_orderby=" + summaryCfg.getOrderby()).append("'");
//			htmlBuilder.append(" frameborder='0'");
			htmlBuilder.append(" height='250px'");
			htmlBuilder.append(" width='" + 320 + "px'");
			htmlBuilder.append(" style='margin-left:5px;'");
			htmlBuilder.append(">");
			htmlBuilder.append("</div>");
			}
		}
		return htmlBuilder.toString();
	}

	/**
	 * 打印
	 */
	public String toPrintHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		StringBuffer htmlBuilder = new StringBuffer();
		int displayType = getPrintDisplayType(doc, runner, webUser);
		if (displayType == PermissionType.HIDDEN) {
			return this.getPrintHiddenValue();
		}else if(displayType == PermissionType.MODIFY){
		
			htmlBuilder.append("<table border='0'>");
			htmlBuilder.append("<tr>");
			htmlBuilder.append("<td>");
			SummaryCfgProcess summaryCfgProcess = (SummaryCfgProcess) ProcessFactory.createProcess(SummaryCfgProcess.class);
			SummaryCfgVO summaryCfg = (SummaryCfgVO) summaryCfgProcess.doView(getReminderid());
			if(summaryCfg!=null){
			String contextPath = Environment.getInstance().getContextPath();
			htmlBuilder.append("<iframe");
			htmlBuilder.append(" src='" + contextPath);
			htmlBuilder.append("/portal/dynaform/document/pendinglist-nogray.action?formid=");
			htmlBuilder.append(summaryCfg.getFormId());
			htmlBuilder.append("&application="+get_form().getApplicationid());
			htmlBuilder.append("&_pagelines=10&summaryCfgId=" + summaryCfg.getId()+ "&_orderby=" + summaryCfg.getOrderby()).append("'");
			htmlBuilder.append(" frameborder='0'");
			htmlBuilder.append(" height='250px'");
			htmlBuilder.append(" width='" + 320 + "px'");
			htmlBuilder.append(" style='margin-left:5px;'");
			htmlBuilder.append(">");
			htmlBuilder.append("</iframe>");
			}
			htmlBuilder.append("</td>");
			htmlBuilder.append("</tr><tr>");
			htmlBuilder.append("</table>");
		}
		return htmlBuilder.toString();
	}

}
