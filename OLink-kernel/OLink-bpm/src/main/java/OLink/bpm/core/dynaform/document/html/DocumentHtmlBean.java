package OLink.bpm.core.dynaform.document.html;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import OLink.bpm.core.dynaform.activity.ejb.Activity;
import OLink.bpm.core.dynaform.form.action.FormHelper;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.signature.action.SignatureHelper;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.macro.runner.JavaScriptFactory;
import OLink.bpm.core.macro.runner.JsMessage;
import OLink.bpm.core.dynaform.activity.ejb.ActivityType;
import OLink.bpm.core.workflow.engine.StateMachineHelper;
import OLink.bpm.util.StringUtil;
import org.apache.log4j.Logger;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.dynaform.PermissionType;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.form.ejb.ValidateMessage;
import OLink.bpm.core.privilege.res.ejb.ResVO;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.OBPMDispatcher;

public class DocumentHtmlBean {
	private static final Logger LOG = Logger.getLogger(DocumentHtmlBean.class);

	private HttpServletRequest httpRequest;
	private HttpServletResponse httpResponse;
	private Document doc = null;
	private WebUser webUser = null;
	private IRunner runner = null;
	private ParamsTable params = null;
	private Form form = null;
	private Form templateForm = null;

	private Collection<Activity> activities = null;
	// 当前表单的操作按钮
	private Activity flowAct = null;
	private Activity flexPrintAct = null;
	private Activity flexPrintWFHAct = null;

	// url
	private String mScriptName = "/content.jsp";
	private String mGetDocument = "/portal/dynaform/mysignature/getDocument.action";
	private String mDoCommand = "/portal/dynaform/mysignature/doCommand.action";
	// private String mHttpUrlName = "";
	private String mGetDocumentUrl = "";
	private String mDoCommandUrl = "";
	
	
	
	/**
	 * 产生阅读表单按钮HTML
	 * @return
	 * @throws Exception
	 */
	public String toTemplateFormButtonsHTML() throws Exception {
		return toFormButtonsHTML(getTemplateForm());
	}
	
	/**
	 * 获取表单按钮HTML
	 * @return
	 * @throws Exception
	 */
	public String getActBtnHTML() throws Exception {
		if(this.getTemplateForm() !=null){
			return toTemplateFormButtonsHTML();
		}
		
		return toDefaultFormButtonsHTML();
	}
	
	/**
	 * 产生默认表单按钮HTML
	 */
	public String toDefaultFormButtonsHTML() throws Exception {
		return toFormButtonsHTML(getForm());
	}
	
