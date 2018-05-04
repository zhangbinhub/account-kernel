package OLink.bpm.core.workflow.engine;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import OLink.bpm.core.workflow.element.*;
import OLink.bpm.core.workflow.utility.Factory;
import OLink.bpm.core.workflow.element.Element;
import OLink.bpm.core.workflow.element.Relation;
import org.apache.bsf.BSFManager;

import OLink.bpm.core.workflow.element.FlowDiagram;

public class WFRunner {
	private static BSFManager BSF_MANAGER = new BSFManager();
	// public static final int FLOWSTATUS_OPEN_NOSTART = 0x00000001;
	// public static final int FLOWSTATUS_OPEN_START = 0x00000010;
	// public static final int FLOWSTATUS_OPEN_RUN_RUNNING = 0x00000100;
	// public static final int FLOWSTATUS_OPEN_RUN_SUSPEND = 0x00001000;
	// public static final int FLOWSTATUS_CLOSE_ABORT = 0x00010000;
	// public static final int FLOWSTATUS_CLOSE_COMPLETE = 0x00100000;
	// public static final int FLOWSTATUS_CLOSE_TERMINAT = 0x01000000;
	// public static final int FLOWSTATUS_OPEN_RUN_BACK = 0x10000000;

	// public static final String START = "start";
	// public static final String PASS = "pass";
	// public static final String SUSPEND = "suspend";
	// public static final String ABORT = "abort";
	// public static final String COMPLETE = "complete";
	// public static final String TERMINATE = "terminate";
	// public static final String BACK = "back";

	// public static final String START2RUNNING = "1";
	// public static final String START2TERMINATE = "2";
	// public static final String SUSPEND2RUNNING = "3";
	// public static final String RUNNING2SUSPEND = "4";
	// public static final String SUSPEND2ABORT = "5";
	// public static final String RUNNING2COMPLETE = "6";
	// public static final String RUNNING2TERMIATE = "7";
	// public static final String RUNNING2RUNNING_NEXT = "80";
	// public static final String RUNNING2RUNNING_BACK = "81";
	// public static final String RUNNING2RUNNING_SELF = "82";
	// public static final String SUSPEND2SUSPEND = "9";

	private FlowDiagram flowDiagram = null;

	public WFRunner(String xml, String applicationid) {
		this.flowDiagram = Factory.trnsXML2Dgrm(xml);
		this.flowDiagram._applicationid = applicationid;

	}

	public WFRunner(File f, String applicationid) {
		this.flowDiagram = Factory.trnsXML2Dgrm(f);
		this.flowDiagram._applicationid = applicationid;
	}

	// public void setFlowDiagram(String xml) {
	// fd = Factory.trnsXML2Dgrm(xml);
	// }
	// public void setFlowDiagram(File f) {
	// fd = Factory.trnsXML2Dgrm(f);
	// }
	/**
	 * 设置流程状态
	 * 
	 * @param
	 */
	public void setFlowstatus(int status) throws Exception {
		flowDiagram.setFlowstatus(status);
	}

	public int getFlowstatus() throws Exception {
		return flowDiagram.getFlowstatus();
	}

	/**
	 * 设置流程运转路径
	 * 
	 * @param
	 */
	public void setFlowpath(String path) {
		flowDiagram.setFlowpath(path);
	}

	/**
	 * 获取流程运转路径
	 * 
	 * @param
	 */
	public Collection<String[]> getFlowpath() {
		return flowDiagram.getFlowpath();
	}

	/**
	 * 获取流程运转路径最后审核结点
	 * 
	 * @param
	 */
	public Node getFlowpathLastNode() {
		return flowDiagram.getFlowpathLastNode();
	}

	/**
	 * 获取当前用户可处理的第一个当前结点
	 */
	/*
	 * public Node getCurrentNode(WebUser webUser){ return
	 * fd.getCurrentNode(webUser); }
	 */
	/**
	 * 获取所有当前结点
	 */
	/*
	 * public Vector getAllCurrentNode(){ return fd.getAllCurrentNode(); }
	 */

	/**
	 * 获取所有当前结点的realnamelist
	 */
	/*
	 * public String getAllCurrNdRealNameList(boolean onlyId){ return
	 * fd.getAllCurrNdRealNameList(onlyId); }
	 */

