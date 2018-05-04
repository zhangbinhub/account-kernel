package OLink.bpm.core.dynaform.document.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.core.dynaform.document.dql.DB2SQLFunction;
import OLink.bpm.core.dynaform.document.dql.DQLASTUtil;
import OLink.bpm.core.dynaform.document.ejb.Item;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.base.dao.DAOException;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.util.DbTypeUtil;
import org.apache.log4j.Logger;

import OLink.bpm.core.dynaform.form.ejb.mapping.TableMapping;
import OLink.bpm.util.ObjectUtil;

public class DB2DocStaticTblDAO extends AbstractDocStaticTblDAO implements
		DocumentDAO {
	public final static Logger log = Logger.getLogger(DB2DocStaticTblDAO.class);

	public DB2DocStaticTblDAO(Connection conn, String applicationId)
			throws Exception {
		super(conn, applicationId);
		dbType = "DB2: ";
		this.schema = DbTypeUtil.getSchema(conn, DbTypeUtil.DBTYPE_DB2);
		sqlFuction = new DB2SQLFunction();
	}

	/**
	 * 生成限制条件sql.并把orderby 放到num之后
	 * 
	 * @param sql
	 *            sql语句
	 * @param page
	 *            当前页码
	 * @param lines
	 *            每页显示行数
	 * @param orderby
	 *            orderby 语句
	 * @return 生成限制条件sql语句字符串
	 */
	public String buildLimitString(String sql, String orderby, int page,
			int lines) {
		if (lines == Integer.MAX_VALUE) {
			return sql;
		}
		// Modify by James:2010-01-03, fixed page divide error.
		// int from = (page - 1) * lines;
		int from = (page - 1) * lines + 1;

		int to = page * lines;
		StringBuffer pagingSelect = new StringBuffer(100);

		pagingSelect.append("Select * from (select row_.*, rownumber() over(");
		if (orderby != null && !orderby.trim().equals(""))
			pagingSelect.append(orderby);
		pagingSelect.append(" ) AS rown from ( ");
		pagingSelect.append(sql);
		pagingSelect.append(" ) AS row_) AS rows_ where rows_.rown BETWEEN ");
		pagingSelect.append(from);
		pagingSelect.append(" AND ");
		pagingSelect.append(to);
		return pagingSelect.toString();
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
		return buildLimitString(sql, null, page, lines);
	}

	public void setBaseProperties(Document doc, ResultSet rs) throws Exception {
		ResultSetMetaData metaData = rs.getMetaData();
		for (int i = 1; i <= metaData.getColumnCount(); i++) {
			String colName = metaData.getColumnLabel(i);

			if (!colName.startsWith("ITEM_")) {
				try {
					switch (metaData.getColumnType(i)) {

					case Types.CLOB:
						ObjectUtil.setProperty(doc, colName, rs
								.getString(colName), false);
						break;
					case Types.CHAR:
					case Types.VARCHAR:
						ObjectUtil.setProperty(doc, colName, rs
								.getString(colName), false);
						break;

					case Types.NUMERIC:
					case Types.INTEGER:
					case Types.DECIMAL:
						if (metaData.getPrecision(i) > 1
								&& metaData.getScale(i) == 0) {
							ObjectUtil.setProperty(doc, colName, Integer
									.valueOf((rs.getInt(colName))), false);
						} else if (metaData.getPrecision(i) == 1
								&& metaData.getScale(i) == 0) {
							ObjectUtil.setProperty(doc, colName, (rs
									.getInt(colName) == 1 ? Boolean
									.valueOf(true) : Boolean.valueOf(false)),
									false);
						} else {
							ObjectUtil.setProperty(doc, colName, new Double(rs
									.getDouble(colName)), false);
						}
						break;
					case Types.DOUBLE:
						if (metaData.getScale(i) == 0) {
							ObjectUtil.setProperty(doc, colName, Integer
									.valueOf(rs.getInt(colName)), false);
						} else {
							ObjectUtil.setProperty(doc, colName, new Double(rs
									.getDouble(colName)), false);
						}
						break;

					case Types.FLOAT:
						ObjectUtil.setProperty(doc, colName, new Float(rs
								.getFloat(colName)), false);
						break;

					case Types.REAL:
					case Types.BOOLEAN:
						ObjectUtil.setProperty(doc, colName, Boolean.valueOf(rs
								.getBoolean(colName)), false);
						break;
					case Types.DATE:
					case Types.TIME:
					case Types.TIMESTAMP:
						ObjectUtil.setProperty(doc, colName, rs
								.getTimestamp(colName), false);
						break;
					default:
						ObjectUtil.setProperty(doc, colName, rs
								.getObject(colName), false);
					}
				} catch (Exception e) {
					log.warn(colName + " error: " + e.getMessage());
				}
			}
		}

		if (doc.getForm() != null
				&& doc.getForm().getType() == Form.FORM_TYPE_NORMAL) {
			doc.setMappingId(doc.getId());
		}

		doc.setItems(createItems(rs, doc));
	}

	protected void setItemValue(ResultSet rs, int i, Item item)
			throws SQLException {
		ResultSetMetaData metaData = rs.getMetaData();
		int columnType = metaData.getColumnType(i);

		// Set Item's type and value
		switch (columnType) {

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
			item.setDatevalue(rs.getTimestamp(i));
			break;

		default:
			item.setType(Item.VALUE_TYPE_VARCHAR);
			item.setVarcharvalue(rs.getString(i));
		}
	}

	public int findVersions(String id) throws DAOException, SQLException {
		PreparedStatement statement = null;
		ResultSet rs = null;

		int rtn = 0;
		try {
			String formName = findFormName(id);
			if (formName != null && !formName.equals("")) {
				String sql = "SELECT doc.VERSIONS FROM "
						+ getFullTableName(_TBNAME);
				sql += " doc WHERE doc.ID=?";
//				log.info(dbType + sql);
				statement = connection.prepareStatement(sql);
				statement.setString(1, id);
				rs = statement.executeQuery();
			}
			if (rs != null) {
				if (rs.next()) {
					rtn = rs.getInt("VERSIONS");
					rs.close();
				}
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			PersistenceUtils.closeResultSet(rs);//Add By XGY 20130218
			PersistenceUtils.closeStatement(statement);
		}

		return rtn;
	}

	/**
	 * 根据符合DQL语句,最后修改文档日期,以及应用标识查询并分页,返回文档的DataPackage.
	 * 
	 * DataPackage为一个封装类，此类封装了所得到的文档数据并分页。 dql语句的详细说明参考以上文档头.
	 * 
	 * @see DataPackage#datas
	 * @see DataPackage#getPageNo()
	 * @see DataPackage#getLinesPerPage()
	 * @see DataPackage#getPageCount()
	 * @param dql
	 *            dql语句
	 * @param date
	 *            最后修改文档日期
	 * @param page
	 *            当前页码
	 * @param lines
	 *            每页显示行数
	 * @param application
	 *            应用标识
	 * @return 符合条件的文档的DataPackage
	 * @throws Exception
	 */
	public Iterator<Document> queryByDQLAndDocumentLastModifyDate(String dql,
			Date date, int page, int lines, String domainid) throws Exception {
		if ((dql != null && dql.trim().length() > 0)) {

			String formName = parseDQLFormName(dql);
			TableMapping tableMapping = getTableMapping(formName);

			String where = DQLASTUtil.parseToHQL(parseDQLWhere(dql),
					tableMapping, sqlFuction);

			String sql = "SELECT * FROM " + tableMapping.getTableName(1)
					+ " doc WHERE ISTMP=0 AND DOMAINID='" + domainid + "'";
			if (where != null && where.trim().length() > 0) {
				sql += " AND " + where;
			}

			// 增量的情况下加入最后修改日期条件
			if (date != null) {
				sql += " AND LASTMODIFIED > ?";
			}

			// 查询指定的页和行
			sql = buildLimitString(sql, page, lines);

			ArrayList<Document> datas = new ArrayList<Document>();
			PreparedStatement statement = null;
			ResultSet rs = null;
//			log.info(dbType + sql);
			try {
				statement = connection.prepareStatement(sql);

				// 增量情况下设置参数
				if (date != null) {
					statement.setTimestamp(1, new Timestamp(date.getTime()));
				}

				rs = statement.executeQuery();

				while (rs != null && rs.next()) {
					Document doc = new Document();
					setBaseProperties(doc, rs);
					datas.add(doc);
				}

				return datas.iterator();
			} catch (SQLException e) {
				throw e;
			} finally {
				PersistenceUtils.closeResultSet(rs);//Add By XGY 20130218
				PersistenceUtils.closeStatement(statement);
			}
		}
		return null;
	}
}
