package OLink.bpm.core.networkdisk.dao;

import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.networkdisk.ejb.NetDisk;

public class HibernateNetDiskDAO extends HibernateBaseDAO<NetDisk> implements NetDiskDAO {

	public HibernateNetDiskDAO(String voClassName) {
		super(voClassName);
	}
}
