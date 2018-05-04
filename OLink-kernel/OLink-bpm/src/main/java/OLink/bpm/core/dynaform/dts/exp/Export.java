package OLink.bpm.core.dynaform.dts.exp;

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
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.core.dynaform.document.ejb.DocumentProcess;
import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSource;
import OLink.bpm.core.dynaform.dts.exp.columnmapping.ejb.ColumnMapping;
import OLink.bpm.core.dynaform.dts.exp.mappingconfig.ejb.MappingConfig;
import OLink.bpm.core.dynaform.form.ejb.ValidateMessage;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.macro.runner.JavaScriptFactory;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.file.FileOperate;
import OLink.bpm.util.property.DefaultProperty;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.document.ejb.Item;
import OLink.bpm.core.dynaform.dts.exp.mappingconfig.ejb.MappingConfigProcess;
import OLink.bpm.util.DateUtil;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import eWAP.core.Tools;

public class Export {

	static Logger log = Logger.getLogger(Export.class);

	private static final String defaultPath = "C:/DoucemntExportLog/";

	/**
	 * exportDocument,导出document
	 * 
	 * @param mappcfgId
	 *            表单对应的mappcfgid
	 * @param IsIncrementExprot
	 *            是否是增量导出
	 * @param mappcfgId
	 *            表单对应的mappcfgid
	 * @param user
	 *            取得当前用户
	 * @param application
	 *            所属的应用的id
	 * @return 导出数据，返回导出状态
	 * @throws Exception
	 */
	public static String exprotDocument(String mappcfgId, boolean IsIncrementExprot, WebUser user, String application)
			throws Exception {

		MappingConfigProcess mp = (MappingConfigProcess) ProcessFactory.createProcess(MappingConfigProcess.class);
		MappingConfig mappingConfig = (MappingConfig) mp.doView(mappcfgId);

		return exprotDocument(mappingConfig, IsIncrementExprot, user, application);
	}

