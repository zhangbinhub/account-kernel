package OLink.bpm.core.workflow.storage.runtime.ejb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.constans.Environment;
import OLink.bpm.core.workflow.engine.StateMachine;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.document.ejb.DocumentProcess;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.user.ejb.BaseUser;
import OLink.bpm.core.workflow.element.Node;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiProcess;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiVO;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;

/**
 * 流程实例
 * 
 */
public class FlowStateRT extends ValueObject {
	private static final Logger LOG = Logger.getLogger(FlowStateRT.class);

	private static final long serialVersionUID = -8494020771756018964L;

	private String id;

	/**
	 * 当前流程ID
	 */
	private String flowid;

	/**
	 * 当前流程
	 */
	private BillDefiVO flowVO;

	/**
	 * 当前流程XML
	 */
	private String flowXML;

	/**
	 * 当前流程名称
	 */
	private String flowName;

	/**
	 * 当前流程最后修改日期
	 */
	private Date lastModified;

	/**
	 * 当前流程最后修改人ID
	 */
	private String lastModifierId;

	/**
	 * 当前文档ID
	 */
	private String docid;

	/**
	 * 文档
	 */
	private Document document;

	/**
	 * 当前状态代码
	 */
	private int state;

	/**
	 * 流程状态标签
	 */
	private String stateLabel;

	/**
	 * 最后审核时间
	 */
	private Date auditdate;

	/**
	 * 流程发起人姓名
	 */
	private String initiator;

	/**
	 * 最后审核人姓名
	 */
	private String audituser;

	/**
	 * 最后一次流程处理的代码
	 */
	private String lastFlowOperation;

	/**
	 * 当前审核人名称(以分","号隔开)
	 */
	private String auditorNames;

	/**
	 * 审批人列表
	 */
	private String auditorList;

	/**
	 * 当前处理人
	 */
	private Collection<ActorRT> actors;

	/**
	 * 当前节点集合
	 */
	private Collection<NodeRT> noderts;

	/**
	 * 父流程实例
	 */
	private FlowStateRT parent;

	/**
	 * 子流程节点ID
	 */
	private String subFlowNodeId;

	/**
	 * 子流程的所有实例是否都已经完成
	 */
	private boolean complete = false;

	/**
	 * 子流程是否回调
	 */
	private boolean callback = false;

	/**
	 * 令牌
	 */
	private String token;

	/**
	 * 子流程序号
	 */
	private int position;

	/**
	 * 瞬态流程实例
	 */
	private boolean temp = false;

	/**
	 * 异步启动父流程到下一结点的子流程节点ID
	 */
	private String asyncSubFlowNodeId;

	public String getSubFlowNodeId() {
		return subFlowNodeId;
	}

	public void setSubFlowNodeId(String subFlowNodeId) {
		this.subFlowNodeId = subFlowNodeId;
	}

	public boolean isComplete() {
		return complete;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}

	public boolean isCallback() {
		return callback;
	}

