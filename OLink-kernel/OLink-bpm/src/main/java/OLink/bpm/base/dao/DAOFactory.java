package OLink.bpm.base.dao;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * The data access object factory.
 */
public class DAOFactory implements MethodInterceptor {
	private Enhancer enhancer = new Enhancer();

	private static boolean ISCACHE = false;

	/**
	 * Retrieve the data access object.
	 * 
	 * @param voClassName
	 *            The value object class name.
	 * @return The relate data access object.
	 * @throws Exception
	 */
	public static IDesignTimeDAO<?> getDefaultDAO(String voClassName) throws Exception {
		IDesignTimeDAO<?> dao = null;

		if (voClassName.indexOf(".ejb.") > 0) {
			String factoryClassName = getDaoClassName(voClassName);

			Class<?> factoryClazz = Class.forName(factoryClassName);

			DAOFactory factory = new DAOFactory();

			Class<?>[] argtypes = new Class[1];
			argtypes[0] = String.class;

			Object[] args = new Object[1];
			args[0] = voClassName;

			if (ISCACHE) {
				dao = (IDesignTimeDAO<?>) factory.getInstrumentedClass(new Class<?>[] {},
						factoryClazz, argtypes, args);

			} else {
				dao = (IDesignTimeDAO<?>) factoryClazz.getConstructor(argtypes)
						.newInstance(args);
			}
		}

		return dao;
	}

	public Object getInstrumentedClass(Class<?>[] iclzs, Class<?> clz,
			Class<?>[] argtypes, Object[] args) {

		enhancer.setInterfaces(iclzs);
		enhancer.setSuperclass(clz);
		enhancer.setCallback(this);

		return enhancer.create(argtypes, args);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.cglib.proxy.MethodInterceptor#intercept(java.lang.Object,
	 *      java.lang.reflect.Method, java.lang.Object[],
	 *      net.sf.cglib.proxy.MethodProxy)
	 */
	public Object intercept(Object o, Method method, Object[] methodParameters,
			MethodProxy methodProxy) throws Throwable {
		if (method.getName().equals("finalize")) {
			return methodProxy.invokeSuper(o, methodParameters);
		}

		Object result;

		try {
			result = methodProxy.invokeSuper(o, methodParameters);
		} catch (Throwable t) {
			throw t;
		} finally {
		}
		return result;
	}

	/**
	 * Retrieve the data access class name.
	 * 
	 * @param voClassName
	 *            The value object class name.
	 * @return The data access class name.
	 */
	private static String getDaoClassName(String voClassName) {
		String packageName = voClassName.substring(0, voClassName
				.lastIndexOf(".ejb."))
				+ ".dao";
		String className = voClassName.substring(voClassName
				.lastIndexOf(".ejb.") + 5, voClassName.length());
		className = className.replaceAll("VO", "");

		String ormType = Constants.ormType2Str(Constants.ORM_DEFAULT);

		String daoClassName = packageName + "." + ormType + className + "DAO";
		return daoClassName;
	}
}
