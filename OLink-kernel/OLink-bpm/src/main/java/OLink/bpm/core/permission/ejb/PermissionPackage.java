package OLink.bpm.core.permission.ejb;

//import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.constans.Web;
import OLink.bpm.core.resource.ejb.ResourceType;
import OLink.bpm.core.resource.ejb.ResourceVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.resource.ejb.ResourceProcess;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.StringUtil;
import org.apache.commons.collections.FastArrayList;
import org.apache.log4j.Logger;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

public class PermissionPackage {
	static Logger log = Logger.getLogger(PermissionPackage.class);
	private static Map<Object, Set<String>> _cache;
	/**
	 * store cache of application
	 */
	private static Map<String, Map<Object, Set<String>>> applicationCaches = null;

	private static Set<String> getApplicationPermissionSet(Object key, WebUser user) {
		
			synchronized (PermissionPackage.class) {
				if (applicationCaches == null) {
				applicationCaches = new HashMap<String, Map<Object, Set<String>>>();
			}
		}

		Map<Object, Set<String>> cache = applicationCaches.get(user.getDefaultApplication());
		if (cache == null) {
			// 清空前一个 application cache
			_cache = null;
			return getPermissionSet(key, user);
		}
		Set<String> ps = cache.get(key);
		return ps;
	}

