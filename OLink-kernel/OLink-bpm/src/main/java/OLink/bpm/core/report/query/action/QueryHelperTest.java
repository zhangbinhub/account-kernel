package OLink.bpm.core.report.query.action;

import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.core.report.query.ejb.QueryProcess;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.report.query.ejb.Query;
import junit.framework.TestCase;

public class QueryHelperTest extends TestCase {

	QueryHelper helper=null;
	protected void setUp() throws Exception {
		super.setUp();
		helper=new QueryHelper();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Test method for 'QueryHelper.getParameters(String)'
	 */
	public void testGetParameters() throws Exception {
 
		Query vo=new Query();
		vo.setQueryString("select * from t_user where uesrname like $P{name} and  password  like $P{pwd}");
		QueryProcess dp = (QueryProcess) ProcessFactory
		.createProcess(QueryProcess.class);
		dp.doCreate(vo);
		Collection<String> coll=helper.getParameters(vo.getId());
		assertEquals(2, coll.size());
		boolean flag=true;
		for (Iterator<String> iter = coll.iterator(); iter.hasNext();) {
			String temp= iter.next();
			if(!temp.equals("name")&&!temp.equals("pwd"))
				flag=false;
		}
		assertTrue(flag);
		dp.doRemove(vo.getId());
	}

	/*
	 * Test method for 'QueryHelper.getParametersBySQL(String)'
	 */
	public void testGetParametersBySQL() throws Exception {
		String sql="select * from t_user where uesrname like $P{name} and  password  like $P{pwd}";
		Collection<String> coll=helper.getParametersBySQL(sql);
		assertEquals(2, coll.size());
		boolean flag=true;
		for (Iterator<String> iter = coll.iterator(); iter.hasNext();) {
			String temp= iter.next();
			if(!temp.equals("name")&&!temp.equals("pwd"))
				flag=false;
		}
		assertTrue(flag);
	}

}
