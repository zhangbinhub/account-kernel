package OLink.bpm.core.networkdisk.ejb;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.networkdisk.dao.NetDiskGroupDAO;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;

public class NetDiskGroupProcessBean extends AbstractDesignTimeProcessBean<NetDiskGroup>
		implements NetDiskGroupProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5732761766615743594L;

	@Override
	protected IDesignTimeDAO<NetDiskGroup> getDAO() throws Exception {
		return (NetDiskGroupDAO) DAOFactory.getDefaultDAO(NetDiskGroup.class.getName());
	}

}