	/**
	 * 产生表单按钮HTML
	 * @param form
	 * @return
	 */
	public String toFormButtonsHTML(Form form){
		StringBuffer html = new StringBuffer();
		String showAct = httpRequest.getParameter("show_act");
		String skinType = httpRequest.getSession().getAttribute(
				"SKINTYPE") == null ? "default" : (String) httpRequest
				.getSession().getAttribute("SKINTYPE");

		try {
			if ((showAct == null || showAct.equals("true"))
					&& form.getActivitys() != null
					&& !form.getActivitys().isEmpty()) {
				for (Iterator<Activity> aiter = form.getActivitys().iterator(); aiter
						.hasNext();) {
					Activity act = aiter.next();
					act.setApplicationid(form.getApplicationid());
					int permissionType = PermissionType.MODIFY;
					if (act.isReadonly(this.getRunner(), form
							.getFullName())) {
						permissionType = PermissionType.DISABLED;
					}
					if (!act.isHidden(this.getRunner(), form, this
							.getDoc(), webUser, ResVO.FORM_TYPE)) {
						if (act.getType() == ActivityType.WORKFLOW_PROCESS
								|| act.getType() == ActivityType.DOCUMENT_EDIT_AUDITOR) {
							// 是否审批者
							if (!StateMachineHelper.isDocEditUser(
									this.getDoc(), webUser)) {
								continue;
							}

							if (aiter.hasNext() && !skinType.equals("gray"))
								if(skinType != null && !skinType.equals("") && (skinType.equals("fresh")||skinType.equals("blue")||skinType.equals("mild")||skinType.equals("brisk"))){
									html.append(act.toHtml(permissionType));
								}else{
								html
										.append(act.toHtml(permissionType)
												+ "<span style='float:left;display:block;width:2px;height:23px;'><img style='float:left' src='"
												+ httpRequest.getContextPath()
												+ "/portal/"
												+ skinType
												+ "/resource/imgv2/front/main/act_seperate.gif'/></span>");
								}
							else
								html.append(act.toHtml(permissionType));

						} else if (act.getType() == ActivityType.START_WORKFLOW) {
							if (this.getDoc().getFlowid() == null
									|| this.getDoc().getFlowid().equals("")  //update by XGY 2012.11.29
									|| this.getDoc().getStateLabel() == null
									|| this.getDoc().getStateLabel().equals("")) {
								if (aiter.hasNext() && !skinType.equals("gray")){
									if(skinType != null && !skinType.equals("") && (skinType.equals("fresh")||skinType.equals("blue")||skinType.equals("mild")||skinType.equals("brisk"))){
										html.append(act.toHtml(permissionType));
									}else{
									html.append(act.toHtml(permissionType)
													+ "<img style='float:left' src='"
													+ httpRequest
															.getContextPath()
													+ "/portal/"
													+ skinType
													+ "/resource/imgv2/front/main/act_seperate.gif'/>");
									}
								}else
									html.append(act.toHtml(permissionType));
							} else {
								continue;
							}

						} else {
							if (aiter.hasNext() && !skinType.equals("gray"))
								if(skinType != null && !skinType.equals("") && (skinType.equals("fresh")||skinType.equals("blue")||skinType.equals("mild")||skinType.equals("brisk"))){
									html.append(act.toHtml(permissionType));
								}else{
								html
										.append(act.toHtml(permissionType)
												+ "<span style='float:left;display:block;width:2px;height:23px;'><img src='"
												+ httpRequest.getContextPath()
												+ "/portal/"
												+ skinType
												+ "/resource/imgv2/front/main/act_seperate.gif'/></span>");
								}
							else
								html.append(act.toHtml(permissionType));
						}
					}
				}
			}
		} catch (Exception e) {
			LOG.error("getActBtnHTML", e);
		}

		return html.toString();
	}

	// 如果没有错误提示，就执行事件后脚本
	public String doActAfterActionScript() throws Exception {
		StringBuffer html = new StringBuffer();
		Activity activity = (Activity) this.httpRequest
				.getAttribute("ACTIVITY_INSTNACE");
		int errorMegCount = ((LinkedHashMap<?, ?>) this.httpRequest
				.getAttribute("fieldErrors")).size();
		if (errorMegCount == 0 && activity != null
				&& activity.getAfterActionScript() != null
				&& activity.getAfterActionScript().trim().length() > 0) {

			StringBuffer label = new StringBuffer();
			label.append("Activity Action(").append(activity.getId()).append(
					")." + activity.getName()).append("afterActionScript");
			Object result = this.getRunner().run(label.toString(),
					activity.getAfterActionScript());

			if (result != null) {
				if (result instanceof JsMessage) {
					this.httpRequest.setAttribute("message", result);
					RequestDispatcher rd = this.httpRequest
							.getRequestDispatcher(activity.getBackAction());
					rd.forward(this.httpRequest, this.httpResponse);
					return null;
				} else if (result instanceof String
						&& ((String) result).trim().length() > 0) {
					html
							.append("<textarea name='msg' id='msg' style='display:none'>"
									+ result + "</textarea>");
					html
							.append("<script>doAlert(document.getElementById('msg').value);</script>");
					return html.toString();
				}
			}
		}
		return html.toString();
	}

