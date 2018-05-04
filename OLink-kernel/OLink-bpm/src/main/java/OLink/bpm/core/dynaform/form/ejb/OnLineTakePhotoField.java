package OLink.bpm.core.dynaform.form.ejb;

import java.io.File;

import OLink.bpm.constans.Environment;
import OLink.bpm.core.dynaform.PermissionType;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.document.ejb.Item;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.HtmlEncoder;
import OLink.bpm.util.StringUtil;

public class OnLineTakePhotoField extends FormField implements ValueStoreField {

	private static final long serialVersionUID = -8363899875473204943L;

	private Environment env = Environment.getInstance();

	/**
	 * 图片的高
	 */
	protected String imgh;
	/**
	 * 图片的宽
	 */
	protected String imgw;

	/**
	 * 空实现
	 */
	public String toMbXMLText(Document doc, IRunner runner, WebUser webUser)
			throws Exception {
		return null;
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
	 * 以网格结合Document,返回的字符串为重定义后的html
	 */
	public String toGridHtmlText(Document doc, IRunner runner, WebUser webUser)
			throws Exception {
		return toHtmlTxt(doc, runner, webUser);
	}

	/**
	 * 根据onlinetakephotoField的显示类型不同,返回的结果字符串不同.
	 * 新建的Document,onlinetakephotoField的显示类型为默认的MODIFY
	 * 。此时根据Form模版的onlinetakephotoField内容结合Document,返回的字符串为重定义后的html.
	 * 若根据流程节点设置对应onlinetakephotoField的显示类型不同,(默认为MODIFY),返回的结果字符串不同.
	 * 1)若节点设置对应onlinetakephotoField的显示类型为隐藏HIDDEN（值为3），返回 “******”字符串。
	 * 2)若节点设置对应onlinetakephotoField显示类型为MODIFY
	 * 内容结合Document中的ITEM存放的值,返回字符串为重定义后的html。
	 * 
	 */
	public String toHtmlTxt(Document doc, IRunner runner, WebUser webUser)
			throws Exception {
		StringBuffer html = new StringBuffer();
		int displayType = getDisplayType(doc, runner, webUser);
		Item item = null;
		if (displayType == PermissionType.HIDDEN) {
			return this.getHiddenValue();
		} else {
			if (doc != null) {
				item = doc.findItem(this.getName());
				boolean isnew = true;
				if (item != null && item.getValue() != null) {
					isnew = false;
				}
				html.append("<table style='border:0'>");
				html.append("<tr>");
				html.append("<td style='border:0'>");
				html.append("<img alt='拍照图片' border=0 src='"
						+ env.getContextPath() + getFieldValue(doc) + "' id='"
						+ getFieldId(doc) + "_img' width='" + imgw
						+ "' height='" + imgh + "' />");
				html.append("</td>");

				if (displayType == PermissionType.MODIFY) {
					html.append("<td style='border:0;valign:botton'>");
					html.append("<input type='hidden'");
					html.append(" id='" + getFieldId(doc) + "'");
					html.append(" name='" + getName() + "'");
					html.append(" fieldType='" + getTagName() + "'");
					if (!isnew) {
						html.append(" value='" + getFieldValue(doc) + "'");
					}
					html.append("/>");
					html.append("<input type='button'");
					html.append("onclick=\"onlinetakephoto('" + getFieldId(doc)
							+ "')\"");
					html.append("class='button_searchdel4' />");
				}
				html.append("</td>");
				html.append("<td style='border:0'>");
				html.append(getDiscript()); // 显示描述
				html.append("</td>");
				html.append("</tr>");
				html.append("</table>");
			}
		}
		return html.toString();
	}

	/**
	 * 
	 * Form模版的(onlinetakephotoField)内容结合Document中的ITEM存放的值,返回字符串为重定义后的打印html文本
	 * 
	 * @param doc
	 *            Document
	 * @param runner
	 *            动态脚本执行器
	 * @param params
	 *            参数
	 * @param user
	 *            webuser
	 * @throws Exception
	 */
	public String toPrintHtmlTxt(Document doc, IRunner runner, WebUser webUser)
			throws Exception {
		StringBuffer html = new StringBuffer();

		if (doc != null) {
			int displayType = getPrintDisplayType(doc, runner, webUser);

			if (displayType == PermissionType.HIDDEN) {
				return this.getPrintHiddenValue();
			}

			Item item = doc.findItem(this.getName());
			String value = "";

			if (item != null && item.getValue() != null) {
				value = (String) item.getValue();
				Environment env = Environment.getInstance();
				String filePath = env.getRealPath(value);
				File file = new File(filePath);
				if (!file.exists()) {
					value = "/resource/image/photo.bmp";
				}
			} else {
				value = "/resource/image/photo.bmp";
			}

			html.append("<SPAN style=\"FONT-SIZE: 9pt\">");
			html.append("<img border='0' width='" + imgw + "' height='" + imgh
					+ "' src='" + env.getContextPath() + value + "' />");
			html.append("</SPAN>");
		}

		return html.toString();
	}

	/**
	 * Form模版的在线拍照组件内容结合Document中的ITEM存放的值,返回字符串为重定义后的以PDF的形式输出
	 * 
	 * @param doc
	 *            Document(文档对象)
	 * @param runner
	 *            动态脚本执行器
	 * @param webUser
	 *            webUser
	 * @return PDF的格式的HTML的文本
	 */
	public String toPdfHtmlTxt(Document doc, IRunner runner, WebUser webUser)
			throws Exception {
		StringBuffer html = new StringBuffer();

		if (doc != null) {
			int displayType = getPrintDisplayType(doc, runner, webUser);

			if (displayType == PermissionType.HIDDEN) {
				return this.getPrintHiddenValue();
			}

			Item item = doc.findItem(this.getName());
			// String fileFullName = doc.getItemValueAsString(getName());
			String value = "";
			String url = "";
			if (item != null && item.getValue() != null) {
				value = (String) item.getValue();
				String webPath = value;
				url = doc.get_params().getContextPath() + webPath;
				/*
				 * String filePath = env.getRealPath(webPath); File file = new
				 * File(filePath); String src = file.exists() ? filePath : "";
				 */
			} else {
				url = doc.get_params().getContextPath()
						+ "/resource/image/photo.bmp";
			}
			String baseUrl = Environment.getInstance().getBaseUrl();
			if (baseUrl != null && baseUrl.endsWith("/")) {
				baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
			}
			url = baseUrl + url;
			html.append("<img border='0' width='" + imgw + "' height='" + imgh
					+ "' src='" + url + "' />");
		}

		return html.toString();
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
	public String getText(Document doc, IRunner runner, WebUser webUser)
			throws Exception {
		String fileFullName = getFieldValue(doc);
		if (fileFullName != null) {
			String webPath = fileFullName;
			String fileName = fileFullName.substring(fileFullName
					.lastIndexOf("/") + 1, fileFullName.length());
			String url = doc.get_params().getContextPath() + webPath;
			String image = "<a href=\"javaScript:viewDoc('" + doc.getId()
					+ "','" + doc.getFormid() + "')\"><img alt='" + fileName
					+ "' border='0' src='" + url + "' width='" + imgw
					+ "' height='" + imgh + "' /></a>";
			image += "<a href='" + url + "' target='bank'><img alt='"
					+ fileName + "' border='0' src='"
					+ doc.get_params().getContextPath()
					+ "/resource/images/picture_go.png' /></a>";
			return image;
		}

		return super.getText(doc, runner, webUser);
	}

	/**
	 * 获取表单域真实值
	 * 
	 * @param doc
	 * @return
	 */
	protected String getFieldValue(Document doc) {
		String rtn = "";
		if (doc != null) {
			Item item = doc.findItem(getName());
			// 文本类型取值
			if (item != null && item.getValue() != null) {
				Object value = item.getValue();
				if (StringUtil.isBlank((String) value)) {
					rtn = "/resource/image/photo.bmp";
				} else {
					String valueStr = HtmlEncoder.encode(value + "");
					valueStr = valueStr != null && !valueStr.equals("null") ? valueStr
							: "";
					rtn = valueStr;
				}
			} else {
				rtn = "/resource/image/photo.bmp";
			}
		}
		return rtn;
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

}
