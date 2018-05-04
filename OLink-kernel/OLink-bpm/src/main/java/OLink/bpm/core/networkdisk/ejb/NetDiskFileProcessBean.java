package OLink.bpm.core.networkdisk.ejb;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.core.networkdisk.dao.NetDiskFileDAO;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;

public class NetDiskFileProcessBean extends AbstractDesignTimeProcessBean<NetDiskFile>
		implements NetDiskFileProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8277239506770546970L;

	@Override
	protected IDesignTimeDAO<NetDiskFile> getDAO() throws Exception {
		return (NetDiskFileDAO) DAOFactory.getDefaultDAO(NetDiskFile.class.getName());
	}

	//获得总页数
	public int getTotalLines(String hql) throws Exception {
		return getDAO().getTotalLines(hql);
	}

}
