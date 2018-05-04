package OLink.bpm.desktop.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import OLink.bpm.base.action.BaseAction;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.constans.Web;
import OLink.bpm.core.dynaform.activity.ejb.Activity;
import OLink.bpm.core.dynaform.activity.ejb.ActivityProcess;
import OLink.bpm.core.dynaform.activity.ejb.ActivityType;
import OLink.bpm.core.dynaform.document.action.DocumentHelper;
import OLink.bpm.core.dynaform.form.action.FormHelper;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.dynaform.form.ejb.ValidateMessage;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.macro.runner.JavaScriptFactory;
import OLink.bpm.core.macro.runner.JsMessage;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.workflow.FlowType;
import OLink.bpm.core.workflow.element.AbortNode;
import OLink.bpm.core.workflow.element.CompleteNode;
import OLink.bpm.core.workflow.element.SuspendNode;
import OLink.bpm.core.workflow.element.TerminateNode;
import OLink.bpm.core.workflow.engine.StateMachineHelper;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import OLink.bpm.util.cache.MemoryCacheUtil;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.document.ejb.DocumentBuilder;
import OLink.bpm.core.dynaform.document.ejb.DocumentProcess;
import OLink.bpm.core.dynaform.view.ejb.ViewProcess;
import OLink.bpm.core.workflow.element.ManualNode;
import OLink.bpm.core.workflow.element.Node;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiVO;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.util.CreateProcessException;

import com.opensymphony.webwork.ServletActionContext;

public class DServiceAction extends BaseAction<Activity> {

	private static final long serialVersionUID = 1L;

	private String _activityid;

	/**
	 * 默认构造方法
	 * 
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public DServiceAction() throws ClassNotFoundException {
		super(ProcessFactory.createProcess(ActivityProcess.class), new Activity());
	}

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
	public String doAction() {
		try {
			ParamsTable params = getParams();
			String _activityid = params.getParameterAsString("_activityid");

			if (_activityid != null && _activityid.trim().length() > 0) {
				String formid = params.getParameterAsString("_formid");
				Activity act = null;
				if (!StringUtil.isBlank(formid)) {
					FormProcess formPross = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
					Form form = (Form) formPross.doView(formid);
					act = form.findActivity(_activityid);
				} else {
					String viewid = params.getParameterAsString("_viewid");
					ViewProcess viewProcess = (ViewProcess) ProcessFactory.createProcess(ViewProcess.class);
					View view = (View) viewProcess.doView(viewid);
					act = view.findActivity(_activityid);
				}

				ServletActionContext.getRequest().setAttribute("ACTIVITY_INSTNACE", act);

				if (doBefore(act).trim().equalsIgnoreCase("error")) {
					return ERROR;
				}
				switch (act.getType()) {
				case ActivityType.BATCH_APPROVE:

					break;
				case ActivityType.CLEAR_ALL:

					break;

				case ActivityType.CLOSE_WINDOW:
					return "viewList";
				case ActivityType.DOCUMENT_BACK:
					return "viewList";
				case ActivityType.DOCUMENT_CREATE:
					return doNewDocument(act);
				case ActivityType.DOCUMENT_DELETE:
					return doDelete(act);
				case ActivityType.DOCUMENT_MODIFY:
					return doViewDocument();
				case ActivityType.DOCUMENT_QUERY:

					break;
				case ActivityType.DOCUMENT_UPDATE:
					return doSaveDocument(act);
				case ActivityType.EXPTOEXCEL:

					break;
				case ActivityType.NOTHING:

					break;
				case ActivityType.PRINT:

					break;
				case ActivityType.PRINT_WITHFLOWHIS:

					break;
				case ActivityType.SAVE_BACK:
					return doSaveBack(act);
				case ActivityType.SAVE_CLOSE_WINDOW:
					return doSaveClose(act);
				case ActivityType.SAVE_NEW_WITH_OLD:
					return doSaveNewWithOld(act);
				case ActivityType.SAVE_NEW_WITHOUT_OLD:
					return doSaveNewWithOutOld(act);
				case ActivityType.SAVE_WITHOUT_VALIDATE:
					return doSaveWithOutValidate(act);
				case ActivityType.SCRIPT_PROCESS:
					break;
				case ActivityType.WORKFLOW_PROCESS:
					return doSubmitDocument();
				default:
					break;
				}
				throw new Exception("Unsupport activity type!");
			} else {
				return doViewDocument();
			}
		} catch (Exception e) {
			this.addFieldError("SystemError", e.getMessage());
			e.printStackTrace();
			return ERROR;
		}
	}

	/**
	 * 流程提交
	 * 
	 * @return
	 * @throws Exception
	 */
	private String doSubmitDocument() throws Exception {
		if (!doValidate())
			return ERROR;
		try {
			WebUser user = getUser();
			ParamsTable params = getParams();
			DocumentProcess proxy = createDocumentProcess(getApplication());
			String formid = params.getParameterAsString("_formid");
			FormProcess formProcess = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
			Form form = (Form) formProcess.doView(formid);
			String docid = params.getParameterAsString("_docid");
			Document olddoc = (Document) proxy.doView(docid);
			Document doc = form.createDocument(olddoc, params, user);
			if (docid != null && docid.length() > 0) {
				doc.setId(docid);
			}
			proxy.doFlow(doc, params, get_currid(), get_nextids(), get_flowType(doc.getFlowVO()),
					get_attitude(), user);

			setContent(doc);
			MemoryCacheUtil.putToPrivateSpace(doc.getId(), doc, getUser());
		} catch (Exception e) {
			this.addFieldError("SystemError", e.getMessage());
			e.printStackTrace();
			return ERROR;
		}
		return "viewList";
	}

