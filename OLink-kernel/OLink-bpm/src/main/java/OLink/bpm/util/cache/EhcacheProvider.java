package OLink.bpm.util.cache;

import java.util.HashMap;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class EhcacheProvider implements ICacheProvider {
	CacheManager manager = CacheManager.create();

	HashMap<String, MethodCacheCleaner> clearedNames = new HashMap<String, MethodCacheCleaner>();

	public IMyCache createCache(String name, int maxElementsInMemory, boolean overflowToDisk,
			boolean eternal, long timeToLiveSeconds, long timeToIdleSeconds) {
		Cache ehcache = new Cache(name, maxElementsInMemory, overflowToDisk, eternal, timeToLiveSeconds,
				timeToIdleSeconds);
		manager.addCache(ehcache);

		MyCache mycache = new MyCache(ehcache);
		return mycache;
	}

	/**
	 * 获取默认缓存对象
	 *  
	 * @return 返回默认缓存对象
	 */
	public IMyCache getDefaultCache() {
		return getCache(DEFAULT_CACHE_NAME);
	}

	/**
	 * 根据缓存名称获取缓存对象
	 * 
	 * @return 返回缓存对象
	 */
	public IMyCache getCache(String name) {
		Cache c = manager.getCache(name);
		if (c != null) {
			MyCache mycache = new MyCache(c);
			return mycache;
		} else {
			return null;
		}
	}

	/**
	 * 内存回收
	 */
	public void finalize() throws Throwable {
		manager.shutdown();
	}

	/**
	 * 根据缓存名称清除缓存
	 */
	public void clearCache(String name) {
		Cache cache = manager.getCache(name);
		if (cache != null) {
			try {
				cache.removeAll();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (CacheException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取所有需要清除缓存的方法名称
	 * 
	 * @return 所有名称
	 */
	public String[] getCacheNames() {
		return manager.getCacheNames();
	}

	/**
	 * 获取所有需要清除缓存的方法名称
	 * 
	 * @return 所有名称
	 */
	public String[] getClearedNames() {
		return clearedNames.keySet().toArray(new String[clearedNames.keySet().size()]);
	}

	/**
	 * 清除所有缓存
	 */
	void clearAll() {
		String[] cacheNames = getCacheNames();
		for (int i = 0; i < cacheNames.length; i++) {
			clearCache(cacheNames[i]);
		}
	}
	/**
	 * 根据缓存方法名称清除缓存
	 * 
	 * @return true|false:true清除成功，false清除失败
	 */
	public boolean clearByCacheName(String cacheName) {
		MethodCacheCleaner cleaner = clearedNames.get(cacheName);
		if (cleaner != null && cleaner.isClearAll()) {
			clearAll();
			return true;
		}
		return false;
	}

	/**
	 * 设置清除掉的缓存键值对
	 */
	public void setClearedNames(HashMap<String, MethodCacheCleaner> clearedNames) {
		this.clearedNames = clearedNames;
	}
}

class MyCache implements IMyCache {
	Cache cache;

	MyCache(Cache cache) {
		this.cache = cache;
	}

	public IMyElement get(Object key) {
		if (key == null) {
			return null;
		}

		Object element = cache.get(key);
		if (element == null) {
			return null;
		}
		return new MyElement(cache.get(key));
	}

	public void put(IMyElement element) {
		Element em = ((MyElement) element).getElement();
		cache.put(em);
	}

	public void put(Object key, Object value) {
		MyElement element = new MyElement(key, value);
		put(element);
	}

}

class MyElement implements IMyElement {
	Element element;

	MyElement(Element element) {
		this.element = element;
	}

	MyElement(Object key, Object value) {
		this.element = new Element(key, value);
	}

	public Element getElement() {
		return element;
	}

	public Object getValue() {
		return this.element.getObjectValue();
	}

	public Object getKey() {
		return this.element.getObjectKey();
	}

}