	/**
	 * exportDocument,导出document
	 * 
	 * @param mappingconfig
	 *            表单对应的mappincofig
	 * @param IsIncrementExprot
	 *            是否是增量导出
	 * @param mappcfgId
	 *            表单对应的mappcfgid
	 * @param user
	 *            取得当前用户
	 * @param application
	 *            所属的应用的id
	 * @return 导出数据，返回导出状态
	 * @throws Exception
	 */
	public static String exprotDocument(MappingConfig mappingconfig, boolean IsIncrementExprot, WebUser user,
			String application) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;
		StringBuffer errorInfo = new StringBuffer();// 记录出错信息
		Collection<ColumnMapping> map_columnmappings = null; // temp_columnmappings
		Collection<ColumnMapping> all_columnmappings = null;// columnmappings
		try {

			String path = DefaultProperty.getProperty("DOCUMENTEXPORTLOG_PATH");
			if (path == null || path.trim().length() < 1)
				path = defaultPath;

			String fileFullName = path + mappingconfig.getName() + DateUtil.getDateStrCompact(new Date())
					+ System.currentTimeMillis() + ".txt";// 输出出错信息的路径和文件名

			if (mappingconfig == null) {
				errorInfo.append(Export_Erro_type.ERROR_TYPE_02 + "\r\n");
				FileOperate.writeFile(fileFullName, errorInfo.toString(), true);
				return Export_Erro_type.ERROR_TYPE_02;
			}

			// 从相应的mappincofig中拿出columnmappings,如果size=0，则填写相应的错误日志，并返回
			map_columnmappings = mappingconfig.getColumnMappings();
			if (map_columnmappings == null || map_columnmappings.size() == 0) {
				errorInfo.append(Export_Erro_type.ERROR_TYPE_04 + "\r\n");
				FileOperate.writeFile(fileFullName, errorInfo.toString(), true);
				return Export_Erro_type.ERROR_TYPE_04;
			}

			// 从相应的mappingcofig中拿出datasource并生成相应的connection,statement
			DataSource dataSource = mappingconfig.getDatasource();
			conn = dataSource.getConnection();
			DatabaseMetaData dma = conn.getMetaData();
			String DatabaseVersion = dma.getDatabaseProductVersion();
			stmt = conn.createStatement();

			all_columnmappings = new HashSet<ColumnMapping>();
			all_columnmappings.addAll(map_columnmappings);

			// 调用createBasicColumn生成基本需要的column并加入到all_columnmappings
			createBasicColumn(all_columnmappings);

			String tablename = mappingconfig.getTablename();
			Map<String, String> DBExistColumns = null; // 数据库中存在的字段
			Collection<ColumnMapping> needAddColumns = null; // 需要新增的段
			ResultSet rs = dma.getTables(null, null, tablename.toUpperCase(), null);
			Session session = PersistenceUtils.currentSession();
			if (!rs.next()) {

				// 当不存在该表时,生成创建表语句
				String sqlCreatTable = getCreateTableSQL(mappingconfig, all_columnmappings, DatabaseVersion);
				stmt.executeUpdate(sqlCreatTable);

			} else {

				DBExistColumns = new HashMap<String, String>();
				needAddColumns = new ArrayList<ColumnMapping>();

				// 如果该表存在，则取出相应表的所有列,并与all_columnmappings中的列相比，找出在表列不存在的列，并存放在needAddColumns中
				rs = dma.getColumns(null, dataSource.getUsername().toUpperCase(), tablename.toUpperCase(), null);
				while (rs.next()) {
					DBExistColumns.put(rs.getString("COLUMN_NAME"), rs.getString("TYPE_NAME"));
				}

				for (Iterator<ColumnMapping> iter = all_columnmappings.iterator(); iter.hasNext();) {

					ColumnMapping cm = iter.next();
					if (!DBExistColumns.containsKey(cm.getToName().toUpperCase()))
						needAddColumns.add(cm);
				}

				// 根据needAddColumns,修改表,添加字段
				if (needAddColumns != null && needAddColumns.size() > 0) {
					for (Iterator<ColumnMapping> iter = needAddColumns.iterator(); iter.hasNext();) {
						ColumnMapping cm = iter.next();
						String sqlAlterTable = creatAlterTableSQL(mappingconfig, cm, DatabaseVersion);
						stmt.executeUpdate(sqlAlterTable.toString());
					}
				}
			}

			// MappconfigConfig.columns
			Collection<ColumnMapping> compareColums = null;

			/**
			 * 和目标数据库比较后的结果 MappconfigConfig.columns与目标数据库存在的字段类型不同时,以数据库字段为准
			 */
			if (DBExistColumns != null && DBExistColumns.size() > 0) {
				compareColums = compareDateType(DBExistColumns, all_columnmappings);
				compareColums.addAll(needAddColumns);
			}

			// 生成插入语句
			String sqlInsert = null;
			if (compareColums != null)
				// 表已存在情况
				sqlInsert = creatInsertSQL(mappingconfig, compareColums);
			else
				// 第一次创建表
				sqlInsert = creatInsertSQL(mappingconfig, all_columnmappings);

			// 准备PrepareStatement
			pstmt = conn.prepareStatement(sqlInsert);

			// 获取数据,相当于view的filterscript
			String dql = mappingconfig.getValuescript();
			DocumentProcess process = (DocumentProcess) ProcessFactory.createProcess(DocumentProcess.class);

			long totalLine = 0;
			if (!IsIncrementExprot || mappingconfig.getLastRun() == null)
				totalLine = process.getNeedExportDocumentTotal(dql, null, user.getDomainid());
			else
				totalLine = process.getNeedExportDocumentTotal(dql, mappingconfig.getLastRun(), user.getDomainid());

			if (totalLine == 0) {
				errorInfo.append(Export_Erro_type.ERROR_TYPE_03 + "\r\n");
				FileOperate.writeFile(fileFullName, errorInfo.toString(), true);
				return Export_Erro_type.ERROR_TYPE_03;
			}
			log.info("一共需要导出" + totalLine + "条数据");

			if (!IsIncrementExprot) { // 如果是全部导出的话,先将以前的数据删掉
				stmt.executeUpdate("delete from " + mappingconfig.getTablename());
			}

			int fetchSize = 600;

			long page = totalLine / fetchSize;
			if (totalLine % fetchSize != 0)
				page++;

			for (int i = 1; i <= page; i++) {
				long time1 = System.currentTimeMillis();
				Iterator<Document> iter = null;
				// 每次取fetchSize条数据
				if (IsIncrementExprot && mappingconfig.getLastRun() != null) {
					iter = process.queryByDQLAndDocumentLastModifyDate(dql, mappingconfig.getLastRun(), i, fetchSize,
							user.getDomainid());
				} else {
					iter = process.iteratorLimitByDQL(dql, i, fetchSize, user.getDomainid());
				}
				// session.clear();
				log.info((System.currentTimeMillis() - time1) + "ms");
				// 插入数据
				log.info("Session Size : " + session.getStatistics().getEntityCount());
				if (iter != null) {
					Collection<ColumnMapping> tempcols = null;
					if (compareColums != null && compareColums.size() > 0)
						tempcols = compareColums; // 表已在情况
					else
						tempcols = all_columnmappings; // 第一次创建表时
					IRunner runner = JavaScriptFactory.getInstance(null, application);
					long start3 = System.currentTimeMillis();
					int count = 0;
					for (; iter.hasNext();) {
						long startTime = System.currentTimeMillis();
						Document dm = iter.next();
						runner.initBSFManager(dm, new ParamsTable(), user, new ArrayList<ValidateMessage>());
						int columnNum = 2;

						if (tempcols != null) {
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
									pstmt.setDate(columnNum, new java.sql.Date(new Date().getTime()));
								} else if (cm.getToName().equals("FLOWSTATE")) {
									if (dm.getState() != null)
										pstmt.setString(columnNum, String.valueOf(dm.getState().getState()));
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
						}
						try {
							pstmt.executeUpdate();
						} catch (Exception e) {
							log.info(dm.getId() + "-->这条记录已存在,将删除再插入");
							stmt.executeUpdate("delete from " + mappingconfig.getTablename() + " where DOC_ID='"
									+ dm.getId() + "'");
							e.printStackTrace();
							pstmt.executeUpdate();
						}

						log.info("Insert row " + (++count) + " Used " + (System.currentTimeMillis() - startTime)
								+ "(ms)");
					}
					log.info("Total 插入 (" + totalLine + ") 条数据的时间: " + (System.currentTimeMillis() - start3) + "MS");
				}
			}

			MappingConfigProcess mp = (MappingConfigProcess) ProcessFactory.createProcess(MappingConfigProcess.class);
			mappingconfig.setLastRun(new Date());
			mp.doUpdate(mappingconfig);
			// session.refresh(mappingconfig);
			errorInfo.append("Derive " + totalLine + " data altogether");
			FileOperate.writeFile(fileFullName, errorInfo.toString(), true);
			log.info("Export End");
			return String.valueOf(totalLine);
		} catch (Exception e) {
			throw e;
		} finally {
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
	}

