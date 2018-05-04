package OLink.bpm.core.packager.ejb;

import java.util.Collection;
import java.util.Date;

import OLink.bpm.base.dao.ValueObject;

public class Package extends ValueObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 未安装
	 */
	public static final int INSTALL_STATE_NEVER = 0;

	/**
	 * 已安装
	 */
	public static final int INSTALL_STATE_ALREADY = 1;

	/**
	 * 待安装
	 */
	public static final int INSTALL_STATE_STAND = 2;

	private String name;

	private String title;

	private Date modifyDate;

	/**
	 * 打包的规则,包含哪些元素,排除哪些元素
	 */
	private Collection<Rule> rules;

	private String description;

	private String releaseNotos;

	private int installState;

	public int getInstallState() {
		return installState;
	}

	public void setInstallState(int installState) {
		this.installState = installState;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReleaseNotos() {
		return releaseNotos;
	}

	public void setReleaseNotos(String releaseNotos) {
		this.releaseNotos = releaseNotos;
	}

	public Collection<Rule> getRules() {
		return rules;
	}

	public void setRules(Collection<Rule> rules) {
		this.rules = rules;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
