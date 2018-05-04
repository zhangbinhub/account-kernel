package OLink.bpm.core.dynaform.summary.ejb;

import java.util.Collection;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.ejb.IDesignTimeProcess;

/**
 * @author Happy
 *
 */
public interface SummaryCfgProcess extends
		IDesignTimeProcess<SummaryCfgVO> {
	
	Collection<SummaryCfgVO> doQueryByFormId(String formId) throws Exception;
	
	SummaryCfgVO doViewByFormIdAndScope(String formId, int scope) throws Exception;
	
	DataPackage<SummaryCfgVO> doQueryHomePageSummaryCfgs(ParamsTable params) throws Exception;
	
	/**
	 * 在给定的软件ID判断下是否存在同名摘要
	 * @param title
	 * @param applicationId
	 * @return
	 * @throws Exception
	 */
	boolean isExistWithSameTitle(String title, String applicationId) throws Exception;
}
