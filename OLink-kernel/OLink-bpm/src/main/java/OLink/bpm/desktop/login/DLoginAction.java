package OLink.bpm.desktop.login;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.constans.Environment;
import OLink.bpm.constans.Web;
import OLink.bpm.core.department.ejb.DepartmentProcess;
import OLink.bpm.core.department.ejb.DepartmentVO;
import OLink.bpm.core.deploy.application.ejb.ApplicationProcess;
import OLink.bpm.core.domain.ejb.DomainVO;
import OLink.bpm.core.dynaform.pending.ejb.PendingProcess;
import OLink.bpm.core.resource.ejb.ResourceType;
import OLink.bpm.core.security.action.LoginHelper;
import OLink.bpm.core.user.action.UserDefinedHelper;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.util.HtmlEncoder;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.property.MultiLanguageProperty;
import OLink.bpm.core.deploy.application.action.ApplicationHelper;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.core.permission.action.PermissionHelper;
import OLink.bpm.core.resource.ejb.ResourceProcess;
import OLink.bpm.core.table.constants.MobileConstant;
import OLink.bpm.core.user.ejb.UserProcess;
import OLink.bpm.util.StringUtil;
import org.apache.log4j.Logger;

import OLink.bpm.core.domain.ejb.DomainProcess;
import OLink.bpm.core.dynaform.pending.ejb.PendingVO;
import OLink.bpm.core.dynaform.summary.ejb.SummaryCfgVO;
import OLink.bpm.core.resource.ejb.ResourceVO;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionSupport;

/**
 * 桌面应用登录Action
 * 
 * @author Tom
 * 
 */
public class DLoginAction extends ActionSupport {

	private static final long serialVersionUID = 1L;

	private String username;

	private String password;

	private String domainname;

	private int updateState;

	private static final int PENDING_UPDATE = 1, MENU_UPDATE = 2,
			CONTACT_UPDATE = 3;

	public static final String PENGING_LIST = "pengingList";

	private List<String> pengingList = new ArrayList<String>();

	private static final Logger LOG = Logger.getLogger(DLoginAction.class);

	private boolean validateLogin() {
		HttpSession session = ServletActionContext.getRequest().getSession();
		Object user = session.getAttribute(Web.SESSION_ATTRIBUTE_FRONT_USER);
		return user != null;
	}

