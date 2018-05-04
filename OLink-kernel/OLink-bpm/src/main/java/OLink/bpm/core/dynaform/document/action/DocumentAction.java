package OLink.bpm.core.dynaform.document.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.base.ejb.IRunTimeProcess;
import OLink.bpm.constans.Environment;
import OLink.bpm.constans.Web;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.dynaform.form.ejb.ValidateMessage;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.core.workflow.FlowType;
import OLink.bpm.core.workflow.engine.StateMachine;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiProcess;
import OLink.bpm.core.workflow.storage.runtime.ejb.*;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import OLink.bpm.util.cache.MemoryCacheUtil;
import OLink.bpm.util.property.DefaultProperty;
import OLink.bpm.core.dynaform.form.action.ImpropriateException;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.workflow.element.Node;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiVO;
import OLink.bpm.core.workflow.storage.runtime.ejb.ActorRT;
import OLink.bpm.core.workflow.storage.runtime.ejb.CirculatorProcess;
import OLink.bpm.base.action.AbstractRunTimeAction;
import OLink.bpm.core.dynaform.document.ejb.DocumentProcess;
import OLink.bpm.core.dynaform.form.ejb.FormField;
import OLink.bpm.core.user.action.WebUser;
import org.apache.log4j.Logger;
import org.jfree.util.Log;

import OLink.bpm.core.workflow.storage.runtime.ejb.ActorRTProcessBean;
import OLink.bpm.util.CreateProcessException;
import OLink.bpm.util.OBPMSessionContext;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.webwork.portlet.context.PortletActionContext;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.Preparable;

/**
 * @author Marky
 */
