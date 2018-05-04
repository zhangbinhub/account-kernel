package OLink.bpm.core.remoteserver.dao;

import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.remoteserver.ejb.RemoteServerVO;

public class HibernateRemoteServerDAO extends HibernateBaseDAO<RemoteServerVO> implements RemoteServerDAO{

	public HibernateRemoteServerDAO(String voClassName) {
		super(voClassName);
	}
}
