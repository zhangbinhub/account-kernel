package OLink.bpm.webservice;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import OLink.bpm.constans.Web;
import OLink.bpm.core.department.ejb.DepartmentProcess;
import OLink.bpm.core.department.ejb.DepartmentVO;
import OLink.bpm.core.domain.ejb.DomainVO;
import OLink.bpm.core.role.ejb.RoleProcess;
import OLink.bpm.core.role.ejb.RoleVO;
import OLink.bpm.core.superuser.ejb.SuperUserProcess;
import OLink.bpm.core.superuser.ejb.SuperUserVO;
import OLink.bpm.core.user.ejb.*;
import OLink.bpm.core.workcalendar.calendar.ejb.CalendarProcess;
import OLink.bpm.core.workcalendar.calendar.ejb.CalendarVO;
import OLink.bpm.webservice.fault.UserServiceFault;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.core.domain.ejb.DomainProcess;
import OLink.bpm.webservice.model.SimpleUser;
import OLink.bpm.core.user.ejb.UserDepartmentSet;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.core.user.ejb.BaseUser;
import OLink.bpm.util.ObjectUtil;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import OLink.bpm.webservice.model.SimpleAdmin;
import org.apache.log4j.Logger;

public class UserService {
	private final static Logger LOG = Logger.getLogger(UserService.class);

	private final static String STANDARD_CALENDAR_NAME = "Standard_Calendar";

	/**
	 * 检查用户是否合法
	 * 
	 * @param domainName
	 *            域名称
	 * @param userAccount
	 *            用户账号
	 * @param userPassword
	 *            用户密码
	 * @param userType
	 *            用户类型
	 * @return 如果合法返回true,否则返回false
	 * @throws UserServiceFault
	 *             用户服务异常
	 */
	public SimpleUser validateUser(String domainName, String userAccount,
								   String userPassword, int userType) throws UserServiceFault {

		try {
			BaseUser user = null;
			UserProcess userProcess = (UserProcess) ProcessFactory
					.createProcess(UserProcess.class);
			SuperUserProcess sUserProcess = (SuperUserProcess) ProcessFactory
					.createProcess(SuperUserProcess.class);

			switch (userType) {
			case SimpleUser.USER_TYPE_DOMAINUSER:
				user = userProcess.login(userAccount, userPassword, domainName);
				break;
			case SimpleUser.USER_TYPE_DOMAINADMIN:
			case SimpleUser.USER_TYPE_DEVELOPER:
				user = sUserProcess.login(userAccount, userPassword);
				break;
			default:
				throw new UserServiceFault("Invaild.user.type");
			}

			SimpleUser simpleUser = convertToSimple(user);
			simpleUser.setLoginpwd(userPassword); // 设置明文密码
			simpleUser.setDomainName(domainName); // 设置登录的域名称

			return simpleUser;
		} catch (Exception e) {
			throw new UserServiceFault(e.getMessage());
		}
	}

	/**
	 * 转换为简单用户对象
	 * 
	 * @param user
	 *            用户
	 * @return SimpleUser 用户
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private SimpleUser convertToSimple(BaseUser user)
			throws IllegalAccessException, InvocationTargetException {
		if (user != null) {
			SimpleUser dest = new SimpleUser();
			ObjectUtil.copyProperties(dest, user);
			return dest;
		}
		return null;
	}

	/**
	 * 转换为简单用户对象
	 * @SuppressWarnings convertToSimple方法不支持泛型
	 * @param user
	 *            用户
	 * @return SimpleUser 用户
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@SuppressWarnings("unchecked")
	private Object convertToSimple(BaseUser user, Class beanClass)
			throws IllegalAccessException, InvocationTargetException,
			InstantiationException {
		if (user != null) {
			Object dest = beanClass.newInstance();
			ObjectUtil.copyProperties(dest, user);
			return dest;
		}
		return null;
	}

	/**
	 * 设置企业域信息
	 * 
	 * @param domainName
	 *            企业域名称
	 * @param vo
	 *            企业域用户
	 * @throws Exception
	 */
	private void setDomainInfo(String domainName, UserVO vo) throws Exception {
		DomainProcess domainProcess = (DomainProcess) ProcessFactory
				.createProcess(DomainProcess.class);
		CalendarProcess calendarProcess = (CalendarProcess) ProcessFactory
				.createProcess(CalendarProcess.class);

		// 设置企业域
		DomainVO domain = domainProcess.getDomainByName(domainName);
		vo.setDomainid(domain.getId());

		// 设置工作日历
		CalendarVO calendar = (CalendarVO) calendarProcess.doViewByName(
				STANDARD_CALENDAR_NAME, domain.getId());
		vo.setCalendarType(calendar.getId());
	}

