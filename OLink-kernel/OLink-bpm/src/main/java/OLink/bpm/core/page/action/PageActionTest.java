package OLink.bpm.core.page.action;

import java.util.HashMap;
import java.util.Map;

import OLink.bpm.base.action.BaseAction;
import OLink.bpm.core.page.ejb.Page;
import junit.framework.TestCase;

public class PageActionTest extends TestCase {
	PageAction action;

	Map<String, String[]> params = new HashMap<String, String[]>();

	protected void setUp() throws Exception {
		action = new PageAction();
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testAction() throws Exception {
		String id = doSave();
		doEdit(id);
		doDelete(id);
	}

	/*
	 * Test method for 'FormAction.doSave()'
	 */
	public String doSave() throws Exception {
		Page vo = new Page();
		//PersistenceUtils.getSessionSignal().sessionSignal++;
		vo.setName("HomePage");
		vo.setDefHomePage(true);
		vo
				.setTemplatecontext("<FONT face=Arial size=2>\n"
						+ "<DIV style=\"LINE-HEIGHT:10px\">\n"
						+ "<TABLE borderColor=#000000cell Spacing=2 cellPadding=0 width=\"100%\" bgColor=#ffffff border=0 heihgt=\"\">\n"
						+ "<TBODY>\n"
						+ "<TR height=20>\n"
						+ "<TD>\n"
						+ "<P><STRONG><FONT size=4>WelcometoGoldenBear-ASystemforExpenseManagement.</FONT></STRONG></P>\n"
						+ "<P><IMG src=\"/webapp/core/dynaform/form/formeditor/buttonimage/standard/include.gif\" className=\"IncludeField\" type=\"includefield\" refreshOnChanged=\"false\" valueScript=\"@quot;1168324524828000@quot;\" integratePage=\"false\"></P></TD></TR>\n"
						+ "<TR>\n"
						+ "<TD>\n"
						+ "<P align=right>&nbsp;&nbsp;<A href=\"/webapp/core/dynaform/view/displayView.action?_viewid=1165479224530000&amp;isedit=true&amp;\"><FONT face=Arial color=#800080 size=2>more</FONT></A></P></TD></TR>\n"
						+ "<TR height=30>\n"
						+ "<TD><FONT face=Arial size=2><IMG src=\"/webapp/core/dynaform/form/formeditor/buttonimage/standard/include.gif\" className=\"IncludeField\" type=\"includefield\" refreshOnChanged=\"false\" valueScript=\"@quot;1168326859125000@quot;\" integratePage=\"false\"></FONT></TD></TR>\n"
						+ "<TR>\n"
						+ "<TD>\n"
						+ "<P align=right>&nbsp;&nbsp;<A href=\"/webapp/core/dynaform/view/displayView.action?_viewid=1165476187473000&amp;isedit=true&amp;\"><FONT face=Arial color=#800080 size=2>more</FONT></A></P></TD></TR>\n"
						+ "<TR height=20>\n"
						+ "<TD><FONT face=Arialsize=2></FONT></TD></TR>\n"
						+ "<TR>\n"
						+ "<TD>\n"
						+ "<P align=right>&nbsp;</P></TD></TR></TBODY></TABLE></DIV></FONT><A href=\"/webapp/core/dynaform/view/displayView.action?_viewid=1165894246390000&amp;isedit=true&amp;\"></HREF></A>\n"
						+ "<DIV style=\"LINE-HEIGHT:10px\">&nbsp;</DIV>\n");
		action.setContent(vo);
		//PersistenceUtils.getSessionSignal().sessionSignal--;
		action.doSave();

		//PersistenceUtils.getSessionSignal().sessionSignal++;
		params.put("id", new String[] { vo.getId() });
		BaseAction.getContext().setParameters(params);
		//PersistenceUtils.getSessionSignal().sessionSignal--;
		action.doView();
		Page findVO = (Page) action.getContent();

		assertEquals(findVO.getName(), vo.getName());

		return findVO.getId();
	}

	/*
	 * Test method for 'BaseAction.doEdit()'
	 */
	public void doEdit(String id) throws Exception {
		//PersistenceUtils.getSessionSignal().sessionSignal++;
		params.put("id", new String[] { id });
		BaseAction.getContext().setParameters(params);
		action.doView();
		Page oldy = (Page) action.getContent();

		oldy.setName("NewHomePage");
		action.setContent(oldy);
		action.doSave();

		BaseAction.getContext().setParameters(params);
		action.doView();
		Page newly = (Page) action.getContent();

		assertEquals(oldy.getName(), newly.getName());
		//PersistenceUtils.getSessionSignal().sessionSignal--;
	}

	/*
	 * Test method for 'BaseAction.doDelete()'
	 */
	public void doDelete(String id) throws Exception {
		//PersistenceUtils.getSessionSignal().sessionSignal++;
		action.set_selects(new String[] { id });
		action.doDelete();

		params.put("id", new String[] { id });
		BaseAction.getContext().setParameters(params);
		action.doView();
		Page find = (Page) action.getContent();

		assertNull(find);
		//PersistenceUtils.getSessionSignal().sessionSignal--;
	}

}
