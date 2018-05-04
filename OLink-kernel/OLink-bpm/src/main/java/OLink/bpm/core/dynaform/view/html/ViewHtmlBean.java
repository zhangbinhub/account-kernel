package OLink.bpm.core.dynaform.view.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.dynaform.PermissionType;
import OLink.bpm.core.dynaform.activity.ejb.Activity;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.form.ejb.ValidateMessage;
import OLink.bpm.core.dynaform.signature.action.SignatureHelper;
import OLink.bpm.core.dynaform.view.ejb.Column;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.dynaform.view.ejb.ViewProcess;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.macro.runner.JavaScriptFactory;
import OLink.bpm.core.privilege.res.ejb.ResVO;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import OLink.bpm.core.dynaform.form.ejb.Form;
import org.apache.log4j.Logger;

import com.opensymphony.webwork.ServletActionContext;

public class ViewHtmlBean {
	private static final Logger LOG = Logger.getLogger(ViewHtmlBean.class);

	// 地图视图只读
	protected boolean mapReadonly = false;

	protected View view;

	protected WebUser webUser;

	protected ParamsTable params;

	protected IRunner runner;

	protected HttpServletRequest request;

	protected Map<String, Object> columnHiddenMap = new HashMap<String, Object>();

	public WebUser getWebUser() {
		return webUser;
	}

	public void setWebUser(WebUser webUser) {
		this.webUser = webUser;
	}

	public HttpServletRequest getHttpRequest() {
		return request;
	}

	public void setHttpRequest(HttpServletRequest httpRequest) {
		this.request = httpRequest;
		params = ParamsTable.convertHTTP(httpRequest);
		Object content = request.getAttribute("content");
		if (content instanceof View) {
			this.view = (View) content;
		} else {
			try {
				String viewid = params.getParameterAsString("_viewid");
				ViewProcess process = (ViewProcess) ProcessFactory
						.createProcess(ViewProcess.class);
				this.view = (View) process.doView(viewid);
			} catch (Exception e) {
				LOG.warn(e.toString());
			}
		}
		if (view == null) {
			view = new View();
		}
	}

	/**
	 * 输出操作HTML
	 * 
	 * @return
	 */
	public String toActHtml() {
		return toActHtml(PermissionType.MODIFY);
	}

