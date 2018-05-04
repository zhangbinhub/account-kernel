package OLink.bpm.core.workflow.storage.runtime.ejb;

import java.util.Collection;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.base.ejb.IRunTimeProcess;
import OLink.bpm.constans.Environment;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.workflow.element.SubFlow;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiVO;

public interface FlowStateRTProcess extends IRunTimeProcess<FlowStateRT> {
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
	 * 更新参数为继承ValueObject的对象
	 * 
	 * @param object
	 *            继承ValueObject的对象
	 * @throws Exception
	 */
	void doUpdate(ValueObject object) throws Exception;

	/**
	 * 新建一个对象
	 * 
	 * @param object
	 *            继承ValueObject的对象
	 * @throws Exception
	 */
	void doCreate(ValueObject object) throws Exception;
	
	
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

	Collection<FlowStateRT> getSubStates(String parent) throws Exception;
	
	
	/**
	 * 获取当前用户可执行的流程实例
	 * @param doc
	 * @param user
	 * @param currFlowStateId
	 * @return
	 * @throws Exception
	 */
	FlowStateRT getCurrFlowStateRT(Document doc, WebUser user, String currFlowStateId) throws Exception;
	
	/**
	 * 当前文档下的用户是否存在可执行的多实例
	 * @param doc
	 * @param user
	 * @return
	 * @throws Exception
	 */
	boolean isMultiFlowState(Document doc, WebUser user) throws Exception;
	
	
	/**
	 * 当前文档下是否有多个流程实例
	 * @param doc
	 * @param user
	 * @return
	 * @throws Exception
	 */
	boolean isMultiFlowState(Document doc) throws Exception;
	
	/**
	 * 创建一个瞬时的主流程实例
	 * @param doc
	 * @param flowVO
	 * @return
	 * @throws Exception
	 */
	FlowStateRT createTransientFlowStateRT(Document doc, BillDefiVO flowVO, WebUser user) throws Exception;
	
	/**
	 * 创建一个瞬时的主流程实例
	 * @param doc
	 * @param flowVO
	 * @return
	 * @throws Exception
	 */
	FlowStateRT createTransientFlowStateRT(Document doc, String flowId, WebUser user) throws Exception;
	
	
	/**
	 * 创建一个瞬时的子流程实例
	 * @param subFlowDoc
	 * @param subFlowVO
	 * @param parentInstance
	 * @param token
	 * @param node
	 * @param user
	 * @return
	 * @throws Exception
	 */
	FlowStateRT createTransientSubFlowStateRT(Document subFlowDoc, BillDefiVO subFlowVO, FlowStateRT parentInstance, String token, SubFlow node, WebUser user)throws Exception;
	
	/**
	 * 启动流程实例；
	 * @param instance
	 * @param doc
	 * @param params
	 * @throws Exception
	 */
	void startFlow(FlowStateRT instance, Document doc, ParamsTable params) throws Exception;
	
	
//	public void createAndStartSubFlow(FlowStateRT ParentInstance,SubFlow node,ParamsTable params,Form form,BillDefiVO flowVO,int count)throws Exception;
	
	
	/**
	 * 审批文档
	 * @param params
	 * @param instance
	 * @param currNodeId
	 * @param nextNodeIds
	 * @param flowOption
	 * @param comment
	 * @param evt
	 * @param user
	 * @throws Exception
	 */
	void doApprove(ParamsTable params, FlowStateRT instance, String currNodeId,
				   String[] nextNodeIds, String flowOption, String comment, Environment evt, WebUser user) throws Exception;
	
	/**
	 * 同一批次创建的所有子流程实例是否都已经完成
	 * @param subFlowInstance
	 * @return
	 * @throws Exception
	 */
	boolean isAllSubFlowStateRTComplete(FlowStateRT subFlowInstance) throws Exception;
	
	/**
	 * 异步提交流程到下一步
	 * @param subFlowInstance
	 * @param params
	 * @param user
	 * @throws Exception
	 */
	void asynchronous2Next(FlowStateRT instance, String subFlowNodeid, ParamsTable params, WebUser user) throws Exception;
	/**
	 * 提交父流程实例到下一步
	 * @param subFlowInstance
	 * @param user
	 * @throws Exception
	 */
	void doParentFlow2Next(FlowStateRT subFlowInstance, ParamsTable params, WebUser user) throws Exception;
	
	/**
	 * 子流程回调
	 * @param subFlowInstance
	 * @param runner
	 * @param script
	 * @param params
	 * @param user
	 * @throws Exception
	 */
	void callBack(FlowStateRT subFlowInstance, IRunner runner, String script, ParamsTable params, WebUser user) throws Exception;
	
	/**
	 * 运行回调脚本
	 * @param params
	 * @param instance
	 * @param user
	 * @throws Exception
	 */
	void runCallbackScript(ParamsTable params, FlowStateRT instance, WebUser user) throws Exception;
	
	Collection<FlowStateRT> doQueryBySQL(String sql) throws Exception;
}
