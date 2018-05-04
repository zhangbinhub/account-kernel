package OLink.bpm.core.dynaform.activity.action;

import java.util.HashMap;

import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.core.dynaform.activity.ejb.Activity;
import OLink.bpm.base.action.BaseAction;
import eWAP.core.Tools;

import junit.framework.TestCase;

/**
 * @author nicholas
 */
public class ActivityActionTest extends TestCase {

	private String iconid = null;
	// private String viewid=null;

	private String name = null;
	ActivityAction action;
	// FormAction formaction;

	Activity avty = new Activity();

	protected void setUp() throws Exception {
		super.setUp();
		action = new ActivityAction();
		// formaction=new FormAction();

	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Test method for
	 * 'ActivityAction.doSave()'
	 */
	public void testDoSave() throws Exception {

		name = Tools.getSequence() + "ActyName";
		avty.setName(name);
		iconid = Tools.getSequence() + "iconid";
		// viewid=Tools.getSequence();
		action.set_iconid(iconid);

		action.setContent(avty);
		action.doSave();
		doView();
		doList();
		doDelete();

	}

	/*
	 * Test method for
	 * 'ActivityAction.doView()'
	 */
	public void doView() throws Exception {
		String id = action.getContent().getId();
		HashMap<String, String[]> mp = new HashMap<String, String[]>();
		mp.put("id", new String[] { id });

		BaseAction.getContext().setParameters(mp);
		action.doView();

	}

	/*
	 * Test method for
	 * 'ActivityAction.doList()'
	 */
	public void doList() throws Exception {
		action.doList();
		// Collection data = action.getDatas().datas;
	}

	/*
	 * Test method for
	 * 'ActivityAction.doList()'
	 */
	public void doDelete() throws Exception {
		// //PersistenceUtils.getSessionSignal().sessionSignal++;
		String id = action.getContent().getId();

		action.set_selects(new String[] { id });

		action.doDelete();
		// //PersistenceUtils.getSessionSignal().sessionSignal--;
		PersistenceUtils.closeSession();

	}

}
