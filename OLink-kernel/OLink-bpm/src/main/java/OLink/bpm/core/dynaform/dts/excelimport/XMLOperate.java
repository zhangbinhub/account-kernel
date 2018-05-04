//Source file: D:\\excelimport\\src\\excelimport\\XMLOperate.java

//Source file: E:\\excelimport\\src\\excelimport\\XMLOperate.java

package OLink.bpm.core.dynaform.dts.excelimport;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Stack;

import OLink.bpm.core.dynaform.dts.excelimport.utility.CommonUtil;
import org.apache.log4j.Logger;
import org.xml.sax.AttributeList;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author nicholas
 */
@SuppressWarnings("deprecation")
public class XMLOperate implements org.xml.sax.DocumentHandler {
	private static final Logger log = Logger.getLogger(XMLOperate.class);
	private Stack<Object> _stk = null;

	private ExcelMappingDiagram _fdgm = null;

	private String elementname = "";

	private String elementvalue = "";

	// private boolean isProperties = false;

	// private boolean isPropertiesElement = false;

	// private Object object = null;
	/**
	 * @roseuid 3E0428DC0120
	 */
	public XMLOperate() {
		_fdgm = new ExcelMappingDiagram();
		_stk = new Stack<Object>();
	}

	/**
	 * In this Section , Do XML Parser. Parser XML Stream to Vector of <b>Node.
	 * 
	 * @param target
	 * @param data
	 * @throws SAXException
	 * @roseuid 3E0A6E190329
	 */
	public void processingInstruction(String target, String data) throws SAXException {

	}

	/**
	 * @param ch[]
	 * @param start
	 * @param length
	 * @throws SAXException
	 * @roseuid 3E0A6E190347
	 */
	public void ignorableWhitespace(final char ch[], final int start, final int length) throws SAXException {

	}

	/**
	 * @param locator
	 * @roseuid 3E0A6E19036F
	 */
	public void setDocumentLocator(final Locator locator) {

	}

	/**
	 * @roseuid 3E0A6E190383
	 */
	public void startDocument() {

	}

	/**
	 * @roseuid 3E0A6E19038D
	 */
	public void endDocument() {
		// _stk.clear();
	}

