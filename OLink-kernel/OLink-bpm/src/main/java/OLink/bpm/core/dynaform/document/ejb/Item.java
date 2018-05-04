// Source file:
// C:\\Java\\workspace\\SmartWeb3\\src\\com\\cyberway\\dynaform\\document\\ejb\\Item.java

package OLink.bpm.core.dynaform.document.ejb;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import OLink.bpm.util.StringList;
import OLink.bpm.util.DateUtil;
import OLink.bpm.util.StringUtil;
import eWAP.core.Tools;


/**
 * @author nicholas
 */
public class Item implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 262646455669007893L;

	/**
	 * 文本类型
	 */
	public static final String VALUE_TYPE_VARCHAR = "VALUE_TYPE_VARCHAR";

	/**
	 * 数字类型
	 */

	public static final String VALUE_TYPE_NUMBER = "VALUE_TYPE_NUMBER";

	/**
	 * 日期类型
	 */

	public static final String VALUE_TYPE_DATE = "VALUE_TYPE_DATE";

	/**
	 * BLOB类型
	 */

	public static final String VALUE_TYPE_BLOB = "VALUE_TYPE_BLOB";

	/**
	 * 大文本类型
	 */

	public static final String VALUE_TYPE_TEXT = "VALUE_TYPE_TEXT";

	/**
	 * 文本列表
	 */
	public static final String VALUE_TYPE_STRINGLIST = "VALUE_TYPE_STRINGLIST";

	/**
	 * 包含类型
	 */

	public static final String VALUE_TYPE_INCLUDE = "VALUE_TYPE_INCLUDE";

	/**
	 * 数字格式
	 */
	public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("00000000000000000000.000000");

	/**
	 * 日期格式
	 */
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	public static final SimpleDateFormat DATE_FORMAT_SHORT = new SimpleDateFormat("yyyy-MM-dd");

	private Document document;

	/**
	 * @uml.property name="id"
	 */
	private String id;

	/**
	 * @uml.property name="lastModified"
	 */
	private Date lastModified;

	/**
	 * 文档项名称
	 * 
	 * @uml.property name="name"
	 */
	private String name;

	/**
	 * @uml.property name="type"
	 */
	private String type;

	/**
	 * @uml.property name="isbrief"
	 */
	private boolean isbrief;

	/**
	 * @uml.property name="orderno"
	 */
	private int orderno;

	/**
	 * @uml.property name="formname"
	 */
	private String formname;

	/**
	 * @uml.property name="issubformvalue"
	 */
	private boolean issubformvalue;

	/**
	 * @uml.property name="varcharvalue"
	 */
	private String varcharvalue;

	/**
	 * @uml.property name="numbervalue"
	 */
	private Double numbervalue;

	/**
	 * @uml.property name="textvalue"
	 */
	private String textvalue;

	/**
	 * 日期值
	 * 
	 * @uml.property name="datevalue"
	 */
	private Date datevalue;

	/**
	 * 是否已改变
	 * 
	 * @uml.property name="isChanged"
	 */
	private boolean isChanged;

	/**
	 * 是否已改变
	 * 
	 * @return true|false
	 * @uml.property name="isChanged"
	 */
	public boolean isChanged() {
		return isChanged;
	}

	/**
	 * @param isChanged
	 *            the isChanged to set
	 * @uml.property name="isChanged"
	 */
	public void setChanged(boolean isChanged) {
		this.isChanged = isChanged;
	}

	/**
	 * 1)VALUE_TYPE_INCLUDE(include), 2)VALUE_TYPE_NUMBER,
	 * <p>
	 * 3)VALUE_TYPE_VARCHAR, 4)VALUE_TYPE_DATE,
	 * <p>
	 * 5)VALUE_TYPE_TEXT,6)VALUE_TYPE_BLOB(BLOB)
	 * <P>
	 * 7)VALUE_TYPE_STRINGLIST.
	 * 
	 * @see Item#VALUE_TYPE_INCLUDE
	 * @see Item#VALUE_TYPE_NUMBER
	 * @see Item#VALUE_TYPE_VARCHAR
	 * @see Item#VALUE_TYPE_TEXT
	 * @see Item#VALUE_TYPE_DATE
	 * @return item
	 * @uml.property name="type"
	 */
	public String getType() {
		return type;
	}

	/**
	 * 1)VALUE_TYPE_INCLUDE(include), 2)VALUE_TYPE_NUMBER,
	 * <p>
	 * 3)VALUE_TYPE_VARCHAR, 4)VALUE_TYPE_DATE,
	 * <p>
	 * 5)VALUE_TYPE_TEXT,6)VALUE_TYPE_BLOB(BLOB)
	 * <P>
	 * 7)VALUE_TYPE_STRINGLIST.
	 * 
	 * @see Item#VALUE_TYPE_INCLUDE
	 * @see Item#VALUE_TYPE_NUMBER
	 * @see Item#VALUE_TYPE_VARCHAR
	 * @see Item#VALUE_TYPE_TEXT
	 * @see Item#VALUE_TYPE_DATE
	 * @param type
	 *            item
	 * @uml.property name="type"
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * 
	 * @return Returns the numbervalue.
	 * @uml.property name="numbervalue"
	 */
	public Double getNumbervalue() {
		if (getType().equals(VALUE_TYPE_NUMBER)) {
			return numbervalue;
		} else {
			String tmp = null;
			if (getType().equals(VALUE_TYPE_VARCHAR)) {
				tmp = varcharvalue;
			} else if (getType().equals(VALUE_TYPE_TEXT)) {
				tmp = textvalue;
			}
			if (tmp != null) {
				try {
					return new Double(tmp);
				} catch (Exception e) {
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @param numbervalue
	 * @uml.property name="numbervalue"
	 */
	public void setNumbervalue(Double numbervalue) {
		this.numbervalue = numbervalue;
	}

	/**
	 * 
	 * @return
	 * @uml.property name="varcharvalue"
	 */
	public String getVarcharvalue() {
		if (getType().equals(VALUE_TYPE_VARCHAR)) {
			if (varcharvalue != null && varcharvalue.length() > 255) {
				return varcharvalue.substring(0, 255);
			} else {
				return varcharvalue;
			}

		} else if (getType().equals(VALUE_TYPE_NUMBER)) {
			Double num = getNumbervalue();
			return num != null ? DECIMAL_FORMAT.format(num) : "";
		} else if (getType().equals(VALUE_TYPE_DATE)) {
			Date dt = getDatevalue();
			if (dt != null) {
				return DATE_FORMAT.format(dt);
			}
		} else if (getType().equals(VALUE_TYPE_TEXT)) {
			return getTextvalue();
		}
		return null;
	}

	/**
	 * 
	 * @param varcharvalue
	 * @uml.property name="varcharvalue"
	 */
	public void setVarcharvalue(String varcharvalue) {
		this.varcharvalue = varcharvalue;
	}

	/**
	 * @return
	 * @uml.property name="textvalue"
	 */
	public String getTextvalue() {
		if (getType().equals(VALUE_TYPE_TEXT)) {
			return textvalue;
		} else {
			return getVarcharvalue();
		}
	}

	/**
	 * @param varcharvalue
	 *            The varcharvalue to set.
	 * @uml.property name="textvalue"
	 */
	public void setTextvalue(String textvalue) {
		this.textvalue = textvalue;
	}

	public Item() {
		type = VALUE_TYPE_VARCHAR;
		try {
			id = Tools.getSequence();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return
	 */
	public Object getValue() {
		if (getType().equals(VALUE_TYPE_VARCHAR)) {
			return getVarcharvalue();
		} else if (getType().equals(VALUE_TYPE_NUMBER)) {
			return getNumbervalue();
		} else if (getType().equals(VALUE_TYPE_DATE)) {
			return getDatevalue();
		} else if (getType().equals(VALUE_TYPE_TEXT)) {
			return getTextvalue();
		}
		return null;
	}

	/**
	 * 
	 * @return Document
	 * @hibernate.many-to-one column="DOC_ID"
	 *                        class="Document"
	 * @uml.property name="document"
	 */
	public Document getDocument() {
		return document;
	}

	/**
	 * 
	 * @param document
	 *            Document
	 * @uml.property name="document"
	 */
	public void setDocument(Document document) {
		this.document = document;
	}

	/**
	 * 
	 * @uml.property name="id"
	 */
	public String getId() {
		return id;
	}

	/**
	 * 
	 * @param id
	 * 
	 * @uml.property name="id"
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 
	 * @return
	 * @uml.property name="lastModified"
	 */
	public Date getLastModified() {
		return lastModified;
	}

	/**
	 * 最后修改日期
	 * 
	 * @param lastModified
	 * @uml.property name="lastModified"
	 */
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	/**
	 * 
	 * @return item名称
	 * @uml.property name="name"
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param name
	 * @uml.property name="name"
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return
	 * @uml.property name="datevalue"
	 */
	public Date getDatevalue() {
		if (getType().equals(VALUE_TYPE_DATE)) {
			return datevalue;
		} else {
			String tmp = null;
			if (getType().equals(VALUE_TYPE_VARCHAR)) {
				tmp = varcharvalue;
			} else if (getType().equals(VALUE_TYPE_TEXT)) {
				tmp = textvalue;
			}
			if (tmp != null) {
				try {
					return new Date(DATE_FORMAT.parse(tmp).getTime());
				} catch (Exception e) {
					try {
						return new Date(DATE_FORMAT_SHORT.parse(tmp).getTime());
					} catch (Exception e2) {
					}
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @param datevalue
	 *            The datevalue to set.
	 * @uml.property name="datevalue"
	 */
	public void setDatevalue(Date datevalue) {
		this.datevalue = datevalue;
	}

	/**
	 * 输出文本
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("<name>");
		sb.append(name);
		sb.append("<value>");
		sb.append(getValue() + "");
		return sb.toString();
	}

	/**
	 * 
	 * @return the isbrief
	 * @uml.property name="isbrief"
	 */
	public boolean getIsbrief() {
		return isbrief;
	}

	/**
	 * 
	 * @param isbrief
	 *            the isbrief to set
	 * @uml.property name="isbrief"
	 */
	public void setIsbrief(boolean isbrief) {
		this.isbrief = isbrief;
	}

	/**
	 * 
	 * @return
	 * @uml.property name="issubformvalue"
	 */
	public boolean getIssubformvalue() {
		return issubformvalue;
	}

	/**
	 * 
	 * @param issubformvalue
	 *            The issubformvalue to set.
	 * @uml.property name="issubformvalue"
	 */
	public void setIssubformvalue(boolean issubformvalue) {
		this.issubformvalue = issubformvalue;
	}

	/**
	 * 序列号
	 * 
	 * @return
	 * @uml.property name="orderno"
	 */
	public int getOrderno() {
		return orderno;
	}

	/**
	 * 
	 * @param orderno
	 *            The orderno to set.
	 * @uml.property name="orderno"
	 */
	public void setOrderno(int orderno) {
		this.orderno = orderno;
	}

	/**
	 * 
	 * @param value
	 * 
	 */
	public void setValue(Object value) {// 
		if (value == null) {
			// Nothing to do
		} else if (this.getType().equals(Item.VALUE_TYPE_VARCHAR)) {
			this.setVarcharvalue(value + "");
		} else if (this.getType().equals(Item.VALUE_TYPE_TEXT)) {
			this.setTextvalue(value + "");
		} else if (value != null && this.getType().equals(Item.VALUE_TYPE_DATE)) {
			if (value instanceof Date) {
				this.setDatevalue((Date) value);
			} else if (value instanceof String) {
				try {
					this.setDatevalue(DATE_FORMAT.parse((String) value));
				} catch (Exception e) {
					try {
						this.setDatevalue(DATE_FORMAT_SHORT.parse((String) value));
					} catch (Exception e1) {
						this.setDatevalue(null);
					}
				}
			}
		} else if (value != null && this.getType().equals(Item.VALUE_TYPE_NUMBER)) {
			Double d = new Double(0.0);
			if (value instanceof Number) {
				d = new Double(((Number) value).doubleValue());
			} else if (value instanceof String) {
				if (StringUtil.isNumber((String) value)) {
					d = new Double((String) value);
				}
			}
			this.setNumbervalue(d);
		}
	}

	/**
	 * 表单名称
	 * 
	 * @hibernate.property column="FORMNAME"
	 * @return 表单名称
	 * @uml.property name="formname"
	 */
	public String getFormname() {
		return formname;
	}

	/**
	 * 表单名称
	 * 
	 * @param formname
	 *            The formname to set.
	 * @uml.property name="formname"
	 */
	public void setFormname(String formname) {
		this.formname = formname;
	}

	/**
	 * 以数组形式获取值
	 * 
	 * @return
	 */
	public Object[] getValueArray() {
		// if (true) {
		// return new String[]{"1","2","3"};
		// }
		if (this.getIssubformvalue() && this.getVarcharvalue() != null) {
			if (this.getVarcharvalue().length() == 0) {
				return new Object[0];
			}

			StringList sl = new StringList(this.getVarcharvalue());
			Collection<String> vc = sl.toCollection();

			Object[] rtn = new Object[vc.size()];
			Iterator<String> iter = vc.iterator();
			int i = 0;
			while (iter.hasNext()) {
				String v = iter.next();
				if (type.equals(VALUE_TYPE_VARCHAR)) {
					rtn[i] = v;
				} else if (type.equals(VALUE_TYPE_NUMBER)) {
					try {
						rtn[i] = new Double(v);
					} catch (Exception e) {
						rtn[i] = new Double(0.0);
					}
				} else if (type.equals(VALUE_TYPE_DATE)) {
					try {
						rtn[i] = DateUtil.parseDate(v);
					} catch (Exception e) {
					}
				} else if (type.equals(VALUE_TYPE_TEXT)) {
					rtn[i] = v;
				}
				i++;
			}

			return rtn;

		} else {
			return null;
		}

	}
}
