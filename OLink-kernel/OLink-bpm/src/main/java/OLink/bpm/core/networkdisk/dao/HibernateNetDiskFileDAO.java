package OLink.bpm.core.networkdisk.dao;

import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.networkdisk.ejb.NetDiskFile;

public class HibernateNetDiskFileDAO extends HibernateBaseDAO<NetDiskFile> implements
		NetDiskFileDAO {

	public HibernateNetDiskFileDAO(String voClassName) {
		super(voClassName);
	}
}
