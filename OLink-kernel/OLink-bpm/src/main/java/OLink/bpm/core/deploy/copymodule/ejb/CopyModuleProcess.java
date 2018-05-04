package OLink.bpm.core.deploy.copymodule.ejb;

import OLink.bpm.core.deploy.module.ejb.ModuleVO;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.base.ejb.IDesignTimeProcess;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.resource.ejb.ResourceVO;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiVO;

/**
 * 
 * @author eshow
 * 
 */
public interface CopyModuleProcess extends IDesignTimeProcess<CopyModuleVO> {
	/**
	 * 复制表单
	 * 
	 * @param vo
	 *            CopyModuleVO对象
	 * @param moduleId
	 *            模块对象
	 * @param formId
	 *            表单标识
	 * @return 表单对象
	 * @throws Exception
	 */
	Form copyForm(CopyModuleVO vo, String moduleId, String formId, String _formid) throws Exception;

	/**
	 * 复制视图
	 * 
	 * @param vo
	 *            CopyModuleVO对象
	 * @param moduleId
	 *            模块对象
	 * @param viewId
	 *            视图标识
	 * @return 视图对象
	 * @throws Exception
	 */
	View copyView(CopyModuleVO vo, String moduleId, String oldViewId, String newViewid) throws Exception;

	/**
	 * 复制流程
	 * 
	 * @param vo
	 *            CopyModuleVO对象
	 * @param moduleId
	 *            模块Id
	 * @param flowId
	 *            流程标识
	 * @return 流程对象
	 * @throws Exception
	 */
	BillDefiVO copyFlow(CopyModuleVO vo, String moduleId, BillDefiVO newBillDefiVO, String newFlowid) throws Exception;

	/**
	 * 复制模块
	 * 
	 * @param vo
	 *            CopyModuleVO对象
	 * @param moduleId
	 *            模块标识
	 * @param newModuldeId
	 *            新模块标识
	 * @param superior
	 *            新模块上级
	 * @return 模块对象
	 * @throws Exception
	 */
	ModuleVO copyModule(CopyModuleVO vo, String oldModuleId, String newModuldeId, String superior)
			throws Exception;

	/**
	 * 复制整个模块 通过应用的ID拿回应用的所有模块，进行复制
	 * 
	 * @param application
	 * @return 模块对象
	 * @throws Exception
	 */
	ModuleVO copyModuleALL(String moduleid) throws Exception;

	/**
	 * 复制模块
	 * 
	 * @param moduleId
	 *            被复制的模块ID
	 * @param superiorId
	 *            复制到的目标模块(上级)
	 * @return 模块对象
	 * @throws Exception
	 */
	ModuleVO copyModuleALL(String moduleId, String superiorId) throws Exception;

	/**
	 * 复制模块中视图对应的菜单
	 * 
	 * @param view
	 *            视图对象
	 * @return 菜单对象
	 * @throws Exception
	 */
	ResourceVO copyResource(View view, ResourceVO superior) throws Exception;

	/**
	 * 复制模块(包括模块所有子元素)
	 * 
	 * @param moduleId
	 *            旧模块ID
	 * @param newModuleId
	 *            新模块ID
	 * @param superiorId
	 *            上级模块ID
	 * @return 复制后的模块
	 * @throws Exception
	 */
	ModuleVO copyModuleALL(String moduleId, String newModuleId, String superiorId) throws Exception;

}
