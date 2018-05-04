package OLink.bpm.core.xmpp;

import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

import javax.net.SocketFactory;

import OLink.bpm.core.xmpp.provider.ObpmServerProvider;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.provider.ProviderManager;
import org.xmlpull.mxp1.MXParser;
import org.xmlpull.v1.XmlPullParser;

public class XMPPConfig {
	
	private int minConn = 5; // 最小连接数
	private int maxConn = 10; // 最大连接数

	private String host = "localhost"; // 服务器地址
	private String serviceName = "localhost";
	private int port = 5222; // 服务器端口
	private String chatDomain = "chat";
	private String mucDomain = "conference";
	private String username = "obpm_server"; // 系统用户名称
	private String password = "obpm_server"; // 系统用户密码
	private boolean debug = true; // 是否调试
	private boolean compression = false; // 是否压缩
	private String sparkVersion = "Spark 2.6.3";
	private String usernameDomainSeparator = "-";
	private static XMPPConfig singleton;
	private static final Object LOCK = new Object();

	public String getSparkVersion() {
		return sparkVersion;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	public boolean isCompression() {
		return compression;
	}

	public void setCompression(boolean compression) {
		this.compression = compression;
	}

	public int getMinConn() {
		return minConn;
	}

	public void setMinConn(int minConn) {
		this.minConn = minConn;
	}

	public int getMaxConn() {
		return maxConn;
	}

	public void setMaxConn(int maxConn) {
		this.maxConn = maxConn;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getChatDomain() {
		return chatDomain;
	}

	public void setChatDomain(String chatDomain) {
		this.chatDomain = chatDomain;
	}

	public String getMucDomain() {
		return mucDomain;
	}

	public void setMucDomain(String mucDomain) {
		this.mucDomain = mucDomain;
	}

	public final static Logger LOG = Logger.getLogger(XMPPConfig.class);

	private XMPPConfig() {
		init();
	}

	public static XMPPConfig getInstance() {
        // Synchronize on LOCK to ensure that we don't end up creating
        // two singletons.
        synchronized (LOCK) {
            if (null == singleton) {
            	XMPPConfig config = new XMPPConfig();
            	singleton = config;         		
            }
        }
        return singleton;   	 
    }

	private void init() {
		try {
			boolean found = false;
			// Try to load the configutation from an XML file specific for this
			Enumeration<URL> resources = this.getClass().getClassLoader().getResources("xmpp.cfg.xml");
			while (resources.hasMoreElements()) {
				found = parseURL(resources.nextElement());
			}
			if (!found) {
				LOG.warn("File xmpp.cfg.xml not found. Using default config.");
			}
			
			// 添加信息(XML)解释器
			ProviderManager providerManager = ProviderManager.getInstance();
			providerManager.addIQProvider("obpm", "obpm:iq:service", new ObpmServerProvider());
		} catch (Exception e) {
			/* Do Nothing */
		}
	}

	private boolean parseURL(URL url) {
		boolean parsedOK = false;
		InputStream systemStream = null;
		try {
			systemStream = url.openStream();
			XmlPullParser parser = new MXParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
			parser.setInput(systemStream, "UTF-8");
			int eventType = parser.getEventType();
			do {
				if (eventType == XmlPullParser.START_TAG) {
					if (parser.getName().equals("host")) {
						host = parser.nextText();
					} else if (parser.getName().equals("port")) {
						port = parseIntProperty(parser, port);
					} else if (parser.getName().equals("serviceName")) {
						serviceName = parser.nextText();
					} else if (parser.getName().equals("chat")) {
						chatDomain = parser.nextText();
					} else if (parser.getName().equals("muc")) {
						mucDomain = parser.nextText();
					} else if (parser.getName().equals("max_size")) {
						maxConn = parseIntProperty(parser, maxConn);
					} else if (parser.getName().equals("min_size")) {
						minConn = parseIntProperty(parser, minConn);
					} else if (parser.getName().equals("username")) {
						username = parser.nextText();
					} else if (parser.getName().equals("password")) {
						password = parser.nextText();
					} else if (parser.getName().equals("spark_version")) {
						sparkVersion = parser.nextText();
					} else if (parser.getName().equals("username_domain_separator")) {
						usernameDomainSeparator = parser.nextText();
					} else if (parser.getName().equals("debug")) {
						debug = Boolean.parseBoolean(parser.nextText());
					}
				}
				eventType = parser.next();
			} while (eventType != XmlPullParser.END_DOCUMENT);
			parsedOK = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				systemStream.close();
			} catch (Exception e) {
				/* Do Nothing */
			}
		}
		return parsedOK;
	}

	public String getUsernameDomainSeparator() {
		return usernameDomainSeparator;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	private static int parseIntProperty(XmlPullParser parser, int defaultValue) throws Exception {
		try {
			return Integer.parseInt(parser.nextText());
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
			return defaultValue;
		}
	}

	public SocketFactory getSocketFactory() {
		return null;
	}

	public boolean sendInitialPresence() {
		return true;
	}
}
