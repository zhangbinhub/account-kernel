package OLink.bpm.core.fieldextends.ejb;

import java.lang.reflect.Method;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.department.ejb.DepartmentVO;
import OLink.bpm.core.user.ejb.UserVO;

/**
 * 用户扩展表影射类
 * 
 * @author think
 */
@SuppressWarnings("serial")
public class FieldExtendsVO extends ValueObject {

	public static final String TABLE_USER = "tableUser";// 所属表标识，用户表
	public static final String TABLE_DEPT = "tableDept";// 所属表标识，部门表
	public static final String TYPE_STRING = "string";// 类型标识，字符串
	public static final String TYPE_DATE = "date";// 类型标识，日期
	public static final String TYPE_NUMBER = "number";// 类型标识，数字
	public static final String TYPE_CLOB = "clob";// 类型标识，大字段

	private String fid;
	private String forTable;// 字段所属表
	private String name;// 字段名字
	private String label;// 字段标签
	private String type;// 字段类型
	private Boolean isNull;// 是否可以为空
	private Boolean enabel;// 是否显示在列表中
	private Integer sortNumber;// 排序位置优先级，数据越小，排序越前

	public String getFid() {
		return fid;
	}

	public void setFid(String fid) {
		this.fid = fid;
	}

	public String getForTable() {
		return forTable;
	}

	public void setForTable(String forTable) {
		this.forTable = forTable;
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

	public Boolean getIsNull() {
		return isNull;
	}

	public void setIsNull(Boolean isNull) {
		this.isNull = isNull;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Boolean getEnabel() {
		return enabel;
	}

	public void setEnabel(Boolean enabel) {
		this.enabel = enabel;
	}

	public Integer getSortNumber() {
		return sortNumber;
	}

	public void setSortNumber(Integer sortNumber) {
		this.sortNumber = sortNumber;
	}
	
	/**
	 * 
	 * @param object DepartmentVO or UserVO object
	 * @return
	 */
	public String getValue(ValueObject object) {
		//获取当前字段名
		String fieldName = this.getName();
		fieldName = fieldName.replaceFirst("f","F");
		String value = "";
		//根据字段名组成方法名，并获取方法的返回值
		try {
			if (object instanceof DepartmentVO) {
				Method method = DepartmentVO.class.getMethod("get" + fieldName);
				Object result = method.invoke(object);
				if(result != null) {
					value = result.toString();
				}
			} else if (object instanceof UserVO) {
				Method method = UserVO.class.getMethod("get" + fieldName);
				Object result = method.invoke(object);
				if(result != null) {
					value = result.toString();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	
}
