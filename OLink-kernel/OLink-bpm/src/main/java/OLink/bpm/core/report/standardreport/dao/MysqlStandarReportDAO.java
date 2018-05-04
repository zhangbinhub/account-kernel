package OLink.bpm.core.report.standardreport.dao;

import java.sql.Connection;

import OLink.bpm.core.dynaform.form.ejb.FormProcessBean;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.util.DbTypeUtil;

public class MysqlStandarReportDAO  extends AbstractStandardReportDAO implements
StandardReportDAO{

	public MysqlStandarReportDAO(Connection conn) throws Exception {
		super(conn);
		dbType="MY SQL: ";
		this.schema = DbTypeUtil.getSchema(conn, DbTypeUtil.DBTYPE_MYSQL);
	}

	public String getSql(String formId, String startdate, String enddate,
			String[] columnName, String application, String dbmethod) throws Exception {
		FormProcessBean fpb = new FormProcessBean();
		Form form = (Form)fpb.doView(formId);
		String wheresql = "";
		String usedTimeCol ="";
		String relationName =getFullTableName("t_relationhis");

		
		
		String formname = getFullTableName("TLK_"+form.getName());
		usedTimeCol = " round((timestampdiff(minute,actiontime,processtime))/60,2) usedtime ";
		
		
		if(startdate != null && !startdate.equals(""))
		   wheresql += " and timestamp('"+startdate+"') <= actiontime ";
		
		if(enddate != null && !enddate.equals(""))
		  wheresql += " and timestamp('"+enddate+"') >= actiontime ";
		
		
		String sql = "select " + dbmethod + "(usedtime) USEDTIME, "+getColums(columnName)+" from ("+getGeneralsql(formname,relationName,wheresql,usedTimeCol)+") as ss group by "
		              +getGroupAndOrderBy(columnName)+" order by "+getGroupAndOrderBy(columnName);
		
		return sql;
	}



}
