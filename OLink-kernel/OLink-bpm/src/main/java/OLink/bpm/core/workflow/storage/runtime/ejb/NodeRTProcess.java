package OLink.bpm.core.workflow.storage.runtime.ejb;

import java.util.Collection;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.base.ejb.IRunTimeProcess;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.workflow.element.Node;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.user.ejb.BaseUser;
import OLink.bpm.core.workflow.element.ManualNode;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiVO;

public interface NodeRTProcess extends IRunTimeProcess<NodeRT> {

	NodeRT doCreate(ParamsTable params, NodeRT parentNodert,
					FlowStateRT instance, Node node, String flowoption,
					BaseUser baseUser) throws Exception;

	/**
	 * 查询节点的集合列表,参数为文档标识和流程的标识
	 * 
	 * @param docid
	 *            文
	 * @param flowid
	 * @return
	 * @throws Exception
	  * @deprecate since 2.6
	 */
	@Deprecated
	Collection<NodeRT> queryNodeRTByDocidAndFlowid(
			String docid, String flowid) throws Exception;
	
	
	Collection<NodeRT> queryNodeRTByFlowStateIdAndDocId(
			String instanceId, String docId) throws Exception;

	void doUpdate(ValueObject vo) throws Exception;

	/**
	 * 根据Document主键(ID)、流程(id),返回当前用户运行时节点
	 * 
	 * @param docid
	 *            Document id
	 * @param flowid
	 *            BillDefiVO流程定义对象 id
	 * @param user
	 *            web用户对象
	 * @return 当前用户获取运行时节点
	 * @throws Exception
	 */
	NodeRT doView(String docid, String flowid, String flowStateId, WebUser user)
			throws Exception;

	/**
	 * 根据文档，文档相应流程查询，获取所有运行时节点
	 * 
	 * @param docid
	 *            document id
	 * @param flowStateId
	 *            流程实例 id
	 * @return 文档全部的流程节点
	 * @throws Exception
	 */
	Collection<NodeRT> doQuery(String docid, String flowStateId)
			throws Exception;

	/**
	 * Remove a value object
	 * 
	 * @param pk
	 *            The value object's primrary key.
	 * @throws Exception
	 */
	void doRemove(String pk) throws Exception;

	Collection<NodeRT> queryByFlowStateRT(String stateId)
			throws Exception;

	/**
	 * 
	 * @param nodeRT
	 * @param doc
	 * @param flowVO
	 * @throws Exception
	 */
	NodeRT doUpdateByActorIds(NodeRT nodeRT, Document doc,
							  BillDefiVO flowVO, String[] actorIds) throws Exception;

	/**
	 * 处理当前节点
	 * 
	 * @param nodeRT
	 *            当前节点
	 * @param user
	 *            处理者
	 * @throws Exception
	 */
	boolean process(NodeRT nodeRT, WebUser user, String flowOption,
					int residual) throws Exception;

	/**
	 * 根据聚合条件判断是否创建下一个节点
	 * 
	 * @param instance
	 * @param nextNode
	 * @return
	 * @throws Exception
	 */
	boolean isCreateAble(FlowStateRT instance, NodeRT nodeRT,
						 Node nextNode) throws Exception;

	/**
	 * 根据流程节点获取流程当前节点
	 * 
	 * @param docid
	 *            文档ID
	 * @param flowid
	 *            流程ID
	 * @param nodeid
	 *            节点ID
	 * @return
	 * @throws Exception
	 */
	NodeRT doViewByNodeid(String docid, String flowStateId, String nodeid)
			throws Exception;
	
	/**
	 * 获取并创建抄送人 
	 * @param params
	 * @param doc
	 * @param nodeRT
	 * @param node
	 * @return
	 * @throws Exception
	 */
	Collection<Circulator> getCirculatorList(ParamsTable params, Document doc, NodeRT nodeRT, ManualNode node) throws Exception;
}
