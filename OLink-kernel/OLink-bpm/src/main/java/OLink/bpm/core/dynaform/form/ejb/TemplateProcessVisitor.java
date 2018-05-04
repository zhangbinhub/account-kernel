/*
 * Created on 2005-1-22
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package OLink.bpm.core.dynaform.form.ejb;

/**
 * @author ZhouTY
 * 
 * Preferences - Java - Code Style - Code Templates
 */

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import OLink.bpm.core.dynaform.component.ejb.Component;
import OLink.bpm.core.dynaform.component.ejb.ComponentProcess;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import org.apache.commons.beanutils.PropertyUtils;
import org.htmlparser.Attribute;
import org.htmlparser.Tag;
import org.htmlparser.Text;
import org.htmlparser.visitors.NodeVisitor;

import OLink.bpm.util.DateUtil;
import OLink.bpm.util.ObjectUtil;
import eWAP.core.Tools;
import OLink.bpm.util.text.TemplateContext;

/**
 * @author nicholas
 */
public class TemplateProcessVisitor extends NodeVisitor {
	Form _form;

	public TemplateProcessVisitor() {

	}

	public TemplateProcessVisitor(Form form) {
		_form = form;

	}

	public void visitStringNode(Text text) {
		String str = text.getPage().getText(text.getStartPosition(),
				text.getEndPosition());
		if (!(str == null || str.equals("") || str.equals("\n") || str
				.equals("\n\r"))) {
			Textpart field = new Textpart();
			field.setText(str);
			_form.addTextpart(field);

		}
	}

	/**
	 * @SuppressWarnings getAttributesEx 方法不支持泛型
	 */
	@SuppressWarnings("unchecked")
	public void visitTag(Tag tag) {
		try {
			String className = tag.getAttribute("className");
			if (className == null) {
				String str = tag.getPage().getText(tag.getStartPosition(),
						tag.getEndPosition());
				StringBuffer text = new StringBuffer();

				Collection<?> attributes = tag.getAttributesEx();
				text.append("<");
				if (attributes != null && !attributes.isEmpty()) {
					Iterator<?> iterator = attributes.iterator();
					Attribute attr = (Attribute) iterator.next();
					text.append(attr.getName()); // 第一个属性为TagName

					while (iterator.hasNext()) {
						attr = (Attribute) iterator.next();
						if (!StringUtil.isBlank(attr.getName())
								&& !StringUtil.isBlank(attr.getValue())) {
							text.append(" ").append(attr.getName());
							text.append("='").append(attr.getValue()).append(
									"'");
						}
					}
				}
				text.append(">");

				if (str != null && str.length() > 0) {
					Textpart field = new Textpart();
					field.setText(text.toString());
					_form.addTextpart(field);
				}
			} else if (className
					.equalsIgnoreCase("OLink.bpm.core.dynaform.form.ejb.form")
					|| className
							.equalsIgnoreCase("OLink.bpm.core.dynaform.form.ejb.replayform")) {
				if (_form == null) {
					Class<?> cls = Class.forName(className);
					_form = (Form) cls.newInstance();
				}
				setFields(_form, tag.getAttributesEx());
			} else if (className
					.equalsIgnoreCase("OLink.bpm.core.dynaform.form.ejb.ComponentTag")) {
				String componentid = tag.getAttribute("componentid");
				String aliasesExpr = tag.getAttribute("aliases");
				String[] aliases = aliasesExpr.split(";");
				Map<String, String> aliaslist = new HashMap<String, String>();
				for (int i = 0; i < aliases.length; i++) {
					String oldName = aliases[i].substring(0, aliases[i]
							.indexOf(":"));
					String alias = aliases[i]
							.substring(aliases[i].indexOf(":") + 1);
					aliaslist.put(oldName, alias);
				}

				ComponentProcess cp = (ComponentProcess) ProcessFactory
						.createProcess(ComponentProcess.class);
				Component component = (Component) cp.doView(componentid);

				if (component != null) {
					TemplateContext context = TemplateContext.parse(component
							.getTemplatecontext());
					for (Iterator<?> iter = aliaslist.entrySet().iterator(); iter
							.hasNext();) {
						Entry<?, ?> entry = (Entry<?, ?>) iter.next();
						String value = (String) entry.getValue();
						context.putParams((String)entry.getKey(), value);
					}

					TemplateParser.parseTemplate(_form, context.toText());
					_form.addComponent(component);
				}
			} else if (className != null) {
				Class<?> cls = Class.forName(className);
				Object obj = cls.newInstance();

				if (obj instanceof FormField) {
					FormField f = (FormField) obj;
					f.setId(Tools.getSequence());
					setFields(f, tag.getAttributesEx());

					_form.addField(f);
				}
			}

		} catch (Exception e) {
			try {//增加 by  XGY
				throw e;
			} catch (Exception e1) {
			}
		}
	}

