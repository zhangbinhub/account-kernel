package OLink.bpm.core.dynaform.view.action;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.core.dynaform.view.ejb.Column;
import OLink.bpm.core.dynaform.view.ejb.ViewProcess;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.web.DWRHtmlUtils;

public class ViewUtil {
	public Map<String, String> getViewsByModule(String moduleid, String application) throws Exception {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("none", "Select");
		// //PersistenceUtils.getSessionSignal().sessionSignal++;
		ViewProcess process = (ViewProcess) ProcessFactory.createProcess(ViewProcess.class);
		Collection<View> datas = process.getViewsByModule(moduleid, application);
		Iterator<View> it = datas.iterator();
		while (it.hasNext()) {
			View vo = it.next();
			map.put(vo.getId(), vo.getName());
		}
		// //PersistenceUtils.getSessionSignal().sessionSignal--;
		PersistenceUtils.closeSession();
		return map;
	}

	public Map<String, String> getColsByView(String viewid) throws Exception {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("", "{*[Select]*}");
		// //PersistenceUtils.getSessionSignal().sessionSignal++;
		ViewProcess process = (ViewProcess) ProcessFactory.createProcess(ViewProcess.class);
		View view = (View) process.doView(viewid);
		if (view != null) {
			Iterator<Column> iter = view.getColumns().iterator();
			while (iter.hasNext()) {
				Column col = iter.next();
				map.put(col.getId(), col.getName());
			}
			// //PersistenceUtils.getSessionSignal().sessionSignal--;
			PersistenceUtils.closeSession();
		}
		return map;

	}

	public String creatView(String selectFieldName, String moduleid, String def, String application) throws Exception {
		Map<String, String> map = getViewsByModule(moduleid, application);
		return DWRHtmlUtils.createOptions(map, selectFieldName, def);
	}

	public String creatCol(String selectFieldName, String viewid, String def, String application) throws Exception {
		Map<String, String> map = getViewsByModule(viewid, application);
		return DWRHtmlUtils.createOptions(map, selectFieldName, def);
	}
}