	public void setCallback(boolean callback) {
		this.callback = callback;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * 子状态集合
	 */
	private Collection<FlowStateRT> subStates;

	public String getLastModifierId() {
		return lastModifierId;
	}

	public void setLastModifierId(String lastModifierId) {
		this.lastModifierId = lastModifierId;
	}

	public String getFlowName() {
		return flowName;
	}

	public void setFlowName(String flowName) {
		this.flowName = flowName;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public Collection<FlowStateRT> getSubStates() {
		try {
			if (subStates == null) {
				FlowStateRTProcess process = new FlowStateRTProcessBean(
						getApplicationid());
				subStates = process.getSubStates(this.id);
			}
		} catch (Exception e) {
			LOG.error("getSubStates", e);
		}

		return subStates;
	}

	/**
	 * 获取流程状态的标识
	 * 
	 * @return 流程状态的标识
	 * @hibernate.id column="ID" generator-class="assigned"
	 */
	public String getId() {
		return id;
	}

	/**
	 * 设置流程状态的标识
	 * 
	 * @param id
	 *            流程状态的标识
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 获取文档标识
	 * 
	 * @return 文档标识
	 */
	public String getDocid() {
		return docid;
	}

	/**
	 * 设置文档标识
	 * 
	 * @param docid
	 *            文档标识
	 */
	public void setDocid(String docid) {
		this.docid = docid;
	}

	public Document getDocument() {
		if (this.document == null) {
			try {
				DocumentProcess process = (DocumentProcess) ProcessFactory
						.createRuntimeProcess(DocumentProcess.class,
								applicationid);
				this.setDocument((Document) process.doView(this.getDocid()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(document !=null){
			document.setState(this);
		}
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
		this.docid = document.getId();
		this.document.setState(this);
	}

	/**
	 * 获取状态标签
	 * 
	 * @return
	 */
	public String getStateLabel() {
		if (!StringUtil.isBlank(stateLabel))
			return stateLabel;
		StringBuffer labelBuffer = new StringBuffer();
		try {

			if (this.getNoderts() != null && !this.getNoderts().isEmpty()) {
				for (Iterator<NodeRT> iter = this.getNoderts().iterator(); iter
						.hasNext();) {
					NodeRT nodert = iter.next();
					labelBuffer.append(nodert.getStatelabel()).append(",");
				}
				labelBuffer.deleteCharAt(labelBuffer.lastIndexOf(","));
			}
			this.stateLabel = labelBuffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return stateLabel;
	}

	/**
	 * 设置状态标签
	 * 
	 * @param stateLabel
	 */
	public void setStateLabel(String stateLabel) {
		this.stateLabel = stateLabel;
	}

	/**
	 * 获取最后处理时间
	 * 
	 * @return
	 */
	public Date getAuditdate() {
		return auditdate;
	}

	/**
	 * 设置最后处理时间
	 * 
	 * @param auditdate
	 */
	public void setAuditdate(Date auditdate) {
		this.auditdate = auditdate;
	}

	/**
	 * 获取流程发起人
	 * 
	 * @return
	 */
	public String getInitiator() {
		return initiator;
	}

	/**
	 * 设置流程发起人
	 * 
	 * @param initiator
	 */
	public void setInitiator(String initiator) {
		this.initiator = initiator;
	}

	/**
	 * 获取最后审批人Id
	 * 
	 * @return
	 */
	public String getAudituser() {
		return audituser;
	}

	/**
	 * 设置最后审批人ID
	 * 
	 * @param audituser
	 */
	public void setAudituser(String audituser) {
		this.audituser = audituser;
	}

	/**
	 * 获取最后处理类型
	 * 
	 * @return
	 */
	public String getLastFlowOperation() {
		return lastFlowOperation;
	}

	/**
	 * 设置最后处理类型
	 * 
	 * @param lastFlowOperation
	 */
	public void setLastFlowOperation(String lastFlowOperation) {
		this.lastFlowOperation = lastFlowOperation;
	}

	/**
	 * 获取当前处理人名称
	 * 
	 * @return
	 */
	public String getAuditorNames() {
		if (!StringUtil.isBlank(auditorNames)) {
			return auditorNames;
		} else {
			auditorNames = calculateAuditorNames();
		}

		return auditorNames;

	}

	/**
	 * 重计算流程审批人
	 * 
	 * @return
	 */
	public String calculateAuditorNames() {
		StringBuffer names = new StringBuffer();
		try {
			if (this.getNoderts() == null || this.getNoderts().isEmpty())
				return "";
			for (Iterator<NodeRT> iterator = getNoderts().iterator(); iterator
					.hasNext();) {
				NodeRT nodeRT = iterator.next();
				if (nodeRT.getActorrts() != null
						&& !nodeRT.getActorrts().isEmpty()) {
					for (Iterator<ActorRT> iter = nodeRT.getActorrts()
							.iterator(); iter.hasNext();) {
						ActorRT actorRT = iter.next();
						names.append(actorRT.getName()).append(",");
					}

				}
			}
			if (names.length() > 0)
				names.setLength(names.length() - 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return names.toString();
	}

	/**
	 * 设置流程处理人名称
	 * 
	 * @param auditorNames
	 */
	public void setAuditorNames(String auditorNames) {
		this.auditorNames = auditorNames;
	}

	/**
	 * 获取流程当前处理人
	 * 
	 * @return
	 */
	public String getAuditorList() {
		if (StringUtil.isBlank(this.auditorList)) {
			try {
				auditorList = calculateAuditorList();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return auditorList;
	}

	/**
	 * 设置流程当前处理人
	 * 
	 * @param auditorList
	 */
	public void setAuditorList(String auditorList) {
		this.auditorList = auditorList;
	}

	/**
	 * 获取流程标识
	 * 
	 * @return 文档标识
	 */
	public String getFlowid() {
		return flowid;
	}

	/**
	 * 获取文档标识
	 * 
	 * @param flowid
	 *            文档标识
	 */
	public void setFlowid(String flowid) {
		this.flowid = flowid;
	}

	public BillDefiVO getFlowVO() {
		if (this.flowVO == null) {
			try {
				BillDefiProcess process = (BillDefiProcess) ProcessFactory
						.createProcess(BillDefiProcess.class);
				BillDefiVO flow = (BillDefiVO) process.doView(this.getFlowid());
				this.setFlowVO(flow);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		setFlowXML(flowVO);
		return flowVO;
	}
	
	public void setFlowXML(BillDefiVO flow){
		if (!StringUtil.isBlank(this.getFlowXML())) {
			if (this.getFlowName() != null) {
				flow.setSubject(this.getFlowName());
			}
			if (this.getLastModifierId() != null) {
				flow.setAuthorname(this.getLastModifierId());
			}
			if (this.getFlowXML() != null) {
				flow.setFlow(this.getFlowXML());
			}
			if (this.getLastModified() != null) {
				flow.setLastmodify(this.getLastModified());
			}
		}
	}

	public void setFlowVO(BillDefiVO flowVO) {
		this.flowVO = flowVO;
		this.flowid = flowVO.getId();
	}

	/**
	 * 获取流程状态
	 * 
	 * @return 状态
	 */
	public int getState() {
		return state;
	}

	/**
	 * 设置状态
	 * 
	 * @param state
	 *            状态
	 */
	public void setState(int state) {
		this.state = state;
	}

	/**
	 * 获取关联的角色集合
	 * 
	 * @return 角色集合
	 * @throws Exception
	 */
	public Collection<ActorRT> getActors() throws Exception {
		if (this.isComplete())
			return new ArrayList<ActorRT>();
		if (!this.isComplete() && (actors == null || actors.isEmpty())) {
			ActorRTProcess process = new ActorRTProcessBean(applicationid);
			actors = process.queryByFlowStateRT(id);
		}
		return actors;
	}

	/**
	 * 关联的角色集合
	 * 
	 * @param actors
	 *            角色集合
	 */
	public void setActors(Collection<ActorRT> actors) {
		this.actors = actors;
	}

	/**
	 * 获取流程XML
	 * 
	 * @return
	 */
	public String getFlowXML() {
		return flowXML;
	}

	/**
	 * 设置流程XML
	 * 
	 * @return
	 */
	public void setFlowXML(String flowXML) {
		this.flowXML = flowXML;
	}

	/**
	 * 获取子流程序号
	 * 
	 * @return
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * 设置子流程序号
	 * 
	 * @param index
	 */
	public void setPosition(int position) {
		this.position = position;
	}

	public boolean isTemp() {
		return temp;
	}

	public void setTemp(boolean temp) {
		this.temp = temp;
	}

	/**
	 * 获取关联节点集合
	 * 
	 * @return 节点集合
	 * @throws Exception
	 */
	public Collection<NodeRT> getNoderts() throws Exception {
		if (noderts == null) {
			NodeRTProcess process = new NodeRTProcessBean(this.applicationid);
			noderts = process.queryByFlowStateRT(this.id);
		}
		return noderts;
	}

	/**
	 * 刷新当前实例的 节点信息
	 * 
	 * @param currNodeRT
	 * @return
	 * @throws Exception
	 */
	public Collection<NodeRT> refreshNoderts(NodeRT currNodeRT)
			throws Exception {
		Collection<NodeRT> list = this.getNoderts();
		for (Iterator<NodeRT> iter = list.iterator(); iter.hasNext();) {
			NodeRT nodert = iter.next();
			if (nodert.getId().equals(currNodeRT.getId())) {
				list.remove(nodert);
				list.add(currNodeRT);
				break;
			}
		}
		this.noderts = list;
		return list;
	}

	/**
	 * 设置关联节点集合
	 * 
	 * @param noderts
	 *            节点集合
	 */
	public void setNoderts(Collection<NodeRT> noderts) {
		this.noderts = noderts;
	}

	/**
	 * 查找当前用户所负责的节点
	 * 
	 * @param user
	 *            web用户
	 * @return 节点对象
	 * @throws Exception
	 */
	public NodeRT getNodeRT(WebUser user) throws Exception {
		// 查找当前用户所负责的NodeRT
		if (getNoderts() != null) {
			Collection<NodeRT> nodertList = getNoderts();
			for (Iterator<NodeRT> iter = nodertList.iterator(); iter.hasNext();) {
				NodeRT nodert = iter.next();
				Iterator<ActorRT> it = nodert.getPendingActorRTList()
						.iterator();
				while (it.hasNext()) {
					ActorRT actorrt = it.next();
					if ((actorrt).isEquals(user)
							|| user.isAgent(actorrt.getActorid(), flowid)) {
						return nodert;
					}
				}
			}
		}
		return null;
	}

	/**
	 * 获取流程节点处理人的角色以逗号折分处理人的角色 (role1,role2,role3,role4)
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getAcotrIdListStr() throws Exception {
		StringBuffer listStr = new StringBuffer();
		for (Iterator<ActorRT> iterator = getActors().iterator(); iterator
				.hasNext();) {
			ActorRT actorRT = iterator.next();
			listStr.append(actorRT.getActorid());
			listStr.append(",");
		}
		if (!getActors().isEmpty()) {
			listStr.deleteCharAt(listStr.lastIndexOf(","));
		}
		return listStr.toString();

	}

	/**
	 * 获取当前状态所有节点审批用户列表
	 * 
	 * @return 当前状态所有节点审批用户列表
	 * @throws Exception
	 */
	public Collection<BaseUser> getAllUserList() throws Exception {
		Collection<BaseUser> rtn = new ArrayList<BaseUser>();

		for (Iterator<NodeRT> iterator = getNoderts().iterator(); iterator
				.hasNext();) {
			NodeRT nodeRT = iterator.next();
			Collection<BaseUser> userList = nodeRT.getUserList();
			rtn.addAll(userList);
		}

		return rtn;
	}

	/**
	 * 移除运行中节点
	 * 
	 * @param nodeid
	 *            节点ID
	 * @throws Exception
	 */
	public NodeRT removeNodeRT(String nodeid) throws Exception {
		for (Iterator<NodeRT> iterator = getNoderts().iterator(); iterator
				.hasNext();) {
			NodeRT nodeRT = iterator.next();
			if (nodeRT.getNodeid().equals(nodeid)) {
				getNoderts().remove(nodeRT);
				return nodeRT;
			}
		}

		return null;
	}

	public FlowStateRT getParent() {
		return parent;
	}

	public void setParent(FlowStateRT parent) {
		this.parent = parent;
	}

	/**
	 * 当前实例是否为子流程实例
	 * 
	 * @return
	 */
	public boolean isSubFlowStete() {

		return this.subFlowNodeId != null && this.parent != null;
	}

	public String getAsyncSubFlowNodeId() {
		return asyncSubFlowNodeId;
	}

	public void setAsyncSubFlowNodeId(String asyncSubFlowNodeId) {
		this.asyncSubFlowNodeId = asyncSubFlowNodeId;
	}

	public void startFlow(WebUser user, Document doc, ParamsTable params)
			throws Exception {
		FlowStateRT instance = doc.getState();
		Node firstNode = flowVO.getFirstNode();
		Node startNode = flowVO.getStartNodeByFirstNode(firstNode);
		StateMachine.doFlow(params, instance, startNode.id,
				new String[] { firstNode.id }, user, "", "",
				Environment.getInstance());
	}

	/**
	 * 通过查询获取审批人
	 * 
	 * @return 审批人 (JSON)
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String calculateAuditorList() throws Exception {
		Map<String, Collection<?>> auditorJSON = new JSONObject();
		Collection<NodeRT> noderts = getNoderts();
		for (Iterator<NodeRT> iterator = noderts.iterator(); iterator.hasNext();) {
			NodeRT nodeRT = iterator.next();
			auditorJSON.put(nodeRT.getNodeid(), nodeRT.getActorIdList());
		}
		return auditorJSON.toString();
	}

	/**
	 * 根据节点获取审批人
	 * 
	 * @return 审批人 (JSON)
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String calculateAuditorList(NodeRT nodeRT) throws Exception {
		Map<String, Collection<?>> auditorJSON = new JSONObject();
		auditorJSON.put(nodeRT.getNodeid(), nodeRT.getActorIdList());
		return auditorJSON.toString();
	}

	/**
	 * 根据节点获取审批人
	 * 
	 * @return 审批人
	 * @throws Exception
	 */
	public String calculateAuditorNames(NodeRT nodeRT) throws Exception {
		StringBuffer names = new StringBuffer();
		if (nodeRT.getActorrts() != null && !nodeRT.getActorrts().isEmpty()) {
			for (Iterator<ActorRT> iterator = nodeRT.getActorrts().iterator(); iterator
					.hasNext();) {
				ActorRT actorRT = iterator.next();
				names.append(actorRT.getName()).append(",");
			}
			if (names.length() > 0)
				names.setLength(names.length() - 1);
		}
		return names.toString();
	}

	public void addNodeRT(NodeRT nodeRT) throws Exception {
		for (Iterator<NodeRT> iterator = getNoderts().iterator(); iterator
				.hasNext();) {
			NodeRT vo = iterator.next();
			if (nodeRT.getNodeid().equals(vo.getNodeid())) {
				this.loopAction = true;
				getNoderts().remove(vo);
				NodeRTProcess nodeRTProcess = (NodeRTProcess) ProcessFactory.createRuntimeProcess(NodeRTProcess.class, getApplicationid());
				nodeRTProcess.doRemove(vo.getId());
				break;
			}
		}
		getNoderts().add(nodeRT);
	}
	
	/**
	 * 是否为节点循环提交动作
	 */
	private boolean loopAction = false;
	
	public boolean isLoopAction(){
		return this.loopAction;
	}
}
