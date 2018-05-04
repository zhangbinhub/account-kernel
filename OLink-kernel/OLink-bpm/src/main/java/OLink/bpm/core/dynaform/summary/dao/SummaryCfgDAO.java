package OLink.bpm.core.dynaform.summary.dao;


import java.util.Collection;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.dynaform.summary.ejb.SummaryCfgVO;

/**
 * @author Happy
 *
 */
public interface SummaryCfgDAO extends IDesignTimeDAO<SummaryCfgVO> {
	
	Collection<SummaryCfgVO> queryByFormId(String formId) throws Exception;
	
	SummaryCfgVO findByFormIdAndScope(String formId, int scope) throws Exception;
	DataPackage<SummaryCfgVO> queryHomePageSummaryCfgs(ParamsTable params) throws Exception;
	boolean isExistWithSameTitle(String title, String applicationId) throws Exception;
}
