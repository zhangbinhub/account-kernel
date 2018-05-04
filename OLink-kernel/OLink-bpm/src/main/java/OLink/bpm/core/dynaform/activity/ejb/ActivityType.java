package OLink.bpm.core.dynaform.activity.ejb;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpSession;

import OLink.bpm.constans.Environment;
import OLink.bpm.core.dynaform.PermissionType;
import OLink.bpm.core.image.repository.ejb.ImageRepositoryProcess;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import OLink.bpm.core.image.repository.ejb.ImageRepositoryVO;
import org.apache.log4j.Logger;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.webwork.portlet.context.PortletActionContext;

public abstract class ActivityType implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3210615361376221094L;

	public static final Logger log = Logger.getLogger(ActivityType.class);

	public ActivityType(Activity act) {
		this.act = act;
	}

	/**
	 * 按钮类型为查询Document.
	 */
	public static final int DOCUMENT_QUERY = 1;

	/**
	 * 按钮类型为创建Document.
	 */
	public static final int DOCUMENT_CREATE = 2;

	/**
	 * 按钮类型为删除Document.
	 */
	public static final int DOCUMENT_DELETE = 3;

	/**
	 * 按钮类型为更新Document.
	 */
	public static final int DOCUMENT_UPDATE = 34;

	/**
	 * 按钮类型为流程处理
	 */
	public static final int WORKFLOW_PROCESS = 5;

	/**
	 * 按钮类型为SCRIPT处理
	 */
	public static final int SCRIPT_PROCESS = 6;

	/**
	 * 按钮类型为修改Document
	 */
	public static final int DOCUMENT_MODIFY = 7;

	/**
	 * 按钮类型为关闭窗口
	 */
	public static final int CLOSE_WINDOW = 8;

	/**
	 * 按钮类型为保存并窗口
	 */
	public static final int SAVE_CLOSE_WINDOW = 9;

	/**
	 * 按钮类型为回退
	 */
	public static final int DOCUMENT_BACK = 10;

	/**
	 * 按钮类型为保存并回退
	 */
	public static final int SAVE_BACK = 11;

	/**
	 * 按钮类型保存并新建(新建有一条有旧数据Document)
	 */
	public static final int SAVE_NEW_WITH_OLD = 12;

	/**
	 * 没任何操作
	 */
	public static final int NOTHING = 13;

	/**
	 * 按钮类型为打印
	 */
	public static final int PRINT = 14;

	/**
	 * 按钮类型为连同流程图一起打印
	 */
	public static final int PRINT_WITHFLOWHIS = 15;

	/**
	 * 按钮类型为导出Excel
	 */
	public static final int EXPTOEXCEL = 16;

	/**
	 * 按钮类型为保存并新建(新建一条空Document)
	 */
	public static final int SAVE_NEW_WITHOUT_OLD = 17;

	/**
	 * 按钮类型为清掉所有这个form的数据
	 */

	public static final int CLEAR_ALL = 18;

	/**
	 * 按钮类型为保存但不执行校验
	 */
	public static final int SAVE_WITHOUT_VALIDATE = 19;

	/**
	 * 按钮类型为批量审批按钮
	 */
	public static final int BATCH_APPROVE = 20;

	/**
	 * 按钮类型为复制按钮
	 */
	public static final int DOCUMENT_COPY = 21;

	/**
	 * 按钮类型为查看流程图
	 */
	public static final int DOCUEMNT_VIEWFLOWIMAGE = 22;

	/**
	 * 按钮类型为编辑当前审批人
	 */
	public static final int DOCUMENT_EDIT_AUDITOR = 24;

	/**
	 * 按钮类型为PDF导出
	 */
	public static final int EXPTOPDF = 25;

	/**
	 * 按钮类型为文件下载
	 */
	public static final int FILE_DOWNLOAD = 26;

	/**
	 * 按钮类型为 Excel导入
	 */
	public static final int EXCEL_IMPORT = 27;
	/**
	 * 按钮类型为 电子签章
	 */
	public static final int SIGNATURE = 28;
	/**
	 * 按钮类型为 批量电子签章
	 */
	public static final int BATCHSIGNATURE = 29;

	/**
	 * 按钮类型为FLEX打印
	 */
	public static final int FLEX_PRINT = 30;

	/**
	 * 按钮类型为FLEX带流程历史打印
	 */
	public static final int FLEX_PRINT_WITHFLOWHIS = 31;

	/**
	 * 按钮类型为跳转操作
	 */
	public static final int JUMP = 32;

	/**
	 * start workflow Button; 2010-9-26
	 */
	public static final int START_WORKFLOW = 33;
	/*
	 * save and start workflow button
	 */
	public static final int SAVE_SARTWORKFLOW = 4;

	/**
	 * 按钮类型为流程回撤操作
	 */
	public static final int WORKFLOW_RETRACEMENT = 35;
	
	/**
	 * 按钮类型为视图打印
	 */
	public static final int PRINT_VIEW = 36;

	private static final Map<String, String> formActivityTypeMap;

	private static final Map<String, String> viewActivityTypeMap;

	static {
		formActivityTypeMap = new LinkedHashMap<String, String>();
		formActivityTypeMap.put(NOTHING + "", "Nothing");
		// formActivityTypeMap.put(DOCUMENT_QUERY + "", "Query");
		formActivityTypeMap.put(DOCUMENT_UPDATE + "", "Save_Without_Workflow");
		formActivityTypeMap.put(SAVE_SARTWORKFLOW + "", "Save_With_Workflow");
		formActivityTypeMap.put(SAVE_BACK + "", "Save_Back");
		formActivityTypeMap.put(SAVE_NEW_WITH_OLD + "", "Save_New_With_Old");
		formActivityTypeMap.put(SAVE_NEW_WITHOUT_OLD + "", "Save_New_WithOut_Old");
		formActivityTypeMap.put(SAVE_WITHOUT_VALIDATE + "", "Save_WithOut_Validate");
		formActivityTypeMap.put(DOCUMENT_COPY + "", "Save_Copy");
		formActivityTypeMap.put(SAVE_CLOSE_WINDOW + "", "Save_Close_Window");
		formActivityTypeMap.put(DOCUMENT_BACK + "", "Back");
		formActivityTypeMap.put(PRINT + "", "Print");
		formActivityTypeMap.put(PRINT_WITHFLOWHIS + "", "Print_With_FlowHis");
		formActivityTypeMap.put(WORKFLOW_PROCESS + "", "WorkflowProcess");
		formActivityTypeMap.put(WORKFLOW_RETRACEMENT + "", "WorkflowRetracement");
		formActivityTypeMap.put(START_WORKFLOW + "", "StartupWorkflow");
		// formActivityTypeMap.put(SCRIPT_PROCESS + "",// "ScriptProcess");
		formActivityTypeMap.put(CLOSE_WINDOW + "", "Close_Window");
		formActivityTypeMap.put(DOCUMENT_EDIT_AUDITOR + "", "Edit_Auditor");
		formActivityTypeMap.put(EXPTOPDF + "", "PDF_Export");
		formActivityTypeMap.put(FILE_DOWNLOAD + "", "File_Download");
		formActivityTypeMap.put(SIGNATURE + "", "Signature");
		formActivityTypeMap.put(FLEX_PRINT + "", "Flex_Print");
		formActivityTypeMap.put(JUMP + "", "Jump");

		viewActivityTypeMap = new LinkedHashMap<String, String>();
		viewActivityTypeMap.put(DOCUMENT_QUERY + "", "Query");
		viewActivityTypeMap.put(DOCUMENT_CREATE + "", "Create");
		viewActivityTypeMap.put(DOCUMENT_DELETE + "", "Delete");
		viewActivityTypeMap.put(BATCH_APPROVE + "", "Batch_Approve");
		viewActivityTypeMap.put(EXPTOEXCEL + "", "Excel_Export");
		viewActivityTypeMap.put(CLEAR_ALL + "", "Clear_All_Datas");
		viewActivityTypeMap.put(EXCEL_IMPORT + "", "Excel_Import");
		viewActivityTypeMap.put(FILE_DOWNLOAD + "", "File_Download");
		viewActivityTypeMap.put(BATCHSIGNATURE + "", "BatchSignature");
		viewActivityTypeMap.put(PRINT_VIEW + "", "Print");
	}

	// View
	protected final static String VIEW_NAMESPACE = "/portal/dynaform/view";

	protected final static String VIEW_JSP_NAMESPACE = "/portal/dispatch/dynaform/view";
	
	protected final static String VIEW_SHARE_JSP_NAMESPACE = "/portal/share/dynaform/view";

	protected final static String VIEW_BUTTON_ID = "button_act";

	protected final static String VIEW_BUTTON_CLASS = "button-dis";

	// Document
	protected final static String DOCUMENT_NAMESPACE = "/portal/dynaform/document";

	protected final static String DOCUMENT_JSP_NAMESPACE = "/portal/dispatch/dynaform/document";

	// Document共用jsp的namespace
	protected final static String DOCUMENT_SHARE_JSP_NAMESPACE = "/portal/share/dynaform/document";

	protected final static String DOCUMENT_BUTTON_ID = "button_act";

	protected final static String DOCUMENT_BUTTON_CLASS = "button-document";

	// Icon
	protected final static String ICON_BUTTON_CLASS = "button-image";

	protected final static String DOCUMENT_BUTTON_ON_CLASS = "button-onchange";

	protected final static String ACTIVITY_NAMESPACE = "/portal/dynaform/activity";

	protected final static String ACTIVITY_JSP_NAMESPACE = "/portal/dispatch/dynaform/activity";

	protected final static String BASE_ACTION = "/";

	protected StringBuffer htmlBuilder;

	protected Activity act;

	/**
	 * 获取按钮执行后脚本
	 * 
	 * @return
	 */
	public abstract String getAfterAction();

	/**
	 * 获取按钮执行前脚本
	 * 
	 * @return 脚本
	 */
	public abstract String getBeforeAction();

	/**
	 * 获取按钮执行返回脚本
	 * 
	 * @return 脚本
	 */
	public abstract String getBackAction();

	/**
	 * 返回重定义后的html
	 * 
	 * @param permissionType
	 * @return
	 */
	public String toHtml(int permissionType) {
		htmlBuilder = new StringBuffer();
		addDefaultButton(permissionType);
		return htmlBuilder.toString();
	}

	public String toButtonHtml(int permissionType) {
		htmlBuilder = new StringBuffer();
		addButton(getInnerText(), getOnClickFunction(), "button-cmd", permissionType);
		return htmlBuilder.toString();
	}

	/**
	 * 按钮的基本函数,在onclick时调用
	 * 
	 * @return 函数的执行语句
	 */
	public abstract String getOnClickFunction();

	/**
	 * 获取按钮的标识
	 * 
	 * @return 按钮的标识
	 */
	public abstract String getButtonId();

	/**
	 * 获取按钮的默认样式
	 * 
	 * @return 默认样式
	 */
	public abstract String getDefaultClass();

	/**
	 * 获取点击按钮的默认样式
	 * 
	 * @return 默认样式
	 */
	public abstract String getDefaultOnClass();

	/**
	 * 获取按钮页面的样式
	 * 
	 * @return 样式
	 */
	public String getButtonClass() {
		if (!StringUtil.isBlank(act.getIconurl())) {
			return ICON_BUTTON_CLASS;
		} else {
			return getDefaultClass();
		}
	}

	/**
	 * 给按钮增加图片显示
	 * 
	 * @return 图片地址<img src=.......>按钮名</img>
	 */
	public String getInnerText() {
		StringBuffer textBuilder = new StringBuffer();
		try {
			if (!StringUtil.isBlank(act.getIconurl())) {
				ImageRepositoryProcess irProcess = (ImageRepositoryProcess) ProcessFactory
						.createProcess(ImageRepositoryProcess.class);
				ImageRepositoryVO imgRepository = (ImageRepositoryVO) irProcess.doView(act.getIconurl());

				textBuilder.append("<img ");
				textBuilder.append(" src='");
				String imageURI = imgRepository.getContent();
				String context = Environment.getInstance().getContext(imageURI);
				textBuilder.append(context);
				textBuilder.append("'");
				textBuilder.append("/>");
			} else {
				String name = act.getName();
				// if(name != null && name.length() > 4){
				// name = name.substring(0, 4) + "..";
				// }
				textBuilder.append("{*[" + name + "]*}");
			}
		} catch (Exception e) {
			log.warn(e.getMessage());
		}
		return textBuilder.toString();
	}

	protected void addDefaultButton() {
		addButton(getInnerText(), getOnClickFunction());
	}

	protected void addDefaultButton(int permissionType) {
		addButton(getInnerText(), getOnClickFunction(), getDefaultClass(), permissionType);
	}

	protected void addButton(String innerText, String function) {
		addButton(innerText, function, getDefaultClass(), PermissionType.MODIFY);
	}

	protected void addButton(String innerText, String function, String className, int permissionType) {

		String contextPath = "";
		if (PortletActionContext.isRender()) {
			contextPath = PortletActionContext.getRenderRequest().getContextPath();
		} else if (PortletActionContext.isEvent()) {
			try {
				contextPath = PortletActionContext.getActionRequest().getContextPath();
			} catch (Exception e) {
				contextPath = Environment.getInstance().getContextPath();
			}
		} else {
			contextPath = Environment.getInstance().getContextPath();
		}
		String skinType = null;
		try {
			HttpSession session = ServletActionContext.getRequest().getSession();
			skinType = (String) session.getAttribute("SKINTYPE");
		} catch (Exception e) {
			// ignore
		}

		if (skinType != null && !skinType.equals("") && skinType.equals("gray")) {
			htmlBuilder.append("<input icon='null' type='button' ");
			htmlBuilder.append(" name='" + getButtonId() + "'");
			htmlBuilder.append(" title='" + act.getName() + "'");
			if (PermissionType.MODIFY == permissionType) {
				htmlBuilder.append(" onclick=\"" + function + "\"");
			}
			if (PermissionType.DISABLED == permissionType) {
				htmlBuilder.append(" disabled='disabled'");
			}
			htmlBuilder.append(" value='" + innerText + "'>&nbsp;&nbsp;");
		} else {
			if(skinType != null && !skinType.equals("") &&skinType.equals("default")){
				htmlBuilder.append("<span class='" + className + "'");
			}
			else{
				htmlBuilder.append("<div class='" + className + "'");
			}
			if (PermissionType.DISABLED == permissionType) {
				htmlBuilder.append(" disabled='disabled'");
			}			
			if(skinType != null && !skinType.equals("") &&skinType.equals("default")){
				htmlBuilder.append(" ><a href=\"###\"");
			}
			else{
				htmlBuilder.append(" ><div class='btn_left'></div><div class='btn_mid'><a href=\"###\"");
			}			
			htmlBuilder.append(" name='" + getButtonId() + "'");
			htmlBuilder.append(" class='" +function.substring(0, function.indexOf('(')) + "'");
			htmlBuilder.append(" title='" + act.getName() + "'");
			if (PermissionType.MODIFY == permissionType) {
				htmlBuilder.append(" onclick=\"" + function + "\"");
			}
			//htmlBuilder.append(" onmouseover='this.className=\"" + getButtonOnClass() + "\"'");
			//htmlBuilder.append(" onmouseout='this.className=\"" + getButtonClass() + "\"'");
			htmlBuilder.append(" >");			
			if(skinType != null && !skinType.equals("") &&skinType.equals("default")){
				htmlBuilder.append("<span>");
			}			
			
			if (skinType != null && !skinType.equals("") && (skinType.equals("fresh")||skinType.equals("blue")||skinType.equals("mild")||skinType.equals("brisk"))) {
			}else if(act.getIcon() != null && !act.getIcon().equals("")){
				htmlBuilder.append("<img style='border:0px solid blue;vertical-align:middle;' src='" + contextPath
						+ "/lib/icon/" + act.getIcon() + "' />&nbsp;");
			}else {
				htmlBuilder.append("<img style='border:0px solid blue;vertical-align:middle;' src='" + contextPath
						+ "/resource/imgv2/front/act/act_" + act.getType() + ".gif' />&nbsp;");
			}
			htmlBuilder.append(innerText);
			if(skinType != null && !skinType.equals("") &&skinType.equals("default")){
				htmlBuilder.append("</span></a></span>");
			}		
			else{
				htmlBuilder.append("</a></div><div class='btn_right'></div></div>");
			}			
		}
	}

	protected String getButtonStyle() {
		// return "background-image: url(../../../resource/imgnew/act/act_" +
		// act.getType() + ".gif)";
		return "";
	}

	/**
	 * 获取点击按钮页面的样式
	 * 
	 * @return 页面的样式
	 */
	protected String getButtonOnClass() {
		if (!StringUtil.isBlank(act.getIconurl())) {
			return ICON_BUTTON_CLASS;
		} else {
			return getDefaultOnClass();
		}
	}

	public Activity getActivity() {
		return act;
	}

	/**
	 * 获取表单的按钮类型{code: name}映射
	 * 
	 * @return 表单按钮类型映射
	 */
	public static Map<String, String> getFormActivityTypeMap() {
		return formActivityTypeMap;
	}

	/**
	 * 获取表单的按钮类型{code: name}映射，加上多语言标签
	 * 
	 * @return 表单按钮类型映射
	 */
	public static Map<String, String> getFormActTypeMapWithMulti() {
		Map<String, String> rtn = new LinkedHashMap<String, String>();
		Map<String, String> formActMap = ActivityType.getFormActivityTypeMap();
		for (Iterator<?> iterator = formActMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<?, ?> entry = (Entry<?, ?>) iterator.next();
			// 加上多语言标签
			rtn.put((String) entry.getKey(), "{*[" + entry.getValue() + "]*}");
		}

		return rtn;
	}

	/**
	 * 获取视图的按钮类型{code: name}映射
	 * 
	 * @return 视图按钮类型映射
	 */
	public static Map<String, String> getViewActivityTypeMap() {
		return viewActivityTypeMap;
	}

	/**
	 * 获取视图的按钮类型{code: name}映射，加上多语言标签
	 * 
	 * @return 视图按钮类型映射
	 */
	public static Map<String, String> getViewActTypeMapWithMulti() {
		Map<String, String> rtn = new LinkedHashMap<String, String>();
		Map<String, String> viewActMap = ActivityType.getViewActivityTypeMap();
		for (Iterator<?> iterator = viewActMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<?, ?> entry = (Entry<?, ?>) iterator.next();
			// 加上多语言标签
			rtn.put((String) entry.getKey(), "{*[" + entry.getValue() + "]*}");
		}

		return rtn;
	}

	/**
	 * 获取所有的按钮类型{code: name}映射
	 * 
	 * @return 按钮类型映射
	 */
	public static Map<Object, String> getAllActivityTypeMap() {
		LinkedHashMap<Object, String> map = new LinkedHashMap<Object, String>();
		map.putAll(formActivityTypeMap);
		map.putAll(viewActivityTypeMap);

		return map;
	}
}
