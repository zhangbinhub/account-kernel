package OLink.bpm.core.email.util;

import java.util.Properties;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlParser {
	
	private Properties props;

	public XmlParser(String url) {
		ParseXml parseXml = new ParseXml();
		try {
			parseXml.parse(url);
			props = parseXml.getProps();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			parseXml = null;
		}

	}

	public String getString(String name) {
		return props.getProperty(name);
	}
	
	public void clear() {
		if (props != null) {
			props.clear();
		}
	}
	
	private static class ConfigParser extends DefaultHandler {
		
		// 定义一个properties用来存放属性
		private Properties props;
		private String currentName;
		private StringBuffer currentValue = new StringBuffer();

		public ConfigParser() {
			this.props = new Properties();
		}

		public Properties getProps() {
			return this.props;
		}

		// 这里是将xml中元素值加入currentValue
		public void characters(char[] ch, int start, int length)
				throws SAXException {

			currentValue.append(ch, start, length);
		}

		// 在遇到</xx>时，将之间的字符存放在props中间
		public void endElement(String uri, String localName, String name)
				throws SAXException {
			props.put(currentName.toLowerCase(), currentValue.toString().trim());
		}

		// 定义开始解析元素的方法，这里将<xx>中的名称xx提出来，
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			currentValue.delete(0, currentValue.length());
			currentName = qName;
		}

	}
	
	private static class ParseXml {
		// 定义一个Proerties用来存放属性值
		private Properties props;

		public Properties getProps() {
			return this.props;
		}

		public void parse(String filename) throws Exception {
			// 将我们的解析器对象化
			ConfigParser handler = new ConfigParser();
			// 获取SAX工厂对象
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(false);
			factory.setValidating(false);
			// 获取SAX解析
			SAXParser parser = factory.newSAXParser();

			try {
				// 将解析器和解析对象xml联系起来，开始解析
				parser.parse(filename, handler);
				// 获取解析成功后的属性
				props = handler.getProps();
			} finally {
				factory = null;
				parser = null;
				handler = null;
			}
		}
	}
}
