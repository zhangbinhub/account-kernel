package OLink.bpm.core.report.query.dao;

import java.util.Collection;

import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.report.query.ejb.Parameter;

public interface ParameterDAO extends IDesignTimeDAO<Parameter> {
	
	Collection<Parameter> getParamtersByQuery(String queryid, String application)
			throws Exception;
}