	/**
	 * 改变用户密码
	 * 
	 * @param id
	 *            用户ID
	 * @param password
	 *            用户密码
	 * @throws UserServiceFault
	 */
	public void changePassword(String id, String password)
			throws UserServiceFault {
		try {
			UserProcess userProcess = (UserProcess) ProcessFactory
					.createProcess(UserProcess.class);
			UserVO user = (UserVO) userProcess.doView(id);
			user.setLoginpwd(password);

			userProcess.doUpdate(user);
		} catch (Exception e) {
			LOG.error("Change Password", e);
			throw new UserServiceFault(e.getMessage());
		}
	}

	/**
	 * 改变管理员密码
	 * 
	 * @param id
	 *            管理员ID
	 * @param password
	 *            新密码
	 * @throws UserServiceFault
	 */
	public void changeAdminPassword(String id, String password)
			throws UserServiceFault {
		try {
			SuperUserProcess superUserProcess = (SuperUserProcess) ProcessFactory
					.createProcess(SuperUserProcess.class);
			SuperUserVO admin = (SuperUserVO) superUserProcess.doView(id);
			admin.setLoginpwd(password);

			superUserProcess.doUpdate(admin);
		} catch (Exception e) {
			LOG.error("Change Admin Password", e);
			throw new UserServiceFault(e.getMessage());
		}

	}

	/**
	 * 创建超级管理员
	 * 
	 * @param admin
	 *            管理员值对象
	 * @return 创建后管理员
	 * @throws UserServiceFault
	 */
	public SimpleAdmin createAdmin(SimpleAdmin admin) throws UserServiceFault {
		try {
			SuperUserProcess superUserProcess = (SuperUserProcess) ProcessFactory
					.createProcess(SuperUserProcess.class);

			if (validateAdminParameter(admin))
				throw new NullPointerException("对象或对象属性存在空值!");

			SuperUserVO vo = new SuperUserVO();
			ObjectUtil.copyProperties(vo, admin);
			vo.setSuperAdmin(true); // 超级管理员

			try {
				superUserProcess.doCreate(vo);
			} catch (Exception e) {
				throw new Exception("该账号" + admin.getLoginno() + "已存在.");
			}
		} catch (Exception e) {
			LOG.error("Create Admin Error", e);
			throw new UserServiceFault(e.getMessage());
		}
		return admin;
	}

	/**
	 * 更新超级管理员信息
	 * 
	 * @param user
	 * @throws UserServiceFault
	 */
	public void updateAdmin(SimpleAdmin admin) throws UserServiceFault {
		try {
			SuperUserProcess superUserProcess = (SuperUserProcess) ProcessFactory
					.createProcess(SuperUserProcess.class);
			if (admin == null || StringUtil.isBlank(admin.getId()))
				throw new NullPointerException("对象为空或对象的ID为空.");

			if (validateAdminParameter(admin))
				throw new NullPointerException("对象或对象属性存在空值!");

			SuperUserVO vo = (SuperUserVO) superUserProcess.doView(admin
					.getId());
			if (vo == null)
				throw new Exception("数据库不存在该ID" + admin.getId() + "对象.");

			ObjectUtil.copyProperties(vo, admin);
			superUserProcess.doUpdate(vo);
		} catch (Exception e) {
			LOG.error("Update Admin Error", e);
			throw new UserServiceFault(e.getMessage());
		}
	}

	/**
	 * 获取管理员
	 * 
	 * @param id
	 * @return
	 * @throws UserServiceFault
	 */
	public SimpleAdmin getAdmin(String id) throws UserServiceFault {
		try {
			SuperUserProcess superUserProcess = (SuperUserProcess) ProcessFactory
					.createProcess(SuperUserProcess.class);
			if (id == null)
				throw new NullPointerException("主键为空!");

			SuperUserVO vo = (SuperUserVO) superUserProcess.doView(id);
			if (vo != null) {
				return (SimpleAdmin) convertToSimple(vo, SimpleAdmin.class);
			}
		} catch (Exception e) {
			LOG.error("Get Admin Error", e);
			throw new UserServiceFault(e.getMessage());
		}

		return null;
	}

	/**
	 * 删除超级管理员
	 * 
	 * @param id
	 *            管理员ID
	 * @throws UserServiceFault
	 */
	public void deleteAdmin(String id) throws UserServiceFault {
		try {
			SuperUserProcess superUserProcess = (SuperUserProcess) ProcessFactory
					.createProcess(SuperUserProcess.class);
			if (id == null)
				throw new NullPointerException("主键为空!");

			superUserProcess.doRemove(id);
		} catch (Exception e) {
			LOG.error("Delete Admin Error", e);
			throw new UserServiceFault(e.getMessage());
		}
	}

