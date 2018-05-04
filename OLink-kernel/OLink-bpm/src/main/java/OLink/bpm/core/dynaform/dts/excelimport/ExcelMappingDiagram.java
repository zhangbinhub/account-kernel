package OLink.bpm.core.dynaform.dts.excelimport;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import netscape.javascript.JSObject;

/**
 * @author nicholas
 */
public class ExcelMappingDiagram extends Canvas {

	private static final long serialVersionUID = 7116266842685042424L;

	private static HashMap<String, Object> IMG_RESOURCE = new HashMap<String, Object>(
			10);

	private int _statues; // 鼠标状态

	static final int ACTION_NORMAL = 0x00000000;

	static final int ACTION_REMOVE = 0x00000001;

	static final int ACTION_ADD_ACTOR = 0x00000010;

	static final int ACTION_ADD_NODE = 0x00000100;

	static final int ACTION_ADD_RELATION = 0x00001000;

	static final int ACTION_ADD_GROUP = 0x00010000;

	static final int ACTION_EDIT_ACTOR = 0x10000010;

	static final int ACTION_EDIT_NODE = 0x10000100;

	static final int ACTION_EDIT_RELATION = 0x10001000;

	static final int ACTION_EDIT_GROUP = 0x10010000;

	static final int ACTION_BREAK_LINE = 0x00100000;

	static final int PROCESSOR_TYPE_ACTOR = 1;

	static final int PROCESSOR_TYPE_PERSON = 2;

	static final int PROCESSOR_TYPE_GROUP = 3;

	static final int PROCESSOR_TYPE_TIMER = 4;

	private Vector<Element> _elems = new Vector<Element>();

	// private XMLOperate _xmlopt;

	private Element _selected;

	private Element _currToEdit;

	transient Image _tmpimg;

	// private ExcelMappingPanel _fpanel;

	public int flowstatus = FlowType.FLOWSTATUS_OPEN_NOSTART;

	public String flowpath = "";

	public String deleteMSG = null;

	private boolean _changed = false;

	// private String _startnodeid; // 用于在开始流程时设置currnodeid

	JSObject win = null;

