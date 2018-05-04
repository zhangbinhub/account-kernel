package OLink.bpm.core.networkdisk.ejb;

import OLink.bpm.base.dao.ValueObject;

public class NetDisk extends ValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4362884515151014951L;
	private String id;//编号
	private long totalSize;//总容量  10：为默认总容量M
	private long uploadSize;//单个上传文件大小  100：为默认上传大小kb
	private long haveUseSize;//已使用容量
	private String pemission;//是否允许用户使用网盘  true：为默认允许，false：为禁止
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public long getTotalSize() {
		return totalSize;
	}
	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}
	public long getUploadSize() {
		return uploadSize;
	}
	public void setUploadSize(long uploadSize) {
		this.uploadSize = uploadSize;
	}
	public long getHaveUseSize() {
		return haveUseSize;
	}
	public void setHaveUseSize(long haveUseSize) {
		this.haveUseSize = haveUseSize;
	}
	public String getPemission() {
		return pemission;
	}
	public void setPemission(String pemission) {
		this.pemission = pemission;
	}
	
	
	
	
	
}
