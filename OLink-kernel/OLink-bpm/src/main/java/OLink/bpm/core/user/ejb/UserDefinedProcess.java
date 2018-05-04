package OLink.bpm.core.user.ejb;

import java.util.Collection;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.ejb.IDesignTimeProcess;

public interface UserDefinedProcess extends IDesignTimeProcess<UserDefined> {
	Collection<UserDefined> doViewByApplication(String applicationId)
	throws Exception;

int doViewCountByName(String name, String applicationid) throws Exception;


DataPackage<UserDefined> getDatapackage(String hql, ParamsTable params) throws Exception;

}
