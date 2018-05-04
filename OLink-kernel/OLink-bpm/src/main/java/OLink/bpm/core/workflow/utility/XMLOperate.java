//Source file: D:\\BILLFLOW\\src\\billflow\\XMLOperate.java

//Source file: E:\\billflow\\src\\billflow\\XMLOperate.java

package OLink.bpm.core.workflow.utility;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Stack;

import OLink.bpm.core.workflow.element.Element;
import OLink.bpm.core.workflow.element.FlowDiagram;
import org.xml.sax.AttributeList;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

@SuppressWarnings("deprecation")
public class XMLOperate implements org.xml.sax.DocumentHandler {
	private Stack<Object> _stk = null;

	private FlowDiagram _fdgm = null;

//	@SuppressWarnings("unused")
//	private String elementname = "";

	private String elementvalue = "";

	// private Object object = null;
	/**
	 * @roseuid 3E0428DC0120
	 */
	public XMLOperate() {
		_fdgm = new FlowDiagram();
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
	public void processingInstruction(String target, String data)
			throws SAXException {

	}

	/**
	 * @param ch
	 *            []
	 * @param start
	 * @param length
	 * @throws SAXException
	 * @roseuid 3E0A6E190347
	 */
	public void ignorableWhitespace(final char ch[], final int start,
			final int length) throws SAXException {

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

	/**
	 * This Method executed when a new XML Lable appear. It new a Node and Push
	 * it to Stack.
	 * 
	 * @param name
	 * @param attributes
	 * @roseuid 3E0A6E190397
	 */

	public void setFields(Class<?> cls, Object obj, AttributeList attributes)
			throws Exception {
		// Set object Field value.
		for (int i = 0; i < attributes.getLength(); i++) {
			String fieldname = attributes.getName(i);
			String fieldval = attributes.getValue(i);

			// CommonUtil.printDebugInfo("fieldname->"+fieldname+"
			// fieldval->"+fieldval);

			Field field = null;
			try {
				// for(int j=0; j<cls.getFields().length; j++)
				// CommonUtil.printDebugInfo("Field"+j+"->"+cls.getFields()[j].getName());
				field = cls.getField(fieldname);
				if (Modifier.isFinal(field.getModifiers())) {
					continue;
				}
			} catch (Exception e) {
				continue;
			}
			Class<?> type = field.getType();

			if (field != null) {
				if (type.equals(Long.TYPE)) { // Long
					fieldval = CommonUtil.isNumberString(fieldval) ? fieldval
							: "0";
					field.setLong(obj, Long.valueOf(fieldval).longValue());
					continue;
				} else if (type.equals(Integer.TYPE)) { // Int
					fieldval = CommonUtil.isNumberString(fieldval) ? fieldval
							: "0";
					field.setInt(obj, Integer.valueOf(fieldval).intValue());
					continue;
				} else if (type.equals(Short.TYPE)) { // Short
					fieldval = CommonUtil.isNumberString(fieldval) ? fieldval
							: "0";
					field.setShort(obj, Short.valueOf(fieldval).shortValue());
					continue;
				} else if (type.equals(Double.TYPE)) { // Double
					fieldval = CommonUtil.isNumberString(fieldval) ? fieldval
							: "0";
					field.setDouble(obj, new Double(fieldval).doubleValue());
					continue;
				} else if (type.equals(Class.forName("java.lang.String"))) { // String
					// CommonUtil.printDebugInfo("fieldname->"+fieldname+"
					// fieldval->"+fieldval);
					if (obj instanceof FlowDiagram) {
						CommonUtil.printDebugInfo("fieldname->" + fieldname
								+ "   fieldval->" + fieldval);
					}
					fieldval = fieldval != null ? fieldval : "";
					field.set(obj, fieldval);
					continue;
				}

				else if (type.equals(Float.TYPE)) { // Float
					fieldval = CommonUtil.isNumberString(fieldval) ? fieldval
							: "0";
					field.setFloat(obj, new Float(fieldval).floatValue());
					continue;
				}

				else if (type.equals(Boolean.TYPE)) { // Boolean
					fieldval = CommonUtil.isBooleanString(fieldval) ? fieldval
							: "false";
					field.setBoolean(obj, (Boolean.valueOf(fieldval))
							.booleanValue());
					continue;
				}

				else if (type.equals(Class.forName("java.sql.Date"))) { // Date
					java.sql.Date dt = CommonUtil.isValidDate(fieldval) ? CommonUtil
							.strToDate(fieldval)
							: new java.sql.Date(System.currentTimeMillis());
					field.set(obj, dt);
					continue;
				}

				else if (type.equals(Class.forName("java.util.Date"))) { // java.util.Date
					java.util.Date dt = CommonUtil.isValidDate(fieldval) ? CommonUtil
							.strToDate(fieldval)
							: new java.sql.Date(System.currentTimeMillis());
					field.set(obj, dt);
					continue;
				}
			}

		}
	}

	public void startElement(String name, AttributeList attributes) {
		try {
			if (name != null
					&& name.trim().toLowerCase().equals(
							this._fdgm.getClass().getName().toLowerCase())) {
				_stk.push(_fdgm);
				return;
			} else if (name != null
					&& name.startsWith("OLink.bpm.core.workflow.")) {
				Class<?> cls = Class.forName(name);

				Class<?>[] sign = new Class[1];

				sign[0] = this._fdgm.getClass();
				Constructor<?> cnstr = cls.getConstructor(sign);

				Object[] params = new Object[1];
				params[0] = this._fdgm;

				Object obj = cnstr.newInstance(params);

				if (_stk.peek() != null) {
					if (_stk.peek() instanceof FlowDiagram) {
						((FlowDiagram) _stk.peek())
								.appendElement((Element) obj);
						_stk.push(obj);
					} else if (_stk.peek() instanceof Element) {
						((Element) _stk.peek()).appendElement((Element) obj);
						_stk.push(obj);
					}
				}
			}

//			this.elementname = name;
			this.elementvalue = "";// 清空

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param ch
	 *            []
	 * @param start
	 * @param length
	 * @roseuid 3E0A6E1903B5
	 */
	public void characters(char ch[], int start, int length) {
		String fieldval = String.valueOf(ch, start, length);
		elementvalue += fieldval;
	}

	/**
	 * @param fieldval
	 * @param node
	 * @throws Exception
	 */
	private void setObjectFieldValue(String fieldname, String fieldval,
			Object node) throws Exception {
		// String fieldval = String.valueOf(ch, start, length);
		
			Field field = node.getClass().getField(fieldname);
			Class<?> type = field.getType();

			if (field != null && !Modifier.isFinal(field.getModifiers())) {
				if (type.equals(Long.TYPE)) { // Long
					fieldval = CommonUtil.isNumberString(fieldval) ? fieldval
							: "0";
					field.setLong(node, Long.valueOf(fieldval).longValue());
				} else if (type.equals(Integer.TYPE)) { // Int
					fieldval = CommonUtil.isNumberString(fieldval) ? fieldval
							: "0";
					field.setInt(node, Integer.valueOf(fieldval).intValue());
				} else if (type.equals(Short.TYPE)) { // Short
					fieldval = CommonUtil.isNumberString(fieldval) ? fieldval
							: "0";
					field.setShort(node, Short.valueOf(fieldval).shortValue());
				} else if (type.equals(Double.TYPE)) { // Double
					fieldval = CommonUtil.isNumberString(fieldval) ? fieldval
							: "0";
					field.setDouble(node, new Double(fieldval).doubleValue());
				} else if (type.equals(Class.forName("java.lang.String"))) { // String
					fieldval = fieldval != null ? fieldval : "";
					field.set(node, fieldval);
				} else if (type.equals(Float.TYPE)) { // Float
					fieldval = CommonUtil.isNumberString(fieldval) ? fieldval
							: "0";
					field.setFloat(node, new Float(fieldval).floatValue());
				} else if (type.equals(Boolean.TYPE)) { // Boolean
					fieldval = CommonUtil.isBooleanString(fieldval) ? fieldval
							: "false";
					field.setBoolean(node, (Boolean.valueOf(fieldval))
							.booleanValue());
				} else if (type.equals(Class.forName("java.sql.Date"))) { // Date
					java.sql.Date dt = CommonUtil.isValidDate(fieldval) ? CommonUtil
							.strToDate(fieldval)
							: new java.sql.Date(System.currentTimeMillis());
					field.set(node, dt);
				}

				else if (type.equals(Class.forName("java.util.Date"))) { // java.util.Date
					java.util.Date dt = CommonUtil.isValidDate(fieldval) ? CommonUtil
							.strToDate(fieldval)
							: new java.sql.Date(System.currentTimeMillis());
					field.set(node, dt);
					// continue;
				}
			}
		
	}

	/**
	 * @param name
	 * @roseuid 3E0A6E1903DD
	 */
	public void endElement(String name) {
		// setFieldValue(elementvalue);
		if (_stk != null && !_stk.isEmpty()) {
			if (name.startsWith("OLink.bpm.core.workflow.")) {
				_stk.pop();
			} else {
				Object obj = _stk.peek();
				try {
					setObjectFieldValue(name, elementvalue, obj);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * @param e
	 * @throws SAXException
	 * @roseuid 3E0A6E1A0009
	 */
	public void fatalError(SAXParseException e) throws SAXException {
		throw e;
	}

	/**
	 * @return OLink.bpm.core.workflow.FlowDiagram
	 * @roseuid 3E0A6E1A001D
	 */
	public FlowDiagram getResult() {
		return _fdgm;
	}
}
