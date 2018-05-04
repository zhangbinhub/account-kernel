package OLink.bpm.util.cache;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;

public class MyCacheManager {

	private static ICacheProvider _application;

	private MyCacheManager() {
	}

	public static ICacheProvider getProviderInstance()
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		if (_application == null) {
			try {
				CacheConfig cconf = CacheConfig.getInstance();

				String providerClassName = cconf.getProviderClassName();

				if (providerClassName != null) {
					Class<?> clazz = Class.forName(providerClassName);
					_application = (ICacheProvider) clazz.newInstance();

					for (Iterator<?> iter = cconf.getMethodCacheConfigs().values()
							.iterator(); iter.hasNext();) {
						MethodCacheConfig value = (MethodCacheConfig) iter
								.next();
						// 禁止将对象写到磁盘
						_application.createCache(value.signature,
								value.maxElementsInMemory, false, false,
								value.timeToIdleSeconds,
								value.timeToLiveSeconds);
					}
					_application.setClearedNames(cconf.getMethodCleaners());
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return _application;
	}

	public static String buildCacheKeyString(Class<?> clazz, Method method) {
		StringBuffer sb = new StringBuffer();
		sb.append(clazz.getName()).append(".").append(method.getName()).append(
				"(");

		Class<?>[] paramsTypes = method.getParameterTypes();
		for (int i = 0; i < paramsTypes.length; i++) {
			sb.append(paramsTypes[i].getName());
			if (i < paramsTypes.length - 1) {
				sb.append(",");
			}
		}
		sb.append(")");
		return sb.toString();
	}

	public static String buildCacheKeyString(Method method) {
		StringBuffer sb = new StringBuffer();
		sb.append(method.getDeclaringClass().getName()).append(".").append(
				method.getName()).append("(");

		Class<?>[] paramsTypes = method.getParameterTypes();
		for (int i = 0; i < paramsTypes.length; i++) {
			sb.append(paramsTypes[i].getName());
			if (i < paramsTypes.length - 1) {
				sb.append(",");
			}
		}
		sb.append(")");
		return sb.toString();
	}
}
