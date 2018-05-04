package OLink.bpm.core.homepage.action;


import OLink.bpm.base.action.BaseAction;
import OLink.bpm.core.homepage.ejb.HomePage;
import OLink.bpm.core.homepage.ejb.HomePageProcess;
import OLink.bpm.util.ProcessFactory;

public class HomePageAction extends BaseAction<HomePage> {
//
//	private String tempRoles;
//	/**
//	 * 
//	 */
	private static final long serialVersionUID = -4664734701291763551L;
//
//	/**
//	 * @SuppressWarnings 工厂方法不支持泛型
//	 * @throws ClassNotFoundException
//	 */
	@SuppressWarnings("unchecked")
	public HomePageAction() throws ClassNotFoundException {
		super(ProcessFactory.createProcess(HomePageProcess.class), new HomePage());
	}

}
