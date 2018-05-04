package OLink.bpm.core.report.crossreport.runtime.action;

import java.util.Iterator;
import java.util.List;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.department.action.DepartmentHelper;
import OLink.bpm.core.user.action.WebUser;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONArray;
import OLink.bpm.core.role.action.RoleHelper;
import OLink.bpm.util.DbTypeUtil;
import OLink.bpm.util.StringUtil;

public class ReportConditionParser {

	public static final String TYPE_TEXT = "00";

	public static final String TYPE_NUMBER = "01";

	public static final String TYPE_DATE = "02";

	public static final String TYPE_SEARCH_FORM = "03";

	public static final String TYPE_SYSTEM_VAR = "04";

	public static final String ITEM_PREFIX = "ITEM_";

	/**
	 * 系统变量：当前在线用户的帐号
	 */
	public static final String SYSVAR_CURRACC = "curracc";

	/**
	 * 系统变量：当前在线用户的部门
	 */
	public static final String SYSVAR_CURRDEPT = "currdept";

	/**
	 * 系统变量：当前在线用户的部门ID
	 */
	public static final String SYSVAR_CURRDEPTID = "currdeptid";

	/**
	 * 系统变量：当前在线用户的角色
	 */
	public static final String SYSVAR_CURRROLE = "currrole";

	/**
	 * 系统变量：当前在线用户的角色ID
	 */
	public static final String SYSVAR_CURRROLEID = "currroleid";

	/**
	 * 系统变量：当前在线用户的名称
	 */
	public static final String SYSVAR_CURRNAME = "currname";

	/**
	 * 系统变量：当前在线用户的ID
	 */
	public static final String SYSVAR_CURRUSERID = "curruserid";
	
	/**
	 * 系统变量：当前会话
	 */
	public static final String SYSVAR_SESSION = "currsession";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}

	public static String parserToDQL(String condition) {
		return null;
	}

	public static String parserToHQL(String condition) {
		return null;
	}

	/**
	 * 根据参数将目标转化为SQL内容
	 * 
	 * @param condition
	 * @param params
	 * @return
	 */
	public static String parseToSQL(String condition, ParamsTable params, WebUser user) throws Exception {
		//FormProcess formPrcoess = (FormProcess) ProcessFactory.createProcess(FormProcess.class);

		JSONArray array = JSONArray.fromObject(condition);
		List<?> list = (List<?>) JSONArray.toCollection(array);

		String applicationid = params.getParameterAsString("application");
		StringBuffer sql = new StringBuffer();
		for (Iterator<?> iter = list.iterator(); iter.hasNext();) {
			MorphDynaBean bean = (MorphDynaBean) iter.next();
			String matchValue = null;

			String type = (String) bean.get("type");
			String match = (String) bean.get("match");
			String field = (String) bean.get("field");
			String operator = (String) bean.get("operator");
			matchValue = match;


			if (StringUtil.isBlank(matchValue)) {
				continue;
			}

			if (field.startsWith("$")) {
				if (field.indexOf("LastModified") >= 0) {
					sql.append(" AND ");
					sql
							.append(DbTypeUtil.getSQLFunction(applicationid).toChar("d." + field.substring(1),
									"yyyy-MM-dd"));
				} else {
					sql.append(" AND d.").append(field.substring(1));
				}
			} else {

					sql.append(" AND ").append("ITEM_"+field);
			}
			sql.append(" ").append(operator).append(" ");

			// 判断操作符是不是IN或NOT IN，是则加括号
			if (operator.equals("IN") || operator.equals("NOT IN"))
				sql.append("(");

			if (!type.equals(TYPE_DATE) && !type.equals(TYPE_SEARCH_FORM)) {
				if (operator.equals("LIKE")) {
					sql.append("'%");
				} else {
					sql.append("'");
				}
			}

			if (type.equals(TYPE_TEXT) || type.equals(TYPE_NUMBER)) {
				sql.append(match);
			}else if (type.equals(TYPE_DATE)) {
				sql.append(DbTypeUtil.getSQLFunction(applicationid).toDate("'" + match + "'", "yyyy-MM-dd"));
			} else if (type.equals(TYPE_SYSTEM_VAR)) {
				String sysVar = match;
				if (!StringUtil.isBlank(sysVar)) {
					if (sysVar.equals(SYSVAR_CURRACC)) {
						sql.append(user.getLoginno());
					} else if (sysVar.equals(SYSVAR_CURRNAME)) {
						sql.append(user.getName());
					} else if (sysVar.equals(SYSVAR_CURRDEPT)) {
						if (user.getDepartments().size() > 0) {

							DepartmentHelper dhelper = new DepartmentHelper();
							String deptString = dhelper.queryDeptNamesByIdString(user.getDeptlist().substring(1,
									user.getDeptlist().length() - 1));
							sql.append(deptString);
						}
					} else if (sysVar.equals(SYSVAR_CURRROLE)) {
						if (user.getDepartments().size() > 0) {

							RoleHelper rhelper = new RoleHelper();
							String roleString = rhelper.queryRoleNamesByIds(user.getRolelist().substring(1,
									user.getRolelist().length() - 1));
							sql.append(roleString);
						}
					} else if (sysVar.equals(SYSVAR_CURRROLEID)) {
						if (user.getDepartments().size() > 0) {
							sql.append(user.getRolelist().substring(1, user.getRolelist().length() - 1));
						}
					} else if (sysVar.equals(SYSVAR_CURRUSERID)) {
						sql.append(user.getId());
					} else if (sysVar.equals(SYSVAR_CURRDEPTID)) {
						if (user.getDepartments().size() > 0) {
							sql.append(user.getDeptlist().substring(1, user.getDeptlist().length() - 1));
						}
					} else if (sysVar.equals(SYSVAR_SESSION)) {
						String sessionPropName = (String)bean.get("sessionfield");
						if (!StringUtil.isBlank(sessionPropName)) {
							sql.append(params.getHttpRequest().getSession().getAttribute(sessionPropName));
						}
					}
				}
			}

			if (!type.equals(TYPE_DATE) && !type.equals(TYPE_SEARCH_FORM)) {
				if (operator.equals("LIKE")) {
					sql.append("%'");
				} else {
					sql.append("'");
				}
			}

			// 判断操作符是不是IN或NOT IN，是则加括号
			if (operator.equals("IN") || operator.equals("NOT IN"))
				sql.append(")");
		}
		return sql.toString();
	}

}
