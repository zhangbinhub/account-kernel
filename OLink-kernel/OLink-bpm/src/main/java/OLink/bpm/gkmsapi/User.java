package OLink.bpm.gkmsapi;

import java.util.Iterator;

import OLink.bpm.core.department.ejb.DepartmentVO;
import OLink.bpm.core.user.ejb.UserVO;
import eWAP.core.Tools;

/**
 *用户类
 *为用户管理和组织结构管理提供用户
 */
public class User {
	private String user_id;
	private	String account;
	private String gid;
	private	String name;
	private	String displayName;
	private	String pwd;
	private	String md5Pwd;
	private	String ugCode;
	private	boolean state;
	private	int sex;
	private	String birthday;
	private	String email;
	private	String mobile;
	private	String fax;
	private	String webAddress;
	private	String postcode;
	private	String officeTel;
	private	String address;
	private	String position;
	private	String remark;
	private String location;
	private String zoneid;
	private String ug_name;
	
	/**
	 * 默认构造方法
	 */
	public User(){
		this.user_id = "";
		this.account = "";
		this.gid = "";
		this.state=true;
		this.sex=0;
	}
	
	/**
	 * 构造方法，根据用户帐号构造用户对象
	 * @param account 用户帐号
	 */
	public User(String account){
		this.user_id = "";
		this.gid = "";
		this.account=account;
		this.state=true;
		this.sex=0;
	}
	
	/**
	 * 构造方法，根据用户帐号，密码和MD5密码构造用户对象
	 * @param account 用户帐号
	 * @param pwd     密码
	 * @param md5Pwd  MD5密码
	 */
	public User(String account,String pwd,String md5Pwd){
		this.user_id = "";
		this.gid = "";
		this.account=account;
		this.pwd=pwd;
		this.md5Pwd=md5Pwd;
	}
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getMd5Pwd() {
		return md5Pwd;
	}
	public void setMd5Pwd(String md5Pwd) {
		this.md5Pwd = md5Pwd;
	}
	public String getUgCode() {
		return ugCode;
	}
	public void setUgCode(String ugCode) {
		this.ugCode = ugCode;
	}
	public boolean isState() {
		return state;
	}
	public void setState(boolean state) {
		this.state = state;
	}
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	public String getWebAddress() {
		return webAddress;
	}
	public void setWebAddress(String webAddress) {
		this.webAddress = webAddress;
	}
	public String getPostcode() {
		return postcode;
	}
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}
	public String getOfficeTel() {
		return officeTel;
	}
	public void setOfficeTel(String officeTel) {
		this.officeTel = officeTel;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	
	/**
	 * 将平台用户对象转为GKE的用户对象
	 * @param uservo
	 */
	public void userVOToUser(UserVO uservo){
		this.user_id = GKUtil.getUUID(uservo.getId());
		this.account = uservo.getLoginno();
		this.name = uservo.getName();
		this.displayName = uservo.getName();
		if(Tools.decryptPassword(uservo.getLoginpwd())!=null){
			this.pwd = Tools.decryptPassword(uservo.getLoginpwd());
		}else{
			this.pwd = "teemlink";
		}
		StringBuffer ugCodeSB = new StringBuffer();
		//ugCodeSB.append("0|");
		ugCodeSB.append(GKUtil.getUUID(uservo.getDomainid())+"|");
		if(uservo.getDepartments()!=null){
			for (Iterator<DepartmentVO> iterator = uservo.getDepartments().iterator(); iterator.hasNext();) {
				DepartmentVO departmentVO = iterator.next();
				ugCodeSB.append(GKUtil.getUUID(departmentVO.getId())+"|");
			}
		}
		if(ugCodeSB.lastIndexOf("|")!=-1){
			ugCodeSB.deleteCharAt(ugCodeSB.lastIndexOf("|"));
		}
		this.ugCode = ugCodeSB.toString();
		this.state = uservo.getUseIM();
		this.sex = 0;
		this.email = uservo.getEmail();
		this.mobile = uservo.getTelephone();
		this.remark = uservo.getRemarks();
	}
	
	/**
	 *将User的信息转换成字符串
	 *@return String类型的字符串，包括User对象的所有信息
	 */
	public String toXml(){
		StringBuffer str=new StringBuffer(100);
		str.append("<user ");
		if(!user_id.equals(""))
			str.append("user_id=\""+user_id+"\" ");
		if(!account.equals(""))
			str.append("account=\""+GKUtil.codeXml(account)+"\" ");
		if(!gid.equals(""))
			str.append("gid=\""+gid+"\" ");
		if(name!=null)
			str.append(" name=\""+GKUtil.codeXml(name)+"\" ");
		if(displayName!=null)
			str.append("display_name=\""+GKUtil.codeXml(displayName)+"\" ");
		if(pwd!=null)
			str.append("pwd=\""+GKUtil.codeXml(pwd)+"\" ");
		if(md5Pwd!=null)
			str.append("md5_pwd=\""+GKUtil.codeXml(md5Pwd)+"\" ");
		if(ugCode!=null)
			str.append("ug_code=\""+GKUtil.codeXml(ugCode)+"\" ");
		str.append("state=\"");
		str.append(state==false?0:1);
		str.append("\" sex=\"");
		str.append(String.valueOf(sex));
		str.append("\" ");
		if(birthday!=null)
			str.append("birthday=\""+GKUtil.codeXml(birthday)+"\" ");
		if(email!=null)
			str.append("email=\""+GKUtil.codeXml(email)+"\" ");
		if(mobile!=null)
			str.append("mobile=\""+GKUtil.codeXml(mobile)+"\" ");
		if(officeTel!=null)
			str.append("office_tel=\""+GKUtil.codeXml(officeTel)+"\" ");
		if(fax!=null)
			str.append("fax=\""+GKUtil.codeXml(fax)+"\" ");
		if(webAddress!=null)
			str.append("webAddress=\""+GKUtil.codeXml(webAddress)+"\" ");
		if(postcode!=null)
			str.append("postcode=\""+GKUtil.codeXml(postcode)+"\" ");
		if(officeTel!=null)
			str.append("officeTel=\""+GKUtil.codeXml(officeTel)+"\" ");
		if(address!=null)
			str.append("address=\""+GKUtil.codeXml(address)+"\" ");
		if(position!=null)
			str.append("position=\""+GKUtil.codeXml(position)+"\" ");
		if(remark!=null)
			str.append("remark=\""+GKUtil.codeXml(remark)+"\" ");
		if(location!=null)
			str.append("location=\""+GKUtil.codeXml(location)+"\" ");
		if(zoneid!=null)
			str.append("zoneid=\""+GKUtil.codeXml(zoneid)+"\" ");
		if(ug_name!=null)
			str.append("ug_name=\""+GKUtil.codeXml(ug_name)+"\" ");
		str.append("/>");
		return str.toString();
	}
	
	public String toString(){
		return toXml();
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	public String getZoneid() {
		return zoneid;
	}

	public void setZoneid(String zoneid) {
		this.zoneid = zoneid;
	}

	public String getUg_name() {
		return ug_name;
	}

	public void setUg_name(String ugName) {
		ug_name = ugName;
	}
	
	
}
