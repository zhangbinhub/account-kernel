package OLink.bpm.core.email.runtime.mail;

import java.security.Security;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;

import OLink.bpm.core.email.runtime.model.Email;
import OLink.bpm.core.email.util.Utility;
import org.apache.log4j.Logger;

import OLink.bpm.core.email.runtime.model.EmailHeader;
import OLink.bpm.core.email.util.Constants;

import com.sun.mail.imap.AppendUID;
import com.sun.mail.imap.IMAPFolder;

public class ImapProtocolImpl implements Protocol {

	private static Logger log = Logger.getLogger(ImapProtocolImpl.class);
	private ConnectionMetaHandler handler;
	private String folderName;
	private ConnectionProfile profile;
	private AuthProfile auth;
	
	public ImapProtocolImpl(ConnectionProfile profile, String folderName,
			AuthProfile auth, ConnectionMetaHandler handler) {
		this.profile = profile;
		this.folderName = folderName;
		this.auth = auth;
		this.handler = handler;
	}
	
	public ConnectionMetaHandler connect(int connectType) throws Exception {
		return this.connect(connectType, false);
	}
	
	public ConnectionMetaHandler connect(int connectType, boolean debug) throws Exception {
		try {
			Properties props = System.getProperties();
			if (handler == null || handler.getStore() == null || !handler.getStore().isConnected()) {
				if (log.isDebugEnabled()) {
					props.setProperty("mail.debug", "true");
					System.setProperty("javax.net.debug", "all");
				}
				if (profile.getFetchSSL() != null && profile.getFetchSSL().toLowerCase().equals("true")) {
					Security.addProvider( new com.sun.net.ssl.internal.ssl.Provider());
					
					Security.setProperty("ssl.SocketFactory.provider", "OBPMSSLSocketFactory");
					//Security.setProperty("ssl.SocketFactory.provider", "javax.net.ssl.SSLSocketFactory");
					props.setProperty("mail.store.protocol", "imap");
					props.setProperty("mail.imap.host", profile.getFetchServer());
					props.setProperty("mail.imap.port", profile.getFetchPort());
				      
					props.setProperty("mail.imap.socketFactory.class", "OBPMSSLSocketFactory");
					//props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
					props.setProperty("mail.imap.socketFactory.fallback", "false");
					//props.setProperty("mail.imap.port", profile.getFetchPort());
					props.setProperty("mail.imap.socketFactory.port", profile.getFetchPort());
				} else {
					//Security.addProvider(null);
					Security.removeProvider("ssl.SocketFactory.provider");
					props.remove("mail.imap.socketFactory.class");
					props.remove("mail.imap.socketFactory.fallback");
				}
				props.setProperty("mail.imap.auth.plain.disable", "true");
				Session session = Session.getDefaultInstance(props);
				session.setDebug(debug);
				log.debug("session instance initiated");
				if (handler == null) {
					handler = new ConnectionMetaHandler();
				}
				handler.setStore(session.getStore(profile.getProtocol()));
				log.debug("session store set. protocol is: " + profile.getProtocol());
				handler.getStore().connect(profile.getFetchServer(), profile.getIFetchPort(), auth.getUserName(), auth.getPassword());
				if (handler.getStore().isConnected()) {
					log.debug("Store has been connected... Successful");
				} else {
					log.warn("Connection unsuccessfull...!!");
				}
				handler.initFolders();
			}
			Folder folder = handler.getStore().getFolder(folderName);
			handler.setFolder(folder);
			log.debug("Got mailbox folder. Folder is: " + folder.getFullName());
			
			Map<String, Folder> imapUserFolders = FolderCache.getUserFolderMap(auth);
			imapUserFolders.put(folderName, folder);
			FolderCache.putUserFolderMap(auth, imapUserFolders);
		} catch (Exception e) {
			log.error(e.toString());
			throw e;
		}
		return handler;
	}

	public ConnectionMetaHandler deleteMessages(int[] messageIds)
			throws Exception {
		Folder folder = null;
		try {
			folder = getFolder();
			if (messageIds != null && messageIds.length > 0) {
				for (int i=0;i<messageIds.length;i++) {
					try {
						if (messageIds[i] > 0) {
							Message msg = folder.getMessage(messageIds[i]);
							msg.setFlag(Flags.Flag.DELETED, true);
						}
					} catch (Exception e) {
						log.debug("error while deleting messsage", e);
					}
				}
				folder.expunge();
			}
		} catch (Exception e) {
			throw e;
		}
		return handler;
	}

