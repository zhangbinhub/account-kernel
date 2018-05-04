package OLink.bpm.core.dynaform.view.ejb.condition;

import java.util.Iterator;
import java.util.List;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.department.action.DepartmentHelper;
import OLink.bpm.core.dynaform.document.ejb.Item;
import OLink.bpm.core.dynaform.form.ejb.DateField;
import OLink.bpm.core.dynaform.form.ejb.FormField;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.dynaform.form.ejb.mapping.TableMapping;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.dynaform.view.ejb.ViewProcess;
import OLink.bpm.core.role.action.RoleHelper;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.user.ejb.UserProcess;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.util.StringUtil;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONArray;

public class FilterConditionParser {

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

	ConditionVisitor visitor;
	ParamsTable params;
	WebUser user;
	View view;

	public FilterConditionParser(ParamsTable params, WebUser user, View view) {
		this(params, user, view, new DefaultConditionVisitor(view.getApplicationid()));
	}

	public FilterConditionParser(ParamsTable params, WebUser user, View view, ConditionVisitor visitor) {
		this.visitor = visitor;
		this.params = params;
		this.user = user;
		this.view = view;
	}

	/**
	 * 根据参数将目标转化为SQL内容
	 * 
	 * @SuppressWarnings JSONArray.toCollection 方法不支持泛型
	 * @param condition
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String parseToSQL(String condition) throws Exception {
		FormProcess formPrcoess = (FormProcess) ProcessFactory.createProcess(FormProcess.class);

		JSONArray array = JSONArray.fromObject(condition);
		List<MorphDynaBean> list = (List<MorphDynaBean>) JSONArray.toCollection(array);

		TableMapping tableMapping = null;
		if (view != null) {
			String relatedFormId = view.getRelatedForm();
			Form relatedForm = (Form) formPrcoess.doView(relatedFormId);
			tableMapping = relatedForm.getTableMapping();
		}

		for (Iterator<MorphDynaBean> iter = list.iterator(); iter.hasNext();) {
			MorphDynaBean bean = iter.next();

			String column = (String) bean.get("field");
			String type = (String) bean.get("type");
			String match = StringUtil.dencodeHTML((String) bean.get("match"));
			String operator = (String) bean.get("operator");

			visitor.visitColumnType(type);
			if (column.startsWith("$")) {
				visitor.visitColumn("d." + column.substring(1));
			} else {
				if (tableMapping != null) {
					String columnName = tableMapping.getColumnName(column);
					visitor.visitColumn(columnName);
				}
			}
			visitor.visitOperator(operator);

			if (type.equals(TYPE_TEXT)) {
				visitor.visitStringValue(match);
			} else if (type.equals(TYPE_NUMBER)) {
				visitor.visitNumberValue(match);
			} else if (type.equals(TYPE_DATE)) {
				visitor.visitDateValue(match, "yyyy-MM-dd");
			} else if (type.equals(TYPE_SEARCH_FORM)) {
				Form form = view.getSearchForm();
				if (form != null && form.findFieldByName(match) != null) {
					FormField formField = form.findFieldByName(match);
					String fieldType = formField.getFieldtype();
					if (Item.VALUE_TYPE_VARCHAR.equals(fieldType)) {
						visitor.visitStringValue(params.getParameterAsString(match));
					} else if (Item.VALUE_TYPE_NUMBER.equals(fieldType)) {
						// 可设置数字类型
						visitor.visitNumberValue(params.getParameterAsString(match));
					} else if (Item.VALUE_TYPE_DATE.equals(fieldType)) {
						// 可设置日期类型
						DateField dateField = (DateField) formField;
//						visitor.visitDateValue(params.getParameterAsString(match), dateField.getDatePattern());
						visitor.visitDateValue(params.getParameterAsString(match), dateField.getDatePatternValue());
					} else {
						// 可设置字符类型
						visitor.visitStringValue(params.getParameterAsString(match));
					}
				}
			} else if (type.equals(TYPE_SYSTEM_VAR)) { // 系统变量
				String sysVar = match;
				if (!StringUtil.isBlank(sysVar)) {
					String sysValue = "";
					if (sysVar.equals(SYSVAR_CURRACC)) {
						sysValue = user != null ? user.getLoginno() : "";
					} else if (sysVar.equals(SYSVAR_CURRNAME)) {
						sysValue = user != null ? user.getName() : "";
					} else if (sysVar.equals(SYSVAR_CURRDEPT)) {
						if (user != null && user.getDepartments().size() > 0) {
							DepartmentHelper dhelper = new DepartmentHelper();
							String deptString = dhelper.queryDeptNamesByIdString(user.getDeptlist().substring(1,
									user.getDeptlist().length() - 1));
							sysValue = deptString;
						}
					} else if (sysVar.equals(SYSVAR_CURRROLE)) {
						if (user != null && user.getDepartments().size() > 0) {

							RoleHelper rhelper = new RoleHelper();
							String roleString = rhelper.queryRoleNamesByIds(user.getRolelist().substring(1,
									user.getRolelist().length() - 1));
							sysValue = roleString;
						}
					} else if (sysVar.equals(SYSVAR_CURRROLEID)) {
						if (user != null && user.getDepartments().size() > 0) {
							sysValue = user.getRolelist().substring(1, user.getRolelist().length() - 1);
						}
					} else if (sysVar.equals(SYSVAR_CURRUSERID)) {
						sysValue = user != null ? user.getId() : "";
					} else if (sysVar.equals(SYSVAR_CURRDEPTID)) {
						if (user != null && user.getDepartments().size() > 0) {
							sysValue = user.getDeptlist().substring(1, user.getDeptlist().length() - 1);
						}
					} else if (sysVar.equals(SYSVAR_SESSION)) {
						String sessionAttrName = (String) bean.get("sessionfield");
						if (!StringUtil.isBlank(sessionAttrName) && params != null) {
							sysValue = (String) params.getHttpRequest().getSession().getAttribute(sessionAttrName);
						}
					}

					visitor.visitStringValue(sysValue);
				}
			}
		}
		return visitor.getConditions();
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		ViewProcess viewProcess = (ViewProcess) ProcessFactory.createProcess(ViewProcess.class);
		String viewid = "11de-9c47-2aad607b-a4bc-bdf898597d7d";
		View view = (View) viewProcess.doView(viewid);

		String userid = "11de-c13a-0cf76f8b-a3db-1bc87eaaad4c";
		UserProcess userProcess = (UserProcess) ProcessFactory.createProcess(UserProcess.class);
		UserVO userVO = (UserVO) userProcess.doView(userid);

		ParamsTable params = new ParamsTable();
		WebUser user = new WebUser(userVO);
		FilterConditionParser parser = new FilterConditionParser(params, user, view);
		System.out
				.println(parser
						.parseToSQL("[{field:'字段1',operator:'=',type:'03',match:'字段1'},{field:'字段2',operator:'NOT IN',type:'00',match:'11,33,44'}]"));

	}
}
