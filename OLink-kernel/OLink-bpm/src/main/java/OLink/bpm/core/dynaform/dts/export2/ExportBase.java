package OLink.bpm.core.dynaform.dts.export2;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.document.ejb.DocumentProcess;
import OLink.bpm.core.dynaform.document.ejb.Item;
import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSource;
import OLink.bpm.core.dynaform.dts.exp.Export_Erro_type;
import OLink.bpm.core.dynaform.dts.exp.columnmapping.ejb.ColumnMapping;
import OLink.bpm.core.dynaform.dts.exp.mappingconfig.ejb.MappingConfig;
import OLink.bpm.core.dynaform.dts.exp.mappingconfig.ejb.MappingConfigProcess;
import OLink.bpm.core.dynaform.dts.export2.sql.BaseSql;
import OLink.bpm.core.dynaform.dts.export2.sql.SqlFactory;
import OLink.bpm.core.dynaform.form.ejb.ValidateMessage;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.macro.runner.JavaScriptFactory;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.DateUtil;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.property.DefaultProperty;
import org.apache.log4j.Logger;

import eWAP.core.Tools;

import com.opensymphony.xwork.ActionSupport;
import com.opensymphony.xwork.Validateable;

/**
 * @author nicholas
 */
public abstract class ExportBase extends ActionSupport implements Validateable {
	
	private static final long serialVersionUID = 7297917288559831035L;

	static Logger log = Logger.getLogger(ExportBase.class);

	private static final String defaultPath = "C:/DoucemntExportLog/";

	public static final String fileFullName;

	Connection conn = null;

	Statement stmt = null;

	PreparedStatement pstmt = null;

	StringBuffer errorInfo = new StringBuffer();// 记录出错信息

	DataSource dataSource = null;

	DatabaseMetaData dma = null;

	MappingConfig mappingconfig = null;

	String tableName = null;

	String DatabaseVersion = null;

	String application = null;

	String domainid = null;

	int fetchSize = 600;

	WebUser user = null;

	protected abstract long getTotalLine(MappingConfig mfg, String dql,
			DocumentProcess process) throws Exception;

	protected abstract Iterator<Document> getAppointedRows(MappingConfig mfg, int page,
														   int lines, String dql, DocumentProcess process) throws Exception;

	public ExportBase(MappingConfig mappingconfig, String application,
			WebUser user) {
		this.setMappingconfig(mappingconfig);
		this.application = application;
		this.domainid = user.getDomainid();
		this.user = user;
	}

	public String exportDocument() throws Exception {

		initalVariable();
		Collection<ColumnMapping> a_colmaps = new HashSet<ColumnMapping>();// columnmappings
		Map<String, String> DBCols = new HashMap<String, String>(); // 数据库中存在的字段
		Collection<ColumnMapping> nedAdCols = new ArrayList<ColumnMapping>(); // 需要新增的段
		Collection<ColumnMapping> compareColums = null;

		String sqlInsert = null;
		String dql = mappingconfig.getValuescript();
		BaseSql sqlBuilder = SqlFactory.createDb(dataSource.getDbType());

		//Session session = PersistenceUtils.currentSession();
		// DocumentProcess process = (DocumentProcess)
		// ProcessFactory.createProcess(DocumentProcess.class);

		DocumentProcess process = (DocumentProcess) ProcessFactory.createRuntimeProcess(DocumentProcess.class,this.application);
		a_colmaps.addAll(mappingconfig.getColumnMappings());
		createBasicColumn(a_colmaps);

		try {

			// 查找数据库中同名表中有哪些字段，并放入到DBExistColumns map 中
			ResultSet rs = dma.getTables(null, dataSource.getUsername()
					.toUpperCase(), tableName.toUpperCase(), null);
			if (!rs.next()) {

				String sql = sqlBuilder.createTable(tableName, a_colmaps);
				stmt.executeUpdate(sql);
			} else {

				alterTable(dataSource, tableName, stmt, DBCols, nedAdCols,
						a_colmaps, sqlBuilder);
			}

			/**
			 * 和目标数据库比较后的结果 MappconfigConfig.columns与目标数据库存在的字段类型不同时,以数据库字段为准
			 */
			if (DBCols != null && DBCols.size() > 0) {
				compareColums = compareDateType(DBCols, a_colmaps, sqlBuilder);
				compareColums.addAll(nedAdCols);
			}

			if (compareColums != null)
				sqlInsert = BaseSql.creatInsertSQL(tableName, compareColums);
			else
				sqlInsert = BaseSql.creatInsertSQL(tableName, a_colmaps);

			pstmt = conn.prepareStatement(sqlInsert);

			long totalLine = getTotalLine(mappingconfig, dql, process);

			if (totalLine == 0) {
				return Export_Erro_type.ERROR_TYPE_03;
			}

			long page = totalLine / fetchSize;
			if (totalLine % fetchSize != 0)
				page++;

			for (int i = 1; i <= page; i++) {
				Iterator<Document> iter = getAppointedRows(mappingconfig, i, fetchSize,
						dql, process);

				if (iter != null) {
					Collection<ColumnMapping> tempcols = (compareColums != null && compareColums
							.size() > 0) ? compareColums : a_colmaps;
					IRunner runner = JavaScriptFactory.getInstance(null,
							application);
					int count = 0;
					for (; iter.hasNext();) {
						Document dm = iter.next();
						runner.initBSFManager(dm, new ParamsTable(), user,
								new ArrayList<ValidateMessage>());
						if (tempcols != null) {
							insertOneRow(tempcols, pstmt, dm, runner);
						}
					}
					log.info("Insert row " + (++count) + "(ms)");
				}

			}

			updateMapping(mappingconfig);
			log.info("Export End");
			return String.valueOf(totalLine);
		} catch (Exception e) {
			throw e;
		} finally {
			this.colseStament();
		}
	}

