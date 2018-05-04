package OLink.bpm.util.cache;

import java.util.HashMap;

public interface ICacheProvider {
	String DEFAULT_CACHE_NAME = "DEFAULT_CACHE";

	IMyCache getDefaultCache();

	IMyCache createCache(String name, int maxElementsInMemory,
						 boolean overflowToDisk, boolean eternal, long timeToLiveSeconds,
						 long timeToIdleSeconds);

	IMyCache getCache(String name);

	void clearCache(String name);

	String[] getCacheNames();

	void setClearedNames(HashMap<String, MethodCacheCleaner> clearedNames);

	boolean clearByCacheName(String cacheName);

	/**
	 * 获取所有需要清除缓存的方法名称
	 * 
	 * @return 所有名称
	 */
	String[] getClearedNames();
}
