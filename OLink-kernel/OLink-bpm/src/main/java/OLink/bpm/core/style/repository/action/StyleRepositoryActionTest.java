package OLink.bpm.core.style.repository.action;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.core.deploy.module.action.ModuleAction;
import OLink.bpm.core.deploy.module.ejb.ModuleVO;
import OLink.bpm.core.style.repository.ejb.StyleRepositoryVO;
import OLink.bpm.core.deploy.application.action.ApplicationAction;
import junit.framework.TestCase;

/**
 * 
 * @author yecp
 * 
 */
public class StyleRepositoryActionTest extends TestCase {

	private StyleRepositoryAction action;

	private ApplicationAction actionApp;

	private ModuleAction actionModule;

	protected void setUp() throws Exception {
		action = new StyleRepositoryAction();
		actionApp = new ApplicationAction();
		actionModule = new ModuleAction();
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testDoSave() {
		try {
			// add a Helper
			// PersistenceUtils.getSessionSignal().sessionSignal++;
			ApplicationVO app = new ApplicationVO();
			app.setName("yecpTest");
			actionApp.setContent(app);
			// PersistenceUtils.getSessionSignal().sessionSignal--;
			actionApp.doSave();

			// PersistenceUtils.getSessionSignal().sessionSignal++;
			ModuleVO module = new ModuleVO();
			module.setName("yecpModule");
			module.setApplication(app);
			Collection<ModuleVO> moduleColl = new HashSet<ModuleVO>();
			moduleColl.add(module);
			app.setModules(moduleColl);
			actionModule.setContent(module);
			// PersistenceUtils.getSessionSignal().sessionSignal--;
			actionModule.doSave();

			// PersistenceUtils.getSessionSignal().sessionSignal++;
			StyleRepositoryVO vo = new StyleRepositoryVO();
			// vo.setApplication(app);
			// vo.setModule(module);
			vo.setName("styletest");
			vo.setContent("style1");
			action.setContent(vo);
			// PersistenceUtils.getSessionSignal().sessionSignal--;
			action.doSave();

			// PersistenceUtils.getSessionSignal().sessionSignal++;
			StyleRepositoryVO vo2 = new StyleRepositoryVO();
			// vo2.setApplication(app);
			// vo2.setModule(module);
			vo2.setName("styletest2");
			vo2.setContent("style2");
			action.setContent(vo2);
			// PersistenceUtils.getSessionSignal().sessionSignal--;
			action.doSave();

			// get ID
			String[] id = { vo.getId() };
			Map<String, String[]> params = new HashMap<String, String[]>();
			params.put("id", id);
			String[] id2 = { vo2.getId() };

			// find it by id
			StyleRepositoryAction.getContext().setParameters(params);
			action.doView();
			StyleRepositoryVO vo2e = (StyleRepositoryVO) action.getContent();
			assertEquals("styletest", vo2e.getName());
			assertEquals("style1", vo2e.getContent());

			// find by StyleRepositoryHelper
			String styleContent = StyleRepositoryHelper.getStyleContent(vo.getId());
			assertEquals("style1", styleContent);
			StyleRepositoryHelper helpU = new StyleRepositoryHelper();
			helpU.setModuleid(module.getId());
			// Collection styleList = helpU.get_listStyle(null);
			// StyleRepositoryVO voh = (StyleRepositoryVO)
			// styleList.toArray()[0];
			// StyleRepositoryVO voh2 = (StyleRepositoryVO)
			// styleList.toArray()[1];
			//			
			// assertEquals("styletest", voh.getName());
			// assertEquals("styletest2", voh2.getName());
			//			
			// StyleRepositoryHelper help = new StyleRepositoryHelper();
			// Collection styleList2 = help.get_listStyleByApp(null);
			// assertTrue(styleList2.size() >= 2);

			// and edit it
			// vo2e.setName("style edit");
			// vo2e.setContent("style edit");
			// action.setContent(vo2e);
			// action.doSave();

			StyleRepositoryAction.getContext().setParameters(params);
			action.doView();
			// find it by ID
			StyleRepositoryVO vo2p = (StyleRepositoryVO) action.getContent();
			assertEquals("style edit", vo2p.getName());
			assertEquals("style edit", vo2p.getContent());
			// then delete then
			action.set_selects(id);
			action.doDelete();
			action.doView();
			assertNull(action.getContent());
			action.set_selects(id2);
			action.doDelete();
			action.doView();
			assertNull(action.getContent());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
