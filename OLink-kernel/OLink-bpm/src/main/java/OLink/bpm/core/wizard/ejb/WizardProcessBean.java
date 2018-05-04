package OLink.bpm.core.wizard.ejb;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import OLink.bpm.core.deploy.module.ejb.ModuleProcess;
import OLink.bpm.core.deploy.module.ejb.ModuleVO;
import OLink.bpm.core.dynaform.activity.ejb.Activity;
import OLink.bpm.core.dynaform.activity.ejb.ActivityType;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.dynaform.view.ejb.Column;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.dynaform.view.ejb.ViewProcess;
import OLink.bpm.core.links.ejb.LinkVO;
import OLink.bpm.core.resource.ejb.ResourceProcess;
import OLink.bpm.core.resource.ejb.ResourceType;
import OLink.bpm.core.resource.ejb.ResourceVO;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.wizard.util.WizardUtil;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.deploy.application.ejb.ApplicationProcess;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiProcess;
import eWAP.core.Tools;

/**
 * WizardProcessBean class.
 * 
 * @author zhuxuehong, Sam
 * @since JDK1.4
 */

public class WizardProcessBean extends AbstractDesignTimeProcessBean<WizardVO> implements WizardProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1074889227713498941L;

	/**
	 * @SuppressWarnings getDefaultDAO得到的process不确定
	 */
	@SuppressWarnings("unchecked")
	protected IDesignTimeDAO<WizardVO> getDAO() throws Exception {
		return (IDesignTimeDAO<WizardVO>) DAOFactory.getDefaultDAO(WizardVO.class.getName());
	}

	private ModuleProcess moduleProcess;
	private FormProcess formProcess;

	private final static WizardUtil wizardUtil = new WizardUtil();

	private ViewProcess viewProcess;
	private BillDefiProcess billDefiProcess;
	private ResourceProcess resourceProcess;
	private ApplicationProcess applicationProcess;

	public WizardProcessBean() {
		try {
			setupProcesses();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setupProcesses() throws Exception {
		moduleProcess = (ModuleProcess) ProcessFactory.createProcess(ModuleProcess.class);
		formProcess = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
		viewProcess = (ViewProcess) ProcessFactory.createProcess(ViewProcess.class);
		billDefiProcess = (BillDefiProcess) ProcessFactory.createProcess(BillDefiProcess.class);
		resourceProcess = (ResourceProcess) ProcessFactory.createProcess(ResourceProcess.class);
		applicationProcess = (ApplicationProcess) ProcessFactory.createProcess(ApplicationProcess.class);
	}

	/**
	 * 当确认时，程序创建所有的表单，视图，菜单，流程
	 * 
	 * @param vo
	 *            向导VO
	 * @param user
	 *            当前在线用户
	 * @param applicationid
	 *            当前应用ID
	 * @param contextBasePath
	 *            当前应用的相对路径
	 * @throws Exception
	 */
	public void confirm(ValueObject vo, WebUser user, String applicationid, String contextBasePath) throws Exception {
		WizardVO wizardvo = (WizardVO) vo;
		try {
			ModuleVO module = createModule(wizardvo, applicationid);

			Form mainForm = createMainForm(wizardvo, module, contextBasePath);
			Form subForm = null;
			if (wizardvo.getF_Type().equals("01")) {
				subForm = createSubForm(wizardvo, module, contextBasePath);
				View subView = createViewForSubForm(wizardvo, subForm, contextBasePath);
				wizardvo.setF_subForm_viewid(subView.getId());
				updateSubForm(mainForm, subView, wizardvo, contextBasePath);
			}
			createWorkflow(wizardvo, module, mainForm, user, contextBasePath);

			ResourceVO topMenu = createResource(wizardvo, module);

			createView(wizardvo, module, topMenu, mainForm, contextBasePath);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 找回主表单，并设置子表单的关联
	 * 
	 * @param mainForm
	 *            主表单
	 * @param subView
	 *            给子表单用的视图
	 * @param wizardvo
	 *            向导VO
	 * @param contextBasePath
	 *            当前应用的相对路径
	 * @throws Exception
	 */
	private void updateSubForm(Form mainForm, View subView, WizardVO wizardvo, String contextBasePath) throws Exception {
		mainForm.setTemplatecontext(mainForm.getTemplatecontext()
				+ wizardUtil.getUpdatedTemplateContext(wizardvo, contextBasePath));
		formProcess.doUpdate(mainForm);
	}

	/**
	 * 创建流程
	 * 
	 * @param vo
	 *            向导VO
	 * @param modulevo
	 *            模块VO
	 * @param form
	 *            主表单
	 * @param user
	 *            当前在线用户
	 * @param contextBasePath
	 *            当前应用的相对路径
	 * @return 创建好的流程VO
	 * @throws Exception
	 */
	private BillDefiVO createWorkflow(WizardVO vo, ModuleVO modulevo, Form form, WebUser user, String contextBasePath)
			throws Exception {
		BillDefiVO billDefiVO = new BillDefiVO();
		Date lastmodify = new Date();
		// wizardUtil = new WizardUtil();
		if (vo.getW_workflowid() == null || vo.getW_workflowid().trim().length() == 0) {

			billDefiVO.setApplicationid(vo.getApplicationid());
			billDefiVO.setAuthorname(user.getName());
			billDefiVO.setAuthorno(user.getLoginno());
			billDefiVO.setSortId(Tools.getTimeSequence());
			billDefiVO.setModule(modulevo);
			billDefiVO.setId(Tools.getSequence());
			billDefiVO.setFlow(wizardUtil.getW_content(contextBasePath, vo.getW_content()));
			billDefiVO.setLastmodify(lastmodify);
			billDefiVO.setSubject(vo.getW_name());

			billDefiProcess.doCreate(billDefiVO);
		} else {
			billDefiVO = (BillDefiVO) billDefiProcess.doView(vo.getW_workflowid());
			if (billDefiVO != null) {
				billDefiVO.setSubject(vo.getW_name());
				billDefiVO.setFlow(vo.getW_content());
				billDefiProcess.doUpdate(billDefiVO);
			}
		}

		// 如果选择了流程处理时则把form的流程处理(activity)和workflow联系起来
		Activity activity = new Activity();
		activity.setId(Tools.getSequence());
		activity.setName("Submit_WorkFlow");
		activity.setApplicationid(vo.getApplicationid());
		if (billDefiVO != null)
			activity.setOnActionFlow(billDefiVO.getId());
		activity.setParentForm(form.getId());
		activity.setType(ActivityType.WORKFLOW_PROCESS);
		activity.setSortId(Tools.getTimeSequence());

		form.getActivitys().add(activity);
		formProcess.doUpdate(form);

		return billDefiVO;
	}

	/**
	 * 为子表单创建视图
	 * 
	 * @param vo
	 *            向导VO
	 * @param form
	 *            子表单
	 * @param contextBasePath
	 *            当前应用的相对路径
	 * @return 创建好的子表单视图
	 * @throws Exception
	 */
	private View createViewForSubForm(ValueObject vo, Form form, String contextBasePath) throws Exception {

		WizardVO wizardVO = (WizardVO) vo;
		ModuleVO moduleVO = form.getModule();

		View view = new View();

		String formName = moduleVO.getApplication().getName() + "/" + moduleVO.getName() + "/" + form.getName();
		String filterCode = "\"$formname = '" + formName + "\"";

		// 常规属性
		view.setId(Tools.getSequence());
		view.setSortId(Tools.getTimeSequence());
		view.setApplicationid(vo.getApplicationid());
		view.setName("SubFormView_".concat(wizardVO.getF_name_sub()));

		// 设置view的属性
		view.setOpenType(View.OPEN_TYPE_POP);
		view.setEditMode(View.EDIT_MODE_DESIGN);
		view.setFilterScript(filterCode);
		view.setModule(moduleVO);
		view.setShowTotalRow(false);
		view.setPagination(true);
		view.setPagelines("15");
		view.setRelatedForm(form.getId());

		viewProcess.doCreate(view);

		// 建立与view相关的cloumn
		List<Column> subFormViewColumn = new ArrayList<Column>();

		// 设置column列
		// Column name = new Column();
		String[] fields = null;
		if (wizardVO.getF_subForm_viewColumns().trim().length() > 0 && !wizardVO.getF_subForm_viewColumns().equals("")) {
			fields = wizardVO.getF_subForm_viewColumns().split(";");
		}
		if (fields != null) {// && fields.length > 0 && !"".equals(fields)
			for (int i = 0; i < fields.length; i++) {
				Column column = new Column();
				column.setId(Tools.getSequence());
				column.setOrderno(i);
				column.setFormid(form.getId());
				column.setApplicationid(vo.getApplicationid());
				column.setParentView(view.getId());
				column.setFieldName(fields[i]);
				column.setName(fields[i]);
				subFormViewColumn.add(column);
				view.getColumns().add(column);
			}
		}

		// 建立与view相关的activity
		Activity activity = null;
		if (wizardVO.getF_subForm_viewActivitys() != null) {

			String[] v_activity = wizardVO.getF_subForm_viewActivitys();

			if (v_activity != null && v_activity.length > 0) {
				for (int i = 0; i < v_activity.length; i++) {
					activity = new Activity();
					activity.setId(Tools.getSequence());
					activity.setName(WizardVO._VIEWACLIST.get(v_activity[i]));
					activity.setType(Integer.parseInt(v_activity[i]));
					// activity.setOnActionView(view);
					activity.setParentView(view.getId());
					activity.setOnActionForm(form.getId());
					activity.setOrderno(i + 1);
					activity.setApplicationid(vo.getApplicationid());

					view.getActivitys().add(activity);
				}
			}
			viewProcess.doUpdate(view);
		}

		return view;
	}

	/**
	 * 创建视图
	 * 
	 * @param vo
	 *            向导VO
	 * @param modulevo
	 *            模块VO
	 * @param resource
	 *            菜单VO
	 * @param form
	 *            主表单VO
	 * @param contextBasePath
	 *            当前应用的相对路径
	 * @throws Exception
	 */
	private void createView(WizardVO vo, ModuleVO modulevo, ResourceVO resource, Form form, String contextBasePath)
			throws Exception {

		String pendingFilterScript = "";
		String allFilterScript = "";
		String formName = modulevo.getApplication().getName() + "/" + modulevo.getName() + "/" + form.getName();

		// 建立与view相关的searchForm
		Form searchForm = new Form();
		if (vo.getV_searchForm().length() > 0 && vo.getV_searchForm() != null) {
			searchForm.setApplicationid(vo.getApplicationid());
			searchForm.setId(Tools.getSequence());
			searchForm.setModule(modulevo);
			searchForm.setSortId(Tools.getTimeSequence());
			searchForm.setName(vo.getF_name() + "_Search");
			searchForm.setType(Form.FORM_TYPE_SEARCHFORM);
			searchForm.setTemplatecontext(wizardUtil.getV_searchForm(vo.getV_searchForm(), contextBasePath));
			formProcess.doCreate(searchForm);
			searchForm = (Form) formProcess.doView(searchForm.getId());
		}
		String[] v_type = vo.getV_type();
		if (v_type != null && v_type.length > 0) {
			for (int i = 0; i < v_type.length; i++) {
				if (v_type[i].equalsIgnoreCase(WizardVO.VIEWDISPLAY_PENDING)) {
					// 设置FORM的待办View
					vo.setPending(true);
					if (vo.getV_filter() != null && vo.getV_filter().length() > 0) {
						pendingFilterScript = wizardUtil.getV_filter(contextBasePath, vo, formName);
					}
					getview(vo, modulevo, resource, form, searchForm, pendingFilterScript, v_type[i]);
				} else {
					allFilterScript = formName;
					if (vo.getV_filter() != null && vo.getV_filter().length() > 0) {
						allFilterScript = wizardUtil.getV_filter(contextBasePath, vo, formName);
					}
					getview(vo, modulevo, resource, form, searchForm, allFilterScript, v_type[i]);
				}
			}
		}
	}

	/**
	 * 创建View的主体
	 * 
	 * @param vo
	 *            向导VO
	 * @param modulevo
	 *            模块VO
	 * @param resource
	 *            菜单VO
	 * @param form
	 *            主表单VO
	 * @param searchForm
	 *            查询模板VO
	 * @param filterscript
	 *            过滤脚本
	 * @param v_type
	 *            视图类型
	 * @throws Exception
	 */
	private void getview(WizardVO vo, ModuleVO modulevo, ResourceVO resource, Form form, Form searchForm,
			String filterscript, String v_type) throws Exception {
		View view = new View();
		view.setId(Tools.getSequence());
		view.setSortId(Tools.getTimeSequence());
		view.setApplicationid(vo.getApplicationid());
		if (v_type != null && v_type.equalsIgnoreCase(WizardVO.VIEWDISPLAY_PENDING)) {
			view.setName(vo.getV_name() + "_" + WizardVO.VIEWDISPLAY_PENDING);
		} else {
			view.setName(vo.getV_name());
		}

		view.setOpenType(View.OPEN_TYPE_NORMAL);
		view.setEditMode(View.EDIT_MODE_CODE_DQL);
		view.setFilterScript(filterscript);
		view.setModule(modulevo);
		view.setShowTotalRow(vo.getV_isShowTotalRow() == null ? false : vo.getV_isShowTotalRow().equalsIgnoreCase(
				"true"));
		view.setPagination(vo.getV_isPagination() == null ? false : vo.getV_isPagination().equalsIgnoreCase("true"));
		view.setPagelines(vo.getV_pagelines());
		if (searchForm != null) {
			Form _searchForm = new Form();
			_searchForm.setId(searchForm.getId());
			view.setSearchForm(_searchForm);
		}

		// 新建视图相关链接
		LinkVO lVO = new LinkVO();
		lVO.setActionContent(view.getId());
		lVO.setApplicationid(view.getApplicationid());
		lVO.setName(view.getName());
		lVO.setType(LinkVO.LinkType.VIEW.getCode());
		lVO.setQueryString("[]");
		lVO.setModuleid(view.getModule().getId());

		// 建立与相关的resource
		ResourceVO resourceVO = new ResourceVO();
		resourceVO.setLink(lVO);
		resourceVO.setId(Tools.getSequence());
		resourceVO.setSuperior(resource);
		resourceVO.setModule(modulevo.getId());
		resourceVO.setApplication(vo.getApplicationid());
		resourceVO.setApplicationid(vo.getApplicationid());

		if (v_type != null && v_type.equalsIgnoreCase(WizardVO.VIEWDISPLAY_PENDING)) {
			resourceVO.setDescription(vo.getV_description() + "_" + WizardVO.VIEWDISPLAY_PENDING);
		} else {
			resourceVO.setDescription(vo.getV_description() + "_" + WizardVO.VIEWDISPLAY_ALL);
		}
		resourceVO.setType(ResourceType.RESOURCE_TYPE_MENU);
		resourceVO.setIsprotected(false);
		resourceVO.setResourceAction(ResourceType.ACTION_TYPE_VIEW);

		resourceVO.setDisplayView(view.getId());
		view.setRelatedResourceid(resourceVO.getId());
		
		viewProcess.doCreate(view);
		resourceProcess.doCreateMenu(resourceVO);

		// 建立与view相关的column
		List<Column> v_cloumn = getAllColumn(vo, view, form, vo.getApplicationid());
		for (Iterator<Column> iter = v_cloumn.iterator(); iter.hasNext();) {
			Column column = iter.next();
			view.getColumns().add(column);
		}

		// 建立与view相关的activity
		Activity act = null;
		if (v_type != null && v_type.equalsIgnoreCase(WizardVO.VIEWDISPLAY_ALL)) {
			String[] v_activity = vo.getV_activity();
			if (v_activity != null && v_activity.length > 0) {
				for (int i = 0; i < v_activity.length; i++) {
					act = new Activity();
					act.setId(Tools.getSequence());
					act.setName(WizardVO._VIEWACLIST.get(v_activity[i]));
					act.setType(Integer.parseInt(v_activity[i]));
					if (v_activity[i].equals("1") || v_activity[i].equals("16")) {
						act.setOnActionView(view.getId());
					} else if (v_activity[i].equals("2") || v_activity[i].equals("4")) {
						act.setOnActionForm(form.getId());
						// act.setParentForm(form);
					}
					act.setParentView(view.getId());
					act.setOrderno(i + 1);
					act.setApplicationid(vo.getApplicationid());
					view.getActivitys().add(act);
				}
			}
		}
		viewProcess.doUpdate(view);
	}

	/**
	 * 创建菜单
	 * 
	 * @param vo
	 *            向导VO
	 * @param modulevo
	 *            模块VO
	 * @return 创建好后的菜单VO
	 * @throws Exception
	 */
	private ResourceVO createResource(WizardVO vo, ModuleVO modulevo) throws Exception {
		ResourceVO resource = new ResourceVO();

		resource.setId(Tools.getSequence());
		resource.setSortId(Tools.getTimeSequence());
		resource.setApplication(vo.getApplicationid());
		resource.setApplicationid(vo.getApplicationid());
		resource.setModule(modulevo.getId());
		resource.setDescription(vo.getR_description());
		resource.setOrderno(vo.getR_orderno() + "");
		resource.setType(ResourceType.RESOURCE_TYPE_MENU);
		resource.setIsprotected(false);
		resource.setResourceAction(ResourceType.ACTION_TYPE_NONE);

		if (!vo.getR_superior().equals(""))
			resource.setSuperior((ResourceVO) resourceProcess.doView(vo.getR_superior()));

		resourceProcess.doCreate(resource);
		return resource;
	}

	/**
	 * 创建主菜单
	 * 
	 * @param wizardvo
	 *            向导VO
	 * @param modulevo
	 *            模块VO
	 * @param contextBasePath
	 *            当前应用的相对路径
	 * @return 创建好的表单VO
	 * @throws Exception
	 */
	private Form createMainForm(WizardVO wizardvo, ModuleVO modulevo, String contextBasePath) throws Exception {
		Form form = null;

		Activity act;
		// 创建Form
		if (wizardvo.getF_formid() == null || wizardvo.getF_formid().trim().length() == 0) {
			form = new Form();
			form.setApplicationid(wizardvo.getApplicationid());
			form.setModule(modulevo);

			form.setName(wizardvo.getF_name());
			form.setTemplatecontext(wizardUtil.getF_TemplateContext(wizardvo.getF_templatecontext(), wizardvo
					.getF_style(), contextBasePath));
			form.setSortId(Tools.getTimeSequence());
			form.setType(Form.FORM_TYPE_NORMAL);
			formProcess.doCreate(form);

			// 根据提交回来的信息，新建表单的Activity
			String[] f_activitys = wizardvo.getF_activitys();
			if (f_activitys != null && f_activitys.length > 0) {
				for (int i = 0; (i < f_activitys.length)
						&& !(f_activitys[i].equals(String.valueOf(ActivityType.WORKFLOW_PROCESS))); i++) {
					act = new Activity();
					act.setId(Tools.getSequence());
					act.setName(WizardVO._FORMACLIST.get(f_activitys[i]));
					act.setType(Integer.parseInt(f_activitys[i]));
					act.setOnActionForm(form.getId());
					act.setApplicationid(wizardvo.getApplicationid());
					act.setOrderno(i + 1);
					act.setParentForm(form.getId());

					form.getActivitys().add(act);
				}
				formProcess.doUpdate(form);

			}
		}
		return form;
	}

	/**
	 * 创建子表单
	 * 
	 * @param wizardvo
	 *            向导VO
	 * @param modulevo
	 *            模块VO
	 * @param contextBasePath
	 *            当前应用的相对路径
	 * @return 创建好的子表单VO
	 * @throws Exception
	 */
	private Form createSubForm(WizardVO wizardvo, ModuleVO modulevo, String contextBasePath) throws Exception {
		Form form = null;

		Activity act;
		// 创建Form
		if (wizardvo.getF_formId_sub() == null || wizardvo.getF_formId_sub().trim().length() == 0) {
			form = new Form();
			form.setApplicationid(wizardvo.getApplicationid());
			form.setModule(modulevo);

			form.setName(wizardvo.getF_name_sub());
			form.setTemplatecontext(wizardUtil.getF_TemplateContext(wizardvo.getF_fieldsdescription_sub(), wizardvo
					.getF_style_sub(), contextBasePath));
			form.setSortId(Tools.getTimeSequence());
			form.setType(Form.FORM_TYPE_NORMAL);
			formProcess.doCreate(form);

			// 根据提交回来的信息，新建表单的Activity
			String[] f_activitys = wizardvo.getF_activitys_sub();
			if (f_activitys != null && f_activitys.length > 0) {
				for (int i = 0; (i < f_activitys.length)
						&& !(f_activitys[i].equals(String.valueOf(ActivityType.WORKFLOW_PROCESS))); i++) {
					act = new Activity();
					act.setId(Tools.getSequence());
					act.setName(WizardVO._FORMACLIST.get(f_activitys[i]));
					act.setType(Integer.parseInt(f_activitys[i]));
					act.setOnActionForm(form.getId());
					act.setParentForm(form.getId());
					act.setOrderno(i + 1);
					act.setApplicationid(wizardvo.getApplicationid());

					form.getActivitys().add(act);
				}
				formProcess.doUpdate(form);

			}
		}
		return form;
	}

	/**
	 * 创建模块
	 * 
	 * @param vo
	 *            向导VO
	 * @param applicationid
	 *            当前应用ID
	 * @return 创建好的模块VO
	 * @throws Exception
	 */
	private ModuleVO createModule(WizardVO vo, String applicationid) throws Exception {
		ModuleVO modulevo = new ModuleVO();
		ApplicationVO appvo = (ApplicationVO) applicationProcess.doView(applicationid);
		if (vo.getModuleid() == null || vo.getModuleid().trim().length() == 0) {
			modulevo.setId(Tools.getSequence());
			modulevo.setApplicationid(appvo.getId());
			modulevo.setApplication(appvo);
			modulevo.setName(vo.getM_name());
			modulevo.setDescription(vo.getM_description());
			modulevo.setSortId(Tools.getTimeSequence());
			moduleProcess.doCreate(modulevo);
		} else {
			modulevo = (ModuleVO) moduleProcess.doView(vo.getModuleid());
			if (modulevo != null) {
				modulevo.setName(vo.getM_name());
				modulevo.setDescription(vo.getM_description());
				moduleProcess.doUpdate(modulevo);
			}

		}
		if (modulevo != null)
			vo.setModuleid(modulevo.getId());
		return modulevo;
	}

	/**
	 * 获取所有的列字段
	 * 
	 * @param wizardvo
	 *            向导VO
	 * @param view
	 *            视图VO
	 * @param form
	 *            表单VO
	 * @param applicationid
	 *            当前应用的ID
	 * @return 视图的所有列字段的集合
	 * @throws Exception
	 */
	private List<Column> getAllColumn(WizardVO wizardvo, View view, Form form, String applicationid) throws Exception {
		List<Column> v_cloumns = new ArrayList<Column>();

		// 设置column列
		String[] fields = null;
		if (wizardvo.getV_columns().trim().length() > 0 && !wizardvo.getV_columns().equals("")) {
			fields = wizardvo.getV_columns().split(";");
		}
		if (fields != null) {// && fields.length > 0 && !"".equals(fields)
			for (int i = 0; i < fields.length; i++) {
				Column columns = new Column();
				columns.setOrderno(i);
				columns.setFormid(form.getId());
				columns.setApplicationid(applicationid);
				columns.setParentView(view.getId());
				columns.setFieldName(fields[i]);
				columns.setName(fields[i]);
				columns.setId(Tools.getSequence());
				v_cloumns.add(columns);
			}
		}
		return v_cloumns;
	}
}