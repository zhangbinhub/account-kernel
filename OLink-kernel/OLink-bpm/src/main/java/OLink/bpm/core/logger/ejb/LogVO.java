package OLink.bpm.core.logger.ejb;

import java.util.Date;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.superuser.ejb.SuperUserVO;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.StringUtil;

/**
 * 操作日志
 * @author Tom
 *
 */
public class LogVO extends ValueObject {

	private static final long serialVersionUID = -3332388760090024822L;

	private UserVO user;
	private SuperUserVO superUser;
	private Date date;
	private String type;
	private String description;
	private String operator;
	private String ip;

	/**
	 * @return the user
	 */
	public UserVO getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(UserVO user) {
		this.user = user;
	}

	/**
	 * @return the superUser
	 */
	public SuperUserVO getSuperUser() {
		return superUser;
	}

	/**
	 * @param superUser the superUser to set
	 */
	public void setSuperUser(SuperUserVO superUser) {
		this.superUser = superUser;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the operator
	 */
	public String getOperator() {
		return operator;
	}

	/**
	 * @param operator the operator to set
	 */
	public void setOperator(String operator) {
		this.operator = operator;
	}

	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @param ip the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public static LogVO valueOf(WebUser webUser, String type, String description, String ip) throws Exception {
		LogVO log = new LogVO();
		log.setDescription(description);
		log.setDate(new Date());
		log.setType(type);
		log.setIp(ip);
		log.setOperator(webUser.getName());
		log.setDomainid(webUser.getDomainid());
		log.setApplicationid(webUser.getDefaultApplication());
		if (StringUtil.isBlank(webUser.getId())) {
			return log;
		}
		if (webUser.isDeveloper() || webUser.isDomainAdmin()
				|| webUser.isSuperAdmin()) {
			SuperUserVO superUser = new SuperUserVO();
			superUser.setId(webUser.getId());
			log.setSuperUser(superUser);
		} else {
			UserVO user = new UserVO();
			user.setId(webUser.getId());
			log.setUser(user);
		}
		return log;
	}
	
	public static LogVO valueOf(SuperUserVO superUser, String type, String description, String ip) throws Exception {
		return valueOf(new WebUser(superUser), type, description, ip);
	}
	
	public static LogVO valueOf(UserVO user, String type, String description, String ip) throws Exception {
		return valueOf(new WebUser(user), type, description, ip);
	}

}
