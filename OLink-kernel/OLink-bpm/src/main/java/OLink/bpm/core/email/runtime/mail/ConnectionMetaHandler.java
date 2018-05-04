package OLink.bpm.core.email.runtime.mail;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Store;

import OLink.bpm.core.email.util.Constants;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

public class ConnectionMetaHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5420747046218417670L;
	private Folder folder;
	private Store store;
	private Map<Long, Folder> systemFolders;
	private Map<Long, Folder> otherFolders;

	public ConnectionMetaHandler() {
		systemFolders = new HashMap<Long, Folder>();
		otherFolders = new HashMap<Long, Folder>();
	}

	/**
	 * @return the folder
	 */
	public Folder getFolder() {
		return folder;
	}

	/**
	 * @param folder
	 *            the folder to set
	 */
	public void setFolder(Folder folder) {
		this.folder = folder;
	}

	/**
	 * @return the store
	 */
	public Store getStore() {
		return store;
	}

	/**
	 * @param store
	 *            the store to set
	 */
	public void setStore(Store store) {
		this.store = store;
	}

	public void closeFolder(boolean flag) throws MessagingException {
		if (folder != null && folder.isOpen()) {
			folder.close(flag);
		}
	}

	public void closeStore() throws MessagingException {
		if (store != null && store.isConnected()) {
			store.close();
		}
		if (systemFolders != null) {
			systemFolders.clear();
		}
		if (otherFolders != null) {
			otherFolders.clear();
		}
	}

	protected void initFolders() throws Exception {
		if (store instanceof IMAPStore) {
			IMAPFolder[] imapFolders = (IMAPFolder[]) store.getDefaultFolder().list();
			for (int i = 0; i < imapFolders.length; i++) {
				IMAPFolder folder = imapFolders[i];
				String name = folder.getFullName();
				if (Constants.isSystemFolder(name)) {
					systemFolders.put(folder.getUIDValidity(), folder);
				} else {
					otherFolders.put(folder.getUIDValidity(), folder);
				}
			}
		} else {
			throw new Exception("该方法只使用于IMAP协议");
		}
	}

	public IMAPFolder getFolderByUID(long uid) {
		IMAPFolder folder = (IMAPFolder) systemFolders.get(uid);
		if (folder == null) {
			folder = (IMAPFolder) otherFolders.get(uid);
		}
		return folder;
	}

	public void removeFolder(long key) {
		otherFolders.remove(key);
	}

	public Map<Long, Folder> getSystemFolders() throws Exception {
		return systemFolders;
	}

	public Map<Long, Folder> getOtherFolders() throws Exception {
		return otherFolders;
	}
	
	public void removeOtherFolder() {
		if (folder instanceof IMAPFolder) {
			try {
				otherFolders.remove(((IMAPFolder)folder).getUIDValidity());
			} catch (MessagingException e) {
				
			}
		}
	}
	
	public IMAPFolder getFolderByName(String name, String ownerid) {
		Map<Long, Folder> folders = null;
		if (Constants.SYSTEM_FOLDER_ID.equals(ownerid)) {
			folders = systemFolders;
		} else {
			folders = otherFolders;
		}
		if (folders != null) {
			Set<Map.Entry<Long, Folder>> entrys = folders.entrySet();
			for (Iterator<Map.Entry<Long, Folder>> it = entrys.iterator(); it
					.hasNext();) {
				Map.Entry<Long, Folder> entry = it.next();
				IMAPFolder folder = (IMAPFolder) entry.getValue();
				if (folder.getFullName().equals(name)) {
					return folder;
				}
			}
		}
		return null;
	}

}
