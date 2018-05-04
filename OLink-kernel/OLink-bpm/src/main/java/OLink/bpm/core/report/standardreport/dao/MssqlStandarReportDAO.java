package OLink.bpm.core.report.standardreport.dao;

import java.sql.Connection;

import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.form.ejb.FormProcessBean;
import OLink.bpm.util.DbTypeUtil;

public class MssqlStandarReportDAO  extends AbstractStandardReportDAO implements
StandardReportDAO{

	public MssqlStandarReportDAO(Connection conn) throws Exception {
		super(conn);
		dbType="MS SQL Server: ";
		this.schema = DbTypeUtil.getSchema(conn, DbTypeUtil.DBTYPE_MSSQL);
	}

	public String getSql(String formId, String startdate, String enddate,
			String[] columnName, String application, String dbmethod) throws Exception {
		FormProcessBean fpb = new FormProcessBean();
		Form form = (Form)fpb.doView(formId);
		String wheresql = "";
		String usedTimeCol ="";
		String relationName =getFullTableName("t_relationhis");

		

		String formname = getFullTableName("TLK_"+form.getName());
		usedTimeCol = " round(cast(DateDiff (\"Minute\",actiontime,processtime) as FLOAT)/60 ,2) usedtime ";
		
		if(startdate != null && !startdate.equals(""))
		   wheresql += " and cast('"+startdate+"' as  datetime)<= actiontime ";
		
		if(enddate != null && !enddate.equals(""))
		  wheresql += " and cast('"+enddate+"' as  datetime) >= actiontime ";
		
		
		String sql = "select " + dbmethod + "(usedtime) USEDTIME, "+getColums(columnName)+" from ("+getGeneralsql(formname,relationName,wheresql,usedTimeCol)+") a group by "
		              +getGroupAndOrderBy(columnName)+" order by "+getGroupAndOrderBy(columnName);


		return sql;
	}



}
