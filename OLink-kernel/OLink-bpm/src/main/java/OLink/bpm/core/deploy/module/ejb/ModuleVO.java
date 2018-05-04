
package OLink.bpm.core.deploy.module.ejb;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.task.ejb.Task;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.image.repository.ejb.ImageRepositoryVO;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiVO;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.core.dynaform.printer.ejb.Printer;
import OLink.bpm.core.style.repository.ejb.StyleRepositoryVO;

/**
 * 注册的功能模块
 * 
 * @hibernate.class table="T_MODULE" lazy = "false"
 */
public class ModuleVO extends ValueObject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4753641130316589274L;

	/**
	 * @uml.property name="id"
	 */
	private String id;

	/**
	 * @uml.property name="name"
	 */
	private String name;

	/**
	 * @uml.property name="pathname"
	 */
	private String pathname;

	/**
	 * @uml.property name="ispublished"
	 */
	private boolean ispublished;

	/**
	 * @uml.property name="description"
	 */
	private String description;

	/**
	 * @uml.property name="orderno"
	 */
	private int orderno;

	public ApplicationVO application;

	public ModuleVO superior;

	/**
	 * @uml.property name="views"
	 */
	private Set<View> views;

	/**
	 * @uml.property name="forms"
	 */
	public Set<Form> forms;

	/**
	 * @uml.property name="imglibs"
	 */
	public Set<ImageRepositoryVO> imglibs;

	/**
	 * @uml.property name="stylelibs"
	 */
	public Set<StyleRepositoryVO> stylelibs;

	/**
	 * @uml.property name="flowdefis"
	 */
	public Set<BillDefiVO> flowdefis;
	/**
	 * @uml.property name="tasks"
	 */
	public Set<Task> tasks;

	private Set<Printer> printers;

	/**
	 * @uml.property name="inuredate"
	 */

	/**
	 * @uml.property name="lastmodifytime"
	 */
	private Date lastmodifytime;

	/**
	 * @uml.property name="commitDate"
	 */
	private Date commitDate;

	/**
	 * @uml.property name="remoteServer"
	 */
	private String remoteServer;

	/**
	 * 获取确认保存时间
	 * 
	 * @hibernate.property column = "COMMITDATE"
	 * @return 保存时间
	 * @uml.property name="commitDate"
	 */
	public Date getCommitDate() {
		return commitDate;
	}

	/**
	 * 设置保存时间
	 * 
	 * @param commitDate
	 *            保存时间
	 * @uml.property name="commitDate"
	 */
	public void setCommitDate(Date commitDate) {
		this.commitDate = commitDate;
	}

	/**
	 * 获取相关联的应用
	 * 
	 * @return ApplicationVO
	 * @hibernate.many-to-one 
	 *                        class="ApplicationVO"
	 *                        column="APPLICATION"
	 * @uml.property name="application"
	 */
	public ApplicationVO getApplication() {
		return application;
	}

	/**
	 * 设置相关联的应用
	 * 
	 * @param application
	 *            应用对象
	 * @uml.property name="application"
	 */
	public void setApplication(ApplicationVO application) {
		this.application = application;
	}

	/**
	 * 获取最后修改日期. 此为记录表单修改的最后日期.
	 * 
	 * @hibernate.property column="LASTMODIFYTIME"
	 * @return 最后的修改日期
	 * @uml.property name="lastmodifytime"
	 */
	public Date getLastmodifytime() {
		return lastmodifytime;
	}

	/**
	 * 设置最后的修改日期. 此为记录表单修改的最后日期.
	 * 
	 * @param lastmodifytime
	 *            最后的修改日期
	 * @uml.property name="lastmodifytime"
	 */
	public void setLastmodifytime(Date lastmodifytime) {
		this.lastmodifytime = lastmodifytime;
	}

	/**
	 * 获取模块描述
	 * 
	 * @return 模块描述
	 * @hibernate.property column="DESCRIPTION"
	 * @uml.property name="description"
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 设置模块描述
	 * 
	 * @param description
	 *            模块描述
	 * @uml.property name="description"
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 获取主键标识
	 * 
	 * @hibernate.id column="ID" generator-class="assigned"
	 * @uml.property name="id"
	 */
	public String getId() {
		return id;
	}

	/**
	 * 设置标识
	 * 
	 * @param id
	 *            标识
	 * @uml.property name="id"
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 获取是否发布
	 * 
	 * @hibernate.property column="ISPUBLISHED"
	 * @uml.property name="ispublished"
	 */
	public boolean isIspublished() {
		return ispublished;
	}

	/**
	 * 设置是否发布
	 * 
	 * @param ispublished
	 *            the ispublished to set
	 * @uml.property name="ispublished"
	 */
	public void setIspublished(boolean ispublished) {
		this.ispublished = ispublished;
	}

	/**
	 * 获取模块名
	 * 
	 * @return 模块名
	 * @hibernate.property column="NAME"
	 * @uml.property name="name"
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置模块名
	 * 
	 * @param name
	 *            模块名
	 * @uml.property name="name"
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取排序号
	 * 
	 * @return 排序号
	 * @hibernate.property column="ORDERNO"
	 * @uml.property name="orderno"
	 */
	public int getOrderno() {
		return orderno;
	}

	/**
	 * 设置排序号
	 * 
	 * @param orderno
	 *            排序号
	 * @uml.property name="orderno"
	 */
	public void setOrderno(int orderno) {
		this.orderno = orderno;
	}

	/**
	 * 获取路径
	 * 
	 * @return 路径
	 * @hibernate.property column="PATHNAME"
	 * @uml.property name="pathname"
	 */
	public String getPathname() {
		return pathname;
	}

	/**
	 * 设置路径
	 * 
	 * @param pathname
	 *            路径
	 * @uml.property name="pathname"
	 */
	public void setPathname(String pathname) {
		this.pathname = pathname;
	}

	/**
	 * 获取模块的上级
	 * 
	 * @return 模块的上级
	 * @hibernate.many-to-one class="ModuleVO"
	 *                        not-null = "false" column="SUPERIOR"
	 * @uml.property name="superior"
	 */
	public ModuleVO getSuperior() {
		return superior;
	}

	/**
	 * 设置模块的上级
	 * 
	 * @param superior
	 *            模块的上级
	 * @uml.property name="superior"
	 */
	public void setSuperior(ModuleVO superior) {
		this.superior = superior;
	}

	/**
	 * 获取模块下相关联的视图
	 * 
	 * @return 视图的集合
	 * @hibernate.set name="views" cascade="delete" inverse="true" lazy="true"
	 * @hibernate.collection-key column="MODULE"
	 * @hibernate.collection-one-to-many 
	 *                                   class="View"
	 * @uml.property name="views"
	 */
	public Set<View> getViews() {
		return views;
	}

	/**
	 * 设置模块下相关联的视图
	 * 
	 * @param views
	 *            视图集合
	 * @uml.property name="views"
	 */
	public void setViews(Set<View> views) {
		this.views = views;
	}

	/**
	 * 获取模块下相关联的表单
	 * 
	 * @return 关联的表单的集合
	 * @hibernate.set name="forms" cascade="delete" inverse="true" lazy="true"
	 *                order-by="ID"
	 * @hibernate.collection-key column="MODULE"
	 * @hibernate.collection-one-to-many 
	 *                                   class="Form"
	 * @uml.property name="forms"
	 */
	public Set<Form> getForms() {
		return forms;
	}

	/**
	 * 设置模块下相关联的表单
	 * 
	 * @param forms
	 *            相关联的表单集合
	 * @uml.property name="forms"
	 */
	public void setForms(Set<Form> forms) {
		this.forms = forms;
	}

	/**
	 * 获取模块下图片库
	 * 
	 * @return 图片库集合
	 * @hibernate.set name="imglibs" cascade="delete" lazy="true" inverse="true"
	 *                order-by = "ID"
	 * @hibernate.collection-key column="MODULE"
	 * @hibernate.collection-one-to-many 
	 *                                   class="ImageRepositoryVO"
	 * @uml.property name="imglibs"
	 */
	public Set<ImageRepositoryVO> getImglibs() {
		return imglibs;
	}

	/**
	 * 设置图片库
	 * 
	 * @param imglibs
	 *            图片库集合
	 * @uml.property name="imglibs"
	 */
	public void setImglibs(Set<ImageRepositoryVO> imglibs) {
		this.imglibs = imglibs;
	}

	/**
	 * 获取样式库
	 * 
	 * @hibernate.set name="stylelibs" cascade="delete" lazy="true"
	 *                inverse="true"
	 * @hibernate.collection-key column="MODULE"
	 * @hibernate.collection-one-to-many 
	 *                                   class="StyleRepositoryVO"
	 * @uml.property name="stylelibs"
	 * @return 样式库集合
	 */
	public Set<StyleRepositoryVO> getStylelibs() {
		return stylelibs;
	}

	/**
	 * 设置样式库
	 * 
	 * @param stylelibs
	 *            样式库集合
	 * @uml.property name="stylelibs"
	 */
	public void setStylelibs(Set<StyleRepositoryVO> stylelibs) {
		this.stylelibs = stylelibs;
	}

	/**
	 * 获取模块下的流程集合
	 * 
	 * @hibernate.set name="flowdefis" cascade="delete" lazy="true"
	 *                inverse="true"
	 * @hibernate.collection-key column="MODULE"
	 * @hibernate.collection-one-to-many 
	 *                                   class="BillDefiVO"
	 * @uml.property name="flowdefis"
	 * @return 流程集合
	 */
	public Set<BillDefiVO> getFlowdefis() {
		return flowdefis;
	}

	/**
	 * 设置 流程集合
	 * 
	 * @param flowdefis
	 *            流程集合
	 * @uml.property name="flowdefis"
	 */
	public void setFlowdefis(Set<BillDefiVO> flowdefis) {
		this.flowdefis = flowdefis;
	}

	/**
	 * 获取完整的模块名
	 * 
	 * @return 完整的模块名
	 */
	public String getFullName() {
		String moduleName = "";
		ModuleVO mv = this;

		moduleName = mv.getName() + "/" + moduleName;
		while (mv.getSuperior() != null) {
			mv = mv.getSuperior();
			moduleName = mv.getName() + "/" + moduleName;
		}

		moduleName = mv.getApplication().getName() + "/" + moduleName;

		return moduleName;
	}

	/**
	 * 获取运程服务器地址
	 * 
	 * @hibernate.property column = "REMOTESERVER"
	 * @return
	 * @uml.property name="remoteServer"
	 */
	public String getRemoteServer() {
		return remoteServer;
	}

	/**
	 * 设置运程服务器地址
	 * 
	 * @param remoteServer
	 *            运程服务器地址
	 * @uml.property name="remoteServer"
	 */
	public void setRemoteServer(String remoteServer) {
		this.remoteServer = remoteServer;
	}

	/**
	 * 获取模块下的定时器集合
	 * 
	 * @hibernate.set name="tasks" cascade="delete" inverse="true" lazy="true"
	 * @hibernate.collection-key column="MODULE"
	 * @hibernate.collection-one-to-many class="Task"
	 * @uml.property name="tasks"
	 * @return 定时器集合
	 */
	public Set<Task> getTasks() {
		return tasks;
	}

	/**
	 * 设置定时器集合
	 * 
	 * @param tasks
	 *            定时器集合
	 */
	public void setTasks(Set<Task> tasks) {
		this.tasks = tasks;
	}

	public Set<Printer> getPrinters() {
		return printers;
	}

	public void setPrinters(Set<Printer> printers) {
		this.printers = printers;
	}

}
