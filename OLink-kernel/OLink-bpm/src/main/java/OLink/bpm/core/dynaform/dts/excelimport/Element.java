//Source file: D:\\excelimport\\src\\excelimport\\Element.java

//Source file: E:\\excelimport\\src\\excelimport\\Element.java

package OLink.bpm.core.dynaform.dts.excelimport;

import OLink.bpm.core.dynaform.dts.excelimport.utility.CommonUtil;
import OLink.bpm.core.dynaform.dts.excelimport.utility.Sequence;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

/**
 * @author nicholas
 */
public abstract class Element implements Serializable {
	
	private static final long serialVersionUID = -1249506871063835891L;
	static final Color DEF_BGCOLOR = new Color(128, 255, 128);
	static final Color DEF_COLOR = Color.black;
	static final Color DEF_SELECTEDCOLOR = Color.pink;
	static final Color DEF_CURREDITCOLOR = Color.red;
	static final Color DEF_PASSEDCOLOR = Color.green;
	static final Font DEF_FONT = new Font("宋体", 0, 10);

	public int scale;
	public String id; // 唯一标识元素的编号
	public String name = ""; // 元素的名称，可以是步骤或关系
	public String description; // 备注

	Color bgcolor = DEF_BGCOLOR; // 元素的背景色
	Color color = DEF_COLOR; // 元素使用的默认颜色
	Font font = DEF_FONT; // 元素使用的默认字体

	protected Vector<Element> _subelems = new Vector<Element>();
	protected ExcelMappingDiagram _owner;

	/**
	 * @param owner
	 * @roseuid 3E0A6E170109
	 */
	public Element(ExcelMappingDiagram owner) {
		_owner = owner;
		id = Sequence.getSequence() + "";
	}

	/**
	 * @return java.lang.String
	 * @throws Exception
	 * @roseuid 3E02EA340261
	 */
	public String toXML(){
		String rslt = "";

		Class<? extends Element> cls = this.getClass();

		rslt = "<" + cls.getName() + ">\n";

		Field[] flds = cls.getFields();

		try {
			for (int i = 0; i < flds.length; i++) {
				Field field = flds[i];

				String fieldval = "";
				if (field != null) {
					Class<?> type = field.getType();
					if (type.equals(Long.TYPE)) { // Long
						fieldval = field.getLong(this) + "";
						rslt += "<" + flds[i].getName() + ">" + CommonUtil.replaceCharacter(fieldval) + "</"
								+ flds[i].getName() + ">\n";
						continue;
					} else if (type.equals(Integer.TYPE)) { // Int
						fieldval = field.getInt(this) + "";
						rslt += "<" + flds[i].getName() + ">" + CommonUtil.replaceCharacter(fieldval) + "</"
								+ flds[i].getName() + ">\n";
						continue;
					} else if (type.equals(Short.TYPE)) { // Short
						fieldval = field.getShort(this) + "";
						rslt += "<" + flds[i].getName() + ">" + CommonUtil.replaceCharacter(fieldval) + "</"
								+ flds[i].getName() + ">\n";
						continue;
					} else if (type.equals(Double.TYPE)) { // Double
						fieldval = field.getDouble(this) + "";
						rslt += "<" + flds[i].getName() + ">" + CommonUtil.replaceCharacter(fieldval) + "</"
								+ flds[i].getName() + ">\n";
						continue;
					} else if (type.equals(Class.forName("java.lang.String"))) { // String
						fieldval = (String) field.get(this);
						rslt += "<" + flds[i].getName() + ">" + CommonUtil.replaceCharacter(fieldval) + "</"
								+ flds[i].getName() + ">\n";
						continue;
					}

					else if (type.equals(Float.TYPE)) { // Float
						fieldval = field.getFloat(this) + "";
						rslt += "<" + flds[i].getName() + ">" + CommonUtil.replaceCharacter(fieldval) + "</"
								+ flds[i].getName() + ">\n";
						continue;
					}

					else if (type.equals(Boolean.TYPE)) { // Boolean
						fieldval = field.getBoolean(this) + "";
						rslt += "<" + flds[i].getName() + ">" + CommonUtil.replaceCharacter(fieldval) + "</"
								+ flds[i].getName() + ">\n";
						continue;
					}

					else if (type.equals(Class.forName("java.sql.Date"))) { // Date
						fieldval = field.get(this) + "";
						rslt += "<" + flds[i].getName() + ">" + CommonUtil.replaceCharacter(fieldval) + "</"
								+ flds[i].getName() + ">\n";
						continue;
					}

					else if (type.equals(Class.forName("java.util.Date"))) { // java.util.Date
						fieldval = field.get(this) + "";
						rslt += "<" + flds[i].getName() + ">" + CommonUtil.replaceCharacter(fieldval) + "</"
								+ flds[i].getName() + ">\n";
						continue;
					} else if (type.equals(Class.forName("java.util.Properties"))) {
						Properties props = (Properties) field.get(this);
						if (props != null && props.size() > 1) {
							rslt += "<properties>\n";
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
								if (!key.toString().equalsIgnoreCase("properties")) {
									String prop = "";
									prop += "<" + key.toString() + ">";
									prop += CommonUtil.replaceCharacter(value.toString());
									prop += "</" + key.toString() + ">\n";
									proplist = prop + proplist;
								}
								// e.nextElement();
								// props.keys()
							}
							rslt += proplist;
							rslt += "</properties>\n";
							// Enumeration e = props.elements();
							// props.
							// e.
						} else if (props != null && props.size() == 1) {
							rslt += "<properties>null</properties>";
						} else {
							rslt += "<properties>";
							rslt += "null";
							rslt += "</properties>\n";
						}
					}
					// rslt += flds[i].getName() + "='" + fieldval + "' ";
				}
			}
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e2){
			e2.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		// 递归调用SUBELEMENT的TOXML方法

		// this._subelems
		/*
		 * for (java.util.Enumeration em = this._subelems.elements();
		 * em.hasMoreElements(); ) { Element subelm = (Element)
		 * em.nextElement(); rslt += subelm.toXML(); }
		 */
		rslt += "</" + cls.getName() + ">\n";

		return rslt;
	}

