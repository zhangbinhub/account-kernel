package OLink.bpm.core.networkdisk.ejb;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import OLink.bpm.core.networkdisk.dao.NetDiskFolderDAO;

public class NetDiskFolderProcessBean extends AbstractDesignTimeProcessBean<NetDiskFolder>
		implements NetDiskFolderProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6946299735767950445L;

	@Override
	protected IDesignTimeDAO<NetDiskFolder> getDAO() throws Exception {
		return (NetDiskFolderDAO) DAOFactory.getDefaultDAO(NetDiskFolder.class.getName());
	}

}
