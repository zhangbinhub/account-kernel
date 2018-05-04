package OLink.bpm.core.report.query.dao;

import java.util.Collection;

import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.report.query.ejb.Query;

public interface QueryDAO extends IDesignTimeDAO<Query> {
	
  Collection<Query> get_queryStringList(String moduleid, String application) throws Exception;
  
  Collection<Query> get_queryByAppId(String application) throws Exception;
}