	/**
	 * @roseuid 3E0428D90248
	 */
	public ExcelMappingDiagram() {
		// _tmpimg = this.createImage(this.size().width, this.size().height);
		if (this.getSize().width <= 0 || this.getSize().height <= 0) {
			this.setSize(1000, 600);
		}
		_tmpimg = new BufferedImage(this.getSize().width,
				this.getSize().height, BufferedImage.TYPE_INT_RGB);

		_statues = ACTION_NORMAL;

		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Image getImageResource(String filename) {
		Object obj = IMG_RESOURCE.get(filename);
		Image img = null;
		if (obj == null) {
			URL uri = null;
			uri = this.getClass().getResource(filename);
			try {
				obj = Toolkit.getDefaultToolkit().createImage(uri);
				IMG_RESOURCE.put(filename, obj);
			} catch (Exception e) {
				obj = getImageResource("unknow.gif");
			}
		}
		img = (Image) obj;
		return img;
	}

	public void setJSObject(JSObject win) {
		this.win = win;
	}

	/**
	 * @return the _statues
	 * @uml.property name="_statues"
	 */
	public int get_statues() {
		return this._statues;
	}

	// 编辑时用到的接口
	public Element getCurrToEdit() {
		return _currToEdit;
	}

	public boolean getChanged() {
		return this._changed;
	}

	public void editNode(Node grp) {
		this._selected = grp;
	}

	/**
	 * @param id
	 * @param name
	 * @param description
	 * @roseuid 3E0406A90239
	 */
	public void editRelation(Relation rlt) {
		this._selected = rlt;
	}

	public boolean isCurrentSelected(Element em) {
		return em != null && _selected != null && em.equals(_selected);
	}

	public boolean isCurrentToEdit(Element em) {
		return em != null && _currToEdit != null && em.equals(_currToEdit);
	}

	public boolean isContain(Vector<?> all, Node beforeNode) {
		if (all != null) {
			Enumeration<?> enm = all.elements();
			while (enm.hasMoreElements()) {
				Object item = enm.nextElement();
				if (item instanceof Node) {
					Node n = (Node) item;
					if (n.id.equals(beforeNode.id)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public void addRelation() {
		Relation rlt = new Relation(this);
		this.appendElement(rlt);
		editRelation(rlt);
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
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			break;
		case ACTION_REMOVE:
			this.setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
			break;
		case ACTION_ADD_ACTOR:
			this.setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
			break;
		case ACTION_ADD_NODE:
			this.setCursor(new Cursor(Cursor.MOVE_CURSOR));
			break;

		case ACTION_BREAK_LINE: // add by gusd
			this.setCursor(new Cursor(Cursor.SW_RESIZE_CURSOR));
			break; // end

		default:
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}

	/**
	 * @param id
	 * @roseuid 3E0A6E1A03DF
	 */
	private void delElement(String id) {

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
		update(g, true);
	}

	public void update(Graphics g, boolean fillWiteColor) {

		Graphics tg = _tmpimg.getGraphics();

		if (fillWiteColor) {
			tg.setColor(Color.WHITE);
			// tg.fillRect(0, 0, this.size().width, this.size().height);
			tg.fillRect(0, 0, this.getSize().width, this.getSize().height);
		} else {
			// tg.clearRect(0, 0, this.size().width, this.size().height);
			tg.clearRect(0, 0, this.getSize().width, this.getSize().height);
		}

		for (Enumeration<Element> e = _elems.elements(); e.hasMoreElements();) {
			Element em = e.nextElement();
			em.paint(tg);
		}
		g.drawImage(_tmpimg, 0, 0, this);
	}

	/**
	 * @param g
	 * @roseuid 3E0A6E1B0065
	 */
	public void paint(Graphics g) {
		update(g);
	}

	public String toXML() throws IllegalAccessException, ClassNotFoundException {
		StringBuffer rslt = new StringBuffer();
		try {

			Class<? extends ExcelMappingDiagram> cls = this.getClass();

			rslt.append("<" + cls.getName() + ">\n");

			Field[] flds = cls.getFields();

			for (int i = 0; i < flds.length; i++) {
				Field field = flds[i];
				String fieldval = "";
				if (field != null) {
					Class<?> type = field.getType();
					if (type != null)
						if (type.equals(Long.TYPE)) { // Long
							fieldval = field.getLong(this) + "";
							rslt.append("<" + flds[i].getName() + ">"
									+ fieldval + "</" + flds[i].getName()
									+ ">\n");
							continue;
						} else if (type.equals(Integer.TYPE)) { // Int
							fieldval = field.getInt(this) + "";
							rslt.append("<" + flds[i].getName() + ">"
									+ fieldval + "</" + flds[i].getName()
									+ ">\n");
							continue;
						} else if (type.equals(Short.TYPE)) { // Short
							fieldval = field.getShort(this) + "";
							rslt.append("<" + flds[i].getName() + ">"
									+ fieldval + "</" + flds[i].getName()
									+ ">\n");
							continue;
						} else if (type.equals(Double.TYPE)) { // Double
							fieldval = field.getDouble(this) + "";
							rslt.append("<" + flds[i].getName() + ">"
									+ fieldval + "</" + flds[i].getName()
									+ ">\n");
							continue;
						} else if (type.equals(Class
								.forName("java.lang.String"))) { // String
							fieldval = (String) field.get(this);
							rslt.append("<" + flds[i].getName() + ">"
									+ fieldval + "</" + flds[i].getName()
									+ ">\n");
							continue;
						}

						else if (type.equals(Float.TYPE)) { // Float
							fieldval = field.getFloat(this) + "";
							rslt.append("<" + flds[i].getName() + ">"
									+ fieldval + "</" + flds[i].getName()
									+ ">\n");
							continue;
						}

						else if (type.equals(Boolean.TYPE)) { // Boolean
							fieldval = field.getBoolean(this) + "";
							rslt.append("<" + flds[i].getName() + ">"
									+ fieldval + "</" + flds[i].getName()
									+ ">\n");
							continue;
						}

						else if (type.equals(Class.forName("java.sql.Date"))) { // Date
							fieldval = field.get(this) + "";
							rslt.append("<" + flds[i].getName() + ">"
									+ fieldval + "</" + flds[i].getName()
									+ ">\n");
							continue;
						}

						else if (type.equals(Class.forName("java.util.Date"))) { // java.util.Date
							fieldval = field.get(this) + "";
							rslt.append("<" + flds[i].getName() + ">"
									+ fieldval + "</" + flds[i].getName()
									+ ">\n");
							continue;
						}
				}
			}
			// rslt += ">\n";

			// 递归调用SUBELEMENT的TOXML方法

			for (Enumeration<Element> em = this._elems.elements(); em
					.hasMoreElements();) {
				Element subelm = em.nextElement();
				rslt.append(subelm.toXML());
			}

			rslt.append("</" + cls.getName() + ">\n");
		} catch (IllegalArgumentException e1) {
			throw e1;
		} catch (IllegalAccessException e2) {
			throw e2;
		} catch (ClassNotFoundException e) {
			throw e;
		}

		return rslt.toString();

	}

	/**
	 * @param e
	 * @roseuid 3E0A6E1B0097
	 */
	void removeElement(Element emn) {
		if (emn != null) {
			if (emn instanceof Node) {
				Vector<Element> v = getAllElements();
				for (Enumeration<Element> e = v.elements(); e.hasMoreElements();) {
					Element elem = e.nextElement();
					if (elem instanceof Relation) {
						Relation r = (Relation) elem;
						if ((r.startnodeid != null && r.startnodeid
								.equals(emn.id))
								|| (r.endnodeid != null && r.endnodeid
										.equals(emn.id))) {
							// Node n = r.getStartnode();
							_elems.removeElement(r);
						}
					}
				}
				_elems.removeElement(emn);
			} else {
				Relation r = (Relation) emn;
				_elems.removeElement(r);
			}
		}
	}

	/**
	 * @param id
	 * @roseuid 3E0A6E1B00AB
	 */
	void removeElement(String id) {
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
	 * @return excelimport.Element
	 * @roseuid 3E0A6E1B00C9
	 */
	public Element chkSelectedElement(int x, int y) {
		Vector<Element> v = getAllElements();
		for (Enumeration<Element> e = v.elements(); e.hasMoreElements();) {
			Element em = e.nextElement();
			if (em.isSelected(x, y)) {
				return em;
			}
		}
		return null;
	}

	/**
	 * @return the flowstatus
	 * @uml.property name="flowstatus"
	 */
	public int getFlowstatus() {
		return this.flowstatus;
	}

	/**
	 * 设置流程运转路径
	 * 
	 * @param
	 * @uml.property name="flowpath"
	 */
	public void setFlowpath(String path) {
		if (this.flowpath == null || this.flowpath.trim().length() <= 0) {
			this.flowpath = path;
		} else {
			this.flowpath = this.flowpath + ";" + path;
		}
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
				for (Enumeration<Element> sube = em._subelems.elements(); sube
						.hasMoreElements();) {
					vct.addElement(sube.nextElement());
				}
			}

		}
		return vct;
	}

	/**
	 * @param id
	 * @return excelimport.Element
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
			if (em._subelems != null) {
				for (Enumeration<Element> sube = em._subelems.elements(); sube
						.hasMoreElements();) {
					Element subem = sube.nextElement();

					if (subem.id != null && subem.id.equals(id)) {
						return subem;
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
		int count = e.getClickCount();
		if (count >= 2) {
			Element em = this.chkSelectedElement(e.getX(), e.getY());
			if (em != null) {
				win.eval("javascript:editElement()");
			}
		}
	}

	// protected void processMouseEvent(MouseEvent e) {
	// if (e.getID() == MouseEvent.MOUSE_CLICKED) {
	// }
	// super.processMouseEvent(e);
	// }

	//
	// public boolean isSelectElement(MouseEvent e){
	// Element em = this.chkSelectedElement(e.getX(), e.getY());
	// if ((em != null) && !(em instanceof Relation)) {
	// return true;
	// }else{
	// return false;
	// }
	// }

	/**
	 * @param e
	 * @roseuid 3E0A6E1B0137
	 */
	void this_mousePressed(MouseEvent e) {

		int x = e.getX();
		int y = e.getY();

		boolean isChangeCursor = false;

		Element em = this.chkSelectedElement(x, y);
		if (em != null) {

			if (em instanceof Node) {
				((Node) em).holdPoint.x = x - ((Node) em).x;
				((Node) em).holdPoint.y = y - ((Node) em).y;
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
					// Node nd = (Node) em;
					for (Enumeration<Element> el = this.getAllElements()
							.elements(); deleteMSG == null
							&& el.hasMoreElements();) {
						Element elem = el.nextElement();
						if (elem instanceof Relation) {
							Relation rl = (Relation) elem;
							if (((rl.startnodeid != null && rl.startnodeid
									.equals(em.id)) || (rl.endnodeid != null && rl.endnodeid
									.equals(em.id)))
									&& rl.ispassed) {
								deleteMSG = "相关流程已处理,不能删除!";
							}
						}
					}
				}
				if (deleteMSG == null) {
					this.removeElement(em);
				} else { // 提示不能删除的原因
					ErrorDialog errordialog = new ErrorDialog(new Frame(),
							"操作错误!", true);
					Label lab = new Label(deleteMSG);
					errordialog.setLayout(new BorderLayout());
					errordialog.add(lab, BorderLayout.CENTER);
					errordialog.setBounds(300, 300, 250, 70);
					// errordialog.show();
					errordialog.setVisible(true);
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
					// int pos = relation.checkWhichpoint(new Point(x, y)); //
					// 检查按下点是否恰好在该流程线的原有折点上，如果是，返回该折点的位置
					relation.setBreakpoint(new Point(x, y));
					relation.setCurrentselect(true);
					isChangeCursor = true;

				} else {
					this._selected = em;
					_selected.moveTo(x, y);
				}
			}
			repaint();
		} else {
			if ((_statues == ExcelMappingDiagram.ACTION_ADD_RELATION)
					&& (_selected instanceof Relation)) {
				Relation r = (Relation) _selected;
				if (r.getStartnode() == null) {
					removeElement(r);
					r = null;
					_selected = null;
				}
			}
		}
		if (!isChangeCursor) { // isChangeCursor为true时，用户准备拖拉流程线，不释放鼠标指针的拖拉样式
			this.changeStatues(ExcelMappingDiagram.ACTION_NORMAL);
			isChangeCursor = false;
		}

	}

	/**
	 * @param e
	 * @roseuid 3E0A6E1B0155
	 */
	void this_mouseReleased(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		Element em = this.chkSelectedElement(x, y);

		if (_selected instanceof Relation && (em instanceof Node || em == null)) {
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
				if (em == null || r.getStartnode().id.equals(r.getEndnode().id)) {
					// r.getStartnode().removeSubElement(r.id);
					this.removeElement(r);
				}
				// } else {
				// r.getStartnode().removeSubElement(r.id);
				// }
			} else {
				int pos = r.getChangevector();
				r.setCurrentselect(false);
				if (pos == -1) { // 原先按下的点不是原有折点
					boolean bool = r.checkDistance(new Point(x, y));
					if (!bool) { // 检查鼠标释放的点拖拉的距离是否小于一个常量，如果是，则当作没有拖拉
						r.addVector(new Point(x, y)); // 如果鼠标释放的点拖拉的距离大于一个常量，则当作一个新的折点
					}
				} else { // 原先按下的点为原有折点，鼠标释放后要改变原有折点的坐标
					r.changeVector(new Point(x, y));
					r.setChangevector(-1);

				}
			}

		}
		// 设置当前选中
		if (_selected != null && em != null && _selected.equals(em)) {
			_currToEdit = em;
		}
		this.changeStatues(ExcelMappingDiagram.ACTION_NORMAL);
		_selected = null;

		this.repaint();
	}

	/**
	 * @param e
	 * @roseuid 3E0A6E1B0169
	 */
	void this_mouseMoved(MouseEvent e) {

		if ((this._statues == ACTION_ADD_NODE)
				|| (this._statues == ACTION_ADD_ACTOR)) {
			this.this_mouseDragged(e);
		} else if (this._statues == ACTION_BREAK_LINE) {
		}

		else {
			// int x;
			// int y;
			Graphics tg = this.getGraphics();
			tg.setColor(Color.black);
			Element em = this.chkSelectedElement(e.getX(), e.getY());
			if ((em != null) && !(em instanceof Relation)) {
				this.changeStatues(this._statues);
				// if(this._statues == ACTION_REMOVE){

				// }
				Node nd = (Node) em;
				// java.awt.FontMetrics fm = this.getFontMetrics(nd.font);
				if (em.isSelected(e.getX(), e.getY())) {
					/*
					 * String snote = ""; if (nd.description == null ||
					 * nd.description.equals("null")) { snote = ""; } else if
					 * (nd.description.getBytes().length <= 18) { snote =
					 * nd.description; } else { snote =
					 * nd.description.substring(0, 9) + "..."; }
					 */
					nd.showTips(tg); // 显示注释
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

		int x = e.getX();
		int y = e.getY();
		if (this._selected != null && this._statues != ACTION_BREAK_LINE
				&& this._selected instanceof Relation) { // 从一个结点到另一个结点画流程的拖拉过程中
			Relation r = (Relation) this._selected;
			if (r.getEndnode() == null) {
				_selected.moveTo(x, y);
				this.repaint();
			}
		} else if (this._selected != null && this._statues != ACTION_BREAK_LINE) {

			_selected.moveTo(x, y);

			this.repaint();

		} else if (this._selected != null && this._selected instanceof Relation
				&& this._statues == ACTION_BREAK_LINE) { // 拖拉流程线产生折点的过程
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
		_tmpimg = this.createImage(this.getSize().width, this.getSize().height);
	}

	void this_mouseEntered(MouseEvent e) {
	}

	void appendElement(Element e) {
		_elems.addElement(e);
	}

	public static void main(String[] args) {

	}

	public MasterSheet getMasterSheet() {
		Iterator<Element> iter = this._elems.iterator();
		while (iter.hasNext()) {
			Element element = iter.next();
			if (element instanceof MasterSheet) {
				return (MasterSheet) element;
			}
		}
		return null;
	}

	public Collection<Element> getDetailSheets() {

		Vector<Element> rtn = new Vector<Element>();
		Iterator<Element> iter = this._elems.iterator();
		while (iter.hasNext()) {
			Element element = iter.next();
			if (element instanceof DetailSheet) {
				rtn.addElement(element);
			}
		}
		return rtn;
	}

	public Collection<LinkageKey> getLinkageKeys() {
		Vector<LinkageKey> rtn = new Vector<LinkageKey>();
		Iterator<Element> iter = this._elems.iterator();
		while (iter.hasNext()) {
			Element element = iter.next();
			if (element instanceof Relation) {
				LinkageKey lk = ((Relation) element).getLinkageKey();
				if (lk != null) {
					rtn.addElement(lk);
				}
			}
		}
		return rtn;

	}

	public LinkageKey getLinkageKeyByMasterKey(Column masterKeyColumn) {
		Collection<LinkageKey> linkageKeys = getLinkageKeys();
		Iterator<LinkageKey> iter = linkageKeys.iterator();
		while (iter.hasNext()) {
			LinkageKey key = iter.next();
			Column msk = key.getMasterSheetKeyColumn();
			if (msk.name != null && msk.name.equals(masterKeyColumn.name)) {
				return key;
			}
		}
		return null;

	}

	public LinkageKey getLinkageKeyByDetailKey(Column detailKeyColumn) {
		Collection<LinkageKey> linkageKeys = getLinkageKeys();
		Iterator<LinkageKey> iter = linkageKeys.iterator();
		while (iter.hasNext()) {
			LinkageKey key = iter.next();
			Column msk = key.getDetailSheetKeyColumn();
			if (msk.name != null && msk.name.equals(detailKeyColumn.name)) {
				return key;
			}
		}
		return null;

	}

}
