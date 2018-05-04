package OLink.bpm.core.deploy.copymodule.ejb;

import java.io.Serializable;
import java.util.HashMap;

import OLink.bpm.base.dao.ValueObject;

public class CopyModuleVO extends ValueObject implements Serializable {

	private static final long serialVersionUID = -423071484839993694L;
	// 模块名
	private String modulename;

	// 应用ID
	private String applicationid;
	// 模块描述
	private String description;
	// 模块Id
	private String moduleId;

	private String[] formId;

	private String viewId;
	
	/**
	 * 存放ID
	 * @SuppressWarnings 不清楚存放的是什么
	 */
	@SuppressWarnings("unchecked")
	private HashMap map = new HashMap();

	private boolean _isCopyModuel;

	/**
	 * 获取模块描述
	 * 
	 * @return 模块描述
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 设置模块描述
	 * 
	 * @param description
	 *            模块描述
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 获取复制的模块标识
	 * 
	 * @return 复制的模块标识
	 */
	public String getModuleId() {
		return moduleId;
	}

	/**
	 * 设置复制的模块标识
	 * 
	 * @param moduleId
	 *            复制的模块标识
	 */
	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	/**
	 * 获取复制的模块名
	 * 
	 * @return 复制的模块名
	 */
	public String getModulename() {
		return modulename;
	}

	/**
	 * 设置复制的模块名
	 * 
	 * @param modulename
	 *            复制的模块名
	 */
	public void setModulename(String modulename) {
		this.modulename = modulename;
	}

	/**
	 * 获取应用标识
	 */
	public String getApplicationid() {
		return applicationid;
	}

	/**
	 * 设置应用标识
	 */
	public void setApplicationid(String applicationid) {
		this.applicationid = applicationid;
	}

	/**
	 * 获取保存的表单标识的数组
	 * 
	 * @return 表单标识的数组
	 */
	public String[] getFormId() {
		return formId;
	}

	/**
	 * 设置保存的表单标识的数组
	 * 
	 * @param formId
	 *            表单标识的数组
	 */
	public void setFormId(String[] formId) {
		this.formId = formId;
	}

	/**
	 * 获取视图标识
	 * 
	 * @return 视图标识
	 */
	public String getViewId() {
		return viewId;
	}

	/**
	 * 设置视图标识
	 * 
	 * @param viewId
	 */
	public void setViewId(String viewId) {
		this.viewId = viewId;
	}

	/**
	 * 获取是否是复制模块的状态
	 * 
	 * @return 是否是复制模块的状态
	 */
	public boolean is_isCopyModuel() {
		return _isCopyModuel;
	}

	/**
	 * 设置是否是复制模块的状态
	 * 
	 * @param copyModuel
	 *            是否是复制模块的状态
	 */
	public void set_isCopyModuel(boolean copyModuel) {
		_isCopyModuel = copyModuel;
	}

	/**
	 * 获取存对象放相应标识
	 * 
	 * @return 存放对象相应标识集合
	 */
	@SuppressWarnings("unchecked")
	public HashMap getMap() {
		return map;
	}

	/**
	 * 设置存放对象相应标识
	 * 
	 * @param map
	 *            存放对象相应标识集合
	 */
	@SuppressWarnings("unchecked")
	public void setMap(HashMap map) {
		this.map = map;
	}

}
