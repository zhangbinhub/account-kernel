package OLink.bpm.core.workflow.statelabel.ejb;

import OLink.bpm.base.dao.ValueObject;

/**
 * @hibernate.class table="T_STATELABEL"
 * @author Marky
 */
public class StateLabel extends ValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2269159226787453368L;

	private String id; // 主键

	private String name; // name

	private String description; // 说明

	private String value; // 值

	private String orderNo;// 排序值

	/**
	 * 获取标识
	 * 
	 * @hibernate.id column="ID" generator-class="assigned"
	 */
	public String getId() {
		return id;
	}

	/**
	 * 获取标识
	 * 
	 * @param id
	 *            标识
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 获取状态的名称
	 * 
	 * @return 状态的名称
	 * @hibernate.property column="NAME"
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置状态的名称
	 * 
	 * @param name
	 *            状态的名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取排序值
	 * 
	 * @return 排序值
	 * @hibernate.property column="ORDERNO"
	 */
	public String getOrderNo() {
		return orderNo;
	}

	/**
	 * 设置排序值
	 * 
	 * @param orderNo
	 *            排序值
	 */
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	/**
	 * 获取流程状态的描述
	 * 
	 * @hibernate.property column="DESCRIPTION"
	 * @return 流程状态的描述
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 设置流程状态的描述
	 * 
	 * @param description
	 *            流程状态的描述
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 获取状态的值
	 * 
	 * @hibernate.property column="VALUE"
	 * @return 状态的值
	 */
	public String getValue() {
		return value;
	}

	/**
	 * 设置状态的值
	 * 
	 * @param value
	 *            状态的值
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
