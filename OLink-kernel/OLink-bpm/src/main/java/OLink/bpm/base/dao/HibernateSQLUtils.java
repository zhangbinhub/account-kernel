package OLink.bpm.base.dao;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Stack;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.util.StringUtil;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

/**
 * The base hibernate sql utility.
 */
public class HibernateSQLUtils implements SQLUtils {

	private static final Logger log = Logger.getLogger(HibernateSQLUtils.class);

	/**
	 * Create the where statement.
	 * 
	 * @param classname
	 *            String
	 * @param params
	 *            Object
	 * @return The where statement.
	 * @see SQLUtils#createWhere(String,
	 *      Object)
	 */
	public String createWhere(String classname, Object params) {

		// If the paramter is null, return the "";
		if (params == null)
			return "";

		// If system cann't find the class, return the "";
		try {
			Class.forName(classname);
		} catch (Exception ex) {
			return "";
		}

		return createWhere((ParamsTable) params);
	}

	/**
	 * Create the where statement.
	 * 
	 * @param params
	 *            The parameter table
	 * @return The where statement.
	 */
	public String createWhere(ParamsTable params) {
		// If the paramter is null, return the "";
		if (params == null)
			return "";

		ParamsTable paramsTable = getParameterTable(params);
		Iterator<String> iter = paramsTable.getParameterNames();
		String cndtn = "";

		while (iter.hasNext()) {
			// String prmn = (String) iter.next();
			String prmn = iter.next();
			String paramsValue = paramsTable.getParameterAsString(prmn);
			int st = prmn.indexOf("_");

			if (st > 0 && paramsValue != null && paramsValue.length() > 0) {
				String fieldname = prmn.substring(st + 1);
				
				// 非空
				if (prmn.toLowerCase().startsWith("inn_")) {
					cndtn += fieldname + " IS NOT NULL and ";
					continue;
				}
				
				if (prmn.toLowerCase().startsWith("san_")) {
					cndtn += fieldname + " <> '' and ";
					continue;
				}
				if (prmn.toLowerCase().startsWith("sxn_")) {
					cndtn += fieldname + " != '' and ";
					continue;
				}
				// 非零
				if (prmn.toLowerCase().startsWith("inz_")) {
					cndtn += fieldname + " <> 0 and ";
					continue;
				}
				if (prmn.toLowerCase().startsWith("lnz_")) {
					cndtn += fieldname + " <> 0 and ";
					continue;
				}
				if (prmn.toLowerCase().startsWith("dnz_")) {
					cndtn += fieldname + " <> 0 and ";
					continue;
				}
				if (prmn.toLowerCase().startsWith("cnz_")) {
					cndtn += fieldname + " <> 0 and ";
					continue;
				}
				if (prmn.toLowerCase().startsWith("snz_")) {
					cndtn += fieldname + " <> ' and ";
					continue;
				}
				if (prmn.toLowerCase().startsWith("fnz_")) {
					cndtn += fieldname + " <> 0 and ";
					continue;
				}
				// 正数
				if (prmn.toLowerCase().startsWith("ip_")) {
					cndtn += fieldname + " > 0 and ";
					continue;
				}
				if (prmn.toLowerCase().startsWith("lp_")) {
					cndtn += fieldname + " > 0 and ";
					continue;
				}
				if (prmn.toLowerCase().startsWith("dp_")) {
					cndtn += fieldname + " > 0 and ";
					continue;
				}
				if (prmn.toLowerCase().startsWith("cp_")) {
					cndtn += fieldname + " > 0 and ";
					continue;
				}
				if (prmn.toLowerCase().startsWith("fp_")) {
					cndtn += fieldname + " > 0 and ";
					continue;
				}
				// 负数
				if (prmn.toLowerCase().startsWith("in_")) {
					cndtn += fieldname + " < 0 and ";
					continue;
				}
				if (prmn.toLowerCase().startsWith("ln_")) {
					cndtn += fieldname + " < 0 and ";
					continue;
				}
				if (prmn.toLowerCase().startsWith("dn_")) {
					cndtn += fieldname + " < 0 and ";
					continue;
				}
				if (prmn.toLowerCase().startsWith("cn_")) {
					cndtn += fieldname + " < 0 and ";
					continue;
				}
				if (prmn.toLowerCase().startsWith("fn_")) {
					cndtn += fieldname + " < 0 and ";
					continue;
				}

				String[] vallist = null;
				try {
					vallist = StringUtil.split(paramsValue, "|");
				} catch (Exception ex) {
				}
				String val = "";

				cndtn += " ( ";
				for (int j = 0; j < vallist.length; j++) {
					val = vallist[j].trim();
					
					// 等于
					if (prmn.toLowerCase().startsWith("n_") && val.length() > 0) {
						cndtn += fieldname + " = " + val + " or ";
						continue;
					}
					if (prmn.toLowerCase().startsWith("i_") && val.length() > 0) {
						cndtn += fieldname + " = " + val + " or ";
						continue;
					}
					if (prmn.toLowerCase().startsWith("l_") && val.length() > 0) {
						cndtn += fieldname + " = " + val + " or ";
						continue;
					}
					if (prmn.toLowerCase().startsWith("d_") && val.length() > 0) {
						// cndtn += fieldname + " = '" + val + "' or ";
						cndtn += fieldname + " = TO_DATE('" + val + "', 'yyyy-mm-dd') or ";
						continue;
					}
					if (prmn.toLowerCase().startsWith("c_") && val.length() > 0) {
						cndtn += fieldname + " = " + val + " or ";
						continue;
					}
					if (prmn.toLowerCase().startsWith("s_") && val.length() > 0) {
						cndtn += fieldname + " like '" + val + "' or ";
						continue;
					}
					if (prmn.toLowerCase().startsWith("f_") && val.length() > 0) {
						cndtn += fieldname + " = " + val + " or ";
						continue;
					}
					if (prmn.toLowerCase().startsWith("t_") && val.length() > 0) {
						cndtn += fieldname + " = '" + val + "' or ";
						continue;
					}
					if (prmn.toLowerCase().startsWith("b_") && val.length() > 0) {
						cndtn += fieldname + " = '" + val + "' or ";
						continue;
					}
					// 不等于
					if (prmn.toLowerCase().startsWith("xi_") && val.length() > 0) {
						cndtn += fieldname + " <> '" + val + "' or ";
						continue;
					}
					if (prmn.toLowerCase().startsWith("xl_") && val.length() > 0) {
						cndtn += fieldname + " <> " + val + " or ";
						continue;
					}
					if (prmn.toLowerCase().startsWith("xn_") && val.length() > 0) {
						cndtn += fieldname + " <> " + val + " or ";
						continue;
					}

					// 大于
					if (prmn.toLowerCase().startsWith("ib_") && val.length() > 0) {
						cndtn += fieldname + " > " + val + " or ";
						continue;
					}
					if (prmn.toLowerCase().startsWith("lb_") && val.length() > 0) {
						cndtn += fieldname + " > " + val + " or ";
						continue;
					}
					if (prmn.toLowerCase().startsWith("db_") && val.length() > 0) {
						cndtn += fieldname + " > " + val + " or ";
						continue;
					}
					if (prmn.toLowerCase().startsWith("cb_") && val.length() > 0) {
						cndtn += fieldname + " > " + val + " or ";
						continue;
					}
					if (prmn.toLowerCase().startsWith("sb_") && val.length() > 0) {
						cndtn += fieldname + " > '" + val + "' or ";
						continue;
					}
					if (prmn.toLowerCase().startsWith("fb_") && val.length() > 0) {
						cndtn += fieldname + " > " + val + " or ";
						continue;
					}
					if (prmn.toLowerCase().startsWith("tb_") && val.length() > 0) {
						cndtn += fieldname + " > '" + val + "' or ";
						continue;
					}
					// 小于
					if (prmn.toLowerCase().startsWith("is_") && val.length() > 0) {
						cndtn += fieldname + " < " + val + " or ";
						continue;
					}
					if (prmn.toLowerCase().startsWith("ls_") && val.length() > 0) {
						cndtn += fieldname + " < " + val + " or ";
						continue;
					}
					if (prmn.toLowerCase().startsWith("ds_") && val.length() > 0) {
						cndtn += fieldname + " < " + val + " or ";
						continue;
					}
					if (prmn.toLowerCase().startsWith("cs_") && val.length() > 0) {
						cndtn += fieldname + " < " + val + " or ";
						continue;
					}
					if (prmn.toLowerCase().startsWith("ss_") && val.length() > 0) {
						cndtn += fieldname + " < '" + val + "' or ";
						continue;
					}
					if (prmn.toLowerCase().startsWith("fs_") && val.length() > 0) {
						cndtn += fieldname + " < " + val + " or ";
						continue;
					}
					if (prmn.toLowerCase().startsWith("ts_") && val.length() > 0) {
						cndtn += fieldname + " < '" + val + "' or "; // for
						// oracle
						continue;
					}
					// 大于等于
					if (prmn.toLowerCase().startsWith("ibe_") && val.length() > 0) {
						cndtn += fieldname + " >= " + val + " or ";
						continue;
					}
					if (prmn.toLowerCase().startsWith("lbe_") && val.length() > 0) {
						cndtn += fieldname + " >= " + val + " or ";
						continue;
					}
					if (prmn.toLowerCase().startsWith("dbe_") && val.length() > 0) {
						cndtn += fieldname + " >= " + val + " or ";
						continue;
					}
					if (prmn.toLowerCase().startsWith("cbe_") && val.length() > 0) {
						cndtn += fieldname + " >= " + val + " or ";
						continue;
					}
					if (prmn.toLowerCase().startsWith("sbe_") && val.length() > 0) {
						cndtn += fieldname + " >= '" + val + "' or ";
						continue;
					}
					if (prmn.toLowerCase().startsWith("mbe_") && val.length() > 0) {
						cndtn += fieldname + " >= '" + val.substring(0, 10) + " " + val.substring(10, val.length())
								+ "' or ";
						continue;
					}
					if (prmn.toLowerCase().startsWith("fbe_") && val.length() > 0) {
						cndtn += fieldname + " >= " + val + " or ";
						continue;
					}
					if (prmn.toLowerCase().startsWith("tbe_") && val.length() > 0) {
						cndtn += fieldname + " >= '" + val + "' or ";
						continue;
					}
					// 小于等于
					if (prmn.toLowerCase().startsWith("ise_") && val.length() > 0) {
						cndtn += fieldname + " <= " + val + " or ";
						continue;
					}
					if (prmn.toLowerCase().startsWith("lse_") && val.length() > 0) {
						cndtn += fieldname + " <= " + val + " or ";
						continue;
					}
					if (prmn.toLowerCase().startsWith("dse_") && val.length() > 0) {
						cndtn += fieldname + " <= " + val + " or ";
						continue;
					}
					if (prmn.toLowerCase().startsWith("cse_") && val.length() > 0) {
						cndtn += fieldname + " <= " + val + " or ";
						continue;
					}
					if (prmn.toLowerCase().startsWith("sse_") && val.length() > 0) {
						cndtn += fieldname + " <= '" + val + "' or ";
						continue;
					}
					if (prmn.toLowerCase().startsWith("mse_") && val.length() > 0) {
						cndtn += fieldname + " <= '" + val.substring(0, 10) + " " + val.substring(10, val.length())
								+ "' or ";
						continue;
					}
					if (prmn.toLowerCase().startsWith("fse_") && val.length() > 0) {
						cndtn += fieldname + " <= " + val + " or ";
						continue;
					}
					if (prmn.toLowerCase().startsWith("tse_") && val.length() > 0) {
						cndtn += fieldname + " <= '" + val + "' or ";
						continue;
					}
					// 模糊
					if (prmn.toLowerCase().startsWith("sm_") && val.length() > 0) {
						cndtn += fieldname + " like '%" + val + "%' or ";
						continue;
					}
				}
				cndtn = cndtn.endsWith("or ") ? cndtn.substring(0, cndtn.length() - 3) + " ) and " : cndtn;
				cndtn = cndtn.trim().endsWith("(") ? "" : cndtn;
			}
		}

		cndtn = cndtn.endsWith("and ") ? cndtn.substring(0, cndtn.length() - 4) : cndtn;
		return cndtn;
	}

