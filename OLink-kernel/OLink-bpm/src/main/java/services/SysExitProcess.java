/*
/*
 * 创建日期: 2011-05-30
 * 创建人:	 xgy
 * 项目名称: eWAP平台
 * 项目地点: 昆明南天电脑系统公司
 * 系统出口函数
 */
package services;

/**
 * @author xgy
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */

import jatools.engine.ReportJob;
import jatools.resources.Messages;
import jatools.server.FileFinder;
import jatools.server.ReportExporter;
import jatools.server.ReportWriter;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.*;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import eWAP.core.IDefIO;
import eWAP.core.Tools;

import eWAP.core.ResourcePool;

import eWAP.core.dbaccess.*;

public class SysExitProcess extends Base {
	ResourcePool req = null;
	int DbsNo;
	ConnectionFactory dbclass = null; // 数据库操作实例
	HttpServletRequest request;
	HttpServletResponse response;
	String DefineID;
	String Name;
	String FormType;
	String checkCode;
	int OPMODE;
	String InstID;
	String UserID;
	String dbType = null;
	String UserLevel = null;
	String inst_no = null;
	String Cancel_Inst = null;

	public SysExitProcess() {
		super();
	}

	public void GetUUIDForFront() {
		req.setTransFlag(0);
		req.setResultObj(UUID.randomUUID().toString());
	}

	public void GetDbs() {
		req.setTransFlag(0);
		req.setResultObj(ResourcePool.getDsConfig());
	}

	@SuppressWarnings("rawtypes")
	public void ChangePwd() {

		String password = req.getParameter("password");
		String Newpassword = req.getParameter("Newpassword");

		// 固定格式，不必修改

		ArrayList tmpv = null;
		String sqlStrC = "";
		String sqlStr = "";
		// int flag;
		String userPwd;

		// 检查上传参数
		sqlStr = "select USER_NAME , USER_PASSWORD from T_MGT_OPERATOR_Y where inst_no = '"
				+ InstID + "' and USER_CODE='" + UserID + "'";
		try {

			tmpv = dbclass.doQuery(sqlStr, 0, 0);

			if (tmpv == null) {
				// 数据库操作失败
				setMsg("密码修改失败，失败原因：数据库操作异常");
				req.setTransFlag(-1);
				req.setTransMsg(getMsg());
			} else {
				Hashtable tmph = (Hashtable) tmpv.get(0);
				userPwd = Tools.trimNull((String) tmph.get("USER_PASSWORD"
						.toUpperCase()));

				// 请求上传密码加密
				String codePass = Tools.cryptMd5Str(password);
				if (codePass != null) {
					codePass = codePass.trim();
				}

				if (((userPwd == null || userPwd.equals("")) && (codePass == null || codePass
						.equals("")))
						|| userPwd.equals(codePass) || userPwd.equals("密码过期")) {

					String pwd = Tools.cryptMd5Str(Newpassword);

					sqlStrC = "update T_MGT_OPERATOR_Y set USER_PASSWORD='"
							+ pwd + "',Passwd_Date=SysDate where inst_no = '"
							+ InstID + "' and USER_CODE='" + UserID + "'";

					boolean updateFlag = dbclass.executeUpdate(sqlStrC);
					if (updateFlag == false) {
						System.out.println("ERROR UPDATE");
						req.setTransFlag(-1);
						setMsg("密码修改失败");
					} else {
						setMsg("密码修改成功");
						req.setTransFlag(0);
						req.setTransMsg(getMsg());
					}
				} else {

					setMsg("密码修改失败，失败原因：原密码不正确");
					req.setTransFlag(-1);
					req.setTransMsg(getMsg());
				}
			}
		} catch (Exception e) {
			System.out.println("修改密码出错：" + e.toString());
			setMsg("密码修改失败，失败原因：数据库操作异常");
			req.setTransFlag(-1);
			req.setTransMsg(getMsg());
		}
		return;
	}

	public void ResetPassword() {
		// 固定格式，不必修改　

		String User_Code = req.getParameter("user_code");
		String Inst_Code = req.getParameter("inst_no");

		String User_Password = "";
		User_Password = Tools.cryptMd5Str("888888");
		String sqlStr = "update T_MGT_OPERATOR_Y set USER_PASSWORD='"
				+ User_Password + "',Passwd_Date=SysDate where inst_no ='"
				+ Inst_Code + "' and USER_CODE ='" + User_Code + "'";
		try {
			if (!dbclass.executeUpdate(sqlStr)) {
				DbSetMsg(dbclass.getMsg());
				req.setTransFlag(-1);
				req.setTransMsg(getMsg());
			} else {
				setMsg("密码重置成功");
				req.setTransFlag(0);
				req.setTransMsg(getMsg());
			}
		} catch (Exception e) {
			setMsg("重置密码失败，失败原因：系统数据操作异常");
			req.setTransFlag(-1);
			req.setTransMsg(getMsg());
		}
		return;
	}

	@SuppressWarnings("rawtypes")
	public void InqMenu()

	{
		String ip = req.getRequestIP();

		StringBuffer returnStrBuff = new StringBuffer("");
		String returnStr = "";
		String tempStr = "";
		ArrayList tmpv = null;
		if (ip == null || !ip.equals("127.0.0.1")) {
			setMsg("无权操作");
			req.setTransFlag(-1);
			req.setTransMsg(getMsg());
			return;
		}
		try {
			String queryStr = "select decode(Flag,'2','    '||Menu_Level,'3','        '||Menu_Level,'4','              '||Menu_Level,Menu_Level) Menu_Level,decode(Flag,'2','    '||Menu_Name,'3','       '||Menu_Name,'4','              '||Menu_Name,Menu_Name) Menu_Name,Menu_Page from (select Menu_Level,Menu_Level Flag,Up_Menu_Code,Menu_Code,Menu_Name,Menu_Page,0 DispId from t_sys_menu union select '-',m.Menu_Level,m.Up_Menu_Code,m.Menu_Code,'       '||Func_Name,'         '||Func_Code,DispId from t_sys_menu m,t_sys_func f where m.Menu_Code=f.Menu_Code) order by Menu_Code,DispId";
			tmpv = dbclass.doQuery(queryStr, -1, -1);
			if (tmpv == null) {
				DbSetMsg("数据库错误:" + dbclass.getMsg());
				req.setTransMsg(getMsg());
				req.setTransFlag(-1);
				req.setResultObj(tempStr);
				return;
			}
			String menuFile = ResourcePool.getRootpath()
					+ "/files/ParaFile/T_SYS_MENU.txt";
			if (tmpv.size() == 0) {
				InputStream is = null;
				BufferedReader br = null;
				is = new BufferedInputStream(new FileInputStream(menuFile));
				br = new BufferedReader(new InputStreamReader(is));
				String tempS = "";
				while ((tempS = br.readLine()) != null) {
					returnStrBuff.append(tempS + "\r\n");
				}
				br.close();
				is.close();
			} else {
				// 处理返回数据
				for (int i = 0; i < tmpv.size(); i++) {
					Hashtable tmph = (Hashtable) tmpv.get(i);
					String Level = tmph
							.get("Menu_Level".toUpperCase())
							+ "	";
					String Name = (String) tmph.get("Menu_Name".toUpperCase());
					String Code = (String) tmph.get("Menu_Page".toUpperCase());
					returnStrBuff.append(Level + " " + Name + " " + Code
							+ "\r\n");
				}
				returnStr = returnStrBuff.toString();
				PrintWriter out = new PrintWriter(new BufferedWriter(
						new FileWriter(menuFile)));
				out.print(returnStr);
				out.close();
			}
			if (returnStr.length() > 0) {
				tempStr = "MenuTxt|=|" + returnStr;
			}
			req.setTransFlag(0);
			req.setResultObj(tempStr);
		} catch (Exception e) {
			e.printStackTrace();
			setMsg("查询出现异常!");
			req.setTransFlag(-1);
			req.setTransMsg(getMsg());
			return;
		}
		return;
	}

	@SuppressWarnings("rawtypes")
	public void LoadMenu() {
		String ip = req.getRequestIP();

		String resultStr = req.getParameter("MenuTxt");// 要更新的结果集信息

		if (resultStr == null || resultStr.equals("")) {
			setMsg("菜单信息为空");
			req.setTransFlag(-1);
			req.setTransMsg(getMsg());
			return;
		}
		resultStr = resultStr.replaceAll("\t", "");
		resultStr = resultStr.replaceAll("\r\n", "\n");
		String[] AllResultInf = resultStr.split("\n");// 存放所有结果信息
		if (ip == null || !ip.equals("127.0.0.1")) {
			setMsg("无权操作");
			req.setTransFlag(-1);
			req.setTransMsg(getMsg());
			return;
		}
		try {
			dbclass.beginTransction();

			String sql = "select func_code,nvl(IsLog,'0') IsLog,nvl(Visible,'1') Visible,nvl(IsCheck,'0') IsCheck from T_SYS_FUNC  where IsLog='1' or Visible='0' or IsCheck='1'";

			ArrayList tmpv = dbclass.doQuery(sql, 0, 0);

			Hashtable<String, String> h_currentLogFuncs = new Hashtable<String, String>();

			for (int i = 0; i < tmpv.size(); i++) {
				Hashtable tmph = (Hashtable) tmpv.get(i);
				String func_id = ((String) tmph.get("func_code".toUpperCase()))
						.trim();
				String IsLog = ((String) tmph.get("IsLog".toUpperCase()))
						.trim();
				String IsCheck = ((String) tmph.get("IsCheck".toUpperCase()))
						.trim();
				String Visible = ((String) tmph.get("Visible".toUpperCase()))
						.trim();
				h_currentLogFuncs.put(func_id, IsLog + Visible + IsCheck);
			}
			// 删除原有数据
			if (!dbclass.executeUpdate("delete from T_SYS_MENU")) {
				setMsg("删除菜单出错");
				req.setTransFlag(-1);
				req.setTransMsg(getMsg());

				return;
			}
			if (!dbclass.executeUpdate("delete from T_SYS_FUNC ")) {
				setMsg("删除菜单出错");
				req.setTransFlag(-1);
				req.setTransMsg(getMsg());

				return;
			}
			int j = 0, sno = 0, k = 0;
			int[] up_m = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
			int new_m = -1;
			String lvlStr = "";
			String pageStr = "";

			for (int i = 0; i < AllResultInf.length; i++) {
				AllResultInf[i] = AllResultInf[i].trim();
				if (AllResultInf[i].equals("")
						|| AllResultInf[i].startsWith("#"))
					continue; // 空行或者注释则忽略
				AllResultInf[i] = AllResultInf[i].replaceAll(" +", " ");
				String[] resultArray = AllResultInf[i].split(" ", -1);
				if (resultArray[1].trim().equalsIgnoreCase(""))
					continue;
				j++;
				if (resultArray.length < 3)
					pageStr = "";
				else
					pageStr = resultArray[2];
				lvlStr = resultArray[0].trim();
				if (lvlStr.equalsIgnoreCase("F") || lvlStr.equals("-")
						|| lvlStr.equals("")) {
					String islog = "0";
					String isCheck = "0";
					String visibale = "1";
					String ttStr = h_currentLogFuncs
							.get(resultArray[2].trim());
					if (ttStr != null) {
						if (ttStr.charAt(0) == '1')
							islog = "1";
						if (ttStr.charAt(1) == '0')
							visibale = "0";
						if (ttStr.charAt(2) == '1')
							isCheck = "1";
					}
					sql = "insert into T_SYS_FUNC values ('" + pageStr + "',"
							+ new_m + ",'" + visibale + "','" + resultArray[1]
							+ "','" + islog + "'," + j + ",'" + isCheck + "')";
				} else {
					new_m = j;
					sno = Integer.parseInt(lvlStr);
					if (sno == 1) {
						for (k = 0; k < 10; k++)
							up_m[k] = -1;
						up_m[1] = j;
						sql = "insert into T_SYS_MENU values (" + j + "," + j
								+ ",'" + lvlStr + "','" + resultArray[1]
								+ "','" + pageStr + "')";
					} else {
						up_m[sno] = j;
						sno = sno - 1;
						while (sno > 0 && up_m[sno] == -1)
							sno--;
						if (sno == 0 || up_m[sno] == -1)
							sql = "insert into T_SYS_MENU values (" + j + ","
									+ j + ",'" + lvlStr + "','"
									+ resultArray[1] + "','" + pageStr + "')";
						else
							sql = "insert into T_SYS_MENU values (" + j + ","
									+ up_m[sno] + ",'" + lvlStr + "','"
									+ resultArray[1] + "','" + pageStr + "')";
					}
				}
				if (!dbclass.executeUpdate(sql)) {

					setMsg("插入数据出错");
					req.setTransFlag(-1);
					req.setTransMsg(getMsg());
					return;
				}
			}
			dbclass
					.executeUpdate("delete from T_ROLE_PRIVATE p where not exists (select 1 from T_SYS_FUNC where Func_Code=p.Private_Code) and Private_Type='2'");
			dbclass
					.executeUpdate("delete from T_USER_PRIVATE p where not exists (select 1 from T_SYS_FUNC where Func_Code=p.Private_Code) and Private_Type='2'");

			setMsg("菜单维护成功");
			req.setTransFlag(0);
			req.setTransMsg(getMsg());
		} catch (Exception e) {
			setMsg("菜单装入出错" + e);
			req.setTransFlag(-1);
			req.setTransMsg(getMsg());

		}
		return;
	}

