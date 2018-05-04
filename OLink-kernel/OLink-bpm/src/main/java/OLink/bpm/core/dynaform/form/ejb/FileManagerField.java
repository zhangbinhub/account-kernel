// Source file:
// C:\\Java\\workspace\\SmartWeb3\\src\\com\\cyberway\\dynaform\\form\\ejb\\InputField.java

package OLink.bpm.core.dynaform.form.ejb;

import java.io.File;

import OLink.bpm.constans.Environment;
import OLink.bpm.core.dynaform.PermissionType;
import OLink.bpm.core.dynaform.document.ejb.Item;
import OLink.bpm.core.macro.runner.AbstractRunner;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.StringUtil;
import OLink.bpm.core.dynaform.document.ejb.Document;

/**
 * 
 * 文件管理组件，可以管理指定目录下的所有文件
 */
public class FileManagerField extends FormField implements ValueStoreField {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2295552984683189284L;

	/**
	 * @roseuid 41ECB66E012A
	 */
	//private static String cssClass = "imageupload-cmd";
	/**
	 * 图片的高
	 */
	protected String imgh;
	/**
	 * 图片的宽
	 */
	protected String imgw;
	/**
	 * 限制大小
	 */
	protected String limitsize;
	/**
	 * 文件类型
	 */
	protected String limitType;
	/**
	 * 根据模式获得保存路径
	 */
	protected String fileCatalog;
	/**
	 * 根据模式获得保存路径
	 */
	protected String filePattern;

	public ValidateMessage validate(IRunner runner, Document doc) throws Exception {
		return null;
	}

	/**
	 * @roseuid 41ECB66E0152
	 */
	public void store() {

	}

	public String toGridHtmlText(Document doc, IRunner runner, WebUser webUser) throws Exception {
		return toHtmlTxt(doc, runner, webUser);
	}

	/**
	 * 根据ImageUploadField的显示类型不同,返回的结果字符串不同.
	 * 新建的Document,ImageUploadField的显示类型为默认的MODIFY
	 * 。此时根据Form模版的ImageUploadField内容结合Document,返回的字符串为重定义后的html.
	 * 若根据流程节点设置对应ImageUploadField的显示类型不同,(默认为MODIFY),返回的结果字符串不同.
	 * 1)若节点设置对应ImageUploadField的显示类型为隐藏HIDDEN（值为3），返回 “******”字符串。
	 * 2)若节点设置对应ImageUploadField显示类型为MODIFY
	 * (值为2)时,Form模版的图片上传组件(ImageUploadField)
	 * 内容结合Document中的ITEM存放的值,返回字符串为重定义后的html。
	 * 通过强化HTML标签及语法，表达附件上传组件(ImapgUploadField)的布局、属性、事件、样式、等。 否则返回空字符串。
	 * 
	 * @param doc
	 *            文档对象
	 * @see AbstractRunner#run(String, String)
	 * @return 字符串内容为重定义后的html的图片上传组件标签及语法
	 * 
	 */
	public String toHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
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

				String path;
				String fileSaveMode = getFilePattern() != null ? getFilePattern() : "00";

				if (!StringUtil.isBlank(fileSaveMode) && fileSaveMode.equals("01")) {
					path = getFileCatalog();
				} else {
					path = "ITEM_PATH";
				}

				String uploadList = "uploadList";

				uploadList = uploadList + "_" + getFieldId(doc);
				html.append("<table>");
				html.append("<tr>");
				html.append("<td style='border:0'>");
				html.append("<input type='hidden'");
				html.append(" id='" + getFieldId(doc) + "'");
				html.append(" name='" + getName() + "'");
				html.append(" fieldType='" + getTagName() + "'");
				if (!isnew) {
					html.append(" value='" + item.getValue() + "'");
				}
				html.append("/>");

