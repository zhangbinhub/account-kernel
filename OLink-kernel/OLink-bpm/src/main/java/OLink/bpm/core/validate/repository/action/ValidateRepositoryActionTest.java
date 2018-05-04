package OLink.bpm.core.validate.repository.action;

import java.util.Collection;
import java.util.HashMap;

import OLink.bpm.base.action.BaseAction;
import OLink.bpm.core.validate.repository.ejb.ValidateRepositoryVO;
import junit.framework.TestCase;

public class ValidateRepositoryActionTest extends TestCase {

	ValidateRepositoryAction action=null;
	ValidateRepositoryVO vo=null;
	String name=null;
	protected void setUp() throws Exception {
		super.setUp();
		action=new ValidateRepositoryAction();
		vo=new ValidateRepositoryVO();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Test method for 'ValidateRepositoryAction.doSave()'
	 */
	public void testDoSave() throws Exception {
		name="test";
		vo.setName(name);
		action.setContent(vo);
		action.doSave();
		doView();
		doList();
		doEdit();
	}

	/*
	 * Test method for 'BaseAction.doEdit()'
	 */
	public void doEdit() {

	}

	/*
	 * Test method for 'BaseAction.doView()'
	 */
	public void doView() throws Exception {
		String id = action.getContent().getId();

		HashMap<String, String[]> mp = new HashMap<String, String[]>();
		mp.put("id", new String[] { id });

		BaseAction.getContext().setParameters(mp);
		action.doView();

		String afterViewId = action.getContent().getId();
		assertEquals(afterViewId, id);
	}

	/*
	 * Test method for 'BaseAction.doDelete()'
	 */
	public void doDelete() throws Exception {
		String id = action.getContent().getId();
		action.set_selects(new String[] { id });
		action.doDelete();

	}

	/*
	 * Test method for 'BaseAction.doList()'
	 */
	public void doList() throws Exception {
		HashMap<String, String> mp = new HashMap<String, String>();
		mp.put("s_name", name);
		BaseAction.getContext().setParameters(mp);
		action.doList();
		Collection<ValidateRepositoryVO> data = action.getDatas().datas;
		assertNotNull(data);
		//DataSource ds = (DataSource) data.iterator().next();
		//assertEquals(ds.getName(), name);

	}

}
