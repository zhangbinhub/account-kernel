package OLink.bpm.core.dynaform.document.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.core.dynaform.document.dql.MysqlSQLFunction;
import OLink.bpm.util.DbTypeUtil;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.document.ejb.Item;
import OLink.bpm.core.dynaform.form.ejb.Form;
import org.apache.log4j.Logger;

import OLink.bpm.util.ObjectUtil;

public class MysqlDocStaticTblDAO extends AbstractDocStaticTblDAO implements DocumentDAO {
	public final static Logger log = Logger.getLogger(MysqlDocStaticTblDAO.class);

	public MysqlDocStaticTblDAO(Connection conn, String applicationId) throws Exception {
		super(conn, applicationId);
		dbType = "MY SQL: ";
		this.schema = DbTypeUtil.getSchema(conn, DbTypeUtil.DBTYPE_MYSQL);
		sqlFuction = new MysqlSQLFunction();
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

		int to = (page - 1) * lines;
		StringBuffer pagingSelect = new StringBuffer(100);

		pagingSelect.append("SELECT * FROM (");
		pagingSelect.append(sql);
		pagingSelect.append(" ) AS TB LIMIT " + to + "," + lines);

		return pagingSelect.toString();
	}

	public void setBaseProperties(Document doc, ResultSet rs) throws Exception {
		ResultSetMetaData metaData = rs.getMetaData();
		for (int i = 1; i <= metaData.getColumnCount(); i++) {
			String colName = metaData.getColumnLabel(i);
			if (!colName.startsWith("ITEM_")) {
				try {
					switch (metaData.getColumnType(i)) {

					case Types.CLOB:
						ObjectUtil.setProperty(doc, colName, rs.getString(colName), false);
						break;
					case Types.CHAR:
					case Types.VARCHAR:
						ObjectUtil.setProperty(doc, colName, rs.getString(colName), false);
						break;

					case Types.NUMERIC:
					case Types.TINYINT:
					case Types.BIT:
					case Types.INTEGER:
						if (metaData.getPrecision(i) > 1 && metaData.getScale(i) == 0) {
							ObjectUtil.setProperty(doc, colName, Integer.valueOf(rs.getInt(colName)), false);
						} else if (metaData.getPrecision(i) == 1 && metaData.getScale(i) == 0) {
							ObjectUtil.setProperty(doc, colName, (rs.getInt(colName) == 0 ? Boolean.valueOf(false) : Boolean
									.valueOf(true)), false);
						} else {
							ObjectUtil.setProperty(doc, colName, new Double(rs.getDouble(colName)), false);
						}
						break;
					case Types.DECIMAL:
					case Types.DOUBLE:
						if (metaData.getScale(i) == 0) {
							ObjectUtil.setProperty(doc, colName, Integer.valueOf(rs.getInt(colName)), false);
						} else {
							ObjectUtil.setProperty(doc, colName, new Double(rs.getDouble(colName)), false);
						}

						break;

					case Types.FLOAT:
						ObjectUtil.setProperty(doc, colName, new Float(rs.getFloat(colName)), false);
						break;

					case Types.REAL:
					case Types.BOOLEAN:
						ObjectUtil.setProperty(doc, colName, Boolean.valueOf(rs.getBoolean(colName)), false);
						break;
					case Types.DATE:
					case Types.TIME:
					case Types.TIMESTAMP:
						ObjectUtil.setProperty(doc, colName, rs.getTimestamp(colName), false);
						break;
					default:
						ObjectUtil.setProperty(doc, colName, rs.getObject(colName), false);
					}
				} catch (Exception e) {
					log.warn(colName + " error: " + e.getMessage());
				}
			}
		}

		if (doc.getForm() != null && doc.getForm().getType() == Form.FORM_TYPE_NORMAL) {
			doc.setMappingId(doc.getId());
		}

		doc.setItems(createItems(rs, doc));
	}

	protected void setItemValue(ResultSet rs, int i, Item item) throws SQLException {
		ResultSetMetaData metaData = rs.getMetaData();
		int columnType = metaData.getColumnType(i);

		// Set Item's type and value
		switch (columnType) {

		case Types.LONGVARCHAR:
		case Types.CLOB:
			item.setType(Item.VALUE_TYPE_TEXT);
			item.setTextvalue(rs.getString(i));
			break;

		case Types.CHAR:
		case Types.VARCHAR:
			item.setType(Item.VALUE_TYPE_VARCHAR);
			item.setVarcharvalue(rs.getString(i));
			break;

		case Types.NUMERIC:
		case Types.INTEGER:
		case Types.DECIMAL:
		case Types.DOUBLE:
		case Types.FLOAT:
		case Types.BOOLEAN:
		case Types.REAL:
			item.setType(Item.VALUE_TYPE_NUMBER);
			item.setNumbervalue(new Double(rs.getDouble(i)));
			break;

		case Types.DATE:
		case Types.TIME:
		case Types.TIMESTAMP:
			item.setType(Item.VALUE_TYPE_DATE);
			try {
				item.setDatevalue(rs.getTimestamp(i));
			} catch (Exception e) {
				item.setDatevalue(null);
			}

			break;

		default:
			item.setType(Item.VALUE_TYPE_VARCHAR);
			item.setVarcharvalue(rs.getString(i));
		}
	}

	public boolean isExist(String id) throws Exception {
		PreparedStatement statement = null;
		ResultSet rs = null;

		try {
			String sql = "SELECT COUNT(*) FROM " + getFullTableName(_TBNAME) + " doc WHERE doc.ID=?";

			statement = connection.prepareStatement(sql);
			statement.setString(1, id);
			rs = statement.executeQuery();

			if (rs.next()) {
				return rs.getLong(1) > 0;
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			PersistenceUtils.closeResultSet(rs);//Add By XGY 20130218
			PersistenceUtils.closeStatement(statement);
		}
		return false;
	}
}
