package OLink.bpm.core.sysconfig.action;

import java.io.IOException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import javax.servlet.http.HttpServletResponse;

import OLink.bpm.core.department.ejb.DepartmentProcess;
import OLink.bpm.core.department.ejb.DepartmentVO;
import OLink.bpm.core.domain.ejb.DomainVO;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.core.sysconfig.ejb.SysConfigProcess;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.domain.ejb.DomainProcess;
import OLink.bpm.core.sysconfig.ejb.AuthConfig;
import OLink.bpm.core.sysconfig.ejb.CheckoutConfig;
import OLink.bpm.core.sysconfig.ejb.EmailConfig;
import OLink.bpm.core.sysconfig.ejb.ImConfig;
import OLink.bpm.core.sysconfig.ejb.LdapConfig;
import OLink.bpm.core.sysconfig.ejb.SysConfigProcessBean;
import OLink.bpm.gkmsapi.GK_MS_API;
import OLink.bpm.gkmsapi.XMLParser;
import OLink.bpm.util.ProcessFactory;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionSupport;

public class SysConfigAction extends ActionSupport {
	
	private static final long serialVersionUID = -1950571738993994028L;
	
	private AuthConfig authConfig;
	private LdapConfig ldapConfig;
	private EmailConfig emailConfig;
	private ImConfig imConfig;
	private CheckoutConfig checkoutConfig;
	
	private int _tabcount = 1;
	
	public AuthConfig getAuthConfig() {
		return authConfig;
	}

	public void setAuthConfig(AuthConfig authConfig) {
		this.authConfig = authConfig;
	}

	public EmailConfig getEmailConfig() {
		return emailConfig;
	}

	public void setEmailConfig(EmailConfig emailConfig) {
		this.emailConfig = emailConfig;
	}

	public LdapConfig getLdapConfig() {
		return ldapConfig;
	}

	public void setLdapConfig(LdapConfig ldapConfig) {
		this.ldapConfig = ldapConfig;
	}
	
	public ImConfig getImConfig() {
		return imConfig;
	}

	public void setImConfig(ImConfig imConfig) {
		this.imConfig = imConfig;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public CheckoutConfig getCheckoutConfig() {
		return checkoutConfig;
	}

	public void setCheckoutConfig(CheckoutConfig checkoutConfig) {
		this.checkoutConfig = checkoutConfig;
	}

	private boolean testLDAP() {
		Hashtable<Object, Object> env = new Hashtable<Object, Object>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, ldapConfig.getUrl());
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		try {
			new InitialDirContext(env);
			return true;
		} catch (NamingException e) {
			this.addFieldError("ldapConfig.url", e.getMessage());
			return false;
		}
		
	}
	
	private boolean isExistEmptyLdapConfigField() {
		boolean isExist = false;
		if(ldapConfig.getUrl() == null || "".equals(ldapConfig.getUrl())) {
			this.addFieldError("ldapConfig.url", "{*[ldapConfig.url.need]*}");
			isExist = true;
		}
		if(ldapConfig.getBaseDN() == null || "".equals(ldapConfig.getBaseDN())) {
			this.addFieldError("ldapConfig.baseDN", "{*[ldapConfig.baseDN.need]*}");
			isExist = true;
		}
		if(ldapConfig.getDirStructure() == null || "".equals(ldapConfig.getDirStructure())) {
			this.addFieldError("ldapConfig.dirStructure", "{*[ldapConfig.dirStructure.need]*}");
			isExist = true;
		}
		if(ldapConfig.getLoginno_() == null || "".equals(ldapConfig.getLoginno_())) {
			this.addFieldError("ldapConfig.loginno", "{*[ldapConfig.loginno.need]*}");
			isExist = true;
		}
		if(ldapConfig.getLoginpwd_() == null || "".equals(ldapConfig.getLoginpwd_())) {
			this.addFieldError("ldapConfig.loginpwd", "{*[ldapConfig.loginpwd.need]*}");
			isExist = true;
		}
		return isExist;
	}
	
	private boolean validateDoSave() {
		if(authConfig != null && authConfig.isLdaptLogin()) {
			if(ldapConfig == null) {
				this.addFieldError("ldapConfig", "ldapConfig is null!");
				return false;
			}
			if(isExistEmptyLdapConfigField()){
				return false;
			}
			if(!this.ldapConfig.isValidDirStructure()) {
				this.addFieldError("ldapConfig.dirStructure.format", "{*[ldap.dirStructure.format.illegal]*}");
				return false;
			}
			if(!this.ldapConfig.isValidBaseDN()) {
				this.addFieldError("ldapConfig.baseDN.format", "{*[ldap.baseDN.format.illegal]*}");
				return false;
			}
			if(!testLDAP())
				return false;
		}
		return true;
	}

