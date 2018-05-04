package OLink.bpm.gkmsapi;
import java.security.MessageDigest;

/**
 * CTT类
 * Click To Talk的缩写，就是在网页上点击一下就可以
 * 发起一个登陆，聊天，加为好友,进入部落，加入部落，语音聊天等操作
 */
public class CTT {

	/**
	 *登陆
	 *@param gid  用户GID
	 *@param pwd  用户密码
	 *@param type 登陆类型
	       		  0---密码是明码
		   		  1---密码是MD5码
	 *@return CTT登陆的超链接地址
	 */
	public static String login(String gid,String pwd,int type){
		StringBuffer str=new StringBuffer();
		str.append("elava://login?gid=");
		str.append(gid);
		str.append("&pwd=");
		if(type!=0)
		{
			str.append(pwd);
		}
		else
		{	
			str.append(str2MD5(str.toString()));
		}
		return str.toString();
	}

	/** 
	 *聊天
	 *@param  toGid 聊天对象的GID
	 *@param  gid   操作者的GID
	 *@return 指定2个GID进行聊天的超链接地址
	 */
	public static String chat(String toGid,String gid){
		StringBuffer str=new StringBuffer();
		str.append("elava://chat?");
		if(gid.length()> 0)
		{
			str.append("gid=");
			str.append(gid);
			str.append("&");
		}
		str.append("contact=");
		str.append(toGid);
		return str.toString();
	}

	/** 
	 *聊天
	 *@param  gid 聊天对象的GID
	 *@return 当前默认GID和指定GID进行聊天的超链接地址
	 */
	public static String chat(String gid){
		StringBuffer str=new StringBuffer();
		str.append("elava://chat?");
		str.append("contact=");
		str.append(gid);
		return str.toString();
	}
	
	/** 
	 *加为好友
	 *@param  toGid 好友的GID
	 *@param  gid   操作者的GID
	 *@return 增加好友的超链接地址
	 */
	public static String addFriend(String toGid, String gid ){
		StringBuffer str=new StringBuffer();
		str.append("elava://add?");
		if(gid.length()>0)
		{
			str.append("gid=");
			str.append(gid);
			str.append("&");
		}
		str.append("contact=");
		str.append(toGid);
		return str.toString();
	}
	
	/** 
	 *加为好友
	 *@param  gid 好友的GID
	 *@return 当前默认GID将指定GID增加好友的超链接地址
	 */
	public static String addFriend(String gid){
		StringBuffer str=new StringBuffer();
		str.append("elava://add?");
		str.append("contact=");
		str.append(gid);
		return str.toString();
	}

	/** 
	 *加入部落
	 *@param  id    部落ID
	 *@param  gid   操作者的GID
	 *@return 加入指定部落的超链接地址
	 */
	public static String addTribe(String id,String gid){
		StringBuffer str=new StringBuffer();
		str.append("elava://addtribe?");
		if(gid.length()>0)
		{
			str.append("gid=");
			str.append(gid);
			str.append("&");
		}
		str.append("id=");
		str.append(id);
		return str.toString();
	}

	/** 
	 *加入部落
	 *@param  id    部落ID
	 *@return 当前默认的GID加入指定部落的超链接地址
	 */
	public static String addTribe(String id){
		StringBuffer str=new StringBuffer();
		str.append("elava://addtribe?");
		str.append("id=");
		str.append(id);
		return str.toString();
	}

	/** 
	 *进入部落
	 *@param  id    部落ID
	 *@param  gid   操作者的GID
	 *@return 进入指定部落的超链接地址
	 */
	public static String enterTribe(String id, String gid)
	{
		StringBuffer str=new StringBuffer();
		str.append("elava://entertribe?");
		if(gid.length()>0)
		{
			str.append("gid=");
			str.append(gid);
			str.append("&");
		}
		str.append("id=");
		str.append(id);
		return str.toString();
	}

	/** 
	 *进入部落
	 *@param  id    部落ID
	 *@return 当前默认的GID进入指定部落的超链接地址
	 */
	public static String enterTribe(String id)
	{
		StringBuffer str=new StringBuffer();
		str.append("elava://entertribe?");
		str.append("id=");
		str.append(id);
		return str.toString();
	}

	/** 
	 *语音聊天
	 *@param  toGid 聊天对象的GID
	 *@param  gid   操作者的GID
	 *@return 指定2个GID进行聊天的超链接地址
	 */
	public static String call(String toGid,String gid)
	{
		StringBuffer str=new StringBuffer();
		str.append("elava://call?");
		if(gid.length()>0)
		{
			str.append("gid=");
			str.append(gid);
			str.append("&");
		}
		str.append("contact=");
		str.append(toGid);
		return str.toString();
	}

	/** 
	 *语音聊天
	 *@param  gid 聊天对象的GID
	 *@return 当前默认GID和指定GID进行聊天的超链接地址
	 */
	public static String call(String gid)
	{
		StringBuffer str=new StringBuffer();
		str.append("elava://call?");
		str.append("contact=");
		str.append(gid);
		return str.toString();
	}

	private final static String[] hexDigits = {
	  "0", "1", "2", "3", "4", "5", "6", "7",
	  "8", "9", "A", "B", "C", "D", "E", "F"
	};
	
	/**
	* 转换字节数组为16进制字串
	* @param b 字节数组
	* @return 16进制字串
	*/	
	private static String byteArrayToHexString(byte[] b) 
	{
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
		  resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}
	
	private static String byteToHexString(byte b){
		int n=b;
		if(n<0){
			n+=256;
		}		
		return hexDigits[n/16]+hexDigits[n%16];
	}
	
	public static String str2MD5(String orignal){
		String resultString=null;
		try{
			resultString=new String(orignal);
			MessageDigest md = MessageDigest.getInstance("MD5");
			resultString=byteArrayToHexString(md.digest(resultString.getBytes()));
		}catch(Exception ex){			
		}
		return resultString;
	}

}
