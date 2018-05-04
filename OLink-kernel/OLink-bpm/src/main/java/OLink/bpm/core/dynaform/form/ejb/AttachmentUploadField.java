// Source file:
// C:\\Java\\workspace\\SmartWeb3\\src\\com\\cyberway\\dynaform\\form\\ejb\\InputField.java

package OLink.bpm.core.dynaform.form.ejb;

import OLink.bpm.core.dynaform.PermissionType;
import OLink.bpm.core.macro.runner.AbstractRunner;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.document.ejb.Item;
import OLink.bpm.core.user.action.WebUser;

public class AttachmentUploadField extends AbstractUploadField {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3744258226928041132L;

	/**
	 * @roseuid 41ECB66E012A
	 */

	public AttachmentUploadField() {

	}

	/**
	 * 空实现
	 */
	public ValidateMessage validate(IRunner runner, Document doc) throws Exception {
		return null;
	}

	public void store() {

	}
	
	

	/**
	 * 
	 * Form模版的附件上传组件内容结合Document,返回重定义后的打印html.
	 * 
	 * @param doc
	 *            Document对象
	 * @param runner
	 *            动态执行脚本器
	 * @param params
	 *            参数
	 * @param user
	 *            webuser
	 * 
	 * @see AbstractRunner#run(String, String)
	 * @return Form模版的附件上传组件内容结合Document为重定义后的打印html
	 * @throws Exception
	 */
	public String toPrintHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		StringBuffer html = new StringBuffer();

		if (doc != null) {
			int displayType = getPrintDisplayType(doc, runner, webUser);

			if (displayType == PermissionType.HIDDEN) {
				return this.getPrintHiddenValue();
			}

			Item item = doc.findItem(this.getName());
			String value = "";
			html.append("<SPAN style=\"FONT-SIZE: 9pt\">");
			if (item != null && item.getValue() != null && !item.getValue().equals("")) {
				value = (String) item.getValue();
				String[] valueArry = value.split(";");
				for(int i=0;i<valueArry.length;i++){
					int index = valueArry[i].lastIndexOf("/");
					html.append(valueArry[i].substring(index + 1, valueArry[i].length())).append(";");
				}
				html.deleteCharAt(html.lastIndexOf(";"));
			}else{
				html.append("&nbsp;");
			}
			html.append("</SPAN>");

		}

		return html.toString();
	}
	
	/**
	 * Form模版的文件上传组件内容结合Document中的ITEM存放的值,返回字符串为重定义后的以PDF的形式输出
	 * 
	 * @param doc
	 *            Document(文档对象)
	 * @param runner
	 *            动态脚本执行器
	 * @param webUser
	 *            webUser
	 * @return PDF的格式的HTML的文本
	 */
	public String toPdfHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		return toPrintHtmlTxt(doc,runner,webUser);
	}

	/**
	 * 添加页面上传按钮的Script
	 * 
	 * @param attachmentName
	 *            附件名
	 * @param uploadList
	 *            上传列表
	 * @return 字符串内容为script
	 */
	protected String addScript(String fieldId, String uploadList,boolean readonly,boolean refresh,String applicationid,String opentype) {
		StringBuffer script = new StringBuffer();
		script.append("<script language='JavaScript'>");
		script.append(getRefreshUploadListFunction(fieldId, uploadList,readonly,refresh,applicationid,opentype));
		script.append("</script>");

		return script.toString();
	}

	/**
	 * 返回模板描述
	 * 
	 * @return 描述
	 * @roseuid 41E7917A033F
	 */
	public String toTemplate() {
		StringBuffer template = new StringBuffer();
		template.append("<span'");
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



	/**
	 * 空实现
	 */
	public String toMbXMLText(Document doc, IRunner runner, WebUser webUser) throws Exception {
		return null;
	}

	/**
	 * 获取上传的附件名,在页面的显示附件名
	 */
	public String getText(Document doc, IRunner runner, WebUser webUser) throws Exception {
		String fileFullName = doc.getItemValueAsString(getName());
		int index = fileFullName.split(";")[0].lastIndexOf("/");
		if (index != -1) {
			String fileName = fileFullName.split(";")[0].substring(index + 1, fileFullName.split(";")[0].length());
			if(fileFullName.split(";").length>1){
				return fileName+"...";
			}else{
				return fileName;
			}
		}
		return super.getText(doc, runner, webUser);
	}

	protected String getRefreshUploadListFunction(String fieldId, String uploadList,boolean readonly,boolean refresh,String applicationid,String opentype) {
		return "refreshUploadList(document.getElementById('" + fieldId + "').value, '" + uploadList + "',"+readonly+","+refresh+",'"+applicationid+"','"+opentype+"')";
	}
}
