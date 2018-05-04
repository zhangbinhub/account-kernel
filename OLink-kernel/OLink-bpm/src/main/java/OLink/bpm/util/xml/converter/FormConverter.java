package OLink.bpm.util.xml.converter;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import OLink.bpm.core.dynaform.form.ejb.Form;
import org.apache.commons.beanutils.BeanUtils;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.SingleValueConverterWrapper;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.basic.DateConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class FormConverter implements Converter {

	private String[] propNames = new String[] { "type", "version",
			"beforopenscript", "discription", "lastmodifytime",
			"templatecontext", "activityXML" };

	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		Form form = (Form) source;
		writer.addAttribute("id", form.getId());
		writer.addAttribute("name", form.getName());

		for (int i = 0; i < propNames.length; i++) {
			try {
				Object value = BeanUtils.getProperty(form, propNames[i]);
				writer.startNode(propNames[i]);
				if (value != null) {
					context.convertAnother(value);
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
		Form form = new Form();
		form.setId(reader.getAttribute("id"));
		form.setName(reader.getAttribute("name"));

		Converter converter = new SingleValueConverterWrapper(
				new DateConverter("yyyy-MM-dd HH:mm:ss", new String[] {}));

		while (reader.hasMoreChildren()) {
			reader.moveDown();

			try {
				if (reader.getNodeName().equals("lastmodifytime")) {
					BeanUtils.setProperty(form, reader.getNodeName(), context
							.convertAnother(form, Date.class, converter));
				} else {
					BeanUtils.setProperty(form, reader.getNodeName(), context
							.convertAnother(form, String.class));
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			reader.moveUp();
		}

		return form;
	}

	/**
	 * @SuppressWarnings canConvert方法不支持泛型
	 */
	@SuppressWarnings("unchecked")
	public boolean canConvert(Class type) {
		return type.equals(Form.class);
	}

}