	public static String creatInsertSQL(MappingConfig mappingconfig, Collection<ColumnMapping> columns) {

		StringBuffer sqlInsert = new StringBuffer();
		sqlInsert.append(" insert into ");
		sqlInsert.append(mappingconfig.getTablename());
		sqlInsert.append(" (ID,");

		for (Iterator<ColumnMapping> iter = columns.iterator(); iter.hasNext();) {
			ColumnMapping cm = iter.next();
			sqlInsert.append(cm.getToName());
			if (iter.hasNext())
				sqlInsert.append(", ");
		}
		sqlInsert.append(" ) values (?,");

		for (Iterator<ColumnMapping> iter = columns.iterator(); iter.hasNext();) {
			iter.next();
			sqlInsert.append("?");
			if (iter.hasNext())
				sqlInsert.append(", ");

		}
		sqlInsert.append(" ) ");
		log.info(sqlInsert.toString());
		return sqlInsert.toString();
	}

	public static String creatAlterTableSQL(MappingConfig mappingconfig, ColumnMapping cm, String DatabaseVersion) {
		StringBuffer sqlAlterTable = new StringBuffer();
		sqlAlterTable.append("ALTER TABLE ").append(mappingconfig.getTablename());
		sqlAlterTable.append(" ADD ").append(cm.getToName()).append(" ");
		if (cm.getToType().equals(ColumnMapping.DATA_TYPE_VARCHAR))
			sqlAlterTable.append(" VARCHAR ");
		else if (cm.getToType().equals(ColumnMapping.DATA_TYPE_NUMBER))
			sqlAlterTable.append(" DECIMAL ");
		else if (cm.getToType().equals(ColumnMapping.DATA_TYPE_DATE)) {
			if (DatabaseVersion.indexOf("Microsoft") > -1)
				sqlAlterTable.append(" DateTime ");
			else if (DatabaseVersion.indexOf("Oracle") > -1)
				sqlAlterTable.append(" DATE ");
		}

		if (!cm.getToType().equals(ColumnMapping.DATA_TYPE_DATE))
			if (cm.getToName().equals("DOC_ID"))
				sqlAlterTable.append(" (").append(cm.getLength()).append(") unique");
			else
				sqlAlterTable.append(" (").append(cm.getLength()).append(")");
		return sqlAlterTable.toString();
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

	public static String getCreateTableSQL(MappingConfig mappingconfig, Collection<ColumnMapping> columnmappings,
			String DatabaseVersion) {
		StringBuffer sqlCreatTable = new StringBuffer();
		sqlCreatTable.append(" CREATE TABLE ");
		sqlCreatTable.append(mappingconfig.getTablename());
		sqlCreatTable.append(" ( ID Number(22,3)  PRIMARY KEY");
		if (columnmappings.size() > 0)
			sqlCreatTable.append(",");
		for (Iterator<ColumnMapping> iter = columnmappings.iterator(); iter.hasNext();) {
			ColumnMapping cm = iter.next();
			if (cm.getToName().equals("DOC_ID")) {
				sqlCreatTable.append(cm.getToName().toUpperCase());
				sqlCreatTable.append(" VARCHAR ");
				sqlCreatTable.append(" (").append(cm.getLength()).append(")");
				sqlCreatTable.append("  unique ");
			} else if (cm.getToType().equals(ColumnMapping.DATA_TYPE_VARCHAR)) {
				sqlCreatTable.append(cm.getToName().toUpperCase());
				sqlCreatTable.append(" VARCHAR ");
				sqlCreatTable.append(" (").append(cm.getLength()).append(")");
			} else if (cm.getToType().equals(ColumnMapping.DATA_TYPE_NUMBER)) {
				sqlCreatTable.append(cm.getToName().toUpperCase());
				sqlCreatTable.append(" DECIMAL ");
				// sqlCreatTable.append("
				// (").append(cm.getLength()).append(")");
				sqlCreatTable.append(" (").append(cm.getLength()).append(",");
				sqlCreatTable.append(cm.getPrecision()).append(")");
			} else if (cm.getToType().equals(ColumnMapping.DATA_TYPE_DATE)) {
				sqlCreatTable.append(cm.getToName().toUpperCase()); // Column 名字
				if (DatabaseVersion.indexOf("Microsoft") > -1)
					sqlCreatTable.append(" DateTime ");
				else if (DatabaseVersion.indexOf("Oracle") > -1)
					sqlCreatTable.append(" Date ");
			} else if (cm.getToType().equals("TIMESTAMP")) {
				sqlCreatTable.append(cm.getToName().toUpperCase());
				sqlCreatTable.append(" TIMESTAMP ");
				sqlCreatTable.append(" (").append(cm.getLength()).append(")");
			}

			if (iter.hasNext())
				sqlCreatTable.append(", ");
		}
		sqlCreatTable.append(" ) ");
		return sqlCreatTable.toString();
	}

	/**
	 * MappconfigConfig.columns与数据库存在的字段类型不同时,以数据库字段为准
	 * 
	 * @param DataBaseFieldInfo
	 * @param columnmappings
	 * @return
	 */
	public static Collection<ColumnMapping> compareDateType(Map<String, String> DataBaseFieldInfo, Collection<ColumnMapping> columnmappings) {
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
				String datetype = DataBaseFieldInfo.get(cm.getToName().toUpperCase());
				if ((datetype.indexOf("NUMBER") != -1 || datetype.indexOf("DECIMAL") != -1)
						&& !cm.getToType().equals(ColumnMapping.DATA_TYPE_NUMBER)) {

					em.setToType(ColumnMapping.DATA_TYPE_NUMBER);
				} else if (datetype.indexOf("VARCHAR") != -1 && !cm.getToType().equals(ColumnMapping.DATA_TYPE_VARCHAR)) {
					em.setToType(ColumnMapping.DATA_TYPE_VARCHAR);
				}

				else if ((datetype.indexOf("DateTime") != -1 || datetype.indexOf("Date") != -1)
						&& !cm.getToType().equals(ColumnMapping.DATA_TYPE_DATE)) {
					em.setToType(ColumnMapping.DATA_TYPE_DATE);
				}
				columns.add(em);
			}
		}
		return columns;
	}

	/**
	 * 
	 * @param pstmt
	 * @param runner
	 * @param cm
	 * @param columnNum
	 * @param dm
	 * @throws Exception
	 */
	private static void setParams(PreparedStatement pstmt, IRunner runner, ColumnMapping cm, int columnNum, Document dm)
			throws Exception {
		String colType = cm.getType();
		Object value = null;

		try {
			if (colType.equals(ColumnMapping.COLUMN_TYPE_FIELD)) {
				Item item = dm.findItem(cm.getFromName());
				if (cm.getFromName() != null && cm.getFromName().startsWith("$")) {
					String prop = cm.getFromName().substring(1, cm.getFromName().length());
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
				label.append("Export(").append(cm.getId()).append(")." + cm.getToName()).append(".Valuescript");
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

	/**
	 * 根据ColumnMapping的data type获取Item的值
	 * 
	 * @param type
	 * @param item
	 * @return
	 */
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

	public static void main(String args[]) throws Exception {
		// MappingConfig temp = new MappingConfig();
		// exprotDocument(temp);
	}
}