	@SuppressWarnings("rawtypes")
	public void CreateUser() {
		String ip = req.getRequestIP();

		if (ip == null || !ip.equals("127.0.0.1")) {
			setMsg("无权操作");
			req.setTransFlag(-1);
			req.setTransMsg(getMsg());
			return;

		}

		String tStr = "";
		String user = req.getParameter("User_Code");
		String pass = req.getParameter("Pass");
		pass = Tools.cryptMd5Str(pass);
		try {

			File read = new File(ResourcePool.getRootpath()
					+ "/files/ParaFile/CreateUser.sql");

			BufferedReader br = new BufferedReader(new FileReader(read));
			while (true) {
				String temp = null;
				temp = br.readLine();
				if (temp == null)
					break;
				temp = temp.trim();
				if (temp.equals(""))
					continue;
				if (temp.charAt(0) == '#')
					continue;
				tStr = temp;
			}
			br.close();
		} catch (FileNotFoundException e) { // 文件未找到
			setMsg("创建初始管理用户时出错：文件未找到");
			req.setTransFlag(-1);
			req.setTransMsg(getMsg());
			return;
		} catch (IOException e) {
			setMsg("创建初始管理用户时出错IO例外");
			req.setTransFlag(-1);
			req.setTransMsg(getMsg());
			return;
		}

		tStr = tStr.replaceFirst("USER_CODE", user);
		tStr = tStr.replaceFirst("PASSWORD", pass);
		String sqlStr = "select user_code from T_MGT_OPERATOR_Y where Inst_No ='0000' and user_code ='"
				+ user + "' ";
		ArrayList tmpv = dbclass.doQuery(sqlStr, 0, 0);
		if (tmpv.size() > 0) {
			setMsg("初始管理用户已存在");
			req.setTransFlag(-1);
			req.setTransMsg(getMsg());
			return;
		}
		sqlStr = tStr;

		if (!dbclass.executeUpdate(sqlStr)) {
			setMsg("插入数据出错");
			req.setTransFlag(-1);
			req.setTransMsg(getMsg());
			return;
		} else {
			setMsg("创建初始管理用户成功");
			req.setTransFlag(0);
			req.setTransMsg(getMsg());
			return;
		}

	}

	@SuppressWarnings("rawtypes")
	public void GetNoticeList() {
		ArrayList tmpv = null;
		String sqlStr = "";
		String returnStr = "|";

		final String USE_FLAG = "1"; // 使用标志的，有效(需要和数据库一致)
		// 要存到的变量名
		String caption = "hidden_notice|=";
		String tempStr = caption + returnStr;

		/* 忽略权限检查 */

		try {
			sqlStr = "select * from T_NOTICE where USE_FLAG='" + USE_FLAG
					+ "' order by CREATE_DATE DESC";
			tmpv = dbclass.doQuery(sqlStr, 0, 0);

			if (tmpv != null && tmpv.size() != 0) {
				for (int i = 0; i < tmpv.size(); i++) {
					Hashtable tmph = (Hashtable) tmpv.get(i);
					String TITLE = ((String) tmph.get("TITLE".toUpperCase()))
							.trim();
					String NOTICE = ((String) tmph.get("NOTICE".toUpperCase()))
							.trim();
					String IMPORTANT_FLAG = ((String) tmph.get("IMPORTANT_FLAG"
							.toUpperCase())).trim();
					String CREATE_DATE = ((String) tmph.get("CREATE_DATE"
							.toUpperCase())).trim();

					returnStr = returnStr + TITLE + "|#|" + NOTICE + "|#|"
							+ IMPORTANT_FLAG + "|#|" + CREATE_DATE;
					returnStr = returnStr + "|||"; // 使用3条竖线隔开不同公告
				}

				if (returnStr.endsWith("|||")) {
					returnStr = returnStr.substring(0, returnStr.length() - 3);
				}

				tempStr = caption + returnStr;
			}

		} catch (Exception e) {
		}
		req.setTransFlag(0);
		req.setResultObj(tempStr);
		return;
	}

	public void SetFunc() {
		String Flag = req.getParameter("Flag");
		String funcID = req.getParameter("funcID");

		// 返回结果类

		try {
			String sqlStr = "";
			switch (Flag.charAt(0)) {
			case '1':
				sqlStr = "update T_SYS_FUNC set Visible=case when Func_Code in ("
						+ funcID + ") then '1' else '0' end";
				break;
			case '2':
				sqlStr = "update T_SYS_FUNC set IsLog=case when Func_Code in ("
						+ funcID + ") then '1' else '0' end";
				break;
			case '3':
				sqlStr = "update T_SYS_FUNC set IsCheck=case when Func_Code in ("
						+ funcID + ") then '1' else '0' end";
				break;
			}
			if (!dbclass.executeUpdate(sqlStr)) {
				req.setTransFlag(-1);
				req.setTransMsg("更新失败!");

				return;
			}
			req.setTransFlag(0);
			req.setTransMsg("设置标志成功!");
		} catch (Exception e) {
			req.setTransFlag(-1);
			req.setTransMsg("设置用户权限标志时发生异常!");
		}
		return;
	}

	@SuppressWarnings("rawtypes")
	public void GetFunc() {

		String Flag = req.getParameter("Flag");
		try {
			Hashtable<String, String> h_currentUserMenu = new Hashtable<String, String>();
			String sql = "";
			switch (Flag.charAt(0)) {
			case '1':
				sql = "select distinct Menu_Code from T_SYS_FUNC f where Visible='1'";
				break;
			case '2':
				sql = "select distinct Menu_Code from T_SYS_FUNC f where IsLog='1'";
				break;
			case '3':
				sql = "select distinct Menu_Code from T_SYS_FUNC f where IsCheck='1'";
				break;
			}
			String funcList = "";
			while (true) {
				ArrayList tmpv = dbclass.doQuery(sql, 0, 0);
				funcList = "";
				if (tmpv.size() <= 0)
					break;
				for (int i = 0; i < tmpv.size(); i++) {
					Hashtable tmph = (Hashtable) tmpv.get(i);
					String menu_id = Tools.trimNull((String) tmph
							.get("Menu_Code".toUpperCase()));
					if (h_currentUserMenu.get(menu_id) == null) {
						h_currentUserMenu.put(menu_id, "1");
						if (funcList.equals(""))
							funcList = "'" + menu_id + "'";
						else
							funcList = funcList + ",'" + menu_id + "'";
					}
				}
				if (funcList.equals(""))
					break;
				sql = "select distinct Up_Menu_Code Menu_Code "
						+ "from T_SYS_MENU "
						+ "where trim(Menu_Code)<>trim(Up_Menu_Code) and Menu_Code in ("
						+ funcList + ") ";
			}
			switch (Flag.charAt(0)) {
			case '1':
				sql = "select Menu_Code,Up_Menu_Code,Menu_Name,'-' Func_Code,'' cc from T_SYS_MENU union "
						+ "select DispId,Menu_Code,Func_Name,Func_Code,Visible from T_SYS_FUNC "
						+ "order by Menu_Code";
				break;
			case '2':
				sql = "select Menu_Code,Up_Menu_Code,Menu_Name,'-' Func_Code,'' cc from T_SYS_MENU union "
						+ "select DispId,Menu_Code,Func_Name,Func_Code,IsLog from T_SYS_FUNC "
						+ "order by Menu_Code";
				break;
			case '3':
				sql = "select Menu_Code,Up_Menu_Code,Menu_Name,'-' Func_Code,'' cc from T_SYS_MENU union "
						+ "select DispId,Menu_Code,Func_Name,Func_Code,IsCheck from T_SYS_FUNC "
						+ "order by Menu_Code";
				break;
			}

			ArrayList tmpv = dbclass.doQuery(sql, 0, 0);

			if (tmpv == null || tmpv.size() == 0) {
				DbSetMsg(dbclass.getMsg());
				req.setTransFlag(-1);
				req.setTransMsg("检索功能错误，请与系统管理员联系！");
				return;
			}
			String returnStr = "func|=|";
			boolean next1 = false;
			boolean next2 = false;
			String func_code1 = "";
			String func_code2 = "";
			for (int i = 0; i < tmpv.size(); i++) {
				Hashtable tmph = (Hashtable) tmpv.get(i);
				String menu_code = Tools.trimNull((String) tmph.get("Menu_Code"
						.toUpperCase()));
				String up_menu_code = Tools.trimNull((String) tmph
						.get("Up_Menu_Code".toUpperCase()));
				String menu_name = Tools.trimNull((String) tmph.get("Menu_Name"
						.toUpperCase()));
				String func_code = Tools.trimNull((String) tmph.get("Func_Code"
						.toUpperCase()));
				String check = Tools.trimNull((String) tmph.get("cc"
						.toUpperCase()));
				if (check != null && check.equals("1"))
					check = "checked";
				else
					check = " ";
				if (h_currentUserMenu.get(menu_code) != null)
					check = "checked";

				if (func_code.equals("-")) {
					next1 = false;
					next2 = false;
					if (i + 1 < tmpv.size()) {
						tmph = (Hashtable) tmpv.get(i + 1);
						func_code1 = Tools.trimNull((String) tmph
								.get("Func_Code".toUpperCase()));
						if (!func_code1.equals("-"))
							next1 = true;
					}
					if (i + 2 < tmpv.size() && next1) {
						tmph = (Hashtable) tmpv.get(i + 2);
						func_code2 = Tools.trimNull((String) tmph
								.get("Func_Code".toUpperCase()));
						if (func_code2.equals("-"))
							next2 = true;
					}
				}
				if (next1 && next2) {
					returnStr = returnStr + menu_code.trim() + "|"
							+ up_menu_code.trim() + "|" + menu_name.trim()
							+ "|" + func_code1.trim() + "|" + check.trim()
							+ "|#|";
					i++;
				} else
					returnStr = returnStr + menu_code.trim() + "|"
							+ up_menu_code.trim() + "|" + menu_name.trim()
							+ "|" + func_code.trim() + "|" + check.trim()
							+ "|#|";
			}
			if (returnStr.endsWith("|#|"))
				returnStr = returnStr.substring(0, returnStr.length() - 3);
			req.setTransFlag(0);
			req.setResultObj(returnStr);
			return;
		} catch (Exception e) {
			setMsg("下载功能列表时发生异常, 请联系技术人员!");
			req.setTransFlag(-1);
			req.setTransMsg(getMsg());
		}
		return;
	}

