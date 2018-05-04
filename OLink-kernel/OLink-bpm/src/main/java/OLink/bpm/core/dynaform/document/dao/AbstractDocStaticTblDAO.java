package OLink.bpm.core.dynaform.document.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.dynaform.document.dql.DQLASTUtil;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.dynaform.form.ejb.mapping.TableMapping;
import OLink.bpm.core.dynaform.work.ejb.WorkVO;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.document.ejb.Item;
import OLink.bpm.core.dynaform.document.dql.SQLFunction;
import OLink.bpm.util.StringUtil;
import org.apache.log4j.Logger;

import eWAP.core.Tools;

import OLink.bpm.base.dao.DAOException;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.HibernateSQLUtils;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.base.dao.ValueObject;

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
 * VERSIONS, SORTID, APPLICATIONID, STATEINT, STATELABEL ".
 * 
 * <p>
 * 若查询语句中的字列有item字段时直接写item名.如上述的省份,城市.$formname="付款费用/payment and 省份='广东省 and
 * 城市='广州'".省份,城市为ITEM字段.
 * 
 * 
 * @author Chris
 * 
 */
public abstract class AbstractDocStaticTblDAO {

	final static int OPERATEMODE_CREATE = 1;

	final static int OPERATEMODE_UPDATE = 2;

	final static int OPERATEMODE_DELETE = 3;

	final static int OPERATEMODE_UPDATE_ITEM = 4;

	final static int OPERATEMODE_UPDATE_SUBITEM = 5;

	final static String _TBNAME = "T_DOCUMENT";

	final static String ITEM_FIELD_PREFIX = "ITEM_";

	protected final static Logger log = Logger.getLogger(AbstractDocStaticTblDAO.class);

	protected Connection connection;

	protected String applicationId;

	protected String dbType = "Oracle :";// 标识数据库类型

	protected SQLFunction sqlFuction;

	protected String schema = "";

	public AbstractDocStaticTblDAO(Connection conn, String applicationId) throws Exception {
		this.connection = conn;
		this.applicationId = applicationId;
	}

	/**
	 * 获取表格映射关系
	 * 
	 * @param formName
	 *            表单全名
	 * @param applicationId
	 *            应用ID
	 * @return 表格映射
	 * @throws Exception
	 */
	protected TableMapping getTableMapping(String formName) throws Exception {
		String shortName = getFormShortName(formName);

		FormProcess formProcess = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
		Form form = formProcess.doViewByFormName(shortName, applicationId);
		if (form == null) {
			throw new Exception("Form: " + formName + " does not exist");
		}

		TableMapping mapping = form.getTableMapping();
		return mapping;
	}

	/**
	 * 根据父Document主键(primary key),返回所属父Document的子Document集合.
	 * 
	 * @param parentid
	 *            Document主键(primary key)
	 * @return 所属父Document的子Document集合.
	 * @throws Exception
	 */

	public Collection<Document> queryByParentID(String parentid) throws Exception {
		String sql = "SELECT doc.formname FROM " + getFullTableName(_TBNAME) + " doc";
		sql += " WHERE doc.parent = ? AND doc.istmp = 0 GROUP BY formname";

		Collection<Document> rtn = new ArrayList<Document>();
		PreparedStatement statement = null;
		ResultSet rs=null;
		try {

			statement = connection.prepareStatement(sql);
			statement.setString(1, parentid);
			rs = statement.executeQuery();
			while (rs.next()) {
				String formname = rs.getString("FORMNAME");
				Collection<Document> childs = queryByParentID(parentid, formname);
				if (childs != null) {
					rtn.addAll(childs);
				}
			}
			return rtn;
		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeResultSet(rs);//Add By XGY 20130218
			PersistenceUtils.closeStatement(statement);
		}
	}

	/**
	 * 根据父文档主键)与子表单名查询,返回所属父Document的子Document集合.
	 * 
	 * @param parentid
	 *            父文档主键
	 * @param formName
	 *            子表单名
	 * @return 所属父Document的子Document集合.
	 * @throws Exception
	 */
	public Collection<Document> queryByParentID(String parentid, String formName) throws Exception {
		return queryByParentID(parentid, formName, null);
	}

	/**
	 * 根据父文档主键)与子表单名查询,返回所属父Document的子Document集合.
	 * 
	 * @param parentid
	 *            父文档主键
	 * @param formName
	 *            子表单名
	 * @param istmp
	 *            是否临时表单可选0|1|null
	 * @return 所属父Document的子Document集合.
	 * @throws Exception
	 */
	public Collection<Document> queryByParentID(String parentid, String formName, Boolean istmp) throws Exception {
		// 表单映射
		TableMapping tableMapping = getTableMapping(formName);

		StringBuffer sql = new StringBuffer();

		sql.append("SELECT " + getSelectPart(tableMapping) + " FROM " + getInnerJoinPart(tableMapping));
		sql.append(" WHERE d.parent = ?");
		if (istmp != null) {
			sql.append(" AND d.istmp = " + istmp);
		}

//		log.info(dbType + sql.toString());
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {

			statement = connection.prepareStatement(sql.toString());
			statement.setString(1, parentid);

			rs = statement.executeQuery();

			ArrayList<Document> docs = new ArrayList<Document>();
			while (rs != null && rs.next()) {
				Document doc = new Document();
				setBaseProperties(doc, rs);
				docs.add(doc);
			}
			return docs;

		} catch (SQLException e) {
			throw e;
		} finally {
			PersistenceUtils.closeResultSet(rs);//Add By XGY 20130218
			PersistenceUtils.closeStatement(statement);
		}
	}

	/**
	 * 存储所建的Document.
	 * 
	 * @param doc
	 *            Document对象
	 */
	public void storeDocument(Document doc) throws Exception {
		boolean isExit = false;
		PreparedStatement statement = null;
		ResultSet rs=null;
		try {
			TableMapping tableMapping = getTableMapping(doc.getFormname());

			String sql = "SELECT COUNT(*) ROWCOUNT_ FROM " + getInnerJoinPart(tableMapping);
			sql += " WHERE d.ID = ?";

//			log.info(dbType + sql);

			statement = connection.prepareStatement(sql);
			statement.setString(1, doc.getId());
			rs = statement.executeQuery();
			if (rs.next() && rs.getInt(1) > 0) {
				isExit = true;
			}

		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeResultSet(rs);//Add By XGY 20130218
			PersistenceUtils.closeStatement(statement);
		}

		if (isExit) {
			updateDocument(doc);
		} else {
			createDocument(doc);
		}
	}

	/**
	 * 创建文档头. 此文档头用来保存不同Document的信息. 此方法实现数据库文档表的相应字列插入相应Document属性的值.
	 * 
	 * @param doc
	 *            Document对象
	 * @throws Exception
	 */
	public void createDocumentHead(Document doc) throws Exception {
		PreparedStatement statement = null;

		String sql = "INSERT INTO " + getFullTableName(_TBNAME) + " (";
		try {

			sql += "ID,PARENT,LASTMODIFIED,FORMNAME,STATE,STATEINT,INITIATOR,AUDITUSER,AUDITDATE,AUTHOR,AUTHOR_DEPT_INDEX,CREATED,FORMID,ISTMP,VERSIONS,SORTID,APPLICATIONID,STATELABEL,AUDITORNAMES,LASTFLOWOPERATION,LASTMODIFIER,DOMAINID,AUDITORLIST,MAPPINGID";

			sql += ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			// Transfer Data
			statement = connection.prepareStatement(sql);

			int index = setBaseParameters(0, statement, doc, DQLASTUtil.TABEL_TYPE_CONTENT, false);
			statement.setObject(++index, doc.getMappingId());
//			log.info(dbType + sql);
			// Exec SQL
			statement.execute();

		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeStatement(statement);
		}
	}

	/**
	 * 
	 * 创建文档.并创建相应的文档头。 此实现根据相应的Document的item的类型值与值对应保存到数据库相应的动态表相应名的字列.
	 * 动态表的字列为公共字列+动态字列。动态表动态字列的名为以"ITEM_"为前缀加上ITEM名. 公共字列为所有生成的动态表都共有的字列。
	 * 公共字列为：ID, PARENT, LASTMODIFIED , FORMNAME , OWNER , STATE , AUDITDATE ,
	 * AUTHOR , CREATED , ISSUBDOC , FORMID, ISTMP ,VERSIONS , SORTID ,
	 * APPLICATIONID , STATEINT , STATELABEL 。 *
	 * 而公共字列值即赋予Document相应属性值.(比如：动态表中的FORMNAME字列值
	 * ，即赋予Document相应FORMNAME属性的值。其它公共字列以致类推).
	 * 
	 * @see OracleDocStaticTblDAO#createDocumentHead(Document)
	 * @param doc
	 *            Document对象
	 * @throws Exception
	 */
	public void createDocument(Document doc) throws Exception {
		createDocument(doc, DQLASTUtil.TABEL_TYPE_CONTENT);
	}

