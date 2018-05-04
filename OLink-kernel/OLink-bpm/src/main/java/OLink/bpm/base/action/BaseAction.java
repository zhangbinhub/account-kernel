package OLink.bpm.base.action;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.constans.Environment;
import OLink.bpm.constans.Web;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.util.property.MultiLanguageProperty;
import org.apache.log4j.Logger;

import OLink.bpm.base.ejb.IDesignTimeProcess;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.StringUtil;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionSupport;

/**
 * The abstract action, it basic class for the action of run time instance.
 * 
 */
public abstract class BaseAction<E> extends ActionSupport implements Action {
	/**
	 * The serial version uid.
	 */
	private static final long serialVersionUID = 1715838387735663211L;
	/**
	 * The default logger.
	 */
	private static final Logger log = Logger.getLogger(BaseAction.class);
	/**
	 * The inner value object
	 */
	protected ValueObject content = null;
	/**
	 * The inner data package
	 */
	protected DataPackage<E> datas = null;
	/**
	 * The inner design time process
	 */
	protected IDesignTimeProcess<E> process = null;
	/**
	 * The selected items in web
	 */
	protected String[] _selects = null;
	/**
	 * The parameter table
	 */
	protected ParamsTable params;
	/**
	 * The applcation name.
	 */
	protected String application;

	/**
	 * BaseAction constructor
	 * 
	 * @param process
	 *            The BaseProcess
	 * @param content
	 *            The ValueObject
	 */
	public BaseAction(IDesignTimeProcess<E> process, ValueObject content) {
		this.process = process;
		this.content = content;
	}

	/**
	 * Get WebUser Object.
	 * 
	 * @SuppressWarnings WebWork不支持泛型
	 * 
	 * @return WebUser Object user
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public WebUser getUser() throws Exception {
		Map session = getContext().getSession();

		WebUser user = (WebUser) session.get(getWebUserSessionKey());

		if (user == null) {
			user = getAnonymousUser();
		}

		return user;
	}

	public String getWebUserSessionKey() {
		return Web.SESSION_ATTRIBUTE_USER;
	}

	/**
	 * The action for return back in web.
	 * 
	 * @return��"SUCCESS" when action run successfully, "ERROR" otherwise.
	 * @throws Exception
	 */
	public String doBack() {
		return null;
	}

	/**
	 * The action to initiate a new value object back.
	 * 
	 * @return "SUCCESS" when action run successfully, "ERROR" otherwise.
	 * @throws Exception
	 */
	public String doNew() {
		return SUCCESS;
	}

	/**
	 * The action to edit a object.
	 * 
	 * @SuppressWarnings WebWork不支持泛型
	 * @return "SUCCESS" when action run successfully, "ERROR" otherwise.
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String doEdit() {
		try {
			Map params = getContext().getParameters();

			String id = ((String[]) params.get("id"))[0];
			ValueObject contentVO = process.doView(id);
			setContent(contentVO);
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("", e.getMessage());
			return INPUT;
		}

		return SUCCESS;
	}

	static String getReqClass(String str) {
		int loc = str.indexOf("$");
		if (loc >= 0)
			return str.substring(loc);
		else
			return str;
	}

	/**
	 * The action to save a object.
	 * 
	 * @return "SUCCESS" when action run successfully, "INPUT" when the input
	 *         doesn't pass validation. "ERROR" when error occur.
	 * @throws Exception
	 */
	public String doSave() {
		try {
			// Save the value object and return the success message.
			// String str=getReqClass(process.getClass().toString());
			// if(str.equals("$WarpApplicationProcessBean"))
			// new ApplicationProcessBean().doCreateOrUpdate(content);
			// if(str.equals("$WarpDataSourceProcessBean"))
			// new DataSourceProcessBean().doCreateOrUpdate(content);
			// else process.doCreateOrUpdate(content);
			process.doCreateOrUpdate(content);

			this.addActionMessage("{*[Save_Success]*}");
			return SUCCESS;
		} catch (Exception e) {
			// Catch the exception and return the error message.
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}
	}

