package OLink.bpm.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import OLink.bpm.core.dynaform.document.ejb.Document;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.converters.AbstractArrayConverter;
import org.apache.log4j.Logger;

public class ObjectUtil {
	public final static Logger log = Logger.getLogger(ObjectUtil.class);

	public static final String SETTER_PREFIX = "set";

	private static final Map<Class<?>, Map<MethodKey, Method>> class2MethodMap = 
		new ConcurrentHashMap<Class<?>, Map<MethodKey, Method>>();

	/**
	 * 类与方法的映射（方法名全部大写）
	 */
	private static final Map<Class<?>, Map<MethodKey, Method>> class2MethodUpperCaseMap = 
		new ConcurrentHashMap<Class<?>, Map<MethodKey, Method>>();

	/**
	 * 基础类型与包装类型的映射
	 */
	static final Map<Class<?>, Class<?>> base2PackClassMap = new HashMap<Class<?>, Class<?>>();

	static {
		// 注册转换器
		BeanUtilsBean util = BeanUtilsBean.getInstance();
		util.getConvertUtils().register(new IntegerArrayConverter(), int[].class);

		// 初始化Document
		Method[] methods = Document.class.getMethods();
		Map<MethodKey, Method> name2MethodMap = new ConcurrentHashMap<MethodKey, Method>();
		Map<MethodKey, Method> name2MethodUpperCaseMap = new ConcurrentHashMap<MethodKey, Method>();
		for (int i = 0; i < methods.length; i++) {
			name2MethodMap.put(new MethodKey(methods[i].getName(), methods[i].getParameterTypes()), methods[i]);
			MethodKey key = new MethodKey(methods[i].getName().toUpperCase(), methods[i].getParameterTypes());
			// System.out.println(key);
			name2MethodUpperCaseMap.put(key, methods[i]);
		}
		class2MethodMap.put(Document.class, name2MethodMap);
		class2MethodUpperCaseMap.put(Document.class, name2MethodUpperCaseMap);

		base2PackClassMap.put(int.class, Integer.class);
		base2PackClassMap.put(double.class, Double.class);
		base2PackClassMap.put(long.class, Long.class);
		base2PackClassMap.put(boolean.class, Boolean.class);
		base2PackClassMap.put(char.class, Character.class);
		base2PackClassMap.put(byte.class, Byte.class);
		base2PackClassMap.put(short.class, Short.class);
		base2PackClassMap.put(float.class, Float.class);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Document dest = new Document();
		//Document orig = new Document();
		// orig.set_new(true);
		//
		// copyByFields(dest, orig, true);

		try {
			// setProperty(dest, "lastmodified", new Timestamp(new
			// Date().getTime()), false);
			setProperty(dest, "istmp", false, false);

			System.out.println(dest.getIstmp());
			System.out.println(dest.getStateid());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// String name = getSimpleName(ObjectUtil.class);
	}

	public static Object copyProperties(Object dest, Object orig) throws IllegalAccessException,
			InvocationTargetException {
		BeanUtils.copyProperties(dest, orig);
		return dest;
	}

	/**
	 * 直接使用对象的属性进行复制
	 * 
	 * @param dest
	 *            目标对象
	 * @param orig
	 *            原对象
	 * @param excludeNull
	 *            是否排除空值
	 */
	public static void copyByFields(Object dest, Object orig, boolean excludeNull) {
		Field[] fields = orig.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			try {
				fields[i].setAccessible(true);
				if (Modifier.isFinal(fields[i].getModifiers())) {
					continue;
				}

				Object value = fields[i].get(orig);
				if (excludeNull) {
					if (value != null) {
						fields[i].set(dest, value);
					}
				} else {
					fields[i].set(dest, value);
				}

			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	public static Collection<Field> getClassAllFields(Class<?> clazz) {
		Collection<Field> rtn = new ArrayList<Field>();
		Class<?> current = clazz;

		while (current != null) {
			Field[] fields = current.getDeclaredFields();
			rtn.addAll(Arrays.asList(fields));

			current = current.getSuperclass();
		}

		return rtn;
	}

	public static void setProperty(Object bean, String name, Object value) {
		setProperty(bean, name, value, true);
	}

	public static void setProperty(Object bean, String name, Object value, boolean caseSensitive) {
		if (value == null) {
			// Nothing to do
			return;
		}
		StringBuffer methodName = new StringBuffer(name);
		methodName.setCharAt(0, Character.toUpperCase(methodName.charAt(0)));
		methodName.insert(0, SETTER_PREFIX);

		Method method = getMethodByName(bean, methodName.toString(), value, caseSensitive);
		if (method != null) {
			invokeMethod(bean, method, new Object[] { value });
		}
	}

	/**
	 * 根据方法名称获取方法
	 * 
	 * @param bean
	 * @param methodName
	 * @param caseSensitive
	 * @return
	 */
	private static Method getMethodByName(Object bean, String methodName, Object value, boolean caseSensitive) {
		if (class2MethodMap.containsKey(bean.getClass())) { // 从缓存中获取
			if (caseSensitive) {
				Map<MethodKey, Method> name2MethodMap = class2MethodMap.get(bean.getClass());
				return name2MethodMap.get(new MethodKey(methodName, new Class[] { value.getClass() }));
			} else {
				Map<MethodKey, Method> name2MethodUpperCaseMap = class2MethodUpperCaseMap.get(bean.getClass());
				return name2MethodUpperCaseMap.get(new MethodKey(methodName.toUpperCase(), new Class[] { value
						.getClass() }));
			}
		} else {
			Method[] methods = bean.getClass().getMethods();
			MethodKey otherKey = new MethodKey(methodName, new Class[] { value.getClass() });

			for (int i = 0; i < methods.length; i++) {
				Method method = methods[i];
				MethodKey key = new MethodKey(method.getName(), method.getParameterTypes());

				if (caseSensitive) {
					if (key.equals(otherKey)) {
						return method;
					}
				} else {
					if (key.equalsIgnoreCase(otherKey)) {
						return method;
					}
				}
			}
		}

		return null;
	}

	public static Collection<Method> getWriteMethods(Object bean) {
		Collection<Method> rtn = new ArrayList<Method>();

		Method[] methods = bean.getClass().getMethods();
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getName().startsWith(SETTER_PREFIX)) {
				rtn.add(methods[i]);
			}
		}
		return rtn;
	}

	public static void invokeMethod(Object bean, Method method, Object[] values) {
		try {
			boolean allowInvoke = true;

			Class<?>[] paramClazzs = method.getParameterTypes();

			if (paramClazzs.length != values.length) {
				allowInvoke = false;
			} else {
				for (int i = 0; i < paramClazzs.length; i++) {
					// 是否可以转型为ParameterType

					if (!paramClazzs[i].isAssignableFrom(values[i].getClass()) && !paramClazzs[i].isPrimitive()) {
						allowInvoke = false;
						break;
					}
				}
			}

			if (allowInvoke) {
				method.invoke(bean, values);
			}
		} catch (IllegalArgumentException e) {
			log.debug(method.getName() + " error: " + e.getMessage());
		} catch (IllegalAccessException e) {
			log.debug(method.getName() + " error: " + e.getMessage());
		} catch (InvocationTargetException e) {
			log.debug(method.getName() + " error: " + e.getMessage());
		}
	}

	/**
	 * 使用序列化进行深度克隆
	 * 
	 * @param obj
	 * @return
	 */
	public static Object clone(Object obj) {
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bout);
			out.writeObject(obj);
			out.close();

			ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
			ObjectInputStream in = new ObjectInputStream(bin);
			Object newObj = in.readObject();
			in.close();

			return newObj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 对于org.apache.commons.beanutils.converters.IntegerArrayConverter的扩展,
	 * 处理Object[]对象
	 * 
	 * @author Nicholas
	 * 
	 */
	public static class IntegerArrayConverter extends AbstractArrayConverter {
		int[] model = new int[0];
		Object[] objects = new Object[0];

		/**
		 * @SuppressWarnings convert方法不支持泛型
		 * *(non-Javadoc)
		 * @see AbstractArrayConverter#convert(Class, Object)
		 */
		@SuppressWarnings("unchecked")
		public Object convert(Class type, Object value) {
			// Deal with a null value
			if (value == null) {
				if (useDefault) {
					return (defaultValue);
				} else {
					throw new ConversionException("No value specified");
				}
			}

			// Deal with the no-conversion-needed case
			if (model.getClass() == value.getClass()) {
				return (value);
			}

			// Deal with input value as a String array
			if (strings.getClass() == value.getClass()) {
				try {
					String values[] = (String[]) value;
					int results[] = new int[values.length];
					for (int i = 0; i < values.length; i++) {
						results[i] = Integer.parseInt(values[i]);
					}
					return (results);
				} catch (Exception e) {
					if (useDefault) {
						return (defaultValue);
					} else {
						throw new ConversionException(value.toString(), e);
					}
				}
			}

			// Deal with input value as a Object array
			if (objects.getClass() == value.getClass()) {
				try {
					Object[] values = (Object[]) value;
					int results[] = new int[values.length];

					for (int i = 0; i < values.length; i++) {
						if (values[i].getClass() == Integer.class) {
							results[i] = (((Integer) values[i]).intValue());
						} else if (values[i].getClass() == String.class) {
							results[i] = Integer.parseInt((String) values[i]);
						}
					}

					return results;
				} catch (Exception e) {
					if (useDefault) {
						return (defaultValue);
					} else {
						throw new ConversionException(value.toString(), e);
					}
				}
			}

			// Parse the input value as a String into elements
			// and convert to the appropriate type
			try {
				List list = parseElements(value.toString());
				int results[] = new int[list.size()];
				for (int i = 0; i < results.length; i++) {
					results[i] = Integer.parseInt((String) list.get(i));
				}
				return (results);
			} catch (Exception e) {
				if (useDefault) {
					return (defaultValue);
				} else {
					throw new ConversionException(value.toString(), e);
				}
			}
		}
	}

	public static String getSimpleName(Class<?> clazz) {
		String fullName = clazz.getName();
		String simpleName = fullName.substring(fullName.lastIndexOf(".") + 1, fullName.length());
		return simpleName;
	}
}

class MethodKey implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5322967332090584316L;