	/**
	 * 根据不同类型创建不同的表。如：DQLASTUtil.TABEL_TYPE_CONTENT为1时为以TLK_开头的动态表；
	 * 为2时以LOG_开头的动态表.
	 * 
	 * @param doc
	 * @param tabelType
	 * @throws Exception
	 */
	public void createDocument(Document doc, int tabelType) throws Exception {
		if (tabelType == DQLASTUtil.TABEL_TYPE_CONTENT) {
			// 创建文档头
			createDocumentHead(doc);
		}
		PreparedStatement statement = null;
		Form form = doc.getForm();
		TableMapping tableMapping = form.getTableMapping();
		// 表单映射的数据库表名
		String tableName = tableMapping.getTableName(tabelType);
		StringBuffer sql = new StringBuffer();
		sql.append("INSERT INTO " + getFullTableName(tableName) + " (");

		try {
			// 1. 添加Item
			StringBuffer sqlPart2 = new StringBuffer();
			Collection<Item> items = doc.getItems();
			Iterator<Item> iter = items.iterator();

			if (!items.isEmpty()) {
				while (iter.hasNext()) {
					Item item = iter.next();
					String columnName = tableMapping.getColumnName(item.getName());

					if (!StringUtil.isBlank(columnName)) {
						sql.append(columnName).append(",");
						sqlPart2.append("?,");
					}
				}
				String temp = sqlPart2.substring(0, sqlPart2.lastIndexOf(","));
				sqlPart2.setLength(0);
				sqlPart2.append(temp);
			}

			// 2. 添加基本值
			sql.append(tableMapping.getPrimaryKeyName());

			if (form.getType() == Form.FORM_TYPE_NORMAL) { // 普通模式
				sql
						.append(",PARENT,LASTMODIFIED,FORMNAME,STATE,STATEINT,AUDITUSER,AUDITDATE,AUTHOR,AUTHOR_DEPT_INDEX,CREATED,FORMID,ISTMP,VERSIONS,SORTID,APPLICATIONID,STATELABEL,AUDITORNAMES,LASTFLOWOPERATION,LASTMODIFIER,DOMAINID,AUDITORLIST");
				if (!items.isEmpty()) {
					sqlPart2.append(",?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?");
				} else {
					sqlPart2.append("?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?");
				}
			}

			// 创建Log 表
			if (tabelType == DQLASTUtil.TABEL_TYPE_LOG) {
				sql.append(",DOC_ID");
				sql.append(") VALUES (" + sqlPart2 + ",?,?");
			} else if (tabelType == DQLASTUtil.TABEL_TYPE_CONTENT) {
				sql.append(") VALUES (" + sqlPart2 + ",?");
			}

			sql.append(")");

//			log.info(dbType + sql.toString());

			// Transfer Data
			statement = connection.prepareStatement(sql.toString());

			// 3.设置Item值
			int j = 0;
			iter = items.iterator();
			while (iter.hasNext()) {
				Item item = iter.next();
				String columnName = tableMapping.getColumnName(item.getName());
				if (!StringUtil.isBlank(columnName)) {
					j++;
					prepItemData(statement, doc, j, item);
				}
			}

			// 4. 设置基本字段值
			if (tabelType == DQLASTUtil.TABEL_TYPE_LOG) {
				int index = setBaseParameters(j, statement, doc, DQLASTUtil.TABEL_TYPE_LOG, true); // 日志
				statement.setString(++index, doc.getId());
			} else if (tabelType == DQLASTUtil.TABEL_TYPE_CONTENT) {
				if (form.getType() == Form.FORM_TYPE_NORMAL) { // 普通模式
					setBaseParameters(j, statement, doc);
				} else if (form.getType() == Form.FORM_TYPE_NORMAL_MAPPING) { // 映射模式
					statement.setObject(++j, doc.getMappingId());
				}
			}

			// Exec SQL
			statement.executeUpdate();

		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeStatement(statement);

		}
	}

	/**
	 * 设置参数
	 * 
	 * @param currentIndex
	 * @param statement
	 * @param doc
	 * @return
	 * @throws Exception
	 */
	protected int setBaseParameters(int currentIndex, PreparedStatement statement, Document doc) throws Exception {
		return setBaseParameters(currentIndex, statement, doc, DQLASTUtil.TABEL_TYPE_CONTENT, true);
	}

	protected int setBaseParameters(int currentIndex, PreparedStatement statement, Document doc, int tabelType,
			boolean withoutBaseAttirbute) throws Exception {
		if (tabelType == DQLASTUtil.TABEL_TYPE_CONTENT) {
			statement.setString(++currentIndex, doc.getId());
		} else if (tabelType == DQLASTUtil.TABEL_TYPE_LOG) {
			statement.setString(++currentIndex, Tools.getTimeSequence());
		}

		statement.setString(++currentIndex, doc.getParentid());

		// Lastmodified
		if (doc.getLastmodified() != null) {
			statement.setTimestamp(++currentIndex, new Timestamp(doc.getLastmodified().getTime()));
		} else {
			statement.setTimestamp(++currentIndex, null);
		}
		statement.setString(++currentIndex, doc.getFormname());
		statement.setString(++currentIndex, doc.getStateid());
		statement.setInt(++currentIndex, doc.getStateInt());
		if (!withoutBaseAttirbute)
			statement.setString(++currentIndex, doc.getInitiator());
		statement.setString(++currentIndex, doc.getAudituser());

		// AUDITDATE
		if (doc.getAuditdate() != null) {
			statement.setTimestamp(++currentIndex, new Timestamp(doc.getAuditdate().getTime()));
		} else {
			statement.setTimestamp(++currentIndex, null);
		}

		if (doc.getAuthor() != null) {
			statement.setString(++currentIndex, doc.getAuthor().getId());
		} else {
			statement.setString(++currentIndex, null);
		}
		statement.setString(++currentIndex, doc.getAuthorDeptIndex());

		if (doc.getCreated() != null) {
			statement.setTimestamp(++currentIndex, new Timestamp(doc.getCreated().getTime()));
		} else {
			// statement.setNull(++currentIndex, Types.NULL);
			statement.setTimestamp(++currentIndex, null);
		}

		statement.setString(++currentIndex, doc.getFormid());
		statement.setInt(++currentIndex, doc.getIstmp() ? 1 : 0);
		// statement.setString(++currentIndex, doc.getFlowid());
		statement.setInt(++currentIndex, doc.getVersions()+1);
		statement.setString(++currentIndex, doc.getSortId());
		statement.setString(++currentIndex, doc.getApplicationid());
		statement.setString(++currentIndex, doc.getStateLabel());
		statement.setString(++currentIndex, doc.getAuditorNames());
		statement.setString(++currentIndex, doc.getLastFlowOperation());

		if (doc.getLastmodifier() != null) {
			statement.setString(++currentIndex, doc.getLastmodifier());
		} else {
			statement.setString(++currentIndex, null);
		}

		statement.setString(++currentIndex, doc.getDomainid());
		statement.setString(++currentIndex, doc.getAuditorList());

		return currentIndex;
	}

	/**
	 * 根据用户,更新相应的文档头,并更新所属用户文档.
	 * 
	 * 此实现更新动态表结构，表结构规则为表名为"TLK_"+表单名,动态表的字列为公共字列+动态字列。动态表动态字列的名为以"ITEM_"
	 * 为前缀加上ITEM名. 公共字列为所有生成的动态表都共有的字列。 公共字列为：ID, PARENT, LASTMODIFIED ,
	 * FORMNAME , OWNER , STATE , AUDITDATE , AUTHOR , CREATED , ISSUBDOC ,
	 * FORMID, ISTMP , VERSIONS , SORTID , APPLICATIONID , STATEINT , STATELABEL
	 * 。
	 * 
	 * @param doc
	 *            文档对象
	 * @param user
	 *            用户
	 * @see OracleDocStaticTblDAO#updateDocumentHead(Document,
	 *      WebUser)
	 */

