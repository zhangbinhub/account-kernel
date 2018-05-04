package OLink.bpm.webservice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.HibernateSQLUtils;
import OLink.bpm.core.deploy.application.ejb.ApplicationProcess;
import OLink.bpm.core.domain.ejb.DomainVO;
import OLink.bpm.core.superuser.ejb.SuperUserProcess;
import OLink.bpm.core.superuser.ejb.SuperUserVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.webservice.fault.ApplicationServiceFault;
import OLink.bpm.webservice.model.SimpleApplication;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.core.domain.ejb.DomainProcess;

public class ApplicationService {

	/**
	 * 查询软件列表
	 * 
	 * @param name
	 *            软件名称
	 * @return 软件列表
	 * @throws ApplicationServiceFault
	 */
	public Collection<SimpleApplication> searchApplicationsByName(String name)
			throws ApplicationServiceFault {
		try {
			ApplicationProcess applicationProcess = (ApplicationProcess) ProcessFactory
					.createProcess(ApplicationProcess.class);
			ParamsTable params = new ParamsTable();
			params.setParameter("t_name", name);
			Collection<ApplicationVO> appList = applicationProcess
					.doSimpleQuery(params);
			if (appList != null && !appList.isEmpty()) {
				return convertToSimple(appList);
			}

		} catch (Exception e) {
			throw new ApplicationServiceFault(e.getMessage());
		}

		return new ArrayList<SimpleApplication>();
	}

	/**
	 * 根据名称查询软件
	 * 
	 * @param name
	 *            软件名称
	 * @return 软件
	 * @throws ApplicationServiceFault
	 *             软件服务异常
	 */
	public SimpleApplication searchApplicationByName(String name)
			throws ApplicationServiceFault {
		try {
			ApplicationProcess applicationProcess = (ApplicationProcess) ProcessFactory
					.createProcess(ApplicationProcess.class);
			ApplicationVO app = applicationProcess.doViewByName(name);
			if (app != null) {
				return convertToSimple(app);
			}
		} catch (Exception e) {
			throw new ApplicationServiceFault(e.getMessage());
		}

		return null;
	}

	/**
	 * 根据参数进行查询,匹配条件前缀 + 字段名称=Key,如"="为t_xxx,详细请查看HibernateSQLUtils
	 * 
	 * @see HibernateSQLUtils
	 * @param parameters
	 * @return 应用集合
	 * @throws ApplicationServiceFault
	 */
	public Collection<SimpleApplication> searchApplicationsByFilter(
			Map<String, Object> parameters) throws ApplicationServiceFault {
		try {
			ApplicationProcess applicationProcess = (ApplicationProcess) ProcessFactory
					.createProcess(ApplicationProcess.class);
			ParamsTable params = new ParamsTable();
			params.putAll(parameters);

			DataPackage<ApplicationVO> appPackage = applicationProcess
					.doQuery(params);
			if (appPackage != null && appPackage.datas != null
					&& !appPackage.datas.isEmpty()) {
				return convertToSimple(appPackage.datas);
			}

		} catch (Exception e) {
			throw new ApplicationServiceFault(e.getMessage());
		}

		return new ArrayList<SimpleApplication>();
	}

	/**
	 * 根据域标识查询应用
	 * 
	 * @param domainId
	 *            域ID
	 * @return 应用的集合
	 * @throws ApplicationServiceFault
	 */
	public Collection<SimpleApplication> searchApplicationsByDomain(
			String domainId) throws ApplicationServiceFault {
		try {
			DomainProcess domainProcess = (DomainProcess) ProcessFactory
					.createProcess(DomainProcess.class);
			DomainVO domain = (DomainVO) domainProcess.doView(domainId);
			Collection<ApplicationVO> appList = domain.getApplications();

			return convertToSimple(appList);
		} catch (Exception e) {
			throw new ApplicationServiceFault(e.getMessage());
		}
	}

	/**
	 * 根据域管理员用户获取应用
	 * 
	 * @param domainAdminId
	 *            域管理员用户
	 * @return 应用集合
	 * @throws ApplicationServiceFault
	 */
	public Collection<SimpleApplication> searchApplicationsByDomainAdmin(
			String domainAdminId) throws ApplicationServiceFault {
		try {
			ApplicationProcess applicationProcess = (ApplicationProcess) ProcessFactory
					.createProcess(ApplicationProcess.class);
			Collection<ApplicationVO> appList = applicationProcess
					.getApplicationsByDoaminAdmin(domainAdminId);
			if (appList != null && !appList.isEmpty()) {
				return convertToSimple(appList);
			}

		} catch (Exception e) {
			throw new ApplicationServiceFault(e.getMessage());
		}

		return new ArrayList<SimpleApplication>();
	}