	String methodName;

	Class<?>[] methodParameters;

	public MethodKey(String methodName, Class<?>[] methodParameters) {
		this.methodName = methodName;
		this.methodParameters = methodParameters;
	}

	// public native int hashCode();

	public String toString() {
		StringBuffer tmp = new StringBuffer();
		if (methodParameters.length > 0) {
			tmp.append("(");
			for (int i = 0; i < methodParameters.length; i++) {
				if (methodParameters[i] != null)
					tmp.append(methodParameters[i]).append(",");
			}
			tmp.deleteCharAt(tmp.lastIndexOf(","));
			tmp.append(")");
		}

		return methodName + tmp;
	}

	public boolean equals(Object obj) {
		if (obj != null && obj instanceof MethodKey) {
			MethodKey ck = (MethodKey) obj;
			if ((methodName != null && methodName.equals(ck.methodName))) {
				if (methodParameters != null) {
					if (methodParameters.length == ck.methodParameters.length) {
						for (int i = 0; i < methodParameters.length; i++) {
							Class<?> param1 = methodParameters[i];
							Class<?> param2 = ck.methodParameters[i];
							if (param1 != null) {
								if (param2.isPrimitive()) {
									Class<?> packageClass = ObjectUtil.base2PackClassMap.get(param2);
									if (!packageClass.equals(param1)) {
										return false;
									}
								} else {
									if (!param2.isAssignableFrom(param1)) {
										return false;
									}
								}
							} else {
								// 参数都未空
								if (param2 != null)
									return false;
							}
						}
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean equalsIgnoreCase(Object obj) {
		if (obj != null && obj instanceof MethodKey) {
			MethodKey ck = (MethodKey) obj;
			if ((methodName != null && methodName.equalsIgnoreCase(ck.methodName))) {
				if (methodParameters != null) {
					if (methodParameters.length == ck.methodParameters.length) {
						for (int i = 0; i < methodParameters.length; i++) {
							Class<?> param1 = methodParameters[i];
							Class<?> param2 = ck.methodParameters[i];
							if (param1 != null) {
								if (!param2.isAssignableFrom(param1))
									return false;
							} else {
								if (param2 != null)
									return false;
							}
						}
						return true;
					}
				}
			}
		}
		return false;
	}

	public int hashCode() {
		return methodName.hashCode();
	}
}
