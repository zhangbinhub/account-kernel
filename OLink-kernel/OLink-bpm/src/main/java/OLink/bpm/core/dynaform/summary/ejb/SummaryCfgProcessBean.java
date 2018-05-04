package OLink.bpm.core.dynaform.summary.ejb;


import java.util.Collection;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import OLink.bpm.core.dynaform.summary.dao.SummaryCfgDAO;

/**
 * @author Happy
 *
 */
public class SummaryCfgProcessBean extends
		AbstractDesignTimeProcessBean<SummaryCfgVO> implements SummaryCfgProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1598156119769582486L;

	@Override
	protected IDesignTimeDAO<SummaryCfgVO> getDAO() throws Exception {
		return (SummaryCfgDAO) DAOFactory.getDefaultDAO(SummaryCfgVO.class.getName());
	}

	public Collection<SummaryCfgVO> doQueryByFormId(String formId) throws Exception {
		return ((SummaryCfgDAO)this.getDAO()).queryByFormId(formId);
	}

	public SummaryCfgVO doViewByFormIdAndScope(String formId, int scope) throws Exception{
		return ((SummaryCfgDAO)this.getDAO()).findByFormIdAndScope(formId, scope);
	}

	public DataPackage<SummaryCfgVO> doQueryHomePageSummaryCfgs(
			ParamsTable params) throws Exception {
		return ((SummaryCfgDAO)this.getDAO()).queryHomePageSummaryCfgs(params);
	}

	public boolean isExistWithSameTitle(String title, String applicationId)
			throws Exception {
		return ((SummaryCfgDAO)this.getDAO()).isExistWithSameTitle(title, applicationId);
	}

	

}
