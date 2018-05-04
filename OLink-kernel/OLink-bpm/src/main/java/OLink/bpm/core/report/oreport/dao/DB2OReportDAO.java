package OLink.bpm.core.report.oreport.dao;

import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;

import OLink.bpm.core.dynaform.document.dql.DB2SQLFunction;

public class DB2OReportDAO extends AbstractOReportDAO implements OReportDAO{

	public DB2OReportDAO(Connection conn, String applicationId) throws Exception {
		super(conn, applicationId);
		sqlFuction = new DB2SQLFunction();
	}

	static {
		singleFuncs[0] = "year(column)";//年
		singleFuncs[1] = "quarter(column)";//季度
		singleFuncs[2] = "month(column)";//月
		singleFuncs[3] = "week_iso(column)";//周
		singleFuncs[4] = "dayofweek_iso(column)";//工作日
		singleFuncs[5] = "day(column)";//日
		singleFuncs[6] = "hour(column)";//小时
		
		doubleFuncs[0] = "'Q'||trim(char(quarter(column)))||' '||trim(char(year(column)))";//季度&年
		doubleFuncs[1] = "trim(char(month(column)))||'-'||trim(char(year(column)))";//月&年
		doubleFuncs[2] = "'W'||trim(char(week_iso(column)))||' '||trim(char(year(column)))";//周&年
		
		sDateFuncs[0] = "date(column)";//全日期
		sDateFuncs[1] = "trim(char(date(column)))||' '||trim(char(time(column)))";//日期&时间
		
		//by子句函数
		doubleBys[0] = "year(column), quarter(column)";//季度&年
		doubleBys[1] = "year(column), month(m, column)";//月&年
		doubleBys[2] = "year(yyyy, column), week_iso(ww, column)";//周&年
		
		sDateBys[0] = sDateFuncs[0];
		sDateBys[1] = "date(column), time(column)";//日期&时间
		
		itemLabelFuncs[0] = "'Q'||trim(char(quarter(column)))||' '||trim(char(year(column)))";//季度&年
		itemLabelFuncs[1] = "trim(char(month(column)))||'-'||trim(char(year(column)))";//月&年
		itemLabelFuncs[2] = "'W'||trim(char(week_iso(column)))||' '||trim(char(year(column)))";//周&年

		funcMath[0] = "quarter(column) + year(column)*4 between smallCount and bigCount";
		funcMath[1] = "month(column) + year(column)*12 between smallCount and bigCount";
		funcMath[2] = "week(column) + year(column)*53 between smallCount and bigCount";
		
		funcMath[3] = "date(column) between date('earlyDate') and date('laterDate')";
		funcMath[4] = "timestamp(column) between timestamp('earlyDate') and timestamp('laterDate')";
		
		stdFunc = "stddev";
	}

	public Map<String, String> getCeilFuncSqlAndBy(String column, double interval, String method) {
		//select trim(char(ceil(BugID/300)*300))||' to '||trim(char((ceil(BugID/300)+1)*300)) xAxis,
	    //       count(BugID) yAxis
	    //from buglist group by ceil(BugID/300) order by ceil(BugID/300)
		String func = "ceil(" + column + "/" + interval + ")";
		String sql = "select trim(char(" + func + "*" + interval +"))||' to '||trim(char((" + func + "+1)*" + interval + ")) xAxis, " + method + " yAxis0\n";
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("sql", sql);
		map.put("by", func);
		return map;
	}
}