package OLink.bpm.core.dynaform.pending.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.HibernateSQLUtils;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.dynaform.pending.ejb.PendingVO;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.DbTypeUtil;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;

public class MssqlPendingDAO extends AbstractPendingDAO implements PendingDAO {

	public MssqlPendingDAO(Connection connection) {
		super(connection);
		dbType = "MS SQL Server: ";
		this.schema = DbTypeUtil.getSchema(connection, DbTypeUtil.DBTYPE_MSSQL);
	}

	/**
	 * 生成限制条件sql.
	 * 
	 * @param sql
	 *            sql语句
	 * @param page
	 *            当前页码
	 * @param lines
	 *            每页显示行数
	 * @return 生成限制条件sql语句字符串
	 */
	public String buildLimitString(String sql, int page, int lines) {
		if (lines == Integer.MAX_VALUE) {
			return sql;
		}

		// int to = (page - 1) * lines;
		StringBuffer pagingSelect = new StringBuffer(100);

		int databaseVersion = 0;
		try {
			databaseVersion = connection.getMetaData().getDatabaseMajorVersion();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (9 <= databaseVersion) {// 2005 row_number() over () 分页
			pagingSelect.append("SELECT TOP " + lines + " * FROM (");
			pagingSelect.append("SELECT ROW_NUMBER() OVER (ORDER BY DOMAINID) AS ROWNUMBER, TABNIC.* FROM (");
			pagingSelect.append(sql);
			pagingSelect.append(") TABNIC) TableNickname ");
			pagingSelect.append("WHERE ROWNUMBER>" + lines * (page - 1));

		} else {
			pagingSelect.append("SELECT TOP " + lines * page + " * FROM (");
			pagingSelect.append(sql);
			pagingSelect.append(") TABNIC");
		}

		return pagingSelect.toString();
	}

	public DataPackage<PendingVO> queryByFilter(ParamsTable params, WebUser user) throws Exception {
		FormProcess process = (FormProcess) ProcessFactory.createProcess(FormProcess.class);

		Long _currpage = params.getParameterAsLong("_currpage");
		Long _pagelines = params.getParameterAsLong("_pagelines");
		int currpage = _currpage != null ? _currpage.intValue() : 1;
		int pagelines = _pagelines != null ? _pagelines.intValue() : 10;

		String _orderby = params.getParameterAsString("_orderby");
		String formId = params.getParameterAsString("formid");
		Form form = (Form) process.doView(formId);

		// int limit = currpage * pagelines != 0 ? currpage * pagelines :
		// Integer.MAX_VALUE;

		StringBuffer sqlBuilder = new StringBuffer();
		sqlBuilder.append("SELECT top " + Integer.MAX_VALUE + " * FROM T_PENDING WHERE STATE IN");
		sqlBuilder.append(" (SELECT FLOWSTATERT_ID FROM T_ACTORRT WHERE ACTORID IN("
				+ user.getActorListString(form.getApplicationid()) + ") AND PENDING=1)");
		sqlBuilder.append(" AND FORMID='" + formId).append("'");
		sqlBuilder.append(" AND SUMMARY IS NOT NULL");

		sqlBuilder.append(" ORDER BY ").append(StringUtil.isBlank(_orderby) ? "ID" : _orderby.toUpperCase());

		return queryBySQLPage(sqlBuilder.toString(), currpage, pagelines, user.getDomainid());
	}

	public DataPackage<PendingVO> queryBySQLPage(String sql, int page, int lines, String domainid) throws Exception {

		HibernateSQLUtils sqlUtil = new HibernateSQLUtils();
		sql = sqlUtil.appendCondition(sql, "DOMAINID ='" + domainid + "'");

		PreparedStatement statement = null;
		ResultSet rs=null;
		DataPackage<PendingVO> dpg = new DataPackage<PendingVO>();

		dpg.rowCount = (int) countBySQL(sql, domainid);
		dpg.linesPerPage = lines;
		dpg.pageNo = page;

		sql = this.buildLimitString(sql, page, lines);

		try {
			statement = connection.prepareStatement(sql);
			rs = statement.executeQuery();

			int databaseVersion = connection.getMetaData().getDatabaseMajorVersion();
			if (9 <= databaseVersion) {
			} else {
				// JDBC1.0
				for (int i = 0; i < (page - 1) * lines && rs.next(); i++) {
					// keep empty
				}
			}

			Collection<PendingVO> datas = new ArrayList<PendingVO>();
			for (int i = 0; i < lines && rs.next(); i++) {
				PendingVO pendingVO = new PendingVO();
				setProperties(rs, pendingVO);
				datas.add(pendingVO);
			}

			dpg.datas = datas;

			return dpg;
		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeResultSet(rs);
			PersistenceUtils.closeStatement(statement);
		}
	}
}
