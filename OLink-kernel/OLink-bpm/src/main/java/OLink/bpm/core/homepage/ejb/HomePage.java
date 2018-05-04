package OLink.bpm.core.homepage.ejb;

import java.util.Collection;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.dynaform.summary.ejb.SummaryCfgVO;
import OLink.bpm.core.style.repository.ejb.StyleRepositoryVO;


/**
 * @hibernate.class table="T_HOMEPAGE"
 */
public class HomePage extends ValueObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8613791341470549733L;

	/**
	 * REGULAR_MODE为常规模式 CUSTOMIZE_MODE为自定义模式
	 */
	public final static int REGULAR_MODE = 16, CUSTOMIZE_MODE = 256;

	private String name;

	private String layoutType;

	private String id;

	private boolean published;

	private String description;

	private Collection<Reminder> reminders;
	
	private Collection<SummaryCfgVO> summaryCfgs;

	private String roles;

	private String roleNames;

	/**
	 * 定制模式
	 */
	private Integer defineMode = REGULAR_MODE;

	private String templateContext;

	private StyleRepositoryVO style;

	public Integer getDefineMode() {
		if (this.defineMode == null)
			this.defineMode = REGULAR_MODE;
		return this.defineMode;
	}

	public void setDefineMode(Integer defineMode) {
		this.defineMode = defineMode;
	}

	public String getTemplateContext() {
		return this.templateContext;
	}

	public void setTemplateContext(String templateContext) {
		this.templateContext = templateContext;
	}

	public StyleRepositoryVO getStyle() {
		return this.style;
	}

	public void setStyle(StyleRepositoryVO style) {
		this.style = style;
	}

	public String getRoleNames() {
		return roleNames;
	}

	public void setRoleNames(String roleNames) {
		this.roleNames = roleNames;
	}

	/**
	 * @hibernate.property column="ROLES"
	 */
	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	/**
	 * @hibernate.property column="NAME"
	 * @return
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @hibernate.property column="LAYOUTTYPE"
	 * @return
	 */
	public String getLayoutType() {
		return layoutType;
	}

	public void setLayoutType(String layoutType) {
		this.layoutType = layoutType;
	}
//
//	/**
//	 * ������ҳ����������
//	 * 
//	 * @hibernate.set name="reminders" table="T_REMINDER" inverse="true"
//	 *                cascade="delete"
//	 * 
//	 * @hibernate.collection-key column="HOMEPAGE_ID"
//	 * @hibernate.collection-one-to-many 
//	 *                                   class="Reminder"
//	 * @return
//	 */
	public Collection<Reminder> getReminders() {
		return reminders;
	}

	public void setReminders(Collection<Reminder> reminders) {
		this.reminders = reminders;
	}
	
	public Collection<SummaryCfgVO> getSummaryCfgs() {
		return summaryCfgs;
	}

	public void setSummaryCfgs(Collection<SummaryCfgVO> summaryCfgs) {
		this.summaryCfgs = summaryCfgs;
	}

	/**
	 * @hibernate.id column="ID" generator-class="assigned"
	 * @uml.property name="id"
	 */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @hibernate.property column="DESCRIPTION"
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @hibernate.property column="DEFAULTTYPR"
	 * @return
	 */
	public boolean getPublished() {
		return published;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}
}
