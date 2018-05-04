package OLink.bpm.core.workflow.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import OLink.bpm.core.department.ejb.DepartmentProcess;
import OLink.bpm.core.department.ejb.DepartmentVO;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.macro.runner.JavaScriptFactory;
import OLink.bpm.core.role.ejb.RoleProcess;
import OLink.bpm.core.role.ejb.RoleVO;
import OLink.bpm.core.workflow.element.Node;
import OLink.bpm.core.workflow.utility.NameList;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.user.ejb.UserProcess;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.core.workflow.utility.NameNode;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.constans.Web;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.json.JsonUtil;
import OLink.bpm.core.dynaform.activity.ejb.Activity;
import OLink.bpm.core.dynaform.activity.ejb.ActivityType;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.document.ejb.DocumentProcess;
import OLink.bpm.core.dynaform.form.action.FormHelper;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.user.ejb.BaseUser;
import OLink.bpm.core.workflow.element.ManualNode;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiProcess;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiVO;
import OLink.bpm.core.workflow.storage.runtime.ejb.FlowStateRT;
import OLink.bpm.core.workflow.storage.runtime.ejb.FlowStateRTProcess;
import OLink.bpm.core.workflow.storage.runtime.ejb.Type;
import OLink.bpm.util.StringUtil;
import OLink.bpm.util.web.DWRHtmlUtils;

public class StateMachineUtil {

	public Collection<Node> getFirstNodeList(Document doc) throws Exception {
		return StateMachine.getFirstNodeList(doc);
	}
	
	public Collection<Node> getFirstNodeList(String docid, String flowid) throws Exception {
		return StateMachine.getFirstNodeList(docid, flowid);
	}

	public String getFirstNodeListByFlowid(Document doc, String divid) throws Exception {
		Collection<Node> cols = null;
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		cols = StateMachine.getFirstNodeList(doc);
		if (cols != null) {
			for (Iterator<Node> iter = cols.iterator(); iter.hasNext();) {
				ManualNode startNode = (ManualNode) iter.next();
				map.put(startNode.id, startNode.name);
			}
		}
		String[] str = new String[10];
		return DWRHtmlUtils.createHtmlStr(map, divid, str);
	}

	public String getFirstNodeListByDocidAndFlowid(String docid, String flowid, String divid) throws Exception {
		Collection<Node> cols = null;
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		BillDefiProcess process = (BillDefiProcess) ProcessFactory.createProcess(BillDefiProcess.class);
		BillDefiVO flowVO = (BillDefiVO) process.doView(flowid);

		cols = StateMachine.getFirstNodeList(docid, flowVO);
		if (cols != null) {
			for (Iterator<Node> iter = cols.iterator(); iter.hasNext();) {
				ManualNode startNode = (ManualNode) iter.next();
				map.put(startNode.id, startNode.name);
			}
		}

		String[] str = new String[10];
		return DWRHtmlUtils.createHtmlStr(map, divid, str);
	}

