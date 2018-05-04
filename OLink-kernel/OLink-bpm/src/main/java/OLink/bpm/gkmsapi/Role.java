package OLink.bpm.gkmsapi;

import java.util.ArrayList;
import java.util.Collection;

import OLink.bpm.core.role.ejb.RoleVO;

public class Role {

	private String id;
	private String name;
	private String remark;
	private String ug_code;
	private Collection<String> privs;
	
	/**
	 * 输出XML字符串
	 */
	public String toString(){
		return toXML();
	}
	
	/**
	 * 平台角色转为GKE角色
	 * @param rolevo
	 */
	public void roleVOToRole(RoleVO rolevo){
		id = GKUtil.getUUID(rolevo.getId());
		name = rolevo.getName();
		remark = rolevo.getName();
		privs = new ArrayList<String>();
		privs.add("AUDIOCOMM");//语音通讯
		privs.add("VIDEOCOMM");//视频通讯
		privs.add("REMOTECTRLINVITE");//远程协助
		privs.add("SHAREFOLDER");//共享文件夹
		privs.add("GROUPMSG");//群发信息
		privs.add("MOBILEMSG");//发送短信
		privs.add("GROUPMOBILEMSG");//群发短信
		privs.add("SENDFILE");//发送文件
		privs.add("MODIFYPASSWORD");//修改个人资料
	}
	
	/**
	 * 将角色与域管理
	 * @param domainID
	 */
	public void setDomainToRole(String domainID){
		ug_code = GKUtil.getUUID(domainID);
	}
	
	/**
	 * 拼装XML格式
	 * @return
	 */
	public String toXML(){
		StringBuffer sb = new StringBuffer();
		sb.append("<role ");
		sb.append("ug_code=\""+GKUtil.codeXml(ug_code)+"\" ");
		sb.append("id=\""+GKUtil.codeXml(id)+"\" ");
		sb.append("name=\""+GKUtil.codeXml(name)+"\" ");
		sb.append("remark=\""+GKUtil.codeXml(remark)+"\" />");
		if(privs!=null){
			sb.append("<privs>");
			for(int i=0;i<privs.size();i++){
				sb.append("<priv code=\""+GKUtil.codeXml((privs.toArray()[i]).toString())+"\" />");
			}
			sb.append("</privs>");
		}
		return sb.toString();
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Collection<String> getPrivs() {
		return privs;
	}

	public void setPrivs(Collection<String> privs) {
		this.privs = privs;
	}

	public String getUg_code() {
		return ug_code;
	}

	public void setUg_code(String ugCode) {
		ug_code = ugCode;
	}
	
	
	
}
