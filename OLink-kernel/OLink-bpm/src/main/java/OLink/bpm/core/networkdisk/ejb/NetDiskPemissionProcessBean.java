package OLink.bpm.core.networkdisk.ejb;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import OLink.bpm.core.networkdisk.dao.NetDiskPemissionDAO;

public class NetDiskPemissionProcessBean extends AbstractDesignTimeProcessBean<NetDiskPemission>
		implements NetDiskPemissionProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4311331486716417514L;

	@Override
	protected IDesignTimeDAO<NetDiskPemission> getDAO() throws Exception {
		return (NetDiskPemissionDAO) DAOFactory.getDefaultDAO(NetDiskPemission.class.getName());
	}

}
