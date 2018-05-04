package OLink.bpm.gkmsapi;

public class Test
{
	public static void main(String[] args)
	{
		String res;

		GK_MS_API api=new GK_MS_API();
		api.init("192.168.0.100", 8900, "admin", "admin", 0);
		try {
			XMLParser xml = api.getChildUg("0");
			xml = api.getAllUser();
			System.out.println("");
		} catch (Exception e) {
			e.printStackTrace();
		}

		/**
		 * 全集 同步组织
		 * 全集同步是，会删掉系统内存在的，但是请求的xml中不存在的部门或子部门数据.（注意这一点）
		 * 全集同步时不用调用“所有消息更新同步”接口
		 */
		//ArrayList<Ug> ugs = new ArrayList<Ug>();

		//Ug ug0 = new Ug("13","13", "0", false);		// 添加下级单位
		//ugs.add(ug0);

//		Ug ug1 = new Ug("1","11", "13", true);		// 添加部门
//		ugs.add(ug1);

//		Ug ug2 = new Ug("2","22", "0");				// 添加部门
//		ugs.add(ug2);

//		Ug ug3 = new Ug("3","33", "0");				// 添加部门
//		ugs.add(ug3);

		//res=api.updUgs(ugs,"123456");

		/**
		 * 全集 同步用户
		 * 全集同步时，会删掉系统内存在的，但是请求的xml中不存在的用户资料.（注意这一点）
		 * 全集同步时不用调用“所有消息更新同步”接口
		 */
		//ArrayList<User> users = new ArrayList<User>();

		//User user0=new User("1231232", "111111", "");
		//user0.setUgCode("eeaed9b746094145b3e9918da0013ce0");
		//user0.setUser_id("11e1-1bbe-98dc0de9-8098-8d7aecd1683b");
		//user0.setLocation("1");
		//users.add(user0);

//		User user1=new User("q", "111111", "");
//		user1.setLocation("2");
//		users.add(user1);

//		User user2=new User("w", "111111", "");
//		user2.setLocation("3");
//		users.add(user2);

//		User user3=new User("e", "111111", "");
//		user3.setLocation("4");
//		users.add(user3);

		//res=api.updUsers(users,"123456");
		//res = api.sendRequest("<request type=\"role\" subtype=\"delfromuser\" msid=\"\"><message sync=\"0\"><role id=\"2f08a9366750437a7fd564aa69638310\" /><users><user user_id=\"75ab33db317f4f6b8047f3a97969bfbf\" /></users></message></request>");
		//res = api.sendRequest("<request type=\"role\" subtype=\"addtouser\" msid=\"\"><message sync=\"0\"><role id=\"2f08a9366750437a7fd564aa69638310\" /><users><user user_id=\"67427a60-bf8b-4302-ac57-f68d3c5c710e\"  /></users></message></request>");
		//System.out.println(res);


		/**
		 * 发送所有消息更新同步
		 */
//		res = api.notifySyncAll();
//		System.out.println(res);


		//--------发送IM消息例子-------
		/*Message imm=new Message();
		imm.setSub_type("sys");
		//imm.setSender("testuser111");
		imm.addRecvAccount("testuser");
		//imm.addRecvGid("38139580");
		imm.setMessage("测试消息","aaa");
		res=api.sendMessage(imm,"123");
		System.out.println(res);*/

		/*-------SMS消息发送例子-------*/
		/*Message smm=new Message("sms","");
		smm.setSender("testuser111");
		smm.addRecvAccount("testuser");
		smm.setMessage("message","nihao");
		res=api.sendMessage(smm,"123");

		System.out.println(res);*/

//

		//-------用户管理例子-------

//		/*-------添加用户-------*/
//		User user=new User("g", "111111", "");
//		user.setLocation("9");
//		res=api.addUser(user,"123456");

//		System.out.println(res);

//
//
//		/*-------删除用户-------*/
//		res=api.delUser("","bcd","","123");

//		System.out.println(res);

//
//
//		/*-------修改密码-------*/
//		res=api.modPass("","123123","","111111","","321");

//		System.out.println(res);

//
//
//		/*-------修改用户信息-------*/
//		User user=new User("123123","123123","");
//		user.setAddress("北京市");
//		res=api.updUser(user,"111");

//		System.out.println(res);

//
//		/*-------用户分配短信-------*/
//		res=api.setSms("1", "a", "123456", "100", "add", "123");

//		System.out.println(res);

//
//		/*-------查询所有用户信息-------*/

//		res=api.getAllUser("321");
//		System.out.println(res);
//
//
//		/*-------查询指定用户信息-------*/
//		res=api.getUser("","a","","123");

//		System.out.println(res);

//
//		/*-------查看用户在线状态------*/
//		User user=new User("a","123123","");
//		ArrayList users=new ArrayList();
//		users.add(user);
//		res=api.checkOnline(users, "123");

//		System.out.println(res);

//
//		//-------组织结构管理例子-------
//
//		/*-------添加组织-------*/
//		Ug ug=new Ug("400","sa","0");
//		res=api.addUg(ug,"123");

//		System.out.println(res);

//
//
//		/*-------删除组织-------*/
//		res=api.delUg("100","123");

//		System.out.println(res);

//
//
//		/*-------修改组织信息-------*/
//		Ug ug=new Ug("100","shen","0");
//		api.addUg(ug,"123123");
//		res=api.updUg(ug,"123");

//		System.out.println(res);

//
//		/*-------查询所有组织信息------*/
//		res=api.getAllUgs("123");

//		System.out.println(res);

//
//
//		/*-------查询组织基本信息-------*/
//		res=api.getUgInfo("100","123");

//		System.out.println(res);

//
//
//
//		/*-------查询子组织基本信息-------*/
//		res=api.getChildUg("100","123");

//		System.out.println(res);

//
//
//		/*-------部门分配用户-------*/
//		res=api.addUserToUg("a","100","123");

//		System.out.println(res);

//
//
//		/*-------部门取消用户-------*/
//		res=api.delUserFromUg("a","100","123");

//		System.out.println(res);

//
//
//		/*-------查询组织子用户基本信息-------*/
//		res=api.getChildUser("0","123");

//		System.out.println(res);

//
//
//		/*-------查询用户所属组织基本信息-------*/
		//res=api.getUserUgs("b","123");
		//System.out.println(res);
//
//
//		/*-------passport验证-------*/
//		res=api.passportLogin("45502609","passport","123");

//		System.out.println(res);

//
//
//		/*-------OA系统用户身份验证-------*/
//		res=api.oaLogin("a","123123",0,"123");

//		System.out.println(res);

//
//		/*-------服务器状态控制-------*/
//		res=api.serverControl("status", "123");

//		System.out.println(res);

//
//		//4)户自己写的XML请求的例子
//		/*
//		String xml="<request type=\"im\" subtype=\"\" msid=\"123\">"+
//		            "<message type=\"normal\">" +
//		            "<receivers>" +
//		            "<receiver account=\"aaa\" />" +
//		            "<receiver gid=\"38139580\" />" +
//		            "</receivers>" +
//		            "<body>nihao!</body>" +
//		            "<htmlbody>nihao!</htmlbody>" +
//		            "</message>" +
//		            "</request>";
//		res=api.sendRequest(xml);

//		System.out.println(res);

//		*/

		//-------CTT实例-------
//		/**
//		 * 各CTT方法返回的字符串就是实现该功能的超链接地址，例如登陆操作：
//		 * 在网页上需要登录个客户端的时候，使用一下生成的串：
//		 * eg. elava://login?gid=5000.33854266&pwd=E10ADC3949BA59ABBE56E057F20F883E
//		 * 编写网页<a href="elava://login?gid=5000.33854266&pwd=E10ADC3949BA59ABBE56E057F20F883E">点击这里将会CTT启动登录</a>
//		 * 当用户在网页上点击此处连接的时候，将会自动登录客户端。
//		 * 也可以把生成的串拷贝到浏览器地址栏，回车，也可以测试自动登录。
//		 */
//		/*-------登陆-------*/
//		res=CTT.login("6761556","8095105",0);
//
//		/*-------聊天-------*/
//		res=CTT.chat("6761556","1100497");
//
//		/*-------加为好友-------*/
//		res=CTT.addFriend("6761556","1100497");
//
//		/*-------加入部落-------*/
//		res=CTT.addTribe("5","1100497");
//
//		/*-------进入部落-------*/
//		res=CTT.enterTribe("6","1100497");
//
//		/*-------语音聊天-------*/
//		res=CTT.call("6761556","1100497");
//
//		System.out.println(res);
	}

}