	/*
	 * 获取流程信息
	 */
	public String getFlowMsgHTML() throws Exception {
		StringBuffer html = new StringBuffer();
		flowAct = this.getForm().getActivityByType(
				ActivityType.WORKFLOW_PROCESS);
		if (flowAct != null) {
			String fshowtype = flowAct.getFlowShowType();
			if (fshowtype == null || fshowtype.equals("") //Update by XGY 2012.11.29
					|| fshowtype.equals("ST01")) {

			}
		}
		return html.toString();
	}

	// 获取提交按钮的打开方法
	public String getFlowShowType() {
		try {
			if (this.getFlowAct() != null)
				return this.getFlowAct().getFlowShowType().toString();
			else
				return null;
		} catch (Exception e) {
			return null;
		}
	}

	public String getFlowId() throws Exception {
		try {
			if (this.getDoc() != null && !StringUtil.isBlank(this.getDoc().getStateid())) {
				return this.getDoc().getState().getFlowid();
			} else {
				return this.getForm().getOnActionFlow();
			}
		} catch (Exception e) {
			return null;
		}
	}

	/*
	 * 获取表单HTML
	 */
	public String getFormHTML() throws Exception {
		if(this.getTemplateForm() !=null){
			return this.toTemplateFormHTML();
		}
		return this.toDefaultFormHTML();
		
	}
	
	/**
	 * 生成普通表单HTML
	 * @return
	 * @throws Exception
	 */
	public String toDefaultFormHTML()  throws Exception {
		StringBuffer html = new StringBuffer();
		Collection<ValidateMessage> errors = new ArrayList<ValidateMessage>();
		if (this.getForm() != null) {
			try {
				html.append(this.getForm().toHtml(this.getDoc(),
						this.getParams(), webUser, errors).toString());
			} catch (Exception e) {
				LOG.warn(e);
				throw e;
			}
		}
		return html.toString();
	}
	
	/**
	 * 生成阅读表单HTML
	 * @return
	 * @throws Exception
	 */
	public String toTemplateFormHTML()  throws Exception {
		StringBuffer html = new StringBuffer();
		Collection<ValidateMessage> errors = new ArrayList<ValidateMessage>();
		if (this.getTemplateForm() != null) {
			try {
				html.append(this.getTemplateForm().toHtml(this.getDoc(),
						this.getParams(), webUser, errors).toString());
			} catch (Exception e) {
				LOG.warn(e);
				throw e;
			}
		}
		return html.toString();
	}
	
	
	public String getFormPrintHTML() throws Exception {
		StringBuffer html = new StringBuffer();
		Collection<ValidateMessage> errors = new ArrayList<ValidateMessage>();
		if (this.getForm() != null) {
			try {
				html.append(this.getForm().toPrintHtml(this.getDoc(),
						this.getParams(), webUser, errors).toString());
			} catch (Exception e) {
				LOG.warn(e);
				throw e;
			}
		}
		return html.toString();
	}

	// 检查是否安有电子签章操作
	public boolean isSignatureExist() throws Exception {
		Boolean signatureExist = false;
		String sb = httpRequest.getParameter("signatureExist");
		if ("false".equals(sb) || sb == null) {
			signatureExist = SignatureHelper.signatureExistMethod(this
					.getActivities(), null);
		} else {
			signatureExist = true;
		}
		return signatureExist.booleanValue();
	}

	public boolean isEditCurrDoc() {
		Boolean isEdit = true;
		String currURL = httpRequest.getRequestURL().toString();
		if (currURL.indexOf("new.action") > 0
				|| currURL.indexOf("edit.action") > 0
				|| currURL.indexOf("save.action") > 0) {
			isEdit = true;
		}
		return isEdit;
	}

