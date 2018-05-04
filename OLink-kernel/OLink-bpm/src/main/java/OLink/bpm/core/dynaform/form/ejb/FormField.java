package OLink.bpm.core.dynaform.form.ejb;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import OLink.bpm.core.macro.runner.AbstractRunner;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.role.ejb.RoleVO;
import OLink.bpm.util.*;
import OLink.bpm.core.dynaform.document.ejb.Item;
import OLink.bpm.core.permission.ejb.PermissionProcess;
import OLink.bpm.core.validate.repository.ejb.ValidateRepositoryProcess;
import OLink.bpm.core.dynaform.PermissionType;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.privilege.res.ejb.ResVO;
import OLink.bpm.core.validate.repository.ejb.ValidateRepositoryVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import org.apache.log4j.Logger;

import OLink.bpm.core.user.action.WebUser;
import eWAP.core.Tools;


/**
 * @author Marky
 */
public abstract class FormField implements FormElement, Cloneable, Serializable {

	private static final long serialVersionUID = 7550413028715958740L;
	/**
	 * 字符串的形式脚本,以;分开(option;option)
	 */
	public final static String SCRIPT_TYPE_STATICTEXT = "STATICTEXT";
	/**
	 * 宏语言的形式脚本
	 */
	public final static String SCRIPT_TYPE_JAVASCRIPT = "JAVASCRIPT";

	public final static String TEXT_TYPE_READONLY = "readonly";

	public final static String TEXT_TYPE_TEXT = "text";

	public final static String TEXT_TYPE_HIDDEN = "hidden";

	public final static String TEXT_TYPE_PASSWORD = "password";

	/**
	 * 视图编辑模式
	 */
	public static final String EDITMODE_VIEW = "00";// 视图编辑模式
	/**
	 * 代码编辑模式
	 */
	public static final String EDITMODE_CODE = "01";// 代码编辑模式

	/**
	 * 字段的其它属性.
	 */
	protected Map<String, String> _otherprops = new HashMap<String, String>();

	/**
	 * @uml.property name="seq"
	 */
	protected int seq;

	/**
	 * 表示该“Field”将在变化时刷新文档.
	 * 
	 * @uml.property name="refreshOnChanged"
	 */
	protected boolean refreshOnChanged;

	/**
	 * 表示该“Field”将在文档刷新时重新计算.
	 * 
	 * @uml.property name="calculateOnRefresh"
	 */
	protected boolean calculateOnRefresh;

	/**
	 * @uml.property name="id"
	 */
	protected String id;

	/**
	 * Form的主键
	 * 
	 * @uml.property name="formid"
	 */
	protected String formid;

	/**
	 * Field名称
	 * 
	 * @uml.property name="name"
	 */
	protected String name;

	protected String type;

	/**
	 * 字段值类型
	 * 
	 * @uml.property name="fieldtype"
	 */
	protected String fieldtype;

	/**
	 * 文本框类型
	 * 
	 * @uml.property name="textType"
	 */
	protected String textType;

	/**
	 * 字段描述
	 * 
	 * @uml.property name="discript"
	 */
	protected String discript;

	/**
	 * 计算隐藏条件
	 * 
	 * @uml.property name="hiddenScript"
	 */
	protected String hiddenScript;

	/**
	 * 隐藏时显示值
	 */
	protected String hiddenValue = "";

	/**
	 * 打印隐藏时显示值
	 */
	protected String printHiddenValue;

	/**
	 * 计算只读条件
	 * 
	 * @uml.property name="readonlyScript"
	 */
	protected String readonlyScript;

	/**
	 * 计算值
	 * 
	 * @uml.property name="valueScript"
	 */
	protected String valueScript;

	/**
	 * 打印文档时是否打印该字段，默认为“打印”
	 * 
	 * @uml.property name="hiddenPrintScript"
	 */
	protected String hiddenPrintScript;

	/**
	 * 校验规则
	 * 
	 * @uml.property name="validateRule"
	 */
	protected String validateRule;

	/**
	 * @uml.property name="orderno"
	 */
	protected int orderno;

	public Form _form;

	/**
	 * @uml.property name="editMode"
	 */
	protected String editMode;

	/**
	 * @uml.property name="filtercondition"
	 */
	protected String filtercondition;

	/**
	 * @uml.property name="processDescription"
	 */
	protected String processDescription;

	/*
	 * 别名
	 */
	/**
	 * @uml.property name="alias"
	 */
	protected String alias;

	/**
	 * 字段长度
	 * 
	 * @uml.property name="fieldWidth"
	 */
	protected int fieldWidth;

	/**
	 * 列表显示宽度（可为px或百分比如20px、20%）
	 * 
	 * @uml.property name="showWidth"
	 */
	protected String showWidth;

