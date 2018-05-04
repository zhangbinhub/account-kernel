//Source file: D:\\BILLFLOW\\src\\billflow\\Element.java

//Source file: E:\\billflow\\src\\billflow\\Element.java

package OLink.bpm.core.workflow.element;

//import java.util.Hashtable;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import OLink.bpm.core.workflow.utility.CommonUtil;

public abstract class Element implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1141506192272655372L;

	public String id; // 唯一标识元素的编号

	/**
	 * @param owner
	 * @roseuid 3E0A6E170109
	 */
	public Element(FlowDiagram owner) {
		_owner = owner;
	}

	public Vector<Element> _subelems = new Vector<Element>();

	protected FlowDiagram _owner;

	/**
	 * @return java.lang.String
	 * @throws Exception
	 * @roseuid 3E02EA340261
	 */
	public String toXML() throws Exception {
		StringBuffer rslt = new StringBuffer();

		try {
			Class<? extends Element> cls = this.getClass();

			rslt.append("<").append(cls.getName()).append(">\n");

			Field[] flds = cls.getFields();

			for (int i = 0; i < flds.length; i++) {
				Field field = flds[i];

				String fieldval = "";

				if (field != null) {
					Class<?> type = field.getType();
					if (type.equals(Long.TYPE)) { // Long
						fieldval = field.getLong(this) + "";
						rslt.append("<").append(flds[i].getName()).append(">")
								.append(CommonUtil.replaceCharacter(fieldval))
								.append("</").append(flds[i].getName()).append(
										">\n");
						continue;
					} else if (type.equals(Integer.TYPE)) { // Int
						fieldval = field.getInt(this) + "";
						rslt.append("<").append(flds[i].getName()).append(">")
								.append(CommonUtil.replaceCharacter(fieldval))
								.append("</").append(flds[i].getName()).append(
										">\n");
						continue;
					} else if (type.equals(Short.TYPE)) { // Short
						fieldval = field.getShort(this) + "";
						rslt.append("<").append(flds[i].getName()).append(">")
								.append(CommonUtil.replaceCharacter(fieldval))
								.append("</").append(flds[i].getName()).append(
										">\n");
						continue;
					} else if (type.equals(Double.TYPE)) { // Double
						fieldval = field.getDouble(this) + "";
						rslt.append("<").append(flds[i].getName()).append(">")
								.append(CommonUtil.replaceCharacter(fieldval))
								.append("</").append(flds[i].getName()).append(
										">\n");
						continue;
					} else if (type.equals(Class.forName("java.lang.String"))) { // String
						fieldval = (String) field.get(this);
						rslt.append("<").append(flds[i].getName()).append(">")
								.append(CommonUtil.replaceCharacter(fieldval))
								.append("</").append(flds[i].getName()).append(
										">\n");
						continue;
					}

					else if (type.equals(Float.TYPE)) { // Float
						fieldval = field.getFloat(this) + "";
						rslt.append("<").append(flds[i].getName()).append(">")
								.append(CommonUtil.replaceCharacter(fieldval))
								.append("</").append(flds[i].getName()).append(
										">\n");
						continue;
					}

					else if (type.equals(Boolean.TYPE)) { // Boolean
						fieldval = field.getBoolean(this) + "";
						rslt.append("<").append(flds[i].getName()).append(">")
								.append(CommonUtil.replaceCharacter(fieldval))
								.append("</").append(flds[i].getName()).append(
										">\n");
						continue;
					}

					else if (type.equals(Class.forName("java.sql.Date"))) { // Date
						fieldval = field.get(this) + "";
						rslt.append("<").append(flds[i].getName()).append(">")
								.append(CommonUtil.replaceCharacter(fieldval))
								.append("</").append(flds[i].getName()).append(
										">\n");
						continue;
					}

					else if (type.equals(Class.forName("java.util.Date"))) { // java.util.Date
						fieldval = field.get(this) + "";
						rslt.append("<").append(flds[i].getName()).append(">")
								.append(CommonUtil.replaceCharacter(fieldval))
								.append("</").append(flds[i].getName()).append(
										">\n");
						continue;
					} else if (type.equals(Class
							.forName("java.util.Properties"))) {
						Properties props = (Properties) field.get(this);
						if (props != null && props.size() > 1) {
							rslt.append("<properties>\n");
							Enumeration<?> e = props.keys();
							Object key = null;
							Object value = null;
							// props.setProperty("name3","kkkk");
							// e.nextElement();
							String proplist = "";
							for (; e.hasMoreElements();) {
								// Properties prop;
								key = e.nextElement();
								value = props.get(key);
								if (!key.toString().equalsIgnoreCase(
										"properties")) {
									String prop = "";
									prop += "<" + key.toString() + ">";
									prop += CommonUtil.replaceCharacter(value
											.toString());
									prop += "</" + key.toString() + ">\n";
									proplist = prop + proplist;
								}
								// e.nextElement();
								// props.keys()
							}
							rslt.append(proplist);
							rslt.append("</properties>\n");
							// Enumeration e = props.elements();
							// props.
							// e.
						} else if (props != null && props.size() == 1) {
							rslt.append("<properties>null</properties>");
						} else {
							rslt.append("<properties>");
							rslt.append("null");
							rslt.append("</properties>\n");
						}
					}
					// rslt += flds[i].getName() + "='" + fieldval + "' ";
				}
			}
			// rslt += ">\n";

			// 递归调用SUBELEMENT的TOXML方法

			// this._subelems
			for (Enumeration<Element> em = this._subelems.elements(); em
					.hasMoreElements();) {
				Element subelm = em.nextElement();
				rslt.append(subelm.toXML());
			}

			rslt.append("</").append(cls.getName()).append(">\n");
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return rslt.toString();
	}

	/**
	 * @param e
	 * @roseuid 3E0A6E170254
	 */
	public void appendElement(Element e) {
		_subelems.addElement(e);
	}

	public Vector<Element> getSubelems() {
		return this._subelems;
	}

	public abstract boolean removeSubElement(String id);

	public abstract void removeAllSubElement();

}
