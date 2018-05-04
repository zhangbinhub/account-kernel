package OLink.bpm.core.usersetup.ejb;

import java.io.Serializable;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.user.ejb.UserVO;

public class UserSetupVO extends ValueObject implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1227247252182057631L;

	private String id;
	
	private String userId;
	
	private UserVO user;
	
	private String userSkin;
	
	private String userStyle;
	
	private int useHomePage;
	
	private String pendingStyle;
	
	private String generalPage;
	
	private String domainid;
	
	private int status;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserSkin() {
		return userSkin;
	}

	public void setUserSkin(String userSkin) {
		this.userSkin = userSkin;
	}

	public String getUserStyle() {
		return userStyle;
	}

	public void setUserStyle(String userStyle) {
		this.userStyle = userStyle;
	}

	public UserVO getUser() {
		return user;
	}

	public void setUser(UserVO user) {
		this.user = user;
	}

	public String getPendingStyle() {
		return pendingStyle;
	}

	public void setPendingStyle(String pendingStyle) {
		this.pendingStyle = pendingStyle;
	}

	public String getDomainid() {
		return domainid;
	}

	public void setDomainid(String domainid) {
		this.domainid = domainid;
	}

	public String getGeneralPage() {
		return generalPage;
	}

	public void setGeneralPage(String generalPage) {
		this.generalPage = generalPage;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getUseHomePage() {
		return useHomePage;
	}

	public void setUseHomePage(int useHomePage) {
		this.useHomePage = useHomePage;
	}
}
