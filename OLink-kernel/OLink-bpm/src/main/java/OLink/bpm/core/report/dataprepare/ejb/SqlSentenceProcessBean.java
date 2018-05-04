package OLink.bpm.core.report.dataprepare.ejb;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import OLink.bpm.core.report.dataprepare.dao.SqlSentenceDAO;

public class SqlSentenceProcessBean extends AbstractDesignTimeProcessBean<SqlSentence> implements SqlSentenceProcess{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5499661875055166661L;

	/**
	 * @SuppressWarnings getDefaultDAO获取的到的process不定
	 */
	@SuppressWarnings("unchecked")
	protected IDesignTimeDAO<SqlSentence> getDAO() throws Exception {
		return (IDesignTimeDAO<SqlSentence>) DAOFactory.getDefaultDAO(SqlSentence.class.getName());
	}
	
	public DataPackage<SqlSentence> getSqlSentenceByDataPrepare(String id) throws Exception{
		 return ((SqlSentenceDAO) getDAO()).getSqlSentenceByDataPrepare(id);
	}
}
