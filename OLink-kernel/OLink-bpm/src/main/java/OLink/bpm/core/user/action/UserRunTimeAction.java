package OLink.bpm.core.user.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import OLink.bpm.core.user.ejb.ExistNameException;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.util.StringUtil;
import OLink.bpm.util.http.ResponseUtil;
import OLink.bpm.core.user.ejb.UserProcess;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.constans.Web;
import OLink.bpm.core.email.email.ejb.EmailUser;
import OLink.bpm.core.user.ejb.BaseUser;
import OLink.bpm.core.workflow.engine.StateMachineHelper;
import OLink.bpm.util.ProcessFactory;

import com.opensymphony.webwork.ServletActionContext;

public class UserRunTimeAction extends UserAction {

	public UserRunTimeAction() throws Exception {
		super();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private EmailUser emailUser;
	/**
	 * 保存个人信息
	 * 
	 * @SuppressWarnings webwork不支持泛型
	 * @return 成功处理返回"SUCCESS",否则提示失败
	 * @throws Exception
	 */
	public String doSavePersonal() throws Exception {
		try {
			UserVO user = (UserVO) getContent();
			//增加 by XGY
			if(user.getProxyUser()!=null && user.getStartProxyTime()!=null && user.getEndProxyTime()==null){
				this.addFieldError("1", "{*[Please]*}{*[Input]*}{*[Proxy]*}{*[End]*}{*[Date]*}");
				return INPUT;
			}
			
			if(user.getProxyUser()!=null && user.getStartProxyTime()==null && user.getEndProxyTime()!=null){
				this.addFieldError("1", "{*[Please]*}{*[Input]*}{*[Proxy]*}{*[Start]*}{*[Date]*}");
				return INPUT;
			}
			
			if(user.getProxyUser()!=null && startProxyTime!=null && !startProxyTime.equals("") &&user.getStartProxyTime().getTime()>user.getEndProxyTime().getTime()){
				this.addFieldError("1", "{*[page.core.calendar.overoftime]*}");
				return INPUT;
			}
			
			if(user.getProxyUser()!=null && endProxyTime!=null && !endProxyTime.equals("") &&user.getEndProxyTime().getTime()<(new Date()).getTime()){
				this.addFieldError("1", "{*[Proxy]*}{*[End]*}{*[Date]*}不得晚于{*[Current]*}{*[Time]*}");
				return INPUT;
			}
			
			WebUser webUser = getUser();
			
			//Email信息处理
//			EmailUserProcess euserpro = (EmailUserProcess) ProcessFactory.createProcess(EmailUserProcess.class);
//			EmailUser emailUser = (EmailUser)euserpro.doView(getEmailUser().getId());
//			if(emailUser == null){
//				euserpro.doCreateEmailUser(getEmailUser());
//			}else{
//				euserpro.doUpdateEmailUser(getEmailUser());
//			}
			
			//待办信息设置
//			UserDefined userDefined = getUserDefined();
//			userDefined.setApplicationid(getApplication());
//			userDefined.setUserId(webUser.getId());
//			
//			UserDefinedProcess userDefinedProcess = (UserDefinedProcess) ProcessFactory.createProcess(UserDefinedProcess.class);
			
			//判断是否为后台默认首页
//			if("1".equals(userDefined.getType())){
//				userDefined.setType("0");
//				userDefined.setCreator(user.getName());
//				userDefinedProcess.doCreate(userDefined);
//			}else{
//				//判断是否己有自定义首页
//				if (userDefinedProcess.doView(userDefined.getId()) == null) {
//					userDefinedProcess.doCreate(userDefined);
//				} else {
//					userDefinedProcess.doUpdate(userDefined);
//				}
//			}
			((UserProcess) process).doPersonalUpdate(user);
			setContent(user);
			webUser.setName(user.getName());
			webUser.setLoginno(user.getLoginno());
			webUser.setLoginpwd(user.getLoginpwd());
			webUser.setEmail(user.getEmail());
			webUser.setTelephone(user.getTelephone());
			webUser.setCalendarType(user.getCalendarType());
			webUser.setStartProxyTime(user.getStartProxyTime());
			webUser.setEndProxyTime(user.getEndProxyTime());
//Add BY XGY 20130408			
			webUser.setDefaultDepartment(user.getDefaultDepartment());


			// 把用户设置赋给webuser
			webUser.setUserSetup(user.getUserSetup());
			HttpSession session = ServletActionContext.getRequest().getSession();
			//getContext().getSession().put(getWebUserSessionKey(), webUser);

			if (user.getUserSetup() != null) {
				String uskin = user.getUserSetup().getUserSkin();
				if (!StringUtil.isBlank(uskin)) {
					String oldSkin = (String) session.getAttribute(Web.SKIN_TYPE);
					if (!StringUtil.isBlank(oldSkin)) {
						if(!oldSkin.equals(uskin)){
							session.setAttribute(Web.SKIN_TYPE, uskin);
							return "switchskin";
						}
					}else{
						session.setAttribute(Web.SKIN_TYPE, uskin);
						return "switchskin";
					}
				}
			}

			this.addActionMessage("{*[Save_Success]*}");
		} catch (ExistNameException e) {
			this.addFieldError("1", e.getMessage());
		}
		return SUCCESS;
	}

	/**
	 * 用户选择
	 */
	public String doSelectUser() throws Exception {
		return SUCCESS;
	}

	public String getWebUserSessionKey() {
		return Web.SESSION_ATTRIBUTE_FRONT_USER;
	}

	public String listUserExcept() throws Exception {

		ParamsTable params = getParams();

		WebUser user = getUser();

		params.setParameter("t_domainid", getDomain());
		DataPackage<UserVO> pack = ((UserProcess) process).queryUsersExcept(params, user);

		setDatas(pack);

		return SUCCESS;
	}

	/**
	 * 点击树节点对應部门的用户列表。
	 * 
	 * @return
	 */
	public String doTreeList() {
		try {
			ParamsTable params = getParams();
			String domain = params.getParameterAsString("domain");
			String departid = params.getParameterAsString("departid");

			// DepartmentProcess departmentProcess = (DepartmentProcess)
			// ProcessFactory.createProcess(DepartmentProcess.class);
			if (domain != null && domain.trim().length() > 0) {
				if (departid == null || "".equals(departid)) {
					params.setParameter("t_domainid", domain);
					WebUser user = getUser();
					setDatas(process.doQuery(params, user));
				}
				// 列出選擇部門的所有用戶
				else {
					params.setParameter("t_domainid", domain);
					params.setParameter("sm_userDepartmentSets.departmentId", departid);
					WebUser user = getUser();
					setDatas(process.doQuery(params, user));
				}
			}
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("", e.getMessage());
			return INPUT;
		}

	}

	/**
	 * 根据部门获取用户列表
	 * 
	 * @return
	 */
	public String doUserListByDept() {
		StringBuffer html = new StringBuffer();
		HttpServletResponse response = ServletActionContext.getResponse();
		try {
			ParamsTable params = getParams();
			String domain = params.getParameterAsString("domain");
			String departid = params.getParameterAsString("departid");

			Long page = this.getParams().getParameterAsLong("_currpage");
			int currentPage = page != null ? page.intValue() : 1;
			this.getParams().setParameter("_pagelines", 10);
			int total = 0;

			// DepartmentProcess departmentProcess = (DepartmentProcess)
			// ProcessFactory.createProcess(DepartmentProcess.class);
			if (domain != null && domain.trim().length() > 0) {
				if (departid != null && !"".equals(departid)) {
					
					String hql = "FROM "+UserVO.class.getName()+ " WHERE userDepartmentSets.departmentId='"+departid+"' AND domainid ='"+domain+"'";
					hql+=biuldUserIdStr();
					
					Collection<UserVO> users = process.doQueryByHQL(hql, currentPage, 10);
					total = process.doGetTotalLines(hql);
//					params.setParameter("t_domainid", domain);
//					params.setParameter("sm_userDepartmentSets.departmentId", departid);
//					WebUser user = getUser();
//					setDatas(process.doQuery(params, user));
//					Collection<UserVO> users = this.getDatas().getDatas();
					for (Iterator<UserVO> iter = users.iterator(); iter.hasNext();) {
						UserVO tempUser = iter.next();
						html.append("<div class='list_div' title='" + tempUser.getName() + "'>");
						html.append("<input class='list_div_click' type='checkbox' name='" + tempUser.getName()
								+ "' id='" + tempUser.getId() + "' onclick='selectUser(jQuery(this),true)'>");
						html.append(tempUser.getName());
						html.append("</div>");
					}

				}
				
				if (total > 0) {
					String url = "/portal/user/treelist.action?departid=" + departid;
					url += "&domain=" + domain;
					getPageDiv(html, currentPage, total, url);
				}
			}
			if (html.toString() != "") {
				response.setContentType("text/html;charset=UTF-8");
				response.getWriter().write(html.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return html.toString();
	}
	
	
	public String biuldUserIdStr() throws Exception {
		StringBuffer w = new StringBuffer();
		ParamsTable params = getParams();
		if(!StringUtil.isBlank(params.getParameterAsString("_isGetApprover2SubFlow")) && "true".equals(params.getParameterAsString("_isGetApprover2SubFlow"))){
			String _flowId = params.getParameterAsString("_flowId");
			String _docId = params.getParameterAsString("_docId");
			String _nodeId = params.getParameterAsString("_nodeId");
			 Collection<BaseUser> users = StateMachineHelper.getPrincipalList(_docId, getUser(), _nodeId,ServletActionContext.getRequest(), _flowId);
			 if(users !=null && !users.isEmpty()){
				 w.append(" AND id in(");
				 for (Iterator<BaseUser> iterator = users.iterator(); iterator.hasNext();) {
					BaseUser user = iterator.next();
					w.append("'").append(user.getId()).append("',");
				 }
				 w.setLength(w.length()-1);
				 w.append(")");
			 }
		}
		return w.toString();
	}

	/**
	 * 根据企业域获取所有用户列表
	 * 
	 * @return
	 */
	public String doAllUser() {
		StringBuffer html = new StringBuffer();
		HttpServletResponse response = ServletActionContext.getResponse();
		try {
			ParamsTable params = getParams();
			String domain = params.getParameterAsString("domain");
			String sm_name = params.getParameterAsString("sm_name");
			
			Long page = this.getParams().getParameterAsLong("_currpage");
			int currentPage = page != null ? page.intValue() : 1;
			int total =0;
			this.getParams().setParameter("_pagelines", 10);

			if (getDomain() != null && domain.trim().length() > 0) {
				String hql = "FROM "+UserVO.class.getName()+ " WHERE domainid='"+domain+"' ";
				if(!StringUtil.isBlank(sm_name)){
					hql+=" AND name like '%"+sm_name+"%' ";
				}
				hql+=biuldUserIdStr();
//				params.setParameter("t_domainid", domain);
//				WebUser user = getUser();
				Collection<UserVO> users = process.doQueryByHQL(hql,currentPage, 10);
				total = process.doGetTotalLines(hql);
				for (Iterator<UserVO> iter = users.iterator(); iter.hasNext();) {
					UserVO tempUser = iter.next();
					html.append("<div class='list_div' title='" + tempUser.getName() + "'>");
					html.append("<input class='list_div_click' type='checkbox' name='" + tempUser.getName() + "' id='"
							+ tempUser.getId() + "' onclick='selectUser(jQuery(this),true)'>");
					html.append(tempUser.getName());
					html.append("</div>");
				}
			}
			if (total > 0) {
				String url = "/portal/user/getAllUser.action?domain=" + domain + "&sm_name=" + sm_name;
				getPageDiv(html, currentPage,total, url);
			}

			if (!"".equals(html.toString())) {
				response.setContentType("text/html;charset=UTF-8");
				response.getWriter().write(html.toString());
			}
		} catch (Exception e) {
		}
		return html.toString();
	}

	/**
	 * 根据选中的ID获取用户JSON
	 * 
	 * @return
	 */
	public String doListBySelectToJSON() {
		String[] selects = get_selects();
		try {
			UserProcess userProcess = (UserProcess) ProcessFactory.createProcess(UserProcess.class);
			List<String> list = new ArrayList<String>();
			if (selects != null && selects.length > 0) {
				for (int i = 0; i < selects.length; i++) {
					UserVO userVO = (UserVO) userProcess.doView(selects[i]);
					if (userVO != null) {
						list.add(userVO.toJSON());
					}
				}
			}

			ResponseUtil.setJsonToResponse(ServletActionContext.getResponse(), list.toString());
		} catch (Exception e) {
			addFieldError("", e.getMessage());
			e.printStackTrace();
		}

		return NONE;
	}

	/**
	 * 获取所有在线用户列表
	 * 
	 * @return
	 */
	// @SuppressWarnings("unchecked")
	public String doOnLineUserList() {
		StringBuffer html = new StringBuffer();
		HttpServletResponse response = ServletActionContext.getResponse();
		try {
			Long page = this.getParams().getParameterAsLong("_currpage");
			int currentPage = page != null ? page.intValue() : 1;
			this.getParams().setParameter("_pagelines", 10);

			// OnlineUsers onlineusers = new OnlineUsers();
			// Collection users =
			// OnlineUsers.doQuery(this.getParams()).getDatas();
			DataPackage<WebUser> datas = OnlineUsers.doQuery(this.getParams());
			html.append("<input type='hidden' value='" + OnlineUsers.getUsersCount()
					+ "' id='onLineUsersCount' name='onLineUsersCount'>");
			for (Iterator<WebUser> iter = datas.datas.iterator(); iter.hasNext();) {
				WebUser tempUser = iter.next();
				html.append("<div class='list_div' title='" + tempUser.getName() + "'>");
				html.append("<input class='list_div_click' type='checkbox' name='" + tempUser.getName() + "' id='"
						+ tempUser.getId() + "' onclick='selectUser(jQuery(this),true)'>");
				html.append(tempUser.getName());
				html.append("</div>");
			}
			if (datas.getPageCount() > 0) {
				String url = "/portal/user/getOnLineUserList.action";
				getPageDiv(html, currentPage, datas.getRowCount(), url);
			}
			if (!"".equals(html.toString())) {
				response.setContentType("text/html;charset=UTF-8");
				response.getWriter().write(html.toString());
			}
		} catch (Exception e) {
		}
		return html.toString();
	}

	/**
	 * 根据角色查找用户列表
	 * 
	 * @return
	 */
	// @SuppressWarnings("unchecked")
	public String getUserListByRole() {
		StringBuffer html = new StringBuffer();
		HttpServletResponse response = ServletActionContext.getResponse();
		try {
			ParamsTable params = getParams();
			String rolesid = params.getParameterAsString("rolesid");
			String applicationid = params.getParameterAsString("applicationid");

			Long page = params.getParameterAsLong("_currpage");
			int currentPage = page != null ? page.intValue() : 1;
			int total =0;
			params.setParameter("_pagelines", 10);

//			params.setParameter("sm_userRoleSets.roleId", rolesid);
//			params.setParameter("t_domainid", getUser().getDomainid());
			if (applicationid != null && applicationid.trim().length() > 0) {
				if (rolesid != null && !"".equals(rolesid)) {
					
					String hql = "FROM "+UserVO.class.getName()+ " WHERE userRoleSets.roleId='"+rolesid+"' AND domainid ='"+getUser().getDomainid()+"'";
					hql+=biuldUserIdStr();
					
					
					
//					DataPackage<UserVO> datas = this.process.doQuery(params, getUser());
					Collection<UserVO> users = process.doQueryByHQL(hql,currentPage, 10);
					total = process.doGetTotalLines(hql);
					for (Iterator<UserVO> iter = users.iterator(); iter.hasNext();) {
						UserVO tempUser = iter.next();
						html.append("<div class='list_div' title='" + tempUser.getName() + "'>");

						html.append("<input class='list_div_click' type='checkbox' name='" + tempUser.getName()
								+ "' id='" + tempUser.getId() + "' onclick='selectUser(jQuery(this),true)'>");
						html.append(tempUser.getName());
						html.append("</div>");
					}
					String url = "/portal/user/getUserListByRole.action?applicationid=" + applicationid;
					url += "&rolesid=" + rolesid;
					getPageDiv(html, currentPage, total, url);
				}
			}
			if (html.toString() != "") {
				response.setContentType("text/html;charset=UTF-8");
				response.getWriter().write(html.toString());
			}
		} catch (Exception e) {
		}
		return html.toString();
	}

	public void getPageDiv(StringBuffer html, int currentPage, int total, String url) {
		int pageCount = (int) Math.ceil((double) total / 10.0);
		
		html.append("<div style='padding:5px;border-bottom:1px solid gray;'>");
		if (currentPage > 1) {
			html.append("<a style='cursor: pointer;color:#316AC5;' onclick='doPageNav(\"" + url
					+ "&_currpage=1\")'>{*[FirstPage]*}</a>&nbsp;");
			html.append("<a style='cursor: pointer;color:#316AC5;' onclick='doPageNav(\"" + url + "&_currpage="
					+ (currentPage - 1) + "\")'>{*[PrevPage]*}</a>&nbsp;");
		}

		if (currentPage < pageCount) {
			html.append("<a style='cursor: pointer;color:#316AC5;' onclick='doPageNav(\"" + url + "&_currpage="
					+ (currentPage + 1) + "\")'>{*[NextPage]*}</a>&nbsp;");
			html.append("<a style='cursor: pointer;color:#316AC5;' onclick='doPageNav(\"" + url + "&_currpage="
					+ pageCount + "\")'>{*[EndPage]*}</a>&nbsp;");
		}

		html.append("{*[InPage]*}").append(currentPage).append("{*[Page]*}/{*[Total]*}").append(pageCount).append(
				"{*[Pages]*}&nbsp;");
		html.append("</div>");
	}

	public EmailUser getEmailUser() {
		return emailUser;
	}

	public void setEmailUser(EmailUser emailUser) {
		this.emailUser = emailUser;
	}
}
