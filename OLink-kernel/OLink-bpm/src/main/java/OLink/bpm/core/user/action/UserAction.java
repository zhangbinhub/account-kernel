package OLink.bpm.core.user.action;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import OLink.bpm.base.action.BaseAction;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.constans.Web;
import OLink.bpm.core.department.ejb.DepartmentProcess;
import OLink.bpm.core.department.ejb.DepartmentVO;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.core.domain.action.DomainHelper;
import OLink.bpm.core.domain.ejb.DomainVO;
import OLink.bpm.core.email.email.action.EmailUserHelper;
import OLink.bpm.core.email.email.ejb.EmailUser;
import OLink.bpm.core.email.email.ejb.EmailUserProcess;
import OLink.bpm.core.networkdisk.ejb.NetDiskProcess;
import OLink.bpm.core.role.ejb.RoleProcess;
import OLink.bpm.core.role.ejb.RoleVO;
import OLink.bpm.core.superuser.ejb.SuperUserProcess;
import OLink.bpm.core.usersetup.ejb.UserSetupProcess;
import OLink.bpm.core.usersetup.ejb.UserSetupVO;
import OLink.bpm.core.xmpp.XMPPSender;
import OLink.bpm.core.xmpp.notification.ContactNotification;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.user.ejb.UserDefinedProcess;
import OLink.bpm.core.user.ejb.UserDepartmentSet;
import OLink.bpm.util.StringUtil;
import OLink.bpm.core.domain.ejb.DomainProcess;
import OLink.bpm.core.user.ejb.ExistNameException;
import OLink.bpm.core.user.ejb.UserDefined;
import OLink.bpm.core.user.ejb.UserProcess;
import OLink.bpm.core.user.ejb.UserRoleSet;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.util.DateUtil;
import eWAP.core.ResourcePool;
import eWAP.core.Tools;