	// 如果没有错误提示，就执行事件后脚本
	public String checkDocument() throws Exception {
		StringBuffer html = new StringBuffer();
		Activity activity = (Activity) this.httpRequest
				.getAttribute("ACTIVITY_INSTNACE");
		int errorMegCount = ((LinkedHashMap<?, ?>) this.httpRequest
				.getAttribute("fieldErrors")).size();
		if (errorMegCount == 0 && activity != null
				&& activity.getAfterActionScript() != null
				&& activity.getAfterActionScript().trim().length() > 0) {
			StringBuffer label = new StringBuffer();
			label.append("Activity Action(").append(activity.getId()).append(
					")." + activity.getName()).append("afterActionScript");
			Object result = this.getRunner().run(label.toString(),
					activity.getAfterActionScript());
			if (result != null) {
				if (result instanceof JsMessage) {
					// httpRequest.setAttribute("message", result);
					// RequestDispatcher rd =
					// httpRequest.getRequestDispatcher(activity.getBackAction());
					// rd.forward(httpRequest, response);
					// return null;
				} else if (result instanceof String
						&& ((String) result).trim().length() > 0) {
					html
							.append("<textarea name='msg' id='msg' style='display:none'>"
									+ result + "</textarea>");
					html
							.append("<script>doAlert(document.getElementById('msg').value);</script>");
				}
			}
		}
		return html.toString();
	}

	public HttpServletRequest getHttpRequest() {
		return httpRequest;
	}

	public void setHttpRequest(HttpServletRequest httpRequest) {
		this.httpRequest = httpRequest;
	}

	public Document getDoc() {
		if (doc == null){
			this.doc = (Document) httpRequest.getAttribute("content");
		}
		return doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}

	public WebUser getWebUser() {
		return webUser;
	}

	public void setWebUser(WebUser webUser) {
		this.webUser = webUser;
	}

	public IRunner getRunner() throws Exception {
		if (this.runner == null) {
			IRunner r = JavaScriptFactory.getInstance(this.getParams()
					.getSessionid(), getForm().getApplicationid());
			r.initBSFManager(this.getDoc(), this.getParams(),
					this.getWebUser(), new ArrayList<ValidateMessage>());
			this.runner = r;
		}
		return this.runner;
	}

	public void setRunner(IRunner runner) {
		this.runner = runner;
	}

	public ParamsTable getParams() {
		if (this.params == null) {
			this.params = ParamsTable.convertHTTP(httpRequest);
		}
		return this.params;
	}

	public void setParams(ParamsTable params) {
		this.params = params;
	}

	public Form getForm() throws Exception {
		if (this.form == null) {
			String formid = null;
			if (httpRequest.getParameter("_formid") != null) {
				formid = httpRequest.getParameter("_formid");
			} else {
				formid = this.getDoc().getFormid();
			}
			this.form = FormHelper.get_FormById(formid);
		}
		return form;
	}

	public void setForm(Form form) {
		this.form = form;
	}

	public Collection<Activity> getActivities() throws Exception {
		if (this.activities == null && this.getForm() !=null)
			this.activities = this.getForm().getActivitys();
		return this.activities;
	}

	public void setActivities(Collection<Activity> activities) {
		this.activities = activities;
	}

	public int getActivitiesSize() throws Exception {
		if (this.activities == null && this.getForm() !=null)
			this.activities = this.getForm().getActivitys();
		return this.activities.size();
	}

	public Activity getFlowAct() throws Exception {
		return this.getForm().getActivityByType(ActivityType.WORKFLOW_PROCESS);
	}

	public void setFlowAct(Activity flowAct) {
		this.flowAct = flowAct;
	}

	public Activity getFlexPrintAct() throws Exception {
		if (this.flexPrintAct == null && this.getForm() != null) {
			this.flexPrintAct = this.getForm().getActivityByType(
					ActivityType.FLEX_PRINT);
		}
		return this.flexPrintAct;
	}

	public void setFlexPrintAct(Activity flexPrintAct) {
		this.flexPrintAct = flexPrintAct;
	}