	/**
	 * Get the parameters table.
	 * 
	 * @param params
	 *            The parameters.
	 * @return The parameters table.
	 */
	private ParamsTable getParameterTable(Object params) {
		ParamsTable paramsTable = null;

		if (params instanceof ParamsTable)
			return (ParamsTable) params;

		Class<? extends Object> paramsClazz = params.getClass();
		Field[] paramsFields = paramsClazz.getDeclaredFields();
		paramsTable = new ParamsTable();

		for (int i = 0; i < paramsFields.length; i++) {
			try {
				String paramsName = paramsFields[i].getName();
				String paramsValue = (String) PropertyUtils.getProperty(params, paramsName);
				paramsTable.setParameter(paramsName, paramsValue);
			} catch (Exception ex) {
				continue;
			}
		}

		return paramsTable;
	}

	public String createOrderBy(ParamsTable params) {
		// If the paramter is null, return the "";
		if (params == null)
			return "";

		String orderby = getOrderField(params);
		String desc = getOrderDirection(params);
		String[] orderlist = StringUtil.split(orderby, ';');
		StringBuffer rtn = new StringBuffer();

		// loop the orderlist & create the statement.;
		if (orderlist != null && orderlist.length > 0) {
			for (int i = 0; i < orderlist.length; i++) {
				// Ingore if the field name is not validity.
				rtn.append(orderlist[i]);
				if (!StringUtil.isBlank(desc)) {
					rtn.append(" " + desc);
				}
				rtn.append(",");
			}
			rtn = rtn.deleteCharAt(rtn.lastIndexOf(","));
		}
		if (StringUtil.isBlank(rtn.toString())) {
			rtn = rtn.append(" id");
		}
		return rtn.toString();
	}

