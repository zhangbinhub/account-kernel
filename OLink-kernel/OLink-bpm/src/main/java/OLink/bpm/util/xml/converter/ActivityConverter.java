package OLink.bpm.util.xml.converter;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.dynaform.activity.ejb.Activity;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ActivityConverter implements Converter {

	private String[] propNames = new String[] { "id", "name", "type",
			"beforeActionScript", "afterActionScript", "hiddenScript",
			"iconurl", "approveLimit", "stateToShow", "orderno",
			"onActionForm", "onActionView", "onActionFlow", "parentView",
			"parentForm" };

	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		Activity activity = (Activity) source;

		for (int i = 0; i < propNames.length; i++) {
			try {
				Object value = PropertyUtils
						.getProperty(activity, propNames[i]);
				writer.startNode(propNames[i]);
				if (value != null) {
					if (propNames[i].endsWith("View")
							|| propNames[i].endsWith("Form")
							|| propNames[i].endsWith("Flow")) {
						context.convertAnother(((ValueObject) value).getId());
					} else {
						context.convertAnother(value);
					}
				}
				writer.endNode();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
	}

	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @SuppressWarnings canConvert方法不支持泛型
	 */
	@SuppressWarnings("unchecked")
	public boolean canConvert(Class type) {
		return type.equals(Activity.class);
	}

}
