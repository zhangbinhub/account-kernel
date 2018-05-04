package OLink.bpm.core.report.dataprepare.ejb;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.ejb.IDesignTimeProcess;

public interface SqlSentenceProcess extends IDesignTimeProcess<SqlSentence>{
	DataPackage<SqlSentence> getSqlSentenceByDataPrepare(String id) throws Exception;
}
