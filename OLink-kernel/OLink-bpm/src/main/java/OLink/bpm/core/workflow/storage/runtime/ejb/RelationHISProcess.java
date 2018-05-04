package OLink.bpm.core.workflow.storage.runtime.ejb;

import java.util.Collection;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.base.ejb.IRunTimeProcess;

public interface RelationHISProcess extends IRunTimeProcess<RelationHIS> {
	RelationHIS findRelHISByCondition(String docid,
									  String startnodeid, String endnodeid, boolean ispassed)
			throws Exception;

	Collection<RelationHIS> queryRelationHIS(String docid,
											 String flowid, String endnodeid) throws Exception;

	/**
	 * @param docid
	 * @param flowid
	 * @return
	 * @throws Exception
	 * @deprecated since 2.6 Please use "doQueryByDocIdAndFlowStateId" method
	 */
	@Deprecated
	Collection<RelationHIS> doQuery(String docid, String flowid)
			throws Exception;
	
	Collection<RelationHIS> doQueryByDocIdAndFlowStateId(String docid, String flowStateId) throws Exception;

	/**
	 * 获取最后一条历史记录
	 * 
	 * @param docid
	 *            Document id
	 * @param flowid
	 *            流程 id
	 * @return 最后一条历史记录
	 * @throws Exception
	 * @deprecated since 2.6 Please use "doViewLastByDocIdAndFolowStateId" method
	 */
	@Deprecated
	RelationHIS doViewLast(String docid, String flowid)
			throws Exception;
	
	/**
	 * 获取最后一条历史记录
	 * 
	 * @param docid
	 *            Document id
	 * @param flowStateId
	 *            流程实例 id
	 * @return 最后一条历史记录
	 * @throws Exception
	 */

	RelationHIS doViewLastByDocIdAndFolowStateId(String docId, String flowStateId)
			throws Exception;

	/**
	 * query start node history exclude duplicate
	 */
	Collection<String> queryStartNodeHis(String docid, String flowid,
										 String endnodeid) throws Exception;

	void doCreate(ValueObject object) throws Exception;

	/**
	 * 获取最后一条历史记录
	 * 
	 * @param docid
	 * @param flowid
	 * @param flowOperation
	 *            流程操作（查看FlowType）
	 * @return
	 * @throws Exception
	 */
	RelationHIS doViewLast(String docid, String flowid,
						   String flowOperation) throws Exception;

	Collection<RelationHIS> doQueryByStartNode(String id,
											   String flowStateId, String nodeid, String running2running_next)
			throws Exception;

	RelationHIS doViewLastByEndNode(String docid, String flowStateId,
									String endnodeid) throws Exception;

	RelationHIS doViewLastByStartNode(String docid, String flowid,
									  String startnodeid) throws Exception;
	
	Collection<RelationHIS> doQueryBySQL(String sql) throws Exception;
}
