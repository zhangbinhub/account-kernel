package OLink.bpm.core.xmpp.provider;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import OLink.bpm.core.xmpp.service.MenuService;
import OLink.bpm.core.xmpp.service.MethodInvoker;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import OLink.bpm.core.xmpp.service.OBPMService;

public class ObpmServerProvider implements IQProvider {
	private static Map<String, Class<? extends OBPMService>> registeredServiceClassMap = new HashMap<String, Class<? extends OBPMService>>();

	static {
		registeredServiceClassMap.put("MenuService", MenuService.class);
	}

	public IQ parseIQ(XmlPullParser parser) throws Exception {
		int eventType;
		String elementName;
		boolean done = false;
		OBPMService instance = null;
		Class<? extends OBPMService> clazz = null;

		//eventType = parser.next();
		String namespace = parser.getNamespace();
		MethodProvider methodProvider = new MethodProvider();

		if ("obpm:iq:service".equals(namespace)) {
			while (!done) {
				try {
					eventType = parser.next();
					if (eventType == XmlPullParser.START_TAG) {
						elementName = parser.getName();
						namespace = parser.getNamespace();
						
						if ("name".equals(elementName)) {
							String serviceName = parser.nextText();
							if (registeredServiceClassMap.containsKey(serviceName)) {
								clazz = registeredServiceClassMap.get(serviceName);
								instance = clazz.newInstance();
							}
						} else if ("method".equals(elementName)) {
							MethodInvoker invoker = (MethodInvoker) methodProvider.parseExtension(parser);
							instance.setInvoker(invoker);
						} else {
							throw new XMPPException("Unknown combination of namespace \"" + namespace
									+ "\" and element name \"" + elementName + "\" in Service packet.");
						}
					} else if (eventType == XmlPullParser.END_TAG) {
						if ("obpm".equals(parser.getName())) {
							// 方法调用
							done = true;
						}
					}
				} catch (XmlPullParserException e) {
					throw e;
				} catch (IOException e) {
					throw e;
				} catch (InstantiationException e) {
					throw e;
				} catch (IllegalAccessException e) {
					throw e;
				}
			}
		}

		return instance;
	}

}
