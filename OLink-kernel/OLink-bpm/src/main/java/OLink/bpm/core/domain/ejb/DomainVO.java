package OLink.bpm.core.domain.ejb;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.domain.level.ejb.DomainLevelVO;
import OLink.bpm.core.workcalendar.calendar.action.CalendarType;
import OLink.bpm.core.workcalendar.calendar.ejb.CalendarProcess;
import OLink.bpm.core.department.ejb.DepartmentVO;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.core.superuser.ejb.SuperUserVO;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.core.workcalendar.calendar.ejb.CalendarVO;
import OLink.bpm.util.ProcessFactory;

/**
 * 
 * @hibernate.class table="T_DOMAIN" batch-size="10" lazy="false"
 */
public class DomainVO extends ValueObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4259448158835058227L;
	
	/**
	 * 非激活
	 */
	public static final int STATE_INACTIVE = 0;
	/**
	 * 激活
	 */
	public static final int STATE_ACTIVATED = 1;

	/**
	 * 域主键
	 * 
	 * @uml.property name="id"
	 */
	private String id;

	/**
	 * 域名称
	 * 
	 * @uml.property name="name"
	 */
	private String name;

	/**
	 * 部门(department)集合
	 * 
	 * @uml.property name="roles"
	 */
	private Collection<DepartmentVO> departments;
	/**
	 * 应用(Application)集合
	 */
	private Collection<ApplicationVO> applications;
	/**
	 * 域管理员(SuperUserVO)集合
	 */
	private Collection<SuperUserVO> users;
	
	/**
	 * 皮肤(Skin)类型
	 */
	private String skinType;

	/**
	 * 1:effective ;0:invalid
	 */
	private int status = 1;
	/**
	 * 默认工作日历种类
	 */
	private String defaultCalendar;
	/**
	 * 域等级:普通域,升级域,高级域,VIP域
	 */
	private DomainLevelVO level;
	/**
	 * 描述
	 * 
	 * @uml.property name="description"
	 */
	private String description;
	
	/**短信平台会员编码*/
	private String smsMemberCode;
	/**短信平台会员密码*/
	private String smsMemberPwd;
	
	private Boolean log;
	

	/**
	 * 获取域标识
	 * 
	 * @return java.lang.String
	 * @hibernate.id column="ID" generator-class="assigned"
	 * @roseuid 44C5FCE0027C
	 * @uml.property name="id"
	 */
	public String getId() {
		return id;
	}

	/**
	 * 获取域名
	 * 
	 * @return 域名<java.lang.String>
	 * @hibernate.property column="NAME"
	 * @roseuid 44C5FCE002C2
	 * @uml.property name="name"
	 */
	public String getName() {
		if (name != null)
			this.name = name.toLowerCase();
		return name;
	}

	/**
	 * 所属域的部门集合
	 * 
	 * @return 所属域的部门集合<java.util.Collection>
	 * @hibernate.set cascade = "none" order-by = "ID" name="departments"
	 *                table="T_DEPARTMENT"
	 * @hibernate.collection-one-to-many class="DepartmentVO"
	 * @hibernate.collection-key column = "DOMAIN_ID"
	 * @roseuid 44C5FCE10007
	 * @uml.property name="departments"
	 */
	public Collection<DepartmentVO> getDepartments() {
		if (departments == null) {
			return new HashSet<DepartmentVO>();
		}
		return departments;
	}
	
	/**
	 * 所属域的前台用户集合
	 * 
	 * @return 所属域的部门集合<java.util.Collection>
	 * @hibernate.set cascade = "none" order-by = "ID" name="departments"
	 *                table="T_DEPARTMENT"
	 * @hibernate.collection-one-to-many class="DepartmentVO"
	 * @hibernate.collection-key column = "DOMAIN_ID"
	 * @roseuid 44C5FCE10007
	 * @uml.property name="departments"
	 */
	public Collection<UserVO> getUserVOs() {
		Collection<UserVO> users = new HashSet<UserVO>();
		if(departments!=null){
			for (Iterator<DepartmentVO> iterator = departments.iterator(); iterator.hasNext();) {
				DepartmentVO departmentVO = iterator.next();
				users.addAll(departmentVO.getUsers());
			}
		}
		return users;
	}

	/**
	 * 获取域用户
	 * 
	 * @return 域用户集合<java.util.Collection>
	 */
	public Collection<SuperUserVO> getUsers() {
		if (users == null)
			users = new HashSet<SuperUserVO>();
		return users;
	}

	/**
	 * 设置标识
	 * 
	 * @param aId
	 * @roseuid 44C5FCE00290
	 * @uml.property name="id"
	 */
	public void setId(String aId) {
		id = aId;
	}

	/**
	 * 设置域名
	 * 
	 * @param aName
	 * @roseuid 44C5FCE002D6
	 * @uml.property name="name"
	 */
	public void setName(String aName) {
		name = aName;
		if (name != null)
			this.name = name.toLowerCase();
	}

	/**
	 * 设置域部门
	 * 
	 * @param aDepartments
	 *            部门集合
	 * @roseuid 44C5FCE10025
	 * @uml.property name="departments"
	 */
	public void setDepartments(Collection<DepartmentVO> aDepartments) {
		departments = aDepartments;
	}

	/**
	 * 设置用户到域用户
	 * 
	 * @param aUsers
	 *            用户对象
	 * @roseuid 44C5FCE1007F
	 * @uml.property name="users"
	 */
	public void setUsers(Collection<SuperUserVO> aUsers) {
		users = aUsers;
	}

	/**
	 * 获取域的描述
	 * 
	 * @return 域的描述
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 设置域的描述
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 获取所属域下的应用集合
	 * 
	 * @return 应用的集合
	 */
	public Collection<ApplicationVO> getApplications() {
		if (applications == null)
			applications = new HashSet<ApplicationVO>();
		return applications;
	}

	/**
	 * 设置应用为当前域管理
	 * 
	 * @param applications
	 */
	public void setApplications(Collection<ApplicationVO> applications) {
		this.applications = applications;
	}

	/**
	 * 获取第一个软件
	 * 
	 * @return 软件对象
	 */
	public ApplicationVO getFirstApplication() {
		if (getApplications() != null && !getApplications().isEmpty()) {
			return getApplications().iterator().next();
		}

		return null;

	}

	/**
	 * 域状态(激活,未激活)
	 * 
	 * @hibernate column="STATUS"
	 * @return
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * 设置域状态
	 * 
	 * @param status
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * 域级别(普通域,升级域,高级域,VIP域)
	 * 
	 * @hibernate column="LEVEL"
	 * @return 域级别对象
	 */
	public DomainLevelVO getLevel() {
		return level;
	}

	/**
	 * 设置域的级别
	 * 
	 * @param level
	 */
	public void setLevel(DomainLevelVO level) {
		this.level = level;
	}

	/**
	 * 获取域默认的工作日历种类
	 * 
	 * @return (java.lang.String) 日历标识
	 */
	public String getDefaultCalendar() {
		if (this.defaultCalendar == null) {
			try {
				CalendarVO calendar = (CalendarVO) ProcessFactory
						.createProcess(CalendarProcess.class).doViewByName(CalendarType.getName(1), getId());
				defaultCalendar = calendar.getId();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return defaultCalendar;
	}

	/**
	 * 设置工作日历种类
	 * 
	 * @param defaultCalendar
	 */
	public void setDefaultCalendar(String defaultCalendar) {
		this.defaultCalendar = defaultCalendar;
	}

	/**
	 * 设置皮肤类型
	 * 
	 * @param skinType
	 * @uml.property name="skintype"
	 */
	public void setSkinType(String skinType) {
		this.skinType = skinType;
	}

	/**
	 * 获取皮肤类型
	 * @return
	 */
	public String getSkinType() {
		return skinType;
	}

	/**获取短信平台会员编码*/
	public String getSmsMemberCode() {
		return smsMemberCode;
	}

	/**设置短信平台会员编码*/
	public void setSmsMemberCode(String smsMemberCode) {
		this.smsMemberCode = smsMemberCode;
	}

	/**获取短信平台会员密码*/
	public String getSmsMemberPwd() {
		return smsMemberPwd;
	}

	/**设置短信平台会员密码*/
	public void setSmsMemberPwd(String smsMemberPwd) {
		this.smsMemberPwd = smsMemberPwd;
	}

	/**
	 * @return the log
	 */
	public Boolean getLog() {
		if (log == null) {
			log = Boolean.valueOf(false);
		}
		return log;
	}

	/**
	 * @param log the log to set
	 */
	public void setLog(Boolean log) {
		this.log = log;
	}

}