	/**
	 * 获取ID与负责人的映射
	 * 
	 * @SuppressWarnings JsonUtil.toCollectio不支持泛型
	 * @param node
	 * @param domainid
	 * @param applicationid
	 * @return ID与负责人的映射
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, BaseUser> getPrincipalMap(ParamsTable params, Node nextnode, String domainid,
			String applicationid,BaseUser auditor) throws Exception {
		LinkedHashMap<String, BaseUser> userMap = new LinkedHashMap<String, BaseUser>();
		if (!(nextnode instanceof ManualNode)) {
			return userMap;
		}
		// 提交信息
		String submitTo = null;
		// json转化后的提交信息
		Collection<Object> submitInfo = null;
		// 标识用户是否指定审批人
		String isToPerson = "";
		// 某一节点下的用户指定的审批人列表
		Collection<Object> selectuserlist = null;


		if (params != null) {
			submitTo = params.getParameterAsString("submitTo");
			
			if (submitTo != null && !submitTo.equals("")) {
				submitInfo = JsonUtil.toCollection(submitTo);
				for (Iterator<Object> iterator = submitInfo.iterator(); iterator.hasNext();) {
					Map<String, String> tmpmap = (Map<String, String>) iterator.next();
					String nodeid = tmpmap.get("nodeid");
					if (nodeid != "" && nodeid != null && nodeid.equals(nextnode.id)) {
						// 获取用户操作“是否指定审批人”true||false
						isToPerson = tmpmap.get("isToPerson");
						// 获取用户指定的审批人列表
						selectuserlist = JsonUtil.toCollection(tmpmap.get("userids"));
					} else
						continue;
				}
			}
		}

		UserProcess userProcess = (UserProcess) ProcessFactory.createProcess(UserProcess.class);
		RoleProcess roleProcess = (RoleProcess) ProcessFactory.createProcess(RoleProcess.class);
		DepartmentProcess deptProcess = (DepartmentProcess) ProcessFactory.createProcess(DepartmentProcess.class);
		// 用户指定审批人
		if (isToPerson.endsWith("true")) {
			for (Iterator<Object> iterator = selectuserlist.iterator(); iterator.hasNext();) {
				Object userObj = iterator.next();
				UserVO user = null;
				if (userObj instanceof String) {
					user = (UserVO) userProcess.doView((String) userObj);
				} else {
					user = (UserVO) userObj;
				}
				if (user != null) {
					userMap.put(user.getId(), user);
				}
			}
		} else {// 用户不指定审批人，默认提交给该节点下所有审批人
			ManualNode mNode = (ManualNode) nextnode;
			
			switch (mNode.actorEditMode) {
			case ManualNode.ACTOR_EDIT_MODE_CODE:
				if (StringUtil.isBlank(mNode.actorListScript)) {
					return userMap;
				}
				//为什么没有把session id 写进去，没有搞明白
				//IRunner runner = JavaScriptFactory.getInstance("", applicationid);
				IRunner runner = JavaScriptFactory.getInstance(params.getSessionid(), applicationid);

				// 重新注册一些公共工具类 start
				if (params != null && params.getHttpRequest() != null) {
					HttpServletRequest request = params.getHttpRequest();
					Document doc = (Document) request.getAttribute("content");
					WebUser webUser = (WebUser) request.getSession().getAttribute(Web.SESSION_ATTRIBUTE_FRONT_USER);
					runner.initBSFManager(doc, params, webUser, new ArrayList());
				}
				// ----------------- end

				StringBuffer label = new StringBuffer();
				label.append("ManualNode(").append(nextnode.id).append(").").append(nextnode.name).append(
						".auditorListScript");
				Object obj = runner.run(label.toString(), StringUtil.dencodeHTML(mNode.actorListScript));

				// 兼容多种返回值，BaseUser,Collection<BaseUser>,BaseUser[],Collection<String>,NativeArray
				if (BaseUser.class.isAssignableFrom(obj.getClass())) {
					BaseUser user = (BaseUser) obj;
					userMap.put(user.getId(), user);
				} else if (obj instanceof UserVO) {
					UserVO user = (UserVO) obj;
					userMap.put(user.getId(), user);
				} else {
					Collection userList = new ArrayList(); // 用户列表
					if (obj instanceof Collection) {
						userList = (Collection) obj;
					} else if (obj instanceof UserVO[]) {
						userList = Arrays.asList((UserVO[]) obj);
					}
					for (Iterator iterator = userList.iterator(); iterator.hasNext();) {
						Object userObj = iterator.next();
						UserVO user = null;
						if (userObj instanceof String) {
							user = (UserVO) userProcess.doView((String) userObj);
						} else {
							user = (UserVO) userObj;
						}
						if (user != null) {
							userMap.put(user.getId(), user);
						}
					}
				}
				break;
			case ManualNode.ACTOR_EDIT_MODE_DESIGN:
				NameList nameList = NameList.parser(mNode.namelist);
				Collection<NameNode> nameNodeList = nameList.toNameNodeCollection();
				for (Iterator iter = nameNodeList.iterator(); iter.hasNext();) {
					NameNode nameNode = (NameNode) iter.next();
					String actorId = nameNode.getId();

					UserVO user = null;
					switch (nameNode.getType()) {
					case Type.TYPE_USER:
						user = (UserVO) userProcess.doView(actorId);
						userMap.put(user.getId(), user);
						break;
					case Type.TYPE_ROLE:
						RoleVO roleVO = (RoleVO) roleProcess.doView(actorId);
						Collection roleUsers = roleVO.getUsersByDomain(domainid);
						for (Iterator iterator = roleUsers.iterator(); iterator.hasNext();) {
							user = (UserVO) iterator.next();
							userMap.put(user.getId(), user);
						}
						break;
					case Type.TYPE_DEPARTMENT:
						DepartmentVO dept = (DepartmentVO) deptProcess.doView(actorId);
						Collection deptUsers = dept.getUsers();
						for (Iterator iterator = deptUsers.iterator(); iterator.hasNext();) {
							user = (UserVO) iterator.next();
							userMap.put(user.getId(), user);
						}
						break;
					default:
						break;
					}
				}
				break;
				//用户设计模式
			case ManualNode.ACTOR_EDIT_MODE_USER_DESIGN:
				NameList userList = NameList.parser(mNode.userList);
				Collection<NameNode> nameNodes = userList.toNameNodeCollection();
				for (Iterator<NameNode> iter = nameNodes.iterator(); iter.hasNext();) {
					NameNode nameNode = iter.next();
					String userId = nameNode.getId();

					UserVO user = null;
					switch (nameNode.getType()) {
					case Type.TYPE_USER:
						user = (UserVO) userProcess.doView(userId);
						userMap.put(user.getId(), user);
						break;
					case Type.TYPE_ROLE:
						RoleVO roleVO = (RoleVO) roleProcess.doView(userId);
						Collection roleUsers = roleVO.getUsersByDomain(domainid);
						for (Iterator iterator = roleUsers.iterator(); iterator.hasNext();) {
							user = (UserVO) iterator.next();
							userMap.put(user.getId(), user);
						}
						break;
					case Type.TYPE_DEPARTMENT:
						DepartmentVO dept = (DepartmentVO) deptProcess.doView(userId);
						Collection deptUsers = dept.getUsers();
						for (Iterator iterator = deptUsers.iterator(); iterator.hasNext();) {
							user = (UserVO) iterator.next();
							userMap.put(user.getId(), user);
						}
						break;
					default:
						break;
					}
				}
				break;
				//组织设计模式
			case ManualNode.ACTOR_EDIT_MODE_ORGANIZATION_DESIGN:
				if(ManualNode.ORG_AUDITOR.equals(mNode.orgField)){//流程提交者
					if(ManualNode.ORG_SCOPE_SUPERIOR.equals(mNode.orgScope)){
						loadSuperior(auditor, userMap);
					}else if(ManualNode.ORG_SCOPE_LOWER.equals(mNode.orgScope)){
						loadSubordinates(auditor, userMap);
					}else if(ManualNode.ORG_SCOPE_DEPT_DEFAULT.equals(mNode.orgScope)){
						loadUsersDefaultDeptUsers(auditor, userMap);
					}else if(ManualNode.ORG_SCOPE_DEPT_ALL_SUPERIOR.equals(mNode.orgScope)){
						loadAllSuperiorDeptUsers(auditor, userMap);
					}else if(ManualNode.ORG_SCOPE_DEPT_ALL_LOWER.equals(mNode.orgScope)){
						loadAllSubordinateDeptUsers(auditor, userMap);
					}else if(ManualNode.ORG_SCOPE_DEPT_LINE_SUPERIOR.equals(mNode.orgScope)){
						loadLineSuperiorDeptUsers(auditor, userMap);
					}else if(ManualNode.ORG_SCOPE_DEPT_LINE_LOWER.equals(mNode.orgScope)){
						loadLineSubordinateDeptUsers(auditor, userMap);
					}
					
					
				}else if(ManualNode.ORG_AUTHOR.equals(mNode.orgField)){//表单作者
					
				}
				break;
			default:
				break;
			}
		}
		return userMap;

	}
	
	
	/**
	 * 装载上级用户
	 * @param user
	 * @param userMap
	 * @throws Exception
	 */
	private static void loadSuperior(BaseUser user,LinkedHashMap<String, BaseUser> userMap)throws Exception{
		if(user.getSuperior() !=null){
			userMap.put(user.getSuperior().getId(), user.getSuperior());
		}
	}
	