	public void disconnect() throws Exception {
		try {
			Map<String, Folder> imapUserFolders = FolderCache.getUserFolderMap(auth);
			Iterator<String> iter = imapUserFolders.keySet().iterator();
			Folder tmp = null;
			while (iter.hasNext()) {
				try {
					tmp = imapUserFolders.get(iter.next());
					closeFolder(tmp);
					tmp = null;
				} catch (Throwable e) {
					log.debug("Unable to close folder:" + tmp);
				}
			}
			FolderCache.removeUserFolderMap(auth);
		} catch (Throwable e1) {
			e1.printStackTrace();
		}
		
		try {
			//handler.closeFolder(true);
			//handler.closeStore();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updateFolders() throws Exception {
		if (handler != null) {
			handler.initFolders();
		}
	}
	
	public void closeFolder(Folder folder) {
		if (folder != null) {
			try {
				if (folder.isOpen()) {
					folder.close(true);
					log.info("Folder: " + folder.getName() + " was open and now closed.");

					Map<String, Folder> imapUserFolders = FolderCache.getUserFolderMap(auth);
					imapUserFolders.put(folder.getName(), null);
					FolderCache.putUserFolderMap(auth, imapUserFolders);
				} else {
					log.info("Folder: " + folder.getName() + " was already closed.");
				}
			} catch (MessagingException e) {
				log.info("Error while closing folder: " + folder.getName(), e);
			}
		}
	}

	public void emptyFolder() throws Exception {
		Folder folder = getFolder();
		try {
			Message msgs[] = folder.getMessages();
			FetchProfile fp = new FetchProfile();
			fp.add(FetchProfile.Item.ENVELOPE);
			folder.fetch(msgs, fp);
			
			int ids[] = new int[msgs.length];
			for (int i=0; i<msgs.length; i++) {
				ids[i] = msgs[i].getMessageNumber();
			}
			if (ids.length > 0) {
				flagAsDeleted(ids);
				// deleteMessages(ids);
			}
		} catch (Exception e) {
			log.warn("Could not delete all messages in folder: " + folderName);
		}
	}
	
	public void renameFolder(String newName) throws Exception {
		Folder folder = getFolder();
		Folder fNew = handler.getStore().getFolder(profile.getFolderNameSpace() + newName);
		closeFolder(folder);
		folder.renameTo(fNew);
		fNew.setSubscribed(true);
	}

	public Message getMessage(int messageId) throws Exception {
		Message message = null;
		Folder folder = null;
		try {
			try {
				folder = getFolder();
				message = folder.getMessage(messageId);
			} catch (MessagingException e) {
				log.error("Could not fetch message body from remote server.", e);
				throw e;
			}
		} catch (Exception e) {
			throw e;
		}
		return message;
	}
	
	public Message getMessageByUID(long uid) throws Exception {
		Message message = null;
		IMAPFolder folder = null;
		try {
			try {
				folder = (IMAPFolder) getFolder();
				message = folder.getMessageByUID(uid);
			} catch (MessagingException e) {
				log.error("Could not fetch message body from remote server.", e);
				throw e;
			}
		} catch (Exception e) {
			throw e;
		}
		return message;
	}

	public Message[] getMessages(int stratid, int endid) throws Exception {
		Message[] messages = null;
		try {
			try {
				Folder folder = getFolder();
				messages = folder.getMessages(stratid, endid);
			} catch (MessagingException e) {
				log.error("Could not fetch message body from remote server.", e);
				throw e;
			}
		} catch (Exception e) {
			throw e;
		}
		return messages;
	}
	
	public Message[] getMessagesByUID(long start, long end) throws Exception {
		Message[] messages = null;
		try {
			try {
				IMAPFolder folder = (IMAPFolder) getFolder();
				messages = folder.getMessagesByUID(start, end);
			} catch (MessagingException e) {
				log.error("Could not fetch message body from remote server.", e);
				throw e;
			}
		} catch (Exception e) {
			throw e;
		}
		return messages;
	}
	
	public Message[] getMessagesByUID(long[] uids) throws Exception {
		Message[] messages = null;
		try {
			try {
				IMAPFolder folder = (IMAPFolder) getFolder();
				messages = folder.getMessagesByUID(uids);
			} catch (MessagingException e) {
				log.error("Could not fetch message body from remote server.", e);
				throw e;
			}
		} catch (Exception e) {
			throw e;
		}
		return messages;
	}

	public int getTotalMessageCount() throws Exception {
		Folder folder = getFolder();
		if (folder.exists()) {
			return folder.getMessageCount();
		}
		return 0;
	}

	public int getUnreadMessageCount() throws Exception {
		Folder folder = getFolder();
		if (folder.exists()) {
			return folder.getUnreadMessageCount();
		}
		return 0;
	}
	
	public void markAsReadByUID(long uid) throws Exception {
		IMAPFolder folder = (IMAPFolder) getFolder();
		try {
			Message msg = folder.getMessageByUID(uid);
			msg.setFlag(Flags.Flag.SEEN, true);
		} catch (MessagingException e) {
			log.warn("Marking as Read not worked.", e);
		}
	}
	
	public void markAsReadByUID(long uid, boolean read) throws Exception {
		IMAPFolder folder = (IMAPFolder) getFolder();
		try {
			Message msg = folder.getMessageByUID(uid);
			msg.setFlag(Flags.Flag.SEEN, read);
		} catch (MessagingException e) {
			log.warn("Marking as Read not worked.", e);
		}
	}
	
	public Folder getImapFolder(boolean useCache) throws Exception {
		Folder myFolder = null;
		if (isBlank(folderName)) {
			folderName = Constants.DEFAULT_FOLDER_INBOX;
		}
		if (handler != null) {
			Store store = handler.getStore();
			if (store == null || !store.isConnected()) {
				log.debug("Connection is closed. Restoring it...");
				handler = connect(Folder.READ_WRITE);
				log.debug("Connection re-established");
			}
			
			Map<String, Folder> imapUserFolders = null;
			if (useCache) {
				imapUserFolders = FolderCache.getUserFolderMap(auth);
				myFolder = imapUserFolders.get(folderName);
			}
			if (myFolder == null) {
				myFolder = handler.getStore().getFolder(folderName);
			}
			if (!myFolder.isOpen()) {
				try {
					log.debug("Folder :" + folderName + " is closed. Opening.");
					myFolder.open(Folder.READ_WRITE);
					log.debug("Folder is open.");
				} catch (Throwable e) {
					log.debug("nevermind go on");
				}
			}
			if (useCache) {
				try {
					imapUserFolders.put(folderName, myFolder);
					FolderCache.putUserFolderMap(auth, imapUserFolders);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return myFolder;
	}
	
	public Folder getFolder() throws Exception {
		return getImapFolder(true);
	}
	
	public void createFolder() throws Exception {
		Folder folder = getFolder();
		try {
			if (!folder.exists()) {
				folder.create(Folder.HOLDS_MESSAGES);
				folder.setSubscribed(true);
			} else {
				if (!folder.isSubscribed()) {
					folder.setSubscribed(true);
				}
			}
		} catch (MessagingException e) {
			if (folder != null) {
				log.warn("Could not create folder: " + folder.getName());
			} else {
				throw e;
			}
		}
	}
	
	public void renameTo(String folderName) throws Exception {
		if (isBlank(folderName)) {
			throw new Exception("文件夹名称不能为空!");
		}
		//handler.closeFolder(true);
		handler.getFolder().renameTo(handler.getStore().getFolder(folderName));
	}
	
	public void deleteFolder(boolean flag) throws Exception {
		long folderid = ((IMAPFolder)handler.getFolder()).getUIDValidity();
		handler.closeFolder(true);
		handler.getFolder().delete(flag);
		handler.removeFolder(folderid);
	}

	public void flagAsDeleted(int[] ids) throws Exception {
		Folder folder = null;
		try {
			folder = getFolder();
			if (ids != null && ids.length > 0) {
				for (int i=0;i<ids.length;i++) {
					try {
						if (ids[i] > 0) {
							Message msg = folder.getMessage(ids[i]);
							msg.setFlag(Flags.Flag.SEEN, true);
							msg.setFlag(Flags.Flag.DELETED, true);
						}
					} catch (Exception e) {
						log.debug("error while deleting messsage", e);
					}
				}
			}
		} catch (Exception e) {
			log.error("Could not delete message ids!", e);
			throw e;
		}
	}
	
	public void deletedMessageByUID(long[] uids) throws Exception {
		IMAPFolder folder = null;
		try {
			folder = (IMAPFolder) getFolder();
			if (uids != null && uids.length > 0) {
				for (int i=0; i<uids.length; i++) {
					try {
						if (uids[i] > 0) {
							Message msg = folder.getMessageByUID(uids[i]);
							msg.setFlag(Flags.Flag.SEEN, true);
							msg.setFlag(Flags.Flag.DELETED, true);
						}
					} catch (Exception e) {
						log.debug("error while deleting messsage", e);
					}
				}
			}
		} catch (Exception e) {
			log.error("Could not delete message ids!", e);
			throw e;
		}
	}
	
	public Folder[] listFolders() throws Exception {
		ProtocolFactory factory = new ProtocolFactory(profile, auth, handler);
		ImapProtocolImpl protocol = (ImapProtocolImpl)factory.getImapProtocol(folderName);
		Folder folder = protocol.getFolder();
		Folder parent = null;
		Folder[] folders = null;
		try {
			parent = folder.getParent();
			folders = parent.list();
		} catch (MessagingException e) {
			log.warn("Cannot get folder list.");
		} finally {
			// closeFolder(parent);
		}
		return folders;
	}
	
	public void moveMessageByUID(long uid, String destFolder) throws Exception {
		ProtocolFactory factory = new ProtocolFactory(profile, auth, handler);
		ImapProtocolImpl fromProtocol = (ImapProtocolImpl)factory.getImapProtocol(folderName);
		ImapProtocolImpl destProtocol = (ImapProtocolImpl)factory.getImapProtocol(destFolder);
		Folder from = fromProtocol.getFolder();
		Folder dest = null;

		try {
			Message msg = fromProtocol.getMessageByUID(uid);
			if (msg != null) {
				from = fromProtocol.getFolder();
				dest = destProtocol.getFolder();
				from.copyMessages(new Message[] {msg}, dest);
				// deleteMessages(new int[] {msg.getMessageNumber()});
				flagAsDeleted(new int[] {msg.getMessageNumber()});
			}
		} catch (IndexOutOfBoundsException e) {
			log.debug("Index kaçtı. Moving message to folder : " + destFolder + " failed.", e);
		} catch (Exception e) {
			log.warn("Moving message to folder : " + destFolder + " failed.", e);
		}
	}

	/**
	 * 
	 * @param messageIds
	 * @param destFolder
	 * @throws Exception
	 */
	public void moveMessagesByUID(long uids[], String destFolder) throws Exception {
		ProtocolFactory factory = new ProtocolFactory(profile, auth, handler);
		ImapProtocolImpl fromProtocol = (ImapProtocolImpl)factory.getImapProtocol(folderName);
		ImapProtocolImpl destProtocol = (ImapProtocolImpl)factory.getImapProtocol(destFolder);
		Folder from = fromProtocol.getFolder();
		Folder dest = null;

		try {
			Message msg = null;
			
			int counter = 0;
			dest = destProtocol.getFolder();
			Message msgs[] = new MimeMessage[uids.length];
			int[] nums = new int[uids.length];
			// copy messages to destination folder first
			for (int i=0; i < uids.length; i++) {
				try {
					msg = fromProtocol.getMessageByUID(uids[i]);
					if (msg != null) {
						msgs[counter] = msg;
						nums[counter] = msg.getMessageNumber();
						counter++;
					}
				} catch (Exception e) {
					log.debug("error while copying messages", e);
				}
			}
			
			from.copyMessages(msgs, dest);
			// now delete the processed messages all at a time.
			// deleteMessages(messageIds);
			flagAsDeleted(nums);
		} catch (IndexOutOfBoundsException e) {
			log.debug("Index kaçtı. Moving message to folder : " + destFolder + " failed.", e);
		} catch (Exception e) {
			log.warn("Moving message to folder : " + destFolder + " failed.", e);
		}
	}

	/**
	 * 
	 * @param messageIds
	 * @param destFolders
	 * @throws Exception
	 */
	public void moveMessagesByUID(long uids[], String destFolders[]) throws Exception {
		ProtocolFactory factory = new ProtocolFactory(profile, auth, handler);
		ImapProtocolImpl fromProtocol = (ImapProtocolImpl)factory.getImapProtocol(folderName);
		Folder from = fromProtocol.getFolder();
		Folder dest = null;

		try {
			Message msg = null;
			// copy messages to destination folder first
			int[] nums = new int[uids.length];
			for (int i = 0; i < uids.length; i++) {
				try {
					msg = fromProtocol.getMessageByUID(uids[i]);
					nums[i] = msg.getMessageNumber();
					ImapProtocolImpl destProtocol = (ImapProtocolImpl)factory.getImapProtocol(destFolders[i]);
					dest = destProtocol.getFolder();
					from.copyMessages(new Message[] {msg}, dest);
				} catch (Exception e) {
					log.debug("error while copying messages", e);
				}
			}
			
			// now delete the processed messages all at a time.
			// deleteMessages(messageIds);
			flagAsDeleted(nums);
			
		} catch (Exception e) {
			log.warn("Moving message failed.", e);
		}
	}
	
	protected boolean isBlank(String str) {
		return str == null || str.trim().length() == 0;
	}

	public List<EmailHeader> fetchAllHeaders() throws Exception {
		return fetchHeaders(null);
	}

	public List<Message> fetchAllHeadersAsMessages() throws Exception {
		ArrayList<Message> headers = null;
		Folder folder = null;
		try {
			headers = new ArrayList<Message>();
			folder = getFolder();
			Message[] msgs = folder.getMessages();
			FetchProfile fp = new FetchProfile();
			fp.add(FetchProfile.Item.ENVELOPE);
			fp.add(FetchProfile.Item.FLAGS);
			fp.add(FetchProfile.Item.CONTENT_INFO);
			fp.add("Size");
			fp.add("Date");
			folder.fetch(msgs, fp);

			Message msg = null;
			for (int i = 0; i < msgs.length; i++) {
				try {
					msg = msgs[i];

					boolean deleted = false;
					Flags.Flag flags[] = msg.getFlags().getSystemFlags();
					if (flags != null) {
						Flags.Flag flag = null;
						for (int m=0; m < flags.length; m++) {
							flag = flags[m];
							if (flag.equals(Flags.Flag.DELETED)) {
								deleted = true;
							}
						}
					}
					if (!deleted) {
						headers.add(msg);
					}
				} catch (Exception e) {
					log.debug("probably an error fetching list", e);
				}
			}
		} catch (MessagingException e) {
			log.error("Could not fetch message headers. Is mbox connection still alive???", e);
			throw e;
		} catch (Exception e) {
			log.error("Could not fetch message headers. Is mbox connection still alive???", e);
			throw e;
		}
		return headers;
	}

	public List<EmailHeader> fetchHeaders(int[] indexs) throws Exception {
		ArrayList<EmailHeader> headers = new ArrayList<EmailHeader>();
		IMAPFolder folder = null;
		try {
			folder = (IMAPFolder) getFolder();
			EmailHeader header = null;

			Message[] msgs = null;
			if (indexs == null) {
				msgs = folder.getMessages();
			} else {
				msgs = folder.getMessages(indexs);
			}
			FetchProfile fp = new FetchProfile();
			fp.add(FetchProfile.Item.ENVELOPE);
			fp.add(FetchProfile.Item.FLAGS);
			fp.add(FetchProfile.Item.CONTENT_INFO);
			fp.add("Size");
			fp.add("Date");
			fp.add("Disposition-Notification-To");
			fp.add("X-Priority");
			fp.add("X-MSMail-Priority");
			fp.add("Sensitivity");
			folder.fetch(msgs, fp);

			Message msg = null;
			for (int i = 0; i < msgs.length; i++) {
				try {
					header = new EmailHeader();
					msg = msgs[i];
					header.setEmailUID(folder.getUID(msg));
					header.setMultipart((msg.isMimeType("multipart/*")) ? true : false);
					header.setNums(msgs[i].getMessageNumber());
					header.setFrom(msg.getFrom());
					header.setFolderid(folder.getUIDValidity());
					header.setTo(msg.getRecipients(Message.RecipientType.TO));
					header.setCc(msg.getRecipients(Message.RecipientType.CC));
					header.setBcc(msg.getRecipients(Message.RecipientType.BCC));
					header.setDate(msg.getSentDate());
					header.setReplyTo(msg.getReplyTo());
					header.setSize(msg.getSize());
					header.setSubject(Utility.decodeText(msg.getSubject()));
					//header.setSubject(Utility.updateTRChars(msg.getSubject()));
                    
					// now set the human readables.
					header.setDateString(Utility.getDateToString(header.getDate()));
                    
					header.setFromString(Utility.updateTRChars(Utility.addressArrToStringShort(header.getFrom())));
					header.setToString(Utility.addressArrToStringShort(header.getTo()));
					header.setCcString(Utility.addressArrToStringShort(header.getCc()));
					header.setSizeString(Utility.sizeToHumanReadable(header.getSize()));
					
					boolean deleted = false;
					if (profile.getProtocol().equals("imap")) {
						Flags.Flag flags[] = msg.getFlags().getSystemFlags();
						if (flags != null) {
							Flags.Flag flag = null;
							for (int m=0; m < flags.length; m++) {
								flag = flags[m];
								if (flag.equals(Flags.Flag.SEEN)) {
									header.setUnread(Boolean.valueOf(false));
								}
								
								if (flag.equals(Flags.Flag.DELETED)) {
									deleted = true;
								}
							}
						}
					}
					if (header.getUnread() == null) {
						header.setUnread(Boolean.valueOf(true));
					}
                    
					// it is time to add it to the arraylist
					if (!deleted) {
						headers.add(header);
					}
				} catch (MessagingException e1) {
					log.error("Could not parse headers of e-mail. Message might be defuncted or illegal formatted.", e1);
				}
			}
		} catch (MessagingException e) {
			log.error("Could not fetch message headers. Is mbox connection still alive???", e);
		} catch (Exception e) {
			log.error("Could not fetch message headers. Is mbox connection still alive???", e);
		}
		return headers;
	}
	
	/**
	 * 获取邮件头信息
	 * @param page 第n页
	 * @param lines 每页共n行
	 * @param sort 按邮件时间排序（0-降序 1-升序）
	 * @return
	 * @throws Exception
	 */
	public List<EmailHeader> fetchHeaders(int page, int lines, int sort) throws Exception {
		IMAPFolder folder = (IMAPFolder) getFolder();
		int startCount = (page - 1) * lines == 0 ? 1 :  (page - 1) * lines + 1;
		int endCount = page * lines;
		if (startCount > folder.getMessageCount()
				&& endCount > folder.getMessageCount()) {
			return new ArrayList<EmailHeader>();
		}
		if (startCount > folder.getMessageCount()) {
			startCount = folder.getMessageCount();
		}
		if (endCount > folder.getMessageCount()) {
			endCount = folder.getMessageCount();
		}
		int[] nums = new int[endCount - startCount + 1];
		if (sort != 1) {
			startCount = folder.getMessageCount() - endCount + 1;
			//endCount = folder.getMessageCount() - startCount;
		}
		for (int i = 0; i < nums.length; i++) {
			if (sort != 1) {
				nums[i] = startCount + nums.length - 1 - i;
			} else {
				nums[i] = startCount + i;
			}
		}
		return fetchHeaders(nums);
	}

	public List<EmailHeader> getHeadersSortList(String sortCriteriaRaw,
			String sortDirectionRaw) throws Exception {
		return null;
	}

	/**
	 * @return the folderName
	 */
	public String getFolderName() {
		return folderName;
	}
	
	public Email appendMessage(Email email) throws Exception {
		Message message = email.toMessage(null);
		IMAPFolder folder = (IMAPFolder) getFolder();
//		Message[] messages = folder.addMessages(new Message[]{message});
//		if (messages == null || messages.length == 0) {
//			return message;
//		}
		AppendUID[] uids = folder.appendUIDMessages(new Message[]{message});
		if (uids != null && uids.length > 0) {
			email.setUid(uids[0].uid);
			//if (bool) {
			//	IMAPFolder sent = (IMAPFolder) handler.getStore().getFolder(Constants.DEFAULT_FOLDER_SENT);
			//	folder.copyMessages(new Message[]{getMessageByUID(uids[0].uid)}, sent);
			//}
		}
		//return messages[0];
		return email;
	}

}
