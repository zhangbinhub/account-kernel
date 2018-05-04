package OLink.bpm.gkmsapi;

import java.util.*;

/**
 *消息管理类
 *用于设置消息发送者/接收者/内容，并可将消息内容变成待发送的XML格式
 */
public class Message {
	private String type;                           //0-系统消息(默认值)，1-普通消息，2-短信消息
	private String sub_type;
	private String senderAccount;
	private String senderGid;
	private String ug_code;
	private ArrayList recvAccount;     //接收者account
	private ArrayList recvGid;			//接收者gid
	private ArrayList recvMobile;		//接收者mobile
	private String bodyMessage;
	private String htmlMessage;
	
	/**
	 * 默认构造方法
	 */
	public Message(){
		this.type = "im";
		this.sub_type = "normal";
		this.senderAccount = null;
		this.senderGid = null;
		recvAccount = new ArrayList();
		recvGid = new ArrayList();
		recvMobile = new ArrayList();
		bodyMessage = null;
		htmlMessage = null;
	}
	
	/**
	 * 构造方法，根据类型构造消息对象：0--系统IM消息，1--普通IM消息，2--SMS消息
	 * @param type 消息类型
	 */
	public Message(String type,String sub_type){
		this.type = type;
		this.sub_type = sub_type;
		this.senderAccount = null;
		this.senderGid = null;
		recvAccount = new ArrayList();
		recvGid = new ArrayList();
		recvMobile = new ArrayList();
		bodyMessage = null;
		htmlMessage = null;
	}
	
	/**
	 * 构造方法，根据消息类型，发送者的帐号，发送者的gid，body消息内容，htmlbody消息内容，组织编号构造消息对象
	 * @param type        消息类型
	 * @param account     发送者的帐号
	 * @param gid         发送者的gid
	 * @param bodyMessage body消息内容
	 * @param htmlMessage htmlbody消息内容
	 * @param ugCode
	 */
	public Message(String type,String sub_type,String account,String gid,String bodyMessage,String htmlMessage,String ugCode){
		this.type = type;
		this.sub_type = sub_type;
		this.senderAccount = account;
		this.senderGid = gid;
		this.ug_code = new String();
		this.bodyMessage = bodyMessage;
		this.htmlMessage = htmlMessage;
		recvAccount = new ArrayList();
		recvGid = new ArrayList();
		recvMobile = new ArrayList();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getSub_type(){
		return sub_type;
	}
	
	public void setSub_type(String sub_type){
		this.sub_type = sub_type;
	}
	
	public void setSender(String account){
		this.senderAccount=account;
	}
	
	public void setSender(String account,String gid){
		this.senderAccount=account;
		this.senderGid=gid;
	}
	
	public String getUg_code() {
		return ug_code;
	}

	public void setUg_code(String ug_code) {
		this.ug_code = ug_code;
	}
	
	public void setMessage(String bodyMessage){
		this.bodyMessage=bodyMessage;
	}

	public void setMessage(String bodyMessage,String htmlMessage){
		this.bodyMessage=bodyMessage;
		this.htmlMessage=htmlMessage;
	}
	
	public void addRecvAccount(String account){
		this.recvAccount.add(account);
	}
	
	public void addRecvGid(String gid){
		this.recvGid.add(gid);
	}
	
	public void addRecvMobile(String mobile){
		this.recvMobile.add(mobile);
	}

	public String toXml(){
		return toXml("");
	}

	/**
	 *消息转换成相应的XML请求
	 *@param  messageID 消息ID
	 *@return String类型的发送消息的XML请求
	 */
	public String toXml(String messageID){
		StringBuffer str=new StringBuffer(200);
		str.append("<request type=\"");
		str.append(type);
		str.append("\" subtype=\"\" msid=\"");
		str.append(messageID);
		str.append("\"><message");
		//Update XGY 2012.11.29
		if(type.equals("im"))
			str.append(" type=\""+sub_type+"\"");
		str.append(">");
		if(type.equals("sms")  || sub_type.equals("normal") ){
			str.append("<sender");
			if(senderAccount != null){
				str.append(" account=\"");
				str.append(GKUtil.codeXml(senderAccount));
				str.append("\" ");			
			}
			if(senderGid!=null){
				str.append(" gid=\"");
				str.append(GKUtil.codeXml(senderGid));
				str.append("\" ");			
			}
			str.append("/>");
		}
		str.append("<receivers");
		if(type.equals("sms")  && ug_code!=null)
			str.append(" ug_code=\""+GKUtil.codeXml(ug_code)+"\"");
		str.append(">");
		for(int i=0;i<recvAccount.size();i++){
			String account = (String)recvAccount.get(i);
			str.append("<receiver account=\""+GKUtil.codeXml(account)+"\" />");
		}
		for(int i=0;i<recvGid.size();i++){
			String gid = (String)recvGid.get(i);
			str.append("<receiver gid=\"");
			str.append(GKUtil.codeXml(gid));
			str.append("\" />");
		}
		if(type.equals("sms") && recvMobile.size()!=0){
			for(int i=0;i<recvMobile.size();i++){
				String mobile = (String)recvMobile.get(i);
				str.append("<receiver mobile=\""+GKUtil.codeXml(mobile)+"\" />");
			}
		}
		str.append("</receivers><body>");
		str.append(GKUtil.codeXml(bodyMessage));
		str.append("</body>");
		if(type.equals("im"))
			str.append("<htmlbody>"+GKUtil.codeXml(htmlMessage)+"</htmlbody>");
		str.append("</message></request>");
		return str.toString();
	}
	
	public String toString(){
		return toXml();
	}
}
