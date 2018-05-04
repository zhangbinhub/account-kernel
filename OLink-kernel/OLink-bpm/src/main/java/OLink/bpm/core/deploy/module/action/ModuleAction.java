package OLink.bpm.core.deploy.module.action;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import OLink.bpm.base.action.BaseAction;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.deploy.application.ejb.ApplicationProcess;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.core.deploy.module.ejb.ModuleProcess;
import OLink.bpm.core.deploy.module.ejb.ModuleVO;
import OLink.bpm.core.role.ejb.RoleProcess;
import OLink.bpm.core.role.ejb.RoleVO;
import OLink.bpm.util.ProcessFactory;

/**
 * @author nicholas
 */
public class ModuleAction extends BaseAction<ModuleVO> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @SuppressWarnings 工厂方法无法使用泛型
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public ModuleAction() throws ClassNotFoundException {
		super(ProcessFactory.createProcess(ModuleProcess.class), new ModuleVO());
	}

	public Collection<RoleVO> get_roleList(String application) throws Exception {
		RoleProcess rp = (RoleProcess) ProcessFactory.createProcess(RoleProcess.class);
		return rp.doSimpleQuery(null, application);
	}

	public Collection<ApplicationVO> get_applicationList(String application) throws Exception {
		ApplicationProcess ap = (ApplicationProcess) ProcessFactory.createProcess(ApplicationProcess.class);
		return ap.doSimpleQuery(null, application);
	}

	public String getApplication() {
		ModuleVO content = (ModuleVO) getContent();
		if (content != null && content.getApplication() != null) {
			return content.getApplication().getId();
		} else {
			return null;
		}
	}

	/**
	 * 保存
	 */
	public String doSave() {
		try {
			ModuleVO tempModuleVO = (ModuleVO) (this.getContent());
			boolean flag = false;
			boolean flag1 = false;
			String tempname = tempModuleVO.getName();
			ModuleVO module = ((ModuleProcess) process).getModuleByName(tempname, application);
			if (getParams().getParameter("_superiorid") != "") {
				boolean check = false;
				if (tempModuleVO.getId() == null || tempModuleVO.getId().trim().length() <= 0) {// 判断新建不能重名
					check = true;
				} else if (null != module && !tempModuleVO.getId().trim().equalsIgnoreCase(module.getId())) {// 修改不能重名
					check = true;
				}
				if (check) {
					getParams().setParameter("s_superior", getParams().getParameter("_superiorid"));
					DataPackage<ModuleVO> datas1 = process.doQuery(getParams());
					Iterator<ModuleVO> iter = datas1.datas.iterator();
					while (iter.hasNext()) {
						ModuleVO tempModuleVO1 = iter.next();
						if (tempModuleVO1 != null) {
							if (tempModuleVO.getName().equals(tempModuleVO1.getName())) {
								flag1 = true;
								break;
							}
						}
					}
				}
			}
			if (getParams().getParameter("_superiorid") == "") {
				if (module != null) {

					if (tempModuleVO.getId() == null || tempModuleVO.getId().trim().length() <= 0) {// 判断新建不能重名
						this.addFieldError("1", "{*[ModuleExist]*}");
						flag = true;
					} else if (flag1 == true && !tempModuleVO.getId().trim().equalsIgnoreCase(module.getId())) {// 修改不能重名
						this.addFieldError("1", "{*[ModuleExist]*}");
						flag = true;
					}
				}
			} else {
				if (flag1) {
					this.addFieldError("1", "{*[ModuleExist]*}");
					flag = true;
				}
			}

			if (!flag) {
				// 设置最后修改日期为当前日期
				getModuleVO().setLastmodifytime(new Date());

				// 设置commitDate
				if (getModuleVO().getId() != null && !(getModuleVO().getId().equals(""))) {
					Date oldCommitdate = ((ModuleVO) process.doView(getModuleVO().getId())).getCommitDate();
					getModuleVO().setCommitDate(oldCommitdate);
				}
				return super.doSave();
			} else {
				return INPUT;
			}
		} catch (Exception e) {
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}
	}

	public String doListElement() throws Exception {
		return this.doView();
	}

	public void setApplication(String _applicationid) {

		super.setApplication(_applicationid);

		ModuleVO content = (ModuleVO) getContent();
		try {
			ApplicationProcess ap = (ApplicationProcess) ProcessFactory.createProcess(ApplicationProcess.class);
			ApplicationVO app = (ApplicationVO) ap.doView(_applicationid);
			content.setApplication(app);
		} catch (Exception e) {
			content.setApplication(null);
		}

	}

	public String get_superiorid() {
		ModuleVO content = (ModuleVO) getContent();
		if (content != null && content.getSuperior() != null) {
			return content.getSuperior().getId();
		} else {
			return null;
		}
	}

	public void set_superiorid(String _superiorid) throws Exception {
		ModuleVO content = (ModuleVO) getContent();
		if (_superiorid != null) {
			ModuleProcess mp = getProcess();
			ModuleVO md = (ModuleVO) mp.doView(_superiorid);
			content.setSuperior(md);
		} else {
			content.setSuperior(null);
		}
	}

	public String get_ispublished() {
		ModuleVO content = (ModuleVO) getContent();
		if (content.isIspublished()) {
			return "true";
		} else {
			return "false";
		}
	}

	public void set_ispublished(String ispublished) {
		ModuleVO content = (ModuleVO) getContent();
		if (ispublished != null) {
			if (ispublished.trim().equalsIgnoreCase("true")) {
				content.setIspublished(true);
				return;
			}
		}
		content.setIspublished(false);
	}

	private ModuleVO getModuleVO() throws Exception {
		return (ModuleVO) this.getContent();
	}

	public String doDelete() {
		try {
			if (_selects != null) {
				ModuleProcess moduleprocess = this.getProcess();
				/*
				for (int i = 0; i < _selects.length; i++) {
					moduleprocess.deleteModule(_selects[i], application);
				}
				*/
				String application = getParams().getParameterAsString("application");
				moduleprocess.deleteModules(_selects, application);
			}
		} catch (Exception e) {
			addFieldError("", e.getMessage());
			e.printStackTrace();
			return INPUT;
		}
		return SUCCESS;
	}

	public ModuleProcess getProcess() throws Exception {
		return (ModuleProcess) ProcessFactory.createProcess(ModuleProcess.class);
	}
}
