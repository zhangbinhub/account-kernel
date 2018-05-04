package OLink.bpm.core.user.ejb;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import OLink.bpm.core.department.ejb.DepartmentVO;
import OLink.bpm.core.role.ejb.RoleProcess;
import OLink.bpm.core.role.ejb.RoleVO;
import OLink.bpm.core.department.ejb.DepartmentProcess;
import OLink.bpm.core.usersetup.ejb.UserSetupVO;
import org.apache.commons.beanutils.PropertyUtils;

import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.json.JsonUtil;

/**
 * @hibernate.class table="T_USER" batch-size="10" lazy="false"
 */
public class UserVO extends BaseUser {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8254654111907418034L;

	/**
	 * 等级
	 */
	private int level; // 

	/**
	 * 所属权限组
	 */
	private Collection<RoleVO> roles;

	/**
	 * 备注
	 */
	private String Remarks;

	/**
	 * 所属部门
	 */
	private Collection<DepartmentVO> departments;

	private Collection<UserDepartmentSet> userDepartmentSets;

	private Collection<UserRoleSet> userRoleSets;

	/* 用户设置 */

	private UserSetupVO userSetup;
	
	/* 域管理用户 */
	private String domainUser;
	
	/**
	 * 使用即时通信
	 */
	private Boolean useIM;

	public Boolean getUseIM() {
		if (useIM == null) {
			return false;
		}
		
		return useIM;
	}

	public void setUseIM(Boolean useIM) {
		this.useIM = useIM;
	}

	public String getDomainUser() {
		return domainUser;
	}

	public void setDomainUser(String domainUser) {
		this.domainUser = domainUser;
	}

	/**
	 * 代理用户
	 */
	protected UserVO proxyUser;

	private String field1;// 扩展字段
	private String field2;// 扩展字段
	private String field3;// 扩展字段
	private String field4;// 扩展字段
	private String field5;// 扩展字段
	private String field6;// 扩展字段
	private String field7;// 扩展字段
	private String field8;// 扩展字段
	private String field9;// 扩展字段
	private String field10;// 扩展字段
	private List<String> fieldExtendsValues = new ArrayList<String>();// 要显示在列表的扩展字段值的集合,这个值不映射到Hibernate

	public Collection<UserDepartmentSet> getUserDepartmentSets() {
		if (userDepartmentSets == null) {
			userDepartmentSets = new HashSet<UserDepartmentSet>();
		}

		return userDepartmentSets;
	}

	public void setUserDepartmentSets(
			Collection<UserDepartmentSet> userDepartmentSets) {
		this.userDepartmentSets = userDepartmentSets;
	}

	/**
	 * 获取代理用户
	 * 
	 * @return 用户对象
	 * @hibernate.many-to-one class="UserVO"
	 *                        column="PROXYUSER"
	 */
	public UserVO getProxyUser() {
		return proxyUser;
	}

	/**
	 * 设置代理用户
	 * 
	 * @param proxyUser
	 */
	public void setProxyUser(UserVO proxyUser) {
		this.proxyUser = proxyUser;
	}

	/**
	 * 获得用户备注信息
	 */
	public String getRemarks() {
		return Remarks;
	}

	/**
	 * 设置用户备注信息
	 */
	public void setRemarks(String remarks) {
		Remarks = remarks;
	}
	
	/**
	 * 获取当前用户的所有上级
	 * @return 当前用户的所有上级集合
	 */
	public Collection<UserVO> getSuperiorList(){
		Collection<UserVO> superiors = new ArrayList<UserVO>();
		UserVO user = this;
		while(true){
			UserVO superior = user.getSuperior();
			if(superior != null){
				superiors.add(superior);
				user = superior;
			} else {
				break;
			}
		}
		return superiors;
	}

