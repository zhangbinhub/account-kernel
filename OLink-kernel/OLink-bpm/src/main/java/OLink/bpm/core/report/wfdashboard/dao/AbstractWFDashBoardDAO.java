package OLink.bpm.core.report.wfdashboard.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.core.report.basereport.dao.AbstractReportDAO;
import OLink.bpm.core.report.wfdashboard.ejb.DashBoardVO;
import OLink.bpm.util.StringUtil;

public abstract class AbstractWFDashBoardDAO extends AbstractReportDAO {

	public AbstractWFDashBoardDAO(Connection conn) throws Exception {
		super(conn);
	}

	public Collection<DashBoardVO> getSumWfData(String application, String domainid)
			throws Exception {
		String sql = "SELECT A.FLOWID NAME,COUNT(A.FLOWID) VALUE  FROM "
				+ this.getFullTableName("T_FLOWSTATERT")
				+ " A LEFT OUTER JOIN " + this.getFullTableName("T_DOCUMENT")
				+ " B ON(A.DOCID = B.ID)  WHERE A.STATE=256"
				+ " AND B.APPLICATIONID='" + application + "' AND B.DOMAINID='"
				+ domainid + "'";
		sql += " GROUP  BY A.FLOWID ";

		return getDatas(sql);
	}

	public Collection<DashBoardVO> getSumStableLabelData(String application, String domainid,
			String flowid) throws Exception {
		String sql = "SELECT B.STATELABEL NAME,COUNT(A.FLOWID) VALUE  FROM "
				+ this.getFullTableName("T_FLOWSTATERT")
				+ " A LEFT OUTER JOIN " + this.getFullTableName("T_DOCUMENT")
				+ " B ON(A.DOCID = B.ID)  WHERE A.STATE=256"
				+ " AND B.APPLICATIONID='" + application + "' AND B.DOMAINID='"
				+ domainid + "'";

		if (!StringUtil.isBlank(flowid))
			sql += " AND A.FLOWID ='" + flowid + "'";

		sql += " GROUP  BY B.STATELABEL ";

		return getDatas(sql);
	}

	public Collection<DashBoardVO> getSumTimeData(String application, String domainid,
			String flowid) throws Exception {
		String sql = "SELECT B.STATELABEL NAME,round(AVG((TO_DATE(TO_CHAR(A.PROCESSTIME,'YYYY-MM-dd HH24:MI:ss'),'YYYY-MM-dd HH24:MI:ss')"
				+ "-TO_DATE(TO_CHAR(A.ACTIONTIME,'YYYY-MM-dd HH24:mi:ss'),'YYYY-MM-dd HH24:MI:ss'))*24),2) VALUE FROM "
				+ this.getFullTableName("T_RELATIONHIS")
				+ " A,"
				+ "(select A.FLOWID,A.DOCID,B.STATELABEL,B.DOMAINID,B.APPLICATIONID from "
				+ getFullTableName("T_FLOWSTATERT")
				+ " A,"
				+ getFullTableName("T_DOCUMENT")
				+ " B where A.STATE =256 AND A.DOCID = B.ID) B "
				+ " WHERE A.DOCID = B.DOCID ";
		 sql += " AND B.DOMAINID='"+domainid+"' AND B.APPLICATIONID='"+application+"'";

		if (!StringUtil.isBlank(flowid))
			sql += " AND B.FLOWID ='" + flowid + "'";

		sql += " GROUP BY B.STATELABEL";

		return getDatas(sql);
	}

	public DataPackage<DashBoardVO> getSumRole(String application, String domainid,
											   String flowid, int curPage) throws Exception {
		String sql = "SELECT A.ACTORID NAME,COUNT(A.ACTORID) VALUE  FROM "
				+ this.getFullTableName("T_ACTORRT") + " A LEFT JOIN "
				+ this.getFullTableName("T_FLOWSTATERT")
				+ " B ON(A.FLOWSTATERT_ID = B.ID AND B.STATE=256)";
		        sql += " WHERE A.DOMAINID='"+domainid+"' AND A.APPLICATIONID='"+application+"'";

		if (!StringUtil.isBlank(flowid))
			sql += " AND B.FLOWID ='" + flowid + "'";

		sql += " GROUP  BY A.ACTORID ";

 		int startLine = (curPage- 1)* 10;
		int endLine = startLine + 10;
		
		DataPackage<DashBoardVO> dpg = new DataPackage<DashBoardVO>();
		dpg.rowCount = countSql(sql);
		
		sql = "SELECT * FROM (SELECT NAME,VALUE, ROWNUM ROW_ FROM (" + sql + ") ORDER BY NAME) WHERE ROW_>=" + startLine
				+ " AND ROW_<" + endLine;
		
		dpg.setDatas(getDatas(sql));
  
		dpg.linesPerPage = 10;
		dpg.pageNo = curPage;
		return dpg;
	}

	public Collection<DashBoardVO> getSumRole(String application, String domainid,
			String flowid) throws Exception {
		String sql = "SELECT A.ACTORID NAME,COUNT(A.ACTORID) VALUE  FROM "
				+ this.getFullTableName("T_ACTORRT") + " A INNER JOIN "
				+ this.getFullTableName("T_FLOWSTATERT")
				+ " B ON(A.FLOWSTATERT_ID = B.ID AND B.STATE=256)";

		if (!StringUtil.isBlank(flowid))
			sql += " WHERE B.FLOWID ='" + flowid + "'";

		sql += " GROUP  BY A.ACTORID ";

		return getDatas(sql);
	}

	protected Collection<DashBoardVO> getDatas(String sql) throws Exception {
		PreparedStatement statement = null;
		ResultSet rs=null;
		try {
			statement = connection.prepareStatement(sql);
			rs = statement.executeQuery();
			Collection<DashBoardVO> rtn = new ArrayList<DashBoardVO>();
			while (rs.next()) {
				DashBoardVO vo = new DashBoardVO();
				setProperties(rs, vo);
				rtn.add(vo);
			}
			return rtn;
		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeResultSet(rs);
			PersistenceUtils.closeStatement(statement);
		}
	}

	public int countSql(String sql) throws Exception {
		int rows = 0;
		String newSql = "SELECT COUNT(*) FROM (" + sql + ") A ";
		PreparedStatement statement = null;
		ResultSet rs=null;
		try {
			statement = connection.prepareStatement(newSql);
			rs = statement.executeQuery();
			if (rs.next()) {
				rows = rs.getInt(1);
			}
			return rows;
		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeResultSet(rs);
			PersistenceUtils.closeStatement(statement);
		}

	}

	public void setProperties(ResultSet rs, DashBoardVO vo) throws SQLException {
		vo.setValue(new Double(rs.getDouble("VALUE")));
		vo.setName(rs.getString("NAME"));
	}

}
