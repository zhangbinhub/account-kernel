package OLink.bpm.core.networkdisk.dao;

import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.networkdisk.ejb.NetDiskFolder;

public class HibernateNetDiskFolderDAO extends HibernateBaseDAO<NetDiskFolder> implements NetDiskFolderDAO {

	public HibernateNetDiskFolderDAO(String voClassName) {
		super(voClassName);
	}
}
