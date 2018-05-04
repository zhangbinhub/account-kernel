package OLink.bpm.core.workflow.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.constans.Environment;
import OLink.bpm.core.dynaform.form.ejb.ValidateMessage;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.macro.runner.JavaScriptFactory;
import OLink.bpm.core.table.constants.MobileConstant;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.workflow.FlowType;
import OLink.bpm.core.workflow.engine.state.StateCreator;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import OLink.bpm.util.property.DefaultProperty;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.document.ejb.DocumentProcess;
import OLink.bpm.core.user.ejb.BaseUser;
import OLink.bpm.core.workflow.FlowState;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiProcess;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiVO;
import OLink.bpm.core.workflow.element.CompleteNode;
import OLink.bpm.core.workflow.element.Relation;
import OLink.bpm.core.workflow.element.StartNode;
import OLink.bpm.core.workflow.element.TerminateNode;
import OLink.bpm.core.workflow.storage.runtime.ejb.ActorHIS;
import OLink.bpm.core.workflow.storage.runtime.ejb.RelationHIS;
import OLink.bpm.core.workflow.storage.runtime.ejb.RelationHISProcess;
import org.apache.log4j.Logger;

import OLink.bpm.core.workflow.element.AbortNode;
import OLink.bpm.core.workflow.element.AutoNode;
import OLink.bpm.core.workflow.element.Element;
import OLink.bpm.core.workflow.element.FlowDiagram;
import OLink.bpm.core.workflow.element.ManualNode;
import OLink.bpm.core.workflow.element.Node;
import OLink.bpm.core.workflow.element.SubFlow;
import OLink.bpm.core.workflow.element.SuspendNode;
import OLink.bpm.core.workflow.storage.runtime.ejb.ActorRT;
import OLink.bpm.core.workflow.storage.runtime.ejb.FlowHistory;
import OLink.bpm.core.workflow.storage.runtime.ejb.FlowStateRT;
import OLink.bpm.core.workflow.storage.runtime.ejb.NodeRT;

/**
 * 
 * @author Marky
 * 
 */
public class StateMachineHelper {
	private static Logger LOG = Logger.getLogger(StateMachineHelper.class);

	public boolean isDisplySubmit = false; // 是否显示提交按钮

	public boolean isDisplyFlow = true; // 是否显示流程

	public boolean isFrontEdit;// 是否显示前台手动调整流程

	private static BillDefiProcess getBillDefiProcess() throws Exception {
		return StateMachine.getBillDefiProcess();
	}

	private static RelationHISProcess getRelationHISProcess(String applicationId)
			throws Exception {
		return StateMachine.getRelationHISProcess(applicationId);
	}

	public StateMachineHelper() {
	}

	public StateMachineHelper(Document doc) {
		initFlowImage(doc);
	}

