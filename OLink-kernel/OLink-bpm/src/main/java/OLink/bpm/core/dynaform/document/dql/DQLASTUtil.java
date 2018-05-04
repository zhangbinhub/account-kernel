package OLink.bpm.core.dynaform.document.dql;

import java.io.StringReader;
import java.util.HashSet;

import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.dynaform.form.ejb.mapping.TableMapping;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.dynaform.form.ejb.Form;
import antlr.collections.AST;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.util.StringUtil;

public class DQLASTUtil {
	public final static String TBL_PREFIX = "TLK_";

	public final static String LOG_PREFIX = "LOG_";

	public final static String AUTH_PREFIX = "AUTH_";

	public final static int TABEL_TYPE_CONTENT = 1;// 动态表TLK_开头的

	public final static int TABEL_TYPE_LOG = 2;// 动态LOG表

	public final static int TABLE_TYPE_AUTH = 3; // 动态权限表

	public final static String DOC_FIELD_PREFIX = "";

	public final static String ITEM_FIELD_PREFIX = "ITEM_";

	public final static String TEMP_PREFIX = "TMP_";

	public final static String _TBNAME = "T_DOCUMENT";

	public final static String BLOD_FIELD_CONTENT_SUFFIX = "_CONTENT";

	public final static String BLOD_FIELD_EXTENSE_SUFFIX = "_EXTENSE";

	public final static String BLOD_FIELD_VALUE_SUFFIX = "_VALUE";

	public final static HashSet<String> SYSTEM_FIELDS = new HashSet<String>();

	static {
		// ,,,,,,,,,,,,,,
		SYSTEM_FIELDS.add("ID");
		SYSTEM_FIELDS.add("PARENT");
		SYSTEM_FIELDS.add("LASTMODIFIED");
		SYSTEM_FIELDS.add("FORMNAME");
		SYSTEM_FIELDS.add("STATE");
		SYSTEM_FIELDS.add("AUDITDATE");
		SYSTEM_FIELDS.add("AUTHOR");
		SYSTEM_FIELDS.add("CREATED");
		SYSTEM_FIELDS.add("FORMID");
		SYSTEM_FIELDS.add("ISTMP");
		SYSTEM_FIELDS.add("FLOWID");
		SYSTEM_FIELDS.add("VERSIONS");
		SYSTEM_FIELDS.add("SORTID");
		SYSTEM_FIELDS.add("APPLICATIONID");
		SYSTEM_FIELDS.add("STATEINT");
		SYSTEM_FIELDS.add("STATELABEL");
		SYSTEM_FIELDS.add("LASTFLOWOPERATION");
		SYSTEM_FIELDS.add("AUDITORNAMES");
		SYSTEM_FIELDS.add("AUDITORLIST");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}

	public static String inListToString(AST ast) {
		StringBuffer text = new StringBuffer();
		if (ast != null && ast.getNumberOfChildren() > 0) {
			String t = ast.getText();
			if (t != null && t.equals("inList")) {
				AST tmp = ast.getFirstChild();
				text.append("(");
				while (tmp != null) {
					text.append(tmp.getText());
					tmp = tmp.getNextSibling();
					if (tmp != null) {
						text.append(",");
					}
				}
				text.append(")");
			}
		}
		return text.toString();
	}

	private static String getParentInnerJoinPart(TableMapping tableMapping) {
		String sql = "";
		if (tableMapping.getFormType() == Form.FORM_TYPE_NORMAL) {
			sql += getFullTableName(tableMapping.getTableName()) + " parentdoc";
		} else {
			sql = getFullTableName(_TBNAME) + " parentdoc";
			sql += " INNER JOIN "
					+ getFullTableName(tableMapping.getTableName())
					+ " parentm";
			sql += " ON parentdoc.MAPPINGID=parentm."
					+ tableMapping.getPrimaryKeyName();
		}

		return sql;
	}

	private static String getSubInnerJoinPart(TableMapping tableMapping) {
		String sql = "";
		if (tableMapping.getFormType() == Form.FORM_TYPE_NORMAL) {
			sql += getFullTableName(tableMapping.getTableName()) + " subdoc";
		} else {
			sql = getFullTableName(_TBNAME) + " subdoc";
			sql += " INNER JOIN "
					+ getFullTableName(tableMapping.getTableName()) + " subm";
			sql += " ON subdoc.MAPPINGID=subm."
					+ tableMapping.getPrimaryKeyName();
		}

		return sql;
	}

