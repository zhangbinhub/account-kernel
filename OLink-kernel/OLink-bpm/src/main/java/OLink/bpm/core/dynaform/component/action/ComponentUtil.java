package OLink.bpm.core.dynaform.component.action;

import OLink.bpm.core.dynaform.component.ejb.Component;
import OLink.bpm.core.dynaform.component.ejb.ComponentProcess;
import OLink.bpm.util.ProcessFactory;

public class ComponentUtil {
	public String getTemplateContentById(String id) throws Exception {
		ComponentProcess process = (ComponentProcess) ProcessFactory
				.createProcess(ComponentProcess.class);

		Component vo = (Component) process.doView(id);
		if (vo != null) {
			return vo.getTemplatecontext();
		} else {
			return "";
		}
	}
}