	/**
	 * @param classname
	 *            String
	 * @param params
	 *            Object
	 * @return the Order By statement
	 * @see SQLUtils#createOrderBy(String,
	 *      Object)
	 */
	public String createOrderBy(String classname, Object params) {
		// If the paramter is null, return the "";
		if (params == null)
			return "";

		Class<?> cls = null;
		try {
			cls = Class.forName(classname);
		} catch (Exception ex) {
			return "";
		}

		String orderby = getOrderField(params);
		String desc = getOrderDirection(params);
		String[] orderlist = StringUtil.split(orderby, ';');

		ParamsTable orderbyParams = new ParamsTable();

		Collection<String> values = new ArrayList<String>();
		if (orderlist != null) {
			outer: for (int i = 0; i < orderlist.length; i++) {
				String orderbyFiled = orderlist[i].trim();
				String fieldName = orderbyFiled;
				if (fieldName.indexOf(" ") != -1) {
					fieldName = fieldName.substring(0, fieldName.indexOf(" "));
				}
				Class<?> currentClass = cls;
				while (currentClass != null) {
					try {
						currentClass.getDeclaredField(fieldName);
						values.add(orderbyFiled);
						continue outer;
					} catch (NoSuchFieldException e) {
					}
					currentClass = currentClass.getSuperclass();
				}
			}
		}
		String[] vals = values.toArray(new String[values.size()]);
		orderbyParams.setParameter("_orderby", StringUtil.unite(vals, ";"));
		if (!StringUtil.isBlank(desc)) {
			orderbyParams.setParameter("_desc", desc);
		}

		return createOrderBy(orderbyParams);
	}