	public void startElement(String name, AttributeList attributes) {
		try {
			if (name != null && name.trim().toLowerCase().equals(this._fdgm.getClass().getName().toLowerCase())) {
				_stk.push(_fdgm);
				return;
			} else if (ExcelMappingDiagram.class.getName().equalsIgnoreCase(name)
					|| MasterSheet.class.getName().equalsIgnoreCase(name)
					|| DetailSheet.class.getName().equalsIgnoreCase(name)
					|| Relation.class.getName().equalsIgnoreCase(name) || Column.class.getName().equalsIgnoreCase(name)) {

				Class<?> cls = Class.forName(name);

				Class<?>[] sign = new Class[1];

				sign[0] = this._fdgm.getClass();
				Constructor<?> cnstr = cls.getConstructor(sign);

				Object[] params = new Object[1];
				params[0] = this._fdgm;

				Object obj = cnstr.newInstance(params);

				_fdgm.appendElement((Element) obj);

				_stk.push(obj);
			} else {
				this.elementname = name;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param ch[]
	 * @param start
	 * @param length
	 * @roseuid 3E0A6E1903B5
	 */
	public void characters(char ch[], int start, int length) {
		String fieldval = String.valueOf(ch, start, length);
		elementvalue += fieldval;
	}

	public void setFieldValue(String fieldval) throws Exception {
		fieldval = CommonUtil.undoReplaceCharacter(fieldval);
		Object obj = null;
		if (!_stk.empty()) {
			obj = _stk.peek();

			if (obj instanceof Element) {
				Element node = (Element) obj;

				Field field = null;
				field = node.getClass().getField(this.elementname);

				Class<?> type = field.getType();

				if (field != null) {
					if (type.equals(Long.TYPE)) { // Long
						fieldval = CommonUtil.isNumberString(fieldval) ? fieldval : "0";
						field.setLong(node, Long.valueOf(fieldval).longValue());
					} else if (type.equals(Integer.TYPE)) { // Int
						fieldval = CommonUtil.isNumberString(fieldval) ? fieldval : "0";
						field.setInt(node, Integer.valueOf(fieldval).intValue());
					} else if (type.equals(Short.TYPE)) { // Short
						fieldval = CommonUtil.isNumberString(fieldval) ? fieldval : "0";
						field.setShort(node, Short.valueOf(fieldval).shortValue());
					} else if (type.equals(Double.TYPE)) { // Double
						fieldval = CommonUtil.isNumberString(fieldval) ? fieldval : "0";
						field.setDouble(node, new Double(fieldval).doubleValue());
					} else if (type.equals(Class.forName("java.lang.String"))) { // String
						fieldval = fieldval != null ? fieldval : "";
						field.set(node, fieldval);
					} else if (type.equals(Float.TYPE)) { // Float
						fieldval = CommonUtil.isNumberString(fieldval) ? fieldval : "0";
						field.setFloat(node, new Float(fieldval).floatValue());
					} else if (type.equals(Boolean.TYPE)) { // Boolean
						fieldval = CommonUtil.isBooleanString(fieldval) ? fieldval : "false";
						field.setBoolean(node, (Boolean.valueOf(fieldval)).booleanValue());
					} else if (type.equals(Class.forName("java.sql.Date"))) { // Date
						java.sql.Date dt = CommonUtil.isValidDate(fieldval) ? CommonUtil.strToDate(fieldval)
								: new java.sql.Date(System.currentTimeMillis());
						field.set(node, dt);
					}

					else if (type.equals(Class.forName("java.util.Date"))) { // java.util.Date
						java.util.Date dt = CommonUtil.isValidDate(fieldval) ? CommonUtil.strToDate(fieldval)
								: new java.sql.Date(System.currentTimeMillis());
						field.set(node, dt);

						// continue;
					}
				}
			} else if (obj instanceof ExcelMappingDiagram) {
				ExcelMappingDiagram node = (ExcelMappingDiagram) obj;
				// String fieldval = String.valueOf(ch, start, length);
				Field field = node.getClass().getField(this.elementname);
				Class<?> type = field.getType();

				if (field != null) {
					if (type.equals(Long.TYPE)) { // Long
						fieldval = CommonUtil.isNumberString(fieldval) ? fieldval : "0";
						field.setLong(node, Long.valueOf(fieldval).longValue());
					} else if (type.equals(Integer.TYPE)) { // Int
						fieldval = CommonUtil.isNumberString(fieldval) ? fieldval : "0";
						field.setInt(node, Integer.valueOf(fieldval).intValue());
					} else if (type.equals(Short.TYPE)) { // Short
						fieldval = CommonUtil.isNumberString(fieldval) ? fieldval : "0";
						field.setShort(node, Short.valueOf(fieldval).shortValue());
					} else if (type.equals(Double.TYPE)) { // Double
						fieldval = CommonUtil.isNumberString(fieldval) ? fieldval : "0";
						field.setDouble(node, new Double(fieldval).doubleValue());
					} else if (type.equals(Class.forName("java.lang.String"))) { // String
						fieldval = fieldval != null ? fieldval : "";
						field.set(node, fieldval);
					} else if (type.equals(Float.TYPE)) { // Float
						fieldval = CommonUtil.isNumberString(fieldval) ? fieldval : "0";
						field.setFloat(node, new Float(fieldval).floatValue());
					} else if (type.equals(Boolean.TYPE)) { // Boolean
						fieldval = CommonUtil.isBooleanString(fieldval) ? fieldval : "false";
						field.setBoolean(node, (Boolean.valueOf(fieldval)).booleanValue());
					} else if (type.equals(Class.forName("java.sql.Date"))) { // Date
						java.sql.Date dt = CommonUtil.isValidDate(fieldval) ? CommonUtil.strToDate(fieldval)
								: new java.sql.Date(System.currentTimeMillis());
						field.set(node, dt);
					}

					else if (type.equals(Class.forName("java.util.Date"))) { // java.util.Date
						java.util.Date dt = CommonUtil.isValidDate(fieldval) ? CommonUtil.strToDate(fieldval)
								: new java.sql.Date(System.currentTimeMillis());
						field.set(node, dt);
						// continue;
					}
				}
			}
		}
		// if (n.name!=null
		// &&
		// (n.name.trim().toLowerCase().equals("memo")||n.name.trim().toLowerCase().equals("cell"))
		// && (length>0)
		// ) {
		// Element e = (Element)n.obj;
		// e.setText(String.valueOf(ch, start, length));

		// }
		//
	}

	/**
	 * @param name
	 * @roseuid 3E0A6E1903DD
	 */
	public void endElement(String name) {
		try {
			setFieldValue(elementvalue);
		} catch (Exception e) {
			//change by lr 2013-11-25
			//e.printStackTrace();
		}
		elementvalue = "";
		// CommonUtil.printDebugInfo("EndElement");
		if (name.equalsIgnoreCase(ExcelMappingDiagram.class.getName())
				|| name.equalsIgnoreCase(MasterSheet.class.getName())
				|| name.equalsIgnoreCase(DetailSheet.class.getName())
				|| name.equalsIgnoreCase(Relation.class.getName()) || name.equalsIgnoreCase(Column.class.getName())) {
			if (_stk != null && !_stk.isEmpty()) {
				_stk.pop();
				// return;
			}
		}

	}

	/**
	 * @param e
	 * @throws SAXException
	 * @roseuid 3E0A6E1A0009
	 */
	public void fatalError(SAXParseException e) throws SAXException {
		log.info("Error: " + e);
		throw e;
	}

	/**
	 * @return excelimport.FlowDiagram
	 * @roseuid 3E0A6E1A001D
	 */
	public ExcelMappingDiagram getResult() {
		return _fdgm;
	}
}