	@SuppressWarnings("rawtypes")
	public void getUserMenus() {

		String Mode = req.getParameter("Mode");
		String funcList = "";
		String sql = "";
		Hashtable<String, String> h_currentUserMenu = new Hashtable<String, String>();

		try {
			if (!(InstID.equals("0000") && UserID.equals("Admin"))) {
				sql = "select Menu_Code,Func_Code,(case when  IsCheck<>'1' then 0 else 1 end)+(case when Private_Code is null then 0 else -1 end) flag "
						+ "from (select * from T_SYS_FUNC where Visible='1') f left join ";
				sql += "(select Private_Code from T_USER_PRIVATE where Private_Type='2' and Inst_No='"
						+ InstID + "' and User_Code='" + UserID + "' ";
				sql = sql
						+ "union "
						+ "select a.PRIVATE_Code from T_ROLE_PRIVATE a,T_USER_PRIVATE b where to_char(a.Role_Code)=b.Private_Code and b.Private_Type='1' and a.Private_Type='2' and b.Inst_No='"
						+ InstID + "' and b.User_Code='" + UserID + "'";
				sql = sql
						+ ") p on f.Func_Code=p.Private_Code order by Menu_Code,flag";

				String menuList = "";
				String menuCode = "";
				boolean first = true;
				while (true) {
					int sum = 0;
					ArrayList tmpv = dbclass.doQuery(sql, 0, 0);
					sum = tmpv.size();
					if (sum <= 0)
						break;
					menuList = "";
					funcList = "";
					for (int i = 0; i <= sum; i++) {
						Hashtable tmph;
						String flag = "";
						String func = null;
						String menu_id = "";
						if (i < sum) {
							tmph = (Hashtable) tmpv.get(i);
							menu_id = Tools.trimNull((String) tmph
									.get("Menu_Code".toUpperCase()));
							if (first) {
								flag = Tools.trimNull((String) tmph.get("flag"
										.toUpperCase()));
								func = Tools.trimNull((String) tmph
										.get("Func_Code".toUpperCase()));
								if (flag.equals("1")) {
									if (!menu_id.equals(menuCode))
										continue;
									funcList += "@" + func;
								}
							}
						}
						if (!menuCode.equals("") && !menuCode.equals(menu_id)) {
							String tStr = "";
							if (!funcList.equals(""))
								tStr += funcList + "@";
							h_currentUserMenu.put(menuCode, tStr);
							if (menuList.equals(""))
								menuList = "'" + menuCode + "'";
							else
								menuList = menuList + ",'" + menuCode + "'";
							funcList = "";
						}
						menuCode = menu_id;
					}
					if (menuList.equals(""))
						break;
					sql = "select distinct Up_Menu_Code Menu_Code "
							+ "from T_SYS_MENU "
							+ "where trim(Menu_Code)<>trim(Up_Menu_Code) and Menu_Code in ("
							+ menuList + ") ";
					first = false;
				}

			}
			sql = "select distinct m.Menu_Code MAIN_MENU_CODE,"
					+ "m.Menu_Name MAIN_MENU_NAME,"
					+ "case when m.Menu_Page is null then '' else m.Menu_Page end  MAIN_MENU_PAGE,"
					+ "m.Up_Menu_Code MAIN_UP_MENU_CODE,"
					+ "m.Menu_Level,c.color Color "
					+ "from T_SYS_MENU m left join T_USERMENU_COLOR c on m.menu_name=c.menu_name and Inst_No='"
					+ InstID + "' and User_Code='" + UserID + "'"
					+ "order by m.Menu_Code";
			ArrayList tmpv = dbclass.doQuery(sql, 0, 0);

			String returnStr = "menus|=|";
			for (int i = 0; i < tmpv.size(); i++) {

				Hashtable tmph = (Hashtable) tmpv.get(i);
				String menu_code = Tools.trimNull((String) tmph
						.get("MAIN_MENU_CODE".toUpperCase()));
				if (!(InstID.equals("0000") && UserID.equals("Admin"))) {
					funcList = h_currentUserMenu.get(menu_code);
					if (h_currentUserMenu.get(menu_code) == null)
						continue;
				}

				String menu_name = Tools.trimNull((String) tmph
						.get("MAIN_MENU_NAME".toUpperCase()));
				String menu_page = Tools.trimNull((String) tmph
						.get("MAIN_MENU_PAGE".toUpperCase()));
				String up_menu_code = Tools.trimNull((String) tmph
						.get("MAIN_UP_MENU_CODE".toUpperCase()));
				String menulevel = Tools.trimNull((String) tmph
						.get("Menu_Level".toUpperCase()));
				String menu_color = Tools.trimNull((String) tmph.get("Color"
						.toUpperCase()));
				if (Mode != null && Mode.equals("Color")) {
					switch (Integer.parseInt(menulevel)) {
					case 2:
						menu_name = "  " + menu_name;
						break;
					case 3:
						menu_name = "    " + menu_name;
						break;
					case 4:
						menu_name = "      " + menu_name;
						break;
					case 5:
						menu_name = "        " + menu_name;
						break;
					case 6:
						menu_name = "          " + menu_name;
						break;
					case 7:
						menu_name = "            " + menu_name;
						break;
					}
					returnStr = returnStr + menu_code.trim() + "|#|"
							+ menu_name + "|#|" + menu_color.trim() + "|#|";
				} else {
					returnStr = returnStr + menu_code.trim() + "|"
							+ up_menu_code.trim() + "|" + menu_name.trim()
							+ "|" + menu_page.trim() + "|" + menu_color.trim()
							+ "|" + menulevel.trim() + "|" + funcList + "|#|";
				}
			}
			// Pub.writeDebugLog(LogType.DEBUG, "LoginTrans.getUserMenus()",
			// "返回的数据:" + returnStr.toString(), userId);
			if (returnStr.endsWith("|#|"))
				returnStr = returnStr.substring(0, returnStr.length() - 3);
			req.setTransFlag(0);
			req.setResultObj(returnStr);
			return;

		} catch (Exception e) {
			setMsg("下载菜单参数时发生异常, 请联系技术人员!");
			req.setTransFlag(-1);
			req.setTransMsg(getMsg());
		}
		return;
	}

	@SuppressWarnings("rawtypes")
	public void Role_Add()

	{

		String RoleName = req.getParameter("RoleName");

		ArrayList tmpv = null;
		String sqlStr = "";
		String Success = "No|=|";
		int no = 0;
		try {

			tmpv = dbclass.doQuery(
					"select max(Role_Code)  as Role_Code from T_SYS_ROLE ", 0,
					0);

			if (tmpv.size() > 0) {
				Hashtable tmph = (Hashtable) tmpv.get(0);

				no = Integer.parseInt(Tools.trimNull((String) tmph
						.get("Role_Code".toUpperCase())));
			}
		} catch (Exception e) {
			no = 0;
		}
		no += 1;
		sqlStr = " insert into T_SYS_ROLE values(" + no + ",'" + RoleName
				+ "')";
		if (!dbclass.executeUpdate(sqlStr)) {
			req.setTransFlag(-1);
			req.setTransMsg("新增角色时出错！");
			return;
		} else {
			req.setTransFlag(0);
			req.setTransMsg("新增角色时成功");
			req.setResultObj(Success + no);
			return;
		}

	}

	public void Role_Delete() {
		String No = req.getParameter("No");
		/* 建立公共信息，不必修改 */

		String sqlStr = "";
		sqlStr = " delete from T_SYS_ROLE where Role_Code = " + No;
		if (!dbclass.executeUpdate(sqlStr)) {
			DbSetMsg(dbclass.getMsg());
			req.setTransFlag(-1);
			req.setTransMsg(getMsg());
		} else {
			String FlagStr = "JumpFlag|=|1";
			req.setTransFlag(0);
			req.setTransMsg("删除成功!");
			req.setResultObj(FlagStr);
		}
		sqlStr = " delete from T_ROLE_PRIVATE where Role_Code = " + No;
		dbclass.executeUpdate(sqlStr);
		sqlStr = " delete from T_USER_PRIVATE where Private_Type='1' and Private_Code = '"
				+ No + "'";
		dbclass.executeUpdate(sqlStr);
		return;
	}

	/**
	 * Method：public void userAdd(String user_code,String user_name,String
	 * inst_no,String user_status,String user_level,String user_major,String
	 * auth_user,String TransactionName, String InstID, String UserID) Function:
	 * 新增用户 输入参数： String user_code： 输入的用户编号 String user_name： 输入的用户姓名 String
	 * inst_no： 输入的机构号 String user_status： 输入的用户状态 1-启用 2—注销 String user_level：
	 * 输入的用户级别 1-营业部级；2-支行级；3-网点级 String user_major： 输入的用户专业 * String
	 * TransactionName：交易名 String InstID： 当前用户机构编号，Session变量中 String UserID：
	 * 当前用户机构编号，Session变量中
	 * 
	 * 输出参数:新增用户是否成功
	 * 
	 */
	public void User_Add() {
		String user_code = req.getParameter("user_code");
		String user_name = req.getParameter("user_name");
		String inst_no = req.getParameter("inst_no");
		String user_status = req.getParameter("user_status");
		String user_level = req.getParameter("user_level");
		String user_major = req.getParameter("user_major");
		String auth_user = req.getParameter("auth_user");
		String sflag = req.getParameter("sflag");
		String user_ip = req.getParameter("user_ip");
		String inst_flag = req.getParameter("inst_flag");

		String sqlStr = "";

		String User_Password = Tools.cryptMd5Str("888888");
		sqlStr = "insert into T_MGT_OPERATOR_Y " + " values('" + inst_no
				+ "','" + user_code + "', '" + user_name + "', '"
				+ User_Password + "' , '" + user_status + "', '" + user_level
				+ "', '" + user_major + "','" + auth_user + "',SysDate,'"
				+ sflag + "','" + user_ip + "','" + inst_flag + "')";
		if (!dbclass.executeUpdate(sqlStr)) {
			DbSetMsg(dbclass.getMsg());
			req.setTransFlag(-1);
			req.setTransMsg(getMsg());
		} else {
			setMsg("增加用户成功");
			req.setTransFlag(0);
			req.setTransMsg(getMsg());
		}
		return;
	}

	/**
	 * Method：public void userUpdate(String user_code,String user_name,String
	 * inst_no,String user_status,String user_level,String user_major, String
	 * auth_user,String TransactionName, String InstID, String UserID) Function:
	 * 修改用户信息 输入参数： String user_code： 输入的用户编号 String user_name： 输入的用户姓名 String
	 * inst_no： 输入的机构号 String user_status： 输入的用户状态 1-启用 2—注销 String user_level：
	 * 输入的用户级别 1-营业部级；2-支行级；3-网点级 String user_major： 输入的用户专业 String
	 * TransactionName：交易名 String InstID： 当前用户机构编号，Session变量中 String UserID：
	 * 当前用户机构编号，Session变量中
	 * 
	 * 
	 * 输出参数:修改用户是否成功
	 * 
	 */
	public void User_Update() {
		String user_code = req.getParameter("user_code");
		String user_name = req.getParameter("user_name");
		String inst_no = req.getParameter("inst_no");
		String user_status = req.getParameter("user_status");
		String user_level = req.getParameter("user_level");
		String user_major = req.getParameter("user_major");
		String auth_user = req.getParameter("auth_user");
		String old_inst = req.getParameter("old_inst");
		String sflag = req.getParameter("sflag");
		String user_ip = req.getParameter("user_ip");
		String inst_flag = req.getParameter("inst_flag");

		/* 固定格式，不必修改 */

		String returnStr = "|";
		String titleStr = "";
		String sqlStr = "";
		try {
			dbclass.beginTransction();
			sqlStr = "update T_MGT_OPERATOR_Y set  INST_NO ='" + inst_no
					+ "' , USER_NAME ='" + user_name + "' , USER_STATUS ='"
					+ user_status + "' , USER_LEVEL ='" + user_level
					+ "' , USER_MAJOR ='" + user_major + "' , Auth_User ='"
					+ auth_user + "',Specil_Flag ='" + sflag + "',User_IP ='"
					+ user_ip + "',Inst_Flag ='" + inst_flag
					+ "' where inst_no='" + old_inst + "' and user_code = '"
					+ user_code + "'";

			dbclass.executeUpdate(sqlStr);
			if (!inst_no.equals(old_inst)) {
				sqlStr = "update t_user_private set  INST_NO ='" + inst_no
						+ "' where inst_no='" + old_inst
						+ "' and user_code = '" + user_code + "'";
				dbclass.executeUpdate(sqlStr);
			}
			returnStr = returnStr + user_code;
			titleStr = "user_code|=" + returnStr;
			setMsg("用户修改成功");
			req.setTransFlag(0);
			req.setTransMsg(getMsg());
			req.setResultObj(titleStr);

		} catch (Exception e) {

			setMsg("用户修改失败，失败原因：数据库操作异常");
			req.setTransFlag(-1);
			req.setTransMsg(getMsg());
		}
		return;
	}

	/**
	 * Method： public void userDelete (String inst_no, String user_code,String
	 * TransactionName, String InstID, String UserID) Function: 删除用户 输入参数：
	 * String inst_no： 列表信息传入的用户编号 String user_code： 列表信息传入的用户编号 String
	 * TransactionName：交易名 String InstID： 当前用户机构编号，Session变量中 String UserID：
	 * 当前用户机构编号，Session变量中 输出参数: 删除用户是否成功
	 * 
	 */
	public void User_Delete() {
		String inst_no = req.getParameter("inst_no");
		String user_code = req.getParameter("user_code");

		String sqlStr = "";
		/*
		 * 根据机构编号(Session Var),操作员编号 (Session Var),查询操作员角色 进行权限控制（采用权限控制函数）
		 */
		try {
			/* 业务逻辑 */
			dbclass.beginTransction();
			sqlStr = "delete from T_USER_PRIVATE where inst_no='" + inst_no
					+ "' and  user_code = '" + user_code + "'";
			dbclass.executeUpdate(sqlStr);

			sqlStr = "delete from t_mgt_operator_y where inst_no='" + inst_no
					+ "' and  inst_no ='" + inst_no + "' and user_code = '"
					+ user_code + "'";
			dbclass.executeUpdate(sqlStr);

			String FlagStr = "JumpFlag|=|1";
			req.setTransFlag(0);
			req.setTransMsg("用户删除成功!");
			req.setResultObj(FlagStr);

		} catch (Exception e) {

			setMsg("用户删除失败，失败原因：数据库操作异常");
			req.setTransFlag(-1);
			req.setTransMsg(getMsg());
		}
		return;
	}

