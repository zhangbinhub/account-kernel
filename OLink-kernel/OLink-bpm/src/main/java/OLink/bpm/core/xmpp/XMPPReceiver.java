package OLink.bpm.core.xmpp;

import OLink.bpm.core.xmpp.service.OBPMService;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;

public class XMPPReceiver {
	public final static Logger LOG = Logger.getLogger(XMPPReceiver.class);
	
	public void start() {
		try {
			XMPPConnectionPool connectionPool = XMPPConnectionPool.getInstance();
			XMPPConnection connection = connectionPool.getConnection();

			XMPPConfig xmppConfig = XMPPConfig.getInstance();
			try {
				// 第三个参数可作为域名
				connection.login(xmppConfig.getUsername(), xmppConfig.getPassword());
			} catch (XMPPException e) {
				e.printStackTrace();

				// Create the test accounts
				if (!connection.getAccountManager().supportsAccountCreation())
					LOG.error("Server does not support account creation");

				// Create the account and try logging in again as the same user.
				try {
					createAccount(connection, xmppConfig.getUsername(), xmppConfig.getPassword());
				} catch (Exception e1) {
					e1.printStackTrace();
					LOG.error("Could not create user: " + xmppConfig.getUsername());
				}
			}

			XmppReveiverRunnable xmppReceiverRunnable = new XmppReveiverRunnable(connection);
			new Thread(xmppReceiverRunnable).start();
		} catch (XMPPException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 创建OBPM账号
	 * 
	 * @param username
	 * @param password
	 * @throws XMPPException
	 */
	private void createAccount(XMPPConnection connection, String username, String password) throws XMPPException {
		connection.getAccountManager().createAccount(username, password);
	}

	class XmppReveiverRunnable implements Runnable {
		private XMPPConnection connection;

		public XmppReveiverRunnable(XMPPConnection connection) {
			this.connection = connection;
		}

		public void run() {
			try {
				while (true) {
					// 过滤只接收IQ信息
					PacketFilter filter = new PacketTypeFilter(IQ.class);
					PacketCollector collector = connection.createPacketCollector(filter);
					IQ iqReceived = (IQ) collector.nextResult();
					if (iqReceived instanceof OBPMService) {
						// 发送消息
						Packet iqSended = ((OBPMService) iqReceived).createResultPacket();
						connection.sendPacket(iqSended);
					}

					Thread.sleep(1000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
