package OLink.bpm.core.dynaform.form.ejb;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.dynaform.PermissionType;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.document.ejb.Item;
import OLink.bpm.core.macro.runner.AbstractRunner;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.user.action.WebUser;
import eWAP.core.Tools;

public class HandwritingField extends FormField {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2563991874348348544L;

	/**
	 * @roseuid 41ECB66E012A
	 */
	public HandwritingField() {

	}

	public ValidateMessage validate(IRunner runner, Document doc) throws Exception {
		return null;
	}

	/**
	 * @roseuid 41ECB66E0152
	 */
	public void store() {

	}

	/**
	 * 
	 * Form模版的Handwringting组件内容结合Document中的ITEM存放的值,返回重定义后的html，
	 * 
	 * @param doc
	 *            (Document)文档对象
	 * @param runner
	 *            动态语言执行脚本
	 * @param webUser
	 *            webUser
	 * @see ParamsTable#params
	 * @see AbstractRunner#run(String, String)
	 * @return 重定义后的html
	 * 
	 */
	public String toHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		StringBuffer html = new StringBuffer();
		Item item = null;
		int displayType = getDisplayType(doc, runner, webUser);

		if (displayType == PermissionType.HIDDEN) {
			return toHiddenHtml(doc);
		} else {
			if (doc != null) {
				item = doc.findItem(this.getName());

				if (displayType == PermissionType.MODIFY) {
					boolean isnew = false;
					if (item == null) {
						item = new Item();
						try {
							item.setId(Tools.getSequence());
						} catch (Exception e) {
						}
						isnew = true;
					}

					html.append("<script language='JavaScript'>");
					html.append("var beforesave" + item.getId() + "=new Function(");
					html.append("\"var iframe" + item.getId() + "=document.all.handwritingEditor" + item.getId() + ";");
					html.append("iframe" + item.getId() + ".Document.all.btnsave.onclick();");
					html.append("\");");
					html.append("addfunction(beforesave_functions, beforesave" + item.getId() + ");");
					html.append("</script>");
					String src = "";
					if (doc.is_new() || isnew) {
						src = "/dynaform/document/newitem.do?id=" + item.getId() + "&docid=" + doc.getId()
								+ "&ISNEW=true&ISEDIT=TRUE&name=" + item.getName() + "&_type=handwriting";
					} else {
						src = "/dynaform/document/edititem.do?id=" + item.getId() + "&docid=" + doc.getId()
								+ "&ISNEW=false&ISEDIT=TRUE&name=" + item.getName() + "&_type=handwriting";
					}

					if (item != null) {
						html.append("<IFRAME ID='handwritingEditor" + item.getId() + "' src='" + src
								+ "' frameborder='0' scrolling='no' width='100%' height='100%'></IFRAME>");
					}
				} else {
					if (item != null) {
						String src = "/dynaform/document/edititem.do?id=" + item.getId() + "&docid=" + doc.getId()
								+ "&ISNEW=false&ISEDIT=FALSE&name=" + item.getName() + "&_type=handwriting";
						html.append("<IFRAME ID='handwritingEditor" + item.getId() + "' src='" + src
								+ "' frameborder='0' scrolling='no' width='100%' height='100%'></IFRAME>");
					}
				}
			}
		}
		return html.toString();
	}

	/**
	 * 返回描述模板
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
	 * 空实现
	 */
	public void recalculate(IRunner runner, Document doc, WebUser webUser) throws Exception {
	}

	/**
	 * 空实现
	 */
	public Object runValueScript(IRunner runner, Document doc) throws Exception {
		return null;
	}

	/**
	 * 
	 * Form模版的Handwringting组件内容结合Document中的ITEM存放的值,返回重定义后的打印html文本
	 * 
	 * @param doc
	 *            Document(文档对象)
	 * @param runner
	 *            动态语言执行脚本
	 * @param params
	 *            参数
	 * @param user
	 *            webuser
	 * 
	 * @see AbstractRunner#run(String, String)
	 * @return Form模版的Handwringting组件内容结合Document中的ITEM存放的值为重定义后的打印html
	 * @throws Exception
	 */
	public String toPrintHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		return toHtmlTxt(doc, runner, webUser);
	}

	/**
	 * 空实现
	 */
	public String toMbXMLText(Document doc, IRunner runner, WebUser webUser) throws Exception {
		return null;
	}

}
/*
 * FormField InputField.init(java.lang.String){ return null; }
 */
