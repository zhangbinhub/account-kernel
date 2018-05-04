package OLink.bpm.core.expimp.exp.action;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.core.deploy.application.ejb.ApplicationProcess;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.core.deploy.module.ejb.ModuleVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.web.DWRHtmlUtils;

public class ExpUtil {
	public Map<String, String> getApplication(String application) throws Exception {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("none", "All");
		//PersistenceUtils.getSessionSignal().sessionSignal++;
		ApplicationProcess ap = (ApplicationProcess) ProcessFactory
				.createProcess(ApplicationProcess.class);
		Collection<ApplicationVO> datas = ap.doSimpleQuery(null, application);
		Iterator<ApplicationVO> it = datas.iterator();
		while (it.hasNext()) {
			ApplicationVO av = it.next();
			map.put(av.getId(), av.getName());
		}
		//PersistenceUtils.getSessionSignal().sessionSignal--;
		PersistenceUtils.closeSession();
		return map;
	}

	public Map<String, String> deepSearchModuleTree(Collection<ModuleVO> cols, String applicationId,
													ModuleVO startNode, String excludeNodeId, int deep)
			throws Exception {
		Map<String, String> list = new LinkedHashMap<String, String>();
		list.put("none", "All");
		if (applicationId == null || applicationId.equals(""))
			return list;

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
						Map<String, String> tmp = deepSearchModuleTree(cols, applicationId, vo,
								excludeNodeId, deep + 1);
						list.putAll(tmp);
					}
				}
			} else {
				if (vo.getSuperior() != null
						&& vo.getSuperior().getId().equals(startNode.getId())) {
					if (vo.getId() != null && !vo.getId().equals(excludeNodeId)) {
						Map<String, String> tmp = deepSearchModuleTree(cols, applicationId, vo,
								excludeNodeId, deep + 1);
						list.putAll(tmp);
					}
				}
			}
		}
		return list;
	}

	public String creatApplication(String selectFieldName, String def, String application)
			throws Exception {
		Map<String, String> map = getApplication(application);
		return DWRHtmlUtils.createOptions(map, selectFieldName, def);
	}

	public String creatModule(String selectFieldName, String applicationid,
			String def) throws Exception {
		//PersistenceUtils.getSessionSignal().sessionSignal++;
		ApplicationProcess ap = (ApplicationProcess) ProcessFactory
				.createProcess(ApplicationProcess.class);
		ApplicationVO av = (ApplicationVO) ap.doView(applicationid);

		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("none", "Select");
		if (applicationid != null && !applicationid.equals("none")
				&& !applicationid.equals(""))
			map = deepSearchModuleTree(av.getModules(), applicationid, null,
					null, 0);
		//PersistenceUtils.getSessionSignal().sessionSignal--;
		PersistenceUtils.closeSession();
		return DWRHtmlUtils.createOptions(map, selectFieldName, def);
	}
}
