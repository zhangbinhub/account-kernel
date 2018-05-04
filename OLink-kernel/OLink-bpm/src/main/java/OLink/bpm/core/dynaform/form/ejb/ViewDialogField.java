package OLink.bpm.core.dynaform.form.ejb;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.dynaform.PermissionType;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.dynaform.view.ejb.ViewProcess;
import OLink.bpm.core.macro.runner.AbstractRunner;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.table.constants.MobileConstant;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.dynaform.document.ejb.Document;

/**
 * @author nicholas
 */
public class ViewDialogField extends FormField {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8372638611962536921L;
	/**
	 * @uml.property name="openType"
	 */
	protected String openType;
	/**
	 * @uml.property name="dialogView"
	 */
	protected String dialogView = "";

	/**
	 * 确定脚本
	 */
	protected String okScript = "";

	/**
	 * @uml.property name="mapping"
	 */
	protected String mapping = "";
	
	/**
	 * @uml.property name="divWidth"
	 */
	protected String divWidth = "";
	
	/**
	 * @uml.property name="divHeight"
	 */
	protected String divHeight = "";
	
	public String getDivWidth() {
		return divWidth;
	}

	public void setDivWidth(String divWidth) {
		this.divWidth = divWidth;
	}

	public String getDivHeight() {
		return divHeight;
	}

	public void setDivHeight(String divHeight) {
		this.divHeight = divHeight;
	}

	public boolean isMaximization() {
		return maximization;
	}

	public void setMaximization(boolean maximization) {
		this.maximization = maximization;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @uml.property name="mutilSelect"
	 */
	protected boolean mutilSelect;
	
	/**
	 * @uml.property name="selectOne"
	 */
	protected boolean selectOne;
	
	/**
	 * @uml.property name="maximization"
	 */
	protected boolean maximization;

	/**
	 * @uml.property name="allowViewDoc"
	 */
	protected boolean allowViewDoc;

	/**
	 * @uml.property name="caption"
	 */
	protected String caption = "";

	/**
	 * 是否允许显示文档
	 * 
	 * @return 是否允许显示文档
	 * @uml.property name="allowViewDoc"
	 */
	public boolean isAllowViewDoc() {
		return allowViewDoc;
	}

	/**
	 * 设置是否允许显示文档
	 * 
	 * @param allowViewDoc
	 *            是否允许显示文档
	 * @uml.property name="allowViewDoc"
	 */
	public void setAllowViewDoc(boolean allowViewDoc) {
		this.allowViewDoc = allowViewDoc;
	}

	/**
	 * 获取前台定制的关联属性,(视图列的数据应该赋值给页面元素的某个属性关联以冒号分开)
	 * 
	 * @return 冒号分开组成的关联字符串
	 * @uml.property name="mapping"
	 */
	public String getMapping() {
		return mapping;
	}

	/**
	 * 设置关联属性
	 * 
	 * @param mapping
	 *            关联属性
	 * @uml.property name="mapping"
	 */
	public void setMapping(String mapping) {
		this.mapping = mapping;
	}

	/**
	 * 获取所引用的视图
	 * 
	 * @return 引用的视图
	 * @uml.property name="dialogView"
	 */
	public String getDialogView() {
		return dialogView;
	}

	/**
	 * 设置引用的视图
	 * 
	 * @param dialogView
	 *            引用的视图
	 * @uml.property name="dialogView"
	 */
	public void setDialogView(String dialogView) {
		this.dialogView = dialogView;
	}

	/**
	 * 获取模板描述视图对话框
	 * 
	 * @return 模板字符串描述
	 * @roseuid 41E7917A033F
	 */
	public String toTemplate() {
		StringBuffer template = new StringBuffer();
		template.append("<span");
		template.append(" className='" + this.getClass().getName() + "'");
		template.append(" id='" + getId() + "'");
		template.append(" name='" + getName() + "'");
		template.append(" formid='" + getFormid() + "'");
		template.append(" hiddenScript='" + getHiddenScript() + "'");
		template.append(" hiddenPrintScript='" + getHiddenPrintScript() + "'");
		template.append(" refreshOnChanged='" + isRefreshOnChanged() + "'");
		template.append(" validateRule='" + getValidateRule() + "'");
		template.append(" valueScript='" + getValueScript() + "'");
		template.append(" openType='" + getOpenType() + "'");
		template.append(" okScript='" + getOkScript() + "'");
		template.append("/>");
		return template.toString();
	}

	/**
	 * 
	 * Form模版的ViewDialogField组件内容结合Document,返回重定义后的html
	 * 重定义后的html是根据流程节点设置对应ViewDialogField的显示类型不同(默认为MODIFY)来设置ViewDialogField的相应属性(DISABLED,READONLY).
	 * 
	 * @param doc
	 *            文档对象
	 * @see ParamsTable#params
	 * @see AbstractRunner#run(String, String)
	 * @return 重定义后的html为Form模版的ViewDialogField组件内容结合Document中的ITEM存放的值,
	 * 
	 */
	public String toHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		StringBuffer html = new StringBuffer();
		int displayType = getDisplayType(doc, runner, webUser);
		if (getDialogView() != null && getDialogView().trim().length() > 0) {
			switch (displayType) {
			case PermissionType.HIDDEN:
				html.append(this.getHiddenValue());
				break;
			case PermissionType.MODIFY:
				if(WebUser.TYPE_BG_USER.equals(webUser.getType()))
					html.append(getPreDialogButton(doc, true, false));
				else
					html.append(getDialogButton(doc, true, false));
				break;
			case PermissionType.READONLY:
				if(WebUser.TYPE_BG_USER.equals(webUser.getType()))
					html.append(getPreDialogButton(doc, false, false));
				else
					html.append(getDialogButton(doc, false, false));
				break;
			case PermissionType.DISABLED:
				if(WebUser.TYPE_BG_USER.equals(webUser.getType()))
					html.append(getPreDialogButton(doc, false, true));
				else
					html.append(getDialogButton(doc, false, true));
				break;
			default:
				break;
			}
		}
		return html.toString();
	}