	/**
	 * 获取用户所在部门
	 * 
	 * @hibernate.set name="departments" lazy="false"
	 *                table="T_USER_DEPARTMENT_SET" cascade="save-update"
	 * @hibernate.collection-key column="USERID"
	 * @hibernate.collection-many-to-many 
	 *                                    class="DepartmentVO"
	 *                                    column="DEPARTMENTID"
	 * @return 部门集合.
	 * @roseuid 44C7A196022D
	 */
	public Collection<DepartmentVO> getDepartments() {
		try {
			if (departments == null || departments.isEmpty()) {
				DepartmentProcess departProcess = (DepartmentProcess) ProcessFactory
						.createProcess(DepartmentProcess.class);
				departments = departProcess.queryByUser(this.getId());
				if (departments == null) {
					departments = new ArrayList<DepartmentVO>();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return departments;
	}

	/**
	 * 设置用户所在部门
	 * 
	 * @param deptments
	 *            The deptments to set.
	 * @param deptments
	 * @roseuid 44C7A1960241
	 */
	public void setDepartments(Collection<DepartmentVO> deptments) {
		this.departments = deptments;
	}

	/**
	 * 获取用户所在角色
	 * 
	 * @hibernate.set name="roles" table="T_USER_ROLE_SET" lazy="false"
	 *                cascade="save-update"
	 * @hibernate.collection-key column="USERID"
	 * @hibernate.collection-many-to-many class="RoleVO"
	 *                                    column="ROLEID"
	 * @return 角色集合.
	 */
	public Collection<RoleVO> getRoles() {
		try {
			if (roles == null || roles.isEmpty()) {
				RoleProcess roleProcess = (RoleProcess) ProcessFactory
						.createProcess(RoleProcess.class);
				roles = roleProcess.queryByUser(this.getId());
				if (roles == null) {
					roles = new ArrayList<RoleVO>();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return roles;
	}

	/**
	 * 设置用户所属角色
	 * 
	 * @param roles
	 *            The roles to set.
	 * @param roles
	 * @roseuid 44C7A19602AF
	 */
	public void setRoles(Collection<RoleVO> roles) {
		this.roles = roles;
	}

	/**
	 * 克隆用户对象
	 */
	public Object clone() {

		UserVO userVO = new UserVO();
		try {
			super.clone();
			PropertyUtils.copyProperties(userVO, this);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return userVO;
	}

	/**
	 * 获取用户级别
	 * 
	 * @hibernate.property column="LEVELS"
	 * @return
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * 设置用户级别
	 * 
	 * @param level
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * 获取上级用户
	 * 
	 * @return UserVO
	 * @hibernate.many-to-one class="UserVO"
	 *                        column="SUPERIOR"
	 */
	public UserVO getSuperior() {
		return superior;
	}

	/**
	 * 设置上级用户
	 */
	public void setSuperior(UserVO superior) {
		this.superior = superior;
	}

	public Collection<UserRoleSet> getUserRoleSets() {
		if (userRoleSets == null) {
			userRoleSets = new HashSet<UserRoleSet>();
		}

		return userRoleSets;
	}

	public void setUserRoleSets(Collection<UserRoleSet> userRoleSets) {
		this.userRoleSets = userRoleSets;
	}

	public String getField1() {
		return field1;
	}

	public void setField1(String field1) {
		if ("".equals(field1))
			field1 = null;
		this.field1 = field1;
	}

	public String getField2() {
		return field2;
	}

	public void setField2(String field2) {
		if ("".equals(field2))
			field2 = null;
		this.field2 = field2;
	}

	public String getField3() {
		return field3;
	}

	public void setField3(String field3) {
		if ("".equals(field3))
			field3 = null;
		this.field3 = field3;
	}

	public String getField4() {
		return field4;
	}

	public void setField4(String field4) {
		if ("".equals(field4))
			field4 = null;
		this.field4 = field4;
	}

	public String getField5() {
		return field5;
	}

	public void setField5(String field5) {
		if ("".equals(field5))
			field5 = null;
		this.field5 = field5;
	}

	public String getField6() {
		return field6;
	}

	public void setField6(String field6) {
		if ("".equals(field6))
			field6 = null;
		this.field6 = field6;
	}

	public String getField7() {
		return field7;
	}

	public void setField7(String field7) {
		if ("".equals(field7))
			field7 = null;
		this.field7 = field7;
	}

	public String getField8() {
		return field8;
	}

	public void setField8(String field8) {
		if ("".equals(field8))
			field8 = null;
		this.field8 = field8;
	}

	public String getField9() {
		return field9;
	}

	public void setField9(String field9) {
		if ("".equals(field9))
			field9 = null;
		this.field9 = field9;
	}

	public String getField10() {
		return field10;
	}

	public void setField10(String field10) {
		if ("".equals(field10))
			field10 = null;
		this.field10 = field10;
	}

	public List<String> getFieldExtendsValues() {
		return fieldExtendsValues;
	}

	public void setFieldExtendsValues(List<String> fieldExtendsValues) {
		this.fieldExtendsValues = fieldExtendsValues;
	}

	public String toJSON() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("id", getId());
		map.put("name", getName());
		map.put("domainid", getDomainid());

		return JsonUtil.toJson(map);
	}

	public UserSetupVO getUserSetup() {
		return userSetup;
	}

	public void setUserSetup(UserSetupVO userSetup) {
		this.userSetup = userSetup;
	}

	@Override
	public boolean equals(Object object) {
		return super.equals(object);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

}
