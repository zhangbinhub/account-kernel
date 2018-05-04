package OLink.bpm.core.networkdisk.dao;

import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.networkdisk.ejb.NetDiskGroup;

public class HibernateNetDiskGroupDAO extends HibernateBaseDAO<NetDiskGroup> implements
		NetDiskGroupDAO {

	public HibernateNetDiskGroupDAO(String voClassName) {
		super(voClassName);
	}

}
