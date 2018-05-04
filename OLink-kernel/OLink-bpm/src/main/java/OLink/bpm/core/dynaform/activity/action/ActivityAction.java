package OLink.bpm.core.dynaform.activity.action;

import java.util.Collection;
import java.util.Map;

import OLink.bpm.base.action.BaseAction;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.constans.Web;
import OLink.bpm.core.dynaform.activity.ejb.ActivityParent;
import OLink.bpm.core.dynaform.activity.ejb.ActivityProcess;
import OLink.bpm.core.dynaform.activity.ejb.ActivityType;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.dynaform.form.ejb.ValidateMessage;
import OLink.bpm.core.filedownload.action.FileDownloadUtil;
import OLink.bpm.core.logger.action.LogHelper;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.macro.runner.JavaScriptFactory;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.activity.ejb.Activity;
import OLink.bpm.core.dynaform.view.ejb.ViewProcess;
import org.apache.log4j.Logger;

import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.util.cache.MemoryCacheUtil;

import com.opensymphony.webwork.ServletActionContext;

/**
 * @author nicholas
 */
public class ActivityAction extends BaseAction<Activity> {

	private final static Logger LOG = Logger.getLogger(ActivityAction.class);
	public static final String REQUEST_ATTRIBUTE_ACTIVITY = "ACTIVITY_INSTNACE";

	private String _activityid;

	private String flag; // 标志前移或后移 值为previous或next

	private String _iconid;

	private String parentType;

	private String _formid;

	private String _viewid;

	// 用户选定的下个节点的审批人，多个用';'分隔
	private String principal;

	private static final long serialVersionUID = 4195281197898460985L;

	/**
	 * ActivityAction 构造函数
	 * 
	 * @see BaseAction#BaseAction(BaseProcess,
	 *      ValueObject)
	 * 
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public ActivityAction() throws ClassNotFoundException {
		super(ProcessFactory.createProcess(ActivityProcess.class), new Activity());
	}

	/**
	 * 保存 Activity 信息. 如果处理成功返回"SUCCESS"，否则返回"INPUT"
	 * 
	 * @return result.
	 * @throws throws Exception
	 */
	public String doSave() {
		try {
			Activity activity = (Activity) getContent();
			activity.setIconurl(_iconid);
			((ActivityProcess) process).doUpdate(getParentBySession(), activity);

			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("", e.getMessage());
			return INPUT;
		}
	}

	/**
	 * 显示按钮(Activity)列表. 二种引用Activity: 1)在VIEW引用Activity。
	 * 2）在表单（Form)引用的相关Activity.
	 * 
	 * @return result.
	 */
	public String doList() {
		try {
			Collection<Activity> datas = getParentBySession().getActivitys();
			DataPackage<Activity> datapackage = new DataPackage<Activity>();
			datapackage.setDatas(datas);

			setDatas(datapackage);
			return SUCCESS;
		} catch (Exception e) {
			LOG.error("doList", e);
			addFieldError("", e.getMessage());
			return INPUT;
		}
	}

	public String doDelete() {
		try {
			if (_selects != null)
				((ActivityProcess) process).doRemove(getParentBySession(), _selects);
			return SUCCESS;
		} catch (Exception e) {
			LOG.error("doDelete", e);
			addFieldError("", e.getMessage());
			return INPUT;
		}
	}

	/**
	 * 返回相关活动表单(Form)主键.
	 * 
	 * @return 活动表单主键.
	 */
	public String get_onActionFormid() {
		if (((Activity) getContent()).getOnActionForm() != null) {
			return ((Activity) getContent()).getOnActionForm();
		} else {
			return null;
		}
	}

	/**
	 * 设置相关活动表单(Form)主键
	 * 
	 * @param actionFormid
	 *            Form主键
	 * @throws Exception
	 */
	public void set_onActionFormid(String actionFormid) throws Exception {
		((Activity) getContent()).setOnActionForm(actionFormid);
	}