	public void visitEndTag(Tag tag) {
		if (!tag.getTagName().trim().equalsIgnoreCase("FORM")) {
			String text = tag.getPage().getText(tag.getStartPosition(),
					tag.getEndPosition());
			if (text !=null && ("</SELECT>".equals(text.toUpperCase())
					|| "</TEXTAREA>".equals(text.toUpperCase()))) {
				return;
			}

			if (text != null && text.length() > 0) {
				Textpart field = new Textpart();
				field.setText(text);
				_form.addTextpart(field);
			}
		}
	}

	public void setFields(Object obj, List<Attribute> attributes) throws Exception {
		// Set object Field value.
		for (int i = 0; i < attributes.size(); i++) {
			Attribute attr = attributes.get(i);
			String fieldname = attr.getName();
			String fieldval = attr.getValue();

			Collection<Field> fields = ObjectUtil.getClassAllFields(obj.getClass());
			for (Iterator<Field> iterator = fields.iterator(); iterator.hasNext();) {
				Field field = iterator.next();

				if (field.getName().equalsIgnoreCase(fieldname)) {
					fieldname = field.getName();
					break;
				}
			}

			Class<?> type = null;
			try {
				type = PropertyUtils.getPropertyType(obj, fieldname);
			} catch (Exception e) {
				type = null;
			}

			if (type == null) {
				if (obj instanceof FormField) {
					FormField field = (FormField) obj;
					field.addOtherProps(fieldname, fieldval);
				}
				continue;
			}

			if (type != null) {
				if (type.equals(Long.TYPE)) { // Long
					fieldval = StringUtil.isNumber(fieldval) ? fieldval : "0";
					PropertyUtils.setSimpleProperty(obj, fieldname, Long
							.valueOf(fieldval));
					continue;
				} else if (type.equals(Integer.TYPE)) { // Int
					fieldval = StringUtil.isNumber(fieldval) ? fieldval : "0";
					PropertyUtils.setSimpleProperty(obj, fieldname,
							Integer.valueOf(fieldval));
					continue;
				} else if (type.equals(Short.TYPE)) { // Short
					fieldval = StringUtil.isNumber(fieldval) ? fieldval : "0";
					PropertyUtils.setSimpleProperty(obj, fieldname, Short
							.valueOf(fieldval));
					continue;
				} else if (type.equals(Double.TYPE)) { // Double
					fieldval = StringUtil.isNumber(fieldval) ? fieldval : "0";
					PropertyUtils.setSimpleProperty(obj, fieldname, new Double(
							fieldval));
					continue;
				} else if (type.equals(Class.forName("java.lang.String"))) { // String
					fieldval = fieldval != null ? fieldval : "";
					PropertyUtils.setProperty(obj, fieldname, fieldval.trim());
					continue;
				}

				else if (type.equals(Float.TYPE)) { // Float
					fieldval = StringUtil.isNumber(fieldval) ? fieldval : "0";
					PropertyUtils.setSimpleProperty(obj, fieldname, new Float(
							fieldval));
					continue;
				}

				else if (type.equals(Boolean.TYPE)) { // Boolean
					fieldval = StringUtil.isBoolean(fieldval) ? fieldval
							: "false";
					PropertyUtils.setSimpleProperty(obj, fieldname, Boolean
							.valueOf(fieldval));
					continue;
				}

				else if (type.equals(Class.forName("java.sql.Date"))) { // Date
					java.util.Date dt = StringUtil.isDate(fieldval) ? DateUtil
							.parseDateTime(fieldval) : new java.util.Date(
							System.currentTimeMillis());
					PropertyUtils.setSimpleProperty(obj, fieldname, dt);
					continue;
				}

				else if (type.equals(Class.forName("java.util.Date"))) { // java.util.Date
					java.util.Date dt = StringUtil.isDate(fieldval) ? DateUtil
							.parseDate(fieldval) : new java.sql.Date(System
							.currentTimeMillis());
					PropertyUtils.setSimpleProperty(obj, fieldname, dt);
					continue;
				}
			}

		}
	}

	public Form getResult() {
		return _form;
	}
}
