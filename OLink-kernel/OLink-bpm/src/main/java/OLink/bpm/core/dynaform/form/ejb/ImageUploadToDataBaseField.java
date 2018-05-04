// Source file:
// C:\\Java\\workspace\\SmartWeb3\\src\\com\\cyberway\\dynaform\\form\\ejb\\InputField.java

package OLink.bpm.core.dynaform.form.ejb;

import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.constans.Environment;
import OLink.bpm.core.dynaform.PermissionType;
import OLink.bpm.core.macro.runner.AbstractRunner;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.upload.ejb.UploadProcess;
import OLink.bpm.core.upload.ejb.UploadVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.document.ejb.Item;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.StringUtil;

/**
 * 
 * 上传图片的组件,可支持所有格式的图片文件上传
 */
public class ImageUploadToDataBaseField extends FormField implements ValueStoreField{
	private static final long serialVersionUID = 2295552984683189284L;
	//private static String cssClass = "imageupload-cmd";
	protected String imgh;//图片高
	protected String imgw;//图片宽
	protected String limitsize;//限制大小
	protected Environment env = Environment.getInstance();


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
	protected String addScript(String fieldId, String uploadList,boolean readonly,boolean refresh,String applicationid) {
		StringBuffer script = new StringBuffer();
		script.append("<script language='JavaScript'>");
		script.append(getRefreshUploadListFunction(fieldId, uploadList,readonly,refresh,applicationid));
		script.append("</script>");

		return script.toString();
	}

	/**
	 * 返回模板描述
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
		template.append(" hiddenScript='" + getHiddenScript() + "'");
		template.append(" hiddenPrintScript='" + getHiddenPrintScript() + "'");
		template.append(">");
		return template.toString();
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
	 * @see ImageUploadField#toHtmlTxt(Document,
	 *      AbstractRunner, WebUser)
	 * @see AbstractRunner#run(String, String)
	 * @return 字符串内容为重定义后的打印html的图片上传组件标签及语法
	 * @throws Exception
	 */
	public String toGridHtmlText(Document doc, IRunner runner, WebUser webUser)
	throws Exception {
	return toHtmlTxt(doc,runner,webUser);
	}
	
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
				UploadProcess uploadProcess = (UploadProcess) ProcessFactory.createRuntimeProcess(UploadProcess.class,doc.getApplicationid());
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
				
				String uploadList = "uploadToDataBaseList";
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
					html.append("uploadToDataBaseFrontFile('" + getFieldId(doc) + "',"+ maxsize + ", " + callbakFunction + ",'"+doc.getApplicationid()+"');");
					html.append("\">");
					html.append("</input>");
					// 删除
					html.append("<input type='button' name='btnDelete'  value='{*[Delete]*}'");
					html.append(" onClick=\"deleteToDataBaseFile(document.getElementById('" + getFieldId(doc) + "'), '" + uploadList+ "','"+doc.getApplicationid()+"')\"");
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
	 * Form模版的图片上传组件(ImageUploadField)内容结合Document中的ITEM存放的值,返回字符串为重定义后的以PDF的形式输出
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
		StringBuffer html = new StringBuffer();
		imgh = !StringUtil.isBlank(this.getImgh()) ? this.getImgh().trim() : "100";
		imgw = !StringUtil.isBlank(this.getImgw()) ? this.getImgw() : "100";
		if (doc != null) {
			int displayType = getPrintDisplayType(doc, runner, webUser);

			if (displayType == PermissionType.HIDDEN) {
				return this.getPrintHiddenValue();
			}
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
			if (!StringUtil.isBlank(fileFullName)) {
					html.append("<SPAN style=\"FONT-SIZE: 9pt\">");
					html.append("<img border='0' width='"+imgw+"' height='"+imgh+"'src='" +env.getContextPath() + "/ShowImageServlet?type=image&id="+fileFullName.split("_")[0]+"' alt='"+fileFullName.split("_")[1]+"' />");
					html.append("</SPAN>");
				}else{
					html.append("&nbsp;");
				}
		}

		return html.toString();
	}
	
	/**
	 * 打印
	 */
	public String toPrintHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception{
		StringBuffer html = new StringBuffer();
		imgh = !StringUtil.isBlank(this.getImgh()) ? this.getImgh().trim() : "100";
		imgw = !StringUtil.isBlank(this.getImgw()) ? this.getImgw() : "100";
		if (doc != null) {
			int displayType = getPrintDisplayType(doc, runner, webUser);

			if (displayType == PermissionType.HIDDEN) {
				return this.getPrintHiddenValue();
			}
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
			if(!StringUtil.isBlank(fileFullName)){
				html.append("<SPAN style=\"FONT-SIZE: 9pt\">");
				html.append("<img border='0' width='"+imgw+"' height='"+imgh+"' src='" +env.getContextPath() + "/ShowImageServlet?type=image&id="+fileFullName.split("_")[0]+"' alt='"+fileFullName.split("_")[1]+"' />");
				html.append("</SPAN>");
			}else{
				html.append("&nbsp;");
			}
			html.append(printHiddenElement(doc));
		}

		return html.toString();
	}


	public String toMbXMLText(Document doc, IRunner runner, WebUser webUser) throws Exception {

		return "";
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
		imgh = !StringUtil.isBlank(this.getImgh()) ? this.getImgh().trim() : "100";
		imgw = !StringUtil.isBlank(this.getImgw()) ? this.getImgw() : "100";
		String fileFullName = sb.toString();
		if (!StringUtil.isBlank(fileFullName)) {
			String image= "<a href=\"javaScript:viewDoc('"+doc.getId()+"','"+doc.getFormid()+"')\"><img border='0' src='" + env.getContextPath() + "/ShowImageServlet?type=image&id="+fileFullName.split("_")[0]+"' alt='"+fileFullName.split("_")[1]+"' width='"+imgw+"' height='"+imgh+"' /></a>";
			image+= "<a href='" + env.getContextPath() + "/ShowImageServlet?type=image&id="+fileFullName.split("_")[0] + "' rel='lightbox' title='"+ fileFullName.split("_")[1] + "' target='_blank'>"+ fileFullName.split("_")[1] +"</a>";
			return image;
		}
		if (!StringUtil.isBlank(doc.getParentid())) {
			int displayType = getDisplayType(doc, runner, webUser);
			if (displayType == PermissionType.HIDDEN) {
				return this.getHiddenValue();
			}
		}
		return sb.toString();
	}

	protected String getRefreshUploadListFunction(String fieldId, String uploadList,boolean readonly,boolean refresh,String applicationid) {
		String height = !StringUtil.isBlank(this.getImgh()) ? this.getImgh().trim() : "100";
		String width = !StringUtil.isBlank(this.getImgw()) ? this.getImgw() : "100";
		
		return "refreshImgToDataBaseList(document.getElementById('" + fieldId + "').value, '" + uploadList + "',"+Integer.parseInt(height)+","+Integer.parseInt(width)+","+readonly+","+refresh+",'"+applicationid+"');";
		
	}
	
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


}