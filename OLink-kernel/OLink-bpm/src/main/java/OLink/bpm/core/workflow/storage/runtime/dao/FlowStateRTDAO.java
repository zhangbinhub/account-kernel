package OLink.bpm.core.workflow.storage.runtime.dao;

import java.util.Collection;

import OLink.bpm.base.dao.IBaseDAO;
import OLink.bpm.base.dao.IRuntimeDAO;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.workflow.storage.runtime.ejb.FlowStateRT;

/**
 * 
 * @author Marky
 * 
 */
public interface FlowStateRTDAO extends IRuntimeDAO {
	/**
	 * 获取当前文档流程状态
	 * 
	 * @param docid
	 *            Document id
	 * @param flowid
	 *            文档流程 id
	 * @return 当前文档流程状态
	 * @throws Exception
	 */
	FlowStateRT findFlowStateRTByDocidAndFlowid(String docid,
												String flowid) throws Exception;

	/**
	 * (non-Javadoc)
	 * 
	 * @see IBaseDAO#update(ValueObject)
	 */
	void update(ValueObject vo) throws Exception;

	void create(ValueObject vo) throws Exception;

	ValueObject find(String id) throws Exception;

	Collection<FlowStateRT> queryByParent(String parent)
			throws Exception;
	
	/**
	 * 获取当前文档的流程实例
	 * @param doc
	 * @param user
	 * @param currFlowStateId
	 * @return
	 * @throws Exception
	 */
	FlowStateRT getCurrFlowStateRT(Document doc, WebUser user,
								   String currFlowStateId)throws Exception;
	
	/**
	 * 根据文档ID获取相关流程实例集合
	 * @param docId
	 * @return
	 * @throws Exception
	 */
	Collection<FlowStateRT> getFlowStateRTsByDocId(String docId) throws Exception;
	
	/**
	 * 根据文档ID删除关联的流程实例
	 * @param docId
	 * @throws Exception
	 */
	void doRemoveByDocId(String docId) throws Exception;
	
	/**
	 * 文档是否同时存在多个可执行的流程实例
	 * @param doc
	 * @param user
	 * @return
	 * @throws Exception
	 */
	boolean isMultiFlowState(Document doc, WebUser user)throws Exception;
	
	
	/**
	 * 文档是否同时存在多个流程实例
	 * @param doc
	 * @param user
	 * @return
	 * @throws Exception
	 */
	boolean isMultiFlowState(Document doc)throws Exception;
	
	/**
	 * 是否同一批次创建的子流程实例都已经走完
	 * @param subFlowInstance
	 * @return
	 * @throws Exception
	 */
	boolean isAllSubFlowStateRTComplete(FlowStateRT subFlowInstance)
	throws Exception;
	
	Collection<FlowStateRT> queryBySQL(String sql) throws Exception;
}
