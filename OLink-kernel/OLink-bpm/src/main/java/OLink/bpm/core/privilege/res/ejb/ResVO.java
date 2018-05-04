package OLink.bpm.core.privilege.res.ejb;

import OLink.bpm.base.dao.ValueObject;

public class ResVO extends ValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -219772735588818002L;
	public static final int VIEW_TYPE = 0;// 视图类型为0
	public static final int FORM_TYPE = 1;// 表单类型为1
	public static final int MENU_TYPE = 2;// 菜单类型为2
	public static final int FORM_FIELD_TYPE = 3;// 表单字段类型为3
	public static final int FOLDER_TYPE = 4;// 文件夹类型为4

	private String id;// 资源id
	private String name;// 可能包含通配符名称
	private String applicationid;// 应用编号
	private String caption;// 说明
	private String description;// 描述
	private int type;// 包括表单、表单字段、视图、菜单、文件夹、文件

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getApplicationid() {
		return applicationid;
	}

	public void setApplicationid(String applicationid) {
		this.applicationid = applicationid;
	}

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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
