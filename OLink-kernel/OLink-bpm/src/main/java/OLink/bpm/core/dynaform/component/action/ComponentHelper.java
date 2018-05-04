package OLink.bpm.core.dynaform.component.action;

import OLink.bpm.base.action.BaseHelper;
import OLink.bpm.core.dynaform.component.ejb.Component;
import OLink.bpm.core.dynaform.component.ejb.ComponentProcess;
import OLink.bpm.util.ProcessFactory;

public class ComponentHelper extends BaseHelper<Component> {

	/**
	 * @SuppressWarnings 工厂方法无法使用泛型
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public ComponentHelper() throws ClassNotFoundException {
		super(ProcessFactory.createProcess(ComponentProcess.class));
	}

}
