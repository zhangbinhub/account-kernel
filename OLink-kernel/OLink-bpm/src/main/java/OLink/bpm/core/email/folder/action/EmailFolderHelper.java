package OLink.bpm.core.email.folder.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.email.folder.ejb.EmailFolder;
import OLink.bpm.core.email.folder.ejb.EmailFolderProcess;
import OLink.bpm.core.email.util.Constants;
import OLink.bpm.core.email.util.EmailProcessUtil;
import OLink.bpm.core.user.action.WebUser;
import org.apache.log4j.Logger;

import OLink.bpm.base.action.ParamsTable;

public class EmailFolderHelper {
	
	private static final Logger LOG = Logger.getLogger(EmailFolderHelper.class);
	
	public static String getFolderId(String folderName, String ownerid, WebUser webUser) {
		try {
			EmailFolderProcess process = (EmailFolderProcess) EmailProcessUtil.createProcess(EmailFolderProcess.class, webUser);
			EmailFolder folder = process.getEmailFolderByOwnerId(folderName, ownerid);
			if (folder != null) {
				return folder.getId();
			}
		} catch (Exception e) {
			LOG.warn(e.toString());
		}
		return "";
	}
	
	public static EmailFolder getSystemFolderByName(String folderName, WebUser webUser) {
		try {
			EmailFolderProcess process = (EmailFolderProcess) EmailProcessUtil.createProcess(EmailFolderProcess.class, webUser);
			EmailFolder folder = process.getEmailFolderByOwnerId(folderName, Constants.SYSTEM_FOLDER_ID);
			return folder;
		} catch (Exception e) {
			LOG.warn(e.toString());
		}
		return null;
	}
	
	public static Collection<EmailFolder> getSystemFolders(WebUser webUser) {
		Collection<EmailFolder> systemFolders = null;
		try {
			EmailFolderProcess process = (EmailFolderProcess) EmailProcessUtil.createProcess(EmailFolderProcess.class, webUser);
			systemFolders = process.getSystemEmailFolders();
		} catch (Exception e) {
			LOG.warn(e.toString());
		}
		return systemFolders == null ? new ArrayList<EmailFolder>() : systemFolders;
	}
	
	public static Collection<EmailFolder> getPersonalEmailFolders(WebUser webUser) {
		try {
			EmailFolderProcess process = (EmailFolderProcess) EmailProcessUtil.createProcess(EmailFolderProcess.class, webUser);
			ParamsTable params = new ParamsTable();
			//params.setParameter("_pagelines", Integer.MAX_VALUE);
			DataPackage<EmailFolder> dp = process.getPersonalEmailFolders(webUser.getEmailUser(), params);
			if (dp != null) {
				return dp.datas;
			}
		} catch (Exception e) {
			LOG.warn(e.toString());
		}
		return null;
	}
	
	public static boolean havePersonalEmailFolder(WebUser webUser) {
		try {
			EmailFolderProcess process = (EmailFolderProcess) EmailProcessUtil.createProcess(EmailFolderProcess.class, webUser);
			int count = process.getPersonalEmailFolderCount(webUser.getEmailUser().getId());
			if (count > 0) {
				return true;
			}
		} catch (Exception e) {
			LOG.warn(e.getMessage());
		}
		return false;
	}
	
	public static String getInboxEmailFolderId(WebUser webUser) {
		EmailFolder folder = getSystemFolderByName(Constants.DEFAULT_FOLDER_INBOX, webUser);
		if (folder != null) {
			return folder.getId();
		}
		return "";
	}
	
	public static String getDraftsEmailFolderId(WebUser webUser) {
		EmailFolder folder = getSystemFolderByName(Constants.DEFAULT_FOLDER_DRAFTS, webUser);
		if (folder != null) {
			return folder.getId();
		}
		return "";
	}
	
	public static String getSentEmailFolderId(WebUser webUser) {
		EmailFolder folder = getSystemFolderByName(Constants.DEFAULT_FOLDER_SENT, webUser);
		if (folder != null) {
			return folder.getId();
		}
		return "";
	}
	
	public static String getRemovedEmailFolderId(WebUser webUser) {
		EmailFolder folder = getSystemFolderByName(Constants.DEFAULT_FOLDER_REMOVED, webUser);
		if (folder != null) {
			return folder.getId();
		}
		return "";
	}
	
	public static String getJunkEmailFolderId(WebUser webUser) {
		EmailFolder folder = getSystemFolderByName(Constants.DEFAULT_FOLDER_JUNK, webUser);
		if (folder != null) {
			return folder.getId();
		}
		return "";
	}
	
	public static Collection<EmailFolder> getMoveSystemFolders(WebUser webUser) {
		Collection<EmailFolder> result = new ArrayList<EmailFolder>();
		Collection<EmailFolder> systemFolders = getSystemFolders(webUser);
		for (Iterator<EmailFolder> it = systemFolders.iterator(); it.hasNext(); ) {
			EmailFolder folder = it.next();
			if (Constants.DEFAULT_FOLDER_INBOX.equals(folder.getName())
					|| Constants.DEFAULT_FOLDER_SENT.equals(folder.getName())
					|| Constants.DEFAULT_FOLDER_REMOVED.equals(folder.getName())) {
				result.add(folder);
			}
		}
		return result;
	}
	
	public static Collection<EmailFolder> getRemovableFolders(String folderid, String ownerid, WebUser webUser) {
		Collection<EmailFolder> result = new ArrayList<EmailFolder>();
		try {
			EmailFolderProcess process = (EmailFolderProcess) EmailProcessUtil.createProcess(EmailFolderProcess.class, webUser);
			EmailFolder folder = process.getEmailFolderById(folderid);
			if (folder != null) {
				String folderName = folder.getName();
				if (Constants.DEFAULT_FOLDER_INBOX.equals(folderName)) {
					//return "收件箱";
				} else if (Constants.DEFAULT_FOLDER_DRAFTS.equals(folderName)) {
					//return "草稿箱";
				} else if (Constants.DEFAULT_FOLDER_SENT.equals(folderName)) {
					//return "已发送";
				} else if (Constants.DEFAULT_FOLDER_REMOVED.equals(folderName)) {
					//return "已删除";
				} else if (Constants.DEFAULT_FOLDER_JUNK.equals(folderName)) {
					//return "垃圾箱";
				} else {
					//return folderName;
				}
			}
		} catch (Exception e) {
			LOG.warn(e);
		}
		return result;
	}
	
	public static EmailFolder createEmptyEmailFolder() {
		EmailFolder folder = new EmailFolder();
		folder.setId("");
		folder.setName("");
		return folder;
	}
	
	public static String toPersonalFolderHtml(WebUser webUser) {
		StringBuffer html = new StringBuffer();
		Collection<EmailFolder> list = getPersonalEmailFolders(webUser);
		if (list != null && !list.isEmpty()) {
			for (EmailFolder folder : list) {
				html.append("<li id=\"").append(folder.getId()).append("\">");
				html.append(folder.getDisplayName()).append("</li>").append("\n");
			}
		}
		return html.toString();
	}
	
}
