package OLink.bpm.core.workflow.storage.runtime.dao;

import java.util.Collection;

import OLink.bpm.base.dao.IRuntimeDAO;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.workflow.storage.runtime.ejb.RelationHIS;

/**
 * 
 * @author Nicholas
 * 
 */
public interface RelationHISDAO extends IRuntimeDAO {
	RelationHIS findRelHISByCondition(String docid,
									  String startnodeid, String endnodeid, boolean ispassed)
			throws Exception;

	Collection<RelationHIS> queryRelationHIS(String docid,
											 String flowid, String endnodeid) throws Exception;

	Collection<RelationHIS> query(String docid, String flowid)
			throws Exception;

	/**
	 * 获取一条历史记录
	 * 
	 * @param docid
	 *            Document id
	 * @param flowid
	 *            流程 id
	 * @return 一条历史记录
	 * @throws Exception
	 * @deprecated since 2.6
	 */
	@Deprecated
	RelationHIS find(String docid, String flowid) throws Exception;
	
	/**
	 * 获取一条历史记录
	 * @param docId
	 * @param flowStateId
	 * @return
	 * @throws Exception
	 */
	RelationHIS findByDocIdAndFlowStateId(String docId, String flowStateId) throws Exception;

	RelationHIS findLastRelationHIS(String docid, String flowid,
									String flowOperation) throws Exception;

	RelationHIS findLastByEndNode(String docid, String flowStateId,
								  String endNode) throws Exception;

	/**
	 * 根据开始节点获取最后一条记录
	 * 
	 * @param docid
	 * @param flowid
	 * @param startNode
	 * @return
	 * @throws Exception
	 */
	RelationHIS findLastByStartNode(String docid, String flowid,
									String startNode) throws Exception;

	Collection<String> queryStartNodeHis(String docid, String flowid,
										 String endnodeid) throws Exception;

	void create(ValueObject vo) throws Exception;

	Collection<RelationHIS> queryRelationHIS(String docid,
											 String flowStateId, String snodeid, String flowOperation)
			throws Exception;
	
	Collection<RelationHIS> queryByDocIdAndFlowStateId(String docid, String flowStateId) throws Exception;
	
	Collection<RelationHIS> getDatas(String sql) throws Exception;
}
