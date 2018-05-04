package OLink.bpm.core.deploy.application.ejb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.counter.ejb.CounterProcess;
import OLink.bpm.core.counter.ejb.CounterProcessBean;
import OLink.bpm.core.deploy.application.runtime.CopyApplicationThread;
import OLink.bpm.core.deploy.module.ejb.ModuleProcess;
import OLink.bpm.core.deploy.module.ejb.ModuleVO;
import OLink.bpm.core.dynaform.component.ejb.Component;
import OLink.bpm.core.dynaform.component.ejb.ComponentProcess;
import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSource;
import OLink.bpm.core.dynaform.dts.excelimport.config.ejb.IMPMappingConfigProcess;
import OLink.bpm.core.dynaform.dts.excelimport.config.ejb.IMPMappingConfigVO;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.dynaform.view.ejb.ViewProcess;
import OLink.bpm.core.homepage.ejb.Reminder;
import OLink.bpm.core.homepage.ejb.ReminderProcess;
import OLink.bpm.core.macro.repository.ejb.RepositoryVO;
import OLink.bpm.core.page.ejb.PageProcess;
import OLink.bpm.core.permission.ejb.PermissionProcess;
import OLink.bpm.core.permission.ejb.PermissionVO;
import OLink.bpm.core.resource.ejb.ResourceVO;
import OLink.bpm.core.role.ejb.RoleProcess;
import OLink.bpm.core.role.ejb.RoleVO;
import OLink.bpm.core.style.repository.ejb.StyleRepositoryProcess;
import OLink.bpm.core.style.repository.ejb.StyleRepositoryVO;
import OLink.bpm.core.task.ejb.Task;
import OLink.bpm.core.task.ejb.TaskProcess;
import OLink.bpm.core.user.ejb.UserDefined;
import OLink.bpm.core.user.ejb.UserDefinedProcess;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.core.validate.repository.ejb.ValidateRepositoryVO;
import OLink.bpm.core.workflow.statelabel.ejb.StateLabel;
import OLink.bpm.core.workflow.statelabel.ejb.StateLabelProcess;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiVO;
import OLink.bpm.util.ElementResplaceUtil;
import OLink.bpm.util.ObjectUtil;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.page.ejb.Page;
import OLink.bpm.core.resource.ejb.ResourceProcess;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import OLink.bpm.core.macro.repository.ejb.RepositoryProcess;
import OLink.bpm.core.validate.repository.ejb.ValidateRepositoryProcess;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiProcess;
import eWAP.core.Tools;

import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSourceProcess;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;

