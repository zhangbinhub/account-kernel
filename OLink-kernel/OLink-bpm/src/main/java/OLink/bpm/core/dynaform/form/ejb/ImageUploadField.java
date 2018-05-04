// Source file:
// C:\\Java\\workspace\\SmartWeb3\\src\\com\\cyberway\\dynaform\\form\\ejb\\InputField.java

package OLink.bpm.core.dynaform.form.ejb;

import java.io.File;

import OLink.bpm.constans.Environment;
import OLink.bpm.core.dynaform.document.ejb.Item;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.dynaform.PermissionType;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.StringUtil;

/**
 * 
 * 上传图片的组件,可支持所有格式的图片文件上传
 */
public class ImageUploadField extends AbstractUploadField {
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
	
	public ImageUploadField() {

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
	 * 添加Script
	 * 
	 * @param attachmentName
	 *            附件名
	 * @param uploadList
	 *            上传列表
	 * 
	 * @return 字符串内容为script形式的字符串
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
	 * @return java.lang.String
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
				return toHiddenHtml(doc);
			}

			Item item = doc.findItem(this.getName());
			String fileFullName = doc.getItemValueAsString(getName());
			String value = "";
			String url = "";
			if (item != null && item.getValue() != null) {
				value = (String) item.getValue();
				int index = value.indexOf("_");
//				value = value.substring(index + 1, value.length());

				String webPath = fileFullName.substring(0, index);
				url = doc.get_params().getContextPath() + webPath;

				Environment env = Environment.getInstance();
				String filePath = env.getRealPath(webPath);
				File file = new File(filePath);
				if(file.exists()){
					html.append("<SPAN style=\"FONT-SIZE: 9pt\">");
					html.append("<img border='0' width='"+imgw+"' height='"+imgh+"' src='" +url + "' />");
					html.append("</SPAN>");
				}else{
					html.append("&nbsp;");
				}
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

			Item item = doc.findItem(this.getName());
			String value = "";
			String url = "";
			if (item != null && item.getValue() != null) {
				value = (String) item.getValue();
				int index = value.indexOf("/");
				if (index != -1) {
					url = doc.get_params().getContextPath() + value;
				}
			}

			Environment env = Environment.getInstance();
			String filePath = env.getRealPath(value);
			File file = new File(filePath);
			if(file.exists()){
				html.append("<SPAN style=\"FONT-SIZE: 9pt\">");
				html.append("<img border='0' width='"+imgw+"' height='"+imgh+"' src='" +url + "' />");
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
//		StringBuffer xmlText = new StringBuffer();
//		int displayType = getDisplayType(doc, runner, webUser);
//
//		if (doc != null) {
//			
//			String src = doc.getItemValueAsString(getName());
//			src = src == null ? "" : src.trim();
//			xmlText.append("<").append(MobileConstant.TAG_IMAGEFIELD);
//			xmlText.append(" ").append(MobileConstant.ATT_SRC).append("='").append(src).append("'");
//			xmlText.append(" ").append(MobileConstant.ATT_WIDTH + "='" + getImgw() + "'");
//			xmlText.append(" ").append(MobileConstant.ATT_HIDDEN+ "='" + getImgh() + "'");
//			xmlText.append(" ").append(MobileConstant.ATT_SIZE).append("='").append(getLimitsize()).append("'");
//			
//			xmlText.append(" ").append(MobileConstant.ATT_ID + "='" + getId() + "'");
//			xmlText.append(" ").append(MobileConstant.ATT_NAME + "='" + getName() + "'");
//			xmlText.append(" ").append(MobileConstant.ATT_LABEL).append("='").append(getName()).append("'");
//			
//			if (displayType == PermissionType.READONLY
//					|| (getTextType() != null && getTextType().equalsIgnoreCase("readonly"))
//					|| displayType == PermissionType.DISABLED) {
//				xmlText.append(" ").append(MobileConstant.ATT_READONLY + "='true' ");
//			}
//			//if (displayType == PermissionType.HIDDEN) {
//			//	xmlText.append(" ").append(MobileConstant.ATT_HIDDEN).append(" ='true' ");
//			//}
//			//if (isRefreshOnChanged()) {
//			//	xmlText.append(" ").append(MobileConstant.ATT_REFRESH).append(" ='true' ");
//			//}
//			xmlText.append(">");
//			xmlText.append(getDiscript());
//			xmlText.append("</").append(MobileConstant.TAG_IMAGEFIELD + ">");
//		}
//		return xmlText.toString();
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
		imgh = !StringUtil.isBlank(this.getImgh()) ? this.getImgh().trim() : "100";
		imgw = !StringUtil.isBlank(this.getImgw()) ? this.getImgw() : "100";
		String fileFullName = doc.getItemValueAsString(getName());
		int index = fileFullName.lastIndexOf("/");
		if (index != -1) {
			String fileName = fileFullName.substring(index + 1, fileFullName.length());
			String url = doc.get_params().getContextPath() + fileFullName;
			String image= "<a href=\"javaScript:viewDoc('"+doc.getId()+"','"+doc.getFormid()+"')\"><img border='0' alt='"+fileName+"' src='" + url + "' width='"+imgw+"' height='"+imgh+"' /></a>";
			image+= "<a href='" + url + "' target='bank'><img alt='"+fileName+"' border='0' src='"+doc.get_params().getContextPath()+"/resource/images/picture_go.png' /></a>";
			return image;

			// return fileName;
		}

		return super.getText(doc, runner, webUser);
	}

	protected String getRefreshUploadListFunction(String fieldId, String uploadList,boolean readonly,boolean refresh,String applicationid,String opentype) {
		String height = !StringUtil.isBlank(this.getImgh()) ? this.getImgh().trim() : "100";
		String width = !StringUtil.isBlank(this.getImgw()) ? this.getImgw() : "100";
		
		return "refreshImgList(document.getElementById('" + fieldId + "').value, '" + uploadList + "', '"+Integer.parseInt(height)+"','"+Integer.parseInt(width)+"',"+readonly+","+refresh+",'"+applicationid+"','"+opentype+"');";
		
	}

	public String getLimitType() {
		return "image";
	}
/**
	 * 获取图片大小的高
	 * 
	 * @return 高的单位（px）
	 */
	
	public String getImgh() {
		return imgh;
	}

	/**
	 * 设置图片大小的高
	 * 
	 * @param imgh
	 */
	public void setImgh(String imgh) {
		this.imgh = imgh;
	}
	/**
	 * 获取图片大小的宽
	 * 
	 * @return 宽的单位（px）
	 */
	public String getImgw() {
		return imgw;
	}

	/**
	 * 设置图片大小的宽
	 * 
	 * @param imgw
	 */
	

	public void setImgw(String imgw) {
		this.imgw = imgw;
	}
	
}