package OLink.bpm.core.dynaform.view.ejb.editmode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.HibernateSQLUtils;
import OLink.bpm.core.dynaform.document.ejb.DocumentProcess;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.dynaform.view.ejb.condition.Condition;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.dynaform.document.dql.DQLASTUtil;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.StringUtil;

/**
 * 抽象查询模式(大部分方法在SQL与Design之间共用)
 * 
 * @author nicholas zhen
 * 
 */
public abstract class AbstractEditMode {
	protected View view;

	protected Collection<Condition> conditions = new ArrayList<Condition>();

	public AbstractEditMode(View view) {
		this.view = view;
	}

	/**
	 * 运行脚本
	 * 
	 * @param runner
	 *            脚本执行器
	 * @param label
	 *            标签
	 * @param script
	 *            脚本
	 * @return
	 * @throws Exception
	 */
	public String runScript(IRunner runner, String label, String script) throws Exception {
		Object result = runner.run(label.toString(), script);
		if (result != null && result instanceof String) {
			return (String) result;
		}

		return "";
	}

	/**
	 * 获取查询语句
	 * 
	 * @param params
	 *            参数
	 * @param user
	 *            当前用户
	 * @param sDoc
	 *            查询文档
	 * @return 查询语句
	 * @throws Exception
	 */
	public abstract String getQueryString(ParamsTable params, WebUser user, Document sDoc) throws Exception;

	/**
	 * 获取文档数据包(默认为SQL查询)
	 * 
	 * @param params
	 *            参数
	 * @param user
	 *            当前用户
	 * @param sDoc
	 *            查询文档
	 * @return 文档数据包
	 * @throws Exception
	 */
	public DataPackage<Document> getDataPackage(ParamsTable params, WebUser user, Document sDoc) throws Exception {
		DocumentProcess dp = (DocumentProcess) ProcessFactory.createRuntimeProcess(DocumentProcess.class, view
				.getApplicationid());
		String sql = appendCondition(getQueryString(params, user, sDoc));

		return dp.queryBySQL(sql, params, user.getDomainid());
	}

	/**
	 * 获取文档数据包(默认为SQL查询)
	 * 
	 * @param params
	 *            参数
	 * @param page
	 *            当前页码
	 * @param lines
	 *            每页显示的行数
	 * @param user
	 *            当前用户
	 * @param sDoc
	 *            查询文档
	 * @return 文档数据包
	 * @throws Exception
	 */
	public DataPackage<Document> getDataPackage(ParamsTable params, int page, int lines, WebUser user, Document sDoc)
			throws Exception {
		DocumentProcess dp = (DocumentProcess) ProcessFactory.createRuntimeProcess(DocumentProcess.class, view
				.getApplicationid());
		String sql = appendCondition(getQueryString(params, user, sDoc));

		return dp.queryBySQLPage(sql, params, page, lines, user.getDomainid());
	}

	/**
	 * 获取文档总行数(默认为SQL查询)
	 * 
	 * @param params
	 *            参数
	 * @param user
	 *            当前用户
	 * @param sDoc
	 *            查询文档
	 * @return 文档总行数
	 * @throws Exception
	 */
	public long count(ParamsTable params, WebUser user, Document sDoc) throws Exception {
		DocumentProcess dp = (DocumentProcess) ProcessFactory.createRuntimeProcess(DocumentProcess.class, view
				.getApplicationid());
		String sql = appendCondition(getQueryString(params, user, sDoc));

		return dp.countBySQL(sql, user.getDomainid());
	}

	/**
	 * 添加查询条件
	 * 
	 * @param name
	 *            名称
	 * @param val
	 *            值
	 */
	public void addCondition(String name, String val) {
		try {
			addCondition(name, val, "=");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 添加查询条件
	 * 
	 * @param name
	 *            名称
	 * @param val
	 *            值
	 */
	public void addCondition(String name, String val, String operator) {
		try {
			name = name.startsWith("$") ? name.substring(1) : name; // 处理$符号
			if (isSystemField(name)) {

				conditions.add(new Condition(name, val, operator));
			} else {
				addItemCondition(name, val, operator);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void addItemCondition(String name, String val) {
		addItemCondition(name, val, "=");
	}

	protected void addItemCondition(String name, String val, String operator) {
		try {
			FormProcess formProcess = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
			Form form = (Form) formProcess.doView(view.getRelatedForm());

			if (form != null) {
				conditions.add(new Condition(form.getTableMapping().getColumnName(name), val, operator));
			} else {
				conditions.add(new Condition("ITEM_" + name, val, operator));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 是否为系统字段
	 * 
	 * @param name
	 *            字段名称
	 * @return
	 */
	public boolean isSystemField(String name) {
		for (Iterator<String> iterator = DQLASTUtil.SYSTEM_FIELDS.iterator(); iterator.hasNext();) {
			String systemField = iterator.next();
			if (systemField.equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 为查询语句添加查询条件
	 * 
	 * @param queryString
	 * @return
	 */
	protected String appendCondition(String queryString) {
		String rtn = queryString;
		if (!conditions.isEmpty()) {
			HibernateSQLUtils sqlUtils = new HibernateSQLUtils();
			for (Iterator<Condition> iterator = conditions.iterator(); iterator.hasNext();) {
				Condition condition = iterator.next();

				if (StringUtil.isBlank(condition.getVal())) {
					rtn = sqlUtils.appendConditionToLast(rtn, condition.getName() + "= '' " + " or "
							+ condition.getName() + " is null");
				} else {
					rtn = sqlUtils.appendConditionToLast(rtn, condition.getName() + " " + condition.getOperator()
							+ " '" + condition.getVal() + "'");
				}
			}
		}

		return rtn;
	}
}
