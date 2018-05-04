package OLink.bpm.base.action;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import OLink.bpm.util.StringUtil;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.log4j.Logger;

import OLink.bpm.util.DateUtil;

/**
 * 本类是对http协议对象数进行封装, 并可以对参数进行基本的增,删和格式化参数等功能 并通过实现 java.io.Serializable
 * 接口以启用其序列化功能,
 */
public class ParamsTable implements Serializable {
	private static final Logger LOG = Logger.getLogger(ParamsTable.class);

	/**
	 * 版本的UID.
	 */
	private static final long serialVersionUID = -3970066605460641346L;
	/**
	 * DebugCookie名
	 */
	public static final String DEBUG_COOKIE_NAME = "DEBUG";
	/**
	 * 初始化参数集合(Map)
	 */
	private HashMap<String, Object> params = new HashMap<String, Object>();

	/**
	 * The site http
	 */
	private String siteHttp;

	/**
	 * The context path
	 */
	private String contextPath;

	/**
	 * The session id
	 */
	private String sessionid;

	private transient HttpServletRequest httpRequest;

	public ParamsTable() {

	}

	public ParamsTable(ParamsTable params) {
		this.setHttpRequest(params.getHttpRequest());
		this.setSessionid(params.getSessionid());
		this.setContextPath(params.getContextPath());
	}

	/**
	 * Set参数到URL.以参数的形式传递
	 * 
	 * @param name
	 *            URL参数名
	 * @param value
	 *            URL参数值.
	 */
	public void setParameter(String name, Object value) {
		if (value instanceof String) {
			params.put(name, value);
		} else {
			params.put(name, value);
		}
	}

	/**
	 * 取出参数
	 * 
	 * @param name
	 *            URL参数名
	 * @return URL参数值
	 */
	public Object getParameter(String name) {
		return params.get(name);
	}

	/**
	 * 取出URL参数值并格式化成字符串(java.lang.String)
	 * 
	 * @param name
	 *            URL参数名
	 * @return URL参数值
	 */
	public String getParameterAsString(String name) {
		Object obj = params.get(name);

		if (obj instanceof String)
			return StringUtil.dencodeHTML((String) obj);
		else if (obj instanceof String[])
			return StringUtil.unite((String[]) obj);
		else
			return (obj != null) ? StringUtil.dencodeHTML(obj.toString())
					: null;
	}

	/**
	 * 取出URL参数值并格式化成数组
	 * 
	 * @param name
	 *            URL参数名
	 * @return URL参数值
	 */
	public String[] getParameterAsArray(String name) {
		Object obj = params.get(name);
		if (obj instanceof String[])
			return (String[]) obj;
		else {
			return new String[] { (String) obj };
		}
	}

	/**
	 * 取出URL参数值并格式化成文本型
	 * 
	 * @param name
	 *            URL参数名
	 * @return URL参数值
	 */
	public String getParameterAsText(String name) {
		return getParameterAsText(name, ";");
	}

	/**
	 * 取出URL参数值并格式化成文本型,以参数可进行切割
	 * 
	 * @param name
	 *            URL参数名
	 * @param split
	 *            切割字符
	 * @return URL参数值
	 */
	public String getParameterAsText(String name, String split) {
		Object obj = params.get(name);

		if (obj instanceof String)
			return (String) obj;
		else if (obj instanceof String[])
			return StringUtil.unite((String[]) obj, split);
		else
			return (obj != null) ? obj.toString() : null;
	}

	/**
	 * 取出URL参数值并格式化成Double型
	 * 
	 * @param name
	 *            URL参数名
	 * @return URL参数值
	 */
	public Double getParameterAsDouble(String name) {
		Object obj = params.get(name);
		if (obj instanceof String) {
			String value = (String) obj;

			try {
				return new Double(value);
			} catch (Exception e) {
				return new Double(0.0);
			}
		}

		return null;
	}

	public Integer getParameterAsInteger(String name) {
		Object obj = params.get(name);
		if (obj instanceof String) {
			String value = (String) obj;

			try {
				return new Integer(value);
			} catch (Exception e) {
				return new Integer(0);
			}
		}

		return null;
	}

	public boolean getParameterAsBoolean(String name) {
		Object obj = params.get(name);
		if (obj instanceof String) {
			String value = (String) obj;

			if (!StringUtil.isBlank(value) && value.equals("true")) {
				return true;
			}
		} else
		// 增加了对obj是否Boolean类型的判断
		if (obj instanceof Boolean) {
			return Boolean.parseBoolean(obj.toString());
		}

		return false;
	}

	/**
	 * 取出URL参数值并格式化成Long型
	 * 
	 * @param name
	 *            URL参数名
	 * @return URL参数值
	 */
	public Long getParameterAsLong(String name) {
		Object obj = params.get(name);

		if (obj instanceof String) {
			String value = (String) obj;
			try {
				return Long.valueOf(value);
			} catch (Exception e) {
				return Long.valueOf(0);
			}
		}

		return null;
	}

