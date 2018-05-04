package OLink.bpm.base.action;

import java.util.Map;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.base.ejb.IRunTimeProcess;
import OLink.bpm.constans.Web;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.user.ejb.UserVO;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionSupport;

/**
 * The abstract run-time action, it basic class for the action of run time
 * instance.
 * 
 */
public abstract class AbstractRunTimeAction<E> extends ActionSupport {
	/**
	 * The serial version uid.
	 */
	private static final long serialVersionUID = -8798588653224830361L;

	/**
	 * The inner value object
	 */
	protected ValueObject content = null;
	/**
	 * The inner data package.
	 */
	private DataPackage<E> datas = null;
	/**
	 * The inner run time process
	 */
	protected IRunTimeProcess<E> process = null;
	/**
	 * The select ids
	 */
	protected String[] _selects = null;
	/**
	 * The dmain id
	 */
	protected String domain;
	/**
	 * The application id
	 */
	protected String application;

	/**
	 * Retrieve WebUser Object.
	 * 
	 * @SuppressWarnings WebWork不支持泛型
	 * @return The current WebUser Object.
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public WebUser getUser() throws Exception {
		Map session = getContext().getSession();

		WebUser user = null;

		if (session == null
				|| session.get(Web.SESSION_ATTRIBUTE_FRONT_USER) == null)
			user = getAnonymousUser();
		else
			user = (WebUser) session.get(Web.SESSION_ATTRIBUTE_FRONT_USER);

		return user;
	}

	/**
	 * Retrieve the ParamsTable
	 * 
	 * @return ParamsTable
	 */
	public ParamsTable getParams() {
		ParamsTable pm = ParamsTable.convertHTTP(ServletActionContext
				.getRequest());

		// put the domain id to parameters table.
		if (getDomain() != null)
			pm.setParameter("domainid", getDomain());

		// put the page line count id to parameters table.
		if (pm.getParameter("_pagelines") == null)
			pm.setParameter("_pagelines", Web.DEFAULT_LINES_PER_PAGE);

		return pm;
	}

	/**
	 * The action for show a object value.
	 * 
	 * @SuppressWarnings WebWork不支持泛型
	 * @return "SUCCESS" when action run successfully, "ERROR" otherwise.
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String doView() throws Exception {

		try {
			Map params = getContext().getParameters();

			String[] ids = (String[]) (params.get("id"));
			String id = (ids != null && ids.length > 0) ? ids[0] : null;

			ValueObject contentVO = process.doView(id);
			setContent(contentVO);
		} catch (Exception e) {
			this.addActionError(e.getMessage());
			return INPUT;
		}

		return SUCCESS;
	}

	/**
	 * The action for query the object values.
	 * 
	 * @return "SUCCESS" when action run successfully, "ERROR" otherwise.
	 * @throws Exception
	 */
	public String doList() throws Exception {
		datas = this.getProcess().doQuery(getParams(), getUser());

		return SUCCESS;
	}

	/**
	 * Get the DataPackage
	 * 
	 * @return the DataPackage
	 */
	public DataPackage<E> getDatas() {
		return datas;
	}

	/**
	 * Set the DataPackage
	 * 
	 * @param datas
	 *            the datas to set
	 */
	public void setDatas(DataPackage<E> datas) {
		this.datas = datas;
	}

	/**
	 * Get the selects items.
	 * 
	 * @return the selects.
	 */
	public String[] get_selects() {
		return _selects;
	}

	/**
	 * Set the selects items.
	 * 
	 * @param selects
	 */
	public void set_selects(String[] selects) {
		this._selects = selects;
	}

	/**
	 * Get the ActionContext
	 * 
	 * @return ActionContext
	 */
	public static ActionContext getContext() {
		return ActionContext.getContext();
	}

	/**
	 * Get the VallueObject.
	 * 
	 * @return Returns the content.
	 */
	public ValueObject getContent() {
		return content;
	}

	/**
	 * Set the Value Object
	 * 
	 * @param content
	 *            The content to set.
	 */
	public void setContent(ValueObject content) {
		this.content = content;
	}

	/**
	 * Get the session id
	 * 
	 * @return the session id.
	 */
	public String getSessionid() {
		return ServletActionContext.getRequest().getSession().getId();
	}

	/**
	 * The abstract method to get the process.
	 * 
	 * @return The run time process
	 */
	public abstract IRunTimeProcess<E> getProcess();

	/**
	 * Get the domain.
	 * 
	 * @return The domain.
	 */
	public String getDomain() {
		if (domain != null && domain.trim().length() > 0)
			return domain;

		try {
			return getUser().getDomainid();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * Set the domain
	 * 
	 * @param The
	 *            domain to set.
	 */
	public void setDomain(String domain) {
		this.domain = domain;
		getContent().setDomainid(domain);
	}

	/**
	 * Get the application
	 * 
	 * @return
	 */
	public String getApplication() {
		return application;
	}

	/**
	 * Set the application
	 * 
	 * @param The
	 *            application to set.
	 */
	public void setApplication(String application) {
		this.application = application;
		getContent().setApplicationid(application);
	}

	/**
	 * Get a anonymous user.
	 * 
	 * @return The anonymous user.
	 * @throws Exception
	 */
	private WebUser getAnonymousUser() throws Exception {
		UserVO vo = new UserVO();

		vo.getId();
		vo.setName("GUEST");
		vo.setLoginno("guest");
		vo.setLoginpwd("");
		vo.setRoles(null);
		vo.setEmail("");

		return new WebUser(vo);
	}
}