	/**
	 * 初始化流程图
	 * 
	 * @param flowid
	 * @param docid
	 * @param request
	 */
	private void initFlowImage(Document doc) {
		try {
			FlowStateRT instance = doc.getState();
			if(instance==null)return;
			String path = DefaultProperty.getProperty("BILLFLOW_DIAGRAMPATH");
			Environment env = Environment.getInstance();
			String imgPath = env.getRealPath(path + "/" + instance.getId()
					+ ".jpg");

			File file = new File(imgPath);
			if (!file.exists()) {
				if (instance != null) {
					StateMachine.toJpegImage(instance, instance.getFlowVO()
							.toFlowDiagram());
					StateMachine.toFlowImage(instance, instance.getFlowVO()
							.toFlowDiagram());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据当前用户获取当前流程所有开始节点的下一个手动节点(第一个节点)的列表
	 * 
	 * @param flowid
	 * @param user
	 * 
	 * @return
	 * @throws Exception
	 */
	public Collection<Node> getFirstNodeList(String flowid, WebUser user)
			throws Exception {
		BillDefiVO flowVO = (BillDefiVO) getBillDefiProcess().doView(flowid);
		return flowVO.getFirstNodeList(user);
	}

	/**
	 * 根据第一个节点获取开始节点
	 * 
	 * @param flowid
	 * @param firstNode
	 * @return
	 * @throws Exception
	 */
	public Collection<StartNode> getStartNodeListByFirstNode(String flowid,
															 Node firstNode) throws Exception {
		BillDefiVO flowVO = (BillDefiVO) getBillDefiProcess().doView(flowid);
		return StateMachine.getStartNodeListByFirstNode(flowVO, firstNode);
	}

	/**
	 * 根据当前节点id获取回退节点列表
	 * 
	 * @param currid
	 * @param user
	 * 
	 * @return
	 * @throws Exception
	 */
	public Collection<Node> getBackToNodeList(Document doc, String currid,
			WebUser user) throws Exception {
		return getBackToNodeList(doc, currid, user, 0);
	}

	/**
	 * 获取回退节点（按历史痕迹回退|定制回退）
	 * 
	 * @param doc
	 * @param currid
	 * @param user
	 * @param flowState
	 * @return
	 * @throws Exception
	 */
	public Collection<Node> getBackToNodeList(Document doc, String currid,
											  WebUser user, int flowState) throws Exception {
		BillDefiVO flowVO = null;
		Collection<Node> backToNodeList = new ArrayList<Node>();
		flowVO = doc.getFlowVO();

		FlowDiagram fd = flowVO.toFlowDiagram();
		Node node = (Node) fd.getElementByID(currid);
		// 手工节点回退
		if (node instanceof ManualNode) {
			if (((ManualNode) node).backType == 1 && ((ManualNode) node).cBack) {
				backToNodeList = StateMachine.getBackToNodeList(flowVO, currid,
						user, 0);
			} else if (((ManualNode) node).backType == 0
					&& ((ManualNode) node).cBack) {
				backToNodeList = StateMachine.getBackToNodeList(doc, flowVO,
						currid, user, flowState);
			} else if (((ManualNode) node).retracementScript == null) {// 兼容旧版本
				backToNodeList = StateMachine.getBackToNodeList(doc, flowVO,
						currid, user, flowState);
			}
		} else {// 非手工节点回退(如：挂起)
			backToNodeList = StateMachine.getBackToNodeList(doc, flowVO,
					currid, user, flowState);
		}

		return backToNodeList;

	}

	/**
	 * 获取当前结点
	 * 
	 * @param flowid
	 * @param nodeid
	 * @return
	 * @throws Exception
	 */
	public static Node getCurrNode(String flowid, String nodeid)
			throws Exception {
		BillDefiProcess process = (BillDefiProcess) ProcessFactory
				.createProcess(BillDefiProcess.class);
		BillDefiVO flowVO = (BillDefiVO) process.doView(flowid);
		return getCurrNode(flowVO, nodeid);
	}

	/**
	 * 获取当前结点
	 * 
	 * @param flowid
	 * @param nodeid
	 * @return
	 * @throws Exception
	 */
	public static Node getCurrNode(BillDefiVO flowVO, String nodeid)
			throws Exception {
		return StateMachine.getCurrNode(flowVO, nodeid);
	}

	/**
	 * 获取所有运行时节点
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Collection<NodeRT> getAllNodeRT(Document doc)
			throws Exception {
		if (doc != null && doc.getState() != null) {
			return doc.getState().getNoderts();
		}
		return new ArrayList<NodeRT>();
	}

	/**
	 * 获取当前流程所有历史记录
	 * 
	 * @param docid
	 * @param flowid
	 * @return
	 * @throws Exception
	 */
	public static Collection<RelationHIS> getAllRelationHIS(String docid,
															String flowid, String applicationId) throws Exception {
		return getRelationHISProcess(applicationId).doQuery(docid, flowid);

	}

	/**
	 * 根据进行挂起操作的用户获取运行时节点
	 * 
	 * @return 根据进行挂起操作的用户,运行时的节点
	 * @throws Exception
	 */
	public NodeRT getSuspendNodeRT(Document doc, String applicationId)
			throws Exception {
		// 获取最后一条历史记录
		RelationHIS relationHIS = getRelationHISProcess(applicationId)
				.doViewLast(doc.getId(), doc.getFlowid());
		// 此历史记录只有唯一用户历史记录
		Collection<ActorHIS> actorhissList = relationHIS.getActorhiss();
		ActorHIS actorhis = actorhissList.iterator().next();

		// 获取所有NodeRT
		Collection<NodeRT> nodertList = getAllNodeRT(doc);

		// 获取只有一个处理者的NodeRT列表
		Collection<NodeRT> newNodertList = new ArrayList<NodeRT>();
		for (Iterator<NodeRT> iter = nodertList.iterator(); iter.hasNext();) {
			NodeRT nodert = iter.next();
			Collection<ActorRT> actorrtList = nodert.getActorrts();
			if (actorrtList.size() == 1) {
				newNodertList.add(nodert);
			}
		}

		// 根据进行挂起操作的用户获取NodeRT
		if (newNodertList != null) {
			for (Iterator<NodeRT> iter = newNodertList.iterator(); iter
					.hasNext();) {
				NodeRT nodert = iter.next();
				ActorRT actort = nodert.getActorrts().iterator()
						.next();
				if ((actorhis.getActorid()).equals(actort.getActorid())) {
					return nodert;
				}
			}
		}
		return null;
	}

	/**
	 * 判断当前用户是否可编辑文档. 此实现为通过当前用户、此Document与相应的流程获取当前用户节点是否为空 或根据此Document id
	 * 与相应的流程id来获取当前流程状态是否为0(FlowState.START)作为判断. 若返回true代表用户可以对文档编辑,否则不可以.
	 * 
	 * @param doc
	 * 
	 * @param webUser
	 *            当前用户
	 * 
	 * @return 如果是流程处理者则返回true,否则返回false
	 * @throws Exception
	 */
	public static boolean isDocEditUser(final Document doc, WebUser webUser)
			throws Exception {
		Document flowDoc = doc;
		if (doc == null)
			return false;
		if (doc.getParent() != null) {
			flowDoc = doc.getParent();
		}
		NodeRT nodert = StateMachine.getCurrUserNodeRT(flowDoc, webUser);

		// 流程尚未启动或当前用户为审批人时可编辑
		return flowDoc.getState() == null || nodert != null;
	}

	/**
	 * 返回的字符串为重定义后的XML，表达显示当前用户运行时节点
	 * 
	 * @param flowid
	 *            flow id
	 * @param docid
	 *            Document id
	 * @param webUser
	 *            webuser
	 * @return 字符串为显示当前用户运行时节点
	 * @throws Exception
	 */
	public String toFlowXMLText(Document doc, WebUser webUser) throws Exception {
		StringBuffer buffer = new StringBuffer();
		BillDefiVO flowVO = doc.getFlowVO();

		NodeRT nodert = null;
		FlowDiagram fd = null;
		if (flowVO != null) {
			fd = flowVO.toFlowDiagram();
			nodert = StateMachine.getCurrUserNodeRT(doc, webUser);
		}
		// 获取当前结点
		Node currnode = null;
		State state = null; // 当前节点状态
		if (nodert != null) {
			String currnodeid = nodert.getNodeid();
			if (currnodeid != null) {
				currnode = (Node) fd.getElementByID(currnodeid);
				state = StateCreator.getNodeState(currnode);
			}
			buffer.append("<").append(MobileConstant.TAG_FORM).append(" ");
			buffer.append(" ").append(MobileConstant.ATT_TITLE).append("='")
					.append("{*[Workflow]*}").append("'>");
			buffer.append("<").append(MobileConstant.TAG_WORKFLOW).append(">");
			buffer.append("<").append(MobileConstant.TAG_HIDDENFIELD).append(
					" ").append(MobileConstant.ATT_NAME).append(
					"='_currid' >" + currnodeid + "</").append(
					MobileConstant.TAG_HIDDENFIELD).append(">");

			if (state != null && state.toInt() == FlowState.RUNNING) {// 送下一步
				isDisplySubmit = true;

				Collection<Node> nextNodeList = fd.getNextNodeList(currnodeid);

				if (nextNodeList != null && nextNodeList.size() > 0) {
					Iterator<Node> it3 = nextNodeList.iterator();
					StringBuffer buf1 = new StringBuffer();
					StringBuffer buf2 = new StringBuffer();
					while (it3.hasNext()) {

						Node nextNode = it3.next();
						Node node = getCurrNode(flowVO, nodert.getNodeid());
						boolean issplit = false;
						boolean isgather = false;
						if (node != null && node instanceof ManualNode) {
							issplit = ((ManualNode) node).issplit;
							isgather = ((ManualNode) node).isgather;
						}

						boolean flag = false;
						if (isgather) {// 如果为聚合
							Collection<NodeRT> nodertList = getAllNodeRT(doc);
							if (nodertList != null) {
								for (Iterator<NodeRT> iter = nodertList
										.iterator(); iter.hasNext();) {
									NodeRT nrt = iter.next();

									// 判断所有运行时节点是否为当前节点
									if (!nrt.getNodeid().equals(
											nodert.getNodeid())) {
										Node nd = (Node) fd.getElementByID(nrt
												.getNodeid());

										Node currNode = (Node) fd
												.getElementByID(nodert
														.getNodeid());

										Collection<Node> followTo = fd
												.getAllFollowNodeOnPath(nd.id);
										if (followTo != null
												&& followTo.contains(currNode)) {
											flag = true;
											break;
										}
									}
								}
							}
						}

						if (!flag) {
							@SuppressWarnings("unused")
							boolean isOthers = false;
							@SuppressWarnings("unused")
							String id = "next";
							@SuppressWarnings("unused")
							String flowOperation = FlowType.RUNNING2RUNNING_NEXT;
							if (!(nextNode instanceof ManualNode)) {// 下一个节点中是否存在suspend
								isOthers = true;
								id = "other";
								if (nextNode instanceof SuspendNode) {
									flowOperation = FlowType.RUNNING2SUSPEND;
								} else if (nextNode instanceof AbortNode) {
									flowOperation = FlowType.RUNNING2ABORT;
								} else if (nextNode instanceof TerminateNode) {
									flowOperation = FlowType.RUNNING2TERMIATE;
								} else if (nextNode instanceof CompleteNode) {
									flowOperation = FlowType.RUNNING2COMPLETE;
								}
							}
							if (issplit) {
								buf1.append("<").append(
										MobileConstant.TAG_OPTION).append("");
								buf1.append(" ").append(
										MobileConstant.ATT_VALUE).append(
										"='" + nextNode.id + "' >");
								buf1.append(nextNode.name);
								buf1.append("</").append(
										MobileConstant.TAG_OPTION).append(">");
							} else {
								buf2.append("<").append(
										MobileConstant.TAG_OPTION).append("");
								buf2.append(" ").append(
										MobileConstant.ATT_VALUE).append(
										"='" + nextNode.id + "' >");
								buf2.append(nextNode.name);
								buf2.append("</").append(
										MobileConstant.TAG_OPTION).append(">");
							}
						}

					}
					if (buf1.toString().trim().length() > 0) {
						buffer
								.append("<")
								.append(MobileConstant.TAG_CHECKBOXFIELD)
								.append("  ")
								.append(MobileConstant.ATT_NAME)
								.append("='_nextids'>" + buf1.toString() + "</")
								.append(MobileConstant.TAG_CHECKBOXFIELD)
								.append(">");
					}
					if (buf2.toString().trim().length() > 0) {
						buffer.append("<")
								.append(MobileConstant.TAG_RADIOFIELD).append(
										"  ").append(MobileConstant.ATT_NAME)
								.append("='_nextids'>");
						buffer.append(buf2);
						buffer.append("</").append(
								MobileConstant.TAG_RADIOFIELD).append(">");
					}
					// buffer.append("</NEXTTO>");
				}
				Collection<Node> backNodeList = getBackToNodeList(doc,
						currnode.id, webUser);
				if (backNodeList != null && backNodeList.size() > 0) {
					// buffer.append("<RETURNTO ")
					// .append(MobileConstant.ATT_LABEL).append(
					// " = '{*[Return To]*}:'>");
					buffer.append("<").append(MobileConstant.TAG_SELECTFIELD)
							.append(" ").append(MobileConstant.ATT_NAME)
							.append("='_nextids'>");

					buffer.append("<").append(MobileConstant.TAG_OPTION)
							.append(" ").append(MobileConstant.ATT_VALUE)
							.append("=''>");
					buffer.append("{*[Please]*} {*[Choose]*}");
					buffer.append("</").append(MobileConstant.TAG_OPTION)
							.append(">");
					for (Iterator<Node> iter = backNodeList.iterator(); iter
							.hasNext();) {
						Node backNode = iter.next();
						buffer.append("<").append(MobileConstant.TAG_OPTION)
								.append(" ").append(MobileConstant.ATT_VALUE)
								.append("='" + backNode.id + "'>");
						buffer.append(backNode.name);
						buffer.append("</").append(MobileConstant.TAG_OPTION)
								.append(">");
					}
					buffer.append("</").append(MobileConstant.TAG_SELECTFIELD)
							.append(">");
					// .append("></RETURNTO>");
				}
			} else if (state != null && state.toInt() == FlowState.SUSPEND) {
				Collection<Node> backNodeList = this.getBackToNodeList(doc,
						currnode.id, webUser, FlowState.SUSPEND);
				backNodeList = StateMachine.removeDuplicateNode(backNodeList);
				isDisplySubmit = true;

				if (backNodeList != null) {
					Iterator<Node> it4 = backNodeList.iterator();
					buffer.append("<").append(MobileConstant.TAG_RADIOFIELD)
							.append(" ").append(MobileConstant.ATT_LABEL)
							.append(" = '{*[Resume]*} {*[Flow]*}'  ").append(
									MobileConstant.ATT_NAME).append(
									"='_nextids'>");
					while (it4.hasNext()) {
						Node backNode = it4.next();
						buffer.append("<").append(MobileConstant.TAG_OPTION)
								.append(" ").append(MobileConstant.ATT_VALUE)
								.append("='" + backNode.id + "' ");
						buffer.append(">");
						buffer.append(backNode.name);
						buffer.append("</").append(MobileConstant.TAG_OPTION)
								.append(">");
					}
					buffer.append("</").append(MobileConstant.TAG_RADIOFIELD)
							.append(">");
				}
			}
			if (doc.getState() != null) {
				buffer.append("<").append(MobileConstant.TAG_TEXTAREAFIELD)
						.append(" ").append(MobileConstant.ATT_LABEL).append(
								" = '{*[Approve]*}{*[Remarks]*}' ").append(
								MobileConstant.ATT_NAME).append(
								"='_attitude'></").append(
								MobileConstant.TAG_TEXTAREAFIELD).append(">");
			}
			buffer.append("</").append(MobileConstant.TAG_WORKFLOW).append(">");
			buffer.append("</").append(MobileConstant.TAG_FORM).append(">");
		}

		return buffer.toString();
	} // 流程处理者

	public String toFlowDialogHtmlText(Document doc, WebUser webUser,
			String flowShowType) throws Exception {
		StringBuffer buffer = new StringBuffer();
		Environment evn = Environment.getInstance();

		BillDefiVO flowVO = doc.getFlowVO();
		NodeRT nodert = null;
		FlowDiagram fd = null;
		boolean isToPerson = false;
		boolean needToPersion = false;
		boolean isAppentCirculator = false;
		if (flowVO != null) {
			fd = flowVO.toFlowDiagram();
			nodert = StateMachine.getCurrUserNodeRT(doc, webUser);
		}
		// 获取当前结点
		Node currnode = null;
		State state = null;
		if (nodert != null) {
			String currnodeid = nodert.getNodeid();
			if (currnodeid != null) {
				currnode = (Node) fd.getElementByID(currnodeid);
				state = StateCreator.getNodeState(currnode);
			}

			buffer.append("<input type='hidden' name='_currid' value='"
					+ currnodeid + "'>");
		}

		buffer.append("");

		if (state != null && state.toInt() == FlowState.RUNNING) {// 送下一步
			isDisplySubmit = true;
			Collection<Node> nextNodeList = fd.getNextNodeList(currnode.id);

			// 判断是否能通过
			Node node = (Node) fd.getElementByID(nodert.getNodeid());
			boolean issplit = false;

			// checkedState判断在html拼接的时候是否需要为checkbox 和 radio 添加"默认选中"标识
			boolean checkedState = true;

			if (node != null && node instanceof ManualNode) {
				issplit = ((ManualNode) node).issplit;
			}

			if (nextNodeList != null && nextNodeList.size() > 0) {
				Iterator<Node> it3 = nextNodeList.iterator();
				int imgid = 0;
				while (it3.hasNext()) {
					buffer.append("<tr>");
					buffer
							.append("<td class='flow-next' style='width:20%;white-space:nowrap;word-break:keep-all'>{*[Commit]*}{*[To]*}:</td>");
					Node nextNode = it3.next();

					boolean isOthers = false;
					String id = "next";
					String flowOperation = FlowType.RUNNING2RUNNING_NEXT;
					if (!(nextNode instanceof ManualNode)) {// 下一个节点中是否存在suspend
						isOthers = true;
						id = "other";
						if (nextNode instanceof SuspendNode) {
							flowOperation = FlowType.RUNNING2SUSPEND;
						} else if (nextNode instanceof AbortNode) {
							flowOperation = FlowType.RUNNING2ABORT;
						} else if (nextNode instanceof TerminateNode) {
							flowOperation = FlowType.RUNNING2TERMIATE;
						} else if (nextNode instanceof CompleteNode) {
							flowOperation = FlowType.RUNNING2COMPLETE;
						} else if (nextNode instanceof AutoNode) {
							flowOperation = FlowType.RUNNING2AUTO;
						}
					}
					// TD1
					buffer.append("<td>");
					buffer.append("<input id='" + id + "' type='");
					buffer.append(issplit ? "checkbox' checked" : "radio'");
					// 如果是单选按钮
					if (!issplit && checkedState) {
						buffer.append(" checked");
						checkedState = false;
					}
					buffer.append(" name='_nextids' value='" + nextNode.id);
					buffer.append("' onclick='ev_setFlowType(" + isOthers);
					buffer.append(", this, " + flowOperation + "," + imgid
							+ ")' />" + nextNode.name);
					
					if ((nextNode instanceof ManualNode)
							&& ((ManualNode) nextNode).isToPerson) {// 手动节点指定审批人
						Collection<BaseUser> userList = StateMachineUtil.getPrincipalList(new ParamsTable(), nextNode, doc.getDomainid(), doc.getApplicationid(),webUser);
						if(userList !=null && userList.size()>1){//当下一步审批人超过1人时才出现指定审批人操作
							needToPersion = true;
						}
					}
					if ((nextNode instanceof ManualNode)
							&& ((ManualNode) currnode).isToPerson && needToPersion) {// 编辑审批人
						isToPerson = true;
						buffer.append("&nbsp");
						buffer.append("{*[Specify]*}{*[Auditor]*}:&nbsp");
						buffer.append("<span id='opra_" + imgid
								+ "' style='display:inline;'>");
						buffer.append("<input id='input_" + imgid
								+ "' name='input_" + imgid
								+ "' readonly='true' type='text' />");

						buffer
								.append("<img id='selectUserImg_"
										+ imgid
										+ "' style='cursor:pointer;display:inline;' onclick=\"showUserSelect('actionName', {nextNodeId:'"
										+ nextNode.id
										+ "', docid:'"
										+ doc.getId()
										+ "',flowid:'"
										+ flowVO.getId()
										+ "', textField:'input_"
										+ imgid
										+ "',valueField: 'input_"
										+ imgid
										+ "', readonly: false})\" src='"
										+ evn.getContextPath()
										+ "/script/dialog/images/userselect.gif' /> ");
						buffer.append("</span>");
					} else if ((nextNode instanceof SubFlow)
							&& ((SubFlow) nextNode).isToPerson) {// 子流程节点指定审批人
						isToPerson = true;
						String numberSetingType = ((SubFlow) nextNode).numberSetingType;
						int instanceTotal = getSubFlowInstanceTotal(
								(SubFlow) nextNode, doc, webUser);
						buffer.append("&nbsp");
						buffer.append("{*[Specify]*}{*[Auditor]*}:&nbsp");
						buffer.append("<span id='opra_" + imgid
								+ "' style='display:inline;'>");
						buffer.append("<input id='input_" + imgid
								+ "' name='input_" + imgid
								+ "' readonly='true' type='text' />");

						buffer
								.append("<img id='selectUserImg_"
										+ imgid
										+ "' style='cursor:pointer;display:inline;' onclick=\"showUserSelectOnSubFlow('actionName', {nextNodeId:'"
										+ nextNode.id
										+ "',instanceId:'"
										+ doc.getStateid()
										+ "',numberSetingType:'"
										+ numberSetingType
										+ "',instanceTotal:'"
										+ instanceTotal
										+ "', docid:'"
										+ doc.getId()
										+ "',flowid:'"
										+ flowVO.getId()
										+ "', textField:'input_"
										+ imgid
										+ "',valueField: 'input_"
										+ imgid
										+ "', readonly: false})\" src='"
										+ evn.getContextPath()
										+ "/script/dialog/images/userselect.gif' /> ");
						buffer.append("</span>");
					}
					buffer.append("</td>");

					// 指定流程抄送人-------------------------------------------start

					buffer.append("<td >");
					if ((currnode instanceof ManualNode) && ((ManualNode) currnode).isCarbonCopy
							&& ((ManualNode) currnode).isSelectCirculator && !isAppentCirculator) {// 手动节点指定抄送人
						isToPerson = true;
						isAppentCirculator = true;
						buffer.append("{*[抄送给]*}:&nbsp");
						buffer.append("<span id='opra_" + imgid
								+ "' style='display:inline;'>");
						buffer
								.append("<input id='_circulator"
										+ "' name='_circulator"
										+ "' readonly='true'  type='text'  size='10' />");

						buffer
								.append("<img id='selectUserImg_"
										+ imgid
										+ "' style='cursor:pointer;display:inline;' onclick=\"selectCirculator('actionName', {nextNodeId:'"
										+ currnode.id
										+ "', docid:'"
										+ doc.getId()
										+ "',flowid:'"
										+ flowVO.getId()
										+ "', textField:'_circulator"
										+ "',valueField: '_circulator"
										+ "', readonly: false})\" src='"
										+ evn.getContextPath()
										+ "/script/dialog/images/userselect.gif' /> ");
						buffer.append("</span>");
					}
					buffer.append("</td>");

					// 指定流程抄送人-------------------------------------------end

					buffer.append("</tr>");

					imgid++;
				}
			}

			Collection<Node> backNodeList = getBackToNodeList(doc, currnode.id,
					webUser);
			if (backNodeList != null && backNodeList.size() > 0) {
				buffer
						.append("<td style='white-space:nowrap;word-break:keep-all' class='commFont flow-back'>{*[Return]*}{*[To]*}:");
				buffer.append("</td>");
				buffer.append("<td>");
				buffer
						.append("<select class='flow-back' id='back' name='_nextids'");
				buffer.append(" onchange='ev_setFlowType(false, this, "
						+ FlowType.RUNNING2RUNNING_BACK + ")'>");
				buffer.append("<option value=''>");
				buffer.append("{*[Please]*}{*[Choose]*}");
				buffer.append("</option>");
				for (Iterator<Node> iter = backNodeList.iterator(); iter
						.hasNext();) {
					Node backNode = iter.next();
					buffer.append("<option value='" + backNode.id + "'>");
					buffer.append(backNode.name);
					buffer.append("(").append(backNode.statelabel).append(")");
					buffer.append("</option>");
				}
				buffer.append("</select>");
				if (((ManualNode) currnode).isToPerson) {// 编辑审批人
					isToPerson = true;
					String imgid = "back";
					buffer.append("&nbsp;&nbsp;");
					buffer
							.append("<span id='opra_"
									+ imgid
									+ "' style='display:inline;'>{*[Specify]*}{*[Auditor]*}:&nbsp;");
					buffer.append("<input id='input_" + imgid
							+ "' name='input_" + imgid
							+ "' readonly='true' type='text' />");

					buffer
							.append("<img id='selectUserImg_"
									+ imgid
									+ "' style='cursor:pointer;display:inline;' onclick=\"showBackUserSelect('"
									+ doc.getId()
									+ "','"
									+ flowVO.getId()
									+ "')\" src='"
									+ evn.getContextPath()
									+ "/script/dialog/images/userselect.gif' /> ");
					buffer.append("</span>");
				}
				buffer.append("</td>");
			}
		} else if (state != null && state.toInt() == FlowState.SUSPEND) {
			Collection<Node> backNodeList = this.getBackToNodeList(doc,
					currnode.id, webUser, FlowState.SUSPEND);
			backNodeList = StateMachine.removeDuplicateNode(backNodeList);
			isDisplySubmit = true;

			if (backNodeList != null) {
				Iterator<Node> it4 = backNodeList.iterator();
				buffer
						.append("<td class='flow-next'>{*[Resume]*} {*[Flow]*}: </td>");
				buffer.append("<td>");
				while (it4.hasNext()) {
					Node backNode = it4.next();
					buffer
							.append("<input id='suspend' type='radio' name='_nextids' value='"
									+ backNode.id + "' ");
					buffer.append("' onclick='ev_setFlowType(false, this, ");
					buffer.append(FlowType.SUSPEND2RUNNING + ")' />");
					buffer.append(backNode.name);
				}
				buffer.append("</td>");
			}
		} else {
			isDisplySubmit = false;
			if (state != null && state.toInt() != FlowState.RUNNING) {
				if (state.toInt() == FlowState.START) {
					isDisplyFlow = false;
				} else {
					buffer
							.append("<td style='font-size:12px;font-family: Arial, Helvetica;color:#FF0000'>"
									+ doc.getStateLabel() + "</td>");
				}
			} else {
				buffer
						.append("<td style='font-size:12px;font-family: Arial, Helvetica;'>");
				buffer.append(doc.getStateLabel());
				buffer.append("</td>");
			}
		}

		if (nodert != null) {
			if ((currnode instanceof ManualNode)) {// 编辑审批人
				buffer
						.append("<input id='isToPerson' type='hidden' name='isToPerson' value='"
								+ isToPerson + "' />");
			}
		}

		return buffer.toString();
	}

	/**
	 * 获取子流程实例创建总数
	 * 
	 * @param node
	 * @param doc
	 * @param webUser
	 * @return
	 * @throws Exception
	 */
	public int getSubFlowInstanceTotal(SubFlow subFlowNode, Document doc,
			WebUser webUser) throws Exception {
		int count = 0;
		IRunner runner = JavaScriptFactory.getInstance("", doc
				.getApplicationid());
		runner.initBSFManager(doc, new ParamsTable(), webUser,
				new ArrayList<ValidateMessage>());

		if (SubFlow.NUMBER_SETING_CUSTOM.equals(subFlowNode.numberSetingType)) {
			count = Integer.parseInt(subFlowNode.numberSetingContent);
		} else if (SubFlow.NUMBER_SETING_FIEDL
				.equals(subFlowNode.numberSetingType)) {
			count = Integer.parseInt(doc
					.getItemValueAsString(subFlowNode.numberSetingContent));
		} else if (SubFlow.NUMBER_SETING_SCRIPT
				.equals(subFlowNode.numberSetingType)) {
			Object obj = runner.run("subFlow:" + subFlowNode.name
					+ " numberSetingScript", StringUtil
					.dencodeHTML(subFlowNode.numberSetingContent));
			if (obj != null) {
				count = Integer.parseInt(String.valueOf(obj));
			}
		}

		return count;
	}

	/**
	 * 返回的字符串为重定义后的HTML，表达显示当前用户运行时节点
	 * 
	 * @param flowid
	 *            flow id
	 * @param docid
	 *            Document id
	 * @param webUser
	 *            webuser
	 * @return 字符串为显示当前用户运行时节点
	 * @throws Exception
	 */
	public String toFlowHtmlText(Document doc, WebUser webUser,
			String flowShowType) throws Exception {
		StringBuffer buffer = new StringBuffer();
		Environment evn = Environment.getInstance();

		BillDefiVO flowVO = doc.getState().getFlowVO();
		NodeRT nodert = null;
		FlowDiagram fd = null;
		boolean isToPerson = false;
		if (flowVO != null) {
			fd = flowVO.toFlowDiagram();
			//add by lr for set fd.sessionid;
			fd._sessionid=doc.get_params().getSessionid();
			nodert = StateMachine.getCurrUserNodeRT(doc, webUser);
		}
		// 获取当前结点
		Node currnode = null;
		State state = null;
		if (nodert != null) {
			String currnodeid = nodert.getNodeid();
			if (currnodeid != null) {
				currnode = (Node) fd.getElementByID(currnodeid);
				state = StateCreator.getNodeState(currnode);
				if (currnode instanceof ManualNode) {
					if (((ManualNode) currnode).isFrontEdit) {
						this.isFrontEdit = true;
					}
				}
			}

			buffer.append("<input type='hidden' name='_currid' value='"
					+ currnodeid + "'>");
		}

		buffer.append("");

		if (state != null && state.toInt() == FlowState.RUNNING) {// 送下一步
			isDisplySubmit = true;
			Collection<Node> nextNodeList = fd.getNextNodeList(currnode.id);
			
			// 判断是否能通过
			Node node = (Node) fd.getElementByID(nodert.getNodeid());
			boolean issplit = false;
			boolean needToPersion = false;//是否需要选择审批人操作
			boolean isAppentCirculator = false;

			// checkedState判断在html拼接的时候是否需要为checkbox 和 radio 添加"默认选中"标识
			boolean checkedState = true;

			if (node != null)  {
				if(node instanceof ManualNode) {
					issplit = ((ManualNode) node).issplit;
				}else if(node instanceof AutoNode) {
					issplit = ((AutoNode) node).issplit;
				}
			}


			// 分散的情况下，为每个分支节点增加令牌
			if (issplit) {
				buffer.append("<input name='splitToken' type='hidden' value='"
						+ nodert.getNodeid() + "' /> ");
			}

			if (nextNodeList != null && nextNodeList.size() > 0) {
				Iterator<Node> it3 = nextNodeList.iterator();
				int imgid = 0;
				while (it3.hasNext()) {
					buffer.append("<tr>");
					if (imgid == 0)
						buffer
								.append("<td class='flow-next' style='width:20px;white-space:nowrap;word-break:keep-all'>{*[Commit]*}{*[To]*}：</td>");
					else
						buffer
								.append("<td class='flow-next' style='width:2%;white-space:nowrap;word-break:keep-all'></td>");
					Node nextNode = it3.next();

					boolean isOthers = false;
					needToPersion = false;
					String id = "next";
					String flowOperation = FlowType.RUNNING2RUNNING_NEXT;
					if (!(nextNode instanceof ManualNode)) {
						isOthers = true;
						id = "other";
						if (nextNode instanceof SuspendNode) {
							flowOperation = FlowType.RUNNING2SUSPEND;
						} else if (nextNode instanceof AbortNode) {
							flowOperation = FlowType.RUNNING2ABORT;
						} else if (nextNode instanceof TerminateNode) {
							flowOperation = FlowType.RUNNING2TERMIATE;
						} else if (nextNode instanceof CompleteNode) {
							flowOperation = FlowType.RUNNING2COMPLETE;
						} else if (nextNode instanceof AutoNode) {
							flowOperation = FlowType.RUNNING2AUTO;
						}
					}
					// TD1
					buffer.append("<td >");
					buffer.append("<input id='" + id + "' type='");
					buffer.append(issplit ? "checkbox' checked" : "radio'");
					if (!issplit && checkedState) {
						buffer.append(" checked");
						checkedState = false;
					}
					buffer.append(" name='_nextids' value='" + nextNode.id);
					buffer.append("' onclick='ev_setFlowType(" + isOthers);
					buffer.append(", this, " + flowOperation + ")' />"
							+ nextNode.name);
					buffer.append("</td>");
					buffer.append("<td >");
					
					if ((nextNode instanceof ManualNode)
							&& ((ManualNode) nextNode).isToPerson) {// 手动节点指定审批人
						Collection<BaseUser> userList = StateMachineUtil.getPrincipalList(new ParamsTable(), nextNode, doc.getDomainid(), doc.getApplicationid(),webUser);
						if(userList !=null && userList.size()>1){//当下一步审批人超过1人时才出现指定审批人操作
							needToPersion = true;
						}
					}

					// 循环生成isToPerson，以判断每个节点是否有指定审批人
					if (nodert != null) {
						if ((currnode instanceof ManualNode)
								&& (nextNode instanceof ManualNode)) {// 编辑审批人
							buffer.append("<input id='isToPerson_"
									+ nextNode.id
									+ "' type='hidden' name='isToPerson_"
									+ nextNode.id + "' value='"
									+ needToPersion
									+ "' />");
						}
					}

					if ((nextNode instanceof ManualNode)
							&& ((ManualNode) nextNode).isToPerson && needToPersion) {// 手动节点指定审批人
							isToPerson = true;
							buffer
									.append("&nbsp&nbsp{*[Specify]*}{*[Auditor]*}:&nbsp");
							buffer.append("<span id='opra_" + imgid
									+ "' style='display:inline;'>");
							buffer.append("<input id='input_" + imgid
									+ "' name='input_" + imgid
									+ "' readonly='true' type='text' size='10' />");
	
							buffer
									.append("<img id='selectUserImg_"
											+ imgid
											+ "' style='cursor:pointer;display:inline;' onclick=\"showUserSelect('actionName', {nextNodeId:'"
											+ nextNode.id
											+ "', docid:'"
											+ doc.getId()
											+ "',flowid:'"
											+ flowVO.getId()
											+ "', textField:'input_"
											+ imgid
											+ "',valueField: 'input_"
											+ imgid
											+ "', readonly: false})\" src='"
											+ evn.getContextPath()
											+ "/script/dialog/images/userselect.gif' /> ");
							buffer.append("</span>");
					} else if ((nextNode instanceof SubFlow)
							&& ((SubFlow) nextNode).isToPerson) {// 子流程节点指定审批人
						isToPerson = true;
						String numberSetingType = ((SubFlow) nextNode).numberSetingType;
						int instanceTotal = getSubFlowInstanceTotal(
								(SubFlow) nextNode, doc, webUser);
						buffer
								.append("&nbsp&nbsp{*[Specify]*}{*[Auditor]*}:&nbsp");
						buffer.append("<span id='opra_" + imgid
								+ "' style='display:inline;'>");
						buffer.append("<input id='input_" + imgid
								+ "' name='input_" + imgid
								+ "' readonly='true' type='text'  size='10'/>");

						buffer
								.append("<img id='selectUserImg_"
										+ imgid
										+ "' style='cursor:pointer;display:inline;' onclick=\"showUserSelectOnSubFlow('actionName', {nextNodeId:'"
										+ nextNode.id
										+ "',instanceId:'"
										+ doc.getStateid()
										+ "',numberSetingType:'"
										+ numberSetingType
										+ "',instanceTotal:'"
										+ instanceTotal
										+ "', docid:'"
										+ doc.getId()
										+ "',flowid:'"
										+ flowVO.getId()
										+ "', textField:'input_"
										+ imgid
										+ "',valueField: 'input_"
										+ imgid
										+ "', readonly: false})\" src='"
										+ evn.getContextPath()
										+ "/script/dialog/images/userselect.gif' /> ");
						buffer.append("</span>");
					}
					buffer.append("</td>");

					// 指定流程抄送人-------------------------------------------start

					buffer.append("<td >");
					if ((currnode instanceof ManualNode) && ((ManualNode) currnode).isCarbonCopy
							&& ((ManualNode) currnode).isSelectCirculator && !isAppentCirculator) {// 手动节点指定抄送人
						isToPerson = true;
						isAppentCirculator = true;
						buffer.append("{*[抄送给]*}:&nbsp");
						buffer.append("<span id='opra_" + imgid
								+ "' style='display:inline;'>");
						buffer
								.append("<input id='_circulator"
										+ "' name='_circulator"
										+ "' readonly='true'  type='text'  size='10' />");

						buffer
								.append("<img id='selectUserImg_"
										+ imgid
										+ "' style='cursor:pointer;display:inline;' onclick=\"selectCirculator('actionName', {nextNodeId:'"
										+ currnode.id
										+ "', docid:'"
										+ doc.getId()
										+ "',flowid:'"
										+ flowVO.getId()
										+ "', textField:'_circulator"
										+ "',valueField: '_circulator"
										+ "', readonly: false})\" src='"
										+ evn.getContextPath()
										+ "/script/dialog/images/userselect.gif' /> ");
						buffer.append("</span>");
					}
					buffer.append("</td>");

					// 指定流程抄送人-------------------------------------------end

					buffer.append("</tr>");
					imgid++;
				}
			}

			Collection<Node> backNodeList = getBackToNodeList(doc, currnode.id,
					webUser);
			if (backNodeList != null && backNodeList.size() > 0) {
				buffer.append("<tr>");
				buffer
						.append("<td style='white-space:nowrap;word-break:keep-all' class='commFont flow-back'>{*[Return]*}{*[To]*}:");
				buffer.append("</td>");

				buffer.append("<td>");
				buffer
						.append("<select class='flow-back' id='back' name='_nextids'");
				buffer.append(" onchange='ev_setFlowType(false, this, "
						+ FlowType.RUNNING2RUNNING_BACK + ")'>");
				buffer.append("<option value=''>");
				buffer.append("{*[Please]*}{*[Choose]*}");
				buffer.append("</option>");
				for (Iterator<Node> iter = backNodeList.iterator(); iter
						.hasNext();) {
					Node backNode = iter.next();
					buffer.append("<option value='" + backNode.id + "'>");
					buffer.append(backNode.name);
					buffer.append("(").append(backNode.statelabel).append(")");
					buffer.append("</option>");

				}
				buffer.append("</select>");
				buffer.append("</td>");
				buffer.append("<td >");
				if (((ManualNode) currnode).isToPerson) {// 编辑审批人
					
					Collection<BaseUser> userList = StateMachineUtil.getPrincipalList(new ParamsTable(), currnode, doc.getDomainid(), doc.getApplicationid(),webUser);
					if(userList !=null && userList.size()>1){//当下一步审批人超过1人时才出现指定审批人操作
					isToPerson = true;
					String imgid = "back";
					buffer.append("&nbsp;&nbsp;");
					buffer
							.append("<span id='opra_"
									+ imgid
									+ "' style='display:inline;'>{*[Specify]*}{*[Auditor]*}:&nbsp;");
					buffer.append("<input id='input_" + imgid
							+ "' name='input_" + imgid
							+ "' readonly='true' type='text'  size='10'/>");

					buffer
							.append("<img id='selectUserImg_"
									+ imgid
									+ "' style='cursor:pointer;display:inline;' onclick=\"showBackUserSelect('"
									+ doc.getId()
									+ "','"
									+ flowVO.getId()
									+ "')\" src='"
									+ evn.getContextPath()
									+ "/script/dialog/images/userselect.gif' /> ");
					buffer.append("</span>");
				}
				}
				buffer.append("</td>");
				buffer.append("</tr>");
			}
		} else if (state != null && state.toInt() == FlowState.SUSPEND) {
			Collection<Node> backNodeList = this.getBackToNodeList(doc,
					currnode.id, webUser, FlowState.SUSPEND);
			backNodeList = StateMachine.removeDuplicateNode(backNodeList);
			isDisplySubmit = true;

			if (backNodeList != null) {
				Iterator<Node> it4 = backNodeList.iterator();
				buffer.append("<tr>");
				buffer
						.append("<td class='flow-next' style='width:15%;white-space:nowrap;word-break:keep-all'>{*[Resume]*}{*[Flow]*}: </td>");
				buffer.append("<td>");

				while (it4.hasNext()) {
					Node backNode = it4.next();
					buffer
							.append("<input id='suspend' type='radio' name='_nextids' value='"
									+ backNode.id + "' ");
					buffer.append("' onclick='ev_setFlowType(false, this, 81");
					buffer.append(FlowType.SUSPEND2RUNNING + ")' />");
					buffer.append(backNode.name);
				}
				buffer.append("</td>");
				buffer.append("</tr>");
			}
		} else {
			isDisplySubmit = false;
			if (state != null && state.toInt() != FlowState.RUNNING) {
				if (state.toInt() == FlowState.START) {
					isDisplyFlow = false;
				} else {
					buffer
							.append("<td style='font-size:12px;font-family: Arial, Helvetica;color:#FF0000'>"
									+ doc.getStateLabel() + "</td>");
				}
			} else {
				buffer
						.append("<td style='font-size:12px;font-family: Arial, Helvetica;'>");
				buffer.append(doc.getStateLabel());
				buffer.append("</td>");
			}
		}

		if (nodert != null) {
			if ((currnode instanceof ManualNode)) {// 编辑审批人
				buffer
						.append("<input id='isToPerson' type='hidden' name='isToPerson' value='"
								+ isToPerson + "' />");
			}
		}

		return buffer.toString();
	}

	/**
	 * 返回字符串内容为显示当前处理人。
	 * 
	 * @param doc
	 * 
	 * @return 字符串内容为显示当前处理人
	 * @throws Exception
	 */
	public String toCurrProcessorHtml(Document doc) throws Exception {
		StringBuffer buffer = new StringBuffer();

		Collection<String> nameList = toCurrProcessorList(doc);
		if (nameList != null && !nameList.isEmpty()) {
			buffer.append("(");
			for (Iterator<String> iterator = nameList.iterator(); iterator
					.hasNext();) {
				String name = iterator.next();
				buffer.append(name + ",");
			}
			if (buffer.lastIndexOf(",") != -1) {
				buffer.deleteCharAt(buffer.lastIndexOf(","));
			}
			buffer.append(")");
		}

		return buffer.toString();
	}

	public static String toProcessorHtml(Document doc, WebUser webUser)
			throws Exception {
		StringBuffer buffer = new StringBuffer();
		Collection<String> processorList = toCurrProcessorList(doc);
		String displayProcessor = "";
		StringBuffer displayPocessorStr = new StringBuffer();
		if (processorList.size() > 1) {
			if (processorList.contains(webUser.getName())) {
				for (Iterator<String> iter = processorList.iterator(); iter
						.hasNext();) {
					String name = iter.next();
					if (!webUser.getName().equals(name)) {
						displayPocessorStr.append(name).append(", ");
					}
				}
				if (displayPocessorStr.toString().endsWith(" ")) {
					displayPocessorStr
							.setLength(displayPocessorStr.length() - 2);
				}
				displayProcessor = webUser.getName();
			} else {
				displayProcessor = (String) processorList.toArray()[0];
				Object[] processorArray = processorList.toArray();
				for (int i = 1; i < processorArray.length; i++) {
					String name = (String) processorArray[i];
					displayPocessorStr.append(name).append(", ");
				}
				if (displayPocessorStr.toString().endsWith(" ")) {
					displayPocessorStr
							.setLength(displayPocessorStr.length() - 2);
				}
			}
		} else if (processorList.size() == 1) {
			displayProcessor = (String) processorList.toArray()[0];
		}
		if (processorList != null && !processorList.isEmpty()) {
			buffer
					.append("<div" +
							" title='{*[Current_Processor]*}:(" + displayProcessor +
							")'><span class='formFlowCls'>"
							+"{*[Current_Processor]*}:(<span onmouseover='displayProcessor()' onmouseout='displayProcessor()'>" + displayProcessor + "</span>)</span></div>");
			buffer
					.append("<div id='processorDiv' style='display:none; position: absolute;'><span>{*[More]*}:("
							+ displayPocessorStr.toString() + ")</span></div>");
		}
		return buffer.toString();
	}

	public static Collection<String> toCurrProcessorList(Document doc)
			throws Exception {

		Collection<NodeRT> nodertList = getAllNodeRT(doc);
		Collection<String> processorList = new ArrayList<String>();
		for (Iterator<NodeRT> iter = nodertList.iterator(); iter.hasNext();) {
			NodeRT nodert = iter.next();
			Collection<ActorRT> colls = nodert.getPendingActorRTList();
			Object[] actorrts = colls.toArray();
			if (actorrts.length > 0) {
				for (int i = 0; i < actorrts.length; i++) {
					String actorrtName = ((ActorRT) actorrts[i]).getName();
					processorList.add(actorrtName);
				}

			}
		}
		return processorList;
	}

	// 流程历史
	public static String toHistoryHtml(Document doc, int cellCount)
			throws Exception {
		String docid = doc.getId();
		String flowid = doc.getFlowid();
		String applicationid = doc.getApplicationid();
		FlowHistory his = new FlowHistory();
		if (docid != null && flowid != null && applicationid != null) {
			Collection<RelationHIS> colls = getAllRelationHIS(docid, flowid,
					applicationid);
			his.addAllHis(colls);
		}

		return his.toTextHtml();
	}

	// 流程历史
	public static String toHistoryXml(Document doc, int cellCount)
			throws Exception {
		Collection<RelationHIS> colls = getAllRelationHIS(doc.getId(), doc
				.getFlowid(), doc.getApplicationid());
		FlowHistory his = new FlowHistory();
		his.addAllHis(colls);

		return his.toTextXml();
	}

	/**
	 * is shows history or not
	 * 
	 * @param flowid
	 * @param docid
	 * @return
	 * @throws Exception
	 */
	public boolean isShowHis(String flowid, String docid, String applicationId)
			throws Exception {
		Collection<RelationHIS> colls = getAllRelationHIS(docid, flowid,
				applicationId);
		return colls.size() > 1;
	}

	/**
	 * 返回字符串为显示的流程状态标识。 此实现为通过Document id 与流程(flow) id
	 * 查询当前Document流程状态(flowStateRT)，并通过流程状态获取当前节点的状态标识（State label)
	 * 
	 * @returnf 字符串为显示的流程状态。
	 * @param doc
	 * @throws Exception
	 */
	public static String toFlowStateHtml(Document doc) throws Exception {
		StringBuffer buffer = new StringBuffer();
		FlowStateRT flowStateRT = doc.getState();		
		Collection<String> flowStateList = toCurrProcessorList(doc);
		String displayflowState = "";
		StringBuffer displayflowStateStr = new StringBuffer();		
		if (flowStateRT != null) {
			buffer.append("<span class='formFlowCls'>{*[Flow]*}{*[State]*}:(<span>");

			if (!StringUtil.isBlank(flowStateRT.getStateLabel())) {
				buffer.append(flowStateRT.getStateLabel());
			} else if (!StringUtil.isBlank(doc.getStateLabel())) {
				buffer.append(doc.getStateLabel());
			} else if (isDraft(doc)) {
				buffer.append("{*[" + FlowState.getName(FlowState.DRAFT)
						+ "]*}");
			} else {
				buffer.append("{*[" + FlowState.getName(flowStateRT.getState())
						+ "]*}");
			}
			buffer.append("</span>)</span>");
		}		
		/*if (flowStateList != null && !flowStateList.isEmpty()) {
			buffer.append("{*[Current_flowState]*}:");
			buffer
					.append("<div style='cursor: pointer; position: relative; display: inline;' onMouseOver='displayProcessor()' onMouseOut='displayProcessor()'>(<span class='redColor'>"
							+ displayflowState + "</span>)");
			buffer
					.append("<div id='processorDiv' style='display: none; width: 250px; padding-top: 16px; z-index: 99; position: absolute; left: 0px; top: 0px;'>{*[More]*}:(<span class='redColor'>"
							+ displayflowStateStr.toString() + "</span>)</div>");
			buffer.append("</div>");
		}*/		
		LOG.debug("FlowState HTML: " + buffer.toString());
		return buffer.toString();
	}

	/**
	 * 判断是否为草稿状态
	 * 
	 * @param doc
	 * @param webUser
	 * 
	 * @return
	 * @throws Exception
	 */
	private static boolean isDraft(Document doc) throws Exception {
		Collection<Node> firstNodelist = StateMachine.getFirstNodeList(doc);

		if (firstNodelist != null && firstNodelist.size() > 0) {
			Collection<NodeRT> nodertlist = StateMachine.getAllNodeRT(doc
					.getId(), doc.getStateid(), doc.getApplicationid());

			if (nodertlist != null && nodertlist.size() > 0) {
				for (Iterator<NodeRT> iter = nodertlist.iterator(); iter
						.hasNext();) {
					NodeRT nodert = iter.next();
					if (isContains(firstNodelist, nodert)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private static boolean isContains(Collection<Node> firstNodes, NodeRT nodert) {
		for (Iterator<Node> iter = firstNodes.iterator(); iter.hasNext();) {
			Node firstNode = iter.next();
			if (firstNode.id.equals(nodert.getNodeid())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取当前节点负责人列表
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
	public static Collection<BaseUser> getPrincipalList(String docid,
			WebUser user, String nodeid, HttpServletRequest request,
			String flowid) throws Exception {
		BillDefiProcess process = (BillDefiProcess) ProcessFactory
				.createProcess(BillDefiProcess.class);
		BillDefiVO flowVO = (BillDefiVO) process.doView(flowid);

		DocumentProcess docProcess = (DocumentProcess) ProcessFactory.createRuntimeProcess(DocumentProcess.class,flowVO
				.getApplicationid());
		Document doc = (Document) user.getFromTmpspace(docid);
		if (doc == null) {
			doc = (Document) docProcess.doView(docid);
		}

		ParamsTable params = ParamsTable.convertHTTP(request);
		params.setParameter("docid", docid);
		request.setAttribute("content", doc);
		params.setHttpRequest(request);

		if (doc != null && doc.getFlowVO() != null) {
			Node nextNode = flowVO.findNodeById(nodeid);
			if (nextNode instanceof SubFlow) {
				SubFlow subFlowNode = ((SubFlow) nextNode);
				String subFlowFlowId = null;
				IRunner runner = JavaScriptFactory.getInstance(params
						.getSessionid(), doc.getApplicationid());
				runner.initBSFManager(doc, params, user,
						new ArrayList<ValidateMessage>());
				if (SubFlow.SUBFLOW_DEFINITION_CUSTOM
						.equals(subFlowNode.subFlowDefiType)) {
					subFlowFlowId = subFlowNode.subflowid;
				} else if (SubFlow.SUBFLOW_DEFINITION_SCRIPT
						.equals(subFlowNode.subFlowDefiType)
						&& !StringUtil.isBlank(subFlowNode.subflowScript)) {
					subFlowFlowId = (String) runner.run("subFlow:"
							+ subFlowNode.name + " subFlowScript",
							subFlowNode.subflowScript);
				}
				flowVO = (BillDefiVO) process.doView(subFlowFlowId);
				nextNode = flowVO.getFirstNode();
			}
			return StateMachineUtil.getPrincipalList(params, nextNode, user
					.getDomainid(), flowVO.getApplicationid(),user);
		}

		return new ArrayList<BaseUser>();
	}

	/**
	 * 获取节点的流程抄送人
	 * 
	 * @param docid
	 * @param user
	 * @param nodeid
	 * @param request
	 * @param flowid
	 * @return
	 * @throws Exception
	 */
	public static Collection<BaseUser> getCirculatorList(String docid,
			WebUser user, String nodeid, HttpServletRequest request,
			String flowid) throws Exception {
		BillDefiProcess process = (BillDefiProcess) ProcessFactory
				.createProcess(BillDefiProcess.class);
		BillDefiVO flowVO = (BillDefiVO) process.doView(flowid);

		DocumentProcess docProcess = (DocumentProcess) ProcessFactory.createRuntimeProcess(DocumentProcess.class,flowVO
				.getApplicationid());
		Document doc = (Document) user.getFromTmpspace(docid);
		if (doc == null) {
			doc = (Document) docProcess.doView(docid);
		}

		ParamsTable params = ParamsTable.convertHTTP(request);
		params.setParameter("docid", docid);
		request.setAttribute("content", doc);
		params.setHttpRequest(request);

		if (doc != null && doc.getFlowVO() != null) {
			Node nextNode = flowVO.findNodeById(nodeid);
			return StateMachineUtil.getCirculatorList(params, doc, nextNode,
					doc.getDomainid(), doc.getApplicationid());
		}
		return new ArrayList<BaseUser>();
	}

	/**
	 * 获取文档的其他节点(除当前节点和开始节点)
	 * 
	 * @param docId
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> getOtherNodeMap(String docId,
			String applicationid) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("", "{*[Select]*}");
		if (docId == null || docId.trim().length() <= 0)
			return map;

		DocumentProcess process = (DocumentProcess) ProcessFactory
				.createRuntimeProcess(DocumentProcess.class, applicationid);
		Document doc = (Document) process.doView(docId);
		if (doc != null) {
			Collection<Node> nodes = getOtherNodeList(doc);
			for (Node node : nodes) {
				map.put(node.id, node.statelabel);
			}
		}

		return map;

	}

	/**
	 * 获取文档的其他节点(除当前节点和开始节点)
	 * 
	 * @param docId
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public Collection<Node> getOtherNodeList(String docId, String applicationid)
			throws Exception {
		if (docId == null || docId.trim().length() <= 0)
			return new ArrayList<Node>();

		DocumentProcess process = (DocumentProcess) ProcessFactory
				.createRuntimeProcess(DocumentProcess.class, applicationid);
		Document doc = (Document) process.doView(docId);
		if (doc != null) {
			return getOtherNodeList(doc);
		}
		return new ArrayList<Node>();
	}

	/**
	 * 获取文档的其他节点(除当前节点和开始节点)
	 * 
	 * @param doc
	 *            文档
	 * @param user
	 *            用户
	 * @return
	 * @throws Exception
	 * @author Happy
	 */
	public Collection<Node> getOtherNodeList(Document doc) throws Exception {
		Collection<Node> rtn = new ArrayList<Node>();
		// rtn.add(null);
		if (doc != null) {
			BillDefiVO flowVO = doc.getFlowVO();
			Collection<NodeRT> nodert = null;
			FlowDiagram fd = null;
			if (flowVO != null) {
				fd = flowVO.toFlowDiagram();
				nodert = doc.getState().getNoderts();
			}

			if (nodert != null) {
				StringBuffer currnodeids = new StringBuffer();
				for (NodeRT rt : nodert) {
					currnodeids.append(rt.getNodeid()).append(",");
				}
				Vector<Element> allElemets = fd.getAllElements();

				for (Enumeration<Element> e = allElemets.elements(); e
						.hasMoreElements();) {
					Element elem = e.nextElement();
					if (elem instanceof Relation
							|| currnodeids.toString().indexOf(elem.id) > -1
							|| elem instanceof StartNode) {
						// allElemets.remove(elem);// delete Relation,StartNode
						// and currentNode
					} else {
						rtn.add((Node) elem);
					}
				}

			}
		}

		return rtn;
	}

}
