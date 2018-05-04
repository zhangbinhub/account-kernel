package OLink.bpm.core.report.oreport.dao;

import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;

import OLink.bpm.core.dynaform.document.dql.MssqlSQLFunction;

public class MssqlOReportDAO extends AbstractOReportDAO implements OReportDAO{

	public MssqlOReportDAO(Connection conn, String applicationId) throws Exception {
		super(conn, applicationId);
		sqlFuction = new MssqlSQLFunction();
	}
	
	static {
		singleFuncs[0] = "datepart(yyyy, column)";//年
		singleFuncs[1] = "datepart(q, column)";//季度
		singleFuncs[2] = "datepart(m, column)";//月
		singleFuncs[3] = "datepart(ww, column)";//周
		singleFuncs[4] = "datepart(w, column)";//工作日
		singleFuncs[5] = "datepart(d, column)";//日
		singleFuncs[6] = "datepart(hh, column)";//小时
		
		doubleFuncs[0] = "'Q' + ltrim(str(datepart(q, column))) + ' ' + ltrim(str(datepart(yyyy, column)))";//季度&年
		doubleFuncs[1] = "ltrim(str(datepart(m, column))) + '-' + ltrim(str(datepart(yyyy, column)))";//月&年
		doubleFuncs[2] = "'W' + ltrim(str(datepart(ww, column))) + ' ' ltrim(str(datepart(yyyy, column)))";//周&年
		
		sDateFuncs[0] = "convert(varchar(10), column, 23)";//全日期
		sDateFuncs[1] = "convert(varcher(100), column, 20)";//日期&时间
		
		//by子句函数
		doubleBys[0] = "depart(yyyy, column), depart(q, column)";//季度&年
		doubleBys[1] = "depart(yyyy, column), depart(m, column)";//月&年
		doubleBys[2] = "depart(yyyy, column), depart(ww, column)";//周&年
		
		sDateBys[0] = sDateFuncs[0];
		sDateBys[1] = sDateFuncs[1];
		
		itemLabelFuncs[0] = "'Q' + ltrim(str(datepart(q, column))) + ' ' + ltrim(str(datepart(yyyy, column)))";//季度&年
		itemLabelFuncs[1] = "ltrim(str(datepart(m, column))) + '-' + ltrim(str(datepart(yyyy, column)))";//月&年
		itemLabelFuncs[2] = "'W' + ltrim(str(datepart(ww, column))) + ' ' ltrim(str(datepart(yyyy, column)))";//周&年
		
		funcMath[0] = "datepart(q, column) + datepart(yyyy, column)*4 between smallCount and bigCount";
		funcMath[1] = "datepart(m, column) + datepart(yyyy, column)*12 between smallCount and bigCount";
		funcMath[2] = "datepart(w, column) + datepart(yyyy, column)*52 between smallCount and bigCount";
		
		funcMath[3] = "convert(varchar(10), column, 23) between convert(varchar(10), 'earlyDate', 23) and convert(varchar(10), 'laterDate', 23)";
		funcMath[4] = "convert(varchar(100), column, 20) between convert(varchar(100), 'earlyDate', 20) and convert(varchar(100), 'laterDate', 20)";
	
		stdFunc = "stdev";
	}

	public Map<String, String> getCeilFuncSqlAndBy(String column, double interval, String method) {
		String func = "ceiling(" + column + "/" + interval + ")";
		String sql = "select ltrim(str(" + func + "*" + interval +")) + ' to ' + ltrim(str((" + func + "+1)*" + interval + ")) xAxis, " + method + " yAxis0\n";
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("sql", sql);
		map.put("by", func);
		return map;
	}
}