	/**
	 * @param params
	 * @return the Order By Direction statement
	 */
	private String getOrderDirection(Object params) {
		String desc = "";
		try {
			if (params instanceof ParamsTable) {
				desc = ((ParamsTable) params).getParameterAsString("_desc");
			} else {
				desc = (String) PropertyUtils.getProperty(params, "_desc");
			}

			desc = (desc == null || desc.trim().length() == 0 ? "" : "desc");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return desc;
	}

	/**
	 * @param params
	 * @return the Order By Field statement
	 */
	private String getOrderField(Object params) {
		String orderby = null;
		try {
			if (params instanceof ParamsTable) {
				orderby = ((ParamsTable) params).getParameterAsText("_orderby", ";");
			}
			if (orderby == null)
				orderby = "";
		} catch (Exception ex) {
			ex.printStackTrace();
			orderby = "";
		}
		return orderby.endsWith(";") ? orderby.substring(0, orderby.length() - 1) : orderby;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see SQLUtils#appendCondition(java.lang.String,
	 *      java.lang.String)
	 */
	public String appendCondition(String sql, String condition) {
		String newSQL = sql.toLowerCase();

		int index;
		if ((index = newSQL.indexOf(" where ")) >= 0) {
			// Append after the "where" direct if it has "where" statement
			// already.
			newSQL = sql.substring(0, index + 7) + " (" + condition + ") and " + sql.substring(index + 7);
		} else if ((index = newSQL.indexOf(" order by ")) >= 0) {
			// Append before the "order" if it has no "where" but "order"
			// statement.
			newSQL = sql.substring(0, index) + " where (" + condition + ") " + sql.substring(index);
		} else {
			// Append in the end if it has no "where" or "order".
			newSQL = sql + " where (" + condition + ") ";
		}
		return newSQL;
	}

	public String appendConditionToLast(String sql, String condition) {
		String newSQL = sql.toLowerCase();
		int endIndex = getSubSelectEndIndex(newSQL);
		String start = newSQL.substring(0, endIndex);
		String end = endIndex < newSQL.length() ? newSQL.substring(endIndex) : "";
		int index;
		if ((index = end.indexOf(" where ")) >= 0) {
			// 存在where
			newSQL = start + end.substring(0, index + 7) + " (" + condition + ") and " + end.substring(index + 7);
		} else if ((index = end.indexOf(" order by ")) >= 0) {
			// 没有where但有order by
			newSQL = start + end.substring(0, index) + " where (" + condition + ") " + end.substring(index);
		} else {
			// 不存在where
			newSQL = start + end + " where (" + condition + ") ";
		}

		return newSQL;
	}

	/**
	 * 
	 * @param sql
	 *            查询语句
	 * @return 如果主查询语句中存在WHERE，则返回WHERE子句的开始位置，否则返回最后位置
	 */
	public int getSubSelectEndIndex(String sql) {
		String newSQL = sql.toLowerCase();
		int fromIndex = getFromIndex(newSQL);
		int where = newSQL.indexOf(" where ");
		// int orderby = newSQL.indexOf(" order by ");
		char[] chars = sql.toCharArray();
		boolean flag = false;
		Stack<Character> stack = new Stack<Character>();
		int index = -1;
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < chars.length; i++) {
			if (flag == true && chars[i] != ' ') {
				buffer.append(chars[i]);
			}
			// 从主查询语句的FORM位置之后开始判断
			if (i < fromIndex)
				continue;
			// 如果左括号
			if (chars[i] == '(') {
				stack.push('(');
				if (stack.size() == 1) {
					index = i;
					if (where > 0 && where < index) {// 如果主查询语句中存在WHERE
						return where;
					}
					flag = true;
				} else if (stack.size() == 0) {
					index = i;
					if (where > 0 && where < index) {// 发现取到的是子查询的WHERE，继续查找WHERE
						where = newSQL.indexOf(" where ", where + 6);
					}
					flag = false;
				} else {
					flag = false;
				}
			} else if (chars[i] == ')') {// 如果为右括号
				stack.pop();
				if (stack.size() == 1) {
					index = i;
					if (where > 0 && where < index) {// 如果主查询语句中存在WHERE
						return where;
					}
					flag = true;
				} else if (stack.size() == 0) {
					index = i;
					if (where > 0 && where < index) {// 发现取到的是子查询的WHERE，继续查找WHERE
						where = newSQL.indexOf(" where ", where + 6);
					}
					flag = false;
				} else {
					flag = false;
				}
			}
		}
		return index + 1;
	}

