package OLink.bpm.util.cache;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class CacheConfig {
	public static CacheConfig _application;

	private String providerClassName;

	private HashMap<String, MethodCacheConfig> methodCacheConfigs = new HashMap<String, MethodCacheConfig>();

	private HashMap<String, MethodCacheCleaner> methodCleaners = new HashMap<String, MethodCacheCleaner>();

	public static CacheConfig getInstance() throws IOException {
		if (_application == null) {
			_application = createCacheConfig();
		}
		return _application;
	}

	public static void main(String[] args) {
		try {
			createCacheConfig();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static CacheConfig createCacheConfig() throws IOException {
		CacheConfig cconf = new CacheConfig();
		Properties prop = new Properties();
		Properties clearProp = new Properties();

		InputStream is = CacheConfig.class.getClassLoader()
				.getResourceAsStream("eWAP-cache.properties");//增加 by XGY
		prop.load(is);

		is = CacheConfig.class.getClassLoader().getResourceAsStream(
				"eWAP-cache-clear.properties");//增加 by XGY
		clearProp.load(is);

		cconf.setProviderClassName(prop.getProperty("providerClassName"));
		// prop.keys().
		Enumeration<?> enum1s = prop.keys();
		while (enum1s.hasMoreElements()) {
			String key = (String) enum1s.nextElement();
			if (!key.equals("providerClassName")) {
				MethodCacheConfig methodcache = new MethodCacheConfig();
				methodcache.signature = key;

				String[] tmp = prop.getProperty(key).split(",");
				methodcache.maxElementsInMemory = Integer.parseInt(tmp[0]);
				methodcache.timeToLiveSeconds = Integer.parseInt(tmp[1]);
				methodcache.timeToIdleSeconds = Integer.parseInt(tmp[2]);

				cconf.getMethodCacheConfigs().put(key, methodcache);
			}
		}

		enum1s = clearProp.keys();
		while (enum1s.hasMoreElements()) {
			String key = (String) enum1s.nextElement();
			MethodCacheCleaner cleaner = new MethodCacheCleaner();
			String value = (String) clearProp.get(key);
			if (value.equalsIgnoreCase("All")) {
				cleaner.setClearAll(true);
			}
			cconf.getMethodCleaners().put(key, cleaner);
		}

		return cconf;
	}

	private CacheConfig() {

	}

	public String getProviderClassName() {
		return providerClassName;
	}

	public void setProviderClassName(String providerClassName) {
		this.providerClassName = providerClassName;
	}

	public HashMap<String, MethodCacheConfig> getMethodCacheConfigs() {
		return methodCacheConfigs;
	}

	public void setMethodCacheConfigs(HashMap<String, MethodCacheConfig> methodNames) {
		this.methodCacheConfigs = methodNames;
	}

	public HashMap<String, MethodCacheCleaner> getMethodCleaners() {
		return methodCleaners;
	}

	public void setMethodCleaners(HashMap<String, MethodCacheCleaner> methodCleaners) {
		this.methodCleaners = methodCleaners;
	}

}

class MethodCacheCleaner {
	boolean clearAll;

	Set<String> clearedNames = new HashSet<String>();

	public boolean isClearAll() {
		return clearAll;
	}

	public void setClearAll(boolean clearAll) {
		this.clearAll = clearAll;
	}

	public void addName(String name) {
		clearedNames.add(name);
	}
}

class MethodCacheConfig {
	String signature;

	int maxElementsInMemory;

	int timeToLiveSeconds;

	int timeToIdleSeconds;

	public int getMaxElementsInMemory() {
		return maxElementsInMemory;
	}

	public void setMaxElementsInMemory(int maxElementsInMemory) {
		this.maxElementsInMemory = maxElementsInMemory;
	}

	public int getTimeToIdleSeconds() {
		return timeToIdleSeconds;
	}

	public void setTimeToIdleSeconds(int timeToIdleSeconds) {
		this.timeToIdleSeconds = timeToIdleSeconds;
	}

	public int getTimeToLiveSeconds() {
		return timeToLiveSeconds;
	}

	public void setTimeToLiveSeconds(int timeToLiveSeconds) {
		this.timeToLiveSeconds = timeToLiveSeconds;
	}

	/**
	 * @hibernate.property column="SIGNATURE"
	 */
	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}
}