import com.opensymphony.webwork.ServletActionContext;
public class UserAction extends BaseAction<UserVO> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4965166990637532872L;
	private String domain;

	private String superiorid;
	private String _proxyUser;
	
	private UserDefined userDefined;
	private EmailUser emailUser;

	private Collection<RoleVO> _roleList;// 取得角色(Bluce)
	private Collection<DepartmentVO> _departmentlist;
	private Collection<ApplicationVO> _applicationlist;// 取得所有应用(Bluce)
	
	protected String startProxyTime;
	protected String endProxyTime;

	public String doNew() {
		try {
			getContent().setId(Tools.getSequence());
			//ParamsTable params = getParams();
			//String domain = params.getParameterAsString("domain");
			return super.doNew();
		} catch (Exception e) {
			addFieldError("", e.getMessage());
			return INPUT;
		} 
	}

	/**
	 * 设置 权限资源
	 * 
	 * @param _resourcelist
	 *            资源集合
	 * 
	 *            public void set_resourcelist(Collection _resourcelist) {
	 *            this._resourcelist = _resourcelist; }
	 */

	public String get_proxyUser() {
		UserVO content = (UserVO) this.getContent();
		if (content.getProxyUser() != null) {
			_proxyUser = content.getProxyUser().getId();
		}
		return _proxyUser;
	}

	public void set_proxyUser(String proxyUser) throws Exception {
		_proxyUser = proxyUser;
		if (!StringUtil.isBlank(proxyUser)) {
			UserVO content = (UserVO) this.getContent();
			UserVO proxyUserVO = (UserVO) process.doView(proxyUser);
			if (proxyUserVO != null) {
				content.setProxyUser(proxyUserVO);
			}
		}
	}

	/**
	 * 返回Department集合
	 * 
	 * @return Department集合
	 * @throws Exception
	 */
	public Collection<DepartmentVO> get_departmentlist() throws Exception {
		DepartmentProcess dp = (DepartmentProcess) ProcessFactory
				.createProcess(DepartmentProcess.class);
		_departmentlist = new ArrayList<DepartmentVO>();

		DepartmentVO departmentVO = dp.getRootDepartmentByApplication(
				getApplication(), getDomain());
		Collection<DepartmentVO> subDeptList = dp.getUnderDeptList(departmentVO
				.getId(), 1);

		_departmentlist.add(departmentVO);
		_departmentlist.addAll(subDeptList);

		return _departmentlist;
	}

	/**
	 * Set Department集合
	 * 
	 * @param _departmentlist
	 *            Department集合
	 */
	public void set_departmentlist(Collection<DepartmentVO> _departmentlist) {
		this._departmentlist = _departmentlist;
	}

	/**
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public UserAction() throws Exception {
		super(ProcessFactory.createProcess(UserProcess.class), new UserVO());
	}

	/**
	 * 返回树形Department集合
	 * 
	 * @return Department集合
	 * @throws Exception
	 */
	public Map<String, String> get_departmentTree() throws Exception {
		DepartmentProcess da = (DepartmentProcess) ProcessFactory
				.createProcess(DepartmentProcess.class);
		Collection<DepartmentVO> dc = da.queryByDomain(getDomain());
		Map<String, String> dm = da
				.deepSearchDepartmentTree(dc, null, getContent().getId(), 0);

		return dm;
	}

	/**
	 * 修改获取企业域代码
	 * 
	 * @修改人：Bluce
	 * @return 角色集合
	 * @throws Exception
	 * @修改时间：2010－05－10
	 */
	public Collection<RoleVO> get_roleList() throws Exception {
		StringBuffer applicationids = new StringBuffer();
		RoleProcess rp = (RoleProcess) ProcessFactory
				.createProcess(RoleProcess.class);
		// DomainVO vo = DomainHelper.getDomainVO(getUser());//获取不到企业域(Bluce)
		DomainVO vo = DomainHelper.getDomainVO(domain);
		Collection<ApplicationVO> apps = vo.getApplications();
		Collection<RoleVO> rtn = new LinkedHashSet<RoleVO>();
		for (Iterator<ApplicationVO> iterator = apps.iterator(); iterator
				.hasNext();) {
			ApplicationVO applicationVO = iterator.next();
			applicationids.append("'" + applicationVO.getId() + "',");
		}
		if(applicationids.length()>0){
			applicationids.setLength(applicationids.length()-1);
		}
		rtn = rp.getRolesByapplicationids(applicationids.toString());
		return rtn;
	}

	/**
	 * 返回Department主键集合
	 * 
	 * @return Department主键集合
	 */
	public Collection<String> get_departmentids() {
		LinkedHashSet<String> ids = new LinkedHashSet<String>();

		UserVO user = (UserVO) getContent();
		Collection<DepartmentVO> dptlist = user.getDepartments();
		if (dptlist != null) {
			Iterator<DepartmentVO> iter = dptlist.iterator();
			while (iter.hasNext()) {
				DepartmentVO dept = iter.next();
				ids.add(dept.getId());
			}
		}
		return ids;
	}

	/**
	 * Set Department集合
	 * 
	 * @param _departmentid
	 *            Department主键集合
	 * @throws Exception
	 */

	public void set_departmentids(Collection<String> _departmentids)
			throws Exception {
		Map<?, ?> m = getContext().getParameters();
		Object obj = m.get("_deptSelectItem");// 选中的部门
		String tmp[] = null;
		if (obj instanceof String[] && ((String[]) obj).length > 0) {
			tmp = (String[]) obj;
		}
		UserVO user = (UserVO) getContent();
		if (tmp != null) {
			for (int i = 0; i < tmp.length; i++) {
				DepartmentProcess da = (DepartmentProcess) ProcessFactory
						.createProcess(DepartmentProcess.class);
				DepartmentVO dpt = (DepartmentVO) da.doView(tmp[i]);

				UserDepartmentSet set = new UserDepartmentSet(user.getId(), dpt
						.getId());
				user.getUserDepartmentSets().add(set);
			}
		}

	}

	/**
	 * 保存用户信息
	 * 
	 * @return "SUCCESS"表示成功处理,否则返回错误提示
	 */
	public String doSave() {
		try {
			UserVO user = (UserVO) getContent();
			// user.setUserRoleSets(get_Roles());
			if(user.getProxyUser()!=null && user.getStartProxyTime()!=null && user.getEndProxyTime()==null){
				this.addFieldError("1", "{*[Please]*}{*[Input]*}{*[Proxy]*}{*[End]*}{*[Date]*}");
				return INPUT;
			}
			
			if(user.getProxyUser()!=null && user.getStartProxyTime()==null && user.getEndProxyTime()!=null){
				this.addFieldError("1", "{*[Please]*}{*[Input]*}{*[Proxy]*}{*[Start]*}{*[Date]*}");
				return INPUT;
			}
			
			if(user.getProxyUser()!=null && startProxyTime!=null &&user.getStartProxyTime().getTime()>user.getEndProxyTime().getTime()){
				this.addFieldError("1", "{*[page.core.calendar.overoftime]*}");
				return INPUT;
			}
			
			if(user.getProxyUser()!=null && endProxyTime!=null &&user.getEndProxyTime().getTime()<(new Date()).getTime()){
				this.addFieldError("1", "{*[Proxy]*}{*[End]*}{*[Date]*}不得晚于{*[Current]*}{*[Time]*}");
				return INPUT;
			}

			set_departmentids(null);

			// 保存角色
			set_rolesids(null);

			if (process.doView(user.getId()) == null) {
				process.doCreate(user);
			} else {
				process.doUpdate(user);
			}
			setContent(user);
			
			this.addActionMessage("{*[Save_Success]*}");
			
			// 关联或创建邮件用户
			HttpServletRequest request = ServletActionContext.getRequest();
			EmailUserHelper.checkAndCreateEmailUser(user, request);
			/**
			 * 增加了xmpp的消息发送,此消息将发送到obpm-spark的各个客户端
			 */
			sendNotification();
			return SUCCESS;
		} catch (Exception e) {
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}
	}

	public String doList() {
		try {
			this.validateQueryParams();
			ParamsTable params = getParams();
			if (getDomain() != null && domain.trim().length() > 0) {
				params.setParameter("t_domainid", domain);
				params.setParameter("t_roleid", params
						.getParameterAsString("sm_userRoleSets.roleId"));
				WebUser user = getUser();
				this.setDatas(process.doQuery(params, user));
			}

			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("", e.getMessage());
			return INPUT;
		}

	}

	// department info page click button to show user UnjoinedDeptlist and add
	// user---- dolly 2011-1-9
	public String doUserListUnjoinedDept() {
		try {
			String deptid = this.getParams().getParameterAsString("deptid");
			if (!deptid.equals("")) {
				UserProcess userProcess = (UserProcess) ProcessFactory
						.createProcess(UserProcess.class);
				setDatas(userProcess.queryOutOfDepartment(this.getParams(),
						deptid));
			}
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("", e.getMessage());
			return INPUT;
		}
	}

	public String doUserListUnjoinedRole() {
		try {
			String roleid = this.getParams().getParameterAsString("roleid");
			if (!roleid.equals("")) {
				UserProcess userProcess = (UserProcess) ProcessFactory
						.createProcess(UserProcess.class);
				setDatas(userProcess.queryOutOfRole(this.getParams(), roleid));
			}
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("", e.getMessage());
			return INPUT;
		}
	}

	public String doAddUserToDept() {
		try {
			String[] selects = get_selects();
			String deptid = this.getParams().getParameterAsString("deptid");
			if (selects != null && selects.length > 0) {
				UserProcess userProcess = (UserProcess) ProcessFactory
						.createProcess(UserProcess.class);
				userProcess.addUserToDept(selects, deptid);
				this.addActionMessage("{*[Add]*}{*[Success]*}");
				return SUCCESS;
			} else {
				throw new Exception("{*[core.domain.notChoose]*}");
			}

		} catch (Exception e) {
			addFieldError("", e.getMessage());
			return INPUT;
		}
	}

	public String doAddUserToRole() {
		try {
			String[] selects = get_selects();
			String roleid = this.getParams().getParameterAsString("roleid");
			if (selects != null && selects.length > 0) {
				UserProcess userProcess = (UserProcess) ProcessFactory
						.createProcess(UserProcess.class);
				userProcess.addUserToRole(selects, roleid);
				this.addActionMessage("{*[Add]*}{*[Success]*}");
				return SUCCESS;
			} else {
				throw new Exception("{*[core.domain.notChoose]*}");
			}

		} catch (Exception e) {
			addFieldError("", e.getMessage());
			return INPUT;
		}
	}

	public String listUser() throws Exception {
		return super.doList();
	}

	public String getUserListByRole() {
		StringBuffer html = new StringBuffer();
		HttpServletResponse response = ServletActionContext.getResponse();
		try {
			ParamsTable params = getParams();
			String rolesid = params.getParameterAsString("rolesid");
			String applicationid = params.getParameterAsString("applicationid");
			params.setParameter("sm_userRoleSets.roleId", rolesid);
			if (applicationid != null && applicationid.trim().length() > 0) {
				if (rolesid != null && !"".equals(rolesid)) {
					Collection<UserVO> users = this.process.doQuery(params,
							getUser()).getDatas();
					for (Iterator<UserVO> iter = users.iterator(); iter
							.hasNext();) {
						UserVO tempUser = iter.next();
						html.append("<div class='list_div' title='"
								+ tempUser.getName() + "'>");
						html
								.append("<input class='list_div_click' type='checkbox' name='"
										+ tempUser.getName()
										+ "' id='"
										+ tempUser.getId()
										+ "' onclick='selectUser(jQuery(this),true)'>");
						html.append(tempUser.getName());
						html.append("</div>");
					}

				}
			}
			if (html.toString() != "") {
				response.setContentType("text/html;charset=UTF-8");
				response.getWriter().write(html.toString());
			}
		} catch (Exception e) {
		}
		return html.toString();
	}

	/**
	 * 返回角色集合
	 * 
	 * @return 角色集合
	 * @throws Exception
	 */
	public Collection<UserRoleSet> get_Roles() throws Exception {
		Map<?, ?> m = getContext().getParameters();
		Object obj = m.get("roleids");
		String[] rolesid = null;
		Collection<UserRoleSet> col = new HashSet<UserRoleSet>();
		RoleProcess rp = (RoleProcess) ProcessFactory
				.createProcess(RoleProcess.class);
		UserVO user = (UserVO) getContent();
		if (obj != null && obj instanceof String[]
				&& ((String[]) obj).length > 0) {
			rolesid = (String[]) obj;

			for (int i = 0; i < rolesid.length; i++) {
				RoleVO rv = (RoleVO) rp.doView(rolesid[i]);
				UserRoleSet userRoleSet = new UserRoleSet(user.getId(), rv
						.getId());

				col.add(userRoleSet);
			}
		}
		return col;
	}

	/**
	 * 返回用户密码
	 * 
	 * @return
	 */
	public String get_password() {
		UserVO user = (UserVO) getContent();
		if (user != null && user.getLoginpwd() != null){
			//update by zb 2014-03-06
			return Tools.decryptPassword(user.getLoginpwd());
		}
//			return Web.DEFAULT_SHOWPASSWORD;
		return null;
	}

	/**
	 * 设置用户密码
	 * 
	 * @param _password
	 */
	public void set_password(String _password) {
		UserVO user = (UserVO) getContent();
		//Modify By XGY 20130409
		String password=Tools.getRSAUtil().decrypt(ResourcePool.getPrivateKey(),_password, true);
		user.setLoginpwd(password);
	}

	/**
	 * 返回用户状态
	 * 
	 * @return "true"为可用，"false"为不可用
	 * @throws Exception
	 */
	public String get_strstatus() throws Exception {
		UserVO user = (UserVO) getContent();
		if (user.getStatus() == 1) {
			return "true";
		} else {
			return "false";
		}
	}

	/**
	 * 设置 用户状态
	 * 
	 * @param strname
	 *            用户状态字符串true or false
	 * @throws Exception
	 */
	public void set_strstatus(String strname) throws Exception {
		UserVO user = (UserVO) getContent();
		if (strname != null) {
			if (strname.equalsIgnoreCase("true")) {
				user.setStatus(1);
			} else {
				user.setStatus(0);
			}
		}
	}

	/**
	 * 删除部门
	 * 
	 * @return 成功处理返回"SUCCESS",否则提示不能删除
	 * @throws Exception
	 */
	public String removeDepartment() throws Exception {
		String departmentid = getParams().getParameterAsString(
				"sm_userDepartmentSets.departmentId");
		if (_selects != null && departmentid != null
				&& departmentid.trim().length() > 0) {
			DepartmentProcess da = (DepartmentProcess) ProcessFactory
					.createProcess(DepartmentProcess.class);
			DepartmentVO dep = (DepartmentVO) da.doView(departmentid);

			for (int i = 0; i < _selects.length; i++) {
				String id = _selects[i];
				UserVO user = (UserVO) process.doView(id);
				Collection<UserDepartmentSet> userDepartmentSets = user
						.getUserDepartmentSets();
				Collection<UserDepartmentSet> newSets = new HashSet<UserDepartmentSet>();

				// 删除UserDepartmentSet
				for (Iterator<UserDepartmentSet> iterator = userDepartmentSets
						.iterator(); iterator.hasNext();) {
					UserDepartmentSet set = iterator.next();
					if (!dep.getId().equals(set.getDepartmentId())) {
						newSets.add(set);
					}
				}

				user.setUserDepartmentSets(newSets);
				process.doUpdate(user);
			}
		}
		return SUCCESS;
	}

	/**
	 * 删除角色
	 * 
	 * @return 成功处理返回"SUCCESS",否则提示失败
	 * @throws Exception
	 */
	public String removeRole() throws Exception {
		String roleid = getParams().getParameterAsString(
				"sm_userRoleSets.roleId");
		if (_selects != null && roleid != null && roleid.trim().length() > 0) {
			for (int i = 0; i < _selects.length; i++) {
				String id = _selects[i];
				UserVO user = (UserVO) process.doView(id);
				Collection<UserRoleSet> oldroles = user.getUserRoleSets();
				Collection<UserRoleSet> roleSets = new HashSet<UserRoleSet>();

				for (Iterator<UserRoleSet> it = oldroles.iterator(); oldroles != null
						&& it.hasNext();) {
					UserRoleSet set = it.next();
					if (!roleid.equals(set.getRoleId()))
						roleSets.add(set);
				}
				user.setUserRoleSets(roleSets);
				process.doUpdate(user);
			}
		}
		return SUCCESS;
	}

	/**
	 * 修改用户信息
	 * 
	 * @return 成功处理返回"SUCCESS",否则提示失败
	 */
	public String doEdit() {
		try {

			String id = getParams().getParameterAsString("editPersonalId");
			if (id != null && !id.equals("")) {
				UserProcess process =  (UserProcess)ProcessFactory.createProcess(
						UserProcess.class);
				UserSetupProcess uSprocess = (UserSetupProcess) ProcessFactory
						.createProcess(UserSetupProcess.class);
				UserSetupVO userSetup = uSprocess.getUserSetupByUserId(id);
				UserVO user = (UserVO) process.doView(id);
				user.setUserSetup(userSetup);
				setContent(user);
				

				//获取EmailUser
				EmailUser emailUser1 = new EmailUser();
				ParamsTable params1 = new ParamsTable();
				params1.setParameter("t_name", user.getLoginno());
				EmailUserProcess euserpro = (EmailUserProcess) ProcessFactory.createProcess(EmailUserProcess.class);
				DataPackage<EmailUser> dataEmailUser = euserpro.doQuery(params1);
				if(dataEmailUser.rowCount >= 1){
					for(Iterator<EmailUser> ite1 = dataEmailUser.datas.iterator();ite1.hasNext();){
						 emailUser1 = ite1.next();
						if(emailUser1 != null){
							break;
						}
					}
				}
				setEmailUser(emailUser1);
				
				//获取当前用户的所有角色
				Collection<RoleVO> userRoles = user.getRoles();
				RoleVO roleVO = new RoleVO();
				
				UserDefinedProcess udprocss=(UserDefinedProcess) ProcessFactory.createProcess(UserDefinedProcess.class);
				String applicationid = application;
				params = new ParamsTable();
				params.setParameter("t_applicationid", applicationid);
				params.setParameter("t_userId", id);
				params.setParameter("_orderby", "id");
				DataPackage<UserDefined> dataPackage=udprocss.doQuery(params);
				if(dataPackage.rowCount > 0){
					for(Iterator<UserDefined> ite1 = dataPackage.datas.iterator();ite1.hasNext();){
						userDefined = ite1.next();
					}
				}else{
					params = new ParamsTable();
					params.setParameter("t_applicationid", applicationid);
					params.setParameter("n_published", true);
					params.setParameter("_orderby", "id");
					DataPackage<UserDefined> dataPackage1=udprocss.doQuery(params);
					if(dataPackage1.rowCount>0){
						//遍历相同软件下的不同首页
						for(Iterator<UserDefined> ite1 = dataPackage1.datas.iterator();ite1.hasNext();){
							userDefined = ite1.next();
							//获取首页的角色
							String roleIds = userDefined.getRoleIds();
							if(!StringUtil.isBlank(roleIds)){
								String[] userRoleIds = roleIds.split(",");
								for(int i=0;i<userRoleIds.length;i++){
									if(userRoles.size()>0){
										for(Iterator<RoleVO> ite2 = userRoles.iterator();ite2.hasNext();){
											roleVO = ite2.next();
											if(userRoleIds[i].equals(roleVO.getId())){
												// 前台用户设置信息回显
												setUserDefined(userDefined);
												return SUCCESS;
											}
										}
									}
								}
							}
							
						}
					}					
				}
			} else {
				// 根据ID获取当前用户对象,并返回跳转页面字符串
				String returnString = super.doEdit();
				return returnString;
			}
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("", e.getMessage());
			return INPUT;
		}
		return SUCCESS;
	}
	
	/**
	 * 保存个人信息
	 * 
	 * @return 成功处理返回"SUCCESS",否则提示失败
	 * @throws Exception
	 */
	public String doSavePersonal() throws Exception {
		try {
			UserVO user = (UserVO) getContent();
			((UserProcess) process).doPersonalUpdate(user);
			setContent(user);
			this.addActionMessage("{*[Save_Success]*}");
		} catch (ExistNameException e) {
			this.addFieldError("1", e.getMessage());
		}
		return SUCCESS;
	}

	/**
	 * 删除用户信息
	 * 
	 * @return 成功处理返回"SUCCESS",否则返回"ERROR"
	 */
	public String doDelete() {
		try {
			// 删除网盘信息
			NetDiskProcess netDiskProcess = (NetDiskProcess) ProcessFactory
					.createProcess(NetDiskProcess.class);
			netDiskProcess.doRemove(_selects);
			
			// 删除邮件用户
			HttpServletRequest request = ServletActionContext.getRequest();
			EmailUserHelper.removeEmailUsers(_selects, request);

			String rtn = super.doDelete();
			if(SUCCESS.equals(rtn)){
				/**
				 * 增加了xmpp的消息发送,此消息将发送到obpm-spark的各个客户端
				 */
				sendNotification();
			}
			return rtn;
		} catch (Exception e) {
			addFieldError("", e.getMessage());
			doList();
			return INPUT;
		}
	}

	/**
	 * 获取全部角色
	 * 
	 * @return 角色集合
	 * @throws Exception
	 */
	public Collection<RoleVO> get_allRoles() throws Exception {
		Collection<RoleVO> rtn = new ArrayList<RoleVO>();
		RoleProcess rp = (RoleProcess) ProcessFactory
				.createProcess(RoleProcess.class);
		Collection<RoleVO> roles = rp.doSimpleQuery(null, getApplication());
		if (roles != null) {
			rtn = roles;
		}
		return rtn;
	}

	/**
	 * 获取已加入软件的角色
	 * 
	 * @return 软件角色集合
	 * @throws Exception
	 */
	public Map<String, String> get_softRoles() throws Exception {
		// Collection rtn = new ArrayList();
		Map<String, String> map = new LinkedHashMap<String, String>();
		ParamsTable param = this.getParams();
		String domainid = param.getParameterAsString("domain");
		DomainProcess domainProcee = (DomainProcess) ProcessFactory
				.createProcess(DomainProcess.class);
		DomainVO domain = (DomainVO) domainProcee.doView(domainid);
		if (domain != null) {
			Collection<ApplicationVO> applications = domain.getApplications();
			if (applications != null) {
				Iterator<ApplicationVO> iter = applications.iterator();
				while (iter.hasNext()) {
					ApplicationVO application = iter.next();
					String applicationid = application.getId();
					RoleProcess rp = (RoleProcess) ProcessFactory
							.createProcess(RoleProcess.class);
					Collection<RoleVO> roles = rp
							.getRolesByApplication(applicationid);
					// map.put(applicationid, roles);
					Iterator<RoleVO> iter_roles = roles.iterator();
					while (iter_roles.hasNext()) {
						RoleVO role = iter_roles.next();
						// rtn.add(application.getName() + "-" +
						// role.getName());
						map.put(role.getId(), application.getName() + "-"
								+ role.getName());
					}
				}
			}
		}
		return map;
	}

	/**
	 * 根据域获取域所属软件的角色
	 * 
	 * @return 软件角色集合
	 * @throws Exception
	 */
	public Collection<RoleVO> get_domainOfRoles() throws Exception {
		Collection<RoleVO> rtn = new ArrayList<RoleVO>();
		ParamsTable param = this.getParams();
		String domainid = getDomain();
		if (StringUtil.isBlank(domainid))
			domainid = param.getParameterAsString("domain");
		else if (StringUtil.isBlank(domainid))
			domainid = param.getParameterAsString("t_domainid");
		DomainProcess domainProcee = (DomainProcess) ProcessFactory
				.createProcess(DomainProcess.class);
		DomainVO domain = (DomainVO) domainProcee.doView(domainid);
		if (domain != null) {
			Collection<ApplicationVO> applications = domain.getApplications();
			if (applications != null) {
				Iterator<ApplicationVO> iter = applications.iterator();
				while (iter.hasNext()) {
					ApplicationVO application = iter.next();
					String applicationid = application.getId();
					RoleProcess rp = (RoleProcess) ProcessFactory
							.createProcess(RoleProcess.class);
					Collection<RoleVO> roles = rp
							.getRolesByApplication(applicationid);
					rtn.addAll(roles);
				}
			}
		}
		return rtn;
	}

	public String getDomain() {
		if (domain != null && domain.trim().length() > 0) {
			return domain;
		} else {
			return (String) getContext().getSession().get(
					Web.SESSION_ATTRIBUTE_DOMAIN);
		}
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getSuperiorid() {
		UserVO content = (UserVO) this.getContent();
		if (content.getSuperior() != null) {
			superiorid = content.getSuperior().getId();
		}

		return superiorid;
	}

	public void setSuperiorid(String superiorid) throws Exception {
		this.superiorid = superiorid;

		if (!StringUtil.isBlank(superiorid)) {
			UserVO content = (UserVO) this.getContent();
			UserVO superior = (UserVO) process.doView(superiorid);
			if (superior != null) {
				content.setSuperior(superior);
				content.setLevel(superior.getLevel() + 1);
			}
		}
	}

	/**
	 * 获取用户等级树
	 * 
	 * @return 用户等级树
	 * @throws Exception
	 */
	public Map<String, String> getReportTree() throws Exception {

		UserProcess up = (UserProcess) process;

		ParamsTable params = new ParamsTable();
		params.setParameter("domain", getDomain());

		Collection<?> userList = up.doSimpleQuery(params);
		Map<String, String> tree = new LinkedHashMap<String, String>();
		tree.put("", "{*[None]*}");
		tree.putAll(((UserProcess) process).deepSearchTree(userList, null,
				getContent().getId(), 0));
		return tree;
	}

	/**
	 * 获取所有用户
	 * 
	 * @return 用户列表
	 * @throws Exception
	 */
	public Map<String, String> getAllUsers() throws Exception {

		UserProcess up = (UserProcess) process;

		String domainid = this.domain;
		Collection<UserVO> userList = up.queryByDomain(domainid);
		String id = getContent().getId();
		Map<String, String> tree = new LinkedHashMap<String, String>();
		tree.put("", "{*[None]*}");
		for (Iterator<UserVO> it = userList.iterator(); it.hasNext();) {
			UserVO user = it.next();
			if (!user.getId().equals(id) && !StringUtil.isBlank(user.getId())
					&& !StringUtil.isBlank(user.getName())) {
				tree.put(user.getId(), user.getName());
			}
		}
		return tree;
	}

	public String doSelectUser() throws Exception {
		return SUCCESS;
	}

	public String linkmen() throws Exception {
		UserProcess up = (UserProcess) process;
		up.listLinkmen(getParams());
		return SUCCESS;
	}

	/**
	 * 保存并新建用户信息
	 * 
	 * @return "SUCCESS"表示成功处理,否则返回错误提示
	 * 
	 */
	public String doSaveAndNew() {
		try {

			UserVO user = (UserVO) getContent();
			// user.setUserRoleSets(get_Roles());

			set_rolesids(null);
			set_departmentids(null);

			if (process.doView(user.getId()) == null) {
				process.doCreate(user);
			} else {
				process.doUpdate(user);
			}

			this.setContent(new UserVO());

			this.addActionMessage("{*[Save_Success]*}");
			
			// 关联或创建邮件用户
			HttpServletRequest request = ServletActionContext.getRequest();
			EmailUserHelper.checkAndCreateEmailUser(user, request);
			/**
			 * 增加了xmpp的消息发送,此消息将发送到obpm-spark的各个客户端
			 */
			sendNotification();
			return SUCCESS;
		} catch (Exception e) {
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}
	}

	/**
	 * 获取一个企业域下的所有应用
	 * 
	 * @author Bluce
	 * @return 应用集合
	 * @throws Exception
	 * @date 2010-05-10
	 */
	public Collection<ApplicationVO> get_applicationlist() throws Exception {
		DomainVO vo = DomainHelper.getDomainVO(domain);
		_applicationlist = new ArrayList<ApplicationVO>();
		_applicationlist.addAll(vo.getApplications());
		return _applicationlist;
	}

	/**
	 * 设置角色
	 * 
	 * @author Bluce
	 * @param _roleList
	 */
	public void set_roleList(Collection<RoleVO> _roleList) {
		this._roleList = _roleList;
	}

	public Collection<RoleVO> getRoleList() {
		return this._roleList;
	}
	
	
	public String doTreeList() {
		try {
			//this.validateQueryParams();
			ParamsTable params = getParams();
			String domain = params.getParameterAsString("domain");
			String departid = params.getParameterAsString("departid");

			// DepartmentProcess departmentProcess = (DepartmentProcess)
			// ProcessFactory.createProcess(DepartmentProcess.class);
			if (domain != null && domain.trim().length() > 0) {
				if (departid == null || "".equals(departid)) {
					params.setParameter("t_domainid", domain);
					WebUser user = getUser();
					setDatas(process.doQuery(params, user));
				} else {
					params.setParameter("t_domainid", domain);
					params.setParameter("sm_userDepartmentSets.departmentId",
							departid);
					WebUser user = getUser();
					setDatas(process.doQuery(params, user));
				}

			}
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("", e.getMessage());
			return INPUT;
		}

	}

	/**
	 * 获取前台选择的用户角色ids,保存进数据库
	 * 
	 * @author Bluce
	 * @param _roleids
	 * @throws Exception
	 * @date 2010-05-10
	 */
	//@SuppressWarnings("unchecked")
	public void set_rolesids(Collection<String> _roleids) throws Exception {
		Map<?, ?> m = getContext().getParameters();
		Object obj = m.get("_roleSelectItem");// 选中的角色
		String tmp[] = null;
		if (obj instanceof String[] && ((String[]) obj).length > 0) {
			tmp = (String[]) obj;
		}
		UserVO user = (UserVO) getContent();
		if (tmp != null) {
			for (int i = 0; i < tmp.length; i++) {
				RoleProcess rp = (RoleProcess) ProcessFactory
						.createProcess(RoleProcess.class);
				RoleVO role = (RoleVO) rp.doView(tmp[i]);
				// 去掉勾选应用时的干扰
				if (role != null) {
					UserRoleSet set = new UserRoleSet(user.getId(), role
							.getId());
					user.getUserRoleSets().add(set);
				}
			}
		}
	}

	public UserDefined getUserDefined() {
		return userDefined;
	}

	public void setUserDefined(UserDefined userDefined) {
		this.userDefined = userDefined;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public void set_applicationlist(Collection<ApplicationVO> _applicationlist) {
		this._applicationlist = _applicationlist;
	}

	public String getStartProxyTime() {
		UserVO user = (UserVO) getContent();
		try {
			if (user.getStartProxyTime() != null && !user.getStartProxyTime().equals("")) {
				this.startProxyTime = DateUtil.format(user.getStartProxyTime(), "yyyy-MM-dd HH:mm:ss");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return startProxyTime;
	}

	public void setStartProxyTime(String startProxyTime) {
		if(startProxyTime!=null && !startProxyTime.equals("")){
			try {
				UserVO user = (UserVO) getContent();
				Date date = DateUtil.parseDate(startProxyTime,
						"yyyy-MM-dd HH:mm:ss");
				user.setStartProxyTime(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			this.startProxyTime = startProxyTime;
		}
	}

	public String getEndProxyTime() {
		UserVO user = (UserVO) getContent();
		try {
			if (user.getEndProxyTime() != null && !user.getEndProxyTime().equals("")) {
				this.endProxyTime = DateUtil.format(user.getEndProxyTime(), "yyyy-MM-dd HH:mm:ss");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return endProxyTime;
	}

	public void setEndProxyTime(String endProxyTime) {
		if(endProxyTime!=null && !endProxyTime.equals("")){
			try {
				UserVO user = (UserVO) getContent();
				Date date = DateUtil.parseDate(endProxyTime,
						"yyyy-MM-dd HH:mm:ss");
				user.setEndProxyTime(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			this.endProxyTime = endProxyTime;
		}
	}

	public EmailUser getEmailUser() {
		return emailUser;
	}

	public void setEmailUser(EmailUser emailUser) {
		this.emailUser = emailUser;
	}
	
	/**
	 * xmpp消息发送,部门的增删改将触发此动作
	 * 
	 * 通知所有的obpm-spark客户端更新企业联系人列表
	 * 
	 * @author keezzm
	 * @date 2011-08-17
	 * @last modified by keezzm
	 */
	private void sendNotification() {
		try {
			// 发送XMPP信息
			ContactNotification notification = ContactNotification
					.newInstance(ContactNotification.ACTION_UPDATE);
			SuperUserProcess superUserProcess = (SuperUserProcess) ProcessFactory
					.createProcess(SuperUserProcess.class);
			/**
			 * 默认发送者为admin
			 */
			notification.setSender(superUserProcess.getDefaultAdmin());
			/**
			 * 添加接收者,为所有在线用户
			 */
			DataPackage<WebUser> dataPackage = OnlineUsers
					.doQuery(new ParamsTable());
			Collection<WebUser> users = dataPackage.getDatas();
			for (Iterator<WebUser> iterator = users.iterator(); iterator
					.hasNext();) {
				WebUser webUser = iterator.next();
				notification.addReceiver(webUser);
			}
			XMPPSender.getInstance().processNotification(notification);
		} catch (Exception e) {
			LOG.warn("XMPP Notification Error", e);
		}
	}

}
