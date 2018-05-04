package OLink.bpm.mobile.login;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.constans.Web;
import OLink.bpm.core.deploy.application.action.ApplicationHelper;
import OLink.bpm.core.deploy.application.ejb.ApplicationProcess;
import OLink.bpm.core.domain.ejb.DomainVO;
import OLink.bpm.core.dynaform.pending.ejb.PendingProcess;
import OLink.bpm.core.dynaform.pending.ejb.PendingProcessBean;
import OLink.bpm.core.dynaform.pending.ejb.PendingVO;
import OLink.bpm.core.links.ejb.LinkVO;
import OLink.bpm.core.permission.action.PermissionHelper;
import OLink.bpm.core.resource.action.ResourceHelper;
import OLink.bpm.core.resource.ejb.ResourceVO;
import OLink.bpm.core.role.ejb.RoleVO;
import OLink.bpm.core.table.constants.MobileConstant;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.user.ejb.UserDefinedProcess;
import OLink.bpm.core.user.ejb.UserProcess;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.core.workflow.storage.runtime.ejb.CirculatorProcess;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import OLink.bpm.util.property.MultiLanguageProperty;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.core.domain.ejb.DomainProcess;
import OLink.bpm.core.dynaform.summary.ejb.SummaryCfgProcess;
import OLink.bpm.core.dynaform.summary.ejb.SummaryCfgVO;
import OLink.bpm.core.user.ejb.UserDefined;
import OLink.bpm.core.workflow.storage.runtime.ejb.Circulator;

import org.apache.log4j.Logger;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionSupport;

public class MbLoginAction extends ActionSupport {

	private String username;
	private String password;
	private String domainname;
	private String _resourceId;
	private String _deep;
	private String _currResourceId;
	private String application;
	private boolean isShowApps;
	private int language;
	private int _pagelines;
	private boolean pendingMore = false;

	private static final long serialVersionUID = -4277772173056045618L;
	private static final Logger LOG = Logger.getLogger(MbLoginAction.class);

	public MbLoginAction() throws Exception {
	}

	/**
	 * 登录
	 * 
	 * @return "SUCCESS","ERROR"
	 * @throws Exception
	 */
	public String doLogin() throws Exception {
		// file and to also use the specified CallbackHandler.
		try {
			HttpSession session = ServletActionContext.getRequest()
					.getSession();
			UserProcess process = (UserProcess) ProcessFactory
					.createProcess(UserProcess.class);

			ApplicationProcess appProcess = (ApplicationProcess) ProcessFactory
					.createProcess(ApplicationProcess.class);

			UserVO user = process.login(username, password, domainname);
			if (user != null && user.getStatus() == 1) {
				WebUser webUser = new WebUser(user);
				// webUser.setDomainid(user.getDomainid());

				String application = appProcess.getDefaultApplication(
						webUser.getDefaultApplication(), webUser).getId();
				// webUser.setApplicationid(application);
				webUser.setDefaultApplication(application);

				UserVO vo = (UserVO) user.clone();
				vo.setDefaultApplication(application);
				vo.setLoginpwd(null);
				process.doUpdateWithCache(vo);

				session.setAttribute(Web.SESSION_ATTRIBUTE_DOMAIN, webUser
						.getDomainid());
				// OnlineUserBindingListener oluser = new
				// OnlineUserBindingListener(webUser);
				// session.setAttribute(Web.SESSION_ATTRIBUTE_ONLINEUSER,
				// oluser);
				session.setAttribute(Web.SESSION_ATTRIBUTE_APPLICATION,
						application);

				String language = MultiLanguageProperty.getName(2);
				session.setAttribute(Web.SESSION_ATTRIBUTE_USERLANGUAGE,
						language);
				// session.setMaxInactiveInterval(20 * 60); // 20 minutes

				session.setAttribute(Web.SESSION_ATTRIBUTE_FRONT_USER, webUser);

				ApplicationHelper helper = new ApplicationHelper();
				// 查找当前域下的所有应用（应该改为查找域个数）
				Collection<ApplicationVO> cols = helper
						.getListByWebUser(webUser);
				if (cols.size() > 1) {
					isShowApps = true;
					setApplication(application);
				}
				// 在环境中设置context path
				// Environment.getInstance().setContextPath(ServletActionContext.getRequest().getContextPath());
				String toXml = toResourceXml(null, null, application, webUser,
						isShowApps, " ss ='" + session.getId() + "'");
				if (toXml != null) {
					session.setAttribute("toXml", toXml);
				}
			}

		} catch (Exception e) {
			this.addFieldError("SystemError", e.getMessage());
			LOG.warn(e);
			return ERROR;
		}
		return SUCCESS;
	}

