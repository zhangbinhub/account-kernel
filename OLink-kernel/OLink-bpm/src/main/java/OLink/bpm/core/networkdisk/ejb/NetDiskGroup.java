package OLink.bpm.core.networkdisk.ejb;

import OLink.bpm.base.dao.ValueObject;

public class NetDiskGroup extends ValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4131419150940327146L;
	private String id;//编号
	private String name;//名称
	private String userid;//用户编号
	private String useridGroup;//用户组  将需要的用户添加到组中
	private String description;//对组描述
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getUseridGroup() {
		return useridGroup;
	}
	public void setUseridGroup(String useridGroup) {
		this.useridGroup = useridGroup;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
