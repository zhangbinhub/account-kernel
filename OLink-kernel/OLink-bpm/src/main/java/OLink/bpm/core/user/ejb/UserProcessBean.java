package OLink.bpm.core.user.ejb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.constans.Framework;
import OLink.bpm.core.department.ejb.DepartmentVO;
import OLink.bpm.core.deploy.application.ejb.ApplicationProcess;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.core.domain.dao.DomainDAO;
import OLink.bpm.core.domain.ejb.DomainVO;
import OLink.bpm.core.permission.ejb.PermissionPackage;
import OLink.bpm.core.role.ejb.RoleVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.util.StringUtil;
import OLink.bpm.core.user.dao.UserDAO;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import OLink.bpm.core.domain.ejb.DomainProcess;
import OLink.bpm.core.user.action.WebUser;
import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.HibernateException;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.dao.PersistenceUtils;
import eWAP.core.Tools;

public class UserProcessBean extends AbstractDesignTimeProcessBean<UserVO> implements UserProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4965110888092349931L;
	public final static HashMap<String, WebUser> _cache = new HashMap<String, WebUser>();

	public void doCreate(final ValueObject vo) throws Exception {
		UserVO tmp = null;
		tmp = ((UserDAO) getDAO()).login(((UserVO) vo).getLoginno(), vo.getDomainid());
		if (tmp != null) {
			throw new ExistNameException("{*[core.user.account.exist]*}");
		}

		final UserVO user = (UserVO) vo;

		if (user.getLoginpwd() == null) {
			user.setLoginpwd("");
		} else {
			user.setLoginpwd(encrypt(user.getLoginpwd()));
		}

		try {
			PersistenceUtils.beginTransaction();
			if (user.getId() == null || user.getId().trim().length() == 0) {
				user.setId(Tools.getSequence());
			}

			if (user.getSortId() == null || user.getSortId().trim().length() == 0) {
				user.setSortId(Tools.getTimeSequence());
			}

			getDAO().create(user);
			PersistenceUtils.commitTransaction();
		} catch (final Exception e) {
			e.printStackTrace();
			PersistenceUtils.rollbackTransaction();
		}
		PermissionPackage.clearCache();
	}

	public void doRemove(final String pk) throws Exception {
		try {
			PersistenceUtils.beginTransaction();
			// 检查是否是根部门
			if (pk.equals(Framework.ADMINISTRATOR)) {
				throw new Exception("{*[core.user.cannotdelete]*}");
			}
			getDAO().remove(pk);

			PersistenceUtils.commitTransaction();
		} catch (final HibernateException he) {
			throw new Exception("{*[core.user.cannotdelete]*}");
		} catch (final Exception e) {
			e.printStackTrace();
			PersistenceUtils.rollbackTransaction();
		}

		super.doRemove(pk);
		PermissionPackage.clearCache();
	}

	public void doRemove(final String[] pks) throws Exception {
		try {
			super.doRemove(pks);
		} catch (final Exception e) {
			throw new Exception("{*[core.user.cannotremove]*}");
		}
	}

	/**
	 * 更新用户默认选择的应用
	 * 
	 * @param userid
	 * @param defaultApplicationid
	 * @throws Exception
	 */
	public void doUpdateDefaultApplication(final String userid, final String defaultApplicationid) throws Exception {
		try {
			PersistenceUtils.beginTransaction();
			((UserDAO) getDAO()).updateDefaultApplication(userid, defaultApplicationid);
			PersistenceUtils.commitTransaction();
		} catch (final Exception e) {
			e.printStackTrace();
			PersistenceUtils.rollbackTransaction();
		}

	}

	/**
	 * 不清空缓存的更新方法
	 * 
	 * @param vo
	 * @throws Exception
	 */
	public void doUpdateWithCache(final ValueObject vo) throws Exception {
		final UserVO user = (UserVO) vo;
		update(user);
	}

	/**
	 * 清空缓存的更新方法
	 */
	public void doUpdate(final ValueObject vo) throws Exception {
		final UserVO user = (UserVO) vo;
		update(user);
		PermissionPackage.clearCache();
	}

	private void update(final UserVO vo) throws Exception {
		final UserVO user = vo;
		UserVO tmp = null;

		try {
			tmp = ((UserDAO) getDAO()).login((user.getLoginno()), user.getDomainid());
			if (tmp != null && !tmp.getId().equals(vo.getId())) {
				throw new ExistNameException("{*[core.user.account.exist]*}");
			}

			PersistenceUtils.beginTransaction();

			final UserVO po = (UserVO) getDAO().find(vo.getId());
			final String loginwd = user.getLoginpwd();
			if (po != null && loginwd != null && !loginwd.trim().equals(po.getLoginpwd())) {
				user.setLoginpwd(encrypt(loginwd));
			} else if (po != null) {
				if (vo.getUseIM() && decrypt(po.getLoginpwd()) == null){
					throw new Exception("{*[reset.password.before.use.im]*}");
				}
				
				user.setLoginpwd(po.getLoginpwd());
			}
			if (po != null) {
				// 如果当前用户等级发生改变, 相应的下级也递归改变
				if (user.getLevel() != po.getLevel()) {
					changeLevel(vo);
				}
				PropertyUtils.copyProperties(po, vo);

				getDAO().update(po);
			} else {
				getDAO().update(vo);
			}
			PersistenceUtils.commitTransaction();
		} catch (final Exception e) {
			PersistenceUtils.rollbackTransaction();
			throw e;
		}
	}

	/**
	 * 更改所有下级用户等级
	 * 
	 * @param vo
	 * @throws Exception
	 */
	private void changeLevel(final UserVO vo) throws Exception {
		final ParamsTable params = new ParamsTable();
		params.setParameter("t_superior", vo.getId());

		// 获取下级列表
		final Collection<?> underList = getDAO().simpleQuery(params);

		final Iterator<?> itmp = underList.iterator();
		while (itmp.hasNext()) {
			final UserVO curUser = (UserVO) itmp.next();
			curUser.setLevel(vo.getLevel() + 1);
			getDAO().update(curUser);
			changeLevel(curUser);
		}
	}

	public Collection<UserVO> getUnderList(final String userId) throws Exception {
		return getUnderList(userId, 10);
	}

	public Collection<UserVO> getUnderList(final String userId, final int maxDeep) throws Exception {
		if (maxDeep <= 0) {
			return new ArrayList<UserVO>();
		}
		final ParamsTable params = new ParamsTable();
		params.setParameter("t_superior", userId);
		final Collection<UserVO> underList = getDAO().simpleQuery(params);
		final Collection<UserVO> cols = new ArrayList<UserVO>();
		cols.addAll(underList);

		if (underList != null && !underList.isEmpty()) {
			for (final Iterator<UserVO> iterator = underList.iterator(); iterator.hasNext();) {
				final UserVO user = iterator.next();
				cols.addAll(getUnderList(user.getId(), maxDeep - 1));
			}
		}
		return cols;
	}

	public Collection<UserVO> getSuperiorList(String userId) throws Exception {
		Collection<UserVO> superiors = new ArrayList<UserVO>();
		final ParamsTable params = new ParamsTable();
		params.setParameter("id", userId);
		UserVO user = (UserVO) getDAO().find(userId);
		if (user != null) {
			while (true) {
				UserVO superior = user.getSuperior();
				if (superior != null) {
					superiors.add(superior);
					user = superior;
				} else {
					break;
				}
			}
		}
		return superiors;
	}

	public void changePwd(final String id, final String oldPwd, final String newPwd) throws Exception {
		final UserVO vo = (UserVO) getDAO().find(id);
		if (!oldPwd.equals(decrypt(vo.getLoginpwd()))) {
			throw new Exception("{*[core.user.password.error]*}");
		}
		vo.setLoginpwd(encrypt(newPwd));
		super.doUpdate(vo);
	}

	public UserVO login(final String no) throws Exception {
		UserVO vo = null;
		try {
			vo = ((UserDAO) getDAO()).login(no);
		} catch (final Exception ex) {
			throw new Exception("{*[core.user.notexist]*}");
		}
		if (vo != null) {
			_cache.remove(vo.getId());
		}
		return vo;
	}

	public UserVO login(final String no, final String password, final String domainName) throws Exception {
		UserVO vo = null;
		DomainProcess process = null;
		try{
			process = (DomainProcess) ProcessFactory.createProcess(DomainProcess.class);
		}catch(Exception e){
			throw new Exception("eWAP is not legally authorized");
		}
		final DomainVO domain = process.getDomainByDomainName(domainName);
		
		if (domain == null || domain.getStatus() == 0) {
			throw new Exception("{*[core.domain.notexist]*}");
		}
		vo = ((UserDAO) getDAO()).login(no, domain.getId());

		if (vo == null) {
			throw new Exception("{*[core.user.notexist]*}");
		}
		_cache.remove(vo.getId());

		if (vo.getLoginpwd() != null
				&& (password.equals(decrypt(vo.getLoginpwd())) || vo.getLoginpwd().equals(encryptOld(password)))) {
			if (vo.getStatus() == 1) {
				return vo;
			} else {
				// modified by alex-->
				throw new Exception("{*[core.user.noeffectived]*}");
				// <--modified by alex
			}

		} else {
			throw new Exception("{*[core.user.password.error]*}");
		}
		
	}

	public WebUser getWebUserInstance(final String userid) throws Exception {
		WebUser tmp = _cache.get(userid);
		if (tmp != null) {
			return tmp;
		}
		final UserVO user = (UserVO) doView(userid);
		if (user != null) {
			tmp = new WebUser(user);
			_cache.put(userid, tmp);
			return tmp;
		}
		return null;
	}

	public UserVO login(final String no, final String domain) throws Exception {
		UserVO vo = null;
		try {
			vo = ((UserDAO) getDAO()).login(no, domain);
		} catch (final Exception ex) {
			if (ex.getMessage() != null && ex.getMessage().equals("Row does not exist")) {
				throw new Exception("{*[core.user.notexist]*}");
			} else {
				throw ex;
			}
		}
		if (vo != null)
			_cache.remove(vo.getId());
		return vo;
	}

	public Collection<UserVO> getDatasByDept(String parent, final String domain) throws Exception {
		if (parent == null) {
			parent = "";
		}
		return ((UserDAO) getDAO()).getDatasByDept(parent, domain);
	}

	public Collection<UserVO> getDatasByDept(String parent) throws Exception {
		if (parent == null) {
			parent = "";
		}
		return ((UserDAO) getDAO()).getDatasByDept(parent);
	}

	public Collection<UserVO> getDatasByGroup(String parent, final String domain) throws Exception {
		if (parent == null) {
			parent = "";
		}
		return ((UserDAO) getDAO()).getDatasByRoleid(parent, domain);
	}

	/**
	 * 新的密码加密机制
	 * 
	 * @param s
	 * @return
	 * @throws Exception
	 */
	private String encrypt(final String s) throws Exception {
		return Tools.encryptPassword(s);
	}
	
	/**
	 * 新的密码解密机制
	 * 
	 * @param s
	 * @return
	 * @throws Exception
	 */
	private String decrypt(final String s){
		return Tools.decryptPassword(s);
	}

	/**
	 * 旧的密码加密机制(主要用于登录)
	 * 
	 * @param s
	 * @return
	 * @throws Exception
	 */
	private String encryptOld(final String s) throws Exception {
		return StringUtil.left(Tools.encodeToMD5(s), Framework.PASSWORD_LENGTH);
	}

	// @SuppressWarnings("unchecked")
	protected IDesignTimeDAO<UserVO> getDAO() throws Exception {
		return (UserDAO) DAOFactory.getDefaultDAO(UserVO.class.getName());
	}

	public void doPersonalUpdate(final ValueObject vo) throws Exception {
//		UserSetupProcess usp = (UserSetupProcess) ProcessFactory.createProcess(UserSetupProcess.class);
		final UserVO po = (UserVO) vo;
		final UserVO oldValue = (UserVO) getDAO().find(po.getId());
		po.setId(oldValue.getId());
		po.setDeveloper(oldValue.isDeveloper());
		po.setDomainAdmin(oldValue.isDomainAdmin());
		po.setDomainid(oldValue.getDomainid());
		po.setStatus(oldValue.getStatus());
		po.setSuperAdmin(oldValue.isSuperAdmin());
		po.setUserDepartmentSets(oldValue.getUserDepartmentSets());
		po.setUserRoleSets(oldValue.getUserRoleSets());
		po.setSuperior(oldValue.getSuperior());
//		po.setField1(oldValue.getField1());
//		po.setField2(oldValue.getField2());
//		po.setField3(oldValue.getField3());
//		po.setField4(oldValue.getField4());
//		po.setField5(oldValue.getField5());
		po.setField6(oldValue.getField6());
		po.setField7(oldValue.getField7());
		po.setField8(oldValue.getField8());
		po.setField9(oldValue.getField9());
		po.setField10(oldValue.getField10());
//Add By XGY 20130408
		po.setDefaultDepartment(oldValue.getDefaultDepartment());
		po.setDefaultApplication(oldValue.getDefaultApplication());
		
		//update by zb 2014-04-22
//		UserSetupVO oldUserSetup = usp.getUserSetupByUserId(oldValue.getId());
//		if(oldUserSetup == null){
//			UserSetupVO	usersetup = new UserSetupVO();
//			usersetup.setId(Tools.getSequence());
//			usersetup.setUserId(oldValue.getId());
//			usersetup.setUser(oldValue);
//			oldValue.setUserSetup(usersetup);
//		}else{
//			oldValue.setUserSetup(oldUserSetup);
//		}
//		if (po.getUserSetup() != null) {
			// 更新用户设置
//			oldValue.getUserSetup().setPendingStyle(po.getUserSetup().getPendingStyle());
//			oldValue.getUserSetup().setUserSkin(po.getUserSetup().getUserSkin());
//			oldValue.getUserSetup().setUserStyle(po.getUserSetup().getUserStyle());
//			oldValue.getUserSetup().setUseHomePage(po.getUserSetup().getUseHomePage());
//			oldValue.getUserSetup().setGeneralPage(po.getUserSetup().getGeneralPage());
//			oldValue.getUserSetup().setStatus(po.getUserSetup().getStatus());
//		}
		//update by zb 2014-04-22
		po.setUserSetup(null);
		//update by zb 2014-04-22
//		usp.doCreateOrUpdate(oldValue.getUserSetup());
		
		update(po);
		PermissionPackage.clearCache();
	}

	public UserVO createUser(final UserVO user) throws Exception {
		doCreate(user);
		return user;
	}

	public ValueObject doView(final String pk) throws Exception {
		return getDAO().find(pk);
	}

	public Collection<UserVO> doQueryHasMail(final String application) throws Exception {
		return ((UserDAO) getDAO()).queryHasMail(application);
	}

	public boolean isEmpty() throws Exception {
		return ((UserDAO) getDAO()).isEmpty();
	}

	public DataPackage<UserVO> doQueryByRoleId(final String roleid) throws Exception {

		return ((UserDAO) getDAO()).queryByRoleId(roleid);
	}

	public Collection<UserVO> queryByDomain(final String domainid) throws Exception {
		return queryByDomain(domainid, 1, Integer.MAX_VALUE);
	}

	public Collection<UserVO> queryByDomain(final String domainid, final int page, final int line) throws Exception {
		return ((UserDAO) getDAO()).queryByDomain(domainid, page, line);
	}

	public Collection<UserVO> queryByProxyUserId(final String proxyid) throws Exception {
		return ((UserDAO) getDAO()).queryByProxyUserId(proxyid);
	}

	public DataPackage<UserVO> listUsers(final ParamsTable params, final WebUser user) throws Exception {
		return getDAO().query(params, user);
	}

	public String getDefaultApplicationId(final String userid) throws Exception {
		final ApplicationProcess appProcess = (ApplicationProcess) ProcessFactory
				.createProcess(ApplicationProcess.class);
		final Collection<ApplicationVO> appList = appProcess.queryApplications(userid);
		if (!appList.isEmpty()) {
			final ApplicationVO application = appList.iterator().next();

			return application.getId();
		}
		return null;
	}

	public UserVO getUserByLoginno(final String loginno, final String domainid) throws Exception {
		return ((UserDAO) getDAO()).findByLoginno(loginno, domainid);
	}
	
	public UserVO getUserByLoginnoAndDoaminName(final String loginno, final String domainName) throws Exception {
		DomainDAO domainDAO = (DomainDAO) DAOFactory.getDefaultDAO(DomainVO.class.getName());
		final DomainVO domain = domainDAO.getDomainByName(domainName);
		if (domain == null || domain.getStatus() == 0) {
			throw new Exception("{*[core.domain.notexist]*}, domain name is: " + domainName);
		}

		String hql = "FROM " + UserVO.class.getName() + " vo WHERE vo.loginno ='" + loginno + "'"
				+ " AND vo.domainid='" + domain.getId() + "'";

		return (UserVO) getDAO().getData(hql);
	}

	/**
	 * 深度查询获取用户树
	 * 
	 * @param cols
	 *            所有用户列表
	 * @param startNode
	 *            开始
	 * @param excludeNodeId
	 *            排除的节点ID
	 * @param deep
	 *            深度
	 * @return 树型用户列表
	 * @throws Exception
	 */
	public Map<String, String> deepSearchTree(final Collection<?> cols, final UserVO startNode,
			final String excludeNodeId, final int deep) throws Exception {
		final Map<String, String> list = new LinkedHashMap<String, String>();

		final String prefix = "|------------------------------------------------";
		if (startNode != null) {
			list.put(startNode.getId(), prefix.substring(0, deep * 2) + startNode.getName());
		}

		final Iterator<?> iter = cols.iterator();
		while (iter.hasNext()) {
			final UserVO vo = (UserVO) iter.next();
			if (startNode == null) {
				if (vo.getSuperior() == null) {
					if (vo.getId() != null && !vo.getId().equals(excludeNodeId)) {
						final Map<String, String> tmp = deepSearchTree(cols, vo, excludeNodeId, deep + 1);
						list.putAll(tmp);
					}
				}
			} else {
				if (vo.getSuperior() != null && vo.getSuperior().getId().equals(startNode.getId())) {
					if (vo.getId() != null && !vo.getId().equals(excludeNodeId)) {
						final Map<String, String> tmp = deepSearchTree(cols, vo, excludeNodeId, deep + 1);
						list.putAll(tmp);
					}
				}
			}
		}
		return list;
	}

	/**
	 * 查询
	 */
	public DataPackage<UserVO> queryUsersExcept(final ParamsTable params, final WebUser user) throws Exception {
		return getDAO().query(params, user);
	}

	public DataPackage<UserVO> listLinkmen(final ParamsTable params) throws Exception {
		return ((UserDAO) getDAO()).listLinkmen(params);
	}

	public String queryUserIdsByName(final String username, final String domainid) throws Exception {
		final Collection<UserVO> list = ((UserDAO) getDAO()).queryUsersByName(username, domainid);
		StringBuffer namelist = new StringBuffer();
		for (final Iterator<UserVO> iter = list.iterator(); iter.hasNext();) {
			final UserVO vo = iter.next();
			namelist.append(vo.getId()).append(",");
		}
		if (namelist.toString().endsWith(",")) {
			String temp = namelist.substring(0, namelist.length() - 1);
			namelist.setLength(0);
			namelist.append(temp);
		}
		return namelist.toString();
	}

	public String findUserIdByAccount(final String account, final String domainid) throws Exception {
		final UserVO vo = getUserByLoginno(account, domainid);
		return vo==null?"":vo.getId();
	}

	public Collection<UserVO> queryByDepartment(final String deptId) throws Exception {
		String sql = "SELECT vo.* FROM " + getDAO().getSchema() + "T_USER" + " vo";
		sql += " WHERE vo.ID in (select s.USERID from " + getDAO().getSchema() + "T_USER_DEPARTMENT_SET s";
		sql += " WHERE s.DEPARTMENTID='" + deptId + "')";

		return getDAO().getDatasBySQL(sql);
	}

	// department info page click button to show user UnjoinedDeptlist and add
	// ---- dolly 2011-1-9
	public DataPackage<UserVO> queryOutOfDepartment(final ParamsTable params, final String deptid) throws Exception {
		final DepartmentVO dept = (DepartmentVO) DAOFactory.getDefaultDAO(DepartmentVO.class.getName()).find(deptid);
		String sql = "SELECT vo.* FROM " + getDAO().getSchema() + "T_USER" + " vo";
		sql += " WHERE vo.DOMAINID='" + dept.getDomainid().toString() + "' AND vo.ID not in (select s.USERID from "
				+ getDAO().getSchema() + "T_USER_DEPARTMENT_SET s";
		sql += " WHERE s.USERID!='' and s.DEPARTMENTID='" + deptid + "')";
		return getDAO().getDatapackageBySQL(sql, params, getPage(params), getPagelines(params));
	}

	public DataPackage<UserVO> queryOutOfRole(final ParamsTable params, final String roleid) throws Exception {
		String sql = "SELECT vo.* FROM " + getDAO().getSchema() + "T_USER" + " vo";
		sql += " WHERE vo.ID not in (select s.USERID from " + getDAO().getSchema() + "T_USER_ROLE_SET s";
		sql += " WHERE s.USERID!='' and s.ROLEID='" + roleid + "')";

		return getDAO().getDatapackageBySQL(sql, params, getPage(params), getPagelines(params));
	}

	public int getPage(final ParamsTable params) {
		final String _currpage = params.getParameterAsString("_currpage");
		return (_currpage != null && _currpage.length() > 0) ? Integer.parseInt(_currpage) : 1;
	}

	public int getPagelines(final ParamsTable params) {
		final String _pagelines = params.getParameterAsString("_pagelines");
		return (_pagelines != null && _pagelines.length() > 0) ? Integer.parseInt(_pagelines) : Integer.MAX_VALUE;

	}

	public void addUserToDept(final String[] userids, final String deptid) throws Exception {
		final UserProcess uerProcess = (UserProcess) ProcessFactory.createProcess(UserProcess.class);
		final DepartmentVO dept = (DepartmentVO) DAOFactory.getDefaultDAO(DepartmentVO.class.getName()).find(deptid);
		if (dept != null) {
			// tempSet.addAll(developerSet);
			for (int i = 0; i < userids.length; i++) {
				final String userid = userids[i];
				final UserVO user = (UserVO) uerProcess.doView(userid);
				final UserDepartmentSet uds = new UserDepartmentSet(user.getId(), dept.getId());
				user.getUserDepartmentSets().add(uds);
				doUpdate(user);
			}
		}
	}

	public void addUserToRole(final String[] userids, final String roleid) throws Exception {
		final UserProcess uerProcess = (UserProcess) ProcessFactory.createProcess(UserProcess.class);
		final RoleVO role = (RoleVO) DAOFactory.getDefaultDAO(RoleVO.class.getName()).find(roleid);
		if (role != null) {
			// tempSet.addAll(developerSet);
			for (int i = 0; i < userids.length; i++) {
				final String userid = userids[i];
				final UserVO user = (UserVO) uerProcess.doView(userid);
				final UserRoleSet uRs = new UserRoleSet(user.getId(), role.getId());
				user.getUserRoleSets().add(uRs);
				doUpdate(user);
			}
		}

	}

	public Collection<UserVO> queryByRole(final String roleId) throws Exception {
		String sql = "SELECT vo.* FROM " + getDAO().getSchema() + "T_USER" + " vo";
		sql += " WHERE vo.ID in (select s.USERID from " + getDAO().getSchema() + "T_USER_ROLE_SET s";
		sql += " WHERE s.ROLEID='" + roleId + "')";

		return getDAO().getDatasBySQL(sql);
	}

	public Collection<UserVO> queryByDptIdAndRoleId(final String deptId, final String roleId) throws Exception {
		String sql = "SELECT vo.* FROM " + getDAO().getSchema() + "T_USER" + " vo";
		sql += " WHERE vo.ID in (select s.USERID from " + getDAO().getSchema() + "T_USER_ROLE_SET s";
		sql += " WHERE s.ROLEID='" + roleId + "') and vo.ID in (select s.USERID from " + getDAO().getSchema()
				+ "T_USER_DEPARTMENT_SET s";
		sql += " WHERE s.DEPARTMENTID='" + deptId + "')";
		return getDAO().getDatasBySQL(sql);
	}

	public static void main(String[] args) throws Exception {
		Collection<UserVO> users = new UserProcessBean().getSuperiorList("11de-c13a-0cf76f8b-a3db-1bc87eaaad4c");
		for (Iterator<UserVO> it = users.iterator(); it != null && it.hasNext();) {
			System.out.println(">>>>>>>>>>>>>" + it.next().getName());
		}
	}

	public Collection<UserVO> doQueryByHQL(String hql) throws Exception {
		return ((UserDAO) getDAO()).queryByHQL(hql);
	}
}
