package OLink.bpm.core.sysconfig.ejb;

public class ImConfig  implements java.io.Serializable{

	private static final long serialVersionUID = 8115554852650182965L;
	
	/**
	 * gke为开启状态
	 */
	public static final String GKEOPEN = "1";
	/**
	 * gke是否启用
	 */
	public static final String GKE_API_OPEN = "GKE_API_OPEN";
	/**
	 * gke服务器地址
	 */
	public static final String GKE_API_IP = "GKE_API_IP";
	/**
	 * gke服务器端口
	 */
	public static final String GKE_SERVER_PORT = "GKE_SERVER_PORT";
	/**
	 * gke服务器登录名
	 */
	public static final String GKE_SERVER_LOGINNO = "GKE_SERVER_LOGINNO";
	/**
	 * gke服务器登录密码
	 */
	public static final String GKE_SERVER_PASSWORD = "GKE_SERVER_PASSWORD";
	
	private String open;
	private String ip;
	private String port;
	private String loginno;
	private String password;
	
	
	public String getOpen() {
		return open;
	}
	public void setOpen(String open) {
		this.open = open;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getLoginno() {
		return loginno;
	}
	public void setLoginno(String loginno) {
		this.loginno = loginno;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
	
}
