package OLink.bpm.core.expimp.imp.ejb;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.core.deploy.application.ejb.ApplicationProcess;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.core.deploy.module.ejb.ModuleProcess;
import OLink.bpm.core.deploy.module.ejb.ModuleVO;
import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSourceProcess;
import OLink.bpm.core.dynaform.dts.excelimport.config.ejb.IMPMappingConfigProcess;
import OLink.bpm.core.dynaform.form.ejb.BaseFormProcessBean;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.form.ejb.FormProcessBean;
import OLink.bpm.core.dynaform.printer.ejb.Printer;
import OLink.bpm.core.dynaform.printer.ejb.PrinterProcess;
import OLink.bpm.core.dynaform.summary.ejb.SummaryCfgProcess;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.expimp.exp.ejb.ExpSelect;
import OLink.bpm.core.links.ejb.LinkProcess;
import OLink.bpm.core.macro.repository.ejb.RepositoryProcess;
import OLink.bpm.core.multilanguage.ejb.MultiLanguage;
import OLink.bpm.core.page.ejb.Page;
import OLink.bpm.core.report.crossreport.definition.ejb.CrossReportProcess;
import OLink.bpm.core.report.crossreport.definition.ejb.CrossReportVO;
import OLink.bpm.core.resource.ejb.ResourceVO;
import OLink.bpm.core.role.ejb.RoleProcess;
import OLink.bpm.core.style.repository.ejb.StyleRepositoryVO;
import OLink.bpm.core.task.ejb.Task;
import OLink.bpm.core.user.ejb.UserDefinedProcess;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.dynaform.view.ejb.ViewProcess;
import OLink.bpm.core.expimp.ExpImpElements;
import OLink.bpm.core.macro.repository.ejb.RepositoryVO;
import OLink.bpm.core.permission.ejb.PermissionVO;
import OLink.bpm.core.privilege.res.ejb.ResProcess;
import OLink.bpm.core.role.ejb.RoleVO;
import OLink.bpm.core.style.repository.ejb.StyleRepositoryProcess;
import OLink.bpm.core.task.ejb.TaskProcess;
import OLink.bpm.core.validate.repository.ejb.ValidateRepositoryVO;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiProcess;
import org.apache.log4j.Logger;

import OLink.bpm.base.ejb.IDesignTimeProcess;
import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSource;
import OLink.bpm.core.dynaform.dts.excelimport.config.ejb.IMPMappingConfigVO;
import OLink.bpm.core.dynaform.form.ejb.Confirm;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.dynaform.summary.ejb.SummaryCfgVO;
import OLink.bpm.core.dynaform.view.ejb.ViewProcessBean;
import OLink.bpm.core.links.ejb.LinkVO;
import OLink.bpm.core.multilanguage.ejb.MultiLanguageProcess;
import OLink.bpm.core.page.ejb.PageProcess;
import OLink.bpm.core.permission.ejb.PermissionProcess;
import OLink.bpm.core.privilege.res.ejb.ResVO;
import OLink.bpm.core.resource.ejb.ResourceProcess;
import OLink.bpm.core.table.constants.ConfirmConstant;
import OLink.bpm.core.table.model.NeedConfirmException;
import OLink.bpm.core.user.ejb.UserDefined;
import OLink.bpm.core.validate.repository.ejb.ValidateRepositoryProcess;
import OLink.bpm.util.file.ZipUtil;
import eWAP.core.Tools;
import OLink.bpm.util.xml.XmlUtil;

import com.opensymphony.xwork.ValidationAware;
import com.opensymphony.xwork.ValidationAwareSupport;

public class ImpProcessBean implements ImpProcess {
	final static Logger LOG = Logger.getLogger(ImpProcessBean.class);