	public void SetRoleUser() {
		String addStr = req.getParameter("addStr");
		String delStr = req.getParameter("delStr");
		String Role = req.getParameter("Role");
		String DelSql = "";
		String AddSql = "";

		try {
			dbclass.beginTransction();
			if (delStr != null && !delStr.equals("")) {
				DelSql = "delete from T_USER_PRIVATE where Private_Type='1' and Private_Code='"
						+ Role
						+ "' and trim(Inst_No)||'_'||trim(User_Code) in ("
						+ delStr + ")";
				if (!dbclass.executeUpdate(DelSql)) {
					DbSetMsg(dbclass.getMsg());
					req.setTransFlag(-1);
					req.setTransMsg(getMsg());

					return;
				}
			}
			if (addStr != null && !addStr.equals("")) {
				AddSql = "insert into T_USER_PRIVATE "
						+ "select Inst_No,User_Code,'" + Role + "','1' "
						+ "from T_MGT_OPERATOR_Y  "
						+ "where trim(Inst_No)||'_'||trim(User_Code) in ("
						+ addStr + ")";

				if (!dbclass.executeUpdate(AddSql)) {
					DbSetMsg(dbclass.getMsg());
					req.setTransFlag(-1);
					req.setTransMsg(getMsg());

					return;
				}
			}
			setMsg("设置用户角色成功");
			req.setTransFlag(0);
			req.setTransMsg(getMsg());

		} catch (Exception e) {
			DbSetMsg(dbclass.getMsg());
			req.setTransFlag(-1);
			req.setTransMsg(getMsg());

		}

		return;
	}

	public void SetUserRole() {
		String addStr = req.getParameter("addStr");
		String delStr = req.getParameter("delStr");
		String inst_no = req.getParameter("inst_no");
		String user_code = req.getParameter("user_code");
		String DelSql = "";
		String AddSql = "";

		try {
			dbclass.beginTransction();
			if (delStr != null && !delStr.equals("")) {
				DelSql = "delete from T_USER_PRIVATE where Private_Type='1' and Inst_No ='"
						+ inst_no
						+ "' and USER_CODE ='"
						+ user_code
						+ "' and Private_Code in (" + delStr + ")";
				dbclass.executeUpdate(DelSql);
			}

			if (addStr != null && !addStr.equals("")) {

				AddSql = "insert into T_USER_PRIVATE select Inst_No,User_Code,to_char(Role_Code),'1' from T_SYS_ROLE a,T_MGT_OPERATOR_Y b where b.Inst_No='"
						+ inst_no
						+ "' and b.User_Code='"
						+ user_code
						+ "' and a.Role_Code in (" + addStr + ")";

				if (!dbclass.executeUpdate(AddSql)) {
					DbSetMsg(dbclass.getMsg());
					req.setTransFlag(-1);
					req.setTransMsg(getMsg());

					return;
				}
			}
			setMsg("设置用户分组成功");
			req.setTransFlag(0);
			req.setTransMsg(getMsg());

		} catch (Exception e) {
			DbSetMsg(dbclass.getMsg());
			req.setTransFlag(-1);
			req.setTransMsg(getMsg());

		}

		return;
	}

	@SuppressWarnings("rawtypes")
	public void getFuncScreen() {

		String sele = req.getParameter("ProcessNo");
		String Para1 = req.getParameter("Para1");
		String Para2 = req.getParameter("Para2");

		String sql = "select Menu_Code,Func_Code,(case when Private_Code is null then '0' else '1' end) flag from ( select Menu_Code,Func_Code from T_SYS_FUNC where Visible='1' and IsCheck='1') f left join ";
		switch (Integer.parseInt(sele)) {
		case 1:
			sql += "t_role_private p on f.Func_Code=p.Private_Code  and Private_Type='2' and role_code="
					+ Para1;
			break;
		case 2:
			sql += "(select Private_code from t_user_private where Private_Type='2' and Inst_No ='"
					+ Para1
					+ "' and user_code='"
					+ Para2
					+ "'"
					+ " union "
					+ "select b.Private_Code from T_ROLE_PRIVATE b,T_USER_PRIVATE c where to_char(b.Role_Code)=c.Private_Code and b.Private_Type='2' and c.Inst_No='"
					+ Para1
					+ "' and c.User_Code='"
					+ Para2
					+ "' and c.Private_Type='1') p on f.Func_Code=p.Private_Code";
			break;
		}
		sql += " order by Menu_Code,Func_Code";
		String menuList = "";
		boolean first = true;
		// 过滤重复权限项
		Hashtable<String, String> h_currentUserFuncs = new Hashtable<String, String>();
		ArrayList tmpv;
		String menucode = "";
		while (true) {
			tmpv = dbclass.doQuery(sql, 0, 0);
			int sum = tmpv.size();
			menuList = "";
			String mflag = "0";
			if (sum <= 0)
				break;
			for (int i = 0; i <= sum; i++) {
				Hashtable tmph;
				String menu_id = "";
				String func_id = "";
				String flag = "";
				String ischeck;
				if (i < sum) {
					tmph = (Hashtable) tmpv.get(i);
					menu_id = Tools.trimNull((String) tmph.get("Menu_Code"
							.toUpperCase()));
					func_id = Tools.trimNull((String) tmph.get("Func_Code"
							.toUpperCase()));
					flag = Tools.trimNull((String) tmph.get("flag"
							.toUpperCase()));
					if (first) {
						h_currentUserFuncs.put(func_id, flag);
						if (flag.equals("1"))
							mflag = "1";
					}
				}
				ischeck = h_currentUserFuncs.get(func_id);
				if (ischeck != null && ischeck.equals("1"))
					mflag = "1";

				if (!menucode.equals("") && !menucode.equals(menu_id)) {
					h_currentUserFuncs.put(menucode, mflag);
					mflag = "0";
					if (menuList.equals(""))
						menuList = "'" + menucode + "'";
					else
						menuList = menuList + ",'" + menucode + "'";
				}
				menucode = menu_id;
			}
			sql = "select distinct Up_Menu_Code Menu_Code,Menu_Code Func_Code,'0' flag "
					+ "from T_SYS_MENU "
					+ "where trim(Menu_Code)<>trim(Up_Menu_Code) and Menu_Code in ("
					+ menuList + ") ";
			menuList = "";
			first = false;
		}
		sql = "select  a.Menu_Code MAIN_MENU_CODE,a.Menu_Name MAIN_MENU_NAME, a.Up_Menu_Code UP_MENU_CODE, b.FUNC_CODE, b.FUNC_NAME "
				+ "from T_SYS_MENU a left outer join ( select * from T_SYS_FUNC where Visible='1' and IsCheck='1') b on a.Menu_Code=b.Menu_Code "
				+ "order by a.Menu_Code,b.DispId";

		tmpv = dbclass.doQuery(sql, 0, 0);

		if (tmpv == null) {
			DbSetMsg(dbclass.getMsg());
			req.setTransFlag(-1);
			req.setTransMsg("检索权限错误，请与系统管理员联系！");
			return;
		}

		Vector<Hashtable> v_allFuncs = new Vector<Hashtable>();
		Hashtable<String, String> h_parentIds = new Hashtable<String, String>();

		String pro_func_id = "";
		for (int i = 0; i < tmpv.size(); i++) {
			Hashtable tmph = (Hashtable) tmpv.get(i);
			String menu_code = Tools.trimNull((String) tmph
					.get("MAIN_MENU_CODE".toUpperCase()));
			String func_code = Tools.trimNull((String) tmph.get("FUNC_CODE"
					.toUpperCase()));
			String up_menu_code = Tools.trimNull((String) tmph
					.get("UP_MENU_CODE".toUpperCase()));
			String ischeck;
			if (func_code.equals(""))
				ischeck = h_currentUserFuncs.get(menu_code);
			else
				ischeck = h_currentUserFuncs.get(func_code);
			if (ischeck == null)
				continue;
			String menu_func_code = menu_code + "|" + func_code;
			if (!menu_func_code.equals(pro_func_id)) {

				v_allFuncs.addElement(tmph);
				if (!(!func_code.equals("") && menu_code.equals(up_menu_code)))
					h_parentIds.put(up_menu_code, "1");
			}
			pro_func_id = menu_func_code;
		}

		// 构造返回到前台的Xml对象
		Element funclistElement = new Element("TREE");
		String old_menu_code = "";
		String old_parent_id = "";
		boolean flag = false;
		Element tmpElement = null;

		for (int i = 0; i < v_allFuncs.size(); i++) {
			Hashtable tmph = v_allFuncs.get(i);
			String menu_code = Tools.trimNull((String) tmph
					.get("MAIN_MENU_CODE".toUpperCase()));
			String menu_name = Tools.trimNull((String) tmph
					.get("MAIN_MENU_NAME".toUpperCase()));
			String func_code = Tools.trimNull((String) tmph.get("FUNC_CODE"
					.toUpperCase()));
			String func_name = Tools.trimNull((String) tmph.get("FUNC_NAME"
					.toUpperCase()));
			String up_menu_code = Tools.trimNull((String) tmph
					.get("UP_MENU_CODE".toUpperCase()));

			/* 处理功能属性 */
			if (menu_code.equalsIgnoreCase(old_menu_code) == false) {
				if (flag == true) {
					Element parentElement = Pub.getElementByAttribute(
							funclistElement, "ID", old_parent_id);
					if (parentElement == null)
						funclistElement.addContent(tmpElement);
					else
						parentElement.addContent(tmpElement);
				}

				tmpElement = new Element("FUNC");
				tmpElement.setAttribute("ID", menu_code);
				tmpElement.setAttribute("ENABLED", "TRUE");

				if (h_parentIds.get(menu_code) != null) {
					// 有子权限÷

					tmpElement.setAttribute("TYPE", menu_name);
					tmpElement.setAttribute("folded", "FALSE");
					/* 增加是否选中的标记 */
					String ischeck;
					if (func_code.equals(""))
						ischeck = h_currentUserFuncs.get(menu_code);
					else
						ischeck = h_currentUserFuncs.get(func_code);

					if (ischeck != null && ischeck.equals("1")) {
						tmpElement.setAttribute("CHECKED", "TRUE");
					}

				} else {
					// 无子权限
					tmpElement.setText(menu_name);
					if ((func_code != null)
							&& (func_code.equalsIgnoreCase("") == false)) {
						Element textElement = new Element("PROPERTY");
						textElement.setAttribute("ID", func_code);
						textElement.setAttribute("NAME", func_name);
						textElement.setAttribute("ENABLED", "TRUE");

						String ischeck;
						if (func_code.equals(""))
							ischeck = h_currentUserFuncs.get(menu_code);
						else
							ischeck = h_currentUserFuncs.get(func_code);

						if (ischeck != null && ischeck.equals("1")) {

							tmpElement.setAttribute("CHECKED", "TRUE");
							textElement.setAttribute("CHECKED", "TRUE");
						} else
							textElement.setAttribute("CHECKED", "FALSE");

						tmpElement.addContent(textElement);
					} else {
						String ischeck;
						if (func_code.equals(""))
							ischeck = h_currentUserFuncs.get(menu_code);
						else
							ischeck = h_currentUserFuncs.get(func_code);

						if (ischeck != null && ischeck.equals("1")) {
							tmpElement.setAttribute("CHECKED", "TRUE");
						} else {
							tmpElement.setAttribute("CHECKED", "FALSE");
						}
					}
				}

			} else {
				if (h_parentIds.get(menu_code) == null) {

					if (func_code != null) {
						Element textElement = new Element("PROPERTY");
						textElement.setAttribute("ID", func_code);
						textElement.setAttribute("NAME", func_name);
						textElement.setAttribute("ENABLED", "TRUE");
						String ischeck;
						if (func_code.equals(""))
							ischeck = h_currentUserFuncs.get(menu_code);
						else
							ischeck = h_currentUserFuncs.get(func_code);

						if (ischeck != null && ischeck.equals("1")) {
							tmpElement.setAttribute("CHECKED", "TRUE");
							textElement.setAttribute("CHECKED", "TRUE");
						} else
							textElement.setAttribute("CHECKED", "FALSE");

						tmpElement.addContent(textElement);
					}
				}

			}

			old_menu_code = menu_code;
			flag = true;
			old_parent_id = up_menu_code;
		}
		if (flag == true) {
			Element parentElement = Pub.getElementByAttribute(funclistElement,
					"ID", old_parent_id);
			if (parentElement == null)
				funclistElement.addContent(tmpElement);
			else
				parentElement.addContent(tmpElement);
		}

		Element root = new Element("ROOT");
		root.addContent(funclistElement);

		String xmlStr = new XMLOutputter().outputString(root);
		req.setTransFlag(0);
		req.setResultObj(xmlStr);
		return;
	}

