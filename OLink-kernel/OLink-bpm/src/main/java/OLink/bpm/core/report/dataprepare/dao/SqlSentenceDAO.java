package OLink.bpm.core.report.dataprepare.dao;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.report.dataprepare.ejb.SqlSentence;

public interface SqlSentenceDAO {

	DataPackage<SqlSentence> getSqlSentenceByDataPrepare(String id) throws Exception;
	
}