	/**
	 * 排列方式 horizontal/vertical
	 * 
	 * @uml.property name="layout"
	 */
	protected String layout;

	protected String validateLibs = "";

	/**
	 * 权限字段
	 * 
	 * @uml.property name="authority"
	 */

	protected boolean authority;

	/**
	 * 是否在手机中显示
	 * 
	 * 默认在手机中显示
	 * 
	 * @uml.property name="mobile"
	 */
	protected boolean mobile = true;

	/**
	 * 边框类型(只读时是否有边框)
	 */
	public boolean borderType;

	/**
	 * 打印值的优先级别
	 */
	public static final int HIDDENPRINTVALUE = 1;
	public static final int HIDDENSHOWVALUE = 2;
	public static final int TRUEVALUE = 3;
	public static final int NONE = 4;
	/**
	 * 打印的值的优先级
	 */
	protected int printValue = HIDDENPRINTVALUE;

	public int getPrintValue() {
		return printValue;
	}

	public void setPrintValue(int printValue) {
		this.printValue = printValue;
	}

	/**
	 * 是否是权限字段
	 * 
	 * @return true 是 false否
	 */
	public boolean isAuthority() {
		return authority;
	}

	/**
	 * 设置是否是权限字段
	 * 
	 * @param authority
	 */
	public void setAuthority(boolean authority) {
		this.authority = authority;
	}

	public abstract String toMbXMLText(Document doc, IRunner runner, WebUser webUser) throws Exception;

	/**
	 * 获取所用到的校验库
	 * 
	 * @return
	 * @uml.property name="validateLibs"
	 */
	public String getValidateLibs() {
		return validateLibs;
	}

	/**
	 * 设置引用到的校验库
	 * 
	 * @param validateLibs
	 * @uml.property name="validateLibs"
	 */
	public void setValidateLibs(String validateLibs) {
		this.validateLibs = validateLibs;
	}

	// private int displayType = PermissionType.MODIFY;
	public FormField() {
	}

	/**
	 * 获取标识
	 * 
	 * @return the id
	 * @uml.property name="id"
	 */
	public String getId() {
		return id;
	}

	/**
	 * 设置标识
	 * 
	 * @param id
	 *            标识
	 * @uml.property name="id"
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 表示该"字段"是否将在变化时刷新文档.true为变化时刷新文档 , false为变化时不刷新文档.
	 * 
	 * @return true为变化时刷新文档, false为变化时不刷新文档
	 * @uml.property name="refreshOnChanged"
	 */
	public boolean isRefreshOnChanged() {
		return refreshOnChanged;
	}

	/**
	 * 设置表示该"字段"是否将在变化时刷新文档 .true为变化时刷新文档 , false为变化时不刷新文档.
	 * 
	 * @param refreshOnChanged
	 *            boolean
	 * @uml.property name="refreshOnChanged"
	 */
	public void setRefreshOnChanged(boolean refreshOnChanged) {
		this.refreshOnChanged = refreshOnChanged;
	}

	/**
	 * 返回字段的校验规则
	 * 
	 * @return 字段的校验规则
	 * @uml.property name="validateRule"
	 */
	public String getValidateRule() {
		return validateRule;
	}

	/**
	 * 设置字段的校验规则
	 * 
	 * @param validateRule
	 *            字段的校验规则
	 * @uml.property name="validateRule"
	 */
	public void setValidateRule(String validateRule) {
		this.validateRule = validateRule;
	}

	/**
	 * 返回表单字段值脚本
	 * 
	 * @return 字段值脚本
	 * @uml.property name="valueScript"
	 */
	public String getValueScript() {
		return valueScript;
	}

	/**
	 * 设置表单字段值脚本
	 * 
	 * @param valueScript
	 *            字段值脚本
	 * @uml.property name="valueScript"
	 */
	public void setValueScript(String valueScript) {
		this.valueScript = StringUtil.dencodeHTML(valueScript);
	}

	/**
	 * 执行Field脚本,重新计算字段值. 执行相应字段的值脚本,重新计算出结果值.
	 * 
	 * @roseuid 41DB89D700F9
	 * @param webUser
	 */
	public void recalculate(IRunner runner, Document doc, WebUser webUser) throws Exception {
		getLog().debug(this.getClass().getName() + ".recalculate");
		runValueScript(runner, doc);
	}

	/**
	 * 通过动态语言执行器得到一个boolean值的结果返回
	 * 
	 * @param runner
	 *            动态语言执行器
	 * @param suffix
	 *            截取符
	 * @param script
	 *            脚本
	 * @return boolean
	 * @throws Exception
	 */
	public boolean runBooleanScript(IRunner runner, String suffix, String script) throws Exception {
		Object result = null;

		if (script != null && script.trim().length() > 0) {
			result = runner.run(getScriptLable(suffix), script);

			if (result != null) {
				if (result instanceof Boolean) {
					return ((Boolean) result).booleanValue();
				} else if (result instanceof String) {
					return result.equals("true") ? true : false;
				}
			}
		}

		return false;
	}

