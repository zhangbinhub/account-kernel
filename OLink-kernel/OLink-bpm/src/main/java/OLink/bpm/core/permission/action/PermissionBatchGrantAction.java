package OLink.bpm.core.permission.action;

import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.core.role.ejb.RoleProcess;
import OLink.bpm.core.permission.ejb.PermissionProcess;
import OLink.bpm.core.privilege.operation.ejb.OperationProcess;
import OLink.bpm.core.resource.ejb.ResourceProcess;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.core.permission.ejb.PermissionVO;
import OLink.bpm.core.privilege.operation.ejb.OperationVO;
import OLink.bpm.core.privilege.res.ejb.ResVO;
import OLink.bpm.core.resource.ejb.ResourceVO;
import OLink.bpm.core.role.ejb.RoleVO;
import OLink.bpm.util.ProcessFactory;
import eWAP.core.Tools;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

/**
 * @SuppressWarnings 此类不能使用泛型
 * @author Administrator
 * 
 */
public class PermissionBatchGrantAction extends ActionSupport implements Action {

	private static final long serialVersionUID = -304644165280079279L;

	private String applicationid;

	private Collection _roles;

	private Collection _grantedresources;

	private Collection _grantedoperations;

	private Collection _allresources;

	public Collection get_allresources() {
		return _allresources;
	}

	public void set_allresources(Collection _allresources) {
		this._allresources = _allresources;
	}

	public Collection get_alloperations() {
		return _alloperations;
	}

	public void set_alloperations(Collection _alloperations) {
		this._alloperations = _alloperations;
	}

	private Collection _alloperations;

	public Collection get_roles() {
		return _roles;
	}

	public void set_roles(Collection _roles) {
		this._roles = _roles;
	}

	public Collection get_grantedresources() {
		return _grantedresources;
	}

	public void set_grantedresources(Collection _resources) {
		this._grantedresources = _resources;
	}

	public Collection get_grantedoperations() {
		return _grantedoperations;
	}

	public void set_grantedoperations(Collection _operations) {
		this._grantedoperations = _operations;
	}

	public String getApplicationid() {
		return applicationid;
	}

	public void setApplicationid(String applicationid) {
		this.applicationid = applicationid;
	}

	public String doBatchGrant() throws Exception {
		try {
			System.out.println("doBatchGrant-->" + applicationid);

			RoleProcess roleProcess = (RoleProcess) ProcessFactory
					.createProcess(RoleProcess.class);
			OperationProcess operationProcess = (OperationProcess) ProcessFactory
					.createProcess(OperationProcess.class);

			PermissionProcess permissionProcess = (PermissionProcess) ProcessFactory
					.createProcess(PermissionProcess.class);

			ResourceProcess resourceProcess = (ResourceProcess) ProcessFactory
					.createProcess(ResourceProcess.class);

			try {
				if (_roles != null)
					for (Iterator iterator = _roles.iterator(); iterator
							.hasNext();) {
						String roleId = (String) iterator.next();
						RoleVO roleVO = (RoleVO) roleProcess.doView(roleId);

						// 清空角色所有权限
						Collection<PermissionVO> datas = permissionProcess
								.doQueryByRole(roleId);
						if (datas != null) {
							for (Iterator<PermissionVO> iter = datas.iterator(); iter
									.hasNext();) {
								PermissionVO permission = iter.next();
								permissionProcess.doRemove(permission);
							}
						}

						// 设置菜单
						ParamsTable params = new ParamsTable();
						params.setParameter("application", applicationid);
						Collection<ResourceVO> rs = resourceProcess
								.doQuery(params).datas;

						if (_allresources != null)
							for (Iterator iter = _allresources.iterator(); iter
									.hasNext();) {
								String resourceId = (String) iter.next();

								ResourceVO resourceVO = (ResourceVO) resourceProcess
										.doView(resourceId);
								
								OperationVO operationVO = operationProcess
										.doViewByResource(resourceId, resourceId,
												ResVO.MENU_TYPE);

								createPermissionByResource(
										resourceVO,
										resourcePermissionType(resourceId) ? PermissionVO.TYPE_ALLOW
												: PermissionVO.TYPE_FORBID,
										roleVO);
							}

						// 设置视图、表单权限
						if (_alloperations != null)
							for (Iterator iter = _alloperations.iterator(); iter
									.hasNext();) {
								String operationTxt = (String) iter.next();
								// ResourceVO resourceVO =
								// (ResourceVO)resourceProcess.doView(operationId);

								String[] tmp = operationTxt.split("@");
								
								OperationVO operationVO = operationProcess
										.doViewByResource(tmp[2], tmp[1],
												tmp[0].equals("FORM") ? ResVO.FORM_TYPE
														: ResVO.VIEW_TYPE);

								createPermission(
										tmp[1],
										tmp[2],
										tmp[2],
										tmp[0].equals("FORM") ? ResVO.FORM_TYPE
												: ResVO.VIEW_TYPE,
										operationPermissionType(tmp[2]) ? PermissionVO.TYPE_ALLOW
												: PermissionVO.TYPE_FORBID,
										roleVO);
							}
					}
				
				

			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}

			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("", e.getMessage());
			return INPUT;
		}
	}