	private String doBefore(Activity act) throws Exception {
		ParamsTable params = getParams();
		String formid = params.getParameterAsString("_formid");
		String parentId = params.getParameterAsString("parentid");
		String docid = params.getParameterAsString("_docid");
		WebUser webUser = getUser();
		Document doc = null;
		if (!StringUtil.isBlank(docid)) {
			doc = (Document) webUser.getFromTmpspace(docid);
		} else if (!StringUtil.isBlank(parentId)) {
			doc = (Document) webUser.getFromTmpspace(parentId);
		}
		if (!StringUtil.isBlank(formid)) {
			FormProcess formPross = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
			Form form = (Form) formPross.doView(formid);
			doc = form.createDocument(doc, params, webUser);
		} else {
			doc = new Document();
		}
		IRunner runner = JavaScriptFactory.getInstance(params.getSessionid(), act.getApplicationid());
		runner.initBSFManager(doc, params, webUser, new ArrayList<ValidateMessage>());
		if ((act.getBeforeActionScript()) != null && (act.getBeforeActionScript()).trim().length() > 0) {

			StringBuffer label = new StringBuffer();
			label.append("Activity Action(").append(act.getId()).append(")." + act.getName()).append(
					"beforeActionScript");
			Object result = runner.run(label.toString(), act.getBeforeActionScript());

			if (result != null) {
				if (result instanceof JsMessage) {
					this.addFieldError("SystemError", ((JsMessage) result).getContent());
					return ERROR;
				} else if (result instanceof String && ((String) result).trim().length() > 0) {
					this.addFieldError("SystemError", result.toString());
					return ERROR;
				}
			}
		}
		return SUCCESS;
	}

	public String doViewFlow() throws Exception {
		return "viewDocument";
	}

	public String doShowFlowHis() throws Exception {
		if (!toFlowHisXml())
			return ERROR;
		return "viewDocument";
	}

