package OLink.bpm.core.report.standardreport.dao;

import java.sql.Connection;

import OLink.bpm.core.dynaform.form.ejb.FormProcessBean;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.util.DbTypeUtil;

public class OracleStandarReportDAO extends AbstractStandardReportDAO implements
		StandardReportDAO{

	public OracleStandarReportDAO(Connection conn) throws Exception {
		super(conn);
		this.schema = DbTypeUtil.getSchema(conn, DbTypeUtil.DBTYPE_ORACLE);
	}

	public String getSql(String formId, String startdate, String enddate,
			String[] columnName, String application, String dbmethod) throws Exception {
		FormProcessBean fpb = new FormProcessBean();
		Form form = (Form)fpb.doView(formId);
		String wheresql = "";
		String usedTimeCol ="";
		String relationName =getFullTableName("t_relationhis");

		
		
		String formname = getFullTableName("TLK_"+form.getName());
		usedTimeCol = "round((to_date(to_char(processtime,'YYYY-MM-dd HH24:MI:ss'),'YYYY-MM-dd HH24:MI:ss')" +
		"-to_date(to_char(actiontime,'YYYY-MM-dd HH24:mi:ss'),'YYYY-MM-dd HH24:MI:ss'))*24,2) usedtime";
		
		
		if(startdate != null && !startdate.equals(""))
		   wheresql += " and to_char(actiontime,'YYYY-MM-dd') >="+"'"+startdate+"' ";
		
		if(enddate != null && !enddate.equals(""))
		  wheresql += " and to_char(actiontime,'YYYY-MM-dd') <="+"'"+enddate+"' ";
		
		
		String sql = "select " + dbmethod + "(usedtime) USEDTIME, "+getColums(columnName)+" from ("+getGeneralsql(formname,relationName,wheresql,usedTimeCol)+") group by "
		              +getGroupAndOrderBy(columnName)+" order by "+getGroupAndOrderBy(columnName);

		return sql;
	}



}