	/**
	 * 运行计算CheckboxField值脚本，返回值即为计算结果。
	 * 
	 * @param runner
	 *            AbstractRunner(执行脚本接口类)
	 * @param doc
	 *            文档对象
	 * @return java.lang.String
	 * @roseuid 41DB8C1E03E7
	 */
	public Object runValueScript(IRunner runner, Document doc) throws Exception {
		Object result = null;
		if (getEditMode() != null) {
			if (getEditMode().equals(EDITMODE_VIEW)) {
				if (getFiltercondition() != null && getFiltercondition().trim().length() > 0) {
					result = runner
							.run(getScriptLable("FilterCondition"), StringUtil.dencodeHTML(getFiltercondition()));
					Item item = doc.findItem(this.getName());
					if (item != null) {
						item.setValue(result);
					}
				}
			} else {
				if (getValueScript() != null && getValueScript().trim().length() > 0) {
					result = runner.run(getScriptLable("ValueScript"), getValueScript());
					Item item = doc.findItem(this.getName());
					if (item != null) {
						item.setValue(result);
					}
				}
			}
		}
		return result;
	}

	/**
	 * 获取模板描述
	 * 
	 * @return 模板描述
	 * 
	 */
	public abstract String toTemplate();

	/**
	 * 根据FormField创建Document的Item.
	 * 创建item时,先查找是否有相应Document的item名存在,若存在,即根据FormField为ITEM设置相应属性的值.
	 * 否则没有相应的ITEM,即实例化一个ITEM对象.并设以相应的值.
	 * 
	 * @param doc
	 *            Document 对象
	 * @param value
	 *            field值
	 * @return 创建ITEM
	 * 
	 */

	public Item createItem(Document doc, Object value) {
		Item item = doc.findItem(getName());

		if (item == null) {
			item = new Item();
			try {
				item.setId(Tools.getSequence());
			} catch (Exception e) {
				e.printStackTrace();
			}
			item.setName(getName());
		}

		item.setType(this.getFieldtype());

		if (value != null) {
			item.setValue(value);
		}

		doc.addItem(item);

		return item;
	}

	/**
	 * 获取字段描述
	 * 
	 * @return 字段描述
	 * @uml.property name="discript"
	 */
	public String getDiscript() {
		return HtmlEncoder.encode(discript);
	}

	/**
	 * 获取字段描述
	 * 
	 * @param discript
	 *            字段描述
	 * @uml.property name="discript"
	 */
	public void setDiscript(String discript) {
		this.discript = discript;
	}

	/**
	 * 获取相应表单主键
	 * 
	 * @return 相应表单主键
	 * @uml.property name="formid"
	 */
	public String getFormid() {
		return formid;
	}

	/**
	 * 设置相应表单主键
	 * 
	 * @param formid
	 *            相应表单主键
	 * @uml.property name="formid"
	 */
	public void setFormid(String formid) {
		this.formid = formid;
	}

	/**
	 * 获取打印隐藏脚本
	 * 
	 * @return 打印隐藏脚本
	 * @uml.property name="hiddenPrintScript"
	 */
	public String getHiddenPrintScript() {
		return hiddenPrintScript;
	}

	/**
	 * 设置打印隐藏脚本
	 * 
	 * @param hiddenPrintScript
	 *            打印隐藏脚本
	 * @uml.property name="hiddenPrintScript"
	 */
	public void setHiddenPrintScript(String hiddenPrintScript) {
		this.hiddenPrintScript = StringUtil.dencodeHTML(hiddenPrintScript);
	}

	/**
	 * 获取隐藏脚本
	 * 
	 * @return 隐藏脚本
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
		this.hiddenScript = StringUtil.dencodeHTML(hiddenScript);
	}

	/**
	 * 获取字段名
	 * 
	 * @return 字段名
	 * @uml.property name="name"
	 */
	public String getName() {
		return HtmlEncoder.encode(name);
	}

	/**
	 * 拼接字段显示名
	 * 
	 * @return 字段名
	 * @uml.property name="name"
	 */
	public String getShowName() {
		return "show_" + this.name;
	}

	/**
	 * 设置字段名
	 * 
	 * @param name
	 *            字段名
	 * @uml.property name="name"
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取所属表单
	 * 
	 * @return 所属表单
	 * @uml.property name="_form"
	 */
	public Form get_form() {
		return _form;
	}

	/**
	 * 获取所属表单
	 * 
	 * @param _form
	 *            所属表单
	 * @uml.property name="_form"
	 */
	public void set_form(Form form) {
		this._form = form;
	}

