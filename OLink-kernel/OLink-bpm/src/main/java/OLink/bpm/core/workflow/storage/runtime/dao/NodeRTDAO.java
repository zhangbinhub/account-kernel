package OLink.bpm.core.workflow.storage.runtime.dao;

import java.util.Collection;

import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.base.dao.IRuntimeDAO;
import OLink.bpm.core.workflow.storage.runtime.ejb.NodeRT;

public interface NodeRTDAO extends IRuntimeDAO {
	
	/**
	 * @param docid
	 * @param flowid
	 * @return
	 * @throws Exception
	 * @deprecate since 2.6
	 */
	@Deprecated
	Collection<NodeRT> queryNodeRTByDocidAndFlowid(String docid,
												   String flowid) throws Exception;
	
	
	Collection<NodeRT> queryNodeRTByFlowStateIdAndDocId(String instanceId, String docId) throws Exception;

	/**
	 * 根据文档，文档相应流程查询，获取文档的所有运行时节点
	 * 
	 * @param docid
	 *            document id
	 * @param flowStateId
	 *            流程实例 id
	 * @return 文档全部的流程结点
	 * @throws Exception
	 */
	Collection<NodeRT> query(String docid, String flowStateId)
			throws Exception;

	/**
	 * 
	 * @see IDesignTimeDAO#create(ValueObject)
	 */
	void create(ValueObject vo) throws Exception;

	void remove(String pk) throws Exception;

	void update(ValueObject vo) throws Exception;

	NodeRT findByNodeid(String docid, String flowid, String nodeid)
			throws Exception;
}
