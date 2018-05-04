// Source file:
// C:\\Java\\workspace\\SmartWeb3\\src\\com\\cyberway\\dynaform\\form\\ejb\\InputField.java

package OLink.bpm.core.dynaform.form.ejb;

import OLink.bpm.constans.Environment;
import OLink.bpm.core.macro.runner.AbstractRunner;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.util.HtmlEncoder;
import OLink.bpm.core.dynaform.PermissionType;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.document.ejb.Item;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.StringUtil;

/**
 * HTMLEditor(html的编辑器的组件)
 * 
 * @author Marky
 * 
 */
public class HTMLEditorField extends FormField implements ValueStoreField {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5462825321461415157L;

	/**
	 * 获取ITEM保存类型
	 * (VALUE_TYPE_VARCHAR,VALUE_TYPE_NUMBER,VALUE_TYPE_DATE,VALUE_TYPE_BLOB,VALUE_TYPE_TEXT,VALUE_TYPE_STRINGLIST,VALUE_TYPE_INCLUDE)
	 * 详细你参考ITEM的常量
	 */
	public String getFieldtype() {
		return Item.VALUE_TYPE_TEXT;
	}

	/**
	 * 构造函数
	 * 
	 * @roseuid 41ECB66E012A
	 */
	public HTMLEditorField() {

	}

	/**
	 * 实现
	 */
	public ValidateMessage validate(IRunner runner, Document doc) throws Exception {
		return null;
	}

	/**
	 * 根据HTEMLEDITORFIELD的显示类型不同,返回的结果字符串不同.
	 * 新建的Document,HTEMLEDITORFIELD的显示类型为默认的MODIFY
	 * 。此时根据Form模版的HTEMLEDITORFIELD内容结合Document的Item的值,返回的字符串为重定义后的html.
	 * 若根据流程节点设置对应HTEMLEDITORFIELD的显示类型不同,返回的结果字符串不同.
	 * 
	 * 1)若节点设置对应HTEMLEDITORFIELD的显示类型为隐藏HIDDEN（值为3），返回 “******”字符串。
	 * 2)若为MODIFY(值为2)时,Form模版的html编辑器组件内容结合Document中的ITEM存放的值,返回重定义后的html。
	 * 否则返回字符串内容为Document中的ITEM存放的值重定义后的html，
	 * 
	 * @param doc
	 *            文档对象
	 * @param runner
	 *            动态语言脚本执行器
	 * @param webUser
	 *            webuser
	 * @see AbstractRunner#run(String, String)
	 * @return 字符串内容为重定义后的html的html编辑器组件标签及语法
	 * 
	 */
	public String toHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		StringBuffer html = new StringBuffer();
		Item item = null;
		int displayType = getDisplayType(doc, runner, webUser);

		if (displayType == PermissionType.HIDDEN) {
			return this.getHiddenValue();
		} else if(displayType == PermissionType.READONLY){
			if(doc !=null){
				item = doc.findItem(this.getName());
				String text = item.getTextvalue();
				text = StringUtil.dencodeHTML(text);
				html.append("<div style='width:100%; height:80%;border: 0px solid black;'>");
				if(text!=null){
					html.append(text);
				}
				html.append("</div>");
				return html.toString();
			}
		}else {
			if (doc != null) {
				item = doc.findItem(this.getName());

				String text = item.getTextvalue();
				String tmp = null;
				if (displayType == PermissionType.MODIFY) {
					html.append("<textarea id='" + getId() + "' name='" + getName() + "' style='display: inline;'>");
					if (text != null) {
						tmp = HtmlEncoder.encode(text);
						html.append(tmp);
					}
					html.append("</textarea>");

					Environment env = Environment.getInstance();
					html.append("<table width='100%'><tr><td>");
					html.append("<script type='text/javascript'>");
					html.append("var oFCKeditor = new FCKeditor('" + getName() + "');");
					html.append("oFCKeditor.BasePath	= '" + env.getContextPath() + "/portal/share/dynaform/form/htmlfield/';");
					html.append("oFCKeditor.Height	= 320;");
					html.append("oFCKeditor.ReplaceTextarea();");
					html.append("</script>");
					html.append("</td></tr></table>");
				} else {
					if (text != null) {
						html.append("<div style='width:100%; height:80%;border: 1px solid black;'>");
						html.append(text);
						html.append("</div>");
					} else {
						html.append("<div style='width:100%; height:80%;border: 1px solid black;'>");
						html.append("</div>");
					}
				}
			}
		}
		return html.toString();
	}

	/**
	 * 
	 * Form模版的html编辑器组件内容结合Document中的ITEM存放的值,返回重定义后的打印html文本.
	 * 
	 * @param doc
	 *            Document(文档对象)
	 * @param runner
	 *            动态语言脚本执行器
	 * @param params
	 *            参数
	 * @param user
	 *            webuser
	 * 
	 * @see AbstractRunner#run(String, String)
	 * @return Form模版的html编辑器组件内容结合Document中的ITEM存放的值为重定义后的打印html
	 * @throws Exception
	 */
	public String toPrintHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		StringBuffer html = new StringBuffer();
		Item item = null;
		int displayType = getPrintDisplayType(doc, runner, webUser);
		if (displayType == PermissionType.HIDDEN) {
			return this.getPrintHiddenValue();
		} else if(displayType == PermissionType.MODIFY){
			if(doc !=null){
				item = doc.findItem(this.getName());
				String text = item.getTextvalue();
				html.append("<div style='width:100%; height:80%;border: 0px solid black;'>");
				if(text!=null){
					html.append(text);
				}
				html.append("</div>");
				return html.toString();
			}
		}
		return html.toString();
	}

	/**
	 * 返回模板描述文本
	 * 
	 * @return java.lang.String
	 * @roseuid 41E7917A033F
	 */
	public String toTemplate() {
		StringBuffer template = new StringBuffer();
		template.append("<input type='text'");
		template.append(" className='" + this.getClass().getName() + "'");
		template.append(" id='" + getId() + "'");
		template.append(" name='" + getName() + "'");
		template.append(" formid='" + getFormid() + "'");
		template.append(" discript='" + getDiscript() + "'");
		template.append(" refreshOnChanged='" + isRefreshOnChanged() + "'");
		template.append(" valueScript='" + getValueScript() + "'");
		template.append(" editMode='" + getEditMode() + "'");
		template.append(" hiddenScript='" + getHiddenScript() + "'");
		template.append(" hiddenPrintScript='" + getHiddenPrintScript() + "'");
		template.append(">");
		return template.toString();
	}

	public void recalculate(IRunner runner, Document doc, WebUser webUser) throws Exception {
		getLog().debug("HTMLEditorField.recalculate");
		runValueScript(runner, doc);
	}

	public String toMbXMLText(Document doc, IRunner runner, WebUser webUser) throws Exception {
		return null;
	}

	public String toGridHtmlText(Document doc, IRunner runner, WebUser webUser) throws Exception {
		return null;
	}

	public String getValueMapScript() {
		StringBuffer scriptBuffer = new StringBuffer();
		scriptBuffer.append("valuesMap['" + this.getName() + "'] = FCKeditorAPI.GetInstance('" + this.getName()
				+ "') ? FCKeditorAPI.GetInstance('" + this.getName() + "').GetXHTML(true) : '';");
		return scriptBuffer.toString();
	}

}
/*
 * FormField InputField.init(java.lang.String){ return null; }
 */
