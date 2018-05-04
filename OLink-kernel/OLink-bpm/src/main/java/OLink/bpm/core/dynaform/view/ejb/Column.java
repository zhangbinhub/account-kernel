package OLink.bpm.core.dynaform.view.ejb;

import java.text.DecimalFormat;
import java.util.Iterator;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.dynaform.form.ejb.*;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.macro.util.CurrDocJsUtil;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.StringUtil;
import OLink.bpm.core.permission.ejb.PermissionProcess;
import OLink.bpm.core.dynaform.form.ejb.ValueStoreField;
import org.apache.log4j.Logger;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.dynaform.PermissionType;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.document.ejb.DocumentProcess;
import OLink.bpm.core.dynaform.form.ejb.FormField;
import OLink.bpm.core.dynaform.form.ejb.InputField;
import OLink.bpm.core.dynaform.form.ejb.NullField;
import OLink.bpm.core.privilege.operation.ejb.OperationVO;
import OLink.bpm.core.privilege.res.ejb.ResVO;
import OLink.bpm.util.ProcessFactory;

/**
 * @hibernate.class table="T_COLUMN"
 * 
 * @author marky
 */
public class Column extends ValueObject implements Comparable<Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5375309298623433939L;

	private static final Logger log = Logger.getLogger(Column.class);
	/**
	 * 脚本编辑模式
	 */
	public static final String COLUMN_TYPE_SCRIPT = "COLUMN_TYPE_SCRIPT";
	/**
	 * 视图编辑模式
	 */
	public static final String COLUMN_TYPE_FIELD = "COLUMN_TYPE_FIELD";
	
	/**
	 * 操作列
	 */
	public static final String COLUMN_TYPE_OPERATE = "COLUMN_TYPE_OPERATE";
	
	/**
	 * 图标列
	 */
	public static final String COLUMN_TYPE_LOGO = "COLUMN_TYPE_LOGO";

	/**
	 * 真实值
	 */
	public static final String SHOW_TYPE_VALUE = "00";

	/**
	 * 显示值
	 */
	public static final String SHOW_TYPE_TEXT = "01";
	
	/**
	 * 操作列按钮类型
	 */
	public static final String BUTTON_TYPE_DELETE = "00";
	
	public static final String BUTTON_TYPE_DOFLOW = "01";
	
	public static final String BUTTON_TYPE_TEMPFORM = "03";

	private String id;

	private String name;

	private String width;

	private String valueScript;

	private String hiddenScript;

	private String parentView;

	private String type = COLUMN_TYPE_FIELD;

	private String formid;

	private String fieldName;

	private boolean flowReturnCss; // 需要回退时增加样式

	private String imageName; // 回退的时候在前面的images

	private String fontColor; // 显示的字体

	private int orderno; // 排序

	private boolean sum; // 汇总

	private FormField field;
	
	private String isOrderByField;
	
	private String orderType;
	
	/**
	 * 操作列按钮类型
	 */
	private String buttonType;
	
	/**
	 * 操作列按钮名称
	 */
	private String buttonName;
	
	private String approveLimit;
	
	/**
	 * 列是否可见属性
	 */
	private boolean visible = true;

	/**
	 * 模板表单
	 */
	private String templateForm;
	
	/**
	 * 图标
	 */
	private String icon;

	/*
	 * 字段值显示类型
	 */
	private String showType;

	/**
	 * 映射字段
	 */
	private String mappingField;

	/**
	 * 显示方式的常量
	 */
	public static final String DISPLAY_ALL = "00";
	public static final String DISPLAY_PART = "01";

	/**
	 * 显示方式(默认是显示全部内容)
	 */
	private String displayType = DISPLAY_ALL;

	/**
	 * 显示内容的长度(默认长度为-1,表示显示所有)
	 */
	private String displayLength = "-1";

	/**
	 * 是否显示title(默认显示)
	 */
	private boolean showTitle = true;

	public String getDisplayType() {
		return displayType;
	}

	public void setDisplayType(String displayType) {
		this.displayType = displayType;
	}

	public String getDisplayLength() {
		return displayLength;
	}

	public void setDisplayLength(String displayLength) {
		this.displayLength = displayLength;
	}

	public boolean isShowTitle() {
		return showTitle;
	}

	public void setShowTitle(boolean showTitle) {
		this.showTitle = showTitle;
	}

	/**
	 * 获取排序
	 * 
	 * @hibernate.property column="ORDERNO"
	 * @return 排序号
	 */
	public int getOrderno() {
		return orderno;
	}

	/**
	 * 设置排序
	 * 
	 * @param orderno
	 *            排序
	 */
	public void setOrderno(int orderno) {
		this.orderno = orderno;
	}

	/**
	 * 主键
	 * 
	 * @hibernate.id column="ID" generator-class="assigned"
	 */
	public String getId() {
		return id;
	}

	/**
	 * 设置主键
	 * 
	 * @param id
	 *            主键
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 获取值脚本
	 * 
	 * @hibernate.property column="VALUESCRIPT" type="text"
	 * @return 值脚本
	 */
	public String getValueScript() {
		return valueScript;
	}

	/**
	 * 设置 值脚本
	 * 
	 * @param valueScript
	 *            值脚本
	 */
	public void setValueScript(String valueScript) {
		this.valueScript = valueScript;
	}

	/**
	 * 获取列宽度
	 * 
	 * @hibernate.property column="WIDTH"
	 * @return string
	 */
	public String getWidth() {
		return width;
	}

	/**
	 * 设置 列宽度
	 * 
	 * @param width
	 *            列宽度
	 */
	public void setWidth(String width) {
		this.width = width;
	}

	/**
	 * 获取名字
	 * 
	 * @hibernate.property column="NAME"
	 * @return 名字
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置名字
	 * 
	 * @param name
	 *            名字
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取类型,分别:1.字段(COLUMN_TYPE_FIELD),2.脚本(COLUMN_TYPE_SCRIPT)
	 * 如果选择字段,在视图显示列(column)的值为表单字段值,否则视图column为执行脚本(SCRIPT)后返回的值
	 * 
	 * @hibernate.property column="CTYPE"
	 */
	public String getType() {
		return type;
	}

	/**
	 * 设置类型
	 * 
	 * @param type
	 *            类型 1.字段(COLUMN_TYPE_FIELD),2.脚本(COLUMN_TYPE_SCRIPT)
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * 获取字列名
	 * 
	 * @hibernate.property column="FIELDNAME"
	 * @return string 字列名
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * 设置字列名
	 * 
	 * @param fieldName
	 *            字列名
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * 获取相关表单Form主键
	 * 
	 * @hibernate.property column="FORMID"
	 * @return string
	 */
	public String getFormid() {
		return formid;
	}

	/**
	 * 设置相关表单Form主键
	 * 
	 * @param formid
	 *            表单Form主键
	 */
	public void setFormid(String formid) {
		this.formid = formid;
	}

	/**
	 * 获取操作列类型按钮的名称
	 * @return
	 */
	public String getButtonName() {
		return buttonName;
	}

	/**
	 * 设置操作列按钮的名称
	 * @param buttonName
	 */
	public void setButtonName(String buttonName) {
		this.buttonName = buttonName;
	}
	
	/**
	 * 获取列是否可见属性
	 * @return
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * 设置列是否可见属性
	 * @param visible
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	/**
	 * 获得模板表单的From主键
	 * @return
	 */
	public String getTemplateForm() {
		return templateForm;
	}
	
	/**
	 *设置模板表单的From主键 
	 * @param templateForm
	 */
	public void setTemplateForm(String templateForm) {
		this.templateForm = templateForm;
	}

	/**
	 * 获取Column的显示值
	 * 
	 * @param doc
	 *            文档
	 * @param runner
	 *            动态语言执行器
	 * @return text 显示值
	 * @throws Exception
	 */
	public String getText(Document doc, IRunner runner, WebUser webUser) throws Exception {
		StringBuffer html = new StringBuffer();

		String value = getTextString(doc, runner, webUser);
		html.append(value);

		return (!StringUtil.isBlank(html.toString()) ? html.toString() : "&nbsp");
	}

	public String getTextString(Document doc, IRunner runner, WebUser webUser) throws Exception {
		Object result = null;
		StringBuffer html = new StringBuffer();
		StringBuffer labelBuilder = new StringBuffer();
		String text = "";
		if (getType() != null && getType().equals(COLUMN_TYPE_SCRIPT)) {
			labelBuilder.append("View.").append(getParentView());
			labelBuilder.append(".Column(").append(getId()).append(")." + getName());

			result = runner.run(labelBuilder.toString(), getValueScript());
			text = result != null ? result.toString() : "";
		} else if (getType() != null && getType().equals(COLUMN_TYPE_FIELD)) {
			text = getValue(doc, runner, webUser);
		}
		if (getFlowReturnCss()) {
			html.append(getFlowReturnOperation(doc, text));
		} else {
			html.append(text);
		}
		return (!StringUtil.isBlank(html.toString()) ? html.toString() : "");
	}

	/**
	 * 获取Column的真实值
	 * 
	 * @param doc
	 *            文档
	 * @return value 真实值
	 * @throws Exception
	 */
	public String getValue(Document doc, IRunner runner, WebUser webUser) throws Exception {
		String result = null;
		if (!StringUtil.isBlank(fieldName)) {
			if (fieldName.startsWith("$")) {
				
				
				String propertyName = fieldName.substring(1, fieldName.length());
				result = doc.getValueByPropertyName(propertyName);
				
				
				if((fieldName.equalsIgnoreCase("$auditorNames") || fieldName.equalsIgnoreCase("$stateLabel")) && result.indexOf(":")>=0){
					String[] resultlist = result.split(":");
					StringBuffer html = new StringBuffer("<table>");
					for(int i=0;i<resultlist.length;i++){
						String name = resultlist[i];
						html.append("<tr><td>").append(name).append("&nbsp").append("</td></tr>");
					}
					html.append("</table>");
					result =html.toString();
				}
			} else {
				FormField field = getFormField();
				if (SHOW_TYPE_TEXT.equals(getShowType())) {
					// 此方法慎用，有运算脚本的字段会影响性能
					result = field.getText(doc, runner, webUser);
				} else {
					result = field.getValue(doc, runner, webUser);
				}

			}
		}
		return (result != null ? result.toString() : "");
	}

	public int compareTo(Object o) {
		if (o != null && o instanceof Column) {
			int thisOrderno = this.orderno;
			int otherOrderno = ((Column) o).orderno;
			return (thisOrderno - otherOrderno);
			// return (this.orderno - ((Column) o).orderno);
		}
		return -1;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		return super.equals(obj);
	}

	/**
	 * 获取关联的视图
	 * 
	 * @return
	 */
	public String getParentView() {
		return parentView;
	}

	/**
	 * 设置关联的视图
	 * 
	 * @param parentView
	 *            视图标识
	 */
	public void setParentView(String parentView) {
		this.parentView = parentView;
	}

	/**
	 * 列之前图片,用于流程回退时可在列前增加图片标识
	 * 
	 * @hibernate.property column="IMAGENAME"
	 * @return 图片路径F
	 */
	public String getImageName() {
		return imageName;
	}

	/**
	 * 设置图片的标识
	 * 
	 * @param imageName
	 */
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	/**
	 * 第一列显示字体带有颜色,用于流程回退时可在第一列字体带有颜色
	 * 
	 * @hibernate.property column="FONTCOLOR"
	 * @return 颜色色值
	 */
	public String getFontColor() {
		return fontColor;
	}

	/**
	 * 设置第一列显示字体的颜色
	 * 
	 * @param fontColor
	 */
	public void setFontColor(String fontColor) {
		this.fontColor = fontColor;
	}

	/**
	 * 获取流程是否增加回退标识(是:在第一表字体带有颜色,并在死前加上一个图片. 否:原型)
	 * 
	 * @hibernate.property column = "FLOWRETURNCSS"
	 * @return 布尔值
	 */
	public boolean getFlowReturnCss() {
		return flowReturnCss;
	}

	/**
	 * 设置流程是否增加回退标识
	 * 
	 * @param flowReturnCss
	 */
	public void setFlowReturnCss(boolean flowReturnCss) {
		this.flowReturnCss = flowReturnCss;
	}

	/**
	 * 查询流程是否是回退的状态,如果是回退的状态,在列前加一个小灯泡的标识,并第一列字体带有颜色
	 * 
	 * @param doc
	 *            Document(文档)
	 * @param text
	 *            列的显示值
	 * @return 以html的形式,来设置列的回退标识
	 * @throws Exception
	 */
	public String getFlowReturnOperation(Document doc, String text) throws Exception {
		StringBuffer html = new StringBuffer();

		if ("81".equals(doc.getLastFlowOperation())) {
			html.append("<img src=\"../../../resource/imgnew/backstatelabel" + getImageName() + ".gif\" border=\"0\"/>");
			//html.append("</img>");
			if (getFieldName() != null) {
				html.append("<font color=\"" + this.fontColor + "\">");
				html.append(text);
				html.append("</font>");				
			}
		} else {
			html.append(text);
		}
		return html.toString();
	}

	/**
	 * 获取FormField
	 * 
	 * @return FormField
	 */
	public FormField getFormField() {
		try {
			if (field == null) {
				FormProcess formProcess = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
				Form form = (Form) formProcess.doView(this.getFormid());
				if (form != null) {
					field = form.findFieldByName(this.getFieldName());
				}
			}

			if (field == null) {
				field = new NullField();
				field.setName(getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return field;
	}

	/**
	 * 将列以网格的形式输出,提供视图打开方式为网格时调用此方法
	 * 
	 * @param doc
	 *            Document对
	 * @param runner
	 *            动态语言执行器
	 * @param webUser
	 *            webUser
	 * @return 标准的html中的表格形式输出
	 */
	public String toGridColumnHtml(Document doc, IRunner runner, WebUser webUser, boolean isEdit) {
		StringBuffer htmlBuilder = new StringBuffer();

		try {
			htmlBuilder.append("<td class='table_th_td' ");
			String style = "";
			if (!StringUtil.isBlank(getWidth())) {
				if (getWidth().equals("0")) {
					style = " style='display: none;'";
				} else {
					style = " width= '" + getWidth() + "'";
				}
			}

			htmlBuilder.append(style);
			htmlBuilder.append(">");
			htmlBuilder.append(getContent(doc, runner, webUser, isEdit));
			htmlBuilder.append("</td>\n");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return htmlBuilder.toString();
	}

	private String replaceContent(String content) {
		content = content.replaceAll("\\\"", "\\\\\"");
		content = content.replaceAll("\\\'", "\\\\\'");
		return content;
	}

	/**
	 * 获取单元格创建脚本
	 * 
	 * @param doc
	 *            Document对象
	 * @param runner
	 *            动态语言执行器
	 * @param webUser
	 *            webuser
	 * @return 脚本
	 * @throws Exception
	 */
	public String getCellCreateScript(Document doc, IRunner runner, WebUser webUser) throws Exception {
		StringBuffer scriptBuilder = new StringBuffer();

		scriptBuilder.append("createColumn({");
		if (getWidth() != null && getWidth().equals("0")) {
			if (getWidth().equals("0")) {
				scriptBuilder.append("style:'display: none;'");
			} else {
				scriptBuilder.append("style:'width: ").append(getWidth()).append(";'");
			}
		}
		scriptBuilder.append("}, \"");
		// 表格列内容
		String content = getContent(doc, runner, webUser, true);
		scriptBuilder.append(replaceContent(content));

		scriptBuilder.append("\")");
		return scriptBuilder.toString();
	}

	/**
	 * 获取单元格刷新脚本
	 * 
	 * @param doc
	 *            Document
	 * @param runner
	 *            动态语言执行器
	 * @param webUser
	 *            webuser
	 * @return 刷新脚本
	 * @throws Exception
	 */
	public String getCellRefreshScript(Document doc, IRunner runner, WebUser webUser) throws Exception {
		FormField formField = getFormField();

		StringBuffer refreshScript = new StringBuffer();
		if (formField != null) {
			String id = doc.getId() + "_" + this.getFieldName();
			String showId = id + "_show";
			String editId = id + "_edit";

			if (getType() != null && getType().equals(COLUMN_TYPE_SCRIPT)) {
				// 刷新显示值
				refreshScript.append("refreshField('").append(showId).append("', '");
				refreshScript.append(showId).append("', '").append(this.getText(doc, runner, webUser)).append("');");

			} else if (getType() != null && getType().equals(COLUMN_TYPE_FIELD)) {
				if (formField.isCalculateOnRefresh() && formField instanceof ValueStoreField) {
					String destVal = doc.getItemValueAsString(formField.getName());
					String origVal = doc.get_params().getParameterAsString(formField.getName());
					if (formField.isRender(destVal, origVal)) {
						// 刷新显示值
						refreshScript.append("refreshField('").append(showId).append("', '");
						refreshScript.append(showId).append("', '").append(this.getText(doc, runner, webUser)).append(
								"');");

						// 刷新编辑器
						String fieldHTML = ((ValueStoreField) formField).toGridHtmlText(doc, runner, webUser);
						refreshScript.append("refreshField('").append(editId).append("', '");
						refreshScript.append(editId).append("', \"").append(replaceContent(fieldHTML)).append("\");");
					}
				}
			}
		}

		return refreshScript.toString();
	}

	/**
	 * 获取列数据组成html,并在列有数据显示栏中增加相应的效果
	 * 
	 * @param doc
	 *            Document
	 * @param runner
	 *            动态语言执行器
	 * @param webUser
	 *            webuser
	 * @return 以html的形式
	 * @throws Exception
	 */
	public String getContent(Document doc, IRunner runner, WebUser webUser, boolean isEdit) throws Exception {
		StringBuffer builder = new StringBuffer();

		String id = doc.getId() + "_" + this.getFieldName();
		String showId = id + "_show";
		String editId = id + "_edit";

		FormField formField = getFormField();

		// String style = "";
		// if (!StringUtil.isBlank(getWidth())) {
		// if (getWidth().equals("0")) {
		// style = " style='display: none;'";
		// } else {
		// style = " style='width: " + getWidth() + ";'";
		// }
		// }

		// 生成显示值的Div
		builder.append("<div class='grid-column-show' title='").append("").append("'");
		builder.append(" id=").append("'").append(showId).append("'");

		if(formField instanceof NullField){
			
		}else{
			int displayType = formField.getDisplayType(doc, runner, webUser);
			PermissionProcess permissionProcess = (PermissionProcess) ProcessFactory.createProcess(PermissionProcess.class);
			if (displayType == PermissionType.MODIFY
					&& isEdit
					&& (permissionProcess.check(webUser.getRolesByApplication(doc.getApplicationid()), formField.getId(),
							OperationVO.FORMFIELD_MODIFY, ResVO.FORM_TYPE))) {
				builder.append(" onclick=").append("'").append("doRowEdit(\"" + doc.getId() + "\", event)").append("'");
			}
		}
		// builder.append(style);
		builder.append(">");
		builder.append(this.getText(doc, runner, webUser));
		builder.append("</div>");

		// 生成编辑器的Div
		if (formField instanceof ValueStoreField) {
			builder.append("<div class='grid-column-edit' style='display:none'");
			builder.append(" id=").append("'").append(editId).append("'");
			// builder.append(style);
			builder.append(">");
			builder.append(((ValueStoreField) formField).toGridHtmlText(doc, runner, webUser));

			builder.append("</div>");
		}

		return builder.toString();
	}

	/**
	 * 是否汇总
	 * 
	 * @return
	 */
	public boolean isSum() {
		return sum;
	}

	/**
	 * 设置是否汇总
	 * 
	 * @param sum
	 */
	public void setSum(boolean sum) {
		this.sum = sum;
	}

	/**
	 * 获取汇总值
	 * 
	 * @return 汇总值
	 * @throws Exception
	 */
	public String getSum(WebUser user, ParamsTable params) throws Exception {
		if (isSum()) {
			ViewProcess vp = (ViewProcess) ProcessFactory.createProcess(ViewProcess.class);
			View parent = (View) vp.doView(parentView);
			DocumentProcess dp = (DocumentProcess) ProcessFactory.createRuntimeProcess(DocumentProcess.class,parent.getApplicationid());
			if (parent != null) {
				Document doc = parent.getSearchForm().createDocument(params, user);

				String query = vp.getQueryString(parent, params, user, doc);
				String label = getName() + "{*[Grant_Total]*}";
				if (parent.getEditMode().equals(View.EDIT_MODE_CODE_DQL)) {
					return label + ": " + Double.toString(dp.sumByDQL(query, fieldName, user.getDomainid()));
				} else {
					return label + ": " + Double.toString(dp.sumBySQL(query, user.getDomainid()));
				}
			}
		}

		return "";
	}

	/**
	 * 设置页面数据汇总,在页面的右下角显示
	 * 
	 * @param datas
	 *            数据集合
	 * @return 数据汇总数
	 * @throws Exception
	 */
	public String getSumByDatas(DataPackage<Document> datas, IRunner runner, WebUser webUser) {
		double total = 0;
		String numberPattern = "";
		String num = "0";
		if (isSum()) {
			try {
				FormField formField = getFormField();
				if (formField instanceof InputField) {
					InputField inputField = (InputField) formField;
					numberPattern = inputField.getNumberPattern();
				}
			} catch (Exception e) {
				log.warn("get number pattern error!");
			}

			for (Iterator<Document> iterator = datas.datas.iterator(); iterator.hasNext();) {
				Document doc = iterator.next();
				try {
					runner.declareBean("$CURRDOC", new CurrDocJsUtil(doc), CurrDocJsUtil.class);

					double val = Double.parseDouble(getText(doc, runner, webUser));
					total += val;
				} catch (Exception e) {
					log.warn("sum data error!");
				}
			}

			if (!StringUtil.isBlank(numberPattern)) {
				DecimalFormat format = new DecimalFormat(numberPattern);
				num = format.format(total);
			} else {
				num = "" + total;
			}

			String label = getName() + "{*[Grant_Total]*}";
			return label + ": " + num;
		}
		return "";
	}

	public String getShowType() {
		return showType;
	}

	public void setShowType(String showType) {
		this.showType = showType;
	}
	
	public String getIsOrderByField() {
		return isOrderByField;
	}

	public void setIsOrderByField(String isOrderByField) {
		this.isOrderByField = isOrderByField;
	}
	
	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	/**
	 * 获取隐藏脚本
	 * 
	 * @return 隐藏脚本
	 * @hibernate.property column="HIDDENSCRIPT" type = "text"
	 * @uml.property name="hiddenScript"
	 */
	public String getHiddenScript() {
		return hiddenScript;
	}

	/**
	 * 设置隐藏脚本
	 * 
	 * @param hiddenScript
	 *            隐藏脚本
	 * @uml.property name="hiddenScript"
	 */
	public void setHiddenScript(String hiddenScript) {
		this.hiddenScript = hiddenScript;
	}

	public String getMappingField() {
		return mappingField;
	}

	public void setMappingField(String mappingField) {
		this.mappingField = mappingField;
	}
	
	/**
	 * 获取操作按钮类型
	 * @return
	 */
	public String getButtonType() {
		return buttonType;
	}

	/**
	 * 设置操作按钮类型
	 * @param buttonType
	 */
	public void setButtonType(String buttonType) {
		this.buttonType = buttonType;
	}

	public String getApproveLimit() {
		return approveLimit;
	}

	public void setApproveLimit(String approveLimit) {
		this.approveLimit = approveLimit;
	}
	
	/**
	 * 获取图标路径
	 * @return
	 */
	public String getIcon() {
		return icon;
	}

	/**
	 * 设置图标路径
	 * @param icon
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}

	public boolean isHiddenColumn(IRunner runner) {
		try {
			if (this.getHiddenScript() != null && this.getHiddenScript().trim().length() > 0) {
				StringBuffer label = new StringBuffer();
				label.append("View").append(".Activity(").append(this.getId()).append(")." + this.getName()).append(
						".runHiddenScript");

				Object result = runner.run(label.toString(), this.getHiddenScript());// 运行脚本
				if (result != null && result instanceof Boolean) {
					return ((Boolean) result).booleanValue();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
}
