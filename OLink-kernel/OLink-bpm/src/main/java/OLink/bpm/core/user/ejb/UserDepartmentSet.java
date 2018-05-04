package OLink.bpm.core.user.ejb;

import java.io.Serializable;

/**
 * 用户部门中间对象
 * 
 * @hibernate.class table="T_USER_DEPARTMENT_SET" batch-size="10" lazy="false"
 */
public class UserDepartmentSet implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2833496521401389668L;

	private String id;

	private String userId;

	private String departmentId;

	public UserDepartmentSet() {

	}

	public UserDepartmentSet(String userId, String departmentId) {
		this.userId = userId;
		this.departmentId = departmentId;
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

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}
}
