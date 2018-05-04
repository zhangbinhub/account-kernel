package OLink.bpm.core.dynaform.dts.datasource.action;

import java.util.Collection;
import java.util.HashMap;

import OLink.bpm.base.action.BaseAction;
import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSource;
import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSourceProcess;
import OLink.bpm.util.ProcessFactory;
import junit.framework.TestCase;

/**
 * @author nicholas
 */
public class DataSourceActionTest extends TestCase {

	String username;

	DataSourceAction action;

	DataSourceProcess dp = null;

	protected void setUp() throws Exception {
		super.setUp();
		username ="testuser";
		action = new DataSourceAction();
		dp = (DataSourceProcess) ProcessFactory
				.createProcess((DataSourceProcess.class));
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Test method for 'BaseAction.doNew()'
	 */
	public void testDoNew() {

	}

	/*
	 * Test method for 'BaseAction.doSave()'
	 */
	public void testDoSave() throws Exception {
		// username = Tools.getSequence() + "data1111";
		//
		// ds.setUsername(username);
		// action.setContent(ds);
		// action.doSave();
		// doView();
		// doList();
		// doEdit();
		// doDelete();
		testdataSource();

	}

	public void testdataSource() throws Exception {
		dp
				.createOrUpdate(
						"domain_dt",
						"Create table t_appcation("
								+ "ID varchar(200), DOC_ID varchar(200), VALUE varchar(200) ,PRIMARY KEY  (ID));",
						"01b7706e-978a-1360-aaad-24150df7b76f");// getApplication();
	}

	/*
	 * Test method for 'BaseAction.doView()'
	 */
	public void doView() throws Exception {

		String id = action.getContent().getId();

		HashMap<String, Object> mp = new HashMap<String, Object>();
		mp.put("id", new String[] { id });

		BaseAction.getContext().setParameters(mp);
		action.doView();

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
	 * Test method for 'BaseAction.doList()'
	 */
	public void doList() throws Exception {
		HashMap<String, Object> mp = new HashMap<String, Object>();
		mp.put("s_username", username);
		BaseAction.getContext().setParameters(mp);
		action.doList();
		Collection<?> data = action.getDatas().datas;
		assertNotNull(data);
		DataSource datasource = (DataSource) data.iterator().next();
		assertEquals(datasource.getUsername(), username);

	}

	/*
	 * Test method for 'BaseAction.doDelete()'
	 */
	public void doDelete() throws Exception {
		String id = action.getContent().getId();
		action.set_selects(new String[] { id });
		action.doDelete();
		DataSourceAction actions = new DataSourceAction();
		HashMap<String, Object> mp = new HashMap<String, Object>();
		mp.put("s_username", username);
		BaseAction.getContext().setParameters(mp);
		actions.doList();
		Collection<?> data = actions.getDatas().datas;
		assertFalse(data.size() > 0);

	}

}
