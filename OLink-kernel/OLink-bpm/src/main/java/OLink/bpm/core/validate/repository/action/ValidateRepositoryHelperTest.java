package OLink.bpm.core.validate.repository.action;

import java.util.Map;

import OLink.bpm.core.deploy.module.ejb.ModuleProcess;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.validate.repository.ejb.ValidateRepositoryVO;
import junit.framework.TestCase;
import OLink.bpm.core.validate.repository.ejb.ValidateRepositoryProcess;

public class ValidateRepositoryHelperTest extends TestCase {

	ValidateRepositoryHelper helper = null;

	protected void setUp() throws Exception {
		super.setUp();
		helper = new ValidateRepositoryHelper();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Test method for
	 * 'ValidateRepositoryHelper.get_validate()'
	 */
	public void testGet_validate() throws Exception {
		//String moduleid = null;
		ModuleProcess dp = (ModuleProcess) ProcessFactory
				.createProcess(ModuleProcess.class);
		ValidateRepositoryProcess vp = (ValidateRepositoryProcess) ProcessFactory
				.createProcess(ValidateRepositoryProcess.class);
		//Collection coll = dp.doSimpleQuery(null, null);
		//ModuleVO mod = (ModuleVO) coll.iterator().next();
		// helper.set_moduleid(mod.getId());
		// Map map = helper.get_validate();
		Map<String, String> map = new ValidateRepositoryHelper()
				.get_validate("11de-96ab-1cd52bcd-979f-7d180a5b557b");
		if (map != null) {
			assertEquals(1, map.size());
		}
		ValidateRepositoryVO vo = new ValidateRepositoryVO();
		vo.setName("name");
		// vo.setModule(mod);
		// vo.setApplication(mod.getApplication());
		vp.doCreate(vo);

		// Map map1 = helper.get_validate();
		// if (map == null)
		// assertEquals(1, map1.size());
		// else
		// assertEquals(map.size() + 1, map1.size());
		dp.doRemove(vo.getId());
		helper.get_validate(vo.getApplicationid());

	}

}
