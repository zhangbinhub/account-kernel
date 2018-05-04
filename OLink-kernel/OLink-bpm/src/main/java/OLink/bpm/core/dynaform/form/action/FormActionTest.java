package OLink.bpm.core.dynaform.form.action;

import java.util.Collection;
import java.util.HashMap;

import OLink.bpm.base.action.BaseAction;
import OLink.bpm.core.dynaform.form.ejb.Form;
import junit.framework.TestCase;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.core.dynaform.form.ejb.TemplateParser;
/**
 * @author  nicholas
 */
public class FormActionTest extends TestCase {
	FormAction<Form> action;
	String name=null;
	public static void main(String[] args) {
		junit.textui.TestRunner.run(FormActionTest.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		action = new FormAction<Form>();
	}
	
	public void doList() throws Exception {
		
		HashMap<String, Object> mp = new HashMap<String, Object>();
		mp.put("s_name",name);
		BaseAction.getContext().setParameters(mp);
		action.doList();
		Collection<?> data = action.getDatas().datas;
		assertNotNull(data);
	
		
	}
	
	public void testView() throws Exception {
		name="formname1";
		Form form = (Form)action.getContent();
		form.setName(name);
		form = TemplateParser.parseTemplate(form, form.getTemplatecontext());
		
		action.setContent(form);
		action.doSave();
		String id = action.getContent().getId();

		HashMap<String, Object> mp = new HashMap<String, Object>();
		mp.put("id", new String[] { id });

		BaseAction.getContext().setParameters(mp);

		action.doView();
		
		
		doList();
		doDelete();
		/*Collection errors=new HashSet();
		ParamsTable params=new ParamsTable();
		Document  doc=new Document();
		UserVO vo = new UserVO();
		WebUser u=new WebUser(vo);
		
		String s=form.toHtml(doc, params, u, errors);*/
		
		//String newHtmlfile = "test.html";
		
		//File htmlFile = new File("c:\\java\\"+newHtmlfile);
		
	}
	public void doDelete() throws Exception {
//		//PersistenceUtils.getSessionSignal().sessionSignal++;
		String id = action.getContent().getId();
		
		
		action.set_selects(new String[] { id});
		action.doDelete();
       
		
		
//		//PersistenceUtils.getSessionSignal().sessionSignal--;
		PersistenceUtils.closeSession();

	}
	public void testCreateDoc() {
		
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