	/**
	 * 设置视图框的显示中的触发按钮,使点击按钮会出现视图框
	 * 
	 * @param isEditAble
	 *            是否能编辑(true: 可编辑 false: 不可编辑)
	 * @param isDisable
	 *            是否可见(true: 可见 false: 不可见)
	 * @return 重定义后的HTML
	 */
	public String getDialogButton(Document doc, boolean isEditAble, boolean isDisable) {
		StringBuffer html = new StringBuffer();
		String parameters = "param" + getName();
		html.append("<input type='button' onclick='ViewHelper.convertValuesMapToPage(" + "dy_getValuesMap(),function(text){");
		html.append("var " + parameters + "= {");
		html.append("allow:" + isAllowViewDoc() + ",");
		html.append("mutil:" + isMutilSelect() + ",");
		html.append("selectOne:" + isSelectOne() + ",");
		html.append("parentid:\"" + doc.getId() + "\",");
		html.append("className:\"" + getClass().getName() + "\",");

		html.append("formid:\"" + getFormid() + "\",");
		html.append("fieldid:\"" + this.getId() + "\",");
		if (isEditAble) {
			html.append("isEdit:" + true);
		} else {
			html.append("isEdit:" + false);
		}

		html.append("};");

		html.append("if(showViewDialogField(\"{*[ViewDialog]*}\", text,");
		html.append("\"" + getDialogView() + "\",");
		html.append("\"" + getMapping() + "\",");
		html.append(parameters + ",");
		html.append(getOpenType() + ",");
		try {
			ViewProcess viewProcess = (ViewProcess) ProcessFactory.createProcess(ViewProcess.class);
			View view = (View) viewProcess.doView(getDialogView());
			html.append(view.getViewType());
		} catch (Exception e) {
			e.printStackTrace();
		}
		html.append(",\"" + getDivWidth());
		html.append("\",\"" + getDivHeight());
		html.append("\"," + isMaximization());
		html.append("))");
		
		html.append("{" + (isRefreshOnChanged() ? "dy_refresh(\"" + getName() + "\")" : "") + "}");
		html.append("}");
		html.append(")'");

		if (getCaption() == null || getCaption().trim().length() <= 0) {
			html.append(" value='...'");
		} else {
			html.append(" value='" + getCaption() + "'");
		}
		if (isDisable) {
			html.append(" disabled");
		}
		html.append(" />");

		return html.toString();
	}
	
	public String getPreDialogButton(Document doc, boolean isEditAble, boolean isDisable) {
		StringBuffer html = new StringBuffer();
		String parameters = "param" + getName();
		html.append("<input type='button' onclick='ViewHelper.convertValuesMapToPage(" + "dy_getValuesMap(),function(text){");
		html.append("var " + parameters + "= {");
		html.append("allow:" + isAllowViewDoc() + ",");
		html.append("mutil:" + isMutilSelect() + ",");
		html.append("selectOne:" + isSelectOne() + ",");
		html.append("parentid:\"" + doc.getId() + "\",");
		html.append("className:\"" + getClass().getName() + "\",");

		html.append("formid:\"" + getFormid() + "\",");
		html.append("fieldid:\"" + this.getId() + "\",");
		if (isEditAble) {
			html.append("isEdit:" + true);
		} else {
			html.append("isEdit:" + false);
		}

		html.append("};");

		html.append("if(previewDialogField(\"{*[ViewDialog]*}\", text,");
		html.append("\"" + getDialogView() + "\",");
		html.append("\"" + getMapping() + "\",");
		html.append(parameters + ", ");
		try {
			ViewProcess viewProcess = (ViewProcess) ProcessFactory.createProcess(ViewProcess.class);
			View view = (View) viewProcess.doView(getDialogView());
			html.append(view.getViewType());
		} catch (Exception e) {
			e.printStackTrace();
		}
		html.append("))");
		
		html.append("{" + (isRefreshOnChanged() ? "dy_refresh(\"" + getName() + "\")" : "") + "}");
		html.append("}");
		html.append(")'");

		if (getCaption() == null || getCaption().trim().length() <= 0) {
			html.append(" value='...'");
		} else {
			html.append(" value='" + getCaption() + "'");
		}
		if (isDisable) {
			html.append(" disabled");
		}
		html.append(" />");

		return html.toString();
	}

