/*
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package OLink.bpm.core.dynaform.document.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;

import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.core.dynaform.document.dql.MssqlSQLFunction;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.document.ejb.Item;
import OLink.bpm.util.DbTypeUtil;
import org.apache.log4j.Logger;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DAOException;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.util.ObjectUtil;
import OLink.bpm.util.StringUtil;

/**
 * 本系统的Document的查询语句运用自定义的DQL语句.DQL语句类似HQL语句.
 * <p>
 * DQL查询语句语法为:$formname=formname(模块表单名)+ 查询条件;
 * 
 * 例1: 查询付款费用模块下payment的Document的一条为广东省广州市的记录.
 * <p>
 * formname="付款费用/payment";条件为w="and 省份='广东省' and 城市='广州'",条件用"and" 连接起来.
 * 此时的DQL语句为$formname=formname+w . 此处的"付款费用"为模块名,"payment"该模块下的表单名.((表单名与动态表名同名)
 * <p>
 * 系统会将上述所得的DQL转为hibernate的HQL, 最后得出的SQL语句为"SELECT Item_省份,item_城市 FROM
 * tlk_payment where item_省份='广东省' and item_城市='广州'".
 * tlk_payment为动态表名(表名规则为前缀"tlk"+表单名). (动态表的字段名为前缀"item_"+表单字段名).
 * <p>
 * 如果查询语句中的字列有Document的属性时.要加上"$"+属性名,如:$id,$formname.
 * 有Document属性字段的DQL:$formname="付款费用/payment and $id='1000' and
 * $childs.id='1111'";id,chinlds为Document的属性名. Document的属性名有如下: ID, PARENT,
 * LASTMODIFIED, FORMNAME, STATE, AUDITDATE, AUTHOR, CREATED, FORMID, ISTMP,
 * FLOWID, VERSIONS, SORTID, APPLICATIONID, STATEINT, STATELABEL ".
 * 
 * <p>
 * 若查询语句中的字列有item字段时直接写item名.如上述的省份,城市.$formname="付款费用/payment and 省份='广东省 and
 * 城市='广州'".省份,城市为ITEM字段.
 * 
 * 
 * @author Marky
 * 
 */
