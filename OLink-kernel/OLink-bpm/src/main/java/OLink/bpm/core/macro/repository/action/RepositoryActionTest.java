package OLink.bpm.core.macro.repository.action;

import java.util.Collection;
import java.util.HashMap;

import OLink.bpm.base.action.BaseAction;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.core.deploy.module.ejb.ModuleVO;
import OLink.bpm.core.macro.repository.ejb.RepositoryVO;
import OLink.bpm.core.deploy.application.action.ApplicationAction;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.core.deploy.module.action.ModuleAction;
import eWAP.core.Tools;
import junit.framework.TestCase;

public class RepositoryActionTest extends TestCase {

	   RepositoryAction action;
	   ModuleAction moduleaction;
	   ApplicationAction appaction;
	   String name=null;
	   RepositoryVO rep=new RepositoryVO();
	   ApplicationVO appvo=new ApplicationVO();
	   ModuleVO movo=new ModuleVO();
	   String applicationid=null;
	   String  moduleid=null;
	protected void setUp() throws Exception {
		super.setUp();
		action=new RepositoryAction();
		moduleaction=new ModuleAction();
		appaction=new ApplicationAction();
		
		
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Test method for 'RepositoryAction.doSave()'
	 */
	public void testDoSave() throws Exception{
		applicationid=Tools.getSequence();
		appvo.setId(applicationid);
		name=Tools.getSequence()+"name";
		rep.setName(name);
		
		moduleid=Tools.getSequence();
		movo.setId(moduleid);
		
		action.setContent(rep);
		
	//	action.set_applicationid(appvo.getId());
//		action.set_moduleid(movo.getId());
		action.doSave();
		doView();
		doList();
		doEdit();
		doDelete();
	
		
		

	}

	/*
	 * Test method for 'BaseAction.doNew()'
	 */
	public void testDoNew() {

	}

	/*
	 * Test method for 'BaseAction.doEdit()'
	 */
	public void doEdit() throws Exception {

        String id = action.getContent().getId();
		
		HashMap<String, Object> mp = new HashMap<String, Object>();
		mp.put("id", new String[] { id });

		BaseAction.getContext().setParameters(mp);
		action.doEdit();
	}

	/*
	 * Test method for 'BaseAction.doView()'
	 */
	public void doView() throws Exception{

		String id = action.getContent().getId();

		HashMap<String, Object> mp = new HashMap<String, Object>();
		mp.put("id", new String[] { id });

		BaseAction.getContext().setParameters(mp);
		action.doView();
	}

	
	/*
	 * Test method for 'BaseAction.doList()'
	 */
	public void doList() throws Exception{
		HashMap<String, Object> mp = new HashMap<String, Object>();
		mp.put("s_name", name);
		BaseAction.getContext().setParameters(mp);
		action.doList();
		moduleaction.doList();
		appaction.doList();
		Collection<RepositoryVO> data = action.getDatas().datas;
		assertNotNull(data);
		RepositoryVO repvo= data.iterator().next();
		assertEquals(repvo.getName(), name);

	}
	/*
	 * Test method for 'BaseAction.doDelete()'
	 */
	public void doDelete()throws Exception {
		//PersistenceUtils.getSessionSignal().sessionSignal++;
		String id = action.getContent().getId();

		action.set_selects(new String[] { id });
		action.doDelete();
       
		//PersistenceUtils.getSessionSignal().sessionSignal--;
		PersistenceUtils.closeSession();


	}

}
