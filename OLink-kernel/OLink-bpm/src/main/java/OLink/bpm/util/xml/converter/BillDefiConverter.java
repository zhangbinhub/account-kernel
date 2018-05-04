package OLink.bpm.util.xml.converter;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiVO;
import org.apache.commons.beanutils.BeanUtils;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.SingleValueConverterWrapper;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.basic.DateConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class BillDefiConverter implements Converter {

	private String[] propNames = new String[] { "authorno", "authorname",
			"lastmodify", "flow", "owner", "type" };

	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		BillDefiVO flow = (BillDefiVO) source;
		writer.addAttribute("id", flow.getId());
		writer.addAttribute("subject", flow.getSubject());

		for (int i = 0; i < propNames.length; i++) {
			try {
				Object value = BeanUtils.getProperty(flow, propNames[i]);
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
		BillDefiVO flow = new BillDefiVO();
		flow.setId(reader.getAttribute("id"));
		flow.setSubject(reader.getAttribute("subject"));

		Converter converter = new SingleValueConverterWrapper(
				new DateConverter("yyyy-MM-dd HH:mm:ss", new String[] {}));
		
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			try {
				if (reader.getNodeName().equals("lastmodify")) {
					BeanUtils.setProperty(flow, reader.getNodeName(), context
							.convertAnother(flow, Date.class, converter));
				} else {
					BeanUtils.setProperty(flow, reader.getNodeName(), context
							.convertAnother(flow, String.class));
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			reader.moveUp();
		}

		return flow;
	}

	/**
	 *  @SuppressWarnings canConvert方法不支持泛型
	 */
	@SuppressWarnings("unchecked")
	public boolean canConvert(Class type) {
		return type.equals(BillDefiVO.class);
	}
}
