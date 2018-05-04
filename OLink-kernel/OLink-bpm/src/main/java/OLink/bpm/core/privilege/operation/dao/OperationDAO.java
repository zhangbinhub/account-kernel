package OLink.bpm.core.privilege.operation.dao;

import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.privilege.operation.ejb.OperationVO;

public interface OperationDAO extends IDesignTimeDAO<OperationVO> {

	boolean isEmpty(String applicationId) throws Exception;

	/**
	 * 获取总数
	 * 
	 * @return
	 * @throws Exception
	 */
	int getTotal() throws Exception;
}