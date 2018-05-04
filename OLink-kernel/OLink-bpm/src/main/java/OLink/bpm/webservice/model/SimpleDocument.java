/**
 * SimpleDocument.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package OLink.bpm.webservice.model;

import java.util.HashMap;

public class SimpleDocument implements java.io.Serializable {

	private static final long serialVersionUID = -5684538455736958085L;

	private String id;

	private java.util.Map<Object, Object> items = new HashMap<Object, Object>();

	private String stateLabel;

	public SimpleDocument() {
	}

	/**
	 * 获取标识
	 * 
	 * @return id 标识
	 */
	public String getId() {
		return id;
	}

	/**
	 * 设置文档
	 * 
	 * @param id
	 *            文档
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 获取元素集合
	 * 
	 * @return 元素集合
	 */
	public java.util.Map<Object, Object> getItems() {
		return items;
	}

	/**
	 * 设置元素集合
	 * 
	 * @param items
	 *            元素集合
	 */
	public void setItems(java.util.Map<Object, Object> items) {
		this.items = items;
	}

	/**
	 * 获取文档状态
	 * 
	 * @return stateLabel 文档状态
	 */
	public String getStateLabel() {
		return stateLabel;
	}

	/**
	 * 设置文档状态
	 * 
	 * @param stateLabel
	 *            　文档状态
	 */
	public void setStateLabel(String stateLabel) {
		this.stateLabel = stateLabel;
	}
}
