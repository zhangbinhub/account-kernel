package OLink.bpm.core.xmpp;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import OLink.bpm.core.user.ejb.BaseUser;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.IQ.Type;

import eWAP.core.Tools;

/**
 * XMPP 消息发送器
 * 
 * @author znicholas
 * 
 */
public class XMPPSender {
	public final static Logger LOG = Logger.getLogger(XMPPSender.class);

	private static XMPPSender singleton; // 单例
	private static final Object LOCK = new Object();
	private Queue<XMPPNotification> packetQueue = new ConcurrentLinkedQueue<XMPPNotification>();

	private Object waitForJobsMonitor = new Object();

	private Thread thread = new NotificationThread();

	private boolean isWaitForJobs = false;

	private XMPPSender() throws Exception {
		// 启动线程
		thread.start();
	}

	public static XMPPSender getInstance() throws Exception {
		// Synchronize on LOCK to ensure that we don't end up creating
		// two singletons.
		synchronized (LOCK) {
			if (null == singleton) {
				XMPPSender sender = new XMPPSender();
				singleton = sender;
			}
		}
		return singleton;
	}

	private XMPPConnection connectToServer(BaseUser from) throws Exception {
		XMPPConnection connection = XMPPConnectionPool.getInstance().getConnection();

		if (!connection.isConnected()) {
			connection.connect();
		}
		if (!connection.isAuthenticated()) {
			connection.login(getUserName(from), getPassword(from));
		}

		return connection;
	}

	/**
	 * 提醒处理
	 * 
	 * @param notification
	 */
	public void processNotification(XMPPNotification notification) {
		packetQueue.add(notification);
		kickThread();
	}

	/**
	 * 提醒处理
	 * 
	 * @param notification
	 * @param from
	 *            发送者
	 * @param to
	 *            接收者
	 */
	public void processNotification(XMPPNotification notification, BaseUser from, BaseUser to) {
		notification.setSender(from);
		notification.addReceiver(to);

		packetQueue.add(notification);
		kickThread();
	}

	/**
	 * 唤醒等待的线程
	 */
	private void kickThread() {
		if (!this.thread.isInterrupted()) {
			synchronized (waitForJobsMonitor) {
				waitForJobsMonitor.notifyAll();
			}
		}
	}

	/**
	 * 获取用户密码
	 * 
	 * @param user
	 * @return
	 * @throws Exception
	 */
	private String getPassword(BaseUser user) throws Exception {
		String password = "";

		if (user.isAdmin()) {
			password = user.getLoginpwd();
		} else {// 企业用户
			password = Tools.decryptPassword(user.getLoginpwd());
		}

		return password;
	}

	/**
	 * 获取用户完整登录账号
	 * 
	 * @param user
	 * @return
	 * @throws Exception
	 */
	private String getUserName(BaseUser user) throws Exception {
		String userName = "";
		String separator = XMPPConfig.getInstance().getUsernameDomainSeparator();

		if (user.isAdmin()) {
			userName = user.getLoginno();
		} else {// 企业用户
			userName = user.getLoginno() + separator + user.getDomain().getName();
		}

		return encodeUserName(userName);
	}

	/**
	 * 对特殊字符进行编码
	 * 
	 * @param username
	 * @return
	 */
	private String encodeUserName(String username) {
		username = username.replace("\\", "\\5c");
		username = username.replace("@", "\\40");

		return username;
	}

	/**
	 * 提醒发送的线程
	 * 
	 * @author Nicholas
	 * 
	 */
	private class NotificationThread extends Thread {
		public void run() {
			while (true) {
				synchronized (waitForJobsMonitor) {
					if (!packetQueue.isEmpty()) {
						// 获取并移除此队列的头，如果此队列为空，则返回 null
						XMPPConnection connection = null;
						XMPPNotification notification = packetQueue.poll();
						BaseUser from = notification.getSender();
						try {
							String fromUserName = getUserName(from);
							connection = connectToServer(from);
							XMPPNotification clone = (XMPPNotification) notification.clone();

							Collection<BaseUser> reveivers = notification.getReceivers();
							for (Iterator<BaseUser> iterator = reveivers.iterator(); iterator.hasNext();) {
								BaseUser to = iterator.next();
								String toUserName = getUserName(to);

								clone.setPacketID(Packet.nextID());
								clone.setFrom(fromUserName + "@" + connection.getServiceName());
								clone.setTo(toUserName + "@obpm." + connection.getServiceName());
								clone.setType(Type.SET);

								connection.sendPacket(clone);
								LOG.info("Send Notification successed from: " + fromUserName + " to: " + toUserName);
							}
						} catch (Exception e) {
							LOG.error("Send Notification failed, " + e.getLocalizedMessage());
						} finally {
							XMPPConnectionPool.getInstance().freeConnection(connection);
						}
					}
					isWaitForJobs = packetQueue.isEmpty();
				}

				// 等待新的作业
				if (isWaitForJobs) {
					synchronized (waitForJobsMonitor) {
						try {
							if (waitForJobsMonitor != null) {
								waitForJobsMonitor.wait();
							}
						} catch (InterruptedException e) {
							LOG.warn("XMPPSender.SendPacketThread", e);
						}
					}
				}
			}
		}
	}
}
