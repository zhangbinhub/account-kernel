package OLink.bpm.core.domain.action;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import OLink.bpm.base.action.BaseAction;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.constans.Web;
import OLink.bpm.core.department.ejb.DepartmentProcess;
import OLink.bpm.core.department.ejb.DepartmentVO;
import OLink.bpm.core.deploy.application.ejb.ApplicationProcess;
import OLink.bpm.core.domain.ejb.DomainProcess;
import OLink.bpm.core.domain.ejb.DomainProcessBean;
import OLink.bpm.core.domain.ejb.DomainVO;
import OLink.bpm.core.role.action.RoleHelper;
import OLink.bpm.core.superuser.ejb.SuperUserProcess;
import OLink.bpm.core.superuser.ejb.SuperUserVO;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.workcalendar.calendar.action.CalendarHelper;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import org.apache.commons.lang.StringUtils;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.ActionContext;

import OLink.bpm.core.deploy.application.ejb.ApplicationVO;

/**
 * @see BaseAction DomainAction class.
 * @author Chris
 * @since JDK1.4
 */
public class DomainAction extends BaseAction<DomainVO> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4824002796094284157L;

	public String _strstatus = "true";

	/**
	 * 返回 域状态
	 * 
	 * @return "true"为可用，"false"为不可用
	 * @throws Exception
	 */
	public String get_strstatus() throws Exception {
		DomainVO domain = (DomainVO) getContent();
		if (domain.getStatus() == 1) {
			return "true";
		} else {
			return "false";
		}
	}
	
	/**
	 * 设置 域状态
	 * 
	 * @param strname
	 *            域状态字符串true or false
	 * @throws Exception
	 */
	public void set_strstatus(String strname) throws Exception {
		DomainVO domain = (DomainVO) getContent();
		if (strname != null) {
			if (strname.equalsIgnoreCase("true")) {
				domain.setStatus(1);
			} else {
				domain.setStatus(0);
			}
		}
	}

	/**
	 * 
	 * DepartmentAction structure function.
	 * 
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public DomainAction() throws Exception {
		super(ProcessFactory.createProcess(DomainProcess.class), new DomainVO());
	}

	public String doNew() {
		setContent(new DomainVO());
		return SUCCESS;
	}

	/**
	 * Delete a DomainVO.
	 * 
	 * @return If the action execution was successful.return "SUCCESS".Show an
	 *         success view .
	 *         <p>
	 *         If the action execution was a failure. return "ERROR".Show an
	 *         error view, possibly asking the user to retry entering data.
	 *         <p>
	 *         The "INPUT" is also used if the given input params are invalid,
	 *         meaning the user should try providing input again.
	 * 
	 * @throws Exception
	 */
	public String doDelete() {
		try {
			DepartmentProcess departmentProcess = (DepartmentProcess)ProcessFactory.createProcess(DepartmentProcess.class);
			String errorField = "";
			if (_selects != null) {
				for (int i = 0; i < _selects.length; i++) {
					String id = _selects[i];
					try {
						for (Iterator<DepartmentVO> iterator = departmentProcess.queryByDomain(id).iterator(); iterator.hasNext();) {
							DepartmentVO departmentVO = iterator.next();
							departmentProcess.doRemove(departmentVO);
						}
						process.doRemove(id);
						CalendarHelper cldHelper = new CalendarHelper();
						cldHelper.removeCalendarByDomain(id);
					} catch (Exception e) {
						errorField = e.getMessage() + "," + errorField;
					}
				}
				if (!errorField.equals("")) {
					if (errorField.endsWith(",")) {
						errorField = errorField.substring(0, errorField.length() - 1);
					}
					this.addFieldError("1", errorField);
					return INPUT;
				}
				addActionMessage("{*[delete.successful]*}");
			}

			return SUCCESS;
		} catch (Exception e) {
			addFieldError("", e.getMessage());
			e.printStackTrace();
			return INPUT;
		}
	}

	/**
	 * Save tempDomain.
	 * 
	 * @return If the action execution was successful.return "SUCCESS".Show an
	 *         success view .
	 *         <p>
	 *         If the action execution was a failure. return "ERROR".Show an
	 *         error view, possibly asking the user to retry entering data.
	 *         <p>
	 *         The "INPUT" is also used if the given input params are invalid,
	 *         meaning the user should try providing input again.
	 * 
	 * @throws Exception
	 */
	public String doSave() {

		try {
			DomainVO tempDomain = (DomainVO) (this.getContent());
			String userid = getUser().getId();
			boolean flag = false;
			String tempname = tempDomain.getName();
			DomainVO domain = ((DomainProcess) process).getDomainByName(tempname);

			if (domain != null) {
				if (tempDomain.getId() == null || tempDomain.getId().trim().length() <= 0) {
					this.addFieldError("1", "{*[core.domain.exist]*}");
					flag = true;
				} else if (!tempDomain.getId().trim().equalsIgnoreCase(domain.getId())) {
					this.addFieldError("1", "{*[core.domain.exist]*}");
					flag = true;
				}
			}

			if (!flag) {
				if (tempDomain.getId() == null || tempDomain.getId().equals("")) {
					SuperUserProcess up = (SuperUserProcess) ProcessFactory.createProcess(SuperUserProcess.class);
					SuperUserVO user = (SuperUserVO) up.doView(userid);
					tempDomain.getUsers().add(user);
					process.doCreate(tempDomain);
					//new DomainProcessBean().doCreate(tempDomain); 
					CalendarHelper cldHelper = new CalendarHelper();
					cldHelper.createCalendarByDomain(tempDomain.getId());
				} else {
					DomainVO po = (DomainVO) process.doView(tempDomain.getId());
					if (po != null) {
						tempDomain.setApplications(po.getApplications());
						tempDomain.setUsers(po.getUsers());
					}
					//process.doUpdate(tempDomain);
					new DomainProcessBean().doUpdate(tempDomain);
					
				}
				ServletActionContext.getRequest().getSession().removeAttribute(Web.SKIN_TYPE);
				ServletActionContext.getRequest().getSession().setAttribute(Web.SKIN_TYPE, tempDomain.getSkinType());
				this.addActionMessage("{*[Save_Success]*}");
				return SUCCESS;
			} else {
				return INPUT;

			}
		} catch (Exception e) {
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}
	}

	@SuppressWarnings("unchecked")
	public String doList() {
		try {
			ParamsTable params = getParams();
			params.removeParameter("_pagelines");
			params.setParameter("_pagelines", 10);
			if (params.getParameterAsString("t_users.id") != null) {
				if (!"".equals(params.getParameterAsString("t_users.id"))) {
					Map<String, String> request = (Map) ActionContext.getContext().get("request");
					request.put("userId", params.getParameterAsString("t_users.id"));
				}
			}
			WebUser user = getUser();
			if (user.isSuperAdmin()) {
				params.removeParameter("t_users.id");
			}
			return super.doList();
		} catch (Exception e) {
			addFieldError("", e.getMessage());
			return INPUT;
		}

	}

	/**
	 * 
	 * @return 返回Domain控制管理页
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String doDisplayView() throws Exception {
		Map params = getContext().getParameters();
		String[] ids = (String[]) (params.get("id"));
		String id = null;
		if (ids != null && ids.length > 0) {
			id = ids[0];
		}
		ValueObject contentVO = process.doView(id);
		setContent(contentVO);
		return SUCCESS;
	}

	public String doRemoveApp() throws Exception {
		ParamsTable params = getParams();
		String[] ids = params.getParameterAsArray("_selects");
		try {
			if (ids == null || ids.length <= 0)
				throw new Exception("{*[core.domain.notChoose]*}");
			String domainId = params.getParameterAsString("domain");
			DomainProcess dprocess = (DomainProcess) ProcessFactory.createProcess(DomainProcess.class);
			DomainVO domain = (DomainVO) dprocess.doView(domainId);
			ApplicationProcess process = (ApplicationProcess) ProcessFactory.createProcess(ApplicationProcess.class);
			Set<ApplicationVO> set = new HashSet<ApplicationVO>();
			RoleHelper rh = new RoleHelper();
			set.addAll(domain.getApplications());
			for (Iterator<ApplicationVO> iterator = domain.getApplications().iterator(); iterator.hasNext();) {
				ApplicationVO vo = iterator.next();
				for (int i = 0; i < ids.length; i++) {
					ApplicationVO tmp = (ApplicationVO) process.doView(ids[i]);
					if (tmp != null && tmp.equals(vo)) {
						rh.removeRole(domainId, ids[i]);
						set.remove(vo);
					}
				}
			}
			domain.setApplications(set);
			dprocess.doUpdate(domain);
			PersistenceUtils.currentSession().clear();
		} catch (Exception e) {
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}

		return SUCCESS;
	}
	
	public String doEditByUser() throws Exception{
		ParamsTable params = getParams();
		String domainid=params.getParameterAsString("domain");
		DomainVO vo=(DomainVO) this.process.doView(domainid);
		setContent(vo);
		return SUCCESS;
	}

	public String doRemoveAdmin() throws Exception {
		ParamsTable params = getParams();
		String error = "";
		if (_selects != null) {
			try {

				String domainId = params.getParameterAsString("domain");

				WebUser user = getUser();
				DomainProcess dprocess = (DomainProcess) ProcessFactory.createProcess(DomainProcess.class);
				SuperUserProcess suprocess = (SuperUserProcess) ProcessFactory.createProcess(SuperUserProcess.class);
				DomainVO domain = (DomainVO) dprocess.doView(domainId);

				Collection<SuperUserVO> admins = new HashSet<SuperUserVO>();
				admins.addAll(domain.getUsers());
				for (int i = 0; i < _selects.length; i++) {
					if (!_selects[i].equals(user.getId())) {
						SuperUserVO vo = (SuperUserVO) suprocess.doView(_selects[i]);

						if (admins.contains(vo)) {
							admins.remove(vo);
						}
					} else {
						error = "{*[core.domain.cannotremove]*}";
					}
				}

				domain.setUsers(admins);
				dprocess.doUpdate(domain);
				PersistenceUtils.currentSession().clear();
				if (!StringUtil.isBlank(error)) {
					this.addFieldError("1", error);
				} else {
					this.addActionMessage("{*[delete.successful]*}");
				}
			} catch (Exception e) {
				this.addFieldError("1", e.getMessage());
				return INPUT;
			}
		}
		return SUCCESS;
	}

	public String confirm() throws Exception {
		ParamsTable params = getParams();

		String[] ids = params.getParameterAsArray("_selects");
		try {
			if (ids == null || ids.length <= 0)
				throw new Exception("{*[core.domain.notChoose]*}");
			String domainId = params.getParameterAsString("domain");

			DomainProcess dprocess = (DomainProcess) ProcessFactory.createProcess(DomainProcess.class);

			DomainVO domain = (DomainVO) dprocess.doView(domainId);
			ApplicationProcess process = (ApplicationProcess) ProcessFactory.createProcess(ApplicationProcess.class);
			Set<ApplicationVO> old = new HashSet<ApplicationVO>();
			old.addAll(domain.getApplications());

			for (int i = 0; i < ids.length; i++) {
				ApplicationVO vo = (ApplicationVO) process.doView(ids[i]);
				if (vo != null) {
					old.add(vo);
				}
			}

			domain.setApplications(old);
			dprocess.doUpdate(domain);
		} catch (Exception e) {
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}

		return SUCCESS;
	}

	public String confirmAdmin() throws Exception {

		ParamsTable params = getParams();

		String[] ids = params.getParameterAsArray("_selects");
		try {
			if (ids == null || ids.length <= 0)
				throw new Exception("{*[core.domain.notChoose]*}");
			String domainId = params.getParameterAsString("domain");

			DomainProcess dprocess = (DomainProcess) ProcessFactory.createProcess(DomainProcess.class);

			SuperUserProcess suprocess = (SuperUserProcess) ProcessFactory.createProcess(SuperUserProcess.class);

			DomainVO domain = (DomainVO) dprocess.doView(domainId);

			Collection<SuperUserVO> admins = new HashSet<SuperUserVO>();
			admins.addAll(domain.getUsers());

			for (int i = 0; i < ids.length; i++) {
				SuperUserVO user = (SuperUserVO) suprocess.doView(ids[i]);
				if (user != null) {
					user.setDomainAdmin(true);
					admins.add(user);
				}
			}

			domain.setUsers(admins);
			dprocess.doUpdate(domain);
		} catch (Exception e) {
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}
		return SUCCESS;
	}

	/**
	 * @see ActionContext#getParameters()
	 * @SuppressWarnings getParameters()不支持泛型
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String holdApp() throws Exception {
		try {
			this.validateQueryParams();
			Map params = getContext().getParameters();
			String[] ids = (String[]) (params.get("domain"));
			String id = null;
			if (ids != null && ids.length > 0) {
				id = ids[0];
			}
			ValueObject contentVO = process.doView(id);
			setContent(contentVO);
			Collection<ApplicationVO> col = ((DomainVO) contentVO).getApplications();
			if (col != null && col.size() > 0) {
				DataPackage datas = new DataPackage();
				datas.datas = col;
				ParamsTable paramsTable = getParams();
				String _currpage = paramsTable.getParameterAsString("_currpage");
				String _pagelines = paramsTable.getParameterAsString("_pagelines");
				int page = (_currpage != null && _currpage.length() > 0) ? Integer.parseInt(_currpage) : 1;
				int lines = (_pagelines != null && _pagelines.length() > 0) ? Integer.parseInt(_pagelines) : 10;
				datas.pageNo = page;
				datas.linesPerPage = lines;
				datas.rowCount = col.size();
				setDatas(datas);
			}
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}
	}

	/**
	 * @see ActionContext#getParameters()
	 * @SuppressWarnings getParameters()不支持泛型
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String holdAdmin() throws Exception {
		try {
			Map params = getContext().getParameters();
			String[] ids = (String[]) (params.get("domain"));
			String id = null;
			if (ids != null && ids.length > 0) {
				id = ids[0];
			}
			ValueObject contentVO = process.doView(id);
			setContent(contentVO);
			Collection col = ((DomainVO) contentVO).getUsers();
			if (col != null && col.size() > 0) {
				DataPackage datas = new DataPackage();
				ParamsTable paramsTable = getParams();
				String _currpage = paramsTable.getParameterAsString("_currpage");
				String _pagelines = paramsTable.getParameterAsString("_pagelines");
				int page = (_currpage != null && _currpage.length() > 0) ? Integer.parseInt(_currpage) : 1;
				int lines = (_pagelines != null && _pagelines.length() > 0) ? Integer.parseInt(_pagelines) : 10;
				Iterator it = col.iterator();
				datas.datas = new HashSet();
				// JDBC1.0
				for (int i = 0; it.hasNext(); i++) {
					if (i < (page - 1) * lines) {
						it.next();
						continue;
					}
					if (i >= page * lines)
						break;
					datas.datas.add(it.next());
				}

				datas.pageNo = page;
				datas.linesPerPage = lines;
				datas.rowCount = col.size();
				setDatas(datas);
			}
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}
	}

	public String findManager() throws Exception {
		try {
			ParamsTable params = getParams();
			super.validateQueryParams();
			WebUser user = getUser();
			String name = params.getParameterAsString("sm_name");
			String managerLogin = params.getParameterAsString("sm_users.loginno");
			String _currpage = params.getParameterAsString("_currpage");
			String _pagelines = params.getParameterAsString("_pagelines");
			int page = (_currpage != null && _currpage.length() > 0) ? Integer.parseInt(_currpage) : 1;
			int lines = (_pagelines != null && _pagelines.length() > 0) ? Integer.parseInt(_pagelines) : 10;
			DomainProcess dprocess = (DomainProcess) ProcessFactory.createProcess(DomainProcess.class);

			if (!user.isSuperAdmin()) {
				managerLogin = user.getName();
			}
			if (StringUtils.isBlank(managerLogin) && StringUtils.isBlank(name)) {
				return doList();
			} else {
				if (managerLogin == null) {
					managerLogin = "";
				}
				if (name == null) {
					name = "";
				}
				setDatas(dprocess.queryDomainsbyManagerLoginnoAndName(managerLogin.trim(), name.trim(), page, lines));
			}
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}
	}

	public String get_password() {
		DomainVO vo = (DomainVO) getContent();
		if (vo != null && !StringUtil.isBlank(vo.getSmsMemberPwd()))
			return Web.DEFAULT_SHOWPASSWORD;
		return "";
	}

	public void set_password(String _password) {
		DomainVO vo = (DomainVO) getContent();
		vo.setSmsMemberPwd(_password);
	}

}
