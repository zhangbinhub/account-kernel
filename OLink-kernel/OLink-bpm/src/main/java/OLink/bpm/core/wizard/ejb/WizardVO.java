package OLink.bpm.core.wizard.ejb;

import java.io.Serializable; //import java.util.HashSet;
import java.util.Map; //import java.util.Set;
import java.util.TreeMap;

import OLink.bpm.base.dao.ValueObject;

//import ActivityType;

/**
 * WizardVO class.
 * 
 * @author zhuxuehong, Sam
 * @since JDK1.4
 */
public class WizardVO extends ValueObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -423071484839993694L;

	/**
	 * 模块名字
	 */
	String m_name;
	/**
	 * 模块序列号
	 */
	int m_order;
	/**
	 * 模块描述
	 */
	String m_description;
	/**
	 * 模块ID
	 */
	String moduleid;

	// step2_form
	/**
	 * 表单ID
	 */
	String f_formid;

	/**
	 * 表单名字
	 */
	String f_name;
	/**
	 * 表单中所有的Activity数组
	 */
	String f_activitys[];
	/**
	 * 表单描述
	 */
	String f_description;
	/**
	 * 表单类型
	 */
	String f_Type;
	/**
	 * 表单中字段(Field)描述
	 */
	String f_fieldsdescription;
	/**
	 * 表单中编辑器内容上下文
	 */
	String f_templatecontext;
	/**
	 * 表单样式
	 */
	String f_style;

	/**
	 * 从表单ID
	 */
	String f_formId_sub;

	/**
	 * 从表单名字
	 */
	String f_name_sub;
	/**
	 * 从表单中所有的Activity数组
	 */
	String f_activitys_sub[];
	/**
	 * 从表单描述
	 */
	String f_description_sub;

	/**
	 * 从表单中字段(Field)描述
	 */
	String f_fieldsdescription_sub;
	/**
	 * 从表单中编辑器内容上下文
	 */
	String f_templatecontext_sub;
	/**
	 * 从表单样式
	 */
	String f_style_sub;

	/**
	 * 从表单所属VIEW标识
	 */
	String f_subForm_viewid;

	/**
	 * 从表单所属VIEW要显示的列
	 */
	String f_subForm_viewColumns;

	/**
	 * 从表单所属VIEW要显示的Activity
	 */
	String[] f_subForm_viewActivitys;

	/**
	 * 从表单版本
	 */
	int version_sub;

	// step4_resource
	/**
	 * 在Menu导航中所在的序列号
	 */
	int r_orderno;
	/**
	 * Menu中的描述
	 */
	String r_description;

	/**
	 * Menu所属上级菜单
	 */
	String r_superior;

	// step5_view
	public final static String VIEWDISPLAY_ALL = "All"; // 显示所有信息列表

	public final static String VIEWDISPLAY_PENDING = "Pending"; // 显示待办所有信息列表

	/**
	 * 是否待办
	 */
	private boolean isPending = false;

	/**
	 * 视图名字
	 */
	String v_name;
	/**
	 * 视图描述
	 */
	String v_description;
	/**
	 * 视图中所有的Activity数组
	 */
	String[] v_activity;
	/**
	 * 视图是否分页
	 */
	String v_isPagination = "true";
	/**
	 * 视图如需要分页,每页显示的页数
	 */
	String v_pagelines;
	/**
	 * 视图可选择的类型
	 */
	String[] v_type;
	/**
	 * 视图中是否选择显示总行数
	 */
	String v_isShowTotalRow;
	/**
	 * 视图中要显示的列
	 */
	String v_columns;
	/**
	 * 过滤器代码
	 */
	String v_filterField;
	/**
	 * 过滤器
	 */
	String v_filter;
	/**
	 * 视图使用的搜索表单内容
	 */
	String v_searchForm;
	/**
	 * 已经选择的字段
	 */
	String selectFields;

	String selectFields_sub;

	// step3_dept/role

	String d_name;

	String r_adminname;

	String r_directorname;

	// step3_workflow
	/**
	 * 工作流ID
	 */
	String w_workflowid;
	/**
	 * 工作流名字
	 */
	String w_name;
	/**
	 * 工作流类型
	 */
	String w_flowType;
	/**
	 * 工作流内容
	 */
	String w_content;
	/**
	 * 表单Activity列表
	 */
	public static Map<String, String> _FORMACLIST = new TreeMap<String, String>();
	/**
	 * 视图Activity列表
	 */
	public static Map<String, String> _VIEWACLIST = new TreeMap<String, String>();

	/**
	 * Activity列表
	 */
	// public static Map _ACTIVITYLIST = new TreeMap();
	/**
	 * 初始化视图和表单Activity列表
	 */
	static {
		// 视图Activity列表
		_VIEWACLIST.put("1", "Query");
		_VIEWACLIST.put("2", "Create");
		_VIEWACLIST.put("3", "Delete");
		_VIEWACLIST.put("4", "Update");
		_VIEWACLIST.put("16", "Excel_Export");

		// 表单Activity列表
		_FORMACLIST.put("4", "Save");
		_FORMACLIST.put("8", "Close");
		_FORMACLIST.put("9", "Save&Close");
		_FORMACLIST.put("10", "Back");
		_FORMACLIST.put("11", "Save&Back");
		_FORMACLIST.put("12", "Save&New");
		_FORMACLIST.put("5", "Submit_WorkFlow");

		// //Activity列表
		// _ACTIVITYLIST.put(ActivityType.DOCUMENT_QUERY, "Query");
		// _ACTIVITYLIST.put(ActivityType.DOCUMENT_CREATE, "Create");
		// _ACTIVITYLIST.put(ActivityType.DOCUMENT_DELETE, "Delete");
		// _ACTIVITYLIST.put(ActivityType.DOCUMENT_UPDATE, "Save");
		// _ACTIVITYLIST.put(ActivityType.WORKFLOW_PROCESS, "WorkFlow");
		// _ACTIVITYLIST.put(ActivityType.CLOSE_WINDOW, "Close");
		// _ACTIVITYLIST.put(ActivityType.SAVE_CLOSE_WINDOW, "Save and Close");
		// _ACTIVITYLIST.put(ActivityType.DOCUMENT_BACK, "Back");
		// _ACTIVITYLIST.put(ActivityType.SAVE_BACK, "Save and Back");
		// _ACTIVITYLIST.put(ActivityType.SAVE_NEW_WITHOUT_OLD, "Save and New");
	}

	// public static Map get_ACTIVITYLIST() {
	// return _ACTIVITYLIST;
	// }
	//
	// public static void set_ACTIVITYLIST(Map _activitylist) {
	// _ACTIVITYLIST = _activitylist;
	// }
	/**
	 * 获取表单Activity列表
	 * 
	 * @return 表单Activity集合
	 */
	public static Map<String, String> get_FORMACLIST() {
		return _FORMACLIST;
	}

	/**
	 * 设置表单按键集合
	 * 
	 * @param _formaclist
	 *            表单按键集合
	 */
	public static void set_FORMACLIST(Map<String, String> _formaclist) {
		_FORMACLIST = _formaclist;
	}

	/**
	 * 获取视图按钮集合
	 * 
	 * @return 视图按钮集合
	 */
	public static Map<String, String> get_VIEWACLIST() {
		return _VIEWACLIST;
	}

	/**
	 * 设置视图按钮集合
	 * 
	 * @param _viewaclist
	 *            视图按钮集合
	 */
	public static void set_VIEWACLIST(Map<String, String> _viewaclist) {
		_VIEWACLIST = _viewaclist;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	/**


	/**
	 * 获取表单中所有的按钮数组
	 * 
	 * @return 表单中所有的按钮数组
	 */
	public String[] getF_activitys() {
		return f_activitys;
	}

	/**
	 * 设置表单中所有的按钮数组
	 * 
	 * @param f_activitys
	 *            表单中所有的按钮数组
	 */
	public void setF_activitys(String[] f_activitys) {
		this.f_activitys = f_activitys;
	}

	/**
	 * 获取表单名字
	 * 
	 * @return 表单名字
	 */
	public String getF_name() {
		return f_name;
	}

	/**
	 * 获取从表单所属视图要显示的列
	 * 
	 * @return 从表单所属视图要显示的列
	 */
	public String getF_subForm_viewColumns() {
		return f_subForm_viewColumns;
	}

	/**
	 * 设置从表单所属视图要显示的列
	 * 
	 * @param form_viewColumns
	 *            从表单所属视图要显示的列
	 */
	public void setF_subForm_viewColumns(String form_viewColumns) {
		f_subForm_viewColumns = form_viewColumns;
	}

	/**
	 * 获取从表单所属视图标识
	 * 
	 * @return 视图标识
	 */
	public String getF_subForm_viewid() {
		return f_subForm_viewid;
	}

	/**
	 * 设置从表单所属视图标识
	 * 
	 * @param form_viewid
	 *            视图标识
	 */
	public void setF_subForm_viewid(String form_viewid) {
		f_subForm_viewid = form_viewid;
	}

	/**
	 * 设置表单名字
	 * 
	 * @param f_name
	 *            表单名字
	 */
	public void setF_name(String f_name) {
		this.f_name = f_name;
	}

	/**
	 * 获取表单描述
	 * 
	 * @return 表单描述
	 */
	public String getF_description() {
		return f_description;
	}

	/**
	 * 设置表单描述
	 * 
	 * @param f_description
	 */
	public void setF_description(String f_description) {
		this.f_description = f_description;
	}

	/**
	 * 获取模块描述
	 * 
	 * @return 模块描述
	 */
	public String getM_description() {
		return m_description;
	}

	/**
	 * 设置模块描述
	 * 
	 * @param m_description
	 *            模块描述
	 */
	public void setM_description(String m_description) {
		this.m_description = m_description;
	}

	/**
	 * 获取模块名字
	 * 
	 * @return 模块名字
	 */
	public String getM_name() {
		return m_name;
	}

	/**
	 * 设置模块名字
	 * 
	 * @param m_name
	 *            模块名字
	 */
	public void setM_name(String m_name) {
		this.m_name = m_name;
	}
	/**
	 * 获取模块顺序号
	 * 
	 * @return 模块顺序号
	 */
	public int getM_order() {
		return m_order;
	}
	/**
	 * 设置模块顺序号
	 * 
	 * @param m_order
	 *            模块顺序号
	 */
	public void setM_order(int m_order) {
		this.m_order = m_order;
	}

	/**
	 * 获取菜单描述
	 * 
	 * @return 菜单描述
	 */
	public String getR_description() {
		return r_description;
	}

	/**
	 * 设置菜单描述
	 * 
	 * @param r_description
	 *            菜单描述
	 */
	public void setR_description(String r_description) {
		this.r_description = r_description;
	}

	/**
	 * 获取在Menu导航中所在的序列号
	 * 
	 * @return 序列号
	 */
	public int getR_orderno() {
		return r_orderno;
	}

	/**
	 * 设置在Menu导航中所在的序列号
	 * 
	 * @param r_orderno
	 *            序列号
	 */
	public void setR_orderno(int r_orderno) {
		this.r_orderno = r_orderno;
	}

	/**
	 * 获取视图中所有的Activity数组
	 * 
	 * @return 视图中所有的Activity数组
	 */
	public String[] getV_activity() {
		return v_activity;
	}

	/**
	 * 设置视图中所有的按钮数组
	 * 
	 * @param v_activity
	 *            视图中所有的Activity数组
	 */
	public void setV_activity(String[] v_activity) {
		this.v_activity = v_activity;
	}

	/**
	 * 设置视图描述
	 * 
	 * @return 视图描述
	 */
	public String getV_description() {
		return v_description;
	}

	/**
	 * 获取视图描述
	 * 
	 * @param v_description
	 *            视图描述
	 */
	public void setV_description(String v_description) {
		this.v_description = v_description;
	}

	/**
	 * 获取视图名
	 * 
	 * @return 视图名
	 */
	public String getV_name() {
		return v_name;
	}

	/**
	 * 设置视图名
	 * 
	 * @param v_name
	 */
	public void setV_name(String v_name) {
		this.v_name = v_name;
	}

	/**
	 * 获取工作流的名字
	 * 
	 * @return 工作流的名字
	 */
	public String getW_name() {
		return w_name;
	}

	/**
	 * 设置工作流的名字
	 * 
	 * @param w_name
	 *            工作流的名字
	 */
	public void setW_name(String w_name) {
		this.w_name = w_name;
	}

	/**
	 * 获取视图是否分页
	 * 
	 * @return 视图是否分页
	 */
	public String getV_isPagination() {
		return v_isPagination;
	}

	/**
	 * 设置视图是否分页
	 * 
	 * @param pagination
	 *            视图是否分页
	 */
	public void setV_isPagination(String pagination) {
		v_isPagination = pagination;
	}

	/**
	 * 获取视图中是否选择显示总行数
	 * 
	 * @return 总行数
	 */
	public String getV_isShowTotalRow() {
		return v_isShowTotalRow;
	}

	/**
	 * 设置视图中是否选择显示总行数
	 * 
	 * @param showTotalRow
	 *            显示总行数
	 */
	public void setV_isShowTotalRow(String showTotalRow) {
		v_isShowTotalRow = showTotalRow;
	}

	/**
	 * 获取视图如需要分页,每页显示的页数
	 * 
	 * @return 视图如需要分页,每页显示的页数
	 */
	public String getV_pagelines() {
		return v_pagelines;
	}

	/**
	 * 设置视图如需要分页,每页显示的页数
	 * 
	 * @param v_pagelines
	 *            视图如需要分页,每页显示的页数
	 */
	public void setV_pagelines(String v_pagelines) {
		this.v_pagelines = v_pagelines;
	}

	/**
	 * 获取部门名称
	 * 
	 * @return 部门名称
	 */
	public String getD_name() {
		return d_name;
	}

	/**
	 * 设置部门名称
	 * 
	 * @param d_name
	 *            部门名称
	 */
	public void setD_name(String d_name) {
		this.d_name = d_name;
	}

	/**
	 * 获取角色名
	 * 
	 * @return 角色名
	 */
	public String getR_adminname() {
		return r_adminname;
	}

	/**
	 * 设置角色名
	 * 
	 * @param r_adminname
	 *            角色名
	 */
	public void setR_adminname(String r_adminname) {
		this.r_adminname = r_adminname;
	}

	/**
	 * 流程关联的角色名
	 * 
	 * @return 角色名
	 */
	public String getR_directorname() {
		return r_directorname;
	}

	/**
	 * 设置流程关联的角色名
	 * 
	 * @param r_directorname
	 *            角色名
	 */
	public void setR_directorname(String r_directorname) {
		this.r_directorname = r_directorname;
	}

	/**
	 * 获取模块标识
	 * 
	 * @return 模块标识
	 */
	public String getModuleid() {
		return moduleid;
	}

	/**
	 * 设置模块标识
	 * 
	 * @param moduleid
	 *            模块标识
	 */
	public void setModuleid(String moduleid) {
		this.moduleid = moduleid;
	}

	/**
	 * 获取表单中字段(Field)描述
	 * 
	 * @return 表单中字段(Field)描述
	 */
	public String getF_fieldsdescription() {
		return f_fieldsdescription;
	}

	/**
	 * 设置表单中字段(Field)描述
	 * 
	 * @param f_fieldsdescription
	 *            表单中字段(Field)描述
	 */
	public void setF_fieldsdescription(String f_fieldsdescription) {
		this.f_fieldsdescription = f_fieldsdescription;
	}

	/**
	 * 获取表单类型
	 * 
	 * @return 表单类型
	 */
	public String getF_Type() {
		return f_Type;
	}

	/**
	 * 设置表单类型
	 * 
	 * @param type
	 *            表单类型
	 */
	public void setF_Type(String type) {
		f_Type = type;
	}

	/**
	 * 获取表单中编辑器内容上下文
	 * 
	 * @return 表单中编辑器内容上下文
	 */
	public String getF_templatecontext() {
		return f_templatecontext;
	}

	/**
	 * 设置表单中编辑器内容上下文
	 * 
	 * @param f_templatecontext
	 *            表单中编辑器内容上下文
	 */
	public void setF_templatecontext(String f_templatecontext) {
		this.f_templatecontext = f_templatecontext;
	}

	/**
	 * 获取表单样式
	 * 
	 * @return 表单样式
	 */
	public String getF_style() {
		return f_style;
	}

	/**
	 * 设置表单样式
	 * 
	 * @param f_style
	 *            表单样式
	 */
	public void setF_style(String f_style) {
		this.f_style = f_style;
	}

	/**
	 * 获取表单标识
	 * 
	 * @return 表单标识
	 */
	public String getF_formid() {
		return f_formid;
	}

	/**
	 * 设置表单标识
	 * 
	 * @param f_formid
	 *            表单标识
	 */
	public void setF_formid(String f_formid) {
		this.f_formid = f_formid;
	}

	/**


	/**
	 * 获取视图可选择的类型
	 * 
	 * @return 视图可选择的类型
	 */
	public String[] getV_type() {
		return v_type;
	}

	/**
	 * 设置视图可选择的类型
	 * 
	 * @param v_type
	 *            视图可选择的类型
	 */
	public void setV_type(String[] v_type) {
		this.v_type = v_type;
	}

	/**
	 * 获取视图中要显示的列
	 * 
	 * @return 视图中要显示的列
	 */
	public String getV_columns() {
		return v_columns;
	}

	/**
	 * 设置视图中要显示的列
	 * 
	 * @param v_columns
	 *            视图中要显示的列
	 */
	public void setV_columns(String v_columns) {
		this.v_columns = v_columns;
	}

	/**
	 * 获取 过滤器代码
	 * 
	 * @return 过滤器代码
	 */
	public String getV_filterField() {
		return v_filterField;
	}

	/**
	 * 设置 过滤器代码
	 * 
	 * @param field
	 *            过滤器代码
	 */
	public void setV_filterField(String field) {
		v_filterField = field;
	}

	/**
	 * 获取工作流类型
	 * 
	 * @return 工作流类型
	 */
	public String getW_flowType() {
		return w_flowType;
	}

	/**
	 * 设置工作流类型
	 * 
	 * @param type
	 *            工作流类型
	 */
	public void setW_flowType(String type) {
		w_flowType = type;
	}

	/**
	 * 设置过滤器
	 * 
	 * @return 过滤器
	 */
	public String getV_filter() {
		return v_filter;
	}

	/**
	 * 设置过滤器
	 * 
	 * @param v_filter
	 *            过滤器
	 */
	public void setV_filter(String v_filter) {
		this.v_filter = v_filter;
	}

	/**
	 * 获取视图使用的搜索表单内容
	 * 
	 * @return 视图使用的搜索表单内容
	 */
	public String getV_searchForm() {
		return v_searchForm;
	}

	/**
	 * 设置视图使用的搜索表单内容
	 * 
	 * @param form
	 *            视图使用的搜索表单内容
	 */
	public void setV_searchForm(String form) {
		v_searchForm = form;
	}

	/**
	 * 获取工作流内容
	 * 
	 * @return 工作流内容
	 */
	public String getW_content() {
		return w_content;
	}

	/**
	 * 设置工作流内容
	 * 
	 * @param w_content
	 *            工作流内容
	 */
	public void setW_content(String w_content) {
		this.w_content = w_content;
	}

	/**
	 * 获取工作流标识
	 * 
	 * @return 工作流标识
	 */
	public String getW_workflowid() {
		return w_workflowid;
	}

	/**
	 * 设置工作流标识
	 * 
	 * @param w_workflowid
	 *            工作流标识
	 */
	public void setW_workflowid(String w_workflowid) {
		this.w_workflowid = w_workflowid;
	}

	/**
	 * 获取已经选择的字段
	 * 
	 * @return 已经选择的字段
	 */
	public String getSelectFields() {
		return selectFields;
	}

	/**
	 * 设置已经选择的字段
	 * 
	 * @param selectFields
	 *            已经选择的字段
	 */
	public void setSelectFields(String selectFields) {
		this.selectFields = selectFields;
	}

	/**
	 * 获取是否待办
	 * 
	 * @return 是否待办
	 */
	public boolean isPending() {
		return isPending;
	}

	/**
	 * 设置是否待办
	 * 
	 * @param isPendding
	 *            是否待办
	 */
	public void setPending(boolean isPendding) {
		this.isPending = isPendding;
	}

	/**
	 * 获取从表单中所有的Activity数组
	 * 
	 * @return 从表单中所有的Activity数组
	 */
	public String[] getF_activitys_sub() {
		return f_activitys_sub;
	}

	/**
	 * 设置从表单中所有的Activity数组
	 * 
	 * @param f_activitys_sub
	 *            从表单中所有的Activity数组
	 */
	public void setF_activitys_sub(String[] f_activitys_sub) {
		this.f_activitys_sub = f_activitys_sub;
	}

	/**
	 * 获取从表单描述
	 * 
	 * @return 从表单描述
	 */
	public String getF_description_sub() {
		return f_description_sub;
	}

	/**
	 * 设置从表单描述
	 * 
	 * @param f_description_sub
	 *            从表单描述
	 */
	public void setF_description_sub(String f_description_sub) {
		this.f_description_sub = f_description_sub;
	}

	/**
	 * 获取从表单中字段(Field)描述
	 * 
	 * @return 从表单中字段(Field)描述
	 */
	public String getF_fieldsdescription_sub() {
		return f_fieldsdescription_sub;
	}

	/**
	 * 设置从表单中字段(Field)描述
	 * 
	 * @param f_fieldsdescription_sub
	 *            从表单中字段(Field)描述
	 */
	public void setF_fieldsdescription_sub(String f_fieldsdescription_sub) {
		this.f_fieldsdescription_sub = f_fieldsdescription_sub;
	}

	/**
	 * 获取从表单名字
	 * 
	 * @return 从表单名字
	 */
	public String getF_name_sub() {
		return f_name_sub;
	}

	/**
	 * 设置从表单名字
	 * 
	 * @param f_name_sub
	 *            从表单名字
	 */
	public void setF_name_sub(String f_name_sub) {
		this.f_name_sub = f_name_sub;
	}

	/**
	 * 获取从表单样式
	 * 
	 * @return 从表单样式
	 */
	public String getF_style_sub() {
		return f_style_sub;
	}

	/**
	 * 设置从表单样式
	 * 
	 * @param f_style_sub
	 *            从表单样式
	 */
	public void setF_style_sub(String f_style_sub) {
		this.f_style_sub = f_style_sub;
	}

	/**
	 * 获取从表单中编辑器内容上下文
	 * 
	 * @return 从表单中编辑器内容上下文
	 */
	public String getF_templatecontext_sub() {
		return f_templatecontext_sub;
	}

	/**
	 * 设置从表单中编辑器内容上下文
	 * 
	 * @param f_templatecontext_sub
	 *            从表单中编辑器内容上下文
	 */
	public void setF_templatecontext_sub(String f_templatecontext_sub) {
		this.f_templatecontext_sub = f_templatecontext_sub;
	}

	/**
	 * 获取从表单标识
	 * 
	 * @return 从表单标识
	 */
	public String getF_formId_sub() {
		return f_formId_sub;
	}

	/**
	 * 设置从表单标识
	 * 
	 * @param id_sub
	 *            从表单标识
	 */
	public void setF_formId_sub(String id_sub) {
		f_formId_sub = id_sub;
	}

	/**
	 * 获取从表单的版本
	 * 
	 * @return 从表单的版本
	 */
	public int getVersion_sub() {
		return version_sub;
	}

	/**
	 * 设置从表单的版本
	 * 
	 * @param version_sub
	 *            版本
	 */
	public void setVersion_sub(int version_sub) {
		this.version_sub = version_sub;
	}

	/**
	 * 获取显示的Activity
	 * 
	 * @return 显示的Activity
	 */
	public String[] getF_subForm_viewActivitys() {
		return f_subForm_viewActivitys;
	}

	/**
	 * 设置从表单所属VIEW要显示的Activity
	 * 
	 * @param form_viewActivitys
	 *            显示的Activity
	 */
	public void setF_subForm_viewActivitys(String[] form_viewActivitys) {
		f_subForm_viewActivitys = form_viewActivitys;
	}

	/**
	 * 设置子表单
	 * 
	 * @return 子表单
	 */
	public String getSelectFields_sub() {
		return selectFields_sub;
	}

	/**
	 * 设置子表单
	 * 
	 * @param selectFields_sub
	 *            子表单标识
	 */
	public void setSelectFields_sub(String selectFields_sub) {
		this.selectFields_sub = selectFields_sub;
	}

	/**
	 * 获取Menu所属上级菜单
	 * 
	 * @return 上级菜单
	 */
	public String getR_superior() {
		return r_superior;
	}

	/**
	 * 设置Menu所属上级菜单
	 * 
	 * @param r_superior
	 *            上级菜单
	 */
	public void setR_superior(String r_superior) {
		this.r_superior = r_superior;
	}
}