public class DocumentAction extends AbstractRunTimeAction<Document> implements
		Preparable {
	private static final Logger log = Logger.getLogger(DocumentAction.class);

	private static final long serialVersionUID = 1L;

	private Collection<FormField> _fieldSelect;

	private String _flowid;

	private String _formid;

	private String parentid;

	private ValueObject content;

	private String _approveLimit;

	/**
	 * 批量提交、流程处理按钮节点
	 * 
	 * @return
	 */
	public String get_approveLimit() {
		return _approveLimit;
	}

	public void set_approveLimit(String approveLimit) {
		_approveLimit = approveLimit;
	}

	/**
	 * 返回父文档主键.
	 * 
	 * @return 父文档主键
	 * @uml.property name="parentid"
	 */
	public String getParentid() {
		return parentid;
	}

	/**
	 * 设置父文档主键.
	 * 
	 * @param parentid
	 *            父文档主键
	 * @throws Exception
	 * @uml.property name="parentid"
	 */
	public void setParentid(String parentid) throws Exception {
		this.parentid = parentid;
	}

	/**
	 * 返回文档主键.
	 * 
	 * @return 文档主键
	 * @throws Exception
	 */
	public String get_docid() throws Exception {
		ValueObject doc = getContent();
		if (doc != null)
			return doc.getId();
		else
			return null;
	}

	/**
	 * 设置文档主键
	 * 
	 * @param _docid
	 *            文档主键
	 * @throws Exception
	 */
	public void set_docid(String _docid) throws Exception {
		getContent().setId(_docid);
	}

	/**
	 * 返回文档值对象
	 * 
	 * @return 文档值对象
	 * @uml.property name="content"
	 */
	public ValueObject getContent() {
		if (content == null) {
			content = new Document();
		}
		return content;
	}

	/**
	 * DocumentAction构造函数
	 * 
	 * @throws ClassNotFoundException
	 */
	public DocumentAction() throws ClassNotFoundException {
	}

	/**
	 * 根据文档主键，查找文档.
	 * 
	 * @SuppressWarnings webwork 不支持泛型
	 * @return SUCCESS
	 * @throws Exception
	 */

	@SuppressWarnings("unchecked")
	public String doView() throws Exception {
		Map<String, String> request = (Map<String, String>) ActionContext
				.getContext().get("request");

		WebUser user = this.getUser();
		String _docid = this.getParams().getParameterAsString("_docid");
		// log.info("session id is:" + this.getParams().getSessionid());
		DocumentProcess proxy = (DocumentProcess) getProcess();
		Document doc = (Document) proxy.doView(_docid);
		if (doc == null) {
			doc = new Document();
		}
		// 为了防止解析iscript出现异常，因此将当前的doc对象放入session当中去
		try {
			HttpSession oldSession = OBPMSessionContext.getInstance()
					.getSession(this.getParams().getSessionid());
			oldSession.setAttribute("_currentDocObj", doc);
		} catch (Exception ex) {
			// log.error("get session error:" + ex.getMessage());
		}
		doc.get_params().setSessionid(this.getParams().getSessionid());
		// log.info("已经把文档对象设置进了session中，session id is："
		// + this.getParams().getSessionid());

		FormProcess formPross = (FormProcess) ProcessFactory
				.createProcess(FormProcess.class);
		String formid = get_formid();
		if (StringUtil.isBlank(formid)) {
			formid = doc.getFormid();
		}
		Form form = (Form) formPross.doView(formid);
		if (form != null) {
			form.recalculateDocument(doc, getParams(), user);
		}

		// -------------------------文档已阅未阅功能
		if (doc.getState() != null) {
			Collection<ActorRT> actors = doc.getState().getActors();
			for (Iterator<ActorRT> iter = actors.iterator(); iter.hasNext();) {
				ActorRT actor = iter.next();
				if (user.getId().equals(actor.getActorid())) {
					if (!actor.getIsread()) {
						actor.setIsread(true);
						ActorRTProcess process = new ActorRTProcessBean(
								getApplication());
						process.doUpdate(actor);
					}
					break;
				}
			}
			// 查找抄送人为当前用户
			CirculatorProcess cProcess = (CirculatorProcess) ProcessFactory
					.createRuntimeProcess(CirculatorProcess.class,
							getApplication());
			Circulator circulator = cProcess.findByCurrDoc(doc.getId(), doc
					.getState().getId(), false, user);
			if (circulator != null) {
				circulator.setRead(true);
				circulator.setReadTime(new Date());
				cProcess.doUpdate(circulator);// 更新为已阅
			}

			// -------------------------------- 选择可执行的流程实例
			if (!StringUtil.isBlank(doc.getId())) {
				FlowStateRTProcess stateProcess = (FlowStateRTProcess) ProcessFactory
						.createRuntimeProcess(FlowStateRTProcess.class,
								getApplication());
				if (stateProcess.isMultiFlowState(doc)) {// 有多个没完成是流程实例
					doc.setState(stateProcess.getCurrFlowStateRT(doc, user,
							null));// 绑定一个可执行的文档实例
					doc.setMulitFlowState(stateProcess.isMultiFlowState(doc,
							user));// 是否存在多个可执行实例
				}
			}
		}

		setContent(doc);
		request.put("signatureExist",
				this.getParams().getParameterAsString("signatureExist"));
		MemoryCacheUtil.putToPrivateSpace(doc.getId(), doc, user);

		return SUCCESS;
	}

	/**
	 * 根据文档主键，查找session中的document.
	 * 
	 * @return SUCCESS
	 * @throws Exception
	 */
	public String doPrint() throws Exception {
		WebUser user = this.getUser();
		String _docid = this.getParams().getParameterAsString("_docid");
		Document doc = (Document) MemoryCacheUtil.getFromPrivateSpace(_docid,
				user);
		setContent(doc);
		return SUCCESS;
	}

	/**
	 * 新建一个Document.
	 * 
	 * @return result. 处理成功返回"SUCCESS"
	 * @throws Exception
	 */
	public String doNew() {
		try {
			FormProcess formPross = (FormProcess) ProcessFactory
					.createProcess(FormProcess.class);
			DocumentProcess proxy = (DocumentProcess) getProcess();
			WebUser user = getUser();
			Form form = (Form) formPross.doView(get_formid());
			if (form != null) {
				Document newDoc = proxy.doNew(form, user, getParams());
				setContent(newDoc);
				// 放入Session中
				ServletActionContext.getRequest().setAttribute("content.id",
						newDoc.getId());
				MemoryCacheUtil.putToPrivateSpace(newDoc.getId(), newDoc, user);
			}
			return SUCCESS;
		} catch (Exception e) {
			addFieldError("1", e.getMessage());
			e.printStackTrace();
			return INPUT;
		}
	}

	/**
	 * 保存并创建一条有数据的记录文档. 如果成功处理，返回"SUCCESS",将再创建一条有旧数据的记录文档。否则返回"INPUT",创建失败。
	 * 
	 * @return "SUCCESS" or "INPUT"
	 * @throws Exception
	 */
	public String doSaveNewWithOld() {
		try {
			if (doSave().equals(SUCCESS)) {
				return doNew();
			} else {
				return INPUT;
			}
		} catch (Exception e) {
			addFieldError("1", e.getMessage());
			e.printStackTrace();
			return INPUT;
		}

	}

	/**
	 * 保存并创建一条空的记录文档,如果成功处理. 返回"SUCCESS",将再创建一条空的记录文档。否则返回"INPUT",创建失败。
	 * 
	 * @return "SUCCESS" or "INPUT"
	 * @throws Exception
	 */
	public String doSaveNewWithOutOld() {
		try {
			if (doSave().equals(SUCCESS)) {
				WebUser user = getUser();
				FormProcess formPross = (FormProcess) ProcessFactory
						.createProcess(FormProcess.class);
				DocumentProcess proxy = (DocumentProcess) getProcess();
				Form form = (Form) formPross.doView(get_formid());
				if (form != null) {
					Document newDoc = proxy.doNewWithOutItems(form, user,
							getParams());
					setContent(newDoc);
					ServletActionContext.getRequest().setAttribute(
							"content.id", newDoc.getId());
					MemoryCacheUtil.putToPrivateSpace(newDoc.getId(), newDoc,
							user);
				}
				return SUCCESS;
			} else {
				return INPUT;
			}
		} catch (Exception e) {
			addFieldError("1", e.getMessage());
			e.printStackTrace();
			return INPUT;
		}
	}

	public String doSaveAndCopy() {
		try {
			if (doSave().equals(SUCCESS)) {
				WebUser user = getUser();
				Document oldDoc = (Document) getContent();

				FormProcess formPross = (FormProcess) ProcessFactory
						.createProcess(FormProcess.class);
				DocumentProcess proxy = (DocumentProcess) getProcess();
				Form form = (Form) formPross.doView(get_formid());
				Document newDoc = proxy.doNewWithChildren(form, user,
						getParams(), oldDoc.getChilds());
				setContent(newDoc);
				MemoryCacheUtil.putToPrivateSpace(newDoc.getId(), newDoc, user);
				return SUCCESS;
			} else {
				return INPUT;
			}
		} catch (Exception e) {
			addFieldError("1", e.getMessage());
			e.printStackTrace();
			return INPUT;
		}
	}

	public String doPreview() {
		try {
			FormProcess formPross = (FormProcess) ProcessFactory
					.createProcess(FormProcess.class);
			WebUser user = getUser();
			Form form = (Form) formPross.doView(get_formid());
			if (form != null) {
				Document newDoc = form.createDocument(getParams(), user);
				setContent(newDoc);
			}
		} catch (Exception e) {
			addFieldError("1", e.getMessage());
		}

		return SUCCESS;
	}

	public String doNothing() throws Exception {
		Document doc = (Document) getContent();
		MemoryCacheUtil.putToPrivateSpace(doc.getId(), doc, getUser());
		return SUCCESS;
	}

	/**
	 * 保存文档. 文档无状态并有流程时开启流程.
	 * 
	 * @return SUCCESS，INPUT
	 * @throws Exception
	 * @throws Exception
	 */
	public String doSave() throws Exception {
		try {
			WebUser user = this.getUser();

			DocumentProcess proxy = (DocumentProcess) getProcess();

			Document doc = (Document) getContent();
			doc.setDomainid(getDomain());
			doc = rebuildDocument(doc, getParams());
			proxy.doCreateOrUpdate(doc, user);

			setContent(doc);
			MemoryCacheUtil.putToPrivateSpace(doc.getId(), doc, getUser());
			this.addActionMessage("{*[Save_Success]*}");
		} catch (Exception e) {
			this.addFieldError("1", e.getMessage());
			if ((e instanceof ImpropriateException)) {
				// 加载数据库中最新的文档到上下文环境
				setContent(getProcess().doView(getContent().getId()));
				MemoryCacheUtil.putToPrivateSpace(getContent().getId(),
						getContent(), getUser());
			} else {
				e.printStackTrace();
			}
			return INPUT;
		}
		return SUCCESS;
	}

	public String doSaveStartWorkFlow() throws Exception {
		try {
			WebUser user = this.getUser();
			ParamsTable params = getParams();
			DocumentProcess proxy = (DocumentProcess) getProcess();
			Document doc = (Document) getContent();
			doc = rebuildDocument(doc, params);
			doc.setDomainid(getDomain());
			doc.setVersions(Integer.valueOf(params
					.getParameterAsString("content.versions")));
			proxy.doStartFlowOrUpdate(doc, params, user);
			setContent(doc);
			MemoryCacheUtil.putToPrivateSpace(doc.getId(), doc, getUser());
			this.addActionMessage("{*[Save_Success]*}");
		} catch (Exception e) {
			this.addFieldError("1", e.getMessage());
			if ((e instanceof ImpropriateException)) {
				// 加载数据库中最新的文档到上下文环境
				setContent(getProcess().doView(getContent().getId()));
				MemoryCacheUtil.putToPrivateSpace(getContent().getId(),
						getContent(), getUser());
			} else {
				e.printStackTrace();
			}
			return INPUT;
		}
		return SUCCESS;
	}

	/*
	 * 启动流程
	 */
	@SuppressWarnings("deprecation")
	public String doStartWorkFlow() {
		try {
			WebUser user = this.getUser();
			ParamsTable params = getParams();

			DocumentProcess proxy = (DocumentProcess) getProcess();
			BillDefiProcess flowProcss = (BillDefiProcess) ProcessFactory
					.createProcess(BillDefiProcess.class);
			Document doc = (Document) getContent();
			doc = rebuildDocument(doc, params);
			doc.setDomainid(getDomain());
			BillDefiVO flowVO = null;
			String selectFlow = "";
			if (params.getParameterAsDouble("_editMode") == null
					|| params.getParameterAsDouble("_editMode") == 0) {
				selectFlow = params.getParameterAsString("selectFlow");
			} else if (params.getParameterAsDouble("_editMode") == 1) {
				selectFlow = (String) params.getHttpRequest().getAttribute(
						"selectFlow");
			}

			if (selectFlow != null && selectFlow != "") {
				doc.setFlowid(selectFlow);
				flowVO = (BillDefiVO) flowProcss.doView(selectFlow);
				doc.setFlowVO(flowVO);
				params.setParameter("_flowid", selectFlow);
			} else {
				flowVO = doc.getFlowVO();
			}
			Node firstNode = StateMachine
					.getFirstNode(flowVO, user);

			if (firstNode != null) {
				Node startNode = StateMachine
						.getStartNodeByFirstNode(flowVO, firstNode);
				if (startNode != null) {
					proxy.doStartFlowOrUpdate(doc, params, user);
				}
			} else {
				proxy.doCreateOrUpdate(doc, user);
			}
			setContent(doc);
			MemoryCacheUtil.putToPrivateSpace(doc.getId(), doc, getUser());
			this.addActionMessage("成功保存并开启流程！");
		} catch (Exception e) {
			this.addFieldError("1", e.getMessage());
			e.printStackTrace();
			return INPUT;
		}
		return SUCCESS;
	}

	public String doSaveWithOutValidate() throws Exception {
		try {
			WebUser user = getUser();
			Document doc = (Document) getContent();
			doc = rebuildDocument(doc, getParams());
			DocumentProcess proxy = (DocumentProcess) getProcess();
			doc.setLastmodifier(user.getId());
			doc.setDomainid(getDomain());
			proxy.doCreateOrUpdate(doc, user);
			setContent(doc);
			MemoryCacheUtil.putToPrivateSpace(doc.getId(), doc, getUser());
			this.addActionMessage("{*[Save_Success]*}");
		} catch (Exception e) {
			this.addFieldError("1", e.getMessage());
			if ((e instanceof ImpropriateException)) {
				// 加载数据库中最新的文档到上下文环境
				setContent(getProcess().doView(getContent().getId()));
				MemoryCacheUtil.putToPrivateSpace(getContent().getId(),
						getContent(), getUser());
			} else {
				e.printStackTrace();
			}
			return INPUT;
		}

		return SUCCESS;
	}

	/**
	 * 保存文档并关闭
	 * 
	 * @return
	 * @throws Exception
	 * @throws Exception
	 */
	public String doSaveClose() throws Exception {
		return doSave();
	}

	/**
	 * 返回
	 * 
	 * @return
	 */
	public String doBack() {
		try {
			// 清空对应的Document
			getUser().removeFromTmpspace(getContent().getId());
		} catch (Exception e) {
			addFieldError("", e.getMessage());
			return INPUT;
		}
		return SUCCESS;
	}

	/**
	 * 保存文档并返回.
	 * 
	 * @return SUCCESS OR INPUT
	 * @throws Exception
	 * @throws Exception
	 */
	public String doSaveBack() throws Exception {
		return doSave();
	}

	/**
	 * 批量审批流程
	 * 
	 * @return SUCCESS
	 */
	public String doBattchApprove() {
		String limistStrList = this._approveLimit;
		// String limistStrList = (String)
		// ServletActionContext.getRequest().getAttribute("ApproveLimist");
		Collection<String> limistList = new ArrayList<String>();

		if (!StringUtil.isBlank(limistStrList)) {
			limistList = Arrays.asList(limistStrList.split(","));
		}

		try {
			DocumentProcess proxy = (DocumentProcess) getProcess();

			proxy.doBatchApprove(get_selects(), getUser(), getEnvironment(),
					getParams(), limistList);
		} catch (Exception e) {
			addFieldError("1", e.getMessage());
			e.printStackTrace();
		}

		return SUCCESS;
	}

	/**
	 * 校验当前用户是否可以保存文档.
	 * 根据当前Document是否有子Document并且是否可以编辑,若有子Document并且可以编辑,返回true,
	 * 此时可以保存当前Document. 并根据Document id 、 flow(流程)id 与当前用户作为参数条件来判断.
	 */
	public void validate() {
		final Document doc = (Document) getContent();

		try {
			final ParamsTable params = getParams();
			final WebUser webUser = getUser();
			String _flowType = (String) params.getParameter("_flowType");
			if ("retracement".equals(_flowType)) {
				_flowType = "85";
			}
			DocumentProcess proxy = (DocumentProcess) ProcessFactory
					.createRuntimeProcess(DocumentProcess.class,
							getApplication());
			if (proxy.isDocSaveUser(doc, params, webUser)
					|| (_flowType != null && _flowType
							.equals(FlowType.RUNNING2RUNNING_RETRACEMENT))) {
				Collection<ValidateMessage> errors = proxy.doValidate(doc,
						params, webUser);
				if (errors != null && errors.size() > 0) {
					for (Iterator<ValidateMessage> iter = errors.iterator(); iter
							.hasNext();) {
						ValidateMessage err = iter.next();
						this.addFieldError(err.getFieldname(),
								err.getErrmessage());
					}
				}
			} else {
				addFieldError("isDocSaveUser", "{*[core.document.cannotsave]*}");
			}
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("1", e.getMessage());
		}
		setContent(doc);
	}

	/**
	 * 返回相关表单主键
	 * 
	 * @return 表单主键
	 * @uml.property name="formid"
	 */
	public String get_formid() {
		return _formid;
	}

	/**
	 * 设置相关表单主键
	 * 
	 * @param formid
	 * @uml.property name="formid"
	 */
	public void set_formid(String _formid) {
		this._formid = _formid;
	}

	/**
	 * 返回相应流程主键.
	 * 
	 * @return 流程主键
	 */
	public String get_flowid() {
		return _flowid;
	}

	/**
	 * 设置相应流程
	 * 
	 * @param _flowid
	 *            流程主键
	 */
	public void set_flowid(String _flowid) {
		String[] flowids = null;
		if (_flowid.indexOf(",") != -1) { // 有多个parmeter时只获取一个
			flowids = _flowid.trim().split(",");
			_flowid = flowids[0];
		}
		this._flowid = _flowid;
	}

	/**
	 * 获取参数. 从http请求获取出参数（paramters）.
	 * 
	 * 如：请求ServletContext,请求ServerName，请求ContextPath等.
	 * 若还没有设置每一页显示的Document行数时,此设置为默认的每页显示10条Dcument记录. 否则按VIEW的设置条数显示.
	 * 
	 * @return 参数表
	 * @see ParamsTable#params
	 * @see ParamsTable#convertHTTP(HttpServletRequest)
	 */
	public ParamsTable getParams() {
		ParamsTable pm = null;

		if (PortletActionContext.isPortletRequest()) {
			pm = ParamsTable.convertHTTP(PortletActionContext.getRequest());
		} else {
			pm = ParamsTable.convertHTTP(ServletActionContext.getRequest());
		}

		if (pm.getParameter("_pagelines") == null) {
			pm.setParameter("_pagelines", Web.DEFAULT_LINES_PER_PAGE);
		}

		return pm;
	}

	/**
	 * 返回查询的字段
	 * 
	 * @return 查询的字段
	 * @uml.property name="_fieldSelect"
	 */
	public Collection<FormField> get_fieldSelect() {
		return _fieldSelect;
	}

	/**
	 * 设置查询的字段
	 * 
	 * @param select
	 * @uml.property name="_fieldSelect"
	 */
	public void set_fieldSelect(Collection<FormField> select) {
		_fieldSelect = select;
	}

	/**
	 * 设置Document对象
	 * 
	 * @param content
	 *            Document对象
	 * @uml.property name="content"
	 */
	public void setContent(ValueObject content) {
		this.content = content;
	}

	/**
	 * 返回web 用户对象
	 * 
	 * @return web user.
	 * @throws Exception
	 */
	public WebUser getUser() throws Exception {
		Map<?, ?> session = getContext().getSession();
		WebUser user = null;
		if (session == null
				|| session.get(Web.SESSION_ATTRIBUTE_FRONT_USER) == null) {
			UserVO vo = new UserVO();
			vo.getId();
			vo.setName("GUEST");
			vo.setLoginno("guest");
			vo.setLoginpwd("");
			vo.setRoles(null);
			vo.setEmail("");
			// vo.setLanguageType(1);
			user = new WebUser(vo);
		} else {
			user = (WebUser) session.get(Web.SESSION_ATTRIBUTE_FRONT_USER);
		}
		return user;
	}

	/**
	 * 获取ActionContext
	 * 
	 * @return ActionContext
	 */
	public static ActionContext getContext() {
		ActionContext context = ActionContext.getContext();
		return context;
	}

	/**
	 * 获取设置环境
	 * 
	 * @return Environment
	 */
	public Environment getEnvironment() {
		String ctxPath = ServletActionContext.getRequest().getContextPath();
		Environment evt = Environment.getInstance();
		evt.setContextPath(ctxPath);
		return evt;
	}

	/**
	 * 返回存放Document id 数组
	 * 
	 * @return 存放Document id 数组
	 * @uml.property name="_selects"
	 */
	public String[] get_selects() {
		return _selects;
	}

	/**
	 * 设置存放Document id 数组
	 * 
	 * @param selects
	 * @uml.property name="_selects"
	 */
	public void set_selects(String[] selects) {
		this._selects = selects;
	}

	/**
	 * 删除Document
	 * 
	 * @return SUCCESS OR ERROR
	 * @throws Exception
	 */
	public String doDelete() {
		try {
			if (_selects != null) {
				DocumentProcess proxy = (DocumentProcess) getProcess();
				proxy.doRemove(_selects);
			}
		} catch (Exception e) {
			log.error("doDelete", e);
			addFieldError("", e.getMessage());
		}

		return SUCCESS;
	}

	/**
	 * 删除地图Document
	 * 
	 * @return SUCCESS OR ERROR
	 * @throws Exception
	 */
	public String doDeleteMap() {
		try {
			if (_selects != null) {
				DocumentProcess proxy = (DocumentProcess) getProcess();
				proxy.doRemove(_selects);
			}
		} catch (Exception e) {
			log.error("doDelete", e);
			addFieldError("", e.getMessage());
		}

		return SUCCESS;
	}

	/**
	 * 查出这个formid的所有document,然后一条条删除，如果是审批中或者是审批完成不能删除的，则放到errorfiel中显示出来
	 * 
	 * @return SUCCESS
	 * @throws Exception
	 */
	public String doDeleteAll() throws Exception {
		try {
			DocumentProcess proxy = (DocumentProcess) getProcess();
			Collection<Document> docs = proxy.queryByDQL(getSimpleDql(),
					getDomain()).datas;

			for (Iterator<Document> iter = docs.iterator(); iter.hasNext();) {
				Document doc = iter.next();
				proxy.doRemove(doc.getId());

			}
			addActionMessage("{*[delete.successful]*}");
		} catch (Exception e) {
			log.error("doDelete", e);
			addFieldError("", e.getMessage());
		}

		return SUCCESS;
	}

	private String getSimpleDql() throws Exception {
		FormProcess fb = (FormProcess) (ProcessFactory
				.createProcess(FormProcess.class));

		Form form = (Form) fb.doView(_formid);

		return "$formname = '" + form.getFullName() + "'";
	}

	private static DocumentProcess createDocumentProcess(String applicationid) {
		try {
			DocumentProcess process = (DocumentProcess) ProcessFactory
					.createRuntimeProcess(DocumentProcess.class, applicationid);
			return process;
		} catch (CreateProcessException e) {
			Log.error(e);
		}
		return null;
	}

	/**
	 * 执行保存或提交流程等相关操作,必须加入此拦截以确保数据完整
	 */
	public void prepare() throws Exception {
		WebUser user = getUser();
		FormProcess formPross = (FormProcess) ProcessFactory
				.createProcess(FormProcess.class);

		DocumentProcess proxy = (DocumentProcess) getProcess();
		// 先与po合并
		Document mo = proxy.mergePO((Document) getContent(), user);
		if (_formid != null) {
			// 重新计算
			Document content = ((Form) formPross.doView(_formid))
					.recalculateDocument(mo, getParams(), user);
			content.setAuditorList(getParams().getParameterAsString(
					"content.auditorList"));
			setContent(content);
		}
	}

	public IRunTimeProcess<Document> getProcess() {
		return createDocumentProcess(getApplication());
	}

	public String doNewWord() {
		try {
			ParamsTable params = getParams();
			ServletActionContext.getRequest().setAttribute("isFile",
					this.isFile(params));

			ServletActionContext.getRequest().setAttribute("RHList",
					listFiles("REDHEAD_DOCPATH"));
			ServletActionContext.getRequest().setAttribute("SECList",
					listFiles("SECSIGN_PATH"));
			ServletActionContext.getRequest().setAttribute("TList",
					listFiles("TEMPLATE_DOCPATH"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}

	public String doSecEdit() {
		try {
			String dir = DefaultProperty.getProperty("SECSIGN_PATH");
			String realPath = getEnvironment().getRealPath(dir);
			File file = new File(realPath);
			if (!file.exists()) {
				if (!file.mkdir())
					throw new Exception("Folder create failure");
			}
			File[] secFiles = file.listFiles();
			ServletActionContext.getRequest()
					.setAttribute("secFiles", secFiles);
			ServletActionContext.getRequest().setAttribute("secPath",
					DefaultProperty.getProperty("SECSIGN_PATH"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}

	public String listFiles(String path) throws Exception {
		String dir = DefaultProperty.getProperty(path);
		String realPath = getEnvironment().getRealPath(dir);
		File file = new File(realPath);
		if (!file.exists()) {
			if (!file.mkdir())
				throw new Exception("Folder create failure");
		}
		File[] fileList = file.listFiles();
		StringBuffer options = new StringBuffer();
		if (fileList.length > 0) {
			for (int i = 0; i < fileList.length; i++) {
				String fileName = fileList[i].getName();
				String filePath = dir + fileName;
				options.append("<option value=\"" + filePath + "\" title=\""
						+ fileName + "\">" + fileName + "</option>\n");
			}
		}
		return options.toString();
	}

	/**
	 * 开始执行动作,用于word组件
	 * 
	 * @return
	 */
	public String doStart() {
		try {
			ParamsTable params = getParams();
			ServletActionContext.getRequest().setAttribute("isFile",
					this.isFile(params));

			ServletActionContext.getRequest().setAttribute("RHList",
					listFiles("REDHEAD_DOCPATH"));
			ServletActionContext.getRequest().setAttribute("SECList",
					listFiles("SECSIGN_PATH"));
			ServletActionContext.getRequest().setAttribute("TList",
					listFiles("TEMPLATE_DOCPATH"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}

	public String doChangeAuditor() throws Exception {
		try {
			WebUser user = getUser();
			Document doc = (Document) getContent();
			doc = rebuildDocument(doc, getParams());
			DocumentProcess proxy = (DocumentProcess) getProcess();
			doc.setLastmodifier(user.getId());
			doc.setDomainid(getDomain());
			proxy.doChangeAuditor(doc, getParams(), user);
			setContent(doc);
			MemoryCacheUtil.putToPrivateSpace(doc.getId(), doc, getUser());
			this.addActionMessage("{*[更改审批人成功]*}");
		} catch (Exception e) {
			this.addFieldError("1", e.getMessage());
			if ((e instanceof ImpropriateException)) {
				// 加载数据库中最新的文档到上下文环境
				setContent(getProcess().doView(getContent().getId()));
				MemoryCacheUtil.putToPrivateSpace(getContent().getId(),
						getContent(), getUser());
			} else {
				e.printStackTrace();
			}
			return INPUT;
		}

		return SUCCESS;
	}

	/**
	 * 查询文件是否存在(1.表示文件存在,0:表示文件不存在)
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public String isFile(ParamsTable params) throws Exception {
		String _docid[] = params.getParameterAsArray("_docid");
		if (_docid != null) {
			String dir = DefaultProperty.getProperty("WEB_DOCPATH");
			String realPath = getEnvironment().getRealPath(dir);
			File file = new File(realPath + "\\" + _docid[0]);
			boolean exist = file.exists();
			if (exist) {// 1.文件存在,0不存在
				return "1";
			} else {
				return "0";
			}
		}
		return "0";
	}

	/**
	 * 重新构建文档
	 * 
	 * @param doc
	 * @param params
	 * @return
	 */
	protected Document rebuildDocument(Document doc, ParamsTable params) {
		String formid = params.getParameterAsString("_formid");
		try {
			if (!StringUtil.isBlank(params
					.getParameterAsString("_refreshDocument"))
					&& !StringUtil.isBlank(doc.getId())
					&& !StringUtil.isBlank(formid)) {
				doc = (Document) MemoryCacheUtil.getFromPrivateSpace(
						doc.getId(), getUser());
				FormProcess formPross = (FormProcess) ProcessFactory
						.createProcess(FormProcess.class);
				Form form = (Form) formPross.doView(formid);
				doc = form.createDocument(doc, params, getUser());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doc;
	}

}
