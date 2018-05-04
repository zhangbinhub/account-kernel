package OLink.bpm.core.dynaform.form.ejb;

import java.sql.Connection;

import OLink.bpm.core.dynaform.form.dao.FormTableDAO;
import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSourceAware;
import OLink.bpm.base.dao.IRuntimeDAO;
import OLink.bpm.base.ejb.AbstractRunTimeProcessBean;
import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSource;
import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSourceProcess;
import OLink.bpm.core.table.ddlutil.ChangeLog;
import OLink.bpm.util.ProcessFactory;
import com.jamonapi.proxy.MonProxyFactory;

import OLink.bpm.util.RuntimeDaoManager;

public class FormTableProcessBean extends AbstractRunTimeProcessBean<Form>
		implements FormTableProcess, DataSourceAware {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1000182515582549093L;

	private String datasourceId;

	public FormTableProcessBean(String applicationId) {
		super(applicationId);
	}

	protected IRuntimeDAO getDAO() throws Exception {
		return new RuntimeDaoManager().getFormTableDAO(getConnection(),
				getApplicationId());
	}

	/**
	 * 为需要创建动态表的表单执行创建
	 */
	public void createDynaTable(Form newForm) throws Exception {
		if (isHasDynaTable(newForm)) {
			((FormTableDAO) getDAO()).createDynaTable(newForm);
		} else {
			if (isDynaTableExists(newForm)) {
				((FormTableDAO) getDAO()).dropDynaTable(newForm);
			}
		}
	}

	/**
	 * 删除动态表
	 */
	public void dropDynaTable(Form oldForm) throws Exception {
		if (isDynaTableExists(oldForm)) {
			((FormTableDAO) getDAO()).dropDynaTable(oldForm);
		}
	}

	/**
	 * 检查动态表是否已经存在
	 */
	public boolean isDynaTableExists(Form form) throws Exception {
		return ((FormTableDAO) getDAO()).isDynaTableExists(form);
	}

	/**
	 * 同步动态表
	 */
	public void synchronizeDynaTable(Form formVO) throws Exception {
		((FormTableDAO) getDAO()).synchronizeDynaTable(formVO);
	}

	/**
	 * 更新动态表
	 */
	public void updateDynaTable(Form newForm, Form oldForm) throws Exception {
		if (isHasDynaTable(newForm)) {
			((FormTableDAO) getDAO()).updateDynaTable(newForm, oldForm);
		} else {
			if (isDynaTableExists(newForm)) {
				((FormTableDAO) getDAO()).dropDynaTable(newForm);
			}
		}
	}

	/**
	 * 2.6新增
	 * 
	 * @param newForm
	 * @param oldForm
	 * @throws Exception
	 */
	public void synDynaTable(Form newForm, Form oldForm) throws Exception {
		if(getDatasourceId() != null){
			DataSourceProcess dp = (DataSourceProcess) ProcessFactory
					.createProcess(DataSourceProcess.class);
			DataSource dt = (DataSource) dp.doView(getDatasourceId());
			if (isHasDynaTable(newForm)) {
				((FormTableDAO) new RuntimeDaoManager().getFormTableDAODtId(
						getConnectionByDtId(), getDatasourceId())).updateDynaTable(
						newForm, oldForm, dt);
			} else {
				if (isDynaTableExists(newForm)) {
					((FormTableDAO) new RuntimeDaoManager().getFormTableDAODtId(
							getConnectionByDtId(), getDatasourceId()))
							.dropDynaTable(newForm);
				}
			}
		}
	}

	/**
	 * 在保存或更新时对Form的改变进行校验
	 * 
	 * @throws Exception
	 */
	public void doChangeValidate(ChangeLog log) throws Exception {
		((FormTableDAO) getDAO()).changeValidate(log);
	}

	/**
	 * 创建或者更新动态表
	 */
	public void createOrUpdateDynaTable(Form newForm, Form oldForm)
			throws Exception {
		if (isHasDynaTable(newForm)) {
			((FormTableDAO) getDAO()).createOrUpdateDynaTable(newForm, oldForm);
		} else {
			if (isDynaTableExists(newForm)) {
				((FormTableDAO) getDAO()).dropDynaTable(newForm);
			}
		}
	}

	/**
	 * 判断表单是否需要创建动态表
	 * 
	 * @param form
	 *            表单
	 * @return 如果需要创建，则返回 true；否则返回 false。
	 */
	public boolean isHasDynaTable(Form form) {
		boolean rtn = false;
		switch (form.getType()) {
		case Form.FORM_TYPE_NORMAL:
			rtn = true;
			break;
		case Form.FORM_TYPE_NORMAL_MAPPING:
			rtn = true;
			break;
		case Form.FORM_TYPE_SUBFORM:
			rtn = true;
			break;
		case Form.FORM_TYPE_SEARCHFORM:
		case Form.FORM_TYPE_TEMPLATEFORM:	
			rtn = false;
			break;
		default:
			break;
		}
		return rtn;
	}

	public String getDatasourceId() {
		return this.datasourceId;
	}

	public void setDatasourceId(String datasourceId) {
		this.datasourceId = datasourceId;
	}

	/**
	 * 通过datasourceId获取connection
	 * 
	 * 2.6新增
	 * 
	 * @return Connection
	 * @throws Exception
	 */
	protected Connection getConnectionByDtId() throws Exception {
		if (getDatasourceId() != null) {
			DataSourceProcess dp = (DataSourceProcess) ProcessFactory
					.createProcess(DataSourceProcess.class);
			DataSource dt = (DataSource) dp.doView(getDatasourceId());
			if (dt != null) {
				return MonProxyFactory.monitor(dt.getConnection());
			}
		}
		return null;
	}
}
