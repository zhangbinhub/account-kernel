package OLink.bpm.core.xmpp;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

/**
 * XMPPConnection 连接池
 * 
 * @author nicholas-zhen
 * 
 */
public class XMPPConnectionPool {
	public final static Logger LOG = Logger.getLogger(XMPPConnectionPool.class);

	private String name = "XMPPConnectionPool"; // 连接池名字
	private int inUsed = 0; // 使用的连接数
	private ArrayList<XMPPConnection> freeConnections = new ArrayList<XMPPConnection>();// 容器，空闲连接
	private static XMPPConnectionPool singleton;
	private static final Object LOCK = new Object();
	private XMPPConfig xmppConfig;

	public static XMPPConnectionPool getInstance() {
        // Synchronize on LOCK to ensure that we don't end up creating
        // two singletons.
        synchronized (LOCK) {
            if (null == singleton) {
            	XMPPConnectionPool pool = new XMPPConnectionPool();
            	singleton = pool;         		
            }
        }
        return singleton;   	 
    }
	
	private XMPPConnectionPool() {
		// 初始化配置文件
		xmppConfig = XMPPConfig.getInstance();
	}

	/**
	 * 用完，释放连接
	 * 
	 * @param con
	 */
	public synchronized void freeConnection(XMPPConnection con) {
		if (con != null) {
			if (con.isConnected()) {
				con.disconnect();
			}
			this.freeConnections.add(con);// 添加到空闲连接的末尾
			this.inUsed--;
		}
	}

	/**
	 * 
	 * 从连接池里得到连接
	 * 
	 * @return
	 * @throws XMPPException
	 */
	public synchronized XMPPConnection getConnection() throws XMPPException {
		XMPPConnection con = null;
		if (this.freeConnections.size() > 0) {
			con = this.freeConnections.get(0);
			this.freeConnections.remove(0);// 如果连接分配出去了，就从空闲连接里删除
			if (con == null)
				con = getConnection(); // 继续获得连接
		} else {
			con = newConnection(); // 新建连接
		}
		if (xmppConfig.getMaxConn() == 0 || xmppConfig.getMaxConn() < this.inUsed) {
			con = null;// 等待 超过最大连接时
		}
		if (con != null) {
			this.inUsed++;
			if (con.isConnected()){ // 如果已连接则先断开
				con.disconnect();
			}
			con.connect();
			LOG.info("得到　" + this.name + "　的连接，现有" + inUsed + "个连接在使用!");
		}
		return con;
	}

	/**
	 *释放全部连接
	 */
	public synchronized void release() {
		Iterator<XMPPConnection> allConns = this.freeConnections.iterator();
		while (allConns.hasNext()) {
			XMPPConnection con = allConns.next();
			if (con.isConnected()) {
				con.disconnect();
			}
		}
		this.freeConnections.clear();
	}

	/**
	 * 创建新连接
	 * 
	 * @return
	 * @throws XMPPException
	 */
	private XMPPConnection newConnection() throws XMPPException {
		// Create the configuration for this new connection
		ConnectionConfiguration config = new ConnectionConfiguration(xmppConfig.getHost(), xmppConfig.getPort());
		config.setDebuggerEnabled(xmppConfig.isDebug());
		config.setSASLAuthenticationEnabled(false);
		config.setCompressionEnabled(xmppConfig.isCompression());
//		config.setSendPresence(xmppConfig.sendInitialPresence());
		if (xmppConfig.getSocketFactory() == null) {
			config.setSocketFactory(xmppConfig.getSocketFactory());
		}
		XMPPConnection connection = new XMPPConnection(config);
		return connection;
	}

	/**
	 * 定时处理函数
	 */
	public synchronized void TimerEvent() {
		// 暂时还没有实现以后会加上的
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}
}
