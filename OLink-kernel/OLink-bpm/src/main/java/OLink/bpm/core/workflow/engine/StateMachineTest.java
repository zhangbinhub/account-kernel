package OLink.bpm.core.workflow.engine;

import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.core.user.action.UserAction;
import OLink.bpm.core.workflow.storage.definition.action.BillDefiAction;
import OLink.bpm.core.workflow.utility.NameList;
import junit.framework.TestCase;

public class StateMachineTest extends TestCase {
	BillDefiAction action;

	UserAction userAction;

	protected void setUp() throws Exception {
		super.setUp();
		action = new BillDefiAction();
		userAction = new UserAction();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testDoFlowprcss() throws Exception {
		/*
		 * FlowDiagram fd = new FlowDiagram();
		 * 
		 * StartNode startNode1 = fd.addStartNode("formname1", "", 100, 100);
		 * 
		 * StartNode startNode2 = fd.addStartNode("formname2", "", 100, 100);
		 * 
		 * String namelist = "(U1161258943671001|sky;" + "U1161261465406001|m)";
		 * 
		 * ManualNode manualNode = fd.addManualNode("name", "", 100, 100);
		 * 
		 * Relation relation = new Relation(fd); // 添加关系
		 * relation.setStartnode(startNode1); relation.setEndnode(manualNode);
		 * relation.name = "relation"; fd.appendElement(relation);
		 * 
		 * String flow = fd.toXML();
		 * 
		 * BillDefiVO flowVO = new BillDefiVO();
		 * flowVO.setId(Tools.getSequence()); flowVO.setFlow(flow);
		 * 
		 * DocumentProcess dp = (DocumentProcess) ProcessFactory
		 * .createProcess(DocumentProcess.class); Document doc = (Document)
		 * dp.doView("1161066825015000");
		 * 
		 * UserProcess up = (UserProcess) ProcessFactory
		 * .createProcess(UserProcess.class); UserVO user = (UserVO)
		 * up.doView("1161258943671001"); Environment evt =
		 * Environment.getInstance(); evt.setApplicationRealPath("c:/");
		 * 
		 * // StateMachine.doFlow(doc.getId(), flowVO, startNode1.id, // new
		 * String[] { manualNode.id }, new WebUser(user), "", "", evt);
		 */
	}

	public void testNameList() throws Exception {
		String nlStr = "{A001|a|test;B001|b;({C001|c;D001|d};{E001|e;F001|f});(G001|g;H001|h);I001|i}";
		NameList nl = NameList.parser(nlStr);
		Collection<String[]> colls = nl.toCollection();
		Iterator<String[]> it = colls.iterator();
		it.next();
		action.getApplication();
		userAction.getApplication();
		
	}
}
