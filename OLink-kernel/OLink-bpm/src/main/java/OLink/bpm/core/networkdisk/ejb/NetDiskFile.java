package OLink.bpm.core.networkdisk.ejb;

import OLink.bpm.base.dao.ValueObject;

public class NetDiskFile extends ValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7722958762580172908L;
	private String id;//编号
	private String name;//文件名称
	private String type;//文件类型
	private long size;//文件大小b
	private String modifyTime;//最后修改日期
	private String shareTime;//共享日期
	private String userid;//用户编号
	private String folderPath;//文件夹
	private String folderWebPath;//文件网络路径
	private NetDiskPemission pemission;//权限
	
	
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public String getModifyTime() {
		return modifyTime;
	}
	public void setModifyTime(String modifyTime) {
		this.modifyTime = modifyTime;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	
	public String getFolderPath() {
		return folderPath;
	}
	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}
	
	public NetDiskPemission getPemission() {
		return pemission;
	}
	public void setPemission(NetDiskPemission pemission) {
		this.pemission = pemission;
	}
	public String getFolderWebPath() {
		return folderWebPath;
	}
	public void setFolderWebPath(String folderWebPath) {
		this.folderWebPath = folderWebPath;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getShareTime() {
		return shareTime;
	}
	public void setShareTime(String shareTime) {
		this.shareTime = shareTime;
	}
}