	/**
	 * The action to view a object.
	 * 
	 * @SuppressWarnings WebWork不支持泛型
	 * @return "SUCCESS" when action run successfully, "ERROR" otherwise.
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String doView() {
		try {
			Map params = getContext().getParameters();

			String[] ids = (String[]) (params.get("id"));
			String id = (ids != null && ids.length > 0) ? ids[0] : null;

			ValueObject contentVO = process.doView(id);
			setContent(contentVO);
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("", e.getMessage());
			return INPUT;
		}

		return SUCCESS;
	}

	/**
	 * The action to delete the selected object.
	 * 
	 * @return "SUCCESS" when action run successfully, "ERROR" otherwise.
	 * @throws Exception
	 */
	public String doDelete() {
		try {
			if (_selects != null)
				process.doRemove(_selects);

			addActionMessage("{*[delete.successful]*}");
			return SUCCESS;
		} catch (Exception e) {
			if (e.getMessage() != null
					&& e.getMessage().indexOf(
							"Could not execute JDBC batch update") > -1) {
				addFieldError("", "{*[Resource.has.been.cited]*}");
			} else {
				LOG.error(this.getClass().getName() + "doDelete", e);
				addFieldError("", e.getMessage());
			}
			return INPUT;
		}
	}

	/**
	 * The action to query objects.
	 * 
	 * @return "SUCCESS" when action run successfully, "ERROR" otherwise.
	 * @throws Exception
	 */
	public String doList() {
		try {
			this.validateQueryParams();
			datas = this.process.doQuery(getParams(), getUser());
		} catch (Exception e) {
			log.error(this.getClass().getName() + ".doList", e);
			addFieldError("", e.getMessage());
			return INPUT;
		}

		return SUCCESS;
	}

	/**
	 * The default action.
	 * 
	 * @return "SUCCESS" when action run successfully, "ERROR" otherwise.
	 * @throws Exception
	 */
	public String doDefault() {
		return INPUT;
	}

	/**
	 * Get the ActionContext
	 * 
	 * @return ActionContext
	 */
	public static ActionContext getContext() {
		ActionContext context = ActionContext.getContext();
		return context;
	}

