package OLink.bpm.core.networkdisk.ejb;

import OLink.bpm.base.dao.ValueObject;

public class NetDiskFolder extends ValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3475479608614842472L;
	private String id;//编号
	private String userid;//用户编号
	private String folderPath;//文件夹
	private String shareTime;//共享日期
	private NetDiskPemission pemission;//权限
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
	public String getFolderPath() {
		return folderPath;
	}
	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}
	public String getShareTime() {
		return shareTime;
	}
	public void setShareTime(String shareTime) {
		this.shareTime = shareTime;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public NetDiskPemission getPemission() {
		return pemission;
	}
	public void setPemission(NetDiskPemission pemission) {
		this.pemission = pemission;
	}
}
