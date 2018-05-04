package OLink.bpm.util.xml.converter;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.view.ejb.Column;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;

import OLink.bpm.core.dynaform.activity.ejb.Activity;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.SingleValueConverterWrapper;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.basic.DateConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ViewConverter implements Converter {

	private String[] propNames = new String[] { "filterScript",
			"sqlFilterScript", "searchForm", "relatedResourceid", "openType",
			"pagination", "pagelines", "editMode", "filterCondition",
			"showTotalRow", "orderForm", "orderField", "orderType", "refresh",
			"lastmodifytime", "activitys", "columns" };

	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		View view = (View) source;
		writer.addAttribute("id", view.getId());
		writer.addAttribute("name", view.getName());

		for (int i = 0; i < propNames.length; i++) {
			try {
				Object value = PropertyUtils.getProperty(view, propNames[i]);
				if (value != null) {
					writer.startNode(propNames[i]);
					if (propNames[i].equals("activitys")) {
						Set<Activity> activitys = new HashSet<Activity>();
						for (Iterator<Activity> iterator = view.getActivitys().iterator(); iterator
								.hasNext();) {
							Activity act = iterator.next();
							activitys.add(act);
						}
						context.convertAnother(activitys);
					} else if (propNames[i].equals("columns")) {
						Set<Column> columns = new HashSet<Column>();
						for (Iterator<Column> iterator = view.getColumns().iterator(); iterator
								.hasNext();) {
							Column column = iterator.next();
							columns.add(column);
						}
						context.convertAnother(columns);
					} else {
						context.convertAnother(value);

					}
					writer.endNode();
				}

			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}

	}
	
	/**
	 * @SuppressWarnings convertAnother方法不支持泛型
	 */
	@SuppressWarnings("unchecked")
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		View view = new View();
		view.setId(reader.getAttribute("id"));
		view.setName(reader.getAttribute("name"));

		Converter converter = new SingleValueConverterWrapper(
				new DateConverter("yyyy-MM-dd HH:mm:ss", new String[] {}));

		while (reader.hasMoreChildren()) {
			reader.moveDown();

			try {
				if (reader.getNodeName().equals("activitys")) {
					view.setActivitys((Set<Activity>) context.convertAnother(view,
							HashSet.class));
				} else if (reader.getNodeName().equals("columns")) {
					view.setColumns((Set<Column>) context.convertAnother(view,
							HashSet.class));
				} else if (reader.getNodeName().equals("searchForm")) {
					view.setSearchForm((Form) context.convertAnother(view,
							Form.class));
				} else if (reader.getNodeName().equals("lastmodifytime")) {
					BeanUtils.setProperty(view, reader.getNodeName(), context
							.convertAnother(view, Date.class, converter));
				} else {
					BeanUtils.setProperty(view, reader.getNodeName(), context
							.convertAnother(view, String.class));
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			reader.moveUp();
		}

		return view;
	}

	/**
	 * @SuppressWarnings canConvert方法不支持泛型
	 */
	@SuppressWarnings("unchecked")
	public boolean canConvert(Class type) {
		return type.equals(View.class);
	}

}
