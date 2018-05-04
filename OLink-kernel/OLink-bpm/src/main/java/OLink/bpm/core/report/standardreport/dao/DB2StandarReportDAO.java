package OLink.bpm.core.report.standardreport.dao;

import java.sql.Connection;

import OLink.bpm.core.dynaform.form.ejb.FormProcessBean;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.util.DbTypeUtil;

public class DB2StandarReportDAO  extends AbstractStandardReportDAO implements StandardReportDAO{

	public DB2StandarReportDAO(Connection conn) throws Exception {
		super(conn);
		dbType="DB2: ";
		this.schema = DbTypeUtil.getSchema(conn, DbTypeUtil.DBTYPE_DB2);
	}

	public String getSql(String formId, String startdate, String enddate,
			String[] columnName, String application, String dbmethod) throws Exception {
		FormProcessBean fpb = new FormProcessBean();
		Form form = (Form)fpb.doView(formId);
		String wheresql = "";
		String usedTimeCol ="";
		
		String formname = getFullTableName("TLK_"+form.getName());
		String relationName  = getFullTableName("T_RELATIONHIS");
		usedTimeCol = "(DAYS(processtime) - DAYS(actiontime)) * 86400+ (MIDNIGHT_SECONDS(processtime) - MIDNIGHT_SECONDS(actiontime)) usedtime ";
		
		
		if(startdate != null && !startdate.equals(""))
		   wheresql += " and actiontime >="+"timestamp('"+startdate+" 00:00:00') ";
		
		if(enddate != null && !enddate.equals(""))
		  wheresql += " and actiontime <="+"timestamp('"+enddate+" 00:00:00') ";   
		
		
		String sql = "select round((cast(" + dbmethod + "(usedtime) as float)/60)/60,2) USEDTIME, "+getColums(columnName)+" from ("+getGeneralsql(formname,relationName,wheresql,usedTimeCol)+")  group by "
		              +getGroupAndOrderBy(columnName)+" order by "+getGroupAndOrderBy(columnName);

		return sql;
	}



	
}