public class CopyApplicationProcessBean extends AbstractDesignTimeProcessBean<ApplicationVO> implements
		CopyApplicationProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 573848857517354744L;

	// 新应用的applicationid
	private String applicationId;

	private StateLabelProcess stateLabelProcess;

	private IMPMappingConfigProcess mappingConfig;

	private StyleRepositoryProcess styleRepositoryProcess;

	private ReminderProcess reminderProcess;

	private PageProcess pageProcess;

	private UserDefinedProcess homePageProcess;

	private ComponentProcess componentProcess;

	private ValidateRepositoryProcess validateRepositoryProcess;

	private RoleProcess roleProcess;

	private DataSourceProcess dataSourceProcess;

	private ResourceProcess resourceProcess;

	private RepositoryProcess repositoryProcess;

	private ModuleProcess modulePorcess;

	Map<String, String> remindermap = null; // 保存reminder的id key: 旧id value ：
	// 新id

	Map<String, String> formMap = null;// 保存from的id key: 旧id value ： 新id

	Map<String, String> viewMap = null;// 保存view的id key: 旧id value ： 新id

	Map<String, String> flowMap = null; // 保存flow的id key: 旧id value ： 新id

	Map<String, String> moduleMap = null; // 保存flow的id key: 旧id value ： 新id

	Map<String, String> resourceMap = null;

	Map<String, String> homepageMap = null;

	Map<String, String> styleMap = null;

	public void setProcess() throws Exception {
		stateLabelProcess = (StateLabelProcess) ProcessFactory.createProcess(StateLabelProcess.class);
		mappingConfig = (IMPMappingConfigProcess) ProcessFactory.createProcess(IMPMappingConfigProcess.class);
		styleRepositoryProcess = (StyleRepositoryProcess) ProcessFactory.createProcess(StyleRepositoryProcess.class);
		reminderProcess = (ReminderProcess) ProcessFactory.createProcess(ReminderProcess.class);
		modulePorcess = (ModuleProcess) ProcessFactory.createProcess(ModuleProcess.class);
		pageProcess = (PageProcess) ProcessFactory.createProcess(PageProcess.class);
		homePageProcess = (UserDefinedProcess) ProcessFactory.createProcess(UserDefinedProcess.class);
		validateRepositoryProcess = (ValidateRepositoryProcess) ProcessFactory
				.createProcess(ValidateRepositoryProcess.class);
		roleProcess = (RoleProcess) ProcessFactory.createProcess(RoleProcess.class);
		resourceProcess = (ResourceProcess) ProcessFactory.createProcess(ResourceProcess.class);
		dataSourceProcess = (DataSourceProcess) ProcessFactory.createProcess(DataSourceProcess.class);
		repositoryProcess = (RepositoryProcess) ProcessFactory.createProcess(RepositoryProcess.class);
		componentProcess = (ComponentProcess) ProcessFactory.createProcess(ComponentProcess.class);
		modulePorcess = (ModuleProcess) ProcessFactory.createProcess(ModuleProcess.class);

	}

	public CopyApplicationProcessBean(String applicationId) throws Exception {
		this.applicationId = applicationId;
		setProcess();
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public void copyAll(String applicationid) throws Exception {
		new CopyApplicationThread(applicationid, getApplicationId()).start();
	}

	public void copyComponent(String applicationid) throws Exception {
		Collection<Component> comList = componentProcess.doSimpleQuery(null, applicationid);
		if (comList != null && comList.size() > 0) {
			for (Iterator<Component> iterator = comList.iterator(); iterator.hasNext();) {
				Component component = iterator.next();
				if (component != null) {
					Component copyComponent = new Component();
					ObjectUtil.copyProperties(copyComponent, component);
					copyComponent.setId(Tools.getSequence());
					int conut = getCount(component.getName(), getApplicationId(), component.getDomainid());
					copyComponent.setName(component.getName() + "_copy" + conut);
					copyComponent.setApplicationid(getApplicationId());
					if (styleMap != null && styleMap.size() > 0) {
						for (Iterator<Map.Entry<String, String>> iterator2 = styleMap.entrySet().iterator(); iterator2
								.hasNext();) {
							Map.Entry<String, String> style = iterator2.next();
							if (component.getStyle().getId().equals(style.getKey())) {
								StyleRepositoryVO styleVO = (StyleRepositoryVO) styleRepositoryProcess
										.doView(style.getValue());
								copyComponent.setStyle(styleVO);
							}
						}
					}
					componentProcess.doCreate(copyComponent);
				}
			}
		}

	}

	public void copyDataSource(String applicationid) throws Exception {
		Collection<DataSource> dataSourceList = dataSourceProcess.doSimpleQuery(null, applicationid);
		if (dataSourceList != null && dataSourceList.size() > 0) {
			for (Iterator<DataSource> iterator = dataSourceList.iterator(); iterator.hasNext();) {
				DataSource dataSource = iterator.next();
				if (dataSource != null) {
					DataSource copiesDataSource = new DataSource();
					ObjectUtil.copyProperties(copiesDataSource, dataSource);
					copiesDataSource.setId(Tools.getSequence());
					copiesDataSource.setApplicationid(getApplicationId());
					int count = getCount(dataSource.getName(), getApplicationId(), dataSource.getDomainid());
					copiesDataSource.setName(dataSource.getName() + "_copy" + count);
					dataSourceProcess.doCreate(copiesDataSource);
				}
			}
		}
	}

	public void copyExcelConf(String applicationid) throws Exception {
		Collection<IMPMappingConfigVO> mappingConfigList = mappingConfig.doSimpleQuery(null, applicationid);
		if (mappingConfigList != null) {
			for (Iterator<IMPMappingConfigVO> iterator = mappingConfigList.iterator(); iterator.hasNext();) {
				IMPMappingConfigVO mapping = iterator.next();
				if (mapping != null) {
					IMPMappingConfigVO copiesMapping = new IMPMappingConfigVO();
					ObjectUtil.copyProperties(copiesMapping, mapping);
					copiesMapping.setId(Tools.getSequence());
					copiesMapping.setApplicationid(getApplicationId());
					int count = getCount(mapping.getName(), getApplicationId(), mapping.getDomainid());
					copiesMapping.setName(mapping.getName() + "_copy" + count);
					mappingConfig.doCreate(copiesMapping);
				}
			}
		}

	}

	/*
	 * copy Homepage (non-Javadoc)
	 * 
	 * @see CopyApplicationProcess#copyHomepage
	 *      (java.lang.String)
	 */
	public void copyHomepage(String applicationid) throws Exception {
		homepageMap = new HashMap<String, String>();
		Set<Reminder> reminders = new HashSet<Reminder>();
		Collection<UserDefined> homepageList = homePageProcess.doSimpleQuery(null, applicationid);
		if (homepageList != null) {
			for (Iterator<UserDefined> iterator = homepageList.iterator(); iterator.hasNext();) {
				UserDefined homepage = iterator.next();
				if (homepage != null) {
					UserDefined copiesUserDefined = new UserDefined();
					copiesUserDefined.setApplicationid(getApplicationId());
					copiesUserDefined.setId(Tools.getSequence());
					int count = getCount(homepage.getName(), getApplicationId(), homepage.getDomainid());
					copiesUserDefined.setName(homepage.getName() + "_copy" + count);
					copiesUserDefined.setDescription(homepage.getDescription());
					copiesUserDefined.setLayoutType(homepage.getLayoutType());
					copiesUserDefined.setPublished(homepage.getPublished());
					if (remindermap != null && remindermap.size() > 0) {
						for (Iterator<Map.Entry<String, String>> iter = remindermap.entrySet().iterator(); iter
								.hasNext();) {
							Map.Entry<String, String> entry = iter.next();
							String key = entry.getKey();
							String value = entry.getValue();
							Collection<Reminder> reminderList = homepage.getReminders();
							for (Iterator<Reminder> iterator2 = reminderList.iterator(); iterator2.hasNext();) {
								Reminder reminder = iterator2.next();
								if (reminder.getId().equals(key)) {
									Reminder rem = (Reminder) reminderProcess.doView(value);
									reminders.add(rem);
								}
							}

						}
					}
					copiesUserDefined.setReminders(reminders);
					homepageMap.put(homepage.getId(), copiesUserDefined.getId());
					homePageProcess.doCreate(copiesUserDefined);
				}
			}
		}

	}

	public void copyMacrolibs(String applicationid) throws Exception {
		Collection<RepositoryVO> macList = repositoryProcess.doSimpleQuery(null, applicationid);
		if (macList != null) {
			for (Iterator<RepositoryVO> iterator = macList.iterator(); iterator.hasNext();) {
				RepositoryVO vo = iterator.next();
				if (vo != null) {
					RepositoryVO copiesVo = new RepositoryVO();
					ObjectUtil.copyProperties(copiesVo, vo);
					copiesVo.setApplicationid(getApplicationId());
					copiesVo.setId(Tools.getSequence());
					int count = getCount(vo.getName(), getApplicationId(), vo.getDomainid());
					copiesVo.setName(vo.getName() + "_copy" + count);
					repositoryProcess.doCreate(copiesVo);
				}

			}
		}
	}

	public void copyMenu(String applicationid) throws Exception {
		resourceMap = new HashMap<String, String>();
		Collection<ResourceVO> resourceList = resourceProcess.doSimpleQuery(null, applicationid);
		if (resourceList != null) {
			for (Iterator<ResourceVO> iterator = resourceList.iterator(); iterator.hasNext();) {
				ResourceVO resource = iterator.next();
				if (resource != null) {
					ResourceVO copiesResource = new ResourceVO();
					ObjectUtil.copyProperties(copiesResource, resource);
					copiesResource.setId(Tools.getSequence());
					copiesResource.setApplicationid(getApplicationId());
					copiesResource.setApplication(getApplicationId());
					int aDescription = getCount(resource.getDescription(), getApplicationId(), resource.getDomainid());
					copiesResource.setDescription(resource.getDescription() + "_copy" + aDescription);
					Set<PermissionVO> relatedPermissions = new HashSet<PermissionVO>();
					relatedPermissions.addAll(resource.getRelatedPermissions());
					copiesResource.setRelatedPermissions(relatedPermissions);
					// 当类型为视图的时候,替换resource的菜单下的视图
					resourceProcess.doCreate(copiesResource);
					resourceMap.put(resource.getId(), copiesResource.getId());
				}
			}
			ElementResplaceUtil util = new ElementResplaceUtil(formMap, viewMap, flowMap, moduleMap, styleMap,
					resourceMap);
			util.resplaceResource(getApplicationId());
		}
	}

	public void copyPage(String applicationid) throws Exception {
		Collection<Page> pageList = pageProcess.doSimpleQuery(null, applicationid);
		if (pageList != null) {
			for (Iterator<Page> iterator = pageList.iterator(); iterator.hasNext();) {
				Page page = iterator.next();
				if (page != null) {
					Page copiesPage = new Page();
					ObjectUtil.copyProperties(copiesPage, page);
					copiesPage.setApplicationid(getApplicationId());
					copiesPage.setId(Tools.getSequence());
					int count = getCount(page.getName(), getApplicationId(), page.getDomainid());
					copiesPage.setName(page.getName() + "_copy" + count);
					if (styleMap != null && styleMap.size() > 0) {
						for (Iterator<Map.Entry<String, String>> iterator2 = styleMap.entrySet().iterator(); iterator2
								.hasNext();) {
							Map.Entry<String, String> style = iterator2.next();
							if (page.getStyle().getId().equals(style.getKey())) {
								StyleRepositoryVO styleVO = (StyleRepositoryVO) styleRepositoryProcess
										.doView(style.getValue());
								copiesPage.setStyle(styleVO);
							}
						}
					}
					pageProcess.doCreate(copiesPage);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see CopyApplicationProcess#copyReminder
	 *      (java.lang.String)
	 */
	public void copyReminder(String applicationid) throws Exception {
		remindermap = new HashMap<String, String>();
		Collection<Reminder> reminderList = reminderProcess.doSimpleQuery(null, applicationid);
		if (reminderList != null) {
			for (Iterator<Reminder> iterator = reminderList.iterator(); iterator.hasNext();) {
				Reminder reminder = iterator.next();
				if (reminder != null) {
					Reminder copiesReminder = new Reminder();
					ObjectUtil.copyProperties(copiesReminder, reminder);
					copiesReminder.setApplicationid(getApplicationId());
					copiesReminder.setId(Tools.getSequence());
					int count = getCount(reminder.getTitle(), getApplicationId(), reminder.getDomainid());
					copiesReminder.setTitle(reminder.getTitle() + "_copy" + count);
					if (reminder.getHomepage() != null) {
						for (Iterator<Map.Entry<String, String>> iterator2 = homepageMap.entrySet().iterator(); iterator2
								.hasNext();) {
							Map.Entry<String, String> entry = iterator2.next();
							String key = entry.getKey();
							String value = entry.getValue();
							UserDefined Homepage = reminder.getUserDefined();
							if (Homepage.getId().equals(key)) {
								UserDefined homepages = (UserDefined) homePageProcess.doView(value);
								copiesReminder.setUserDefined(homepages);
							}
						}
					}
					remindermap.put(reminder.getId(), copiesReminder.getId());
					reminderProcess.doCreate(copiesReminder);
					ElementResplaceUtil util = new ElementResplaceUtil(formMap, viewMap, flowMap, moduleMap, styleMap,
							resourceMap);
					util.resplaceReminder(getApplicationId());
				}
			}
		}
	}

	/*
	 * (non-Javadoc) copy role
	 * 
	 * @see CopyApplicationProcess#copyRole
	 *      (java.lang.String)
	 */
	public void copyRole(String applicationid) throws Exception {
		Set<PermissionVO> permission = new HashSet<PermissionVO>();
		Collection<RoleVO> roleList = roleProcess.getRolesByApplication(applicationid);
		if (roleList != null) {
			for (Iterator<RoleVO> iterator = roleList.iterator(); iterator.hasNext();) {
				RoleVO role = iterator.next();
				if (role != null) {
					RoleVO copiesRole = new RoleVO();
					ObjectUtil.copyProperties(copiesRole, role);
					copiesRole.setApplicationid(getApplicationId());
					copiesRole.setId(Tools.getSequence());
					copiesRole.setPermission(role.getPermission());
					int count = getCount(role.getName(), getApplicationId(), role.getDomainid());
					copiesRole.setName(role.getName() + "_copy" + count);
					Collection<UserVO> users = new HashSet<UserVO>();
					users.addAll(role.getUsers());
					copiesRole.setUsers(users);
					copiesRole.setPermission(permission);
					roleProcess.doCreate(copiesRole);
					resplace(role.getId(), copiesRole.getId());
				}
			}
		}

	}

	public void resplace(String role, String copiesrole) throws Exception {
		Set<PermissionVO> permission = new HashSet<PermissionVO>();
		RoleVO vo = (RoleVO) roleProcess.doView(role);
		RoleVO copiesVO = (RoleVO) roleProcess.doView(copiesrole);
		Collection<PermissionVO> permissions = vo.getPermission();
		if (permissions != null && permissions.size() > 0) {
			for (Iterator<PermissionVO> iterator2 = permissions.iterator(); iterator2.hasNext();) {
				PermissionVO per = iterator2.next();
				String resourceId = per.getResourceId();
				PermissionProcess pree = (PermissionProcess) ProcessFactory.createProcess(PermissionProcess.class);
				for (Iterator<Map.Entry<String, String>> iterator3 = resourceMap.entrySet().iterator(); iterator3
						.hasNext();) {
					Map.Entry<String, String> object = iterator3.next();
					if (object.getKey().equals(resourceId)) {
						PermissionVO copiesper = new PermissionVO();
						copiesper.setResourceId(resourceId);
						copiesper.setId(Tools.getSequence());
						copiesper.setApplicationid(getApplicationId());
						copiesper.setRoleId(copiesVO.getId());
						pree.doCreate(copiesper);
						permission.add(copiesper);
					}
				}
			}
		}
		copiesVO.setPermission(permission);
		roleProcess.doUpdate(copiesVO);
	}

	public void copyStatelabel(String applicationid) throws Exception {
		Collection<StateLabel> statelabelList = stateLabelProcess.doSimpleQuery(null, applicationid);
		if (statelabelList != null) {
			for (Iterator<StateLabel> iterator = statelabelList.iterator(); iterator.hasNext();) {
				StateLabel stateLabel = iterator.next();
				if (stateLabel != null) {
					StateLabel vo = new StateLabel();
					ObjectUtil.copyProperties(vo, stateLabel);
					vo.setId(Tools.getSequence());
					vo.setApplicationid(getApplicationId());
					int count = getCount(stateLabel.getName(), getApplicationId(), stateLabel.getDomainid());
					vo.setName(stateLabel.getName() + "_copy" + count);
					stateLabelProcess.doCreate(vo);
				}

			}
		}
	}

	public void copyStylelibs(String applicationid) throws Exception {
		styleMap = new HashMap<String, String>();
		Collection<StyleRepositoryVO> styleList = styleRepositoryProcess.doSimpleQuery(null, applicationid);
		if (styleList != null) {
			for (Iterator<StyleRepositoryVO> iterator = styleList.iterator(); iterator.hasNext();) {
				StyleRepositoryVO vo = iterator.next();
				if (vo != null) {
					StyleRepositoryVO copiesVO = new StyleRepositoryVO();
					ObjectUtil.copyProperties(copiesVO, vo);
					copiesVO.setApplicationid(getApplicationId());
					copiesVO.setId(Tools.getSequence());
					int count = getCount(vo.getName(), getApplicationId(), vo.getDomainid());
					copiesVO.setName(vo.getName() + "_copy" + count);
					styleRepositoryProcess.doCreate(copiesVO);
					styleMap.put(vo.getId(), copiesVO.getId());
				}
			}
		}
	}

	/**
	 * 复制定时任务
	 * 
	 * @param applicationid
	 * @param moduleid
	 * @param copiesmoduleId
	 * @throws Exception
	 */
	public void copyTask(String applicationid, String moduleid, String copiesmoduleId) throws Exception {
		TaskProcess taskProcess = (TaskProcess) ProcessFactory.createProcess(TaskProcess.class);
		Collection<Task> taskList = taskProcess.getTaskByModule(applicationid, moduleid);
		if (taskList != null) {
			for (Iterator<Task> iterator = taskList.iterator(); iterator.hasNext();) {
				Task task = iterator.next();
				if (task != null) {
					Task copiesTask = new Task();
					ObjectUtil.copyProperties(copiesTask, task);
					copiesTask.setId(Tools.getSequence());
					copiesTask.setApplicationid(getApplicationId());
					copiesTask.setModule(getModule(copiesmoduleId));
					Collection<Integer> daysOfWeek = new ArrayList<Integer>();
					daysOfWeek.addAll(task.getDaysOfWeek());
					copiesTask.setDaysOfWeek(daysOfWeek);
					int count = getCount(task.getName(), getApplicationId(), task.getDomainid());
					copiesTask.setName(task.getName() + "_copy" + count);
					taskProcess.doCreate(copiesTask);
				}

			}
		}
	}

	public void copyValidatelibs(String applicationid) throws Exception {
		Collection<ValidateRepositoryVO> validateList = validateRepositoryProcess.doSimpleQuery(null, applicationid);
		if (validateList != null) {
			for (Iterator<ValidateRepositoryVO> iterator = validateList.iterator(); iterator.hasNext();) {
				ValidateRepositoryVO vo = iterator.next();
				if (vo != null) {
					ValidateRepositoryVO copiesVO = new ValidateRepositoryVO();
					ObjectUtil.copyProperties(copiesVO, vo);
					copiesVO.setApplicationid(getApplicationId());
					copiesVO.setId(Tools.getSequence());
					int count = getCount(vo.getName(), getApplicationId(), vo.getDomainid());
					copiesVO.setName(vo.getName() + "_copy" + count);
					validateRepositoryProcess.doCreate(copiesVO);
				}
			}
		}
	}

	/**
	 * 复制应用中的module
	 * 
	 * @return null
	 * @param applicationid
	 *            应用ID
	 */
	public void copyModule(String applicationid) throws Exception {
		moduleMap = new HashMap<String, String>();
		formMap = new HashMap<String, String>();
		viewMap = new HashMap<String, String>();
		Collection<ModuleVO> moduleList = modulePorcess.doSimpleQuery(null, applicationid);
		ApplicationProcess process = (ApplicationProcess) ProcessFactory.createProcess(ApplicationProcess.class);
		Collection<ValueObject> copy_module = new ArrayList<ValueObject>();
		if (moduleList != null && moduleList.size() > 0) {
			for (Iterator<ModuleVO> iterator = moduleList.iterator(); iterator.hasNext();) {
				ModuleVO module = iterator.next();
				if (module != null) {
					ModuleVO vo = new ModuleVO();
					vo.setApplication((ApplicationVO) process.doView(getApplicationId()));
					vo.setApplicationid(getApplicationId());
					vo.setId(Tools.getSequence());
					vo.setSortId(Tools.getTimeSequence());
					vo.setCommitDate(module.getCommitDate());
					vo.setDescription(module.getDescription());
					vo.setLastmodifytime(module.getLastmodifytime());
					// vo.setSortId(module.getSortId());
					vo.setSuperior(module.getSuperior());
					int count = getCount(module.getName(), getApplicationId(), module.getDomainid());
					vo.setName(module.getName() + "_copy" + count);
					copy_module.add(vo);
					moduleMap.put(module.getId(), vo.getId());
					copyModuleElement(applicationid, module.getId(), vo.getId());
				}
			}
			for (Iterator<ValueObject> iterator = copy_module.iterator(); iterator.hasNext();) {
				ModuleVO module = (ModuleVO) iterator.next();
				ModuleVO superior = module.getSuperior();
				if (superior != null && !StringUtil.isBlank(superior.getId())) {
					for (Iterator<ValueObject> it = copy_module.iterator(); it.hasNext();) {
						ModuleVO m = (ModuleVO) it.next();
						if (m != null && m.getId().equals(moduleMap.get(superior.getId()))) {
							// modulePorcess.doCreate(m);//先保存上级模块
							module.setSuperior(m);
						}
					}
				}
				// modulePorcess.doCreate(module);//保存当前模块
			}
			modulePorcess.doCreate(copy_module);
			ElementResplaceUtil util = new ElementResplaceUtil(formMap, viewMap, flowMap, moduleMap, styleMap,
					resourceMap);
			util.resplace(getApplicationId());
		}
	}

	public void copyModuleElement(String applicationid, String moduleid, String copiesModule) throws Exception {
		copyTask(applicationid, moduleid, copiesModule);
		copyForm(applicationid, moduleid, copiesModule);
		copyView(applicationid, moduleid, copiesModule);
		copyFlow(applicationid, moduleid, copiesModule);
	}

	public void copyForm(String applicationid, String moduleid, String copiesModuleId) throws Exception {
		FormProcess formProcess = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
		Collection<Form> forms = formProcess.getFormsByModule(moduleid, applicationid);
		if (forms != null && forms.size() > 0) {
			for (Iterator<Form> iterator = forms.iterator(); iterator.hasNext();) {
				Form form = iterator.next();
				if (form != null) {
					Form copiesForm = new Form();
					copiesForm.setDomainid(form.getDomainid());
					copiesForm.setLastmodifier(form.getLastmodifier());
					copiesForm.setTemplatecontext(form.getTemplatecontext());
					copiesForm.setShowLog(form.isShowLog());
					copiesForm.setType(form.getType());
					copiesForm.setStyle(form.getStyle());
					copiesForm.setVersion(form.getVersion());
					copiesForm.setActivityXML(form.getActivityXML());
					copiesForm.setApplicationid(getApplicationId());
					copiesForm.setId(Tools.getSequence());
					copiesForm.setRelationName(form.getRelationName());
					copiesForm.setRelationText(form.getRelationText());
					copiesForm.setIsopenablescript(form.getIsopenablescript());
					
					copiesForm.setIseditablescript(form.getIseditablescript());
					
					copiesForm.setModule(getModule(copiesModuleId));
					int count = getCount(form.getName(), getApplicationId(), form.getDomainid());
					copiesForm.setName(form.getName() + "_copy" + count);
					formProcess.doCreate(copiesForm);
					formMap.put(form.getId(), copiesForm.getId());
				}

			}
		}
	}

	public void copyView(String applicationid, String moduleid, String copiesModuleId) throws Exception {
		ViewProcess viewProcess = (ViewProcess) ProcessFactory.createProcess(ViewProcess.class);
		Collection<View> views = viewProcess.getViewsByModule(moduleid, applicationid);
		if (views != null && views.size() > 0) {
			for (Iterator<View> iterator = views.iterator(); iterator.hasNext();) {
				View view = iterator.next();
				if (view != null) {
					View copiesView = new View();
					ObjectUtil.copyProperties(copiesView, view);
					copiesView.setId(Tools.getSequence());
					copiesView.setActivityXML(view.getActivityXML());
					copiesView.setColumnXML(view.getColumnXML());
					copiesView.setApplicationid(getApplicationId());
					copiesView.setModule(getModule(copiesModuleId));
					int count = getCount(view.getName(), getApplicationId(), view.getDomainid());
					copiesView.setName(view.getName() + "_copy" + count);
					viewProcess.doCreate(copiesView);
					viewMap.put(view.getId(), copiesView.getId());
				}

			}
		}
	}

	public void copyFlow(String applicationid, String moduleid, String copiesModuleId) throws Exception {
		flowMap = new HashMap<String, String>();
		BillDefiProcess billProcess = (BillDefiProcess) ProcessFactory.createProcess(BillDefiProcess.class);
		Collection<BillDefiVO> flows = billProcess.getBillDefiByModule(moduleid);
		if (flows != null) {
			for (Iterator<BillDefiVO> iterator = flows.iterator(); iterator.hasNext();) {
				BillDefiVO vo = iterator.next();
				if (vo != null) {
					BillDefiVO copiesVO = new BillDefiVO();
					ObjectUtil.copyProperties(copiesVO, vo);
					copiesVO.setId(Tools.getSequence());
					copiesVO.setApplicationid(getApplicationId());
					int count = getCount(copiesVO.getSubject(), copiesVO.getApplicationid(), copiesVO.getDomainid());
					copiesVO.setSubject(vo.getSubject() + "_copy" + count);
					copiesVO.setModule(getModule(copiesModuleId));
					billProcess.doCreate(copiesVO);
					flowMap.put(vo.getId(), copiesVO.getId());
				}
			}
		}
	}

	public ModuleVO getModule(String pk) throws Exception {
		return (ModuleVO) modulePorcess.doView(pk);
	}

	/**
	 * 计数
	 * 
	 * @param name
	 * @param applicationid
	 * @param domainid
	 * @return
	 * @throws Exception
	 */
	public int getCount(String name, String applicationid, String domainid) throws Exception {
		return getCountProcess(applicationid).getNextValue(name, applicationid, domainid);
	}

	public CounterProcess getCountProcess(String application) throws Exception {
		return new CounterProcessBean(application);
	}

	protected IDesignTimeDAO<ApplicationVO> getDAO() throws Exception {
		return null;
	}

}
