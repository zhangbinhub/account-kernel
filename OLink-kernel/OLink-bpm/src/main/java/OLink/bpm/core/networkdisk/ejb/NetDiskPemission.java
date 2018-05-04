package OLink.bpm.core.networkdisk.ejb;

import OLink.bpm.core.role.ejb.RoleVO;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.department.ejb.DepartmentVO;

public class NetDiskPemission extends ValueObject {

	private static final long serialVersionUID = 4362884516151013951L;
	private String id;//编号
	private String userid;//用户编号
	private String name;//名称
	private String type;//类型file和folder
	private String selectObject;//选择对象用户、部门、角色、组
	private NetDiskGroup group;
	private DepartmentVO department;
	private RoleVO role;
	private String users;
	private String operate;//权限操作  查看，下载
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getOperate() {
		return operate;
	}
	public void setOperate(String operate) {
		this.operate = operate;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSelectObject() {
		return selectObject;
	}
	public void setSelectObject(String selectObject) {
		this.selectObject = selectObject;
	}
	
	public NetDiskGroup getGroup() {
		return group;
	}
	public void setGroup(NetDiskGroup group) {
		this.group = group;
	}
	public DepartmentVO getDepartment() {
		return department;
	}
	public void setDepartment(DepartmentVO department) {
		this.department = department;
	}
	public RoleVO getRole() {
		return role;
	}
	public void setRole(RoleVO role) {
		this.role = role;
	}
	public String getUsers() {
		return users;
	}
	public void setUsers(String users) {
		this.users = users;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
