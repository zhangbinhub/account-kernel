package OLink.bpm.core.user.ejb;

import java.io.Serializable;

/**
 * 用户部门中间对象
 * 
 * @hibernate.class table="T_USER_DEPARTMENT_SET" batch-size="10" lazy="false"
 */
public class UserRoleSet implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2833496521401389668L;

	private String id;

	private String userId;

	private String roleId;

	public UserRoleSet() {

	}

	public UserRoleSet(String userId, String roleId) {
		this.userId = userId;
		this.roleId = roleId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
}
