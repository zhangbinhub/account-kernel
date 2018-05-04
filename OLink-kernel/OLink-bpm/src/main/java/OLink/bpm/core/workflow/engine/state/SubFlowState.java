package OLink.bpm.core.workflow.engine.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import OLink.bpm.constans.Environment;
import OLink.bpm.core.dynaform.document.ejb.DocumentProcess;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.workflow.FlowType;
import OLink.bpm.core.workflow.element.Node;
import OLink.bpm.core.workflow.element.SubFlow;
import OLink.bpm.core.workflow.storage.runtime.ejb.NodeRTProcessBean;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.workflow.FlowState;
import OLink.bpm.core.workflow.element.mapping.FieldMappingItem;
import OLink.bpm.core.workflow.engine.StateMachine;
import OLink.bpm.core.workflow.engine.StateMachineUtil;
import OLink.bpm.core.workflow.storage.runtime.ejb.FlowStateRT;
import OLink.bpm.core.workflow.storage.runtime.ejb.NodeRTProcess;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.dynaform.form.ejb.ValidateMessage;
import OLink.bpm.core.macro.runner.JavaScriptFactory;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.workflow.engine.State;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiProcess;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiVO;
import OLink.bpm.core.workflow.storage.runtime.ejb.FlowStateRTProcess;
import OLink.bpm.core.workflow.storage.runtime.ejb.NodeRT;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import OLink.bpm.util.json.JsonUtil;
import eWAP.core.Tools;

public class SubFlowState extends AbstractState implements State {

	public SubFlowState(Node node) {
		super(node);
	}

	public NodeRT process(ParamsTable params, NodeRT origNodeRT, FlowStateRT instance, WebUser user,
						  String flowOption) throws Exception {
		
		
		//1.创建并启动子流程
		createAndStartSubFlowState(instance,origNodeRT, params, user);
		
		// 2更新当前节点状态
		
		SubFlow subFlowNode = (SubFlow) node;
		if(!subFlowNode.callback){
			
			instance.setAsyncSubFlowNodeId(subFlowNode.id);
			
			//启动回调脚本
			FlowStateRTProcess instanceProcess = (FlowStateRTProcess) ProcessFactory.createRuntimeProcess(FlowStateRTProcess.class, instance.getApplicationid());
			instanceProcess.runCallbackScript(params, instance, user);
		}
		
		return super.process(params, origNodeRT, instance, user, flowOption);
		
	}
	
	/**
	 * 创建并启动子流程
	 * @param instance
	 * @param params
	 * @param user
	 * @throws Exception
	 */
	public void createAndStartSubFlowState(FlowStateRT instance,NodeRT origNodeRT, ParamsTable params,WebUser user) throws Exception {
		
		SubFlow subFlowNode = (SubFlow) node;
		IRunner runner = JavaScriptFactory.getInstance(params.getSessionid(), instance.getApplicationid());
		runner.initBSFManager(instance.getDocument(), params, user, new ArrayList<ValidateMessage>());
		
		String subFlowFlowId ="";
		String subFlowFormId="";
		int count = 0;
		int position = 1;
		
		FormProcess formProcess = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
		BillDefiProcess flowProcess = (BillDefiProcess) ProcessFactory.createProcess(BillDefiProcess.class);
		//TOKEN 生成策略  上一个节点的ID:UUID
		String token = origNodeRT.getId()+":"+Tools.getSequence();
		String _subFlowApproverInfo = params.getParameterAsString("_subFlowApproverInfo");
		
		
		//获取子流程绑定流程ID
		if(SubFlow.SUBFLOW_DEFINITION_CUSTOM.equals(subFlowNode.subFlowDefiType)){
			subFlowFlowId = subFlowNode.subflowid;
		}else if(SubFlow.SUBFLOW_DEFINITION_SCRIPT.equals(subFlowNode.subFlowDefiType) && !StringUtil.isBlank(subFlowNode.subflowScript)){
			subFlowFlowId = (String)runner.run("subFlow:"+subFlowNode.name+" subFlowScript", StringUtil.dencodeHTML(subFlowNode.subflowScript));
		}
		//获取子流程绑定表单ID
		if(SubFlow.PARAM_PASSING_SHARE.equals(subFlowNode.paramPassingType)){
			subFlowFormId = instance.getDocument().getFormid();
		}else if(SubFlow.PARAM_PASSING_MAPPING.equals(subFlowNode.paramPassingType) || SubFlow.PARAM_PASSING_SCRIPT.equals(subFlowNode.paramPassingType)){
			subFlowFormId = subFlowNode.subFlowFormId;
		}
		//获取子流程实例创建数量
		if(SubFlow.NUMBER_SETING_CUSTOM.equals(subFlowNode.numberSetingType)){
			count = Integer.parseInt(subFlowNode.numberSetingContent);
		}else if(SubFlow.NUMBER_SETING_FIEDL.equals(subFlowNode.numberSetingType)){
			count = Integer.parseInt(instance.getDocument().getItemValueAsString(subFlowNode.numberSetingContent));
		}else if(SubFlow.NUMBER_SETING_SCRIPT.equals(subFlowNode.numberSetingType)){
			Object obj = runner.run("subFlow:"+subFlowNode.name+" numberSetingScript",  StringUtil.dencodeHTML(subFlowNode.numberSetingContent));
			if(obj !=null){
				count = Integer.parseInt(String.valueOf(obj));
			}
		}else if(SubFlow.NUMBER_SETING_GROUP_TOTAL.equals(subFlowNode.numberSetingType)){
			
			if(!StringUtil.isBlank(_subFlowApproverInfo)){
				Collection<Object> col = JsonUtil.toCollection(_subFlowApproverInfo);
				for(Iterator<Object> iter = col.iterator();iter.hasNext();){
					@SuppressWarnings("unchecked")
					Map<String, Object> item = (Map<String, Object>) iter.next();
					String nodeid = (String) item.get("nodeid");
					if(subFlowNode.id.equals(nodeid)){
						/*
						Object[] approvers =  (Object[]) item.get("approver");
						*/
						String approverstr = (String)item.get("approver");
						Collection<Object> approvers = JsonUtil.toCollection(approverstr);
						count = approvers.size();
						break;
					}
				}
			}
		}
		
		if(!StringUtil.isBlank(_subFlowApproverInfo)){
			params.setParameter("_subFlowNodeId", subFlowNode.id);
		}
		
		Form form = (Form) formProcess.doView(subFlowFormId);
		BillDefiVO subFlowVO = (BillDefiVO) flowProcess.doView(subFlowFlowId);
		Collection<FieldMappingItem>  fieldMappingInfo = subFlowNode.getFieldMappingInfo();
		
		if(form!=null && subFlowVO!=null && count>0){
			for(int i=0;i<count;i++,position++){
				createAndStartOneFlowStateRT(instance, form, subFlowVO, subFlowNode, token,position, fieldMappingInfo,runner, params, user);
			}
		}
		
		
		
		
		
	}
	
