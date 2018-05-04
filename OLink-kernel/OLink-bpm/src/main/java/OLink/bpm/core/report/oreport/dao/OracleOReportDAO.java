package OLink.bpm.core.report.oreport.dao;

import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;

import OLink.bpm.core.dynaform.document.dql.OracleSQLFunction;

public class OracleOReportDAO extends AbstractOReportDAO implements OReportDAO{

	public OracleOReportDAO(Connection conn, String applicationId) throws Exception {
		super(conn, applicationId);
		sqlFuction = new OracleSQLFunction();
	}
	
	static {
		singleFuncs[0] = "to_char(column, 'yyyy')";//年
		singleFuncs[1] = "to_char(column, 'q')";//季度
		singleFuncs[2] = "to_char(column, 'mm')";//月
		singleFuncs[3] = "to_char(column, 'ww')";//周
		singleFuncs[4] = "to_char(column, 'd')";//工作日
		singleFuncs[5] = "to_char(column, 'dd')";//日
		singleFuncs[6] = "to_char(column, 'HH24')";//小时
		
		doubleFuncs[0] = "'Q'||to_char(column, 'q')||' '||to_char(column, 'yyyy')";//季度&年
		doubleFuncs[1] = "to_char(column, 'mm')||'-'||to_char(column, 'yyyy')";//月&年
		doubleFuncs[2] = "'W'||to_char(column, 'ww')||' '||to_char(column, 'yyyy')";//周&年
		
		sDateFuncs[0] = "to_char(column, 'yyyy-mm-dd')";//全日期
		sDateFuncs[1] = "to_char(column, 'yyyy-mm-dd HH24:MI:SS')";//日期&时间
		
		//by子句函数
		doubleBys[0] = "to_char(column, 'yyyy'), to_char(column, 'q')";//季度&年
		doubleBys[1] = "to_char(column, 'yyyy'), to_char(column, 'mm')";//月&年
		doubleBys[2] = "to_char(column, 'yyyy'), to_char(column, 'ww')";//周&年
		
		sDateBys[0] = sDateFuncs[0];
		sDateBys[1] = sDateFuncs[1];
		
		itemLabelFuncs[0] = "'Q'||to_char(column, 'q')||' '||to_char(column, 'yyyy')";//季度&年
		itemLabelFuncs[1] = "to_char(column, 'mm')||'-'||to_char(column, 'yyyy')";//月&年
		itemLabelFuncs[2] = "'W'||to_char(column, 'ww')||' '||to_char(column, 'yyyy')";//周&年
		
		funcMath[0] = "to_number(to_char(column, 'q')) + to_number(to_char(column, 'yyyy'))*4 between smallCount and bigCount";
		funcMath[1] = "to_number(to_char(column, 'mm')) + to_number(to_char(column, 'yyyy'))*12 between smallCount and bigCount";
		funcMath[2] = "to_number(to_char(column, 'ww')) + to_number(to_char(column, 'yyyy'))*53 between smallCount and bigCount";
		
		funcMath[3] = "column between to_date('earlyDate', 'yyyy-mm-dd') and to_date('laterDate', 'yyyy-mm-dd')";
		funcMath[4] = "column between to_date('earlyDate', 'yyyy-mm-dd HH24:MI:SS') and to_date('laterDate', 'yyyy-mm-dd HH24:MI:SS')";
	
		stdFunc = "stddev";
	}

	public Map<String, String> getCeilFuncSqlAndBy(String column, double interval, String method) {
		String func = "ceil(" + column + "/" + interval + ")";
		String sql = "select (" + func + "-1)*" + interval + "||' to '||" + func + "*" + interval + " xAxis, " + method + " yAxis0\n";
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("sql", sql);
		map.put("by", func);
		return map;
	}
}