	public static String transTo(String text, TableMapping tableMapping,
			ParamsTable params, int side, int ident, int opt,
			SQLFunction sqlFuction) {
		try {
			FormProcess formProcess = (FormProcess) ProcessFactory
					.createProcess(FormProcess.class);

			StringBuffer str = new StringBuffer();
			if (text != null) {
				if (ident == DqlTokenTypes.IDENT) {
					if (text.toUpperCase().startsWith("$PARENT.$ID")) {
						str.append(side == 1 ? "(" : "");
						str.append("d.parent");
						str.append(side == 2 ? ")" : "");
					} else if (text.toUpperCase().startsWith("$PARENT.")) {
						// 增加父表单条件查询
						text = text.substring(8);

						int pos = text.indexOf(".");

						String parentFormName = text.substring(0, pos);

						int len = parentFormName.length();

						Form parentForm = formProcess
								.doViewByFormName(parentFormName, tableMapping
										.getApplicationId());

						parentFormName = getFullTableName(parentForm
								.getTableMapping().getTableName());

						String fieldName = text.substring(len + 1);

						if (fieldName.startsWith("$")) {
							str
									.append(
											"d.parent in (select parentdoc.id from ")
									.append(_TBNAME)
									.append(" parentdoc where ");
							str.append("parentdoc.").append(
									fieldName.substring(1, fieldName.length()));

						} else {
							String columnName = parentForm.getTableMapping()
									.getColumnName(fieldName);

							str
									.append(
											"d.parent in (select parentdoc.id from ")
									.append(
											getParentInnerJoinPart(parentForm
													.getTableMapping()))
									.append(" where ");

							if (opt == DqlTokenTypes.ILIKE) {
								str.append(sqlFuction.lower(columnName))
										.append(")");
							} else {
								str.append(columnName);
							}
						}
					}

					else if (text.toUpperCase().startsWith("$STATE.STATE")) {
						// 流程状态代码
						str.append(side == 1 ? "(" : "");
						str.append("d.stateInt");
						str.append(text.substring(12, text.length()));
						str.append(side == 2 ? ")" : "");
					}
					// doc.state.noderts.statelabel
					else if (text.toUpperCase().startsWith(
							"$STATE.NODERTS.STATELABEL")) {
						// 流程状态
						str.append(side == 1 ? "(" : "");
						str.append("d.stateLabel");
						str.append(text.substring(25, text.length()));
						str.append(side == 2 ? ")" : "");
					}

					else if (text.toUpperCase().startsWith("$CHILDS")) {
						// 增加子表单条件查询
						text = text.substring(8);

						int pos = text.indexOf(".");

						String childFormName = text.substring(0, pos);

						int len = childFormName.length();

						Form childForm = formProcess.doViewByFormName(
								childFormName, tableMapping.getApplicationId());

						childFormName = getFullTableName(childForm
								.getTableMapping().getTableName());

						String fieldName = text.substring(len + 1);

						if (fieldName.startsWith("$")) {
							str.append(side == 1 ? "(" : "");

							str.append("d.childs.");
							str.append(childForm.getTableMapping()
									.getTableName());
							str.append(side == 2 ? ")" : "");
						} else {
							String columnName = childForm.getTableMapping()
									.getColumnName(fieldName);

							str.append("d.id in (select subdoc.parent from ")
									.append(
											getSubInnerJoinPart(childForm
													.getTableMapping()))
									.append(" where ");

							if (opt == DqlTokenTypes.ILIKE) {
								str.append(
										sqlFuction
												.lower("subdoc." + columnName))
										.append(")");
							} else {
								str.append("subdoc.").append(columnName);
							}
						}
					} else if (text.startsWith("$author.id")) {
						str.append(side == 1 ? "(" : "");
						str.append("d.author");
						str.append(side == 2 ? ")" : "");
					} else if (text.startsWith("$state.actors.actorid")) {
						str
								.append("d.id in (")
								.append(
										"select states.docid from "
												+ getFullTableName("t_flowstatert")
												+ " states, "
												+ getFullTableName("t_actorrt")
												+ " actors ")
								.append(
										"where states.id = actors.flowstatert_id and actors.actorid ");

						str.append(side == 2 ? ")" : "");
					} else if (text.startsWith("$")) {
						str.append(side == 1 ? "(" : "");
						String fieldName = text.substring(1, text.length());
						str.append("d.");
						if (SYSTEM_FIELDS.contains(fieldName.toUpperCase())) {
							str.append(fieldName);
						} else {
							str.append(tableMapping.getColumnName(fieldName));
						}
						str.append(side == 2 ? ")" : "");
					} else if (text.startsWith("#")) {
						// 日期处理
						str.append(side == 1 ? "(" : "");

						if (text.startsWith("#T")) {
							if (text.startsWith("#T$")) {
								str.append(sqlFuction.toChar(text.substring(3,
										text.length()), "yyyy-MM-dd HH:mm:ss"));
							} else {
								String columnName = tableMapping
										.getColumnName(text.substring(2, text
												.length()));
								str.append(sqlFuction.toChar(columnName,
										"yyyy-MM-dd HH:mm:ss"));
							}
						} else {
							if (text.startsWith("#$")) {
								str.append(sqlFuction.toChar(text.substring(2,
										text.length()), "yyyy-MM-dd"));
							} else {
								String columnName = tableMapping
										.getColumnName(text.substring(1, text
												.length()));
								str.append(sqlFuction.toChar(columnName,
										"yyyy-MM-dd"));
							}
						}
						str.append(side == 2 ? ")" : "");
					} else {
						str.append(side == 1 ? "(" : "");

						if (opt == DqlTokenTypes.ILIKE) {
							str.append(sqlFuction.lower(tableMapping
									.getColumnName(text)));
						} else {
							str.append(tableMapping.getColumnName(text));
						}
						str.append(side == 2 ? ")" : "");
					}
				} else {
					str.append(side == 1 ? "(" : "");
					if (opt == DqlTokenTypes.ILIKE) {
						str.append(sqlFuction.lower(text));
					} else {
						str.append(text);
					}

					str.append(side == 2 ? ")" : "");
				}
			}
			return str.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * 返回转换成的HQL语句
	 * 
	 * @param dql
	 *            DQL语句
	 * @param moduleName
	 *            模块名
	 * @param params
	 *            ParamsTable(参数)
	 * @see ParamsTable#params
	 * @return 转换成的HQL语句
	 * @throws Exception
	 */
	public static String parseToHQL(String dql, TableMapping tableMapping,
			ParamsTable params, SQLFunction sqlFuction) throws Exception {
		DqlBaseLexer lexer = new DqlBaseLexer(new StringReader(dql));
		DqlBaseParser parser = new DqlBaseParser(lexer);
		parser.exprList();
		// parser.whenClause();
		AST t = parser.getAST();

		ExprTreeParser treeParser = new ExprTreeParser();
		String x = treeParser.expr(t, tableMapping, params, 0, 0, sqlFuction);
		if (x.endsWith("AND )"))
			x = x.substring(0, x.length() - 5) + ")";
		return x;
	}

	/**
	 * 返回转换成的HQL语句
	 * 
	 * @param dql
	 *            DQL语句
	 * @param params
	 *            ParamsTable(参数)
	 * @see ParamsTable#params
	 * @return 转换成的HQL语句
	 * @throws Exception
	 */
	public static String parseToHQL(String dql, ParamsTable params,
			SQLFunction sqlFuction) throws Exception {
		return parseToHQL(dql, null, params, sqlFuction);
	}

	/**
	 * 返回将DQL语句转换成的HQL语句
	 * 
	 * @param dql
	 *            DQL语句
	 * @return 转换成的HQL语句
	 * @throws Exception
	 */
	public static String parseToHQL(String dql, TableMapping tableMapping,
			SQLFunction sqlFuction) throws Exception {
		return parseToHQL(dql, tableMapping, new ParamsTable(), sqlFuction);
	}

	/**
	 * 获取表名. 表名的规则为以前缀"TLK_"+表单名(form name).
	 * 
	 * @param formname
	 * @return
	 */
	public static String getItemTblName(String formname, int tabelType) {

		int pos = formname.lastIndexOf("/");
		//		
		//		
		// String tblname = TBL_PREFIX + formname;
		// tblname = tblname.replaceAll("/", "_");
		// tblname = tblname.replaceAll(" ", "_");
		// tblname = tblname.replaceAll("#", "_");
		String tblname = "";
		if (pos != -1) {
			if (tabelType == DQLASTUtil.TABEL_TYPE_CONTENT) {
				tblname = formname.substring(pos + 1);
			} else if (tabelType == DQLASTUtil.TABEL_TYPE_LOG) {
				tblname = LOG_PREFIX + formname.substring(pos + 1);
			} else if (tabelType == DQLASTUtil.TABLE_TYPE_AUTH) {
				tblname = AUTH_PREFIX + formname.substring(pos + 1);
			}
		} else {
			int start = formname.lastIndexOf("=");
			if (tabelType == DQLASTUtil.TABEL_TYPE_CONTENT) {
				tblname = formname.substring(start + 1);
			} else if (tabelType == DQLASTUtil.TABEL_TYPE_LOG) {
				tblname = LOG_PREFIX + formname.substring(start + 1);
			} else if (tabelType == DQLASTUtil.TABLE_TYPE_AUTH) {
				tblname = AUTH_PREFIX + formname.substring(pos + 1);
			}
		}

		return tblname.toUpperCase();

	}

	public static void setSchema(String schema0) {
		schema = schema0;
	}

	private static String schema = "";

	public static String getFullTableName(String tblname) {
		if (schema != null && !schema.trim().equals("")) {
			return schema.trim().toUpperCase() + "."
					+ tblname.trim().toUpperCase();
		}
		return tblname.trim().toUpperCase();
	}

	public static String addSchema(String sql, String schema) {
		String newSQL = sql.toLowerCase();
		int index = sql.indexOf(" from ");
		if (index >= 0) {
			String new1 = sql.substring(0, index + 6);
			newSQL = sql.substring(index + 6);
			String new2 = "";
			String new3 = "";
			String tablename = "";
			if ((index = newSQL.indexOf(" where ")) >= 0) {
				new3 = addSchema(newSQL.substring(index), schema);
				tablename = newSQL.substring(0, index);
			}
			if ((index = tablename.indexOf(" order by ")) >= 0) {
				new2 = addSchema(tablename.substring(index), schema);
				tablename = tablename.substring(0, index);
			}
			if ((index = tablename.indexOf(".")) >= 0) {
				String newSchema = tablename.substring(0, index);
				if (StringUtil.isBlank(newSchema))
					tablename = schema + tablename;
			} else {
				tablename = schema + "." + tablename;
			}
			newSQL = new1 + tablename + new2 + new3;
		}
		return newSQL;
	}

}
