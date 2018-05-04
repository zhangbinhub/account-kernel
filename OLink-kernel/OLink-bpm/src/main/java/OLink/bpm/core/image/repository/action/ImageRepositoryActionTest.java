package OLink.bpm.core.image.repository.action;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import OLink.bpm.core.deploy.module.ejb.ModuleVO;
import OLink.bpm.core.deploy.application.action.ApplicationAction;
import OLink.bpm.core.image.repository.ejb.ImageRepositoryVO;
import junit.framework.TestCase;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.core.deploy.module.action.ModuleAction;

/**
 * 
 * @author yecp
 * 
 */
public class ImageRepositoryActionTest extends TestCase {

	private ImageRepositoryAction action;

	private ApplicationAction actionApp;

	private ModuleAction actionModule;

	protected void setUp() throws Exception {
		action = new ImageRepositoryAction();
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
			//PersistenceUtils.getSessionSignal().sessionSignal++;
			ApplicationVO app = new ApplicationVO();
			app.setName("yecpTest");
			actionApp.setContent(app);
			//PersistenceUtils.getSessionSignal().sessionSignal--;
			actionApp.doSave();

			//PersistenceUtils.getSessionSignal().sessionSignal++;
			ModuleVO module = new ModuleVO();
			module.setName("yecpTestModule");
			module.setApplication(app);
			Collection<ModuleVO> moduleColl = new HashSet<ModuleVO>();
			moduleColl.add(module);
			app.setModules(moduleColl);
			actionModule.setContent(module);
			//PersistenceUtils.getSessionSignal().sessionSignal--;
			actionModule.doSave();

			//PersistenceUtils.getSessionSignal().sessionSignal++;
			ImageRepositoryVO vo = new ImageRepositoryVO();
			//vo.setApplication(app);
			vo.setModule(module);
			vo.setName("imagetest");
			vo.setContent("/imageupload");
			action.setContent(vo);
			//PersistenceUtils.getSessionSignal().sessionSignal--;
			action.doSave();

			//PersistenceUtils.getSessionSignal().sessionSignal++;
			ImageRepositoryVO vo2 = new ImageRepositoryVO();
			//vo2.setApplication(app);
			vo2.setModule(module);
			vo2.setName("imagetest2");
			vo2.setContent("/imageupload2");
			action.setContent(vo2);
			//PersistenceUtils.getSessionSignal().sessionSignal--;
			action.doSave();

			// get ID
			String[] id = { vo.getId() };
			Map<String, String[]> params = new HashMap<String, String[]>();
			params.put("id", id); // find it by id
			ImageRepositoryAction.getContext().setParameters(params);
			action.doView();
			ImageRepositoryVO vo2e = (ImageRepositoryVO) action.getContent();
			assertEquals("imagetest", vo2e.getName());
			assertEquals("/imageupload", vo2e.getContent());
			// find by ImageRepositoryHelper

			String imageContent = ImageRepositoryHelper.getImageContent(vo
					.getId());

			assertEquals("/imageupload", imageContent);
			ImageRepositoryHelper helpMe = new ImageRepositoryHelper();
			helpMe.setModuleid(module.getId());
			Map<String, String> imageList = helpMe.get_listImage(null);
			assertEquals("imagetest", imageList.get(vo.getId()));
			assertEquals("imagetest2", imageList.get(vo2.getId()));

			// and edit it
			vo2e.setName("image edit");
			vo2e.setContent("/imageupload edit");
			action.setContent(vo2e);
			action.doSave();

			ImageRepositoryAction.getContext().setParameters(params);
			action.doView();//
			// find it by ID
			ImageRepositoryVO vo2p = (ImageRepositoryVO) action.getContent();
			assertEquals("image edit", vo2p.getName());
			assertEquals("/imageupload edit", vo2p.getContent());
			// then delete it
			action.set_selects(id);
			action.doDelete();
			action.doView();
			assertNull(action.getContent());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