	public void initalVariable() throws Exception {

		dataSource = mappingconfig.getDatasource();
		conn = dataSource.getConnection();
		dma = conn.getMetaData();
		stmt = conn.createStatement();
		tableName = mappingconfig.getTablename();
		DatabaseVersion = dma.getDatabaseProductVersion();
	}

	public void colseStament() throws Exception {

		if (stmt != null) {
			stmt.close();
			stmt = null;
		}
		if (pstmt != null) {
			pstmt.close();
			pstmt = null;
		}
		if (conn != null && !conn.isClosed()) {
			conn.close();
			conn = null;
		}

	}

	public void validate() {
		if (getMappingconfig() == null) {
			this.addFieldError("Erro", "Mappingconfig is not exist");
		} else if (getMappingconfig().getColumnMappings() == null
				|| getMappingconfig().getColumnMappings().size() <= 0) {
			this.addFieldError("Erro",
					"Mappingconfig' columnMapping is not exist");
		}
	}

	/**
	 * @return the mappingconfig
	 * @uml.property name="mappingconfig"
	 */
	public MappingConfig getMappingconfig() {
		return mappingconfig;
	}

	/**
	 * @param mappingconfig
	 *            the mappingconfig to set
	 * @uml.property name="mappingconfig"
	 */
	public void setMappingconfig(MappingConfig mappingconfig) {
		this.mappingconfig = mappingconfig;
	}