	/**
	 * 输出操作HTML
	 * 
	 * @return
	 */
	public String toActHtml(int pType) {
		boolean isEdit = true;
		StringBuffer htmlBuffer = new StringBuffer();
		try {
			Document parent = (Document) request.getAttribute("parent");
			Document tdoc = parent != null ? parent : new Document();

			if (parent != null) {
				isEdit = !StringUtil.isBlank(request.getParameter("isedit")) ? Boolean
						.parseBoolean(request.getParameter("isedit"))
						: true;
			}

			String contextPath = request.getContextPath();

			IRunner runner = getRunner();
			Iterator<Activity> aiter = view.getActivitys().iterator();
			HttpSession session = ServletActionContext.getRequest()
					.getSession();
			String skinType = (String) session.getAttribute("SKINTYPE");

			// 标志含有批量审批按钮
			boolean isBATCH_APPROVEAct = false;
			Activity batch_approve_act = null;
			while (aiter.hasNext()) {
				Activity act = aiter.next();
				act.setApplicationid(view.getApplicationid());

				// 如果有批量提交按钮添加审批备注属性
				if (act.getType() == 20) {
					isBATCH_APPROVEAct = true;
					batch_approve_act = act;
				}
				int permissionType = pType;
				if (act.isReadonly(this.getRunner(), view.getName())) {
					permissionType = PermissionType.DISABLED;
				}

				if (!act.isHidden(runner, view, tdoc, webUser, ResVO.VIEW_TYPE)
						&& isEdit) {
					if (skinType != null && skinType.equals("gray")) {
						htmlBuffer.append(act.toHtml(permissionType));
					} else {
						if (aiter.hasNext()){
							if (skinType != null && !skinType.equals("") && (skinType.equals("fresh")||skinType.equals("blue")||skinType.equals("mild")||skinType.equals("brisk"))){
								htmlBuffer.append(act.toHtml(permissionType));
							}else {
								htmlBuffer.append(act.toHtml(permissionType)
										+ "<span style='vertical-align:middle'><img style='float:left; vertical-align:middle;' src='"
										+ contextPath
										+ "/resource/imgv2/front/main/act_seperate.gif'></span>");
							}
						}else {
							htmlBuffer.append(act.toHtml(permissionType));
						}
					}
				} else {
					mapReadonly = true;
				}
			}
			//gird视图添加保存和取消按钮
			if(view.getOpenType()==View.OPEN_TYPE_GRID){
				htmlBuffer.append("<div id='VIEW_OPEN_TYPE_GRID_BTN' style='display:none;'>");
				if (skinType != null && !skinType.equals("") && (skinType.equals("fresh")||skinType.equals("blue")||skinType.equals("mild")||skinType.equals("brisk"))){
				}else{
					htmlBuffer.append("<span style='vertical-align:middle'><img style='float:left; vertical-align:middle;' src='"
							+ contextPath
							+ "/resource/imgv2/front/main/act_seperate.gif'></span>");
				}
				htmlBuffer.append("<span class='button-document' id='doSave_btn'><a href=\"###\" name='button_act' title='{*[Save]*}' onclick=\"doSave()\" onmouseover='this.className=\"button-onchange\"' onmouseout='this.className=\"button-document\"' ><span><img style='border:0px solid blue;vertical-align:middle;' src='"+ contextPath + "/resource/imgv2/front/act/act_4.gif' />{*[Save]*}</span></a></span>");

				if (skinType != null && !skinType.equals("") && (skinType.equals("fresh")||skinType.equals("blue")||skinType.equals("mild")||skinType.equals("brisk"))){
				}else{
					htmlBuffer.append("<span style='vertical-align:middle'><img style='float:left; vertical-align:middle;' src='"
							+ contextPath
							+ "/resource/imgv2/front/main/act_seperate.gif'></span>");
				}
				htmlBuffer.append("<span class='button-document' id='doCancelAll_btn'><a href=\"###\" name='button_act' title='{*[Cancel]*}' onclick=\"doCancelAll()\" onmouseover='this.className=\"button-onchange\"' onmouseout='this.className=\"button-document\"' ><span><img style='border:0px solid blue;vertical-align:middle;' src='"+ contextPath + "/resource/imgv2/front/act/act_4.gif' />{*[Cancel]*}{*[All]*}</span></a></span>");
				htmlBuffer.append("</div>");
			}

			if (isBATCH_APPROVEAct) {
				htmlBuffer
						.append("<script>jQuery(function(){jQuery('#inputAuditRemarkDiv').dialog({autoOpen: false,width: 800,buttons: {'{*[Ok]*}': function() {jQuery('#_attitude').val(jQuery('#temp_attitude').val());if(jQuery('#_attitude').val()!=''){jQuery(this).dialog('close');ev_submit('"
								+ batch_approve_act.getId()
								+ "',true);}else{alert('{*[Please]*}{*[Input]*}{*[Audit]*}{*[Remark]*}');}},'{*[Cancel]*}': function() {jQuery(this).dialog('close');}}});});</script>");
				htmlBuffer
						.append("<textarea id='_attitude' type='text' style='display:none;' name='_attitude'></textarea>");
				htmlBuffer
						.append("<div id='inputAuditRemarkDiv' style='display:none;width:280;' title='{*[Input]*}{*[Audit]*}{*[Remark]*}'><textarea id='temp_attitude' rows='12' cols='35' name='temp_attitude' style='width:100%;'></textarea></div>");
			}
		} catch (Exception e) {
			LOG.warn("toActHtml", e);
		}

		return htmlBuffer.toString();
	}

	/**
	 * 是否显示查询表单
	 * 
	 * @return 是否显示查询表单
	 */
	public boolean isShowSearchForm() {
		return view.getSearchForm() != null;
	}

	/**
	 * 是否显示查询表单常规按钮(查询、重置)
	 * 
	 * @return 是否显示查询表单
	 */
	public boolean isShowSearchFormButton() {
		try {
			Form searchForm = view.getSearchForm();
			if (searchForm != null && searchForm.getFields().size() > 0) {
				return searchForm.checkDisplayType();
			}
		} catch (Exception e) {
			LOG.warn("isShowSearchFormButton", e);
		}

		return false;
	}

	/**
	 * 输出查询表单HTML
	 * 
	 * @return
	 */
	public String toSearchFormHtml() {
		try {
			Form searchForm = view.getSearchForm();
			Document searchDoc = ((Document) request
					.getAttribute("currentDocument"));
			searchDoc = searchDoc == null ? new Document() : searchDoc;
			String ehtml = searchForm.toHtml(searchDoc, params, webUser,
					new ArrayList<ValidateMessage>());

			return ehtml;
		} catch (Exception e) {
			LOG.warn("toSearchFormHtml", e);
		}

		return "";
	}