	/**
	 * 根据文档主键，查找文档.
	 * 
	 * @return SUCCESS
	 * @throws Exception
	 */
	private String doViewDocument() {
		try {
			WebUser user = this.getUser();
			DocumentProcess proxy = createDocumentProcess(getApplication());
			String _docid = getParams().getParameterAsString("_docid");
			Document doc = (Document) proxy.doView(_docid);
			setContent(doc);
			MemoryCacheUtil.putToPrivateSpace(doc.getId(), doc, user);
			toXml(doc, new ArrayList<String>(), false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "viewDocument";
	}

	public String doRefresh() throws Exception {
		// synchronized (user) {
		// long time = System.currentTimeMillis();
		try {
			WebUser user = this.getUser();

			FormProcess formProcess = (FormProcess) ProcessFactory.createProcess(FormProcess.class);

			ParamsTable params = getParams();
			String formid = params.getParameterAsString("_formid");

			Form form = (Form) formProcess.doView(formid);

			String docid = params.getParameterAsString("_docid");
			DocumentProcess proxy = createDocumentProcess(getApplication());
			Document olddoc = (Document) MemoryCacheUtil.getFromPrivateSpace(docid, user);
			if (olddoc == null)
				olddoc = (Document) proxy.doView(docid);

			Document doc = (Document) proxy.doView(docid);
			doc = form.createDocument(doc, params, user);

			if (docid != null && docid.length() > 0) {
				doc.setId(docid);
			}
			setContent(doc);
			MemoryCacheUtil.putToPrivateSpace(doc.getId(), doc, getUser());
			toXml(doc, doc.compareTo(olddoc), true);
		} catch (Exception e) {
			this.addFieldError("SystemError", e.getMessage());
			e.printStackTrace();
			return ERROR;
		}
		return "refreshDocument";
	}

	public String doAfter() throws Exception {
		return doAction();
	}

	public String get_activityid() {
		return _activityid;
	}

	public void set_activityid(String _activityid) {
		this._activityid = _activityid;
	}

	/**
	 * 保存并创建一条有数据的记录文档. 如果成功处理，返回"SUCCESS",将再创建一条有旧数据的记录文档。否则返回"INPUT",创建失败。
	 * 
	 * @return "SUCCESS" or "ERROR"
	 * @throws Exception
	 */
	public String doSaveNewWithOld(Activity act) {
		try {
			if (doSaveDocument(act).equals("viewDocument")) {
				return doNewDocument(act);
			} else {
				return ERROR;
			}
		} catch (Exception e) {
			addFieldError("SystemError", e.getMessage());
			e.printStackTrace();
			return ERROR;
		}

	}

	/**
	 * 保存并创建一条空的记录文档,如果成功处理. 返回"SUCCESS",将再创建一条空的记录文档。否则返回"INPUT",创建失败。
	 * 
	 * @return "SUCCESS" or "ERROR"
	 * @throws Exception
	 */
	public String doSaveNewWithOutOld(Activity act) {
		try {
			if (doSaveDocument(act).equals("viewDocument")) {
				WebUser user = getUser();
				FormProcess formPross = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
				DocumentProcess proxy = createDocumentProcess(getApplication());
				ParamsTable params = getParams();
				String formid = params.getParameterAsString("_formid");

				Form form = (Form) formPross.doView(formid);
				if (form != null) {
					Document newDoc = proxy.doNewWithOutItems(form, user, params);
					setContent(newDoc);
					MemoryCacheUtil.putToPrivateSpace(newDoc.getId(), newDoc, user);
					toXml(newDoc, new ArrayList<String>(), false);
				}

				return "viewDocument";
			} else {
				return ERROR;
			}
		} catch (Exception e) {
			addFieldError("SystemError", e.getMessage());
			e.printStackTrace();
			return ERROR;
		}
	}

	public String doSaveAndCopy(Activity act) {
		try {
			if (doSaveDocument(act).equals("viewDocument")) {
				WebUser user = getUser();
				Document oldDoc = (Document) getContent();
				ParamsTable params = getParams();
				String formid = params.getParameterAsString("_formid");
				FormProcess formPross = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
				DocumentProcess proxy = createDocumentProcess(getApplication());
				oldDoc.setDomainid(getDomain());
				Form form = (Form) formPross.doView(formid);
				Document newDoc = proxy.doNewWithChildren(form, user, params, oldDoc.getChilds());
				setContent(newDoc);
				MemoryCacheUtil.putToPrivateSpace(newDoc.getId(), newDoc, user);
				toXml(newDoc, new ArrayList<String>(), false);
				return "viewDocument";
			} else {
				return ERROR;
			}
		} catch (Exception e) {
			addFieldError("SystemError", e.getMessage());
			e.printStackTrace();
			return ERROR;
		}
	}

	/**
	 * 保存文档并关闭
	 * 
	 * @return
	 * @throws Exception
	 */
	public String doSaveClose(Activity act) {
		if (doSaveDocument(act).equals("viewDocument"))
			return "viewList";
		return ERROR;
	}

	private String doNewDocument(Activity act) throws Exception {
		try {
			WebUser user = getUser();
			ParamsTable params = getParams();
			// DocumentProcess proxy =
			// createDocumentProcess(user.getApplicationid());
			FormProcess formProcess = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
			Form form = (Form) formProcess.doView(act.getOnActionForm());
			if (form == null) {
				String formid = params.getParameterAsString("_formid");
				form = (Form) formProcess.doView(formid);
			}
			Document doc = form.createDocument(params, user);
			DocumentBuilder builder = new DocumentBuilder(doc, params);
			builder.setForm(form);
			Document newDoc = builder.getNewDocument(user);
			// proxy.doCreate(newDoc);
			setContent(newDoc);

			MemoryCacheUtil.putToPrivateSpace(newDoc.getId(), newDoc, user);
			toXml(doc, new ArrayList<String>(), false);
			return "viewDocument";
		} catch (Exception e) {
			addFieldError("SystemError", e.getMessage());
			e.printStackTrace();
			return ERROR;
		}
	}

	public String getWebUserSessionKey() {
		return Web.SESSION_ATTRIBUTE_FRONT_USER;
	}

	public String doSaveWithOutValidate(Activity act) {
		try {
			WebUser user = this.getUser();
			ParamsTable params = getParams();
			String formid = params.getParameterAsString("_formid");
			DocumentProcess proxy = createDocumentProcess(getApplication());
			FormProcess formProcess = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
			Form form = (Form) formProcess.doView(formid);
			Document doc = form.createDocument(params, user);
			doc.setLastmodifier(user.getId());
			doc.setDomainid(getDomain());
			proxy.doStartFlowOrUpdate(doc, params, user);

			setContent(doc);
			MemoryCacheUtil.putToPrivateSpace(doc.getId(), doc, getUser());
			toXml(doc, new ArrayList<String>(), false);
		} catch (Exception e) {
			this.addFieldError("1", e.getMessage());
			e.printStackTrace();
			return ERROR;
		}

		return SUCCESS;
	}

	/**
	 * 保存文档并返回.
	 * 
	 * @return SUCCESS OR ERROR
	 * @throws Exception
	 */
	public String doSaveBack(Activity act) {
		if (doSaveDocument(act).equals("viewDocument"))
			return "viewList";
		return ERROR;
	}

	/**
	 * 保存文档. 文档无状态并有流程时开启流程.
	 * 
	 * @return SUCCESS，ERROR
	 * @throws Exception
	 */
	private String doSaveDocument(Activity act) {
		if (!doValidate())
			return ERROR;
		try {
			WebUser user = this.getUser();
			ParamsTable params = getParams();
			String formid = params.getParameterAsString("_formid");
			DocumentProcess proxy = createDocumentProcess(getApplication());
			FormProcess formProcess = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
			Form form = (Form) formProcess.doView(formid);
			String docid = getParams().getParameterAsString("_docid");
			Document olddoc = (Document) proxy.doView(docid);
			Document doc = null;
			if (olddoc != null) {
				doc = form.createDocument(olddoc, params, user);
			} else {
				doc = form.createDocument(params, user);
			}
			if (docid != null && docid.length() > 0) {
				doc.setId(docid);
			}
			doc.setDomainid(getDomain());
			proxy.doStartFlowOrUpdate(doc, params, user);
			setContent(doc);
			MemoryCacheUtil.putToPrivateSpace(doc.getId(), doc, getUser());
			toXml(doc, new ArrayList<String>(), false);
		} catch (Exception e) {
			this.addFieldError("SystemError", e.getMessage());
			e.printStackTrace();
			return ERROR;
		}
		// }
		return "viewDocument";
	}

	private boolean toFlowHisXml() {
		try {
			ParamsTable params = getParams();
			String docid = params.getParameterAsString("_docid");
			Document doc = null;
			if (!StringUtil.isBlank(docid)) {
				doc = DocumentHelper.getDocumentById(docid, getApplication());
			}
			if (doc != null && doc.getParent() == null && !doc.getIstmp()) {
				String xmlText = StateMachineHelper.toHistoryXml(doc, 4);
				HttpServletRequest request = ServletActionContext.getRequest();
				// HttpSession session = request.getSession();
				request.setAttribute("toXml", xmlText);
				return true;
			}
		} catch (Exception e) {
			addFieldError("SystemError", e.getMessage());
		}
		return false;
	}

	private void toXml(Document doc, Collection<String> columnNames, boolean isRefresh) throws Exception {
		boolean isEdit = true;
		WebUser webUser = getUser();
		ParamsTable params = getParams();

		String formid = doc.getFormid();

		if (formid == null || formid.length() <= 0) {
			formid = params.getParameterAsString("formid");
		}

		Form form = FormHelper.get_FormById(formid);
		if (form == null) {
			throw new Exception("Form {*[does not exist or deleted]*}!");
		}
		isEdit = StateMachineHelper.isDocEditUser(doc, webUser);
		IRunner runner = JavaScriptFactory.getInstance(params.getSessionid(), form.getApplicationid());
		runner.initBSFManager(doc, params, webUser, new ArrayList<ValidateMessage>());

		String docid = params.getParameterAsString("_docid");
		if (docid == null) {
			docid = "";
		}
		if (form.getIseditablescript() != null && form.getIseditablescript().trim().length() > 0) {
			StringBuffer label = new StringBuffer();
			label.append("DocumentContent.Form(").append(form.getId()).append(")." + form.getName()).append(
					".runIsEditAbleScript");

			Object result = runner.run(label.toString(), form.getIseditablescript());
			if (result != null && result instanceof Boolean) {
				boolean isEdit_bs = (((Boolean) result).booleanValue());
				if (isEdit_bs != isEdit)
					isEdit = false;
			}
		}
		doc.setEditAble(isEdit);

		if (form != null) {
			String xmlText = form.toMbXML(doc, params, columnNames, webUser, new ArrayList<ValidateMessage>(),
					getEnvironment(), isRefresh);
			HttpServletRequest request = ServletActionContext.getRequest();
			// HttpSession session = request.getSession();
			request.setAttribute("toXml", xmlText);
		}
	}

	private static DocumentProcess createDocumentProcess(String applicationid) throws CreateProcessException {
		DocumentProcess process = (DocumentProcess) ProcessFactory.createRuntimeProcess(DocumentProcess.class,
				applicationid);

		return process;
	}

	private String _viewid;
	/**
	 * 下一个节点数组
	 */
	private String[] _nextids;

	/**
	 * 当前节点id
	 */
	private String _currid;

	private String _flowType;

	private String _attitude;

	public String get_attitude() {
		return _attitude;
	}

	public void set_attitude(String _attitude) {
		this._attitude = _attitude;
	}

	public String get_flowType(BillDefiVO flowVO) throws Exception {
		String[] nextids = get_nextids();
		String currNodeid = null;
		for (int i = 0; i < nextids.length; i++) {
			if (nextids[i] != null && !nextids[i].trim().equals("")) {
				currNodeid = nextids[i];
				break;
			}
		}
		Node nextNode = StateMachineHelper.getCurrNode(flowVO, currNodeid);

		_flowType = FlowType.RUNNING2RUNNING_NEXT;
		if (!(nextNode instanceof ManualNode)) {// 下一个节点中是否存在suspend
			if (nextNode instanceof SuspendNode) {
				_flowType = FlowType.RUNNING2SUSPEND;
			} else if (nextNode instanceof AbortNode) {
				_flowType = FlowType.RUNNING2ABORT;
			} else if (nextNode instanceof TerminateNode) {
				_flowType = FlowType.RUNNING2TERMIATE;
			} else if (nextNode instanceof CompleteNode) {
				_flowType = FlowType.RUNNING2COMPLETE;
			}
		}
		return _flowType;
	}

	public void set_flowType(String type) {
		_flowType = type;
	}

	public String get_currid() {
		return _currid;
	}

	public void set_currid(String _currid) {
		this._currid = _currid;
	}

	public String[] get_nextids() {
		if (_nextids != null && _nextids.length == 1) {
			String tmp = _nextids[0].endsWith(";") ? _nextids[0].substring(0, _nextids[0].length() - 1) : _nextids[0];
			String[] spl = tmp.split(";");
			_nextids = new String[spl.length];
			for (int i = 0; i < spl.length; i++) {
				_nextids[i] = spl[i];
			}
		}
		return _nextids;
	}

	public void set_nextids(String[] _nextids) {
		this._nextids = _nextids;
	}

	public String get_viewid() {
		return _viewid;
	}

	public void set_viewid(String _viewid) {
		this._viewid = _viewid;
	}

	// private Collection _columnNames;

	private String domain = null;

	/**
	 * 删除Document
	 * 
	 * @return SUCCESS OR ERROR
	 * @throws Exception
	 */
	public String doDelete(Activity act) throws Exception {
		if (_selects != null) {
			DocumentProcess proxy = createDocumentProcess(getApplication());
			proxy.doRemove(_selects);
		}

		return "viewList";
	}

	// public Collection get_columnNames() {
	// return _columnNames;
	// }
	//
	// public void set_columnNames(Collection names) {
	// _columnNames = names;
	// }

	public String getDomain() {
		if (domain != null && domain.trim().length() > 0) {
			return domain;
		} else {
			return (String) getContext().getSession().get(Web.SESSION_ATTRIBUTE_DOMAIN);
		}
	}

	public void setDomain(String domain) {
		this.domain = domain;
		getContent().setDomainid(domain);
	}

	public String getApplication() {
		if (application != null && application.trim().length() > 0) {
			return application;
		} else {
			return (String) getContext().getSession().get(Web.SESSION_ATTRIBUTE_APPLICATION);
		}
	}

	private Map<String, List<String>> fieldErrors;

	public void addFieldError(String fieldname, String message) {
		List<String> thisFieldErrors = getFieldErrors().get(fieldname);

		if (thisFieldErrors == null) {
			thisFieldErrors = new ArrayList<String>();
			this.fieldErrors.put(fieldname, thisFieldErrors);
		}
		thisFieldErrors.add(message);
	}

	public Map<String, List<String>> getFieldErrors() {
		if (fieldErrors == null)
			fieldErrors = new HashMap<String, List<String>>();
		return fieldErrors;
	}

	/**
	 * @SuppressWarnings API支持泛型Map(String, List(String))
	 */
	@SuppressWarnings("unchecked")
	public void setFieldErrors(Map fieldErrors) {
		this.fieldErrors = fieldErrors;
	}

	/**
	 * 校验当前用户是否可以保存文档.
	 * 根据当前Document是否有子Document并且是否可以编辑,若有子Document并且可以编辑,返回true,
	 * 此时可以保存当前Document. 并根据Document id 、 flow(流程)id 与当前用户作为参数条件来判断.
	 */
	public boolean doValidate() {
		boolean flag = true;
		try {
			ParamsTable params = getParams();
			WebUser webUser = getUser();
			String formid = params.getParameterAsString("_formid");
			DocumentProcess proxy = createDocumentProcess(getApplication());
			FormProcess formProcess = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
			Form form = (Form) formProcess.doView(formid);
			String docid = params.getParameterAsString("_docid");
			Document olddoc = (Document) proxy.doView(docid);
			Document doc = null;
			if (olddoc != null) {
				doc = form.createDocument(olddoc, params, webUser);
			} else {
				doc = form.createDocument(params, webUser);
			}
			if (proxy.isDocSaveUser(doc, params, webUser)) {
				Collection<ValidateMessage> errors = proxy.doValidate(doc, params, webUser);
				if (errors != null && errors.size() > 0) {
					for (Iterator<ValidateMessage> iter = errors.iterator(); iter.hasNext();) {
						ValidateMessage err = iter.next();
						addFieldError("SystemError", err.getErrmessage());
					}
					flag = false;
				}
			} else {
				flag = false;
				addFieldError("SystemError", "{*[core.document.cannotsave]*}");
			}
			setContent(doc);
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
			addFieldError("SystemError", e.getMessage());
		}
		return flag;
	}

	@Override
	public void set_selects(String[] selects) {
		if (selects != null && selects.length == 1) {
			_selects = selects[0].split(";");
		} else {
			super.set_selects(selects);
		}
	}

}
