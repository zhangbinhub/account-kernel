package OLink.bpm.core.report.oreport.dao;

import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;

import OLink.bpm.core.dynaform.document.dql.HsqldbSQLFunction;

public class HsqldbOReportDAO extends AbstractOReportDAO implements OReportDAO{

	public HsqldbOReportDAO(Connection conn, String applicationId) throws Exception {
		super(conn, applicationId);
		sqlFuction = new HsqldbSQLFunction();
	}

	static {
		singleFuncs[0] = "year(column)";//年
		singleFuncs[1] = "quarter(column)";//季度
		singleFuncs[2] = "month(column)";//月
		singleFuncs[3] = "week_of_year(column)";//周
		singleFuncs[4] = "day_of_week(column)";//工作日
		singleFuncs[5] = "day_of_month(column)";//日
		singleFuncs[6] = "to_char(column, 'HH24')";//
		
		doubleFuncs[0] = "'Q' + quarter(column) + ' ' + year(column)";//季度&年
		doubleFuncs[1] = "month(column) + '-' + year(column)";//月&年
		doubleFuncs[2] = "'W' + week_of_year(column) + ' ' + year(column)";//周&年
		
		sDateFuncs[0] = "to_char(column, 'YYYY-MM-DD')";//日期
		sDateFuncs[1] = "to_char(column, 'YYY-MM-DD HH24:MI:SS')";//日期&时间
		
		//by子句函数
		doubleBys[0] = "year(column), quarter(column)";//季度&年
		doubleBys[1] = "year(column), month(m, column)";//月&年
		doubleBys[2] = "year(yyyy, column), week_of_year(ww, column)";//周&年
		
		sDateBys[0] = sDateFuncs[0];
		sDateBys[1] = sDateFuncs[1];
		
		itemLabelFuncs[0] = "'Q' + quarter(column) + ' ' + year(column)";//季度&年
		itemLabelFuncs[1] = "month(column) + '-' + year(column)";//月&年
		itemLabelFuncs[2] = "'W' + week_of_year(column) + ' ' + year(column)";//周&年

		funcMath[0] = "quarter(column) + year(column)*4 between smallCount and bigCount";
		funcMath[1] = "month(column) + year(column)*12 between smallCount and bigCount";
		funcMath[2] = "week(column) + year(column)*53 between smallCount and bigCount";
		
		funcMath[3] = "";
		funcMath[4] = "timestamp(column) between timestamp('earlyDate') and timestamp('laterDate')";
	
		stdFunc = "stddev";
	}

	public Map<String, String> getCeilFuncSqlAndBy(String column, double interval, String method) {
		String func = "ceiling(" + column + "/" + interval + ")";
		//需修正
		String sql = "select ltrim(str(" + func + "*" + interval +")) + ' to ' + ltrim(str((" + func + "+1)*" + interval + ")) xAxis, " + method + " yAxis0\n";
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("sql", sql);
		map.put("by", func);
		return map;
	}
}