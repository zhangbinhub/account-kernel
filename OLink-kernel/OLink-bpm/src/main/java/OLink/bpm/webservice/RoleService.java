package OLink.bpm.webservice;

import java.util.Collection;
import java.util.HashSet;

import OLink.bpm.core.permission.ejb.PermissionVO;
import OLink.bpm.core.role.ejb.RoleProcess;
import OLink.bpm.core.role.ejb.RoleVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.webservice.fault.RoleServiceFault;
import OLink.bpm.webservice.model.SimpleRole;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.core.resource.ejb.ResourceProcess;
import OLink.bpm.core.resource.ejb.ResourceVO;
import OLink.bpm.util.StringUtil;
import OLink.bpm.util.sequence.Sequence;
import OLink.bpm.util.ObjectUtil;

public class RoleService {

	public SimpleRole createRole(SimpleRole role) throws RoleServiceFault {
		try {
			RoleProcess process = (RoleProcess) ProcessFactory
					.createProcess(RoleProcess.class);
			if (role == null || StringUtil.isBlank(role.getName())) {
				throw new NullPointerException("对象为空或对象的名称为空.");
			}

			ApplicationVO application = WebServiceUtil.validateApplication(role
					.getApplicationName());
			RoleVO vo = new RoleVO();
			ObjectUtil.copyProperties(vo, role);
			vo.setApplicationid(application.getId());

			RoleVO temp = process.doViewByName(role.getName(),
					application.getId());
			if (temp == null) {
				process.doCreate(vo);
			} else {
				if (role.getName().equalsIgnoreCase(temp.getName())) {
					throw new Exception("该角色名称" + role.getName() + "已存在.");
				}
			}
		} catch (Exception e) {
			throw new RoleServiceFault(e.getMessage());
		}
		return role;
	}

	public void updateRole(SimpleRole role) throws RoleServiceFault {
		try {
			RoleProcess process = (RoleProcess) ProcessFactory
					.createProcess(RoleProcess.class);
			if (role == null || StringUtil.isBlank(role.getName())) {
				throw new NullPointerException("对象为空或对象名称为空!");
			}
			ApplicationVO application = WebServiceUtil.validateApplication(role
					.getApplicationName());
			RoleVO vo = (RoleVO) process.doView(role.getId());
			if (vo == null)
				throw new Exception("数据库不存在该" + role.getId() + "对象.");

			if (!vo.getName().equals(role.getName())) {
				RoleVO temp = process.doViewByName(role
						.getName(), application.getId());
				if (temp != null) {
					throw new Exception("该角色名称" + role.getName() + "已存在.");
				}
			}

			ObjectUtil.copyProperties(vo, role);
			process.doUpdate(vo);
		} catch (Exception e) {
			throw new RoleServiceFault(e.getMessage());
		}
	}

	public SimpleRole getRole(String pk) throws RoleServiceFault {
		SimpleRole role = null;
		try {
			RoleProcess process = (RoleProcess) ProcessFactory
					.createProcess(RoleProcess.class);
			if (pk == null) {
				throw new NullPointerException("主键为空.");
			}
			RoleVO vo = (RoleVO) process.doView(pk);
			if (vo != null) {
				role = new SimpleRole();
				ObjectUtil.copyProperties(role, vo);
			}
		} catch (Exception e) {
			throw new RoleServiceFault(e.getMessage());
		}
		return role;
	}

	public void deleteRole(String pk) throws RoleServiceFault {
		try {
			RoleProcess process = (RoleProcess) ProcessFactory
					.createProcess(RoleProcess.class);
			if (pk == null) {
				throw new NullPointerException("主键为空.");
			}
			process.doRemove(pk);
		} catch (Exception e) {
			throw new RoleServiceFault(e.getMessage());
		}
	}

	/**
	 * 设置角色权限
	 * 
	 * @param role
	 *            -角色
	 * @param permission
	 *            -权限ID字符串组
	 * @throws RoleServiceFault
	 */
	public void setPermissionSet(SimpleRole role, String[] resources)
			throws RoleServiceFault {
		try {
			RoleProcess process = (RoleProcess) ProcessFactory
					.createProcess(RoleProcess.class);
			if (role == null || StringUtil.isBlank(role.getId()))
				throw new NullPointerException("对象为空或对象的ID为空!");
			ApplicationVO application = WebServiceUtil.validateApplication(role
					.getApplicationName());
			RoleVO vo = (RoleVO) process.doView(role.getId());
			if (vo == null)
				throw new Exception("数据库不存在该ID对象.");

			Collection<PermissionVO> coll = new HashSet<PermissionVO>();
			if (resources != null) {
				for (String id : resources) {
					ResourceProcess rp = (ResourceProcess) ProcessFactory
							.createProcess(ResourceProcess.class);
					ResourceVO res = (ResourceVO) rp.doView(id);
					if (res != null
							&& res.getApplicationid().equals(
									application.getId())) {
						PermissionVO per = new PermissionVO();
						per.setApplicationid(vo.getApplicationid());
						per.setId(Sequence.getSequence());
						per.setResourceId(res.getId());
						coll.add(per);
					} else {
						throw new Exception(id + "不存在或权限与角色不在同一应用！");
					}
				}
			}
			vo.setPermission(coll);
			process.doUpdate(vo);
		} catch (Exception e) {
			throw new RoleServiceFault(e.getMessage());
		}
	}

}
