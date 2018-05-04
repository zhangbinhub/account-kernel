package OLink.bpm.core.superuser.action;

import java.util.Collection;
import java.util.Map;

import OLink.bpm.constans.Web;
import OLink.bpm.core.superuser.ejb.SuperUserProcess;
import OLink.bpm.core.superuser.ejb.SuperUserVO;
import OLink.bpm.base.action.BaseAction;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import eWAP.core.ResourcePool;
import eWAP.core.Tools;

public class SuperUserAction extends BaseAction<SuperUserVO> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3690803945441341287L;

	/**
	 * @SuppressWarnings 不确定存放的是对象集合还是ID集合
	 */
	@SuppressWarnings("unchecked")
	private Collection domains;

	/**
	 * @SuppressWarnings 不确定存放的是对象集合还是ID集合
	 */
	@SuppressWarnings("unchecked")
	private Collection applications;

	public Collection<?> getDomains() {
		return domains;
	}

	public Collection<?> getApplications() {
		return applications;
	}

	public void setDomains(Collection<?> domains) {
		this.domains = domains;
	}

	public void setApplications(Collection<?> applications) {
		this.applications = applications;
	}

	/**
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public SuperUserAction() throws Exception {
		super(ProcessFactory.createProcess(SuperUserProcess.class), new SuperUserVO());
	}

	public String doNew() {
		setContent(new SuperUserVO());
		return SUCCESS;
	}
	
	private boolean doValidateSave(SuperUserVO user) {
		if(user.getId() != null) {
			try {
				SuperUserProcess superUserProcess = (SuperUserProcess)ProcessFactory.createProcess(SuperUserProcess.class);
				SuperUserVO superuser = (SuperUserVO)superUserProcess.doView(user.getId());
				if(superuser != null && (user.isSuperAdmin() != superuser.isSuperAdmin() || user.isDomainAdmin() != superuser.isDomainAdmin()
						||user.isDeveloper() != superuser.isDeveloper())) {
					if(superuser.getDomains().size() > 0)
						return false;
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	/**
	 * 保存用户信息
	 * 
	 * @return "SUCCESS"表示成功处理,否则返回错误提示
	 */
	public String doSave() {
		try {
			SuperUserVO user = (SuperUserVO) getContent();
//			if(!doValidateSave(user)) {
//				this.addFieldError("user.exsit.domain", "{*[user.be.exist.domain]*}");
//				return INPUT;
//			}
			super.doSave();
			setContent(user);
			return SUCCESS;
		} catch (Exception e) {
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}
	}

	/**
	 * 返回用户密码
	 * 
	 * @return
	 */
	public String get_password() {
		SuperUserVO user = (SuperUserVO) getContent();
		if (user != null && user.getLoginpwd() != null)
			return Web.DEFAULT_SHOWPASSWORD;
		return null;
	}

	/**
	 * 设置用户密码
	 * 
	 * @param _password
	 */
	public void set_password(String _password) {
		SuperUserVO user = (SuperUserVO) getContent();
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
		SuperUserVO user = (SuperUserVO) getContent();
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
		SuperUserVO user = (SuperUserVO) getContent();
		if (strname != null) {
			if (strname.equalsIgnoreCase("true")) {
				user.setStatus(1);
			} else {
				user.setStatus(0);
			}
		}
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
				SuperUserVO user = (SuperUserVO) process.doView(id);
				setContent(user);
			} else {
				return super.doEdit();
			}
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("", e.getMessage());
			return INPUT;
		}
	}

	/**
	 * 保存个人信息
	 * 
	 * @return 成功处理返回"SUCCESS",否则提示失败
	 * @throws Exception
	 */
	public String doSavePersonal() throws Exception {
		((SuperUserProcess) process).doPersonalUpdate(this.getContent());
		this.addActionMessage("{*[Save_Success]*}");
		return SUCCESS;
	}

	/**
	 * 删除用户信息
	 * 
	 * @return 成功处理返回"SUCCESS",否则返回"ERROR"
	 */
	//@SuppressWarnings("unchecked")
	public String doDelete() {
		boolean flag = false;
		try {
			Map<?, ?> session = getContext().getSession();
			WebUser user = (WebUser) session.get(Web.SESSION_ATTRIBUTE_USER);
			String superUserID = user.getId();
			for (int i = 0; i < this.get_selects().length; i++) {
				if (this.get_selects()[i] != null) {
					if (superUserID.equals(get_selects()[i])) {
						addFieldError("1", "{*[core.superuser.referenced]*}");
						flag = true;
						break;
					}
				}
			}
		} catch (Exception e) {
			addFieldError("", e.getMessage());
		}

		if (!flag) {
			String result = super.doDelete();
			if("input".equals(result)) {
				super.doList();
			}
			return result;
		} else {
			super.doList();
			return INPUT;
		}
	}

	public String doAddAdmin() {
		try {
			SuperUserProcess process = (SuperUserProcess) ProcessFactory.createProcess(SuperUserProcess.class);
			ParamsTable params = getParams();
			setDatas(process.getUnJoinedAdminList(params));
		} catch (Exception e) {
			addFieldError("", e.getMessage());
		}

		return SUCCESS;
	}
	
	public String doListByDomain() {
		try {
			ParamsTable table = getParams();
			String domainid = table.getParameterAsString("domain");
			if (StringUtil.isBlank(domainid)) {
				throw new Exception("{*[Query]*} {*[page.obj.dofailure]*}");
			}
			table.setParameter("s_domains.id", domainid);
			return super.doList();
		} catch (Exception e) {
			e.printStackTrace();
			this.addFieldError("1", e.getMessage());
			return SUCCESS;
		}
	}
	
}