	public void setFuncSele() {
		String sele = req.getParameter("ProcessNo");
		String Para1 = req.getParameter("Para1");
		String Para2 = req.getParameter("Para2");
		String funcID = req.getParameter("funcID");
		String DelFuncID = req.getParameter("DelFuncID");

		// 返回结果类

		try {
			dbclass.beginTransction();

			/* 删除权限 */
			String sqlStr = "";
			int processNo = Integer.parseInt(sele);
			if (DelFuncID != null && !DelFuncID.equals("")) {
				switch (processNo) {
				case 1:
					sqlStr = "delete from T_ROLE_PRIVATE where Role_Code="
							+ Para1
							+ " and Private_Type='2' and Private_code in ("
							+ DelFuncID + ")";
					break;
				case 2:
					if (DelFuncID.equals("#"))
						sqlStr = "delete from t_user_private where Private_Type='2' and Inst_No ='"
								+ Para1 + "' and user_code='" + Para2 + "'";
					else
						sqlStr = "delete from t_user_private where Private_Type='2' and Inst_No ='"
								+ Para1
								+ "' and user_code='"
								+ Para2
								+ "' and Private_code in (" + DelFuncID + ")";
					break;
				}

				// 删除原有数据
				if (!dbclass.executeUpdate(sqlStr)) {
					req.setTransFlag(-1);
					req.setTransMsg("删除权限失败[" + sqlStr + "]!");

					return;
				}
			}
			// 增加新数据
			if (funcID != null && !funcID.equals("")) {
				switch (processNo) {
				case 1:
					sqlStr = "insert into T_ROLE_PRIVATE select "
							+ Para1
							+ ",Func_Code,'2' from T_SYS_FUNC where Func_Code in ("
							+ funcID + ")";
					break;
				case 2:
					sqlStr = "insert into t_user_private select '"
							+ Para1
							+ "','"
							+ Para2
							+ "',Func_Code,'2' from T_SYS_FUNC where Func_Code in ("
							+ funcID
							+ ") and Func_Code not in (select b.Private_Code from T_ROLE_PRIVATE b,T_USER_PRIVATE c where b.Role_Code=to_number(nvl(c.Private_Code,-1)) and b.Private_Type='3' and c.Inst_No='"
							+ Para1 + "' and c.User_Code='" + Para2
							+ "' and c.Private_Type='1')";
					break;
				}
				if (!dbclass.executeUpdate(sqlStr)) {
					req.setTransFlag(-1);
					req.setTransMsg("设置权限失败[" + sqlStr + "]！");

					return;
				}
			}

			req.setTransFlag(0);
			req.setTransMsg("设置权限成功!");
		} catch (Exception e) {

			req.setTransFlag(-1);
			req.setTransMsg("设置权限时发生异常!");
		}
		return;
	}

	public void SetUserGroup_Report() {
		String role_code = req.getParameter("role_code");
		String addStr = req.getParameter("addStr");
		String delStr = req.getParameter("delStr");

		String DelSql = "";
		String AddSql = "";

		try {
			dbclass.beginTransction();
			if (delStr != null && !delStr.equals("")) {
				DelSql = "delete from T_ROLE_PRIVATE  where Role_Code ="
						+ role_code
						+ " and private_type='4' and Private_Code in ("
						+ delStr + ")";
				dbclass.executeUpdate(DelSql);
			}
			if (addStr != null && !addStr.equals("")) {
				AddSql = "insert into T_ROLE_PRIVATE select " + role_code
						+ ",queryno,'4' from t_accquery where queryno in ("
						+ addStr + ")";

				if (!dbclass.executeUpdate(AddSql)) {
					DbSetMsg(dbclass.getMsg());
					req.setTransFlag(-1);
					req.setTransMsg(getMsg());

					return;
				}
			}
			setMsg("设置报表查询范围成功");
			req.setTransFlag(0);
			req.setTransMsg(getMsg());

		} catch (Exception e) {
			DbSetMsg(dbclass.getMsg());
			req.setTransFlag(-1);
			req.setTransMsg(getMsg());

		}
		return;
	}

	public void SetUser_AccountItem() {
		String user_code = req.getParameter("user_code");
		String inst_no = req.getParameter("inst_no");
		String addStr = req.getParameter("addStr");
		String delStr = req.getParameter("delStr");

		String DelSql = "";
		String AddSql = "";

		try {
			dbclass.beginTransction();
			if (delStr != null && !delStr.equals("")) {
				DelSql = "delete from T_USER_PRIVATE  where Inst_No ='"
						+ inst_no + "' and USER_CODE ='" + user_code
						+ "' and Private_Type='3' and Private_Code in ("
						+ delStr + ")";
				dbclass.executeUpdate(DelSql);
			}
			if (addStr != null && !addStr.equals("")) {
				AddSql = "insert into T_USER_PRIVATE select '"
						+ inst_no
						+ "','"
						+ user_code
						+ "',ItemNo,'3' from T_ITEM  a where ItemNo in ("
						+ addStr
						+ ") and not exists (select 'x' from T_ROLE_PRIVATE b,T_USER_PRIVATE c where b.Role_Code=to_number(c.Private_Code) and b.Private_Type='3' and c.Inst_No='"
						+ inst_no
						+ "' and c.User_Code='"
						+ user_code
						+ "' and c.Private_Type='1' and to_number(c.Private_Code)=a.ItemNo)";
				if (!dbclass.executeUpdate(AddSql)) {
					DbSetMsg(dbclass.getMsg());
					req.setTransFlag(-1);
					req.setTransMsg(getMsg());

				}
			}
			setMsg("设置用户“帐户查询范围”成功");
			req.setTransFlag(0);
			req.setTransMsg(getMsg());

		} catch (Exception e) {
			DbSetMsg(dbclass.getMsg());
			req.setTransFlag(-1);
			req.setTransMsg(getMsg());

		}
		return;
	}

	public void SetUser_Report() {
		String user_code = req.getParameter("user_code");
		String inst_no = req.getParameter("inst_no");
		String addStr = req.getParameter("addStr");
		String delStr = req.getParameter("delStr");

		String DelSql = "";
		String AddSql = "";

		try {
			dbclass.beginTransction();
			if (delStr != null && !delStr.equals("")) {
				DelSql = "delete from t_user_private  where Inst_No ='"
						+ inst_no + "' and User_Code ='" + user_code
						+ "' and private_type='4' and Private_Code in("
						+ delStr + ")";
				dbclass.executeUpdate(DelSql);
			}
			if (addStr != null && !addStr.equals("")) {
				AddSql = "insert into t_user_private select '"
						+ inst_no
						+ "','"
						+ user_code
						+ "',queryno,'4' from t_accquery where queryno in ("
						+ addStr
						+ ")  and queryNo not in (select b.Private_Code from T_ROLE_PRIVATE b,T_USER_PRIVATE c where b.Role_Code=to_number(nvl(c.Private_Code,-1)) and b.Private_Type='4' and c.Inst_No='"
						+ inst_no + "' and c.User_Code='" + user_code
						+ "' and c.Private_Type='1' )";

				if (!dbclass.executeUpdate(AddSql)) {
					DbSetMsg(dbclass.getMsg());
					req.setTransFlag(-1);
					req.setTransMsg(getMsg());

					return;
				}
			}
			setMsg("设置用户报表查询范围成功");
			req.setTransFlag(0);
			req.setTransMsg(getMsg());

		} catch (Exception e) {
			DbSetMsg(dbclass.getMsg());
			req.setTransFlag(-1);
			req.setTransMsg(getMsg());

		}
		return;

	}

	public void SetUser_Net() {
		String user_code = req.getParameter("user_code");
		String net_no = req.getParameter("addStr");
		String inst_no = req.getParameter("inst_no");

		String DelStr = "";
		String AddStr = "";
		DelStr = "delete from T_USER_PRIVATE  where Inst_No ='" + inst_no
				+ "' and User_Code ='" + user_code + "' and Private_Type='5'";
		try {
			dbclass.beginTransction();
			dbclass.executeUpdate(DelStr);
			if (!net_no.equals("")) {
				AddStr = "insert into T_USER_PRIVATE select '" + inst_no
						+ "','" + user_code
						+ "',Inst_No,'5' from T_INST_INFO where Inst_No in ("
						+ net_no + ")";

				if (!dbclass.executeUpdate(AddStr)) {
					DbSetMsg(dbclass.getMsg());
					req.setTransFlag(-1);
					req.setTransMsg(getMsg());

					return;
				}
			}
			setMsg("设置用户在活期历史明细查询时的网点范围成功");
			req.setTransFlag(0);
			req.setTransMsg(getMsg());

		} catch (Exception e) {
			DbSetMsg(dbclass.getMsg());
			req.setTransFlag(-1);
			req.setTransMsg(getMsg());

		}
		return;

	}

	public void getConInfo() {

		String ServerIP_Sock = "";
		String ServerPort_Sock = "";
		String BlockTime_Sock = "";

		String ServerIP_Ftp = "";
		String UserID_Ftp = "";
		String PWD_Ftp = "";
		String Path_Ftp = "";

		String returnStr = "|";
		String caption = "";

		/**
		 * 查询系统配置信息
		 */

		try {

			caption = "ServerIP_Sock^ServerPort_Sock^BlockTime_Sock^ServerIP_Ftp^UserID_Ftp^PWD_Ftp^Path_Ftp|=";

			String xmlFile = ResourcePool.getRootpath() + "/config/system.xml";

			ServerIP_Sock = ResourcePool.GetConfigInfo(xmlFile, "SockInfo",
					"ServerIP")[0].trim();
			ServerPort_Sock = ResourcePool.GetConfigInfo(xmlFile, "SockInfo",
					"ServerPort")[0].trim();
			BlockTime_Sock = ResourcePool.GetConfigInfo(xmlFile, "SockInfo",
					"BlockTime")[0].trim();

			ServerIP_Ftp = ResourcePool.GetConfigInfo(xmlFile, "FtpInfo",
					"ServerIP")[0].trim();
			UserID_Ftp = ResourcePool.GetConfigInfo(xmlFile, "FtpInfo",
					"UserID")[0].trim();
			PWD_Ftp = ResourcePool.GetConfigInfo(xmlFile, "FtpInfo", "PWD")[0]
					.trim();
			Path_Ftp = ResourcePool.GetConfigInfo(xmlFile, "FtpInfo", "Path")[0]
					.trim();

			returnStr = returnStr + ServerIP_Sock + "^" + ServerPort_Sock + "^"
					+ BlockTime_Sock + "^" + ServerIP_Ftp + "^" + UserID_Ftp
					+ "^" + PWD_Ftp + "^" + Path_Ftp;

			returnStr = caption + returnStr;
			req.setTransFlag(0);
			req.setResultObj(returnStr);

		} catch (Exception e) {

			setMsg("查询系统通信参数配置信息失败");
			req.setTransFlag(-1);
			req.setTransMsg(getMsg());

		}
		return;
	}

	/**
	 * Method：更新系统通信参数配置信息
	 * 
	 * @param TransactionName
	 *            交易名
	 * @param InstID
	 *            用户机构ID
	 * @param UserID
	 *            用户ID
	 * @return void
	 */