	/**
	 * 装载下级用户
	 * @param user
	 * @param userMap
	 * @throws Exception
	 */
	private static void loadSubordinates(BaseUser user,LinkedHashMap<String, BaseUser> userMap)throws Exception{
		UserProcess process = (UserProcess)ProcessFactory.createProcess(UserProcess.class);
		Collection<UserVO> lowerList = process.getUnderList(user.getId(), 1);
		for(Iterator<UserVO> iter = lowerList.iterator();iter.hasNext();){
			UserVO u = iter.next();
			userMap.put(u.getId(),u);
		}
	}
	
	/**
	 * 装载本部门的用户
	 * @param user
	 * @param userMap
	 * @throws Exception
	 */
	private static void loadUsersDefaultDeptUsers(BaseUser user,LinkedHashMap<String, BaseUser> userMap)throws Exception{
		DepartmentProcess process = (DepartmentProcess)ProcessFactory.createProcess(DepartmentProcess.class);
		DepartmentVO dept = (DepartmentVO) process.doView(user.getDefaultDepartment());
		if(dept != null){
			Collection<UserVO> _userList = dept.getUsers();
			for(Iterator<UserVO> iter = _userList.iterator();iter.hasNext();){
				UserVO u = iter.next();
				userMap.put(u.getId(),u);
			}
		}
	}
	