	/**
	 * 是否隐藏列
	 * 
	 * @param column
	 *            当前列
	 * @return
	 */
	public boolean isHiddenColumn(Column column) {
		try {
			if (columnHiddenMap.containsKey(column.getId())) {
				return ((Boolean) columnHiddenMap.get(column.getId()))
						.booleanValue();
			}
			if (column.getHiddenScript() != null
					&& column.getHiddenScript().trim().length() > 0) {
				StringBuffer label = new StringBuffer();
				label.append("View").append("." + view.getName()).append(
						".Activity(").append(column.getId()).append(
						")." + column.getName()).append(".runHiddenScript");

				IRunner runner = getRunner();
				Object result = runner.run(label.toString(), column
						.getHiddenScript());// 运行脚本
				if (result != null && result instanceof Boolean) {
					columnHiddenMap.put(column.getId(), result);
					return ((Boolean) result).booleanValue();
				} else {
					columnHiddenMap.put(column.getId(), Boolean.valueOf(false));
				}
			} else {
				columnHiddenMap.put(column.getId(), false);
			}
		} catch (Exception e) {
			LOG.warn("isHiddenColumn", e);
		}

		return false;
	}

	/**
	 * 是否存在电子签章
	 * 
	 * @return
	 */
	public boolean isSignatureExist() {
		Boolean signatureExist = SignatureHelper.signatureExistMethod(null,
				view.getActivitys());
		return signatureExist != null ? signatureExist.booleanValue() : false;
	}

	/**
	 * 获取电子签章信息
	 * 
	 * @param dataPackage
	 *            数据包
	 * @return
	 */
	public Map<String, String> getSignatureInfo(
			DataPackage<Document> dataPackage) {
		if (dataPackage.rowCount > 0) {
			Document doc = dataPackage.datas.iterator().next();
			return getSignatureInfo(doc);
		}

		return new HashMap<String, String>();
	}

	/**
	 * 获取电子签章信息
	 * 
	 * @param doc
	 *            文档
	 * @return
	 */
	public Map<String, String> getSignatureInfo(Document doc) {
		Map<String, String> rtn = new HashMap<String, String>();

		String GetBatchDocument = "/portal/dynaform/mysignature/getBatchDocument.action";
		String mDoCommand = "/portal/dynaform/mysignature/doCommand.action";
		String mHttpUrlName = request.getRequestURI();
		String mGetBatchDocumentUrl = "http://" + request.getServerName() + ":"
				+ request.getServerPort()
				+ mHttpUrlName.substring(0, mHttpUrlName.indexOf("/", 2))
				+ GetBatchDocument;
		String mDoCommandUrl = "http://" + request.getServerName() + ":"
				+ request.getServerPort()
				+ mHttpUrlName.substring(0, mHttpUrlName.indexOf("/", 2))
				+ mDoCommand;

		String FormID = doc.getFormid();
		rtn.put("FormID", FormID);
		rtn.put("DocumentID", doc.getId());
		rtn.put("DomainID", doc.getDomainid());
		rtn.put("ApplicationID", doc.getApplicationid());
		rtn.put("mGetBatchDocumentUrl", mGetBatchDocumentUrl);
		rtn.put("mDoCommandUrl", mDoCommandUrl);

		return rtn;
	}