	public String doLogin() throws Exception {

		long time = System.currentTimeMillis();
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

				ApplicationVO appvo = appProcess.getDefaultApplication(webUser
						.getDefaultApplication(), webUser);
				if (appvo == null) {
					appvo = appProcess.queryByDomain(
							user.getDomainid()).iterator().next();

				}
				String application = appvo == null ? "" : appvo.getId();
				// webUser.setApplicationid(application);
				webUser.setDefaultApplication(application);

				UserVO vo = (UserVO) user.clone();
				vo.setDefaultApplication(application);
				vo.setLoginpwd(null);
				process.doUpdateWithCache(vo);

				session.setAttribute(Web.SESSION_ATTRIBUTE_DOMAIN, webUser
						.getDomainid());
				session.setAttribute(Web.SESSION_ATTRIBUTE_APPLICATION,
						application);
				DomainProcess process2 = (DomainProcess) ProcessFactory
						.createProcess(DomainProcess.class);
				DomainVO domainvo = (DomainVO) process2.doView(user
						.getDomainid());
				session.setAttribute(Web.SKIN_TYPE, domainvo.getSkinType());

				if (MultiLanguageProperty.getType(language) == 0)
					setLanguage(MultiLanguageProperty.getName(2));
				session.setAttribute(Web.SESSION_ATTRIBUTE_USERLANGUAGE,
						language);
				// session.setMaxInactiveInterval(20 * 60); // 20 minutes

				session.setAttribute(Web.SESSION_ATTRIBUTE_FRONT_USER, webUser);
				session.setAttribute(Web.SESSION_ATTRIBUTE_LOGINBYAPP, Boolean
						.valueOf(true));
				// 初始化webuser
				LoginHelper.initWebUser(ServletActionContext.getRequest(),
						user, application, domainname);

				// 在环境中设置context path
				Environment.getInstance().setContextPath(
						ServletActionContext.getRequest().getContextPath());
				String toXml = getPendingXmlByApplication(webUser)
						+ getMenuXmlByApplication(webUser)
						+ getUserListXml(user.getDomainid()) + getUserInfoXml();
				if (toXml != null) {
					toXml = "<" + MobileConstant.TAG_MAIN + " "
							+ MobileConstant.ATT_SESSIONID + "='"
							+ session.getId() + "' "
							+ MobileConstant.ATT_SESSION_LIFT_CYCLE + "='"
							+ session.getMaxInactiveInterval() + "'>" + toXml
							+ "</" + MobileConstant.TAG_MAIN + ">";
					ServletActionContext.getRequest().setAttribute("toXml",
							toXml);
				}
				// System.out.println("login->:" + toXml);
			}
			ServletActionContext.getRequest().getSession().setAttribute(
					PENGING_LIST, pengingList);
			long end = System.currentTimeMillis();
			System.out.println("doLogin Waste Time: " + (end - time) + "(ms)");
		} catch (Exception e) {
			LOG.warn(e);
			this.addFieldError("SystemError", e.getMessage());
			e.printStackTrace();
			// System.out.println(e.toString());
			return ERROR;
		}
		return SUCCESS;
	}

	/**
	 * @deprecated 旧版本方法，已掉弃
	 */
	public String doChange() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpSession session = request.getSession();
		if (session == null
				|| session.getAttribute(Web.SESSION_ATTRIBUTE_FRONT_USER) == null) {
			this.addFieldError("SystemError", "服务器出现错误，请尝试重新登录。nologin");
			return ERROR;
		}
		try {
			DLoginHelper helper = new DLoginHelper(request, response);
			String toXml = helper.getChangePendingXml();
			request.setAttribute("toXml", toXml);
		} catch (Exception e) {
			LOG.warn(e);
			request.setAttribute("toXml", "");
		}
		// System.gc();
		return SUCCESS;
	}

	private String getMenuXmlByApplication(WebUser user) {
		StringBuffer xml = new StringBuffer();
		if (user == null)
			return xml.toString();
		try {
			// DomainProcess process = (DomainProcess) ProcessFactory
			// .createProcess(DomainProcess.class);
			// DomainVO vo = (DomainVO) process.doView(user.getDomainid());
			Collection<ApplicationVO> apps = new ApplicationHelper()
					.getListByWebUser(user);// vo.getApplications();
			if (apps == null || apps.isEmpty()) {
				return xml.toString();
			}
			PermissionHelper pHelper = new PermissionHelper();
			xml.append("<" + MobileConstant.TAG_APPLICATION + ">");

			for (Iterator<ApplicationVO> it = apps.iterator(); it.hasNext();) {
				ApplicationVO app = it.next();

				if (app != null && !StringUtil.isBlank(app.getId())) {
					xml.append("<" + MobileConstant.TAG_MENU_POP + " ");
					xml.append(MobileConstant.ATT_ID + "='" + app.getId()
							+ "' ");
					// xml.append(MobileConstant.ATT_SRC + "='" +
					// app.getLogourl() + "' ");
					xml.append(MobileConstant.ATT_NAME + "='" + app.getName()
							+ "'>");
					Collection<ResourceVO> topMenus = get_topmenus(app.getId(),
							user.getDomainid());
					Collection<ResourceVO> temp = topMenus;

					ResourceProcess process1 = (ResourceProcess) ProcessFactory
							.createProcess(ResourceProcess.class);

					for (Iterator<ResourceVO> it1 = topMenus.iterator(); it1
							.hasNext();) {
						ResourceVO resvo = it1.next();
						if (resvo == null
								|| !pHelper.checkPermission(resvo, app.getId(),
										getUser())) {
							continue;
						}
						temp = process1.doGetDatasByParent(resvo.getId());
						if (temp != null && temp.size() == 0) {

							String url = resvo.toUrlString(getUser(),
									ServletActionContext.getRequest());

							xml.append("<" + MobileConstant.TAG_MENU_ITEM + " "
									+ MobileConstant.ATT_NAME + "='"
									+ resvo.getDescription() + "' ");
							// xml.append(MobileConstant.ATT_APPLICATIONID +
							// "='" + app.getId() + "' ");
							xml.append(MobileConstant.ATT_ORDER + "='"
									+ resvo.getOrderno() + "' ");
							xml.append(MobileConstant.ATT_URL + "='"
									+ HtmlEncoder.encode(url) + "'>");
							xml.append("</" + MobileConstant.TAG_MENU_ITEM
									+ ">");
						} else if (app != null && user != null) {
							String nextXml = menuRecursive(resvo, temp, app
									.getId(), user.getDomainid(), pHelper);
							if (nextXml != null) {
								xml.append(nextXml);
							}
						}
					}

					xml.append("</" + MobileConstant.TAG_MENU_POP + ">");
					try {
						PersistenceUtils.closeSessionAndConnection();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			xml.append("</" + MobileConstant.TAG_APPLICATION + ">");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return xml.toString();
	}

	private String menuRecursive(ResourceVO resvo, Collection<ResourceVO> coll,
			String appId, String domainId, PermissionHelper pHelper)
			throws Exception {
		StringBuffer xml = new StringBuffer();
		ResourceProcess process = (ResourceProcess) ProcessFactory
				.createProcess(ResourceProcess.class);
		xml.append("<" + MobileConstant.TAG_MENU + " "
				+ MobileConstant.ATT_NAME + "='" + resvo.getDescription()
				+ "' ");
		xml.append(MobileConstant.ATT_ORDER + "='" + resvo.getOrderno() + "'>");
		for (Iterator<ResourceVO> it1 = coll.iterator(); it1.hasNext();) {
			ResourceVO resvo1 = it1.next();
			if (resvo1 == null
					|| !pHelper.checkPermission(resvo1, appId, getUser())) {
				continue;
			}
			Collection<ResourceVO> subMenus = process.doGetDatasByParent(resvo1
					.getId());
			if (subMenus != null && subMenus.size() > 0) {
				xml.append(menuRecursive(resvo1, subMenus, appId, domainId,
						pHelper));
			} else {
				String url = resvo1.toUrlString(getUser(), ServletActionContext
						.getRequest());

				xml.append("<" + MobileConstant.TAG_MENU_ITEM + " "
						+ MobileConstant.ATT_NAME + "='"
						+ resvo1.getDescription() + "' ");
				// xml.append(MobileConstant.ATT_APPLICATIONID+ "='" + appId +
				// "' ");
				xml.append(MobileConstant.ATT_ORDER + "='"
						+ resvo1.getOrderno() + "' ");
				xml.append(MobileConstant.ATT_URL + "='"
						+ HtmlEncoder.encode(url) + "'>");
				xml.append("</" + MobileConstant.TAG_MENU_ITEM + ">");
			}
		}
		xml.append("</" + MobileConstant.TAG_MENU + ">");
		return xml.toString();
	}

	private String getUserInfoXml() {
		try {
			WebUser user = getUser();
			StringBuffer sb = new StringBuffer();
			sb.append("<" + MobileConstant.TAG_USERINFO).append(" ");
			sb.append(MobileConstant.ATT_ID + "='" + user.getId() + "'")
					.append(" ");
			sb.append(MobileConstant.ATT_NAME + "='" + user.getName() + "'")
					.append(" ");
			sb
					.append(
							MobileConstant.ATT_DOMAIN + "='"
									+ user.getDomainid() + "'").append(" ");
			StringBuffer dep = new StringBuffer();
			Collection<DepartmentVO> deps = user.getDepartments();
			for (Iterator<DepartmentVO> it = deps.iterator(); it.hasNext();) {
				DepartmentVO vo = it.next();
				if (vo != null) {
					if (dep.length() > 0) {
						dep.append(",");
					}
					dep.append(vo.getName());
				}
			}
			sb.append(MobileConstant.ATT_DEP + "='" + dep.toString() + "'")
					.append("></" + MobileConstant.TAG_USERINFO + ">");
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	private String getPendingXmlByApplication(WebUser user) {
		StringBuffer xml = new StringBuffer();
		xml.append("<" + MobileConstant.TAG_PENDING_LIST + ">");
		try {
			// HttpServletRequest request = ServletActionContext.getRequest();
			Collection<ApplicationVO> apps = new ApplicationHelper()
					.getListByWebUser(user);
			for (Iterator<ApplicationVO> it = apps.iterator(); it.hasNext();) {
				ApplicationVO app = it.next();

				xml.append("<" + MobileConstant.TAG_PENDING_GROUP + " ");
				xml.append(MobileConstant.ATT_ID + "='" + app.getId() + "' ");
				xml.append(MobileConstant.ATT_NAME + "='" + app.getName()
						+ "'>");

				try {
					UserDefinedHelper hph = new UserDefinedHelper();
					hph.setApplicationid(app.getId());
					Collection<SummaryCfgVO> cfgs = hph.getUserSummaryCfg(user);
					if (cfgs == null || cfgs.isEmpty()) {
						xml.append("</" + MobileConstant.TAG_PENDING_GROUP
								+ ">");
						continue;
					}

					PendingProcess pProcess = (PendingProcess) ProcessFactory
							.createRuntimeProcess(PendingProcess.class, app
									.getId());
					ParamsTable params = new ParamsTable();

					for (Iterator<SummaryCfgVO> it3 = cfgs.iterator(); it3
							.hasNext();) {
						SummaryCfgVO cfg = it3.next();
						params.setParameter("formid", cfg.getFormId());
						params.setParameter("_orderby", cfg.getOrderby());
						params.setParameter("_pagelines", Integer.MAX_VALUE
								+ "");
						DataPackage<PendingVO> datas = pProcess
								.doQueryByFilter(params, user);

						Collection<PendingVO> list = new ArrayList<PendingVO>();
						if (datas != null && datas.datas != null) {
							list = datas.datas;
						}
						for (Iterator<PendingVO> it2 = list.iterator(); it2
								.hasNext();) {
							PendingVO pending = it2.next();
							xml.append("<" + MobileConstant.TAG_PENDING_ITEM
									+ " ");

							xml.append(MobileConstant.ATT_ID + "='"
									+ pending.getId() + "' ");
							// xml.append(MobileConstant.ATT_URL + "='" + url +
							// "' ");

							xml.append(MobileConstant.ATT_FORMID + "='"
									+ pending.getFormid() + "'>");
							pengingList.add(pending.getId());
							xml.append("[" + cfg.getTitle() + "] "
									+ pending.getSummary());
							xml.append("</" + MobileConstant.TAG_PENDING_ITEM
									+ ">");

						}
					}
				} catch (Exception e) {
					LOG.warn(e);
				}

				xml.append("</" + MobileConstant.TAG_PENDING_GROUP + ">");
				try {
					PersistenceUtils.closeSessionAndConnection();
				} catch (Exception e) {
					LOG.warn(e);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		xml.append("</" + MobileConstant.TAG_PENDING_LIST + ">");
		return xml.toString();
	}

	private String getUserListXml(String domain) throws Exception {
		StringBuffer xml = new StringBuffer();
		DepartmentProcess dp = (DepartmentProcess) ProcessFactory
				.createProcess(DepartmentProcess.class);
		DepartmentVO root = dp.getRootDepartmentByApplication("", domain);
		if (root == null) {
			return xml.toString();
		}
		xml.append("<" + MobileConstant.TAG_CONTACT + ">");
		UserProcess up = (UserProcess) ProcessFactory
				.createProcess(UserProcess.class);
		ParamsTable table = new ParamsTable();
		table.setParameter("sm_userDepartmentSets.departmentId", root.getId());
		table.setParameter("_pagelines", Integer.MAX_VALUE);
		DataPackage<UserVO> users = up.doQuery(table);

		xml.append("<" + MobileConstant.TAG_DEP + " " + MobileConstant.ATT_NAME
				+ "='" + root.getName() + "' " + MobileConstant.ATT_CODE + "='"
				+ root.getCode() + "'>");
		if (users.datas != null && !users.datas.isEmpty()) {
			for (Iterator<UserVO> it_user = users.datas.iterator(); it_user
					.hasNext();) {
				UserVO uservo = it_user.next();
				xml.append("<" + MobileConstant.TAG_CONTACT_USER + " "
						+ MobileConstant.ATT_ID + "='" + uservo.getId() + "'>");
				xml.append(uservo.getName());
				xml.append("</" + MobileConstant.TAG_CONTACT_USER + ">");
			}
		}
		// 获取顶部门下的其他部门信息
		xml.append(getUserListByDep(root, domain));
		xml.append("</" + MobileConstant.TAG_DEP + ">");

		xml.append("</" + MobileConstant.TAG_CONTACT + ">");
		// System.out.println(xml.toString());
		return xml.toString();
	}

	private String getUserListByDep(DepartmentVO depvo, String domain)
			throws Exception {
		StringBuffer xml = new StringBuffer();
		DepartmentProcess dp = (DepartmentProcess) ProcessFactory
				.createProcess(DepartmentProcess.class);
		Collection<DepartmentVO> deps = dp.getDatasByParent(depvo.getId());
		if (deps != null && !deps.isEmpty()) {
			for (Iterator<DepartmentVO> it = deps.iterator(); it.hasNext();) {
				DepartmentVO dep = it.next();
				xml
						.append("<" + MobileConstant.TAG_DEP + " "
								+ MobileConstant.ATT_NAME + "='"
								+ dep.getName() + "' "
								+ MobileConstant.ATT_CODE + "='"
								+ dep.getCode() + "'>");
				xml.append(getUserListByDep(dep, domain));
				UserProcess up = (UserProcess) ProcessFactory
						.createProcess(UserProcess.class);
				ParamsTable table = new ParamsTable();
				table.setParameter("sm_userDepartmentSets.departmentId", dep
						.getId());
				table.setParameter("_pagelines", Integer.MAX_VALUE);
				DataPackage<UserVO> users = up.doQuery(table);
				for (Iterator<UserVO> it_user = users.datas.iterator(); it_user
						.hasNext();) {
					UserVO uservo = it_user.next();
					xml.append("<" + MobileConstant.TAG_CONTACT_USER + " "
							+ MobileConstant.ATT_ID + "='" + uservo.getId()
							+ "'>");
					xml.append(uservo.getName());
					xml.append("</" + MobileConstant.TAG_CONTACT_USER + ">");
				}
				xml.append("</" + MobileConstant.TAG_DEP + ">");
			}
		}
		return xml.toString();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDomainname() {
		return domainname;
	}

	public void setDomainname(String domainname) {
		this.domainname = domainname;
	}

	public int getUpdateState() {
		return updateState;
	}

	public void setUpdateState(int updateState) {
		this.updateState = updateState;
	}

	private String language = "CN";

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public WebUser getUser() throws Exception {
		HttpSession session = ServletActionContext.getRequest().getSession();
		WebUser user = null;
		if (session == null
				|| session.getAttribute(Web.SESSION_ATTRIBUTE_FRONT_USER) == null) {
			UserVO vo = new UserVO();
			vo.getId();
			vo.setName("GUEST");
			vo.setLoginno("guest");
			vo.setLoginpwd("");
			vo.setRoles(null);
			vo.setEmail("");
			// vo.setLanguageType(1);
			user = new WebUser(vo);
		} else {
			user = (WebUser) session
					.getAttribute(Web.SESSION_ATTRIBUTE_FRONT_USER);
		}
		return user;
	}

	public String getApplicationId() {
		return (String) getContext().getSession().get("APPLICATION");
	}

	public static ActionContext getContext() {
		return ActionContext.getContext();
	}

	/**
	 * Retrieve the top menus.
	 * 
	 * @return Returns the top menus collection.
	 * @throws Exception
	 */
	public Collection<ResourceVO> get_topmenus(String application,
			String domain, ParamsTable params) throws Exception {
		return getSubMenus(null, application, domain, params);
	}

	public Collection<ResourceVO> get_topmenus(String application, String domain)
			throws Exception {
		ParamsTable params = new ParamsTable();
		params.setParameter("xi_type", ResourceType.RESOURCE_TYPE_MOBILE);
		return get_topmenus(application, domain, params);
	}

	/**
	 * Retrieve the sub menus (get the parent menu id from the web paramenter).
	 * 
	 * @return The sub menus collection.
	 * @throws Exception
	 */
	public Collection<ResourceVO> get_submenus(String application,
			String domain, ParamsTable params) throws Exception {
		Collection<ResourceVO> menus = null;
		try {
			ResourceProcess process = (ResourceProcess) ProcessFactory
					.createProcess(ResourceProcess.class);
			String _pid = ServletActionContext.getRequest().getParameter(
					"_parent");
			ResourceVO parent = (ResourceVO) process.doView(_pid);

			// if (parent != null) {
			menus = getSubMenus(parent, application, domain, params);
			// parent.setSuperiorid("-1");
			// menus.add(parent);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return menus;
	}

	/**
	 * Retrieve the sub menus
	 * 
	 * @param startNode
	 *            The parent menu.
	 * @return The sub menus under the parment menu.
	 * @throws Exception
	 */
	public Collection<ResourceVO> getSubMenus(ResourceVO startNode,
			String application) throws Exception {
		ResourceProcess process = (ResourceProcess) ProcessFactory
				.createProcess(ResourceProcess.class);
		ArrayList<ResourceVO> list = new ArrayList<ResourceVO>();
		ParamsTable params = new ParamsTable();
		params.setParameter("_orderby", "orderno");
		params.setParameter("application", application);
		Collection<ResourceVO> cols = process
				.doSimpleQuery(params, application);

		Iterator<ResourceVO> iter = cols.iterator();
		while (iter.hasNext()) {
			ResourceVO vo = iter.next();
			if (startNode == null) {
				if (vo.getSuperior() == null) {
					list.add(vo);
				}
			} else {
				if (vo.getSuperior() != null) {
					ResourceVO superior = vo.getSuperior();
					while (superior != null) {
						if (superior.getId().equals(startNode.getId())) {
							list.add(vo);
							break;
						}
						superior = superior.getSuperior();
					}
				}
			}
		}

		return list;
	}

	public Collection<ResourceVO> getSubMenus(String startNodeid,
			String application, String domain) throws Exception {
		ArrayList<ResourceVO> list = new ArrayList<ResourceVO>();
		ParamsTable params = new ParamsTable();
		params.setParameter("_orderby", "orderno");
		params.setParameter("application", application);
		ResourceProcess process = (ResourceProcess) ProcessFactory
				.createProcess(ResourceProcess.class);
		Collection<ResourceVO> cols = process
				.doSimpleQuery(params, application);

		Iterator<ResourceVO> iter = cols.iterator();
		while (iter.hasNext()) {
			ResourceVO vo = iter.next();
			if (startNodeid == null) {
				if (vo.getSuperior() == null
						&& (vo.getIsview() == null
								|| vo.getIsview().equals("public") || (vo
								.getIsview().equals("private")
								&& vo.getColids() != null && vo.getColids()
								.indexOf(domain) >= 0))) {
					list.add(vo);
				}
			} else if (vo.getIsview() == null
					|| vo.getIsview().equals("public")
					|| (vo.getIsview().equals("private")
							&& vo.getColids() != null && vo.getColids()
							.indexOf(domain) >= 0)) {
				if (vo.getSuperior() != null) {
					ResourceVO superior = vo.getSuperior();
					while (superior != null) {
						if (superior.getId().equals(startNodeid)) {
							list.add(vo);
							break;
						}
						superior = superior.getSuperior();
					}
				}
			}
		}

		return list;
	}

	public Collection<ResourceVO> getSubMenus(ResourceVO startNode,
			String application, String domain, ParamsTable params)
			throws Exception {
		ArrayList<ResourceVO> list = new ArrayList<ResourceVO>();

		params.setParameter("_orderby", "orderno");
		params.setParameter("application", application);
		ResourceProcess process = (ResourceProcess) ProcessFactory
				.createProcess(ResourceProcess.class);
		Collection<ResourceVO> cols = process
				.doSimpleQuery(params, application);

		Iterator<ResourceVO> iter = cols.iterator();
		while (iter.hasNext()) {
			ResourceVO vo = iter.next();
			if (startNode == null) {
				if (vo.getSuperior() == null
						&& (vo.getIsview() == null
								|| vo.getIsview().equals("public") || (vo
								.getIsview().equals("private")
								&& vo.getColids() != null && vo.getColids()
								.indexOf(domain) >= 0))) {
					list.add(vo);
				}
			} else if (vo.getIsview() == null
					|| vo.getIsview().equals("public")
					|| (vo.getIsview().equals("private")
							&& vo.getColids() != null && vo.getColids()
							.indexOf(domain) >= 0)) {
				if (vo.getSuperior() != null) {
					ResourceVO superior = vo.getSuperior();
					while (superior != null) {
						if (superior.getId().equals(startNode.getId())) {
							list.add(vo);
							break;
						}
						superior = superior.getSuperior();
					}
				}
			}
		}

		return list;
	}

	// private String getString(String str) {
	// if (StringUtil.isBlank(str))
	// return "";
	// return str;
	// }

	public String doLogout() {
		HttpSession session = ServletActionContext.getRequest().getSession();
		Object object = session.getAttribute(Web.SESSION_ATTRIBUTE_FRONT_USER);
		if (object != null) {
			session.removeAttribute(Web.SESSION_ATTRIBUTE_FRONT_USER);
		}
		if(session!=null) session.invalidate();
		ServletActionContext.getRequest().setAttribute("toXml", "");
		return SUCCESS;
	}

	public String doRefreshUser() {
		try {
			WebUser user = getUser();
			if (user != null) {
				String domain = user.getDomainid();
				if (!StringUtil.isBlank(domain)) {
					String xml = getUserListXml(domain);
					ServletActionContext.getRequest().setAttribute("toXml",
							xml == null ? "" : xml);
				} else {
					LOG.warn("Can't find domain id! " + user.getName());
				}
			} else {
				LOG.warn("User can't login!");
			}
		} catch (Exception e) {
			LOG.warn(e);
		}
		return SUCCESS;
	}

	/**
	 * update request
	 * 
	 * 更新请求
	 * 
	 * @return
	 * @throws Exception
	 */
	public String update() {
		try {
			WebUser webUser = getUser();
			HttpSession session = ServletActionContext.getRequest()
					.getSession();
			StringBuffer xml = new StringBuffer();
			if (updateState == PENDING_UPDATE) {
				xml.append("<" + MobileConstant.TAG_MAIN + " "
						+ MobileConstant.ATT_SESSIONID + "='" + session.getId()
						+ "'>");
				xml.append(getPendingXmlByApplication(webUser));
				xml.append("</" + MobileConstant.TAG_MAIN + ">");
			} else if (updateState == MENU_UPDATE) {
				xml.append("<" + MobileConstant.TAG_MAIN + " "
						+ MobileConstant.ATT_SESSIONID + "='" + session.getId()
						+ "'>");
				xml.append(getMenuXmlByApplication(webUser));
				xml.append("</" + MobileConstant.TAG_MAIN + ">");

			} else if (updateState == CONTACT_UPDATE) {
				xml.append("<" + MobileConstant.TAG_MAIN + " "
						+ MobileConstant.ATT_SESSIONID + "='" + session.getId()
						+ "'>");
				xml.append(getUserListXml(webUser.getDomainid()));
				xml.append("</" + MobileConstant.TAG_MAIN + ">");
			} else if (updateState == (PENDING_UPDATE + MENU_UPDATE)) {
				xml.append("<" + MobileConstant.TAG_MAIN + " "
						+ MobileConstant.ATT_SESSIONID + "='" + session.getId()
						+ "'>");
				xml.append(getPendingXmlByApplication(webUser));
				xml.append(getMenuXmlByApplication(webUser));
				xml.append("</" + MobileConstant.TAG_MAIN + ">");
			} else if (updateState == (PENDING_UPDATE + CONTACT_UPDATE)) {
				xml.append("<" + MobileConstant.TAG_MAIN + " "
						+ MobileConstant.ATT_SESSIONID + "='" + session.getId()
						+ "'>");
				xml.append(getPendingXmlByApplication(webUser));
				xml.append(getUserListXml(webUser.getDomainid()));
				xml.append("</" + MobileConstant.TAG_MAIN + ">");
			} else if (updateState == (CONTACT_UPDATE + MENU_UPDATE)) {
				xml.append("<" + MobileConstant.TAG_MAIN + " "
						+ MobileConstant.ATT_SESSIONID + "='" + session.getId()
						+ "'>");
				xml.append(getMenuXmlByApplication(webUser));
				xml.append(getUserListXml(webUser.getDomainid()));
				xml.append("</" + MobileConstant.TAG_MAIN + ">");
			} else if (updateState == (PENDING_UPDATE + MENU_UPDATE + CONTACT_UPDATE)) {
				xml.append("<" + MobileConstant.TAG_MAIN + " "
						+ MobileConstant.ATT_SESSIONID + "='" + session.getId()
						+ "'>");
				xml.append(getPendingXmlByApplication(webUser));
				xml.append(getMenuXmlByApplication(webUser));
				xml.append(getUserListXml(webUser.getDomainid()));
				xml.append("</" + MobileConstant.TAG_MAIN + ">");
			}
			ServletActionContext.getRequest().setAttribute("toXml",
					xml.toString());
		} catch (Exception e) {
			LOG.warn(e);
			this.addFieldError("SystemError", e.getMessage());
			return ERROR;
		}
		return SUCCESS;
	}
}