	/**
	 * 装载直属上级部门用户
	 * @param user
	 * @param userMap
	 * @throws Exception
	 */
	private static void loadLineSuperiorDeptUsers(BaseUser user,LinkedHashMap<String, BaseUser> userMap)throws Exception{
		DepartmentProcess process = (DepartmentProcess)ProcessFactory.createProcess(DepartmentProcess.class);
		DepartmentVO dept = (DepartmentVO) process.doView(user.getDefaultDepartment());
		if(dept != null){
			DepartmentVO dep = process.findLineSuperiorByIndexCode(dept.getIndexCode());
			Collection<UserVO> _userList = dep.getUsers();
			for(Iterator<UserVO> iter = _userList.iterator();iter.hasNext();){
				UserVO u = iter.next();
				userMap.put(u.getId(),u);
			}
		}
	}
	
	/**
	 * 装载直属下级部门用户
	 * @param user
	 * @param userMap
	 * @throws Exception
	 */
	private static void loadLineSubordinateDeptUsers(BaseUser user,LinkedHashMap<String, BaseUser> userMap)throws Exception{
		DepartmentProcess process = (DepartmentProcess)ProcessFactory.createProcess(DepartmentProcess.class);
		DepartmentVO dept = (DepartmentVO) process.doView(user.getDefaultDepartment());
		if(dept != null){
			Collection<DepartmentVO> deptList = process.doQueryLineSubordinatesByIndexCode(dept.getIndexCode());
			for(Iterator<DepartmentVO> it = deptList.iterator();it.hasNext();){
				DepartmentVO dep = it.next();
				Collection<UserVO> _userList = dep.getUsers();
				for(Iterator<UserVO> iter = _userList.iterator();iter.hasNext();){
					UserVO u = iter.next();
					userMap.put(u.getId(),u);
				}
			}
		}
	}
	
