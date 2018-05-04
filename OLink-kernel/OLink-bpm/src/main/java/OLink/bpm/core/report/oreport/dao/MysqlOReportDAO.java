package OLink.bpm.core.report.oreport.dao;

import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;

import OLink.bpm.core.dynaform.document.dql.MysqlSQLFunction;

public class MysqlOReportDAO extends AbstractOReportDAO implements OReportDAO{

	public MysqlOReportDAO(Connection conn, String applicationId) throws Exception {
		super(conn, applicationId);
		sqlFuction = new MysqlSQLFunction();
	}
	
	static {
		//
		singleFuncs[0] = "year(column)";//年
		singleFuncs[1] = "quarter(column)";//季度
		singleFuncs[2] = "month(column)";//月
		singleFuncs[3] = "week(column)";//周
		singleFuncs[4] = "dayofweek(column)";//工作日
		singleFuncs[5] = "dayofmonth(column)";//日
		singleFuncs[6] = "hour(column)";//小时
		
		doubleFuncs[0] = "concat('Q', quarter(column), ' ', year(column))";//季度&年
		doubleFuncs[1] = "concat(month(column), '-', year(column))";//月&年
		doubleFuncs[2] = "concat('W', week(column)+1, ' ', year(column))";//周&年
		
		sDateFuncs[0] = "date_format(column, '%Y-%m-%d')";//全日期
		sDateFuncs[1] = "date_format(column, '%Y-%m-%d %H:%i:%s')";//日期&时间
		
		//by子句函数
		doubleBys[0] = "year(column), quarter(column)";//季度&年
		doubleBys[1] = "year(column), month(column)";//月&年
		doubleBys[2] = "year(column), week(column)";//周&年
		
		sDateBys[1] = sDateFuncs[0];
		sDateBys[0] = sDateFuncs[1];
		
		itemLabelFuncs[0] = "concat('Q', quarter(column), ' ', year(column))";//季度&年
		itemLabelFuncs[1] = "concat(month(column), '-', year(column))";//月&年
		itemLabelFuncs[2] = "concat('W', week(column), ' ', year(column))";//周&年
		
		funcMath[0] = "quarter(column) + year(column)*4 between smallCount and bigCount";
		funcMath[1] = "month(column) + year(column)*12 between smallCount and bigCount";
		funcMath[2] = "week(column) + year(column)*53 between smallCount and bigCount";
		
		funcMath[3] = "date_format(column, '%Y-%m-%d') between date_format('earlyDate', '%Y-%m-%d') and date_format('laterDate', '%Y-%m-%d')";
		funcMath[4] = "date_format(column, '%Y-%m-%d %H:%i:%s') between date_format('earlyDate', '%Y-%m-%d %H:%i:%s') and date_format('laterDate', '%Y-%m-%d %H:%i:%s')";
		
		stdFunc = "std";
	}

	public Map<String, String> getCeilFuncSqlAndBy(String column, double interval, String method) {
		String func = "ceil(" + column + "/" + interval + ")";
		String sql = "select concat((" + func + "-1)*" + interval +", ' to ', " + func + "*" + interval + ") xAxis, " + method + " yAxis0\n";
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("sql", sql);
		map.put("by", func);
		return map;
	}
}