public class MssqlDocStaticTblDAO extends AbstractDocStaticTblDAO implements
		DocumentDAO {
	public final static Logger log = Logger
			.getLogger(MssqlDocStaticTblDAO.class);

	public MssqlDocStaticTblDAO(Connection conn, String applicationId)
			throws Exception {
		super(conn, applicationId);
		dbType = "MS SQL Server: ";
		this.schema = DbTypeUtil.getSchema(conn, DbTypeUtil.DBTYPE_MSSQL);
		sqlFuction = new MssqlSQLFunction();
	}

	/**
	 * 根据符合SQL执行语句以及应用标识查询并分页,返回文档的数据集合.
	 * 
	 * @param sql
	 * @param params
	 * @param page
	 *            当前页码
	 * @param lines
	 *            每页显示行数
	 * 
	 * @return 文档的数据集合
	 * @throws Exception
	 * @throws SQLException
	 * @throws DAOException
	 */
	public Collection<Document> queryBySQL(String sql, int page, int lines,
			String domainid) throws Exception {
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			ArrayList<Document> datas = new ArrayList<Document>();

			sql = buildLimitString(sql, page, lines);
			
			statement = connection.prepareStatement(sql);

			rs = statement.executeQuery();

			int databaseVersion = connection.getMetaData()
					.getDatabaseMajorVersion();
			if (9 <= databaseVersion) {
			} else {
				// JDBC1.0
				long emptylines = 1L * (page - 1) * lines;
				for (int i = 0; i < emptylines && rs.next(); i++) {
					// keep empty
				}
				// if (page>1)
				// rs.absolute((page-1)* lines); //JDBC2.0
			}

			for (int i = 0; i < lines && rs.next(); i++) {
				Document doc = new Document();
				setBaseProperties(doc, rs);
				datas.add(doc);
			}

			return datas;

		} catch (SQLException e) {
			throw e;
		} finally {
			PersistenceUtils.closeResultSet(rs);//Add By XGY 20130218
			PersistenceUtils.closeStatement(statement);
		}
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
	 * @throws SQLException
	 */
	public String buildLimitString(String sql, int page, int lines)
			throws SQLException {
		if (lines == Integer.MAX_VALUE) {
			return sql;
		}

		// int to = (page - 1) * lines;
		StringBuffer pagingSelect = new StringBuffer(100);

		int databaseVersion = connection.getMetaData()
				.getDatabaseMajorVersion();
		if (9 <= databaseVersion) {// 2005 row_number() over () 分页
			pagingSelect.append("SELECT TOP " + lines + " * FROM (");
			pagingSelect
					.append("SELECT ROW_NUMBER() OVER (ORDER BY DOMAINID) AS ROWNUMBER, TABNIC.* FROM (");
			
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

	/**
	 * sortCols 排序字段数组 tName 表的别名
	 * **/
	public String getOrderFieldsToSqlString(String[] sortCols, String tName) {
		String sortCol = "";
		for (int i = 0; i < sortCols.length; i++) {
			if (sortCols[i] != null && sortCols[i].trim().length() > 0) {
				if (tName != null && !tName.equals("")) {
					if (i == sortCols.length - 1) {
						if(sortCols[i].contains("AuditorNames")){
							sortCol += "cast(" + tName + "." + sortCols[i].substring(0, 13) + "as varchar(5000)) " + sortCols[i].substring(13);
						}else{
							sortCol += tName + "." + sortCols[i];
						}
					} else {
						if(sortCols[i].contains("AuditorNames")){
							sortCol += "cast(" + tName + "." + sortCols[i].substring(0, 13) + "as varchar(5000)) " + sortCols[i].substring(13) + ",";
						}else{
							sortCol += tName + "." + sortCols[i] + ",";
						}
					}
				} else {
					if (i == sortCols.length - 1) {
						sortCol += sortCols[i];
					} else {
						sortCol += sortCols[i] + ",";
					}
				}
			}
		}
		return sortCol;
	}
	
	/**
	 * 生成order by 条件.
	 * 
	 * @param sql
	 *            sql语句
	 * @param params
	 *            参数
	 * @return 生成order by 条件sql语句字符串
	 */
	protected String bulidOrderString(String sql, ParamsTable params) {
		StringBuffer buffer = new StringBuffer();
		String[] sortCols =null;
		String sortStatus = "";
		String orderby = "";

		if (params != null) {
			sortCols = (String[]) params.getParameter("_sortCol");
			sortStatus = (String) params.getParameter("_sortStatus");
			orderby = params.getParameterAsString("_orderby");
		}

		buffer.append("SELECT top " + Integer.MAX_VALUE + " * FROM (" + sql
				+ ") ordtb");

		if (sortCols != null && sortCols.length> 0) {
			buffer.append(" ORDER BY ");
			buffer.append(this.getOrderFieldsToSqlString(sortCols, "ordtb"));
			buffer.append(", ordtb.ID");
		} else if (orderby != null && orderby.trim().length() > 0) {
			buffer.append(" ORDER BY ordtb." + orderby);
			buffer.append(StringUtil.isBlank(sortStatus) ? "" : " "
					+ sortStatus); // 增加排序方式,
			// ASC、DESC
			buffer.append(", ordtb.ID");
		} else if (-1 == sql.toUpperCase().indexOf("ORDER BY")) {
			buffer.append(" ORDER BY ordtb.ID");
		}

		return buffer.toString();
	}

	public void setBaseProperties(Document doc, ResultSet rs) throws Exception {
		ResultSetMetaData metaData = rs.getMetaData();
		for (int i = 1; i <= metaData.getColumnCount(); i++) {
			String colName = metaData.getColumnName(i);
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
					case Types.BIT:
						if (metaData.getPrecision(i) > 1
								&& metaData.getScale(i) == 0) {
							ObjectUtil.setProperty(doc, colName, Integer
									.valueOf(rs.getInt(colName)), false);
						} else if (metaData.getPrecision(i) == 1
								&& metaData.getScale(i) == 0) {
							ObjectUtil.setProperty(doc, colName, (rs
									.getInt(colName) == 0 ? Boolean
									.valueOf(false) : Boolean.valueOf(true)),
									false);
						} else {
							ObjectUtil.setProperty(doc, colName, new Double(rs
									.getDouble(colName)), false);
						}
						break;
					case Types.DECIMAL:
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

	public void setItemValue(ResultSet rs, int i, Item item)
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

	public boolean isExist(String id) throws Exception {
		PreparedStatement statement = null;
		ResultSet rs = null;

		try {
			String sql = "SELECT COUNT(*) _ROWCOUNT FROM "
					+ getFullTableName(_TBNAME) + " doc WHERE doc.ID=?";

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
	
	/**
	 * 获取处理中SQL语句
	 * 
	 * @param actorId
	 * @return
	 */
	protected String getProcessingSQL(String actorId) {
		String processingSQL = "select distinct top 100 percent doc.lastmodified, doc.id DOCID,doc.formid FORMID,fs.flowid FLOWID,pen.flowname FLOWNAME,doc.statelabel STATELABEL,convert(nvarchar(1000),pen.summary) as SUBJECT,convert(nvarchar(2000),doc.AUDITORNAMES) as AUDITORNAMES,convert(varchar(2000),doc.AUDITORLIST) as AUDITORLIST,doc.APPLICATIONID,doc.DOMAINID "
				+ " from  "
				+ getFullTableName(_TBNAME)
				+ "  doc,"
				+ getFullTableName("t_flow_intervention")
				+ " pen,"
				+ getFullTableName("t_flowstatert")
				+ " fs,"
				+ getFullTableName("t_nodert")
				+ " node,"
				+ getFullTableName("t_actorrt")
				+ " actor "
				+ " where "
				+ "doc.id = pen.id and doc.id =fs.docid and fs.id =node.flowstatert_id and  node.id = actor.nodert_id and "
				+ "doc.issubdoc is null and doc.parent is null and doc.state is not null and "
				+ "doc.statelabel is not null "
				+ " and actor.actorid in ('"
				+ actorId
				+ "')";

		return processingSQL;
	}
	
	protected String getProcessedSQL(String actorId) {
		String processedSQL = "select distinct top 100 percent doc.lastmodified, doc.id DOCID,doc.formid FORMID,fs.flowid FLOWID,rhis.flowname FLOWNAME,doc.statelabel STATELABEL,convert(nvarchar(1000),pen.summary) as SUBJECT,convert(nvarchar(2000),doc.AUDITORNAMES) as AUDITORNAMES,convert(varchar(2000),doc.AUDITORLIST) as AUDITORLIST,doc.APPLICATIONID,doc.DOMAINID "
				+ " from  "
				+ getFullTableName(_TBNAME)
				+ "  doc,"
				+ getFullTableName("t_actorhis")
				+ " ahis,"
				+ getFullTableName("t_relationhis")
				+ " rhis,"
				+ getFullTableName("t_flow_intervention")
				+ " pen,"
				+ getFullTableName("t_flowstatert")
				+ " fs"
				+ " where "
				+ "doc.id=rhis.docid and rhis.id = ahis.nodehis_id and doc.id = pen.id and doc.id =fs.docid and "
				+ "doc.issubdoc is null and doc.parent is null and doc.state is not null and "
				+ "doc.statelabel is not null "
				+ " and ahis.actorid in ('"
				+ actorId
				+ "')";

		return processedSQL;
	}
}
