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

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.core.dynaform.document.dql.OracleSQLFunction;
import OLink.bpm.core.dynaform.document.ejb.Item;
import OLink.bpm.util.DbTypeUtil;
import OLink.bpm.util.ObjectUtil;
import OLink.bpm.util.StringUtil;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.form.ejb.Form;
import org.apache.log4j.Logger;

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
public class OracleDocStaticTblDAO extends AbstractDocStaticTblDAO implements DocumentDAO {
	public final static Logger log = Logger.getLogger(OracleDocStaticTblDAO.class);

	// Connection connection;

	public OracleDocStaticTblDAO(Connection conn, String applicationId) throws Exception {
		super(conn, applicationId);
		this.schema = DbTypeUtil.getSchema(conn, DbTypeUtil.DBTYPE_ORACLE);
		sqlFuction = new OracleSQLFunction();
	}

	public boolean isExist(String id) throws Exception {
		PreparedStatement statement = null;
		ResultSet rs = null;

		try {
			String sql = "SELECT count(*) FROM " + getFullTableName("T_DOCUMENT") + " doc WHERE doc.id=?";

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
	public String buildLimitString(String sql, String orderby, int page, int lines) {
		if (lines == Integer.MAX_VALUE) {
			return sql;
		}

		int from = (page - 1) * lines;
		int to = page * lines;
		StringBuffer pagingSelect = new StringBuffer(100);

		pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
		pagingSelect.append(sql);
		pagingSelect.append(" ) row_ where rownum <= ");
		pagingSelect.append(to);
		pagingSelect.append(") where rownum_ > ");
		pagingSelect.append(from);
		if (orderby != null && !orderby.trim().equals(""))
			pagingSelect.append(orderby);

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
						ObjectUtil.setProperty(doc, colName, rs.getString(colName), false);
						break;
					case Types.CHAR:
					case Types.VARCHAR:
						ObjectUtil.setProperty(doc, colName, rs.getString(colName), false);
						break;

					case Types.NUMERIC:
					case Types.INTEGER:
						if (metaData.getPrecision(i) > 1 && metaData.getScale(i) == 0) {
							ObjectUtil.setProperty(doc, colName, Integer.valueOf(rs.getInt(colName)), false);
						} else if (metaData.getPrecision(i) == 1 && metaData.getScale(i) == 0) {
							ObjectUtil.setProperty(doc, colName, (rs.getInt(colName) == 1 ? Boolean.valueOf(true) : Boolean
									.valueOf(false)), false);
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
						ObjectUtil.setProperty(doc, colName, new Double(rs.getFloat(colName)), false);
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
		String[] sortCols = null;
		String sortStatus = "";
		String orderby = "";
		String fieldType = "";

		if (params != null) {
			sortCols = (String[]) params.getParameter("_sortCol");
			sortStatus = (String) params.getParameter("_sortStatus");
			orderby = params.getParameterAsString("_orderby");
			fieldType = params.getParameterAsString("fieldType");
		}

		buffer.append("SELECT * FROM (" + sql + ") ordtb");
		if (sortCols != null && sortCols.length > 0) {

			if (fieldType != null && fieldType.trim().length() > 0 && fieldType.equals(Item.VALUE_TYPE_TEXT)) {
				buffer.append(" ORDER BY cast(" + this.getOrderFieldsToSqlString(sortCols, "ordtb") + " as varchar2(4000))");
			} else {
				buffer.append(" ORDER BY " + this.getOrderFieldsToSqlString(sortCols, "ordtb"));
			}
			buffer.append(", ordtb.ID");
		} else if (orderby != null && orderby.trim().length() > 0) {
			buffer.append(" ORDER BY ordtb." + orderby + " ");
			buffer.append(StringUtil.isBlank(sortStatus) ? "" : " " + sortStatus); // 增加排序方式,
			// ASC、DESC
			buffer.append(", ordtb.ID");
		} else {
			buffer.append(" ORDER BY ordtb.ID");
		}

		return buffer.toString();
	}
	
	protected String getProcessingSQL(String actorId) {
		String processingSQL = "select distinct doc.id DOCID,doc.formid FORMID,fs.flowid FLOWID,pen.flowname FLOWNAME,doc.statelabel STATELABEL,to_char(substr(pen.summary,1,1000)) as SUBJECT,to_char(substr(doc.AUDITORNAMES,1,2000)) as AUDITORNAMES,to_char(substr(doc.AUDITORLIST,1,2000)) as AUDITORLIST,doc.APPLICATIONID,doc.DOMAINID "
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
		String processedSQL = "select distinct doc.id DOCID,doc.formid FORMID,fs.flowid FLOWID,rhis.flowname FLOWNAME,doc.statelabel STATELABEL,to_char(substr(pen.summary,1,1000)) as SUBJECT,to_char(substr(doc.AUDITORNAMES,1,2000)) as AUDITORNAMES,to_char(substr(doc.AUDITORLIST,1,2000)) as AUDITORLIST,doc.APPLICATIONID,doc.DOMAINID "
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
