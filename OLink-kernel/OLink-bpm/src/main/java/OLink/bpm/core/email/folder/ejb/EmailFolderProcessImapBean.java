package OLink.bpm.core.email.folder.ejb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.mail.Folder;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.email.util.Constants;
import OLink.bpm.util.StringUtil;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import OLink.bpm.core.email.runtime.mail.ImapProtocolImpl;
import OLink.bpm.core.email.runtime.mail.ProtocolFactory;
import OLink.bpm.core.email.email.ejb.EmailUser;
import org.apache.log4j.Logger;

import OLink.bpm.base.action.ParamsTable;

import com.sun.mail.imap.IMAPFolder;

public class EmailFolderProcessImapBean extends AbstractDesignTimeProcessBean<EmailFolder> implements
		EmailFolderProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6319523364694651021L;
	private transient ProtocolFactory protocolFactory;
	
	private static final Logger LOG = Logger.getLogger(EmailFolderProcessImapBean.class);

	public EmailFolderProcessImapBean(ProtocolFactory protocolFactory) throws Exception {
		this.protocolFactory = protocolFactory;
	}

	protected IDesignTimeDAO<EmailFolder> getDAO() throws Exception {
		return null;
	}

	public void doCreateEmailFolderByName(String folderName, String ownerId) throws Exception {
		ImapProtocolImpl protocol = (ImapProtocolImpl) protocolFactory.getImapProtocol(folderName);
		protocol.connect(Constants.CONNECT_TYPE_READ_ONLY);
		protocol.createFolder();
		protocol.updateFolders();
	}
	
	private boolean createEmailFolderByName(ImapProtocolImpl protocol) throws Exception {
		if (getSystemEmailFolderByName(protocol.getFolderName()) == null) {
			protocol.connect(Constants.CONNECT_TYPE_READ_ONLY);
			protocol.createFolder();
			Thread.sleep(1000);
			return true;
		}
		return false;
	}

	public EmailFolder getEmailFolderById(String folderid) throws Exception {
		try {
			IMAPFolder folder = protocolFactory.getConnectionMetaHandler().getFolderByUID(Long.parseLong(folderid));
			if (folder != null) {
				return EmailFolder.valueOf(folder);
			}
		} catch (NumberFormatException e) {
			LOG.warn(e.toString());
		}
		return null;
	}

	public EmailFolder getEmailFolderByOwnerId(String folderName, String ownerId) throws Exception {
		Map<Long, Folder> folders = null;
		if (Constants.SYSTEM_FOLDER_ID.equals(ownerId)) {
			folders = protocolFactory.getConnectionMetaHandler().getSystemFolders();
		} else {
			folders = protocolFactory.getConnectionMetaHandler().getOtherFolders();
		}
		if (folders != null) {
			Set<Map.Entry<Long, Folder>> entrys = folders.entrySet();
			for (Iterator<Map.Entry<Long, Folder>> it = entrys.iterator(); it.hasNext();) {
				Map.Entry<Long, Folder> entry = it.next();
				IMAPFolder folder = (IMAPFolder) entry.getValue();
				if (folder.getFullName().equals(folderName)) {
					return EmailFolder.valueOf(folder);
				}
			}
		}
		return null;
	}

	public DataPackage<EmailFolder> getPersonalEmailFolders(EmailUser user, ParamsTable params) throws Exception {
		Map<Long, Folder> personalFolders = protocolFactory.getConnectionMetaHandler().getOtherFolders();
		Collection<EmailFolder> result = new ArrayList<EmailFolder>();
		Set<Map.Entry<Long, Folder>> entrys = personalFolders.entrySet();
		for (Iterator<Map.Entry<Long, Folder>> it = entrys.iterator(); it.hasNext();) {
			Map.Entry<Long, Folder> entry = it.next();
			IMAPFolder folder = (IMAPFolder) entry.getValue();
			EmailFolder ef = EmailFolder.valueOf(folder);
			if (ef != null) {
				result.add(ef);
			}
		}
		DataPackage<EmailFolder> dp = new DataPackage<EmailFolder>();
		dp.datas = result;
		dp.setPageNo(1);
		dp.setLinesPerPage(Integer.MAX_VALUE);
		dp.setRowCount(result.size());
		return dp;
	}

	public Collection<EmailFolder> getSystemEmailFolders() throws Exception {
		Map<Long, Folder> systemFolders = protocolFactory.getConnectionMetaHandler().getSystemFolders();
		Collection<EmailFolder> result = new ArrayList<EmailFolder>();
		Set<Map.Entry<Long, Folder>> entrys = systemFolders.entrySet();
		for (Iterator<Map.Entry<Long, Folder>> it = entrys.iterator(); it.hasNext();) {
			Map.Entry<Long, Folder> entry = it.next();
			IMAPFolder folder = (IMAPFolder) entry.getValue();
			EmailFolder ef = EmailFolder.valueOf(folder);
			if (ef != null) {
				result.add(ef);
			}
		}
		return result;
	}

	public void initCreatDefaultMailFolder() throws Exception {
		ImapProtocolImpl protocol = null;
		boolean isUpdate = false;
		protocol = (ImapProtocolImpl) protocolFactory.getImapProtocol(Constants.DEFAULT_FOLDER_DRAFTS);
		isUpdate = createEmailFolderByName(protocol);
		protocol = (ImapProtocolImpl) protocolFactory.getImapProtocol(Constants.DEFAULT_FOLDER_JUNK);
		if (!isUpdate) {
			isUpdate = createEmailFolderByName(protocol);
		} else {
			createEmailFolderByName(protocol);
		}
		protocol = (ImapProtocolImpl) protocolFactory.getImapProtocol(Constants.DEFAULT_FOLDER_SENT);
		if (!isUpdate) {
			isUpdate = createEmailFolderByName(protocol);
		} else {
			createEmailFolderByName(protocol);
		}
		protocol = (ImapProtocolImpl) protocolFactory.getImapProtocol(Constants.DEFAULT_FOLDER_REMOVED);
		if (!isUpdate) {
			isUpdate = createEmailFolderByName(protocol);
		} else {
			createEmailFolderByName(protocol);
		}
		if (protocol != null && isUpdate) {
			protocol.updateFolders();
		}
	}

	public boolean emailFolderIsCreate(String folderName, String ownerid) throws Exception {
		ImapProtocolImpl protocol = (ImapProtocolImpl) protocolFactory.getImapProtocol(folderName);
		protocol.connect(Constants.CONNECT_TYPE_READ_ONLY);
		Folder folder = protocol.getFolder();
		return !(folder == null || !folder.exists());
	}

	public static void main(String[] args) throws Exception {
		// ConnectionProfile profile = EmailConfig.getConnectionProfile();
		// AuthProfile auth = new AuthProfile();
		// ConnectionMetaHandler handler = null;
		// auth.setUserName("");
		// auth.setPassword("");
		// ProtocolFactory factory = new ProtocolFactory(profile, auth,
		// handler);
		// EmailFolderProcessImapBean bean = new
		// EmailFolderProcessImapBean(factory);
		// EmailFolder emailFolder =
		// bean.getEmailFolderByOwnerId(Constants.DEFAULT_FOLDER_INBOX,
		// Constants.SYSTEM_FOLDER_ID);
		// //System.out.println(emailFolder);
		// Collection folders = bean.getSystemEmailFolders();
		// for (Iterator it = folders.iterator(); it.hasNext(); ) {
		// EmailFolder folder = (EmailFolder) it.next();
		// System.out.println(folder);
		// }
	}

	public EmailFolder getSystemEmailFolderByName(String folderName) throws Exception {
		return getEmailFolderByOwnerId(folderName, Constants.SYSTEM_FOLDER_ID);
	}

	@Override
	public void doCreate(ValueObject vo) throws Exception {
		EmailFolder folder = (EmailFolder) vo;
		ImapProtocolImpl protocol = (ImapProtocolImpl) protocolFactory.getImapProtocol(folder.getName());
		protocol.connect(Constants.CONNECT_TYPE_READ_ONLY);
		protocol.createFolder();
		protocol.updateFolders();
	}

	@Override
	public void doUpdate(ValueObject vo) throws Exception {
		EmailFolder folder = (EmailFolder) vo;
		if (StringUtil.isBlank(folder.getId())) {
			throw new Exception("{*[core.email.folder.create.error]*}");
		}
		IMAPFolder oldFolder = protocolFactory.getConnectionMetaHandler().getFolderByUID(Long.valueOf(folder.getId()));
		if (oldFolder == null) {
			throw new Exception("{*[core.email.folder.create.error]*}");
		}
		ImapProtocolImpl protocol = (ImapProtocolImpl) protocolFactory.getImapProtocol(oldFolder.getName());
		protocol.connect(Constants.CONNECT_TYPE_READ_ONLY);
		protocol.renameTo(folder.getName());
		protocol.updateFolders();
	}

	public void doRemoveEmailFolder(EmailFolder folder, EmailUser user) throws Exception {
		if (StringUtil.isBlank(folder.getId())) {
			throw new Exception("{*[core.email.folder.delete.error]*}");
		}
		IMAPFolder oldFolder = protocolFactory.getConnectionMetaHandler().getFolderByUID(Long.valueOf(folder.getId()));
		if (oldFolder == null) {
			throw new Exception("{*[core.email.folder.delete.error]*}");
		}
		ImapProtocolImpl protocol = (ImapProtocolImpl) protocolFactory.getImapProtocol(oldFolder.getName());
		protocol.connect(Constants.CONNECT_TYPE_READ_ONLY);
		if (protocol.getTotalMessageCount() > 0) {
			throw new Exception("{*[core.email.folder.deleted]*}");
		}
		protocol.deleteFolder(true);
	}

	public int getPersonalEmailFolderCount(String ownerid) throws Exception {
		Map<Long, Folder>  folders = protocolFactory.getConnectionMetaHandler().getOtherFolders();
		if (folders != null) {
			return folders.size();
		}
		return 0;
	}

}
