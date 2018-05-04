package OLink.bpm.core.xmpp;

import OLink.bpm.core.superuser.ejb.SuperUserProcess;
import OLink.bpm.core.user.ejb.UserProcess;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.core.xmpp.notification.SiteMessageIQ;
import OLink.bpm.core.superuser.ejb.SuperUserVO;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.IQ.Type;

import OLink.bpm.core.domain.ejb.DomainProcess;
import OLink.bpm.core.domain.ejb.DomainVO;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.user.ejb.UserProcessBean;
import OLink.bpm.util.ProcessFactory;

public class SiteMessageIQSender {

	private static SiteMessageIQSender singleton;
	private static final Object LOCK = new Object();
	private XMPPConnection systemConnection = null;
	private SuperUserVO admin = null;

	private SiteMessageIQSender() throws Exception {
		try {
			SuperUserProcess superUserProcess = (SuperUserProcess) ProcessFactory.createProcess(SuperUserProcess.class);
			admin = superUserProcess.getDefaultAdmin();

			systemConnection = XMPPConnectionPool.getInstance().getConnection();
			systemConnection.login(admin.getLoginno(), admin.getLoginpwd());
		} catch (Exception e) {
			throw e;
		}
	}

	public static SiteMessageIQSender getInstance() throws Exception {
		synchronized (LOCK) {
			if (null == singleton) {
				singleton = new SiteMessageIQSender();
			}
		}
		return singleton;
	}

	public boolean isConnected() {
		return this.systemConnection.isConnected();
	}

	public void connect() throws XMPPException {
		this.systemConnection.connect();
	}

	private void connectToServer() {
		try {
			if (systemConnection == null) {
				// 重新获取connection
				XMPPConnectionPool.getInstance().freeConnection(systemConnection);
				systemConnection = XMPPConnectionPool.getInstance().getConnection();
			}
			if (!systemConnection.isConnected()) {
				systemConnection.connect();
			}
			if (!systemConnection.isAuthenticated()) {
				systemConnection.login(admin.getLoginno(), admin.getLoginpwd());
			}
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送IQ
	 * 
	 * @param siteMessageIQ
	 * @param webUser
	 * @throws Exception
	 */
	public void process(SiteMessageIQ siteMessageIQ, WebUser webUser) throws Exception {
		connectToServer();
		DomainProcess dp = (DomainProcess) ProcessFactory.createProcess(DomainProcess.class);
		DomainVO domain = (DomainVO) dp.doView(webUser.getDomainid());
		SiteMessageIQ clone = siteMessageIQ.clone();
		// String sparkVersion = XMPPConfig.getInstance().getSparkVersion();
		String separator = XMPPConfig.getInstance().getUsernameDomainSeparator();
		clone.setPacketID(Packet.nextID());
		clone
				.setTo(siteMessageIQ.getTo() + separator + domain.getName() + "@obpm."
						+ systemConnection.getServiceName());
		clone.setFrom(siteMessageIQ.getFrom() + separator + domain.getName()
				+ "@" + systemConnection.getServiceName());
		systemConnection.sendPacket(clone);
	}

	public static void main(String[] args) throws Exception {
		UserProcess process = new UserProcessBean();
		String domainid = "11de-c138-782d2f26-9a62-8bacb70a86e1"; // 企业域ID，从数据库获得
		UserVO to = process.getUserByLoginno("testuser", domainid);
		UserVO from = process.getUserByLoginno("testboss", domainid);
		
		SiteMessageIQ siteMessageIQ = new SiteMessageIQ();
		siteMessageIQ.setTitle("www");
		siteMessageIQ.setContent("ddddd");
		siteMessageIQ.getReceivers().add(to);
		siteMessageIQ.setSender(from);
		siteMessageIQ.setType(Type.SET);
		XMPPSender siteMessageIQSender = XMPPSender.getInstance();
		// if (!siteMessageIQSender.isConnected()) {
		// siteMessageIQSender.connect();
		// }
		siteMessageIQSender.processNotification(siteMessageIQ);
	}
}
