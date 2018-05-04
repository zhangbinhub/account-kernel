//Source file: D:\\BILLFLOW\\src\\billflow\\Node.java

//Source file: E:\\billflow\\src\\billflow\\Node.java

package OLink.bpm.core.workflow.element;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

public abstract class Node extends PaintElement {

	// public String nodertid; //运行时节点id

	// public String actorid; //运行时角色id

	/**
	 * 
	 */
	private static final long serialVersionUID = 6517949879810615754L;

	public String prenodeid;

	public String statelabel;

	public String backnodeid;// 回退至节点id

	public String formname;// 节点对应表单名

	public String fieldpermlist;// 节点表单域权限对应，形如@a;#b;$c格式，代码含义详见PermissionType.java类

	public boolean isstartandnext;// 启动时是否送下一人

	public int x;

	public int y;

	public int width;

	public int m_width;

	public int m_height;

	public int height;

	public static final int WIDTH = 46;

	public static final int M_WIDTH = 50;

	public static final int HEIGHT = 55;

	public static final int M_HEIGHT = 50;

	protected Image _img;

	protected Rectangle _imgrect;

	protected Rectangle _txtrect;

	protected Rectangle _iconrect;

	public boolean _iscurrent;

	public Point _handlePoint = new Point(0, 0);

	private Shape _shape;

	/**
	 * private
	 * 
	 * @param owner
	 * @roseuid 3E0428DA0235
	 */
	public Node(FlowDiagram owner) {
		super(owner);
		this.width = WIDTH;
		this.m_width = M_WIDTH;
		this.m_height = M_HEIGHT;
		this.height = HEIGHT;
		resize();
	}

	public Shape getShape() {
		if (_shape == null) {
			Rectangle area = new Rectangle();
			if (_imgrect != null)
				area.add(_imgrect);
			if (_txtrect != null)
				area.add(_txtrect);
			if (_iconrect != null)
				area.add(_iconrect);

			_shape = area;
		}
		return _shape;
	}

	/**
	 * @param path
	 * @return java.awt.Image
	 * @roseuid 3E046AF60136
	 */
	protected Image getImage(String path) {
		Toolkit tk = Toolkit.getDefaultToolkit();
		Image img = tk.getImage(path);
		return img;
	}

	/**
	 * @param g
	 * @roseuid 3E046AF60245
	 */
	public abstract void paint(OGraphics g);

	/**
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @roseuid 3E0475DE000F
	 */
	protected void resize() {
		// try {
		if (_imgrect == null) {
			_imgrect = new Rectangle();
		}
		if (_txtrect == null) {
			_txtrect = new Rectangle();
		}
		if (_iconrect == null) {
			_iconrect = new Rectangle();
		}

		java.awt.FontMetrics fm = _owner.getFontMetrics(font);

		// this._imgrect.setBounds(x, y, width,
		// height-(fm.getHeight()+CLEARANCE));
		this._imgrect.setBounds(x, y, 45, 45);
		this._txtrect.setBounds(x, y + height - fm.getHeight(), width, fm
				.getHeight());
		this._iconrect.setBounds(x + width, y, 11, 12);
		// }
		// catch(Exception) {
		// }
	}

	protected void resizeForMobile() {
		if (_imgrect == null) {
			_imgrect = new Rectangle();
		}
		if (_txtrect == null) {
			_txtrect = new Rectangle();
		}
		if (_iconrect == null) {
			_iconrect = new Rectangle();
		}

		java.awt.FontMetrics fm = _owner.getFontMetrics(font);
		_imgrect.setBounds(x, y, 50, 50);
		_txtrect.setBounds(x, y + height - fm.getHeight(), width, fm
				.getHeight());
		_iconrect.setBounds(x + width, y, 25, 25);
	}

	/**
	 * @param x
	 * @param y
	 * @return boolean
	 * @roseuid 3E0A6E190080
	 */
	public boolean isSelected(int x, int y) {
		// Rectangle rc = new Rectangle();
		return _imgrect.contains(x, y) || _txtrect.contains(x, y);
	}

	/**
	 * @param x
	 * @param y
	 * @roseuid 3E0A6E19009E
	 */
	public void moveTo(int x, int y) {
		this.x = x - _handlePoint.x;
		this.y = y - _handlePoint.y;

		resize();
	}

	/**
	 * @return java.awt.Rectangle
	 * @roseuid 3E0A6E1900C6
	 */
	public Rectangle getRepaintRect() {
		Rectangle rct = new Rectangle(x - 20, y - 20, width + 20, height + 20);
		return rct;
	}

	public boolean removeSubElement(String id) {
		if (_subelems == null) {
			return false;
		}

		for (Enumeration<Element> e = _subelems.elements(); e.hasMoreElements();) {
			Element em = e.nextElement();

			if (em.id != null && em.id.equals(id)) {
				em.removeAllSubElement();
				_subelems.removeElement(em);
			}

		}

		return false;

	}

	public void removeAllSubElement() {
		this._subelems.removeAllElements();
	}

	/**
	 * @param e
	 * @roseuid 3E0A6F970129
	 */
	public void onMouseReleased(MouseEvent e) {

	}

	/**
	 * @param e
	 * @roseuid 3E0A6F9700E3
	 */
	public void onMousePressed(MouseEvent e) {

	}

	/**
	 * @param e
	 * @roseuid 3E0A6F970089
	 */
	public void onMouseMoved(MouseEvent e) {

	}

	/**
	 * @param e
	 * @roseuid 3E0A6F97002F
	 */
	public void onMouseDragged(MouseEvent e) {

	}

	/**
	 * @param e
	 * @roseuid 3E0A6F96039F
	 */
	public void onMouseClicked(MouseEvent e) {

	}

	protected void drawTips(Graphics g, String tips) {
		if (tips != null) {
			String[] tiplist = tips.split("\n");

			java.awt.FontMetrics fm = g.getFontMetrics(this.font);
			g.drawRect(x + width / 2, y + height, fm.getAscent() * 18, (fm
					.getHeight() + 5)
					* tiplist.length + 10);
			g.setColor(new java.awt.Color(0xFFFFE1));
			g.fillRect(x + width / 2 + 1, y + height + 1,
					fm.getAscent() * 18 - 1, (fm.getHeight() + 5)
							* tiplist.length + 10 - 1);
			g.setColor(java.awt.Color.black);

			for (int i = 0; i < tiplist.length; i++) {
				g.drawString(tiplist[i], x + width / 2 + 5, y + height
						+ (i + 1) * (fm.getHeight() + 5));
			}
		}
	}

	public abstract void showTips(Graphics g);

}
