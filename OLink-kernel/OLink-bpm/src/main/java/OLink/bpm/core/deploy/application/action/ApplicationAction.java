package OLink.bpm.core.deploy.application.action;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpSession;

import OLink.bpm.base.action.BaseAction;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.base.ejb.AbstractRunTimeProcessBean;
import OLink.bpm.core.deploy.application.ejb.ApplicationProcess;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.core.deploy.module.action.ModuleHelper;
import OLink.bpm.core.deploy.module.ejb.ModuleProcess;
import OLink.bpm.core.deploy.module.ejb.ModuleVO;
import OLink.bpm.core.dynaform.dts.datasource.action.DatasourceHelper;
import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSource;
import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSourceProcess;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.dynaform.form.ejb.FormTableProcessBean;
import OLink.bpm.core.superuser.ejb.SuperUserProcess;
import OLink.bpm.core.superuser.ejb.SuperUserVO;
import OLink.bpm.core.tree.Node;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.DbTypeUtil;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import OLink.bpm.util.http.ResponseUtil;
import OLink.bpm.util.json.JsonUtil;
import OLink.bpm.core.dynaform.form.ejb.Form;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import OLink.bpm.core.deploy.application.ejb.CopyApplicationProcess;


import eWAP.core.Tools;
import com.opensymphony.webwork.ServletActionContext;

/**
 * @see BaseAction ApplicationAction class.
 * @author Darvense
 * @since JDK1.4
 */