	/**
	 * Get the environment
	 * 
	 * @return The environment
	 */
	public Environment getEnvironment() {
		String ctxPath = ServletActionContext.getRequest().getContextPath();

		Environment evt = Environment.getInstance();
		evt.setContextPath(ctxPath);

		return evt;
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
	 * Set the ValueObject
	 * 
	 * @param content
	 *            The content to set.
	 */
	public void setContent(ValueObject content) {
		this.content = content;
	}

	/**
	 * Get the Parameters table
	 * 
	 * @return ParamsTable
	 */
	public ParamsTable getParams() {
		if (params == null) {
			// If the parameters table is empty, then initiate it.
			params = ParamsTable.convertHTTP(getRequest());

			// put all the request parameters map in to parameters table.
			// putRequestParameters();

			// put the application id to parameters table.
			if (getApplication() != null)
				params.setParameter("application", getApplication());
			// put the session id to parameters table.
			if (getSessionid() != null)
				params.setSessionid(getSessionid());

			// put the page line count id to parameters table.
			if (params.getParameter("_pagelines") == null)
				params.setParameter("_pagelines", Web.DEFAULT_LINES_PER_PAGE);
		}

		return params;
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
	 * @param datas
	 *            the datas to set
	 */
	public void setDatas(DataPackage<E> datas) {
		this.datas = datas;
	}

	/**
	 * Get the selected items.
	 * 
	 * @return the selects.
	 */
	public String[] get_selects() {
		return _selects;
	}

	/**
	 * Set the selected items.
	 * 
	 * @param selects
	 */
	public void set_selects(String[] selects) {
		this._selects = selects;
	}

	/**
	 * Get the session id.
	 * 
	 * @return The session id.
	 */
	public String getSessionid() {
		return getSession().getId();
	}

	/**
	 * Get the application
	 * 
	 * @return The application
	 */
	public String getApplication() {
		return application;
	}

	/**
	 * Set the application
	 * 
	 * @param application
	 *            The application to set.
	 */
	public void setApplication(String application) {
		this.application = application;
		if (!StringUtil.isBlank(application)) {
			getContent().setApplicationid(application);
		}
	}

	/**
	 * Get a anonymous user.
	 * 
	 * @return The anonymous user.
	 * @throws Exception
	 */
	protected WebUser getAnonymousUser() throws Exception {
		UserVO vo = new UserVO();

		vo.getId();
		vo.setName("GUEST");
		vo.setLoginno("guest");
		vo.setLoginpwd("");
		vo.setRoles(null);
		vo.setEmail("");

		return new WebUser(vo);
	}

	/**
	 * Put all the request parameters map in to parameters table.
	 * 
	 * @SuppressWarnings servlet api不支持泛型
	 */
	@SuppressWarnings("unchecked")
	protected void putRequestParameters() {
		HttpServletRequest request = getRequest();
		Map m = request.getParameterMap();
		String realPath = getSession().getServletContext().getRealPath("/");
		params.setParameter("realPath", realPath);
		params.setContextPath(request.getContextPath());

		Iterator<Entry<String, Object>> iter = m.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, Object> entry = iter.next();
			String name = entry.getKey();
			Object value = entry.getValue();
			try {
				// If there is only one string in the string array, the put the
				// string only, not array.
				if (value instanceof String[])
					if (((String[]) value).length > 1)
						params.setParameter(name, value);
					else
						params.setParameter(name, ((String[]) value)[0]);
				else
					params.setParameter(name, value);
			} catch (Exception e) {
				log.warn("Set parameter: " + name + " failed, the value is: "
						+ value);
			}
		}
		params.setHttpRequest(request);
	}

	private HttpServletRequest getRequest() {
		return ServletActionContext.getRequest();
	}

	private HttpSession getSession() {
		return ServletActionContext.getRequest().getSession();
	}

	/**
	 * 验证查询参数中是否包含特殊字符，防止sql注入。
	 * <p>
	 * 避免进入内容后，退出提交内容到列表检查
	 * </p>
	 * 
	 * @throws Exception
	 * @see {@link #specialSymbols(String)}
	 * @see {@link #getQueryRegex()}
	 */
	protected void validateQueryParams() throws Exception {
		// 避免进入内容后，退出到列表检查
		if (content == null || content.getId() == null) {
			ParamsTable params = getParams();
			Set<String> keys = params.getParams().keySet();
			for (Iterator<String> it = keys.iterator(); it.hasNext();) {
				String key = it.next();
				// "realPath"：是方法putRequestParameters中的realPath
				if ("realPath".equals(key))
					continue;
				Object value = params.getParameter(key);
				if (value instanceof String) {
					// 不检查Button值
					if (key.indexOf("Button") >= 0
							|| value.toString().indexOf("<img src=") >= 0) {
						continue;
					}

					String string = value.toString().trim();
					if (specialSymbols(string)) {
						throw new Exception(
								"{*[core.special.symbols.error]*}: "
										+ value.toString());
					} else if (string.indexOf('_') >= 0) {
						// "_" 会导致查找到所有数据
						if (string.replaceAll("_", "").trim().length() == 0) {
							throw new Exception(
									"{*[core.special.symbols.error]*}: "
											+ string);
						}
					}
				}
			}
		}
	}

	/**
	 * 默认常用特殊字符<br>
	 * [!$^&*+=|{}';'\",<>/?~！#￥%……&*——|{}【】‘；：”“'。，、？]
	 */
	public static final String DEFAULT_QUERY_REGEX = "[!$^&*+=|{}';'\",<>/?~！#￥%……&*——|{}【】‘；：”“'。，、？]";

	/**
	 * 判断查询参数中是否以特殊字符开头，如果以特殊字符开头则返回true，否则返回false
	 * 
	 * @param value
	 * @return
	 * @see {@link #getQueryRegex()}
	 * @see {@link #DEFAULT_QUERY_REGEX}
	 */
	protected boolean specialSymbols(String value) {
		if (StringUtil.isBlank(value)) {
			return false;
		}
		Pattern pattern = Pattern.compile(getQueryRegex());
		Matcher matcher = pattern.matcher(value);

		char[] specialSymbols = getQueryRegex().toCharArray();

		boolean isStartWithSpecialSymbol = false; // 是否以特殊字符开头
		for (int i = 0; i < specialSymbols.length; i++) {
			char c = specialSymbols[i];
			if (value.indexOf(c) == 0) {
				isStartWithSpecialSymbol = true;
				break;
			}
		}

		return matcher.find() && isStartWithSpecialSymbol;
	}

	/**
	 * 获取查询过滤的非法字符
	 * 
	 * @return
	 */
	protected String getQueryRegex() {
		return DEFAULT_QUERY_REGEX;
	}

	/**
	 * 根据多语言Key获取不同语言Key所对应的字符串
	 * 
	 * @param key
	 * @return
	 * @see #getMultiLanguage(String, String)
	 * @author Tom
	 */
	public String getMultiLanguage(String key) {
		return this.getMultiLanguage(key, key);
	}

	/**
	 * 根据多语言Key获取不同语言Key所对应的字符串
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 * @see MultiLanguageProperty#getProperty(String,
	 *      String, String)
	 * @author Tom
	 */
	public String getMultiLanguage(String key, String defaultValue) {
		try {
			HttpSession session = getSession();
			String language = (String) session
					.getAttribute(Web.SESSION_ATTRIBUTE_USERLANGUAGE);
			return MultiLanguageProperty.getProperty(language, key,
					defaultValue);
		} catch (Exception e) {
			log.warn("Load multilanguage " + key + "error: " + e.getMessage());
		}
		return defaultValue;
	}

}
