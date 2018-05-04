
package OLink.bpm.core.counter.ejb;

import OLink.bpm.base.dao.ValueObject;

import java.io.Serializable;

/**
 * @author nicholas
 */
public class CounterVO extends ValueObject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2144226856287519164L;
	/**
	 * 
	 * @uml.property name="id"
	 */
	private String id;
	/**
	 * @uml.property name="name"
	 */
	private String name;
	/**
	 * @uml.property name="counter"
	 */
	private int counter;

	/**
	 * 获取生成的最后一个计数号
	 * 
	 * @return the counter
	 * @uml.property name="counter"
	 */
	public int getCounter() {
		return counter;
	}

	/**
	 * 获取主键
	 * 
	 * @return the id
	 * @uml.property name="id"
	 */
	public String getId() {
		return id;
	}

	/**
	 * 获取计数号的名字
	 * 
	 * @return the name
	 * @uml.property name="name"
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置计数号的名字
	 * 
	 * @param counter
	 *            the counter to set
	 * @uml.property name="counter"
	 */
	public void setCounter(int counter) {
		this.counter = counter;
	}

	/**
	 * 设置主键
	 * 
	 * @param id
	 *            the id to set
	 * @uml.property name="id"
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 设置名字
	 * 
	 * @param name
	 *            the name to set
	 * @uml.property name="name"
	 */
	public void setName(String name) {
		this.name = name;
	}
}