	/**
	 * 装载所有上级部门用户
	 * @param user
	 * @param userMap
	 * @throws Exception
	 */
	private static void loadAllSuperiorDeptUsers(BaseUser user,LinkedHashMap<String, BaseUser> userMap)throws Exception{
		DepartmentProcess process = (DepartmentProcess)ProcessFactory.createProcess(DepartmentProcess.class);
		DepartmentVO dept = (DepartmentVO) process.doView(user.getDefaultDepartment());
		if(dept != null){
			Collection<DepartmentVO> deptList = process.doQueryAllSuperiorsByIndexCode(dept.getIndexCode());
			for(Iterator<DepartmentVO> it = deptList.iterator();it.hasNext();){
				DepartmentVO dep = it.next();
				Collection<UserVO> _userList = dep.getUsers();
				for(Iterator<UserVO> iter = _userList.iterator();iter.hasNext();){
					UserVO u = iter.next();
					userMap.put(u.getId(),u);
				}
			}
		}
	}
	
	/**
	 * 装载所有下级部门用户
	 * @param user
	 * @param userMap
	 * @throws Exception
	 */
	private static void loadAllSubordinateDeptUsers(BaseUser user,LinkedHashMap<String, BaseUser> userMap)throws Exception{
		DepartmentProcess process = (DepartmentProcess)ProcessFactory.createProcess(DepartmentProcess.class);
		DepartmentVO dept = (DepartmentVO) process.doView(user.getDefaultDepartment());
		if(dept != null){
			Collection<DepartmentVO> deptList = process.doQueryAllSubordinatesByIndexCode(dept.getIndexCode());
			for(Iterator<DepartmentVO> it = deptList.iterator();it.hasNext();){
				DepartmentVO dep = it.next();
				Collection<UserVO> _userList = dep.getUsers();
				for(Iterator<UserVO> iter = _userList.iterator();iter.hasNext();){
					UserVO u = iter.next();
					userMap.put(u.getId(),u);
				}
			}
		}
	}

	/**
	 * 获取节点的负责人列表
	 * 
	 * @param node
	 * @param domainid
	 * @param applicationid
	 * @return
	 * @throws Exception
	 */
	public static Collection<BaseUser> getPrincipalList(ParamsTable params, Node node, String domainid,
			String applicationid,BaseUser auditor) throws Exception {

		if (!StringUtil.isBlank(params.getParameterAsString("_subFlowApproverInfo"))
				&& !StringUtil.isBlank(params.getParameterAsString("_subFlowNodeId"))
				&& !StringUtil.isBlank(params.getParameterAsString("_position"))) {
			return getSubFlowApprover(params, node, domainid, applicationid);
		}
		return getPrincipalMap(params, node, domainid, applicationid,auditor).values();
	}

	/**
	 * 获取子流程第一个节点的处理人
	 * 
	 * @param params
	 * @param node
	 * @param domainid
	 * @param applicationid
	 * @return
	 * @throws Exception
	 */
	public static Collection<BaseUser> getSubFlowApprover(ParamsTable params, Node node, String domainid,
			String applicationid) throws Exception {

		Collection<BaseUser> rtn = new ArrayList<BaseUser>();

		String _subFlowApproverInfo = params.getParameterAsString("_subFlowApproverInfo");
		String _subFlowNodeId = params.getParameterAsString("_subFlowNodeId");
		String _position = params.getParameterAsString("_position");

		if (!StringUtil.isBlank(_subFlowApproverInfo) && !StringUtil.isBlank(_subFlowNodeId)
				&& !StringUtil.isBlank(_position)) {

			Collection<Object> col = JsonUtil.toCollection(_subFlowApproverInfo);

			for (Iterator<Object> iter = col.iterator(); iter.hasNext();) {
				@SuppressWarnings("unchecked")
				Map<String, Object> item = (Map<String, Object>) iter.next();
				String nodeid = (String) item.get("nodeid");
				if (_subFlowNodeId.equals(nodeid)) {

					// Object[] approvers = (Object[]) item.get("approver");
					String approverstr = (String) item.get("approver");
					Collection<Object> approvers = JsonUtil.toCollection(approverstr);

					for (Iterator<Object> it = approvers.iterator(); it.hasNext();) {
						@SuppressWarnings("unchecked")
						Map<String, Object> approver = (Map<String, Object>) it.next();
						String position = String.valueOf(approver.get("position"));

						if (_position.equals(position)) {
							UserProcess userProcess = (UserProcess) ProcessFactory.createProcess(UserProcess.class);
							Object[] users = (Object[]) approver.get("userids");
							StringBuffer userids = new StringBuffer();
							for (int j = 0; j < users.length; j++) {
								userids.append("'").append(users[j].toString()).append("',");
							}
							if (userids.length() > 0) {
								userids.setLength(userids.length() - 1);
								String hql = "FROM " + UserVO.class.getName() + " WHERE id in(" + userids.toString()
										+ ")";
								Collection<UserVO> u = userProcess.doQueryByHQL(hql);
								if (u != null && !u.isEmpty())
									rtn.addAll(u);
							}

							break;
						}

					}
					break;
				}
			}
		}
		return rtn;
	}