	/**
	 * 取出URL参数值并格式化成Date型
	 * 
	 * @param name
	 *            URL参数名
	 * @return URL参数值
	 */
	public Date getParameterAsDate(String name) {
		Object obj = params.get(name);
		if (obj instanceof String) {
			try {
				String value = (String) obj;
				if (value != null && StringUtil.isDate(value))
					return new Date(DateUtil.parseDate(value).getTime());
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}

	/**
	 * 返回参数名迭代器(Iterator)
	 * 
	 * @return The parameter names.
	 */
	public Iterator<String> getParameterNames() {
		return params.keySet().iterator();
	}

	/**
	 * 可以用包装或适配器方法解决
	 * 
	 * @param Portlet
	 *            request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static ParamsTable convertHTTP(PortletRequest request) {
		ParamsTable params = new ParamsTable();

		params.siteHttp = request.getServerName();
		params.contextPath = request.getContextPath();
		// params.setParameter("realPath",
		// request.getServletContext().getRealPath("/"));
		Map m = request.getParameterMap();
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
				LOG.warn("Set parameter: " + name + " failed, the value is: "
						+ value);
			}
		}

		params.sessionid = request.getPortletSession().getId();
		// params.setHttpRequest(request);

		return params;
	}

	/**
	 * 检索来自HTTP协议的参数
	 * 
	 * @SuppressWarnings HttpServlet API不支持泛型
	 * @param request
	 *            The http request.
	 * @return The paramters tables.
	 */
	@SuppressWarnings("unchecked")
	public static ParamsTable convertHTTP(HttpServletRequest request) {
		ParamsTable params = new ParamsTable();

		params.siteHttp = request.getServerName();
		params.contextPath = request.getContextPath();
		params.sessionid = request.getSession().getId();
		// 设置真实路径
		params.setParameter("realPath", request.getSession()
				.getServletContext().getRealPath("/"));
		params.setHttpRequest(request);

		// 设置request参数
		Map m = request.getParameterMap();
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
				LOG.warn("Set parameter: " + name + " failed, the value is: "
						+ value);
			}
		}

		return params;
	}

	public HttpServletRequest getHttpRequest() {
		return httpRequest;
	}

	public void setHttpRequest(HttpServletRequest httpRequest) {
		this.httpRequest = httpRequest;
	}

	/**
	 * 取出URL参数值并格式化成Arrary型
	 * 
	 * @param name
	 *            URL参数名
	 * @param index
	 *            arrary[index]
	 * @return URL参数值
	 */
	public Object getParameter(String name, int index) {
		Object obj = params.get(name);
		if (obj != null && obj instanceof String[]) {
			String[] col = (String[]) obj;
			return col[index];
		}
		return null;
	}

	/**
	 * SetURL参数对Array
	 * 
	 * @param name
	 *            URL参数名
	 * @param index
	 *            arrary[index]
	 * @param value
	 *            URL参数值
	 */
	public void setParameter(String name, int index, Object value) {
		Object obj = params.get(name);
		if (obj != null && obj instanceof String[]) {
			String[] col = (String[]) obj;
			col[index] = (String) value;
		}
	}

	/**
	 * 取出参数的动态属性
	 * 
	 * @return 以数组(Array)形式的动态属性.
	 */
	public DynaProperty[] getDynaProperties() {
		DynaProperty[] dynaProps = new DynaProperty[params.size()];
		Iterator<String> iter = params.keySet().iterator();
		int count = 0;

		while (iter.hasNext()) {
			String paramName = iter.next();
			DynaProperty prop = new DynaProperty(paramName, params.get(
					paramName).getClass());
			dynaProps[count] = prop;
			count++;
		}
		return dynaProps;
	}

	/**
	 * 根据动态属性名取出动态属性
	 * 
	 * @param name
	 *            动态属性名
	 * @return 动态属性对象
	 */
	public DynaProperty getDynaProperty(String name) {
		DynaProperty prop = null;

		if (params.get(name) != null)
			prop = new DynaProperty(name, params.get(name).getClass());
		else
			prop = new DynaProperty(name, String.class);

		return prop;
	}

	/**
	 * 移除参数
	 * 
	 * @param name
	 *            参数名
	 */
	public void removeParameter(String name) {
		params.remove(name);
	}

	/**
	 * Get the the http site name.
	 * 
	 * @return The http site name.
	 */
	public String getSiteHttp() {
		return siteHttp;
	}

	/**
	 * 返回的部分请求的URI，指示请求的范围内。上下文路径总是先在一个请求的URI。路径以一个“/”字符，但并没有结束的"/"字符。在默认（根）
	 * Servlet的情况下，此方法返回""。该容器不解码此字符串。
	 * 
	 * @return 一个String指定请求的URI部分，指示请求的上下文
	 */
	public String getContextPath() {
		if (this.contextPath != null) {
			return this.contextPath;
		}
		return "/";

	}

	/**
	 * 设置上下文路径
	 * 
	 * @param contextPath
	 *            以http协议形式路径
	 * @uml.property name="contextPath"
	 */
	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	/**
	 * Put all the map
	 * 
	 * @param map
	 *            The map
	 */
	public void putAll(Map<String, ?> map) {
		this.params.putAll(map);
	}

	public Map<String, ?> getParams() {
		return this.params;
	}

	public boolean containsKey(Object key) {
		return this.params.containsKey(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof ParamsTable) {
			ParamsTable pt = (ParamsTable) obj;
			return this.params.equals(pt.params);
		}
		return false;
	}

	public int hashCode() {
		return this.params.hashCode();
	}

	/**
	 * 取出应用的session标识
	 * 
	 * @return the sessionid
	 */
	public String getSessionid() {
		return sessionid;
	}

	/**
	 * 设置应用的session标识
	 * 
	 * @param sessionid
	 *            the sessionid to set
	 */
	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}

	public String toString() {
		return params.toString();
	}
}