	/**
	 * 获取相关活动视图(view)主键.
	 * 
	 * @return 活动视图主键
	 */
	public String get_onActionViewid() {
		return ((Activity) getContent()).getOnActionView();
	}

	/**
	 * 设置相关活动视图(view)主键
	 * 
	 * @param actionViewid
	 *            View主键
	 * @throws Exception
	 */
	public void set_onActionViewid(String actionViewid) throws Exception {
		((Activity) getContent()).setOnActionView(actionViewid);
	}

	/**
	 * 返回相关活动流程主键
	 * 
	 * @return 活动流程主键
	 */
	public String get_onActionFlowid() {
		return ((Activity) getContent()).getOnActionForm();
	}

	/**
	 * Set相关活动流程主键
	 * 
	 * @param actionFlowid
	 *            活动流程主键
	 * @throws Exception
	 */
	public void set_onActionFlowid(String actionFlowid) throws Exception {
		((Activity) getContent()).setOnActionFlow(actionFlowid);
	}

	/**
	 * 返回相关活动flex动态打印主键
	 * 
	 * @return 活动flex动态打印主键
	 */
	public String get_onActionPrintid() {
		return ((Activity) getContent()).getOnActionPrint();
	}

	/**
	 * Set相关活动flex动态打印主键
	 * 
	 * @param actionFlowid
	 *            活动flex动态打印主键
	 * @throws Exception
	 */
	public void set_onActionPrintid(String actionPrintid) throws Exception {
		((Activity) getContent()).setOnActionPrint(actionPrintid);
	}

	/**
	 * 改变Activity(按钮)位置次序. 如Activity位置次序前移一位或后移, 值为previous(前移一位)或next(后移一位)
	 * 
	 * @return result
	 * @throws Exception
	 */
	public String changeOrder() throws Exception {
		String id = getParams().getParameterAsString("id");
		((ActivityProcess) this.process).changeOrder(id, getParentBySession(), flag);
		return SUCCESS;
	}

	/**
	 * 标志Activity位置次序左移一位或右移一位 值为previous(前移一位)或next(后移一位)
	 * 
	 * @return 值为previous或next
	 * @uml.property name="flag"
	 */
	public String getFlag() {
		return flag;
	}

	/**
	 * Set标志Activity位置次序前移一位或后移一位
	 * 
	 * @param flag
	 *            值为previous(前移一位)或next(后移一位)
	 * @uml.property name="flag"
	 */
	public void setFlag(String flag) {
		this.flag = flag;
	}

	/**
	 * 返回图标
	 * 
	 * @return 图标
	 * @throws Exception
	 * @uml.property name="_iconid"
	 */
	public String get_iconid() throws Exception {
		return this._iconid;

	}

	/**
	 * Set 图标
	 * 
	 * @param _iconid
	 * @throws Exception
	 * @uml.property name="_iconid"
	 */
	public void set_iconid(String _iconid) throws Exception {
		this._iconid = _iconid;

	}

	/**
	 * 查询Activity. 如果归还值为"SUCCESS"表示成功处理.
	 * 
	 * @SuppressWarnings WebWork不支持泛型
	 * @return result.
	 */
	public String doView() {
		try {
			Map<?, ?> params = getContext().getParameters();
			Object obj = params.get("id");
			String id = ((String[]) obj)[0];

			Activity act = getParentBySession().findActivity(id);
			this._iconid = act.getIconurl();
			setContent(act);

			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("", e.getMessage());
			return INPUT;
		}
	}

	/**
	 * 保存并新建 Activity. 如果处理成功返回"SUCCESS",即再新建一个Activity，否则返回"INPUT".
	 * 
	 * @return result.
	 * @throws throws Exception
	 */
	public String doSaveAndNew() throws Exception {
		doSave();
		setContent(new Activity());
		return SUCCESS;
	}

