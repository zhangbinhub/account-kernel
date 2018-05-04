package OLink.bpm.core.validate.repository.action;

import java.util.Date;

import OLink.bpm.base.action.BaseAction;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.validate.repository.ejb.ValidateRepositoryVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.validate.repository.ejb.ValidateRepositoryProcess;

public class ValidateRepositoryAction extends BaseAction<ValidateRepositoryVO> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 默认构造方法
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ValidateRepositoryAction() throws Exception {
		super(ProcessFactory.createProcess(ValidateRepositoryProcess.class),
				new ValidateRepositoryVO());
	}

	/**
	 * 保存前做名称唯一性验证
	 * 修改者：Bluce
	 * 修改时间：2010－05－06
	 */
	public String doSave() {
		ParamsTable pt = getParams();
		String id = pt.getParameterAsString("content.id");
		String name = pt.getParameterAsString("content.name");
		try {
			if(!((ValidateRepositoryProcess)process).isValidateNameExist(id, name, application)){
				// 设置修改日期
				((ValidateRepositoryVO) getContent()).setLastmodifytime(new Date());
				return super.doSave();
			}
			else{
				return INPUT;
			}
		} catch (Exception e) {
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}
	}
	
}