				if (displayType == PermissionType.MODIFY) {
					html.append("<input type='button' value='{*[FileManager]*}' name='btnSelectDept'");
					html.append(" onClick=\"");
					int maxsize = !StringUtil.isBlank(getLimitsize()) ? Integer.valueOf(getLimitsize()).intValue() * 1024 : 3145728;

					// 生成回调函数
					String callbakFunction = "function(){";
					if (limitType.equals("file")) {
						callbakFunction += fileGetRefreshUploadListFunction(getFieldId(doc), uploadList,false,super.refreshOnChanged) + ";";
					} else if (limitType.equals("image")) {
						callbakFunction += imageGetRefreshUploadListFunction(getFieldId(doc), uploadList,false,super.refreshOnChanged) + ";";
					}
					callbakFunction += "}";

					html.append("FileManager('" + path + "','" + getFieldId(doc) + "','_viewid','" + getLimitType() + "',"
							+ maxsize + ", '" + fileSaveMode + "', " + callbakFunction + ", '" + doc.getApplicationid() + "');");
					html.append("\" />");
					// 删除
					html.append("<input type='button' value='{*[Delete]*}' name='btnDelete'");
					html.append(" onClick=\"FMdeleteFile(document.getElementById('" + getFieldId(doc) + "'), '" + uploadList
							+ "')\" />");
				}

				html.append("</td>");

				html.append("<td style='border:0'>");
				html.append(getDiscript()); // 显示描述
				html.append("</td>");

