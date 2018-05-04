package OLink.bpm.core.report.wfdashboard.dao;

import java.sql.Connection;
import java.util.Collection;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.report.wfdashboard.ejb.DashBoardVO;
import OLink.bpm.util.DbTypeUtil;
import OLink.bpm.util.StringUtil;

public class MySqlWFDashBoardDAO extends AbstractWFDashBoardDAO implements WFDashBoardDAO {

	public MySqlWFDashBoardDAO(Connection conn) throws Exception {
		super(conn);
		this.schema = DbTypeUtil.getSchema(conn, DbTypeUtil.DBTYPE_MYSQL);
	}
	
	public DataPackage<DashBoardVO> getSumRole(String application, String domainid,
											   String flowid, int curPage) throws Exception {
		String sql = "SELECT A.ACTORID NAME,COUNT(A.ACTORID) VALUE  FROM "
				+ this.getFullTableName("T_ACTORRT") + " A LEFT JOIN "
				+ this.getFullTableName("T_FLOWSTATERT")
				+ " B ON(A.FLOWSTATERT_ID = B.ID AND B.STATE='256')";
		
		   sql += " WHERE A.DOMAINID='"+domainid+"' AND A.APPLICATIONID='"+application+"'";

		if (!StringUtil.isBlank(flowid))
			sql += " AND B.FLOWID ='" + flowid + "'";

		sql += " GROUP  BY A.ACTORID ";

 		int startLine = (curPage- 1)* 10;
		int endLine = startLine + 10;
		
		DataPackage<DashBoardVO> dpg = new DataPackage<DashBoardVO>();
		dpg.rowCount = countSql(sql);
		
		sql = "SELECT NAME,VALUE FROM (SELECT NAME,VALUE FROM (" + sql + ") A ORDER BY NAME) B LIMIT " + startLine
				+ " ," + endLine;
		
		dpg.setDatas(getDatas(sql));
  
		dpg.linesPerPage = 10;
		dpg.pageNo = curPage;
		return dpg;
	}
	
	public Collection<DashBoardVO> getSumTimeData(String application, String domainid,
			String flowid) throws Exception {
		String sql = "SELECT B.STATELABEL NAME,ROUND(AVG(TIMESTAMPDIFF(MINUTE,ACTIONTIME,PROCESSTIME)/60),2) VALUE FROM "
				+ this.getFullTableName("T_RELATIONHIS")
				+ " A,"
				+ "(select A.FLOWID,A.DOCID,B.STATELABEL,B.DOMAINID,B.APPLICATIONID from "
				+ getFullTableName("T_FLOWSTATERT")
				+ " A,"
				+ getFullTableName("T_DOCUMENT")
				+ " B where A.STATE ='256' AND A.DOCID = B.ID) B "
				+ " WHERE A.DOCID = B.DOCID ";
		 sql += " AND B.DOMAINID='"+domainid+"' AND B.APPLICATIONID='"+application+"'";
		
		

		if (!StringUtil.isBlank(flowid))
			sql += " AND B.FLOWID ='" + flowid + "'";

		sql += " GROUP BY B.STATELABEL";

		return getDatas(sql);
	}

}
