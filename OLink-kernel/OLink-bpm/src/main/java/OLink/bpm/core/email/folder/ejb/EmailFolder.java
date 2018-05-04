package OLink.bpm.core.email.folder.ejb;

import java.util.Date;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.email.util.Constants;

import com.sun.mail.imap.IMAPFolder;

public class EmailFolder extends ValueObject {

	private static final long serialVersionUID = 1L;

	private String id;
	private String ownerId;
	private String name;
	private String displayName;
	private Date createDate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the ownerId
	 */
	public String getOwnerId() {
		return ownerId;
	}

	/**
	 * @param ownerId the ownerId to set
	 */
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the createDate
	 */
	public Date getCreateDate() {
		return createDate;
	}

	/**
	 * @param createDate the createDate to set
	 */
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	@Override
	public String toString() {
		return "[id=" + id + ", name=" + name +"]";
	}
	
	public static EmailFolder valueOf(IMAPFolder folder) {
		EmailFolder emailFolder = null;
		if (folder != null) {
			try {
				emailFolder = new EmailFolder();
				emailFolder.setId(String.valueOf(folder.getUIDValidity()));
				emailFolder.setName(folder.getFullName());
				emailFolder.setDisplayName(Constants.getFolderDisplay(emailFolder.getName()));
			} catch (Exception e) {
			}
		}
		return emailFolder;
	}
	
}