	/**
	 * 创建并启动单个子流程实例
	 * @param instance
	 * @param form
	 * @param subFlowVO
	 * @param subFlowNode
	 * @param token
	 * @param fieldMappingInfo
	 * @param params
	 * @param user
	 * @throws Exception
	 */
	public void createAndStartOneFlowStateRT(FlowStateRT instance,Form form,BillDefiVO subFlowVO,SubFlow subFlowNode,String token,int position, Collection<FieldMappingItem> fieldMappingInfo,IRunner runner,ParamsTable params,WebUser user) throws Exception{
		
		FlowStateRTProcess instanceProcess = (FlowStateRTProcess) ProcessFactory.createRuntimeProcess(FlowStateRTProcess.class, instance.getApplicationid());
		
		Document subFlowDoc = null;
		Document parentFlowDoc = instance.getDocument();
		FlowStateRT subFlowInstance = null;
		//参数传递-表单字段映射
		if(SubFlow.PARAM_PASSING_MAPPING.equals(subFlowNode.paramPassingType)){
			subFlowDoc = form.createDocument(new ParamsTable(), user);
			if(fieldMappingInfo !=null && !fieldMappingInfo.isEmpty()){
				for(FieldMappingItem mappingItem : fieldMappingInfo){
					subFlowDoc.findItem(mappingItem.getSubField()).setValue(parentFlowDoc.findItem(mappingItem.getParentField()).getValue());
				}
			}
			
		}else if(SubFlow.PARAM_PASSING_SCRIPT.equals(subFlowNode.paramPassingType)){//参数传递-脚本
			subFlowDoc = form.createDocument(new ParamsTable(), user);
			if(fieldMappingInfo !=null && !fieldMappingInfo.isEmpty()){
				for(FieldMappingItem mappingItem : fieldMappingInfo){
					Object result = runner.run("subFlow:"+subFlowNode.name+" filedMappingScript",  StringUtil.dencodeHTML(mappingItem.getScript()));
					subFlowDoc.findItem(mappingItem.getSubField()).setValue(result);
				}
			}
			
			
		}else if(SubFlow.PARAM_PASSING_SHARE.equals(subFlowNode.paramPassingType)){//共享父流程表单
			subFlowDoc = parentFlowDoc;
		}
		
		subFlowInstance = instanceProcess.createTransientSubFlowStateRT(subFlowDoc, subFlowVO, instance,token, subFlowNode,user);
		
		
		if(subFlowDoc !=null && subFlowInstance!=null){
			subFlowDoc.setState(subFlowInstance);
			subFlowInstance.setDocument(subFlowDoc);
			subFlowInstance.setPosition(position);
			params.setParameter("_position", position);
			Node firstNode = subFlowVO.getFirstNode();
			Node startNode = subFlowVO.getStartNodeByFirstNode(firstNode);
			
			instanceProcess.doApprove(params, subFlowInstance, startNode.id, new String[]{firstNode.id}, FlowType.RUNNING2RUNNING_NEXT, "", Environment.getInstance(), user);
		}
	}
	

