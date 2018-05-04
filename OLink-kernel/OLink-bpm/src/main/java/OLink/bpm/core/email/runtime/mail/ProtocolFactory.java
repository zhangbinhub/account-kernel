package OLink.bpm.core.email.runtime.mail;

import java.util.Date;

import javax.mail.Folder;

import OLink.bpm.core.email.runtime.model.Email;
import OLink.bpm.core.email.runtime.model.EmailHeader;
import OLink.bpm.core.email.runtime.model.EmailPart;
import OLink.bpm.core.email.util.EmailConfig;


/**
 * 
 * @author Tom
 * 
 */
public class ProtocolFactory{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6459184572982090224L;
	private ConnectionProfile profile;
	private AuthProfile auth;
	private ConnectionMetaHandler handler;

	public ProtocolFactory(ConnectionProfile profile, AuthProfile auth,
			ConnectionMetaHandler handler) {
		this.profile = profile;
		this.auth = auth;
		this.handler = handler;
	}

	public Protocol getPop3Protocol() {

		return null;
	}

	public Protocol getImapProtocol(String folderName) {
		return new ImapProtocolImpl(profile, folderName, auth, handler);
	}

	public Protocol getProtocol(String folderName) {
		if (profile.getProtocol().equals("pop3")) {
			return getPop3Protocol();
		} else {
			return getImapProtocol(folderName);
		}
	}
	
	public static void main(String[] args) throws Exception {
		test0();
		test1();
		test2();
	}
	
	private static void test0() throws Exception {
		ConnectionProfile profile = EmailConfig.getConnectionProfile();
		AuthProfile auth = new AuthProfile();
		ConnectionMetaHandler handler = null;
		auth.setUserName("allen");
		auth.setPassword("123456");
		ProtocolFactory factory = new ProtocolFactory(profile, auth, handler);
		ImapProtocolImpl protocol = (ImapProtocolImpl) factory.getImapProtocol("test");
		handler = protocol.connect(Folder.READ_WRITE);
		protocol.createFolder();
		handler.closeFolder(true);
		//handler.getFolder().delete(false);
		//ImapProtocolImpl protocol2 = (ImapProtocolImpl) factory.getImapProtocol("test2");
		//protocol2.connect(Folder.READ_WRITE);
		//Folder folder = handler.getStore().getFolder("test");
		//((IMAPFolder)handler.getFolder()).renameTo(folder);
		System.out.println(handler.getFolder().getFullName());
		handler.closeStore();
	}
	
	private static void test1() throws Exception {
		ConnectionProfile profile = EmailConfig.getConnectionProfile();
		AuthProfile auth = new AuthProfile();
		ConnectionMetaHandler handler = null;
		auth.setUserName("allen");
		auth.setPassword("123456");
		ProtocolFactory factory = new ProtocolFactory(profile, auth, handler);
		ImapProtocolImpl protocol = (ImapProtocolImpl) factory.getImapProtocol("INBOX");
		handler = protocol.connect(Folder.READ_WRITE);
		
		System.out.println("=======" + protocol.getTotalMessageCount());
		System.out.println("=======" + protocol.listFolders().length);
		for (int i = 1; i < protocol.getTotalMessageCount(); i++) {
			if (i == 5) {
				break;
			}
			System.out.println("=======" + protocol.getMessage(protocol.getTotalMessageCount()).getSubject());
		}
//		List list = protocol.fetchHeaders(new int[]{1,2,3,5,8,12,44,55});
//		if (list != null && !list.isEmpty()) {
//			for (Iterator it = list.iterator(); it.hasNext(); ) {
//				EmailHeader header = (EmailHeader) it.next();
//				System.out.println("===" + header.getSubject() + "===" + header.getDateString() + "===" + header.getFromString());
//			}
//		}

		handler.closeStore();
	}
	
	private static void test2() throws Exception {
		ConnectionProfile profile = EmailConfig.getConnectionProfile();
		AuthProfile auth = new AuthProfile();
		ConnectionMetaHandler handler = null;
		auth.setUserName("allen");
		auth.setPassword("123456");
		ProtocolFactory factory = new ProtocolFactory(profile, auth, handler);
		ImapProtocolImpl protocol = (ImapProtocolImpl) factory.getImapProtocol("test");
		handler = protocol.connect(Folder.READ_WRITE, true);
		
		Email email = new Email();
		EmailHeader header = new EmailHeader();
		header.setDate(new Date());
		header.setSubject("test email");
		EmailPart part = new EmailPart();
		part.setContent("test content");
		email.addPart(part);
		email.setBaseHeader(header);
		
		System.out.println("----111111111---->" + protocol.getTotalMessageCount());
		
		protocol.appendMessage(email);
		
		System.out.println("----222222222---->" + protocol.getTotalMessageCount());
		
		handler.closeStore();
	}

	/**
	 * @return the profile
	 */
	public ConnectionProfile getProfile() {
		return profile;
	}

	/**
	 * @return the auth
	 */
	public AuthProfile getAuth() {
		return auth;
	}

	/**
	 * @return the handler
	 */
	public ConnectionMetaHandler getConnectionMetaHandler() {
		return handler;
	}

	/**
	 * @param handler the handler to set
	 */
	public void setConnectionMetaHandler(ConnectionMetaHandler handler) {
		this.handler = handler;
	}

}