	public Activity getFlexPrintWFHAct() throws Exception {
		if (this.flexPrintWFHAct == null && this.getForm() != null) {
			this.flexPrintWFHAct = this.getForm().getActivityByType(
					ActivityType.FLEX_PRINT);
		}
		return this.flexPrintWFHAct;
	}

	// 运行表单的是否可打开脚本
	public void isOpenAble() throws Exception {
		boolean isopenable = true;
		Document doc = this.getDoc();
		if (doc!=null && !doc.getIstmp()) {
			if (this.getForm().getIsopenablescript() != null
					&& this.getForm().getIsopenablescript().trim().length() > 0) {
				StringBuffer label = new StringBuffer();
				label.append("Document Print.Form(").append(
						this.getForm().getId()).append(
						")." + this.getForm().getName()).append(
						".runIsOpenAbleScript");

				Object result = this.getRunner().run(label.toString(),
						this.getForm().getIsopenablescript());
				if (result != null && result instanceof Boolean) {
					isopenable = (((Boolean) result).booleanValue());
				}
			}
			if (!isopenable) {
				String url = this.getParams().getParameterAsString("_backURL");
				new OBPMDispatcher().sendRedirect(url, this.getHttpRequest(),
						this.getHttpResponse());
			}
		}

	}

	public void setFlexPrintWFHAct(Activity flexPrintWFHAct) {
		this.flexPrintWFHAct = flexPrintWFHAct;
	}

	public String getMHttpUrlName() {

		return httpRequest.getRequestURI();
	}

	// public void setMHttpUrlName(String httpUrlName) {
	// mHttpUrlName = httpUrlName;
	// }

	public String getMGetDocumentUrl() {
		mGetDocumentUrl = "http://"
				+ httpRequest.getServerName()
				+ ":"
				+ httpRequest.getServerPort()
				+ this.getMHttpUrlName().substring(0,
						this.getMHttpUrlName().indexOf("/", 2)) + mGetDocument;
		return mGetDocumentUrl;
	}

	public void setMGetDocumentUrl(String getDocumentUrl) {
		mGetDocumentUrl = getDocumentUrl;
	}

	public String getMDoCommandUrl() {
		mDoCommandUrl = "http://"
				+ httpRequest.getServerName()
				+ ":"
				+ httpRequest.getServerPort()
				+ this.getMHttpUrlName().substring(0,
						this.getMHttpUrlName().indexOf("/", 2)) + mDoCommand;
		return mDoCommandUrl;
	}

	public void setMDoCommandUrl(String doCommandUrl) {
		mDoCommandUrl = doCommandUrl;
	}

	public String getMScriptName() {
		return mScriptName;
	}

	public void setMScriptName(String scriptName) {
		mScriptName = scriptName;
	}

	public String getMGetDocument() {
		return mGetDocument;
	}

	public void setMGetDocument(String getDocument) {
		mGetDocument = getDocument;
	}

	public String getMDoCommand() {
		return mDoCommand;
	}

	public void setMDoCommand(String doCommand) {
		mDoCommand = doCommand;
	}

	public HttpServletResponse getHttpResponse() {
		return httpResponse;
	}

	public void setHttpResponse(HttpServletResponse httpResponse) {
		this.httpResponse = httpResponse;
	}

	/**
	 * 获取阅读表单
	 * @return
	 * @throws Exception 
	 */
	public Form getTemplateForm() throws Exception {
		if(templateForm ==null && !StringUtil.isBlank(this.getHttpRequest().getParameter("_templateForm"))){
			templateForm = FormHelper.get_FormById(this.getHttpRequest().getParameter("_templateForm"));
		}
			
		return templateForm;
	}

	/**
	 * 设置阅读表单
	 * @param templateForm
	 */
	public void setTemplateForm(Form templateForm) {
		this.templateForm = templateForm;
	}
	
	

}
