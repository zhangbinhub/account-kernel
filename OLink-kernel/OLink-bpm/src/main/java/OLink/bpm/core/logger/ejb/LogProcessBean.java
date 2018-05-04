package OLink.bpm.core.logger.ejb;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.logger.dao.LogDAO;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.StringUtil;
import OLink.bpm.base.action.ParamsTable;

public class LogProcessBean extends AbstractDesignTimeProcessBean<LogVO> implements LogProcess {

	private static final long serialVersionUID = 3998082574597686749L;

	@Override
	protected IDesignTimeDAO<LogVO> getDAO() throws Exception {
		return (LogDAO) DAOFactory.getDefaultDAO(LogVO.class.getName());
	}
	
	@Override
	public DataPackage<LogVO> doQuery(ParamsTable params, WebUser user)
			throws Exception {
		return ((LogDAO)getDAO()).queryLog(params, user);
		//return getDAO().query(params, user);
	}

	public DataPackage<LogVO> getLogsByDomain(ParamsTable params, WebUser user)
			throws Exception {
		String domain = params.getParameterAsString("domain");
		if (StringUtil.isBlank(domain)) {
			throw new Exception("domainid is null!");
		}
		return ((LogDAO)getDAO()).queryLog(params, user, domain);
	}
	
}
