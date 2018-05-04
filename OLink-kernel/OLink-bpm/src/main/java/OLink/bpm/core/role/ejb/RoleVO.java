
package OLink.bpm.core.role.ejb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.user.ejb.UserProcess;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.permission.ejb.PermissionVO;

/**
 * @hibernate.class table="T_ROLE" batch-size="10" lazy="false"
 */
public class RoleVO extends ValueObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6725803831825481841L;
	
//	public static final String WORK_MANAGER_FALSE = "false";
//	public static final String WORK_MANAGER_TRUE = "true";

	/**
	 * 主键
	 */
	private String id;

	/**
	 * 权限组名称
	 */
	private String name;

	/**
	 * 角色编号
	 */
	private String roleNo;
	/**
	 * 英文名
	 */
	private String engname;
	/**
	 * 菜单关联权限
	 */
	private Collection<PermissionVO> permission;

	/**
	 * 用户组
	 */
	private Collection<UserVO> users;
	
	private String applicationid;//角色所属应用 
	
//	private String isWorkManager; //是否为流程监控管理员 

	/**
	 * 英文名
	 * 
	 * @return 部门(java.lang.String)
	 * @hibernate.property column="ENGNAME"
	 * @roseuid 44C8C25D0057
	 */
	public String getEngname() {
		return engname;
	}

	/**
	 * 角色标识
	 * 
	 * @return 角色标识(java.lang.String)
	 * @hibernate.id column="ID" generator-class = "assigned"
	 * @roseuid 44C7A18C03A5
	 */
	public String getId() {
		return id;
	}

	/**
	 * 角色名
	 * 
	 * @return 角色名(java.lang.String)
	 * @hibernate.property column="NAME"
	 * @roseuid 44C8C25D035A
	 */
	public String getName() {
		return name;
	}

	/**
	 * 角色编号
	 * @return 角色编号(java.lang.String)
	 * @hibernate.property colum="ROLENO"
	 */
	public String getRoleNo() {
		return roleNo;
	}

	/**
	 * 获取当前角色下的所有用户
	 * 
	 * @return java.util.Collection
	 * @hibernate.set name="users" table="T_USER_ROLE_SET" inverse="true"
	 *                lazy="true"
	 * @hibernate.collection-key column = "ROLEID"
	 * @hibernate.collection-many-to-many class="UserVO"
	 *                                    column="USERID"
	 * @roseuid 44C8C2600029
	 */
	public Collection<UserVO> getUsers() {
		try {
			if (users == null) {
				UserProcess userProcess = (UserProcess) ProcessFactory.createProcess(UserProcess.class);
				users = userProcess.queryByRole(this.getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return users;
	}

	/**
	 * 
	 * 获取所属角色下的菜单关联权限
	 * 
	 * @hibernate.set name="permission" table="T_PERMISSION" cascade="all"
	 *                lazy="false"
	 * @hibernate.collection-key column="ROLE_ID"
	 * @hibernate.collection-one-to-many class="PermissionVO"
	 * @return 所属角色下的菜单关联权限
	 */
	public Collection<PermissionVO> getPermission() {
		if (permission == null)
			permission = new HashSet<PermissionVO>();
		return permission;
	}

	/**
	 * 
	 * 设置菜单关联权限
	 * 
	 * @param permission
	 */
	public void setPermission(Collection<PermissionVO> permission) {
		this.permission = permission;
	}

	/**
	 * 设置英文名
	 * 
	 * @param engname
	 * @roseuid 44C8C25D018D
	 */
	public void setEngname(String engname) {
		this.engname = engname;
	}

	/**
	 * 设置角色标识
	 * 
	 * @param id
	 *            角色标识
	 * @roseuid 44C7A18C03A6
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 设置角色名
	 * 
	 * @param name
	 *            角色名
	 * @roseuid 44C8C25E009E
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 设置角色编号
	 * @param roleNo
	 *              角色编号
	 */
	public void setRoleNo(String roleNo) {
		this.roleNo = roleNo;
	}

	/**
	 * 设置用户为当前角色的下级
	 * 
	 * @param users
	 *            用户集合
	 * @roseuid 44C8C2600192
	 */
	public void setUsers(Collection<UserVO> users) {
		this.users = users;
	}
	
/*
	public String getIsWorkManager() {
		
		if(isWorkManager ==null || isWorkManager.trim().length()<=0){
			return WORK_MANAGER_FALSE;
		}
		return isWorkManager;
	}

	public void setIsWorkManager(String isWorkManager) {
		this.isWorkManager = isWorkManager;
	}
*/
	/**
	 * 获得User的对象
	 * 
	 * @param domainId
	 *            域标识
	 * @return 用户集合
	 */
	public Collection<UserVO> getUsersByDomain(String domainId) {
		Collection<UserVO> rtn = new ArrayList<UserVO>();
		for (Iterator<UserVO> iterator = getUsers().iterator(); iterator.hasNext();) {
			UserVO userVO = iterator.next();
			if (userVO.getDomainid().equals(domainId)) {
				rtn.add(userVO);
			}
		}
		return rtn;
	}

	/**
	 * 获得角色所属应用的id
	 *@author Bluce
	 * @return 应用 application 的 id
	 * @date 2010-05-10
	 */
	public String getApplicationid() {
		return applicationid;
	}

	/**
	 * 设置角色所属应用的id
	 * @author Bluce
	 * @param 应用applicationid
	 */
	public void setApplicationid(String applicationid) {
		this.applicationid = applicationid;
	}

}
/**
 * 
 * void RoleVO.setName(java.lang.String){ this.name = name; }
 * 
 * 
 * void RoleVO.getName(){ return name; }
 * 
 * 
 * void RoleVO.setDepartment(DepartmentVO){
 * this.department = department; }
 * 
 * 
 * void RoleVO.getDepartment(){ return department; }
 * 
 * 
 * void RoleVO.setEngname(java.lang.String){ this.engname = engname; }
 * 
 * 
 * void RoleVO.getResources(){ return resources; }
 * 
 * 
 * void RoleVO.setUsers(java.util.Collection){ this.users = users; }
 * 
 * 
 * 
 * void RoleVO.getUsers(){ return users; }
 * 
 * 
 * void RoleVO.getEngname(){ return engname; }
 * 
 * 
 * 
 * 
 * void RoleVO.setResources(java.util.Collection){ this.resources = resources; }
 * 
 */
