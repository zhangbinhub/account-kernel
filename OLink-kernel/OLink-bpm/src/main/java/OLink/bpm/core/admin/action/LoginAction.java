package OLink.bpm.core.admin.action;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import OLink.bpm.constans.Environment;
import OLink.bpm.constans.Web;
import OLink.bpm.core.superuser.ejb.SuperUserProcess;
import OLink.bpm.core.superuser.ejb.SuperUserVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.user.action.WebUser;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.ActionSupport;

public class LoginAction extends ActionSupport {
	private String username;

	private String password;

	private String domainName;

	private String checkcode;

	private static final long serialVersionUID = -4277772173056045618L;

	public LoginAction() throws Exception {
	}

	/**
	 * 登录
	 * 
	 * @return "SUCCESS","ERROR"
	 * @throws Exception
	 */
	public String doLogin() {
		// file and to also use the specified CallbackHandler.

		boolean flag = false;
		Cookie[] cookies = ServletActionContext.getRequest().getCookies();
		Cookie pwdErrorTimes = null;
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				if (cookies[i].getName().equalsIgnoreCase("adminPWDErrorTimes")) {
					pwdErrorTimes = cookies[i];
				}
			}
			if (pwdErrorTimes == null) {
				pwdErrorTimes = new Cookie("adminPWDErrorTimes", "0");
				pwdErrorTimes.setMaxAge(60 * 60 * 24);
			}
		}

		try {
			HttpSession session = ServletActionContext.getRequest().getSession();
			if (pwdErrorTimes != null && isExceedTimes(pwdErrorTimes)) {
				String code = (String) session.getAttribute(Web.SESSION_ATTRIBUTE_CHECKCODE);
				if (this.checkcode == null || this.checkcode.trim().length()<=0) {
					throw new Exception("{*[page.login.character]*}");
				}else if (!this.checkcode.equalsIgnoreCase(code)) {
					throw new Exception("{*[core.security.character.error]*}");
				}
			}
			SuperUserProcess sprocess = (SuperUserProcess) ProcessFactory.createProcess(SuperUserProcess.class);
			SuperUserVO user = sprocess.login(username, password);

			if (user != null && user.getStatus() == 1) {
				WebUser webUser = new WebUser(user);
				setActionContext(webUser, session, pwdErrorTimes);
			}
		} catch (Exception e) {
			this.addFieldError("1", e.getMessage());
			setErrorTimes(pwdErrorTimes);
			if (isExceedTimes(pwdErrorTimes))
				ServletActionContext.getRequest().setAttribute("showCode", Boolean.valueOf(true));
			ServletActionContext.getResponse().addCookie(pwdErrorTimes);
			return ERROR;
		}
		if (flag) {
			return SUCCESS;
		}
		return "manage";
	}

	/**
	 * 设置错误次数
	 * 
	 * @param cookie
	 * @return
	 */
	public void setErrorTimes(Cookie cookie) {
		String val = cookie.getValue();
		if (val != null && val.trim().length() > 0) {
			int errorTime = Integer.parseInt(val);
			cookie.setMaxAge(60 * 60 * 24);
			cookie.setValue(String.valueOf((errorTime + 1)));
		}
	}

	/**
	 * 清除错误次数
	 * 
	 * @param cookie
	 */
	public void clearErrorTimes(Cookie cookie) {
		cookie.setMaxAge(0);
		cookie.setValue(null);
	}

	/**
	 * 检查错误次数
	 * 
	 * @param cookie
	 * @return
	 */
	public boolean isExceedTimes(Cookie cookie) {
		String val = cookie.getValue();
		int errorTime = Integer.parseInt(val);
		return errorTime > 3;
	}

	/**
	 * 注销
	 * 
	 * @return SUCCESS
	 * @throws Exception
	 */
	public String doLogout() {
		return SUCCESS;
	}

	/**
	 * 返回密码
	 * 
	 * @return 返回密码
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * 设置密码
	 * 
	 * @param password
	 *            密码
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * 返回用户帐号
	 * 
	 * @return
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * 设置用户帐号
	 * 
	 * @param username
	 *            用户帐号
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	public String getDomainName() {
		return domainName;
	}

	public String getCheckcode() {
		return checkcode;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public void setCheckcode(String checkcode) {
		this.checkcode = checkcode;
	}

	public void setActionContext(WebUser webUser, HttpSession session, Cookie pwdErrorTimes) {
		// OnlineUserBindingListener oluser = new
		// OnlineUserBindingListener(webUser);
		// session.setAttribute(Web.SESSION_ATTRIBUTE_ONLINEUSER, oluser);
		session.setAttribute(Web.SESSION_ATTRIBUTE_USER, webUser);
		// 在环境中设置context path
		Environment.getInstance().setContextPath(ServletActionContext.getRequest().getContextPath());
		clearErrorTimes(pwdErrorTimes);
		ServletActionContext.getResponse().addCookie(pwdErrorTimes);
	}
}
