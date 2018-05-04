package OLink.bpm.core.report.standardreport.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.core.report.basereport.dao.AbstractReportDAO;
import OLink.bpm.core.report.standardreport.action.ReportUtil;
import org.apache.log4j.Logger;

public abstract class AbstractStandardReportDAO extends AbstractReportDAO {

	public AbstractStandardReportDAO(Connection conn) throws Exception {
		super(conn);
	}

	protected final static Logger log = Logger
			.getLogger(AbstractStandardReportDAO.class);

	private HashMap<String, String> userMap = new HashMap<String, String>();


	public Collection<Map<String, String>> getSummaryReport(String sql) throws Exception {
		PreparedStatement statement = null;
		ResultSet rs = null;
		Collection<Map<String, String>> datas = new ArrayList<Map<String, String>>();
		try {

			statement = connection.prepareStatement(sql);

			rs = statement.executeQuery();

			while (rs != null && rs.next()) {
				datas.add(getSummaryRow(rs));
			}

			return datas;

		} catch (SQLException e) {
			throw e;
		} finally {
			PersistenceUtils.closeResultSet(rs);
			PersistenceUtils.closeStatement(statement);
		}
	}

	public int getReportRowsNum(String sql) throws Exception {
		String countSql = "select count(*) from (" + sql + ") a  ";
		PreparedStatement statement = null;
		ResultSet rs = null;
		int rowsNum = 0;
		try {
			statement = connection.prepareStatement(countSql);
			rs = statement.executeQuery();
			while (rs != null && rs.next()) {
				rowsNum = rs.getInt(1);
			}

			return rowsNum;

		} catch (SQLException e) {
			throw e;
		} finally {
			PersistenceUtils.closeResultSet(rs);
			PersistenceUtils.closeStatement(statement);
		}
	}

	public Map<String, String> getSummaryRow(ResultSet rs) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		ResultSetMetaData metaData = rs.getMetaData();
		for (int i = 1; i <= metaData.getColumnCount(); i++) {
			String colName = metaData.getColumnName(i);
			if (colName.startsWith("AUDITOR")) {
				map.put(colName, getAuditorName(rs.getString(colName)));
			} else if (colName.equals("USEDTIME")) {
				map.put(colName, String.valueOf(rs.getFloat(colName)));
			} else {
				map.put(colName.toUpperCase(), rs.getString(colName));
			}

		}
		return map;
	}


	private String getAuditorName(String userId) throws Exception {
		if (!userMap.containsKey(userId)) {

			userMap.put(userId, ReportUtil.getUserNameById(userId));
		}

		return userMap.get(userId);
	}


	protected String getGeneralsql(String formname, String relationName,
			String wheresql, String usedTimeCol) {

		String sql = "select relation.AUDITOR, actiontime arrivedtime, processtime sendouttime, ENDNODENAME NODENAME,";

		sql += usedTimeCol;
		sql += ", tabs.* ";

		sql += " from "
				+ relationName
				+ " relation, "
				+ formname
				+ " tabs where relation.docid = tabs.id and tabs.istmp=0 and relation.auditor is not null";

		sql += wheresql;
		return sql;
	}


	protected String getColums(String[] columnName) {
		StringBuffer columns = new StringBuffer(0);


		if (columnName != null && columnName.length > 0) {

			for (int i = 0; i < columnName.length; i++) {
				columns.append(columnName[i] + ", ");
			}
			columns.delete(columns.lastIndexOf(","), columns.length());
		} else {
			columns = new StringBuffer("AUDITOR ");
		}

		return columns.toString();
	}


	protected String getGroupAndOrderBy(String[] columnName) {
		StringBuffer groupOrderBy = new StringBuffer();
		// 锟斤拷锟斤拷锟窖★拷锟斤拷column锟斤拷b锟斤拷应锟斤拷group by 锟斤拷锟揭诧拷锟給rderby锟斤拷锟�
		if (columnName != null && columnName.length > 0) {

			for (int i = 0; i < columnName.length; i++) {
				groupOrderBy.append(columnName[i] + ",");
			}
		} else {
			groupOrderBy = new StringBuffer(" AUDITOR ");
		}

		//groupOrderBy = groupOrderBy.substring(0, groupOrderBy.length() - 1);
		groupOrderBy = groupOrderBy.deleteCharAt(groupOrderBy.length() - 1);
		return groupOrderBy.toString();
	}
	
}