	/**
	 * 获取流程的第一个结点
	 * 
	 * @param
	 */
	public Node getFirstNode() {
		return flowDiagram.getFirstNode();
	}

	/**
	 * 获取当前结点的所有符合条件的下一个Node节点
	 * 
	 * @param
	 */
	public Vector<Node> getNextNodeByCndtn(Node nd) {
		Vector<Node> rs = new Vector<Node>();
		Vector<Relation> all = getNodeNextRelation(nd);
		Iterator<Relation> iter = all.iterator();
		while (iter.hasNext()) {
			Relation r = iter.next();
			Node nextNode = getNextNode(r);
			if (r.editMode.equals(Relation.EDITMODE_VIEW)) {// marky
				if (nextNode != null && isCondition(r.filtercondition)) {
					rs.add(nextNode);
				}
			} else if (r.editMode.equals(Relation.EDITMODE_CODE)) {
				if (nextNode != null && isCondition(r.condition)) {
					rs.add(nextNode);
				}
			}

		}
		return rs;
	}

	/**
	 * 获取当前结点的所有下一个Relation即步骤
	 * 
	 * @param
	 */
	public Vector<Relation> getNodeNextRelation(Node nd) {
		return flowDiagram.getNodeNextRelation(nd);
	}

	/**
	 * 根据当前relation获取下一结点
	 * 
	 * @param
	 */
	public Node getNextNode(Relation r) {
		return flowDiagram.getNextNode(r);
	}

	/**
	 * 根据当前relation获取上一结点
	 * 
	 * @param
	 */
	/*
	 * public Node getBeforeNode(Node nd) { return fd.getBeforeNode(nd); }
	 */

	/**
	 * 将结点设为当前结点
	 * 
	 * @param
	 */
	public void setCurrentNode(Node current) {
		flowDiagram.setCurrentNode(current);
	}

