package OLink.bpm.core.logger.dao;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.logger.ejb.LogVO;
import OLink.bpm.core.user.action.WebUser;

public interface LogDAO extends IDesignTimeDAO<LogVO> {

	DataPackage<LogVO> queryLog(ParamsTable params, WebUser user) throws Exception;
	
	DataPackage<LogVO> queryLog(ParamsTable params, WebUser user, String domain) throws Exception;
	
}
