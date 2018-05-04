package OLink.bpm.core.report.standardreport.dao;

import java.sql.Connection;

import OLink.bpm.core.dynaform.form.ejb.FormProcessBean;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.util.DbTypeUtil;

public class HsqldbStandarReportDAO  extends AbstractStandardReportDAO implements
StandardReportDAO {

	public HsqldbStandarReportDAO(Connection conn) throws Exception {
		super(conn);
		dbType="HypersonicSQL: ";
		this.schema = DbTypeUtil.getSchema(conn, DbTypeUtil.DBTYPE_HSQLDB);
	}

	public String getSql(String formId, String startdate, String enddate,
			String[] columnName, String application, String dbmethod) throws Exception {
		FormProcessBean fpb = new FormProcessBean();
		Form form = (Form)fpb.doView(formId);
		String wheresql = "";
		String usedTimeCol ="";	
	
		String formname = getFullTableName(".TLK_"+form.getName());
		String relationName  =getFullTableName(".T_RELATIONHIS");
		usedTimeCol = "round( DATEDIFF('ss',actiontime,processtime),2) usedtime ";
		
		if(startdate != null && !startdate.equals(""))
		   wheresql += " and actiontime >="+"convert('"+startdate+" 00:00:00',date) ";
		
		if(enddate != null && !enddate.equals(""))
		  wheresql += " and actiontime <="+"convert(('"+enddate+" 00:00:00',date) ";
		
		
		String sql = "select round((convert(" + dbmethod + "(usedtime),float)/60)/60,2) USEDTIME, "+getColums(columnName)+" from ("+getGeneralsql(formname,relationName,wheresql,usedTimeCol)+")  group by "
		              +getGroupAndOrderBy(columnName);

		return sql;
	}
	


  

}
