package OLink.bpm.core.email.folder.dao;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.email.folder.ejb.EmailFolder;

public interface EmailFolderDAO extends IDesignTimeDAO<EmailFolder> {

	EmailFolder queryMailFolderById(String folderid) throws Exception;
	
	EmailFolder queryMailFolderByOwnerId(String folderName, String ownerId) throws Exception;
	
	/**
	 * 根据文件名称判断该文件是否已创建
	 * @param folderName
	 * @return
	 */
	boolean judgeMailFolderIsCreate(String folderName, String ownerid) throws Exception;
	
	int queryPersonalEmailFolderCount(String ownerid) throws Exception;
	
	DataPackage<EmailFolder> queryPersonalEmailFolders(String ownerid, ParamsTable params) throws Exception;
	
}
