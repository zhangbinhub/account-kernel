package OLink.bpm.core.dynaform.component.action;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import OLink.bpm.base.action.BaseAction;
import OLink.bpm.core.dynaform.component.ejb.Component;
import OLink.bpm.core.dynaform.form.ejb.FormField;
import junit.framework.TestCase;

/**
 * @author   nicholas
 */
public class ComponentActionTest extends TestCase {
	ComponentAction action;

	Map<String, String[]> params = new HashMap<String, String[]>();

	protected void setUp() throws Exception {
		action = new ComponentAction();
		super.setUp();
	}

	protected void tearDown() throws Exception {

	}

	public void testAction() throws Exception {
		String id = doSave();
		doEdit(id);
		doSelectList();
		doFieldList(id);
		doDelete(id);
	}

	/*
	 * Test method for
	 * 'ComponentAction.doSelectList()'
	 */
	public void doSelectList() throws Exception {
//		//PersistenceUtils.getSessionSignal().sessionSignal++;
		action.doSelectList();
		Collection<Component> datas = action.getDatas().getDatas();
		assertNotNull(datas);
		assertTrue(datas.size() > 0);
//		//PersistenceUtils.getSessionSignal().sessionSignal--;
	}

	/*
	 * Test method for
	 * 'ComponentAction.doFieldList()'
	 */
	public void doFieldList(String id) throws Exception {
//		//PersistenceUtils.getSessionSignal().sessionSignal++;
		params.put("id", new String[] { id });
		BaseAction.getContext().setParameters(params);
		action.doFieldList();
		Collection<FormField> fiels = action.getFieldList();

		assertNotNull(fiels);
		assertTrue(fiels.size() > 0);
//		//PersistenceUtils.getSessionSignal().sessionSignal--;
	}

	/*
	 * Test method for 'FormAction.doSave()'
	 */
	public String doSave() throws Exception {
		Component comp = new Component();
//		//PersistenceUtils.getSessionSignal().sessionSignal++;
		comp.setName("text");

		comp.setTemplatecontext("<TABLE borderColor=#000000 cellSpacing=2 cellPadding=3 width=\"100%\" bgColor=#ffffff border=1 heihgt=\"\">"
				+ "<TBODY>"
				+ "<TR>"
				+ "<TD>&nbsp;text1<INPUT name=${text11} className=\"InputField\" refreshOnChanged=\"false\" calculateOnRefresh=\"false\" popToChoice=\"false\" discript valueScript validateRule hiddenScript hiddenPrintScript readonlyScript textType=\"text\" selectDate=\"false\" fieldtype=\"VALUE_TYPE_VARCHAR\" dialogView validateLibs></TD>"
				+ "<TD>&nbsp;text3<INPUT name=${text33} className=\"InputField\" refreshOnChanged=\"false\" calculateOnRefresh=\"false\" popToChoice=\"false\" discript valueScript validateRule hiddenScript hiddenPrintScript readonlyScript textType=\"text\" selectDate=\"false\" fieldtype=\"VALUE_TYPE_VARCHAR\" dialogView validateLibs></TD></TR>"
				+ "<TR>"
				+ "<TD>&nbsp;text2<INPUT name=${text22} className=\"InputField\" refreshOnChanged=\"false\" calculateOnRefresh=\"false\" popToChoice=\"false\" discript valueScript validateRule hiddenScript hiddenPrintScript readonlyScript textType=\"text\" selectDate=\"false\" fieldtype=\"VALUE_TYPE_VARCHAR\" dialogView validateLibs></TD>"
				+ "<TD>&nbsp;text4<TEXTAREA name=${text44} className=\"TextareaField\" refreshOnChanged=\"false\" calculateOnRefresh=\"false\" discript valueScript validateRule hiddenScript hiddenPrintScript fieldtype=\"VALUE_TYPE_TEXT\" validateLibs></TEXTAREA></TD></TR></TBODY></TABLE>");
		action.setContent(comp);
//		//PersistenceUtils.getSessionSignal().sessionSignal--;
		action.doSave();

//		//PersistenceUtils.getSessionSignal().sessionSignal++;
		params.put("id", new String[] { comp.getId() });
		BaseAction.getContext().setParameters(params);
//		//PersistenceUtils.getSessionSignal().sessionSignal--;
		action.doView();
		Component findComp = (Component) action.getContent();

		assertEquals(findComp.getName(), comp.getName());

		return findComp.getId();
	}

	/*
	 * Test method for 'BaseAction.doEdit()'
	 */
	public void doEdit(String id) throws Exception {
//		//PersistenceUtils.getSessionSignal().sessionSignal++;
		params.put("id", new String[] { id });
		BaseAction.getContext().setParameters(params);
		action.doView();
		Component oldy = (Component) action.getContent();

		oldy.setName("newViewName");
		action.setContent(oldy);
		action.doSave();

		BaseAction.getContext().setParameters(params);
		action.doView();
		Component newly = (Component) action.getContent();

		assertEquals(oldy.getName(), newly.getName());
//		//PersistenceUtils.getSessionSignal().sessionSignal--;
	}

	/*
	 * Test method for 'BaseAction.doDelete()'
	 */
	public void doDelete(String id) throws Exception {
//		//PersistenceUtils.getSessionSignal().sessionSignal++;
		action.set_selects(new String[] { id });
		action.doDelete();

		params.put("id", new String[] { id });
		BaseAction.getContext().setParameters(params);
		action.doView();
		Component find = (Component) action.getContent();

		assertNull(find);
//		//PersistenceUtils.getSessionSignal().sessionSignal--;
	}

}
