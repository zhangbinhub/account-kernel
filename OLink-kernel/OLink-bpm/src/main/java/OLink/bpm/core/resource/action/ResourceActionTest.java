package OLink.bpm.core.resource.action;

import java.util.Collection;
import java.util.HashMap;

import OLink.bpm.base.action.BaseAction;
import OLink.bpm.core.resource.ejb.ResourceVO;
import junit.framework.TestCase;

public class ResourceActionTest extends TestCase {

	private ResourceAction action;
	private ResourceVO vo=new ResourceVO();
	private String description=null;
	protected void setUp() throws Exception {
		super.setUp();
		action=new ResourceAction();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
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
	 * Test method for 'BaseAction.doSave()'
	 */
	public void testDoSave() throws Exception {
		description="testxx11";
        vo.setDescription(description);
		action.setContent(vo);
		action.doSave();
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

		HashMap<String, Object> mp = new HashMap<String, Object>();
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
		
		ResourceVO r1=new ResourceVO();
		ResourceVO r2=new ResourceVO();
		
		action.setContent(r1);
		action.doSave();
		
		r2.setSuperior(r1);
		action.setContent(r2);
		action.doSave();
		
		
		boolean flag=false;
	    try {
	    	action.set_selects(new String[] { r1.getId() });
			action.doDelete();
		} catch (Exception e) {
		    flag=true;	
		}
		assertTrue(flag);
		
		
		boolean flag1=false;
	    try {
	    	action.set_selects(new String[] { r2.getId() });
			action.doDelete();
		} catch (Exception e) {
		    flag1=true;	
		}
		assertTrue(!flag1);
		
		action.set_selects(new String[] { r1.getId() });
		action.doDelete();
		
	}

	/*
	 * Test method for 'BaseAction.doList()'
	 */
	public void doList() throws Exception {
		HashMap<String, Object> mp = new HashMap<String, Object>();
		mp.put("s_description", description);
		BaseAction.getContext().setParameters(mp);
		action.doList();
		Collection<ResourceVO> data = action.getDatas().datas;
		assertNotNull(data);
		ResourceVO vo = data.iterator().next();
		assertEquals(vo.getDescription(), description);

	}
	
	public void getSubMenusTest() throws Exception {
//		String topMenuid = "11de-829b-663f9c64-91df-a5701ffbaeaf";
//		String appid = "11de-8299-24a47b9e-91df-a5701ffbaeaf";
//		String domainid = "11de-829b-981ef1f5-91df-a5701ffbaeaf";
		
		//Collection list = action.getSubMenus(topMenuid, appid, domainid);
	}

}
