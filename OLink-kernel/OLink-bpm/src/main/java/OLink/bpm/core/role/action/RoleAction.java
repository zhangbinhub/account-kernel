package OLink.bpm.core.role.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import OLink.bpm.base.action.BaseAction;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.constans.Environment;
import OLink.bpm.core.department.ejb.DepartmentProcess;
import OLink.bpm.core.department.ejb.DepartmentVO;
import OLink.bpm.core.permission.ejb.PermissionVO;
import OLink.bpm.core.resource.ejb.ResourceProcess;
import OLink.bpm.core.role.ejb.RoleProcess;
import OLink.bpm.core.role.ejb.RoleVO;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.permission.ejb.PermissionProcess;
import OLink.bpm.util.StringUtil;
import OLink.bpm.core.deploy.application.ejb.ApplicationProcess;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.core.resource.ejb.ResourceVO;
import OLink.bpm.core.user.ejb.UserProcess;

import com.opensymphony.webwork.ServletActionContext;

public class RoleAction extends BaseAction<RoleVO> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2005136705776969715L;

	private String jsonStr;

	private String checkedList;
	
	private String tempRoles;

	/**
	 * 顶级菜单ID列表
	 */
	private Collection<String> _topresourcelist;

	public Collection<String> get_topresourcelist() {
		RoleVO role = (RoleVO) getContent();
		_topresourcelist = new HashSet<String>();
		
		try {
			ResourceProcess process = (ResourceProcess) ProcessFactory.createProcess(ResourceProcess.class);
		if (role.getId() != null && role.getId().trim().length() > 0) {
			Collection<PermissionVO> col = role.getPermission();
			Iterator<PermissionVO> it = col.iterator();
			while (it.hasNext()) {
				PermissionVO per = it.next();
				if (!StringUtil.isBlank(per.getResourceId())) {
					ResourceVO currentResource = (ResourceVO) process.doView(per.getResourceId());
					while (currentResource.getSuperior() != null) {
						currentResource = currentResource.getSuperior();
					}
					_topresourcelist.add(currentResource.getId());
				}
			}
		}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return _topresourcelist;
	}

	public void set_topresourcelist(Collection<String> _topresourcelist) {
		this._topresourcelist = _topresourcelist;
	}

	public String doSelectAjax() throws Exception {
		return SUCCESS;
	}

	/**
	 * @SuppressWarnings 工厂方法创建的业务类，无法使用泛型
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public RoleAction() throws Exception {
		super(ProcessFactory.createProcess(RoleProcess.class), new RoleVO());
	}

	/**
	 * 保存
	 */
	public String doSave() {

		try {
			UserProcess userProcess = (UserProcess) ProcessFactory.createProcess(UserProcess.class);
			PermissionProcess permissionProcess = (PermissionProcess) ProcessFactory
					.createProcess(PermissionProcess.class);
			RoleVO tempRoleVO = (RoleVO) (this.getContent());
			boolean flag = false;
			String tempname = tempRoleVO.getName();
			RoleVO role = ((RoleProcess) process).doViewByName(tempname, application);
			
			if(tempRoleVO.getRoleNo() != null && !tempRoleVO.getRoleNo().equals("")){
				ParamsTable p = new ParamsTable();
				p.setParameter("t_roleno", tempRoleVO.getRoleNo());
				Collection<RoleVO> rlvo_list = process
						.doSimpleQuery(p, tempRoleVO.getApplicationid());
				if (!rlvo_list.isEmpty()) {
					RoleVO rlvo = rlvo_list.iterator().next();
					if (!rlvo.getId().equals(tempRoleVO.getId())) {
						throw new Exception("编号已存在！");
					}
				}
			}

			if (role != null) {
				if (tempRoleVO.getId() == null || tempRoleVO.getId().trim().length() <= 0) {// 判断新建不能重名
					this.addFieldError("1", "{*[core.role.exist]*}");
					flag = true;
				} else if (!tempRoleVO.getId().trim().equalsIgnoreCase(role.getId())) {// 修改不能重名
					this.addFieldError("1", "{*[core.role.exist]*}");
					flag = true;
				}
			}

			if (!flag) {
				if (tempRoleVO.getId() != null) {
					Collection<PermissionVO> permissions = permissionProcess.doQueryByRole(tempRoleVO.getId());
					Set<PermissionVO> set = new HashSet<PermissionVO>();
					for (Iterator<PermissionVO> it = permissions.iterator(); it.hasNext();) {
						set.add(it.next());
					}
					DataPackage<UserVO> users = userProcess.doQueryByRoleId(tempRoleVO.getId());
					tempRoleVO.setPermission(set);
					tempRoleVO.setUsers(users.getDatas());
					this.setContent(tempRoleVO);
				}
				return super.doSave();
			} else {
				return INPUT;
			}
		} catch (Exception e) {
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}
	}

	/** 保存并新建 */
	public String doSaveAndNew() {
		try {
			UserProcess userProcess = (UserProcess) ProcessFactory.createProcess(UserProcess.class);
			PermissionProcess permissionProcess = (PermissionProcess) ProcessFactory
					.createProcess(PermissionProcess.class);
			RoleVO tempRoleVO = (RoleVO) (this.getContent());
			boolean flag = false;
			String tempname = tempRoleVO.getName();
			RoleVO role = ((RoleProcess) process).doViewByName(tempname, application);

			if (role != null) {
				if (tempRoleVO.getId() == null || tempRoleVO.getId().trim().length() <= 0) {// 判断新建不能重名
					this.addFieldError("1", "{*[core.role.exist]*}");
					flag = true;
				} else if (!tempRoleVO.getId().trim().equalsIgnoreCase(role.getId())) {// 修改不能重名
					this.addFieldError("1", "{*[core.role.exist]*}");
					flag = true;
				}
			}

			if (!flag) {
				if (tempRoleVO.getId() == null || tempRoleVO.getId().equals("")) {
					process.doCreate(tempRoleVO);
				} else {
					Collection<PermissionVO> permissions = permissionProcess.doQueryByRole(tempRoleVO.getId());
					Set<PermissionVO> set = new HashSet<PermissionVO>();
					for (Iterator<PermissionVO> it = permissions.iterator(); it.hasNext();) {
						set.add(it.next());
					}
					DataPackage<UserVO> users = userProcess.doQueryByRoleId(tempRoleVO.getId());
					tempRoleVO.setPermission(set);
					tempRoleVO.setUsers(users.getDatas());
					this.setContent(tempRoleVO);
					process.doUpdate(tempRoleVO);
				}
				this.addActionMessage("{*[Save_Success]*}");
				setContent(new RoleVO());
				return SUCCESS;
			} else {
				return INPUT;
			}
		} catch (Exception e) {
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}
	}

	public String getNodes() {
		try {
			String applicationid = getApplication();
			String JSONStr = getRoleJSON(applicationid);

			this.setJsonStr(JSONStr);
		} catch (Exception e) {
			this.addFieldError("getNodes", e.getMessage());
			return INPUT;
		}

		return SUCCESS;
	}

	public String getRoleJSON(String application) throws Exception {
		StringBuffer builder = new StringBuffer();
		ApplicationProcess actionProcess = (ApplicationProcess) ProcessFactory.createProcess(ApplicationProcess.class);
		RoleProcess rp = (RoleProcess) process;
		ApplicationVO appvo = (ApplicationVO) actionProcess.doView(application);
		if (application == null || application.trim().length() <= 0) {
			builder.append("[");
			builder.append(getNodeJSON(appvo));
			builder.deleteCharAt(builder.lastIndexOf(","));
			builder.append("]");
		} else {
			Collection<RoleVO> all = new ArrayList<RoleVO>();
			all.addAll(rp.getRolesByApplication(application));
			builder.append("[");
			for (Iterator<RoleVO> iter = all.iterator(); iter.hasNext();) {
				ValueObject vo = iter.next();
				builder.append(getNodeJSON(vo));
			}
			builder.deleteCharAt(builder.lastIndexOf(","));
			builder.append("]");
		}

		return "";
	}

	public String doRolesList() {
		StringBuffer html = new StringBuffer();
		HttpServletResponse response = ServletActionContext.getResponse();
		try {
			Long page = this.getParams().getParameterAsLong("_currpage");
			int currentPage = page != null ? page.intValue() : 1;
			this.getParams().setParameter("_pagelines", 10);

			// Collection roles = this.process.doSimpleQuery(getParams(),
			// getApplication());
			DataPackage<RoleVO> datas = this.process.doQuery(this.getParams());
			for (Iterator<RoleVO> iter = datas.datas.iterator(); iter.hasNext();) {
				RoleVO tempRole = iter.next();
				html.append("<div id='" + tempRole.getId() + "' class='list_div' title='" + tempRole.getName()
						+ "' onclick='getUserListByRole(jQuery(this))'>");
				html.append("<img id='img_" + tempRole.getId() + "' class='selectImg_right' src='"
						+ Environment.getInstance().getContextPath() + "/resource/images/right_2.gif'/>");
				html.append(tempRole.getName());
				html.append("</div>");
			}

			String url = "/portal/role/getRolesList.action?application="
					+ this.getParams().getParameterAsString("application");
			getPageDiv(html, currentPage, datas.getPageCount(), url);

			if (!"".equals(html.toString())) {
				response.setContentType("text/html;charset=UTF-8");
				response.getWriter().write(html.toString());
			}
		} catch (Exception e) {
		}
		return html.toString();
	}

	public Map<String, String> get_departmentList(String application) throws Exception {
		DepartmentProcess dp = (DepartmentProcess) ProcessFactory.createProcess(DepartmentProcess.class);
		Collection<DepartmentVO> dc = dp.doSimpleQuery(null, application);

		Map<String, String> dm = dp.deepSearchDepartmentTree(dc, null, null, 0);
		return dm;
	}

	public String getJsonStr() {
		return jsonStr;
	}

	public void setJsonStr(String jsonStr) {
		this.jsonStr = jsonStr;
	}

	private String getNodeJSON(ValueObject node) throws Exception {
		StringBuffer JSONbuffer = new StringBuffer();
		String ctxPath = ServletActionContext.getRequest().getContextPath();
		if (ctxPath.equals("/")) {
			ctxPath = "";
		}
		String id = "";
		String name = "";
		String icon = "";

		if (node instanceof RoleVO) {
			RoleVO role = (RoleVO) node;
			id = "R" + role.getId();
			name = role.getName();
			icon = ctxPath + "/resource/images/dtree/group.gif";
		}

		if (checkedList == null) {
			checkedList = "";
		}

		JSONbuffer.append("{");
		JSONbuffer.append("text:'" + name + "',");
		JSONbuffer.append("id:'" + id + "',");
		JSONbuffer.append("icon:'" + icon + "',");
		JSONbuffer.append("checked:" + (checkedList.indexOf(id) != -1));
		JSONbuffer.append("},");
		return JSONbuffer.toString();
	}

	public void getPageDiv(StringBuffer html, int currentPage, int pageCount, String url) {
		html.append("<div style='padding:5px;border-bottom:1px solid gray;'>");
		if (currentPage > 1) {
			html.append("<a style='cursor: pointer;color:#316AC5;' onclick='doLeftPageNav(\"" + url + "&_currpage=1\")'>{*[FirstPage]*}</a>&nbsp;");
			html.append("<a style='cursor: pointer;color:#316AC5;' onclick='doLeftPageNav(\"" + url + "&_currpage=" + (currentPage - 1)
					+ "\")'>{*[PrevPage]*}</a>&nbsp;");
		}

		if (currentPage < pageCount) {
			html.append("<a style='cursor: pointer;color:#316AC5;' onclick='javascript:doLeftPageNav(\"" + url + "&_currpage=" + (currentPage + 1)
					+ "\")'>{*[NextPage]*}</a>&nbsp;");
			html.append("<a style='cursor: pointer;color:#316AC5;' onclick='doLeftPageNav(\"" + url + "&_currpage=" + pageCount
					+ "\")'>{*[EndPage]*}</a>&nbsp;");
		}

		html.append("{*[InPage]*}").append(currentPage).append("{*[Page]*}/{*[Total]*}").append(pageCount).append(
				"{*[Pages]*}&nbsp;");
		html.append("</div>");
	}
	
	/**
	 * 角色列表
	 */
	@SuppressWarnings("unchecked")
	public String doList(){
		ParamsTable params = this.getParams();
		params.removeParameter("tempRoles");
		params.setParameter("_orderby", "name");
		getContext().getParameters().put("tempRoles", tempRoles);
		return super.doList();
	}

	public String getTempRoles() {
		return tempRoles;
	}

	public void setTempRoles(String tempRoles) {
		this.tempRoles = tempRoles;
	}
	
}