	public String doSave() {
		if(!validateDoSave())return INPUT;
		SysConfigProcess sysConfigProcess = new SysConfigProcessBean();
		try {
			sysConfigProcess.save(authConfig, ldapConfig, emailConfig,imConfig,checkoutConfig);
			this.addActionMessage("{*[Save_Success]*}");
		} catch (Exception e) {
			e.printStackTrace();
			this.addFieldError("save.error", e.getMessage());
			return INPUT;
		}
		return SUCCESS;
	}
	
	/**
	 * 显示配置
	 * @return
	 */
	public String doView() {
		SysConfigProcess sysConfigProcess = new SysConfigProcessBean();
		try {
			this.authConfig = sysConfigProcess.getAuthConfig();
			this.ldapConfig = sysConfigProcess.getLdapConfig();
			this.emailConfig = sysConfigProcess.getEmailConfig();
			this.imConfig = sysConfigProcess.getImConfig();
			this.checkoutConfig = sysConfigProcess.getCheckoutConfig();
		} catch (Exception e) {
			e.printStackTrace();
			this.addFieldError("error", e.getMessage());
			return INPUT;
		}
		
		return SUCCESS;
	}
	
	/**
	 * 同步域数据到gke
	 * @return
	 */
	public String domainSynchronousToGke(){
		HttpServletResponse response = null;
		try{
			ActionContext ctx = ActionContext.getContext();
			response = (HttpServletResponse) ctx.get(ServletActionContext.HTTP_RESPONSE);
			response.setContentType("text/html;charset=UTF-8");
			DomainProcess domainProcess = (DomainProcess)ProcessFactory.createProcess(DomainProcess.class);
			Collection<DomainVO> domains = domainProcess.getAllDomain();
			XMLParser xml = null;
			for (Iterator<DomainVO> iterator1 = domains.iterator(); iterator1.hasNext();){
				DomainVO domainVO = iterator1.next();
				xml = GK_MS_API.getInstance().getUgInfo(domainVO.getId());
				if(xml.getCode()==0){
					xml = GK_MS_API.getInstance().updUg(domainVO);
					if(xml.getCode()!=0){
						response.getWriter().print("{*[Synchronize.fail]*}："+xml.getMessage());
						break;
					}
				}else{
					xml = GK_MS_API.getInstance().addUg(domainVO);
					if(xml.getCode()!=0){
						response.getWriter().print("{*[Synchronize.fail]*}："+xml.getMessage());
						break;
					}
				}
			}
			if(xml.getCode()==0){
				response.getWriter().print("{*[Synchronize.successful]*}");
			}
			response.getWriter().close();
		}catch(Exception e){
			e.printStackTrace();
			try {
				response.getWriter().print("{*[Synchronize.fail]*}: "+e.getMessage());
				response.getWriter().close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return NONE;
	}
	
	/**
	 * 同步部门到GKE
	 * @return
	 */
	public String departmentSynchronousToGke(){
		HttpServletResponse response = null;
		try{
			ActionContext ctx = ActionContext.getContext();
			response = (HttpServletResponse) ctx.get(ServletActionContext.HTTP_RESPONSE);
			response.setContentType("text/html;charset=UTF-8");
			DomainProcess domainProcess = (DomainProcess)ProcessFactory.createProcess(DomainProcess.class);
			DepartmentProcess departmentProcess = (DepartmentProcess)ProcessFactory.createProcess(DepartmentProcess.class);
			//域集合
			Collection<DomainVO> domains = domainProcess.getAllDomain();
			XMLParser xml = null;
			for (Iterator<DomainVO> iterator = domains.iterator(); iterator.hasNext();) {
			DomainVO domainVO = iterator.next();
			
			for (Iterator<DepartmentVO> iterator3 = domainVO.getDepartments().iterator(); iterator3.hasNext();) {
				DepartmentVO departmentVO = iterator3.next();
				if(departmentVO.getSuperior()==null){
					xml = GK_MS_API.getInstance().getUgInfo(departmentVO.getId());
					if(xml.getCode()==0){
						xml = GK_MS_API.getInstance().updUg(departmentVO,domainVO.getId());
						if(xml.getCode()!=0){
							response.getWriter().print("{*[Synchronize.fail]*}："+xml.getMessage());
							break;
						}else{
							Collection<DepartmentVO> departmentVOS = departmentProcess.getUnderDeptList(departmentVO.getId());
							for (Iterator<DepartmentVO> iterator2 = departmentVOS.iterator(); iterator2.hasNext();) {
								DepartmentVO departmentVO11 = iterator2.next();
								xml = GK_MS_API.getInstance().getUgInfo(departmentVO11.getId());
								if(xml.getCode()==0){
									xml = GK_MS_API.getInstance().updUg(departmentVO11,domainVO.getId());
									if(xml.getCode()!=0){
										response.getWriter().print("{*[Synchronize.fail]*}："+xml.getMessage());
										break;
									}
								}else{
									xml = GK_MS_API.getInstance().addUg(departmentVO11,domainVO.getId());
									if(xml.getCode()!=0){
										response.getWriter().print("{*[Synchronize.fail]*}："+xml.getMessage());
										break;
									}
								}
							}
							if(xml.getCode()!=0){
								break;
							}
						}
					}else{
						xml = GK_MS_API.getInstance().addUg(departmentVO,domainVO.getId());
						if(xml.getCode()!=0){
							response.getWriter().print("{*[Synchronize.fail]*}："+xml.getMessage());
							break;
						}else{
							Collection<DepartmentVO> departmentVOS = departmentProcess.getUnderDeptList(departmentVO.getId());
							for (Iterator<DepartmentVO> iterator2 = departmentVOS.iterator(); iterator2.hasNext();) {
								DepartmentVO departmentVO11 = iterator2.next();
								xml = GK_MS_API.getInstance().getUgInfo(departmentVO11.getId());
								if(xml.getCode()==0){
									xml = GK_MS_API.getInstance().updUg(departmentVO11,domainVO.getId());
									if(xml.getCode()!=0){
										response.getWriter().print("{*[Synchronize.fail]*}："+xml.getMessage());
										break;
									}
								}else{
									xml = GK_MS_API.getInstance().addUg(departmentVO11,domainVO.getId());
									if(xml.getCode()!=0){
										response.getWriter().print("{*[Synchronize.fail]*}："+xml.getMessage());
										break;
									}
								}
							}
							if(xml.getCode()!=0){
								break;
							}
						}
					}
				}
			}
				if(xml.getCode()!=0){
					break;
				}
			}
			if(xml.getCode()==0){
				response.getWriter().print("{*[Synchronize.successful]*}");
			}
		}catch(Exception e){
			e.printStackTrace();
			try {
				response.getWriter().print("{*[Synchronize.fail]*}: "+e.getMessage());
				response.getWriter().close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return NONE;
	}
	
	
	/**
	 * 同步用户到GKE
	 * @return
	 */
	public String userSynchronousToGke(){
		HttpServletResponse response = null;
		try{
			ActionContext ctx = ActionContext.getContext();
			response = (HttpServletResponse) ctx.get(ServletActionContext.HTTP_RESPONSE);
			response.setContentType("text/html;charset=UTF-8");
			DepartmentProcess departmentProcess = (DepartmentProcess)ProcessFactory.createProcess(DepartmentProcess.class);
			Collection<DepartmentVO> departments = departmentProcess.doSimpleQuery(new ParamsTable());
			XMLParser xml = GK_MS_API.getInstance().getAllUser();
			Map<String,String> userids = null;
			if(xml.getCode()==0){
				userids = xml.getUserids();
			}
			for (Iterator<DepartmentVO> iterator3 = departments.iterator(); iterator3.hasNext();) {
				DepartmentVO departmentVO = iterator3.next();
				if(departmentVO.getUsers()!=null && departmentVO.getUsers().size()>0){
					for (Iterator<UserVO> iterator2 = departmentVO.getUsers().iterator(); iterator2.hasNext();) {
						UserVO userVO = iterator2.next();
						xml = GK_MS_API.getInstance().getUser(userVO.getLoginno());
						if(xml.getCode()==0){
							xml = GK_MS_API.getInstance().updUser(userVO);
							if(xml.getCode()!=0){
								response.getWriter().print("{*[Synchronize.fail]*}："+xml.getMessage());
								break;
							}
						}else{
							xml = GK_MS_API.getInstance().addUser(userVO);
							if(xml.getCode()!=0){
								response.getWriter().print("{*[Synchronize.fail]*}："+xml.getMessage());
								break;
							}
						}
						userids.remove(userVO.getLoginno());
					}
					if(xml.getCode()!=0){
						break;
					}
				}
			}
			if(userids!=null){
				for (Iterator<String> iterator = userids.values().iterator(); iterator.hasNext();) {
					String gid = iterator.next();
					GK_MS_API.getInstance().delUser(gid);
				}
	
				if(xml.getCode()==0){
					response.getWriter().print("{*[Synchronize.successful]*}");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			try {
				response.getWriter().print("{*[Synchronize.fail]*}: "+e.getMessage());
				response.getWriter().close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return NONE;
	}

	public int get_tabcount() {
		return _tabcount;
	}

	public void set_tabcount(int tabcount) {
		_tabcount = tabcount;
	}
	
}
