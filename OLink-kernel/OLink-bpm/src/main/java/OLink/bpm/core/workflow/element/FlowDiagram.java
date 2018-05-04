package OLink.bpm.core.workflow.element;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.ImageIcon;

import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.macro.runner.JavaScriptFactory;
import OLink.bpm.core.workflow.utility.CommonUtil;
import OLink.bpm.core.workflow.utility.FlowType;
import OLink.bpm.core.workflow.utility.Sequence;
import OLink.bpm.core.workflow.applet.BFApplet;
import OLink.bpm.core.workflow.engine.RunActionException;
import OLink.bpm.util.StringUtil;
import org.apache.log4j.Logger;

import netscape.javascript.JSObject;

/**
 * 流程图表 通过XML表达以及存储节点信息
 * 
 */
public class FlowDiagram extends Canvas {

	/**
	 * 
	 */
	private Logger logger = Logger.getLogger(this.getClass());
	
	private static final long serialVersionUID = -3468474934969534191L;

	private static HashMap<String, Object> IMG_RESOURCE = new HashMap<String, Object>(10);

	private int _statues; // 鼠标状态

	public static final int ACTION_NORMAL = 0x00000000;

	public static final int ACTION_REMOVE = 0x00000001;

	public static final int ACTION_ADD_ABORTNODE = 0x00000010;

	public static final int ACTION_ADD_AUTONODE = 0x00000011;

	public static final int ACTION_ADD_COMPLETENODE = 0x00000012;

	public static final int ACTION_ADD_MANUALNODE = 0x00000013;

	public static final int ACTION_ADD_STARTNODE = 0x00000014;

	public static final int ACTION_ADD_SUSPENDNODE = 0x00000015;

	public static final int ACTION_ADD_TERMINATENODE = 0x00000016;

	public static final int ACTION_ADD_SUBFLOW = 0x00000017;

	public static final int ACTION_ADD_RELATION = 0x00001000;

	public static final int ACTION_EDIT_NODE = 0x10000010;

	public static final int ACTION_EDIT_RELATION = 0x10001000;

	public static final int ACTION_BREAK_LINE = 0x00100000;

	private Vector<Element> _elems = new Vector<Element>();

	private PaintElement _selected;// **

	private Element _currToEdit;// **

	Image _tmpimg;

	public int flowstatus = FlowType.FLOWSTATUS_OPEN_NOSTART;

	public String flowpath = "";

	public String deleteMSG = null;

	public int width = 2048;

	public int height = 1536;

	public String _applicationid;

	public String _sessionid;

	private boolean _changed = false;

	private double _zoomrate = 1;

