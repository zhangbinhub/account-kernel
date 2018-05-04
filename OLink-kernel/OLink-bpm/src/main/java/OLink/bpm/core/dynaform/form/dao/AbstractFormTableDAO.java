package OLink.bpm.core.dynaform.form.dao;

import java.sql.Connection;
import java.util.Collection;

import OLink.bpm.core.table.ddlutil.AbstractValidator;
import OLink.bpm.core.table.model.Table;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.dynaform.document.dql.DQLASTUtil;
import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSource;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.table.alteration.AddTableChange;
import OLink.bpm.core.table.ddlutil.AbstractTableDefinition;
import OLink.bpm.core.table.ddlutil.ChangeLog;
import org.apache.log4j.Logger;

/**
 * 
 * @author Nicholas
 * 
 */
public abstract class AbstractFormTableDAO {
	protected static Logger LOG = Logger.getLogger(AbstractFormTableDAO.class);

	public Connection conn;

	protected String schema;

	public AbstractFormTableDAO(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 创建动态表
	 * 
	 * @param vo
	 *            表单值对象
	 * @param conn
	 *            JDBC Connection
	 * @throws Exception
	 */
	public void createDynaTable(Form newForm) throws Exception {
		ChangeLog log = new ChangeLog();
		log.compare(newForm, null);

		getTableDefinition().processChanges(log);
		if (newForm != null && newForm.isShowLog()) {
			createDynaTableByType(newForm, DQLASTUtil.TABEL_TYPE_LOG);
		}

		if (!isDynaTableExists(newForm, DQLASTUtil.TABLE_TYPE_AUTH)) {
			createAuthDynaTable(newForm);
		}
	}

	/**
	 * 创建权限动态表
	 * 
	 * @param form
	 *            表单
	 * @throws Exception
	 */
	public void createAuthDynaTable(Form form) throws Exception {
		ChangeLog log = new ChangeLog();
		Table authTable = log.createTableByType(form,
				DQLASTUtil.TABLE_TYPE_AUTH);
		AbstractTableDefinition tableDefinition = getTableDefinition();

		tableDefinition.getSQLBuilder().createTable(authTable, false);
		String sql = tableDefinition.getSQLBuilder().getSQL();
		tableDefinition.evaluateBatch(sql, false);
	}

	/**
	 * 创建不同类型的动态表
	 * 
	 * @param newForm
	 * @param tableType
	 * @throws Exception
	 */
	public void createDynaTableByType(Form newForm, int tableType)
			throws Exception {
		ChangeLog log = new ChangeLog();
		log.compare(newForm, null, tableType);

		getTableDefinition().processChanges(log);
	}

	/**
	 * 2.6新增
	 * 
	 * @param newForm
	 * @param tableType
	 * @param dt
	 * @throws Exception
	 */
	public void createDynaTableByType(Form newForm, int tableType, DataSource dt)
			throws Exception {
		ChangeLog log = new ChangeLog();
		log.compare(newForm, null, tableType, dt);

		getTableDefinition().processChanges(log);
	}

	/**
	 * 更新动态表
	 * 
	 * @param vo
	 *            表单值对象
	 * @param conn
	 *            JDBC Connection
	 * @throws Exception
	 */
	public void updateDynaTable(Form newForm, Form oldForm) throws Exception {
		ChangeLog log = new ChangeLog();
		log.compare(newForm, oldForm);
		// use factory method to get TableDefinition

		getTableDefinition().processChanges(log);
		if (newForm != null) {
			if (newForm.isShowLog()) {
				if (!isDynaTableExists(newForm, DQLASTUtil.TABEL_TYPE_LOG)) {
					createDynaTableByType(newForm, DQLASTUtil.TABEL_TYPE_LOG);
				} else {
					updateDynaTableByType(newForm, oldForm,
							DQLASTUtil.TABEL_TYPE_LOG);
				}
			} else {
				if (isDynaTableExists(oldForm, DQLASTUtil.TABEL_TYPE_LOG)) {
					dropDynaTableByType(oldForm, DQLASTUtil.TABEL_TYPE_LOG);
				}
			}

			if (isDynaTableExists(oldForm, DQLASTUtil.TABLE_TYPE_AUTH)) {
				updateDynaTableByType(newForm, oldForm,
						DQLASTUtil.TABLE_TYPE_AUTH);
			} else if (!isDynaTableExists(newForm, DQLASTUtil.TABLE_TYPE_AUTH)) {
				createAuthDynaTable(newForm);
			}
		}
	}

	/**
	 * 2.6新增
	 * 
	 * @param newForm
	 * @param oldForm
	 * @param dt
	 * @throws Exception
	 */
	public void updateDynaTable(Form newForm, Form oldForm, DataSource dt)
			throws Exception {
		ChangeLog log = new ChangeLog();
		log.compare(newForm, oldForm, dt);
		// use factory method to get TableDefinition

		getTableDefinition().processChanges(log);
		if (newForm != null) {
			if (newForm.isShowLog()) {
				if (!isDynaTableExists(newForm, DQLASTUtil.TABEL_TYPE_LOG)) {
					createDynaTableByType(newForm, DQLASTUtil.TABEL_TYPE_LOG,
							dt);
				} else {
					updateDynaTableByType(newForm, oldForm,
							DQLASTUtil.TABEL_TYPE_LOG, dt);
				}
			} else {
				if (isDynaTableExists(oldForm, DQLASTUtil.TABEL_TYPE_LOG)) {
					dropDynaTableByType(oldForm, DQLASTUtil.TABEL_TYPE_LOG, dt);
				}
			}

			if (isDynaTableExists(oldForm, DQLASTUtil.TABLE_TYPE_AUTH)) {
				updateDynaTableByType(newForm, oldForm,
						DQLASTUtil.TABLE_TYPE_AUTH, dt);
			} else if (!isDynaTableExists(newForm, DQLASTUtil.TABLE_TYPE_AUTH)) {
				createAuthDynaTable(newForm);
			}
		}
	}

	/**
	 * 更新不同类型的动态表
	 * 
	 * 
	 * @param vo
	 *            表单值对象
	 * @param conn
	 *            JDBC Connection
	 * @throws Exception
	 */
	public void updateDynaTableByType(Form newForm, Form oldForm)
			throws Exception {
		updateDynaTableByType(newForm, oldForm, DQLASTUtil.TABEL_TYPE_CONTENT);
	}

	/**
	 * 2.6新增
	 * 
	 * @param newForm
	 * @param oldForm
	 * @param dt
	 * @throws Exception
	 */
	public void updateDynaTableByType(Form newForm, Form oldForm, DataSource dt)
			throws Exception {
		updateDynaTableByType(newForm, oldForm, DQLASTUtil.TABEL_TYPE_CONTENT,
				dt);
	}

	public void updateDynaTableByType(Form newForm, Form oldForm, int tabelType)
			throws Exception {
		ChangeLog log = new ChangeLog();
		log.compare(newForm, oldForm, tabelType);
		getTableDefinition().processChanges(log);
	}

	/**
	 * 2.6新增
	 * 
	 * @param newForm
	 * @param oldForm
	 * @param tabelType
	 * @param dt
	 * @throws Exception
	 */
	public void updateDynaTableByType(Form newForm, Form oldForm,
			int tabelType, DataSource dt) throws Exception {
		ChangeLog log = new ChangeLog();
		log.compare(newForm, oldForm, tabelType, dt);
		getTableDefinition().processChanges(log);
	}

	public void createOrUpdateDynaTable(Form newForm, Form oldForm)
			throws Exception {
		// 动态表不存在
		if (oldForm != null && isDynaTableExists(oldForm)) {
			// DT与RT不同步的情况
			updateDynaTable(newForm, oldForm);
		} else if (newForm != null && !isDynaTableExists(newForm)) {
			createDynaTable(newForm);
		}
	}

	/**
	 * 删除动态表
	 * 
	 * @param id
	 *            表单ID
	 * @param conn
	 *            JDBC Connection
	 * @throws Exception
	 */
	public void dropDynaTable(Form oldForm) throws Exception {
		ChangeLog log = new ChangeLog();
		log.compare(null, oldForm);

		getTableDefinition().processChanges(log);
		if (oldForm != null && oldForm.isShowLog()) {
			dropDynaTableByType(oldForm, DQLASTUtil.TABEL_TYPE_LOG);
		}
		dropDynaTableByType(oldForm, DQLASTUtil.TABLE_TYPE_AUTH);
	}

	/**
	 * 2.6新增
	 * 
	 * @param oldForm
	 * @param dt
	 * @throws Exception
	 */
	public void dropDynaTable(Form oldForm, DataSource dt) throws Exception {
		ChangeLog log = new ChangeLog();
		log.compare(null, oldForm, dt);

		getTableDefinition().processChanges(log);
		if (oldForm != null && oldForm.isShowLog()) {
			dropDynaTableByType(oldForm, DQLASTUtil.TABEL_TYPE_LOG, dt);
		}
		dropDynaTableByType(oldForm, DQLASTUtil.TABLE_TYPE_AUTH, dt);
	}

	/**
	 * 删除不同类型的动态表,比如前缀为log_表
	 * 
	 * @param id
	 *            表单ID
	 * @param conn
	 *            JDBC Connection
	 * @throws Exception
	 */
	public void dropDynaTableByType(Form oldForm, int tableType)
			throws Exception {
		ChangeLog log = new ChangeLog();
		log.compare(null, oldForm, tableType);

		getTableDefinition().processChanges(log);

	}

	/**
	 * 2.6新增
	 * 
	 * @param oldForm
	 * @param tableType
	 * @throws Exception
	 */
	public void dropDynaTableByType(Form oldForm, int tableType, DataSource dt)
			throws Exception {
		ChangeLog log = new ChangeLog();
		log.compare(null, oldForm, tableType, dt);

		getTableDefinition().processChanges(log);

	}

	public boolean isDynaTableExists(Form form) throws Exception {
		return isDynaTableExists(form, DQLASTUtil.TABEL_TYPE_CONTENT);
	}

	/**
	 * 判断动态表是否存在
	 * 
	 * @param form
	 *            表单
	 * @param tableType
	 *            动态表类型
	 * @return 存在返回true,否则返回false
	 * @throws Exception
	 */
	public boolean isDynaTableExists(Form form, int tableType) throws Exception {
		try {
			ChangeLog log = new ChangeLog();
			Table table = log.createTableByType(form, tableType);
			AbstractValidator validator = getValidator();
			// 新建一个Add Table Change,检验动态表是否已存在
			validator.checkChange(new AddTableChange(table));
			Collection<?> confirms = validator.getConfirms();
			// 没有任何信息则认为动态表不存在
			if (confirms.size() > 0) {
				return true;
			}
		} catch (Exception e) {
			throw e;
		}

		return false;
	}

	/**
	 * 表单改动校验
	 * 
	 * @param log
	 * @throws Exception
	 */
	public void changeValidate(ChangeLog log) throws Exception {
		getValidator().checkChanges(log);
	}

	public void synchronizeDynaTable(Form formVO) throws Exception {
		if (!isDynaTableExists(formVO)) {
			createDynaTable(formVO);
		}
	}

	public ValueObject find(String id) throws Exception {
		throw new UnsupportedOperationException();
	}

	public void create(ValueObject vo) throws Exception {
		throw new UnsupportedOperationException();
	}

	public void remove(String pk) throws Exception {
		throw new UnsupportedOperationException();
	}

	public void update(ValueObject vo) throws Exception {
		throw new UnsupportedOperationException();
	}

	public abstract AbstractValidator getValidator();

	public abstract AbstractTableDefinition getTableDefinition();
}
