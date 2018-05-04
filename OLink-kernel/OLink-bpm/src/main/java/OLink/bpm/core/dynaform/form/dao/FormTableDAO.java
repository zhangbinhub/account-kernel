package OLink.bpm.core.dynaform.form.dao;

import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSource;
import OLink.bpm.core.table.ddlutil.ChangeLog;

public interface FormTableDAO {

	/**
	 * 创建动态表
	 * 
	 * @param vo
	 *            表单值对象
	 * @param conn
	 *            JDBC Connection
	 * @throws Exception
	 */
	void createDynaTable(Form newForm) throws Exception;

	/**
	 * 更新动态表
	 * 
	 * @param vo
	 *            表单值对象
	 * @param conn
	 *            JDBC Connection
	 * @throws Exception
	 */
	void updateDynaTable(Form newForm, Form oldForm) throws Exception;
	
	void updateDynaTable(Form newForm, Form oldForm, DataSource dt) throws Exception;

	/**
	 * 删除动态表
	 * 
	 * @param id
	 *            表单ID
	 * @param conn
	 *            JDBC Connection
	 * @throws Exception
	 */
	void dropDynaTable(Form oldForm) throws Exception;
	
	void dropDynaTable(Form oldForm, DataSource dt) throws Exception;

	boolean isDynaTableExists(Form form) throws Exception;

	void synchronizeDynaTable(Form formVO) throws Exception;

	void createOrUpdateDynaTable(Form newForm, Form oldForm) throws Exception;

	void changeValidate(ChangeLog log) throws Exception;
}