	JSObject win = null;

	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @roseuid 3E0428D90248
	 */
	public FlowDiagram() {
		// _tmpimg = this.createImage(this.size().width, this.size().height);
		// if (this.getSize().width <= 0 || this.getSize().height <= 0) {
		this.setSize(width, height);
		// }
		// _tmpimg = new BufferedImage(width, height,
		// BufferedImage.TYPE_INT_RGB);

		_statues = ACTION_NORMAL;

		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Image get_tmpimg() {
		if (_tmpimg == null) {
			_tmpimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		}
		return _tmpimg;
	}

	public Image getImageResource(String filename) {
		Object obj = IMG_RESOURCE.get(filename);
		Image img = null;
		if (obj == null) {
			try {
				ImageIcon icon = new ImageIcon(BFApplet.class.getResource(filename));
				obj = icon.getImage();
			} catch (Exception e) {
				obj = Toolkit.getDefaultToolkit().createImage(new byte[0]);
			}
			IMG_RESOURCE.put(filename, obj);
		}
		img = (Image) obj;
		return img;
	}

	public void setJSObject(JSObject win) {
		this.win = win;
	}

	public int get_statues() {
		return this._statues;
	}

	// 编辑时用到的接口
	public Element getCurrToEdit() {
		return _currToEdit;
	}

	public void setCurrToEdit(Element _currToEdit) {
		this._currToEdit = _currToEdit;
	}

	public boolean getChanged() {
		return this._changed;
	}

	// happy modify
	public void editManualNode(ManualNode grp, String id, String name, String statelabel, String namelist, String note,
			String passcondition, String exceedaction, String backnodeid, String formname, String fieldpermlist, boolean issplit,
			boolean isgather, int actorEditMode, String actorListScript, String notificationStrategyJSON, boolean cBack,
			boolean isToPerson, int retracementEditMode, int backType, String bnodelist, boolean cRetracement,
			String retracementScript, int x, int y) {
		grp.id = id;
		grp.name = name;
		grp.statelabel = statelabel;
		grp.note = note;
		grp.namelist = namelist;

		grp.note = note;
		grp.passcondition = passcondition;
		grp.exceedaction = exceedaction;
		grp.backnodeid = backnodeid;

		// grp.properties = props;
		grp.formname = formname;
		grp.fieldpermlist = fieldpermlist;
		grp.issplit = issplit;
		grp.isgather = isgather;
		grp.actorEditMode = actorEditMode;
		grp.actorListScript = actorListScript;
		grp.notificationStrategyJSON = notificationStrategyJSON;

		// happy modify
		grp.cBack = cBack;
		grp.isToPerson = isToPerson;
		grp.retracementEditMode = retracementEditMode;
		grp.retracementScript = retracementScript;
		grp.backType = backType;
		grp.bnodelist = bnodelist;
		grp.cRetracement = cRetracement;

		grp.x = x;
		grp.y = y;

		this._selected = grp;
	}

	// the StartNode edit Process method

	public void editStartNode(StartNode sn, String id, String name, String statelabel, int x, int y) {

		sn.id = id;
		sn.name = name;
		sn.statelabel = statelabel;
		sn.x = x;
		sn.y = y;

		this._selected = sn;
	}

	public void editAbortNode(AbortNode sn, String id, String name, String statelabel, int x, int y) {

		sn.id = id;
		sn.name = name;
		sn.statelabel = statelabel;
		sn.x = x;
		sn.y = y;

		this._selected = sn;
	}

	public void editCompleteNode(CompleteNode sn, String id, String name, String statelabel, int x, int y) {

		sn.id = id;
		sn.name = name;
		sn.statelabel = statelabel;
		sn.x = x;
		sn.y = y;

		this._selected = sn;
	}

	public void editTerminateNode(TerminateNode sn, String id, String name, String statelabel, int x, int y) {

		sn.id = id;
		sn.name = name;
		sn.statelabel = statelabel;
		sn.x = x;
		sn.y = y;

		this._selected = sn;
	}

	public void editSuspendNode(SuspendNode sn, String id, String name, String statelabel, int x, int y) {

		sn.id = id;
		sn.name = name;
		sn.statelabel = statelabel;
		sn.x = x;
		sn.y = y;

		this._selected = sn;
	}

	// the AutoNode class process method

	public void editAutoNode(AutoNode an, String id, String name, String statelabel, boolean issplit, boolean isgather,
			int autoAuditType, String delayDay, String delayHour, String delayMinute, String auditDateTime, int x, int y) {

		an.id = id;
		an.name = name;
		an.statelabel = statelabel;
		an.auditDateTime = auditDateTime;
		an.isgather = isgather;
		an.issplit = issplit;
		an.autoAuditType = autoAuditType;
		an.delayDay = delayDay;
		an.delayHour = delayHour;
		an.delayMinute = delayMinute;
		an.x = x;
		an.y = y;

		this._selected = an;
	}

	@SuppressWarnings("deprecation")
	public void editSubFlow(SubFlow an, String id, String name, String statelabel, String subflowid, String subflowname,
			String startupScript, String callbackScript, String subflowFormid, String subflowFormname, boolean crossform, int x,
			int y) {

		an.id = id;
		an.name = name;
		an.statelabel = statelabel;
		an.subflowid = subflowid;
		an.subflowname = subflowname;
		an.startupScript = startupScript;
		an.callbackScript = callbackScript;
		an.subFlowFormId = subflowFormid;
		an.subFlowFormName = subflowFormname;
		an.crossform = crossform;
		an.x = x;
		an.y = y;

		this._selected = an;
	}

	/**
	 * @param id
	 * @param name
	 * @param note
	 * @roseuid 3E0406A90239
	 */
	public void editRelation(Relation rlt, String id, String name, String condition, String note, String action,
			String validateScript, String filtercondition, String editMode, String processDescription) {
		rlt.id = id;
		rlt.name = name;
		rlt.condition = condition;
		rlt.note = note;
		rlt.action = action;
		rlt.validateScript = validateScript;
		rlt.filtercondition = filtercondition;
		rlt.editMode = editMode;
		rlt.processDescription = processDescription;
		this._selected = rlt;
	}

	public boolean isCurrentSelected(Element em) {
		return em != null && _selected != null && em.equals(_selected);
	}

	public boolean isCurrentToEdit(Element em) {
		return em != null && _currToEdit != null && em.equals(_currToEdit);
	}

	/*
	 * 获取指定结点前的所有结点（踢除指定结点）
	 */
	public Vector<Element> getAllBeforeNode(Node node, boolean ispassed) {
		Vector<Element> all = getAllNodeBeforeNode(null, node, ispassed);
		if (isContain(all, node)) {
			all.removeElement(node);
		}
		return all;
	}

	/*
	 * 获取指定结点前的所有结点
	 */
	public Vector<Element> getAllNodeBeforeNode(Vector<Element> allnode, Node node, boolean ispassed) {
		if (allnode == null) {
			allnode = new Vector<Element>();
		}
		if (node == null || node instanceof StartNode) {
			return allnode;
		}

		Vector<Element> allrelation = getNodeBeforeRelation(node, ispassed);
		Enumeration<Element> enum11 = allrelation.elements();
		while (enum11.hasMoreElements()) {
			Object item = enum11.nextElement();
			if (item instanceof Relation) {
				Relation r = (Relation) item;
				Node beforeNode = getStartNode(r);
				// if(!allnode.contains(node)){
				if (!isContain(allnode, beforeNode)) {
					if (beforeNode instanceof ManualNode) {
						ManualNode tmp = (ManualNode) beforeNode;
						if ((tmp.namelist).indexOf("*") == -1) {
							allnode.addElement(beforeNode);
						}
					}
					allnode = getAllNodeBeforeNode(allnode, beforeNode, ispassed);
				}
			}
		}

		return allnode;
	}

	public boolean isContain(Vector<Element> all, Node beforeNode) {
		if (all != null) {
			Enumeration<Element> enum11 = all.elements();
			while (enum11.hasMoreElements()) {
				Object item = enum11.nextElement();
				if (item instanceof Node) {
					Node n = (Node) item;
					if (n.id == beforeNode.id) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public ManualNode addManualNode(String name, String statelabel, int x, int y) {
		ManualNode grp = new ManualNode(this);
		String id = Sequence.getSequence() + "";
		String namelist = "";
		String note = "";
		String passcondition = "";
		String exceedaction = "";
		String backnodeid = "";
		String formname = "";
		String fieldpermlist = "";
		boolean issplit = false;
		boolean isgather = false;
		int actorEditMode = 0;
		String actorListScript = "";
		String notificationStrategyJSON = "";

		// happy modify
		boolean cBack = true;
		boolean isToPerson = false;
		int retracementEditMode = 0;
		int backType = 0;
		String bnodelist = "";
		boolean cRetracement = false;
		String retracementScript = "";

		editManualNode(grp, id, name, statelabel, namelist, note, passcondition, exceedaction, backnodeid, formname,
				fieldpermlist, issplit, isgather, actorEditMode, actorListScript, notificationStrategyJSON, cBack, isToPerson,
				retracementEditMode, backType, bnodelist, cRetracement, retracementScript, x, y);

		this._elems.addElement(grp);
		return grp;
	}

	public ManualNode createManualNode() {
		ManualNode mn = new ManualNode(this);
		return mn;
	}

	// the StartNode add process method

	public StartNode addStartNode(String name, String statelabel, int x, int y) {
		StartNode cn = new StartNode(this);
		editStartNode(cn, Sequence.getSequence(), name, statelabel, x, y);
		this._elems.addElement(cn);
		return cn;
	}

	public AbortNode addAbortNode(String name, String statelabel, int x, int y) {
		AbortNode cn = new AbortNode(this);
		editAbortNode(cn, Sequence.getSequence(), name, statelabel, x, y);
		this._elems.addElement(cn);
		return cn;
	}

	public CompleteNode addCompleteNode(String name, String statelabel, int x, int y) {
		CompleteNode cn = new CompleteNode(this);
		editCompleteNode(cn, Sequence.getSequence(), name, statelabel, x, y);
		this._elems.addElement(cn);
		return cn;
	}

	public SuspendNode addSuspendNode(String name, String statelabel, int x, int y) {
		SuspendNode cn = new SuspendNode(this);
		editSuspendNode(cn, Sequence.getSequence(), name, statelabel, x, y);
		this._elems.addElement(cn);
		return cn;
	}

	public TerminateNode addTerminateNode(String name, String statelabel, int x, int y) {
		TerminateNode cn = new TerminateNode(this);
		editTerminateNode(cn, Sequence.getSequence(), name, statelabel, x, y);
		this._elems.addElement(cn);
		return cn;
	}

	// the AutoNode process method
	public AutoNode addAutoNode(String name, String statelabel, int x, int y) {
		AutoNode an = new AutoNode(this);
		editAutoNode(an, Sequence.getSequence(), name, statelabel, false, false, 1, "", "", "", "", x, y);
		this._elems.addElement(an);
		return an;
	}

	/**
	 * 添加子流程节点
	 * 
	 * @param name
	 * @param statelabel
	 * @param x
	 * @param y
	 * @return
	 */
	public SubFlow addSubFlow(String name, String statelabel, int x, int y) {
		SubFlow an = new SubFlow(this);
		String subflowid = "";
		String subflowname = "";
		String startupScript = "";
		String callbackScript = "";
		String subflowFormid = "";
		String subflowFormname = "";
		boolean crossform = false;

		editSubFlow(an, Sequence.getSequence(), name, statelabel, subflowid, subflowname, startupScript, callbackScript,
				subflowFormid, subflowFormname, crossform, x, y);
		this._elems.addElement(an);

		return an;
	}

	public void addRelation(String name, String condition, String note, String action, String validateScript,
			String filtercondition, String editMode, String processDescription) {
		Relation rlt = new Relation(this);
		editRelation(rlt, Sequence.getSequence(), name, condition, note, action, validateScript, filtercondition, editMode,
				processDescription);
		this._elems.addElement(rlt);
	}

	/**
	 * @param id
	 * @roseuid 3E0406950172
	 */
	public void delActor(String id) {
		delElement(id);
	}

	/**
	 * @param id
	 * @roseuid 3E0406B003D4
	 */
	public void delRelation(String id) {
		delElement(id);
	}

	/**
	 * 改变鼠标状态
	 * 
	 * @param statues
	 * @roseuid 3E0A6E1A0258
	 */
	public void changeStatues(int statues) {

		this._statues = statues;
		switch (_statues) {
		case ACTION_NORMAL:
			this.setCursor(new Cursor(Cursor.HAND_CURSOR));
			break;
		case ACTION_REMOVE:
			this.setCursor(new Cursor(Cursor.HAND_CURSOR));
			break;
		case ACTION_ADD_ABORTNODE:
			this.setCursor(new Cursor(Cursor.HAND_CURSOR));
			break;
		case ACTION_ADD_AUTONODE:
			this.setCursor(new Cursor(Cursor.HAND_CURSOR));
			break;
		case ACTION_ADD_COMPLETENODE:
			this.setCursor(new Cursor(Cursor.HAND_CURSOR));
			break;
		case ACTION_ADD_MANUALNODE:
			this.setCursor(new Cursor(Cursor.HAND_CURSOR));
			break;
		case ACTION_ADD_STARTNODE:
			this.setCursor(new Cursor(Cursor.HAND_CURSOR));
			break;
		case ACTION_ADD_SUSPENDNODE:
			this.setCursor(new Cursor(Cursor.HAND_CURSOR));
			break;
		case ACTION_ADD_TERMINATENODE:
			this.setCursor(new Cursor(Cursor.HAND_CURSOR));
			break;
		case ACTION_ADD_SUBFLOW:
			this.setCursor(new Cursor(Cursor.HAND_CURSOR));
			break;
		case ACTION_BREAK_LINE: // add by gusd
			this.setCursor(new Cursor(Cursor.HAND_CURSOR));
			break; // end

		default:
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}

	public void zoomIn() {
		if (_zoomrate * 0.9 < 0.5) {
			return;
		}

		_zoomrate *= 0.9;

		this.getGraphics().clearRect(0, 0, width, height);

		this.setSize((int) (width / _zoomrate), (int) (height / _zoomrate));

	}

	public void zoomOut() {
		if (_zoomrate / 0.9 > 2) {
			return;
		}
		_zoomrate /= 0.9;

		this.getGraphics().clearRect(0, 0, width, height);

		this.setSize((int) (width / _zoomrate), (int) (height / _zoomrate));

	}

	/**
	 * @param id
	 * @roseuid 3E0A6E1A03DF
	 */
	public void delElement(String id) {

		for (Enumeration<Element> e = _elems.elements(); e.hasMoreElements();) {
			Element em = e.nextElement();
			if (em.id != null && em.id.equals(id)) {
				_elems.removeElement(em);
			}

		}
	}

	/**
	 * @param g
	 * @roseuid 3E0A6E1B0047
	 */
	public void update(Graphics g) {
		Graphics tg = get_tmpimg().getGraphics();

		OGraphics og = new OGraphics(tg);

		paintTo(og, true);

		paintScreen(g);

	}

	public void paintTo(OGraphics og, boolean fillBackground) {

		// 清除背景
		if (fillBackground) {
			og.setColor(Color.WHITE);
			og.fillRect(0, 0, width, height);

			// 画背景网格
			og.setColor(Color.lightGray);

			for (int i = 0; i < width / 50; i++) {
				og.drawLine(i * 50, 0, i * 50, height);
			}

			for (int i = 0; i < width / 5; i++) {
				og.drawLine(i * 5, 0, i * 5, 5);
			}

			for (int i = 0; i < width / 25; i++) {
				og.drawLine(i * 25, 0, i * 25, 10);
			}

			for (int i = 0; i < width / 50; i++) {
				og.drawLine(0, i * 50, width, i * 50);
			}

			for (int i = 0; i < width / 5; i++) {
				og.drawLine(0, i * 5, 5, i * 5);
			}

			for (int i = 0; i < width / 25; i++) {
				og.drawLine(0, i * 25, 10, i * 25);
			}

		} else {
			og.setColor(Color.WHITE);
			og.fillRect(0, 0, width, height);
			// og.clearRect(0, 0, width, height);
		}

		// 画元素
		for (Enumeration<Element> e = _elems.elements(); e.hasMoreElements();) {
			Object te = e.nextElement();
			if (te instanceof PaintElement) {
				PaintElement em = (PaintElement) te;
				em.paint(og);
			}
		}
	}

	public Rectangle getMaxRect() {

		// OGraphics og = new OGraphics();
		// og.setCompressRate(0.5);
		// paintMobile(og);

		// ///////////////
		// 画元素

		Rectangle maxRect = new Rectangle(0, 0, 1, 1);
		for (Enumeration<Element> e = _elems.elements(); e.hasMoreElements();) {
			Object te = e.nextElement();
			if (te instanceof PaintElement) {
				PaintElement em = (PaintElement) te;
				maxRect.add(em.getRepaintRect());
			}
		}

		maxRect.setSize(maxRect.width + 50, maxRect.height + 50);

		return maxRect;
	}

	public void paintMobile(OGraphics mg) {

		// this.setSize(width, height);
		mg.setColor(Color.WHITE);

		// 画背景画布
		double rate = mg.getCompressRate();
		mg.setCompressRate(1.0);
		mg.fillRect(0, 0, mg.getMaxX(), mg.getMaxY());

		mg.setCompressRate(rate);
		for (Enumeration<Element> e = _elems.elements(); e.hasMoreElements();) {
			Object te = e.nextElement();
			if (te instanceof PaintElement) {
				PaintElement em = (PaintElement) te;
				em.paintMobile(mg);
			}
		}
	}

	private void paintScreen(Graphics g) {
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		g.drawImage(get_tmpimg(), 0, 0, (int) (width / _zoomrate), (int) (height / _zoomrate), 0, 0, width, height, null);
	}

	/**
	 * @param g
	 * @roseuid 3E0A6E1B0065
	 */
	public void paint(Graphics g) {
		update(g);
	}

	/**
	 * @param e
	 * @roseuid 3E0A6E1B0079
	 */

	public void appendElement(Element e) {
		_elems.addElement(e);
	}

	public String toXML() {
		String rslt = "";
		try {

			Class<? extends FlowDiagram> cls = this.getClass();

			rslt = "<" + cls.getName() + ">\n";

			Field[] flds = cls.getFields();

			for (int i = 0; i < flds.length; i++) {
				Field field = flds[i];
				String fieldval = "";

				Class<?> type = field.getType();

				if (field != null) {
					if (type.equals(Long.TYPE)) { // Long
						fieldval = field.getLong(this) + "";
						rslt += "<" + flds[i].getName() + ">" + fieldval + "</" + flds[i].getName() + ">\n";
						continue;
					} else if (type.equals(Integer.TYPE)) { // Int
						fieldval = field.getInt(this) + "";
						rslt += "<" + flds[i].getName() + ">" + fieldval + "</" + flds[i].getName() + ">\n";
						continue;
					} else if (type.equals(Short.TYPE)) { // Short
						fieldval = field.getShort(this) + "";
						rslt += "<" + flds[i].getName() + ">" + fieldval + "</" + flds[i].getName() + ">\n";
						continue;
					} else if (type.equals(Double.TYPE)) { // Double
						fieldval = field.getDouble(this) + "";
						rslt += "<" + flds[i].getName() + ">" + fieldval + "</" + flds[i].getName() + ">\n";
						continue;
					} else if (type.equals(Class.forName("java.lang.String"))) { // String
						fieldval = (String) field.get(this);
						rslt += "<" + flds[i].getName() + ">" + fieldval + "</" + flds[i].getName() + ">\n";
						continue;
					}

					else if (type.equals(Float.TYPE)) { // Float
						fieldval = field.getFloat(this) + "";
						rslt += "<" + flds[i].getName() + ">" + fieldval + "</" + flds[i].getName() + ">\n";
						continue;
					}

					else if (type.equals(Boolean.TYPE)) { // Boolean
						fieldval = field.getBoolean(this) + "";
						rslt += "<" + flds[i].getName() + ">" + fieldval + "</" + flds[i].getName() + ">\n";
						continue;
					}

					else if (type.equals(Class.forName("java.sql.Date"))) { // Date
						fieldval = field.get(this) + "";
						rslt += "<" + flds[i].getName() + ">" + fieldval + "</" + flds[i].getName() + ">\n";
						continue;
					}

					else if (type.equals(Class.forName("java.util.Date"))) { // java.util.Date
						fieldval = field.get(this) + "";
						rslt += "<" + flds[i].getName() + ">" + fieldval + "</" + flds[i].getName() + ">\n";
						continue;
					}
				}
			}
			// rslt += ">\n";

			// 递归调用SUBELEMENT的TOXML方法
			for (Enumeration<Element> em = this._elems.elements(); em.hasMoreElements();) {
				Element subelm = em.nextElement();
				rslt += subelm.toXML();
			}

			rslt += "</" + cls.getName() + ">\n";
		} catch (Exception e) {

		}

		return rslt;

	}

	/**
	 * @param e
	 * @roseuid 3E0A6E1B0097
	 */
	public void removeElement(Element emn) {
		if (emn != null) {
			if (emn instanceof Node) {
				Vector<Element> v = getAllElements();
				for (Enumeration<Element> e = v.elements(); e.hasMoreElements();) {
					Element elem = e.nextElement();
					if (elem instanceof Relation) {
						Relation r = (Relation) elem;
						if ((r.startnodeid != null && r.startnodeid.equals(emn.id))
								|| (r.endnodeid != null && r.endnodeid.equals(emn.id))) {
							_elems.removeElement(r);
						}
					}
				}
				_elems.removeElement(emn);
			} else {
				// Relation r = (Relation) emn;
				// r.getStartnode().removeSubElement(r.id);
				_elems.removeElement(emn);
			}
		}
		_currToEdit = null;
	}

	/**
	 * @param id
	 * @roseuid 3E0A6E1B00AB
	 */
	public void removeElement(String id) {
		for (Enumeration<Element> e = _elems.elements(); e.hasMoreElements();) {
			Element em = e.nextElement();

			if (em.id != null && em.id.equals(id)) {
				_elems.removeElement(em);
			}

		}
	}

	/**
	 * @param x
	 * @param y
	 * @return OLink.bpm.core.workflow.Element
	 * @roseuid 3E0A6E1B00C9
	 */
	public PaintElement chkSelectedElement(int x, int y) {
		Vector<Element> v = getAllElements();
		for (Enumeration<Element> e = v.elements(); e.hasMoreElements();) {
			Object te = e.nextElement();
			if (te instanceof PaintElement) {
				PaintElement em = (PaintElement) te;

				if (em.isSelected(x, y)) {
					return em;
				}

			}
		}
		return null;
	}

	public int getFlowstatus() {
		return this.flowstatus;
	}

	/**
	 * 设置流程运转路径
	 * 
	 * @param
	 */
	public void setFlowpath(String path) {
		if (this.flowpath == null || this.flowpath.trim().length() <= 0) {
			this.flowpath = path;
		} else {
			this.flowpath = this.flowpath + ";" + path;
		}
	}

	/**
	 * 获取流程运转路径
	 * 
	 * @param
	 */
	public Collection<String[]> getFlowpath() {
		Collection<String[]> colls = new ArrayList<String[]>();
		if (flowpath != null && flowpath.trim().length() > 0) {
			String[] path = CommonUtil.split(this.flowpath, ';');
			for (int i = 0; i < path.length; i++) {
				String[] t = CommonUtil.split(path[i], ',');
				colls.add(t);
			}
		}
		return colls;
	}

	/**
	 * 获取流程运转路径最后审核结点
	 * 
	 * @param
	 */
	public Node getFlowpathLastNode() {
		Collection<String[]> colls = getFlowpath();
		Object[] obj = colls.toArray();
		String nodeid = "";
		Node node = null;
		if (obj.length >= 1) {
			String[] path = (String[]) obj[obj.length - 1];
			nodeid = path[0];
		}
		if (nodeid != null && nodeid.trim().length() > 0) {
			node = (Node) getElementByID(nodeid);
		}
		return node;
	}

	/**
	 * 设置流程状态
	 * 
	 * @param
	 */
	public void setFlowstatus(int status) throws Exception {
		// if (this.flowstatus == FLOWSTATUS_OPEN_NOSTART &&
		// ( (status & FLOWSTATUS_OPEN_START) > 0)) {
		// this.flowstatus = status;
		// Node n = getFirstNode(); //设起始点
		// setCurrentNode(n);
		// }
		// else
		if (this.flowstatus == FlowType.FLOWSTATUS_OPEN_NOSTART
				&& ((status & (FlowType.FLOWSTATUS_OPEN_RUN_RUNNING | FlowType.FLOWSTATUS_CLOSE_TERMINAT)) > 0)) {
			this.flowstatus = status;
		} else if (this.flowstatus == FlowType.FLOWSTATUS_OPEN_RUN_RUNNING
				&& ((status & (FlowType.FLOWSTATUS_OPEN_RUN_SUSPEND | FlowType.FLOWSTATUS_CLOSE_COMPLETE
						| FlowType.FLOWSTATUS_CLOSE_TERMINAT | FlowType.FLOWSTATUS_OPEN_RUN_RUNNING)) > 0)) {
			this.flowstatus = status;
		} else if (this.flowstatus == FlowType.FLOWSTATUS_OPEN_RUN_SUSPEND
				&& ((status & (FlowType.FLOWSTATUS_OPEN_RUN_RUNNING | FlowType.FLOWSTATUS_OPEN_RUN_SUSPEND | FlowType.FLOWSTATUS_CLOSE_ABORT)) > 0)) {
			this.flowstatus = status;
		} else {
			throw new Exception("{*[core.workflow.status.error]*}");
		}
		// //保存流程流转路径
		// if (this.flowstatus == FLOWSTATUS_OPEN_START) {
		// setFlowpath(getCurrentNode().id + "," + START);
		// }else if (this.flowstatus == FLOWSTATUS_OPEN_RUN_RUNNING){
		// setFlowpath(getCurrentNode().id + "," + PASS);
		// }else if (this.flowstatus == FLOWSTATUS_OPEN_RUN_SUSPEND){
		// setFlowpath(getCurrentNode().id + "," + SUSPEND);
		// }else if (this.flowstatus == FLOWSTATUS_CLOSE_TERMINAT){
		// setFlowpath(getCurrentNode().id + "," + TERMINATE);
		// }else if (this.flowstatus == FLOWSTATUS_CLOSE_ABORT){
		// setFlowpath(getCurrentNode().id + "," + ABORT);
		// }else if (this.flowstatus == FLOWSTATUS_CLOSE_COMPLETE){
		// setFlowpath(getCurrentNode().id + "," + COMPLETE);
		// }
	}

	/**
	 * 获取当前结点
	 * 
	 * @param
	 */
	public Node getFirstCurrentNode() {
		Vector<Element> ems = getAllElements();
		Enumeration<Element> enum11 = ems.elements();
		while (enum11.hasMoreElements()) {
			Element item = enum11.nextElement();
			if (item instanceof Node) {
				Node nd = (Node) item;
				if (nd._iscurrent) {
					return nd;
				}
			}
		}

		return null;
	}

	/**
	 * 获取流程的第一个结点
	 * 
	 * @param
	 */
	public Node getFirstNode() {
		Vector<Element> ems = getAllElements();
		Enumeration<Element> enum11 = ems.elements();
		while (enum11.hasMoreElements()) {
			Element item = enum11.nextElement();
			if (item instanceof Node) {
				Node nd = (Node) item;
				if (nd instanceof StartNode) {
					return nd;
				}
			}
		}
		return null;
	}

	/**
	 * 获取所有开始节点
	 * 
	 * @return
	 */
	public Collection<StartNode> getStartNodeList() {
		Vector<Element> ems = getAllElements();
		Enumeration<Element> enum11 = ems.elements();
		Collection<StartNode> colls = new ArrayList<StartNode>();
		while (enum11.hasMoreElements()) {
			Element item = enum11.nextElement();
			if (item instanceof Node) {
				Node nd = (Node) item;
				if (nd instanceof StartNode) {
					colls.add((StartNode) nd);
				}
			}
		}
		return colls;
	}

	/**
	 * 获取当前结点的所有下一个Relation即步骤
	 * 
	 * @param
	 */
	public Vector<Relation> getNodeNextRelation(Node nd) {
		if (nd == null) {
			return null;
		}
		Vector<Relation> rv = new Vector<Relation>();
		Enumeration<Element> enum11 = this._elems.elements();
		while (enum11.hasMoreElements()) {
			Object item = enum11.nextElement();
			// 添加子元素
			if (item instanceof Node) {
				Node node = (Node) item;
				Collection<Element> subElements = node.getSubelems();
				for (Iterator<Element> iterator = subElements.iterator(); iterator.hasNext();) {
					Object subElment = iterator.next();
					if (subElment instanceof Relation) {
						Relation r = (Relation) subElment;
						if (r.startnodeid != null && r.startnodeid.equals(nd.id)) {
							rv.addElement(r);
						}
					}
				}
			}

			if (item instanceof Relation) {
				Relation r = (Relation) item;
				if (r.startnodeid != null && r.startnodeid.equals(nd.id)) {
					rv.addElement((Relation) item);
				}
			}
		}

		return rv;
	}

	/**
	 * 获取当前结点的所有上一个Relation即步骤
	 * 
	 * @param
	 */
	public Vector<Element> getNodeBeforeRelation(Node nd, boolean ispassed) {
		if (nd == null) {
			return null;
		}
		Vector<Element> rv = new Vector<Element>();
		for (Enumeration<Element> e = _elems.elements(); e.hasMoreElements();) {
			Element em = e.nextElement();
			if (em._subelems != null) {
				for (Enumeration<Element> sube = em._subelems.elements(); sube.hasMoreElements();) {
					Element subem = sube.nextElement();
					if (subem instanceof Relation) {
						Relation relation = (Relation) subem;
						if (relation.id != null && relation.endnodeid.equals(nd.id)) {
							if (ispassed) {
								if (relation.ispassed) {
									rv.addElement(relation);
								}
							} else {
								rv.addElement(relation);
							}

						}
					}

				}
			}

		}

		return rv;
	}

	/**
	 * 根据当前relation获取下一结点
	 * 
	 * @param
	 * @throws InterruptedException 
	 * @throws Exception
	 */
	public Node getNextNode(Relation r)  {
		Node end = null;
//		try {
//			Thread.sleep(1);
//		} catch (InterruptedException e1) {
//			e1.printStackTrace();
//		}//手工停止1毫秒
//		logger.info("开始获取下一个节点........,session id is:"+_sessionid);
		if(_sessionid==null){
			return null;
		}
		IRunner runner = JavaScriptFactory.getInstance(_sessionid, _applicationid);
		Boolean flag = new Boolean(true);

		String condition = r.condition;
		String filtercondition = r.filtercondition;// marky
		condition = StringUtil.dencodeHTML(condition);
		filtercondition = StringUtil.dencodeHTML(filtercondition);// marky
		try {
			String labelId = getId() + "-" + r.startnodeid + "-" + r.endnodeid;
			if (r.editMode != null && r.editMode.equals(Relation.EDITMODE_VIEW)) {// '00':view
				if (!(filtercondition).equals("") && (filtercondition) != null) {// marky
					filtercondition.replaceAll("\n", " ");

					StringBuffer label = new StringBuffer();
					
					label.append("RELATION(").append(labelId).append(r.name + ")").append(".filtercondition");
					//logger.info("取出连线的条件进行执行......."+label.toString());

					Object obj = runner.run(label.toString(), filtercondition);
					if (obj instanceof Boolean) {
						flag = (Boolean) obj;
					}
				}

			} else {
				if (!(condition).equals("") && (condition) != null) {
					condition.replaceAll("\n", " ");

					StringBuffer label = new StringBuffer();
					
					label.append("RELATION(").append(labelId).append(r.name + ")").append(".condition");
//					logger.info("取出连线的条件脚本进行执行......."+label.toString());
				
					Object obj = runner.run(label.toString(), condition);
					if (obj instanceof Boolean) {
						flag = (Boolean) obj;
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (flag.booleanValue()) {
			end = r.getEndnode();
		}

		return end;
	}

	/**
	 * 获取当前任一relation中上一结点
	 * 
	 * @param
	 */
	public Node getStartNode(Relation r) {
		Node end = r.getStartnode();
		return end;
	}

	/**
	 * 将结点设为当前结点
	 * 
	 * @param
	 */
	public void setCurrentNode(Node current) {
		if (current == null) {
			return;
		}
		// Vector ems = getAllElements();
		// Enumeration enum1 = ems.elements();
		// while (enum1.hasMoreElements()) {
		// Element item = (Element) enum1.nextElement();
		// if (item instanceof Node) {
		// Node nd = (Node) item;
		// if (nd.iscurrent) {
		// nd.iscurrent = false;
		// }
		// if (nd.id != null && current.id !=null && nd.id.equals(current.id)) {
		// nd.iscurrent = true;
		// }
		// }
		// }
		current._iscurrent = true;
	}

	/**
	 * @return java.util.Vector
	 * @roseuid 3E0A6E1B00E7
	 */
	public Vector<Element> getAllElements() {
		Vector<Element> vct = new Vector<Element>();
		for (Enumeration<Element> e = _elems.elements(); e.hasMoreElements();) {
			Element em = e.nextElement();
			vct.addElement(em);

			if (em._subelems != null) {
				for (Enumeration<Element> sube = em._subelems.elements(); sube.hasMoreElements();) {
					vct.addElement(sube.nextElement());
				}
			}

		}
		return vct;
	}
	
	public Collection<Node> getAllNodes() {
		Collection<Node> rtn = new Vector<Node>();
		Vector<Element> elements = getAllElements();
		for (Iterator<Element> iterator = elements.iterator(); iterator.hasNext();) {
			Element element = iterator.next();
			if (element instanceof Node){
				rtn.add((Node)element);
			}
		}
		return rtn;
	}
	
	/**
	 * 根据开始节点和结束接点获取关系
	 * 
	 * @return
	 */
	public Relation getRelation(String startnodeid, String endnodeid) {
		Collection<Element> colls = this.getAllElements();
		for (Iterator<Element> iter = colls.iterator(); iter.hasNext();) {
			Object element = iter.next();
			if (element instanceof Relation) {
				Relation relation = (Relation) element;
				if (startnodeid.equals(relation.startnodeid) && endnodeid.equals(relation.endnodeid)) {
					return relation;
				}
			}
		}
		return null;
	}

	public Object validate(IRunner runner, String startnodeid, String endnodeid) throws Exception {
		Relation relation = this.getRelation(startnodeid, endnodeid);
		if (relation != null) {
			String relationId = getId() + startnodeid + endnodeid;
			String script = StringUtil.dencodeHTML(relation.validateScript);
			if (script != null && !script.equals("")) {
				StringBuffer label = new StringBuffer();
				label.append("RELATION(").append(relationId).append(relation.name + ")").append(".Validate");
				Object rtn = runner.run(label.toString(), script);
				return rtn;
			}
		}
		return null;
	}

	public void runAction(IRunner runner, String startnodeid, String endnodeid) throws Exception {
		Relation relation = this.getRelation(startnodeid, endnodeid);
		if (relation != null) {
			String action = relation.action;
			if (action != null && action.trim().length() > 0) {
				action = StringUtil.dencodeHTML(action);

				StringBuffer label = new StringBuffer();
				// relationId = flowid + startnodeid + endnodeid 标识流程线段唯一
				String relationId = getId() + "-" + startnodeid + "-" + endnodeid;
				label.append("RELATION(").append(relationId).append(").");
				label.append(relation.name).append(".Action");
				Object rtn = runner.run(label.toString(), action);
				if (rtn instanceof String && !StringUtil.isBlank((String) rtn)) {
					throw new RunActionException((String) rtn);
				}
			}
		}
	}

	/**
	 * @param id
	 * @return OLink.bpm.core.workflow.Element
	 * @roseuid 3E0A6E1B00F1
	 */
	public Element getElementByID(String id) {

		if (id == null || id.trim().length() == 0) {
			return null;
		}

		for (Enumeration<Element> e = _elems.elements(); e.hasMoreElements();) {
			Element em = e.nextElement();
			if (em.id != null && em.id.equals(id)) {
				return em;
			}
			if (em.getSubelems() != null) {
				for (Enumeration<Element> sube = em.getSubelems().elements(); sube.hasMoreElements();) {
					Element subem = sube.nextElement();

					if (subem.id != null && subem.id.equals(id)) {
						return subem;
					}
				}
			}

		}
		return null;
	}

	public Node getNodeByID(String id) {
		Element element = getElementByID(id);
		if (element instanceof Node) {
			return (Node) element;
		}
		return null;
	}

	/**
	 * 根据当前节点获取上一步所有节点 happy
	 * 
	 * @param node
	 * @return
	 */
	public Vector<Node> getBackSetpNode(Node node) {
		Vector<Node> nodes = new Vector<Node>();
		Vector<Relation> allrelation = getNodeBackStepRelation(node);
		for (Iterator<Relation> iter = allrelation.iterator(); iter.hasNext();) {
			Relation re = iter.next();
			if (re.endnodeid.equals(node.id)) {
				Node n = getStartNode(re);
				if (!(n instanceof AutoNode) && !(n instanceof StartNode)) {
					nodes.add(n);
				}
			}
		}
		return nodes;

	}

	/**
	 * 根据当前节点获取所有上一步Relation happy
	 * 
	 * @param nd
	 * @return
	 */
	public Vector<Relation> getNodeBackStepRelation(Node nd) {
		if (nd == null) {
			return null;
		}
		Vector<Relation> rv = new Vector<Relation>();
		for (Iterator<Element> iter = _elems.iterator(); iter.hasNext();) {
			Element subem = iter.next();
			if (subem instanceof Relation) {
				Relation relation = (Relation) subem;
				if (relation.id != null && relation.endnodeid.equals(nd.id)) {
					rv.add(relation);
				}
			}
		}
		return rv;
	}

	/**
	 * 获取流程流转Relation
	 * 
	 * @param
	 */
	public Element getElementByBeginEndNodeID(String startid, String endid) {

		if (startid == null || startid.trim().length() == 0 || endid == null || endid.trim().length() == 0) {
			return null;
		}

		for (Enumeration<Element> e = _elems.elements(); e.hasMoreElements();) {
			Element em = e.nextElement();
			if (em.getSubelems() != null) {
				for (Enumeration<Element> sube = em.getSubelems().elements(); sube.hasMoreElements();) {
					Element subem = sube.nextElement();
					if (subem instanceof Relation) {
						Relation relation = (Relation) subem;
						if (relation.id != null && relation.startnodeid.equals(startid) && relation.endnodeid.equals(endid)) {
							return subem;
						}
					}

				}
			}

		}
		return null;
	}

	/**
	 * @throws Exception
	 * @roseuid 3E0A6E1B010F
	 */
	private void jbInit() throws Exception {
		this.addComponentListener(new java.awt.event.ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				this_componentResized(e);
			}
		});
		this.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				this_mouseMoved(e);
			}

			public void mouseDragged(MouseEvent e) {
				this_mouseDragged(e);
			}
		});
		this.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				this_mouseClicked(e);
			}

			public void mousePressed(MouseEvent e) {
				this_mousePressed(e);
			}

			public void mouseReleased(MouseEvent e) {
				this_mouseReleased(e);
			}

			public void mouseEntered(MouseEvent e) {
				this_mouseEntered(e);
			}
		});
	}

	/**
	 * @param e
	 * @roseuid 3E0A6E1B0123
	 */
	void this_mouseClicked(MouseEvent e) {
		int x = getDiagramX(e);
		int y = getDiagramY(e);

		if (e.getClickCount() >= 2) {
			Element em = this.chkSelectedElement(x, y);
			if (em != null) {
				win.eval("javascript:editElement()");
			}
		}
	}

	/**
	 * @param e
	 * @roseuid 3E0A6E1B0137
	 */
	void this_mousePressed(MouseEvent e) {
		int x = getDiagramX(e);
		int y = getDiagramY(e);

		switch (this._statues) {
		case ACTION_ADD_ABORTNODE:
			addAbortNode("", "", x, y);
			break;
		case ACTION_ADD_AUTONODE:
			addAutoNode("", "", x, y);
			break;
		case ACTION_ADD_COMPLETENODE:
			addCompleteNode("", "", x, y);
			break;
		case ACTION_ADD_MANUALNODE:
			addManualNode("", "", x, y);
			break;
		case ACTION_ADD_RELATION:
			addRelation("", "", "", "", "", "", "", "");
			break;
		case ACTION_ADD_STARTNODE:
			addStartNode("", "", x, y);
			break;
		case ACTION_ADD_SUSPENDNODE:
			addSuspendNode("", "", x, y);
			break;
		case ACTION_ADD_TERMINATENODE:
			addTerminateNode("", "", x, y);
			break;
		case ACTION_ADD_SUBFLOW:
			addSubFlow("", "", x, y);
			break;
		default:
			break;
		}

		boolean isChangeCursor = false;

		PaintElement em = this.chkSelectedElement(x, y);
		if (em != null) {
			if (em instanceof Node) {// 设置抓取点
				((Node) em)._handlePoint.x = x - ((Node) em).x;
				((Node) em)._handlePoint.y = y - ((Node) em).y;
			}

			_changed = true;
			if (_statues == ACTION_REMOVE) {
				// deleteMSG = null;
				if (em instanceof Relation) {
					Relation r = (Relation) em;
					if (r.ispassed) {
						deleteMSG = "相关流程已处理,不能删除!";
					}
				}
				if (em instanceof Node) {
					Node nd = (Node) em;
					if (em.id != null && nd._iscurrent) {
						deleteMSG = "当前节点在处理中,不能删除!";
					} else {
						for (Enumeration<Element> el = this.getAllElements().elements(); deleteMSG == null && el.hasMoreElements();) {
							Element elem = el.nextElement();
							if (elem instanceof Relation) {
								Relation rl = (Relation) elem;
								if (((rl.startnodeid != null && rl.startnodeid.equals(em.id)) || (rl.endnodeid != null && rl.endnodeid
										.equals(em.id)))
										&& rl.ispassed) {
									deleteMSG = "相关流程已处理,不能删除!";
								}
							}
						}
					}
				}
				if (deleteMSG == null) {
					this.removeElement(em);
				}
			} else if (_statues == ACTION_BREAK_LINE) { // add by gusd

				if (em instanceof Relation) {
					_selected = em;
					Relation relation = (Relation) _selected;
					relation.setBreakpoint(new Point(x, y));
					isChangeCursor = true;
				}

			} // add by gusd

			else {
				if (_selected instanceof Relation && em instanceof Node) {
					Relation r = (Relation) _selected;
					if (r.getStartnode() == null && em != null) {
						r.setStartnode((Node) em);
						Node node = (Node) em;
						Point point = new Point(node.x, node.y);
						r.addVector(point);
					}
				} else if (em instanceof Relation) {
					this.changeStatues(ACTION_BREAK_LINE);
					_selected = em;
					Relation relation = (Relation) _selected;
					relation.setChangevector(-1);
					relation.setBreakpoint(new Point(x, y));
					relation.setCurrentselect(true);
					isChangeCursor = true;

				} else {
					this._selected = em;
					_selected.moveTo(x, y);
				}
			}
			repaint();
		} else {// em == null

			if ((_statues == FlowDiagram.ACTION_ADD_RELATION) && (_selected instanceof Relation)) {
				Relation r = (Relation) _selected;
				if (r.getStartnode() == null) {
					this.removeElement(r);
					r = null;
					_selected = null;
				}
			} else {
				if (_selected instanceof Node) {
					// ((Node)_selected).
				}
				_selected = null;
				_currToEdit = null;
			}
			repaint();
		}
		if (!isChangeCursor) { // isChangeCursor为true时，用户准备拖拉流程线，不释放鼠标指针的拖拉样式
			this.changeStatues(FlowDiagram.ACTION_NORMAL);
			isChangeCursor = false;
		}

	}

	/**
	 * @param e
	 * @roseuid 3E0A6E1B0155
	 */
	void this_mouseReleased(MouseEvent e) {
		int x = getDiagramX(e);
		int y = getDiagramY(e);

		Element em = this.chkSelectedElement(x, y);

		if (_selected != null && _selected instanceof Relation && (em == null || em instanceof Node)) {
			Relation r = (Relation) _selected;
			r.setCurrentselect(false);
			if (r.getEndnode() == null) {
				if (em != null) {
					_changed = true;
					r.setEndnode((Node) em);
					Node node = (Node) em;

					Point point = new Point(node.x, node.y);
					r.addVector(point);
				}
				if (em == null ){//|| (false && r.getStartnode().id.equals(r.getEndnode().id))) {
					_elems.removeElement(r);
				}
			} else {
				// 忽略掉10个像素差异
				int nx = Math.round((x + 10) / 20) * 20;
				int ny = Math.round((y + 10) / 20) * 20;

				int pos = r.getChangevector();
				r.setCurrentselect(false);
				if (pos == -1) { // 原先按下的点不是原有折点
					boolean bool = r.checkDistance(new Point(nx, ny));
					if (!bool) { // 检查鼠标释放的点拖拉的距离是否小于一个常量，如果是，则当作没有拖拉
						r.addVector(new Point(nx, ny)); // 如果鼠标释放的点拖拉的距离大于一个常量，则当作一个新的折点
					}
				} else { // 原先按下的点为原有折点，鼠标释放后要改变原有折点的坐标
					r.changeVector(new Point(nx, ny));
					r.setChangevector(-1);
				}

			}

		}
		// 设置当前选中
		if (_selected != null && em != null && _selected.equals(em)) {
			_currToEdit = em;

			if (em instanceof Node) {
				Node nd = (Node) em;
				Point p = new Point(nd.x + nd._imgrect.width / 2, nd.y + nd._imgrect.height / 2);

				// 忽略掉10个像素差异
				int nx = p.x;
				int ny = p.y;

				nx = Math.round((nx + 10) / 20) * 20;
				ny = Math.round((ny + 10) / 20) * 20;

				((Node) em).x = nx - nd._imgrect.width / 2;
				((Node) em).y = ny - nd._imgrect.height / 2;
			}
		}

		this.changeStatues(FlowDiagram.ACTION_NORMAL);
		_selected = null;

		this.repaint();
	}

	/**
	 * @param e
	 * @roseuid 3E0A6E1B0169
	 */
	void this_mouseMoved(MouseEvent e) {
		int x = getDiagramX(e);
		int y = getDiagramY(e);

		if (this._statues == ACTION_BREAK_LINE) {
		}

		else {
			Graphics tg = get_tmpimg().getGraphics();
			tg.setColor(Color.black);
			PaintElement em = this.chkSelectedElement(x, y);
			if ((em != null) && !(em instanceof Relation)) {
				this.changeStatues(this._statues);
				// if(this._statues == ACTION_REMOVE){

				// }
				Node nd = (Node) em;
				if (em.isSelected(x, y)) {
					@SuppressWarnings("unused")
					String snote = "";
					if (nd.note == null || nd.note.equals("null")) {
						snote = "";
					} else if (nd.note.getBytes().length <= 18) {
						snote = nd.note;
					} else {
						snote = nd.note.substring(0, 9) + "...";
					}

					nd.showTips(tg); // 显示注释
					paintScreen(this.getGraphics());
				}
			} else {
				this.repaint();
			}

		}
	}

	/**
	 * @param e
	 * @roseuid 3E0A6E1B0187
	 */
	void this_mouseDragged(MouseEvent e) {

		int x = getDiagramX(e);
		int y = getDiagramY(e);
		if (this._selected != null && this._statues != ACTION_BREAK_LINE && this._selected instanceof Relation) { // 从一个结点到另一个结点画流程的拖拉过程中
			Relation r = (Relation) this._selected;
			if (r.getEndnode() == null) {
				_selected.moveTo(x, y);
				this.repaint();
			}
		} else if (this._selected != null && this._statues != ACTION_BREAK_LINE) {

			_selected.moveTo(x, y);

			this.repaint();

		} else if (this._selected != null && this._selected instanceof Relation && this._statues == ACTION_BREAK_LINE) { // 拖拉流程线产生折点的过程
			Relation r = (Relation) this._selected;
			Point p = new Point(x, y);
			int pos = r.getChangevector(); // 检查拖拉点是否原有折点
			r.setCurrentselect(true);
			if (pos == -1) { // 拖拉点不是原有折点，把鼠标移动点作为临时的_movepoint
				r.setMovepoint(p);
			} else { // 拖拉点是原有折点,鼠标移动点当作原有折点的新位置
				r.changeVector(p);
			}

			this.repaint();
		} // end

	}

	/**
	 * @param e
	 * @roseuid 3E0A6E1B019B
	 */
	void this_componentResized(ComponentEvent e) {
		// _tmpimg = this.createImage(this.size().width, this.size().height);
	}

	public boolean isAssignBack(Node node) { // 删除节点时判断此节点是否为指定回退节点
		boolean isAssignBack = false;
		for (Enumeration<Element> e = this.getAllElements().elements(); e.hasMoreElements();) {
			Element em = e.nextElement();
			if (em != null && em instanceof ManualNode) {
				ManualNode nd = (ManualNode) em;
				if (nd.exceedaction != null && nd.exceedaction.trim().equals(FlowType.DOBACKTONODE)
						&& nd.backnodeid.equals(node.id)) {
					return true;
				}
			}
		}
		return isAssignBack;
	}

	void this_mouseEntered(MouseEvent e) {
	}

	public static void main(String[] args) {
		FlowDiagram fd = new FlowDiagram();
		try {

			fd.setFlowstatus(FlowType.FLOWSTATUS_OPEN_RUN_RUNNING);
			fd.setFlowstatus(FlowType.FLOWSTATUS_OPEN_RUN_SUSPEND);
			fd.setFlowstatus(FlowType.FLOWSTATUS_OPEN_RUN_SUSPEND);
			fd.setFlowstatus(FlowType.FLOWSTATUS_OPEN_RUN_RUNNING);
			fd.setFlowstatus(FlowType.FLOWSTATUS_OPEN_RUN_RUNNING);
			fd.setFlowstatus(FlowType.FLOWSTATUS_CLOSE_COMPLETE);
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	/**
	 * 获取当前节点关联的所有下一个节点
	 * 
	 * @param currnodeid
	 *            当前节点
	 * @return 当前节点关联的所有下一个节点
	 * @throws InterruptedException 
	 */
	public Collection<Node> getNextNodeList(String currnodeid)  {

		Element em = this.getElementByID(currnodeid);
		if (em instanceof Node) {
			Node node = (Node) em;
			Vector<Relation> relations = this.getNodeNextRelation(node);
			Collection<Node> colls = new ArrayList<Node>();
			if (relations != null) {
				Iterator<Relation> it = relations.iterator();

				while (it.hasNext()) {
					Node nextNode = this.getNextNode(it.next());
					if (nextNode != null) {
						colls.add(nextNode);
					}
				}
			}
			return colls;

		}
		return null;
	}

	/**
	 * 获取所有后续节点列表
	 */
	public Collection<Node> getAllFollowNodeOnPath(String nodeid) {
		class FollowNodeOnPath {
			Collection<Node> list = new ArrayList<Node>();

			Collection<Node> getAllFollowNodeOnPath(Node beginNode) {
				Collection<Node> nextNodeList = getNextNodeList(beginNode.id);
				for (Iterator<Node> iter = nextNodeList.iterator(); iter.hasNext();) {
					Node node = iter.next();
					if (!list.contains(node)) {
						list.add(node);
						list.addAll(getAllFollowNodeOnPath(node));
					}
				}
				return list;
			}
		}
		Element em = getElementByID(nodeid);
		if (em != null && em instanceof Node) {
			Node node = (Node) getElementByID(nodeid);

			return (new FollowNodeOnPath()).getAllFollowNodeOnPath(node);
		}
		return null;
	}

	/**
	 * 获取当前节点列表关联的所有下一个节点
	 */
	public Collection<Node> getNextNodeList(Collection<Node> nodelist) {
		ArrayList<Node> rtn = new ArrayList<Node>();
		if (nodelist != null) {
			for (Iterator<Node> iter = nodelist.iterator(); iter.hasNext();) {
				Node nd = iter.next();
				String currid = nd.id;
				Collection<Node> colls = getNextNodeList(currid);
				rtn.addAll(colls);
			}
		}
		return rtn;
	}

	/**
	 * 获取结点列表
	 */
	public Collection<Node> getNodeListByIds(String[] ids) {
		ArrayList<Node> rtn = new ArrayList<Node>();
		if (ids != null) {
			for (int i = 0; i < ids.length; i++) {
				Element em = this.getElementByID(ids[i]);
				if (em instanceof Node) {
					rtn.add((Node) em);
				}
			}
		}
		return rtn;
	}

	/**
	 * 获取子流程ID列表
	 * 
	 * @return Collection<String>
	 */
	public Collection<SubFlow> getSubFlowNodeList() {
		ArrayList<SubFlow> rtn = new ArrayList<SubFlow>();
		Collection<Element> elements = getAllElements();

		try {
			for (Iterator<Element> iterator = elements.iterator(); iterator.hasNext();) {
				Element elemnt = iterator.next();
				if (elemnt instanceof SubFlow) {
					rtn.add((SubFlow) elemnt);
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return rtn;
	}

	int getDiagramX(MouseEvent e) {
		return (int) (e.getX() * _zoomrate);
	}

	int getDiagramY(MouseEvent e) {
		return (int) (e.getY() * _zoomrate);
	}

	/**
	 * 获取指定结点上一结点列表
	 */
	/*
	 * public Collection getPreviousNodeList(String currid) { ArrayList rtn =
	 * new ArrayList(); if (currid!=null) { Element em =
	 * this.getaElementByID(currid); } return null; }
	 */

}
