package OLink.bpm.core.report.query.action;

import java.util.Collection;
import java.util.HashMap;

import OLink.bpm.base.action.BaseAction;
import OLink.bpm.core.report.query.ejb.Query;
import junit.framework.TestCase;

public class QueryActionTest extends TestCase {

	QueryAction action=null;
	Query vo=null;
	private String _paramsDefaultValue[];
	private String _paramsName[];
	String name=null;
	protected void setUp() throws Exception {
		super.setUp();
		action=new QueryAction();
		vo=new Query();
		_paramsDefaultValue=new String[2];
		_paramsName=new String[2];
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Test method for 'QueryAction.doSave()'
	 */
	public void testDoSave() throws Exception {
		//QueryHelper helper=new QueryHelper();
		name="thrive";
		vo.setName(name);
		vo.setQueryString("select * from t_user where uesrname like $P{name} and  password  like $P{pwd}");
		action.setContent(vo);
        action.doSave();
		
        //Collection params=helper.getParametersBySQL(vo.getQueryString());  
        _paramsName=new String[]{"name","pwd"};
        _paramsDefaultValue=new String[]{"peng","peng"};

    	//HashMap mp = new HashMap();
        action.set_paramsDefaultValue(_paramsDefaultValue);
        action.set_paramsName(_paramsName);
        action.setContent(vo);
        action.doSave();
        doDelete();
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
	public void doDoList() throws Exception {
		HashMap<String, String> mp = new HashMap<String, String>();
		mp.put("s_name", name);
		BaseAction.getContext().setParameters(mp);
		action.doList();
		Collection<?> data = action.getDatas().datas;
		assertNotNull(data);
		Query ds = (Query) data.iterator().next();
		assertEquals(ds.getName(), name);
	}

}
