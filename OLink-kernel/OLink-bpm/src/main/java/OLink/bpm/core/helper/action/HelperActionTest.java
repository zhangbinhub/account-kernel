package OLink.bpm.core.helper.action;

import java.util.HashMap;
import java.util.Map;

import OLink.bpm.base.action.BaseAction;
import junit.framework.TestCase;
import OLink.bpm.core.helper.ejb.HelperVO;
/**
 * 
 * @author yecp
 *
 */
public class HelperActionTest extends TestCase {

	private HelperAction action;

	

	protected void setUp() throws Exception {
		super.setUp();
		action = new HelperAction();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testDoSave() {
		try {
			//add a Helper
			HelperVO vo = new HelperVO();
			vo.setUrl("hero");
			vo.setTitle("test yecp");
			vo.setContext("test test");
			action.setContent(vo);
			action.doSave();

		
			//find it byName
			HelperVO vo2p = action.getHelperByname("hero", null);
			assertEquals("hero",vo2p.getUrl());
			assertEquals("test yecp",vo2p.getTitle());
			assertEquals("test test",vo2p.getContext());		
			String[] id={vo2p.getId()};//get its ID
			
			//and edit it
			vo2p.setUrl("hero edit");
			vo2p.setTitle("yecp edit");
			vo2p.setContext("test edit");
			action.setContent(vo2p);
			action.doSave();
			Map<String, String[]> params=new HashMap<String, String[]>();
			params.put("id",id);
			BaseAction.getContext().setParameters(params);
			action.doView();//find it by ID
			HelperVO vo2e=(HelperVO)action.getContent();
			assertEquals("hero edit",vo2e.getUrl());
			assertEquals("yecp edit",vo2e.getTitle());
			assertEquals("test edit",vo2e.getContext());
			
			//then delete it
			action.set_selects(id);
			action.doDelete();
			action.doView();
			assertNull(action.getContent());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	

}
