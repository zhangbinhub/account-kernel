package OLink.bpm.core.workflow.storage.definition.ejb;

import java.util.Collection;

import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import OLink.bpm.core.workflow.storage.definition.dao.BillDefiDAO;
import OLink.bpm.util.StringUtil;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DAOFactory;

public class BillDefiProcessBean extends
		AbstractDesignTimeProcessBean<BillDefiVO> implements BillDefiProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6215310765110250933L;
	
	protected IDesignTimeDAO<BillDefiVO> getDAO() throws Exception {
		return (BillDefiDAO) DAOFactory.getDefaultDAO(BillDefiVO.class.getName());
	}

	public Collection<BillDefiVO> getBillDefiByModule(String moduleid)
			throws Exception {
		return ((BillDefiDAO) getDAO()).getBillDefiByModule(moduleid);
	}

	public ValueObject doView(String pk) throws Exception {
		return super.doView(pk);
	}

	public BillDefiVO doViewBySubject(String subject, String applicationId)
			throws Exception {
		return ((BillDefiDAO) getDAO()).findBySubject(subject, applicationId);
	}

	public boolean isSubjectExisted(BillDefiVO vo) throws Exception {
		BillDefiVO po = (BillDefiVO) doView(vo.getId());
		if (po == null || !vo.getSubject().equals(po.getSubject())) {
			if (!StringUtil.isBlank(vo.getSubject())) {
				ParamsTable params = new ParamsTable();
				params.setParameter("t_subject", vo.getSubject());
				Collection<BillDefiVO> billDefiList = doSimpleQuery(params, vo
						.getApplicationid());
				if (!billDefiList.isEmpty()) {
					return true;
				}
			}
		}
		return false;
	}
}