	/**
	 * 查询软件列表
	 * 
	 * @param developerId
	 *            开发者ID
	 * @return 软件列表
	 * @throws ApplicationServiceFault
	 *             软件服务异常
	 */
	public Collection<SimpleApplication> searchApplicationsByDeveloper(
			String developerId) throws ApplicationServiceFault {
		try {
			ApplicationProcess applicationProcess = (ApplicationProcess) ProcessFactory
					.createProcess(ApplicationProcess.class);
			Collection<ApplicationVO> appList = applicationProcess
					.getApplicationsByDeveloper(developerId);
			if (appList != null && !appList.isEmpty()) {
				return convertToSimple(appList);
			}

		} catch (Exception e) {
			throw new ApplicationServiceFault(e.getMessage());
		}

		return new ArrayList<SimpleApplication>();
	}

	/**
	 * 企业用户订购应用
	 * 
	 * @param userAccount
	 *            域管理员账号
	 * @param domainName
	 *            域名称
	 * @param applicationId
	 *            应用ID
	 * @throws ApplicationServiceFault
	 */
	public boolean addApplication(String userAccount, String domainName,
			String applicationId) throws ApplicationServiceFault {
		boolean isVaild = false;

		try {
			SuperUserProcess sUserProcess = (SuperUserProcess) ProcessFactory
					.createProcess(SuperUserProcess.class);
			ApplicationProcess applicationProcess = (ApplicationProcess) ProcessFactory
					.createProcess(ApplicationProcess.class);
			DomainProcess domainProcess = (DomainProcess) ProcessFactory
					.createProcess(DomainProcess.class);
			ParamsTable params = new ParamsTable();

			params.setParameter("t_loginno", userAccount);
			Collection<?> userList = sUserProcess.doSimpleQuery(params);
			if (userList != null && !userList.isEmpty()) {
				SuperUserVO user = (SuperUserVO) userList.toArray()[0];
				DomainVO domain = user.getDomainByName(domainName);
				if (domain != null) {
					ApplicationVO application = (ApplicationVO) applicationProcess
							.doView(applicationId);
					Collection<ApplicationVO> appSet = new HashSet<ApplicationVO>();
					appSet.addAll(domain.getApplications());
					appSet.add(application);

					domain.setApplications(appSet);
					domainProcess.doUpdate(domain);
					isVaild = true;
				}
			}
		} catch (Exception e) {
			throw new ApplicationServiceFault(e.getMessage());
		}

		return isVaild;
	}

	/**
	 * 转换为简单软件对象列表
	 * 
	 * @param appList
	 *            软件列表
	 * @return 应用集合
	 */
	private Collection<SimpleApplication> convertToSimple(
			Collection<ApplicationVO> appList) {
		Collection<SimpleApplication> sAppList = new ArrayList<SimpleApplication>();
		for (Iterator<?> iterator = appList.iterator(); iterator.hasNext();) {
			ApplicationVO app = (ApplicationVO) iterator.next();
			sAppList.add(convertToSimple(app));
		}

		return sAppList;
	}

	/**
	 * 转换为简单软件对象
	 * 
	 * @param app
	 *            软件
	 * @return
	 */
	private SimpleApplication convertToSimple(ApplicationVO app) {
		if (app != null) {
			SimpleApplication sApp = new SimpleApplication();

			sApp.setId(app.getId());
			sApp.setCreateDate(app.getCreateDate());
			sApp.setDescription(app.getDescription());
			sApp.setName(app.getName());
			sApp.setRegisterCount(app.getDomains().size());

			Collection<?> owners = app.getOwners();
			Collection<String> developerNames = new ArrayList<String>();
			for (Iterator<?> iterator = owners.iterator(); iterator.hasNext();) {
				SuperUserVO developer = (SuperUserVO) iterator.next();
				developerNames.add(developer.getName());
			}

			sApp.setDeveloperNames(developerNames);

			return sApp;
		}
		return null;
	}

	public static void main(String[] args) throws ApplicationServiceFault {
		ApplicationService service = new ApplicationService();
		Map<String, Object> params = new HashMap<String, Object>();
		Collection<?> appList = service.searchApplicationsByFilter(params);
		for (Iterator<?> iterator = appList.iterator(); iterator.hasNext();) {
			// SimpleApplication sp = (SimpleApplication) iterator.next();
		}
	}
}
