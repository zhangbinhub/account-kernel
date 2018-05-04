package OLink.bpm.core.xmpp.provider;

import OLink.bpm.core.xmpp.service.MethodInvoker;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.xmlpull.v1.XmlPullParser;

public class MethodProvider implements PacketExtensionProvider {
	public PacketExtension parseExtension(XmlPullParser parser) throws Exception {
		int eventType;
		String elementName;

		boolean done = false;
		MethodInvoker invoker = new MethodInvoker();
		String methodName = parser.getAttributeValue("", "name");
		invoker.setMethodName(methodName);

		while (!done) {
			eventType = parser.next();

			if (eventType == XmlPullParser.START_TAG) {
				elementName = parser.getName();
				if ("parameter".equals(elementName)) {
					String name = parser.getAttributeValue("", "name");
					String type = parser.getAttributeValue("", "type");
					String value = parser.getAttributeValue("", "value");
					
					invoker.addMethodParameter(name, type, value);
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if ("method".equals(parser.getName())) {
					done = true;
				}
			}
		}

		return invoker;
	}
}
