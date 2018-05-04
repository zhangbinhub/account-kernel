package OLink.bpm.core.workflow.element;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

public abstract class PaintElement extends Element {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7430510801383466828L;

	public PaintElement(FlowDiagram owner) {
		super(owner);
	}

	public abstract void paintMobile(OGraphics g);

	/**
	 * @param g
	 * @roseuid 3E03EDD100DF
	 */
	public abstract void paint(OGraphics g);

	static final Color DEF_BGCOLOR = Color.white;
	static final Color DEF_COLOR = Color.black;
	static final Color DEF_SELECTEDCOLOR = Color.pink;
	static final Color DEF_CURREDITCOLOR = Color.orange;
	static final Color DEF_PASSEDCOLOR = Color.green;
	static final Font DEF_FONT = new Font("宋体", 0, 10);

	public int scale;
	public String name; // 元素的名称，可以是步骤或关系
	public String note; // 备注

	Color bgcolor = DEF_BGCOLOR; // 元素的背景色
	Color color = DEF_COLOR; // 元素使用的默认颜色
	Font font = DEF_FONT; // 元素使用的默认字体

	/**
	 * @param x
	 * @param y
	 * @return boolean
	 * @roseuid 3E0A6E170286
	 */
	public abstract boolean isSelected(int x, int y);

	/**
	 * @param x
	 * @param y
	 * @roseuid 3E0A6E1702A4
	 */
	public abstract void moveTo(int x, int y);

	/**
	 * @return java.awt.Rectangle
	 * @roseuid 3E0A6E1702C2
	 */
	public abstract Rectangle getRepaintRect();

	/**
	 * @param e
	 * @roseuid 3E0A6E5003A0
	 */
	public abstract void onMouseClicked(MouseEvent e);

	/**
	 * @param e
	 * @roseuid 3E0A6E73033C
	 */
	public abstract void onMouseDragged(MouseEvent e);

	/**
	 * @param e
	 * @roseuid 3E0A6ED700BF
	 */
	public abstract void onMouseMoved(MouseEvent e);

	/**
	 * @param e
	 * @roseuid 3E0A6EDD0244
	 */
	public abstract void onMousePressed(MouseEvent e);

	/**
	 * @param e
	 * @roseuid 3E0A6EE40140
	 */
	public abstract void onMouseReleased(MouseEvent e);

}