	/**
	 * 根据表单文档输出表格行HTML
	 * 
	 * @param doc
	 *            表单文档
	 * @return
	 * @throws Exception
	 */
	public String toRowHtml(Document doc) throws Exception {
		StringBuffer htmlBuffer = new StringBuffer();
		String contextPath = request.getContextPath();
		try {
			IRunner runner = getRunner();
			runner.initBSFManager(doc, params, webUser,
					new ArrayList<ValidateMessage>());
			Iterator<Column> iter = view.getColumns().iterator();
			while (iter.hasNext()) {
				Column col = iter.next();
				String result = col.getText(doc, runner, webUser);
				String tip = result;

				boolean isHidden = isHiddenColumn(col);
				if (!isHidden) {
					// 宽度为0时隐藏
					if ((col.getWidth() != null && col.getWidth().equals("0")) || !col.isVisible()) {
						htmlBuffer
								.append("<td class='table_th_td' style='display: none;'>");
					} else {
						htmlBuffer.append("<td class='table_th_td' width='"
								+ col.getWidth() + "'>");
					}

					// 是否只读 || 是否操作列
					if (view.getReadonly() || ("COLUMN_TYPE_OPERATE").equals(col.getType()) || ("COLUMN_TYPE_LOGO").equals(col.getType())) {
						htmlBuffer.append(result);
					} else {
						if (result != null)
							if (result.toString().toLowerCase().indexOf("<a ") != -1&& result.toString().toLowerCase().indexOf("</a>") != -1
								|| 	result.toString().toLowerCase().indexOf("<input ") != -1 && (result.toString().toLowerCase().indexOf("type='button'") != -1 || result.toString().toLowerCase().indexOf("type=button") != -1)) {
								htmlBuffer.append(result);
							} else {
								String templateForm = "";
								if(View.DISPLAY_TYPE_TEMPLATEFORM.equals(view.getDisplayType())){
									templateForm = view.getTemplateForm();
								}
								htmlBuffer
										.append("<a href=\"javaScript:viewDoc('"
												+ doc.getId()
												+ "', '"
												+ doc.getFormid()
												+ "', '"
												+ isSignatureExist()
												+ "','"
												+templateForm
												+"')\"");
								if (result.indexOf("img") != 0) {
									if (col.isShowTitle())
										htmlBuffer.append(" title='" + tip
												+ "'>");
									else
										htmlBuffer.append(">");
									String displayType = col.getDisplayType();
									if (Column.DISPLAY_ALL.equals(displayType)) {
										htmlBuffer.append(result + "</a>");
									} else {
										int displayLength = -1;
										String length = col.getDisplayLength();
										//不为空/空字符串/非数字类型
										if(!StringUtil.isBlank(length) && length.matches("\\d+"))
											displayLength = Integer.valueOf(length);
										if (displayLength > -1) {
											if (result.length() > displayLength) {
												String r = result.substring(0,
														displayLength);
												htmlBuffer.append(r + "..."
														+ "</a>");
											} else {
												htmlBuffer.append(result
														+ "</a>");
											}
										} else {
											htmlBuffer.append(result + "</a>");
										}
									}
								} else {
									htmlBuffer.append(" title='" + tip + "'>"
											+ result + "</a>");
								}
							}
					}
					if(("COLUMN_TYPE_OPERATE").equals(col.getType()) && (Column.BUTTON_TYPE_DELETE).equals(col.getButtonType())){
						htmlBuffer.append("<input type=button onclick='on_delete(\"" + doc.getId() + "\")' value='" + col.getButtonName() + "'/>");
						htmlBuffer.append("</td>");
					}else if(("COLUMN_TYPE_OPERATE").equals(col.getType()) && (Column.BUTTON_TYPE_DOFLOW).equals(col.getButtonType())){
						htmlBuffer.append("<input type=button onclick='on_doflow(\"" + doc.getId() + "\"" +  "," 
								            + "\"" + col.getApproveLimit() +"\")' value='" + col.getButtonName() +"'/>");
						htmlBuffer.append("</td>");
					}else if(("COLUMN_TYPE_OPERATE").equals(col.getType()) && (Column.BUTTON_TYPE_TEMPFORM).equals(col.getButtonType())){
						htmlBuffer.append("<input type=button onclick=\"javaScript:viewDoc('"
								+ doc.getId() + "', '" 
								+ doc.getFormid() + "', '" 
								+ isSignatureExist() + "', '" 
								+ col.getTemplateForm() + "')\" value='" 
								+ col.getButtonName() 
								+"'/>");
						htmlBuffer.append("</td>");
					}
					if(("COLUMN_TYPE_LOGO").equals(col.getType()) && col.getIcon() != null && !col.getIcon().equals("")){
						htmlBuffer.append("<img style='' src='" + contextPath + "/lib/icon/" + col.getIcon()+ "'/>");
						htmlBuffer.append("</td>");
					}else if (result != null && result.length() > 0) {
						htmlBuffer.append("</td>");
					}else {
						htmlBuffer.append("&nbsp;</td>");
					}
				}
			}
		} catch (Exception e) {
			LOG.warn("toRowHtml", e);
			throw new Exception(e);
		}

		return htmlBuffer.toString();
	}

	/**
	 * 获取宏脚本执行器
	 * 
	 * @return
	 * @throws Exception
	 */
	public IRunner getRunner() throws Exception {
		if (runner == null) {
			Document parent = (Document) request.getAttribute("parent");
			Document tdoc = parent != null ? parent : new Document();
			runner = JavaScriptFactory.getInstance(
					request.getSession().getId(), view.getApplicationid());
			runner.initBSFManager(tdoc, params, webUser,
					new ArrayList<ValidateMessage>());
		}

		return runner;
	}

	public String getViewStyle() {
		if (view == null || view.getStyle() == null) {
			return null;
		}
		return view.getStyle().getId();
	}
	
	
	public String getApplicationid() throws Exception{
		if(view==null){
			String viewid = params.getParameterAsString("_viewid");
			ViewProcess process = (ViewProcess) ProcessFactory.createProcess(ViewProcess.class);
			view = (View) process.doView(viewid);
		}
		return view.getApplicationid();
	}
}