	/**
	 * @param g
	 * @roseuid 3E03EDD100DF
	 */
	public abstract void paint(Graphics g);

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

	/**
	 * @param x
	 * @param y
	 * @return boolean
	 * @roseuid 3E0A6E170286
	 */
	public abstract boolean isSelected(int x, int y);

	/**
	 * @param x
	 * @param y
	 * @roseuid 3E0A6E1702A4
	 */
	public abstract void moveTo(int x, int y);

	/**
	 * @return java.awt.Rectangle
	 * @roseuid 3E0A6E1702C2
	 */
	public abstract Rectangle getRepaintRect();

	public abstract boolean removeSubElement(String id);

	// public abstract void remove();

	public abstract void removeAllSubElement();

	/**
	 * @param e
	 * @roseuid 3E0A6E5003A0
	 */
	public abstract void onMouseClicked(MouseEvent e);

	/**
	 * @param e
	 * @roseuid 3E0A6E73033C
	 */
	public abstract void onMouseDragged(MouseEvent e);

	/**
	 * @param e
	 * @roseuid 3E0A6ED700BF
	 */
	public abstract void onMouseMoved(MouseEvent e);

	/**
	 * @param e
	 * @roseuid 3E0A6EDD0244
	 */
	public abstract void onMousePressed(MouseEvent e);

	/**
	 * @param e
	 * @roseuid 3E0A6EE40140
	 */
	public abstract void onMouseReleased(MouseEvent e);
}
/**
 * Element.setName(String){ name = aName; } Element.getColor(){ return color; }
 * Element.set_owner(FlowDiagram){ _owner = a_owner; } Element.getName(){ return
 * name; } Element.getScale(){ return scale; } Element.setId(String){ id = aId; }
 * Element.getId(){ return id; } Element.setFont(Font){ font = aFont; }
 * Element.getFont(){ return font; } Element.getBgcolor(){ return bgcolor; }
 * Element.setNote(String){ note = aNote; } Element.get_owner(){ return _owner; }
 * Element.setBgcolor(Color){ bgcolor = aBgcolor; } Element.setColor(Color){
 * color = aColor; } Element.setScale(int){ scale = aScale; } Element.getNote(){
 * return note; }
 */
