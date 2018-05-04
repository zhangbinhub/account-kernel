package OLink.bpm.core.networkdisk.ejb;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import OLink.bpm.core.networkdisk.dao.NetDiskDAO;

public class NetDiskProcessBean extends AbstractDesignTimeProcessBean<NetDisk> implements
		NetDiskProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9065002664723531746L;

	@Override
	protected IDesignTimeDAO<NetDisk> getDAO() throws Exception {
		return (NetDiskDAO) DAOFactory.getDefaultDAO(NetDisk.class.getName());
	}

}
