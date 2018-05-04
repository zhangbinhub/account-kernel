package OLink.bpm.core.dynaform.activity.ejb;

import java.util.Collection;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.permission.ejb.PermissionProcess;
import OLink.bpm.core.workflow.element.FlowDiagram;
import OLink.bpm.core.workflow.engine.StateMachine;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.view.ejb.ViewProcess;
import OLink.bpm.core.workflow.FlowState;
import OLink.bpm.core.workflow.element.Node;
import OLink.bpm.core.workflow.storage.runtime.ejb.NodeRT;
import OLink.bpm.util.StringUtil;
import org.apache.log4j.Logger;

import OLink.bpm.core.dynaform.PermissionType;
import OLink.bpm.core.dynaform.activity.ejb.type.BatchApprove;
import OLink.bpm.core.dynaform.activity.ejb.type.BatchSignature;
import OLink.bpm.core.dynaform.activity.ejb.type.ClearAll;
import OLink.bpm.core.dynaform.activity.ejb.type.CloseWindow;
import OLink.bpm.core.dynaform.activity.ejb.type.Copy;
import OLink.bpm.core.dynaform.activity.ejb.type.DocumentBack;
import OLink.bpm.core.dynaform.activity.ejb.type.DocumentCreate;
import OLink.bpm.core.dynaform.activity.ejb.type.DocumentDelete;
import OLink.bpm.core.dynaform.activity.ejb.type.DocumentEditAuditor;
import OLink.bpm.core.dynaform.activity.ejb.type.DocumentQuery;
import OLink.bpm.core.dynaform.activity.ejb.type.DocumentUpdate;
import OLink.bpm.core.dynaform.activity.ejb.type.ExcelImport;
import OLink.bpm.core.dynaform.activity.ejb.type.ExportToExcel;
import OLink.bpm.core.dynaform.activity.ejb.type.ExportToPdf;
import OLink.bpm.core.dynaform.activity.ejb.type.FileDownload;
import OLink.bpm.core.dynaform.activity.ejb.type.FlexPrint;
import OLink.bpm.core.dynaform.activity.ejb.type.FlexPrintWithFlowHis;
import OLink.bpm.core.dynaform.activity.ejb.type.Jump;
import OLink.bpm.core.dynaform.activity.ejb.type.Nothing;
import OLink.bpm.core.dynaform.activity.ejb.type.NullType;
import OLink.bpm.core.dynaform.activity.ejb.type.Print;
import OLink.bpm.core.dynaform.activity.ejb.type.PrintView;
import OLink.bpm.core.dynaform.activity.ejb.type.PrintWithFlowHis;
import OLink.bpm.core.dynaform.activity.ejb.type.SaveBack;
import OLink.bpm.core.dynaform.activity.ejb.type.SaveCloseWindow;
import OLink.bpm.core.dynaform.activity.ejb.type.SaveNewWithOld;
import OLink.bpm.core.dynaform.activity.ejb.type.SaveNewWithOutOld;
import OLink.bpm.core.dynaform.activity.ejb.type.SaveStartWorkFlow;
import OLink.bpm.core.dynaform.activity.ejb.type.SaveWithOutValidate;
import OLink.bpm.core.dynaform.activity.ejb.type.Siganure;
import OLink.bpm.core.dynaform.activity.ejb.type.StartWorkFlow;
import OLink.bpm.core.dynaform.activity.ejb.type.WorkFlowProcess;
import OLink.bpm.core.dynaform.activity.ejb.type.WorkFlowRetracement;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.workflow.element.ManualNode;

/**
 * @hibernate.class table="T_ACTIVITY"
 * @author Marky
 */
