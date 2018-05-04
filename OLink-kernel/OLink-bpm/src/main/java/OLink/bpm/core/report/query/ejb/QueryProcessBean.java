package OLink.bpm.core.report.query.ejb;

import java.util.Collection;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import OLink.bpm.core.report.query.dao.QueryDAO;

public class QueryProcessBean extends AbstractDesignTimeProcessBean<Query> implements QueryProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3089071407204416480L;

	protected IDesignTimeDAO<Query> getDAO() throws Exception {
		return (QueryDAO) DAOFactory.getDefaultDAO(Query.class.getName());
	}

	public Collection<Query> get_queryStringList(String moduleid,
			String application) throws Exception {
		return ((QueryDAO) getDAO()).get_queryStringList( moduleid,
				application);
	}

	public Collection<Query> get_queryByAppId(String appid, String application)
			throws Exception {
		return ((QueryDAO) getDAO()).get_queryByAppId(application);
	}
}
