package OLink.bpm.core.superuser.ejb;

import java.util.Collection;
import java.util.HashMap;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.constans.Framework;
import OLink.bpm.constans.Web;
import OLink.bpm.core.permission.ejb.PermissionPackage;
import OLink.bpm.core.superuser.dao.SuperUserDAO;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.user.ejb.ExistNameException;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.HibernateException;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.PersistenceUtils;
import eWAP.core.Tools;

public class SuperUserProcessBean extends AbstractDesignTimeProcessBean<SuperUserVO> implements SuperUserProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9124880983154638693L;

	/**
	 * @SuppressWarnings 工厂方法不支持泛型
	 */
	//@SuppressWarnings("unchecked")
	protected IDesignTimeDAO<SuperUserVO> getDAO() throws Exception {
		return (SuperUserDAO) DAOFactory.getDefaultDAO(SuperUserVO.class.getName());
	}

	public final static HashMap<String, WebUser> _cache = new HashMap<String, WebUser>();

	public void doCreate(ValueObject vo) throws Exception {
		SuperUserVO tmp = null;
		tmp = ((SuperUserDAO) getDAO()).login(((SuperUserVO) vo).getLoginno());
		if (tmp != null) {
			throw new ExistNameException("{*[core.superuser.exist]*}");
		}
		String id = Tools.getSequence();
		vo.setId(id);

		SuperUserVO user = (SuperUserVO) vo;

		if (user.getLoginpwd() == null) {
			user.setLoginpwd("");
		} else {
			user.setLoginpwd(user.getLoginpwd());
		}

		super.doCreate(user);
		PermissionPackage.clearCache();
	}

	public void doRemove(String pk) throws Exception {
		try {
			PersistenceUtils.beginTransaction();
			// 检查是否是根部门
			if (pk.equals(Framework.ADMINISTRATOR)) {
				throw new Exception("{*[core.superadmin.cannotdel]*}");
			}
			getDAO().remove(pk);

			PersistenceUtils.commitTransaction();
		} catch (HibernateException he) {
			throw new Exception("{*[core.superuser.referenced]*}");
		} catch (Exception e) {
			e.printStackTrace();
			PersistenceUtils.rollbackTransaction();
		}

		super.doRemove(pk);
		PermissionPackage.clearCache();
	}

	public void doRemove(String[] pks) throws Exception {
		try {
			super.doRemove(pks);
		} catch (Exception e) {
			throw new Exception("{*[core.superuser.referenced]*}");
		}
	}

	public void doUpdate(ValueObject vo) throws Exception {
		SuperUserVO user = (SuperUserVO) vo;
		SuperUserVO tmp = null;
		tmp = ((SuperUserDAO) getDAO()).login(user.getLoginno());
		if (tmp != null && !tmp.getId().equals(vo.getId())) {
			throw new ExistNameException("{*[core.superuser.exist]*}");
		}

		try {
			PersistenceUtils.beginTransaction();
			SuperUserVO po = (SuperUserVO) getDAO().find(vo.getId());
			String loginwd = user.getLoginpwd();
			if (po !=null && loginwd != null && !loginwd.trim().equals(po.getLoginpwd())
					&& !loginwd.trim().equals(Web.DEFAULT_SHOWPASSWORD)) {
				user.setLoginpwd(user.getLoginpwd());
			} else if(po !=null){
				user.setLoginpwd(po.getLoginpwd());
			}
			if (po != null) {
				PropertyUtils.copyProperties(po, vo);
				getDAO().update(po);
			} else {
				getDAO().update(vo);
			}

			PersistenceUtils.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			PersistenceUtils.rollbackTransaction();
		}
		PermissionPackage.clearCache();
		// super.doUpdate(user);
	}

	public void changePwd(String id, String oldPwd, String newPwd) throws Exception {
		SuperUserVO vo = (SuperUserVO) getDAO().find(id);
		if (!oldPwd.equals(vo.getLoginpwd())) {
			throw new Exception("{*[core.superuser.password.error]*}!");
		}
		vo.setLoginpwd(newPwd);
		super.doUpdate(vo);
	}

	public SuperUserVO login(String no, String password) throws Exception {
		SuperUserVO vo = null;
		try {
			vo = ((SuperUserDAO) getDAO()).login(no);
		} catch (Exception ex) {
			throw ex;
		}

		if (vo == null) {
			throw new Exception("{*[core.superuser.notexist]*}!");
		}
		_cache.remove(vo.getId());

		if (vo.getLoginpwd().equals(password)) {
			if (vo.getStatus() == 1) {
				return vo;
			} else {
				throw new Exception("{*[core.superuser.noeffectived]*}!");
			}

		} else {
			throw new Exception("{*[core.superuser.password.error]*}!");
		}
	}

	public WebUser getWebUserInstance(String userid) throws Exception {
		WebUser tmp = _cache.get(userid);
		if (tmp != null) {
			return tmp;
		}

		SuperUserVO user = (SuperUserVO) doView(userid);
		if (user != null) {
			tmp = new WebUser(user);
			_cache.put(userid, tmp);
			return tmp;
		}
		return null;
	}

	public SuperUserVO login(String no) throws Exception {
		SuperUserVO vo = null;
		try {
			vo = ((SuperUserDAO) getDAO()).login(no);
		} catch (Exception ex) {
			if (ex.getMessage() != null && ex.getMessage().equals("Row does not exist")) {
				throw new Exception("{*[core.superuser.notexist]*}!");
			} else {
				throw ex;
			}
		}
		if (vo != null)
			_cache.remove(vo.getId());
		return vo;
	}

	public Collection<SuperUserVO> getDatasByDomain(String domain) throws Exception {
		if (domain == null) {
			domain = "";
		}
		return ((SuperUserDAO) getDAO()).getDatasByDomain(domain);
	}

	public Collection<SuperUserVO> getDatasByType(int userType) throws Exception {
		return ((SuperUserDAO) getDAO()).getDatasByType(userType);
	}

	// private String encrypt(String s) throws Exception {
	// return StringUtil.left(Security.encodeToMD5(s),
	// Framework.PASSWORD_LENGTH);
	// }

	public void doPersonalUpdate(ValueObject vo) throws Exception {

		SuperUserVO user = (SuperUserVO) vo;
		SuperUserVO oldValue = (SuperUserVO) getDAO().find(user.getId());
		String loginwd = user.getLoginpwd();
		if (loginwd != null && !loginwd.trim().equals(oldValue.getLoginpwd())
				&& !loginwd.trim().equals(Web.DEFAULT_SHOWPASSWORD)) {
			oldValue.setLoginpwd(user.getLoginpwd());
		}
		oldValue.setName(user.getName());
		oldValue.setEmail(user.getEmail());

		try {
			PersistenceUtils.beginTransaction();
			getDAO().update(oldValue);
			PersistenceUtils.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			PersistenceUtils.rollbackTransaction();
		}
		PermissionPackage.clearCache();

	}

	public Collection<SuperUserVO> doQueryHasMail() throws Exception {
		return ((SuperUserDAO) getDAO()).queryHasMail();
	}

	public boolean isEmpty() throws Exception {
		return ((SuperUserDAO) getDAO()).isEmpty();
	}

	public SuperUserVO getDefaultAdmin() throws Exception {
		return doViewByLoginno("admin");
	}

	public SuperUserVO doViewByLoginno(String loginno) throws Exception {
		return ((SuperUserDAO) getDAO()).findByLoginno(loginno);
	}

	/**
	 * 根据应用查找未加入的开发者
	 * 
	 * @param params
	 *            查询参数
	 * @return 开发者数据包
	 * @throws Exception
	 */
	public DataPackage<SuperUserVO> getUnjoinedDeveloperList(ParamsTable params) throws Exception {

		String sql = "select vo.* from " + getDAO().getSchema() + "T_SUPERUSER vo where";
		sql += " vo.ID not in (select s.USERID from " + getDAO().getSchema()
				+ "T_APPLICATION_SUPERUSER_SET s where s.APPLICATIONID ='" + params.getParameterAsString("id") + "')";
		sql += " AND vo.ISDEVELOPER = 1";
		sql += " AND vo.ISSUPERADMIN = 0";

		return getDAO().getDatapackageBySQL(sql, params, getPage(params), getPagelines(params));
	}

	/**
	 * 根据应用查找已加入的开发者
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public DataPackage<SuperUserVO> getJoinedDeveloperList(ParamsTable params) throws Exception {
		String hql = "FROM " + SuperUserVO.class.getName() + " vo";
		hql += " WHERE vo.applications.id = '" + params.getParameterAsString("id") + "' ";
		hql += " AND vo.developer = true";
		hql += " AND vo.superAdmin = false";
		return getDAO().getDatapackage(hql, params, getPage(params), getPagelines(params));
	}

	public int getPage(ParamsTable params) {
		String _currpage = params.getParameterAsString("_currpage");
		return (_currpage != null && _currpage.length() > 0) ? Integer.parseInt(_currpage) : 1;
	}

	public int getPagelines(ParamsTable params) {
		String _pagelines = params.getParameterAsString("_pagelines");
		return (_pagelines != null && _pagelines.length() > 0) ? Integer.parseInt(_pagelines) : Integer.MAX_VALUE;

	}

	public DataPackage<SuperUserVO> getUnJoinedAdminList(ParamsTable params) throws Exception {
		String sql = "select vo.* from " + getDAO().getSchema() + "T_SUPERUSER vo where";
		sql += " vo.ID not in (select s.USERID from " + getDAO().getSchema()
				+ "T_DOMAIN_SUPERUSER_SET s where s.DOMAINID ='" + params.getParameterAsString("domain") + "')";
		sql += " AND vo.STATUS = 1";
		sql += " AND (vo.ISDOMAINADMIN = 1 OR vo.ISSUPERADMIN = 1)";
		sql += " AND vo.loginno <> 'admin'";
		return getDAO().getDatapackageBySQL(sql, params, getPage(params), getPagelines(params));
	}

}
