package OLink.bpm.core.workflow.statelabel.action;

import java.util.Collection;
import java.util.HashMap;

import OLink.bpm.base.action.BaseAction;
import junit.framework.TestCase;
import OLink.bpm.core.workflow.statelabel.ejb.StateLabel;

public class StateLabelActionTest extends TestCase {
	StateLabel statelable = new StateLabel();

	StateLabelAction action;

	String orderno;

	protected void setUp() throws Exception {
		super.setUp();
		action = new StateLabelAction();

	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Test method for 'BaseAction.doNew()'
	 */
	public void testDoNew() throws Exception {

	}

	/*
	 * Test method for 'BaseAction.doSave()'
	 */

	public void testDoSave() throws Exception {

		orderno = "1111";

		statelable.setOrderNo(orderno);
		action.setContent(statelable);
		action.doSave();
		doView();
		doList();
		doEdit();
		doDelete();

	}

	/*
	 * Test method for 'OLink.bpm.core.bug.action.CommonInfoAction.doView()'
	 */
	public void doView() throws Exception {
		String id = action.getContent().getId();

		HashMap<String, String[]> mp = new HashMap<String, String[]>();
		mp.put("id", new String[] { id });

		BaseAction.getContext().setParameters(mp);
		action.doView();

	}

	/*
	 * Test method for 'OLink.bpm.core.bug.action.CommonInfoAction.doList()'
	 */
	public void doList() throws Exception {

		HashMap<String, String> mp = new HashMap<String, String>();
		mp.put("s_orderNo", orderno);
		BaseAction.getContext().setParameters(mp);
		action.doList();
		Collection<StateLabel> data = action.getDatas().datas;
		assertNotNull(data);
		StateLabel com = data.iterator().next();
		assertEquals(com.getOrderNo(), orderno);

	}

	/*
	 * Test method for 'OLink.bpm.core.bug.action.CommonInfoAction.doEdit()'
	 */
	public void doEdit() throws Exception {

		String id = action.getContent().getId();

		HashMap<String, String[]> mp = new HashMap<String, String[]>();
		mp.put("id", new String[] { id });

		BaseAction.getContext().setParameters(mp);
		action.doEdit();

	}

	/*
	 * Test method for 'OLink.bpm.core.bug.action.CommonInfoAction.doDelete()'
	 */
	public void doDelete() throws Exception {

		String id = action.getContent().getId();
		action.set_selects(new String[] { id });
		action.doDelete();
		StateLabelAction actions = new StateLabelAction();
		HashMap<String, String> mp = new HashMap<String, String>();
		mp.put("s_orderNo", orderno);
		BaseAction.getContext().setParameters(mp);
		actions.doList();
		Collection<StateLabel> data = actions.getDatas().datas;

		assertFalse(data.size() > 0);

	}

}
