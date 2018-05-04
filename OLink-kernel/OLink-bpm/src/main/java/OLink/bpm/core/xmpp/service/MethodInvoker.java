package OLink.bpm.core.xmpp.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.jivesoftware.smack.packet.PacketExtension;

public class MethodInvoker implements PacketExtension {
	private String methodName;
	private Collection<MethodParameter> paramList = new ArrayList<MethodParameter>();

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public void addMethodParameter(String name, String type, String value) {
		paramList.add(new MethodParameter(name, type, value));
	}

	public Class<?>[] getParameterTypes() throws ServiceException {
		Collection<Object> rtn = new ArrayList<Object>();

		for (Iterator<MethodParameter> iterator = paramList.iterator(); iterator.hasNext();) {
			MethodParameter parameter = iterator.next();
			Class<?> parameterType = parameter.getJavaType();
			rtn.add(parameterType);
		}

		return rtn.toArray(new Class<?>[rtn.size()]);
	}

	public Object[] getParameterValues() throws ServiceException {
		Collection<Object> rtn = new ArrayList<Object>();

		for (Iterator<MethodParameter> iterator = paramList.iterator(); iterator.hasNext();) {
			MethodParameter parameter = iterator.next();
			rtn.add(parameter.getValue());
		}

		return rtn.toArray();
	}

	static class MethodParameter {
		private String name;
		private String type;
		private String value;
		private static final Map<String, Class<?>> javaTypeMap = new HashMap<String, Class<?>>();

		static {
			javaTypeMap.put("string", String.class);
			javaTypeMap.put("int", int.class);
			javaTypeMap.put("integer", Integer.class);
			javaTypeMap.put("double", double.class);
			javaTypeMap.put("boolean", boolean.class);
			javaTypeMap.put("date", Date.class);
			javaTypeMap.put("datetime", Timestamp.class);
		}

		public MethodParameter(String name, String type, String value) {
			this.name = name;
			this.type = type;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public String getType() {
			return type;
		}

		public Class<?> getJavaType() throws ServiceException {
			if (javaTypeMap.containsKey(type)) {
				return javaTypeMap.get(type);
			}

			throw new ServiceException("No corresponding java type, xmpp type is: " + type);
		}

		public void setType(String type) {
			this.type = type;
		}

		public Object getValue() throws ServiceException {
			ConvertUtilsBean convert = BeanUtilsBean.getInstance().getConvertUtils();
			// 类型转换
			Object obj = convert.convert(value, getJavaType());
			return obj;
		}
	}

	public String getElementName() {
		return methodName;
	}

	public String invoke(Object obj) throws SecurityException, NoSuchMethodException, ServiceException,
			IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Class<?> clazz = obj.getClass();
		Method method = clazz.getMethod(getMethodName(), getParameterTypes());
		return (String) method.invoke(obj, getParameterValues());
	}

	public String getNamespace() {
		return "";
	}

	public String toXML() {
		String xml = "<method name=\"" + methodName + "\">";
		for (Iterator<MethodParameter> iterator = paramList.iterator(); iterator.hasNext();) {
			MethodParameter parameter = iterator.next();
			try {
				xml += "<parameter name=\"" + parameter.getName() + "\" value=\"" + parameter.getValue() + "\" type=\""
						+ parameter.getType() + "\">";
				xml += "</parameter>";
			} catch (ServiceException e) {
				e.printStackTrace();
			}
		}
		xml += "</method>";

		return xml;
	}
}
