package OLink.bpm.core.dynaform.form.ejb;

import OLink.bpm.constans.Environment;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.dynaform.document.ejb.Item;
import OLink.bpm.core.dynaform.PermissionType;

public class MapField extends FormField implements ValueStoreField {
	
	private static final long serialVersionUID = 3900336554465032659L;
	protected String openType;//打开地图类型
	
	public String getOpenType() {
		return openType;
	}

	public void setOpenType(String openType) {
		this.openType = openType;
	}

	@Override
	public String toMbXMLText(Document doc, IRunner runner, WebUser webUser)
			throws Exception {
		return null;
	}

	/**
	 *模板
	 */
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
	 * 网格格式输出HTML
	 */
	public String toGridHtmlText(Document doc, IRunner runner, WebUser webUser)
			throws Exception {
		return toHtmlTxt(doc,runner,webUser);
	}

	/**
	 *普通模式输出HTML
	 */
	public String toHtmlTxt(Document doc, IRunner runner, WebUser webUser)
			throws Exception {
		if(WebUser.TYPE_BG_USER.equals(webUser.getType()))
			return toPreviewHtml(doc, runner, webUser);
		else
			return toHtml(doc, runner, webUser);
	}
	
	public String toPreviewHtml(Document doc, IRunner runner, WebUser webUser) throws Exception {
		StringBuffer html = new StringBuffer();
		int displayType = getDisplayType(doc, runner, webUser);

		if (displayType == PermissionType.HIDDEN) {
			return this.getHiddenValue();
		} else {
			if("dialog" == openType||"dialog".equals(openType)){
				html.append("<table style='width:100%;height:100%;margin:0px;'>");
				html.append("<tr>");
				html.append("<td style='border:0'>");
				html.append("<input type='button' value='{*[map]*}' name='btnSelectDept'");
				html.append(" onClick=\"");
				html.append("preGoogleMap('"+ getName() + "', '" + doc.getId() + "', '" + doc.getApplicationid() + "');");
				html.append("\" />");
				html.append("</td>");
				html.append("<td style='border:0'>");
				html.append(getDiscript()); // 显示描述
				html.append("</td>");
				html.append("</tr>");
				html.append("</table>");
			}else {
				html.append("<iframe name='googlemap' id='googlemap' style='margin:0px'");
				html.append(" frameborder=0 ");
				html.append(" style='width:100%;height:430 px' ");
				html.append(" src='" + Environment.getInstance().getContextPath());
				html.append("/core/dynaform/form/googlemap/googlemap.jsp?type="+openType+"&fieldID="+getFieldId(doc)+"&applicationid="+doc.getApplicationid()+"&displayType="+displayType );
				html.append("' >");
				html.append("</iframe>");
			}
		}
		return html.toString();
	}
	
	public String toHtml(Document doc, IRunner runner, WebUser webUser) throws Exception {
		StringBuffer html = new StringBuffer();
		Item item = null;
		int displayType = getDisplayType(doc, runner, webUser);

		if (displayType == PermissionType.HIDDEN) {
			return this.getHiddenValue();
		} else {
			if (doc != null) {
				item = doc.findItem(this.getName());
				boolean isnew = true;
				if (item != null && item.getValue() != null) {
					isnew = false;
				}
				
				if(openType=="dialog"||openType.equals("dialog")){
					html.append("<input type='hidden'");
					html.append(" id='" + getFieldId(doc) + "'");
					html.append(" name='" + getName() + "'");
					html.append(" fieldType='" + getTagName() + "'");
					if (!isnew) {
						html.append(" value='" + item.getValue() + "'");
					}
					html.append("/>");
					html.append("<table style='width:100%;height:100%;margin:0px;'>");
					html.append("<tr>");
					html.append("<td style='border:0'>");
	
					if (displayType == PermissionType.MODIFY) {
						html.append("<input type='button' value='{*[map]*}' name='btnSelectDept'");
						html.append(" onClick=\"");
						html.append("FormGoogleMap('"+getFieldId(doc)+ "','"+doc.getApplicationid()+"');");
						html.append("\" />");
					}
					html.append("</td>");
					html.append("<td style='border:0'>");
					html.append(getDiscript()); // 显示描述
					html.append("</td>");
					html.append("</tr>");
					html.append("</table>");
				}else {
					html.append("<input type='hidden'");
					html.append(" id='" + getFieldId(doc) + "'");
					html.append(" name='" + getName() + "'");
					html.append(" fieldType='" + getTagName() + "'");
					if (!isnew) {
						html.append(" value='" + item.getValue() + "'");
					}
					html.append("/>");
					html.append("<iframe name='googlemap' id='googlemap' style='margin:0px;width:100%;height:550px;'");
					html.append(" frameborder=0 ");
					//html.append(" style='' ");
					html.append(" src='" + Environment.getInstance().getContextPath());
					html.append("/portal/share/googlemap/form/googlemap.jsp?type="+openType+"&fieldID="+getFieldId(doc)+"&applicationid="+doc.getApplicationid()+"&displayType="+displayType );
					html.append("' >");
					html.append("</iframe>");
				}
			}
		}
		return html.toString();
	}

	/**
	 * 打印输出HTML
	 */
	public String toPrintHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		StringBuffer html = new StringBuffer();

		if (doc != null) {
			int displayType = getPrintDisplayType(doc, runner, webUser);

			if (displayType == PermissionType.HIDDEN) {
				return this.getPrintHiddenValue();
			}
			Item item = doc.findItem(this.getName());
			html.append(item.getValue());
		}
		return html.toString();
	}
	
}
