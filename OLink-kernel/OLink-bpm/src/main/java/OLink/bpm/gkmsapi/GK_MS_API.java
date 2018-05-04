
package OLink.bpm.gkmsapi;

import java.util.*;

import OLink.bpm.core.department.ejb.DepartmentVO;
import OLink.bpm.core.domain.ejb.DomainVO;
import OLink.bpm.core.role.ejb.RoleVO;
import OLink.bpm.core.sysconfig.ejb.ImConfig;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.core.user.ejb.UserProcess;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.property.PropertyUtil;

/**
 *GK_MS_API类
 *为调用API建立连接并提供收发请求服务
 */
public class GK_MS_API {
	private String ip;
	private int port;
	private String authorization;  	 //保存base64(user:md5(pwd))结果
	public int sync=0;
	private static GK_MS_API gkmsapi;
	
	
	public static GK_MS_API getInstance(){
		if(gkmsapi==null){
			gkmsapi = new GK_MS_API();
		}
		PropertyUtil.reload("im");
		gkmsapi.init(PropertyUtil.get(ImConfig.GKE_API_IP), Integer.parseInt(PropertyUtil.get(ImConfig.GKE_SERVER_PORT)), PropertyUtil.get(ImConfig.GKE_SERVER_LOGINNO), PropertyUtil.get(ImConfig.GKE_SERVER_PASSWORD), 0);
		return gkmsapi;
	}

	/**
	 *初始化操作
	 *根据ip，port建立连接
	 *@param ip   服务器的IP地址
	 *@param port 服务器对应gkmsapi的端口号
	 */
	public void init(String ip,int port){
		this.ip=ip;
		this.port=port;
		this.sync=0;
		authorization=null;
	}

	/**
	 *初始化操作
	 *根据ip，port建立连接
	 *@param ip   服务器的IP地址
	 *@param port 服务器对应gkmsapi的端口号
	 *@param sync sync=1或者没有sync 表示发送消息，sync=0表示不发送消息
	 */
	public void init(String ip,int port, int sync){
		this.ip=ip;
		this.port=port;
		this.sync=sync;
		authorization=null;
	}

	/**
	 *初始化操作
	 *根据ip，port，用户名，密码建立连接
	 *@param ip   服务器的IP地址
	 *@param port 服务器对应gkmsapi的端口号
	 *@param user 授权用户的用户名
	 *@param pwd  授权用户的密码
	 */
	public void init(String ip,int port,String user,String pwd){
		this.ip=ip;
		this.port=port;
		this.sync=0;
		//计算base64(user:md5(pwd))的结果，并保存在authorization中
		authorization=GKUtil.encode((user+":"+GKUtil.str2MD5(pwd)).getBytes());
	}

	/**
	 *初始化操作
	 *根据ip，port，用户名，密码建立连接
	 *@param ip   服务器的IP地址
	 *@param port 服务器对应gkmsapi的端口号
	 *@param user 授权用户的用户名
	 *@param pwd  授权用户的密码
	 *@param sync sync=1或者没有sync 表示发送消息，sync=0表示不发送消息
	 */
	public void init(String ip,int port,String user,String pwd, int sync){
		this.ip=ip;
		this.port=port;
		this.sync=sync;
		//计算base64(user:md5(pwd))的结果，并保存在authorization中
		authorization=GKUtil.encode((user+":"+GKUtil.str2MD5(pwd)).getBytes());
	}

	/**
	 * sync=1或者没有sync 表示发送消息，sync=0表示不发送消息
	 */
	public void setSync(int sync){
		this.sync = sync;
	}

	/**
	 * 向服务器发送XML请求
	 * @param xml : 请求的消息包(UTF-8编码)
	 * @return 发送XML请求的服务器回复请求
	 */
	public String sendRequest(String xml) throws Exception
	{
		HttpClient hc = new HttpClient(ip,port,authorization);
		String res = hc.request("POST", "/api/gkmsapi", xml);
		return res;
	}

	/**
	 *发送IM消息或者SMS消息
	 *@param  mes        消息对象
	 *@param  messageID  消息ID
	 *@return 发送消息的服务器回复信息
	 */
	public String sendMessage(Message mes,String messageID) throws Exception{
		return sendRequest(mes.toXml(messageID));
	}
	
	
	/**
	 *发送IM消息或者SMS消息
	 *@param  mes        消息对象
	 *@param  messageID  消息ID
	 *@return 发送消息的服务器回复信息
	 */
	public XMLParser sendMessage(Message mes) throws Exception{
		XMLParser xml = new XMLParser();
		xml.parseXML(sendRequest(mes.toXml("")));
		return xml;
	}