	FormProcess formProcess;
	ViewProcess viewProcess;
	BillDefiProcess billDefiProcess;
	RoleProcess roleProcess;
	ModuleProcess moduleProcess;
	ResourceProcess resourceProcess;
	RepositoryProcess repositoryProcess;
	ValidateRepositoryProcess validateRepositoryProcess;
	IMPMappingConfigProcess excelMappingConfigProcess;
	SummaryCfgProcess reminderProcess;
	ApplicationProcess applicationProcess;
	PageProcess pageProcess;
	TaskProcess taskProcess;
	CrossReportProcess crossReportProcess;
	DataSourceProcess dataSourceProcess;
	PrinterProcess printerProcess;
	ResProcess resProcess;
	MultiLanguageProcess multiLanguageProcess;

	// Import时需要的Process
	PermissionProcess permissionProcess;
	StyleRepositoryProcess styleProcess;
	UserDefinedProcess homePageProcess;
	LinkProcess linkProcess;

	public ImpProcessBean() {
		try {
			formProcess = new FormProcessBean();
			viewProcess = new ViewProcessBean();
			billDefiProcess = (BillDefiProcess) ProcessFactory
					.createProcess(BillDefiProcess.class);
			roleProcess = (RoleProcess) ProcessFactory
					.createProcess(RoleProcess.class);
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

			homePageProcess = (UserDefinedProcess) ProcessFactory
					.createProcess(UserDefinedProcess.class);
			styleProcess = (StyleRepositoryProcess) ProcessFactory
					.createProcess(StyleRepositoryProcess.class);
			permissionProcess = (PermissionProcess) ProcessFactory
					.createProcess(PermissionProcess.class);
			applicationProcess = (ApplicationProcess) ProcessFactory
					.createProcess(ApplicationProcess.class);
			pageProcess = (PageProcess) ProcessFactory
					.createProcess(PageProcess.class);
			taskProcess = (TaskProcess) ProcessFactory
					.createProcess(TaskProcess.class);
			crossReportProcess = (CrossReportProcess) ProcessFactory
					.createProcess(CrossReportProcess.class);
			dataSourceProcess = (DataSourceProcess) ProcessFactory
					.createProcess(DataSourceProcess.class);
			printerProcess = (PrinterProcess) ProcessFactory
					.createProcess(PrinterProcess.class);
			resProcess = (ResProcess) ProcessFactory
					.createProcess(ResProcess.class);
			multiLanguageProcess = (MultiLanguageProcess) ProcessFactory
			.createProcess(MultiLanguageProcess.class);
			linkProcess = (LinkProcess) ProcessFactory
					.createProcess(LinkProcess.class);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 对导入的文件进行校验和处理
	 * 
	 * @param importFile
	 *            导入文件
	 * @return 校验感知
	 * @throws Exception
	 */
	public ValidationAware doImportValidate(ImpSelect select, File importFile)
			throws Exception {
		ExpImpElements elements = parseFile(importFile);

		ValidationAware vas = new ValidationAwareSupport();

		ApplicationVO application = (ApplicationVO) applicationProcess
				.doView(select.getApplicationid());

		String typeName = ExpSelect.getExportTypeNameMap().get(
				Integer.valueOf(elements.getExportType()));

		boolean flag = false;

		switch (elements.getExportType()) {
		case ExpSelect.EXPROT_TYPE_APPLICATION:
			flag = select.getImportType() != ImpSelect.IMPORT_TYPE_APPLICATION;
			break;
		case ExpSelect.EXPROT_TYPE_MODULE:
			flag = select.getImportType() != ImpSelect.IMPORT_TYPE_MODULE;
			break;
		case ExpSelect.EXPROT_TYPE_MODULE_ELEMENTS:
			flag = select.getImportType() != ImpSelect.IMPORT_TYPE_MODULE_ELEMENTS;
			break;
		default:
			break;
		}
		if (flag) {
			throw new Exception("{*[import.type.not.match]*} (" + typeName
					+ ")");
		}

		for (Iterator<Form> iterator = elements.getForms().iterator(); iterator
				.hasNext();) {
			Form form = iterator.next();
			form.setApplicationid(application.getId());
			try {
				formProcess.doChangeValidate(form);
			} catch (NeedConfirmException e) {
				Collection<Confirm> confirms = e
						.getConfirms();
				for (Iterator<Confirm> iterator2 = confirms.iterator(); iterator2
						.hasNext();) {
					Confirm confirm = iterator2.next();
					if (confirm.getMsgKeyCode() == ConfirmConstant.FIELD_DATA_EXIST) {
						vas.addActionMessage(confirm.getMessage());
					} else {
						vas.addActionError(confirm.getMessage());
					}
				}
			}
		}

		return vas;
	}

	public ExpImpElements parseFile(File importFile) {
		try {
			String[] xmlContents = ZipUtil.readZipFile(importFile);
			if (xmlContents.length > 0) {
				ExpImpElements elements = (ExpImpElements) XmlUtil
						.toOjbect(xmlContents[0]);
				return elements;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void doImport(ImpSelect select, File importFile) throws Exception {
		if (importFile == null) {
			throw new Exception("{*[core.util.cannotsave]*}");
		}

		ExpImpElements elements = parseFile(importFile);

		ApplicationVO application = (ApplicationVO) applicationProcess
				.doView(select.getApplicationid());
		ModuleVO superior = (ModuleVO) moduleProcess.doView(select
				.getModuleid());
		
		// 导入ModuleVO
		for (Iterator<ModuleVO> iterator = elements.getModules().iterator(); iterator
				.hasNext();) {
			ModuleVO module = iterator.next();
			module.setApplication(application);
			module.setApplicationid(application.getId());
			if (select.getImportType() == ImpSelect.IMPORT_TYPE_MODULE) {
				if (superior != null
						&& !module.getId().equals(superior.getId())) {
					module.setSuperior(superior);
				} else {
					module.setSuperior(null);
				}
			}
			doCreateOrUpdate(moduleProcess, module);
		}

		// 导入ResVO
		for (Iterator<ResVO> iterator = elements.getRes().iterator(); iterator
				.hasNext();) {
			ResVO resVO = iterator.next();
			resVO.setApplicationid(application.getId());
			doCreateOrUpdate(resProcess, resVO);
		}

		// 导入MultiLanguage
		if(elements.getMultiLanguage() != null){
			for (Iterator<MultiLanguage> iterator = elements.getMultiLanguage().iterator(); iterator
					.hasNext();) {
				MultiLanguage multiLanguage = iterator.next();
				multiLanguage.setApplicationid(application.getId());
				doCreateOrUpdate(multiLanguageProcess, multiLanguage);
			}
		}
		
		// 导入Task
		for (Iterator<Task> iterator = elements.getTasks().iterator(); iterator
				.hasNext();) {
			Task task = iterator.next();
			task.setApplicationid(application.getId());
			doCreateOrUpdate(taskProcess, task);
		}
		
		// 导入RoleVO
		for (Iterator<RoleVO> iterator = elements.getRoles().iterator(); iterator
				.hasNext();) {
			RoleVO role = iterator.next();
			role.setApplicationid(application.getId());
			doCreateOrUpdate(roleProcess, role);
		}

		// 导入ResourceVO
		for (Iterator<ResourceVO> iterator = elements.getResources().iterator(); iterator
				.hasNext();) {
			ResourceVO resource = iterator.next();
			resource.setApplicationid(application.getId());

			// 导入LinkVO
			LinkVO link = resource.getLink();
			if (link != null) {
				link.setApplicationid(application.getId());
				doCreateOrUpdate(linkProcess, link);
			}
			doCreateOrUpdate(resourceProcess, resource);
		}

		// 导入PermissionVO
		for (Iterator<PermissionVO> iterator = elements.getPermissions()
				.iterator(); iterator.hasNext();) {
			PermissionVO permission = iterator.next();
			permission.setApplicationid(application.getId());
			doCreateOrUpdate(permissionProcess, permission);
		}

		// 导入RepositoryVO
		for (Iterator<RepositoryVO> iterator = elements.getRepositories()
				.iterator(); iterator.hasNext();) {
			RepositoryVO repository = iterator.next();
			repository.setApplicationid(application.getId());
			doCreateOrUpdate(repositoryProcess, repository);
		}

		// 导入ValidateRepositoryVO
		for (Iterator<ValidateRepositoryVO> iterator = elements
				.getValidateRepositories().iterator(); iterator.hasNext();) {
			ValidateRepositoryVO repository = iterator.next();
			repository.setApplicationid(application.getId());
			doCreateOrUpdate(validateRepositoryProcess, repository);
		}

		// 导入IMPMappingConfigVO
		for (Iterator<IMPMappingConfigVO> iterator = elements
				.getExcelMappingConfigs().iterator(); iterator.hasNext();) {
			IMPMappingConfigVO excelMappConfig = iterator.next();
			excelMappConfig.setApplicationid(application.getId());
			doCreateOrUpdate(excelMappingConfigProcess, excelMappConfig);
		}
		// 导入Page
		if (elements.getPages() != null)
			for (Iterator<Page> iterator = elements.getPages().iterator(); iterator
					.hasNext();) {
				Page page = iterator.next();
				StyleRepositoryVO style = page.getStyle();
				if (style != null) {
					style.setApplicationid(application.getId());
					doCreateOrUpdate(styleProcess, style);
				}
				try {
					UserDefined homePage = new UserDefined();
					homePage.setId(Tools.getSequence());
					homePage.setName(page.getName());
					homePage.setDescription(page.getDiscription());
					homePage.setDefineMode(UserDefined.CUSTOMIZE_MODE);
					homePage.setTemplateContext(page.getTemplatecontext());
					homePage.setApplicationid(application.getId());
					homePage.setPublished(page.isDefHomePage());
					homePage.setRoleNames(page.getRoleNames());
					homePage.setRoleIds(page.getRoles());
					homePage.setStyle(page.getStyle());
					homePage.setVersion(page.getVersion());
					doCreateOrUpdate(homePageProcess, homePage);
				} catch (Exception e) {
					LOG.warn(e.getMessage());
				}
			}

		// 导入HomePage
		if (elements.getUserDefineds() != null)
			for (Iterator<UserDefined> iterator = elements.getUserDefineds()
					.iterator(); iterator.hasNext();) {
				UserDefined homePage = iterator.next();
				homePage.setApplicationid(application.getId());
				StyleRepositoryVO style = homePage.getStyle();
				if (style != null) {
					style.setApplicationid(application.getId());
					doCreateOrUpdate(styleProcess, style);
				}

				doCreateOrUpdate(homePageProcess, homePage);
			}


		// 导入Form
		for (Iterator<Form> iterator = elements.getForms().iterator(); iterator
				.hasNext();) {
			Form form = iterator.next();
			form.setCheckout(false);
			form.setCheckoutHandler("");
			form.setApplicationid(application.getId());

			StyleRepositoryVO style = form.getStyle();
			if (style != null) {
				style.setApplicationid(application.getId());
				doCreateOrUpdate(styleProcess, style);
			}
			if (select.getImportType() == ImpSelect.IMPORT_TYPE_MODULE_ELEMENTS) {
				form.setModule(superior);
			}
			doCreateOrUpdateForm(formProcess, form);
		}

		// 导入Reminder
		for (Iterator<SummaryCfgVO> iterator = elements.getReminders().iterator(); iterator
				.hasNext();) {
			SummaryCfgVO reminder = iterator.next();
			reminder.setApplicationid(application.getId());

			UserDefined homePage = reminder.getUserDefined();
			if (homePage != null) {
				homePage.setApplicationid(application.getId());
				doCreateOrUpdate(homePageProcess, homePage);
			}
			doCreateOrUpdate(reminderProcess, reminder);
		}
		
		// 导入View
		for (Iterator<View> iterator = elements.getViews().iterator(); iterator
				.hasNext();) {
			View view = iterator.next();
			view.setCheckout(false);
			view.setCheckoutHandler("");
			view.setApplicationid(application.getId());

			StyleRepositoryVO style = view.getStyle();
			if (style != null) {
				style.setApplicationid(application.getId());
				doCreateOrUpdate(styleProcess, style);
			}

			Form form = view.getSearchForm();
			if (form != null) {
				form.setApplicationid(application.getId());
				if (select.getImportType() == ImpSelect.IMPORT_TYPE_MODULE_ELEMENTS) {
					form.setModule(superior);
				}
				doCreateOrUpdateForm(formProcess, form);
			}
			if (select.getImportType() == ImpSelect.IMPORT_TYPE_MODULE_ELEMENTS) {
				view.setModule(superior);
			}
			doCreateOrUpdate(viewProcess, view);
		}

		// 导入Workflow
		for (Iterator<BillDefiVO> iterator = elements.getWorkflows().iterator(); iterator
				.hasNext();) {
			BillDefiVO flow = iterator.next();
			flow.setCheckout(false);
			flow.setCheckoutHandler("");
			flow.setApplicationid(application.getId());
			if (select.getImportType() == ImpSelect.IMPORT_TYPE_MODULE_ELEMENTS) {
				flow.setModule(superior);
			}
			doCreateOrUpdate(billDefiProcess, flow);
		}

		// 导入CrossReport
		for (Iterator<CrossReportVO> iterator = elements.getCrossReports()
				.iterator(); iterator.hasNext();) {
			CrossReportVO crossReportVO = iterator.next();
			crossReportVO.setCheckout(false);
			crossReportVO.setCheckoutHandler("");
			crossReportVO.setApplicationid(application.getId());
			if (select.getImportType() == ImpSelect.IMPORT_TYPE_MODULE_ELEMENTS) {
				crossReportVO.setModule(superior.getId());
			}
			doCreateOrUpdate(crossReportProcess, crossReportVO);
		}

		// 导入DataSource
		for (Iterator<DataSource> iterator = elements.getDataSource()
				.iterator(); iterator.hasNext();) {
			DataSource datasource = iterator.next();
			datasource.setApplicationid(application.getId());
			doCreateOrUpdate(dataSourceProcess, datasource);
		}
		// 导入Printer
		for (Iterator<Printer> iterator = elements.getPrinter().iterator(); iterator
				.hasNext();) {
			Printer printer = iterator.next();
			printer.setCheckout(false);
			printer.setCheckoutHandler("");
			printer.setApplicationid(application.getId());
			if (select.getImportType() == ImpSelect.IMPORT_TYPE_MODULE_ELEMENTS) {
				printer.setModule(superior);
			}
			doCreateOrUpdate(printerProcess, printer);
		}
	}

	public void doCreateOrUpdate(IDesignTimeProcess<?> process, ValueObject vo)
			throws Exception {
		ValueObject po = process.doView(vo.getId());
		try {
			if (po == null) {
				process.doCreate(vo);
			} else {
				vo.setVersion(po.getVersion());
				process.doUpdate(vo);
			}
		} catch (Exception e) {
			LOG.error("Create or update " + vo.getClass().getName() + "("
					+ vo.getId() + ") failed ", e);
			throw e;
		}

	}
	public void doCreateOrUpdateForm(IDesignTimeProcess<?> process, Form vo)
	throws Exception {
	     ValueObject po = process.doView(vo.getId());
	     BaseFormProcessBean obj=(BaseFormProcessBean) process;
	     try {
		   if (po == null) {
			obj.doCreate(vo);
		 } else {
			vo.setVersion(po.getVersion());
			obj.doUpdate1(vo);
		}
	   } catch (Exception e) {
		LOG.error("Create or update " + vo.getClass().getName() + "("
				+ vo.getId() + ") failed ", e);
		throw e;
	}
	
	}
}
