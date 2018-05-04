package OLink.bpm.core.expimp;

import java.util.ArrayList;
import java.util.Collection;

import OLink.bpm.core.deploy.module.ejb.ModuleVO;
import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSource;
import OLink.bpm.core.dynaform.dts.excelimport.config.ejb.IMPMappingConfigVO;
import OLink.bpm.core.dynaform.printer.ejb.Printer;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.macro.repository.ejb.RepositoryVO;
import OLink.bpm.core.multilanguage.ejb.MultiLanguage;
import OLink.bpm.core.page.ejb.Page;
import OLink.bpm.core.permission.ejb.PermissionVO;
import OLink.bpm.core.privilege.res.ejb.ResVO;
import OLink.bpm.core.report.crossreport.definition.ejb.CrossReportVO;
import OLink.bpm.core.resource.ejb.ResourceVO;
import OLink.bpm.core.role.ejb.RoleVO;
import OLink.bpm.core.task.ejb.Task;
import OLink.bpm.core.user.ejb.UserDefined;
import OLink.bpm.core.validate.repository.ejb.ValidateRepositoryVO;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiVO;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.summary.ejb.SummaryCfgVO;

/**
 * 导入导出的集合对象，包含所有可导入导出的元素
 * 
 * @author nicholas
 * 
 */
public class ExpImpElements {
	private Collection<ModuleVO> modules;
	private Collection<View> views;
	private Collection<Form> forms;
	private Collection<Page> pages;
	private Collection<UserDefined> userDefineds;
	private Collection<BillDefiVO> workflows;
	private Collection<RoleVO> roles;
	private Collection<ResourceVO> resources; // 菜单
	private Collection<PermissionVO> permissions;
	private Collection<RepositoryVO> repositories;
	private Collection<ValidateRepositoryVO> validateRepositories;
	private Collection<IMPMappingConfigVO> excelMappingConfigs;
	private Collection<SummaryCfgVO> reminders;
	private Collection<Task> tasks;
	private Collection<CrossReportVO> crossReports;
	private Collection<DataSource> dataSource;
	private Collection<Printer> printer; // 打印配置
	private Collection<MultiLanguage> multiLanguage; // 多语言
	private Collection<ResVO> res; // 资源

	private int exportType;

	public Collection<MultiLanguage> getMultiLanguage() {
		return multiLanguage;
	}

	public void setMultiLanguage(Collection<MultiLanguage> multiLanguage) {
		this.multiLanguage = multiLanguage;
	}

	public Collection<Printer> getPrinter() {
		if (printer == null)
			printer = new ArrayList<Printer>();
		return printer;
	}

	public void setPrinter(Collection<Printer> printer) {
		this.printer = printer;
	}

	public Collection<DataSource> getDataSource() {
		if (dataSource == null)
			dataSource = new ArrayList<DataSource>();
		return dataSource;
	}

	public void setDataSource(Collection<DataSource> dataSource) {
		this.dataSource = dataSource;
	}

	public Collection<ResourceVO> getResources() {
		if (resources == null) {
			resources = new ArrayList<ResourceVO>();
		}
		return resources;
	}

	public void setResources(Collection<ResourceVO> resources) {
		this.resources = resources;
	}

	public Collection<RoleVO> getRoles() {
		if (roles == null) {
			roles = new ArrayList<RoleVO>();
		}

		return roles;
	}

	public void setRoles(Collection<RoleVO> roles) {
		this.roles = roles;
	}

	public Collection<ModuleVO> getModules() {
		if (modules == null) {
			modules = new ArrayList<ModuleVO>();
		}
		return modules;
	}

	public void setModules(Collection<ModuleVO> modules) {
		this.modules = modules;
	}

	public Collection<View> getViews() {
		if (views == null) {
			views = new ArrayList<View>();
		}
		return views;
	}

	public void setViews(Collection<View> views) {
		this.views = views;
	}

	public Collection<Form> getForms() {
		if (forms == null) {
			forms = new ArrayList<Form>();
		}
		return forms;
	}

	public void setForms(Collection<Form> forms) {
		this.forms = forms;
	}

	public Collection<BillDefiVO> getWorkflows() {
		if (workflows == null) {
			workflows = new ArrayList<BillDefiVO>();
		}
		return workflows;
	}

	public void setWorkflows(Collection<BillDefiVO> workflows) {
		this.workflows = workflows;
	}

	public Collection<PermissionVO> getPermissions() {
		if (permissions == null) {
			permissions = new ArrayList<PermissionVO>();
		}
		return permissions;
	}

	public void setPermissions(Collection<PermissionVO> permissions) {
		this.permissions = permissions;
	}

	public Collection<RepositoryVO> getRepositories() {
		if (repositories == null) {
			repositories = new ArrayList<RepositoryVO>();
		}
		return repositories;
	}

	public void setRepositories(Collection<RepositoryVO> repositories) {
		this.repositories = repositories;
	}

	public Collection<ValidateRepositoryVO> getValidateRepositories() {
		if (validateRepositories == null) {
			validateRepositories = new ArrayList<ValidateRepositoryVO>();
		}
		return validateRepositories;
	}

	public void setValidateRepositories(
			Collection<ValidateRepositoryVO> validateRepositories) {
		this.validateRepositories = validateRepositories;
	}

	public Collection<IMPMappingConfigVO> getExcelMappingConfigs() {
		if (excelMappingConfigs == null) {
			excelMappingConfigs = new ArrayList<IMPMappingConfigVO>();
		}
		return excelMappingConfigs;
	}

	public void setExcelMappingConfigs(
			Collection<IMPMappingConfigVO> excelMappingConfigs) {
		this.excelMappingConfigs = excelMappingConfigs;
	}

	public Collection<SummaryCfgVO> getReminders() {
		if (reminders == null) {
			reminders = new ArrayList<SummaryCfgVO>();
		}
		return reminders;
	}

	public void setReminders(Collection<SummaryCfgVO> reminders) {
		this.reminders = reminders;
	}

	public int getExportType() {
		return exportType;
	}

	public void setExportType(int exportType) {
		this.exportType = exportType;
	}

	public Collection<Page> getPages() {
		if (pages == null) {
			pages = new ArrayList<Page>();
		}
		return pages;
	}

	public void setPages(Collection<Page> pages) {
		this.pages = pages;
	}


	public Collection<Task> getTasks() {
		if (tasks == null) {
			tasks = new ArrayList<Task>();
		}
		return tasks;
	}

	public void setTasks(Collection<Task> tasks) {
		this.tasks = tasks;
	}

	public Collection<CrossReportVO> getCrossReports() {
		if (crossReports == null) {
			crossReports = new ArrayList<CrossReportVO>();
		}
		return crossReports;
	}

	public void setCrossReports(Collection<CrossReportVO> crossReports) {
		this.crossReports = crossReports;
	}

	public Collection<ResVO> getRes() {
		if (res == null) {
			res = new ArrayList<ResVO>();
		}

		return res;
	}

	public void setRes(Collection<ResVO> res) {
		this.res = res;
	}

	public Collection<UserDefined> getUserDefineds() {
		return userDefineds;
	}

	public void setUserDefineds(Collection<UserDefined> userDefineds) {
		this.userDefineds = userDefineds;
	}
}
