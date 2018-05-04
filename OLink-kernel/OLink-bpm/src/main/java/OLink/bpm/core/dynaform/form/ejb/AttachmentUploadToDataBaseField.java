package OLink.bpm.core.dynaform.form.ejb;

import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.constans.Environment;
import OLink.bpm.core.dynaform.PermissionType;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.upload.ejb.UploadProcess;
import OLink.bpm.core.upload.ejb.UploadVO;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.dynaform.document.ejb.Item;
import OLink.bpm.util.StringUtil;

public class AttachmentUploadToDataBaseField extends FormField implements ValueStoreField{

	private static final long serialVersionUID = -482924526686699415L;
	protected String limitsize;//限制大小
	protected String limitNumber;//一次上传限制上传数量
	protected Environment env = Environment.getInstance();
	
	@Override
	public String toMbXMLText(Document doc, IRunner runner, WebUser webUser)
			throws Exception {
		return null;
	}

	/**
	 * 返回模板描述
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

	public String toGridHtmlText(Document doc, IRunner runner, WebUser webUser)
			throws Exception {
		return toHtmlTxt(doc,runner,webUser);
	}

	/**
	 * 输出HTML
	 */
	public String toHtmlTxt(Document doc, IRunner runner, WebUser webUser)
			throws Exception {
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
				//从t_upload表中查找出field的文件
				StringBuffer sb = new StringBuffer();
				UploadProcess uploadProcess = (UploadProcess) ProcessFactory.createRuntimeProcess(UploadProcess.class, doc.getApplicationid());
				Collection<UploadVO> datas = uploadProcess.findByColumnName("FIELDID",getFieldId(doc));
				if(datas.size()>0){
					for (Iterator<UploadVO> ite = datas.iterator(); ite.hasNext();) {
						UploadVO uploadVO = ite.next();
						sb.append(uploadVO.getId()+"_"+uploadVO.getName());
						sb.append(";");
					}
					if(sb.lastIndexOf(";")!=-1){
						sb.deleteCharAt(sb.lastIndexOf(";"));
					}
				}
				String uploadList = "uploadFileToDataBaseList";
				uploadList = uploadList + "_" + getFieldId(doc);
				html.append("<table>");
				html.append("<tr>");
				html.append("<td style='border:0'>");
				html.append("<input type='hidden'");
				html.append(" id='" + getFieldId(doc) + "'");
				html.append(" name='" + getName() + "'");
				html.append(" fieldType='" + getTagName() + "'");
				if (!isnew) {
					html.append(" value='" + sb.toString() + "'");
				}
				html.append("/>");
				if (displayType == PermissionType.MODIFY) {
					html.append("<input  type='button'  value='{*[Upload]*}' name='btnSelectDept'");
					html.append(" onClick=\"");
					int maxsize = !StringUtil.isBlank(getLimitsize()) ? Integer.valueOf(getLimitsize()).intValue() * 1024 : 10485760;
					String callbakFunction = "function(){" + getRefreshUploadListFunction(getFieldId(doc), uploadList,false,super.refreshOnChanged,doc.getApplicationid()) + "}";
					html.append("AttachmentUploadToDataBase('" + getFieldId(doc) + "',"+ maxsize + ", " + callbakFunction + ",'"+doc.getApplicationid()+"','"+limitNumber+"');");
					html.append("\">");
					html.append("</input>");
					// 删除
					html.append("<input type='button' name='btnDelete'  value='{*[Delete]*}'");
					html.append(" onClick=\"deleteAttachmentUploadToDataBase(document.getElementById('" + getFieldId(doc) + "'), '" + uploadList+ "','"+doc.getApplicationid()+"')\"");
					html.append("</input>");

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
					html.append(addScript(getFieldId(doc), uploadList,displayType != PermissionType.MODIFY,super.refreshOnChanged,doc.getApplicationid()));
				}
			}
		}
		return html.toString();
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
	protected String addScript(String fieldId, String uploadList,boolean readonly,boolean refresh,String applicationid) {
		StringBuffer script = new StringBuffer();
		script.append("<script language='JavaScript'>");
		script.append(getRefreshUploadListFunction(fieldId, uploadList,readonly,refresh,applicationid));
		script.append("</script>");

		return script.toString();
	}
	
	protected String getRefreshUploadListFunction(String fieldId, String uploadList,boolean readonly,boolean refresh,String applicationid) {
		return "refreshAttachmentUploadToDataBaseList(document.getElementById('" + fieldId + "').value, '" + uploadList + "',"+readonly+","+refresh+",'"+applicationid+"')";
	}

	/**
	 * 打印
	 */
	public String toPrintHtmlTxt(Document doc, IRunner runner, WebUser webUser)
			throws Exception {
		StringBuffer sb = new StringBuffer();
		if (doc != null) {
			int displayType = getPrintDisplayType(doc, runner, webUser);

			if (displayType == PermissionType.HIDDEN) {
				return this.getPrintHiddenValue();
			}else{
				//从t_upload表中查找出field的文件
				UploadProcess uploadProcess = (UploadProcess)ProcessFactory.createRuntimeProcess(UploadProcess.class,doc.getApplicationid());
				Collection<UploadVO> datas = uploadProcess.findByColumnName("FIELDID",getFieldId(doc));
				if(datas.size()>0){
					for (Iterator<UploadVO> ite = datas.iterator(); ite.hasNext();) {
						UploadVO uploadVO = ite.next();
						sb.append(uploadVO.getName());
						sb.append(";");
					}
					if(sb.lastIndexOf(";")!=-1){
						sb.deleteCharAt(sb.lastIndexOf(";"));
					}
				}
			}
		}
		return sb.toString();
	}
	
	/**
	 * pdf打印
	 */
	public String toPdfHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		StringBuffer sb = new StringBuffer();
		if (doc != null) {
			int displayType = getPrintDisplayType(doc, runner, webUser);

			if (displayType == PermissionType.HIDDEN) {
				return this.getPrintHiddenValue();
			}else{
				//从t_upload表中查找出field的文件
				UploadProcess uploadProcess = (UploadProcess)ProcessFactory.createRuntimeProcess(UploadProcess.class,doc.getApplicationid());
				Collection<UploadVO> datas = uploadProcess.findByColumnName("FIELDID",getFieldId(doc));
				if(datas.size()>0){
					for (Iterator<UploadVO> ite = datas.iterator(); ite.hasNext();) {
						UploadVO uploadVO = ite.next();
						sb.append(uploadVO.getName());
						sb.append(";");
					}
					if(sb.lastIndexOf(";")!=-1){
						sb.deleteCharAt(sb.lastIndexOf(";"));
					}
				}
			}
		}
		return sb.toString();
	}
	
	/**
	 * 获取上传的附件名,在页面的显示附件名
	 */
	public String getText(Document doc, IRunner runner, WebUser webUser) throws Exception {
		//从t_upload表中查找出field的文件
		StringBuffer sb = new StringBuffer();
		UploadProcess uploadProcess = (UploadProcess)ProcessFactory.createRuntimeProcess(UploadProcess.class,doc.getApplicationid());
		Collection<UploadVO> datas = uploadProcess.findByColumnName("FIELDID",getFieldId(doc));
		if(datas.size()>0){
			for (Iterator<UploadVO> ite = datas.iterator(); ite.hasNext();) {
				UploadVO uploadVO = ite.next();
				sb.append(uploadVO.getId()+"_"+uploadVO.getName());
				sb.append(";");
			}
			if(sb.lastIndexOf(";")!=-1){
				sb.deleteCharAt(sb.lastIndexOf(";"));
			}
		}
		String fileFullName = sb.toString();
		int index = fileFullName.split(";")[0].indexOf("_");
		if (index != -1) {
			String fileName = fileFullName.split(";")[0].substring(index + 1, fileFullName.split(";")[0].length());
			if(datas.size()>1){
				return fileName+"...";
			}else{
				return fileName;
			}
		}
		if (!StringUtil.isBlank(doc.getParentid())) {
			int displayType = getDisplayType(doc, runner, webUser);
			if (displayType == PermissionType.HIDDEN) {
				return this.getHiddenValue();
			}
		}
		return sb.toString();
	}

	public String getLimitsize() {
		return limitsize;
	}

	public void setLimitsize(String limitsize) {
		this.limitsize = limitsize;
	}

	public String getLimitNumber() {
		return limitNumber;
	}

	public void setLimitNumber(String limitNumber) {
		this.limitNumber = limitNumber;
	}
}
