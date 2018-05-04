package OLink.bpm.core.report.query.ejb;

import java.util.Collection;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.report.query.dao.ParameterDAO;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;

public class ParameterProcessBean extends AbstractDesignTimeProcessBean<Parameter> implements ParameterProcess{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 672721533953156222L;

	protected IDesignTimeDAO<Parameter> getDAO() throws Exception {
		return (ParameterDAO) DAOFactory.getDefaultDAO(Parameter.class.getName());
	}
	
	 public Collection<Parameter> getParamtersByQuery(String queryid, String application) throws Exception{
		 return ((ParameterDAO) getDAO()).getParamtersByQuery(queryid, application);
	 }
}