	public void updateDocument(Document doc) throws Exception {
		doc.setIstmp(false);
		// 更新文档头
		updateDocumentHead(doc);

		PreparedStatement statement = null;
		try {
			Form form = doc.getForm();
			TableMapping tableMapping = getTableMapping(doc.getFormname());
			// 表单映射的数据库表名
			// Prepare SQL Statement
			StringBuffer sql = new StringBuffer();
			sql.append("UPDATE " + getFullTableName(tableMapping.getTableName()) + " SET ");

			Collection<Item> items = doc.getItems();
			Iterator<Item> iter = items.iterator();
			while (iter.hasNext()) {
				Item item = iter.next();
				String columnName = tableMapping.getColumnName(item.getName());
				if (!StringUtil.isBlank(columnName)) {
					sql.append(columnName + "=?,");
				}
			}

			String pk = tableMapping.getPrimaryKeyName();
			sql.append(pk + "=?");

			if (form.getType() == Form.FORM_TYPE_NORMAL) { // 普通模式
				sql
						.append(",PARENT=?,LASTMODIFIED=?,FORMNAME=?,STATE=?,STATEINT=?,AUDITUSER=?,AUDITDATE=?,AUTHOR=?,AUTHOR_DEPT_INDEX=?,CREATED=?,FORMID=?,ISTMP=?,VERSIONS=?,SORTID=?,APPLICATIONID=?,STATELABEL=?,AUDITORNAMES=?,LASTFLOWOPERATION=?,LASTMODIFIER=?,DOMAINID=?,AUDITORLIST=?");
			}

			sql.append(" WHERE " + pk + "=?");

//			log.info(dbType + sql);

			// Transfer Data
			statement = connection.prepareStatement(sql.toString());

			int j = 0;
			iter = items.iterator();
			Item item = null;
			while (iter.hasNext()) {
				item = iter.next();
				String columnName = tableMapping.getColumnName(item.getName());
				if (!StringUtil.isBlank(columnName)) {
					j++;
					prepItemData(statement, doc, j, item);
				}
			}

			if (form.getType() == Form.FORM_TYPE_NORMAL) { // 普通模式
				int index = setBaseParameters(j, statement, doc);
				statement.setObject(++index, doc.getMappingId());
			} else if (form.getType() == Form.FORM_TYPE_NORMAL_MAPPING) { // 映射模式
				statement.setObject(++j, doc.getMappingId());
				statement.setObject(++j, doc.getMappingId());
			}

			// Exec SQL
			if (statement.executeUpdate() < 1) {
				throw new Exception("Row does not exist");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			PersistenceUtils.closeStatement(statement);
		}
	}

	/**
	 * 根据用户,更新所属用户文档的文档头. 此实现根据Document ID更新数据库的Document表相应字列值.
	 * 
	 * @param doc
	 *            文档对象
	 * @param user
	 *            用户
	 * @throws Exception
	 */
	private void updateDocumentHead(Document doc) throws Exception {
		PreparedStatement statement = null;
		try {
			// Prepare SQL Statement

			String sql = "UPDATE " + getFullTableName(_TBNAME) + " SET ";

			sql += "ID=?, PARENT=?, LASTMODIFIED=?, FORMNAME=?, STATE=?,STATEINT=?,INITIATOR=?,AUDITUSER=?, AUDITDATE=?, AUTHOR=?,AUTHOR_DEPT_INDEX=?, CREATED=?, FORMID=?, ISTMP=?, VERSIONS=?, SORTID=?, APPLICATIONID=?, STATELABEL=?, AUDITORNAMES=?, LASTFLOWOPERATION=?,LASTMODIFIER=?,DOMAINID=?,AUDITORLIST=?, MAPPINGID=?";
			sql += " WHERE ID=?";

			// Transfer Data
			statement = connection.prepareStatement(sql);

			int currentIndex = setBaseParameters(0, statement, doc, DQLASTUtil.TABEL_TYPE_CONTENT, false);
			statement.setObject(++currentIndex, doc.getMappingId());
			statement.setString(++currentIndex, doc.getId());

//			log.info(dbType + sql);
			// Exec SQL
			if (statement.executeUpdate() < 1) {
				throw new Exception("Row does not exist");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			PersistenceUtils.closeStatement(statement);
		}
	}

	public void removeDocumentByField(Form form, String[] fields) throws Exception {
		Statement statement = null;
		try {
			TableMapping tm = form.getTableMapping();
			StringBuffer set = new StringBuffer();
			for (int i = 0; i < fields.length; i++) {
				String fieldName = tm.getColumnName(fields[i]);
				set.append(fieldName + "=null, ");
			}
			int start = set.lastIndexOf(",");
			set.delete(start, set.length());
			String tableName = tm.getTableName();
			String sql = "update " + tableName + " set " + set;
			statement = connection.createStatement();
//			log.info(dbType + sql);
			statement.execute(sql);
		} catch (SQLException e) {
			throw e;
		} finally {
			PersistenceUtils.closeStatement(statement);
		}
	}

	public void removeDocumentByForm(Form form) throws Exception {
		Statement statement = null;
		try {
			String formName = form.getFullName();
			String condition = "(select STATE from " + getFullTableName(_TBNAME) + " where FORMNAME= '" + formName
					+ "')";

			TableMapping tableMapping = form.getTableMapping();

			// 删除流程相关数据
			String sql2 = "delete from T_ACTORRT where FLOWSTATERT_ID in " + condition;
			String sql3 = "delete from T_FLOWSTATERT where ID in " + condition;
			String sql4 = "delete from T_NODERT where FLOWSTATERT_ID in " + condition;
			String sql5 = "delete from T_PENDING where FORMNAME = '" + formName + "'";

			// 删除表单相关数据
			String sql0 = "delete from " + getFullTableName(_TBNAME) + " where FORMNAME = '" + formName + "'";
			String sql1 = "delete from " + getFullTableName(tableMapping.getTableName()) + " where "
					+ tableMapping.getPrimaryKeyName() + " in (select MAPPINGID from " + getFullTableName(_TBNAME)
					+ " where FORMNAME='" + formName + "')";

			String sql7 = "delete from " + getFullTableName(tableMapping.getTableName(DQLASTUtil.TABEL_TYPE_LOG))
					+ " where DOC_ID in (select id from " + getFullTableName(_TBNAME) + " where FORMNAME='" + formName
					+ "')";

			String sql6 = "delete from " + getFullTableName(tableMapping.getTableName(DQLASTUtil.TABLE_TYPE_AUTH))
					+ " where DOC_ID in (select id from " + getFullTableName(_TBNAME) + " where FORMNAME='" + formName
					+ "')";

			statement = connection.createStatement();

//			log.info(dbType + sql1);
			statement.execute(sql1);
//			log.info(dbType + sql0);
			statement.execute(sql0);
//			log.info(dbType + sql2);
			statement.execute(sql2);
//			log.info(dbType + sql3);
			statement.execute(sql3);
//			log.info(dbType + sql4);
			statement.execute(sql4);
//			log.info(dbType + sql5);
			statement.execute(sql5);
//			log.info(dbType + sql6);
			statement.execute(sql6);

			try {
//				log.info(dbType + sql7);
				statement.execute(sql7);
			} catch (SQLException e) {
				// 有可能不存在log表
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			PersistenceUtils.closeStatement(statement);
		}

	}

	/**
	 * 删除文档,若此文档有子文档连同一起删除. 删除文档的时，同时删除相应的文档(Document)头。
	 * 
	 * @see OracleDocStaticTblDAO#removeDocumentHead(Document)
	 * 
	 * @param doc
	 *            文档对象
	 */
	public void removeDocument(Document doc) throws Exception {
		PreparedStatement statement = null;

		try {
			TableMapping tableMapping = getTableMapping(doc.getFormname());

			Collection<Document> subdocs = doc.getChilds();
			if (subdocs != null && subdocs.size() > 0) {
				Iterator<Document> iter = subdocs.iterator();
				while (iter.hasNext()) {
					Document subdoc = iter.next();
					removeDocument(subdoc);
				}
			}

			String sql = "DELETE FROM " + getFullTableName(tableMapping.getTableName());
			sql += " WHERE " + tableMapping.getPrimaryKeyName() + "=?";

//			log.info(dbType + sql);

			statement = connection.prepareStatement(sql);
			statement.setObject(1, doc.getMappingId());
			statement.executeUpdate();

		} catch (SQLException e) {
			throw e;
		} finally {
			PersistenceUtils.closeStatement(statement);
		}

		removeDocumentHead(doc);

		removeDocument(doc, DQLASTUtil.TABEL_TYPE_LOG);

	}

	/**
	 * 删除文档头,若此文档有子文档头连同一起删除.
	 * 
	 * @param doc
	 *            文档对象
	 * @throws Exception
	 */
	public void removeDocumentHead(Document doc) throws Exception {

		String id = doc.getId();
		PreparedStatement statement = null;
		try {
			Collection<Document> subdocs = doc.getChilds();
			if (subdocs != null && subdocs.size() > 0) {
				Iterator<Document> iter = subdocs.iterator();
				while (iter.hasNext()) {
					Document subdoc = iter.next();
					removeDocument(subdoc);
				}
			}
			String sql = "DELETE FROM " + getFullTableName(_TBNAME) + " WHERE ID = ?";
			statement = connection.prepareStatement(sql);
//			log.info(dbType + sql);
			statement.setString(1, id);
			statement.executeUpdate();

		} catch (SQLException e) {
			throw e;
		} finally {
			PersistenceUtils.closeStatement(statement);
		}
	}

	/**
	 * 根据不同类型删除不同的表
	 */
	public void removeDocument(Document doc, int tableType) throws Exception {
		TableMapping tableMapping = getTableMapping(doc.getFormname());

		String tableName = tableMapping.getTableName(tableType);

		if (!StringUtil.isBlank(tableName)) {
			if (checkTable(tableName)) {
				PreparedStatement statement = null;
				try {
					Collection<Document> subdocs = queryByParentIDAndType(doc.getId(), tableType);
					if (subdocs != null && subdocs.size() > 0) {
						Iterator<Document> iter = subdocs.iterator();
						while (iter.hasNext()) {
							Document subdoc = iter.next();
							removeDocument(subdoc, tableType);
						}
					}

					String sql = "DELETE FROM " + getFullTableName(tableName);
					if (tableType == DQLASTUtil.TABEL_TYPE_LOG) {
						sql = sql + " WHERE DOC_ID = ?";
					}

//					log.info(dbType + sql);
					statement = connection.prepareStatement(sql);
					statement.setString(1, doc.getId());
					statement.executeUpdate();
				} catch (SQLException e) {
					throw e;
				} finally {
					PersistenceUtils.closeStatement(statement);
				}
			}
		}

	}

	/**
	 * 为预编译(PreparedStatement)Document的item. 根据item的值类型,设置相应的预编译参数.
	 * 
	 * @param ps
	 *            PreparedStatement
	 * @param doc
	 *            文档
	 * @param indx
	 *            参数次序
	 * @param fd
	 *            item
	 * @throws Exception
	 */
	private void prepItemData(PreparedStatement ps, Document doc, int indx, Item fd) throws Exception {

		try {

			String fieldType = fd.getType();

			if (fieldType.equals(Item.VALUE_TYPE_NUMBER)) { // Double
				Double value = fd.getNumbervalue();
				if (value == null || value.isNaN()) {
					ps.setDouble(indx, 0);
				} else {
					ps.setDouble(indx, value.doubleValue());
				}
				return;
			}

			if (fieldType.equals(Item.VALUE_TYPE_VARCHAR)) { // String
				String value = fd.getVarcharvalue();
				if (value == null)
					value = "";
				ps.setString(indx, value);
				return;
			}

			if (fieldType.equals(Item.VALUE_TYPE_DATE)) { // Date
				Date value = fd.getDatevalue();
				if (value == null) {
					ps.setNull(indx, Types.DATE);
				} else {
					ps.setTimestamp(indx, new Timestamp(value.getTime()));
				}

				return;
			}

			if (fieldType.equals(Item.VALUE_TYPE_TEXT)) { // TEXT
				String text = fd.getTextvalue();
				ps.setString(indx, text);
				return;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			System.err.println("table " + doc.getFormname() + " ERR FIELD NAME->" + fd.getName());
			throw new Exception("prepData Error!!!");
		}

	}

	/**
	 * 根据DQL,返回表单名
	 * 
	 * @param dql
	 * @return 表单名
	 */
	public String parseDQLFormName(String dql) {

		int pos = dql.toUpperCase().indexOf("$FORMNAME");
		dql = dql.substring(pos + 9);

		int pos2 = dql.toUpperCase().indexOf(" AND");
		String formName = "";
		if (pos2 > 0) {
			formName = dql.substring(1, pos2).trim();
		} else {
			formName = dql.substring(1);
		}

		// String formName = dql.trim().substring(1, pos2 - 1).trim();

		formName = formName.replaceAll("\\(", "");
		formName = formName.replaceAll("\\)", "");
		formName = formName.replaceAll("\\'", "");

		return getFormShortName(formName);
	}

	private String getFormShortName(String formName) {
		String rtn = formName;
		if (formName.indexOf("=") != -1) {
			rtn = rtn.substring(formName.indexOf("=") + 1).trim();
		}
		return rtn.substring((rtn.lastIndexOf("/") + 1), rtn.length()).trim();
	}

	/**
	 * 返回dql语句 条件. dql语句的详细说明参考以上文档头.
	 * 
	 * @param dql
	 * @return dql 条件
	 */
	public String parseDQLWhere(String dql) {
		int pos = dql.toUpperCase().indexOf("$FORMNAME");
		dql = dql.substring(pos + 9);

		int pos2 = dql.toUpperCase().indexOf(" AND");
		if (pos2 > 0) {
			return dql.substring(pos2 + 4).trim();
		} else {
			return "";
		}
		// return dql.trim().substring(pos2 + 3);
	}

	/**
	 * 根据DQL语句查询,返回此文档总行数. dql语句的详细说明参考以上文档头.
	 * 
	 * @param dql
	 *            DQL语句
	 * @return 文档总行数
	 * @throws Exception
	 */
	public long countByDQL(String dql, String domainid) throws Exception {
		String formName = parseDQLFormName(dql);

		TableMapping tableMapping = getTableMapping(formName);

		String where = DQLASTUtil.parseToHQL(parseDQLWhere(dql), tableMapping, sqlFuction);

		String sql = "SELECT COUNT(*)  FROM " + getInnerJoinPart(tableMapping);
		sql += " WHERE d.ISTMP=0";

		if (where != null && where.trim().length() > 0) {
			sql += " AND " + where;
		}
		if (!StringUtil.isBlank(domainid)) {
			sql += " AND d.DOMAINID='" + domainid + "'";
		}

		PreparedStatement statement = null;
		ResultSet rs = null;
//		log.info(dbType + sql);
		try {
			statement = connection.prepareStatement(sql);
			rs = statement.executeQuery();

			if (rs.next()) {
				return rs.getLong(1);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			PersistenceUtils.closeResultSet(rs);//Add By XGY 20130218
			PersistenceUtils.closeStatement(statement);
		}

		return 0;
	}

	/**
	 * SQL关联部分
	 * 
	 * @param tableMapping
	 *            关系映射
	 * @return
	 */
	String getInnerJoinPart(TableMapping tableMapping) {
		return getInnerJoinPart(tableMapping, DQLASTUtil.TABEL_TYPE_CONTENT);
	}

	/**
	 * 获取SQL关联部分
	 * 
	 * @param tableMapping
	 *            表关系映射
	 * @param tableType
	 *            表类型
	 * @return
	 */
	String getInnerJoinPart(TableMapping tableMapping, int tableType) {
		String sql = "";
		if (tableMapping.getFormType() == Form.FORM_TYPE_NORMAL) {
			sql += getFullTableName(tableMapping.getTableName(tableType)) + " d";
		} else {
			sql = getFullTableName(_TBNAME) + " d";
			sql += " INNER JOIN " + getFullTableName(tableMapping.getTableName(tableType)) + " m";
			sql += " ON d.MAPPINGID=m." + tableMapping.getPrimaryKeyName();
		}

		return sql;
	}

	String getSelectPart(TableMapping tableMapping) {
		return getSelectPart(tableMapping, DQLASTUtil.TABEL_TYPE_CONTENT);
	}

	/**
	 * 获取SQL查询内容部分
	 * 
	 * @param tableMapping
	 * @param tableType
	 * @return
	 */
	String getSelectPart(TableMapping tableMapping, int tableType) {
		String sql = "";
		if (tableMapping.getFormType() == Form.FORM_TYPE_NORMAL) {
			sql += "d.*";
		} else {
			sql += "d.*," + tableMapping.getColumnListString();
		}

		return sql;
	}

	public long countBySQL(String sql, String domainid) throws Exception {
		if (StringUtil.isBlank(sql))
			return 0;
		PreparedStatement statement = null;
		ResultSet rs = null;
		sql = "SELECT COUNT(*)  FROM (" + sql + ") doc WHERE doc.DOMAINID ='" + domainid + "'";
//		log.info(dbType + sql);

		try {
			statement = connection.prepareStatement(sql);
			rs = statement.executeQuery();

			if (rs.next()) {
				return rs.getLong(1);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			PersistenceUtils.closeResultSet(rs);//Add By XGY 20130218
			PersistenceUtils.closeStatement(statement);
		}

		return 0;
	}

	/**
	 * 根据DQL语句以及文档某字段名查询,返回此文档此字段总和. dql语句的详细说明参考以上文档头.
	 * 
	 * @param dql
	 *            dql语句
	 * @param fieldName
	 *            字段名
	 * @return 文档此字段总和
	 * @throws Exception
	 */
	public double sumByDQL(String dql, String fieldName, String domainid) throws Exception {
		String formName = parseDQLFormName(dql);

		TableMapping tableMapping = getTableMapping(formName);

		String where = DQLASTUtil.parseToHQL(parseDQLWhere(dql), tableMapping, sqlFuction);

		String sql = "SELECT SUM(" + tableMapping.getColumnName(fieldName) + ")";
		sql += " FROM " + getInnerJoinPart(tableMapping);
		sql += " WHERE d.ISTMP=0 AND d.DOMAINID='" + domainid + "'";

		if (!StringUtil.isBlank(where)) {
			sql += " AND " + where;
		}

		Statement statement = null;
		ResultSet rs = null;
//		log.info(dbType + sql);

		try {
			statement = connection.createStatement();
			rs = statement.executeQuery(sql);

			if (rs.next()) {
				return rs.getDouble(1);
			}
		} catch (SQLException e) {
			if (e.getErrorCode() == 409)
				throw new Exception(fieldName);
			else {
				throw e;
			}
		} finally {
			PersistenceUtils.closeStatement(statement);
		}

		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * OLink.bpm.core.dynaform.document.dao.sumBySQL#sumBySQL(java.lang.String)
	 */
	public double sumBySQL(String sql, String domainid) throws Exception {
		PreparedStatement statement = null;
		ResultSet rs = null;

		HibernateSQLUtils sqlUtil = new HibernateSQLUtils();
		sql = sqlUtil.appendCondition(sql, "DOMAINID ='" + domainid + "'");
//		log.info(dbType + sql);
		try {
			statement = connection.prepareStatement(sql);
			rs = statement.executeQuery();

			if (rs.next()) {
				return rs.getDouble(1);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			PersistenceUtils.closeResultSet(rs);//Add By XGY 20130218
			PersistenceUtils.closeStatement(statement);
		}

		return 0;
	}

	/**
	 * 根据符合DQL语句以及应用标识查询并分页,返回文档的DataPackage.
	 * 
	 * DataPackage为一个封装类，此类封装了所得到的文档数据并分页。 dql语句的详细说明参考以上文档头.
	 * 
	 * @see DataPackage#datas
	 * @see DataPackage#getPageNo()
	 * @see DataPackage#getLinesPerPage()
	 * @see DataPackage#getPageCount()
	 * @param dql
	 *            dql语句
	 * @param page
	 *            当前页码
	 * @param lines
	 *            每页显示行数
	 * @param application
	 *            应用标识
	 * @return 文档的DataPackage
	 * @throws Exceptio
	 */

	public DataPackage<Document> queryByDQLPage(String dql, int page, int lines, String domainid) throws Exception {
		return queryByDQLPage(dql, null, page, lines, domainid);
	}

	/**
	 * 根据DQL以及应用标识查询单个文档
	 * 
	 * @param dql
	 * @param application
	 *            应用标识
	 * @return 符合条件的单个文档
	 * @throws Exception
	 */
	public Document findByDQL(String dql, String domainid) throws Exception {
		Document doc = new Document();

		Collection<Document> collection = queryByDQLPage(dql, 1, 1, domainid).datas;

		if (collection != null && !collection.isEmpty()) {
			doc = (Document) collection.toArray()[0];
		}
		return doc;
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
	public Collection<Document> queryBySQL(String sql, int page, int lines, String domainid) throws Exception {
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			ArrayList<Document> datas = new ArrayList<Document>();

			// HibernateSQLUtils sqlUtil = new HibernateSQLUtils();
			// sql = sqlUtil.appendCondition(sql, "DOMAINID ='" + domainid +
			// "'");
			sql = buildLimitString(sql, page, lines);

//			log.info(dbType + sql);
			statement = connection.prepareStatement(sql);

			rs = statement.executeQuery();

			while (rs != null && rs.next()) {
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
	 */
	public abstract String buildLimitString(String sql, int page, int lines) throws Exception;

	public abstract void setBaseProperties(Document doc, ResultSet rs) throws Exception;

	/**
	 * 根据符合SQL执行语句以及域和应用标识查询单个文档。
	 * 
	 * @param sql
	 * @param domainid
	 * @param application
	 * @return 符合条件的单个文档
	 * @throws Exception
	 */
	public Document findBySQL(String sql, String domainid) throws Exception {
		Document doc = new Document();
		Collection<Document> collection = queryBySQLPage(sql, 1, 1, domainid).datas;

		if (collection != null && !collection.isEmpty()) {
			doc = (Document) collection.toArray()[0];
		}
		return doc;
	}

	/**
	 * 根据符合DQL语句以及应用标识查询,返回文档的DataPackage.
	 * 
	 * DataPackage为一个封装类，此类封装了所得到的文档数据并分页。 dql语句的详细说明参考以上文档头.
	 * 
	 * @see DataPackage#datas
	 * @see DataPackage#getPageNo()
	 * @see DataPackage#getLinesPerPage()
	 * @see DataPackage#getPageCount()
	 * 
	 * @param dql
	 *            DQL语句
	 * @param application
	 *            应用标识
	 * @retur 文档的DataPackage
	 * @throws Exception
	 */
	public DataPackage<Document> queryByDQL(String dql, String domainid) throws Exception {
		return queryByDQLPage(dql, 1, Integer.MAX_VALUE, domainid);
	}

	/**
	 * 根据符合DQL执行语句以及应用标识查询并分页,返回文档的数据集. dql语句的详细说明参考以上文档头.
	 * 
	 * @param dql
	 *            DQL语句
	 * @param pos
	 *            页码
	 * @param size
	 *            每页显示行数
	 * @param application
	 *            应用标识
	 * @return 文档的数据集
	 * @throws Exceptio
	 */
	public Iterator<Document> iteratorLimitByDQL(String dql, int pos, int size, String domainid) throws Exception {
		DataPackage<Document> dpgs = queryByDQLPage(dql, pos, size, domainid);
		if (dpgs != null && dpgs.datas != null) {
			return dpgs.datas.iterator();
		}
		return null;
	}

	/**
	 * 根据符合DQL语句,以及应用标识查询并分页,返回文档的集合. dql语句的详细说明参考以上文档头.
	 * 
	 * @see DataPackage#datas
	 * @param dql
	 *            dql语句
	 * @param pos
	 *            页码
	 * @param size
	 *            每页显示行数
	 * @param application
	 *            应用标识
	 * @return 文档的集合
	 */
	public Collection<Document> queryLimitByDQL(String dql, int pos, int size, String domainid) throws Exception {
		DataPackage<Document> dpgs = queryByDQLPage(dql, pos, size, domainid);
		if (dpgs != null && dpgs.datas != null) {
			return dpgs.datas;
		}
		return null;
	}

	/**
	 * 根据符合DQL执行语句,参数表以及应用标识查询,返回文档的DataPackage.
	 * 
	 * DataPackage为一个封装类，此类封装了所得到的文档数据并分页。 dql语句的详细说明参考以上文档头.
	 * 
	 * @see DataPackage#datas
	 * @see DataPackage#getPageNo()
	 * @see DataPackage#getLinesPerPage()
	 * @see DataPackage#getPageCount()
	 * @param dql
	 *            DQL语句
	 * @param params
	 *            参数表
	 * @param application
	 *            应用标识
	 * @return 文档DataPackage
	 * @see ParamsTable#params
	 * @throws Exception
	 */
	public DataPackage<Document> queryByDQL(String dql, ParamsTable params, String domainid) throws Exception {
		return queryByDQLPage(dql, params, 1, Integer.MAX_VALUE, domainid);
	}

	/**
	 * 根据符合DQL语句以及应用标识查询并分页,返回文档的DataPackage.
	 * 
	 * DataPackage为一个封装类，此类封装了所得到的文档数据并分页。 dql语句的详细说明参考以上文档头.
	 * 
	 * @see DataPackage#datas
	 * @see DataPackage#getPageNo()
	 * @see DataPackage#getLinesPerPage()
	 * @see DataPackage#getPageCount()
	 * @param dql
	 *            dql语句
	 * @param params
	 *            ParamsTable
	 * @see ParamsTable#params
	 * @param page
	 *            当前页码
	 * @param lines
	 *            每页显示行数
	 * @param application
	 *            应用标识
	 * @return 文档的DataPackage
	 * @throws Exceptio
	 */
	public DataPackage<Document> queryByDQLPage(String dql, ParamsTable params, int page, int lines, String domainid)
			throws Exception {
		String formName = parseDQLFormName(dql);

		TableMapping tableMapping = getTableMapping(formName);

		String dqlWhere = parseDQLWhere(dql);

		String where = "";
		if (dqlWhere != null && dqlWhere.trim().length() > 0) {
			where = DQLASTUtil.parseToHQL(dqlWhere, tableMapping, sqlFuction);
		}

		String sql = "SELECT " + getSelectPart(tableMapping) + " FROM " + getInnerJoinPart(tableMapping);
		sql += " WHERE d.ISTMP=0 AND d.DOMAINID='" + domainid + "'";

		if (!StringUtil.isBlank(where)) {
			sql += " AND " + where;
		}

		// 生成order by语句
		if (params != null) {
			sql = bulidOrderString(sql, params);
		}
		DataPackage<Document> dpg = new DataPackage<Document>();
		dpg.datas = queryBySQL(sql, page, lines, domainid);
		dpg.rowCount = (int) countByDQL(dql, domainid);
		dpg.linesPerPage = lines;
		dpg.pageNo = page;

		return dpg;
	}

	public ValueObject find(String id) throws Exception {
		return find(id, DQLASTUtil.TABEL_TYPE_CONTENT);
	}

	/**
	 * 获取Document基础信息
	 * 
	 * @return
	 * @throws Exception
	 */
	public Document findDocumentHead(String id) throws Exception {
		PreparedStatement statement = null;
		ResultSet rs = null;

		try {
			String sql = "";
			String tableName = getFullTableName(_TBNAME);
			sql = "SELECT doc.* FROM " + tableName + " doc WHERE doc.id=?";

			statement = connection.prepareStatement(sql);
			statement.setString(1, id);
			rs = statement.executeQuery();

			if (rs.next()) {
				Document doc = new Document();
				setBaseProperties(doc, rs);

				return doc;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeResultSet(rs);
			PersistenceUtils.closeStatement(statement);
		}

		return null;

	}

	/**
	 * 查找文档对象.
	 * 
	 * @return 文档对象
	 */

	public ValueObject find(String id, int tableType) throws Exception {

		PreparedStatement statement = null;
		ResultSet rs = null;

		try {
			Document head = findDocumentHead(id);

			if (head != null) {
				TableMapping tableMapping = getTableMapping(head.getFormname());

				String sql = "";
				sql += "SELECT " + getSelectPart(tableMapping, tableType) + " FROM " + getInnerJoinPart(tableMapping);

				if (tableType == DQLASTUtil.TABEL_TYPE_CONTENT) {
					sql += " WHERE d.ID=?";

				} else if (tableType == DQLASTUtil.TABEL_TYPE_LOG) {
					sql += " WHERE DOC_ID=?";
				}

//				log.info(dbType + sql);
				statement = connection.prepareStatement(sql);
				statement.setString(1, id);
				rs = statement.executeQuery();

				if (rs.next()) {
					Document doc = new Document();
					setBaseProperties(doc, rs);
					rs.close();
					return doc;
				}
			}

			return null;
		} catch (SQLException e) {
			throw e;
		} finally {
			PersistenceUtils.closeResultSet(rs);//Add By XGY 20130218
			PersistenceUtils.closeStatement(statement);
		}
	}

	public boolean isExist(String id) throws Exception {
		PreparedStatement statement = null;
		ResultSet rs = null;

		try {
			String sql = "SELECT count(*) FROM " + getFullTableName(_TBNAME) + " doc WHERE doc.id=?";

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
	 * create items.
	 * 
	 * @param rs
	 *            ResultSet对象
	 * @return collection of the item
	 * @throws SQLException
	 */
	protected Set<Item> createItems(ResultSet rs, Document doc) throws Exception {
		HashSet<Item> items = new HashSet<Item>();
		ResultSetMetaData metaData = rs.getMetaData();
		TableMapping tableMapping = null;
		if (doc.getForm() != null) {
			tableMapping = doc.getForm().getTableMapping();
		}

		for (int i = 1; i <= metaData.getColumnCount(); i++) {
			items.add(createItem(rs, i, tableMapping));
		}

		return items;
	}

	/**
	 * 创建文档项
	 * 
	 * @param rs
	 * @param index
	 * @param tableMapping
	 * @return
	 * @throws Exception
	 */
	protected Item createItem(ResultSet rs, int index, TableMapping tableMapping) throws Exception {
		ResultSetMetaData metaData = rs.getMetaData();
		if (tableMapping != null) {
			String columnName = metaData.getColumnName(index);
			String fieldName = tableMapping.getFieldName(columnName);

			Item item = new Item();

			if (StringUtil.isBlank(fieldName)) {
				if (!StringUtil.isBlank(columnName) && columnName.toUpperCase().startsWith(ITEM_FIELD_PREFIX)) {
					columnName = columnName.substring(ITEM_FIELD_PREFIX.length());
					fieldName = columnName;
				}
			}

			item.setName(fieldName);
			setItemValue(rs, index, item);

			return item;
		} else {
			Item item = new Item();
			String columnName = metaData.getColumnName(index);
			String fieldName = "";

			if (!StringUtil.isBlank(columnName) && columnName.toUpperCase().startsWith(ITEM_FIELD_PREFIX)) {
				columnName = columnName.substring(ITEM_FIELD_PREFIX.length());
				fieldName = columnName;
			}

			item.setName(fieldName);
			setItemValue(rs, index, item);

			return item;
		}
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

	public long getNeedExportDocumentTotal(String dql, Date date, String domainid) throws Exception {
		if ((dql != null && dql.trim().length() > 0)) {

			String formName = parseDQLFormName(dql);
			TableMapping tableMapping = getTableMapping(formName);

			String where = DQLASTUtil.parseToHQL(parseDQLWhere(dql), tableMapping, sqlFuction);

			String sql = "SELECT COUNT(*) FROM " + getInnerJoinPart(tableMapping);
			sql += " WHERE d.ISTMP=0 AND d.DOMAINID='" + domainid + "'";
			if (where != null && where.trim().length() > 0) {
				sql += " AND " + where;
			}

			// 增量的情况下加入最后修改日期条件
			if (date != null) {
				sql += " AND LASTMODIFIED > ?";
			}

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

				if (rs.next()) {
					return rs.getLong(1);
				}
			} catch (SQLException e) {
				throw e;
			} finally {
				PersistenceUtils.closeResultSet(rs);//Add By XGY 20130218
				PersistenceUtils.closeStatement(statement);
			}
		}
		return 0;
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
	public Iterator<Document> queryByDQLAndDocumentLastModifyDate(String dql, Date date, int page, int lines,
			String domainid) throws Exception {
		if ((dql != null && dql.trim().length() > 0)) {

			String formName = parseDQLFormName(dql);

			TableMapping tableMapping = getTableMapping(formName);

			String where = DQLASTUtil.parseToHQL(parseDQLWhere(dql), tableMapping, sqlFuction);

			String sql = "SELECT " + getSelectPart(tableMapping) + " FROM " + getInnerJoinPart(tableMapping);
			sql += " WHERE d.ISTMP=0 AND d.DOMAINID='" + domainid + "'";
			if (where != null && where.trim().length() > 0) {
				sql += " AND " + where;
			}

			// 增量的情况下加入最后修改日期条件
			if (date != null) {
				sql += " AND LASTMODIFIED > ?";
			}

			// 查询指定的页和行
			// sql = buildLimitString(sql, page, lines);

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
				// JDBC1.0
				for (int i = 0; i < (page - 1) * lines && rs.next(); i++) {
					// keep empty
				}

				for (int i = 0; i < lines && rs.next(); i++) {
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

		if (params != null) {
			sortCols = (String[]) params.getParameter("_sortCol");
			sortStatus = (String) params.getParameter("_sortStatus");
			orderby = params.getParameterAsString("_orderby");
		}

		buffer.append("SELECT * FROM (" + sql + ") table_orderby");
		if (sortCols != null && sortCols.length > 0) {
			buffer.append(" ORDER BY ");
			buffer.append(this.getOrderFieldsToSqlString(sortCols, "table_orderby"));
			buffer.append(", table_orderby.ID");
		} else if (orderby != null && orderby.trim().length() > 0) {
			buffer.append(" ORDER BY table_orderby." + orderby);
			// 增加排序方式, ASC、DESC
			buffer.append(StringUtil.isBlank(sortStatus) ? "" : " " + sortStatus);
			buffer.append(", table_orderby.ID");
		} else if (-1 == sql.toUpperCase().indexOf("ORDER BY")) {
			buffer.append(" ORDER BY table_orderby.ID");
		}
		return buffer.toString();
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
						sortCol += tName + "." + sortCols[i];
					} else {
						sortCol += tName + "." + sortCols[i] + ",";
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

	public DataPackage<Document> queryBySQLPage(String sql, ParamsTable params, int page, int lines, String domainid)
			throws Exception {

		String sqlWithOutOrderBy = sql;

		// 生成order by语句
		if (params != null) {
			sql = bulidOrderString(sql, params);

			if (sql.lastIndexOf(" ORDER BY ") >= 0) {
				sql = sql.substring(0, sql.lastIndexOf(" ORDER BY ")) + " WHERE DOMAINID ='" + domainid + "'"
						+ sql.substring(sql.lastIndexOf(" ORDER BY ", sql.length()));
			}

		} else // 不存在order by也需要增中domainid
		{
			sql = " SELECT * FROM (" + sql + ") table_0 WHERE table_0.DOMAINID ='" + domainid + "'";
		}

		DataPackage<Document> dpg = new DataPackage<Document>();
		dpg.datas = queryBySQL(sql, page, lines, domainid);

		dpg.rowCount = (int) countBySQL(sqlWithOutOrderBy, domainid);
		dpg.linesPerPage = lines;
		dpg.pageNo = page;

		return dpg;
	}

	public DataPackage<Document> queryBySQLPage(String sql, int page, int lines, String domainid) throws Exception {
		return queryBySQLPage(sql, null, page, lines, domainid);
	}

	public DataPackage<Document> queryBySQL(String sql, ParamsTable params, String domainid) throws Exception {
		return queryBySQLPage(sql, params, 1, Integer.MAX_VALUE, domainid);
	}

	public DataPackage<Document> queryBySQL(String sql, String domainid) throws Exception {
		return queryBySQLPage(sql, 1, Integer.MAX_VALUE, domainid);
	}

	public int findVersions(String id) throws Exception {
		PreparedStatement statement = null;
		ResultSet rs = null;

		int rtn = 0;
		try {
			String sql = "SELECT doc.VERSIONS FROM " + getFullTableName(_TBNAME) + " doc";
			sql += " WHERE doc.ID=?";

//			log.info(dbType + sql);

			statement = connection.prepareStatement(sql);
			statement.setString(1, id);
			rs = statement.executeQuery();

			if (rs.next()) {
				rtn = rs.getInt("VERSIONS");
				rs.close();
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			PersistenceUtils.closeResultSet(rs);//Add By XGY 20130218
			PersistenceUtils.closeStatement(statement);
		}

		return rtn;
	}

	public String findFormName(String id) throws SQLException, DAOException {
		String sql = "SELECT FORMNAME FROM " + getFullTableName(_TBNAME) + " doc WHERE doc.ID=?";
		String formName = null;
		PreparedStatement statement = null;
		ResultSet rs=null;
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, id);
			rs = statement.executeQuery();

			if (rs.next()) {
				formName = rs.getString("FORMNAME");
				rs.close();
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			PersistenceUtils.closeResultSet(rs);//Add By XGY 20130218
			PersistenceUtils.closeStatement(statement);
		}

		return formName;
	}

	public String getFullTableName(String tblname) {
		if (this.schema != null && !this.schema.trim().equals("")) {
			return this.schema.trim().toUpperCase() + "." + tblname.trim().toUpperCase();
		}
		return tblname.trim().toUpperCase();
	}

	/**
	 * 查找Document Log
	 */
	public Collection<Document> queryModifiedDocuments(Document doc) throws Exception {
		PreparedStatement statement = null;
		ResultSet rs = null;

		try {
			ArrayList<Document> datas = new ArrayList<Document>();
			if (doc != null) {
				TableMapping tableMapping = getTableMapping(doc.getFormname());
				String tableName = tableMapping.getTableName(DQLASTUtil.TABEL_TYPE_LOG);

				if (checkTable(tableName)) {
					String sql = "SELECT d.*," + tableMapping.getColumnListString();
					sql += " FROM " + getInnerJoinPart(tableMapping, DQLASTUtil.TABEL_TYPE_LOG);
					sql += " WHERE DOC_ID=? ORDER BY d.LASTMODIFIED";

//					log.info(dbType + sql);
					statement = connection.prepareStatement(sql);
					statement.setString(1, doc.getId());
					rs = statement.executeQuery();
				}
			}

			while (rs != null && rs.next()) {
				Document oldDoc = new Document();
				setBaseProperties(oldDoc, rs);
				datas.add(oldDoc);
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
	 * 查找数据库表是否存在
	 */
	public boolean checkTable(String tableName) throws Exception {
		if (!PersistenceUtils.getTableStateMap().containsKey(tableName)) {
			DatabaseMetaData metaData = connection.getMetaData();
			boolean exits = false;
			ResultSet tablers = null;
			try {
				tablers = metaData.getTables(null, null, tableName, null);
				if (tablers.next()) {
					exits = true;
				}
				PersistenceUtils.getTableStateMap().put(tableName, exits);
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				PersistenceUtils.closeResultSet(tablers);
			}
		}

		if (PersistenceUtils.getTableStateMap().get(tableName) != null) {
			return ((Boolean) PersistenceUtils.getTableStateMap().get(tableName)).booleanValue();
		}

		return false;
	}

	public void create(ValueObject vo) throws Exception {
		storeDocument((Document) vo);
	}

	public void remove(String pk) throws Exception {
		Document doc = (Document) find(pk);
		removeDocument(doc);
	}

	public void update(ValueObject vo) throws Exception {
		storeDocument((Document) vo);
	}

	/**
	 * 
	 */
	public Collection<Document> queryByParentIDAndType(String parentid, int tableType) throws Exception {
		String sql = "SELECT doc.FORMNAME FROM " + getFullTableName(_TBNAME) + " doc";
		sql += " WHERE doc.PARENT = ? AND doc.ISTMP = 0 GROUP BY FORMNAME";

		Collection<Document> rtn = new ArrayList<Document>();
		PreparedStatement statement = null;
		ResultSet rs=null;
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, parentid);
			rs = statement.executeQuery();
			while (rs.next()) {
				String formname = rs.getString("FORMNAME");
				Collection<Document> childs = queryByParentIDAndType(parentid, formname, tableType);
				if (childs != null) {
					rtn.addAll(childs);
				}
			}
			return rtn;
		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeResultSet(rs);//Add By XGY 20130218
			PersistenceUtils.closeStatement(statement);
		}
	}

	/**
	 * 
	 */

	public Collection<Document> queryByParentIDAndType(String parentid, String formName, int tableType)
			throws Exception {
		ArrayList<Document> docs = new ArrayList<Document>();
		TableMapping tableMapping = getTableMapping(formName);

		String tableName = tableMapping.getTableName(tableType);

		if (!StringUtil.isBlank(tableName)) {
			if (checkTable(tableName)) {
				String sql = "";
				if (tableType == DQLASTUtil.TABEL_TYPE_LOG) {
					sql += "SELECT d.*," + tableMapping.getColumnListString();
					sql += " FROM" + getFullTableName(_TBNAME) + " d";
					sql += " INNER JOIN " + getFullTableName(tableMapping.getTableName(tableType)) + " m";
					sql += " ON d.ID=DOC_ID";

				} else {
					sql = sql + "SELECT d.*," + tableMapping.getColumnListString() + " FROM "
							+ getInnerJoinPart(tableMapping, tableType);
				}

				sql += " WHERE d.PARENT = ? AND d.ISTMP = 0";

				PreparedStatement statement = null;
				ResultSet rs = null;
				try {

					statement = connection.prepareStatement(sql);
					statement.setString(1, parentid);

					rs = statement.executeQuery();

					while (rs != null && rs.next()) {
						Document doc = new Document();
						setBaseProperties(doc, rs);
						docs.add(doc);
					}

				} catch (SQLException e) {
					throw e;
				} finally {
					PersistenceUtils.closeResultSet(rs);//Add By XGY 20130218
					PersistenceUtils.closeStatement(statement);
				}

			}
		}

		return docs;
	}

	/**
	 * 根据doc删除Auth表记录
	 * 
	 * @param doc
	 *            Document对象
	 * @throws Exception
	 * @throws Exception
	 */
	public void removeAuthByDoc(Document doc) throws Exception {
		TableMapping tableMapping = getTableMapping(doc.getFormShortName());
		String tbName = tableMapping.getTableName(DQLASTUtil.TABLE_TYPE_AUTH);
		String sql0 = "DELETE FROM " + tbName + " WHERE DOC_ID='" + doc.getId() + "'";
//		log.info(dbType + sql0);
		Statement stmt0 = null;
		try {
			stmt0 = connection.createStatement();
			stmt0.execute(sql0);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			stmt0.close();
		}

	}

	/**
	 * 根据条件插入AUTH表记录
	 * 
	 * @param formName
	 *            表单名字
	 * @param docId
	 *            文档ID
	 * @param condition
	 *            要插入的值
	 */
	public void createAuthDocWithCondition(String formName, String docId, Collection<?> condition) throws Exception {
		Statement stmt0=null;
		PreparedStatement stmt2 = null;
        try
        {
    		TableMapping tableMapping = getTableMapping(formName);
			String tbName = tableMapping.getTableName(DQLASTUtil.TABLE_TYPE_AUTH);
	
			String sql0 = "DELETE FROM " + tbName + " WHERE DOC_ID='" + docId + "'";
//			log.info(dbType + sql0);
			stmt0 = connection.createStatement();
			stmt0.execute(sql0);
	
			String sql = "INSERT INTO " + tbName + "(ID, DOC_ID, VALUE) VALUES(?,?,?)";
			stmt2 = connection.prepareStatement(sql);
			for (Iterator<?> iterator = condition.iterator(); iterator.hasNext();) 
			{
				Object obj = iterator.next();
				stmt2.setString(1, Tools.getSequence());
				stmt2.setString(2, docId);
				stmt2.setObject(3, obj);
				stmt2.addBatch();
			}
			stmt2.executeBatch();
        }
        catch(Exception e)
        {
			throw e;
        }
        finally 
        {
			PersistenceUtils.closeStatement(stmt2);
			PersistenceUtils.closeStatement(stmt0);
        }
	}

	/**
	 * 查询符合条件的work信息
	 * 
	 * @param sql
	 * @param params
	 * @param page
	 * @param lines
	 * @param domainid
	 * @return
	 * @throws Exception
	 */
	public DataPackage<WorkVO> queryWorkBySQLPage(ParamsTable params, int page, int lines, WebUser user)
			throws Exception {

		String _processType = params.getParameterAsString("_processType");// 办理类型
		// 1.办理中
		// “processing”
		// 2.已办理“processed”
		String _actorId = params.getParameterAsString("_actorId");
		String _flowId = params.getParameterAsString("_flowId");
		String _subject = params.getParameterAsString("_subject");

		if (_actorId.indexOf(",") > 0)
			_actorId = _actorId.split(",")[0];
		if (_actorId == null || _actorId.trim().length() <= 0)
			_actorId = user.getId();

		String sql = "";

		if ("processing".equals(_processType)) {
			sql = getProcessingSQL(_actorId);
			sql += getWorkSQLCondition(_flowId, _subject);
			//add by zb 2013-6-26
			sql += " order by doc.lastmodified desc";
//			sql += " order by doc.formid,fs.flowid";
		} else if ("processed".equals(_processType)) {
			sql = getProcessedSQL(_actorId);
			sql += getWorkSQLCondition(_flowId, _subject);
			//add by zb 2013-6-26
			sql += " order by doc.lastmodified desc";
//			sql += " order by doc.formid,fs.flowid";
		} else if (_processType == null || "all".equals(_processType)) {
			sql = "SELECT DISTINCT * FROM (";
			sql += getProcessingSQL(_actorId);
			sql += getWorkSQLCondition(_flowId, _subject);
			sql += " UNION ";
			sql += getProcessedSQL(_actorId);
			sql += getWorkSQLCondition(_flowId, _subject);
			sql += ") tab ";
			//add by zb 2013-6-26
			sql += " order by tab.lastmodified desc";
//			sql += " order by tab.formid,tab.flowid";
		}
		

		return queryWorkBySQLPage(sql, page, lines, user);
	}
	
	protected String getWorkSQLCondition(String _flowId, String _subject){
		String sql = "";
		if (_flowId != null && _flowId.trim().length() > 0) {
			sql += " and fs.flowid='" + _flowId + "'";
		}
		if (_subject != null && _subject.trim().length() > 0) {
			sql += " and pen.summary like '%" + _subject + "%'";
		}
		
		return sql;
	}

	/**
	 * 获取处理中SQL语句
	 * 
	 * @param actorId
	 * @return
	 */
	protected String getProcessingSQL(String actorId) {
		//update by zb 2013-6-26
		String processingSQL = "select distinct doc.id DOCID,doc.formid FORMID,fs.flowid FLOWID,pen.flowname FLOWNAME,doc.statelabel STATELABEL,pen.summary SUBJECT,doc.AUDITORNAMES,doc.AUDITORLIST,doc.APPLICATIONID,doc.DOMAINID,doc.lastmodified "
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

	/**
	 * 获取已处理SQL语句
	 * 
	 * @param actorId
	 * @return
	 */
	protected String getProcessedSQL(String actorId) {
		//update by zb 2013-6-26
		String processedSQL = "select distinct doc.id DOCID,doc.formid FORMID,fs.flowid FLOWID,rhis.flowname FLOWNAME,doc.statelabel STATELABEL,pen.summary SUBJECT,doc.AUDITORNAMES,doc.AUDITORLIST,doc.APPLICATIONID,doc.DOMAINID,doc.lastmodified "
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

	/**
	 * 查询符合条件的work信息
	 * 
	 * @param sql
	 * @param page
	 * @param lines
	 * @param domainid
	 * @return
	 * @throws Exception
	 */
	public DataPackage<WorkVO> queryWorkBySQLPage(String sql, int page, int lines, WebUser user) throws Exception {
		String countSql = sql;
		sql = " SELECT * FROM (" + sql + ") table_0 WHERE table_0.DOMAINID ='" + user.getDomainid() + "'";

		DataPackage<WorkVO> dpg = new DataPackage<WorkVO>();
		dpg.datas = queryWorkBySQL(sql, page, lines);

		dpg.rowCount = (int) countWorkBySQL(countSql);
		dpg.linesPerPage = lines;
		dpg.pageNo = page;

		return dpg;

	}

	/**
	 * 查询符合条件的work信息
	 * 
	 * @param sql
	 * @param page
	 * @param lines
	 * @return
	 * @throws Exception
	 */
	public Collection<WorkVO> queryWorkBySQL(String sql, int page, int lines) throws Exception {
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			ArrayList<WorkVO> datas = new ArrayList<WorkVO>();

			sql = buildLimitString(sql, page, lines);

//			log.info(dbType + sql);
			statement = connection.prepareStatement(sql);

			rs = statement.executeQuery();

			while (rs != null && rs.next()) {
				WorkVO work = new WorkVO();
				setWorkProperties(work, rs);
				datas.add(work);
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
	 * 查询符合条件的Work数目
	 * 
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public long countWorkBySQL(String sql) throws Exception {
		if (StringUtil.isBlank(sql))
			return 0;
		PreparedStatement statement = null;
		ResultSet rs = null;
		sql = "SELECT COUNT(*)  FROM (" + sql + ") table_0";
//		log.info(dbType + sql);

		try {
			statement = connection.prepareStatement(sql);
			rs = statement.executeQuery();

			if (rs.next()) {
				return rs.getLong(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			PersistenceUtils.closeResultSet(rs);//Add By XGY 20130218
			PersistenceUtils.closeStatement(statement);
		}

		return 0;
	}

	public void setWorkProperties(WorkVO work, ResultSet rs) {
		try {
			work.setApplicationId(rs.getString("APPLICATIONID"));
			work.setDocId(rs.getString("DOCID"));
			work.setFlowId(rs.getString("FLOWID"));
			work.setFlowName(rs.getString("FLOWNAME"));
			work.setFormId(rs.getString("FORMID"));
			work.setStateLabel(rs.getString("STATELABEL"));
			work.setAuditorNames(rs.getString("AUDITORNAMES"));
			work.setAuditorList(rs.getString("AUDITORLIST"));
			work.setSubject(rs.getString("SUBJECT"));
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 通过存储过程查询
	 * 
	 * 2.6版本新增存储过程调用方法
	 * 
	 * @param procedure
	 * @param params
	 * @param page
	 * @param lines
	 * @param domainid
	 * @return
	 * @throws Exception
	 */
	public DataPackage<Document> queryByProcedure(final String procedure, final ParamsTable params, int page,
			int lines, String domainid) throws Exception {
		CallableStatement statement = null;
		CallableStatement statement1 = null;
		ResultSet rs = null;
		try {
			ArrayList<Document> datas = new ArrayList<Document>();

			// 解析procedure字符串,解析成{call procedureName(?,?,?)}形式
			String proce = parseProcedure(procedure);
			// 预编译存储过程proce
			statement = connection.prepareCall(proce);
			statement1 = connection.prepareCall(proce);
			// 输出日志
//			log.info(dbType + proce);
			// 解析参数
			if (proce.indexOf('(') > -1 && proce.indexOf(')') > -1) {
				parseParameters(statement, procedure, params, page, lines, domainid);
				parseParameters(statement1, procedure, params, 1, Integer.MAX_VALUE, domainid);
			}
			// 运行该存储过程
			boolean re = statement.execute();
			boolean re1 = statement1.execute();
			// 处理返回结果
			if (re) {
				rs = statement.getResultSet();
				while (rs != null && rs.next()) {
					Document doc = new Document();
					setBaseProperties(doc, rs);
					datas.add(doc);
				}
			}
			DataPackage<Document> dpg = new DataPackage<Document>();
			sort(datas, params);

			ArrayList<Document> datasNeed = new ArrayList<Document>();
			if (!procedure.contains("#lines")) {
				for (int i = 0; i < lines; i++) {
					int index = i + (page - 1) * lines;
					if (index < datas.size())
						datasNeed.add(datas.get(index));
					else
						break;
				}
			}
			dpg.datas = datasNeed;
			if (re1 && statement1.getResultSet().last()) {
				dpg.rowCount = statement1.getResultSet().getRow();
			}
			dpg.linesPerPage = lines;
			dpg.pageNo = page;
			return dpg;

		} catch (SQLException e) {
			throw e;
		} finally {
			PersistenceUtils.closeStatement(statement);
			PersistenceUtils.closeStatement(statement1);
		}
	}

	/**
	 * 排序结果集(如果用户的存储过程没有使用order by)
	 * 
	 * 2.6版本新增的方法
	 * 
	 * @param datas
	 * @param params
	 */
	private void sort(List<Document> datas, final ParamsTable params) {
		Collections.sort(datas, new Comparator<Document>() {
			public int compare(Document d1, Document d2) {
				Object value1 = null;
				Object value2 = null;
				String sortCol = params.getParameterAsString("_sortCol");
				String orderBy = params.getParameterAsString("_orderby");
				String orderStatus = params.getParameterAsString("_sortStatus");
				if (!StringUtil.isBlank(orderBy)) {
					Item i1 = d1.findItem(orderBy.substring(5));
					Item i2 = d2.findItem(orderBy.substring(5));
					if (i1 != null && i2 != null) {
						value1 = i1.getValue();
						value2 = i2.getValue();
					} else if (i1 == null && i2 == null) {
						value1 = d1.getValueByProName(orderBy);
						value2 = d2.getValueByProName(orderBy);
					}
				} else if (!StringUtil.isBlank(sortCol)) {
					Item i1 = d1.findItem(sortCol.substring(5));
					Item i2 = d2.findItem(sortCol.substring(5));
					if (i1 != null && i2 != null) {
						value1 = i1.getValue();
						value2 = i2.getValue();
					} else if (i1 == null && i2 == null) {
						value1 = d1.getValueByProName(sortCol);
						value2 = d2.getValueByProName(sortCol);
					}
				}
				if (value1 instanceof Number && value2 instanceof Number) {
					double n1 = ((Number) value1).doubleValue();
					double n2 = ((Number) value2).doubleValue();
					if (n1 < n2)
						return "desc".equalsIgnoreCase(orderStatus) ? 1 : -1;
					if (n1 > n2)
						return "desc".equalsIgnoreCase(orderStatus) ? -1 : 1;

				} else if (value1 instanceof Date && value2 instanceof Date) {
					Date da1 = (Date) value1;
					Date da2 = (Date) value2;
					if (da1.compareTo(da2) < 0)
						return "desc".equalsIgnoreCase(orderStatus) ? 1 : -1;
					if (da1.compareTo(da2) > 0)
						return "desc".equalsIgnoreCase(orderStatus) ? -1 : 1;
				} else if (value1 instanceof String && value2 instanceof String) {
					String s1 = (String) value1;
					String s2 = (String) value2;
					if (s1.compareTo(s2) < 0)
						return "desc".equalsIgnoreCase(orderStatus) ? 1 : -1;
					if (s1.compareTo(s2) > 0)
						return "desc".equalsIgnoreCase(orderStatus) ? -1 : 1;
				}
				return 0;
			}
		});
	}

	/**
	 * 解析存储过程脚本的参数
	 * 
	 * 2.6版本新增的方法
	 * 
	 * @param statement
	 * @param procedure
	 * @param params
	 * @param page
	 * @param lines
	 * @param domainid
	 * @throws Exception
	 */
	protected void parseParameters(CallableStatement statement, String procedure, ParamsTable params, int page,
			int lines, String domainid) throws Exception {
		int index1 = procedure.indexOf("(");
		int index2 = procedure.lastIndexOf(")");
		String parameters = procedure.substring(index1 + 1, index2);
		String[] paramsAsArray = parameters.split(",");
		if (paramsAsArray != null && paramsAsArray.length > 0) {
			for (int i = 0; i < paramsAsArray.length; i++) {
				String[] p = paramsAsArray[i].split(":");
				if (p != null && p.length == 2) {
					String type = p[0].trim();
					String value = p[1].trim();
					if ("String".equals(type)) {
						// #curDomain,#sortCol,#sortStatus,#orderby为系统自带变量,用户定义时不能与此相冲突
						if ("#curDomain".equals(value)) {
							statement.setString(i + 1, domainid);
						} else if ("#sortCol".equals(value)) {
							statement.setString(i + 1, params.getParameterAsString("_sortCol"));
						} else if ("#sortStatus".equals(value)) {
							statement.setString(i + 1, params.getParameterAsString("_sortStatus"));
						} else if ("#orderby".equals(value)) {
							statement.setString(i + 1, params.getParameterAsString("_orderby"));
						} else {
							statement.setString(i + 1, value);
						}
					} else if ("int".equals(type)) {
						// #curPage为系统自带变量,用户定义时不能与此相冲突
						if ("#curPage".equals(value)) {
							statement.setInt(i + 1, page);
						} else if ("#lines".equals(value)) {// #lines为系统自带变量,用户定义时不能与此相冲突
							statement.setInt(i + 1, lines);
						} else {
							statement.setInt(i + 1, Integer.valueOf(value));
						}
					} else if ("Date".equals(type) || "java.sql.Date".equals(type)) {
						statement.setDate(i + 1, java.sql.Date.valueOf(value));
					} else if ("Time".equals(type) || "java.sql.Time".equals(type)) {
						statement.setTime(i + 1, java.sql.Time.valueOf(value));
					} else if ("Timestamp".equals(type) || "java.sql.Timestamp".equals(type)) {
						statement.setTimestamp(i + 1, Timestamp.valueOf(value));
					} else if ("double".equals(type)) {
						statement.setDouble(i + 1, Double.valueOf(value));
					} else if ("float".equals(type)) {
						statement.setFloat(i + 1, Float.valueOf(value));
					} else if ("long".equals(type)) {
						statement.setLong(i + 1, Long.valueOf(value));
					} else {
						throw new Exception("not support type [" + type + "]");
					}
				}
			}
		}
	}

	/**
	 * 解析iscript的存储过程脚本
	 * 
	 * 2.6新增的方法
	 * 
	 * @param procedure
	 * @return {call procedureName(?,?)}形式的表达字符串
	 */
	protected String parseProcedure(String procedure) throws Exception {
		StringBuffer p = new StringBuffer();
		if (!procedure.trim().startsWith("{")) {
			if (!procedure.trim().startsWith("call "))
				throw new Exception("missing 'call' at the beginning of [" + procedure + "]");
			p.append('{');
		} else if (!procedure.trim().startsWith("call", 1)) {
			throw new Exception("missing 'call' at the beginning of [" + procedure + "]");
		}
		int index1 = procedure.indexOf('(');
		int index2 = procedure.lastIndexOf(')');
		if (index1 < 0 && index2 > -1) {
			throw new Exception("missing '(' on [" + procedure + "]");
		} else if (index1 > -1 && index2 < 0) {
			throw new Exception("missing ')' on [" + procedure + "]");
		} else if (index1 > -1 && index2 > -1) {
			p.append(procedure.substring(procedure.indexOf('{') + 1, index1).trim());
			p.append('(');
			String[] parametersAsArray = procedure.substring(index1 + 1, index2).split(",");
			for (int i = 0; i < parametersAsArray.length; i++) {
				p.append('?');
				if (i < parametersAsArray.length - 1)
					p.append(',');
			}
			p.append(')');
		} else {
			p.append(procedure.substring(procedure.indexOf('{') + 1).trim());
		}
		if (!procedure.trim().endsWith("}")) {
			p.append('}');
		}

		return p.toString();
	}

}
