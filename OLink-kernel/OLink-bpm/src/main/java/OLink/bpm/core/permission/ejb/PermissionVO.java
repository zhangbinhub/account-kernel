package OLink.bpm.core.permission.ejb;

import java.io.Serializable;

import OLink.bpm.base.dao.ValueObject;

/**
 * 
 * @hibernate.class table="T_PERMISSION" batch-size="10" lazy="true"
 */
public class PermissionVO extends ValueObject implements Serializable {
	public static int TYPE_ALLOW = 1; // 允许

	public static int TYPE_FORBID = 2; // 禁止

	private static final long serialVersionUID = -7243699709991832076L;

	private String id;
	
	/**
	 * 角色ID
	 */
	private String roleId;

	/**
	 * 菜单ID
	 */
	private String resourceId;

	private String resId;// 资源ID

	private String resName;// 资源名称

	private Integer resType;// 资源类型

	private String operationId;// 操作ID

	private Integer operationCode;// 操作编码

	private Integer type; // 允许或禁止
	
	public Integer getResType() {
		return resType;
	}

	public void setResType(Integer resType) {
		this.resType = resType;
	}

	public Integer getType() {
		if (type == null) {
			return Integer.valueOf(0);
		}

		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	private String sortId;

	private String applicationid;

	public String getSortId() {
		return sortId;
	}

	public void setSortId(String sortId) {
		this.sortId = sortId;
	}

	public String getApplicationid() {
		return applicationid;
	}

	public void setApplicationid(String applicationid) {
		this.applicationid = applicationid;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getResName() {
		return resName;
	}

	public void setResName(String resName) {
		this.resName = resName;
	}

	public Integer getOperationCode() {
		if (operationCode == null) {
			return Integer.valueOf(Integer.MAX_VALUE);
		}

		return operationCode;
	}

	public void setOperationCode(Integer operationCode) {
		this.operationCode = operationCode;
	}

	public String getOperationId() {
		return operationId;
	}

	public void setOperationId(String operationId) {
		this.operationId = operationId;
	}

	public String getResId() {
		return resId;
	}

	public void setResId(String resId) {
		this.resId = resId;
	}

	/**
	 * 获取角色ID
	 * @return
	 */
	public String getRoleId() {
		return roleId;
	}

	/**
	 * 设置角色ID
	 * @param roleId
	 */
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	/**
	 * 获取菜单ID
	 * @return
	 */
	public String getResourceId() {
		return resourceId;
	}

	/**
	 * 设置菜单ID
	 * @param resourceId
	 */
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	
	
}
