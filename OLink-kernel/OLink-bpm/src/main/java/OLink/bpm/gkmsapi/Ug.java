package OLink.bpm.gkmsapi;

import OLink.bpm.core.department.ejb.DepartmentVO;
import OLink.bpm.core.domain.ejb.DomainVO;

/**
 *组织结构类
 *为组织结构管理提供组织结构
 */
public class Ug {
	private String code;
	private String name;
	private String parentCode;
	private boolean sign;
	private String location;
	private String email;
	private String remark;
	
	public Ug(){
		super();
	}

	/**
	 * 构造方法，根据组织编号，组织名称，父组织编号构造组织对象, 默认添加的是部门，不是单位
	 * @param code       组织编号
	 * @param name       组织名称
	 * @param parentCode 父组织编号
	 */
	public Ug(String code, String name, String parentCode) {
		super();
		this.code = code;
		this.name = name;
		this.parentCode = parentCode;
		this.sign = true;
	}

	/**
	 * 构造方法，根据组织编号，组织名称，父组织编号构造组织对象
	 * @param code       组织编号
	 * @param name       组织名称
	 * @param parentCode 父组织编号
	 * @param sign		 组织标示 部门=true|单位=false
	 */
	public Ug(String code, String name, String parentCode, boolean sign) {
		super();
		this.code = code;
		this.name = name;
		this.parentCode = parentCode;
		this.sign = sign;
	}

	/**
	 * 构造方法，根据组织编号，组织名称，父组织编号，组织标识，兄弟组织的组织编号，组织邮件地址，备注构造组织对象
	 * @param code       组织编号
	 * @param name       组织名称
	 * @param parentCode 父组织编号
	 * @param sign       组织标识
	 * @param location   兄弟组织的组织编号
	 * @param email      组织邮件地址
	 * @param remark     备注
	 */
	public Ug(String code, String name, String parentCode, boolean sign,
			String location, String email, String remark) {
		super();
		this.code = code;
		this.name = name;
		this.parentCode = parentCode;
		this.sign = sign;
		this.location = location;
		this.email = email;
		this.remark = remark;
	}

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getParentCode() {
		return parentCode;
	}
	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}
	public boolean isSign() {
		return sign;
	}
	public void setSign(boolean sign) {
		this.sign = sign;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	/**
	 * 域转换为GKE单位
	 * @param domianvo
	 */
	public void domainToUg(DomainVO domainvo){
		code = GKUtil.getUUID(domainvo.getId());
		name = domainvo.getName();
		parentCode = "0";
		sign = false;
		remark = domainvo.getDescription();
	}
	
	/**
	 * 部门转换为GKE部门
	 * @param departmentvo
	 */
	public void DepartmentToUg(DepartmentVO departmentvo, String domainID){
		code = GKUtil.getUUID(departmentvo.getId());
		name = departmentvo.getName();
		sign = true;
		parentCode = departmentvo.getSuperior()!=null?GKUtil.getUUID(departmentvo.getSuperior().getId()):GKUtil.getUUID(domainID);
		location = departmentvo.getSortId();
	}

	/**
	 *将Ug的信息转换成字符串
	 *@return String类型XML，包括Ug对象的所有信息
	 */
	public String toXml(){
		StringBuffer str=new StringBuffer(100);
		str.append("<ug code=\"");
		str.append(GKUtil.codeXml(code));
		str.append("\" name=\"");
		str.append(GKUtil.codeXml(name));
		str.append("\" parent_code=\"");
		str.append(GKUtil.codeXml(parentCode));
		str.append("\" sign=\"");
		if(sign){
			str.append("1");
		}else{
			str.append("0");
		}
		if(location!=null)
			str.append("\" location=\""+GKUtil.codeXml(location));
		if(email!=null)
			str.append("\" email=\""+GKUtil.codeXml(email));
		if(remark!=null)
			str.append("\" remark=\""+GKUtil.codeXml(remark));
		str.append("\" />");
		return str.toString();
	}

	public String toString(){
		return toXml();
	}
}
