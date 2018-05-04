package OLink.bpm.core.expimp.exp.ejb;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import OLink.bpm.constans.Environment;
import OLink.bpm.core.deploy.application.ejb.ApplicationProcess;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.core.deploy.module.ejb.ModuleProcess;
import OLink.bpm.core.deploy.module.ejb.ModuleVO;
import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSource;
import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSourceProcess;
import OLink.bpm.core.dynaform.dts.excelimport.config.ejb.IMPMappingConfigProcess;
import OLink.bpm.core.dynaform.dts.excelimport.config.ejb.IMPMappingConfigVO;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.dynaform.printer.ejb.Printer;
import OLink.bpm.core.dynaform.printer.ejb.PrinterProcess;
import OLink.bpm.core.dynaform.summary.ejb.SummaryCfgProcess;
import OLink.bpm.core.dynaform.summary.ejb.SummaryCfgVO;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.dynaform.view.ejb.ViewProcess;
import OLink.bpm.core.expimp.ExpImpElements;
import OLink.bpm.core.macro.repository.ejb.RepositoryProcess;
import OLink.bpm.core.macro.repository.ejb.RepositoryVO;
import OLink.bpm.core.multilanguage.ejb.MultiLanguage;
import OLink.bpm.core.multilanguage.ejb.MultiLanguageProcess;
import OLink.bpm.core.page.ejb.Page;
import OLink.bpm.core.page.ejb.PageProcess;
import OLink.bpm.core.permission.ejb.PermissionVO;
import OLink.bpm.core.privilege.res.ejb.ResProcess;
import OLink.bpm.core.privilege.res.ejb.ResVO;
import OLink.bpm.core.report.crossreport.definition.ejb.CrossReportProcess;
import OLink.bpm.core.report.crossreport.definition.ejb.CrossReportVO;
import OLink.bpm.core.resource.ejb.ResourceProcess;
import OLink.bpm.core.resource.ejb.ResourceVO;
import OLink.bpm.core.role.ejb.RoleProcess;
import OLink.bpm.core.role.ejb.RoleVO;
import OLink.bpm.core.task.ejb.Task;
import OLink.bpm.core.task.ejb.TaskProcess;
import OLink.bpm.core.user.ejb.UserDefined;
import OLink.bpm.core.user.ejb.UserDefinedProcess;
import OLink.bpm.core.validate.repository.ejb.ValidateRepositoryProcess;
import OLink.bpm.core.validate.repository.ejb.ValidateRepositoryVO;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiProcess;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiVO;
import OLink.bpm.util.DateUtil;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.file.ZipUtil;
import OLink.bpm.util.property.DefaultProperty;
import OLink.bpm.util.xml.XmlUtil;
import OLink.bpm.core.dynaform.form.ejb.Form;
import org.apache.log4j.Logger;

