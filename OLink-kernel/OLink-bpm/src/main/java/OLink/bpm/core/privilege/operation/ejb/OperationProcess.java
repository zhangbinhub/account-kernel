package OLink.bpm.core.privilege.operation.ejb;

import java.util.Collection;

import OLink.bpm.base.ejb.IDesignTimeProcess;

public interface OperationProcess extends IDesignTimeProcess<OperationVO> {
	/**
	 * 操作判断是空
	 * 
	 * @return boolean
	 * @throws Exception
	 */
	boolean isEmpty(String applicationId) throws Exception;

	/**
	 * 根据资源ID和资源类型获取操作
	 * 
	 * @param resourceid
	 *            资源ID
	 * @param resourcetype
	 *            资源类型(Form和View中的操作为非固定操作)
	 * @param applicationid 
	 * @return
	 * @throws Exception
	 */
	Collection<OperationVO> getOperationByResource(String resourceid, int resourcetype, String applicationid) throws Exception;

	OperationVO doViewByResource(String operationid, String resourceid, int resourcetype) throws Exception;
}