	/**
	 * 创建用户
	 * 
	 * @param user
	 *            -简单用户
	 * @return 创建后用户
	 * @throws UserServiceFault
	 *             -WebService异常
	 */
	public SimpleUser createUser(SimpleUser user) throws UserServiceFault {
		try {
			UserProcess userProcess = (UserProcess) ProcessFactory
					.createProcess(UserProcess.class);

			if (validateUserParameter(user))
				throw new NullPointerException("对象或对象属性存在空值!");
			WebServiceUtil.validateDomain(user.getDomainName());

			UserVO vo = new UserVO();
			ObjectUtil.copyProperties(vo, user);

			setDomainInfo(user.getDomainName(), vo);

			try {
				userProcess.doCreate(vo);
			} catch (Exception e) {
				throw new Exception("该账号" + user.getLoginno() + "已存在.");
			}
		} catch (Exception e) {
			LOG.error("Create User Error", e);
			throw new UserServiceFault(e.getMessage());
		}
		return user;
	}

	/**
	 * 更新用户信息
	 * 
	 * @param user
	 * @throws UserServiceFault
	 */
	public void updateUser(SimpleUser user) throws UserServiceFault {
		try {
			UserProcess userProcess = (UserProcess) ProcessFactory
					.createProcess(UserProcess.class);
			if (user == null || StringUtil.isBlank(user.getId()))
				throw new NullPointerException("对象为空或对象的ID为空.");
			if (validateUserParameter(user))
				throw new NullPointerException("对象或对象属性存在空值!");
			WebServiceUtil.validateDomain(user.getDomainName());

			UserVO vo = (UserVO) userProcess.doView(user.getId());

			if (vo == null)
				throw new Exception("数据库不存在该ID" + user.getId() + "对象.");

			ObjectUtil.copyProperties(vo, user);
			setDomainInfo(user.getDomainName(), vo);
			if (StringUtil.isBlank(user.getLoginpwd())) {
				vo.setLoginpwd(Web.DEFAULT_SHOWPASSWORD);
			}
			userProcess.doUpdate(vo);
		} catch (Exception e) {
			LOG.error("Update User Error", e);
			throw new UserServiceFault(e.getMessage());
		}
	}

	/**
	 * 根据主键查找用户
	 * 
	 * @param pk
	 *            -主键
	 * @return 用户
	 * @throws UserServiceFault
	 */
	public SimpleUser getUser(String pk) throws UserServiceFault {
		SimpleUser user = null;
		try {
			UserProcess userProcess = (UserProcess) ProcessFactory
					.createProcess(UserProcess.class);
			if (pk == null)
				throw new NullPointerException("主键为空!");

			UserVO vo = (UserVO) userProcess.doView(pk);
			if (vo != null) {
				user = convertToSimple(vo);
			}
		} catch (Exception e) {
			LOG.error("Get User Error", e);
			throw new UserServiceFault(e.getMessage());
		}
		return user;
	}

	/**
	 * 根据主键删除用户
	 * 
	 * @param pk
	 *            -主键
	 * @throws UserServiceFault
	 */
	public void deleteUser(String pk) throws UserServiceFault {
		try {
			UserProcess userProcess = (UserProcess) ProcessFactory
					.createProcess(UserProcess.class);
			if (pk == null)
				throw new NullPointerException("主键为空!");

			userProcess.doRemove(pk);
		} catch (Exception e) {
			LOG.error("Delete User Error", e);
			throw new UserServiceFault(e.getMessage());
		}
	}

	/**
	 * 根据用户对象删除用户
	 * 
	 * @param user
	 *            -用户
	 * @throws UserServiceFault
	 */
	public void deleteUser(SimpleUser user) throws UserServiceFault {
		try {
			UserProcess userProcess = (UserProcess) ProcessFactory
					.createProcess(UserProcess.class);
			if (user == null)
				throw new NullPointerException("对象为空!");
			WebServiceUtil.validateDomain(user.getDomainName());
			UserVO vo = new UserVO();
			ObjectUtil.copyProperties(vo, user);
			userProcess.doRemove(vo);
		} catch (Exception e) {
			LOG.error("Delete User Error", e);
			throw new UserServiceFault(e.getMessage());
		}
	}