	public void updateConInfo() {
		String ServerIP_Sock = req.getParameter("ServerIP_Sock");
		String ServerPort_Sock = req.getParameter("ServerPort_Sock");
		String BlockTime_Sock = req.getParameter("BlockTime_Sock");
		String ServerIP_Ftp = req.getParameter("ServerIP_Ftp");
		String UserID_Ftp = req.getParameter("UserID_Ftp");
		String PWD_Ftp = req.getParameter("PWD_Ftp");
		String Path_Ftp = req.getParameter("Path_Ftp");

		/**
		 * 更新系统配置信息
		 */

		try {

			String xmlFile = ResourcePool.getRootpath() + "/config/system.xml";

			if (!ResourcePool.EditConfigInfo(xmlFile, "SockInfo", "ServerIP",
					ServerIP_Sock, 0)) {

				setMsg("更新系统通信参数配置信息失败");
				req.setTransFlag(-1);
				req.setTransMsg(getMsg());
				return;

			}
			if (!ResourcePool.EditConfigInfo(xmlFile, "SockInfo", "ServerPort",
					ServerPort_Sock, 0)) {

				setMsg("更新系统通信参数配置信息失败");
				req.setTransFlag(-1);
				req.setTransMsg(getMsg());
				return;

			}
			if (!ResourcePool.EditConfigInfo(xmlFile, "SockInfo", "BlockTime",
					BlockTime_Sock, 0)) {

				setMsg("更新系统通信参数配置信息失败");
				req.setTransFlag(-1);
				req.setTransMsg(getMsg());
				return;

			}

			if (!ResourcePool.EditConfigInfo(xmlFile, "FtpInfo", "ServerIP",
					ServerIP_Ftp, 0)) {

				setMsg("更新系统通信参数配置信息失败");
				req.setTransFlag(-1);
				req.setTransMsg(getMsg());
				return;

			}
			if (!ResourcePool.EditConfigInfo(xmlFile, "FtpInfo", "UserID",
					UserID_Ftp, 0)) {

				setMsg("更新系统通信参数配置信息失败");
				req.setTransFlag(-1);
				req.setTransMsg(getMsg());
				return;

			}
			if (!ResourcePool.EditConfigInfo(xmlFile, "FtpInfo", "PWD",
					PWD_Ftp, 0)) {

				setMsg("更新系统通信参数配置信息失败");
				req.setTransFlag(-1);
				req.setTransMsg(getMsg());
				return;

			}
			if (!ResourcePool.EditConfigInfo(xmlFile, "FtpInfo", "Path",
					Path_Ftp, 0)) {

				setMsg("更新系统通信参数配置信息失败");
				req.setTransFlag(-1);
				req.setTransMsg(getMsg());
				return;

			}

			setMsg("更新系统通信参数配置信息成功\r\n请重启Web服务器！");
			req.setTransFlag(0);
			req.setTransMsg(getMsg());

		} catch (Exception e) {

			setMsg("更新系统通信参数配置信息出错");
			req.setTransFlag(-1);
			req.setTransMsg(getMsg());
		}
		return;
	}

	/**
	 * Method：void getSysParaList(String TransactionName,String InstID,String
	 * UserID) Function 得到文件列表 输入列表： String TransactionName: 交易名称 String
	 * InstID:已登录机构ID String UserID:已登录用户ID
	 * 
	 * 输出参数： 系统参数列表的交易结果集
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public void getSysParaList() {

		ArrayList tmpv = null;
		try {
			/*
			 * 获得查询语句
			 */

			String sqlStr = "select distinct AREA_CODE,AREA_NAME,AREA_ADDR,FIRSTDATE,WORKDATE,PROCESSNO,STEPNO,WRITELOG from T_SYS_PARA";

			String returnStr = "|";
			String titleStr = "";

