package OLink.bpm.core.deploy.copymodule.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import OLink.bpm.core.deploy.copymodule.ejb.CopyModuleProcess;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.util.ElementResplaceUtil;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.base.action.BaseAction;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.deploy.copymodule.ejb.CopyModuleVO;
import OLink.bpm.core.deploy.module.ejb.ModuleVO;
import OLink.bpm.util.StringUtil;

import eWAP.core.Tools;
import com.opensymphony.webwork.ServletActionContext;

public class CopyModuleAction extends BaseAction<CopyModuleVO> {
	// 模块ID
	private String moduleId = null;
	// 模块名
	private String moduleName = null;
	// 是否新建模块
	private boolean _isCopyModuel;

	public boolean is_isCopyModuel() {
		return _isCopyModuel;
	}

	public void set_isCopyModuel(boolean copyModuel) {
		_isCopyModuel = copyModuel;
	}

	/**
	 * @SuppressWarnings 工厂方法无法使用泛型
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public CopyModuleAction() throws ClassNotFoundException {
		super(ProcessFactory.createProcess(CopyModuleProcess.class), new CopyModuleVO());
	}

	private static final long serialVersionUID = 1L;

	/**
	 * 复制模块中的表单的操作
	 * 
	 * @return 成功SUCCESS 否则INPUT
	 * @throws Exception
	 */
	public String doCopyForm() throws Exception {
		try {
			Map<String, String> sessionMap = new HashMap<String, String>();
			ParamsTable params = getParams();
			String[] formId = params.getParameterAsArray("colids");
			if (formId != null && formId[0] != null) {
				if (formId != null && formId.length > 0) {
					for (int i = 0; i < formId.length; i++) {
						if (!StringUtil.isBlank(formId[i])) {
							String _formid = Tools.getSequence();
							sessionMap.put(formId[i], _formid);
						}
					}
					setFormsMap(sessionMap);
				}
			} else {
				throw new Exception("[{*[Errors]*}]: [{*[Not_Choose]*}]!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}
		return SUCCESS;
	}

	/**
	 * dostart
	 * 
	 * @return
	 * @throws Exception
	 */
	public String doStart() throws Exception {
		return SUCCESS;
	}

	public String doCopyFormAfter() throws Exception {
		return SUCCESS;
	}

	public String doCopyViewAfter() throws Exception {
		return SUCCESS;
	}

	/**
	 * 复制模块的操作
	 * 
	 * @return 操作成功时返回字符串SUCCESS，用以表示操作已成功。操作失败时返回INPUT
	 * @throws Exception
	 */

	public String doCopyModule() throws Exception {
		try {
			ParamsTable params = getParams();
			Map<String, String> sessionMap = new HashMap<String, String>();
			moduleName = params.getParameterAsString("content.modulename");
			moduleId = params.getParameterAsString("content.moduleList");
			String description = params.getParameterAsString("content.description");
			String superiorid = params.getParameterAsString("_superiorid");
			CopyModuleVO vo = (CopyModuleVO) this.getContent();
			if (_isCopyModuel) {
				if (moduleName != null && moduleName != "" && description != null) {
					vo.setDescription(description);
					vo.setModulename(moduleName);
				}
				vo.setModuleId(Tools.getSequence());
				CopyModuleProcess copymoduleProcess = getProcess();
				ModuleVO modulevo = copymoduleProcess.copyModule(vo, moduleId, vo.getModuleId(), superiorid);
				sessionMap.put(moduleId, modulevo.getId());
			} else {
				vo.setModuleId(moduleId);
				vo.set_isCopyModuel(_isCopyModuel);
			}
			setModuledsMap(sessionMap);
			setContent(vo);
		} catch (Exception e) {
			e.printStackTrace();
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}
		return SUCCESS;
	}

	/**
	 * 取消时执行的操作
	 * @SuppressWarnings servlet api不支持泛型
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
			this.addFieldError("1", ex1.getMessage());
			return INPUT;
		}
		return SUCCESS;
	}

	/**
	 * 
	 * @return 操作成功时返回字符串SUCCESS，用以表示操作已成功。操作失败时返回INPUT
	 * @throws Exception
	 */
	public String doCopyFlow() throws Exception {
		try {
			Map<String, String> sessionMap = new HashMap<String, String>();
			ParamsTable params = getParams();
			String[] flowid = params.getParameterAsArray("colids");
			if (flowid != null && flowid.length > 0) {
				CopyModuleVO vo = (CopyModuleVO) this.getContent();
				for (int i = 0; i < flowid.length; i++) {
					if (!StringUtil.isBlank(flowid[i])) {
						String _flowid = Tools.getSequence();
						sessionMap.put(flowid[i], _flowid);
					}
					this.setContent(vo);
				}
				this.setflowIdsMap(sessionMap);
				ElementResplaceUtil resplace = new ElementResplaceUtil(getFormsMap(), getViewIdsMap(), getflowIdsMap(),
						getModuledsMap(), null, null);
				resplace.resplace(vo.getApplicationid());
			} else {
				throw new Exception("[{*[Errors]*}]: [{*[Not_Choose]*}!]");
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.addFieldError("", e.getMessage());
			return INPUT;
		}
		return SUCCESS;
	}

	/**
	 * doCopyView
	 * 
	 * @return 操作成功时返回字符串SUCCESS，用以表示操作已成功。操作失败时返回INPUT
	 * @throws Exception
	 */
	public String doCopyView() throws Exception {
		try {
			Map<String, String> sessionMap = new HashMap<String, String>();
			Map<String, String> resourcveMap = new HashMap<String, String>();
			ParamsTable params = getParams();
			String[] viewid = params.getParameterAsArray("colids");
			if (viewid != null && viewid.length > 0) {
				String _module = params.getParameterAsString("s_module");
				CopyModuleProcess copymoduleProcess = getProcess();
				CopyModuleVO vo = (CopyModuleVO) this.getContent();
				for (int i = 0; i < viewid.length; i++) {
					if (StringUtil.isBlank(viewid[i])) {
						String _viewid = Tools.getSequence();
						sessionMap.put(viewid[i], _viewid);
						View view = copymoduleProcess.copyView(vo, _module, viewid[i], _viewid);
						String newResourceId = Tools.getSequence();
						resourcveMap.put(view.getRelatedResourceid(), newResourceId);
					}
					this.setContent(vo);
				}
				setViewIdsMap(sessionMap);
				// setResourceMap(resourcveMap);
			} else {
				throw new Exception("[{*[Errors]*}]: [ {*[Not_Choose]*}!]");
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}
		return SUCCESS;
	}

	/**
	 * COPY ALL
	 * 
	 * @return 操作成功时返回字符串SUCCESS，用以表示操作已成功。操作失败时返回INPUT
	 * @throws Exception
	 */
	public String doCopyModuleAll() throws Exception {
		try {
			CopyModuleProcess copymoduleProcess = getProcess();
			if (_selects != null && _selects.length > 0) {
				for (int i = 0; i < _selects.length; i++) {
					copymoduleProcess.copyModuleALL(_selects[i]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}
		return SUCCESS;
	}

	/**
	 * create Process
	 * 
	 * @return
	 * @throws Exception
	 */
	public static CopyModuleProcess getProcess() throws Exception {
		return (CopyModuleProcess) ProcessFactory.createProcess(CopyModuleProcess.class);
	}

	/**
	 * 替换所有的新增的属性,如formname,activity对应的form
	 * 
	 * @return
	 */
	public String replaceAll() throws Exception {
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}

	/**
	 * 表单id
	 * 
	 * @SuppressWarnings Servlet API 不支持泛型
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, String> getFormsMap() {
		HttpSession session = ServletActionContext.getRequest().getSession();
		Map<String, String> sessionMap = (Map<String, String>) session.getAttribute("formIds");
		if (sessionMap == null) {
			sessionMap = new HashMap<String, String>();
		}
		return sessionMap;
	}

	private void setFormsMap(Map<String, String> map) {
		HttpSession session = ServletActionContext.getRequest().getSession();
		session.setAttribute("formIds", map);
	}

	/**
	 * 视图ID
	 * @SuppressWarnings servlet api不支持泛型
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, String> getViewIdsMap() {
		HttpSession session = ServletActionContext.getRequest().getSession();
		Map<String, String> sessionMap = (Map<String, String>) session.getAttribute("viewIds");
		if (sessionMap == null) {
			sessionMap = new HashMap<String, String>();
		}
		return sessionMap;
	}

	private void setViewIdsMap(Map<String, String> map) {
		HttpSession session = ServletActionContext.getRequest().getSession();
		session.setAttribute("viewIds", map);
	}

	/**
	 * 复制流程id
	 * @SuppressWarnings Servlet API 不支持泛型
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, String> getflowIdsMap() {
		HttpSession session = ServletActionContext.getRequest().getSession();
		Map<String, String> sessionMap = (Map) session.getAttribute("flowIds");
		if (sessionMap == null) {
			sessionMap = new HashMap<String, String>();
		}
		return sessionMap;
	}

	private void setflowIdsMap(Map<String, String> map) {
		HttpSession session = ServletActionContext.getRequest().getSession();
		session.setAttribute("flowIds", map);
	}

	/**
	 * 复制模块id
	 * @SuppressWarnings Servlet API 不支持泛型
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, String> getModuledsMap() {
		HttpSession session = ServletActionContext.getRequest().getSession();
		Map<String, String> sessionMap = (Map<String, String>) session.getAttribute("moduleIds");
		if (sessionMap == null) {
			sessionMap = new HashMap<String, String>();
		}
		return sessionMap;
	}

	private void setModuledsMap(Map<String, String> map) {
		HttpSession session = ServletActionContext.getRequest().getSession();
		session.setAttribute("moduleIds", map);
	}
}