	/**
	 * 处理跨表单
	 * 
	 * @param params
	 * @param origNodeRT
	 * @param doc
	 * @param flowVO
	 * @param user
	 * @param flowOption
	 * @return
	 * @throws Exception
	 * @deprecated since 2.6
	 */
	@Deprecated
	public NodeRT processCrossForm(ParamsTable params, NodeRT origNodeRT,FlowStateRT instance,
			WebUser user, String flowOption) throws Exception {
		SubFlow subFlowNode = (SubFlow) node;
		BillDefiProcess process = (BillDefiProcess) ProcessFactory.createProcess(BillDefiProcess.class);
		FormProcess formProcess = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
		DocumentProcess docProcess = (DocumentProcess) ProcessFactory.createRuntimeProcess(DocumentProcess.class,instance.getApplicationid());
		NodeRTProcess nodeRTProcess = new NodeRTProcessBean(instance.getApplicationid());

		BillDefiVO subFlowVO = (BillDefiVO) process.doView(subFlowNode.subflowid);
		Form subform = (Form) formProcess.doView(subFlowNode.subFlowFormId);
		Document subDoc = subform.createDocument(params, user);

		// 新建子流程状态
		FlowStateRT subStateRT = StateMachine.createFlowStateRT(subFlowVO, subDoc.getId(), instance);
		// 设置子文档属性
		subDoc.setState(subStateRT);
		subDoc.setFlowid(subFlowVO.getId());
		subDoc.setFlowVO(subFlowVO);

		// 执行startup脚本
		runScript(subFlowNode, params, instance.getDocument(), subDoc, instance.getFlowVO(), user);

		// 启动子流程
		Node firstNode = subFlowVO.getFirstNode();
		Node startNode = subFlowVO.getStartNodeByFirstNode(firstNode);
		//暂时注释
//		StateMachine.doFlow(params, subDoc, subFlowVO, startNode.id, new String[] { firstNode.id }, user, flowOption,
//				"", Environment.getInstance());//暂时屏蔽

		// 更新子流程当前节点状态
		NodeRT pnodert = super.process(params, origNodeRT, instance, user, flowOption);
		Collection<NodeRT> snoderts = subDoc.getState().getNoderts();
		for (Iterator<NodeRT> iterator = snoderts.iterator(); iterator.hasNext();) {
			NodeRT nodert = iterator.next();
			nodert.setParentNodertid(pnodert.getId());
			nodeRTProcess.doUpdate(nodert);
		}
		docProcess.doCreateOrUpdate(subDoc, user);

		return pnodert;
	}
	/**
	 * @param parentDoc
	 * @param user
	 * @return
	 * @throws Exception
	 * @deprecated since 2.6
	 */
	@Deprecated
	public FlowStateRT createFlowStateRT(Document parentDoc, WebUser user) throws Exception{
		BillDefiProcess process = (BillDefiProcess) ProcessFactory.createProcess(BillDefiProcess.class);
		FormProcess formProcess = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
		SubFlow subFlowNode = (SubFlow) this.node;
		
		BillDefiVO subFlowVO = (BillDefiVO) process.doView(subFlowNode.subflowid);
		Form subform = (Form) formProcess.doView(subFlowNode.subFlowFormId);
		ParamsTable params = new ParamsTable();
		Document subDoc = subform.createDocument(params, user);
		
		FlowStateRT subStateRT = StateMachine.createFlowStateRT(subFlowVO, subDoc.getId(), parentDoc.getState());
		// 设置子文档属性
		subDoc.setState(subStateRT);
		subDoc.setFlowid(subFlowVO.getId());
		subDoc.setFlowVO(subFlowVO);

		// 执行startup脚本
//		runScript(subFlowNode, params, doc, subDoc, flowVO, user);
//
//		// 启动子流程
//		Node firstNode = subFlowVO.getFirstNode();
//		Node startNode = subFlowVO.getStartNodeByFirstNode(firstNode);
//		StateMachine.doFlow(params, subDoc, subFlowVO, startNode.id, new String[] { firstNode.id }, user, flowOption,
//				"", Environment.getInstance());
		
		
		return subStateRT;
	}

