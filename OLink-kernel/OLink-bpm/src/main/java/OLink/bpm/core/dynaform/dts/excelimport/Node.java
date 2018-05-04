//Source file: D:\\excelimport\\src\\excelimport\\Node.java

//Source file: E:\\excelimport\\src\\excelimport\\Node.java

package OLink.bpm.core.dynaform.dts.excelimport;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Vector;

import OLink.bpm.core.dynaform.dts.excelimport.utility.CommonUtil;

public abstract class Node extends Element {
	
	private static final long serialVersionUID = 4778415341123920061L;

	public int x;

	public int y;
	
	public Point holdPoint = new Point();

	protected Vector<?> _rltn;

	protected Image _img;


	/**
	 * private
	 * 
	 * @param owner
	 * @roseuid 3E0428DA0235
	 */
	public Node(ExcelMappingDiagram owner) {
		super(owner);
		resize();
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
	public abstract void paint(Graphics g); 
	/**
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @roseuid 3E0475DE000F
	 */
	public abstract void resize();
	/**
	 * @param x
	 * @param y
	 * @return boolean
	 * @roseuid 3E0A6E190080
	 */
	public boolean isSelected(int x, int y) {
		return getRect().contains(x, y);
	}
	
	public abstract Rectangle getRect();

	/**
	 * @param x
	 * @param y
	 * @roseuid 3E0A6E19009E
	 */
	public void moveTo(int x, int y) {
//		this.x = (int) (x - getRect().width / 2);
//		this.y = (int) (y - getRect().height / 2);

		
		this.x = x - holdPoint.x;
		this.y = y - holdPoint.y;
		resize();
	}

	/**
	 * @return java.awt.Rectangle
	 * @roseuid 3E0A6E1900C6
	 */
	public Rectangle getRepaintRect() {
		Rectangle rct = new Rectangle(x - 20, y - 20, getRect().width + 20, getRect().height + 20);
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
		String[] tiplist = CommonUtil.split(tips, '\n');

		java.awt.FontMetrics fm = g.getFontMetrics(this.font);
		g.drawRect(x + getRect().width / 2, y + getRect().height, fm.getAscent() * 18, (fm
				.getHeight() + 5)
				* tiplist.length + 10);
		g.setColor(new java.awt.Color(0xFFFFE1));
		g.fillRect(x + getRect().width / 2 + 1, y + getRect().height + 1, fm.getAscent() * 18 - 1,
				(fm.getHeight() + 5) * tiplist.length + 10 - 1);
		g.setColor(java.awt.Color.black);

		for (int i = 0; i < tiplist.length; i++) {
			g.drawString(tiplist[i], x + getRect().width / 2 + 5, y + getRect().height + (i + 1)
					* (fm.getHeight() + 5));
		}
	}

	public abstract void showTips(Graphics g);

}