	/**
	 * 获取节点的抄送人
	 * 
	 * @param params
	 * @param node
	 * @param domainid
	 * @param applicationid
	 * @return
	 * @throws Exception
	 */
	public static Collection<BaseUser> getCirculatorList(ParamsTable params, Document doc, Node node,
			String domainid, String applicationid) throws Exception {

		UserProcess userProcess = (UserProcess) ProcessFactory.createProcess(UserProcess.class);
		RoleProcess roleProcess = (RoleProcess) ProcessFactory.createProcess(RoleProcess.class);
		DepartmentProcess deptProcess = (DepartmentProcess) ProcessFactory.createProcess(DepartmentProcess.class);
		Collection<BaseUser> users = new ArrayList<BaseUser>();
		if (!(node instanceof ManualNode)) {
			return users;
		}

		String _circulatorInfo = params.getParameterAsString("_circulatorInfo");

		// 用户指定抄送人
		if (!StringUtil.isBlank(_circulatorInfo)) {
			users.addAll(getCirculatorsByParameter(params, node, domainid, applicationid));
		} else {// 用户不指定抄送人，默认抄送给该节点下所有抄送人
			ManualNode mNode = (ManualNode) node;
			switch (mNode.circulatorEditMode) {
			case ManualNode.CIRCULATOR_EDIT_MODE_CODE:
				if (StringUtil.isBlank(mNode.circulatorListScript)) {
					return users;
				}
				IRunner runner = JavaScriptFactory.getInstance("", applicationid);

				// 重新注册一些公共工具类 start
				if (params != null && params.getHttpRequest() != null) {
					HttpServletRequest request = params.getHttpRequest();
					// Document doc = (Document)request.getAttribute("content");
					WebUser webUser = (WebUser) request.getSession().getAttribute(Web.SESSION_ATTRIBUTE_FRONT_USER);
					runner.initBSFManager(doc, params, webUser, new ArrayList());// happy
				}
				// ----------------- end

				StringBuffer label = new StringBuffer();
				label.append("ManualNode(").append(node.id).append(").").append(node.name).append(
						".circulatorListScript");
				Object obj = runner.run(label.toString(), StringUtil.dencodeHTML(mNode.circulatorListScript));

				// 兼容多种返回值，BaseUser,Collection<BaseUser>,BaseUser[],Collection<String>,NativeArray
				if (BaseUser.class.isAssignableFrom(obj.getClass())) {
					BaseUser user = (BaseUser) obj;
					users.add(user);
				} else if (obj instanceof UserVO) {
					UserVO user = (UserVO) obj;
					users.add(user);
				} else {
					Collection userList = new ArrayList(); // 用户列表
					if (obj instanceof Collection) {
						userList = (Collection) obj;
					} else if (obj instanceof UserVO[]) {
						userList = Arrays.asList((UserVO[]) obj);
					}
					for (Iterator iterator = userList.iterator(); iterator.hasNext();) {
						Object userObj = iterator.next();
						UserVO user = null;
						if (userObj instanceof String) {
							user = (UserVO) userProcess.doView((String) userObj);
						} else {
							user = (UserVO) userObj;
						}
						if (user != null) {
							users.add(user);
						}
					}
				}
				break;
			case ManualNode.CIRCULATOR_EDIT_MODE_DESIGN:
				NameList nameList = NameList.parser(mNode.circulatorNamelist);
				Collection<NameNode> nameNodeList = nameList.toNameNodeCollection();
				if (nameNodeList == null)
					return users;
				for (Iterator iter = nameNodeList.iterator(); iter.hasNext();) {
					NameNode nameNode = (NameNode) iter.next();
					String actorId = nameNode.getId();

					UserVO user = null;
					switch (nameNode.getType()) {
					case Type.TYPE_USER:
						user = (UserVO) userProcess.doView(actorId);
						users.add(user);
						break;
					case Type.TYPE_ROLE:
						RoleVO roleVO = (RoleVO) roleProcess.doView(actorId);
						Collection roleUsers = roleVO.getUsersByDomain(domainid);
						for (Iterator iterator = roleUsers.iterator(); iterator.hasNext();) {
							user = (UserVO) iterator.next();
							users.add(user);
						}
						break;
					case Type.TYPE_DEPARTMENT:
						DepartmentVO dept = (DepartmentVO) deptProcess.doView(actorId);
						Collection deptUsers = dept.getUsers();
						for (Iterator iterator = deptUsers.iterator(); iterator.hasNext();) {
							user = (UserVO) iterator.next();
							users.add(user);
						}
						break;
					default:
						break;
					}
				}
				break;
				//用户设计模式
			case ManualNode.CIRCULATOR_EDIT_MODE_USER_DESIGN:
				NameList userList = NameList.parser(mNode.circulatorNamelistByUser);
				Collection<NameNode> nameNodes = userList.toNameNodeCollection();
				if (nameNodes == null)
					return users;
				for (Iterator<NameNode> iter = nameNodes.iterator(); iter.hasNext();) {
					NameNode nameNode = iter.next();
					String userId = nameNode.getId();
					UserVO user = null;
					switch (nameNode.getType()) {
					case Type.TYPE_USER:
						user = (UserVO) userProcess.doView(userId);
						users.add(user);
						break;
					case Type.TYPE_ROLE:
						RoleVO roleVO = (RoleVO) roleProcess.doView(userId);
						Collection roleUsers = roleVO.getUsersByDomain(domainid);
						for (Iterator iterator = roleUsers.iterator(); iterator.hasNext();) {
							user = (UserVO) iterator.next();
							users.add(user);
						}
						break;
					case Type.TYPE_DEPARTMENT:
						DepartmentVO dept = (DepartmentVO) deptProcess.doView(userId);
						Collection deptUsers = dept.getUsers();
						for (Iterator iterator = deptUsers.iterator(); iterator.hasNext();) {
							user = (UserVO) iterator.next();
							users.add(user);
						}
						break;
					default:
						break;
					}
				}
				break;
			default:
				break;
			}
		}
		return users;
	}

