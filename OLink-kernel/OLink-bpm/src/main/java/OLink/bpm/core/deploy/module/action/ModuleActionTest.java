package OLink.bpm.core.deploy.module.action;

import java.util.Collection;
import java.util.HashMap;

import OLink.bpm.base.action.BaseAction;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.core.deploy.module.ejb.ModuleVO;
import OLink.bpm.core.deploy.application.action.ApplicationAction;
import junit.framework.TestCase;
import eWAP.core.Tools;

/**
 * @author nicholas
 */
public class ModuleActionTest extends TestCase {

	ModuleAction action;
	ModuleAction action2;
	ApplicationAction appaction;
	String name1 = null;
	String name2 = null;
	String appname = null;
	ModuleVO movo1 = new ModuleVO();
	ModuleVO movo2 = new ModuleVO();

	ApplicationVO appvo = new ApplicationVO();

	public static void main(String[] args) {
		junit.textui.TestRunner.run(ModuleActionTest.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		action = new ModuleAction();
		action2 = new ModuleAction();
		appaction = new ApplicationAction();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Test method for 'BaseAction.doSave()'
	 */
	public void testDoSave() throws Exception {

		// //PersistenceUtils.getSessionSignal().sessionSignal++;
		name1 = Tools.getSequence() + "ModuleName111";
		name2 = Tools.getSequence() + "ModuleName222";
		appname = "ApplicationName";
		movo1.setName(name1);
		movo2.setName(name2);

		appvo.setName(appname);
		movo1.setApplication(appvo);
		movo2.setApplication(appvo);
		action.setContent(movo1);
		action2.setContent(movo2);
		appaction.setContent(appvo);
		appaction.doSave();
		action.doSave();
		action2.doSave();
		// //PersistenceUtils.getSessionSignal().sessionSignal--;
		PersistenceUtils.closeSession();

		doView();
		doList();
		doEdit();

		doDelete();

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

	}

	/*
	 * Test method for 'BaseAction.doList()'
	 */
	public void doList() throws Exception {

		HashMap<String, String> mp = new HashMap<String, String>();
		mp.put("s_name", name1);
		BaseAction.getContext().setParameters(mp);
		action.doList();
		Collection<ModuleVO> data = action.getDatas().datas;
		assertNotNull(data);
		ModuleVO movos = data.iterator().next();
		assertEquals(movos.getName(), name1);
	}

	/*
	 * Test method for 'BaseAction.doEdit()'
	 */
	public void doEdit() throws Exception {

		String id = action.getContent().getId();

		HashMap<String, String[]> mp = new HashMap<String, String[]>();
		mp.put("id", new String[] { id });

		BaseAction.getContext().setParameters(mp);
		action.doEdit();
	}

	/*
	 * Test method for
	 * 'ApplicationAction.doDelete()'
	 */
	public void doDelete() throws Exception {
		// //PersistenceUtils.getSessionSignal().sessionSignal++;
		String id = action.getContent().getId();
		String id2 = action2.getContent().getId();
		action.set_selects(new String[] { id, id2 });
		action.doDelete();
		String appid = appaction.getContent().getId();

		appaction.set_selects(new String[] { appid });

		appaction.doDelete();
		// //PersistenceUtils.getSessionSignal().sessionSignal--;
		PersistenceUtils.closeSession();

	}

}
