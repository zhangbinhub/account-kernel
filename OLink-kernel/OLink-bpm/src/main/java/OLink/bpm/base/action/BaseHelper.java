package OLink.bpm.base.action;

import OLink.bpm.base.ejb.IDesignTimeProcess;

/**
 * The Basic Helper class.
 */
public abstract class BaseHelper<E> {
	/**
	 * The inner application id
	 */
	protected String applicationid = null;
	/**
	 * The inner module id
	 */
	protected String moduleid = null;
	/**
	 * The inner process process
	 */
	public IDesignTimeProcess<E> process = null;

	/**
	 * 构造方法
	 */
	public BaseHelper(IDesignTimeProcess<E> process) {
		this.process = process;
	}

	/**
	 * 获取应用标识
	 * 
	 * @return the application id
	 */
	public String getApplicationid() {
		return applicationid;
	}

	/**
	 * 设置应用标识
	 * 
	 * @param application
	 *            应用标识
	 */
	public void setApplicationid(String applicationid) {
		this.applicationid = applicationid;
	}

	/**
	 * 获取模块标识
	 * 
	 * @return 模块标识
	 */
	public String getModuleid() {
		return moduleid;
	}

	/**
	 * 设置 模块标识
	 * 
	 * @param moduleid
	 */
	public void setModuleid(String moduleid) {
		this.moduleid = moduleid;
	}
}
