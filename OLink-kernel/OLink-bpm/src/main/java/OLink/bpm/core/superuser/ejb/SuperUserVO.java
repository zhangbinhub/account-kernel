package OLink.bpm.core.superuser.ejb;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import OLink.bpm.core.domain.ejb.DomainVO;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.core.user.ejb.BaseUser;

public class SuperUserVO extends BaseUser {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7126705084367988200L;

	private Collection<DomainVO> domains;

	private Collection<ApplicationVO> applications;

	/**
	 * 域管理员集合
	 * 
	 * @return 域集合
	 */
	public Collection<DomainVO> getDomains() {
		if (domains == null)
			domains = new HashSet<DomainVO>();
		return domains;
	}

	/**
	 * 设置管理员为域管理员
	 * 
	 * @param domains
	 */
	public void setDomains(Collection<DomainVO> domains) {
		this.domains = domains;
	}

	/**
	 * 获取管理员下的应用
	 * 
	 * @return 应用集合
	 */
	public Collection<ApplicationVO> getApplications() {
		if (applications == null) {
			applications = new HashSet<ApplicationVO>();
		}
		return applications;
	}

	/**
	 * 设置管理员可管理应用
	 * 
	 * @param applications
	 */
	public void setApplications(Collection<ApplicationVO> applications) {
		this.applications = applications;
	}

	/**
	 * 根据域名称获取管理员所管理的企业域
	 * 
	 * @param domainName
	 *            企业域名称
	 * @return 企业域
	 */
	public DomainVO getDomainByName(String domainName) {
		for (Iterator<DomainVO> iterator = getDomains().iterator(); iterator.hasNext();) {
			DomainVO domain = iterator.next();
			if (domain.getName().equals(domainName)) {
				return domain;
			}
		}
		return null;
	}

	/**
	 * 比较管理员
	 */
	public boolean equals(Object another) {
		if (another == null)
			return false;
		if (another.getClass() != this.getClass())
			return false;
		SuperUserVO other = (SuperUserVO) another;
		return (other.getId() != null && getId() != null && getId().equals(other.getId()))
				&& (getLoginno() != null && other.getLoginno() != null && getLoginno().equals(other.getLoginno()));
	}

	public int hashCode() {
		return super.hashCode();
	}
}