	// 初始化filefullname
	static {

		String path = null;
		try {
			path = DefaultProperty.getProperty("DOCUMENTEXPORTLOG_PATH");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (path == null || path.trim().length() < 1)
			path = defaultPath;

		fileFullName = path + DateUtil.getDateStrCompact(new Date())
				+ System.currentTimeMillis() + ".txt";// 输出出错信息的路径和文件名
	}

	public static Collection<ColumnMapping> createBasicColumn(Collection<ColumnMapping> col) throws Exception {
		// 每条记录的导也时间
		ColumnMapping expdate = new ColumnMapping();
		expdate.setFromName("EXPORTDATE");
		expdate.setToName("EXPORTDATE");
		expdate.setToType(ColumnMapping.DATA_TYPE_DATE);
		expdate.setLength("6");
		col.add(expdate);

		// 主键DocID
		ColumnMapping docid = new ColumnMapping();
		docid.setFromName("DOC_ID");
		docid.setToName("DOC_ID");
		docid.setToType(ColumnMapping.DATA_TYPE_VARCHAR);
		docid.setLength("255");
		col.add(docid);

		// Parentid
		ColumnMapping parentid = new ColumnMapping();
		parentid.setFromName("PARENTID");
		parentid.setToName("PARENTID");
		parentid.setToType(ColumnMapping.DATA_TYPE_VARCHAR);
		parentid.setLength("255");
		col.add(parentid);

		// FLOWSTATE
		ColumnMapping flowState = new ColumnMapping();
		flowState.setFromName("FLOWSTATE");
		flowState.setToName("FLOWSTATE");
		flowState.setToType(ColumnMapping.DATA_TYPE_VARCHAR);
		flowState.setLength("255");
		col.add(flowState);

		// FLOWSTATE NAME
		ColumnMapping flowStateName = new ColumnMapping();
		flowStateName.setFromName("FLOWSTATENAME");
		flowStateName.setToName("FLOWSTATENAME");
		flowStateName.setToType(ColumnMapping.DATA_TYPE_VARCHAR);
		flowStateName.setLength("255");
		col.add(flowStateName);

		return col;
	}

	// public static String getCreateTableSQL(MappingConfig mappingconfig,
	// Collection columnmappings, String DatabaseVersion) {
	// StringBuffer sqlCreatTable = new StringBuffer();
	// sqlCreatTable.append(" CREATE TABLE ");
	// sqlCreatTable.append(mappingconfig.getTablename());
	// sqlCreatTable.append(" ( ID Number(22,3) PRIMARY KEY");
	// if (columnmappings.size() > 0)
	// sqlCreatTable.append(",");
	// for (Iterator iter = columnmappings.iterator(); iter.hasNext();) {
	// ColumnMapping cm = (ColumnMapping) iter.next();
	// if (cm.getToName().equals("DOC_ID")) {
	// sqlCreatTable.append(cm.getToName().toUpperCase());
	// sqlCreatTable.append(" VARCHAR ");
	// sqlCreatTable.append(" (").append(cm.getLength()).append(")");
	// sqlCreatTable.append(" unique ");
	// } else if (cm.getToType().equals(ColumnMapping.DATA_TYPE_VARCHAR)) {
	// sqlCreatTable.append(cm.getToName().toUpperCase());
	// sqlCreatTable.append(" VARCHAR ");
	// sqlCreatTable.append(" (").append(cm.getLength()).append(")");
	// } else if (cm.getToType().equals(ColumnMapping.DATA_TYPE_NUMBER)) {
	// sqlCreatTable.append(cm.getToName().toUpperCase());
	// sqlCreatTable.append(" DECIMAL ");
	// // sqlCreatTable.append("
	// // (").append(cm.getLength()).append(")");
	// sqlCreatTable.append(" (").append(cm.getLength()).append(",");
	// sqlCreatTable.append(cm.getPrecision()).append(")");
	// } else if (cm.getToType().equals(ColumnMapping.DATA_TYPE_DATE)) {
	// sqlCreatTable.append(cm.getToName().toUpperCase()); // Column 名字
	// if (DatabaseVersion.indexOf("Microsoft") > -1)
	// sqlCreatTable.append(" DateTime ");
	// else if (DatabaseVersion.indexOf("Oracle") > -1)
	// sqlCreatTable.append(" Date ");
	// } else if (cm.getToType().equals("TIMESTAMP")) {
	// sqlCreatTable.append(cm.getToName().toUpperCase());
	// sqlCreatTable.append(" TIMESTAMP ");
	// sqlCreatTable.append(" (").append(cm.getLength()).append(")");
	// }
	//
	// if (iter.hasNext())
	// sqlCreatTable.append(", ");
	// }
	// sqlCreatTable.append(" ) ");
	// return sqlCreatTable.toString();
	// }

	public void alterTable(DataSource dataSource, String tabname,
			Statement stmt, Map<String, String> DBCols, Collection<ColumnMapping> nedAdCols,
			Collection<ColumnMapping> a_colmaps, BaseSql sqlBuilder) throws Exception {
		ResultSet rs = dma.getColumns(null, dataSource.getUsername()
				.toUpperCase(), tabname.toUpperCase(), null);
		while (rs.next()) {
			DBCols.put(rs.getString("COLUMN_NAME"), rs.getString("TYPE_NAME"));
		}

		for (Iterator<ColumnMapping> iter = a_colmaps.iterator(); iter.hasNext();) {

			ColumnMapping cm = iter.next();
			if (!DBCols.containsKey(cm.getToName().toUpperCase()))
				nedAdCols.add(cm);
		}

		// 根据needAddColumns,修改表,添加字段
		if (nedAdCols != null && nedAdCols.size() > 0) {
			String sql = sqlBuilder.alertTable(mappingconfig.getTablename(),
					nedAdCols);
			stmt.executeUpdate(sql);
		}
	}

	// public static String creatAlterTableSQL(MappingConfig mappingconfig,
	// ColumnMapping cm, String DatabaseVersion) {
	// StringBuffer sqlAlterTable = new StringBuffer();
	// sqlAlterTable.append("ALTER TABLE ").append(
	// mappingconfig.getTablename());
	// sqlAlterTable.append(" ADD ").append(cm.getToName()).append(" ");
	// if (cm.getToType().equals(ColumnMapping.DATA_TYPE_VARCHAR))
	// sqlAlterTable.append(" VARCHAR ");
	// else if (cm.getToType().equals(ColumnMapping.DATA_TYPE_NUMBER))
	// sqlAlterTable.append(" DECIMAL ");
	// else if (cm.getToType().equals(ColumnMapping.DATA_TYPE_DATE)) {
	// if (DatabaseVersion.indexOf("Microsoft") > -1)
	// sqlAlterTable.append(" DateTime ");
	// else if (DatabaseVersion.indexOf("Oracle") > -1)
	// sqlAlterTable.append(" DATE ");
	// }
	//
	// if (!cm.getToType().equals(ColumnMapping.DATA_TYPE_DATE))
	// if (cm.getToName().equals("DOC_ID"))
	// sqlAlterTable.append(" (").append(cm.getLength()).append(
	// ") unique");
	// else
	// sqlAlterTable.append(" (").append(cm.getLength()).append(")");
	// return sqlAlterTable.toString();
	// }

	public static Collection<ColumnMapping> compareDateType(Map<String, String> DataBaseFieldInfo,
			Collection<ColumnMapping> columnmappings, BaseSql sqlBuilder) {
		Collection<ColumnMapping> columns = new ArrayList<ColumnMapping>();
		for (Iterator<ColumnMapping> iter = columnmappings.iterator(); iter.hasNext();) {
			ColumnMapping cm = iter.next();
			ColumnMapping em = new ColumnMapping();
			em.setFromName(cm.getFromName());
			em.setToName(cm.getToName());
			em.setToType(cm.getToType());
			em.setType(cm.getType());
			em.setValuescript(cm.getValuescript());
			if (DataBaseFieldInfo.containsKey(cm.getToName().toUpperCase())) {
				String datetype = DataBaseFieldInfo.get(cm.getToName()
						.toUpperCase());
				em.setToType(sqlBuilder.transferType(cm, datetype));
				// if ((datetype.contains("NUMBER") || datetype
				// .contains("DECIMAL"))
				// && !cm.getToType().equals(
				// ColumnMapping.DATA_TYPE_NUMBER)) {
				//
				// em.setToType(ColumnMapping.DATA_TYPE_NUMBER);
				// } else if (datetype.contains("VARCHAR")
				// && !cm.getToType().equals(
				// ColumnMapping.DATA_TYPE_VARCHAR)) {
				// em.setToType(ColumnMapping.DATA_TYPE_VARCHAR);
				// }
				//
				// else if ((datetype.contains("DateTime") || datetype
				// .contains("Date"))
				// && !cm.getToType().equals(ColumnMapping.DATA_TYPE_DATE)) {
				// em.setToType(ColumnMapping.DATA_TYPE_DATE);
				// }
				columns.add(em);
			}
		}
		return columns;
	}

	public void insertOneRow(Collection<ColumnMapping> tempcols, PreparedStatement pstmt,
			Document dm, IRunner runner) throws Exception {
		int columnNum = 2;
		for (Iterator<ColumnMapping> it = tempcols.iterator(); it.hasNext();) {
			ColumnMapping cm = it.next();
			if (cm.getToName().equals("DOC_ID")) { // 设置主键
				pstmt.setString(columnNum, dm.getId());
				pstmt.setLong(1, Long.parseLong(Tools.getTimeSequence()));
			} else if (cm.getToName().equals("PARENTID")) { //
				if (dm.getParent() != null)
					pstmt.setString(columnNum, dm.getParent().getId());
				else
					pstmt.setNull(columnNum, Types.NULL);
			} else if (cm.getToName().equals("EXPORTDATE")) { // 设置导出时间
				pstmt.setDate(columnNum,
						new java.sql.Date(new Date().getTime()));
			} else if (cm.getToName().equals("FLOWSTATE")) {
				if (dm.getState() != null)
					pstmt.setString(columnNum, String.valueOf(dm.getState()
							.getState()));
				else
					pstmt.setNull(columnNum, Types.NULL);
			} else if (cm.getToName().equals("FLOWSTATENAME")) {
				if (dm.getState() != null)
					pstmt.setString(columnNum, dm.getStateLabel());
				else
					pstmt.setNull(columnNum, Types.NULL);
			} else {
				try {
					setParams(pstmt, runner, cm, columnNum, dm);
				} catch (Exception e) {
					errorInfo.append(e.getMessage() + "\r\n");
				}
			}
			columnNum++;
		}

		try {
			pstmt.executeUpdate();
		} catch (Exception e) {
			log.info(dm.getId() + "-->这条记录已存在,将删除再插入");
			stmt.executeUpdate("delete from " + mappingconfig.getTablename()
					+ " where DOC_ID='" + dm.getId() + "'");
			e.printStackTrace();
			pstmt.executeUpdate();
		}
	}

	private static void setParams(PreparedStatement pstmt, IRunner runner,
			ColumnMapping cm, int columnNum, Document dm) throws Exception {
		String colType = cm.getType();
		Object value = null;

		try {
			if (colType.equals(ColumnMapping.COLUMN_TYPE_FIELD)) {
				Item item = dm.findItem(cm.getFromName());
				if (cm.getFromName() != null
						&& cm.getFromName().startsWith("$")) {
					String prop = cm.getFromName().substring(1,
							cm.getFromName().length());
					String val = dm.getValueByPropertyName(prop);
					try {
						value = DateUtil.parseDate(val);
					} catch (Exception e) {
						value = val;
					}
				} else {
					value = getValueByDataType(cm.getToType(), item);
				}
			} else if (colType.equals(ColumnMapping.COLUMN_TYPE_SCRIPT)) {
				// long s = System.currentTimeMillis();
				StringBuffer label = new StringBuffer();
				label.append("Export(").append(cm.getId()).append(
						")." + cm.getToName()).append(".Valuescript");
				value = runner.run(label.toString(), cm.getValuescript());
			}

			if (value != null) {
				if (value instanceof Double) {
					pstmt.setDouble(columnNum, ((Double) value).doubleValue());
				} else if (value instanceof String) {
					pstmt.setString(columnNum, (String) value);
				} else if (value instanceof Date) {
					long time = ((Date) value).getTime();
					pstmt.setDate(columnNum, new java.sql.Date(time));
				}
			} else {
				pstmt.setNull(columnNum, Types.NULL);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Document " + dm.getId() + " Script run error");
		}
	}

	private static Object getValueByDataType(String type, Item item) {
		Object rtn = null;
		if (type.equals(ColumnMapping.DATA_TYPE_NUMBER)) {
			double val = item.getNumbervalue().doubleValue();
			rtn = new Double(val);
		} else if (type.equals(ColumnMapping.DATA_TYPE_DATE)) {
			rtn = item.getDatevalue();
		} else if (type.equals(ColumnMapping.DATA_TYPE_VARCHAR)) {
			rtn = item.getVarcharvalue();
		}
		return rtn;
	}

	protected void updateMapping(MappingConfig mappingconfig) throws Exception {
		MappingConfigProcess mp = (MappingConfigProcess) ProcessFactory
				.createProcess(MappingConfigProcess.class);
		mappingconfig.setLastRun(new Date());
		mp.doUpdate(mappingconfig);
	}
}