public class ApplicationAction extends BaseAction<ApplicationVO> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger(ApplicationAction.class);

	private String id;

	private String overviewFile;

	public String getId() {
		return id;
	}

	public void setId(String _applicationid) {
		this.id = _applicationid;
	}

	/**
	 * 
	 * ApplicationAction structure function.
	 * 
	 * @SuppressWarnings 工厂方法无法使用泛型
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public ApplicationAction() throws ClassNotFoundException {
		super(ProcessFactory.createProcess(ApplicationProcess.class),
				new ApplicationVO());
	}

	/**
	 * @SuppressWarnings WebWork不支持泛型
	 */
	public String doEdit() {
		Map params = getContext().getParameters();
		String id = ((String[]) params.get("id"))[0];
		HttpSession session = ServletActionContext.getRequest().getSession();
		// String
		// currentApplication=(String)session.getAttribute("currentApplication");
		session.setAttribute("currentApplication", id);
		return super.doEdit();
	}

	/**
	 * get_ispublished method
	 * 
	 * @return Return true or false String
	 */
	public String get_ispublished() {
		ApplicationVO content = (ApplicationVO) getContent();
		if (content.isIspublished()) {
			return "true";
		} else {
			return "false";
		}
	}

	/**
	 * Set the _ispublished
	 * 
	 * @param ispublished
	 */
	public void set_ispublished(String ispublished) {
		ApplicationVO content = (ApplicationVO) getContent();
		if (ispublished != null) {
			if (ispublished.trim().equalsIgnoreCase("true")) {
				content.setIspublished(true);
				return;
			}
		}
		content.setIspublished(false);
	}

	/**
	 * get_isdefaultsite method
	 * 
	 * @return Return true or false String
	 */
	public String get_isdefaultsite() {
		ApplicationVO content = (ApplicationVO) getContent();
		if (content.isIsdefaultsite()) {
			return "true";
		} else {
			return "false";
		}
	}

	/**
	 * set_isdefaultsite method
	 * 
	 * @param isdefaultsite
	 */
	public void set_isdefaultsite(String isdefaultsite) {
		ApplicationVO content = (ApplicationVO) getContent();
		if (isdefaultsite != null) {
			if (isdefaultsite.trim().equalsIgnoreCase("true")) {
				content.setIsdefaultsite(true);
				return;
			}
		}
		content.setIspublished(false);
	}

	/**
	 * Delete a tempApp
	 * 
	 * @return If the action execution was successful.return "SUCCESS".Show an
	 *         success view .
	 *         <p>
	 *         If the action execution was a failure. return "ERROR".Show an
	 *         error view, possibly asking the user to retry entering data.
	 *         <p>
	 *         The "INPUT" is also used if the given input params are invalid,
	 *         meaning the user should try providing input again.
	 * 
	 * @throws Exception
	 */
	public String doDelete() {
		String errorField = "";

		try {
			if (_selects != null) {
				for (int i = 0; i < _selects.length; i++) {
					String id = _selects[i];
					ApplicationVO tempApp = (ApplicationVO) (process.doView(id));
					if (tempApp.getDomains().size() > 0) {
						errorField += "(" + tempApp.getName()
								+ "){*[core.application.referenced]*},";
					} else {
						ModuleProcess moduleProcess = (ModuleProcess) ProcessFactory
								.createProcess(ModuleProcess.class);

						try {
							int removedModuleSize = 0;
							Collection<ModuleVO> modules = tempApp.getModules();
							if (modules != null) {
								Collection<ModuleVO> tempModule = moduleProcess
										.deepSearchModule(modules, null, null,
												1);
								for (Iterator<ModuleVO> iterator = tempModule
										.iterator(); iterator.hasNext();) {
									ModuleVO module = iterator
											.next();
									if (module.getSuperior() == null) {
										ModuleHelper mh = new ModuleHelper();
										mh.deleteModuleWithSub(moduleProcess,
												module);
									}
									removedModuleSize++;
								}
								if (tempModule.size() == removedModuleSize) {
									tempApp.setOwners(null);
									tempApp.setModules(null);
									process.doUpdate(tempApp);
									process.doRemove(id);
									deleteDataSource(id);
								} else {
									errorField = "("
											+ tempApp.getName()
											+ "){*[core.application.hassub]*}"
											+ (StringUtil.isBlank(errorField) ? ""
													: "," + errorField);
								}
							} else {
								tempApp.setOwners(null);
								process.doUpdate(tempApp);
								process.doRemove(id);
								deleteDataSource(id);
							}

						} catch (Exception e) {
							e.printStackTrace();
							errorField = e.getMessage()
									+ (StringUtil.isBlank(errorField) ? ""
											: "," + errorField);
							// errorField = "(" + tempApp.getName() +
							// "){*[core.application.hassub]*}" + "," +
							// errorField;
						}
					}
					if (!errorField.equals("")) {
						this.addFieldError("1", errorField);
						return INPUT;
					}
				}
				this.addActionMessage("{*[delete.successful]*}");

			}
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("", e.getMessage());
			return INPUT;
		}

	}

	/**
	 * 删除对应软件下所有数据源
	 * 
	 * @param id
	 */
	private void deleteDataSource(String id) {
		try {
			// 删除该软件下所有数据源
			DataSourceProcess dataSourceProcess = (DataSourceProcess) ProcessFactory
					.createProcess(DataSourceProcess.class);
			ParamsTable params = new ParamsTable();
			params.setParameter("t_applicationid", id);
			DataPackage<DataSource> dataPackage = dataSourceProcess
					.doQuery(params);
			if (dataPackage.rowCount > 0) {
				for (Iterator<DataSource> iteratorDataSource = dataPackage.datas
						.iterator(); iteratorDataSource.hasNext();) {
					DataSource dataSource = iteratorDataSource
							.next();
					dataSourceProcess.doRemove(dataSource);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 同步当前应用关联的数据库的所有表单的表(同步生成的表没有数据)
	 * 
	 * @return
	 */
	public String doSynFormTable() {
		try {
			ApplicationVO vo = (ApplicationVO) getContent();

			String newDtsId = vo.getDatasourceid();
			String oldDtsId = ((ApplicationVO) process
					.doView(vo.getId())).getDatasourceid();
			if (newDtsId != null && !newDtsId.equals(oldDtsId)) {
				this.addFieldError("saveBeforeSyn",
						"{*[Please]*}{*[Save]*}{*[Current]*}{*[Application]*}");
				return INPUT;
			}
			FormProcess fp = (FormProcess) ProcessFactory
					.createProcess(FormProcess.class);
			FormTableProcessBean tableProcess = new FormTableProcessBean(vo
					.getApplicationid());
			Iterator<Form> forms = fp.get_formList(vo.getId()).iterator();
			while (forms.hasNext()) {
				tableProcess.createOrUpdateDynaTable(forms.next(), null);
			}
			this.addActionMessage("{*[Synchronization]*}{*[Success]*}");
			return SUCCESS;
		} catch (Exception e) {
			this.addFieldError("synFail",
					"{*[Synchronization]*}{*[Fail]*}[{*[Detail]*}{*[Info]*}:"
							+ e.getMessage() + "]");
			return INPUT;
		}
	}

	public String doSave() {
		try {
			ApplicationVO vo = (ApplicationVO) getContent();
			SuperUserProcess sup = (SuperUserProcess) ProcessFactory
					.createProcess(SuperUserProcess.class);
			SuperUserVO user = (SuperUserVO) sup.doView(getUser().getId());

			String applicationname = vo.getName();

			ApplicationVO application = ((ApplicationProcess) process)
					.doViewByName(applicationname);
			// vo.setCreater(user);

			vo.getOwners().add(user);

			if (application != null) {
				if (application.getId() == null
						|| application.getId().trim().length() <= 0) {
					this.addFieldError("1", "{*[core.application.exist]*}");
					return INPUT;
				}
				if (!vo.getId().trim().equalsIgnoreCase(application.getId())) {
					this.addFieldError("1", "{*[core.application.exist]*}");
					return INPUT;
				}
				/*
				 * DataSource datasource = application.getDataSourceDefine(); if
				 * (StringUtil.isBlank(application.getDatasourceid()) ||
				 * datasource == null || StringUtil.isBlank(datasource.getId()))
				 * { this.addFieldError("empty.datasource",
				 * "{*[core.appliction.datasource.empty]*}"); return INPUT; }
				 */
			}

			if (vo.getDescription() == null
					|| vo.getDescription().trim().length() == 0)
				vo.setDescription("");
			if (vo.getId() != null) {
				AbstractRunTimeProcessBean.removeDataSource(vo.getId());
				DbTypeUtil.remove(vo.getId());
			}
			super.doSave();
			// new ResourceHelper().addReportResource(vo.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return SUCCESS;
	}

	public String doCopySave() {
		try {
			ApplicationVO vo = (ApplicationVO) getContent();
			/*
			 * if (vo.getName() == null || vo.getName().equals("")) { throw new
			 * Exception("{*[page.name.notexist]*}"); }
			 */
			SuperUserProcess sup = (SuperUserProcess) ProcessFactory
					.createProcess(SuperUserProcess.class);
			SuperUserVO user = (SuperUserVO) sup.doView(getUser().getId());
			vo.getOwners().add(user);
			if (vo.getDescription() == null
					|| vo.getDescription().trim().length() == 0)
				vo.setDescription("");
			if (vo.getId() != null) {
				AbstractRunTimeProcessBean.removeDataSource(vo.getId());
				DbTypeUtil.remove(vo.getId());
			}
			super.doSave();
		} catch (Exception e) {
			e.printStackTrace();
			this.addFieldError("Exception", e.getMessage());
		}
		return SUCCESS;
	}

	public String listApps() throws Exception {
		try {
			super.validateQueryParams();
			ParamsTable params = this.getParams();
			WebUser user = getUser();
			DataPackage<ApplicationVO> packages = null;
			if (!user.isSuperAdmin()) {
				params.setParameter("s_owners.id", user.getId());
			} else {
				if (params.getParameterAsString("sm_name") == null
						|| params.getParameterAsString("sm_description") == null) {
					String _currpage = params.getParameterAsString("_currpage");
					String _pagelines = params
							.getParameterAsString("_pagelines");
					String realPath = params.getParameterAsString("realPath");
					params = new ParamsTable();
					params.setParameter("_currpage", _currpage);
					params.setParameter("_pagelines", _pagelines);
					params.setParameter("realPath", realPath);
				}
			}
			packages = process.doQuery(params, user);
			setDatas(packages);
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}

	}

	public String doNew() {
		String appid = null;
		String sortid = null;
		try {
			appid = Tools.getSequence();
			sortid = Tools.getTimeSequence();
		} catch (Exception e) {
			e.printStackTrace();
		}
		ApplicationVO appVO = new ApplicationVO();
		appVO.setId(appid);
		appVO.setSortId(sortid);
		setContent(appVO);
		return SUCCESS;
	}

	/**
	 * 创建一个应用将旧的的应用信息复制到此应用中
	 * 
	 * @return
	 */
	public String doCopyNew() {
		try {
			ParamsTable params = this.getParams();
			String appId = params.getParameterAsString("application");
			ApplicationVO oldapp = (ApplicationVO) process
					.doView(appId);
			ApplicationVO newapp = new ApplicationVO();
			PropertyUtils.copyProperties(newapp, oldapp);
			newapp.setId(Tools.getSequence());
			newapp.setSortId(Tools.getTimeSequence());
			String uniqueLog = "_copy";
			newapp.setName(oldapp.getName() + uniqueLog);
			newapp.setDescription(oldapp.getDescription() + uniqueLog);
			// 复制applicationid为oldapp.getId()的所有数据源
			copyDtsAndResetProperty(oldapp.getId(), newapp);
			this.setContent(newapp);
			return SUCCESS;
			// return doSave();
		} catch (Exception e) {
			e.printStackTrace();
			this.addFieldError("error", e.getMessage());
			return INPUT;
		}
	}

	/**
	 * 复制applicationid为oldappid的所有数据源
	 * 
	 * @param oldappid
	 * @param newapp
	 * @throws Exception
	 */
	protected void copyDtsAndResetProperty(String oldappid, ApplicationVO newapp)
			throws Exception {
		DataSourceProcess dp = (DataSourceProcess) ProcessFactory
				.createProcess(DataSourceProcess.class);
		DatasourceHelper dh = new DatasourceHelper();
		Iterator<DataSource> it_dts = dh.getDataSources(oldappid).iterator();
		Collection<ValueObject> col_newdts = new ArrayList<ValueObject>();
		String uniqueLog = "_copy";
		while (it_dts.hasNext()) {
			DataSource olddts = it_dts.next();
			DataSource newdts = new DataSource();
			PropertyUtils.copyProperties(newdts, olddts);
			newdts.setId(Tools.getSequence());// 赋予一个id
			newdts.setSortId(Tools.getTimeSequence());// 赋予一个sortid
			newdts.setApplicationid(newapp != null ? newapp.getId() : "");// 重新赋予一个applicationid
			newdts.setName(olddts.getName() + uniqueLog);// 重新命名
			// 同步新的应用的datasourceid
			if (!StringUtil.isBlank(olddts.getId())
					&& olddts.getId().equals(newapp.getDatasourceid()))
				newapp.setDatasourceid(newdts.getId());
			col_newdts.add(newdts);
		}
		dp.doUpdate(col_newdts);
	}

	/**
	 * 根据域ID获取应用列表
	 * 
	 * @return
	 * @throws Exception
	 */
	public String listAppsByDomain() throws Exception {
		try {
			this.validateQueryParams();
			ParamsTable params = this.getParams();
			String domainId = params.getParameterAsString("domain");
			WebUser user = getUser();
			DataPackage<ApplicationVO> packages = null;
			params.setParameter("s_domains.id", domainId);
			params.setParameter("f_activated", true);
			packages = process.doQuery(params, user);
			setDatas(packages);
			return SUCCESS;
		} catch (Exception e) {
			log.warn(e);
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}

	}

	/**
	 * 列出所有未加入的应用开发者
	 * 
	 * @SuppressWarnings setDatas方法接收了非ApplicationVO的集合
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String doListUnjoinedDeveloper() {
		try {
			SuperUserProcess superUserProcess = (SuperUserProcess) ProcessFactory
					.createProcess(SuperUserProcess.class);
			DataPackage datas = superUserProcess
					.getUnjoinedDeveloperList(getParams());

			setDatas(datas);
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("", e.getMessage());
			return INPUT;
		}
	}

	/**
	 * 列出所有已加入的应用开发者
	 * 
	 * @SuppressWarnings setDatas方法接收了非ApplicationVO的集合
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String doListJoinedDeveloper() {
		try {
			this.validateQueryParams();
			SuperUserProcess superUserProcess = (SuperUserProcess) ProcessFactory
					.createProcess(SuperUserProcess.class);
			DataPackage datas = superUserProcess
					.getJoinedDeveloperList(getParams());
			setDatas(datas);
			return SUCCESS;
		} catch (Exception e) {
			addFieldError("", e.getMessage());
			return INPUT;
		}
	}

	/**
	 * getModuleTree
	 * 
	 * @throws ClassNotFoundException
	 */
	public String doModuleTree() throws ClassNotFoundException {
		ParamsTable params = getParams();
		ApplicationHelper helper = new ApplicationHelper();
		String applicationid = params.getParameterAsString("id");
		String parentid = params.getParameterAsString("parentid");
		if (applicationid != null && !"".equals(applicationid)) {
			Collection<Node> modTree = helper.getModuleTree(applicationid,
					parentid);
			if (modTree.size() > 0) {
				ResponseUtil.setJsonToResponse(ServletActionContext
						.getResponse(), JsonUtil.collection2Json(modTree));
			}
		}
		return SUCCESS;
	}

	/**
	 * 加入开发者
	 * 
	 * @return
	 */
	public String doAddDeveloper() {
		try {
			String[] selects = get_selects();
			if (selects != null && selects.length > 0) {
				((ApplicationProcess) process).addDevelopersToApplication(
						get_selects(), getId());
				this.addActionMessage("{*[Add]*}{*[Success]*}");
				return SUCCESS;
			} else {
				throw new Exception("{*[core.domain.notChoose]*}");
			}

		} catch (Exception e) {
			addFieldError("", e.getMessage());
			doListUnjoinedDeveloper();
			return INPUT;
		}
	}

	/**
	 * copy application
	 * 
	 * @return
	 */
	public String doCopy() {
		ParamsTable params = getParams();
		try {
			// new application id
			String application = params.getParameterAsString("content.id");
			// old application id
			String applicationid = params.getParameterAsString("application");
			CopyApplicationProcess process = (CopyApplicationProcess) ProcessFactory.createRuntimeProcess(CopyApplicationProcess.class, application);
			// process.copyComponent(applicationid);
			// process.copyDataSource(applicationid);
			// process.copyExcelConf(applicationid);
			// process.copyModule(applicationid);
			// process.copyMacrolibs(applicationid);
			// process.copyValidatelibs(applicationid);
			// process.copyPage(applicationid);
			// process.copyStatelabel(applicationid);
			// process.copyStylelibs(applicationid);
			// process.copyMenu(applicationid);
			// process.copyRole(applicationid);
			// process.copyHomepage(applicationid);
			// process.copyReminder(applicationid);
			process.copyAll(applicationid);
			this.addActionMessage("{*[Copy_Success]*}");
		} catch (Exception e) {
			addFieldError("1", e.getMessage());
			e.printStackTrace();
			return INPUT;
		}
		return SUCCESS;
	}

	/**
	 * 移除开发者
	 * 
	 * @return
	 */
	public String doRemoveDeveloper() {
		try {
			String[] selects = get_selects();
			if (selects != null && selects.length > 0) {
				((ApplicationProcess) process).removeDevelopersFromApplication(
						get_selects(), getId());
				this.addActionMessage("{*[Remove]*}{*[Success]*}");
				return SUCCESS;
			} else {
				throw new Exception("{*[core.domain.notChoose]*}");
			}
		} catch (Exception e) {
			addFieldError("", e.getMessage());
			doListJoinedDeveloper();
			return INPUT;
		}
	}

	public void set_type(String _type) throws Exception {
		if (_type != null) {
			ApplicationVO vo = (ApplicationVO) this.getContent();
			vo.setType(_type);
		}
	}

	public String get_type() throws Exception {
		ApplicationVO vo = (ApplicationVO) this.getContent();
		return vo != null ? vo.getType() : "";
	}

	public String get_dtsname() {
		String _dtsname = "";
		try {
			DataSourceProcess dp = (DataSourceProcess) ProcessFactory
					.createProcess(DataSourceProcess.class);
			ApplicationVO aVO = (ApplicationVO) this.getContent();
			if (aVO != null) {
				DataSource dts = (DataSource) dp.doView(aVO.getDatasourceid());
				if (dts != null)
					_dtsname = dts.getName();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return _dtsname;
	}

	public String get_appName() {
		String appName = "";
		ApplicationVO cur_app = (ApplicationVO) getContent();
		ApplicationProcess ap;
		try {
			if (cur_app != null) {
				ap = (ApplicationProcess) ProcessFactory
						.createProcess(ApplicationProcess.class);
				ApplicationVO appvo = (ApplicationVO) ap
						.doView(cur_app.getId());
				if (appvo != null)
					appName = appvo.getName();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return appName;
	}

	public String get_datasourceid() {
		String dtsid = "";
		ApplicationVO cur_app = (ApplicationVO) getContent();
		ApplicationProcess ap;
		try {
			if (cur_app != null) {
				ap = (ApplicationProcess) ProcessFactory
						.createProcess(ApplicationProcess.class);
				ApplicationVO appvo = (ApplicationVO) ap
						.doView(cur_app.getId());
				if (appvo != null)
					dtsid = appvo.getDatasourceid();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dtsid;
	}

	public String doAddApp() throws Exception {
		ApplicationProcess process = (ApplicationProcess) ProcessFactory
				.createProcess(ApplicationProcess.class);

		ParamsTable params = getParams();
		setDatas(process.getUnjoinApplication(params));
		return SUCCESS;
	}

	/**
	 * 2.6版本新增
	 * 
	 * @return
	 */
	public String getOverviewFile() {
		return overviewFile;
	}

	/**
	 * 2.6版本新增
	 * 
	 * @param overviewFile
	 */
	public void setOverviewFile(String overviewFile) {
		this.overviewFile = overviewFile;
	}

	/**
	 * 2.6版本新增
	 * 
	 * @return
	 */
	public InputStream getOverview() {
		if (this.overviewFile != null) {
			final URL url = ApplicationAction.class.getClassLoader()
					.getResource(this.overviewFile);
			if (url != null) {
				try {
					return url.openStream();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					new Thread(new Runnable() {
						public void run() {
							File file = new File(url.getFile());
							while (file.exists()) {
								if (file.delete()) {
									break;
								}
							}
						}
					}).start();
				}
			}
		}
		return null;
	}

	/**
	 * 2.6版本新增
	 * 
	 * @return
	 * @throws Exception
	 */
	public String doCreateOverview() {
		try {
			this.overviewFile = "appOverview" + System.currentTimeMillis()
					+ ".pdf";
			if (!StringUtil.isBlank(this.id)
					&& !StringUtil.isBlank(overviewFile)) {
				ApplicationUtil.createOverview(this.id, overviewFile);
			}
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			return INPUT;
		}
	}
}