	/**
	 * 
	 * @param sql
	 *            查询语句
	 * @return 返回主查询语句的FROM位置
	 */
	public int getFromIndex(String sql) {
		String newSQL = sql.toLowerCase();
		StringBuffer rtnSQL = new StringBuffer();

		int fromIndex = newSQL.indexOf(" from");
		while (fromIndex > 0) {
			String start = newSQL.substring(0, fromIndex);
			String end = newSQL.substring(fromIndex);
			if (isWhenCase(start)) {
				int index = end.indexOf(" end") + 4;
				rtnSQL.append(start + end.substring(0, index));
				newSQL = end.substring(index);
			} else {
				rtnSQL.append(start);
				break;
			}
			fromIndex = newSQL.indexOf(" from");
		}

		return rtnSQL.length();
	}

	/**
	 * 是否存在WHEN...Case...END语句
	 * 
	 * @param sql
	 *            查询语句
	 * @return 如果是返回true,否则返回false
	 */
	public boolean isWhenCase(String sql) {
		int when = sql.indexOf("when ");
		if (when >= 0) {
			return sql.indexOf("case") > 0;
		}
		return false;
	}

	/**
	 * 是否子查询语句
	 * 
	 * @param sql
	 *            查询语句
	 * @return 如果是返回true,否则返回false
	 */
	public boolean isSubSelect(String sql) {
		int startIndex = sql.indexOf("(");
		StringBuffer buffer = new StringBuffer();
		if (startIndex > 0) {
			String str = sql.substring(startIndex + 1);
			boolean flag = false;
			for (int i = 0; i < str.toCharArray().length; i++) {
				char ch = str.toCharArray()[i];
				if (ch != ' ' || flag) { // 从第一个非空字符开始加载
					buffer.append(ch);
					flag = true;
				}

				if (buffer.length() == 6) {
					break;
				}
			}

			if ("SELECT".equalsIgnoreCase(buffer.toString())) {
				return true;
			}
		}

		return false;
	}

