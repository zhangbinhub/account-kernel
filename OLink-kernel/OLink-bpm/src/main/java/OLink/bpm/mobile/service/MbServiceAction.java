package OLink.bpm.mobile.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import OLink.bpm.base.action.BaseAction;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.constans.Web;
import OLink.bpm.core.dynaform.activity.ejb.Activity;
import OLink.bpm.core.dynaform.activity.ejb.ActivityProcess;
import OLink.bpm.core.dynaform.activity.ejb.ActivityType;
import OLink.bpm.core.dynaform.document.action.DocumentHelper;
import OLink.bpm.core.dynaform.form.action.FormHelper;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.dynaform.form.ejb.ValidateMessage;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.dynaform.view.ejb.ViewProcess;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.macro.runner.JavaScriptFactory;
import OLink.bpm.core.macro.runner.JsMessage;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.workflow.FlowState;
import OLink.bpm.core.workflow.FlowType;
import OLink.bpm.core.workflow.engine.StateMachine;
import OLink.bpm.core.workflow.engine.StateMachineHelper;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiVO;
import OLink.bpm.util.CreateProcessException;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import OLink.bpm.util.cache.MemoryCacheUtil;
import OLink.bpm.core.workflow.element.TerminateNode;
import OLink.bpm.core.workflow.storage.runtime.ejb.ActorRT;
import OLink.bpm.core.workflow.storage.runtime.ejb.CirculatorProcess;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.document.ejb.DocumentBuilder;
import OLink.bpm.core.workflow.element.CompleteNode;
import OLink.bpm.core.workflow.element.Node;
import org.apache.log4j.Logger;
import org.jfree.util.Log;

import OLink.bpm.core.dynaform.document.ejb.DocumentProcess;
import OLink.bpm.core.workflow.element.AbortNode;
import OLink.bpm.core.workflow.element.FlowDiagram;
import OLink.bpm.core.workflow.element.ManualNode;
import OLink.bpm.core.workflow.element.SuspendNode;
import OLink.bpm.core.workflow.storage.runtime.ejb.ActorRTProcess;
import OLink.bpm.core.workflow.storage.runtime.ejb.ActorRTProcessBean;
import OLink.bpm.core.workflow.storage.runtime.ejb.Circulator;
import OLink.bpm.core.workflow.storage.runtime.ejb.FlowStateRTProcess;
import OLink.bpm.core.workflow.storage.runtime.ejb.NodeRT;

import com.opensymphony.webwork.ServletActionContext;

/**
 * @author nicholas
 */
public class MbServiceAction extends BaseAction<Activity> {

	private static final long serialVersionUID = -5649138986770418640L;
	private static final Logger LOG = Logger.getLogger(MbServiceAction.class);

	private String _activityid;

