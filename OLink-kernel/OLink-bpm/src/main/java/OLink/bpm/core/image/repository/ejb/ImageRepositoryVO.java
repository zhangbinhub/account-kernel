package OLink.bpm.core.image.repository.ejb;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.deploy.module.ejb.ModuleVO;

/**
 * @hibernate.class table="T_IMAGEREPOSITORY"
 * @author Marky
 */
public class ImageRepositoryVO extends ValueObject {
	
	private static final long serialVersionUID = -8217855463237935707L;

	private String id;

	/*
	 * 所属Module
	 */
	private ModuleVO module;

	/*
	 * 脚本内容
	 */
	private String content;

	/*
	 * 名称
	 */
	private String name;

	private int version;

	/**
	 * 返回版本
	 * 
	 * @hibernate.property column="VERSIONS"
	 * @return
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * 
	 * @param version
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	/**
	 * 返回脚本内容
	 * 
	 * @hibernate.property column="CONTENT"
	 * @return
	 */

	public String getContent() {
		return content;
	}

	/**
	 * 设置内容
	 * 
	 * @param content
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * 主键
	 * 
	 * @hibernate.id column="ID" generator-class="assigned"
	 * @return Returns the id.
	 */
	public String getId() {
		return id;
	}

	/**
	 * 设置主键
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 返回所属模块(Module)
	 * 
	 * @return ModuleVO
	 * @hibernate.many-to-one class="ModuleVO"
	 *                        column="MODULE"
	 */
	public ModuleVO getModule() {
		return module;
	}

	/**
	 * 设置所属模块(Module)
	 * 
	 * @param module
	 */
	public void setModule(ModuleVO module) {
		this.module = module;
	}

	/**
	 * 返回名称
	 * 
	 * @hibernate.property column="NAME"
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置名称
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

}