	private void createPermission(String resourceId, String operationId, String resourceName,
			Integer resourceType, int permissionType, RoleVO role)
			throws Exception {
		PermissionProcess permissionProcess = (PermissionProcess) ProcessFactory
				.createProcess(PermissionProcess.class);
		PermissionVO pv = new PermissionVO();
		pv.setId(Tools.getSequence());
//		pv.setResource(resourceId);
		pv.setResId(resourceId);
		pv.setResName(resourceName);
		pv.setResType(resourceType);
		pv.setOperationId(operationId);
		// pv.setOperationCode(operationVO.getCode());
		// 赋予禁止或者允许权限
		pv.setType(permissionType);

		pv.setApplicationid(applicationid);
		pv.setRoleId(role.getId());
		role.getPermission().add(pv);

		try {
			PersistenceUtils.beginTransaction();
			permissionProcess.doCreate(pv);
			PersistenceUtils.commitTransaction();
		} catch (Exception e) {
			PersistenceUtils.rollbackTransaction();
		}
	}

	private void createPermissionByResource(ResourceVO resourceVO,
			int permissionType, RoleVO role) throws Exception {
		PermissionProcess permissionProcess = (PermissionProcess) ProcessFactory
				.createProcess(PermissionProcess.class);
		PermissionVO pv = new PermissionVO();
		pv.setId(Tools.getSequence());
		pv.setResourceId(resourceVO.getId());
		pv.setResId(resourceVO.getId());
		pv.setResName(resourceVO.getDescription());
		pv.setResType(ResVO.MENU_TYPE);
		pv.setOperationId(resourceVO.getId());
		// pv.setOperationCode(operationVO.getCode());
		// 赋予禁止或者允许权限
		pv.setType(permissionType);

		pv.setApplicationid(applicationid);
		pv.setRoleId(role.getId());
		role.getPermission().add(pv);

		try {
			PersistenceUtils.beginTransaction();
			permissionProcess.doCreate(pv);
			PersistenceUtils.commitTransaction();
		} catch (Exception e) {
			PersistenceUtils.rollbackTransaction();
		}
	}

	private boolean resourcePermissionType(String resourceId) {

		return _grantedresources != null
				&& _grantedresources.contains(resourceId);
	}

	private boolean operationPermissionType(String operationId) {

		boolean flag = false;
		if (_grantedoperations != null)
			for (Iterator iterator = _grantedoperations.iterator(); iterator
					.hasNext();) {
				String go = (String) iterator.next();
				if (go.indexOf(operationId) >= 0) {
					flag = true;
					break;
				}

			}
		return flag;
	}
}