	private boolean isAction = true;

	/**
	 * 触发对Document、流程与view的等有关操作. 根据Activity(按钮)类型实现对Document,流程与VIEW等不同的操作.
	 * <p>
	 * Activity(按钮)类型常量分别为:
	 * <p>
	 * 1:"ACTIVITY_TYPE_DOCUMENT_QUERY"(查询Document);
	 * 2:"ACTIVITY_TYPE_DOCUMENT_CREATE"(创建Document);
	 * <p>
	 * 3:"ACTIVITY_TYPE_DOCUMENT_DELETE"(删除Document);
	 * 4:"ACTIVITY_TYPE_DOCUMENT_UPDATE"(更新Document);
	 * <p>
	 * 5:"ACTIVITY_TYPE_WORKFLOW_PROCESS"(流程处理);
	 * 6:"ACTIVITY_TYPE_SCRIPT_PROCESS"(SCRIPT);
	 * <p>
	 * 7:"ACTIVITY_TYPE_DOCUMENT_MODIFY"(回退);
	 * 8:"ACTIVITY_TYPE_CLOSE_WINDOW"(关闭窗口);
	 * <p>
	 * 9:"ACTIVITY_TYPE_SAVE_CLOSE_WINDOW"(保存Document并关闭窗口);
	 * 10:"ACTIVITY_TYPE_DOCUMENT_BACK"(回退);
	 * <p>
	 * 11:"ACTIVITY_TYPE_SAVE_BACK"(保存Document并回退);
	 * 12:"ACTIVITY_TYPE_SAVE_NEW_WITH_OLD"(保存并新建保留有旧数据的Document);
	 * <p>
	 * 13:"ACTIVITY_TYPE_Nothing"; 14:"ACTIVITY_TYPE_PRINT"(普通打印);
	 * <p>
	 * 15:"ACTIVITY_TYPE_PRINT_WITHFLOWHIS"(打印包含有流程);
	 * 16:"ACTIVITY_TYPE_EXPTOEXCEL"(将数据导出到EXCEL);
	 * <p>
	 * 17:"ACTIVITY_TYPE_SAVE_NEW_WITHOUT_OLD"((保存并新建一条空的Document));
	 * 
	 * 
	 * @return result.
	 * @throws Exception
	 */
	public String doAction() throws Exception {
		ParamsTable params = getParams();
		String _activityid = params.getParameterAsString("_activityid");
		String formid = params.getParameterAsString("_formid");
		String parentId = params.getParameterAsString("parentid");
		String docid = (String) ServletActionContext.getRequest().getAttribute("content.id");
		Activity act = null;
		if (_activityid != null && _activityid.trim().length() > 0) {
			act = getParentByID().findActivity(get_activityid());
			ServletActionContext.getRequest().setAttribute(REQUEST_ATTRIBUTE_ACTIVITY, act);
		}

		Document doc = null;
		if (!StringUtil.isBlank(docid)) {
			doc = (Document) MemoryCacheUtil.getFromPrivateSpace(docid, getUser());
			if (doc != null) {
				doc.setId(docid);
			}
		} else if (!StringUtil.isBlank(parentId)) {
			doc = (Document) getUser().getFromTmpspace(parentId);
		}

		// 修改BUG 新建Document时重复执行2遍的问题 --Jarod 2011.5.15
		if (!StringUtil.isBlank(formid)) {
			FormProcess formPross = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
			Form form = (Form) formPross.doView(formid);
			doc = form.createDocument(doc, params, getUser());
		} else {
			doc = new Document();
		}
		setContent(doc);

		// 保存操作日志
		if (Web.SESSION_ATTRIBUTE_FRONT_USER.equals(getWebUserSessionKey()) && isAction) {
			LogHelper.saveLogByDynaform(act, doc, getUser());
		}

		// 文件下载处理
		if (act != null && act.getType() == ActivityType.FILE_DOWNLOAD) {
			try {
				return doFileDownload(act, doc);
			} catch (Exception e) {
				addFieldError("", e.getMessage());
				return INPUT;
			}
		}

		return SUCCESS;
	}