	public static void declareBean(String key, Object obj) {
		try {
			BSF_MANAGER.declareBean(key, obj, obj.getClass());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Element getElementByID(String id) {
		return flowDiagram.getElementByID(id);
	}

	/**
	 * 获取流程流转Relation
	 * 
	 * @param
	 */
	public Element getElementByEndNodeID(String beginid, String endid) {
		return flowDiagram.getElementByBeginEndNodeID(beginid, endid);
	}

	public static void registerBean(String key, Object obj) {
		try {
			BSF_MANAGER.registerBean(key, obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isCondition(String js) {
		boolean istrue = true;
		Object result = null;
		if (js != null && js.trim().length() > 0) {
			try {
				// TestVO tv = new TestVO();
				// tv.setName("good morning!");
				//
				// declareBean("$DOC", tv);
				result = BSF_MANAGER.eval("javascript", "tt", 0, 0, js);
				if (result instanceof Boolean) {
					istrue = ((Boolean) result).booleanValue();
				}
			} catch (Exception em) {
				em.printStackTrace();
			}
		}
		return istrue;
	}

	public Object runJavascript(String js) {
		Object result = null;
		if (js != null && js.trim().length() > 0) {
			try {
				result = BSF_MANAGER.eval("javascript", "tt", 0, 0, js);
			} catch (Exception em) {
				em.printStackTrace();
			}
		}
		return result;
	}

	public Object runJavascript(Object util, String js) throws Exception {
		Object result = null;
		if (util != null && js != null && js.trim().length() > 0) {
			try {
				BSF_MANAGER.declareBean("$JSUTIL", util, util.getClass());
				result = BSF_MANAGER.eval("javascript", "tt", 0, 0, js);
			} catch (Exception em) {
				em.printStackTrace();
			}
		}
		return result;
	}

	public boolean isCondition(Object util, String js) throws Exception {
		boolean istrue = true;
		Object result = null;
		if (js != null && js.trim().length() > 0) {
			try {
				BSF_MANAGER.declareBean("$JSUTIL", util, util.getClass());
				result = BSF_MANAGER.eval("javascript", "tt", 0, 0, js);
				if (result instanceof Boolean) {
					istrue = ((Boolean) result).booleanValue();
				}
			} catch (Exception em) {
				em.printStackTrace();
			}
		}
		return istrue;
	}

	public String saveToXML() throws Exception {
		return flowDiagram.toXML();
	}

	// public void testNextStep() { //测试流程下一步
	// Node cn = fd.getCurrentNode();
	// if (cn != null) {
	// log.info("CurrNode->" + cn.name);
	// }
	// else {
	// try {
	// fd.setFlowstatus(FlowType.FLOWSTATUS_OPEN_NOSTART);
	// }
	// catch (Exception ex) {
	// log.info("set FlowStatus error!");
	// }
	// cn = fd.getCurrentNode();
	// log.info("CurrNode->" + cn.name);
	// }
	// Vector v = fd.getNodeNextRelation(cn);
	// Iterator iter = v.iterator();
	// Relation item = null;
	// while (iter.hasNext()) {
	// Relation temp = (Relation) iter.next();
	// if (temp != null) {
	// log.info("condition-->" + temp.condition);
	// log.info("Relation->" + temp.name);
	// item = temp;
	// log.info("NextRelation-->" + item.name);
	// }
	// }
	// if (item != null) {
	// runJavascript(cn.afterscrpt); //流程转向下一步时执行afterscrpt
	// cn = fd.getNextNode(item);
	// if (cn != null) {
	// fd.setCurrentNode(cn);
	// runJavascript(cn.beforescrpt); //流程转入时执行beforescrpt
	// }
	// }
	// try {
	// fd.setFlowstatus(FlowType.FLOWSTATUS_OPEN_RUN_RUNNING);
	// }
	// catch (Exception ex) {
	// log.info("set Flow status error!");
	// }
	// saveXML(fd.toXML());
	//
	// }
	//
	// public void testNextStepByCndtn() { //测试流程下一步
	// Node cn = fd.getCurrentNode();
	// if (cn != null) {
	// log.info("CurrNode->" + cn.name);
	// }
	// else {
	// try {
	// fd.setFlowstatus(FlowType.FLOWSTATUS_OPEN_NOSTART);
	// }
	// catch (Exception ex) {
	// log.info("set FlowStatus error!");
	// }
	// cn = fd.getCurrentNode();
	// log.info("CurrNode->" + cn.name);
	// }
	// Vector v = fd.getNodeNextRelation(cn);
	//
	// Iterator iter = v.iterator();
	// Relation item = null;
	// while (iter.hasNext()) {
	// Relation temp = (Relation) iter.next();
	// if (temp != null) {
	// log.info("Condition-->" + temp.condition);
	// if (isCondition(temp.condition)) {
	// log.info("Relation->" + temp.name);
	// item = temp;
	// log.info("NextRelation-->" + item.name);
	// }
	// }
	// }
	// if (item != null) {
	// runJavascript(cn.afterscrpt); //流程转向下一步时执行afterscrpt
	// cn = fd.getNextNode(item);
	// if (cn != null) {
	// fd.setCurrentNode(cn);
	// runJavascript(cn.beforescrpt); //流程转入时执行beforescrpt
	// }
	// }
	//
	// try {
	// fd.setFlowstatus(FlowType.FLOWSTATUS_OPEN_RUN_RUNNING);
	// }
	// catch (Exception ex) {
	// log.info("set Flow status error!");
	// }
	// saveXML(fd.toXML());
	// }

	public void saveXML(String xml) throws IOException {
		String f = "f:\\temp\\abdc2.xml";
		java.io.FileOutputStream os = null;
		java.io.PrintStream osw = null;
		try {
			os = new java.io.FileOutputStream(f);
			osw = new java.io.PrintStream(os);
			osw.println(xml);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (os != null)
				os.close();
			if (osw != null)
				osw.close();
		}
	}

	/**
	 * 获取指定结点前的所有结点
	 * 
	 * @param
	 */
	public Vector<Element> getAllBeforeNode(Node nd, boolean ispassed) {
		return flowDiagram.getAllBeforeNode(nd, ispassed);
	}

	/**
	 * 获取指定结点前的所有当前结点
	 */
	/*
	 * public Vector getAllCurrentNode(Node nd){ return
	 * fd.getAllCurrentNode(nd); }
	 */

	public static void main(String[] args) {
		/*
		 * File f = new File("d:\\temp\\asdn.xml"); WFRunner wfr = new
		 * WFRunner(f, ""); Node n = null; try { //
		 * wfr.setFlowstatus(WFRunner.FLOWSTATUS_OPEN_START);
		 * 
		 * // wfr.setFlowstatus(WFRunner.FLOWSTATUS_OPEN_RUN_RUNNING); // n =
		 * (Node) wfr.getElementByID("1095848817968"); // wfr.setCurrentNode(n);
		 * // // wfr.setFlowstatus(WFRunner.FLOWSTATUS_OPEN_RUN_RUNNING); // n =
		 * (Node) wfr.getElementByID("1095848823562"); // wfr.setCurrentNode(n);
		 * // // wfr.setFlowstatus(WFRunner.FLOWSTATUS_OPEN_RUN_SUSPEND); // n =
		 * (Node) wfr.getElementByID("1095848823562"); // wfr.setCurrentNode(n);
		 * // // wfr.setFlowstatus(WFRunner.FLOWSTATUS_OPEN_RUN_RUNNING); // n =
		 * (Node) wfr.getElementByID("1095848829875"); // wfr.setCurrentNode(n);
		 * // // n = (Node) wfr.getElementByID("1111978952090"); //
		 * wfr.setFlowpath(wfr.getCurrentNode().id + "," + //
		 * WFRunner.RUNNING2RUNNING_BACK); // wfr.setCurrentNode(n); // // //
		 * Collection colls = wfr.getFlowpath(); // Iterator paths =
		 * colls.iterator(); // while (paths.hasNext()) { // String[] p =
		 * (String[]) paths.next(); // if (p != null && p.length > 0) { // Node
		 * pathn = (Node) wfr.getElementByID(p[0]); // if
		 * (WFRunner.RUNNING2RUNNING_NEXT.equals(p[1])) { // log.info(pathn.name
		 * + "-->" + p[1]); // } // } // }
		 * 
		 * // wfr.saveXML(wfr.saveToXML());
		 * 
		 * // Vector allnode = wfr.getAllBeforeNode(n, false); // Enumeration
		 * enum1 = allnode.elements(); // while (enum1.hasMoreElements()) { //
		 * Object item = (Object) enum1.nextElement(); // if (item instanceof
		 * Node) { // Node nd = (Node)item; // log.info(nd.name); // } // }
		 * 
		 * // UserProxy userProxy = new UserProxy(); // UserVO userVO =
		 * userProxy.doView("U"); // WebUser user = new WebUser(userVO); // n =
		 * wfr.getCurrentNode(user); // Vector allCurr =
		 * wfr.getAllCurrentNode(); // Iterator iters = allCurr.iterator(); //
		 * while(iters.hasNext()){ // Node node = (Node)iters.next(); //
		 * log.info("node-->"+node.name); // Vector all =
		 * wfr.getAllCurrentNode(node); // log.info("all-->"+all); // } //
		 * log.info("allCurr-->"+allCurr); // log.info("n-->"+n.name);
		 * 
		 * // UserProxy userProxy = new UserProxy(); // UserVO userVO =
		 * userProxy.doView("U-05-07-012"); // WebUser user = new
		 * WebUser(userVO); // n = wfr.getCurrentNode(user); // Vector allCurr =
		 * wfr.getNextNodeByCndtn(n); // Iterator iter = allCurr.iterator(); //
		 * while (iter.hasNext()) { // Node nextNode = (Node)iter.next();
		 * //log.info(nextNode.name); // } // // File dir = new File(filepath);
		 * // if(!dir.exists()){ // dir.mkdir(); // } String filepath =
		 * "D:/jboss/server/CyberCMS/deploy/webroot.war/uploads/billflow/"; File
		 * dir = new File(filepath); if (!dir.exists()) { dir.mkdir(); }
		 * filepath = filepath + "1000000000" + ".jpg"; File file = new
		 * File(filepath); } catch (Exception ex) { ex.printStackTrace(); }
		 */
	}

	public FlowDiagram getFlowDiagram() {
		return flowDiagram;
	}

	public void setFlowDiagram(FlowDiagram flowDiagram) {
		this.flowDiagram = flowDiagram;
	}
}