public class ExpProcessBean extends AbstractDesignTimeProcessBean<ExpSelect>
		implements ExpProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4118898639932846216L;

	final static Logger LOG = Logger.getLogger(ExpProcessBean.class);

	ApplicationProcess applicationProcess;
	ModuleProcess moduleProcess;
	// -软件类
	private RoleProcess roleProcess;
	private PageProcess pageProcess;
	private UserDefinedProcess homePageProcess;
	private ResourceProcess resourceProcess;
	private TaskProcess taskProcess;
	private RepositoryProcess repositoryProcess;
	private ValidateRepositoryProcess validateRepositoryProcess;
	private IMPMappingConfigProcess excelMappingConfigProcess;
	private SummaryCfgProcess reminderProcess;
	private DataSourceProcess dataSourceProcess;
	private MultiLanguageProcess multiLanguageProcess;
	// --权限类
	private ResProcess resProcess;

	// -模块类
	private FormProcess formProcess;
	private ViewProcess viewProcess;
	private BillDefiProcess billDefiProcess;
	private CrossReportProcess crossReportProcess;
	private PrinterProcess printerProcess;

	public ExpProcessBean() {
		try {
			formProcess = (FormProcess) ProcessFactory
					.createProcess(FormProcess.class);
			pageProcess = (PageProcess) ProcessFactory
					.createProcess(PageProcess.class);
			homePageProcess = (UserDefinedProcess) ProcessFactory
					.createProcess(UserDefinedProcess.class);
			viewProcess = (ViewProcess) ProcessFactory
					.createProcess(ViewProcess.class);
			billDefiProcess = (BillDefiProcess) ProcessFactory
					.createProcess(BillDefiProcess.class);
			roleProcess = (RoleProcess) ProcessFactory
					.createProcess(RoleProcess.class);
			applicationProcess = (ApplicationProcess) ProcessFactory
					.createProcess(ApplicationProcess.class);
			moduleProcess = (ModuleProcess) ProcessFactory
					.createProcess(ModuleProcess.class);
			resourceProcess = (ResourceProcess) ProcessFactory
					.createProcess(ResourceProcess.class);
			repositoryProcess = (RepositoryProcess) ProcessFactory
					.createProcess(RepositoryProcess.class);
			validateRepositoryProcess = (ValidateRepositoryProcess) ProcessFactory
					.createProcess(ValidateRepositoryProcess.class);
			excelMappingConfigProcess = (IMPMappingConfigProcess) ProcessFactory
					.createProcess(IMPMappingConfigProcess.class);
			reminderProcess = (SummaryCfgProcess) ProcessFactory
					.createProcess(SummaryCfgProcess.class);
			taskProcess = (TaskProcess) ProcessFactory
					.createProcess(TaskProcess.class);
			crossReportProcess = (CrossReportProcess) ProcessFactory
					.createProcess(CrossReportProcess.class);
			dataSourceProcess = (DataSourceProcess) ProcessFactory
					.createProcess(DataSourceProcess.class);
			printerProcess = (PrinterProcess) ProcessFactory
					.createProcess(PrinterProcess.class);
			multiLanguageProcess = (MultiLanguageProcess) ProcessFactory
			.createProcess(MultiLanguageProcess.class);

			resProcess = (ResProcess) ProcessFactory
					.createProcess(ResProcess.class);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	void addApplicationElements(ExpImpElements elements,
			ApplicationVO application) throws Exception {
		Collection<ResVO> allRes = resProcess.doSimpleQuery(null, application
				.getId()); // 资源
		// 处理应用子元素
		Collection<RoleVO> allRoles = roleProcess.doSimpleQuery(null,
				application.getId()); // 角色
		Collection<ResourceVO> resourceList = resourceProcess.doSimpleQuery(
				null, application.getId()); // 菜单资源
		Collection<ResourceVO> allResources = resourceProcess
				.deepSearchResouece(resourceList, null, "", Integer.MAX_VALUE);
		Collection<PermissionVO> allPermissions = new ArrayList<PermissionVO>(); // 角色菜单权限
		for (Iterator<RoleVO> iterator = allRoles.iterator(); iterator
				.hasNext();) {
			RoleVO role = iterator.next();
			allPermissions.addAll(role.getPermission());
		}
		Collection<RepositoryVO> allRepositories = repositoryProcess
				.doSimpleQuery(null, application.getId()); // 函数库
		Collection<ValidateRepositoryVO> allValidateRepositories = validateRepositoryProcess
				.doSimpleQuery(null, application.getId()); // 校验库
		Collection<IMPMappingConfigVO> allExcelMappingConfigs = excelMappingConfigProcess
				.doSimpleQuery(null, application.getId()); // Excel导入配置
		Collection<SummaryCfgVO> allReminders = reminderProcess.doSimpleQuery(null,
				application.getId()); // 提醒
		Collection<Page> allPages = pageProcess.doSimpleQuery(null, application
				.getId());
		Collection<UserDefined> allUserDefineds = homePageProcess.doSimpleQuery(null,
				application.getId());
		Collection<DataSource> allDataSource = dataSourceProcess.doSimpleQuery(
				null, application.getId());// 数据源
		Collection<Task> allTasks = taskProcess.
				doSimpleQuery(null, application.getId());  // 定时任务
		Collection<MultiLanguage> allMultiLanguages = multiLanguageProcess.doSimpleQuery(
				null, application.getId());// 多语言

		elements.setRes(allRes);
		elements.setRoles(allRoles);
		elements.setResources(allResources);
		elements.setPermissions(allPermissions);
		elements.setRepositories(allRepositories);
		elements.setValidateRepositories(allValidateRepositories);
		elements.setExcelMappingConfigs(allExcelMappingConfigs);
		elements.setReminders(allReminders);
		elements.setPages(allPages);
		elements.setUserDefineds(allUserDefineds);
		elements.setDataSource(allDataSource);
		elements.setTasks(allTasks);
		elements.setMultiLanguage(allMultiLanguages);
	}

	void addModulesElements(ExpImpElements elements,
			Collection<ModuleVO> allModules) throws Exception {
		Collection<Form> allForms = new ArrayList<Form>(); // 表单
		Collection<View> allViews = new ArrayList<View>(); // 视图
		Collection<BillDefiVO> allWorkflows = new ArrayList<BillDefiVO>(); // 流程
		Collection<CrossReportVO> allCrossReports = new ArrayList<CrossReportVO>(); // 交叉报表定制
		Collection<Printer> allPrinters = new ArrayList<Printer>();// 打印配置

		for (Iterator<ModuleVO> iterator = allModules.iterator(); iterator
				.hasNext();) {
			ModuleVO module = iterator.next();

			ParamsTable params = new ParamsTable();
			params.setParameter("t_module", module.getId());
			Collection<Form> forms = formProcess.doSimpleQuery(params);
			allForms.addAll(forms);

			Collection<View> views = viewProcess.doSimpleQuery(params);
			allViews.addAll(views);

			Collection<BillDefiVO> workflows = billDefiProcess
					.doSimpleQuery(params);
			allWorkflows.addAll(workflows);

			Collection<CrossReportVO> crossReports = crossReportProcess
					.doSimpleQuery(params);
			allCrossReports.addAll(crossReports);

			Collection<Printer> printers = printerProcess.doSimpleQuery(params);
			allPrinters.addAll(printers);
			

		}

		elements.setModules(allModules);
		elements.setForms(allForms);
		elements.setViews(allViews);
		elements.setWorkflows(allWorkflows);
		elements.setCrossReports(allCrossReports);
		elements.setPrinter(allPrinters);

	}

	void addSelectElements(ExpImpElements elements, ExpSelect select)
			throws Exception {
		Collection<Form> allForms = new ArrayList<Form>(); // 表单
		Collection<View> allViews = new ArrayList<View>(); // 视图
		Collection<BillDefiVO> allWorkflows = new ArrayList<BillDefiVO>(); // 流程
		Collection<CrossReportVO> allCrossReports = new ArrayList<CrossReportVO>(); // 交叉报表定制
		Collection<Printer> allPrinters = new ArrayList<Printer>(); // 打印配置

		String[] forms = select.getForms();
		for (int i = 0; i < forms.length; i++) {
			Form form = (Form) formProcess.doView(forms[i]);
			allForms.add(form);
		}

		String[] views = select.getViews();
		for (int i = 0; i < views.length; i++) {
			View view = (View) viewProcess.doView(views[i]);
			allViews.add(view);
		}

		String[] workflows = select.getWorkflows();
		for (int i = 0; i < workflows.length; i++) {
			BillDefiVO workflow = (BillDefiVO) billDefiProcess
					.doView(workflows[i]);
			allWorkflows.add(workflow);
		}

		String[] crossReports = select.getCrossReports();
		for (int i = 0; i < crossReports.length; i++) {
			CrossReportVO crossReportVO = (CrossReportVO) crossReportProcess
					.doView(crossReports[i]);
			allCrossReports.add(crossReportVO);
		}

		String[] printers = select.getPrinters();
		for (int i = 0; i < printers.length; i++) {
			Printer printer = (Printer) printerProcess.doView(printers[i]);
			allPrinters.add(printer);
		}

		elements.setForms(allForms);
		elements.setViews(allViews);
		elements.setWorkflows(allWorkflows);
		elements.setCrossReports(allCrossReports);
		elements.setPrinter(allPrinters);
	}

	/**
	 * 导出应用所有子元素的XML文件,包含（角色,菜单资源,角色菜单权限,函数库,校验库,Excel导入配置,提醒,模块,表单,视图）
	 * 
	 * @param select
	 *            选择的应用、模块或元素等
	 * @param fileName
	 *            文件保存路径
	 * @return
	 * @throws Exception
	 */
	public File createZipFile(ExpSelect select, String fileName)
			throws Exception {
		File[] tobeZippedFiles = new File[1];
		ExpImpElements elements = new ExpImpElements();
		elements.setExportType(select.getExportType());

		Collection<ModuleVO> allModules = null; // 模块

		ApplicationVO application = (ApplicationVO) applicationProcess
				.doView(select.getApplicationid());

		Collection<ModuleVO> moduleList = null;
		ModuleVO moduleVO = null;
		String xmlFileName = "";

		switch (select.getExportType()) {
		case ExpSelect.EXPROT_TYPE_APPLICATION:
			addApplicationElements(elements, application);
			// 处理模块及其子元素
			moduleList = moduleProcess.doSimpleQuery(null, application.getId());
			allModules = moduleProcess.deepSearchModule(moduleList, null, "",
					Integer.MAX_VALUE);
			addModulesElements(elements, allModules);
			xmlFileName = application.getName();
			break;
		case ExpSelect.EXPROT_TYPE_MODULE:
			moduleVO = (ModuleVO) moduleProcess.doView(select.getModuleid());
			moduleList = moduleProcess.doSimpleQuery(null, application.getId());
			allModules = moduleProcess.deepSearchModule(moduleList, moduleVO,
					null, Integer.MAX_VALUE);
			addModulesElements(elements, allModules);
			xmlFileName = moduleVO.getName();
			break;
		case ExpSelect.EXPROT_TYPE_MODULE_ELEMENTS:
			moduleVO = (ModuleVO) moduleProcess.doView(select.getModuleid());
			addSelectElements(elements, select);
			xmlFileName = moduleVO.getName();
			break;
		default:
			break;
		}

		tobeZippedFiles[0] = getXmlFile(elements, xmlFileName, 0);

		File archiveFile = new File(fileName);
		LOG.info("ZipFileName------------>" + archiveFile.getAbsolutePath());
		ZipUtil.createZipArchive(archiveFile, tobeZippedFiles);

		return archiveFile;
	}

	/**
	 * 导出应用所有子元素的XML文件,包含（角色,菜单资源,角色菜单权限,函数库,校验库,Excel导入配置,提醒,模块,表单,视图）
	 * 
	 * @param applicationId
	 * @return
	 * @throws Exception
	 */
	public File createZipFile(ExpSelect select) throws Exception {
		String fileName = DateUtil.getCurDateStr("yyyyMMddhhmmss") + ".zip";
		return createZipFile(select, getRealFileName(fileName));
	}

	File getXmlFile(Object obj, String name, int version) throws Exception {
		String fileName = name + "-" + version + ".xml";
		return XmlUtil.toXmlFile(obj, getRealFileName(fileName));
	}

	String getRealFileName(String fileName) throws Exception {
		String exportDir = DefaultProperty.getProperty("EXPORT_PATH");
		String fullFileName = Environment.getInstance().getRealPath(
				exportDir + fileName);
		return fullFileName;
	}

	public File getExportFile(String fileName) throws Exception {
		String filepath = DefaultProperty.getProperty("EXPORT_PATH");
		String realpath = Environment.getInstance().getRealPath(
				filepath + "/" + fileName);
		File exportFile = new File(realpath);
		if (exportFile.exists()) {
			return exportFile;
		} else {
			return null;
		}

	}

	protected IDesignTimeDAO<ExpSelect> getDAO() throws Exception {
		return null;
	}
}
