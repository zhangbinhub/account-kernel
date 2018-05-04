package OLink.bpm.util.xml.converter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.collection.PersistentList;
import org.hibernate.collection.PersistentMap;
import org.hibernate.collection.PersistentSet;
import org.jfree.util.Log;

//import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.javabean.JavaBeanConverter;
import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriterHelper;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public class OBPMJavaBeanConvert extends JavaBeanConverter {

	/*
	 */
	private Mapper mapper;
	private OBPMBeanProvider beanProvider;
	/**
	 * @deprecated since 1.2, no necessity for field anymore.
	 */
	private String classAttributeIdentifier;

	public OBPMJavaBeanConvert(Mapper mapper, OBPMBeanProvider beanProvider) {
		super(mapper);
		this.mapper = mapper;
		this.beanProvider = beanProvider;
	}

	public OBPMJavaBeanConvert(Mapper mapper) {
		this(mapper, new OBPMBeanProvider());
	}

//	/**
//	 * @deprecated As of 1.3, use {@link #JavaBeanConverter(Mapper)} and
//	 *             {@link com.thoughtworks.xstream.XStream#aliasAttribute(String, String)}
//	 */
//	public OBPMJavaBeanConvert(Mapper mapper, String classAttributeIdentifier) {
//		this(mapper, new OBPMBeanProvider());
//		this.classAttributeIdentifier = classAttributeIdentifier;
//	}

//	/**
//	 * @deprecated As of 1.2, use {@link #JavaBeanConverter(Mapper)} and
//	 *             {@link com.thoughtworks.xstream.XStream#aliasAttribute(String, String)}
//	 */
//	public OBPMJavaBeanConvert(ClassMapper classMapper, String classAttributeIdentifier) {
//		this((Mapper) classMapper, classAttributeIdentifier);
//	}

	public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
		final String classAttributeName = classAttributeIdentifier != null ? classAttributeIdentifier : mapper
				.aliasForSystemAttribute("class");
		beanProvider.visitSerializableProperties(source, new OBPMBeanProvider.Visitor() {
			public boolean shouldVisit(String name, Class definedIn) {
				return mapper.shouldSerializeMember(definedIn, name);
			}

			public void visit(String propertyName, Class fieldType, Class definedIn, Object newObj) {
				if (newObj != null) {
					writeField(propertyName, fieldType, newObj, definedIn);
				}
			}

			private void writeField(String propertyName, Class fieldType, Object newObj, Class definedIn) {
				String serializedMember = mapper.serializedMember(source.getClass(), propertyName);
				ExtendedHierarchicalStreamWriterHelper.startNode(writer, serializedMember, fieldType);
				newObj = processHibernateObject(newObj);
				Class actualType = newObj.getClass();
				Class defaultType = mapper.defaultImplementationOf(fieldType);

				//System.out.println(propertyName + ", actualType: " + actualType + ", definedIn: " + definedIn);
				if (!actualType.equals(defaultType) && classAttributeName != null) {
					writer.addAttribute(classAttributeName, mapper.serializedClass(actualType));
				}
				context.convertAnother(newObj);

				writer.endNode();
			}
		});
	}

	/**
	 * 处理Hibernate集合对象，讲Hibernate集合类转为一般的Java集合类
	 * 
	 * @param actualType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Object processHibernateObject(Object newObj) {
		Class<?> actualType = newObj.getClass();
		try {
			if (actualType.equals(PersistentSet.class)) {
				Set<?> set = (Set<?>) newObj;
				Set<Object> rtn = LinkedHashSet.class.newInstance();
				for (Iterator<?> iterator = set.iterator(); iterator.hasNext();) {
					Object object = iterator.next();
					rtn.add(object);
				}

				return rtn;
			} else if (actualType.equals(PersistentMap.class)) {
				Map<?, ?> hibernateMap = (Map<?, ?>) newObj;
				Map<Object, Object> rtn = LinkedHashMap.class.newInstance();
				for (Iterator iterator = hibernateMap.entrySet().iterator(); iterator.hasNext();) {
					Map.Entry<?, ?> entry = (Map.Entry<?, ?>) iterator.next();
					rtn.put(entry.getKey(), entry.getValue());
				}
				
				return rtn;
			} else if (actualType.equals(PersistentList.class)) {
				List<?> hibernateList = (List<?>) newObj;
				List<Object> rtn = ArrayList.class.newInstance();
				for (Iterator<?> iterator = hibernateList.iterator(); iterator.hasNext();) {
					Object object = iterator.next();
					rtn.add(object);
				}
				
				return rtn;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return newObj;
	}

	public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
		final Object result = instantiateNewInstance(context);

		while (reader.hasMoreChildren()) {
			reader.moveDown();

			String propertyName = mapper.realMember(result.getClass(), reader.getNodeName());

			boolean propertyExistsInClass = beanProvider.propertyDefinedInClass(propertyName, result.getClass());

			if (propertyExistsInClass) {
				try {
					Class type = determineType(reader, result, propertyName);
					Object value = context.convertAnother(result, type);
					beanProvider.writeProperty(result, propertyName, value);
				} catch (InstantiationError e) {
					Log.error("Write Property(" + propertyName + ") Error: " + e.getMessage());
				} catch (ObjectAccessException e) {
					Log.error("Write Property(" + propertyName + ") Error: " + e.getMessage());
				}

			} else if (mapper.shouldSerializeMember(result.getClass(), propertyName)) {
				throw new ConversionException("Property '" + propertyName + "' not defined in class "
						+ result.getClass().getName());
			}

			reader.moveUp();
		}

		return result;
	}

	private Object instantiateNewInstance(UnmarshallingContext context) {
		Object result = context.currentObject();
		if (result == null) {
			result = beanProvider.newInstance(context.getRequiredType());
		}
		return result;
	}

	private Class determineType(HierarchicalStreamReader reader, Object result, String fieldName) {
		final String classAttributeName = classAttributeIdentifier != null ? classAttributeIdentifier : mapper
				.aliasForSystemAttribute("class");
		String classAttribute = classAttributeName == null ? null : reader.getAttribute(classAttributeName);
		if (classAttribute != null) {
			return mapper.realClass(classAttribute);
		} else {
			return mapper.defaultImplementationOf(beanProvider.getPropertyType(result, fieldName));
		}
	}
}
