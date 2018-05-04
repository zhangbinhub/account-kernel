package OLink.bpm.core.macro.repository.action;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import OLink.bpm.base.action.BaseAction;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.deploy.application.action.ApplicationHelper;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.core.macro.repository.ejb.RepositoryProcess;
import OLink.bpm.core.macro.repository.ejb.RepositoryVO;
import OLink.bpm.core.macro.repository.ejb.SystemBaseLib;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;

public class RepositoryAction extends BaseAction<RepositoryVO> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public RepositoryAction() throws Exception {
		super(ProcessFactory.createProcess(RepositoryProcess.class),
				new RepositoryVO());
	}

	// public String _applicationid;
	//	
	//	
	//	
	//	
	// public String get_applicationid() {
	// RepositoryVO content = (RepositoryVO) this.getContent();
	// if(content.getApplicationid() !=null){
	// _applicationid = content.getApplicationid();
	// }
	// return _applicationid;
	// }
	//
	// public void set_applicationid(String _applicationid) {
	// this._applicationid = _applicationid;
	// if(!StringUtil.isBlank(_applicationid)){
	// RepositoryVO content = (RepositoryVO) this.getContent();
	// content.setApplicationid(_applicationid);
	// }
	// }

	/**
	 * 保存前验证名称是否唯一 修改者：Bluce 修改时期：2010－05－06
	 */
	public String doSave() {
		ParamsTable pt = getParams();
		String id = pt.getParameterAsString("content.id");
		String name = pt.getParameterAsString("content.name");
		try {
			if (!((RepositoryProcess) process).isMacroNameExist(id, name,
					application)) {

				RepositoryVO repository = (RepositoryVO) getContent();
				if (isExistSameFunctionName(repository)) {
					return INPUT;
				}
				if (process.doView(repository.getId()) == null) {
					repository.setApplicationid("");
					repository.setApplicationid(pt
							.getParameterAsString("content.applicationid"));
					process.doCreate(repository);
				} else {
					repository.setApplicationid("");
					String app = pt
							.getParameterAsString("content.applicationid");
					if (app.lastIndexOf(";") > 0) {
						app = app.substring(app.lastIndexOf(";") + 1, app
								.length());
					}
					repository.setApplicationid(app);
					process.doUpdate(repository);
				}
				this.setContent(repository);
				this.addActionMessage("{*[Save_Success]*}");
				return SUCCESS;
			} else {
				this.addFieldError("1","{*[duplicate_name]*}");
				return INPUT;
			}
		} catch (Exception e) {
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}
	}

	private boolean isExistSameFunctionName(RepositoryVO repository)
			throws Exception {
		if (repository != null) {
			String functionContext = repository.getContent().trim();
			int fromIndex = 0;
			StringBuffer message = new StringBuffer("");
			while (true) {
				int indexS = functionContext.indexOf("function", fromIndex);
				if (indexS == -1)
					break;
				int indexE = functionContext.indexOf("(", indexS);
				if (indexE == -1)
					break;
				fromIndex = indexE;
				if (indexS > -1 && indexE > -1 && indexE > indexS) {
					String functionName = functionContext.substring(indexS + 8,
							indexE).trim();
					if (functionName.matches("(\\S|\\d)+")) {
						if (SystemBaseLib.baseLib.contains(functionName)) {
							if (message.length() > 0) {
								message.append("," + functionName);
							} else {
								message.append(functionName);
							}
						}
						// ParamsTable param = new ParamsTable();
						// param.setParameter("t_applicationid",
						// repository.getApplicationid());
						// param.setParameter("sm_content",functionName+"(");
						String dql = "from RepositoryVO rvo where rvo.applicationid='"
								+ repository.getApplicationid()
								+ "' and rvo.content like '%function "
								+ functionName + "(%'";
						if (!StringUtil.isBlank(repository.getId())) {
							dql += " and rvo.id !='" + repository.getId() + "'";
						}
						int size = process
								.doGetTotalLines(dql);
						// Collection<RepositoryVO> repositories =
						// ((RepositoryProcess)process).doSimpleQuery(param);
						// if(repositories != null && repositories.size() > 0){
						if (size > 0) {
							if (message.length() > 0) {
								message.append("," + functionName);
							} else {
								message.append(functionName);
							}
						}
					}
				}
			}
			if (message != null && message.length() > 0) {
				this.addFieldError("function.name.exist",
						"{*[function.name.exist]*}:[" + message.toString()
								+ "]");
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取软件
	 * 
	 * @return 软件软件集合
	 * @throws Exception
	 */
	public Map<String, String> get_applications() throws Exception {

		Map<String, String> map = new HashMap<String, String>();
		// ParamsTable param = this.getParams();
		// String domainid = param.getParameterAsString("domain");
		ApplicationHelper helper = new ApplicationHelper();
		Collection<ApplicationVO> applications = helper.getAppList();
		for (Iterator<ApplicationVO> ite = applications.iterator(); ite
				.hasNext();) {
			ApplicationVO application = ite.next();
			map.put(application.getId(), application.getName());
		}

		return map;
	}

	/**
	 * 函数库列表
	 * 
	 * @return
	 * @throws Exception
	 */
	public String doListAll() throws Exception {
		WebUser user = this.getUser();
		if (user.isSuperAdmin()) {
			this.getParams().removeParameter("application");
			this.getParams().removeParameter("content.applicationid");
			this.getParams().removeParameter("content.content");
			this.getParams().removeParameter("content.sortId");
			this.getParams().removeParameter("content.id");
			this.getParams().removeParameter("domain");
			this.getParams().removeParameter("content.version");
			this.getParams().removeParameter("_rowcount");
			this.getParams().removeParameter("content.name");
		}
		return super.doList();
	}
}
