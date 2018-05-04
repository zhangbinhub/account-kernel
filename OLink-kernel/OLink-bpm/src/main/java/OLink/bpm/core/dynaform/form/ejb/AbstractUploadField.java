package OLink.bpm.core.dynaform.form.ejb;

import OLink.bpm.core.macro.runner.AbstractRunner;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.dynaform.PermissionType;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.document.ejb.Item;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.StringUtil;

public abstract class AbstractUploadField extends FormField implements ValueStoreField {

	private static final long serialVersionUID = -9107366526757917068L;

	protected String limitsize;
	
	protected String openType;

	protected String limitType = "";

	protected String fileCatalog;

	protected String filePattern;
	
	protected String limitNumber;//一次上传限制上传数量

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

	public String toMbXMLText(Document doc, IRunner runner, WebUser webUser) throws Exception {
		return null;
	}

	public String toTemplate() {
		return null;
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
					html.append("<input  type='button'  value='{*[Upload]*}' name='btnSelectDept'");
					// html.append(" disabled='disabled'");
					html.append(" onClick=\"");
					int maxsize = !StringUtil.isBlank(getLimitsize()) ? Integer.valueOf(getLimitsize()).intValue() * 1024 : 10485760;

					String callbakFunction = "function(){" + getRefreshUploadListFunction(getFieldId(doc), uploadList,false,super.refreshOnChanged,doc.getApplicationid(),this.getOpenType()) + ";}";

					html.append("uploadFrontFile('{*[File]*}{*[Upload]*}', '" + path + "','" + getFieldId(doc) + "','_viewid','" + getLimitType() + "',"
							+ maxsize + ", '" + fileSaveMode + "', " + callbakFunction + ", '" + doc.getApplicationid() + "', '" + limitNumber + "');");
					html.append("\">");

					/*
					 * html.append("<img
					 * src='").append(getContextPath(doc)).append(
					 * "/resource/image/search.gif'></button>");
					 */
					html.append("</input>");
					// 删除
					html.append("<input type='button' name='btnDelete'  value='{*[Delete]*}'");
					html.append(" onClick=\"deleteFrontFile(document.getElementById('" + getFieldId(doc) + "'), '" + uploadList+ "','"+doc.getApplicationid()+"')\"");
					html.append("</input>");

				}
				/*
				 * html.append("<img
				 * src='").append(getContextPath(doc)).append(
				 * "/resource/image/search.gif'></button>");
				 */

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
					html.append(addScript(getFieldId(doc), uploadList,displayType != PermissionType.MODIFY,super.refreshOnChanged,doc.getApplicationid(),this.getOpenType()));
				}
			}
		}
		return html.toString();
	}

	public abstract String toPrintHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception;

	protected abstract String getRefreshUploadListFunction(String fieldId, String uploadList,boolean readonly,boolean refresh,String applicationid,String opentype);

	protected abstract String addScript(String id, String uploadList,boolean readonly,boolean refresh,String applicationid,String opentype);

	/**
	 * 获取上传文件的大小
	 * 
	 * @return 上传文件的大小
	 */
	public String getLimitsize() {
		return limitsize;
	}

	/**
	 * 设置上传文件的大小
	 * 
	 * @param limitsize
	 *            上传文件的大小
	 */
	public void setLimitsize(String limitsize) {
		this.limitsize = limitsize;
	}

	/**
	 * 获取上传文件大小的单位(b,kb,m)
	 * 
	 * @return 上传文件大小的单位
	 */
	public String getLimitType() {
		return limitType;
	}

	/**
	 * 设置上传文件大小的单位
	 * 
	 * @param limitType
	 */

	public void setLimitType(String limitType) {
		this.limitType = limitType;
	}

	public String getOpenType() {
		return openType;
	}

	public void setOpenType(String openType) {
		this.openType = openType;
	}

	public String getLimitNumber() {
		return limitNumber;
	}

	public void setLimitNumber(String limitNumber) {
		this.limitNumber = limitNumber;
	}
}