	/**
	 * 获取字段类型。五种类型(1:字符串 2:数字3：日期 4:文本 5:INCLUDE.
	 * 五种类型常量值：VALUE_TYPE_VARCHAR(字符串)
	 * ,VALUE_TYPE_NUMBER(数字),VALUE_TYPE_DATE(日期),VALUE_TYPE_TEXT(文本),INCLUDE
	 * 
	 * @return 字段类型
	 * @uml.property name="fieldtype"
	 */
	public String getFieldtype() {
		return fieldtype;
	}

	/**
	 * 获取文本框类型.四种类型(1:普通(Common),2:密码（Password），3：只读(Readonly),4:隐藏(Hidden)
	 * 
	 * @return 文本类型 四种类型(1:普通(Common),2:密码（Password），3：只读(Readonly),4:隐藏(Hidden)
	 * @uml.property name="textType"
	 */
	public String getTextType() {
		return textType != null ? textType : "";
	}

	/**
	 * 设置字段类型 四种类型(1:字符串 2:数字 3：日期 4:文本 5:INCLUDE)
	 * 五种类型常量值：VALUE_TYPE_VARCHAR(字符串
	 * ),VALUE_TYPE_NUMBER(数字),VALUE_TYPE_DATE(日期),VALUE_TYPE_TEXT(文本),INCLUDE
	 * 
	 * @param fieldType
	 *            字段类型.
	 * @uml.property name="fieldtype"
	 */

	public void setFieldtype(String fieldtype) {
		this.fieldtype = fieldtype;
	}

	/**
	 * 设置文本类型 四种类型(1:普通(Common),2:密码（Password），3：只读(Readonly),4:隐藏(Hidden)
	 * 
	 * @param textType
	 *            文本类型
	 * @uml.property name="textType"
	 */
	public void setTextType(String textType) {
		this.textType = textType;
	}

	/**
	 * 添加表单Field的其它属性
	 * 
	 * @param key
	 *            属性
	 * @param value
	 *            属性值
	 */
	public void addOtherProps(String key, String value) {
		if (key != null && value != null)
			_otherprops.put(key, value);
	}

	// public String getOtherProp(String key) {
	// String value = (String) _otherprops.get(key);
	// return value;
	// }
	/**
	 * 获取表单Field的其它属性
	 */
	public Map<String, String> getOtherPropsAsMap() {
		return _otherprops;
	}

	// public Collection getOtherPropsAsCollection() {
	// return _otherprops.values();
	// }
	/**
	 * 获取表单Field的其它属性
	 * 
	 * @return 表单Field的其它属性
	 */
	protected String toOtherpropsHtml() {
		StringBuffer buffer = new StringBuffer();
		Map<?, ?> coll = getOtherPropsAsMap();
		Collection<?> entrys = coll.entrySet();
		Iterator<?> it = entrys.iterator();
		while (it.hasNext()) {
			Entry<?, ?> entry = (Entry<?, ?>) it.next();
			buffer.append(" ");
			buffer.append(entry.getKey());
			buffer.append("=");
			String value = (String) entry.getValue();
			int pos1 = value.indexOf("'");
			int pos2 = value.indexOf("\"");
			if (pos1 > pos2) {
				buffer.append("'");
				buffer.append(value);
				buffer.append("'");
			} else {
				buffer.append("'");
				buffer.append(value);
				buffer.append("' ");
			}

		}
		return buffer.toString();
	}

