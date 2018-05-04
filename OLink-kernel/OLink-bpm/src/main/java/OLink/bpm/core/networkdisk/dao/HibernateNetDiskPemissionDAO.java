package OLink.bpm.core.networkdisk.dao;

import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.networkdisk.ejb.NetDiskPemission;

public class HibernateNetDiskPemissionDAO extends HibernateBaseDAO<NetDiskPemission> implements
		NetDiskPemissionDAO {

	public HibernateNetDiskPemissionDAO(String voClassName) {
		super(voClassName);
	}
}
