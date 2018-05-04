package OLink.bpm.core.wizard.action;

import java.util.Map;

import OLink.bpm.core.wizard.ejb.WizardVO;
import OLink.bpm.base.action.BaseAction;
import OLink.bpm.core.deploy.module.ejb.ModuleProcess;
import OLink.bpm.core.deploy.module.ejb.ModuleProcessBean;
import OLink.bpm.core.deploy.module.ejb.ModuleVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.wizard.ejb.WizardProcess;
import OLink.bpm.core.wizard.util.WizardUtil;

import com.opensymphony.webwork.ServletActionContext;

/**
 * WizardAction class.
 * 
 * @author zhuxuehong
 * @since JDK1.4
 */
public class WizardAction extends BaseAction<WizardVO> {

	/**
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public WizardAction() throws ClassNotFoundException {
		super(ProcessFactory.createProcess(WizardProcess.class), new WizardVO());
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// 开始wizard的新建，转到step1_module.jsp页面，即是开始新建module
	public String doStart() throws Exception {
		return SUCCESS;
	}

	// 转到step2_form.jsp页面
	public String doToStep2() throws Exception {
		ModuleProcess M_process = new ModuleProcessBean();
		WizardVO tempWizardVO = (WizardVO) (this.getContent());
		String tempname = tempWizardVO.getM_name();
		ModuleVO module = M_process.getModuleByName(tempname, application);
		if (null != module && null == module.getSuperior()) {
			this.addFieldError("1", "{*[ModuleExist]*}");
			return INPUT;
		}
		return SUCCESS;
	}

	public String doToStep2FormInfo() throws Exception {

		return SUCCESS;
	}

	public String doToStep2FormInfoSub() throws Exception {

		return SUCCESS;
	}

	public String doToStep2Field() throws Exception {

		return SUCCESS;
	}

	public String doToStep2FieldSub() throws Exception {

		return SUCCESS;
	}

	public String doToStep2Style() throws Exception {
		return SUCCESS;
	}

	/**
	 * @SuppressWarnings webwork不支持泛型
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String doToStep2StyleSub() throws Exception {
		WizardVO wizardVO = (WizardVO) this.getContent();
		Map<String, Object> sessionMap = getContext().getSession();
		String fieldsDescription = wizardVO.getF_fieldsdescription_sub();
		if (sessionMap.get("f_fieldsdescription_sub") == null) {
			sessionMap.put("f_fieldsdescription_sub", StringUtil.encodeHTML(fieldsDescription));
		}
		getContext().setSession(sessionMap);
		return SUCCESS;
	}

	public String doBackToStep1() throws Exception {
		return SUCCESS;
	}

	public String doBackToFormType() throws Exception {
		return SUCCESS;
	}

	public String doBackToFormInfo() throws Exception {
		return SUCCESS;
	}

	public String doBackToField() throws Exception {
		return SUCCESS;
	}

	public String doBackToFormTypeSub() throws Exception {
		return SUCCESS;
	}

	public String doBackToFormInfoSub() throws Exception {
		return SUCCESS;
	}

	public String doBackToFieldSub() {
		return SUCCESS;
	}

	public String doToEwebEdit() {
		return SUCCESS;
	}

	public String doToSubFormView() {
		return SUCCESS;
	}

	public String doToFormSuccess() {

		WizardVO vo = (WizardVO) this.getContent();

		if (vo.getF_Type().equals("01")) {
			return "toSubForm";
		}

		return SUCCESS;
	}

	public String doToSubFormSuccess() {
		return SUCCESS;
	}

	public String doBackToStep2Style() throws Exception {
		WizardVO vo = (WizardVO) this.getContent();
		if (vo.getF_name_sub() != null) {
			return "toStyleSub";
		}
		return "toStyle";
	}

	public String doBackFormSuccess() throws Exception {
		return SUCCESS;
	}

	// 转到step4_resource.jsp页面

	public String doTostep4() throws Exception {
		return SUCCESS;
	}

	public String doBacktostep2() throws Exception {
		return SUCCESS;
	}

	// 转到step5_view.jsp页面
	public String doTostep5() throws Exception {
		return SUCCESS;
	}

	public String doToviewType() throws Exception {
		return SUCCESS;
	}

	public String doToviewColumn() throws Exception {
		return SUCCESS;
	}

	public String doBacktostep4() throws Exception {
		return SUCCESS;
	}

	public String doViewFilter() {
		return SUCCESS;
	}

	public String doViewSuccess() throws Exception {
		return SUCCESS;
	}

	public String doBacktostep5() throws Exception {
		return SUCCESS;
	}

	// 转到step3_workflow.jsp页面

	public String doNewFlow() throws Exception {
		return SUCCESS;
	}

	public String doWorkFlowType() throws Exception {
		return SUCCESS;
	}

	public String doWorkFlowRole() throws Exception {
		return SUCCESS;
	}

	public String doWorkFlowConfirm() throws Exception {
		return SUCCESS;
	}

	/**
	 * 取消时执行的操作
	 * @SuppressWarnings webwork不支持泛型
	 * @return 操作成功时返回字符串SUCCESS，用以表示操作已成功。操作失败时返回INPUT
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String doCancel() throws Exception {
		try {
			String appids[] = new String[3];
			appids[0] = this.getApplication();
			Map<String, String[]> parametes = getContext().getParameters();
			parametes.put("id", appids);
			getContext().setParameters(parametes);
		} catch (Exception ex1) {
			ex1.printStackTrace();
			return INPUT;
		}
		return SUCCESS;
	}

	/**
	 * 最后一步确认时，创建所有的表单，视图，菜单，流程
	 * @SuppressWarnings webwork不支持泛型
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String doConfirm() throws Exception {
		try {
			((WizardProcess) process).confirm(this.getContent(), this.getUser(), this.getApplication(), this
					.getEnvironment().getApplicationRealPath());
			String modules[] = new String[3];
			modules[0] = ((WizardVO) this.getContent()).getModuleid();
			Map<String, String[]> parameters = getContext().getParameters();
			parameters.put("moduleid", modules);

			ServletActionContext.getRequest().setAttribute("mid", modules[0]);

			getContext().setParameters(parameters);
		} catch (Exception e) {
			e.printStackTrace();
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}
		return SUCCESS;

	}

	public String doValidate() throws Exception {
		ParamsTable params = getParams();

		String formName = params.getParameterAsString("formname");

		String mainFormName = params.getParameterAsString("mainformname");

		WizardUtil util = new WizardUtil();

		String rtnValue = util.validateForm(formName, null, getContent().getApplicationid());
		if (mainFormName != null) {
			rtnValue = util.validateForm(formName, mainFormName, getContent().getApplicationid());
		}
		ServletActionContext.getRequest().setAttribute("formName", rtnValue);
		return SUCCESS;
	}
}
