/*
/*
 * 创建日期: 2011-05-30
 * 项目名称: 工行营业部报表管理管理系统
 * 项目地点: 昆明南天电脑系统公司
 * 功能说明: 应用出口
 */
package services;

/**
 * @author xgy
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;

import eWAP.core.ResourcePool;
import eWAP.core.Tools;
import eWAP.core.dbaccess.*;

public class ApplExitProcess extends Base {
	private static final Logger logger = Logger
			.getLogger(ApplExitProcess.class);
	private ConnectionFactory dbclass = null; // 数据库操作实例
	private ResourcePool req = null;
	String dbType = null;
	String InstID = null;
	String UserID = null;
	String UserLevel = null;
	String TransactionName = null;
	int currentPage = 0;
	int LinesCount = 0;
	String reqClass;
	String reqFunc;
	String reqPara;

	public ApplExitProcess() {
		super();
	}

	/**
	 * 接收前台参数，返回前台void
	 * 
	 * @function: 查询 系统日期（开始日期，工作日期，数据导入日期） exp:
	 *            需要在调用网页中有3个html变量来接受hidden_firstDate
	 *            ,hidden_workDate,hidden_lastDate.
	 * 
	 * 
	 * @return void
	 */
	@SuppressWarnings("rawtypes")
	public void Get_SysDate() {

		ArrayList tmpv = null;
		String sqlStr = "";
		String returnStr = "|";
		String tempStr = "";

		/* 忽略权限检查，在T_SYS_FUNC表中也不必加此交易码。 */

		try {
			sqlStr = "select FIRSTDATE,WORKDATE,ProcessNo from T_SYS_PARA ";
			tmpv = dbclass.doQuery(sqlStr, 0, 0);

			if (tmpv == null) {
				DbSetMsg(dbclass.getMsg());
				// setMsg("查询系统工作日期出错！数据库错误。");
				req.setTransFlag(-1);
				req.setTransMsg(getMsg());
			}
			if (tmpv.size() == 0) {
				// setMsg("没有满足条件的系统工作日期信息！");
				req.setTransFlag(-1);
				req.setTransMsg(getMsg());
			}

			else {
				// 要存到的变量名
				String caption = "hidden_firstDate^hidden_workDate^hidden_lastDate|=";

				Hashtable tmph = (Hashtable) tmpv.get(0);
				String FIRSTDATE = ((String) tmph
						.get("FIRSTDATE".toUpperCase())).trim();
				String WORKDATE = ((String) tmph.get("WORKDATE".toUpperCase()))
						.trim();
				String ProcessNo = ((String) tmph
						.get("ProcessNo".toUpperCase())).trim();
				String LASTDATE = WORKDATE;
				if (ProcessNo.equals("0"))
					LASTDATE = Pub.DateSub(WORKDATE, 1);
				returnStr = returnStr + FIRSTDATE + "^" + WORKDATE + "^"
						+ LASTDATE;

				tempStr = caption + returnStr;
				req.setTransFlag(0);
				req.setResultObj(tempStr);
			}
		} catch (Exception e) {
			// setMsg("系统工作日期信息查询失败，失败原因：数据库操作异常!");
			req.setTransFlag(-1);
			req.setTransMsg(getMsg());
		}

		return;
	}

	/**
	 * 　 Method： void LoadAccItem() Function:
	 * 载入项目表T_ACCOUNT_ITEM中的帐户权限号AccItemNo到页面下拉框 输出参数: 下拉框选项查询结果
	 **/
	@SuppressWarnings("rawtypes")
	public void LoadAccItem() {
		// 固定格式，不必修改

		ArrayList tmpv = null;

		String sqlStr = ""; // 查询字符串
		String returnStr = "|"; // 按照一定规则形成的查询结果字符串
		String titleStr = "AccItemNo|="; // 返回前台的字符串的包头
		String tmpStr = ""; // 完整的返回前台的字符串

		try {
			sqlStr = "select ItemNo  result, ItemNo||' '||ItemName name from T_ITEM  where itemkind like '%9%' order by ItemNo";

			logger.info(sqlStr);
			tmpv = dbclass.doQuery(sqlStr, 0, 0);
			if (tmpv == null) {
				// 数据库操作失败
				DbSetMsg(dbclass.getMsg());
				logger.debug("下拉框载入失败：" + dbclass.getMsg());
				req.setTransFlag(-1);
				req.setTransMsg(getMsg());
			} else {
				for (int i = 0; i < tmpv.size(); i++) {
					// 获得项目号
					Hashtable tmph = (Hashtable) tmpv.get(i);
					String AccItemNo = Tools.trimNull((String) tmph
							.get("result".toUpperCase()));
					String AccItemName = Tools.trimNull((String) tmph
							.get("name".toUpperCase()));

					// logger.info("resultset:" + CURRNO );

					// 按照拼写规则连接返回数据
					returnStr = returnStr + AccItemNo + "|#|" + AccItemName
							+ "|#|";
				}

				// 检索结果为空
				if (tmpv.size() == 0) {
					setMsg("下拉框元素检索失败，失败原因：系统中没有下拉框信息");
					req.setTransMsg(getMsg());
				}
			}

			// 对返回字符串的末尾进行修改，删除最后一个"|#|"
			if (returnStr.length() > 0) {
				if (returnStr.endsWith("|#|")) {
					tmpStr = titleStr
							+ returnStr.substring(0, returnStr.length() - 3);
				} else {
					tmpStr = titleStr + returnStr;
				}
			}
		} catch (Exception e) {
			logger.info("得到帐户权限号列表出错：" + e.toString());
			setMsg("下拉框元素检索失败，失败原因：数据库操作异常");
			req.setTransFlag(-1);
			req.setTransMsg(getMsg());
		}
		// 返回完整的打包后的数据流
		req.setTransFlag(0);
		// logger.info("[returnStr:]" + tmpStr);
		req.setResultObj(tmpStr);
		return;
	}

	@SuppressWarnings("rawtypes")
	public void LoadUserAccountItem() {

		// 固定格式，不必修改

		ArrayList tmpv = null;

		String sqlStr = ""; // 查询字符串
		String returnStr = "|"; // 按照一定规则形成的查询结果字符串
		StringBuffer returnStrBuffer = new StringBuffer("|");
		String titleStr = "ItemNo|="; // 返回前台的字符串的包头
		String tmpStr = ""; // 完整的返回前台的字符串

		try {
			sqlStr = "select b.ItemNo  result, b.ItemName itemname from T_USER_PRIVATE a,T_ITEM b where a.INst_No='"
					+ InstID
					+ "' and a.USER_CODE='"
					+ UserID
					+ "' and Private_Type='3' and to_number(a.Private_Code)=b.ItemNo order by to_number(a.Private_Code)";

			logger.info(sqlStr);
			tmpv = dbclass.doQuery(sqlStr, 0, 0);
			if (tmpv == null) {
				DbSetMsg(dbclass.getMsg());
				logger.debug("下拉框元素检索时出错：" + dbclass.getMsg());
				req.setTransFlag(-1);
				req.setTransMsg(getMsg());
			} else {
				for (int i = 0; i < tmpv.size(); i++) {
					// 获得项目号
					Hashtable tmph = (Hashtable) tmpv.get(i);
					String ITEMNO = Tools.trimNull((String) tmph.get("result"
							.toUpperCase()));
					String ITEMNAME = Tools.trimNull((String) tmph
							.get("itemname".toUpperCase()));

					// 按照拼写规则连接返回数据
					// returnStr = returnStr + ITEMNO + "|#|" + ITEMNAME + "|#|"
					// ;
					returnStrBuffer.append(ITEMNO + "|#|" + ITEMNAME + "|#|");
				}

				// 检索结果为空
				if (tmpv.size() == 0) {
					setMsg("下拉框元素检索失败，失败原因：系统中没有下拉框信息");
					req.setTransMsg(getMsg());
				}
			}

			returnStr = returnStrBuffer.toString();

			// 对返回字符串的末尾进行修改，删除最后一个"|#|"
			if (returnStr.length() > 0) {
				if (returnStr.endsWith("|#|")) {
					tmpStr = titleStr
							+ returnStr.substring(0, returnStr.length() - 3);
				} else {
					tmpStr = titleStr + returnStr;
				}
			}
		} catch (Exception e) {
			logger.info("得到项目号列表出错：" + e.toString());
			setMsg("下拉框元素检索失败，失败原因：数据库操作异常");
			req.setTransFlag(-1);
			req.setTransMsg(getMsg());
		}
		// 返回完整的打包后的数据流
		req.setTransFlag(0);
		// logger.info("[returnStr:]" + tmpStr);
		req.setResultObj(tmpStr);
		return;
	}

	public void WriteDBProc() throws ServletException, IOException {

		String fileDate = req.getParameter("fileDate");
		String fileType = req.getParameter("fileType");
		String fileName = req.getParameter("fileName");

		String valueStr = "";
		int i = -1;
		i = fileName.lastIndexOf('\\');
		if (i >= 0)
			fileName = fileName.substring(i + 1);
		else {
			i = fileName.lastIndexOf('/');
			if (i >= 0)
				fileName = fileName.substring(i + 1);
		}

		if (fileType.startsWith("ftp")) {
			valueStr = Pub.commServerByTcp("9999" + fileDate + "|" + fileType
					+ "|" + fileName + "|");
			System.out.println("commServerByTcp返回:" + valueStr);
			if (valueStr == null) {
				req.setTransFlag(-1);
				req.setTransMsg("连接服务器失败");
				return;
			}
		} else
			valueStr = FileToDBS(fileDate, fileType, fileName);
		if (!valueStr.startsWith("0000")) {
			req.setTransFlag(-1);
			req.setTransMsg(valueStr.substring(4));
		} else {
			req.setTransFlag(0);
			req.setTransMsg(valueStr.substring(4));
		}
		return;
	}

	String FileToDBS(String fileDate, String FileMode, String FileName) {
		String tmpStr = "0000成功";
		return tmpStr;
	}

	@SuppressWarnings("rawtypes")
	public void getUserLevel(String inst_no) {

		ArrayList tmpv = null;
		Hashtable tmph;

		try {
			String caption = "user_level|=";
			String returnStr = "|";

			String sqlStr = "select distinct INST_LEVEL  from  T_INST_INFO   where INST_NO = '"
					+ inst_no + "' ";
			tmpv = dbclass.doQuery(sqlStr, 0, 0);
			if (tmpv == null) {
				DbSetMsg(dbclass.getMsg());
				logger.debug("获取用户级别时出错：" + dbclass.getMsg());
				req.setTransFlag(-1);
				req.setTransMsg(getMsg());
				return;
			} else if (tmpv.size() == 0) {
				setMsg("没有该机构所对应的用户级别！");
				req.setTransFlag(-1);
				req.setTransMsg(getMsg());
			} else {
				tmph = (Hashtable) tmpv.get(0);
				String inst_level = Tools.trimNull((String) tmph
						.get("INST_LEVEL".toUpperCase()));

				int instlevel = Integer.parseInt(inst_level);
				if (instlevel <= 1) {
					returnStr = returnStr + "1";
				} else if (instlevel == 2) {
					returnStr = returnStr + "2";
				} else {
					returnStr = returnStr + "3";
				}

				returnStr = caption + returnStr;
				req.setTransFlag(0);
				logger.info(returnStr);
				req.setResultObj(returnStr);
			}

		} catch (Exception e) {
			logger.info("获取用户级别出错：" + e.toString());
			setMsg("获取用户级别出错");
			req.setTransFlag(-1);
			req.setTransMsg(getMsg());

			/* 日志写法参见相关文档 */
		}
		return;
	}

	/**
	 * 获取报表编号
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public void getReportNo() {

		// 固定格式，不必修改

		ArrayList tmpv = null;

		String sqlStr = "";
		String returnStr = "|";
		StringBuffer returnStrBuffer = new StringBuffer("|");
		String titleStr = "Report_No|=";
		String tmpStr = "";

		try {

			sqlStr = " select a.QueryNo QueryNo,a.Name Name  from t_accquery a"
					+ ",(select private_code from t_user_private "
					+ " where inst_no='"
					+ InstID
					+ "' and user_code='"
					+ UserID
					+ "' and private_type = '4'"
					+ " union "
					+ " SELECT PRIVATE_CODE from t_role_private where Role_Code in"
					+ " ( select private_code from t_user_private where inst_no='"
					+ InstID + "' and user_code='" + UserID + "'"
					+ " and private_type='1' ) and Private_Type = '4'" + " ) b"
					+ " where a.QueryNo=b.private_code  "
					+ " order by a.queryNo ";

			if (InstID.equalsIgnoreCase("0000")
					&& UserID.equalsIgnoreCase("Admin")) {
				sqlStr = " select a.QueryNo QueryNo,a.Name Name  from t_accquery a"
						+ " order by a.queryNo ";
			}

			logger.debug("sqlStr=" + sqlStr);

			tmpv = dbclass.doQuery(sqlStr, 0, 0);
			if (tmpv == null) {
				// 数据库操作失败
				DbSetMsg(dbclass.getMsg());
				logger.debug("下拉框载入时出错：" + dbclass.getMsg());
				req.setTransFlag(-1);
				req.setTransMsg(getMsg());
			} else {

				String other_Info = "";// 记录其它信息

				for (int i = 0; i < tmpv.size(); i++) {
					// 获得机构编号和机构名称
					Hashtable tmph = (Hashtable) tmpv.get(i);
					String QueryNo = Tools.trimNull((String) tmph.get("QueryNo"
							.toUpperCase()));// 报表编号
					String Name = Tools.trimNull((String) tmph.get("Name"
							.toUpperCase()));// 报表名称

					if (QueryNo.equalsIgnoreCase("999")) {
						other_Info = QueryNo + "|#|" + Name + "|#|";
					} else {
						returnStrBuffer.append(QueryNo + "|#|" + Name + "|#|");
					}
				}

				if (!other_Info.equalsIgnoreCase("")) {
					returnStrBuffer.append(other_Info);
				}

				// 检索结果为空
				if (tmpv.size() == 0) {
					setMsg("下拉框元素检索失败，失败原因：系统中没有下拉框信息");
					req.setTransMsg(getMsg());
				}
			}

			returnStr = returnStrBuffer.toString();

			// 对返回字符串的末尾进行修改，删除最后一个"|#|"
			if (returnStr.length() > 0) {
				if (returnStr.endsWith("|#|")) {
					tmpStr = titleStr
							+ returnStr.substring(0, returnStr.length() - 3);
				} else {
					tmpStr = titleStr + returnStr;
				}
			}

		} catch (Exception e) {
			logger.info("得到用户列表出错：" + e.toString());
			setMsg("下拉框元素检索失败，失败原因：数据库操作异常");
			req.setTransFlag(-1);
			req.setTransMsg(getMsg());
		}
		// 返回完整的打包后的数据流
		req.setTransFlag(0);
		req.setResultObj(tmpStr);
		return;
	}

	@SuppressWarnings("rawtypes")
	public void Get_SearchList() {

		String notice_title = req.getParameter("notice_title");
		String use_flag = req.getParameter("use_flag");
		String flag = req.getParameter("flag");

		/* 建立公共信息，不必修改 */

		ArrayList tmpv = null;
		if (true) {
			try {
				/*
				 * 模糊查询条件
				 */
				String whereStr = "";
				boolean andFlag = false;
				String count = "0";

				/*
				 * 判断存在的查询条件
				 */
				if ((notice_title != null) && (!notice_title.equals(""))) {
					whereStr = whereStr + " TITLE like '%" + notice_title
							+ "%' ";
					andFlag = true;
				}
				if ((use_flag != null) && (!use_flag.equals(""))) {
					if (andFlag == true)
						whereStr = whereStr + "and ";
					whereStr = whereStr + " USE_FLAG = '" + use_flag + "' ";
					andFlag = true;
				}

				/*
				 * 获得查询语句
				 */
				String sqlStr = "select TITLE,IMPORTANT_FLAG,USE_FLAG,UPDATE_DATE from T_NOTICE ";
				String allStr = "select count(*) numcount from T_NOTICE";
				if (andFlag == true)
					whereStr = " where " + whereStr;
				// whereStr = whereStr + " INST_NO ='" +inst_no+ "'";

				sqlStr = sqlStr + whereStr + " order by UPDATE_DATE DESC ";
				allStr = allStr + whereStr;

				/*
				 * 返回结果
				 */
				String returnStr = "|";
				StringBuffer returnStrBuffer = new StringBuffer("|");
				String titleStr = "";

				/*
				 * 第一次查询时，才返回总页数，不考虑查询过程中页数变化
				 */
				tmpv = dbclass.doQuery(allStr, 0, 0);
				logger.info(allStr);
				logger.info("查询总结果数");
				if (tmpv == null) {
					DbSetMsg(dbclass.getMsg());
					logger.debug("公告列表查询异常：" + dbclass.getMsg());
					req.setTransFlag(-1);
					req.setTransMsg(getMsg());
					return;
				} else {
					String strC = "1";
					if (tmpv.size() > 0) {
						Hashtable tmph = (Hashtable) tmpv.get(0);
						count = Tools.trimNull((String) tmph.get("numcount"
								.toUpperCase()));

						if (count.equalsIgnoreCase("0")) {
							setMsg("无满足条件的记录");
							req.setTransFlag(-1);
							req.setTransMsg(getMsg());
							return;
						}

						/*
						 * 每页20条记录
						 */
						logger.info("每页" + LinesCount);

						/*
						 * 页数　
						 */
						logger.info("查询得到总记录数：" + count);

						int numC = Integer.parseInt(count) / LinesCount;
						logger.info("numC=" + String.valueOf(numC));

						/*
						 * 最后一页行数
						 */
						int numtail = Integer.parseInt(count) % LinesCount;
						if (numtail > 0)
							numC++;
						strC = String.valueOf(numC);
					}

					if (strC.equalsIgnoreCase("0"))
						strC = "1";

					if (flag.equalsIgnoreCase("0")) {
						// returnStr = returnStr + strC + "^";
						returnStrBuffer.append(strC + "^");
						titleStr = titleStr + "totalPage^";
					}
				}

				/* 查询列表信息　 */
				logger.info("查询具体信息");

				/* 查找当前页数　 */
				/* 查找当前页数　 */
				int total_num = Integer.parseInt(count);
				int intbegin = 0;
				int intend = 0;

				if (flag.equalsIgnoreCase("0") && currentPage == 1) {
					intbegin = 1;
					intend = total_num > LinesCount ? LinesCount : total_num;
				}
				if (flag.equalsIgnoreCase("1")) {
					intbegin = (currentPage - 2) * LinesCount + 1;
					intend = (currentPage - 1) * LinesCount;
					intend = total_num > intend ? intend : total_num;

					// intbegin=(intCurPage-2)*numCount+1;
					// intend=(intCurPage-1)*numCount;
				}
				if (flag.equalsIgnoreCase("2")) {
					intbegin = currentPage * LinesCount + 1;
					intend = (currentPage + 1) * LinesCount;
					intend = total_num > intend ? intend : total_num;

				}
				if (flag.equalsIgnoreCase("0") && currentPage > 1) {
					intbegin = (currentPage - 1) * LinesCount + 1;
					intend = currentPage * LinesCount;
					intend = total_num > intend ? intend : total_num;
				}

				intbegin--; // begin from 0;
				// begin = String.valueOf(intbegin);
				// end = String.valueOf(intend);
				// logger.info("begin:end"+begin+":"+end);

				/**
				 * 查询列表信息
				 **/
				logger.info("查询具体信息" + sqlStr);
				logger.info("intbegin=" + String.valueOf(intbegin));
				logger.info("intend=" + String.valueOf(intend));
				tmpv = dbclass.doQuery(sqlStr, 0, 0);
				if (tmpv == null) {
					DbSetMsg(dbclass.getMsg());
					logger.debug("取公告列表异常: " + dbclass.getMsg());
					req.setTransFlag(-1);
					req.setTransMsg(getMsg());
				} else {
					for (int i = intbegin; i < intend; i++) {
						Hashtable tmph = (Hashtable) tmpv.get(i);
						String TITLE = Tools.trimNull((String) tmph.get("TITLE"
								.toUpperCase()));
						String IMPORTANT_FLAG = Tools.trimNull((String) tmph
								.get("IMPORTANT_FLAG".toUpperCase()));
						String USE_FLAG = Tools.trimNull((String) tmph
								.get("USE_FLAG".toUpperCase()));
						String UPDATE_DATE = Tools.trimNull((String) tmph
								.get("UPDATE_DATE".toUpperCase()));

						// 可读转换
						if (IMPORTANT_FLAG.equals("0"))
							IMPORTANT_FLAG = IMPORTANT_FLAG + " 一般";
						else if (IMPORTANT_FLAG.equals("1"))
							IMPORTANT_FLAG = IMPORTANT_FLAG + " 重要";
						else
							IMPORTANT_FLAG = IMPORTANT_FLAG + " - ";

						if (USE_FLAG.equals("0"))
							USE_FLAG = USE_FLAG + " 无效";
						else if (USE_FLAG.equals("1"))
							USE_FLAG = USE_FLAG + " 有效";
						else
							USE_FLAG = USE_FLAG + " - ";

						UPDATE_DATE = Pub.ToDateFormat(UPDATE_DATE);

						// returnStr = returnStr + TITLE+ "|#|" + IMPORTANT_FLAG
						// + "|#|" + USE_FLAG + "|#|" + UPDATE_DATE + "|#|";
						returnStrBuffer.append(TITLE + "|#|" + IMPORTANT_FLAG
								+ "|#|" + USE_FLAG + "|#|" + UPDATE_DATE
								+ "|#|");
					}
					if (tmpv.size() == 0) {
						setMsg("没有满足条件的公告");
						req.setTransMsg(getMsg());
					}
					String tempStr = "";

					returnStr = returnStrBuffer.toString();

					if (returnStr.length() > 0) {
						titleStr = titleStr + "search_list|=";
						/*
						 * table 的name 和id
						 */
						if (returnStr.endsWith("|#|"))
							tempStr = titleStr
									+ returnStr.substring(0,
											returnStr.length() - 3);
						else
							tempStr = titleStr + returnStr;
					}
					req.setTransFlag(0);
					// logger.info("[tmpStr:]" + tempStr);
					req.setResultObj(tempStr);
					// result.addResultSet(rvs);

				}

			} catch (Exception e) {
				logger.info("得到公告列表出错：" + e.toString());
				setMsg("得到公告列表出错");
				req.setTransFlag(-1);
				req.setTransMsg(getMsg());
				/* 日志写法参见相关文档　 */
			}

		}

		return;
	}

	@SuppressWarnings("rawtypes")
	public void Notice_Add()

	{

		String notice_title = req.getParameter("notice_title");
		String use_flag = req.getParameter("use_flag");
		String notice_content = req.getParameter("notice_content");
		String important_flag = req.getParameter("important_flag");

		ArrayList tmpv = null;
		String sqlStr = "";

		/*
		 * 根据实际情况添加权限逻辑 (略)
		 */

		/*
		 * 根据机构编号(Session Var),操作员编号 (Session Var),查询操作员角色 进行权限控制（采用权限控制函数）
		 */

		if (true) {

			// 判断是否重复
			sqlStr = "select TITLE from T_NOTICE where TITLE='" + notice_title
					+ "' ";
			tmpv = dbclass.doQuery(sqlStr, 0, 0);
			// logger.info(sqlStr);
			if (tmpv == null) {
				DbSetMsg(dbclass.getMsg());
				logger.debug("查询公告时出错：" + dbclass.getMsg());
				req.setTransFlag(-1);
				req.setTransMsg(getMsg());
				return;
			}
			if (tmpv.size() != 0) {
				logger.info("新增失败！标题重复：" + sqlStr);
				setMsg("新增失败！公告标题重复。");
				req.setTransFlag(-1);
				req.setTransMsg(getMsg());
				return;
			}

			// 取系统日期
			String nowTime = Pub.GetNowDate("yyyyMMddhhmmss");

			// 新增信息
			sqlStr = " insert into T_NOTICE(TITLE, NOTICE, IMPORTANT_FLAG, USE_FLAG,CREATE_DATE,CREATE_USER,UPDATE_DATE,UPDATE_USER) "
					+ "values('"
					+ notice_title
					+ "','"
					+ notice_content
					+ "','"
					+ important_flag
					+ "','"
					+ use_flag
					+ "','"
					+ nowTime
					+ "','"
					+ UserID
					+ "','"
					+ nowTime
					+ "','"
					+ UserID + "')";

			logger.info("开始增加. " + sqlStr);
			if (!dbclass.executeUpdate(sqlStr)) {
				DbSetMsg(dbclass.getMsg());
				logger.info("新增公告出错：" + dbclass.getMsg());
				req.setTransFlag(-1);
				req.setTransMsg(getMsg());
			} else {
				logger.info("新增公告成功");
				setMsg("新增公告成功!");
				req.setTransFlag(0);
				req.setTransMsg(getMsg());

			}

		}
		return;
	}

	/**
	 * Function 更新查询 输入列表： String notice_title: 公告标题
	 * 
	 * 输出参数： 得到公告信息
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public void Notice_Query() {

		String notice_title = req.getParameter("notice_title");

		ArrayList tmpv = null;
		String sqlStr = "";
		String returnStr = "|";
		String tempStr = "";

		try {
			sqlStr = "select * from T_NOTICE where TITLE = '" + notice_title
					+ "' ";
			tmpv = dbclass.doQuery(sqlStr, 0, 0);
			// logger.info(sqlStr);

			if (tmpv == null) {
				DbSetMsg(dbclass.getMsg());
				logger.debug("取公告信息时出错：" + dbclass.getMsg());
				req.setTransFlag(-1);
				req.setTransMsg(getMsg());
			}

			if (tmpv.size() == 0) {
				setMsg("没有满足条件的公告信息！");
				req.setTransFlag(-1);
				req.setTransMsg(getMsg());
			}

			else {
				// 要存到的变量名
				String caption = "notice_title2^notice_content2^important_flag2^use_flag2^create_date2^create_id2^update_date2^update_id2|=";

				Hashtable tmph = (Hashtable) tmpv.get(0);
				String TITLE = Tools.trimNull((String) tmph.get("TITLE"
						.toUpperCase()));
				String NOTICE = Tools.trimNull((String) tmph.get("NOTICE"
						.toUpperCase()));
				String IMPORTANT_FLAG = Tools.trimNull((String) tmph
						.get("IMPORTANT_FLAG".toUpperCase()));
				String USE_FLAG = Tools.trimNull((String) tmph.get("USE_FLAG"
						.toUpperCase()));
				String CREATE_DATE = Tools.trimNull((String) tmph
						.get("CREATE_DATE".toUpperCase()));
				String CREATE_USER = Tools.trimNull((String) tmph
						.get("CREATE_USER".toUpperCase()));
				String UPDATE_DATE = Tools.trimNull((String) tmph
						.get("UPDATE_DATE".toUpperCase()));
				String UPDATE_USER = Tools.trimNull((String) tmph
						.get("UPDATE_USER".toUpperCase()));

				// 可读转换
				CREATE_DATE = Pub.ToDateFormat(CREATE_DATE);
				UPDATE_DATE = Pub.ToDateFormat(UPDATE_DATE);

				returnStr = returnStr + TITLE + "^" + NOTICE + "^"
						+ IMPORTANT_FLAG + "^" + USE_FLAG + "^" + CREATE_DATE
						+ "^" + CREATE_USER + "^" + UPDATE_DATE + "^"
						+ UPDATE_USER;

				tempStr = caption + returnStr;
				req.setTransFlag(0);
				logger.info("[tempStr:]" + tempStr);
				req.setResultObj(tempStr);
			}
		} catch (Exception e) {
			logger.info("得到公告信息信息出错：" + e.toString());
			setMsg("公告信息查询失败，失败原因：数据库操作异常");
			req.setTransFlag(-1);
			req.setTransMsg(getMsg());
		}

		return;
	}

	/**
	 * 　 Function: 修改公告信息 输入参数： String notice_title: 公告标题 String notice_content:
	 * 公告内容 String important_flag: 重要标志 String use_flag: 有效标志
	 * 
	 * 
	 * 输出参数:修改公告信息是否成功
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public void Notice_Update() {

		String notice_title = req.getParameter("notice_title");
		String notice_content = req.getParameter("notice_content");
		String important_flag = req.getParameter("important_flag");
		String use_flag = req.getParameter("use_flag");

		/* 固定格式，不必修改　 */

		ArrayList tmpv = null;

		if (true) {
			// 查询是否存在
			String queryStr = "select TITLE from T_NOTICE where TITLE ='"
					+ notice_title + "' ";
			tmpv = dbclass.doQuery(queryStr, 0, 0);
			if (tmpv == null) {
				DbSetMsg(dbclass.getMsg());
				logger.debug("修改公告信息时出错：" + dbclass.getMsg());
				setMsg("查询公告信息时出错！数据库错误。");
				req.setTransFlag(-1);
				req.setTransMsg(getMsg());
				return;
			}
			if (tmpv.size() == 0) {
				logger.info("公告信息修改失败，不存在这样的公告[" + notice_title + "].");
				setMsg("修改的公告信息不存在[" + notice_title + "]!");
				req.setTransFlag(-1);
				req.setTransMsg(getMsg());
				return;
			}

			String nowTime = Pub.GetNowDate("yyyyMMddhhmmss");
			/*
			 * 修改信息
			 */
			String sqlStr = "update T_NOTICE set NOTICE='" + notice_content
					+ "'," + "IMPORTANT_FLAG='" + important_flag + "',"
					+ "USE_FLAG='" + use_flag + "'," + "UPDATE_DATE='"
					+ nowTime + "'," + "UPDATE_USER='" + UserID + "'"
					+ " where TITLE ='" + notice_title + "' ";
			if (!dbclass.executeUpdate(sqlStr)) {
				DbSetMsg(dbclass.getMsg());
				logger.info("修改公告信息时出错：" + dbclass.getMsg());
				req.setTransFlag(-1);
				req.setTransMsg(getMsg());
			} else {
				setMsg("公告信息修改成功!");
				req.setTransFlag(0);
				req.setTransMsg(getMsg());

			}
		}
		return;
	}

	/**
	 * 　 Function: 删除公告 输入参数： String notice_title: 公告标题
	 * 
	 * String TransactionName：交易名 String InstID： 当前用户机构编号，Session变量中 String
	 * UserID： 当前用户机构编号，Session变量中 、 输出参数: 删除公告是否成功
	 * 
	 **/
	@SuppressWarnings("rawtypes")
	public void Notice_Del() {

		String notice_title = req.getParameter("notice_title");

		String sqlStr = "";
		ArrayList tmpv = null;

		if (true) {
			// 查询是否存在
			String queryStr = "select TITLE from T_NOTICE where TITLE ='"
					+ notice_title + "' ";
			tmpv = dbclass.doQuery(queryStr, 0, 0);
			if (tmpv == null) {
				DbSetMsg(dbclass.getMsg());
				logger.debug("删除公告时出错：" + dbclass.getMsg());
				setMsg("查询公告时出错！数据库错误。");
				req.setTransFlag(-1);
				req.setTransMsg(getMsg());
				return;
			}
			if (tmpv.size() == 0) {
				logger.info("公告删除失败，不存在这样的公告[" + notice_title + "].");
				setMsg("删除的公告[" + notice_title + "]不存在!");
				req.setTransFlag(-1);
				req.setTransMsg(getMsg());
				return;
			}

			sqlStr = " delete from T_NOTICE where TITLE ='" + notice_title
					+ "' ";
			if (!dbclass.executeUpdate(sqlStr)) {
				DbSetMsg(dbclass.getMsg());
				logger.info("删除公告时出现异常:" + dbclass.getMsg());
				req.setTransFlag(-1);
				req.setTransMsg(getMsg());
			} else {
				String FlagStr = "JumpFlag|=|1";
				req.setTransFlag(0);
				req.setTransMsg("删除成功!");
				req.setResultObj(FlagStr);

			}
		}
		return;
	}
}
