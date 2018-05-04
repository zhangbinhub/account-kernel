package OLink.bpm.core.dynaform.dts.excelimport.config.ejb;

import OLink.bpm.base.dao.ValueObject;


/**
 * @hibernate.class  table="T_MAPPCONFIG_IMPEXCEL"
 * @author  vxk
 */
public class IMPMappingConfigVO extends ValueObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3059121315799207376L;

	private String id;
	
	private String name;
	
	private String xml;
	
	private String path;

	
	/**
	 * @hibernate.property column="PATH"
	 * @uml.property name="path"
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path  the path to set
	 * @uml.property name="path"
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @hibernate.id  column="ID" generator-class="assigned"
	 * @uml.property  name="id"
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id  the id to set
	 * @uml.property  name="id"
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @hibernate.property  column="NAME"
	 * @uml.property  name="name"
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name  The name to set.
	 * @uml.property  name="name"
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @hibernate.property  column="XML" type = "text"
	 * @uml.property  name="xml"
	 */
	public String getXml() {
		return xml;
	}

	/**
	 * @param xml  The xml to set.
	 * @uml.property  name="xml"
	 */
	public void setXml(String xml) {
		this.xml = xml;
	}
}
