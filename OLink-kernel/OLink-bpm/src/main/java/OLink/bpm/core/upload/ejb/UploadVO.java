package OLink.bpm.core.upload.ejb;

import java.io.InputStream;
import java.io.Serializable;

import OLink.bpm.base.dao.ValueObject;

/**
 * 上传文件信息或上传到数据库
 * @author Administrator
 *
 */
public class UploadVO  extends ValueObject implements Serializable{
	
	private static final long serialVersionUID = 7242896187575021920L;

	private String id;//主键
	
	private String name;//名称
	
	private transient InputStream imgBinary;//图片二进制码
	
	private String fieldid;//表单字段
	
	private String type;//文件类型

	private long size;//文件大小
	
	private String userid;//用户编号
	
	private String modifyDate;//修改日期
	
	private String path;//文件路径
	
	private String folderPath;//文件夹路径

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

	public InputStream getImgBinary() {
		return imgBinary;
	}

	public void setImgBinary(InputStream imgBinary) {
		this.imgBinary = imgBinary;
	}

	public String getFieldid() {
		return fieldid;
	}

	public void setFieldid(String fieldid) {
		this.fieldid = fieldid;
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

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(String modifyDate) {
		this.modifyDate = modifyDate;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFolderPath() {
		return folderPath;
	}

	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