	/**
	 * 处理同一表单
	 * 
	 * @param params
	 * @param origNodeRT
	 * @param doc
	 * @param flowVO
	 * @param user
	 * @param flowOption
	 * @return
	 * @throws Exception
	 * @deprecated since 2.6
	 */
	@Deprecated
	public NodeRT processSingleForm(ParamsTable params, NodeRT origNodeRT, FlowStateRT instance,
			WebUser user, String flowOption) throws Exception {
		SubFlow subFlowNode = (SubFlow) node;
		BillDefiProcess process = (BillDefiProcess) ProcessFactory.createProcess(BillDefiProcess.class);
		NodeRTProcess nodeRTProcess = new NodeRTProcessBean(instance.getApplicationid());

		BillDefiVO subFlowVO = (BillDefiVO) process.doView(subFlowNode.subflowid);
		// 执行startup脚本
		runScript(subFlowNode, params, instance.getDocument(), instance.getDocument(), instance.getFlowVO(), user);

		// 创建主流程当前节点
		NodeRT pNodeRT = nodeRTProcess.doCreate(params, origNodeRT, instance, node, flowOption,user);
		instance.getNoderts().add(pNodeRT);

		// 创建子流程当前节点
		Node firstNode = subFlowVO.getFirstNode();
		
		NodeRT newNodeRT = nodeRTProcess.doCreate(params, pNodeRT, instance, firstNode, flowOption,user);

		return newNodeRT;
	}

	/**
	 * 执行子流程startup脚本
	 * 
	 * @param node
	 * @param params
	 * @param doc
	 * @param flowVO
	 * @param user
	 * @throws Exception
	 */
	public void runScript(SubFlow node, ParamsTable params, Document doc, Document subDoc, BillDefiVO flowVO,
			WebUser user) throws Exception {
		IRunner runner = JavaScriptFactory.getInstance(params.getSessionid(), flowVO.getApplicationid());
		runner.initBSFManager(doc, params, user, new ArrayList<ValidateMessage>());
		// 存在多次注册问题
		runner.declareBean("$STARTUP_DOC", subDoc, Document.class);

		StringBuffer label = new StringBuffer();
		label.append("WorkFlow").append("." + flowVO.getSubject()).append(".Node(").append(node.id).append(
				")." + node.name).append(".StartupScript");

		String startupScript = StringUtil.dencodeHTML(node.startupScript);
		if (!StringUtil.isBlank(startupScript)) {
			runner.run(label.toString(), startupScript);
		}
	}

	public Collection<String> getPrincipalIdList(ParamsTable params, String domainId, String applicationid,WebUser auditor)
			throws Exception {
		SubFlow subFlowNode = (SubFlow) node;
		BillDefiProcess process = (BillDefiProcess) ProcessFactory.createProcess(BillDefiProcess.class);
		BillDefiVO subFlowVO = (BillDefiVO) process.doView(subFlowNode.subflowid);
		Node node = subFlowVO.getFirstNode();

		Collection<String> prinspalIdList = StateMachineUtil.getPrincipalIdList(params, node, domainId, applicationid,auditor);

		return prinspalIdList;
	}

	public int toInt() {
		return FlowState.SUBFLOW;
	}
	
	public SubFlow getTestSubNodeInfo(SubFlow node){
		node.subFlowDefiType =SubFlow.SUBFLOW_DEFINITION_CUSTOM;
		node.subflowid = "11e0-a94d-5743ed3d-b76b-6b0da868ee30";
		node.subFlowFormId = "11e0-a94c-9d3bb847-b76b-6b0da868ee30";
		node.numberSetingType = SubFlow.NUMBER_SETING_GROUP_TOTAL;
		node.numberSetingContent = "2";
		node.paramPassingType = SubFlow.PARAM_PASSING_SHARE;
		node.fieldMappingXML = "";
		node.callback = true;
		
		
		return node;
	}
	
	public void addTestParameter(ParamsTable params){
		String json = "[{'nodeid':'1311128156630','approver':[{'position':'1','userids':['11e0-a080-e47c1507-b8e3-416f0911c282','11de-c13a-0cf76f8b-a3db-1bc87eaaad4c']},{'position':'2','userids':['11e0-a080-e47c1507-b8e3-416f0911c282']}]},{'nodeid':'1310615303007','approver':[{'position':'1','userids':['11de-c13a-26b53fc4-a3db-1bc87eaaad4c','11de-c13a-26b53fc4-a3db-1bc87eaaad5c']},{'position':'2','userids':['11de-c13a-26b53fc4-a3db-1bc87eaaad4c','11de-c13a-26b53fc4-a3db-1bc87eaaad5c']}]}]";
		
		params.setParameter("_subFlowApproverInfo", json);
	}
}