	// /@SuppressWarnings("unchecked")
	public String doFileDownload(Activity act, Document doc) throws Exception {
		ParamsTable params = getParams();

		IRunner runner = JavaScriptFactory.getInstance(params.getSessionid(), doc.getApplicationid());
		runner.initBSFManager(doc, params, getUser(), new java.util.ArrayList<ValidateMessage>());

		// 运行前脚本
		if (!StringUtil.isBlank(act.getBeforeActionScript())) {
			StringBuffer label = new StringBuffer();
			label.append("Activity Action(").append(act.getId()).append(")." + act.getName()).append(
					"beforeActionScript");
			runner.run(label.toString(), act.getBeforeActionScript());
		}

		// 处理文件下载按钮
		if (act.getType() == ActivityType.FILE_DOWNLOAD && !StringUtil.isBlank(act.getFileNameScript())) {
			StringBuffer label = new StringBuffer();
			label.append("Activity(").append(act.getId()).append(")." + act.getName()).append("fileNameScript");
			String result = (String) runner.run(label.toString(), act.getFileNameScript());

			FileDownloadUtil.doFileDownload(ServletActionContext.getResponse(), result);
		}

		return NONE;
	}

	public String doAfter() throws Exception {
		ParamsTable params = getParams();
		String _activityid = params.getParameterAsString("_activityid");

		Activity act = null;
		if (_activityid != null && _activityid.trim().length() > 0) {
			act = getParentByID().findActivity(_activityid);
			ServletActionContext.getRequest().setAttribute(REQUEST_ATTRIBUTE_ACTIVITY, act);
		}

		Document doc = (Document) getContent();
		setContent(doc);

		return SUCCESS;
	}

	/**
	 * 获取 activity 主键.
	 * 
	 * @return activity id.
	 */
	public String get_activityid() {
		return _activityid;
	}

	/**
	 * 设置 activity 主键.
	 * 
	 * @param _activityid
	 *            activity id
	 */
	public void set_activityid(String _activityid) {
		this._activityid = _activityid;
	}

	public String getApplication() {
		if (!StringUtil.isBlank(super.getApplication())) {
			return super.getApplication();
		} else {
			return application = (String) getContext().getSession().get(Web.SESSION_ATTRIBUTE_APPLICATION);
		}
	}

	public String getParentType() {
		return parentType;
	}

	public void setParentType(String parentType) {
		this.parentType = parentType;
	}

	/**
	 * 编辑时从Session中取回Parent对象
	 * 
	 * @return
	 */
	public ActivityParent getParentBySession() {
		if (StringUtil.isBlank(parentType)) {
			return null;
		}

		return (ActivityParent) getContext().getSession().get(parentType.toLowerCase() + "content");
	}

	public ActivityParent getParentByID() throws Exception {
		if (!StringUtil.isBlank(_viewid)) {
			ViewProcess viewProcess = (ViewProcess) ProcessFactory.createProcess(ViewProcess.class);
			return (ActivityParent) viewProcess.doView(_viewid);
		} else if (!StringUtil.isBlank(getParams().getParameterAsString("_templateForm"))) {
			FormProcess formProcess = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
			return (ActivityParent) formProcess.doView(getParams().getParameterAsString("_templateForm"));
		} else if (!StringUtil.isBlank(_formid)) {
			FormProcess formProcess = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
			return (ActivityParent) formProcess.doView(_formid);
		}

		return null;
	}

	public String get_formid() {
		return _formid;
	}

	public void set_formid(String _formid) {
		this._formid = _formid;
	}

	public String get_viewid() {
		return _viewid;
	}

	public void set_viewid(String _viewid) {
		this._viewid = _viewid;
	}

	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}
}
