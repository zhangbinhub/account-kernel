package OLink.bpm.core.homepage.action;

import OLink.bpm.base.action.BaseHelper;
import OLink.bpm.core.homepage.ejb.HomePageProcess;
import OLink.bpm.core.homepage.ejb.HomePage;
import OLink.bpm.util.ProcessFactory;

public class HomePageHelper extends BaseHelper<HomePage> {

	/**
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public HomePageHelper() throws Exception {
		super(ProcessFactory.createProcess(HomePageProcess.class));
	}


}