	/**
	 * 默认构造方法
	 * 
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public MbServiceAction() throws ClassNotFoundException {
		super(ProcessFactory.createProcess(ActivityProcess.class), new Activity());
	}

	/**
	 * <p>
	 * 触发对Document、流程与view的等有关操作. 根据Activity(按钮)类型实现对Document,流程与VIEW等不同的操作.
	 * </p>
	 * 
	 * <ul>
	 * Activity(按钮)类型常量分别为:
	 * <li>1:ACTIVITY_TYPE_DOCUMENT_QUERY"(查询Document);</li>
	 * <li>2:ACTIVITY_TYPE_DOCUMENT_CREATE"(创建Document);</li>
	 * <li>3:ACTIVITY_TYPE_DOCUMENT_DELETE"(删除Document);</li>
	 * <li>4:ACTIVITY_TYPE_DOCUMENT_UPDATE"(更新Document);</li>
	 * <li>5:ACTIVITY_TYPE_WORKFLOW_PROCESS"(流程处理);</li>
	 * <li>6:ACTIVITY_TYPE_SCRIPT_PROCESS"(SCRIPT);</li>
	 * <li>7:ACTIVITY_TYPE_DOCUMENT_MODIFY"(回退);</li>
	 * <li>8:ACTIVITY_TYPE_CLOSE_WINDOW"(关闭窗口);</li>
	 * <li>9:ACTIVITY_TYPE_SAVE_CLOSE_WINDOW"(保存Document并关闭窗口);</li>
	 * <li>10:ACTIVITY_TYPE_DOCUMENT_BACK"(回退);</li>
	 * <li>11:ACTIVITY_TYPE_SAVE_BACK"(保存Document并回退);</li>
	 * <li>12:ACTIVITY_TYPE_SAVE_NEW_WITH_OLD"(保存并新建保留有旧数据的Document);</li>
	 * <li>13:ACTIVITY_TYPE_Nothing";</li>
	 * <li>14:ACTIVITY_TYPE_PRINT"(普通打印);</li>
	 * <li>15:ACTIVITY_TYPE_PRINT_WITHFLOWHIS"(打印包含有流程);</li>
	 * <li>16:ACTIVITY_TYPE_EXPTOEXCEL"(将数据导出到EXCEL);</li>
	 * <li>17:ACTIVITY_TYPE_SAVE_NEW_WITHOUT_OLD"((保存并新建一条空的Document));</li>
	 * </ul>
	 * 
	 * @return result.
	 * @throws Exception
	 */
	public String doAction() {
		try {
			ParamsTable params = getParams();
			String result = null;
			Activity act = null;
			if (!StringUtil.isBlank(_activityid) && !_activityid.equals("null")) {
				String formid = params.getParameterAsString("_formid");
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
					result = "viewList";
					break;
				case ActivityType.DOCUMENT_BACK:
					result = "viewList";
					break;
				case ActivityType.DOCUMENT_CREATE:
					result = doNewDocument(act);
					break;
				case ActivityType.DOCUMENT_DELETE:
					result = doDelete(act);
					break;
				case ActivityType.DOCUMENT_MODIFY:
					result = doViewDocument();
					break;
				case ActivityType.DOCUMENT_QUERY:

					break;
				case ActivityType.DOCUMENT_UPDATE:
				case ActivityType.SAVE_SARTWORKFLOW:
					result = doSaveDocument(act);
					break;
				case ActivityType.EXPTOEXCEL:

					break;
				case ActivityType.NOTHING:

					break;
				case ActivityType.PRINT:

					break;
				case ActivityType.PRINT_WITHFLOWHIS:

					break;
				case ActivityType.SAVE_BACK:
					result = doSaveBack(act);
					break;
				case ActivityType.SAVE_CLOSE_WINDOW:
					result = doSaveClose(act);
					break;
				case ActivityType.SAVE_NEW_WITH_OLD:
					result = doSaveNewWithOld(act);
					break;
				case ActivityType.SAVE_NEW_WITHOUT_OLD:
					result = doSaveNewWithOutOld(act);
					break;
				case ActivityType.SAVE_WITHOUT_VALIDATE:
					result = doSaveWithOutValidate(act);
					break;
				case ActivityType.SCRIPT_PROCESS:
					break;
				case ActivityType.WORKFLOW_PROCESS:
					result = doSubmitDocument();
					break;
				case ActivityType.WORKFLOW_RETRACEMENT:
					result = doWorkFlowRetracemend();
					break;
				default:
					break;
				}
				if (result == null) {
					throw new Exception("Unsupport activity type!");
				} else {
					if (getContent() instanceof Document) {
						String str = doAfter(act, (Document) getContent());
						if (str.trim().equalsIgnoreCase("error")) {
							return ERROR;
						}
					}
				}
			} else {
				if(params.getParameter("activityType")!=null){
					//android菜单跳转到表单
					int activityType = params.getParameterAsInteger("activityType");
					if(activityType == ActivityType.DOCUMENT_CREATE){
						Activity activity = new Activity();
						String formid = params.getParameterAsString("_formid");
						activity.setOnActionForm(formid);
						result = doNewDocument(activity);
					}else{
						result = doViewDocument();
					}
				}else{
					result = doViewDocument();
				}
			}
			return result;
		} catch (Exception e) {
			this.addFieldError("SystemError", e.getMessage());
			LOG.warn(e);
			return ERROR;
		}
	}
	
	/**
	 * 回撤
	 * @return
	 * @throws Exception
	 */
	private String doWorkFlowRetracemend() throws Exception{
		WebUser user = this.getUser();

		if (user.getStatus() == 1) {
			try {
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
				BillDefiVO flowVO = doc.getState().getFlowVO();
				FlowDiagram fd = flowVO.toFlowDiagram();
				NodeRT nodert = doc.getState().getNoderts().iterator().next();
				Node currNode = (Node) fd.getElementByID(nodert.getNodeid());
				Node nextNode = StateMachine.getBackNodeByHis(doc, flowVO, currNode.id, user, FlowState.RUNNING);
				if (nextNode != null) {

					IRunner runner = JavaScriptFactory.getInstance(params.getSessionid(), application);
					runner.initBSFManager(doc, params, user, new ArrayList<ValidateMessage>());

					boolean allowRetracement = false;
					if (((ManualNode) nextNode).retracementEditMode == 0 && ((ManualNode) nextNode).cRetracement) {
						allowRetracement = true;
					} else if (((ManualNode) nextNode).retracementEditMode == 1
							&& ((ManualNode) nextNode).retracementScript != null
							&& (((ManualNode) nextNode).retracementScript).trim().length() > 0) {
						StringBuffer label = new StringBuffer();
						label.append(doc.getFormname()).append(".Activity(").append(params.getParameter("_activityId"))
								.append("流程回撤").append(".retracementScript");
						Object result = runner.run(label.toString(), ((ManualNode) nextNode).retracementScript);
						if (result != null && result instanceof Boolean) {
							if (((Boolean) result).booleanValue())
								allowRetracement = true;
						}
					}

					if (allowRetracement) {
						// 指的审批人
						String submitTo = "[{\"nodeid\":'" + nextNode.id + "',\"isToPerson\":'true',\"userids\":\"["
								+ user.getId() + "]\"},]";
						params.setParameter("submitTo", submitTo);
						params.setParameter("doRetracement", "true");

						String[] nextids = { nextNode.id };
						proxy.doFlow(doc, params, currNode.id, nextids,
								FlowType.RUNNING2RUNNING_RETRACEMENT, get_attitude(), user);
						// doc.setReadusers("");
//						setContent(doc);
						proxy.doUpdate(doc, true);
						MemoryCacheUtil.putToPrivateSpace(doc.getId(), doc, getUser());
						set_attitude("");// 将remarks清空
					} else {
						this.addFieldError("System Error", "此流程状态下不允许回撤");
						return ERROR;
					}
				} else {
					this.addFieldError("System Error", "您没有回撤的权限");
					return ERROR;
				}
			} catch (Exception e) {
				this.addFieldError("System Error", e.getMessage());
				e.printStackTrace();
				LOG.warn(e);
				return ERROR;
			}
			return SUCCESS;
		} else {
			this.addFieldError("System Error", "{*[core.user.noeffectived]*}");
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
		if (!doValidate()) return ERROR;
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
			LOG.warn(e);
			return ERROR;
		}
		ViewProcess viewProcess = (ViewProcess)ProcessFactory.createProcess(ViewProcess.class);
		if(params.getParameterAsString("_viewid")!=null && viewProcess.doView(params.getParameterAsString("_viewid"))!=null){
			return "viewList";
		}else{
			return SUCCESS;
		}
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
					".runIsEditAbleScript");
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

	private String doAfter(Activity act, Document doc) throws Exception {
		ParamsTable params = getParams();
		WebUser webUser = getUser();
		if (doc == null) {
			doc = new Document();
		}
		IRunner runner = JavaScriptFactory.getInstance(params.getSessionid(), act.getApplicationid());
		runner.initBSFManager(doc, params, webUser, new ArrayList<ValidateMessage>());
		if ((act.getBeforeActionScript()) != null && (act.getBeforeActionScript()).trim().length() > 0) {

			StringBuffer label = new StringBuffer();
			label.append("Activity Action").append(act.getId()).append("." + act.getName()).append("afterActionScript");
			Object result = runner.run(label.toString(), act.getAfterActionScript());

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
		if (!toFlowHisXml()) return ERROR;
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
			toFormXml(doc, new ArrayList<String>(), false);
		} catch (Exception e) {
			LOG.warn(e);
		}

		return "viewDocument";
	}

	public String doRefresh() throws Exception {
		// synchronized (user) {
		try {
			WebUser user = this.getUser();

			FormProcess formProcess = (FormProcess) ProcessFactory.createProcess(FormProcess.class);

			ParamsTable params = getParams();
			String formid = params.getParameterAsString("_formid");

			Form form = (Form) formProcess.doView(formid);

			String docid = params.getParameterAsString("_docid");
			DocumentProcess proxy = createDocumentProcess(getApplication());
			Document olddoc = (Document) MemoryCacheUtil.getFromPrivateSpace(docid, user);
			if (olddoc == null) {
				olddoc = (Document) proxy.doView(docid);
			}

			Document doc = (Document) proxy.doView(docid);
			doc = form.createDocument(doc, params, user);

			if (docid != null && docid.length() > 0) {
				doc.setId(docid);
			}
			setContent(doc);
			MemoryCacheUtil.putToPrivateSpace(doc.getId(), doc, getUser());
			toFormXml(doc, doc.compareTo(olddoc), true);
		} catch (Exception e) {
			this.addFieldError("SystemError", e.getMessage());
			LOG.warn(e);
			return ERROR;
		}
		return "refreshDocument";
	}

	public String doAfter() throws Exception {
		return doAction();
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
			LOG.warn(e);
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
					toFormXml(newDoc, new ArrayList<String>(), false);
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
				toFormXml(newDoc, new ArrayList<String>(), false);
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
			toFormXml(doc, new ArrayList<String>(), false);
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
			Document doc = null;
			String _docid = params.getParameterAsString("_docid");
			if(_docid!=null && !_docid.equals("")){
				doc = (Document)proxy.doView(_docid);
			}
			doc = form.createDocument(doc,params, user);
			doc.setLastmodifier(user.getId());
			doc.setDomainid(getDomain());
			proxy.doCreateOrUpdate(doc, user);

			setContent(doc);
			MemoryCacheUtil.putToPrivateSpace(doc.getId(), doc, getUser());
			toFormXml(doc, new ArrayList<String>(), false);
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
			toFormXml(doc, new ArrayList<String>(), false);
		} catch (Exception e) {
			this.addFieldError("SystemError", e.getMessage());
			e.printStackTrace();
			return ERROR;
		}
		// }
		return "viewDocument";
	}
	
	public String doNewDocument() {
		try {
			String _formid = getParams().getParameterAsString("_formid");
			if (StringUtil.isBlank(_formid)) {
				this.addFieldError("SystemError", "Can't find Form");
				return ERROR;
			}
			FormProcess formPross = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
			DocumentProcess proxy = getDocumentProcess();
			WebUser user = getUser();
			Form form = (Form) formPross.doView(_formid);
			if (form != null) {
				Document newDoc = proxy.doNew(form, user, getParams());
				setContent(newDoc);
				// 放入Session中
				ServletActionContext.getRequest().setAttribute("content.id", newDoc.getId());
				MemoryCacheUtil.putToPrivateSpace(newDoc.getId(), newDoc, user);
				
				toFormXml(newDoc, new ArrayList<String>(), false);
				return SUCCESS;
			} else {
				this.addFieldError("SystemError", "Can't find Form");
				return ERROR;
			}
		} catch (Exception e) {
			this.addFieldError("SystemError", "Can't open Form");
			e.printStackTrace();
			return ERROR;
		}
	}
	
	private DocumentProcess getDocumentProcess() {
		try {
			return createDocumentProcess(getApplication());
		} catch (CreateProcessException e) {
			Log.error(e);
		}
		return null;
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
				HttpSession session = request.getSession();
				session.setAttribute("toXml", xmlText);
				return true;
			}
		} catch (Exception e) {
			LOG.warn(e);
			addFieldError("SystemError", e.getMessage());
		}
		return false;
	}

	private void toFormXml(Document doc, Collection<String> columnNames, boolean isRefresh) throws Exception {
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
		
		if (form != null) {
			form.recalculateDocument(doc, getParams(), webUser);
		}
		
		// -------------------------文档已阅未阅功能
		if (doc.getState() != null) {
			Collection<ActorRT> actors = doc.getState().getActors();
			for (Iterator<ActorRT> iter = actors.iterator(); iter.hasNext();) {
				ActorRT actor = iter.next();
				if (webUser.getId().equals(actor.getActorid())) {
					if (!actor.getIsread()) {
						actor.setIsread(true);
						ActorRTProcess process = new ActorRTProcessBean(getApplication());
						process.doUpdate(actor);
					}
					break;
				}
			}
			//查找抄送人为当前用户
			CirculatorProcess cProcess = (CirculatorProcess) ProcessFactory.createRuntimeProcess(CirculatorProcess.class, getApplication());
			Circulator circulator = cProcess.findByCurrDoc(doc.getId(), doc.getState().getId(), false, webUser);
			if(circulator !=null){
				circulator.setRead(true);
				circulator.setReadTime(new Date());
				cProcess.doUpdate(circulator);//更新为已阅
			}
			
			// -------------------------------- 选择可执行的流程实例
			if(!StringUtil.isBlank(doc.getId())){
				FlowStateRTProcess stateProcess = (FlowStateRTProcess) ProcessFactory.createRuntimeProcess(FlowStateRTProcess.class, getApplication());
				if(stateProcess.isMultiFlowState(doc)){//有多个没完成是流程实例
					doc.setState(stateProcess.getCurrFlowStateRT(doc, webUser, null));//绑定一个可执行的文档实例
					doc.setMulitFlowState(stateProcess.isMultiFlowState(doc, webUser));//是否存在多个可执行实例
				}
			}
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
					".runBeforeopenScript");

			Object result = runner.run(label.toString(), form.getIseditablescript());
			if (result != null && result instanceof Boolean) {
				boolean isEdit_bs = (((Boolean) result).booleanValue());
				if (isEdit_bs != isEdit) {
					isEdit = false;
				}
			}
		}
		doc.setEditAble(isEdit);

		if (form != null) {
			String xmlText = form.toMbXML(doc, params, columnNames, webUser, new ArrayList<ValidateMessage>(),
					getEnvironment(), isRefresh);
			HttpServletRequest request = ServletActionContext.getRequest();
			HttpSession session = request.getSession();
			session.setAttribute("toXml", xmlText);
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
		Node nextNode = StateMachine.getCurrNode(flowVO, currNodeid);

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
	
	public String get_activityid() {
		return _activityid;
	}

	public void set_activityid(String _activityid) {
		this._activityid = _activityid;
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
			if(_selects.length==1 && _selects[0].indexOf(";")!=-1){
				_selects =  _selects[0].split(";");
			}
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

	public String getDomain() throws Exception {
		if (!StringUtil.isBlank(domain)) return domain;
		return getUser().getDomainid();
	}

	public void setDomain(String domain) {
		this.domain = domain;
		getContent().setDomainid(domain);
	}

	public String getApplication() {
		return application;
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
	 * @SuppressWarnings API不支持泛型
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
}