	public int getSubSelectStartIndex(String sql) {
		char[] chars = sql.toCharArray();
		boolean flag = false;
		Stack<Character> stack = new Stack<Character>();

		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < chars.length; i++) {
			if (flag == true && chars[i] != ' ') {
				buffer.append(chars[i]);
			}

			if ("SELECT".equalsIgnoreCase(buffer.toString())) {
				return i - 6;
			}

			// 如果为最外层的括号
			if (chars[i] == '(') {
				stack.push('(');
			}

			if (chars[i] == ')') {
				stack.pop();
			}

			flag = stack.size() == 1;
		}

		return -1;
	}

	public static void main(String[] args) throws Exception {
		HibernateSQLUtils utils = new HibernateSQLUtils();

		// String temp =
		// " (tlk_testsuggest)a,(select * from test where aa=bb)t where id in
		// (select id from tlk_testsuggest)";
		// log.info("SQL1: " + " " + temp.substring(0,
		// utils.getSubSelectEndIndex(temp)));
		// 子查询1
		// String sql = "select * from (select * from tlk_项目计划视图 t1 union all
		// select * from tlk_项目阶段计划视图 t2 union all select * from tlk_任务分解发布视图
		// t3) t where '11df-66d6-9562hec2-af6a-575bd4004d59' in (select
		// item_项目成员 from tlk_项目成员 a where a.parent in (select b.id from
		// tlk_项目初始化 b where (b.item_code=t.item_code or item_code=t.item_pcode
		// or item_code in (select item_pcode from tlk_项目阶段计划视图 where item_code
		// = t.item_pcode))))";
		String sql = "SELECT a.*,(when t.a==1 case select b from tttt end) as bb, (when t.a case 2 select b from tttt case 3 select c from ttt end) as cc FROM (tlk_testsuggest)a,(select * from test where aaattt= sfd )t WHERE id IN (SELECT id FROM tlk_testsuggest)";
		log.info("SQL1: " + "  " + utils.appendConditionToLast(sql, "ITEM_pcode= '' or ITEM_pcode is null"));
		// // 子查询2
		// String sql2 = "SELECT * FROM (SELECT * FROM tlk_testsuggest) doc";
		// log.info("SQL2: " + utils.appendConditionToLast(sql2,
		// "parent='parentid'"));
		// sql2 =
		// "SELECT * FROM (SELECT * FROM tlk_testsuggest) doc WHERE (id='id')";
		// // 出错
		// log.info("SQL2-1出错: " + utils.appendConditionToLast(sql2,
		// "parent='parentid'"));
		// sql2 =
		// "SELECT * FROM (SELECT * FROM tlk_testsuggest) doc WHERE id='id'"; //
		// 正常
		// log.info("SQL2-2正常: " + utils.appendConditionToLast(sql2,
		// "parent='parentid'"));
		// // 普通
		// String sql3 = "SELECT * FROM tlk_testsuggest doc";
		// log.info("SQL3: "+ utils.appendConditionToLast(sql3,
		// "parent='parentid'"));
		// sql3 = "SELECT * FROM tlk_testsuggest doc WHERE id='id'";
		// log.info("SQL3-1: " + utils.appendConditionToLast(sql3,
		// "parent='parentid'"));
		// sql3 =
		// "SELECT * FROM tlk_testsuggest doc WHERE id='id' AND name='name'";
		// log.info("SQL3-2: " + utils.appendConditionToLast(sql3,
		// "parent='parentid'"));
	}
}
