package OLink.bpm.core.deploy.application.ejb;

import java.sql.Connection;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import OLink.bpm.core.deploy.application.dao.ApplicationDAO;
import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSource;
import OLink.bpm.core.role.ejb.RoleVO;
import OLink.bpm.core.superuser.ejb.SuperUserProcess;
import OLink.bpm.core.superuser.ejb.SuperUserVO;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.RuntimeDaoManager;
import OLink.bpm.util.StringUtil;
import eWAP.core.Tools;

import OLink.bpm.base.dao.DAOFactory;

public class ApplicationProcessBean extends AbstractDesignTimeProcessBean<ApplicationVO> implements ApplicationProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3793738490908856529L;

	public void doRemove(String pk) throws Exception {
		// 检查是否有下级模块
		super.doRemove(pk);
	}

	protected IDesignTimeDAO<ApplicationVO> getDAO() throws Exception {
		return (ApplicationDAO) DAOFactory.getDefaultDAO(ApplicationVO.class.getName());
	}

	public ApplicationVO getApplicationByDomainName(String domainName) throws Exception {
		return ((ApplicationDAO) getDAO()).getApplicationByDomainName(domainName);
	}

	public Collection<ApplicationVO> queryByDomain(String domainId) throws Exception {
		return ((ApplicationDAO) getDAO()).queryAppsByDomain(domainId, 1, Integer.MAX_VALUE);
	}

	public Map<String, ApplicationVO> getAppDomain_Cache() throws Exception {
		return ((ApplicationDAO) getDAO()).getAppDomain_Cache();

	}

	public boolean isEmpty() throws Exception {
		return ((ApplicationDAO) getDAO()).isEmpty();
	}

	public void doCreate(ValueObject vo) throws Exception {
		try {
			PersistenceUtils.beginTransaction();
			ApplicationVO app = (ApplicationVO) vo;
			testDB(app);
			if (vo.getId() == null || vo.getId().trim().length() == 0) {
				vo.setId(Tools.getSequence());
			}

			if (vo.getSortId() == null || vo.getSortId().trim().length() == 0) {
				vo.setSortId(Tools.getTimeSequence());
			}

			getDAO().create(vo);

			DataSource ds = app.getDataSourceDefine();
			// 初始化RT表
			if (ds != null && ds.getDbTypeName() != null) {
				Connection conn = null;

				try {
					conn = ds.getConnection();
					new RuntimeDaoManager().getApplicationInitDAO(conn, app.getApplicationid(), ds.getDbTypeName())
							.initTables();
				} catch (Exception e) {
					throw e;
				} finally {
					PersistenceUtils.closeConnection(conn);
				}
			}

			PersistenceUtils.commitTransaction();
		} catch (Exception e) {
			PersistenceUtils.rollbackTransaction();
			throw e;
		}
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see OLink.bpm.base.ejb.BaseProcess#doUpdate(ValueObject)
	 */
	public void doUpdate(ValueObject vo) throws Exception {
		ApplicationVO app = (ApplicationVO) vo;
		DataSource ds = app.getDataSourceDefine();
		if (ds != null && ds.getDbTypeName() != null) {
			testDB(app);

			Connection conn = null;
			try {
				conn = ds.getConnection();

				new RuntimeDaoManager().getApplicationInitDAO(conn, app.getApplicationid(), ds.getDbTypeName())
						.initTables();
			} catch (Exception e) {
				throw e;
			} finally {
				conn.close();
			}
		}
		super.doUpdate(vo);
	}

	public Collection<ApplicationVO> queryApplications(String suserid) throws Exception {
		return queryApplications(suserid, 1, Integer.MAX_VALUE);
	}

	public Collection<ApplicationVO> queryApplications(String suserid, int page, int line) throws Exception {
		return ((ApplicationDAO) getDAO()).queryApplications(suserid, page, line);
	}

	public Collection<ApplicationVO> queryAppsByDomain(String domainId, int page, int line) throws Exception {
		return ((ApplicationDAO) getDAO()).queryAppsByDomain(domainId, page, line);
	}

	public ApplicationVO getDefaultApplication(String defaultApplicationid, WebUser user) throws Exception {
		if (StringUtil.isBlank(defaultApplicationid)) {
			Collection<ApplicationVO> appList = queryByDomain(user.getDomainid());
			if (!appList.isEmpty()) {
				// 如果没有默认则选择第一个
				for (Iterator<ApplicationVO> iterator = appList.iterator(); iterator.hasNext();) {
					ApplicationVO application = iterator.next();
					Collection<RoleVO> roles = user.getRolesByApplication(application.getId());
					if (roles != null && !roles.isEmpty()) { // 进行角色校验
						return application;
					}
				}
			}
		} else {
			ApplicationVO application = (ApplicationVO) doView(defaultApplicationid);
			Collection<RoleVO> roles = user.getRolesByApplication(application.getId());
			if (roles != null && !roles.isEmpty()) { // 进行角色校验
				return application;
			} else {
				return getDefaultApplication("", user);
			}
		}

		throw new Exception("{*[core.domain.user.noapp]*}");
	}

	public ApplicationVO findBySIPAppKey(String appKey) throws Exception {
		return ((ApplicationDAO) getDAO()).findBySIPAppKey(appKey);
	}

	public ApplicationVO doViewByName(String name) throws Exception {
		return ((ApplicationDAO) getDAO()).findByName(name);
	}

	public Collection<ApplicationVO> getApplicationsByDeveloper(String developerId) throws Exception {
		String hql = "FROM " + ApplicationVO.class.getName() + " vo";
		hql += " INNER JOIN FETCH vo.owners owners WHERE owners.id = '" + developerId + "' ";
		hql += " AND owners.developer = true";
		hql += " AND owners.superAdmin = false";

		return getDAO().getDatas(hql);
	}

	public Collection<ApplicationVO> getApplicationsByDoaminAdmin(String domainAdminId) throws Exception {
		String hql = "FROM " + ApplicationVO.class.getName() + " vo ";
		hql += " INNER JOIN FETCH vo.domains d";
		hql += " INNER JOIN FETCH d.users u";
		hql += " WHERE u.id='" + domainAdminId + "'";

		return getDAO().getDatas(hql);
	}

	public DataPackage<ApplicationVO> getUnjoinApplication(ParamsTable params) throws Exception {
		String _currpage = params.getParameterAsString("_currpage");
		String _pagelines = params.getParameterAsString("_pagelines");

		int page = (_currpage != null && _currpage.length() > 0) ? Integer.parseInt(_currpage) : 1;
		int lines = (_pagelines != null && _pagelines.length() > 0) ? Integer.parseInt(_pagelines) : Integer.MAX_VALUE;

		String sql = "select vo.* from " + getDAO().getSchema() + "T_APPLICATION vo where";
		sql += " vo.ID not in (select s.APPLICATIONID from " + getDAO().getSchema()
				+ "T_DOMAIN_APPLICATION_SET s where s.DOMAINID ='" + params.getParameterAsString("domain") + "') and vo.ACTIVATED =1";
		return getDAO().getDatapackageBySQL(sql, params, page, lines);
	}

	/**
	 * 添加开发者到应用
	 */
	public void addDevelopersToApplication(String[] developerIds, String id) throws Exception {
		SuperUserProcess suerProcess = (SuperUserProcess) ProcessFactory.createProcess(SuperUserProcess.class);

		ApplicationVO application = (ApplicationVO) getDAO().find(id);
		if (application != null) {
			Set<SuperUserVO> tempSet = new HashSet<SuperUserVO>();
			Collection<SuperUserVO> developerSet = application.getOwners();
			tempSet.addAll(developerSet);
			for (int i = 0; i < developerIds.length; i++) {
				String developerId = developerIds[i];
				SuperUserVO developer = (SuperUserVO) suerProcess.doView(developerId);
				tempSet.add(developer);
			}
			application.setOwners(tempSet);
			doUpdate(application);
		}
	}

	/**
	 * 从应用中移除开发者
	 */
	public void removeDevelopersFromApplication(String[] developerIds, String id) throws Exception {
		ApplicationVO application = (ApplicationVO) getDAO().find(id);
		if (application != null) {
			Set<SuperUserVO> tempSet = new HashSet<SuperUserVO>();
			Collection<SuperUserVO> developerSet = application.getOwners();
			outer: for (Iterator<SuperUserVO> iterator = developerSet.iterator(); iterator.hasNext();) {
				SuperUserVO owener = iterator.next();
				for (int i = 0; i < developerIds.length; i++) {
					String developerId = developerIds[i];
					if (owener.getId().equals(developerId)) {
						continue outer;
					}
				}
				tempSet.add(owener);
			}
			application.setOwners(tempSet);
			doUpdate(application);
		}

	}

	public void testDB(ApplicationVO application) throws Exception {
		try {
			DataSource ds = application.getDataSourceDefine();
			Connection conn = ds.getConnection();
			conn.close();
		} catch (Exception e) {
			throw new Exception("{*[connect.database.failed]*}");
		}
	}
}
