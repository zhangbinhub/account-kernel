package OLink.bpm.core.expimp.exp.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.core.deploy.module.ejb.ModuleProcess;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.report.crossreport.definition.ejb.CrossReportProcess;
import OLink.bpm.core.report.crossreport.definition.ejb.CrossReportVO;
import OLink.bpm.core.task.ejb.Task;
import OLink.bpm.core.task.ejb.TaskProcess;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiVO;
import OLink.bpm.base.action.BaseHelper;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.ejb.IDesignTimeProcess;
import OLink.bpm.core.deploy.module.ejb.ModuleVO;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.dynaform.view.ejb.ViewProcess;
import OLink.bpm.core.expimp.exp.ejb.ExpProcess;
import OLink.bpm.core.expimp.exp.ejb.ExpProcessBean;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiProcess;
import OLink.bpm.util.ProcessFactory;

public class ExpHelper {

	/**
	 * The inner application id
	 */
	protected String applicationid = null;
	/**
	 * The inner module id
	 */
	protected String moduleid = null;
	/**
	 * The inner process process
	 */
	public ExpProcess process = null;

	/**
	 * 获取应用标识
	 * 
	 * @return the application id
	 */
	public String getApplicationid() {
		return applicationid;
	}

	/**
	 * 设置应用标识
	 * 
	 * @param application
	 *            应用标识
	 */
	public void setApplicationid(String applicationid) {
		this.applicationid = applicationid;
	}

	/**
	 * 获取模块标识
	 * 
	 * @return 模块标识
	 */
	public String getModuleid() {
		return moduleid;
	}

	/**
	 * 设置 模块标识
	 * 
	 * @param moduleid
	 */
	public void setModuleid(String moduleid) {
		this.moduleid = moduleid;
	}

	public ExpHelper() throws ClassNotFoundException {
		this.process = new ExpProcessBean();
	}

	/**
	 * 获取表单列表
	 * 
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Collection<Form> get_formList() throws Exception {
		ParamsTable params = new ParamsTable();
		params.setParameter("xi_type", Integer.valueOf(0));
		return (Collection<Form>) getListByModule(createProcess(FormProcess.class), params);
	}

	/**
	 * 获取视图列表
	 * 
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Collection<View> get_viewList() throws Exception {
		return (Collection<View>) getListByModule(createProcess(ViewProcess.class), null);
	}

	/**
	 * 获取流程列表
	 * 
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Collection<BillDefiVO> get_flowList() throws Exception {
		return (Collection<BillDefiVO>) getListByModule(createProcess(BillDefiProcess.class), null);
	}

	/**
	 * 获取定时任务列表
	 * 
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Collection<Task> get_taskList() throws Exception {
		return (Collection<Task>) getListByModule(createProcess(TaskProcess.class), null);
	}

	/**
	 * 获取交叉报表列表
	 * 
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Collection<CrossReportVO> get_crossReportList() throws Exception {
		return (Collection<CrossReportVO>) getListByModule(createProcess(CrossReportProcess.class), null);
	}

	private Collection<?> getListByModule(IDesignTimeProcess<?> iDesignTimeProcess, ParamsTable params)
			throws Exception {
		Collection<Object> list = new ArrayList<Object>();

		ModuleProcess process = (ModuleProcess) ProcessFactory.createProcess(ModuleProcess.class);

		ModuleVO startNode = (ModuleVO) process.doView(this.moduleid);
		// get all modules of the application
		ApplicationVO application = startNode.getApplication();
		Collection<ModuleVO> colls = application.getModules();

		Map<String, String> map = new LinkedHashMap<String, String>();
		if (colls != null) {
			map = process.deepSearchModuleTree(colls, application.getId(), startNode, null);
		} else {
			map.put(startNode.getId(), startNode.getName());
		}

		for (Iterator<String> iter = map.keySet().iterator(); iter.hasNext();) {
			String moduleid = iter.next();
			if (params != null) {
				params.setParameter("s_module", moduleid);
			} else {
				params = new ParamsTable();
				params.setParameter("s_module", moduleid);
			}

			list.addAll(iDesignTimeProcess.doSimpleQuery(params, application.getId()));
		}

		return list;
	}

	@SuppressWarnings( { "unchecked", "unused" })
	private Collection getListByApplication(BaseHelper<?> helper, ParamsTable params) throws Exception {
		Collection list = new ArrayList();

		if (params != null) {
			params.setParameter("s_application", applicationid);
		} else {
			params = new ParamsTable();
			params.setParameter("s_application", applicationid);
		}

		Collection datas = helper.process.doSimpleQuery(params, applicationid);

		if (datas.size() > 0) {
			list.addAll(datas);
		}

		return list;
	}

	private IDesignTimeProcess<?> createProcess(Class<?> iprocessClazz) throws ClassNotFoundException {
		return ProcessFactory.createProcess(iprocessClazz);
	}

	@SuppressWarnings( { "unused", "unchecked" })
	private Collection getListAll(BaseHelper helper, ParamsTable params) throws Exception {
		return helper.process.doSimpleQuery(params, applicationid);
	}
}