				html.append("<td style='border:0'>");
				html.append("<div id='" + uploadList + "'>");
				html.append("</div>");
				html.append("</td>");
				html.append("</tr>");
				html.append("</table>");
				if (!isnew) {
					if (limitType.equals("file")) {
						html.append(fileAddScript(getFieldId(doc), uploadList,displayType != PermissionType.MODIFY,super.refreshOnChanged));
					} else if (limitType.equals("image")) {
						html.append(imageAddScript(getFieldId(doc), uploadList,displayType != PermissionType.MODIFY,super.refreshOnChanged));
					}
				}
			}
		}
		return html.toString();
	}
	
	/**
	 * 获取图片或文件的URL地址，并输出HTML的文本
	 * 
	 * @param doc
	 *            Document(文档对象)
	 * @param runner
	 *            动态脚本执行器
	 * @param webUser
	 *            webUser
	 */
	public String getText(Document doc, IRunner runner, WebUser webUser) throws Exception {
			if (limitType.equals("image")) {
				return imageGetText(doc,runner,webUser);
			} else if (limitType.equals("file")) {
				return fileGetText(doc,runner,webUser);
			}else{
				return null;
			}
	}

	/**
	 * 空实现
	 */
	public String toMbXMLText(Document doc, IRunner runner, WebUser webUser) throws Exception {
		if (limitType.equals("file")) {
			return fileToMbXMLText(doc, runner, webUser);
		} else if (limitType.equals("image")) {
			return imageToMbXMLText(doc, runner, webUser);
		}
		return null;
	}

	/**
	 * 根据类型输出相应的模板
	 */
	public String toTemplate() {
		if (limitType.equals("image")) {
			return imageToTemplate();
		}
		return null;
	}

	/**
	 * 根据类型输出相应的打印html
	 */
	public String toPrintHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		if (limitType.equals("file")) {
			return fileToPrintHtmlTxt(doc, runner, webUser);
		} else if (limitType.equals("image")) {
			return imageToPrintHtmlTxt(doc, runner, webUser);
		}
		return null;
	}
	
	/**
	 * 根据类型输出相应的pdf导出html
	 */
	public String toPdfHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		if (limitType.equals("file")) {
			return fileToPdfHtmlTxt(doc, runner, webUser);
		} else if (limitType.equals("image")) {
			return imageToPdfHtmlTxt(doc, runner, webUser);
		}
		return null;
	}

	// 图片上传区开始
	/**
	 * 添加Script
	 * 
	 * @param attachmentName
	 *            附件名
	 * @param uploadList
	 *            上传列表
	 * 
	 * @return 字符串内容为script形式的字符串
	 */
	protected String imageAddScript(String fieldId, String uploadList,boolean readonly,boolean refresh) {
		StringBuffer script = new StringBuffer();
		script.append("<script language='JavaScript'>");
		script.append("jQuery(document).ready(function(){");
		script.append(imageGetRefreshUploadListFunction(fieldId, uploadList,readonly,refresh));
		script.append("}); ");
		script.append("</script>");

		return script.toString();
	}

	/**
	 * 返回模板描述
	 * 
	 * @return java.lang.String
	 * @roseuid 41E7917A033F
	 */
	public String imageToTemplate() {
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

	public void imageRecalculate(IRunner runner, Document doc, WebUser webUser) throws Exception {
	}

	public Object imageRunValueScript(IRunner runner, Document doc) throws Exception {
		return null;
	}

	/**
	 * 
	 * Form模版的图片上传组件(ImageUploadField)内容结合Document中的ITEM存放的值,返回字符串为重定义后的打印html文本
	 * 
	 * @param doc
	 *            Document
	 * @param runner
	 *            动态脚本执行器
	 * @param params
	 *            参数
	 * @param user
	 *            webuser
	 * 
	 * @see FileManagerField#toHtmlTxt(Document,
	 *      AbstractRunner, WebUser)
	 * @see AbstractRunner#run(String, String)
	 * @return 字符串内容为重定义后的打印html的图片上传组件标签及语法
	 * @throws Exception
	 */
	public String imageToPrintHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		StringBuffer html = new StringBuffer();
		imgh = !StringUtil.isBlank(this.getImgh()) ? this.getImgh().trim() : "100";
		imgw = !StringUtil.isBlank(this.getImgw()) ? this.getImgw() : "100";
		if (doc != null) {
			int displayType = getPrintDisplayType(doc, runner, webUser);

			if (displayType == PermissionType.HIDDEN) {
				return this.getPrintHiddenValue();
			}

			Item item = doc.findItem(this.getName());
			String fileFullName = doc.getItemValueAsString(getName());
			String value = "";
			String url = "";
			String webPath = "";
			
			html.append("<SPAN style=\"FONT-SIZE: 9pt\">");
			
			if (item != null && item.getValue() != null && !fileFullName.equals("") && !item.getValue().equals("")) {
				value = (String) item.getValue();
				String[] strArry = value.split(";");
				for(int i=0;i<strArry.length;i++){
						webPath = strArry[i];
						url = doc.get_params().getContextPath() + webPath;
						Environment env = Environment.getInstance();
						String filePath = env.getRealPath(webPath);
						File file = new File(filePath);
					if(file.exists()){
						html.append("<img border='0' width='"+imgw+"' height='"+imgh+"' src='" +url + "' />");
					}
				}
			}else{
				html.append("&nbsp;");
			}
			html.append("</SPAN>");
		}

		return html.toString();
	}

	/**
	 * Form模版的图片组件内容结合Document中的ITEM存放的值,返回字符串为重定义后的以PDF的形式输出
	 * 
	 * @param doc
	 *            Document(文档对象)
	 * @param runner
	 *            动态脚本执行器
	 * @param webUser
	 *            webUser
	 * @return PDF的格式的HTML的文本
	 */
	public String imageToPdfHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		return imageToPrintHtmlTxt(doc,runner,webUser);
	}

	public String imageToMbXMLText(Document doc, IRunner runner, WebUser webUser) throws Exception {
		return null;
	}

	/**
	 * 获取图片上传的URL的地址，并输出HTML的文本
	 * 
	 * @param doc
	 *            Document(文档对象)
	 * @param runner
	 *            动态脚本执行器
	 * @param webUser
	 *            webUser
	 */
	public String imageGetText(Document doc, IRunner runner, WebUser webUser) throws Exception {
		String fileFullName = doc.getItemValueAsString(getName());
		imgh = !StringUtil.isBlank(this.getImgh()) ? this.getImgh().trim() : "100";
		imgw = !StringUtil.isBlank(this.getImgw()) ? this.getImgw() : "100";
		if (fileFullName!=null) {
			String webPath = fileFullName.split(";")[0];
			String fileName = fileFullName.split(";")[0].substring(fileFullName.split(";")[0].lastIndexOf("/") + 1, fileFullName.split(";")[0].length());
			String url = doc.get_params().getContextPath() + webPath;
			String image = "<a href=\"javaScript:viewDoc('"+doc.getId()+"','"+doc.getFormid()+"')\"><img border='0' alt='"+fileName+"' src='" + url + "' width='"+imgw+"' height='"+imgh+"' /></a>";
			image += "<a href='" + url + "' target='bank'><img alt='"+fileName+"' border='0' src='"+doc.get_params().getContextPath()+"/resource/images/picture_go.png' /></a>";
			return image;

			// return fileName;
		}

		return super.getText(doc, runner, webUser);
	}

	protected String imageGetRefreshUploadListFunction(String fieldId, String uploadList,boolean readonly,boolean refresh) {
		String height = !StringUtil.isBlank(this.getImgh()) ? this.getImgh().trim() : "100";
		String width = !StringUtil.isBlank(this.getImgw()) ? this.getImgw() : "100";

		return "refreshFMImageList(document.getElementById('" + fieldId + "').value, '" + uploadList + "', '"
				+ Integer.parseInt(height) + "','" + Integer.parseInt(width) + "',"+readonly+","+refresh+");";

	}

	// 图片上传区结束

	// 文件上传区开始

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
	public String fileToPrintHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
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
	 * Form模版的文件组件内容结合Document中的ITEM存放的值,返回字符串为重定义后的以PDF的形式输出
	 * 
	 * @param doc
	 *            Document(文档对象)
	 * @param runner
	 *            动态脚本执行器
	 * @param webUser
	 *            webUser
	 * @return PDF的格式的HTML的文本
	 */
	public String fileToPdfHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		return fileToPrintHtmlTxt(doc,runner,webUser);
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
	protected String fileAddScript(String fieldId, String uploadList,boolean readonly,boolean refresh) {
		StringBuffer script = new StringBuffer();
		script.append("<script language='JavaScript'>");
		script.append(fileGetRefreshUploadListFunction(fieldId, uploadList,readonly,refresh));
		script.append("</script>");

		return script.toString();
	}

	/**
	 * 返回模板描述
	 * 
	 * @return 描述
	 * @roseuid 41E7917A033F
	 */
	public String file() {
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
	public void fileRecalculate(IRunner runner, Document doc, WebUser webUser) throws Exception {
	}

	/**
	 * 空实现
	 */
	public Object fileRunValueScript(IRunner runner, Document doc) throws Exception {
		return null;
	}

	/**
	 * 空实现
	 */
	public String fileToMbXMLText(Document doc, IRunner runner, WebUser webUser) throws Exception {
		return null;
	}

	/**
	 * 获取上传的附件名,在页面的显示附件名
	 */
	public String fileGetText(Document doc, IRunner runner, WebUser webUser) throws Exception {
		String fileFullName = doc.getItemValueAsString(getName());
		if (fileFullName!=null) {
			String fileName = fileFullName.split(";")[0].substring(fileFullName.split(";")[0].lastIndexOf("/") + 1, fileFullName.split(";")[0].length());
			if (fileFullName.split(";").length > 1) {
				return fileName + "...";
			} else {
				return fileName;
			}
		}
		return super.getText(doc, runner, webUser);
	}

	protected String fileGetRefreshUploadListFunction(String fieldId, String uploadList,boolean readonly,boolean refresh) {
		return "refreshFMFileList(document.getElementById('" + fieldId + "').value, '" + uploadList + "',"+readonly+","+refresh+")";
	}

	// 文件上传区结束

	public String getImgh() {
		return imgh;
	}

	public void setImgh(String imgh) {
		this.imgh = imgh;
	}

	public String getImgw() {
		return imgw;
	}

	public void setImgw(String imgw) {
		this.imgw = imgw;
	}

	public String getLimitsize() {
		return limitsize;
	}

	public void setLimitsize(String limitsize) {
		this.limitsize = limitsize;
	}

	public String getFileCatalog() {
		return fileCatalog;
	}

	public void setFileCatalog(String fileCatalog) {
		this.fileCatalog = fileCatalog;
	}

	public String getFilePattern() {
		return filePattern;
	}

	public void setFilePattern(String filePattern) {
		this.filePattern = filePattern;
	}

	public void setLimitType(String limitType) {
		this.limitType = limitType;
	}

	public String getLimitType() {
		return limitType;
	}

}