	/**
	 *添加用户
	 *@param  user 待添加的用户
	 *@param  messageID 消息ID
	 *@return 添加用户请求的服务器回复信息
	 */
	public String addUser(UserVO userVO, String messageID) throws Exception{
		String res = "";
		User user = new User();
		user.userVOToUser(userVO);
		String str="<request type=\"user\" subtype=\"adduser\" msid=\""+messageID+"\">" +
				"<message sync=\""+this.sync+"\">"+user+"</message></request>";
		res = sendRequest(str);
		if(userVO.getRoles()!=null){
			for (Iterator<RoleVO> iterator = userVO.getRoles().iterator(); iterator.hasNext();) {
				RoleVO role = iterator.next();
				res = addToUser(userVO.getId(),role.getId(), "");
			}
		}
		try{
			XMLParser xml = new XMLParser();
			xml.parseXML(res);
			if(xml.getCode()==0){
				return " {*[synchronouslyData]*}Gk-Express-Server{*[Success]*}";
			}else{
				return " Gk-Express-Server("+xml.getCode()+"):"+xml.getMessage();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 *添加用户
	 *@param  user 待添加的用户
	 *@param  messageID 消息ID
	 *@return 添加用户请求的服务器回复信息
	 */
	public XMLParser addUser(UserVO userVO) throws Exception{
		User user = new User();
		user.userVOToUser(userVO);
		String str="<request type=\"user\" subtype=\"adduser\" msid=\"\"><message sync=\""+this.sync+"\">"+user+"</message></request>";
		XMLParser xml = new XMLParser();
		xml.parseXML(sendRequest(str));
		return xml;
	}

	/**
	 *删除用户
	 *@param  account   待删除的用户帐号
	 *@param  messageID 消息ID
	 *@return 删除用户请求的服务器回复信息
	 */
	public String delUser(String[] _selects,String messageID) throws Exception{
		String res = "";
		for(int i = 0;i<_selects.length;i++){
			StringBuffer str= new StringBuffer();
			str.append("<request type=\"user\" subtype=\"deluser\" msid=\""+messageID+"\">" +
			"<message sync=\""+this.sync+"\"><user ");
			str.append("user_id=\""+GKUtil.getUUID(_selects[i])+"\" /></message></request>");
			res =  sendRequest(str.toString());
		}
		try{
			XMLParser xml  = new XMLParser();
			xml.parseXML(res);
			if(xml.getCode()==0){
				return " {*[synchronouslyData]*}Gk-Express-Server{*[Success]*}";
			}else{
				return " Gk-Express-Server("+xml.getCode()+"):"+xml.getMessage();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 *删除用户
	 *@param  account   待删除的用户帐号
	 *@param  messageID 消息ID
	 *@return 删除用户请求的服务器回复信息
	 */
	public XMLParser delUser(String code)throws Exception{
		StringBuffer str= new StringBuffer();
		str.append("<request type=\"user\" subtype=\"deluser\" msid=\"\"><message sync=\""+this.sync+"\"><user ");
		str.append("gid=\""+code+"\" /></message></request>");
		XMLParser xml  = new XMLParser();
		xml.parseXML(sendRequest(str.toString()));
		return xml;
	}

	/**
	 *修改指定用户的密码
	 *@param  account   用户帐号
	 *@param  pwd       旧密码
	 *@param  md5_pwd   新密码
	 *@param  messageID 消息ID
	 *@return 修改指定用户的密码请求的服务器回复信息
	 */
	public String modPass(String user_id,String account,String gid,String pwd,String md5_pwd,String messageID) throws Exception{
		StringBuffer str= new StringBuffer();
		str.append("<request type=\"user\" subtype=\"modpass\" msid=\""+messageID
				+"\"><message sync=\""+this.sync+"\"><user ");
		if(!user_id.equals(""))
			str.append("user_id=\""+user_id+"\" ");
		if(!account.equals(""))
			str.append("account=\""+account+"\" ");
		if(!gid.equals(""))
			str.append("gid=\""+gid+"\" ");
		if(!pwd.equals(""))
			str.append("pwd=\""+pwd+"\" ");
		if(!md5_pwd.equals(""))
			str.append("md5_pwd=\""+md5_pwd+"\" ");
		str.append(" /></message></request>");
		return sendRequest(str.toString());
	}

	/**
	 *更新用户信息
	 *@param user      需要修改的用户
	 *@param messageID 消息ID
	 *@return 更新用户信息请求的服务器回复信息
	 */
	public String updUser(UserVO userVO,String messageID){
		String res = "";
		try{
			UserProcess process = (UserProcess) ProcessFactory.createProcess(UserProcess.class);
			UserVO tempUser = (UserVO)process.doView(userVO.getId());
			if(tempUser.getRoles()!=null){
				for (Iterator<RoleVO> iterator = tempUser.getRoles().iterator(); iterator.hasNext();) {
					RoleVO role = iterator.next();
					if(userVO.getRoles()!=null){
						res = delFromUser(userVO.getId(), role.getId(), "");
					}else{
						
					}
				}
			}
			
			User  user= new User();
			user.userVOToUser(userVO);
			String str="<request type=\"user\" subtype=\"upduser\" msid=\""+messageID
					+"\"><message sync=\""+this.sync+"\">"+user.toXml()+"</message></request>";
			res = sendRequest(str);
			if(userVO.getRoles()!=null){
				for (Iterator<RoleVO> iterator = userVO.getRoles().iterator(); iterator.hasNext();) {
					RoleVO role = iterator.next();
					res = addToUser(userVO.getId(), role.getId(), "");
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try{
			XMLParser xml  = new XMLParser();
			xml.parseXML(res);
			if(xml.getCode()==0){
				return " {*[synchronouslyData]*}Gk-Express-Server{*[Success]*}";
			}else{
				return " Gk-Express-Server("+xml.getCode()+"):"+xml.getMessage();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}
	
	
	/**
	 *更新用户信息
	 *@param user      需要修改的用户
	 *@param messageID 消息ID
	 *@return 更新用户信息请求的服务器回复信息
	 */
	public XMLParser updUser(UserVO userVO) throws Exception{
		User  user= new User();
		user.userVOToUser(userVO);
		String str="<request type=\"user\" subtype=\"upduser\" msid=\"\"><message sync=\""+this.sync+"\">"+user.toXml()+"</message></request>";
		XMLParser xml  = new XMLParser();
		xml.parseXML(sendRequest(str));
		return xml;
			
	}

	/**
	 * 用户分配短信
	 * @param user_id	用户ID
	 * @param account	用户帐号
	 * @param gid		用户GID
	 * @param num		增加或者减少的条数
	 * @param type		add		增加
	 * 					reduce	减少
	 * @param messageID	消息ID
	 * @return 用户分配短信请求的服务器回复信息
	 */
	public String setSms(String user_id,String account,String gid,String num,String type,String messageID) throws Exception{
		String str="<request type=\"user\" subtype=\"setsms\" msid=\""+messageID+"\" ><message sync=\""+this.sync+"\"><users num=\""+num+"\" type=\""+type+"\"><user user_id=\""
					+user_id+"\" account=\""+account+"\" gid=\""+gid+"\" /></users></message></request>";
		return sendRequest(str);
	}

	/**
	 *查询所有用户的信息
	 *@param messageID 消息ID
	 *@return 查询所有用户的信息请求的服务器回复信息
	 */
	public String getAllUser(String messageID) throws Exception{
		String str="<request type=\"user\" subtype=\"getalluser\" msid=\""+messageID
				+"\"></request>";
		return sendRequest(str);
	}
	
	/**
	 *查询所有用户的信息
	 *@param messageID 消息ID
	 *@return 查询所有用户的信息请求的服务器回复信息
	 */
	public XMLParser getAllUser() throws Exception{
		String str="<request type=\"user\" subtype=\"getalluser\" msid=\"\"></request>";
		XMLParser xml  = new XMLParser();
		xml.parseXML(sendRequest(str));
		return xml;
	}

	/**
	 *查询指定帐号的用户信息
	 *@param  account   用户帐号
	 *@param  messageID 消息ID
	 *@return 查询指定帐号的用户信息请求的服务器回复信息
	 */
	public String getUser(String user_id,String account,String gid,String messageID) throws Exception{
		StringBuffer str= new StringBuffer();
		str.append("<request type=\"user\" subtype=\"getuser\" msid=\""+messageID
				+"\"><message sync=\""+this.sync+"\"><user ");
		if(!user_id.equals(""))
			str.append("user_id=\""+user_id+"\" ");
		if(!account.equals(""))
			str.append("account=\""+account+"\" ");
		if(!gid.equals(""))
			str.append("gid=\""+gid+"\" ");
		str.append(" /></message></request>");
		return sendRequest(str.toString());
	}
	
	/**
	 *查询指定帐号的用户信息
	 *@param  account   用户帐号
	 *@param  messageID 消息ID
	 *@return 查询指定帐号的用户信息请求的服务器回复信息
	 */
	public XMLParser getUser(String account)throws Exception{
		StringBuffer str= new StringBuffer();
		str.append("<request type=\"user\" subtype=\"getuser\" msid=\"\"><message sync=\""+this.sync+"\"><user ");
		if(!account.equals(""))
			str.append("account=\""+account+"\" ");
		str.append(" /></message></request>");
		XMLParser xml  = new XMLParser();
		xml.parseXML(sendRequest(str.toString()));
		return xml;
	}

	/**
	 * 查看用户在线状态
	 * @param users 	查看的用户数组
	 * @param messageID 消息ID
	 */
	public String checkOnline(List users,String messageID) throws Exception{
		StringBuffer sb=new  StringBuffer();
		sb.append("<request type=\"user\" subtype=\"chkonline\" msid=\"");
		sb.append(messageID);
		sb.append("\" ><users>");
		int size=users.size();
		for(int i=0;i<size;i++){
			User user=(User)users.get(i);
			sb.append("<user ");
			if(!user.getUser_id().equals(""))
				sb.append("user_id=\""+user.getUser_id()+"\" ");
			if(!user.getAccount().equals(""))
				sb.append("account=\""+user.getAccount()+"\" ");
			if(!user.getGid().equals(""))
				sb.append("\" gid=\""+user.getGid()+"\" ");
			sb.append("/>");
		}
		sb.append("</users></request>");
		return sendRequest(sb.toString());
	}

	/**
	 *添加组织
	 *@param  ug        待添加的组织
	 *@param  messageID 消息ID
	 *@return 添加组织请求的服务器回复信息
	 */
	public String addUg(DepartmentVO departmentVO, String domainID, String messageID) throws Exception{
		Ug ug = new Ug();
		ug.DepartmentToUg(departmentVO,domainID);
		String str="<request type=\"ug\" subtype=\"addug\" msid=\""+messageID
				+"\"><message sync=\""+this.sync+"\">"+ug+"</message></request>";
		String res = sendRequest(str);
		String message = "";
		try{
			XMLParser xml  = new XMLParser();
			xml.parseXML(res);
			if(xml.getCode()==0){
				message +=" {*[synchronouslyData]*}Gk-Express-Server{*[Success]*}";
			}else{
				message +=" Gk-Express-Server("+xml.getCode()+"):"+xml.getMessage();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return message;
	}
	
	/**
	 *添加组织
	 *@param  ug        待添加的组织
	 *@param  messageID 消息ID
	 *@return 添加组织请求的服务器回复信息
	 */
	public XMLParser addUg(DepartmentVO departmentVO,String domainID)throws Exception{
		Ug ug = new Ug();
		ug.DepartmentToUg(departmentVO,domainID);
		String str="<request type=\"ug\" subtype=\"addug\" msid=\"\"><message sync=\""+this.sync+"\">"+ug+"</message></request>";
		XMLParser xml  = new XMLParser();
		xml.parseXML(sendRequest(str));
		return xml;
	}
	
	/**
	 *添加组织
	 *@param  ug        待添加的组织
	 *@param  messageID 消息ID
	 *@return 添加组织请求的服务器回复信息
	 */
	public String addUg(DomainVO domainVO, String messageID) throws Exception{
		Ug ug = new Ug();
		ug.domainToUg(domainVO);
		String str="<request type=\"ug\" subtype=\"addug\" msid=\""+messageID
				+"\"><message sync=\""+this.sync+"\">"+ug+"</message></request>";
		String res = sendRequest(str);
		String message = "";
		try{
			XMLParser xml  = new XMLParser();
			xml.parseXML(res);
			if(xml.getCode()==0){
				message +=" {*[synchronouslyData]*}Gk-Express-Server{*[Success]*}";
			}else{
				message +=" Gk-Express-Server("+xml.getCode()+"):"+xml.getMessage();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return message;
	}
	
	/**
	 *添加组织
	 *@param  ug        待添加的组织
	 *@param  messageID 消息ID
	 *@return 添加组织请求的服务器回复信息
	 */
	public XMLParser addUg(DomainVO domainVO){
		Ug ug = new Ug();
		ug.domainToUg(domainVO);
		String str="<request type=\"ug\" subtype=\"addug\" msid=\"\"><message sync=\""+this.sync+"\">"+ug+"</message></request>";
		XMLParser xml = new XMLParser();
		try{
			xml.parseXML(sendRequest(str));
		}catch(Exception e){
			e.printStackTrace();
		}
		return xml;
	}

	/**
	 *删除组织
	 *@param  code 组织编号
	 *@param  messageID 消息ID
	 *@return 删除组织请求的服务器回复信息
	 */
	public String delUg(String code,String messageID) throws Exception{
		String str="<request type=\"ug\" subtype=\"delug\" msid=\""+messageID
				+"\"><message sync=\""+this.sync+"\"><ug code=\""+code+"\" /></message></request>";
		return sendRequest(str);
	}
	
	/**
	 *删除组织
	 *@param  code 组织编号
	 *@param  messageID 消息ID
	 *@return 删除组织请求的服务器回复信息
	 */
	public XMLParser delUg(String code){
		String str="<request type=\"ug\" subtype=\"delug\" msid=\"\"><message sync=\""+this.sync+"\"><ug code=\""+code+"\" /></message></request>";
		XMLParser xml = new XMLParser();
		try{
			xml.parseXML(sendRequest(str));
		}catch(Exception e){
			e.printStackTrace();
		}
		return xml;
	}

	/**
	 *更新组织信息
	 *@param  ug        组织编号
	 *@param  messageID 消息ID
	 *@return 更新组织信息请求的服务器回复信息
	 */
	public String updUg(DomainVO domainVO,String messageID) throws Exception{
		//GKE更新单位
		Ug ug = new Ug();
		ug.domainToUg(domainVO);
		String str="<request type=\"ug\" subtype=\"updug\" msid=\""+messageID
				+"\"><message sync=\""+this.sync+"\">"+ug.toString()+"</message></request>";
		String res = sendRequest(str);
		String message = "";
		try{
			
			XMLParser xml  = new XMLParser();
			xml.parseXML(res);
			if(xml.getCode()==0){
				message +=" {*[synchronouslyData]*}Gk-Express-Server{*[Success]*}";
			}else{
				message +=" Gk-Express-Server("+xml.getCode()+"):"+xml.getMessage();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return message;
	}
	
	
	/**
	 *更新组织信息
	 *@param  ug        组织编号
	 *@param  messageID 消息ID
	 *@return 更新组织信息请求的服务器回复信息
	 */
	public XMLParser updUg(DomainVO domainVO){
		//GKE更新单位
		Ug ug = new Ug();
		ug.domainToUg(domainVO);
		String str="<request type=\"ug\" subtype=\"updug\" msid=\"\"><message sync=\""+this.sync+"\">"+ug.toString()+"</message></request>";
		XMLParser xml  = new XMLParser();
		try{
			xml.parseXML(sendRequest(str));
		}catch(Exception e){
			e.printStackTrace();
		}
		return xml;
	}
	
	/**
	 *更新组织信息
	 *@param  ug        组织编号
	 *@param  messageID 消息ID
	 *@return 更新组织信息请求的服务器回复信息
	 */
	public String updUg(DepartmentVO departmentVO,String domainID,String messageID) throws Exception{
		//GKE更新单位
		Ug ug = new Ug();
		ug.DepartmentToUg(departmentVO, domainID);
		String str="<request type=\"ug\" subtype=\"updug\" msid=\""+messageID
				+"\"><message sync=\""+this.sync+"\">"+ug.toString()+"</message></request>";
		XMLParser xml  = new XMLParser();
		xml.parseXML(sendRequest(str));
		if(xml.getCode()==0){
			return " {*[synchronouslyData]*}Gk-Express-Server{*[Success]*}";
		}else{
			return " Gk-Express-Server("+xml.getCode()+"):"+xml.getMessage();
		}
	}
	
	/**
	 *更新组织信息
	 *@param  ug        组织编号
	 *@param  messageID 消息ID
	 *@return 更新组织信息请求的服务器回复信息
	 */
	public XMLParser updUg(DepartmentVO departmentVO,String domainID) throws Exception{
		//GKE更新单位
		Ug ug = new Ug();
		ug.DepartmentToUg(departmentVO, domainID);
		String str="<request type=\"ug\" subtype=\"updug\" msid=\"\"><message sync=\""+this.sync+"\">"+ug.toString()+"</message></request>";
		XMLParser xml  = new XMLParser();
		xml.parseXML(sendRequest(str));
		return xml;
	}

	/**
	 * 查询所有组织信息
	 * @param messageID 消息ID
	 * @return 查询所有组织信息请求的服务器回复信息
	 */
	public XMLParser getAllUgs(String messageID)throws Exception{
		String str="<request type=\"ug\" subtype=\"getallugs\" msid=\""+messageID+"\" ></request>";
		XMLParser xml  = new XMLParser();
		xml.parseXML(sendRequest(str));
		return xml;
	}
	
	/**
	 * 查询子单位所有孩子节点
	 * @param messageID
	 * @return
	 * @throws Exception
	 */
	public XMLParser getChildUg(String code)throws Exception{
		String str="<request type=\"ug\" subtype=\"getchildug\" msid=\"\" ><message><ug code=\""+GKUtil.getUUID(code)+"\" /></message></request>";
		XMLParser xml  = new XMLParser();
		xml.parseXML(sendRequest(str));
		return xml;
	}

	/**
	 *查询组织基本信息
	 *@param  code   组织编号
	 *@param  messageID 消息ID
	 *@return 查询组织基本信息请求的服务器回复信息
	 */
	public String getUgInfo(String code,String messageID) throws Exception{
		String str="<request type=\"ug\" subtype=\"getuginfo\" msid=\""+messageID
				+"\"><message sync=\""+this.sync+"\"><ug code=\""+code+"\" /></message></request>";
		return sendRequest(str);
	}
	
	/**
	 *查询组织基本信息
	 *@param  code   组织编号
	 *@param  messageID 消息ID
	 *@return 查询组织基本信息请求的服务器回复信息
	 */
	public XMLParser getUgInfo(String code) throws Exception{
		String str="<request type=\"ug\" subtype=\"getuginfo\" msid=\"\"><message sync=\""+this.sync+"\"><ug code=\""+GKUtil.getUUID(code)+"\" /></message></request>";
		XMLParser xml  = new XMLParser();
		xml.parseXML(sendRequest(str));
		return xml;
	}

	/**
	 *查询指定父组织的子组织信息
	 *@param  code   父组织编号
	 *@param  messageID 消息ID
	 *@return 查询指定父组织的子组织信息请求的服务器回复信息
	 */
	public String getChildUg(String code,String messageID) throws Exception{
		String str="<request type=\"ug\" subtype=\"getchildug\" msid=\""+messageID
				+"\"><message sync=\""+this.sync+"\"><ug code=\""+code+"\" /></message></request>";
		return sendRequest(str);
	}

	/**
	 *将指定的用户分配到指定的组织中
	 *@param  account   用户帐号
	 *@param  code   组织编号
	 *@param  messageID 消息ID
	 *@return 将指定的用户分配到指定的组织中请求的服务器回复信息
	 */
	public String addUserToUg(String account,String code,String messageID) throws Exception{
		String str="<request type=\"ug\" subtype=\"addusertoug\" msid=\""+messageID
				+"\"><message sync=\""+this.sync+"\"><ug code=\""+code+"\" /><user account=\""+account
				+"\" /></message></request>";
		return sendRequest(str);
	}

	/**
	 *将指定的用户从指定的组织中取消
	 *@param  account   用户帐号
	 *@param  code   组织编号
	 *@param  messageID 消息ID
	 *@return 将指定的用户从指定的组织中取消请求的服务器回复信息
	 */
	public String delUserFromUg(String account,String code,String messageID) throws Exception{
		String str="<request type=\"ug\" subtype=\"deluserfromug\" msid=\""+messageID
				+"\"><message sync=\""+this.sync+"\"><ug code=\""+code+"\" /><user account=\""+account
				+"\" /></message></request>";
		return sendRequest(str);
	}

	/**
	 *查询指定组织的用户信息
	 *@param  code   组织编号
	 *@param  messageID 消息ID
	 *@return 查询指定组织的用户信息请求的服务器回复信息
	 */
	public String getChildUser(String code,String messageID) throws Exception{
		String str="<request type=\"ug\" subtype=\"getchilduser\" msid=\""+messageID
				+"\"><message sync=\""+this.sync+"\"><ug code=\""+code+"\" /></message></request>";
		return sendRequest(str);
	}
	
	/**
	 *查询指定组织的用户信息
	 *@param  code   组织编号
	 *@param  messageID 消息ID
	 *@return 查询指定组织的用户信息请求的服务器回复信息
	 */
	public XMLParser getChildUser(String code) throws Exception{
		String str="<request type=\"ug\" subtype=\"getchilduser\" msid=\"\"><message sync=\""+this.sync+"\"><ug code=\""+GKUtil.getUUID(code)+"\" /></message></request>";
		XMLParser xml  = new XMLParser();
		xml.parseXML(sendRequest(str));
		return xml;
	}

	/**
	 *查询指定用户所在的组织基本信息
	 *@param  account 用户帐号
	 *@param  messageID 消息ID
	 *@return 查询指定用户所在的组织基本信息请求的服务器回复信息
	 */
	public String getUserUgs(String account,String messageID) throws Exception{
		String str="<request type=\"ug\" subtype=\"getuserugs\" msid=\""+messageID
				+"\"><message sync=\""+this.sync+"\"><user account=\""+account+"\" /></message></request>";
		return sendRequest(str);
	}

	/**
	 *发送passport验证消息
	 *@param  gid       长gid
	 *@param  passport  passport验证码
	 *@param  messageID 消息ID
	 *@return 发送passport验证消息请求的服务器回复信息
	 */
	public String passportLogin(String gid,String passport,String messageID) throws Exception{
		String str="<request type=\"login\" subtype=\"passport\" msid=\""+messageID
				+"\"><message sync=\""+this.sync+"\"><user GID=\""+gid+"\" /><passport>"+passport
				+"</passport></message></request>";
		return sendRequest(str);
	}

	/**
	 *发送passport验证消息
	 *@param  gid       用户gid
	 *@param  zoneid    区号
	 *@param  passport  验证码
	 *@param  messageID 消息ID
	 *@return 发送passport验证消息请求的服务器回复信息
	 */
	public String passportLogin(String gid,String zoneid,String passport,String messageID) throws Exception{
		String str="<request type=\"login\" subtype=\"passport\" msid=\""+messageID
				+"\"><message sync=\""+this.sync+"\"><user gid=\""+gid+"\" zoneid=\""+zoneid+"\" /><passport>"
				+passport+"</passport></message></request>";
		return sendRequest(str);
	}

	/**
	 *OA系统用户身份验证
	 *@param  account   用户帐号
	 *@param  pwd       用户密码
	 *@param  type      0--明码密码 1--md5密码
	 *@param  messageID 消息ID
	 *@return OA系统用户身份验证请求的服务器回复信息
	 */
	public String oaLogin(String account,String pwd,int type,String messageID) throws Exception
	{
		StringBuffer str=new StringBuffer(100);
		str.append("<request type=\"login\" subtype=\"oalogin\" msid=\"");
		str.append(messageID);
		str.append("\"><message sync=\""+this.sync+"\"><user account=\"");
		str.append(account);
		str.append("\" pwd=\"");
		str.append(pwd);
		str.append("\" type=\"");
		if(type!=0){
			str.append("md5");
		}
		str.append("\"></user></message></request>");
		return sendRequest(str.toString());
	}

	/**
	 *OA系统用户身份验证
	 *@param  gid       用户gid
	 *@param  zoneid    区号
	 *@param  pwd       用户密码
	 *@param  type      0--明码密码 1--md5密码
	 *@param  messageID 消息ID
	 *@return OA系统用户身份验证请求的服务器回复信息
	 */
	public String oaLogin(String gid,String zoneid,String pwd,int type,String messageID) throws Exception
	{
		StringBuffer str=new StringBuffer(100);
		str.append("<request type=\"login\" subtype=\"oalogin\" msid=\"");
		str.append(messageID);
		str.append("\"><message sync=\""+this.sync+"\"><user gid=\"");
		str.append(gid);
		str.append("\" zoneid=\"");
		str.append(zoneid);
		str.append("\" pwd=\"");
		str.append(pwd);
		str.append("\" type=\"");
		if(type!=0){
			str.append("md5");
		}
		str.append("\"></user></message></request>");
		return sendRequest(str.toString());
	}

	/**
	 * 服务器状态控制
	 * @param action	start：启动服务器
						stop：停止服务器
						restart：重启服务器
						status：查看服务器运行状态
	 * @param messageID 消息ID
	 * @return 服务器状态控制请求的服务器返回信息
	 */
	public String serverControl(String action,String messageID) throws Exception{
		String str="<request type=\"service\" subtype=\"\" msid=\""+messageID+"\" ><message sync=\""+this.sync+"\"><control action=\""
					+action+"\" /></message></request>";
		return sendRequest(str);
	}

	// 组织树的深度优先遍历
	public String updUgs(ArrayList<Ug> vUgs, String messageID) throws Exception
	{
		StringBuffer sb=new StringBuffer();
		sb.append("<request type=\"sync\" subtype=\"all_ug\" msid=\""+messageID+"\"><message sync=\""+this.sync+"\"><ugs>");
		for(int i=0; i<vUgs.size(); i++)
		{
			Ug ug = vUgs.get(i);
			sb.append(ug.toString());
		}
		sb.append("</ugs></message></request>");
		return sendRequest(sb.toString());
	}

	// 所有用户，可随意顺序
	public String updUsers(ArrayList<User> vUsers, String messageID) throws Exception
	{
		StringBuffer sb=new StringBuffer();
		sb.append("<request type=\"sync\" subtype=\"all_user\" msid=\""+messageID+"\"><message sync=\""+this.sync+"\"><users>");
		for(int i=0; i<vUsers.size(); i++)
		{
			User user = vUsers.get(i);
			sb.append(user.toString());
		}
		sb.append("</users></message></request>");
		return sendRequest(sb.toString());
	}

	// 由于是异步接口，返回可能为空，需要等待一段时间再次调用
	public String getOnlineNum(String messageID) throws Exception
	{
		StringBuffer sb=new StringBuffer();
		sb.append("<request type=\"user\" subtype=\"onlinenum\" msid=\""+messageID+"\" ><message sync=\""+this.sync+"\"></message></request>");
		return sendRequest(sb.toString());
	}

	// 所有消息更新同步
	public String notifySyncAll() throws Exception
	{
		String s = "<request type=\"notify\" subtype=\"syncall\" ><message></message></request>";
		return sendRequest(s);
	}
	
	/**
	 *添加角色
	 *@param  ug        待添加的角色
	 *@param  messageID 消息ID
	 *@return 添加角色请求的服务器回复信息
	 */
	public XMLParser addRole(RoleVO roleVO,Collection<DomainVO> domains,String messageID) throws Exception{
		Role role = new Role();
		role.roleVOToRole(roleVO);
		String res = "";
		if(domains!=null){//GKE需关联域
			for (Iterator<DomainVO> iterator = domains.iterator(); iterator.hasNext();) {
				DomainVO domainVO = iterator.next();
				role.setDomainToRole(domainVO.getId());
				String str="<request type=\"role\" subtype=\"addrole\" msid=\""+messageID
				+"\"><message sync=\""+this.sync+"\">"+role.toString()+"</message></request>";
				res = sendRequest(str);
			}
		}else{
			String str="<request type=\"role\" subtype=\"addrole\" msid=\""+messageID
			+"\"><message sync=\""+this.sync+"\">"+role.toString()+"</message></request>";
			res = sendRequest(str);
		}
		
		XMLParser xml  = null;
		try{
			xml  = new XMLParser();
			xml.parseXML(res);
			if(xml.getCode()==0){
				xml.setMessage(" {*[synchronouslyData]*}Gk-Express-Server{*[Success]*}");
			}else{
				xml.setMessage(" Gk-Express-Server("+xml.getCode()+"):"+xml.getMessage());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return xml;
	}
	
	/**
	 *编辑角色
	 *@param  ug        编辑的角色
	 *@param  messageID 消息ID
	 *@return 编辑角色请求的服务器回复信息
	 */
	public String updRole(RoleVO roleVO,Collection<DomainVO> domains,String messageID) throws Exception{
		Role role = new Role();
		role.roleVOToRole(roleVO);
		String res = "";
		if(domains!=null){//GKE需关联域
			for (Iterator<DomainVO> iterator = domains.iterator(); iterator.hasNext();) {
				DomainVO domainVO = iterator.next();
				role.setDomainToRole(domainVO.getId());
				String str="<request type=\"role\" subtype=\"updrole\" msid=\""+messageID
				+"\"><message sync=\""+this.sync+"\">"+role.toString()+"</message></request>";
				res = sendRequest(str);
			}
		}else{
			String str="<request type=\"role\" subtype=\"updrole\" msid=\""+messageID
			+"\"><message sync=\""+this.sync+"\">"+role.toString()+"</message></request>";
			res = sendRequest(str);
		}
		
		try{
			XMLParser xml  = new XMLParser();
			xml.parseXML(res);
			if(xml.getCode()==0){
				return " {*[synchronouslyData]*}Gk-Express-Server{*[Success]*}";
			}else{
				return " Gk-Express-Server("+xml.getCode()+"):"+xml.getMessage();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 删除角色
	 * @param id
	 * @param messageID
	 * @return
	 */
	public String delRole(String[] ids,String messageID) throws Exception{
		String str="<request type=\"role\" subtype=\"delrole\" msid=\""+messageID;
		str+="\"><message sync=\""+this.sync+"\">";
		str+="<role_list>";
		for(int i=0;i<ids.length;i++){
			str+=("<role id=\""+GKUtil.getUUID(ids[i])+"\" />");
		}
		str+="</role_list>";
		str+="</message></request>";
		String res = sendRequest(str);
		try{
			//解析GKE删除单位提示信息
			XMLParser xml  = new XMLParser();
			xml.parseXML(res);
			if(xml.getCode()==0){
				return " {*[synchronouslyData]*}Gk-Express-Server{*[Success]*}";
			}else{
				return " Gk-Express-Server("+xml.getCode()+"):"+xml.getMessage();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 角色分配至用户
	 * @param vUsers
	 * @param roleID
	 * @param messageID
	 * @return
	 */
	public String addToUser(String userid,String roleID,String messageID) throws Exception{
		String str="<request type=\"role\" subtype=\"addtouser\" msid=\""+messageID+"\">";
		str+="<message sync=\""+this.sync+"\">";
		str+="<role id=\""+GKUtil.getUUID(roleID)+"\" />";
		str+="<users>";
		str+="<user user_id=\""+GKUtil.getUUID(userid)+"\" />";
		str+="</users>";
		str+="</message></request>";
		return sendRequest(str);
	}
	
	/**
	 * 角色取消分配至用户
	 * @param vUsers
	 * @param roleID
	 * @param messageID
	 * @return
	 */
	public String delFromUser(String userid,String roleID,String messageID) throws Exception{
		String str="<request type=\"role\" subtype=\"delfromuser\" msid=\""+messageID+"\">";
		str+="<message sync=\""+this.sync+"\">";
		str+="<role id=\""+GKUtil.getUUID(roleID)+"\" />";
		str+="<users>";
		str+="<user user_id=\""+GKUtil.getUUID(userid)+"\" />";
		str+="</users>";
		str+="</message></request>";
		return sendRequest(str);
	}
	
	/**
	 * 查询角色分配权限
	 * @param roleID
	 * @param messageID
	 * @return
	 */
	public String getRolePrivs(String roleID,String messageID) throws Exception{
		String str="<request type=\"role\" subtype=\"getroleprivs\" msid=\""+messageID+"\">";
		str+="<message sync=\""+this.sync+"\">";
		str+="<role id=\""+GKUtil.getUUID(roleID)+"\" />";
		str+="</message></request>";
		return sendRequest(str);
	}
	
	/**
	 * 查询用户分配角色
	 * @param roleID
	 * @param messageID
	 * @return
	 */
	public String getUserRoles(String user_id,String messageID) throws Exception{
		String str="<request type=\"role\" subtype=\"getuserroles\" msid=\""+messageID+"\">";
		str+="<message sync=\""+this.sync+"\">";
		str+="<user user_id=\""+GKUtil.getUUID(user_id)+"\" />";
		str+="</message></request>";
		return sendRequest(str);
	}
	
	/**
	 * 查询用户权限
	 * @param roleID
	 * @param messageID
	 * @return
	 */
	public String getUserPrivs(String user_id,String messageID) throws Exception{
		String str="<request type=\"role\" subtype=\"getuserprivs\" msid=\""+messageID+"\">";
		str+="<message sync=\""+this.sync+"\">";
		str+="<user user_id=\""+GKUtil.getUUID(user_id)+"\" />";
		str+="</message></request>";
		return sendRequest(str);
	}
}