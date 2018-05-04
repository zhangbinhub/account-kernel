package OLink.bpm.core.deploy.module.ejb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.deploy.application.ejb.ApplicationProcess;
import OLink.bpm.core.deploy.module.dao.ModuleDAO;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.dynaform.view.ejb.ViewProcess;
import OLink.bpm.core.image.repository.ejb.ImageRepositoryVO;
import OLink.bpm.core.resource.ejb.ResourceProcess;
import OLink.bpm.core.style.repository.ejb.StyleRepositoryProcess;
import OLink.bpm.core.task.ejb.Task;
import OLink.bpm.core.task.ejb.TaskProcess;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiProcess;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.image.repository.ejb.ImageRepositoryProcess;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import org.apache.log4j.Logger;

import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.core.style.repository.ejb.StyleRepositoryVO;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiVO;

public class ModuleProcessBean extends AbstractDesignTimeProcessBean<ModuleVO>
		implements ModuleProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 670857627354276130L;
	private FormProcess formProcess;
	private ViewProcess viewProcess;
	private BillDefiProcess billDefiProcess;
	private ResourceProcess resProcess;
	private TaskProcess taskProcess;
	private ImageRepositoryProcess imageRepositoryProcess;
	private StyleRepositoryProcess styleRepositoryProcess;

	public ModuleProcessBean() throws Exception {
		formProcess = (FormProcess) ProcessFactory
				.createProcess(FormProcess.class);
		viewProcess = (ViewProcess) ProcessFactory
				.createProcess(ViewProcess.class);
		billDefiProcess = (BillDefiProcess) ProcessFactory
				.createProcess(BillDefiProcess.class);
		resProcess = (ResourceProcess) ProcessFactory
				.createProcess(ResourceProcess.class);
		taskProcess = (TaskProcess) ProcessFactory
				.createProcess(TaskProcess.class);
		imageRepositoryProcess = (ImageRepositoryProcess) ProcessFactory
				.createProcess(ImageRepositoryProcess.class);
		styleRepositoryProcess = (StyleRepositoryProcess) ProcessFactory
				.createProcess(StyleRepositoryProcess.class);
	}

	@SuppressWarnings("unused")
	private final static Logger log = Logger.getLogger(ModuleProcessBean.class);

	public void doRemove(String pk) throws Exception {
		// 检查是否有下级模块
		super.doRemove(pk);
	}

	protected IDesignTimeDAO<ModuleVO> getDAO() throws Exception {
		return (ModuleDAO) DAOFactory.getDefaultDAO(ModuleVO.class.getName());
	}

	public void doUpdate(ValueObject vo) throws Exception {
		try {
			PersistenceUtils.beginTransaction();

			changeSuperior((ModuleVO) vo);
			super.doUpdate(vo);

			PersistenceUtils.commitTransaction();
		} catch (Exception e) {
			PersistenceUtils.rollbackTransaction();
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 更改上级模块
	 * 
	 * @param vo
	 * @throws Exception
	 */
	public void changeSuperior(ModuleVO vo) throws Exception {
		try {
			PersistenceUtils.beginTransaction();

			ModuleVO newSuperior = vo.getSuperior();
			ModuleVO po = (ModuleVO) getDAO().find(vo.getId());
			ModuleVO oldSuperior = po.getSuperior();
			Collection<ModuleVO> underModuleList = getUnderModuleList(vo
					.getId(), Integer.MAX_VALUE);

			boolean superiorInUnderList = false;
			for (Iterator<ModuleVO> iterator = underModuleList.iterator(); iterator
					.hasNext();) {
				ModuleVO underModule = iterator.next();
				// 新上级为下级模块
				if (newSuperior != null
						&& underModule.getId().equals(newSuperior.getId())) {
					superiorInUnderList = true;
				}
			}

			if (superiorInUnderList) {
				// 更新下级模块
				for (Iterator<ModuleVO> iterator = underModuleList.iterator(); iterator
						.hasNext();) {
					ModuleVO underModule = iterator.next();
					if (underModule.getSuperior().getId().equals(vo.getId())) {
						underModule.setSuperior(oldSuperior);
						getDAO().update(underModule);
					}
				}
			}

			PersistenceUtils.commitTransaction();
		} catch (Exception e) {
			PersistenceUtils.rollbackTransaction();
			e.printStackTrace();
			throw e;
		}
	}

	public Collection<ModuleVO> getUnderModuleList(
			Collection<ModuleVO> moduleList, String moduleId, int maxDeep)
			throws Exception {
		HashSet<ModuleVO> result = new LinkedHashSet<ModuleVO>();

		if (maxDeep <= 0) {
			return result;
		}

		if (moduleList != null && !moduleList.isEmpty()) {
			Iterator<ModuleVO> itmp = moduleList.iterator();
			while (itmp.hasNext()) {
				ModuleVO module = itmp.next();
				if (module.getSuperior() != null
						&& module.getSuperior().getId().equals(moduleId)) {
					result.addAll(getUnderModuleList(moduleList,
							module.getId(), maxDeep - 1));
					result.add(module);
				}
			}
		}

		return result;
	}

	public Collection<ModuleVO> getUnderModuleList(String moduleId, int maxDeep)
			throws Exception {
		HashSet<ModuleVO> result = new LinkedHashSet<ModuleVO>();

		if (maxDeep <= 0) {
			return result;
		}

		ModuleVO superior = (ModuleVO) doView(moduleId);
		if (superior != null) {
			Collection<ModuleVO> moduleList = doSimpleQuery(null, superior
					.getApplicationid());
			if (moduleList != null && !moduleList.isEmpty()) {
				Iterator<ModuleVO> itmp = moduleList.iterator();
				while (itmp.hasNext()) {
					ModuleVO module = itmp.next();
					if (module.getSuperior() != null
							&& module.getSuperior().getId().equals(moduleId)) {
						result.addAll(getUnderModuleList(moduleList, module
								.getId(), maxDeep - 1));
						result.add(module);
					}
				}
			}
		}

		return result;
	}

	public Map<String, String> deepSearchModuleTree(Collection<ModuleVO> cols,
			String applicationId, ModuleVO startNode, String excludeNodeId,
			int deep) throws Exception {
		Map<String, String> list = new LinkedHashMap<String, String>();
		list.put("", "No");

		String prefix = "|------------------------------------------------";
		if (startNode != null) {
			list.put(startNode.getId(), prefix.substring(0, deep * 2)
					+ startNode.getName());
		}

		Iterator<ModuleVO> iter = cols.iterator();
		while (iter.hasNext()) {
			ModuleVO vo = iter.next();

			if (applicationId == null || vo.getApplication() == null
					|| !applicationId.equals(vo.getApplication().getId())) {
				continue;
			}

			if (startNode == null) {
				if (vo.getSuperior() == null) {
					if (vo.getId() != null && !vo.getId().equals(excludeNodeId)) {
						Map<String, String> tmp = deepSearchModuleTree(cols,
								applicationId, vo, excludeNodeId, deep + 1);
						list.putAll(tmp);
					}
				}
			} else {
				if (vo.getSuperior() != null
						&& vo.getSuperior().getId().equals(startNode.getId())) {
					if (vo.getId() != null && !vo.getId().equals(excludeNodeId)) {
						Map<String, String> tmp = deepSearchModuleTree(cols,
								applicationId, vo, excludeNodeId, deep + 1);
						list.putAll(tmp);
					}
				}
			}
		}
		return list;
	}

	public Map<String, String> deepSearchModuleTree(Collection<ModuleVO> cols,
			String applicationId, ModuleVO startNode, String excludeNodeId)
			throws Exception {
		Map<String, String> list = new LinkedHashMap<String, String>();

		if (startNode != null) {
			list.put(startNode.getId(), startNode.getName());
		}

		Iterator<ModuleVO> iter = cols.iterator();
		while (iter.hasNext()) {
			ModuleVO vo = iter.next();

			if (applicationId == null || vo.getApplication() == null
					|| !applicationId.equals(vo.getApplication().getId())) {
				continue;
			}

			if (startNode == null) {
				if (vo.getSuperior() == null) {
					if (vo.getId() != null && !vo.getId().equals(excludeNodeId)) {
						Map<String, String> tmp = deepSearchModuleTree(cols,
								applicationId, vo, excludeNodeId);
						list.putAll(tmp);
					}
				}
			} else {
				if (vo.getSuperior() != null
						&& vo.getSuperior().getId().equals(startNode.getId())) {
					if (vo.getId() != null && !vo.getId().equals(excludeNodeId)) {
						Map<String, String> tmp = deepSearchModuleTree(cols,
								applicationId, vo, excludeNodeId);
						list.putAll(tmp);
					}
				}
			}
		}
		return list;
	}

	public String[] deepSearchModuleid(String applicationId, String moduleId)
			throws Exception {
		ApplicationProcess ap = (ApplicationProcess) ProcessFactory
				.createProcess(ApplicationProcess.class);
		ApplicationVO app = (ApplicationVO) ap.doView(applicationId);

		Collection<ModuleVO> colls = app.getModules();

		ModuleVO moduleVO = (ModuleVO) this.doView(moduleId);

		if (moduleVO.getSuperior() != null) { // 从最上层的module开始deepSearch
			return deepSearchModuleid(applicationId, moduleVO.getSuperior()
					.getId());
		} else {
			Map<String, String> subModules = this.deepSearchModuleTree(colls,
					applicationId, moduleVO, null);

			String[] idList = new String[subModules.size()];

			int count = 0;
			for (Iterator<String> iter = subModules.keySet().iterator(); iter
					.hasNext();) {
				String id = iter.next();
				idList[count] = id;
				count++;
			}
			return idList;
		}
	}

	public Collection<ModuleVO> deepSearchModule(Collection<ModuleVO> colls,
			ModuleVO startNode, String excludeNodeId, int deep)
			throws Exception {
		Collection<ModuleVO> rtn = new ArrayList<ModuleVO>();

		if (deep < 0) {
			return rtn;
		}

		if (startNode != null && !startNode.getId().equals(excludeNodeId)) {
			rtn.add(startNode);
		}

		Iterator<ModuleVO> iter = colls.iterator();
		while (iter.hasNext()) {
			ModuleVO vo = iter.next();

			if (startNode == null) {
				if (vo.getSuperior() == null) {
					if (vo.getId() != null && !vo.getId().equals(excludeNodeId)) {
						Collection<ModuleVO> idList = deepSearchModule(colls,
								vo, excludeNodeId, deep - 1);

						rtn.addAll(idList);
					}
				}
			} else {
				if (vo.getSuperior() != null
						&& vo.getSuperior().getId().equals(startNode.getId())) {
					if (vo.getId() != null && !vo.getId().equals(excludeNodeId)) {
						Collection<ModuleVO> idList = deepSearchModule(colls,
								vo, excludeNodeId, deep - 1);

						rtn.addAll(idList);
					}
				}
			}
		}

		return rtn;
	}

	public Collection<ModuleVO> getModuleByApplication(String application)
			throws Exception {
		return ((ModuleDAO) getDAO()).getModuleByApplication(application);
	}

	/**
	 * 检查是否有下级目录,如果有删除下级
	 * 
	 * @param moduleid
	 * @param application
	 * @throws Exception
	 */
	public void deleteModule(String moduleid, String application)
			throws Exception {
		try {
			PersistenceUtils.beginTransaction();
			Collection<BillDefiVO> flowlist = billDefiProcess
					.getBillDefiByModule(moduleid);
			if (flowlist != null && !flowlist.isEmpty()) {
				billDefiProcess.doRemove(flowlist);
			}
			Collection<View> viewlist = viewProcess.getViewsByModule(moduleid,
					application);
			if (viewlist != null && !viewlist.isEmpty()) {
				viewProcess.doRemove(viewlist);
				resProcess.doRemoveByViewList(viewlist, application);
			}
			Collection<Form> formList = formProcess.getFormsByModule(moduleid,
					application);
			if (formList != null && !formList.isEmpty()) {
				formProcess.doRemove(formList);
			}
			Collection<Task> taskList = taskProcess.getTaskByModule(
					application, moduleid);
			if (taskList != null && !taskList.isEmpty()) {
				for (Iterator<Task> it = taskList.iterator(); it != null
						&& it.hasNext();)
					taskProcess.doRemove(it.next());
			}
			Collection<ImageRepositoryVO> irList = imageRepositoryProcess
					.getImageRepositoryByModule(moduleid, application);
			if (irList != null && !irList.isEmpty()) {
				for (Iterator<ImageRepositoryVO> it = irList.iterator(); it != null
						&& it.hasNext();)
					imageRepositoryProcess.doRemove(it.next());
			}
			Collection<StyleRepositoryVO> srList = styleRepositoryProcess
					.getStyleRepositoryByModule(moduleid, application);
			if (srList != null && !srList.isEmpty()) {
				for (Iterator<StyleRepositoryVO> it = srList.iterator(); it != null
						&& it.hasNext();)
					styleRepositoryProcess.doRemove(it.next());
			}
			this.doRemove(moduleid);
			PersistenceUtils.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			PersistenceUtils.rollbackTransaction();
			throw e;
		}
	}

	public void deleteModules(String[] moduleids, String application)
			throws Exception {
		try {
			StringBuffer errorMsg = new StringBuffer();
			if (moduleids != null && moduleids.length > 0) {
				for (int i = 0; i < moduleids.length; i++) {
					try {
						deleteModule(moduleids[i], application);
					} catch (Exception e) {
						errorMsg.append(e.getMessage() + ";");
					}
				}
				if (errorMsg.lastIndexOf(";") != -1) {
					errorMsg.deleteCharAt(errorMsg.lastIndexOf(";"));
				}
				if (errorMsg.length() > 0) {
					throw new Exception(errorMsg.toString());
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}

	public ModuleVO getModuleByName(String name, String application)
			throws Exception {
		return ((ModuleDAO) getDAO()).findByName(name, application);
	}
}
