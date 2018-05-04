package OLink.bpm.core.workflow.storage.definition.ejb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;


import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.ejb.VersionSupport;
import OLink.bpm.core.deploy.module.ejb.ModuleVO;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.workflow.engine.State;
import OLink.bpm.core.workflow.engine.WFRunner;
import OLink.bpm.core.workflow.engine.state.StateCreator;
import OLink.bpm.core.workflow.element.Element;
import OLink.bpm.core.workflow.element.StartNode;
import OLink.bpm.core.workflow.element.SubFlow;
import OLink.bpm.core.workflow.element.FlowDiagram;
import OLink.bpm.core.workflow.element.Node;
import OLink.bpm.core.workflow.element.Relation;
import OLink.bpm.util.StringUtil;

/**
 * @hibernate.class table="T_BILLDEFI"
 */
public class BillDefiVO extends VersionSupport implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 728924410235215069L;

	private String authorno;

	private String authorname;

	private Date lastmodify;

	private String subject;

	private String flow;

	private String id;

	private String owner;

	private String type;

	private ModuleVO module;

	private transient FlowDiagram _flowDiagram;
	
	/**
	 * 是否签出
	 */
	private  boolean checkout = false;
	
	/**
	 * 签出者
	 */
	private String checkoutHandler;
	
	/**
	 * 是否被签出
	 * @return
	 */
	public boolean isCheckout() {
		return checkout;
	}

	/**
	 * 设置是否签出
	 * @param checkout
	 */
	public void setCheckout(boolean checkout) {
		this.checkout = checkout;
	}

	/**
	 * 获取签出者
	 * @return
	 */
	public String getCheckoutHandler() {
		return checkoutHandler;
	}

	/**
	 * 设置签出者
	 * @param checkoutHandler
	 */
	public void setCheckoutHandler(String checkoutHandler) {
		this.checkoutHandler = checkoutHandler;
	}

	/**
	 * 获取模块
	 * 
	 * @return ModuleVO
	 * @hibernate.many-to-one class="ModuleVO"
	 *                        column="MODULE"
	 */
	public ModuleVO getModule() {
		return module;
	}

	/**
	 * 设置模块
	 * 
	 * @param module
	 *            模块对象
	 */
	public void setModule(ModuleVO module) {
		this.module = module;
	}

	/**
	 * 获取作者
	 * 
	 * @hibernate.property column="AUTHORNAME"
	 * @return 作者
	 */
	public String getAuthorname() {
		return authorname;
	}

	/**
	 * 设置作者
	 * 
	 * @param authorname
	 *            作者
	 */
	public void setAuthorname(String authorname) {
		this.authorname = authorname;
	}

	/**
	 * @hibernate.property column="AUTHORNO"
	 * @return
	 */
	public String getAuthorno() {
		return authorno;
	}

	public void setAuthorno(String authorno) {
		this.authorno = authorno;
	}

	/**
	 * @hibernate.property column="FLOW" type="text" length = "100000"
	 * @return
	 */
	public String getFlow() {
		return flow;
	}
	public void setFlow(String flow) {
		this.flow = flow ;
	}

	/**
	 * @hibernate.property column="LASTMODIFY"
	 * @return
	 */
	public Date getLastmodify() {
		return lastmodify;
	}

	public void setLastmodify(Date lastmodify) {
		this.lastmodify = lastmodify;
	}

	/**
	 * @hibernate.property column="SUBJECT"
	 * @return
	 */
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @hibernate.property column="OWNER"
	 * @return
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * @param owner
	 *            The owner to set.
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * @hibernate.property column="TYPE"
	 * @return
	 */
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @hibernate.id column="ID" generator-class="assigned"
	 */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public FlowDiagram toFlowDiagram() {
		if (_flowDiagram == null) {
			WFRunner wfr = new WFRunner(this.getFlow(), getApplicationid());
			_flowDiagram = wfr.getFlowDiagram();
			_flowDiagram.setId(this.id);

			// 加载子流程图表元素
			//addElementsToSubFlow(_flowDiagram);
		}

		return _flowDiagram;
	}

	@SuppressWarnings("unused")
	private void addElementsToSubFlow(FlowDiagram flowDiagram) {
		Collection<SubFlow> subFlowList = flowDiagram.getSubFlowNodeList();
		BillDefiProcess defiProcess = new BillDefiProcessBean();
		try {
			for (Iterator<SubFlow> iterator = subFlowList.iterator(); iterator.hasNext();) {
				SubFlow subFlow = iterator.next();
				if (!SubFlow.PARAM_PASSING_SHARE.equals(subFlow.paramPassingType)) {
					BillDefiVO flow = (BillDefiVO) defiProcess.doView(subFlow.subflowid);
					FlowDiagram subFlowDiagram = flow.toFlowDiagram();
					subFlow._subelems = subFlowDiagram.getAllElements();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void clearDiagram() {
		_flowDiagram = null;
	}

	public Node findNodeById(String nodeid) throws Exception {
		FlowDiagram fd = this.toFlowDiagram();
		return fd.getNodeByID(nodeid);
	}

	public Node getFirstNode() {
		FlowDiagram fd = this.toFlowDiagram();
		// 获取所有开始结点
		Collection<StartNode> startNodeList = fd.getStartNodeList();
		for (Iterator<StartNode> iter = startNodeList.iterator(); iter.hasNext();) {
			StartNode startNode = iter.next();
			Collection<Node> nextNodeList = fd.getNextNodeList(startNode.id);

			for (Iterator<Node> iter2 = nextNodeList.iterator(); iter2.hasNext();) {
				Node nextNode = iter2.next();
				return nextNode;
			}
		}
		return null;
	}

	/**
	 * 根据用户获取流程的第一个节点
	 * 
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public Node getFirstNode(WebUser user) throws Exception {
		Collection<Node> firstNodeList = getFirstNodeList(user);
		if (firstNodeList != null && firstNodeList.size() > 0) {
			return firstNodeList.iterator().next();
		}
		return null;
	}

	/**
	 * 获取用户有审批权限的第一个节点列表
	 * 
	 * @param user
	 *            用户
	 * @return
	 * @throws Exception
	 */
	public Collection<Node> getFirstNodeList(WebUser user) throws Exception {
		ArrayList<Node> rtn = new ArrayList<Node>();
		FlowDiagram fd = this.toFlowDiagram();

		// 获取所有开始结点
		Collection<StartNode> startNodeList = fd.getStartNodeList();
		for (Iterator<StartNode> iter = startNodeList.iterator(); iter.hasNext();) {
			StartNode startNode = iter.next();
			Collection<Node> nextNodeList = fd.getNextNodeList(startNode.id);

			for (Iterator<Node> iter2 = nextNodeList.iterator(); iter2.hasNext();) {
				Node nextNode = iter2.next();
				State state = StateCreator.getNodeState(nextNode);
				Collection<String> prinspalIdList = state.getPrincipalIdList(new ParamsTable(), user.getDomainid(),
						this.getApplicationid(),user);
				// 当前节点负责人或管理员可获取此节点
				if (prinspalIdList.contains(user.getId()) || "admin".equals(user.getLoginno().toLowerCase())
						|| isAgent(prinspalIdList, this.getId(), user)) {
					rtn.add(nextNode);
				}
			}
		}
		return rtn;
	}

	public static boolean isAgent(Collection<String> prinspalIdList, String flowId, WebUser user) throws Exception {
		for (String userId : prinspalIdList) {
			if (user.isAgent(userId, flowId)) {
				return true;
			}
		}
		return false;

	}

	public Node getStartNodeByFirstNode(Node firstNode) {
		return (Node) getStartNodeListByFirstNode(firstNode).iterator().next();
	}

	public Collection<Element> getStartNodeListByFirstNode(Node firstNode) {
		ArrayList<Element> rtn = new ArrayList<Element>();
		// String flow = flowVO.getFlow();
		// WFRunner wfr = new WFRunner(flow);
		// FlowDiagram fd = wfr.getFlowDiagram();
		FlowDiagram fd = this.toFlowDiagram();

		// 获取所有endnodeid为firstNode的开始结点列表
		Collection<Element> ems = fd.getAllElements();
		for (Iterator<Element> iter = ems.iterator(); iter.hasNext();) {
			Element element = iter.next();
			if (element instanceof Relation) {
				Relation r = (Relation) element;
				if (r.endnodeid != null && r.endnodeid.equals(firstNode.id)) {
					Element em = fd.getElementByID(r.startnodeid);
					if (em != null && em instanceof StartNode) {
						rtn.add(em);
					}
				}
			}
		}
		return rtn;
	}

	public Collection<Node> getNextNodeList(String currnodeid) {
		FlowDiagram fd = this.toFlowDiagram();
		return fd.getNextNodeList(currnodeid);
	}

	public String[] getNextNodeids(String currnodeid) {
		FlowDiagram fd = this.toFlowDiagram();
		Collection<Node> nextNodeList = fd.getNextNodeList(currnodeid);
		String[] nodeids = new String[nextNodeList.size()];
		int index = 0;
		for (Iterator<Node> iterator = nextNodeList.iterator(); iterator.hasNext();) {
			Node node = iterator.next();
			nodeids[index] = node.id;
			index++;
		}

		return nodeids;
	}

	/**
	 * 获取下一个节点
	 * 
	 * @param currnodeid
	 *            当前节点ID
	 * @param nextNodeName
	 *            下一个节点名称
	 * @return 有匹配名称的则返回匹配的节点，没有则返回第一个节点
	 */
	public Node getNextNode(String currnodeid, String nextNodeName) {
		FlowDiagram fd = this.toFlowDiagram();
		Collection<Node> nextNodeList = fd.getNextNodeList(currnodeid);
		for (Iterator<Node> iterator = nextNodeList.iterator(); iterator.hasNext();) {
			Node node = iterator.next();
			if (!StringUtil.isBlank(nextNodeName) && nextNodeName.equals(node.name)) {
				return node;
			}
		}
		return nextNodeList.iterator().next();
	}
}