	/**
	 * 验证用户属性值
	 * 
	 * @param user
	 * @return
	 */
	private boolean validateUserParameter(SimpleUser user) {
		return user == null || StringUtil.isBlank(user.getName())
				|| StringUtil.isBlank(user.getLoginno())
				|| StringUtil.isBlank(user.getLoginpwd());
	}

	/**
	 * 验证管理员属性值
	 * 
	 * @param user
	 * @return
	 */
	private boolean validateAdminParameter(SimpleAdmin admin) {
		return admin == null || StringUtil.isBlank(admin.getName())
				|| StringUtil.isBlank(admin.getLoginno())
				|| StringUtil.isBlank(admin.getLoginpwd());
	}

	/**
	 * 设置用户所属的角色集合
	 * 
	 * @param user
	 *            -用户
	 * @param roles
	 *            -角色ID组
	 * @throws UserServiceFault
	 */
	public void setRoleSet(SimpleUser user, String[] roles)
			throws UserServiceFault {
		try {
			UserProcess userProcess = (UserProcess) ProcessFactory
					.createProcess(UserProcess.class);
			if (user == null || StringUtil.isBlank(user.getId()))
				throw new NullPointerException("对象为空或对象的ID为空!");
			DomainVO domain = WebServiceUtil.validateDomain(user
					.getDomainName());

			UserVO vo = (UserVO) userProcess.doView(user.getId());
			if (vo == null)
				throw new Exception("数据库不存在该ID" + user.getId() + "对象.");
			Collection<UserRoleSet> coll = new HashSet<UserRoleSet>();
			if (roles != null) {
				for (int i = 0; i < roles.length; i++) {
					RoleProcess rp = (RoleProcess) ProcessFactory
							.createProcess(RoleProcess.class);
					RoleVO role = (RoleVO) rp.doView(roles[i]);
					boolean flag = true;
					if (role != null) {
						Iterator<?> it = domain.getApplications().iterator();
						while (it.hasNext()) {
							ApplicationVO temp = (ApplicationVO) it.next();
							if (temp.getId().equals(role.getApplicationid())) {
								UserRoleSet set = new UserRoleSet(user.getId(),
										role.getId());
								coll.add(set);
								flag = false;
								break;
							}
						}
					}
					if (flag) {
						throw new Exception("该角色" + roles[i] + "对应的应用"
								+ role.getApplicationid() + "还没应用到该域"
								+ user.getDomainName());
					}
				}
			}
			vo.setUserRoleSets(coll);
			userProcess.doUpdate(vo);
		} catch (Exception e) {
			LOG.error("Set RoleSet Error", e);
			throw new UserServiceFault(e.getMessage());
		}
	}

	/**
	 * 设置用户所属的部门集合
	 * 
	 * @param user
	 *            -用户
	 * @param deps
	 *            -部门ID组
	 * @throws UserServiceFault
	 */
	public void setDepartmentSet(SimpleUser user, String[] deps)
			throws UserServiceFault {
		try {
			UserProcess userProcess = (UserProcess) ProcessFactory
					.createProcess(UserProcess.class);
			DomainProcess domainProcess = (DomainProcess) ProcessFactory
					.createProcess(DomainProcess.class);

			if (user == null || StringUtil.isBlank(user.getId()))
				throw new NullPointerException("对象为空或对象的ID为空!");

			WebServiceUtil.validateDomain(user.getDomainName());
			UserVO vo = (UserVO) userProcess.doView(user.getId());

			if (vo == null)
				throw new Exception("数据库不存在该ID对象.");
			Collection<UserDepartmentSet> coll = new HashSet<UserDepartmentSet>();
			if (deps != null) {
				DepartmentProcess da = (DepartmentProcess) ProcessFactory
						.createProcess(DepartmentProcess.class);

				for (int i = 0; i < deps.length; i++) {
					DepartmentVO dpt = (DepartmentVO) da.doView(deps[i]);
					if (dpt == null) {
						continue;
					}

					DomainVO deptDomain = (DomainVO) domainProcess.doView(dpt
							.getDomain().getId());
					if (deptDomain != null
							&& deptDomain.getName()
									.equals(user.getDomainName())) {
						UserDepartmentSet set = new UserDepartmentSet(user
								.getId(), dpt.getId());
						coll.add(set);
					} else {
						throw new Exception("部门" + deps[i] + "不存在或部门与用户不在同一个域.");
					}
				}
			}
			vo.setUserDepartmentSets(coll);
			userProcess.doUpdate(vo);
		} catch (Exception e) {
			LOG.error("Set DepartmentSet Error", e);
			throw new UserServiceFault(e.getMessage());
		}
	}

}