	/**
	 * 根据参数获取前台指定的抄送人
	 * 
	 * @param params
	 * @param node
	 * @param domainid
	 * @param applicationid
	 * @return
	 * @throws Exception
	 */
	public static Collection<BaseUser> getCirculatorsByParameter(ParamsTable params, Node node, String domainid,
			String applicationid) throws Exception {

		Collection<BaseUser> rtn = new ArrayList<BaseUser>();

		String _circulatorInfo = params.getParameterAsString("_circulatorInfo");

		if (!StringUtil.isBlank(_circulatorInfo)) {

			Collection<Object> col = JsonUtil.toCollection(_circulatorInfo);

			for (Iterator<Object> iter = col.iterator(); iter.hasNext();) {
				@SuppressWarnings("unchecked")
				Map<String, Object> item = (Map<String, Object>) iter.next();
					UserProcess userProcess = (UserProcess) ProcessFactory.createProcess(UserProcess.class);
					Object[] circulators = (Object[]) item.get("circulator");
					StringBuffer userids = new StringBuffer();
					for (int j = 0; j < circulators.length; j++) {
						userids.append("'").append(circulators[j].toString()).append("',");
					}
					if (userids.length() > 0) {
						userids.setLength(userids.length() - 1);
						String hql = "FROM " + UserVO.class.getName() + " WHERE id in(" + userids.toString() + ")";
						Collection<UserVO> u = userProcess.doQueryByHQL(hql);
						if (u != null && !u.isEmpty())
							rtn.addAll(u);
					}
			}
		}
		return rtn;
	}

