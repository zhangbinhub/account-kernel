package OLink.bpm.core.logger.ejb;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.base.ejb.IDesignTimeProcess;

public interface LogProcess extends IDesignTimeProcess<LogVO> {

	DataPackage<LogVO> getLogsByDomain(ParamsTable params, WebUser user) throws Exception;
	
}