	/**
	 * 字段隐藏时显示*****
	 * 
	 * @param doc
	 * @return
	 */
	protected String toHiddenHtml(Document doc) {
		try {
			String value = doc.getItemValueAsString(this.getName());
			if (!StringUtil.isBlank(value)) {
				return "******";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 获取字段的次序号
	 * 
	 * @return Returns the orderno.
	 * @uml.property name="orderno"
	 */
	public int getOrderno() {
		return orderno;
	}

	/**
	 * 设置表单字段的次序号
	 * 
	 * @param orderno
	 *            The orderno to set.
	 * @uml.property name="orderno"
	 */
	public void setOrderno(int orderno) {
		this.orderno = orderno;
	}

	/**
	 * 获取别名
	 * 
	 * @return Returns the alias.
	 * @uml.property name="alias"
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * 设置别名
	 * 
	 * @param alias
	 *            The alias to set.
	 * @uml.property name="alias"
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * 获取字段宽度
	 * 
	 * @return Returns the fieldWidth.
	 * @uml.property name="fieldWidth"
	 */
	public int getFieldWidth() {
		return fieldWidth;
	}

	/**
	 * 设置字段宽度
	 * 
	 * @param fieldWidth
	 *            The fieldWidth to set.
	 * @uml.property name="fieldWidth"
	 */
	public void setFieldWidth(int fieldWidth) {
		this.fieldWidth = fieldWidth;
	}

	/**
	 * 获取组件显示的宽度
	 * 
	 * @return Returns the showWidth.
	 * @uml.property name="showWidth"
	 */
	public String getShowWidth() {
		return showWidth;
	}

	/**
	 * 设置组件显示的宽度
	 * 
	 * @param showWidth
	 *            The showWidth to set.
	 * @uml.property name="showWidth"
	 */
	public void setShowWidth(String showWidth) {
		this.showWidth = showWidth;
	}

	/**
	 * 获取 排列方式 horizontal/vertical
	 * 
	 * @return 排列方式 horizontal/vertical
	 * @uml.property name="layout"
	 */
	public String getLayout() {
		return layout;
	}

	/**
	 * 设置 排列方式 horizontal/vertical
	 * 
	 * @param layout
	 *            排列方式
	 * @uml.property name="layout"
	 */
	public void setLayout(String layout) {
		this.layout = layout;
	}

	/**
	 * 表示该“Field”将在文档刷新时重新计算
	 * 
	 * @return true or false
	 * @uml.property name="calculateOnRefresh"
	 */
	public boolean isCalculateOnRefresh() {
		return calculateOnRefresh;
	}

	/**
	 * 设置表示该“Field”将在文档刷新时重新计算
	 * 
	 * @param calculateOnRefresh
	 * @uml.property name="calculateOnRefresh"
	 */
	public void setCalculateOnRefresh(boolean calculateOnRefresh) {
		this.calculateOnRefresh = calculateOnRefresh;
	}

	/**
	 * 获取字段只读脚本
	 * 
	 * @return 字段只读脚本
	 * @uml.property name="readonlyScript"
	 */
	public String getReadonlyScript() {
		return readonlyScript;
	}

	/**
	 * 设置字段只读脚本
	 * 
	 * @param readonlyScript
	 * @uml.property name="readonlyScript"
	 */
	public void setReadonlyScript(String readonlyScript) {
		this.readonlyScript = StringUtil.dencodeHTML(readonlyScript);
	}

	/**
	 * 权限判断文本框类型是否屏蔽或只读
	 * 
	 * @param displayType
	 *            分别：1.只读(READONLY)2.修改(MODIFY),3.隐藏(HIDDEN),4.屏蔽(DISABLED)
	 * @return true为屏蔽只读 or false
	 */
	protected boolean isDisable(int displayType) {
		return (this.getTextType() != null && this.getTextType().equalsIgnoreCase("READONLY"))
				|| (displayType == PermissionType.DISABLED) || (displayType == PermissionType.READONLY);
	}

	/**
	 * 获取Field显示类型. 分别：1.只读(READONLY)2.修改(MODIFY),3.隐藏(HIDDEN),4.屏蔽(DISABLED)
	 * 
	 * @param doc
	 *            (Document)文档对象
	 * @param runner
	 *            动态语言执行器
	 * @param webUser
	 *            web用户
	 * 
	 * @return 字段显示类型
	 * @throws Exception
	 */
	public int getDisplayType(Document doc, IRunner runner, WebUser webUser) throws Exception {

		// 根据流程中定义的权限检索字段
		int fieldPermission = PermissionType.MODIFY;
		if (doc.getFieldPermList(webUser) != null) {
			fieldPermission = doc.getFieldPermList(webUser).checkPermission(this);
		}

		// 第一优先级为hidden
		if (fieldPermission == PermissionType.HIDDEN || runBooleanScript(runner, "hiddenScript", getHiddenScript())){
//				|| checkRolePermission(webUser, OperationVO.FORMFIELD_HIDDEN)) {//字段暂无权限配置 因此注释
			return PermissionType.HIDDEN;
		}
		// 第二优先级为disable
		if (!doc.isEditAble(webUser)){
//				|| checkRolePermission(webUser, OperationVO.FORMFIELD_DISABLED)) {//字段暂无权限配置 因此注释
			return PermissionType.DISABLED;
		}
		// 第三优先级为readonly
		if (fieldPermission == PermissionType.READONLY
				|| runBooleanScript(runner, "readOnlyScript", getReadonlyScript()) ){
//				|| checkRolePermission(webUser, OperationVO.FORMFIELD_READONLY)) {//字段暂无权限配置 因此注释
			return PermissionType.READONLY;
		}

		return fieldPermission;
	}

	private boolean checkRolePermission(WebUser webUser, int operationCode) throws Exception {
		Collection<RoleVO> rolelist = webUser.getRolesByApplication(_form.getApplicationid());
		PermissionProcess permissionProcess = (PermissionProcess) ProcessFactory.createProcess(PermissionProcess.class);
		if (permissionProcess.check(rolelist, getId(), operationCode, ResVO.FORM_FIELD_TYPE, false)) {
			return false;
		}
		return false;
	}

	/**
	 * 获得表单字段的完整路径
	 * 
	 * @param formField
	 * @return
	 */
	public String getFullName() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.get_form().getFullName() + "/");
		sb.append(this.getName());
		return sb.toString();
	}

	/**
	 * 获取Field打印时显示类型，分别：1.只读(READONLY)2.修改(MODIFY),3.隐藏(HIDDEN),4.屏蔽(DISABLED)
	 * 
	 * @param doc
	 *            Document
	 * @param runner
	 *            A动态语言执行器
	 * @param webUser
	 *            webUser
	 * @see AbstractRunner#run(String, String)
	 * @return 字段显示类型
	 * @throws Exception
	 */
	public int getPrintDisplayType(Document doc, IRunner runner, WebUser webUser) throws Exception {
		int fieldPermission = PermissionType.MODIFY;
		if (doc.getFieldPermList(webUser) != null) {
			fieldPermission = doc.getFieldPermList(webUser).checkPermission(this);
		}

		if (fieldPermission == PermissionType.HIDDEN
				|| runBooleanScript(runner, "hiddenPrintScript", getHiddenPrintScript())
				|| getDisplayType(doc, runner, webUser) == PermissionType.HIDDEN) {
			// 判断打印时显示的值
			if (runBooleanScript(runner, "hiddenPrintScript", getHiddenPrintScript()))
				this.printValue = HIDDENPRINTVALUE;
			else if (runBooleanScript(runner, "hiddenScript", getHiddenScript()))
				this.printValue = HIDDENSHOWVALUE;
			else
				this.printValue = TRUEVALUE;
			return PermissionType.HIDDEN;
		}

		return fieldPermission;
	}

	/**
	 * valuescript 编辑模式 00：表示视图编辑模式；01：表示代码编辑模式
	 * 
	 * @return valuescript 编辑模式
	 * @uml.property name="editMode"
	 */
	public String getEditMode() {
		return editMode;
	}

	/**
	 * 设置 valuescript 编辑模式 00：表示视图编辑模式；01：表示代码编辑模式
	 * 
	 * @param editMode
	 * @uml.property name="editMode"
	 */
	public void setEditMode(String editMode) {
		this.editMode = editMode;
	}

	/**
	 * valuescript 视图编辑模式的过滤条件；
	 * 
	 * @return
	 * @uml.property name="filtercondition"
	 */
	public String getFiltercondition() {
		return filtercondition;
	}

	/**
	 * @param filtercondition
	 *            the filtercondition to set
	 * @uml.property name="filtercondition"
	 */
	public void setFiltercondition(String filtercondition) {
		this.filtercondition = StringUtil.dencodeHTML(filtercondition);
	}

	/**
	 * valuescript 视图编辑过程描述
	 * 
	 * @return
	 * @uml.property name="processDescription"
	 */
	public String getProcessDescription() {
		return processDescription;
	}

	/**
	 * @param processDescription
	 *            the processDescription to set
	 * @uml.property name="processDescription"
	 */
	public void setProcessDescription(String processDescription) {
		this.processDescription = processDescription;
	}

	/**
	 * 获取次序号
	 * 
	 * @return 次序号
	 * @uml.property name="seq"
	 */
	public int getSeq() {
		return seq;
	}

	/**
	 * 设置次序号
	 * 
	 * @param seq
	 *            次序号
	 * @uml.property name="seq"
	 */
	public void setSeq(int seq) {
		this.seq = seq;
	}

	protected String getContextPath(Document doc) {
		return doc.get_params().getContextPath();
	}

	protected Object getDocParameter(Document doc, String name) {
		return doc.get_params().getParameter(name);
	}

	public String getScriptLable(String suffix) {
		StringBuffer label = new StringBuffer();
		label.append("Form").append("." + _form.getName()).append(".Field(").append(getId()).append(")." + getName())
				.append("." + suffix);

		return label.toString();
	}

	/**
	 * 实现CheckboxField脚本校验.测试CheckboxField输入合法性.
	 * 通过执行CheckboxField校验脚本,校验CheckboxField输入是否合法，如不合法时返回错误信息，合法时返回为空。
	 * CheckboxField校验包括引用的校验库(ValidateLibs)与校验规则(ValidateRule).
	 * 
	 * @param runner
	 *            AbstractRunner(执行脚本接口类)
	 * @see AbstractRunner#run(String, String)
	 * @param doc
	 *            文档对象
	 * @return ValidateMessage
	 * @roseuid 41ECB66E013E
	 */
	public ValidateMessage validate(IRunner runner, Document doc) throws Exception {
		Object result = null;
		StringBuffer rtn = new StringBuffer();

		ValidateMessage msg = new ValidateMessage();
		if (getValidateLibs() != null && getValidateLibs().trim().length() > 0) {
			String libs[] = getValidateLibs().split(";");
			ValidateRepositoryProcess vp = (ValidateRepositoryProcess) ProcessFactory
					.createProcess(ValidateRepositoryProcess.class);
			for (int i = 0; i < libs.length; i++) {
				ValidateRepositoryVO vo = (ValidateRepositoryVO) vp.doView(libs[i]);
				if (vo != null) {
					String content = "";
					String functionName = getFunctionNames(vo.getContent());
					if (functionName != null) {
						String desc = getDiscript();
						if (StringUtil.isBlank(desc))
							desc = getName();
						content += functionName + "('" + getName() + "','" + desc + "');";
						content += vo.getContent();
						String validateLabel = "Validate(" + vo.getId() + ")";
						Object obj = runner.run(getScriptLable(validateLabel), content);
						if (obj instanceof String) {
							rtn.append((String) obj);
						}
					}
				}
			}
		}

		if (getValidateRule() != null && getValidateRule().trim().length() > 0) {
			result = runner.run(getScriptLable("Validate"), StringUtil.dencodeHTML(getValidateRule()));
			// Item item = doc.findItem(this.getName());
			if (result instanceof String) {
				String rs = (String) result;
				if (rs != null && rs.trim().length() > 0) {
					rtn.append(rs);

				}
			}
		}

		if (rtn != null && rtn.toString().trim().length() > 0) {
			msg.setFieldname(this.getName());
			msg.setErrmessage(rtn.toString());
			return msg;
		} else
			return null;
	}

	/**
	 * 获取javascript 函数名
	 * 
	 * @param content
	 *            脚本内容
	 * @return 函数名
	 */

	public String getFunctionNames(String content) {

		if (content == null || content.trim().length() == 0)
			return null;
		int i = content.indexOf("function");
		int j = content.substring(i + 8, content.length()).indexOf("(");
		String functionname = content.substring(i + 8, i + 8 + j);
		if (functionname != null && functionname.trim().length() > 0)
			return functionname;
		else
			return null;
	}

	/**
	 * 获取控件的刷新脚本
	 * 
	 * @param runner
	 *            动态语言执行器
	 * @param doc
	 *            文档对象
	 * @param webUser
	 *            web用户
	 * @return 刷新脚本
	 * @throws Exception
	 */
	public String getRefreshScript(IRunner runner, Document doc, WebUser webUser) throws Exception {
		return getRefreshScript(runner, doc, webUser, false);
	}

	public String getRefreshScript(IRunner runner, Document doc, WebUser webUser, boolean isHidden) throws Exception {

		StringBuffer buffer = new StringBuffer();
		// buffer.append("alert('"+this.getName()+"');");
		String divid = this.getName() + "_divid";

		String fieldHTML = "";
		if (!isHidden) {
			fieldHTML = this.toHtmlTxt(doc, runner, webUser);
			fieldHTML = fieldHTML.replaceAll("\"", "\\\\\"");
			fieldHTML = fieldHTML.replaceAll("\r\n", "");
		}

		buffer.append("refreshField(\"").append(divid).append("\",\"");
		buffer.append(this.getName()).append("\",\"").append(fieldHTML).append("\");");

		return buffer.toString();
	}

	/**
	 * 是否是只读或隐藏
	 * 
	 * @param destVal
	 *            最终值
	 * @param origVal
	 *            起始值
	 * @return
	 */
	public boolean isRender(String destVal, String origVal) {
		if (hiddenScript != null && hiddenScript.trim().length() > 0) {
			return true;
		} else if (readonlyScript != null && readonlyScript.trim().length() > 0) {
			return true;
		} else if (destVal != null && !destVal.equals(origVal)) {
			return true;
		}

		return false;
	}

	/**
	 * 是否是修改文档的Item值
	 * 
	 * @param modifiedDoc
	 *            修改文档
	 * @param newValue
	 *            新值
	 * @return
	 */
	public boolean isModified(Collection<Document> modifiedDoc, Object newValue) {
		boolean flag = false;
		if (modifiedDoc != null && modifiedDoc.size() > 0) {
			Iterator<Document> iter = modifiedDoc.iterator();
			while (iter.hasNext()) {
				Document doc = iter.next();
				if (doc != null) {
					Item item = doc.findItem(this.getName());
					if (item != null) {
						Object oldValue = item.getValue();
						if (oldValue != null) {
							if ((oldValue instanceof Date) && (newValue instanceof Date)) {
								Date oldItemValue = (Date) oldValue;
								Date newItemValue = (Date) newValue;
								if (oldItemValue != null && newItemValue != null
										&& oldItemValue.compareTo(newItemValue) != 0) {
									flag = true;
									break;
								}
							} else {
								if (!oldValue.equals(newValue)) {
									flag = true;
									break;
								}
							}
						} else if (newValue != null) {
							if ((newValue instanceof String) && (!newValue.equals("null"))
									&& (!(newValue.toString().equals("")))) {
								flag = true;
								break;
							} else if ((newValue instanceof Number)) {
								if (new Double(String.valueOf(newValue)).doubleValue() > 0) {
									flag = true;
									break;
								}

							} else if ((newValue instanceof Date) && newValue != null) {
								flag = true;
								break;
							}

						}
					}
				}

			}
		}
		return flag;
	}

	/**
	 * 是否支持手机显示
	 * 
	 * @return 支持手机显示
	 */
	public boolean isMobile() {
		return mobile;
	}

	/**
	 * 设置支持手机显示
	 * 
	 * @param mobile
	 *            支持手机显示
	 */
	public void setMobile(boolean mobile) {
		this.mobile = mobile;
	}

	/**
	 * 获取组件名
	 * 
	 * @return 组件名
	 */
	public String getTagName() {
		return ObjectUtil.getSimpleName(getClass());
	}

	protected String getFieldId(Document doc) {
		return doc.getId() + "_" + getName(); // DocumentID + FieldName
	}

	/**
	 * 获取字段显示值
	 * 
	 * @param doc
	 * @return
	 * @throws Exception
	 */
	public String getText(Document doc, IRunner runner, WebUser webUser) throws Exception {
		if (!StringUtil.isBlank(doc.getParentid())) {
			int displayType = getDisplayType(doc, runner, webUser);
			if (displayType == PermissionType.HIDDEN) {
				return this.getHiddenValue();
			}
		}

		return doc.getItemValueAsString(getName(), this);
	}

	/**
	 * 获取字段真实值
	 * 
	 * @param doc
	 * @param runner
	 * @param webUser
	 * @return
	 * @throws Exception
	 */
	public String getValue(Document doc, IRunner runner, WebUser webUser) throws Exception {
		if (!StringUtil.isBlank(doc.getParentid())) {
			int displayType = getDisplayType(doc, runner, webUser);
			if (displayType == PermissionType.HIDDEN) {
				return this.getHiddenValue();
			}
		}

		return doc.getItemValueAsString(getName(), this);
	}

	public String getValueMapScript() {
		StringBuffer scriptBuffer = new StringBuffer();
		scriptBuffer.append("valuesMap['" + this.getName() + "'] = ev_getValue('" + this.getName() + "');");

		return scriptBuffer.toString();
	}

	public String toPdfHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		StringBuffer html = new StringBuffer();

		if (doc != null) {
			int displayType = getPrintDisplayType(doc, runner, webUser);

			if (displayType == PermissionType.HIDDEN) {
				if (this.printValue == HIDDENPRINTVALUE)
					return "<![CDATA[" + printHiddenValue + "]]>";
				if (this.printValue == HIDDENSHOWVALUE)
					return getHiddenValue();
			}

			if (!getTextType().equalsIgnoreCase("hidden")) {
				Item item = doc.findItem(this.getName());
				if (item != null && item.getValue() != null) {
					html.append("<SPAN style=\"FONT-SIZE: 9pt\">");
					html.append("<![CDATA[" + item.getValue() + "]]>");
					html.append("</SPAN>");
				}
			}
		}
		return html.toString();
	}

