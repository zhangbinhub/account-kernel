/*
 * 创建日期: 2007-07-12
 * 创建人:	 fanxg
 * 功能说明: 把字符串转成用户可读的格式
 */
package services;

import java.util.ArrayList;
import java.util.Hashtable;

import eWAP.core.dbaccess.ConnectionFactory;
import eWAP.core.Tools;

//import java.io.*;
//import java.util.*;
//import java.util.Date;
//import java.sql.ResultSet;
//import com.nantian.webdt.control.trans.void;

//import org.jdom.Element;

//import com.nantian.webdt.control.trans.dbaccess.ConnectionFactory;
//import com.nantian.webdt.util.*;
//import java.sql.Statement;
/**
 * @author administrator
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class userReadableFormat extends Base{

	/**
	 *
	 */
	boolean debug = false;
//	private ConnectionFactory dbclass = null;

	/**
	 * Logger for this class
	 */
	public userReadableFormat() {
		super();
	}


	/**
	 * Method：String ToMoneyFormat(String str_temp)
	 * Function 把str_temp转化成RMB的格式，带2位小数的。
	 * 输入列表：
	 *     String  str_temp:  要转化的字符串
	 *
	 * 输出参数：返回转化成RMB格式的字符串
	 *
	 */
	public String ToMoneyFormat(String str_temp)
	{
		int i=0;  //循环变量

		if(str_temp.equals("")){  //处理为空的情况
			str_temp="0";
		}

		if(str_temp.substring(0,1).equals(".")){
			if(str_temp.length() == 1){  //处理 "."的情况
				str_temp += "00";
			}
			str_temp = "0" + str_temp; //处理".12"的情况
		}

		String[] Array_temp=str_temp.split("\\.");
		if(Array_temp.length == 1)      //如果数组的大小是1，说明没有小数点，+ “.00”，表示两为精度
		{
			str_temp += ".00";
		}
		else{
			if(Array_temp[1].length() == 1) //如果只有一位小数，多加一个零
				str_temp += "0";
		}

		//把上面转换完成的数转换为带点的形式，如：123456变为：123，456
		Array_temp=str_temp.split("\\.");
		String str1=Array_temp[0];
		String str2=Array_temp[1];

		int signa=0;  //在前面加了几个零
		int interlen=str1.length();

		if(interlen%3==1)
		{
			str1="00"+str1;
			signa=2;
		}
		if(interlen%3==2)
		{
			str1="0"+str1;
			signa=1;
		}

		int tt=(str1.length())/3; //要加几个逗号
		String[] mm=new String[tt]; //分别存逗号分开的字符串
		for(i=0; i<tt; i++)
		{
			mm[i]=str1.substring(i*3,3+i*3);
		}

		String result="";

		for(i=0;i<mm.length;i++)
		{
			result += mm[i];
			result += ",";
		}

		result=result.substring(signa,result.length()-1); //去前面加过的零
		//  System.out.println("结果为："+result);
		result=result+ "." + str2;

		return result;
	}

	/** (光大风险抵押金)
	 * Method：String AccountStatus(String flag, int isCDM)
	 * Function 根据str，显示成用户可读的状态
	 * 输入列表：
	 *     String  flag:  状态值
	 *     String  isCDM: 是否是活期帐户，"0"表示是活期帐户
	 *
	 * 输出参数：返回用户可读的状态字符串，帐户状态
	 *
	 */
	public String AccountStatus(String flag, String isCDM)
	{
		String status=""; //返回的状态
		int length2=flag.length()-1; //最后一位   [7]帐户状态：0-正常，1-销户，2-取消
		int length3=flag.length()-3; //倒数第三位 [5]足额否：0-是，1-否

		//1. 根据最后一位来判断状态
		if(flag.charAt(length2)=='0'){
			status="正常";
			//2. 是活期的时候判断是否足额
			if(isCDM.equals("0")){
				if(flag.charAt(length3) == '0')
					status += "(已足额)";
				else if(flag.charAt(length3) == '1')
					status += "(未足额)";
				else
					status += "-"; //未知
			}
		}
		else if(flag.charAt(length2)=='1')
			status="销户";
		else if (flag.charAt(length2)=='2')
			status="取消";
		else
			status="-";	//未知

		return status;
	}

	/** (光大风险抵押金)
	 * Method：String RateState(String str)
	 * Function 根据str，显示成用户可读的状态
	 * 输入列表：
	 *     String  str:  状态值
	 *
	 * 输出参数：返回用户可读的利率状态字符串
	 *
	 */
	public String RateState(String str)
	{
		int str_temp=str.length()-1;

		if(str.charAt(str_temp)=='0') //根据最后一位来判断
		{
			str="无效";
		}
		else if(str.charAt(str_temp)=='1')
		{
			str="有效";
		}
		else
		{
			str="未知";
		}

		return str;
	}

	/** （光大风险抵押金）
	 * Method：String AccountType(String str)
	 * Function 根据str，显示成用户可读的状态
	 * 输入列表：
	 *     String  str:  帐户序号
	 *
	 * 输出参数：返回用户可读的帐户类别（定期/活期）
	 *
	 */
	public String AccountType(String str)
	{
		if(str.equals("000"))
			str = "000/活期";
		else
			str = str + "/定期";

		return str;
	}

	/** （光大风险抵押金）
	 * Method：String InstType(String str)
	 * Function 根据str，显示成用户可读的状态
	 * 输入列表：
	 *     String  str:  机构级别
	 *
	 * 输出参数：返回用户可读的机构级别(营业部/支行/网点……)
	 *
	 */
	public String InstType(String str)
	{
		if(str.equals("0"))
			str = "0 营业部";
		else if(str.equals("1"))
			str = "1 处理中心";
		else if(str.equals("2"))
			str = "2 支行";
		else if(str.equals("3"))
			str = "3 营业网点";
		else if(str.equals("4"))
			str = "4 分理处";
		else if(str.equals("5"))
			str = "5 其它";
		else
			str="- 未知级别";

		return str;
	}

	/**
	 * Method：String ToDateFormat(String str)
	 *
	 * Function: 把str转成用户可读的日期格式，1.yyyyMMdd转成yyyy-MM-dd; 2.hhmmss转成hh:mm:ss; 3. yyyyMMddhhmmss转成yyyy-MM-dd hh:mm:ss，其他不认识的原样返回。
	 * 输入列表：
	 *     String  str:  日期串
	 *
	 * 输出参数：返回用户可读格式
	 *
	 * @author fanxg
	 *
	 */
	public String ToDateFormat(String str)
	{
		//yyyyMMdd
		if(str.length() == 8){
			return str.substring(0,4) + "-" + str.substring(4,6) + "-" + str.substring(6,8);
		}
		//hhmmss
		if(str.length() == 6){
			return str.substring(0,2) + ":" + str.substring(2,4) + ":" + str.substring(4,6);
		}
		//yyyyMMddhhmmss
		if(str.length() == 14){
			return str.substring(0,4) + "-" + str.substring(4,6) + "-" + str.substring(6,8) + " " +str.substring(8,10) + ":" + str.substring(10,12) + ":" + str.substring(12,14);
		}

		return str; //都不是上面的格式则原样返回


	}

	/**(工行 - 考试系统)
	 * Method：String ExamItem(String exam_code)
	 *
	 * Function: 查询exam_code在题考试项目表中对应的名称，组合后返回 [exam_code exam_name]; 如果失败或者异常，都原样返回
	 * 输入列表：
	 *     String  exam_code:  考试项目代码
	 *
	 * 输出参数：返回用户可读格式
	 *
	 * @author fanxg
	 *
	 */
	@SuppressWarnings("rawtypes")
	public String ExamItem(String exam_code,ConnectionFactory dbclass)
	{
		ArrayList tmpv = null;

		try
		{
			//去掉联合查询，在sybase中使用出错。在下面去数据后在组合。by fanxiaogang 2007-08-07
			String sqlStr = "select distinct EXAM_CODE, EXAM_NAME from T_EXAM_ITEM where EXAM_CODE = '" + exam_code + "'";

			tmpv = dbclass.doQuery(sqlStr,0,0);
			if(tmpv==null)
			{
				//数据库操作失败
				DbSetMsg(dbclass.getMsg());
				return exam_code;

			}else
			{
				//获得题库分类代码和名称
				Hashtable tmph = (Hashtable)tmpv.get(0);
				String EXAM_CODE = Tools.trimNull((String)tmph.get("EXAM_CODE".toUpperCase()));
				String EXAM_NAME = Tools.trimNull((String)tmph.get("EXAM_NAME".toUpperCase()));

				return EXAM_CODE + " " + EXAM_NAME; //返回
			}
		}catch ( Exception e)
		{
			return exam_code;
		}
	}

	/**(工行 - 考试系统)
	 * Method：String TitleKind(String title_code)
	 *
	 * Function: 查询title_code在题库分类表中对应的名称，组合后返回 [title_code titile_name]; 如果失败或者异常，都原样返回
	 * 输入列表：
	 *     String  title_code:  题库分类代码
	 *
	 * 输出参数：返回用户可读格式
	 *
	 * @author fanxg
	 *
	 */
	@SuppressWarnings("rawtypes")
	public String TitleKind(String title_code,ConnectionFactory dbclass)
	{
		ArrayList tmpv = null;

		try
		{
			//去掉联合查询，在sybase中使用出错。在下面去数据后在组合。by fanxiaogang 2007-08-07
			String sqlStr = "select distinct TITLE_CODE, TITLE_NAME from T_EXAM_TITLE_KIND where TITLE_CODE = '" + title_code + "'";

			tmpv = dbclass.doQuery(sqlStr,0,0);
			if(tmpv==null)
			{
				//数据库操作失败
				DbSetMsg(dbclass.getMsg());
				return title_code;

			}else
			{
				//获得题库分类代码和名称
				Hashtable tmph = (Hashtable)tmpv.get(0);
				String TITLE_CODE = Tools.trimNull((String)tmph.get("TITLE_CODE".toUpperCase()));
				String TITLE_NAME = Tools.trimNull((String)tmph.get("TITLE_NAME".toUpperCase()));

				return TITLE_CODE + " " + TITLE_NAME; //返回
			}
		}catch ( Exception e)
		{
			return title_code;
		}
	}

	/**(工行 - 考试系统)
	 * Method：String TitleHead(String title_seq)
	 *
	 * Function: 查询title_seq在题库表中对应的题目，组合后返回 [title_seq title_head]; 如果失败或者异常，都原样返回
	 * 输入列表：
	 *     String  title_seq:  题目序号
	 *
	 * 输出参数：返回用户可读格式
	 *
	 * @author fanxg
	 *
	 */
	@SuppressWarnings("rawtypes")
	public String TitleHead(String title_seq,ConnectionFactory dbclass)
	{
		ArrayList tmpv = null;

		try
		{
			//去掉联合查询，在sybase中使用出错。在下面去数据后在组合。by fanxiaogang 2007-08-07
			String sqlStr = "select distinct TITLE_SEQ, TITLE_HEAD from T_EXAM_TITLE where TITLE_SEQ = '" + title_seq + "'";

			tmpv = dbclass.doQuery(sqlStr,0,0);
			if(tmpv==null)
			{
				//数据库操作失败
				DbSetMsg(dbclass.getMsg());
				return title_seq;

			}else
			{
				//获得题目序号和名称
				Hashtable tmph = (Hashtable)tmpv.get(0);
				String TITLE_SEQ = Tools.trimNull((String)tmph.get("TITLE_SEQ".toUpperCase()));
				String TITLE_HEAD = Tools.trimNull((String)tmph.get("TITLE_HEAD".toUpperCase()));

				return TITLE_SEQ + " " + TITLE_HEAD; //返回
			}
		}catch ( Exception e)
		{
			return title_seq;
		}
	}

	/**(工行 - 考试系统)
	 * Method：String TitleType(String title_type)
	 *
	 * Function: 把题型类别转换成可读格式。如果没有则原样返回。
	 * 输入列表：
	 *     String  title_type:  题型类别
	 *
	 * 输出参数：返回用户可读格式
	 *
	 * @author fanxg
	 *
	 */
	public String TitleType(String title_type)
	{
		if(title_type.equals("0"))
			title_type = title_type + " " + "未定义";
		else if(title_type.equals("1"))
			title_type = title_type + " " + "单选";
		else if(title_type.equals("2"))
			title_type = title_type + " " + "多选";
		else if(title_type.equals("3"))
			title_type = title_type + " " + "判断";
		else if(title_type.equals("4"))
			title_type = title_type + " " + "其他";

		return title_type;
	}

	/**(工行 - 考试系统)
	 * Method：String DiffQty(String diff_qty)
	 *
	 * Function: 把难度系数转换成可读格式，并返回。如果没有则原样返回。
	 * 输入列表：
	 *     String  diff_qty:  难度系数
	 *
	 * 输出参数：返回用户可读格式
	 *
	 * @author fanxg
	 *
	 */
	public String DiffQty(String diff_qty)
	{
		if(diff_qty.equals("0"))
			diff_qty = diff_qty + " " + "未定义";
		else if(diff_qty.equals("1"))
			diff_qty = diff_qty + " " + "简单";
		else if(diff_qty.equals("2"))
			diff_qty = diff_qty + " " + "基础";
		else if(diff_qty.equals("3"))
			diff_qty = diff_qty + " " + "中等";
		else if(diff_qty.equals("4"))
			diff_qty = diff_qty + " " + "难";
		else
			diff_qty = diff_qty + " " + "很难";

		return diff_qty;
	}

	/**(工行 - 考试系统)
	 * Method：PersonLevel(String person_level)
	 *
	 * Function: 把 职称代码 转换成可读格式，并返回。如果没有则原样返回。
	 * 输入列表：
	 *     String  person_level:  约定好的职称代码
	 *
	 * 输出参数：返回用户可读格式
	 *
	 * @author fanxg
	 *
	 */
	public String PersonLevel(String person_level)
	{
		if(person_level.equals("0"))
			person_level = person_level + " " + "不限";
		else if(person_level.equals("1"))
			person_level = person_level + " " + "经济员";
		else if(person_level.equals("2"))
			person_level = person_level + " " + "助理经济师";
		else if(person_level.equals("3"))
			person_level = person_level + " " + "助理会计师";
		else if(person_level.equals("4"))
			person_level = person_level + " " + "中级经济师";
		else if(person_level.equals("5"))
			person_level = person_level + " " + "中级会计师";
		else if(person_level.equals("6"))
			person_level = person_level + " " + "高级经济师";
		else if(person_level.equals("7"))
			person_level = person_level + " " + "高级会计师";
		else if(person_level.equals("8"))
			person_level = person_level + " " + "其他";

		return person_level;
	}

	/**(工行 - 考试系统)
	 * Method：EducationLevel(String education_level)
	 *
	 * Function: 把 学历代码 转换成可读格式，并返回。如果没有则原样返回。
	 * 输入列表：
	 *     String  education_level:  约定好的学历代码
	 *
	 * 输出参数：返回用户可读格式
	 *
	 * @author fanxg
	 *
	 */
	public String EducationLevel(String education_level)
	{
		if(education_level.equals("0"))
			education_level = education_level + " " + "不限";
		else if(education_level.equals("1"))
			education_level = education_level + " " + "小学";
		else if(education_level.equals("2"))
			education_level = education_level + " " + "初中";
		else if(education_level.equals("3"))
			education_level = education_level + " " + "高中(中专)";
		else if(education_level.equals("4"))
			education_level = education_level + " " + "大学(专科)";
		else if(education_level.equals("5"))
			education_level = education_level + " " + "大学(本科)";
		else if(education_level.equals("6"))
			education_level = education_level + " " + "研究生";
		else if(education_level.equals("7"))
			education_level = education_level + " " + "博士";
		else if(education_level.equals("8"))
			education_level = education_level + " " + "博士后";
		else if(education_level.equals("9"))
			education_level = education_level + " " + "其他";

		return education_level;
	}

	/**(工行 - 考试系统)
	 * Method：LineCondition(String line)
	 *
	 * Function: 把 高低代码 转换成可读格式，并返回。如果没有则原样返回。
	 * 输入列表：
	 *     String  line:  高低条件代码
	 *
	 * 输出参数：返回用户可读格式
	 *
	 * @author fanxg
	 *
	 */
	public String LineCondition(String line)
	{
		if(line.equals("1"))
			line = line + " " + "高于等于";
		else if(line.equals("2"))
			line = line + " " + "等于";
		else if(line.equals("3"))
			line = line + " " + "低于等于";

		return line;
	}

	/**(工行 - 考试系统)
	 * Method：ItemCondition(String condition)
	 *
	 * Function: 把 充要条件代码 转换成可读格式，并返回。如果没有则原样返回。
	 * 输入列表：
	 *     String  condition:  必要/或者条件代码
	 *
	 * 输出参数：返回用户可读格式
	 *
	 * @author fanxg
	 *
	 */
	public String ItemCondition(String condition)
	{
		if(condition.equals("0"))
			condition = condition + " " + "未设置";
		else if(condition.equals("1"))
			condition = condition + " " + "必要";
		else if(condition.equals("2"))
			condition = condition + " " + "或者";

		return condition;
	}

	/**(工行 - 考试系统)
	 * Method： AuditFlag(String flag)
	 *
	 * Function: 把 审核标志 转换成可读格式，并返回。如果没有则原样返回。
	 * 输入列表：
	 *     String  flag:  标志
	 *
	 * 输出参数：返回用户可读格式
	 *
	 * @author fanxg
	 *
	 */
	public String AuditFlag(String flag)
	{
		if(flag.equals("0"))
			flag = flag + " " + "未审核";
		else if(flag.equals("1"))
			flag = flag + " " + "允许";
		else if(flag.equals("2"))
			flag = flag + " " + "拒绝";

		return flag;
	}

	/**(工行 - 考试系统)
	 * Method： AuditFlag(String flag)
	 *
	 * Function: 把 性别标志 转换成可读格式，并返回。如果没有则原样返回。
	 * 输入列表：
	 *     String  flag:  标志
	 *
	 * 输出参数：返回用户可读格式
	 *
	 * @author fanxg
	 *
	 */
	public String Sex(String flag)
	{
		if(flag.equals("1"))
			flag = flag + " " + "男";
		else if(flag.equals("2"))
			flag = flag + " " + "女";

		return flag;
	}

	/**(工行 - 考试系统)
	 * Method： OpenOrClose(String flag)
	 *
	 * Function: 把 启用标志 转换成可读格式，并返回。如果没有则原样返回。
	 * 输入列表：
	 *     String  flag:  标志
	 *
	 * 输出参数：返回用户可读格式
	 *
	 * @author fanxg
	 *
	 */
	public String OpenOrClose(String flag)
	{
		if(flag.equals("1"))
			flag = flag + " " + "启用";
		else if(flag.equals("2"))
			flag = flag + " " + "关闭";

		return flag;
	}

	/**(工行 - 考试系统)
	 * Method：String Branch2_Name(String ZONENO)
	 *
	 * Function: 查询T_NFPABRP表，返回ZONENO对应的二级分行名称，组合后返回 [ZONENO NOTES]; 如果失败或者异常，都原样返回
	 * 输入列表：
	 *     String  ZONENO:  二级分行好（地区号）
	 *
	 * 输出参数：返回用户可读格式
	 *
	 * @author fanxg
	 *
	 */
	@SuppressWarnings("rawtypes")
	public String Branch2_Name(String ZONENO,ConnectionFactory dbclass)
	{
		ArrayList tmpv = null;

		final String BRTYPE_BRANCH2 = "1"; //二级分行标志（需要和数据库定义一致）

		try
		{
			//去掉联合查询，在sybase中使用出错。在下面去数据后在组合。by fanxiaogang 2007-08-07
			String sqlStr = "select distinct NOTES from T_NFPABRP where ZONENO = '" + ZONENO + "' and BRTYPE='" + BRTYPE_BRANCH2 + "' ";

			tmpv = dbclass.doQuery(sqlStr,0,0);
			if(tmpv==null)
			{
				//数据库操作失败
				DbSetMsg(dbclass.getMsg());
				return ZONENO;

			}else
			{
				//获得题库分类代码和名称
				Hashtable tmph = (Hashtable)tmpv.get(0);
				String NOTES = Tools.trimNull((String)tmph.get("NOTES".toUpperCase()));

				return ZONENO + " " + NOTES; //返回
			}
		}catch ( Exception e)
		{
			return ZONENO;
		}
	}

	/**(工行 - 考试系统)
	 * Method：String Branch3_Name(String ZONENO, String BRNO)
	 *
	 * Function: 查询T_NFPABRP表，返回ZONENO,BRNO对应的支行名称，组合后返回 [BRNO NOTES]; 如果失败或者异常，都原样返回
	 * 输入列表：
	 *     String  ZONENO:  二级分行号
	 *     String  BRNO:    网点号
	 *
	 * 输出参数：返回用户可读格式
	 *
	 * @author fanxg
	 *
	 */
	@SuppressWarnings("rawtypes")
	public String Branch3_Name(String ZONENO, String BRNO,ConnectionFactory dbclass)
	{
		ArrayList tmpv = null;

		try
		{
			//去掉联合查询，在sybase中使用出错。在下面去数据后在组合。by fanxiaogang 2007-08-07
			String sqlStr = "select distinct NOTES from T_NFPABRP where ZONENO='" + ZONENO + "' and BRNO = (select MBRNO from T_NFPABRP where ZONENO='" + ZONENO + "' and BRNO='" + BRNO + "')";

			tmpv = dbclass.doQuery(sqlStr,0,0);
			//logger.info(sqlStr);
			if(tmpv==null)
			{
				//数据库操作失败
				DbSetMsg(dbclass.getMsg());
				return ZONENO + "|" + BRNO;

			}else
			{
				//获得题库分类代码和名称
				Hashtable tmph = (Hashtable)tmpv.get(0);
				String NOTES = Tools.trimNull((String)tmph.get("NOTES".toUpperCase()));

				return ZONENO + "|" + BRNO + " " + NOTES; //返回
			}
		}catch ( Exception e)
		{
			return ZONENO + "|" + BRNO;
		}
	}

}