package OLink.bpm.core.report.query.ejb;

import java.util.Collection;

import OLink.bpm.base.ejb.IDesignTimeProcess;

public interface QueryProcess extends IDesignTimeProcess<Query> {
	
	Collection<Query> get_queryStringList(String moduleid,
										  String application) throws Exception;

	Collection<Query> get_queryByAppId(String appid, String application)
			throws Exception;

}