	/**
	 * 是否可以多选
	 * 
	 * @return true or false
	 * @uml.property name="mutilSelect"
	 */
	public boolean isMutilSelect() {
		return mutilSelect;
	}

	/**
	 * 设置 是否可以多选
	 * 
	 * @param mutilSelect
	 * @uml.property name="mutilSelect"
	 */
	public void setMutilSelect(boolean mutilSelect) {
		this.mutilSelect = mutilSelect;
	}

	public String toPrintHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		if (doc != null) {
			int displayType = getPrintDisplayType(doc, runner, webUser);

			if (displayType == PermissionType.HIDDEN) {
				return this.getPrintHiddenValue();
			}
		}
//		return printHiddenElement(doc);
		return "";
	}

	/**
	 * 获取说明文本
	 * 
	 * @return 说明文本
	 * @hibernate.property column="caption"
	 * @uml.property name="caption"
	 */
	public String getCaption() {
		return caption;
	}

	/**
	 * 设置说明文本
	 * 
	 * @param caption
	 *            说明文本
	 * @uml.property name="caption"
	 */
	public void setCaption(String caption) {
		this.caption = caption;
	}

	/**
	 * 用于为手机平台XML串生成
	 * 
	 * @param runner
	 *            动态脚本执行器
	 * @param doc
	 *            文档对象
	 * @param webUser
	 *            webUser
	 * @return 手机平台XML
	 */

	public String toMbXMLText(Document doc, IRunner runner, WebUser webUser) throws Exception {
		StringBuffer html = new StringBuffer();

		int displayType = getDisplayType(doc, runner, webUser);
		if (getDialogView() != null && getDialogView().trim().length() > 0) {
			html.append("<").append(MobileConstant.TAG_DIALOGVIEW).append(" ").append(MobileConstant.ATT_NAME).append(
					" = '" + getName() + "'");
			html.append(" ").append(MobileConstant.ATT_VALUE).append("='");
			if (getCaption() == null || getCaption().trim().length() <= 0) {
				html.append("Select...'");
			} else {
				html.append(getCaption() + "'");
			}
			if (displayType == PermissionType.HIDDEN) {
				html.append(" ").append(MobileConstant.ATT_HIDDEN).append(" = 'true'");
			} else if (displayType == PermissionType.READONLY || displayType == PermissionType.DISABLED) {
				html.append(" ").append(MobileConstant.ATT_READONLY).append(" = 'true'");
			}

			if (isRefreshOnChanged()) {
				html.append(" ").append(MobileConstant.ATT_REFRESH).append(" = 'true'");
			}

			html.append(">");

			html.append("<").append(MobileConstant.TAG_PARAMETER).append(" ").append(MobileConstant.ATT_NAME).append(
					"='_viewid'>" + getDialogView() + "</").append(MobileConstant.TAG_PARAMETER).append(">");
			html.append("<").append(MobileConstant.TAG_PARAMETER).append(" ").append(MobileConstant.ATT_NAME).append(
					"='parentid'>" + doc.getId() + "</").append(MobileConstant.TAG_PARAMETER).append(">");
			html.append("<").append(MobileConstant.TAG_PARAMETER).append(" ").append(MobileConstant.ATT_NAME).append(
					"='_mapStr'>" + getMapping() + "</").append(MobileConstant.TAG_PARAMETER).append(">");

			html.append("</").append(MobileConstant.TAG_DIALOGVIEW).append(">");

			return html.toString();
		}
		return "";
	}

	/**
	 * 获取打开视图框的类型(1:弹出窗口,3:弹出层)
	 * 
	 * @return 打开视图框的类型 (1:弹出窗口,3:弹出层)
	 */
	public String getOpenType() {
		return openType;
	}

	/**
	 * 设置打开视图框的类型
	 * 
	 * @param openType
	 */
	public void setOpenType(String openType) {
		this.openType = openType;
	}

	public String getOkScript() {
		return okScript;
	}

	public void setOkScript(String okScript) {
		this.okScript = okScript;
	}

	public boolean isSelectOne() {
		return selectOne;
	}

	public void setSelectOne(boolean selectOne) {
		this.selectOne = selectOne;
	}
	
	

}
