package OLink.bpm.core.privilege.operation.ejb;

import java.io.Serializable;

import OLink.bpm.base.dao.ValueObject;

public class OperationVO extends ValueObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7190541570406769190L;

	/**
	 * 所有操作
	 */
	public static final int ALL = 1000;
	/**
	 * 菜单类型为可视
	 */
	public static final int MENU_VISIABLE = 1001;
	/**
	 * 菜单类型为不可视
	 */
	public static final int MENU_INVISIBLE = 1002;

	/**
	 * 表单字段类型为只读(READONLY)
	 */
	public static final int FORMFIELD_READONLY = 1011;
	/**
	 * 表单字段类型为修改(MODIFY)
	 */
	public static final int FORMFIELD_MODIFY = 1012;
	/**
	 * 表单字段类型为隐藏(HIDDEN)
	 */
	public static final int FORMFIELD_HIDDEN = 1013;
	/**
	 * 表单字段类型为屏蔽(DISABLED)
	 */
	public static final int FORMFIELD_DISABLED = 1014;

	/**
	 * 文件夹类型为创建
	 */
	public static final int FOLDER_CREATE = 1021;
	/**
	 * 文件夹类型为重命名
	 */
	public static final int FOLDER_RENAME = 1022;
	/**
	 * 文件夹类型为删除
	 */
	public static final int FOLDER_DELETE = 1023;

	/**
	 * 文件类型为查看
	 */
	public static final int FILE_REVIEW = 1024;
	/**
	 * 文件类型为编辑
	 */
	public static final int FILE_EDIT = 1025;
	/**
	 * 文件类型为删除
	 */
	public static final int FILE_DELETE = 1026;
	/**
	 * 文件类型为下载
	 */
	public static final int FILE_DOWN = 1027;
	/**
	 * 文件类型为移动
	 */
	public static final int FILE_REMOVE = 1028;
	/**
	 * 文件类型为复制
	 */
	public static final int FILE_COPY = 1029;
	/**
	 * 文件类型为上传
	 */
	public static final int FILE_UPLOAD = 1030;
	/**
	 * 文件类型为添加选择文件
	 */
	public static final int FILE_ADD_SELETE_FILE = 1031;

	/**
	 * 主键
	 * 
	 * @uml.property name="id"
	 */
	private String id;
	/**
	 * 名称
	 * 
	 * @uml.property name="name"
	 */
	private String name;

	/**
	 * 资源类型
	 */
	private Integer resType;

	private Integer code;

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

	public Integer getResType() {
		return resType;
	}

	public void setResType(Integer resType) {
		this.resType = resType;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}
}