	/**
	 * 获取节点的负责人ID列表
	 * 
	 * @param node
	 * @param domainid
	 * @param applicationid
	 * @return
	 * @throws Exception
	 */
	public static Collection<String> getPrincipalIdList(ParamsTable params, Node node, String domainid,
														String applicationid, BaseUser auditor) throws Exception {
		return getPrincipalMap(params, node, domainid, applicationid,auditor).keySet();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}

	/**
	 * 为流程的list生成一个有checkbox
	 * 
	 * @param moduleId
	 * @param divid
	 * @return
	 * @throws Exception
	 */

	public String getBillDefiNameCheckBox(String moduleId, String divid) throws Exception {
		BillDefiProcess fp = (BillDefiProcess) ProcessFactory.createProcess(BillDefiProcess.class);
		Collection<BillDefiVO> col = fp.getBillDefiByModule(moduleId);
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		if (col != null) {
			for (Iterator<BillDefiVO> iter = col.iterator(); iter.hasNext();) {
				BillDefiVO vo = iter.next();
				map.put(vo.getId(), vo.getSubject());
			}
		}
		String[] str = new String[10];
		return DWRHtmlUtils.createFiledCheckbox(map, divid, str);
	}

	public String toFlowHtmlText(String docid, HttpServletRequest request) throws Exception {
		WebUser webUser = (WebUser) request.getSession().getAttribute(Web.SESSION_ATTRIBUTE_FRONT_USER);
		Document doc = (Document) webUser.getFromTmpspace(docid);
		if (doc != null) {
			BillDefiVO flowVO = doc.getFlowVO();
			DocumentProcess process = (DocumentProcess) ProcessFactory.createRuntimeProcess(DocumentProcess.class,
					flowVO.getApplicationid());
			doc = (Document) process.doView(docid);

			Form form = FormHelper.get_FormById(doc.getFormid());
			Activity flowAct = form.getActivityByType(ActivityType.WORKFLOW_PROCESS);
			String flowShowType = flowAct.getFlowShowType();

			StateMachineHelper helper = new StateMachineHelper(doc);
			return helper.toFlowHtmlText(doc, webUser, flowShowType);
		}

		return "";
	}
	
	public String toFlowHtmlTextByState(String docid,String stateId,String applicationid, HttpServletRequest request) throws Exception {
		WebUser webUser = (WebUser) request.getSession().getAttribute(Web.SESSION_ATTRIBUTE_FRONT_USER);
		Document doc = (Document) webUser.getFromTmpspace(docid);
		FlowStateRT instance = null;
		if(!StringUtil.isBlank(stateId)){
			FlowStateRTProcess stateProcess = (FlowStateRTProcess)ProcessFactory.createRuntimeProcess(FlowStateRTProcess.class,applicationid);
			instance = ((FlowStateRT) stateProcess.doView(stateId));
		}
		if (doc != null && instance != null) {
			
			DocumentProcess process = (DocumentProcess) ProcessFactory.createRuntimeProcess(DocumentProcess.class,
					applicationid);
			doc = (Document) process.doView(docid);
			doc.setState(instance);

			Form form = FormHelper.get_FormById(doc.getFormid());
			Activity flowAct = form.getActivityByType(ActivityType.WORKFLOW_PROCESS);
			String flowShowType = flowAct.getFlowShowType();

			StateMachineHelper helper = new StateMachineHelper(doc);
			return helper.toFlowHtmlText(doc, webUser, flowShowType);
		}

		return "";
	}
}
