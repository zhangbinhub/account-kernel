//Source file: D:\\BILLFLOW\\src\\billflow\\BFApplet.java

//Source file: E:\\billflow\\src\\billflow\\BFApplet.java

package OLink.bpm.core.workflow.applet;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Panel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;

import OLink.bpm.core.workflow.utility.Factory;
import netscape.javascript.JSObject;
import OLink.bpm.core.workflow.element.AbortNode;
import OLink.bpm.core.workflow.element.AutoNode;
import OLink.bpm.core.workflow.element.CompleteNode;
import OLink.bpm.core.workflow.element.Element;
import OLink.bpm.core.workflow.element.FlowDiagram;
import OLink.bpm.core.workflow.element.ManualNode;
import OLink.bpm.core.workflow.element.Node;
import OLink.bpm.core.workflow.element.Relation;
import OLink.bpm.core.workflow.element.StartNode;
import OLink.bpm.core.workflow.element.SubFlow;
import OLink.bpm.core.workflow.element.SuspendNode;
import OLink.bpm.core.workflow.element.TerminateNode;

public class BFApplet extends Applet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8375041782017930411L;

	boolean isStandalone = false;

	FlowPanel bppanel = new FlowPanel();

	FlowDiagram fd = null;

	BorderLayout borderLayout1 = new BorderLayout();

	JSObject win = null;

	private MediaTracker tracker;

	/**
	 * Construct the applet
	 * 
	 * @roseuid 3E0A6E1602A2
	 */
	public BFApplet() {

	}

	/**
	 * Get a parameter value
	 * 
	 * @param key
	 * @param def
	 * @return java.lang.String
	 * @roseuid 3E0A6E16028E
	 */
	public String getParameter(String key, String def) {
		return isStandalone ? System.getProperty(key, def)
				: (getParameter(key) != null ? getParameter(key) : def);
	}

	/**
	 * Initialize the applet
	 * 
	 * @roseuid 3E0A6E1602AC
	 */
	public void init() {
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ----------------------------------
	protected void loading(Image image1) {
		// wait for the image to load completely
		synchronized (tracker) {
			tracker.addImage(image1, 0);
			try {
				tracker.waitForID(0);
			} catch (InterruptedException interruptedexception) {
				interruptedexception.printStackTrace();
			}
			tracker.removeImage(image1, 0);
		}
	}

	/**
	 * Component initialization
	 * 
	 * @throws Exception
	 * @roseuid 3E0A6E1602AD
	 */
	private void jbInit() throws Exception {
		tracker = new MediaTracker(this);

		// String xmlStr = "";

		// xmlStr = getTestXML();

		// if (getParameter("xmlStr") != null &&
		// !getParameter("xmlStr").toLowerCase().equals("null")) {
		// xmlStr = getParameter("xmlStr");
		// }

		this.setLayout(borderLayout1);

		// addButton();
		this.add(bppanel, BorderLayout.CENTER);
		// loadXML(xmlStr);
	}

	/**
	 * 测试用按钮
	 */
	@SuppressWarnings("unused")
	private void addButton() {
		Panel panel = new Panel();
		Button addRelation = new Button("AddRelation");
		addRelation.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				fd.changeStatues(FlowDiagram.ACTION_ADD_RELATION);
			}
		});
		panel.add(addRelation);
		Button addManual = new Button("addManual");
		addManual.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				fd.changeStatues(FlowDiagram.ACTION_ADD_MANUALNODE);
			}
		});
		panel.add(addManual);
		Button addAbort = new Button("AddAbort");
		addAbort.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				fd.changeStatues(FlowDiagram.ACTION_ADD_ABORTNODE);
			}
		});
		panel.add(addAbort);
		Button addAuto = new Button("AddAuto");
		addAuto.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				fd.changeStatues(FlowDiagram.ACTION_ADD_AUTONODE);
			}
		});
		panel.add(addAuto);
		Button addComplete = new Button("AddComplete");
		addComplete.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				fd.changeStatues(FlowDiagram.ACTION_ADD_COMPLETENODE);
			}
		});
		panel.add(addComplete);
		Button addStart = new Button("AddStart");
		addStart.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				fd.changeStatues(FlowDiagram.ACTION_ADD_STARTNODE);
			}
		});
		panel.add(addStart);
		Button addSuspend = new Button("AddSuspend");
		addSuspend.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				fd.changeStatues(FlowDiagram.ACTION_ADD_SUSPENDNODE);
			}
		});
		panel.add(addSuspend);
		Button addSubFlow = new Button("AddSubFlow");
		addSubFlow.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				fd.changeStatues(FlowDiagram.ACTION_ADD_SUBFLOW);
			}
		});
		panel.add(addSubFlow);

		this.add(panel, BorderLayout.WEST);

	}

	public void loadXML(String xml) throws Exception {

		if (xml == null) {
			xml = "";
		}

		fd = Factory.trnsXML2Dgrm(xml);
		fd.setBackground(Color.white);

		try {// 测试的时候没有Window对象
			win = JSObject.getWindow(this);
		} catch (Exception e) {
		}

		fd.setJSObject(win);

		if (bppanel != null) {
			bppanel.removeAll();
			bppanel.add(fd);
			bppanel.repaint();
		}

	}

	/**
	 * Get Applet information
	 * 
	 * @return java.lang.String
	 * @roseuid 3E0A6E1602C0
	 */
	public String getAppletInfo() {
		return "Applet Information";
	}

	/**
	 * Get parameter info
	 * 
	 * @return String[][]
	 * @roseuid 3E0A6E1602CA
	 */
	public String[][] getParameterInfo() {
		return null;
	}

	public void addAutoNode() {
		fd.changeStatues(FlowDiagram.ACTION_ADD_AUTONODE);
	}

	public void addStartNode() {
		fd.changeStatues(FlowDiagram.ACTION_ADD_STARTNODE);
	}

	public void addSuspendNode() {
		fd.changeStatues(FlowDiagram.ACTION_ADD_SUSPENDNODE);
	}

	public void addTerminateNode() {
		fd.changeStatues(FlowDiagram.ACTION_ADD_TERMINATENODE);
	}

	public void addAbortNode() {
		fd.changeStatues(FlowDiagram.ACTION_ADD_ABORTNODE);
	}

	public void addCompleteNode() {
		fd.changeStatues(FlowDiagram.ACTION_ADD_COMPLETENODE);
	}

	// the Manual class process method
	public void addManualNode() {
		fd.changeStatues(FlowDiagram.ACTION_ADD_MANUALNODE);
	}

	public void addSubFlow() {
		fd.changeStatues(FlowDiagram.ACTION_ADD_SUBFLOW);
	}

	public void addRelation() {
		fd.changeStatues(FlowDiagram.ACTION_ADD_RELATION);
	}

	public void select() {
		fd.changeStatues(FlowDiagram.ACTION_NORMAL);
	}

	public void deSelect() {
		fd.setCurrToEdit(null);
	}

	public void removeElement() {
		// fd.changeStatues(fd.ACTION_REMOVE);
		Element elm = fd.getCurrToEdit();
		fd.removeElement(elm);
		fd.repaint();
	}

	public void zoomIn() {
		fd.zoomIn();
		bppanel.removeAll();
		bppanel.add(fd);
		bppanel.doLayout();
	}

	public void zoomOut() {
		fd.zoomOut();
		bppanel.removeAll();
		bppanel.add(fd);
		bppanel.doLayout();
	}

	public String saveToXML() throws Exception {
		return fd.toXML();
	}

	/**
	 * 获取指定结点前的所有结点
	 * 
	 * @param
	 */
	public Vector<Element> getAllBeforeNode() {
		// ManualProcessor n = getCurrToEditManualProcess();
		// if(n!= null){
		return fd.getAllBeforeNode(getCurrToEditManualProcess(), false);
		// }else{
		// return null;
		// }
	}

	public String getAllBeforeNodeString() {
		StringBuffer s = new StringBuffer();
		Vector<Element> all = getAllBeforeNode();
		Enumeration<Element> enum11 = all.elements();
		while (enum11.hasMoreElements()) {
			Object item = enum11.nextElement();
			if (item instanceof Node) {
				Node node = (Node) item;
				s.append(node.id).append("|").append(node.name).append(";");
			}
		}
		return s.toString();
	}

	public boolean isAssignBack() { // 删除节点时判断此节点是否为指定回退节点
		return fd.isAssignBack(getCurrToEditManualProcess());
	}

	public boolean xmlChanged() { // 检查流程图是否已经修改
		return fd.getChanged();
	}

	/**
	 * 编辑时用到的接口
	 * 
	 * @return Actor
	 */
	public Element getCurrToEditElementProcess() {
		Element currToEdit = fd.getCurrToEdit();
		if (currToEdit != null) {
			return currToEdit;
		} else {
			return null;
		}
	}

	/**
	 * 编辑时用到的接口
	 * 
	 * @return Actor
	 */
	public ManualNode getCurrToEditManualProcess() {
		Element currToEdit = fd.getCurrToEdit();
		if (currToEdit != null && currToEdit instanceof ManualNode) {
			return (ManualNode) currToEdit;
		} else {
			return null;
		}
	}

	/**
	 * 编辑时用到的接口
	 * 
	 * @return Actor
	 */
	public AbortNode getCurrToEditAbortNodeProcess() {
		Element currToEdit = fd.getCurrToEdit();
		if (currToEdit != null && currToEdit instanceof AbortNode) {
			return (AbortNode) currToEdit;
		} else {
			return null;
		}
	}

	/**
	 * 编辑时用到的接口
	 * 
	 * @return Actor
	 */
	public CompleteNode getCurrToEditCompleteNodeProcess() {
		Element currToEdit = fd.getCurrToEdit();
		if (currToEdit != null && currToEdit instanceof CompleteNode) {
			return (CompleteNode) currToEdit;
		} else {
			return null;
		}
	}

	/**
	 * 编辑时用到的接口
	 * 
	 * @return Actor
	 */
	public SuspendNode getCurrToEditSuspendNodeProcess() {
		Element currToEdit = fd.getCurrToEdit();
		if (currToEdit != null && currToEdit instanceof SuspendNode) {
			return (SuspendNode) currToEdit;
		} else {
			return null;
		}
	}

	/**
	 * 编辑时用到的接口
	 * 
	 * @return Actor
	 */
	public StartNode getCurrToEditStartNodeProcess() {
		Element currToEdit = fd.getCurrToEdit();
		if (currToEdit != null && currToEdit instanceof StartNode) {
			return (StartNode) currToEdit;
		} else {
			return null;
		}
	}

	/**
	 * 编辑时用到的接口
	 * 
	 * @return Actor
	 */
	public TerminateNode getCurrToEditTerminateNodeProcess() {
		Element currToEdit = fd.getCurrToEdit();
		if (currToEdit != null && currToEdit instanceof TerminateNode) {
			return (TerminateNode) currToEdit;
		} else {
			return null;
		}
	}

	/**
	 * 编辑时用到的接口
	 * 
	 * @return Actor
	 */
	public AutoNode getCurrToEditAutoNodeProcess() {
		Element currToEdit = fd.getCurrToEdit();
		if (currToEdit != null && currToEdit instanceof AutoNode) {
			return (AutoNode) currToEdit;
		} else {
			return null;
		}
	}

	/**
	 * 编辑时用到的接口
	 * 
	 * @return Actor
	 */
	public Relation getCurrToEditRelation() {
		Element currToEdit = fd.getCurrToEdit();
		if (currToEdit != null && currToEdit instanceof Relation) {
			return (Relation) currToEdit;
		}

		else {
			return null;
		}
	}

	public SubFlow getCurrToEditSubflow() {
		Element currToEdit = fd.getCurrToEdit();
		if (currToEdit != null && currToEdit instanceof SubFlow) {
			return (SubFlow) currToEdit;
		}

		else {
			return null;
		}
	}

	public void editAutoNode(String name, String statelabel, boolean issplit,
			boolean isgather, int autoAuditType, String delayDay,
			String delayHour, String delayMinute, String auditDateTime) {
		fd.changeStatues(FlowDiagram.ACTION_EDIT_NODE);
		Element elm = fd.getCurrToEdit();
		if (elm != null && elm instanceof AutoNode) {
			AutoNode an = (AutoNode) fd.getCurrToEdit();
			fd.editAutoNode(an, an.id, name, statelabel, issplit, isgather,
					autoAuditType, delayDay, delayHour, delayMinute,
					auditDateTime, an.x, an.y);
		}
	}

	public void editSubFlow(String name, String statelabel, String subflowid,
			String subflowname, String startupScript, String callbackScript,
			String subflowFormid, String subflowFormname, boolean crossform) {
		fd.changeStatues(FlowDiagram.ACTION_EDIT_NODE);
		Element elm = fd.getCurrToEdit();
		if (elm != null && elm instanceof SubFlow) {
			SubFlow an = (SubFlow) fd.getCurrToEdit();
			fd.editSubFlow(an, an.id, name, statelabel, subflowid, subflowname,
					startupScript, callbackScript, subflowFormid,
					subflowFormname, crossform, an.x, an.y);
		}
	}

	public void editAbortNode(String name, String statelabel) {
		fd.changeStatues(FlowDiagram.ACTION_EDIT_NODE);
		Element elm = fd.getCurrToEdit();
		if (elm != null && elm instanceof AbortNode) {
			AbortNode an = (AbortNode) fd.getCurrToEdit();
			fd.editAbortNode(an, an.id, name, statelabel, an.x, an.y);
		}
	}

	public void editCompleteNode(String name, String statelabel,
			String nextNodeName) {
		fd.changeStatues(FlowDiagram.ACTION_EDIT_NODE);
		Element elm = fd.getCurrToEdit();
		if (elm != null && elm instanceof CompleteNode) {
			CompleteNode an = (CompleteNode) fd.getCurrToEdit();
			fd.editCompleteNode(an, an.id, name, statelabel, an.x, an.y);
		}
	}

	public void editSuspendNode(String name, String statelabel) {
		fd.changeStatues(FlowDiagram.ACTION_EDIT_NODE);
		Element elm = fd.getCurrToEdit();
		if (elm != null && elm instanceof SuspendNode) {
			SuspendNode an = (SuspendNode) fd.getCurrToEdit();
			fd.editSuspendNode(an, an.id, name, statelabel, an.x, an.y);
		}
	}

	public void editTerminateNode(String name, String statelabel) {
		fd.changeStatues(FlowDiagram.ACTION_EDIT_NODE);
		Element elm = fd.getCurrToEdit();
		if (elm != null && elm instanceof TerminateNode) {
			TerminateNode an = (TerminateNode) fd.getCurrToEdit();
			fd.editTerminateNode(an, an.id, name, statelabel, an.x, an.y);
		}
	}

	// the manualNode edit process method
	// happy modify
	public void editManualNode(String name, String statelabel, String namelist,
			String note, String passcondition, String exceedaction,
			String backnodeid, String formname, String fieldpermlist,
			boolean issplit, boolean isgather, int actorEditMode,
			String actorListScript, String notificationStrategyJSON,
			boolean cBack, boolean isToPerson, int retracementEditMode,
			int backType, String bnodelist, boolean cRetracement,
			String retracementScript) {
		fd.changeStatues(FlowDiagram.ACTION_EDIT_NODE);
		Element elm = fd.getCurrToEdit();
		if (elm != null && elm instanceof ManualNode) {
			ManualNode grp = (ManualNode) fd.getCurrToEdit();
			fd.editManualNode(grp, grp.id + "", name, statelabel, namelist,
					note, passcondition, exceedaction, backnodeid, formname,
					fieldpermlist, issplit, isgather, actorEditMode,
					actorListScript, notificationStrategyJSON, cBack,
					isToPerson, retracementEditMode, backType, bnodelist,
					cRetracement, retracementScript, grp.x, grp.y);
		}
	}

	public void editStartNode(String name, String statelabel) {
		fd.changeStatues(FlowDiagram.ACTION_EDIT_NODE);
		Element elm = fd.getCurrToEdit();
		if (elm != null && elm instanceof StartNode) {
			StartNode sn = (StartNode) fd.getCurrToEdit();
			fd.editStartNode(sn, sn.id, name, statelabel, sn.x, sn.y);
		}
	}

	public void editRelation(String name, String condition, String action,
			String validateScript, String filtercondition, String editMode,
			String processDescription) {
		fd.changeStatues(FlowDiagram.ACTION_EDIT_RELATION);
		Element elm = fd.getCurrToEdit();
		if (elm != null && elm instanceof Relation) {
			Relation rlt = (Relation) fd.getCurrToEdit();
			fd.editRelation(rlt, rlt.id + "", name, condition, "", action,
					validateScript, filtercondition, editMode,
					processDescription);
		}
	}

	public String getTestXML() {
		String xmlStr = "";
		// xmlStr += "<FlowDiagram>";
		// xmlStr += "<ACTION_NORMAL>0</ACTION_NORMAL>";
		// xmlStr += "<ACTION_REMOVE>1</ACTION_REMOVE>";
		// xmlStr += "<ACTION_ADD_NODE>16</ACTION_ADD_NODE>";
		// xmlStr += "<ACTION_ADD_RELATION>4096</ACTION_ADD_RELATION>";
		// xmlStr += "<ACTION_EDIT_NODE>268435472</ACTION_EDIT_NODE>";
		// xmlStr += "<ACTION_EDIT_RELATION>268439552</ACTION_EDIT_RELATION>";
		// xmlStr += "<ACTION_BREAK_LINE>1048576</ACTION_BREAK_LINE>";
		// xmlStr += "<flowstatus>16</flowstatus>";
		// xmlStr += "<flowpath></flowpath>";
		// xmlStr += "<deleteMSG>null</deleteMSG>";
		// xmlStr += "<TOP_ALIGNMENT>0.0</TOP_ALIGNMENT>";
		// xmlStr += "<CENTER_ALIGNMENT>0.5</CENTER_ALIGNMENT>";
		// xmlStr += "<BOTTOM_ALIGNMENT>1.0</BOTTOM_ALIGNMENT>";
		// xmlStr += "<LEFT_ALIGNMENT>0.0</LEFT_ALIGNMENT>";
		// xmlStr += "<RIGHT_ALIGNMENT>1.0</RIGHT_ALIGNMENT>";
		// xmlStr += "<WIDTH>1</WIDTH>";
		// xmlStr += "<HEIGHT>2</HEIGHT>";
		// xmlStr += "<PROPERTIES>4</PROPERTIES>";
		// xmlStr += "<SOMEBITS>8</SOMEBITS>";
		// xmlStr += "<FRAMEBITS>16</FRAMEBITS>";
		// xmlStr += "<ALLBITS>32</ALLBITS>";
		// xmlStr += "<ERROR>64</ERROR>";
		// xmlStr += "<ABORT>128</ABORT>";
		// xmlStr += "<AutoNode>";
		// xmlStr += "<prenodeid></prenodeid>";
		// xmlStr += "<backnodeid></backnodeid>";
		// xmlStr += "<formname>formname</formname>";
		// xmlStr += "<fieldpermlist>fieldpermlist</fieldpermlist>";
		// xmlStr += "<isstartandnext>false</isstartandnext>";
		// xmlStr += "<x>90</x>";
		// xmlStr += "<y>42</y>";
		// xmlStr += "<width>46</width>";
		// xmlStr += "<height>55</height>";
		// xmlStr += "<scale>0</scale>";
		// xmlStr += "<name></name>";
		// xmlStr += "<note></note>";
		// xmlStr += "<id>1161930835124</id>";
		// xmlStr += "</AutoNode>";
		// xmlStr += "<EndNode>";
		// xmlStr += "<prenodeid></prenodeid>";
		// xmlStr += "<backnodeid></backnodeid>";
		// xmlStr += "<formname>formname</formname>";
		// xmlStr += "<fieldpermlist>fieldpermlist</fieldpermlist>";
		// xmlStr += "<isstartandnext>false</isstartandnext>";
		// xmlStr += "<x>328</x>";
		// xmlStr += "<y>128</y>";
		// xmlStr += "<width>46</width>";
		// xmlStr += "<height>55</height>";
		// xmlStr += "<scale>0</scale>";
		// xmlStr += "<name></name>";
		// xmlStr += "<note></note>";
		// xmlStr += "<id>1161930836846</id>";
		// xmlStr += "</EndNode>";
		// xmlStr += "<Relation>";
		// xmlStr += "<state></state>";
		// xmlStr += "<startnodeid>1161930835124</startnodeid>";
		// xmlStr += "<endnodeid>1161930836846</endnodeid>";
		// xmlStr += "<ispassed>false</ispassed>";
		// xmlStr += "<isreturn>false</isreturn>";
		// xmlStr += "<condition>abc</condition>";
		// xmlStr += "<pointstack>90;42;328;128</pointstack>";
		// xmlStr += "<scale>0</scale>";
		// xmlStr += "<name>formname</name>";
		// xmlStr += "<note></note>";
		// xmlStr += "<id>1161930842054</id>";
		// xmlStr += "</Relation>";
		// xmlStr += "</FlowDiagram>";

		InputStream is = null;
		ByteArrayOutputStream out = null;
		try {
			is = this.getClass().getResourceAsStream("testflow.xml");
			out = new ByteArrayOutputStream();
			byte[] buffer = new byte[512];

			while (is.read(buffer) != -1) {
				out.write(buffer);
			}
			return out.toString("UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return xmlStr;
	}

}
