package OLink.bpm.util.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import OLink.bpm.util.xml.converter.OBPMJavaBeanConvert;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import OLink.bpm.util.xml.mapper.OmitNotExistsFiledMapper;

import com.thoughtworks.xstream.XStream;

public class XmlUtil {
	private static Map<?, ?> omitFieldMap;

	public static String toXml(Object obj) {
		return getXstream().toXML(obj);
	}

	public static File toXmlFile(Object obj, String fileName) throws IOException {
		return toXmlFile(obj, fileName, "UTF-8");
	}

	public static File toXmlFile(Object obj, String fileName, String charSetName) throws IOException {
		File file = new File(fileName);
		if (!file.exists()) {
			File dir = file.getParentFile();
			if (!dir.exists()) {
				if(!dir.mkdirs())
					throw new IOException("create directory '" + dir.getPath() + "' failed!");
			}
			if(!file.createNewFile())
				throw new IOException("create file '" + fileName + "' failed!");
		}
		OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(fileName), charSetName);
		getXstream().toXML(obj, out);
		return new File(fileName);
	}

	public static Object toOjbect(String xml) {
		return getXstream().fromXML(xml);
	}

	public static Object toOjbect(File file) throws FileNotFoundException {
		return getXstream().fromXML(new FileReader(file));
	}

	/**
	 * 获取XML解析器，（注：考虑多线程中，静态方法可能存在的问题）
	 * 
	 * @return
	 */
	public static XStream getXstream() {
		XStream xstream = new XStream(new DomDriver());
		xstream.registerConverter(new OBPMJavaBeanConvert(new OmitNotExistsFiledMapper(xstream.getMapper())),
				XStream.PRIORITY_LOW);
		xstream.setMode(XStream.XPATH_ABSOLUTE_REFERENCES);
		xstream.autodetectAnnotations(true);

		initOmitField(xstream);

		return xstream;
	}

	public static Map<?, ?> parse(InputStream is) {
		Map<?, ?> map = new HashMap<Object, Object>();

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			DocumentBuilder builder = factory.newDocumentBuilder();

			Document document = builder.parse(is);

			if (document != null) {
				map = getNodeBean(document.getFirstChild());
			}
			return map;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return map;
	}

	private static Map<?, ?> getNodeBean(Node parent) {
		Map<Object, Object> rtn = new HashMap<Object, Object>();

		if (parent != null) {
			Map<Object, Object> attrMap = new HashMap<Object, Object>();
			if (parent.hasAttributes()) {
				NamedNodeMap attrs = parent.getAttributes();
				for (int j = 0; j < attrs.getLength(); j++) {
					Node attr = attrs.item(j);
					attr.getNodeName();
					attr.getNodeValue();
					attrMap.put(attr.getNodeName(), attr.getNodeValue());
				}
			}

			rtn.put("tagName", parent.getNodeName());
			rtn.put("attr", attrMap);

			NodeList nodeList = parent.getChildNodes();
			if (nodeList != null) {
				List<Object> children = new ArrayList<Object>();
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node child = nodeList.item(i);
					if (child.getNodeType() == Node.ELEMENT_NODE) {
						children.add(getNodeBean(child));
					}
				}

				rtn.put("children", children);
			}
		}

		return rtn;
	}

	/**
	 * 初始化忽略的字段
	 * 
	 * @throws ClassNotFoundException
	 */
	private static void initOmitField(XStream xstream) {
		try {
			Map<?, ?> map = getOmitFieldMap();
			List<?> clazzes = (List<?>) map.get("children");
			for (Iterator<?> iterator = clazzes.iterator(); iterator.hasNext();) {
				Map<?, ?> clazz = (Map<?, ?>) iterator.next();
				Map<?, ?> attr = (Map<?, ?>) clazz.get("attr");
				String className = (String) attr.get("name");
				Class<?> definedIn = Class.forName(className);

				ArrayList<?> fields = (ArrayList<?>) clazz.get("children");
				for (Iterator<?> iterator2 = fields.iterator(); iterator2.hasNext();) {
					Map<?, ?> field = (Map<?, ?>) iterator2.next();
					Map<?, ?> fieldAttr = (Map<?, ?>) field.get("attr");
					String fieldName = (String) fieldAttr.get("name");

					xstream.omitField(definedIn, fieldName);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据配置文件获取忽略字段信息
	 * 
	 * @return
	 */
	private static Map<?, ?> getOmitFieldMap() {
		if (omitFieldMap == null) {
			InputStream is = XmlUtil.class.getClassLoader().getResourceAsStream("xstreamOmitField.xml");
			Map<?, ?> map = XmlUtil.parse(is);
			omitFieldMap = map;
		}
		return omitFieldMap;
	}

	public static void main(String[] args) throws Exception {
		System.out.println(getXstream());
	}
}
