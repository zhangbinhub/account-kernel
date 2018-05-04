/**
 *  12/09/2008
 *
 */
package OLink.bpm.util.xml.converter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.MethodInterceptor;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.ReflectionProviderWrapper;
import com.thoughtworks.xstream.converters.reflection.SerializableConverter;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriterHelper;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.CGLIBMapper;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * This class is a patch for the existing thoughtworks
 * class.  See XSTR-423.  When a revised edition of
 * the XStream library becomes available to fix this
 * problem this class can be deprecated.
 * 
 * This only affects classes that use Hibernate and
 * have lazy loading turned on.
 * 
 * @author Jorg Schaible (amended by Frank Adcock)
 * @since XStream 1.2.2,1.3
 */
public class CGLIBEnhancedConverterHibernateFix extends SerializableConverter {

	/**
	 * @param mapper
	 * @param reflectionProvider
	 */
	public CGLIBEnhancedConverterHibernateFix(Mapper mapper,
			ReflectionProvider reflectionProvider) {
        super(mapper, new CGLIBFilteringReflectionProvider(reflectionProvider));
        this.mapper = mapper;
        this.fieldCache = new HashMap<Object, Object>();
	}

    private static String DEFAULT_NAMING_MARKER = "$$EnhancerByCGLIB$$";
    private static String CALLBACK_MARKER = "CGLIB$CALLBACK_";
    private transient Map<Object, Object> fieldCache;
    private final Mapper mapper;

    /**
     *  @SuppressWarnings canConvert方法不支持泛型
     */
    @SuppressWarnings("unchecked")
	public boolean canConvert(Class type) {
        return (Enhancer.isEnhanced(type) && type.getName().indexOf(DEFAULT_NAMING_MARKER) > 0)
                || type == CGLIBMapper.Marker.class;
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        Class<?> type = source.getClass();
        boolean hasFactory = Factory.class.isAssignableFrom(type);
        ExtendedHierarchicalStreamWriterHelper.startNode(writer, "type", type);
        context.convertAnother(type.getSuperclass());
        writer.endNode();
        writer.startNode("interfaces");
        Class<?>[] interfaces = type.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            if (interfaces[i] == Factory.class) {
                continue;
            }
            ExtendedHierarchicalStreamWriterHelper.startNode(writer, mapper.serializedClass(interfaces[i].getClass()), interfaces[i].getClass());
            context.convertAnother(interfaces[i]);
            writer.endNode();
        }
        writer.endNode();
        writer.startNode("hasFactory");
        writer.setValue(String.valueOf(hasFactory && type.getSuperclass() != Object.class));
        writer.endNode();
        Callback[] callbacks = hasFactory ? ((Factory)source).getCallbacks() : getCallbacks(source);
        // This is a patch of existing ThoughtWorks code
        // See XSTR-423.  Hibernate has a null as the second callback
        // when lazy loading related entities
        // and as such should not be a problem for this.
        // Added: && callbacks[1] != null
        if (callbacks.length > 1 && callbacks[1] != null) {
            throw new ConversionException("Cannot handle CGLIB enhanced proxies with multiple callbacks");
        }
        boolean isInterceptor = MethodInterceptor.class.isAssignableFrom(callbacks[0].getClass());

        ExtendedHierarchicalStreamWriterHelper.startNode(writer, mapper.serializedClass(callbacks[0].getClass()), callbacks[0].getClass());
        context.convertAnother(callbacks[0]);
        writer.endNode();
        try {
            final Field field = type.getDeclaredField("serialVersionUID");
            field.setAccessible(true);
            long serialVersionUID = field.getLong(null);
            ExtendedHierarchicalStreamWriterHelper.startNode(writer, "serialVersionUID", String.class);
            writer.setValue(String.valueOf(serialVersionUID));
            writer.endNode();
        } catch (NoSuchFieldException e) {
            // OK, ignore
        } catch (IllegalAccessException e) {
            // OK, ignore
        }
        if (isInterceptor && type.getSuperclass() != Object.class) {
            writer.startNode("instance");
            super.doMarshalConditionally(source, writer, context);
            writer.endNode();
        }
    }

    /**
     *  @SuppressWarnings canConvert方法不支持泛型
     * @param source
     * @return
     */
	@SuppressWarnings("unchecked")
	private Callback[] getCallbacks(Object source) {
        Class<?> type = source.getClass();
        List<Object> fields = (List<Object>)fieldCache.get(type.getName());
        if (fields == null) {
            fields = new ArrayList<Object>();
            fieldCache.put(type.getName(), fields);
            for (int i = 0; true; ++i) {
                try {
                    Field field = type.getDeclaredField(CALLBACK_MARKER + i);
                    field.setAccessible(true);
                    fields.add(field);
                } catch (NoSuchFieldException e) {
                    break;
                }
            }
        }
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < fields.size(); ++i) {
            try {
                Field field = (Field)fields.get(i);
                list.add(field.get(source));
            } catch (IllegalAccessException e) {
                throw new ObjectAccessException("Access to "
                        + type.getName()
                        + "."
                        + CALLBACK_MARKER
                        + i
                        + " not allowed");
            }
        }
        return list.toArray(new Callback[list.size()]);
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        final Enhancer enhancer = new Enhancer();
        reader.moveDown();
        enhancer.setSuperclass((Class<?>)context.convertAnother(null, Class.class));
        reader.moveUp();
        reader.moveDown();
        List<Object> interfaces = new ArrayList<Object>();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            interfaces.add(context.convertAnother(null, mapper.realClass(reader.getNodeName())));
            reader.moveUp();
        }
        enhancer.setInterfaces(interfaces.toArray(new Class[interfaces.size()]));
        reader.moveUp();
        reader.moveDown();
        enhancer.setUseFactory(Boolean.getBoolean(reader.getValue()));
        reader.moveUp();
        reader.moveDown();
        enhancer.setCallback((Callback)context.convertAnother(null, mapper.realClass(reader.getNodeName())));
        reader.moveUp();
        Object result = null;
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            if (reader.getNodeName().equals("serialVersionUID")) {
                enhancer.setSerialVersionUID(Long.valueOf(reader.getValue()));
            } else if (reader.getNodeName().equals("instance")) {
                result = enhancer.create();
                super.doUnmarshalConditionally(result, reader, context);
            }
            reader.moveUp();
        }
        return serializationMethodInvoker.callReadResolve(result == null ? enhancer.create() : result);
    }

    /**
     *  @SuppressWarnings hierarchyFor方法不支持泛型
     */
    @SuppressWarnings("unchecked")
	protected List hierarchyFor(Class type) {
        List<?> typeHierarchy = super.hierarchyFor(type);
        // drop the CGLIB proxy
        typeHierarchy.remove(typeHierarchy.size()-1);
        return typeHierarchy;
    }

    protected Object readResolve() {
        fieldCache = new HashMap<Object, Object>();
        return this;
    }

    private static class CGLIBFilteringReflectionProvider extends ReflectionProviderWrapper {

        public CGLIBFilteringReflectionProvider(final ReflectionProvider reflectionProvider) {
            super(reflectionProvider);
        }

        public void visitSerializableFields(final Object object, final Visitor visitor) {
            wrapped.visitSerializableFields(object, new Visitor() {
            	/**
            	 * @SuppressWarnings visit方法不支持泛型
            	 */
                @SuppressWarnings("unchecked")
				public void visit(String name, Class type, Class definedIn, Object value) {
                    if (!name.startsWith("CGLIB$")) {
                        visitor.visit(name, type, definedIn, value);
                    }
                }
            });
        }
    }
}
