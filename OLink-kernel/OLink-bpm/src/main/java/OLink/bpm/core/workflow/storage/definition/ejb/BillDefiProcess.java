package OLink.bpm.core.workflow.storage.definition.ejb;

import java.util.Collection;

import OLink.bpm.base.ejb.IDesignTimeProcess;

public interface BillDefiProcess extends IDesignTimeProcess<BillDefiVO> {
	/**
	 * 
	 * @param moduleid
	 *            模块标识
	 * @return 流程对象的集合
	 * @throws Exception
	 */
	Collection<BillDefiVO> getBillDefiByModule(String moduleid)
			throws Exception;

	/**
	 * 根据参数，查询出流程对象
	 * 
	 * @param subject
	 *            流程主题
	 * @param applicationId
	 *            应用标识
	 * @return 流程对象
	 * @throws Exception
	 */
	BillDefiVO doViewBySubject(String subject, String applicationId)
			throws Exception;

	/**
	 * 校验是否有重复的名称
	 * 
	 * @param vo
	 *            流程对象
	 * @return
	 * @throws Exception
	 */
	boolean isSubjectExisted(BillDefiVO vo) throws Exception;

	/**
	 * 删除多个流程对象
	 * 
	 * @param flowList
	 *            流程对象集合
	 * @throws Exception
	 */
	void doRemove(Collection<BillDefiVO> flowList)
			throws Exception;

}