			/**
			 * 查询列表信息
			 **/
			tmpv = dbclass.doQuery(sqlStr, 0, 0);
			if (tmpv == null) {
				DbSetMsg(dbclass.getMsg());
				req.setTransFlag(-1);
				req.setTransMsg(getMsg());
			} else {
				for (int i = 0; i < tmpv.size(); i++) {
					Hashtable tmph = (Hashtable) tmpv.get(i);
					String AREA_CODE = Tools.trimNull((String) tmph
							.get("AREA_CODE".toUpperCase()));
					String AREA_NAME = Tools.trimNull((String) tmph
							.get("AREA_NAME".toUpperCase()));
					String AREA_ADDR = Tools.trimNull((String) tmph
							.get("AREA_ADDR".toUpperCase()));
					String FIRSTDATE = Tools.trimNull((String) tmph
							.get("FIRSTDATE".toUpperCase()));
					String WORKDATE = Tools.trimNull((String) tmph
							.get("WORKDATE".toUpperCase()));
					String PROCESSNO = Tools.trimNull((String) tmph
							.get("PROCESSNO".toUpperCase()));
					String STEPNO = Tools.trimNull((String) tmph.get("STEPNO"
							.toUpperCase()));
					String WRITELOG = Tools.trimNull((String) tmph
							.get("WRITELOG".toUpperCase()));

					returnStr = returnStr + AREA_CODE + "|#|" + AREA_NAME
							+ "|#|" + AREA_ADDR + "|#|" + FIRSTDATE + "|#|"
							+ WORKDATE + "|#|" + PROCESSNO + "|#|" + STEPNO
							+ "|#|" + WRITELOG + "|#|" + "√|#|";
				}
				if (tmpv.size() == 0) {
					setMsg("无系统参数信息");
					req.setTransMsg(getMsg());
				}
				String tempStr = "";
				if (returnStr.length() > 0) {
					titleStr = titleStr + "SysPara_list|=";
					/*
					 * table 的name 和id
					 */
					if (returnStr.endsWith("|#|"))
						tempStr = titleStr
								+ returnStr
										.substring(0, returnStr.length() - 3);
					else
						tempStr = titleStr + returnStr;
				}
				req.setTransFlag(0);
				// logger.debug("[tmpStr:]" + tempStr);
				req.setResultObj(tempStr);
				// result.addResultSet(rvs);
			}

		} catch (Exception e) {
			setMsg("得到系统参数列表出错");
			req.setTransFlag(-1);
			req.setTransMsg(getMsg());
			/* 日志写法参见相关文档　 */
		}
		return;
	}

	@SuppressWarnings("rawtypes")
	public void GetRoleUserByTree() {
		// 固定格式，不必修改
		String RoleCode = req.getParameter("RoleCode");
		ArrayList tmpv = null;

		String sqlStr = ""; // 查询字符串
		String returnStr = "User|=|"; // 按照一定规则形成的查询结果字符串
		String inst = "";
		int upId = 0;
		int Id = 0;
		try {
			sqlStr = "select o.INST_NO,i.INST_NAME,o.USER_NAME||'('||trim(o.USER_CODE)||')' name,i.Inst_No||'_'||trim(o.User_Code) code ,nvl(Private_Code,'0') checked "
					+ "from (select o.*,p.Private_Code from T_MGT_OPERATOR_Y o left join (select * from T_USER_PRIVATE where Private_Code='"
					+ RoleCode
					+ "' and Private_Type='1' ) p on o.Inst_No=trim(p.Inst_No) and o.User_Code=p.User_Code) o ,T_INST_INFO i where i.INST_NO=trim(o.Inst_No) order by UpInst_No,i.Inst_No,user_code  ";
			tmpv = dbclass.doQuery(sqlStr, 0, 0);
			if (tmpv == null) {
				// 数据库操作失败
				DbSetMsg(dbclass.getMsg());
				req.setTransFlag(-1);
				req.setTransMsg(getMsg());
			} else {
				for (int i = 0; i < tmpv.size(); i++) {
					Hashtable tmph = (Hashtable) tmpv.get(i);
					String instCode = Tools.trimNull((String) tmph
							.get("INST_NO".toUpperCase()));
					String inst_name = Tools.trimNull((String) tmph
							.get("INST_NAME".toUpperCase()));
					String user_name = Tools.trimNull((String) tmph.get("name"
							.toUpperCase()));
					String user_code = Tools.trimNull((String) tmph.get("code"
							.toUpperCase()));
					String checked = Tools.trimNull((String) tmph.get("checked"
							.toUpperCase()));
					if (!instCode.equals(inst)) {
						inst = instCode;
						upId = Id + 1;
						Id = upId;
						returnStr = returnStr + upId + "|" + upId + "|"
								+ inst_name + "(" + instCode + ")||" + checked
								+ "|#|";
					}
					Id++;
					returnStr = returnStr + Id + "|" + upId + "|" + user_name
							+ "|" + user_code + "|" + checked + "|#|";
				}

			}

			// 对返回字符串的末尾进行修改，删除最后一个"|#|"
			if (returnStr.length() > 0) {
				if (returnStr.endsWith("|#|")) {
					returnStr = returnStr.substring(0, returnStr.length() - 3);
				}
			}
		} catch (Exception e) {
			setMsg("失败原因：数据库操作异常");
			req.setTransFlag(-1);
			req.setTransMsg(getMsg());
		}
		// 返回完整的打包后的数据流
		req.setTransFlag(0);
		req.setResultObj(returnStr);
		// logger.info(returnStr);
		return;
	}

	public void ExportJReport() throws IOException, ServletException {
		String action = request.getParameter(ReportJob.ACTION_TYPE);

		if ("export".equals(action)) {
			ReportExporter.service(request, response, dbclass.getConnection());
		} else if (action == null) {
			ReportWriter.service(request, response, dbclass.getConnection());
		} else if ("tempfile".equals(action)) {
			FileFinder.service(request, response);
		} else {
			throw new ServletException(Messages.getString("res.42") + action
					+ ".");
		}
		return;
	}

	public void LoadLevel2Inst() {
		StringBuffer tbuf = new StringBuffer();
		tbuf
				.append("select distinct inst_no, linst_name||'('||inst_no||')'  from T_INST_INFO");
		if (UserLevel.equals("1"))
			tbuf.append(" where INST_LEVEL in ('1','2')");
		else
			tbuf.append(" where inst_no = '" + InstID
					+ "' and INST_LEVEL in ('1','2')");
		if (!(Cancel_Inst != null && Cancel_Inst.equals("1")))
			tbuf.append(" and GetFileNo2<>'9'");
		tbuf.append(" order by inst_no");
		req.setSqlStr(tbuf.toString());
		req.setBodyCtrl("机构号^机构名称");
		return;
	}

	public void UserOnloadNet() {
		String sqlStr = "";
		inst_no = req.getParameter("inst_no");
		InstID = req.getUserVariable("InstID").toString();
		UserLevel = req.getUserVariable("UserLevel").toString();
		if (UserLevel.equals("1")) {
			if (inst_no != null && (!inst_no.equals(""))
					&& (!inst_no.equals("0000"))) {
				sqlStr = "select distinct inst_no,inst_name||'('||inst_no||')' from T_INST_INFO where (UPINST_NO = '"
						+ inst_no + "' or UPINST_NO1 = '" + inst_no + "')";
				if (!(Cancel_Inst != null && Cancel_Inst.equals("1")))
					sqlStr += " and GetFileNo2<>'9'";
			} else {
				sqlStr = "select distinct inst_no,inst_name||'('||inst_no||')' from T_INST_INFO where Inst_No<>'0000'";
				if (!(Cancel_Inst != null && Cancel_Inst.equals("1")))
					sqlStr += " and GetFileNo2<>'9'";
				sqlStr += " order by inst_no";
			}
		} else if (UserLevel.equals("2")) {
			sqlStr = "select distinct inst_no,inst_name||'('||inst_no||')' from T_INST_INFO where (UPINST_NO = '"
					+ InstID + "' or UPINST_NO1 ='" + InstID + "')";
			if (!(Cancel_Inst != null && Cancel_Inst.equals("1")))
				sqlStr += " and GetFileNo2<>'9'";
			sqlStr += " order by inst_no";
		} else {
			sqlStr = "select distinct inst_no,inst_name||'('||inst_no||')' from T_INST_INFO where inst_no='"
					+ InstID + "'";
		}
		req.setSqlStr(sqlStr);
		return;
	}

	public void MenuColor_Update() {
		String MenuName = req.getParameter("MenuName");
		String NewValue = req.getParameter("NewValue");
		InstID = req.getUserVariable("InstID").toString();
		UserID = req.getUserVariable("UserID").toString();
		String sqlStr = "merge into T_USERMENU_COLOR r using (select '"
				+ InstID
				+ "' Inst_No,'"
				+ UserID
				+ "' User_Code,Menu_Name from T_SYS_MENU where Menu_Name='"
				+ MenuName.trim()
				+ "') c "
				+ "on (r.Inst_No=c.Inst_No and r.User_Code=c.User_Code and r.Menu_Name=c.Menu_Name) "
				+ "when matched then update set Color ='" + NewValue
				+ "' when not matched then insert values('" + InstID + "','"
				+ UserID + "','" + MenuName.trim() + "','" + NewValue + "')";
		req.setSqlStr(sqlStr);
		return;
	}

	public void LoadUserList() {
		String user_level = req.getParameter("user_level");
		String user_code = req.getParameter("user_code");
		String sqlStr = "select o.USER_CODE  result , trim(i.LINST_NAME)||'('||o.INST_NO||')'||'|'||o.USER_NAME text from T_MGT_OPERATOR_Y o ,T_INST_INFO i where i.INST_NO=o.Inst_No ";
		if (inst_no != null && !inst_no.equals("")) {
			sqlStr = "select o.USER_CODE  result , i.LINST_NAME||'('||o.INST_NO||')'||'|'||o.USER_NAME text "
					+ "from T_MGT_OPERATOR_Y o ,(select inst_no,LINST_NAME from T_INST_INFO where INST_NO in('0000','"
					+ inst_no
					+ "',(select UpInst_No from T_INST_INFO where Inst_No='"
					+ inst_no + "'))) i " + "where o.INST_NO=i.Inst_No ";
		}
		if (user_level != null && !user_level.equals("")
				&& !user_level.equals("any")) {
			sqlStr = sqlStr + " and User_level<='" + user_level + "'";
		}
		if (!(user_code == null || user_code.equals(""))) {
			sqlStr = sqlStr + " and User_Code<>'" + user_code + "'";
		}
		sqlStr += " order by decode(i.Inst_No,'" + inst_no + "','0','1')";
		req.setSqlStr(sqlStr);
		return;
	}

	public void TransLog_Query() {
		String beginDate = req.getParameter("beginDate");
		String endDate = req.getParameter("endDate");
		String userCode = req.getParameter("userCode");
		inst_no = req.getParameter("inst_no");
		String service_name = req.getParameter("service_name");
		String Query_Output = req.getParameter("Query_Output");
		String[] ColumnList;// 此数组用于存储Query_Output字串分解后的数据
		boolean gflag = false;
		String sqlStr = "select ";
		String whereStr = " where 1=1 ";
		String fromStr = " from T_MGT_TRANS_JOUR_Y a";
		String groupStr = " group by ";
		if (Query_Output.indexOf("user_name") != -1
				|| Query_Output.indexOf("user_lvl") != -1) {
			fromStr = fromStr + ",T_MGT_OPERATOR_Y o";
			whereStr = whereStr
					+ "and a.Inst_No=o.Inst_No and trim(a.User_Code)=trim(o.User_Code) ";
		}
		if (Query_Output.indexOf("service_name") != -1) {
			fromStr = fromStr + ",T_SYS_FUNC f,T_SYS_MENU m";
			whereStr = whereStr
					+ " and a.trans_code=f.Func_Code and f.Menu_Code=m.Menu_Code ";
		}
		if (Query_Output.indexOf("LInst_Name") != -1) {
			fromStr = fromStr + ",T_INST_INFO i";
			whereStr = whereStr + "and a.Inst_No=i.Inst_No ";
		}

		ColumnList = Query_Output.split(",");

		String OutColStr = "";

		for (int j = 0; j < (ColumnList.length); j++) {
			OutColStr = ColumnList[j].toString();
			if (OutColStr.equals("Inst_No")) {
				sqlStr = sqlStr + "a.Inst_No";
				groupStr = groupStr + "a.Inst_No";
				gflag = true;
			}
			if (OutColStr.equals("LInst_Name")) {
				if (gflag) {
					sqlStr = sqlStr + ",i.LInst_Name";
					groupStr = groupStr + ",i.LInst_Name";
				} else {
					sqlStr = sqlStr + "i.LInst_Name";
					groupStr = groupStr + "i.LInst_Name";

				}
				gflag = true;
			}
			if (OutColStr.equals("user_code")) {
				if (gflag) {
					sqlStr = sqlStr + ",a.User_Code user_code";
					groupStr = groupStr + ",a.User_Code";
				} else {
					sqlStr = sqlStr + "a.User_Code user_code";
					groupStr = groupStr + "a.User_Code";

				}
				gflag = true;
			}
			if (OutColStr.equals("user_name")) {
				if (gflag) {
					sqlStr = sqlStr + ",o.User_Name user_name";
					groupStr = groupStr + ",o.User_Name";
				} else {
					sqlStr = sqlStr + "o.User_Name user_name";
					groupStr = groupStr + "o.User_Name";

				}
				gflag = true;
			}
			if (OutColStr.equals("user_lvl")) {
				if (gflag) {
					sqlStr = sqlStr
							+ ",decode(trim(o.User_Level),'1','行级','2','支行级','3','网点级') user_lvl";
					groupStr = groupStr + ",o.User_Level";
				} else {
					sqlStr = sqlStr + "o.User_Level user_lvl";
					groupStr = groupStr + "o.User_Level";

				}
				gflag = true;
			}
			if (OutColStr.equals("service_name")) {
				if (gflag) {
					sqlStr = sqlStr
							+ ",trim(m.Menu_Name)||'.'||f.Func_Name service_name";
					groupStr = groupStr
							+ ",trim(m.Menu_Name)||'.'||f.Func_Name";
				} else {
					sqlStr = sqlStr
							+ "trim(m.Menu_Name)||'.'||f.Func_Name service_name";
					groupStr = groupStr + "trim(m.Menu_Name)||'.'||f.Func_Name";

				}
				gflag = true;
			}
			if (OutColStr.equals("t_date")) {
				if (gflag) {
					sqlStr = sqlStr + ",a.Trans_Date t_date";
					groupStr = groupStr + ",a.Trans_Date";
				} else {
					sqlStr = sqlStr + "a.Trans_Date t_date";
					groupStr = groupStr + "a.Trans_Date";

				}
				gflag = true;
			}
			if (OutColStr.equals("t_desc")) {
				if (gflag) {
					sqlStr = sqlStr + ",a.Trans_Data t_desc";
					groupStr = groupStr + ",a.Trans_Data";
				} else {
					sqlStr = sqlStr + "a.Trans_Data t_desc";
					groupStr = groupStr + "a.Trans_Data";

				}
				gflag = true;
			}
			if (OutColStr.equals("cnt")) {
				sqlStr = sqlStr + ",count(*) cnt";
			}
		}
		if (!endDate.equals(""))
			endDate = endDate + "z";
		if (!beginDate.equals("") && endDate.equals(""))
			whereStr = whereStr + "and TRANS_DATE>='" + beginDate + "' ";
		if (beginDate.equals("") && !endDate.equals(""))
			whereStr = whereStr + "and TRANS_DATE<='" + endDate + " '";
		if (!beginDate.equals("") && !endDate.equals(""))
			whereStr = whereStr + "and TRANS_DATE>='" + beginDate
					+ "' and TRANS_DATE<='" + endDate + "' ";
		if (!(inst_no.equals("") || inst_no == null)) {
			whereStr = whereStr + "and a.INST_NO = '" + inst_no + "' ";
		}
		if (!userCode.equals("")) {
			whereStr = whereStr + "and a.USER_CODE like '%" + userCode + "%' ";
		}
		if (!service_name.equals("")) {
			whereStr = whereStr + "and trim(a.Trans_Code)= '" + service_name
					+ "' ";
		}

		if (gflag)
			sqlStr = sqlStr + fromStr + whereStr + groupStr;
		else
			sqlStr = sqlStr + fromStr + whereStr;
		req.setSqlStr(sqlStr);
		return;
	}

	@SuppressWarnings("rawtypes")
	public void GetRoleByTree() {
		ArrayList tmpv = null;
		String sqlStr = ""; // 查询字符串
		String returnStr = "Role|=|"; // 按照一定规则形成的查询结果字符串
		String appid = "";
		int upId = 0;
		int Id = 0;
		String FuncID = req.getParameter("FuncID");
		try {
			sqlStr = "select a.ID||'@'||rid id,a.Name aname,rid,rname,checked "
					+ "from ("
					+ "select r.APPLICATIONID aid,r.id rid,r.NAME||'('||trim(r.roleno)||')' rname,(case when FuncID is null then '0' else 'checked' end) checked "
					+ "from T_ROLE r left join T_ROLE_FUNC rf on r.APPLICATIONID=rf.APPLICATIONID and r.id=rf.roleid and FuncID='"
					+ FuncID + "'" + ") r , T_APPLICATION a "
					+ "where r.aid=a.ID";
			tmpv = dbclass.doQuery(sqlStr, 0, 0);
			if (tmpv == null) {
				// 数据库操作失败
				DbSetMsg(dbclass.getMsg());
				req.setTransFlag(-1);
				req.setTransMsg(getMsg());
			} else {
				for (int i = 0; i < tmpv.size(); i++) {
					Hashtable tmph = (Hashtable) tmpv.get(i);
					String id = Tools.trimNull((String) tmph.get("id"
							.toUpperCase()));
					String aname = Tools.trimNull((String) tmph.get("aname"
							.toUpperCase()));
					String rname = Tools.trimNull((String) tmph.get("rname"
							.toUpperCase()));
					String checked = Tools.trimNull((String) tmph.get("checked"
							.toUpperCase()));
					if (!id.equals(appid)) {
						appid = id;
						upId = Id + 1;
						Id = upId;
						returnStr = returnStr + upId + "|" + upId + "|" + aname
								+ "||" + checked + "|#|";
					}
					Id++;
					returnStr = returnStr + Id + "|" + upId + "|" + rname + "|"
							+ id + "|" + checked + "|#|";
				}

			}

			// 对返回字符串的末尾进行修改，删除最后一个"|#|"
			if (returnStr.length() > 0) {
				if (returnStr.endsWith("|#|")) {
					returnStr = returnStr.substring(0, returnStr.length() - 3);
				}
			}
		} catch (Exception e) {
			setMsg("失败原因：数据库操作异常");
			req.setTransFlag(-1);
			req.setTransMsg(getMsg());
		}
		// 返回完整的打包后的数据流
		req.setTransFlag(0);
		req.setResultObj(returnStr);
		// logger.info(returnStr);
		return;
	}

	public void SetRoleFunc() {
		String addStr = req.getParameter("addStr");
		String delStr = req.getParameter("delStr");
		String FuncID = req.getParameter("FuncID");
		String DelSql = "";
		String AddSql = "";

		try {
			dbclass.beginTransction();
			if (delStr != null && !delStr.equals("")) {
				DelSql = "delete from T_ROLE_FUNC where FuncID='" + FuncID
						+ "' and APPLICATIONID||'@'||ROLEID in (" + delStr
						+ ")";
				if (!dbclass.executeUpdate(DelSql)) {
					DbSetMsg(dbclass.getMsg());
					req.setTransFlag(-1);
					req.setTransMsg(getMsg());

					return;
				}
			}
			if (addStr != null && !addStr.equals("")) {
				AddSql = "insert into T_ROLE_FUNC "
						+ "select APPLICATIONID,ID,'" + FuncID + "' "
						+ "from T_ROLE  " + "where APPLICATIONID||'@'||ID in ("
						+ addStr + ")";

				if (!dbclass.executeUpdate(AddSql)) {
					DbSetMsg(dbclass.getMsg());
					req.setTransFlag(-1);
					req.setTransMsg(getMsg());

					return;
				}
			}
			setMsg("设置用户角色成功");
			req.setTransFlag(0);
			req.setTransMsg(getMsg());

		} catch (Exception e) {
			DbSetMsg(dbclass.getMsg());
			req.setTransFlag(-1);
			req.setTransMsg(getMsg());

		}

		return;
	}

	public void SaveSqlDefine() {
		dbclass.beginTransction();
		if (!dbclass.executeUpdate(req.getSqlStr())) {
			req.setTransFlag(-1);
			req.setResultObj(dbclass.getMsg());
			return;
		}
		String sql = "delete from T_FUNC where FuncID like '" + DefineID
				+ ".%'";
		if (!dbclass.executeUpdate(sql)) {
			req.setTransFlag(-1);
			req.setResultObj(dbclass.getMsg());
			return;
		}
		String[] check = checkCode.split(",", -1);
		if (check.length > 0) {
			sql = null;
			String sName = null;

			for (String c : check) {
				if (c.equals(""))
					continue;
				if (sql == null)
					sql = "insert into T_FUNC ";
				else
					sql += " union ";
				if (c.equals("q"))
					sName = "查询";
				else if (c.equals("i"))
					sName = "新增";
				else if (c.equals("u"))
					sName = "修改";
				else if (c.equals("d"))
					sName = "删除";
				sql += "select '" + DefineID + "." + c + "','" + Name + "."
						+ sName + "' from T_SQLQUERY_DEFINE where ID='"
						+ DefineID + "'";
			}
			if (sql != null && !sql.equals("") && !dbclass.executeUpdate(sql)) {
				req.setTransFlag(-1);
				req.setResultObj(dbclass.getMsg());
				return;
			}
		}
		return;
	}

	public void GetFormDefined() {
		String sqlStr = "";
		if (FormType.equals("1"))
			sqlStr = "select QueryForm from T_SQLQUERY_DEFINE where id='"
					+ DefineID + "'";
		else if (FormType.equals("2"))
			sqlStr = "select OtherForm from T_SQLQUERY_DEFINE where id='"
					+ DefineID + "'";
		String formStr = dbclass.doQueryString(sqlStr, null);
		if (formStr == null || formStr.equals("")) {
			req.setTransFlag(0);
			req.setResultObj("");
			return;
		}
		String[] unit = formStr.split("\\|#\\|", -1);
		StringBuffer buf = new StringBuffer(
				"<table class=\"front-table-full-width front-bgcolor2\" border='1' cellSpacing='1' borderColor='#b4ccee' cellPadding='1' width='100%'>\n <tr>");
		int cnt = 0;
		for (int i = 0; i < unit.length;) {
			String[] val = { "", "", "", "", "", "" };
			for (int j = 0; j < 6; j++) {
				val[j] = unit[i];
				i++;
			}
			String attr = "";
			if (val[3] == "1")
				attr = " disabled ";
			else if (val[3] == "2")
				attr = " style='display:none' ";
			if (val[4].equalsIgnoreCase("2")) {

				buf
						.append("<th >"
								+ val[2]
								+ "</th>\n"
								+ "<td class='input-cmd'><select class='text-normal' id='"
								+ val[1] + "' " + attr + ">");
				if (val[5].toLowerCase().startsWith("select ")) {
					sqlStr = "select DbsNo from T_SQLQUERY_DEFINE where id='"
							+ DefineID + "'";
					int dbsNo = Integer.parseInt(dbclass.doQueryString(sqlStr,
							null));
					val[5] = Tools.toString(val[5]).replaceAll("\\$27\\$", "'");
					@SuppressWarnings("unchecked")
					ArrayList<Object[]> l = req.getConnectionFactory(dbsNo)
							.doQuery(val[5], null, 0, 0, true);
					if (l != null && l.size() > 0) {
						for (Object[] rec : l) {
							for (int j = 0; j < rec.length; j++) {
								buf.append("<option value='" + rec[0] + "'>"
										+ rec[1] + "</option>");
							}
						}
					}
				} else {
					String[] option = val[5].split(";", -1);
					for (String rec : option) {
						String[] field = rec.split(":", -1);
						buf.append("<option value='" + field[0] + "'>"
								+ field[1] + "</option>");
					}
				}
				buf.append("</select></td>");
			} else {
				buf
						.append("<th >"
								+ val[2]
								+ "</th>\n"
								+ "<td class='input-cmd'><input type='text' class='text-normal' id='"
								+ val[1] + "' " + attr + "></td>");
			}
			cnt++;
			if (cnt % 2 == 0 && i != 0 && i != (unit.length - 1))
				buf.append("</tr>\n<tr>\n");
		}
		buf.append("</tr>\n</table>\n");

		req.setTransFlag(0);
		req.setResultObj(buf.toString());
		return;
	}

	@SuppressWarnings("rawtypes")
	public void GetSqlStr() {
		String sqlStr = null;
		ArrayList l = null;
		Object[] str = null;
		switch (OPMODE) {
		case 1:
			if (!new BaseProcess().HasPrivate(req, DefineID + ".q")) {
				req.setTransFlag(-1);
				req.setTransMsg("您没有查询权限");
				return;
			}
			sqlStr = "select QuerySQL,DbsNo,QueryColName,UpdateSQL,DeleteSQL from T_SQLQUERY_DEFINE where id='"
					+ DefineID + "'";
			break;
		case 2:
			if (!new BaseProcess().HasPrivate(req, DefineID + ".i")) {
				req.setTransFlag(-1);
				req.setTransMsg("您没有新增权限");
				return;
			}
			sqlStr = "select InsertSQL,DbsNo from T_SQLQUERY_DEFINE where id='"
					+ DefineID + "'";
			break;
		case 3:
			if (!new BaseProcess().HasPrivate(req, DefineID + ".u")) {
				req.setTransFlag(-1);
				req.setTransMsg("您没有修改权限");
				return;
			}
			sqlStr = "select UpdateSQL,DbsNo,WhereStr from T_SQLQUERY_DEFINE where id='"
					+ DefineID + "'";
			break;
		case 4:
			if (!new BaseProcess().HasPrivate(req, DefineID + ".d")) {
				req.setTransFlag(-1);
				req.setTransMsg("您没有删除权限");
				return;
			}
			sqlStr = "select DeleteSQL,DbsNo,WhereStr from T_SQLQUERY_DEFINE where id='"
					+ DefineID + "'";
			break;
		case 5:
			if (!new BaseProcess().HasPrivate(req, DefineID + ".u")) {
				req.setTransFlag(-1);
				req.setTransMsg("您没有修改权限");
				return;
			}
			sqlStr = "select UpdateQuerySQL,DbsNo,WhereStr from T_SQLQUERY_DEFINE where id='"
					+ DefineID + "'";
			break;
		}
		l = dbclass.doQuery(sqlStr, null, 0, 0, true);
		str = (Object[]) l.get(0);
		if (OPMODE >= 3)
			sqlStr = Tools.toString(str[0]) + " " + Tools.toString(str[2]);
		else
			sqlStr = Tools.toString(str[0]);
		if (OPMODE == 1) {
			String t1 = Tools.toString(str[3]);
			String colName = Tools.toString(str[2]);
			String tStr = "";
			if (!colName.equals(""))
				colName = colName.replaceAll(",", "^");
			if (!t1.equals("")) {
				tStr = "<button value='修改' onclick='UpdateRecord();'";
			}
			t1 = Tools.toString(str[4]);
			if (!t1.equals(""))
				tStr += "<button value='删除' onclick='DeleteRecord();'";
			if (!tStr.equals("")) {
				colName += "^操作[<text style='display:none;'>" + tStr + "]";
				int index = sqlStr.toLowerCase().indexOf("from");
				sqlStr = sqlStr.substring(0, index) + ",0 from "
						+ sqlStr.substring(index + 4);
			}
			req.setBodyCtrl(colName);
		}
		sqlStr = sqlStr.replaceAll("\\$27\\$", "'");
		for (Enumeration enum1 = request.getParameterNames(); enum1
				.hasMoreElements();) {
			String key = (String) enum1.nextElement();
			String value = request.getParameter(key);
			sqlStr = sqlStr.replaceAll(":" + key, value);
		}
		@SuppressWarnings("unchecked")
		Map<String, Object> map = req.getVariables();
		Set<Map.Entry<String, Object>> entries = map.entrySet();

		for (Map.Entry<String, Object> entry : entries) {// 遍历整个MAP,解析KEY中的行列信息
			String key = entry.getKey();
			String value = Tools.toString(map.get(key), "");
			sqlStr = sqlStr.replaceAll(":" + key, value);
		}
		req.setSqlStr(sqlStr);
		return;
	}

	@SuppressWarnings("rawtypes")
	public void FileUpLoad() // throws ServletException, IOException
	{
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);

		String fileType = "";
		String fileName = "";
		String writeName = "";
		InputStream in = null;
		OutputStream out = null;

		try {
			upload.setSizeMax(1024000000);
			upload.setHeaderEncoding("utf-8");
			List items = upload.parseRequest(request);
			Iterator iterator = items.iterator();
			while (iterator.hasNext()) {
				FileItem item = (FileItem) iterator.next();
				if (item.isFormField()) {
					String fieldName = item.getFieldName();
					if (fieldName.equals("fileType"))
						fileType = item.getString();
					if (fieldName.equals("writeFile"))
						writeName = item.getString();
				} else {
					fileName = item.getName();
					if (fileName == null || fileName.equals(""))
						continue;
					int i = -1;
					i = fileName.lastIndexOf('\\');
					if (i >= 0)
						fileName = fileName.substring(i + 1);
					else {
						i = fileName.lastIndexOf('/');
						if (i >= 0)
							fileName = fileName.substring(i + 1);
					}

					if (writeName == null || writeName.equals(""))
						writeName = ResourcePool.getRootpath() + "files/"
								+ fileName;
					if (!fileType.startsWith("ftp")) {
						item.write(new File(writeName));
					} else {
						String SysConfigPath = ResourcePool.getRootpath()
								+ "/config/system.xml";
						String hostname = ResourcePool.GetConfigInfo(
								SysConfigPath, "FtpInfo", "ServerIP")[0];
						String ftp_usr = ResourcePool.GetConfigInfo(
								SysConfigPath, "FtpInfo", "UserID")[0];
						String ftp_pwd = ResourcePool.GetConfigInfo(
								SysConfigPath, "FtpInfo", "PWD")[0];
						out = ResourcePool.putFileFromFtp(fileName, ftp_usr,
								ftp_pwd, hostname);
						if (out == null) {
							req.setTransFlag(-1);
							req.setTransMsg("文件上传连接失败");
							return;
						}
						in = item.getInputStream();
						byte[] b = new byte[1024];
						int len;
						while ((len = in.read(b)) > 0) {
							out.write(b, 0, len);
						}
						in.close();
						out.close();
						in = null;
						out = null;
					}
				}
			}
		} catch (Exception e) {
			try {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			} catch (Exception e1) {
			}
			req.setTransFlag(-1);
			req.setTransMsg("文件上传失败：" + e);
			return;
		}
		req.setTransFlag(0);
		req.setTransMsg("文件上传成功");
	}

	public void CreateJReport() {

		try {
			String action = req.getParameter("_action_type");
			ConnectionFactory dbclass1 = req.getConnectionFactory(DbsNo);
			if ("html".equals(action)) {
				request.setAttribute("SqlConnect", dbclass1.getConnection());
				request.getRequestDispatcher("/jsp/defaultviewer.jsp").forward(
						request, response);
			} else if ("export".equals(action)) {
				ReportExporter.service(request, response, dbclass1
						.getConnection());
			} else if (action == null) {
				ReportWriter.service(request, response, dbclass1
						.getConnection());
			} else if ("tempfile".equals(action)) {
				FileFinder.service(request, response);
			}
		} catch (Exception e) {
			setMsg("CreateJReport异常:" + e);
			req.setTransFlag(-1);
			req.setTransMsg(getMsg());
		}
		return;
	}

	@SuppressWarnings("rawtypes")
	public void CreateExcelByTemplate() {
		Map<String, String> varMap = new HashMap<String, String>();
		String report = "";
		String reportName = null;
		String xmlID = null;
		for (Enumeration enum1 = request.getParameterNames(); enum1
				.hasMoreElements();) {
			String key = (String) enum1.nextElement();
			String value = request.getParameter(key);
			if (key.equalsIgnoreCase("reportName")) {
				reportName = value;
			} else if (key.equalsIgnoreCase("reportID")) {
				report = value;
			} else if (key.equalsIgnoreCase("xmlID")) {
				xmlID = value;
			} else
				varMap.put(key, value);
		}
		if (xmlID == null || xmlID.equals(""))
			xmlID = report;
		try {
			IDefIO cf = Tools.getDefIO();
			// DefIO cf =new DefIO();
			ByteArrayOutputStream out = cf.CreateExcelByTemplate(req,
					ResourcePool.getRootpath() + "/" + report + ".xls",
					ResourcePool.getRootpath() + "/" + xmlID + ".xml", varMap);

			if (out != null) {
				if (!cf.hasRecord()) {
					req.setTransFlag(-1);
					req.setTransMsg("没有满足条件的记录");
					return;
				}
				// reportName=java.net.URLEncoder.encode(reportName, "UTF-8");
				// reportName=new String(reportName.getBytes("GBK"),
				// "ISO8859-1");
				reportName = new String(reportName.getBytes(), "ISO8859-1");
				response.setContentType("application/vnd.ms-excel");
				response.setHeader("Content-disposition:inline;",
						"attachment; filename=" + reportName + ".xls");
				response.setContentLength(out.size());
				ServletOutputStream out1 = response.getOutputStream();
				out.writeTo(out1);
				out.flush();
				out.close();
				out1.flush();
				out1.close();
				out1 = null;
				response.flushBuffer();
			} else {
				req.setTransFlag(-1);
				req.setTransMsg("生成失败:" + req.getTransMsg());
				return;
			}

		} catch (Exception e) {
			req.setTransFlag(-1);
			req.setTransMsg("生成失败:" + req.getTransMsg());
		}
	}
}