	public String doSearchResource() throws Exception {
		try {
			HttpServletRequest request = ServletActionContext.getRequest();
			HttpSession session = request.getSession();
			String toXml = toResourceXml(_resourceId, _deep, getApplication(),
					getUser(), isShowApps, "");
			if (toXml != null) {
				session.setAttribute("toXml", toXml);
			}
		} catch (Exception e) {
			this.addFieldError("SystemError", e.getMessage());
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}

	public String doRefresh() throws Exception {
		try {
			HttpServletRequest request = ServletActionContext.getRequest();
			HttpSession session = request.getSession();
			String toXml = toResourceXml(_resourceId, ""
					+ (Integer.parseInt(_deep) + 1), getApplication(),
					getUser(), isShowApps, "");
			if (toXml != null) {
				session.setAttribute("toXml", toXml);
			}
		} catch (Exception e) {
			this.addFieldError("SystemError", e.getMessage());
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}

	private String toResourceXml(String resourceid, String deep,
			String applicationid, WebUser user, boolean isShow, String handler)
			throws Exception {
		PermissionHelper helper = new PermissionHelper();
		ResourceHelper resHelper = new ResourceHelper();
		resHelper.setApplicationid(applicationid);
		helper.setUser(user);
		if (deep == null || deep.equals("0"))
			deep = "1";
		ResourceVO res = null;
		if (resourceid == null || resourceid.trim().equals("")) {
			res = resHelper.getTopResourceByName("Mobile");
		} else {
			res = resHelper.getResourcById(resourceid);
		}
		// boolean topMenu = false;
		Collection<ResourceVO> col = resHelper.searchResourceForMb(res, user
				.getDomainid());
		StringBuffer buffer = new StringBuffer();
		String title = null;
		if (res != null)
			title = res.getDescription();
		if (title == null || title.trim().length() <= 0
				|| title.equals("Mobile"))

			if (res != null)
				title = res.getDescription();
		if (title == null || title.trim().length() <= 0
				|| title.trim().equals("Mobile"))
			title = "主菜单";
		buffer.append("<").append(MobileConstant.TAG_HOMEPAGE).append(" ")
				.append(MobileConstant.ATT_TITLE).append("='" + title + "' ")
				.append(handler + ">");
		buffer.append("<").append(MobileConstant.TAG_HIDDENFIELD).append(" ")
				.append(MobileConstant.ATT_NAME).append("='_backId'>");
		if (res != null && res.getSuperior() != null) {
			buffer.append(res.getSuperior().getId());
		} else {
			buffer.append("mobile");
			// topMenu = true;
		}
		buffer.append("</").append(MobileConstant.TAG_HIDDENFIELD).append(">");

		if (res != null && res.getId() != null) {
			buffer.append("<").append(MobileConstant.TAG_HIDDENFIELD).append(
					" ").append(MobileConstant.ATT_NAME).append(
					"='_resourceId'>");
			buffer.append(res.getId());
			buffer.append("</").append(MobileConstant.TAG_HIDDENFIELD).append(
					">");
		}
		buffer.append("<").append(MobileConstant.TAG_HIDDENFIELD).append(" ")
				.append(MobileConstant.ATT_NAME).append("='isShowApps'>");
		buffer.append(isShowApps);
		buffer.append("</").append(MobileConstant.TAG_HIDDENFIELD).append(">");
		buffer.append("<").append(MobileConstant.TAG_HIDDENFIELD).append(" ")
				.append(MobileConstant.ATT_NAME).append(
						"='application'>" + applicationid + "</").append(
						MobileConstant.TAG_HIDDENFIELD).append(">");
		buffer.append("<").append(MobileConstant.TAG_HIDDENFIELD).append(" ")
				.append(MobileConstant.ATT_NAME).append(
						"='_deep'>" + (Integer.parseInt(deep) - 1) + "</")
				.append(MobileConstant.TAG_HIDDENFIELD).append(">");
		// if (isShowApps && topMenu) {
		addAppsCommands(buffer);
		// }
		if (col != null) {
			sort(col, helper, buffer, deep, applicationid, user);
		}
		// }
		buffer.append("</").append(MobileConstant.TAG_HOMEPAGE).append(">");
		return buffer.toString();
	}

	private void addMenu(StringBuffer buffer, ResourceVO vo, int index,
			String deep) {
		boolean flag = false;
		if (vo.getLink() != null) {
			if (LinkVO.LinkType.VIEW.getCode().equals(vo.getLink().getType())
					|| LinkVO.LinkType.FORM.getCode().equals(vo.getLink().getType())) {
				flag = true;
			} else {
				if (LinkVO.LinkType.MANUAL_EXTERNAL.getCode().equals(
						vo.getLink().getType())) {
					return;
				}
			}
		} else {
			String temp = vo.getDisplayView();
			if (!StringUtil.isBlank(temp) && !temp.equals("none")
					&& !temp.equals("null")) {
				flag = true;
			} else {
				temp = vo.getOtherurl();
				if (!StringUtil.isBlank(temp) && !temp.equals("none")
						&& !temp.equals("null"))
					return;
			}
		}
		buffer.append("<").append(MobileConstant.TAG_MENU).append(" ").append(
				MobileConstant.ATT_ORDER).append("='" + index + "' ").append(
				MobileConstant.ATT_ID).append(" = '" + vo.getId() + "' ")
				.append(MobileConstant.ATT_DEEP).append(" = '" + deep + "'>");

		buffer.append("<").append(MobileConstant.TAG_TEXT).append(
				">" + vo.getDescription() + "</").append(
				MobileConstant.TAG_TEXT).append(">");
		if (flag) {
			if (vo.getLink() == null) {
				buffer.append("<").append(MobileConstant.TAG_ACTION)
						.append(" ").append(MobileConstant.ATT_TYPE).append(
								"='" + vo.getType() + "'>");
				buffer.append("<").append(MobileConstant.TAG_PARAMETER).append(
						" ").append(MobileConstant.ATT_NAME).append(
						"='_viewid'>" + vo.getDisplayView() + "</").append(
						MobileConstant.TAG_PARAMETER).append(">");
				buffer.append("<").append(MobileConstant.TAG_PARAMETER).append(
						" ").append(MobileConstant.ATT_NAME).append(
						"='application'>" + application + "</").append(
						MobileConstant.TAG_PARAMETER).append(">");
				buffer.append("</").append(MobileConstant.TAG_ACTION).append(
						">");
			} else {
				buffer.append("<").append(MobileConstant.TAG_ACTION)
						.append(" ");
				if (LinkVO.LinkType.FORM.getCode().equals(vo.getLink().getType())) {
					buffer.append(MobileConstant.ATT_TYPE).append(
							"='" + vo.getLink().getType() + "'>");
					buffer.append("<").append(MobileConstant.TAG_PARAMETER)
							.append(" ").append(MobileConstant.ATT_NAME)
							.append(
									"='_formid'>"
											+ vo.getLink().getActionContent()
											+ "</").append(
									MobileConstant.TAG_PARAMETER).append(">");
					buffer.append("<").append(MobileConstant.TAG_PARAMETER)
							.append(" ").append(MobileConstant.ATT_NAME)
							.append(
									"='application'>" + vo.getApplicationid()
											+ "</").append(
									MobileConstant.TAG_PARAMETER).append(">");
				} else {
					buffer.append(MobileConstant.ATT_TYPE).append(
							"='" + vo.getLink().getType() + "'>");
					buffer.append("<").append(MobileConstant.TAG_PARAMETER)
							.append(" ").append(MobileConstant.ATT_NAME)
							.append(
									"='_viewid'>"
											+ vo.getLink().getActionContent()
											+ "</").append(
									MobileConstant.TAG_PARAMETER).append(">");
					buffer.append("<").append(MobileConstant.TAG_PARAMETER)
							.append(" ").append(MobileConstant.ATT_NAME)
							.append(
									"='application'>" + vo.getApplicationid()
											+ "</").append(
									MobileConstant.TAG_PARAMETER).append(">");
				}
				buffer.append("</").append(MobileConstant.TAG_ACTION).append(
						">");
			}
		} else {
			buffer.append("<").append(MobileConstant.TAG_ACTION).append(" ")
					.append(MobileConstant.ATT_TYPE).append("=''>");
			buffer.append("<").append(MobileConstant.TAG_PARAMETER).append(" ")
					.append(MobileConstant.ATT_NAME).append(
							"='_viewid'>" + vo.getDisplayView() + "</").append(
							MobileConstant.TAG_PARAMETER).append(">");
			buffer.append("<").append(MobileConstant.TAG_PARAMETER).append(" ")
					.append(MobileConstant.ATT_NAME).append(
							"='application'>" + application + "</").append(
							MobileConstant.TAG_PARAMETER).append(">");
			buffer.append("</").append(MobileConstant.TAG_ACTION).append(">");
		}
		buffer.append("<").append(MobileConstant.TAG_ICO).append(
				">" + vo.getMobileIco() + "</").append(MobileConstant.TAG_ICO)
				.append(">");
		buffer.append("</").append(MobileConstant.TAG_MENU).append(">");
	}

	private void sort(Collection<ResourceVO> col, PermissionHelper helper,
			StringBuffer buffer, String deep, String applicationid, WebUser user)
			throws Exception {
		ResourceVO[] tmp = new ResourceVO[col.size()];
		int j = 0;
		for (Iterator<ResourceVO> iterator = col.iterator(); iterator.hasNext();) {
			ResourceVO tmpRes = iterator.next();
			tmp[j] = tmpRes;
			j++;
		}
		for (int i = 0; i < tmp.length; i++) {
			ResourceVO tmpRes = tmp[i];
			boolean flag = true;
			if (!StringUtil.isBlank(tmpRes.getOrderno())) {
				flag = false;
			}
			for (j = i + 1; j < tmp.length; j++) {
				if (!flag && !StringUtil.isBlank(tmp[j].getOrderno())) {
					if (tmp[j].getOrderno().compareTo(tmpRes.getOrderno()) <= 0) {
						flag = true;
					}
				}
				if (flag) {
					tmp[i] = tmp[j];
					tmp[j] = tmpRes;
					tmpRes = tmp[i];
				}
			}
			if (helper.checkPermission(tmpRes, applicationid, user)) {
				addMenu(buffer, tmpRes, i, deep);
			}
		}
	}

	public String doChange() {
		// long start = System.currentTimeMillis();
		try {
			HttpServletRequest request = ServletActionContext.getRequest();
			HttpSession session = request.getSession();
			// session.setAttribute(Web.SESSION_ATTRIBUTE_APPLICATION,
			// getApplication());
			WebUser webUser = getUser();
			String userid = webUser.getId();

			UserProcess userProcess = (UserProcess) ProcessFactory
					.createProcess(UserProcess.class);
			UserVO userPO = (UserVO) userProcess.doView(userid);
			UserVO userVO = (UserVO) userPO.clone();
			// 对于User的update进行特别处理
			userVO.setDefaultApplication(getApplication());
			userVO.setLoginpwd(null);
			userProcess.doUpdateWithCache(userVO);
			// webUser.setApplicationid(getApplication());
			MultiLanguageProperty.load(getApplication(), false);
			String toXml = toResourceXml(null, null, getApplication(),
					getUser(), isShowApps, "");
			if (toXml != null) {
				session.setAttribute("toXml", toXml);
			}

		} catch (Exception e) {
			this.addFieldError("SystemError", e.getMessage());
			e.printStackTrace();
			return ERROR;
		}
		// long end = System.currentTimeMillis();
		return SUCCESS;
	}

	private void addAppsCommands(StringBuffer buffer) throws Exception {
		ApplicationHelper helper = new ApplicationHelper();
		Collection<ApplicationVO> cols = helper.getListByWebUser(getUser());
		for (Iterator<ApplicationVO> iterator = cols.iterator(); iterator
				.hasNext();) {
			ApplicationVO appVO = iterator.next();
			buffer.append("<").append(MobileConstant.TAG_ACTION).append(" ")
					.append(MobileConstant.ATT_TYPE).append("='900'");
			if (appVO.getId().equals(application)) {
				buffer.append(" ").append(MobileConstant.ATT_SELECTED).append(
						"='true'");
			}
			buffer.append(" ").append(MobileConstant.ATT_NAME).append(
					"='" + appVO.getName() + "'>");
			buffer.append("<").append(MobileConstant.TAG_PARAMETER).append(" ")
					.append(MobileConstant.ATT_NAME).append(
							"='application'>" + appVO.getId() + "</").append(
							MobileConstant.TAG_PARAMETER).append(">");
			buffer.append("<").append(MobileConstant.TAG_PARAMETER).append(" ")
					.append(MobileConstant.ATT_NAME).append(
							"='isShowApps'>" + isShowApps + "</").append(
							MobileConstant.TAG_PARAMETER).append(">");
			// /
			if (appVO.getLogourl() == null || appVO.getLogourl().equals(""))
				buffer.append("<").append(MobileConstant.TAG_ICO).append(
						">001</").append(MobileConstant.TAG_ICO).append(">");
			else
				buffer.append("<").append(MobileConstant.TAG_ICO).append(">")
						.append(appVO.getLogourl()).append("</").append(
								MobileConstant.TAG_ICO).append(">");

			buffer.append("</").append(MobileConstant.TAG_ACTION).append(">");

		}
	}

	public String doAcquireApps() throws Exception {
		try {
			StringBuffer buffer = new StringBuffer();
			ApplicationHelper helper = new ApplicationHelper();
			Collection<ApplicationVO> cols = helper.getListByWebUser(getUser());
			int i = 0;
			buffer.append("<").append(MobileConstant.TAG_HOMEPAGE).append(" ")
					.append(MobileConstant.ATT_TITLE).append("='软件列表'>");
			for (Iterator<ApplicationVO> iterator = cols.iterator(); iterator
					.hasNext();) {
				ApplicationVO appVO = iterator.next();
				buffer.append("<").append(MobileConstant.TAG_MENU).append(" ")
						.append(MobileConstant.ATT_ORDER).append(
								"='" + i + "' ").append(MobileConstant.ATT_ID)
						.append(" = '" + appVO.getId() + "' ").append(
								MobileConstant.ATT_DEEP).append(" = '0'>");
				buffer.append("<").append(MobileConstant.TAG_TEXT).append(
						">" + appVO.getName() + "</").append(
						MobileConstant.TAG_TEXT).append(">");
				buffer.append("<").append(MobileConstant.TAG_ACTION)
						.append(" ").append(MobileConstant.ATT_TYPE).append(
								"='900'>");
				buffer.append("<").append(MobileConstant.TAG_PARAMETER).append(
						" ").append(MobileConstant.ATT_NAME).append(
						"='application'>" + appVO.getId() + "</").append(
						MobileConstant.TAG_PARAMETER).append(">");
				buffer.append("<").append(MobileConstant.TAG_PARAMETER).append(
						" ").append(MobileConstant.ATT_NAME).append(
						"='isShowApps'>" + isShowApps + "</").append(
						MobileConstant.TAG_PARAMETER).append(">");

				buffer.append("</").append(MobileConstant.TAG_ACTION).append(
						">");
				buffer.append("<").append(MobileConstant.TAG_ICO).append(
						">001</").append(MobileConstant.TAG_ICO).append(">");
				buffer.append("</").append(MobileConstant.TAG_MENU).append(">");
			}
			buffer.append("</").append(MobileConstant.TAG_HOMEPAGE).append(">");
			HttpServletRequest request = ServletActionContext.getRequest();
			HttpSession session = request.getSession();
			session.setAttribute("toXml", buffer.toString());
		} catch (Exception e) {
			this.addFieldError("SystemError", e.getMessage());
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}

	/**
	 * 注销
	 * 
	 * @return SUCCESS
	 * @throws Exception
	 */
	public String doLogout() throws Exception {
		HttpSession session = ServletActionContext.getRequest().getSession();
		if (session != null)
			session.invalidate();
		return null;
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

	public String get_resourceId() {
		return _resourceId;
	}

	public String get_deep() {
		return _deep;
	}

	public void set_resourceId(String id) {
		_resourceId = id;
	}

	public void set_deep(String _deep) {
		this._deep = _deep;
	}

	public int get_pagelines() {
		if (_pagelines <= 0)
			_pagelines = 5;
		return _pagelines;
	}

	public void set_pagelines(int _pagelines) {
		this._pagelines = _pagelines;
	}

	/**
	 * Get WebUser Object.
	 * 
	 * @return WebUser Object user
	 * @throws Exception
	 */
	public WebUser getUser() throws Exception {
		HttpSession session = ServletActionContext.getRequest().getSession();
		WebUser user = (WebUser) session
				.getAttribute(Web.SESSION_ATTRIBUTE_FRONT_USER);
		if (user == null) {
			UserVO vo = new UserVO();
			vo.getId();
			vo.setName("GUEST");
			vo.setLoginno("guest");
			vo.setLoginpwd("");
			vo.setRoles(null);
			vo.setEmail("");
			// vo.setLanguageType(1);
			user = new WebUser(vo);
		}
		return user;
	}

	public boolean getIsShowApps() {
		return isShowApps;
	}

	public void setIsShowApps(boolean isShowApps) {
		this.isShowApps = isShowApps;
	}

	public static ActionContext getContext() {
		return ServletActionContext.getContext();
	}

	public String get_currResourceId() {
		return _currResourceId;
	}

	public void set_currResourceId(String _currResourceId) {
		this._currResourceId = _currResourceId;
	}

	public String getDomainname() {
		return domainname;
	}

	public void setLanguage(int language) {
		this.language = language;
	}

	public void setDomainname(String domainname) {
		this.domainname = domainname;
	}

	private Map<String, List<String>> fieldErrors;

	public void addFieldError(String fieldname, String message) {
		List<String> thisFieldErrors = getFieldErrors().get(fieldname);

		if (thisFieldErrors == null) {
			thisFieldErrors = new ArrayList<String>();
			this.fieldErrors.put(fieldname, thisFieldErrors);
		}
		thisFieldErrors.add(message);
	}

	public Map<String, List<String>> getFieldErrors() {
		if (fieldErrors == null)
			fieldErrors = new HashMap<String, List<String>>();
		return fieldErrors;
	}

	/**
	 * @SuppressWarnings API不支持泛型
	 */
	@SuppressWarnings("unchecked")
	public void setFieldErrors(Map fieldErrors) {
		this.fieldErrors = fieldErrors;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	// ////////////////////////////////////////////////////
	// ////*******************************************/////
	// ////////////////////////////////////////////////////

	public String doLogin2() {
		try {
			HttpServletRequest request = ServletActionContext.getRequest();
			HttpSession session = request.getSession();

			UserProcess process = (UserProcess) ProcessFactory
					.createProcess(UserProcess.class);
			UserVO user = process.login(username, password, domainname);
			if (user == null) {
				throw new Exception("用户名或密码错误");
			}
			if (user.getStatus() != 1) {
				throw new Exception("当前用户：" + user.getLoginno() + "，尚未激活");
			}

			WebUser webUser = MbLoginHelper.initLogin(request, user);
			String language = MultiLanguageProperty.getName(this.language);
			session.setAttribute(Web.SESSION_ATTRIBUTE_USERLANGUAGE, language);
			session
					.setAttribute("_pagelines", Integer
							.valueOf(get_pagelines()));

			StringBuffer xml = new StringBuffer();
			xml.append("<" + MobileConstant.TAG_HOMEPAGE + " "
					+ MobileConstant.ATT_SESSIONID + "='" + session.getId()
					+ "'>");
			xml.append(getApplicationXmlByDomain(webUser));
			xml.append("</" + MobileConstant.TAG_HOMEPAGE + ">");
			if (xml.length() > 5) {
				session.setAttribute("toXml", xml.toString());
			}
			return SUCCESS;
		} catch (InvocationTargetException e1) {
			addFieldError("SystemError", e1.getTargetException().getMessage());
			e1.printStackTrace();
			return ERROR;
		} catch (Exception e) {
			addFieldError("SystemError", e.getMessage());
			e.printStackTrace();
			return ERROR;
		}
	}

	public String doGetMenu() {
		try {
			HttpServletRequest request = ServletActionContext.getRequest();
			HttpSession session = request.getSession();

			String xml = getMenuByApplication(application, _resourceId,
					getUser());
			// if (xml.length() > 5) {
			session.setAttribute("toXml", xml);
			// }
			return SUCCESS;
		} catch (Exception e) {
			addFieldError("SystemError", e.getMessage());
			e.printStackTrace();
			return ERROR;
		}
	}

	public String doHomePage() {
		try {
			HttpSession session = ServletActionContext.getRequest()
					.getSession();
			StringBuffer xml = new StringBuffer();
			xml.append("<" + MobileConstant.TAG_HOMEPAGE + " "
					+ MobileConstant.ATT_SESSIONID + "='" + session.getId()
					+ "'>");
			xml.append(getApplicationXmlByDomain(getUser()));
			xml.append("</" + MobileConstant.TAG_HOMEPAGE + ">");
			if (xml.length() > 5) {
				session.setAttribute("toXml", xml.toString());
			}
			return SUCCESS;
		} catch (Exception e) {
			LOG.warn(e);
			return ERROR;
		}
	}

	public String doChangeApplication() {
		try {
			WebUser user = getUser();
			if (!user.getDefaultApplication().equals(application)) {
				UserProcess process = (UserProcess) ProcessFactory
						.createProcess(UserProcess.class);
				process.doUpdateDefaultApplication(user.getId(), application);
				user.setDefaultApplication(application);
			}
		} catch (Exception e) {
			LOG.warn(e);
		}
		return SUCCESS;
	}

	private String getApplicationXmlByDomain(WebUser user) throws Exception {
		StringBuffer xml = new StringBuffer();
		DomainProcess domainProcess = (DomainProcess) ProcessFactory
				.createProcess(DomainProcess.class);
		DomainVO domain = (DomainVO) domainProcess.doView(user.getDomainid());
		if (domain == null)
			return xml.toString();
		for (ApplicationVO vo : domain.getApplications()) {
			if (vo.isActivated()) {
				xml.append("<" + MobileConstant.TAG_APPLICATION + " ");
				xml
						.append(MobileConstant.ATT_NAME + "='" + vo.getName()
								+ "' ");
				xml.append(MobileConstant.ATT_ID + "='" + vo.getId() + "' ");
				if (vo.getId().equals(user.getDefaultApplication())) {
					this.setApplication(vo.getId());
					xml.append(MobileConstant.ATT_SELECTED + "='true'>");
					xml.append(getMenuByApplication(application, null, user));
				} else {
					xml.append(MobileConstant.ATT_SELECTED + "='false'>");
				}
				xml.append("<").append(MobileConstant.TAG_ICO).append(">001</")
						.append(MobileConstant.TAG_ICO).append(">");
				xml.append("</" + MobileConstant.TAG_APPLICATION + ">");
			}
		}
		return xml.toString();
	}

	private String getMenuByApplication(String application, String superid,
			WebUser user) {
		StringBuffer xml = new StringBuffer();
		try {
			PermissionHelper helper = new PermissionHelper();
			ResourceHelper resHelper = new ResourceHelper();
			resHelper.setApplicationid(application);
			helper.setUser(user);
			ResourceVO res = null;
			if (StringUtil.isBlank(superid)) {
				res = resHelper.getTopResourceByName("Mobile");
				if (user != null && user.getDefaultApplication() != null
						&& !user.getDefaultApplication().equals(application)) {
					UserProcess process = (UserProcess) ProcessFactory
							.createProcess(UserProcess.class);
					process.doUpdateDefaultApplication(user.getId(),
							application);
					user.setDefaultApplication(application);
				}
			} else {
				res = resHelper.getResourcById(superid);
			}
			Collection<ResourceVO> resources = resHelper.searchResourceForMb(
					res, user.getDomainid());
			if (resources != null) {
				int index = 0;
				for (ResourceVO vo : resources) {
					this.addMenu(xml, vo, index++, "0");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return xml.toString();
	}

	/**
	 * 获取软件下得待办信息
	 * 
	 * @param applicationid
	 * @return
	 */
	public String doGetPending() {
		try {
			HttpServletRequest request = ServletActionContext.getRequest();
			HttpSession session = request.getSession();
			session.setAttribute("toXml", toPendingXML());
		} catch (Exception e) {
			addFieldError("SystemError", e.getMessage());
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}

	/**
	 * 将待办信息解析为XML格式
	 * 
	 * @return
	 * @throws Exception
	 */
	public String toPendingXML() throws Exception {
		WebUser webUser = getUser();
		String userid = webUser.getId();
		UserDefinedProcess udprocss = (UserDefinedProcess) ProcessFactory
				.createProcess(UserDefinedProcess.class);

		ParamsTable params1 = new ParamsTable();
		// 获取当前用户自定义首页
		params1.setParameter("t_applicationid", application);
		params1.setParameter("t_userId", userid);
		params1.setParameter("i_useddefined", UserDefined.IS_DEFINED);
		params1.setParameter("_orderby", "id");
		DataPackage<UserDefined> dataPackage = udprocss.doQuery(params1);
		if (dataPackage.rowCount > 0) {
			UserDefined userDefined = new UserDefined();
			for (Iterator<UserDefined> ite1 = dataPackage.datas.iterator(); ite1
					.hasNext();) {
				userDefined = ite1.next();
			}
			return createTemplateElement(application, userDefined, webUser);
		} else {
			// 无自定义首页时,获取后台定制的默认首页
			Collection<RoleVO> userRoles = webUser.getRoles();
			RoleVO roleVO = new RoleVO();
			params1 = new ParamsTable();
			params1.setParameter("t_applicationid", application);
			params1.setParameter("n_published", true);
			params1.setParameter("_orderby", "id");
			DataPackage<UserDefined> dataPackage1 = udprocss.doQuery(params1);
			if (dataPackage1.rowCount > 0) {
				for (Iterator<UserDefined> ite1 = dataPackage1.datas.iterator(); ite1
						.hasNext();) {
					UserDefined userDefined = ite1.next();
					// 判断是否适用于所有角色
					if ("1".equals(userDefined.getDisplayTo())) {
						return createTemplateElement(application, userDefined,
								webUser);
					} else {
						// 获取某一首页的角色
						String roleIds = userDefined.getRoleIds();
						if (!StringUtil.isBlank(roleIds)) {
							String[] userRoleIds = roleIds.split(",");
							for (int i = 0; i < userRoleIds.length; i++) {
								if (userRoles.size() > 0) {
									for (Iterator<RoleVO> ite2 = userRoles
											.iterator(); ite2.hasNext();) {
										roleVO = ite2.next();
										if (userRoleIds[i].equals(roleVO
												.getId())) {
											// 当前角色与 后台首页待办设置的角色
											// 相同时，返回此后台定制的首页待办信息
											return createTemplateElement(
													application, userDefined,
													webUser);
										}
									}
								}
							}
						}
					}
				}
			}
		}

		return "";
	}

	/**
	 * 拼装待办信息XML模板
	 * 
	 * @param applicationid
	 * @param userDefined
	 * @param webUser
	 * @return
	 * @throws Exception
	 */
	public String createTemplateElement(String applicationid,
			UserDefined userDefined, WebUser webUser) throws Exception {
		StringBuffer xml = new StringBuffer();
		int tab1count = 0;
		int show1count = 0;
		int tab2count = 0;
		int show2count = 0;
		String templateElement = userDefined.getTemplateElement();
		if (!StringUtil.isBlank(templateElement)
				&& templateElement.length() > 1) {
			templateElement = templateElement.substring(1, templateElement
					.length() - 1);
			// 获取各布局单元格和对应的元素
			String[] templateElements = templateElement.split(",");
			for (int i = 0; i < templateElements.length; i++) {
				String[] templateElementSubs = templateElements[i]
						.split(":");
				if (!StringUtil.isBlank(templateElementSubs[0])
						&& templateElementSubs[0].length() > 1) {
					// 单元格摘要Id和title
					String[] summaryIds = templateElementSubs[1].split(";");
					String templateTdEle = summaryIds[0].substring(1,
							summaryIds[0].length() - 1);
					// 摘要id数组
					summaryIds = templateTdEle.split("\\|");
					if (summaryIds.length == 1 && summaryIds[0].equals("")) {
						continue;
					}
					for (int j = 0; j < summaryIds.length; j++) {
						SummaryCfgVO summaryCfg = summaryIdCheck(summaryIds[j]);
						if (summaryCfg == null) {
							continue;
						}
						if (summaryCfg.getScope() == SummaryCfgVO.SCOPE_PENDING) {// 代办
							ParamsTable summaryCfgParams = new ParamsTable();
							summaryCfgParams.setParameter("formid", summaryCfg
									.getFormId());
							String formID = summaryCfg.getFormId();

//							if (formID
//									.equals("11e2-2a0e-fe847b4d-9351-d59b7a2e40e8")) {
//								Log.warn("get form:"+summaryCfg.getSummaryScript());
//							}
							summaryCfgParams.setParameter("application",
									applicationid);
							summaryCfgParams.setParameter("_orderby",
									summaryCfg.getOrderby());
							PendingProcess pendingProcess = new PendingProcessBean(
									applicationid);
							DataPackage<PendingVO> pendings = pendingProcess
									.doQueryByFilter(summaryCfgParams, webUser);
							
							tab1count += pendings.rowCount;
							for (Iterator<PendingVO> iterator = pendings.datas
									.iterator(); iterator.hasNext();) {
								PendingVO pendingVO = iterator
										.next();
								

								if (pendingMore && show1count == 10) {
									break;
								}
								// change by lr 2013-08-03 for filt the null
								// state value
							if (pendingVO.getState() != null) {
									xml.append(pendingVO.toMbXMLText(webUser,
											summaryCfg.getId(), summaryCfg
													.getTitle()));
									show1count++;	
								}

								
							}

						} else if (summaryCfg.getScope() == SummaryCfgVO.SCOPE_CIRCULATOR) {// 代阅
							ParamsTable circulatorParams = new ParamsTable();
							circulatorParams.setParameter("formid", summaryCfg
									.getFormId());
							circulatorParams.setParameter("application",
									applicationid);
							CirculatorProcess circulatorProcess = (CirculatorProcess) ProcessFactory
									.createRuntimeProcess(
											CirculatorProcess.class,
											applicationid);
							DataPackage<Circulator> circulators = circulatorProcess
									.getPendingByUser(circulatorParams, webUser);
							tab2count += circulators.rowCount;
							for (Iterator<Circulator> iterator = circulators.datas
									.iterator(); iterator.hasNext();) {
								Circulator circulator = iterator
										.next();
								if (pendingMore && show2count == 10) {
									break;
								}
								xml.append(circulator.toMbXMLText(webUser,
										summaryCfg.getId(), summaryCfg
												.getTitle()));
								show2count++;
							}
						}
					}
				}
			}
		}//change by lr for mobile 2013-08-03
		if ((tab1count > 10 || tab2count > 10) && pendingMore) {
			return "<PENDINGS size='" + (tab1count + tab2count) + "'></PENDINGS>"
					+ xml.toString() + "<TR id='' n=''>more...</TR>";
		} else {
			//Log.warn("return to mobile:"+xml.toString());
			return "<PENDINGS size='" + (tab1count + tab2count) + "'></PENDINGS>"
					+ xml.toString() ;
		}
	}

	/**
	 * 查询摘要 有摘要则返回此摘要对象 否则返回null
	 * 
	 * @param summaryid
	 * @author jack
	 * @return summaryCfg
	 * @throws Exception
	 */
	public static SummaryCfgVO summaryIdCheck(String summaryid)
			throws ClassNotFoundException {
		SummaryCfgVO summaryCfg = null;
		try {
			SummaryCfgProcess summaryCfgPro = (SummaryCfgProcess) ProcessFactory
					.createProcess(SummaryCfgProcess.class);
			if (!StringUtil.isBlank(summaryid))
				summaryCfg = (SummaryCfgVO) summaryCfgPro.doView(summaryid);
		} catch (Exception e) {
			System.err.println(e);
		}
		return summaryCfg;
	}

	public boolean isPendingMore() {
		return pendingMore;
	}

	public void setPendingMore(boolean pendingMore) {
		this.pendingMore = pendingMore;
	}

}