	private static Set<String> getPermissionSet(Object key, WebUser user) {
		Set<String> ps = null;
		if (_cache == null) {
			try {
				loadCache(user);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			ps = _cache.get(key);
		}
		return ps;
	}

	public static boolean checkPermission(HttpServletRequest req, WebUser user) {
		// Start moniter
		Monitor mon = null;
		mon = MonitorFactory.start("public static boolean checkPermission(HttpServletRequest req, WebUser user)");

		if (user.isDeveloper() || user.isDomainAdmin() || user.isSuperAdmin()) {
			return true;
		}
		Object key = getPerimissionKeyByUri(req);
		Set<String> ps = getApplicationPermissionSet(key, user);
		if (ps == null || ps.contains(user.getId())) {
			return true;
		} else {
			if (user.getRoles() != null)
				for (Iterator<? extends ValueObject> iter = user.getRoles().iterator(); iter.hasNext();) {
					ValueObject vo = iter.next();
					if (ps.contains(vo.getId()))
						return true;
				}
		}
		mon.stop();
		return false;
	}

	public static boolean checkPermission(ResourceVO r, WebUser user) {
		// Start moniter
		Monitor mon = null;
		mon = MonitorFactory.start("public static boolean checkPermission(ResourceVO r, WebUser user)");

		if (user.isDeveloper() || user.isSuperAdmin()) {
			return true;
		} else if (r.getDescription().equals("App Definition") || r.getDescription().equals("Dev Studio")
				|| r.getDescription().equals("System")) {
			return false;
		}
		if (user.isDomainAdmin()) {
			return true;
		}
		if (!r.isIsprotected()) {
			return true;
		}
		try {

			Object key = getPermissionKey(r);

			Set<String> ps = getApplicationPermissionSet(key, user);

			if (ps == null || ps.contains(user.getId())) {
				return true;
			} else {
				if (user.getRoles() != null)
					for (Iterator<? extends ValueObject> iter = user.getRoles().iterator(); iter.hasNext();) {
						ValueObject vo = iter.next();
						if (ps.contains(vo.getId()))
							return true;
					}
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		// Stop monitor
		mon.stop();

		return false;
	}

	public synchronized static void clearCache() throws Exception {
		if (applicationCaches != null) {
			applicationCaches.clear();
		}
		_cache = null;
	}

	/**
	 * @SuppressWarnings MyLinkedMap不支持泛型
	 * @param user
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public synchronized static void loadCache(WebUser user) throws Exception {
		try {
			// PersistenceUtils.getSessionSignal().sessionSignal++;
			_cache = new MyLinkedMap();

			ResourceProcess rp = (ResourceProcess) ProcessFactory.createProcess(ResourceProcess.class);
			Collection<ResourceVO> cols = rp.doSimpleQuery(null, user.getDefaultApplication());
			if (cols != null) {
				for (Iterator<ResourceVO> iter = cols.iterator(); iter.hasNext();) {
					ResourceVO r = iter.next();
					if (r.isIsprotected()) {
						Object key = getPermissionKey(r);
						Set<String> ps = getPermissionSet(key, user);
						if (ps == null) {
							ps = new HashSet<String>();

							if (r.getRelatedPermissions() != null)
								for (Iterator<PermissionVO> iterator = r.getRelatedPermissions().iterator(); iterator.hasNext();) {
									PermissionVO pmvo = iterator.next();

									if (!StringUtil.isBlank(pmvo.getRoleId())) {
										ps.add(pmvo.getRoleId());
									}

								}
							_cache.put(key, ps);
						}
					}
				}
				applicationCaches.put(user.getDefaultApplication(), _cache);
			}
			log.info(_cache);
		} catch (Exception e) {
			clearCache();
			throw e;
		} finally {
			PersistenceUtils.closeSession();
		}
	}

	private static Object getPermissionKey(ResourceVO r) throws ClassNotFoundException {
		return new PerimissionKey(r);
	}

	private static Object getPerimissionKeyByUri(HttpServletRequest req) {
		String uri = req.getRequestURI();
		String querystr = req.getQueryString();
		String application = (String) req.getSession().getAttribute(Web.SESSION_ATTRIBUTE_APPLICATION);

		return getPerimissionKeyByUri(uri, querystr, application);
	}

	public static Object getPerimissionKeyByUri(String uri, String querystr, String application) {
		return new PerimissionKey(uri, querystr, application);
	}

}

class PerimissionKey extends Object {
	private FastArrayList key = new FastArrayList();

	PerimissionKey(ResourceVO r) {
		if (r.getResourceAction() != null) {
			if (r.getResourceAction().equals(ResourceType.ACTION_TYPE_ACTIONCLASS)) {
				key.add(r.getId() + "$$" + r.getActionurl() + "$$" + r.getApplicationid());
			} else if (r.getResourceAction().equals(ResourceType.ACTION_TYPE_VIEW)) {
				String viewid = r.getDisplayView();
				key.add(r.getId() + "$$" + viewid + "$$" + r.getApplicationid());
			} else if (r.getResourceAction().equals(ResourceType.ACTION_TYPE_IMP)) {
				String impid = r.getImpMappingConfig();
				key.add(r.getId() + "$$" + impid + "$$" + r.getApplicationid());
			} else if (r.getResourceAction().equals(ResourceType.ACTION_TYPE_OTHERURL)) {
				String url = r.getOtherurl();
				if (url != null) {
					int p = url.indexOf("?");
					if (p > 0) {
						String uri = url.substring(0, p);
						String querystr = url.substring(p + 1);
						setKeyByURI(uri, querystr, r.getApplicationid());
					} else {
						setKeyByURI(url, "", r.getApplicationid());
					}
				}
			} else {
				key.add(r.getId());
			}
		}

	}

	private void setKeyByURI(String uri, String querystr, String application) {
		if (uri != null) {
			if (uri.endsWith("displayView.action")) {
				if (querystr != null) {
					int p1 = querystr.indexOf("_viewid");
					int p2 = querystr.indexOf("=", p1);
					int p3 = querystr.indexOf("&", p2);
					if (p1 >= 0 && p2 > 0) {
						String viewid = p3 > 0 ? querystr.substring(p2 + 1, p3) : querystr.substring(p2);
						key.add(viewid);
					}
				}
			} else if (uri.indexOf(".action") > 0) {
				key.add(uri.substring(0, uri.indexOf(".action") + 7));
			} else {
				// int p = querystr!=null?querystr.length();
				// if (p >= 0) {
				String major = uri;
				major = major.replaceAll("\\\\", "/");
				major = major.replaceAll("//", "/");
				key.add(major);
				if (querystr != null) {
					String[] tmp = querystr.split("&");
					for (int i = 0; i < tmp.length; i++) {
						key.add(tmp[i]);
					}
					// }
				}
				// else {
				// String major = uri;
				// major = major.replaceAll("\\\\", "/");
				// major = major.replaceAll("//", "/");
				// key.add(major);
				// }
			}
		}

	}

	PerimissionKey(String uri, String querystr, String application) {
		setKeyByURI(uri, querystr, application);
	}

	public boolean equals(Object obj) {
		boolean flag = false;
		if (obj instanceof PerimissionKey) {
			PerimissionKey pk = (PerimissionKey) obj;

			if (pk.key.size() > 0 && this.key.containsAll(pk.key)) {
				flag = true;
			}
		} else {
			flag = false;
		}
		return flag;
	}
	
	public int hashCode(){
		return super.hashCode();
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for (Iterator<?> iter = key.iterator(); iter.hasNext();) {
			Object element = iter.next();
			buffer.append("{").append(element).append("}");
		}
		return buffer.toString();
	}

}

/**
 * @SuppressWarnings 不支持泛型
 * @author Administrator
 *
 */
@SuppressWarnings("unchecked")
class MyLinkedMap implements Map {
	private FastArrayList keys = new FastArrayList();

	private FastArrayList values = new FastArrayList();

	public int size() {
		return keys.size();
	}

	public boolean isEmpty() {
		return keys.isEmpty();
	}

	public boolean containsKey(Object key) {
		return keys.contains(key);
	}

	public boolean containsValue(Object value) {
		return values.contains(value);
	}

	public Object get(Object key) {
		int idx = keys.indexOf(key);
		if (idx >= 0) {
			return values.get(idx);
		}
		return null;
	}

	public Object put(Object key, Object value) {
		int idx = keys.indexOf(key);
		Object rtn = null;
		if (idx >= 0) {
			rtn = values.set(idx, value);
		} else {
			keys.add(key);
			values.add(value);
		}
		return rtn;
	}

	public Object remove(Object key) {
		int idx = keys.indexOf(key);
		Object rtn = null;
		if (idx >= 0) {
			keys.remove(idx);
			rtn = values.remove(idx);
		}
		return rtn;
	}

	public void putAll(Map map) {
		if (map != null) {
			for (Iterator iter = map.entrySet().iterator(); iter.hasNext();) {
				Entry entry =  (Entry)iter.next();
				put(entry.getKey(), entry.getValue());
			}
		}
	}

	public void clear() {
		keys.clear();
		values.clear();
	}

	public Set keySet() {
		HashSet keySet = new HashSet();
		for (Iterator iter = keys.iterator(); iter.hasNext();) {
			Object key = iter.next();
			keySet.add(key);
		}
		return keySet;
	}

	public Collection values() {
		return values;
	}

	public Set entrySet() {
		class MyEntry implements Entry {
			Object key;

			Object value;

			MyEntry(Object key, Object value) {
				this.key = key;
				this.value = value;
			}

			public Object getKey() {
				return key;
			}

			public Object getValue() {
				return value;
			}

			public Object setValue(Object arg0) {
				value = arg0;
				return value;
			}

		}

		HashSet set = new HashSet();
		for (Iterator iter = keys.iterator(); iter.hasNext();) {
			Object key = iter.next();
			set.add(new MyEntry(key, this.get(key)));
		}

		return set;
	}

}
