package OLink.bpm.core.role.action;

import OLink.bpm.core.role.ejb.RoleVO;
import junit.framework.TestCase;

public class RoleActionTest extends TestCase {
	
	private RoleAction action;

	public static void main(String[] args) {
		junit.textui.TestRunner.run(RoleActionTest.class);
		
	}

	protected void setUp() throws Exception {
		super.setUp();
		action = new RoleAction();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testDoSave() {
		try {
			RoleVO vo = new RoleVO();
			vo.setName("Jarod Role");
			action.setContent(vo);
			action.doSave();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