public class Activity extends ValueObject implements Comparable<Activity> {
	public final static Logger LOG = Logger.getLogger(Activity.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 3021001094085548730L;
	/**
	 * 按钮关联的表单标识
	 */
	public static final String ACTIVITY_BELONGTO_FORM = "form";
	/**
	 * 按钮关联的视图标识
	 */
	public static final String ACTIVITY_BELONGTO_VIEW = "view";

	private String id;

	private String name;

	private int type;

	private String onActionForm;

	private String onActionView;

	private String onActionFlow;

	private String onActionPrint;

	private String excelName;

	private String beforeActionScript;

	private String afterActionScript;

	private String hiddenScript;

	private String readonlyScript;

	/**
	 * 流程启动脚本
	 */
	private String startFlowScript;

	/**
	 * 编辑模式
	 */
	private int editMode;

	// 流程操作显示方式
	private String flowShowType;

	/**
	 * 文件名称脚本
	 */
	private String fileNameScript;

	private String iconurl;

	private String approveLimit;

	private int orderno; // 排序

	private String stateToShow; // 在某状态下显示此按钮

	private ActivityType actType;

	private String parentView;

	private String parentForm;

	private String impmappingconfigid;

	private int jumpType;

	private boolean expSub;
	
	private String icon;

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public boolean isExpSub() {
		return expSub;
	}

	public void setExpSub(boolean expSub) {
		this.expSub = expSub;
	}

	public static final int JUMPTYPE_NEW = 0;

	private String targetList;

	public int getJumpType() {
		return jumpType;
	}

	public void setJumpType(int jumpType) {
		this.jumpType = jumpType;
	}

	public String getTargetList() {
		return targetList;
	}

	public void setTargetList(String targetList) {
		this.targetList = targetList;
	}

	/**
	 * 获取按钮活动所关联的导入配置映射标识
	 * 
	 * @return 导入配置映射标识
	 */
	public String getImpmappingconfigid() {
		return impmappingconfigid;
	}

	/**
	 * 设置按钮活动所关联的导入配置映射标识
	 * 
	 * @param impmappingconfigid
	 *            配置映射标识
	 */
	public void setImpmappingconfigid(String impmappingconfigid) {
		this.impmappingconfigid = impmappingconfigid;
	}

	/**
	 * 获取排序号
	 * 
	 * @hibernate.property column="ORDERNO"
	 * @return 排序号
	 * @uml.property name="orderno"
	 */
	public int getOrderno() {
		return orderno;
	}

	/**
	 * 设置按钮排序号
	 * 
	 * @param orderno
	 *            排序
	 * @uml.property name="orderno"
	 */
	public void setOrderno(int orderno) {
		this.orderno = orderno;
	}

	/**
	 * 获取 按钮主键,主键为UUID,用来标识按钮的唯一性
	 * 
	 * @see eWAP.core.Tools#getSequence()
	 * @hibernate.id column="ID" generator-class="assigned"
	 * @uml.property name="id"
	 */
	public String getId() {
		return id;
	}

	/**
	 * 设置按钮主键,主键为UUID,用来标识按钮的唯一性.
	 * 
	 * @param id
	 *            Acvitity主键
	 * @uml.property name="id"
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 获取按钮名字
	 * 
	 * @hibernate.property column="NAME"
	 * @return 按钮名字
	 * @uml.property name="name"
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置按钮名字
	 * 
	 * @param name
	 * @uml.property name="name"
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取执行按钮之前执行脚本
	 * 
	 * @hibernate.property column="BEFOREACTIONSCRIPT" type = "text"
	 * @return 执行按钮之前执行的脚本
	 */
	public String getBeforeActionScript() {
		return beforeActionScript;
	}

	/**
	 * 设置执行按钮之前执行脚本
	 * 
	 * @param beforeActionScript
	 *            执行按钮之前执行脚本
	 * @uml.property name="beforeActionScript"
	 */
	public void setBeforeActionScript(String beforeActionScript) {
		this.beforeActionScript = beforeActionScript;
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

	/**
	 * 获取只读脚本
	 * 
	 * @return
	 */
	public String getReadonlyScript() {
		return readonlyScript;
	}

	/**
	 * 设置只读脚本
	 * 
	 * @param readonlyScript
	 */
	public void setReadonlyScript(String readonlyScript) {
		this.readonlyScript = readonlyScript;
	}

	/**
	 * 获取流程启动脚本
	 * 
	 * @return
	 */
	public String getStartFlowScript() {
		return startFlowScript;
	}

	/**
	 * 
	 * @param startFlowScript
	 */
	public void setStartFlowScript(String startFlowScript) {
		this.startFlowScript = startFlowScript;
	}

	/**
	 * 获取编辑模式
	 * 
	 * @return
	 */
	public int getEditMode() {
		return editMode;
	}

	/**
	 * 设置编辑模式
	 * 
	 * @param editMode
	 */
	public void setEditMode(int editMode) {
		this.editMode = editMode;
	}

	/**
	 * 获取按钮活动所关联的表单
	 * 
	 * @return FORM_ID 表单标识
	 * @hibernate.property column="ONACTIONFORM_ID"
	 */
	public String getOnActionForm() {
		return onActionForm;
	}

	/**
	 * 设置按钮活动所关联的表单
	 * 
	 * @param onActionForm
	 *            表单标识
	 */
	public void setOnActionForm(String onActionForm) {
		this.onActionForm = onActionForm;
	}

	/**
	 * 获取按钮活动所关联的视图
	 * 
	 * @return 视图标识
	 * @hibernate.property column="ONACTIONVIEW_ID"
	 */
	public String getOnActionView() {
		return onActionView;
	}

	/**
	 * 设置按钮活动所关联的视图
	 * 
	 * @param onActionView
	 *            视力标识
	 */
	public void setOnActionView(String onActionView) {
		this.onActionView = onActionView;
	}

	/**
	 * 获取按钮活动所关联的流程
	 * 
	 * @return 流程标识
	 * @hibernate.property column="ONACTIONFLOW_ID"
	 */
	public String getOnActionFlow() {
		return onActionFlow;
	}

	/**
	 * 设置按钮活动所关联的流程
	 * 
	 * @param onActionFlow
	 *            流程标识
	 */
	public void setOnActionFlow(String onActionFlow) {
		this.onActionFlow = onActionFlow;
	}

	/**
	 * 获取按钮活动所关联的动态打印
	 * 
	 * @return
	 */
	public String getOnActionPrint() {
		return onActionPrint;
	}

	/**
	 * 设置按钮活动所关联的动态打印
	 * 
	 * @param onActionPrint
	 */
	public void setOnActionPrint(String onActionPrint) {
		this.onActionPrint = onActionPrint;
	}

	/**
	 * 获取按钮活动所关联的EXCEL名称
	 * 
	 * @return EXCEL名称
	 * @hibernate.property column="EXCELNAME_ID"
	 */
	public String getExcelName() {
		return excelName;
	}

	/**
	 * 设置按钮活动所关联的EXCEL名称
	 * 
	 * @param excelName
	 *            EXCEL名称
	 */
	public void setExcelName(String excelName) {
		this.excelName = excelName;
	}

	/**
	 * 获取Activity类型. 分别: 1:文档查询(DOCUMENT_QUERY)
	 * <p>
	 * 2:文档创建(DOCUMENT_CREATE) 3:文档删除(DOCUMENT_DELETE)
	 * <p>
	 * 4:文档更新(DOCUMENT_UPDATE)
	 * <p>
	 * 5:流程处理(WORKFLOW_PROCESS) 6:SCRIPT处理(SCRIPT_PROCESS)
	 * <p>
	 * 7:文档修改(DOCUMENT_MODIFY) 8:关闭窗口(CLOSE_WINDOW);
	 * <p>
	 * 9:保存并关闭窗口(SAVE_CLOSE_WINDOW) 10:回退(DOCUMENT_BACK);
	 * <p>
	 * 11:保存并返回(SAVE_BACK); 12:保存并新建(新建时还保留之前旧的内容)SAVE_NEW_WITH_OLD
	 * <p>
	 * 13:Nothing 14:打印(PRINT)
	 * <p>
	 * 15:与流程图一起打印 (PRINT_WITHFLOWHIS) 16:导出Excel(EXPTOEXCEL)
	 * <p>
	 * 17:保存并新建(新建时不保留之前旧的内容)SAVE_NEW_WITHOUT_OLD
	 * 
	 * @hibernate.property column="TYPE"
	 * @return int
	 * @uml.property name="type"
	 */
	public int getType() {
		return type;
	}

	/**
	 * 设置Activity类型. 分别: 1:文档查询(DOCUMENT_QUERY)
	 * <p>
	 * 2:文档创建(DOCUMENT_CREATE) 3:文档删除(DOCUMENT_DELETE)
	 * <p>
	 * 4:文档更新(DOCUMENT_UPDATE)
	 * <p>
	 * 5:流程处理(WORKFLOW_PROCESS) 6:SCRIPT处理(SCRIPT_PROCESS)
	 * <p>
	 * 7:文档修改(DOCUMENT_MODIFY) 8:关闭窗口(CLOSE_WINDOW);
	 * <p>
	 * 9:保存并关闭窗口(SAVE_CLOSE_WINDOW) 10:回退(DOCUMENT_BACK);
	 * <p>
	 * 11:保存并返回(SAVE_BACK); 12:保存并新建(新建时还保留之前旧的内容)SAVE_NEW_WITH_OLD
	 * <p>
	 * 13:Nothing 14:打印(PRINT)
	 * <p>
	 * 15:和流程一起打印 (PRINT_WITHFLOWHIS) 16:导出Excel(EXPTOEXCEL)
	 * <p>
	 * 17:保存并新建(新建时不保留之前旧的内容)SAVE_NEW_WITHOUT_OLD
	 * 
	 * @param type
	 *            Activity类型.
	 */
	public void setType(int type) {
		this.type = type;

		switch (type) {
		case ActivityType.BATCH_APPROVE:
			actType = new BatchApprove(this);
			break;
		case ActivityType.CLEAR_ALL:
			actType = new ClearAll(this);
			break;
		case ActivityType.CLOSE_WINDOW:
			actType = new CloseWindow(this);
			break;
		case ActivityType.DOCUMENT_CREATE:
			actType = new DocumentCreate(this);
			break;
		case ActivityType.DOCUMENT_BACK:
			actType = new DocumentBack(this);
			break;
		case ActivityType.DOCUMENT_DELETE:
			actType = new DocumentDelete(this);
			break;
		case ActivityType.DOCUMENT_MODIFY:
			actType = new NullType(this);
			break;
		case ActivityType.DOCUMENT_QUERY:
			actType = new DocumentQuery(this);
			break;
		case ActivityType.SAVE_SARTWORKFLOW:
			actType = new SaveStartWorkFlow(this);
			break;
		case ActivityType.DOCUMENT_UPDATE:
			actType = new DocumentUpdate(this);
			break;
		case ActivityType.EXPTOEXCEL:
			actType = new ExportToExcel(this);
			break;
		case ActivityType.NOTHING:
			actType = new Nothing(this);
			break;
		case ActivityType.PRINT:
			actType = new Print(this);
			break;
		case ActivityType.PRINT_WITHFLOWHIS:
			actType = new PrintWithFlowHis(this);
			break;
		case ActivityType.SAVE_BACK:
			actType = new SaveBack(this);
			break;
		case ActivityType.SAVE_CLOSE_WINDOW:
			actType = new SaveCloseWindow(this);
			break;
		case ActivityType.SAVE_NEW_WITH_OLD:
			actType = new SaveNewWithOld(this);
			break;
		case ActivityType.SAVE_NEW_WITHOUT_OLD:
			actType = new SaveNewWithOutOld(this);
			break;
		case ActivityType.SAVE_WITHOUT_VALIDATE:
			actType = new SaveWithOutValidate(this);
			break;
		case ActivityType.WORKFLOW_PROCESS:
			actType = new WorkFlowProcess(this);
			break;
		case ActivityType.DOCUMENT_COPY:
			actType = new Copy(this);
			break;
		case ActivityType.DOCUMENT_EDIT_AUDITOR:
			actType = new DocumentEditAuditor(this);
			break;
		case ActivityType.EXPTOPDF:
			actType = new ExportToPdf(this);
			break;
		case ActivityType.FILE_DOWNLOAD:
			actType = new FileDownload(this);
			break;
		case ActivityType.EXCEL_IMPORT:
			actType = new ExcelImport(this);
			break;
		case ActivityType.SIGNATURE:
			actType = new Siganure(this);
			break;
		case ActivityType.BATCHSIGNATURE:
			actType = new BatchSignature(this);
			break;
		case ActivityType.FLEX_PRINT:
			actType = new FlexPrint(this);
			break;
		case ActivityType.FLEX_PRINT_WITHFLOWHIS:
			actType = new FlexPrintWithFlowHis(this);
			break;
		case ActivityType.JUMP:
			actType = new Jump(this);
			break;
		case ActivityType.START_WORKFLOW:
			actType = new StartWorkFlow(this);
			break;
		case ActivityType.WORKFLOW_RETRACEMENT:
			actType = new WorkFlowRetracement(this);
			break;
		case ActivityType.PRINT_VIEW:
			actType = new PrintView(this);
			break;
		default:
			actType = new NullType(this);
			break;
		}
	}

	/**
	 * 图标地址url
	 * 
	 * @hibernate.property column="ICONURL"
	 */
	public String getIconurl() {
		return iconurl;
	}

	/**
	 * 设置图标URL
	 * 
	 * @param iconurl
	 */
	public void setIconurl(String iconurl) {
		this.iconurl = iconurl;
	}

	/**
	 * 获取流程状态显示
	 * 
	 * @hibernate.property column="STATETOSHOW"
	 * @return 流程状态显示
	 * @uml.property name="stateToShow"
	 */
	public String getStateToShow() {
		return stateToShow;
	}

	/**
	 * 设置流程状态显示
	 * 
	 * @param stateToShow
	 *            流程状态显示
	 * @uml.property name="stateToShow"
	 */
	public void setStateToShow(String stateToShow) {
		this.stateToShow = stateToShow;
	}

	/**
	 * 根据文档与流程状态判断是否隐藏Activity
	 * 
	 * @param doc
	 * 
	 * @return true or false true为隐藏.
	 * @throws Exception
	 */
	public boolean isStateToHidden(Document doc) throws Exception {
		if (getStateToShow() != null && getStateToShow().trim().length() > 0) {
			String[] showStates = getStateToShow().split(",");
			for (int i = 0; i < showStates.length; i++) {
				Collection<String> labelList = doc.getStateLableList();
				if (labelList.size() > 0) {
					if (labelList.contains(showStates[i])) {
						return false;
					}
				} else {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * 是否允许回撤
	 * 
	 * @param doc
	 * @param user
	 * @return
	 * @throws Exception
	 * @author Happy
	 */
	public boolean allowRetracement(IRunner runner, Document doc, WebUser user) throws Exception {
		boolean isHidden = true;
		BillDefiVO flowVO = doc.getFlowVO();
		FlowDiagram fd = flowVO.toFlowDiagram();
		NodeRT nodert = doc.getState().getNoderts().iterator().next();
		Node currNode = (Node) fd.getElementByID(nodert.getNodeid());
		Node nextNode = StateMachine.getBackNodeByHis(doc, flowVO, currNode.id, user, FlowState.RUNNING);
		if (nextNode != null) {
			if (((ManualNode) nextNode).retracementEditMode == 0 && ((ManualNode) nextNode).cRetracement) {
				isHidden = false;
			} else if (((ManualNode) nextNode).retracementEditMode == 1
					&& ((ManualNode) nextNode).retracementScript != null
					&& (((ManualNode) nextNode).retracementScript).trim().length() > 0) {
				StringBuffer label = new StringBuffer();
				label.append(getParentFullName()).append(".Activity(").append(getId()).append(")." + getName()).append(
						".retracementScript");
				Object result = runner.run(label.toString(), ((ManualNode) nextNode).retracementScript);
				if (result != null && result instanceof Boolean) {
					if (((Boolean) result).booleanValue())
						isHidden = false;
				}
			}
		}
		return isHidden;

	}

	public boolean isHidden(IRunner runner, Document doc, WebUser user, int resType) throws Exception {
		boolean isHidden = false;

		// 1.隐藏脚本
		if ((getHiddenScript()) != null && (getHiddenScript()).trim().length() > 0) {
			StringBuffer label = new StringBuffer();
			label.append(getParentFullName()).append(".Activity(").append(getId()).append(")." + getName()).append(
					".runHiddenScript");
			Object result = runner.run(label.toString(), getHiddenScript());
			if (result != null && result instanceof Boolean) {
				isHidden = ((Boolean) result).booleanValue();
			}
		}

		if (getType() == ActivityType.WORKFLOW_PROCESS || getType() == ActivityType.DOCUMENT_EDIT_AUDITOR
				|| getType() == ActivityType.WORKFLOW_RETRACEMENT) {
			// 存在流程才显示流程相关按钮
			isHidden = isHidden || doc.getState() == null;
		}
		if (!isHidden && getType() == ActivityType.WORKFLOW_RETRACEMENT) { // 是否允许流程回撤
			if (allowRetracement(runner, doc, user))
				isHidden = true;
		}
		// 2.权限校验
		PermissionProcess process = (PermissionProcess) ProcessFactory.createProcess(PermissionProcess.class);
		boolean isAllow = process.check(user.getRolesByApplication(doc.getApplicationid()), getId(), this.getType(), resType);

		return isHidden || isStateToHidden(doc) || !isAllow;
	}

	/**
	 * 判断按钮是否隐藏
	 * 
	 * @param runner
	 *            脚本运行器
	 * @param parent
	 *            按钮所属于的父元素
	 * @param doc
	 *            Document对象
	 * @return boolean true or false true为隐藏.
	 * @throws Exception
	 */
	public boolean isHidden(IRunner runner, ActivityParent parent, Document doc) throws Exception {
		boolean isHidden = false;

		if ((getHiddenScript()) != null && (getHiddenScript()).trim().length() > 0) {
			StringBuffer label = new StringBuffer();
			label.append(parent.getFullName()).append(".Activity(").append(getId()).append(")." + getName()).append(
					".runHiddenScript");
			Object result = runner.run(label.toString(), getHiddenScript());
			if (result != null && result instanceof Boolean) {
				isHidden = ((Boolean) result).booleanValue();
			}
		}

		if (getType() == ActivityType.WORKFLOW_PROCESS || getType() == ActivityType.DOCUMENT_EDIT_AUDITOR
				|| getType() == ActivityType.WORKFLOW_RETRACEMENT) {
			// 存在流程才显示流程相关按钮
			isHidden = isHidden || doc.getState() == null;
		}
		return isHidden || isStateToHidden(doc);
	}

	public boolean isHidden(IRunner runner, ActivityParent parent, Document doc, WebUser user, int resType)
			throws Exception {
		boolean isHidden = false;
		String parentFullName = parent.getFullName();

		// 1.隐藏脚本
		if ((getHiddenScript()) != null && (getHiddenScript()).trim().length() > 0) {
			StringBuffer label = new StringBuffer();
			label.append(parentFullName).append(".Activity(").append(getId()).append(")." + getName()).append(
					".runHiddenScript");
			Object result = runner.run(label.toString(), getHiddenScript());
			if (result != null && result instanceof Boolean) {
				isHidden = ((Boolean) result).booleanValue();
			}
		}

		if (getType() == ActivityType.WORKFLOW_PROCESS || getType() == ActivityType.DOCUMENT_EDIT_AUDITOR
				|| getType() == ActivityType.WORKFLOW_RETRACEMENT) {
			// 存在流程才显示流程相关按钮
			isHidden = isHidden || doc.getState() == null;
		}
		if (!isHidden && getType() == ActivityType.WORKFLOW_RETRACEMENT) { // 是否允许流程回撤
			if (allowRetracement(runner, doc, user))
				isHidden = true;
		}
		// 2.权限校验
		PermissionProcess process = (PermissionProcess) ProcessFactory.createProcess(PermissionProcess.class);
		boolean isAllow = process.check(user.getRolesByApplication(parent.getApplicationid()), parent.getId(), this.getId(), resType);

		return isHidden || isStateToHidden(doc) || !isAllow;
	}

	/**
	 * 判断按钮是否只读
	 * 
	 * @param runner
	 *            脚本运行器
	 * @param parentFullName
	 *            按钮所属于的父元素
	 * @return boolean true or false true为只读.
	 * @throws Exception
	 */
	public boolean isReadonly(IRunner runner, String parentFullName) throws Exception {
		boolean isReadonly = false;
		if ((getReadonlyScript()) != null && (getReadonlyScript()).trim().length() > 0) {
			StringBuffer label = new StringBuffer();
			label.append(parentFullName).append(".Activity(").append(getId()).append(")." + getName()).append(
					".runReadonlyScript");
			Object result = runner.run(label.toString(), getReadonlyScript());
			if (result != null && result instanceof Boolean) {
				isReadonly = ((Boolean) result).booleanValue();
			}
		}

		return isReadonly;
	}

	/**
	 * 获取执行按钮动作之后的脚本
	 * 
	 * @hibernate.property column="AFTERACTIONSCRIPT" type = "text"
	 * @return 执行按钮动作之后的脚本
	 */
	public String getAfterActionScript() {
		return afterActionScript;
	}

	/**
	 * 设置执行按钮动作之后的脚本
	 * 
	 * @param afterActionScript
	 *            脚本
	 */
	public void setAfterActionScript(String afterActionScript) {
		this.afterActionScript = afterActionScript;
	}

	/**
	 * 获取审批限制, 当按钮类型为批量审批时, 限制下一步可提交的节点(可多选,以","分隔)
	 * 
	 * @hibernate.property column="APPROVELIMIT"
	 * @return 节点列表
	 */
	public String getApproveLimit() {
		return approveLimit;
	}

	/**
	 * 设置审批限制, 当按钮类型为批量审批时, 限制下一步可提交的节点(可多选,以","分隔)
	 * 
	 * @param approveLimit
	 *            审批限制
	 */
	public void setApproveLimit(String approveLimit) {
		this.approveLimit = approveLimit;
	}

	/**
	 * 将按钮以html的形式输出到页面,形成一个按钮
	 * 
	 * @return
	 */
	public String toHtml() {
		return toHtml(PermissionType.MODIFY);
	}

	public String toHtml(int permissionType) {
		return actType.toHtml(permissionType);
	}

	public String toButtonHtml(int permissionType) {
		return actType.toButtonHtml(permissionType);
	}

	public String toButtonHtml() {
		return toButtonHtml(PermissionType.MODIFY);
	}

	/**
	 * 获取执行按钮回返脚本
	 * 
	 * @return 脚本
	 */
	public String getBackAction() {
		return actType.getBackAction();
	}

	/**
	 * 获取执行按钮执行前脚本
	 * 
	 * @return 脚本
	 */
	public String getActionUrl() {
		return actType.getBeforeAction();
	}

	/**
	 * 获取执行按钮执行后脚本
	 * 
	 * @return 脚本
	 */
	public String getAfterAction() {
		return actType.getAfterAction();
	}

	/**
	 * 获取关联的视图标识
	 * 
	 * @return 视图标识
	 */
	public String getParentView() {
		return parentView;
	}

	/**
	 * 设置关联的视图标识
	 * 
	 * @param parentView
	 *            视图标识
	 */
	public void setParentView(String parentView) {
		this.parentView = parentView;
	}

	/**
	 * 获取关联的表单标识
	 * 
	 * @return 表单标识
	 */
	public String getParentForm() {
		return parentForm;
	}

	/**
	 * 设置关联的表单标识
	 * 
	 * @param parentForm
	 *            表单标识
	 */
	public void setParentForm(String parentForm) {
		this.parentForm = parentForm;
	}

	/**
	 * 根据orderno比较大小
	 */
	public int compareTo(Activity activity) {
		if (activity != null) {
			int thisOrderno = this.orderno;
			int otherOrderno = activity.orderno;
			return (thisOrderno - otherOrderno);
			// return (this.orderno - ((Activity) o).orderno);
		}
		return -1;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		/*
		 * Activity that = (Activity) obj;
		 * 
		 * if (this.getName() != null ? !this.getName().equals(that.getName()) :
		 * that.getName() != null) return false; if (this.getActionUrl() != null
		 * ? !this.getActionUrl().equals(that.getActionUrl()) :
		 * that.getActionUrl() != null) return false; if (this.getAfterAction()
		 * != null ? !this.getAfterAction().equals(that.getAfterAction()) :
		 * that.getAfterAction() != null) return false; if
		 * (this.getAfterActionScript() != null ?
		 * !this.getAfterActionScript().equals(that.getAfterActionScript()) :
		 * that.getAfterActionScript() != null) return false; if
		 * (this.getApplicationid() != null ?
		 * !this.getApplicationid().equals(that.getApplicationid()) :
		 * that.getApplicationid() != null) return false; if
		 * (this.getApproveLimit() != null ?
		 * !this.getApproveLimit().equals(that.getApproveLimit()) :
		 * that.getApproveLimit() != null) return false; if
		 * (this.getBackAction() != null ?
		 * !this.getBackAction().equals(that.getBackAction()) :
		 * that.getBackAction() != null) return false; if
		 * (this.getBeforeActionScript() != null ?
		 * !this.getBeforeActionScript().equals(that.getBeforeActionScript()) :
		 * that.getBeforeActionScript() != null) return false; if
		 * (this.getDomainid() != null ?
		 * !this.getDomainid().equals(that.getDomainid()) : that.getDomainid()
		 * != null) return false;
		 */
		super.equals(obj);
		return true;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * 获取文件名称脚本
	 * 
	 * @return 文件名
	 */
	public String getFileNameScript() {
		return fileNameScript;
	}

	/**
	 * 设置文件名称脚本
	 * 
	 * @param fileNameScript
	 *            文件名称脚本
	 */
	public void setFileNameScript(String fileNameScript) {
		this.fileNameScript = fileNameScript;
	}

	public String getFlowShowType() {
		return flowShowType;
	}

	public void setFlowShowType(String flowShowType) {
		this.flowShowType = flowShowType;
	}

	public String getParentFullName() {
		try {
			FormProcess formProcess = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
			ViewProcess viewProcess = (ViewProcess) ProcessFactory.createProcess(ViewProcess.class);
			if (!StringUtil.isBlank(getParentForm())) {
				Form form = (Form) formProcess.doView(getParentForm());
				return form.getFullName();
			} else if (!StringUtil.isBlank(getParentView())) {
				View view = (View) viewProcess.doView(getParentView());
				return view.getFullName();
			}
		} catch (Exception e) {
			LOG.warn("getParentFullName", e);
		}

		return "";

	}

	public ActivityParent getParent() throws Exception {
		String viewid = getParentView();
		String formid = getParentForm();

		if (!StringUtil.isBlank(viewid)) {
			ViewProcess viewProcess = (ViewProcess) ProcessFactory.createProcess(ViewProcess.class);
			return (ActivityParent) viewProcess.doView(viewid);
		} else if (!StringUtil.isBlank(formid)) {
			FormProcess formProcess = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
			return (ActivityParent) formProcess.doView(formid);
		}

		return null;
	}
}