	public String printHiddenElement(Document doc) {
		StringBuffer html = new StringBuffer();
		Item item = doc.findItem(this.getName());
		if (item == null)
			return html.toString();
		if (item.getValue() == null) {
			html.append("<input type='hidden' name=\"" + item.getName() + "\" value='' />");
		} else {
			html.append("<input type='hidden' name=\"" + item.getName() + "\" value=\"" + item.getValue() + "\" />");
		}
		return html.toString();
	}

	public boolean isBorderType() {
		return borderType;
	}

	public void setBorderType(boolean borderType) {
		this.borderType = borderType;
	}

	public Logger getLog() {
		return Logger.getLogger(this.getClass());
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHiddenValue() {
		return StringUtil.dencodeHTML(hiddenValue);
	}

	public void setHiddenValue(String hiddenValue) {
		if (hiddenValue != null)
			this.hiddenValue = hiddenValue;
		else
			this.hiddenValue = "";
	}

	public String getPrintHiddenValue() {
		if (this.printValue == HIDDENPRINTVALUE)
			return HtmlEncoder.encode(printHiddenValue);
		if (this.printValue == HIDDENSHOWVALUE)
			return getHiddenValue();
		return "";
	}

	public void setPrintHiddenValue(String printHiddenValue) {
		this.printHiddenValue = printHiddenValue;
	}

}
