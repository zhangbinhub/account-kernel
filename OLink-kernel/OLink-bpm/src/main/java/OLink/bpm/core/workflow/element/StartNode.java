package OLink.bpm.core.workflow.element;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Enumeration;

public class StartNode extends Node {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6125046863851429534L;

	public StartNode(FlowDiagram owner) {
		super(owner);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		 * FlowDiagram fd = new FlowDiagram(); StartNode g = new StartNode(fd);
		 * // g.namelist = //
		 * "P1000|赛百威公司/开发部/周志军;A1000|赛百威公司/开发部/部门经理;A1000|赛百威公司/开发部/部门经理;A1000|赛百威公司/开发部/部门经理;A1000|赛百威公司/开发部/部门经理;A1000|赛百威公司/开发部/部门经理"
		 * ; //
		 * /开发部/周志军;A1000|赛百威公司/开发部/部门经理;A1000|赛百威公司/开发部/部门经理;A1000|赛百威公司/开发部
		 * /部门经理;A1000|赛百威公司/开发部/部门经理;A1000|赛百威公司/开发部/部门经理";
		 */
	}

	public void showTips(Graphics g) {

		StringBuffer tips = new StringBuffer();

		tips.append(this.name);

		drawTips(g, tips.toString());

	}

	public void paint(OGraphics g) {
		if (_img == null) {
			_img = _owner.getImageResource("start.gif");
		}

		// Call All Sub Elements PAINT METHOD.
		// 保存当前背景颜色...
		Color old = this.bgcolor;
		if (_owner.isCurrentToEdit(this)) {
			bgcolor = DEF_CURREDITCOLOR;
		}

		if (_owner.isCurrentSelected(this)) {
			bgcolor = DEF_SELECTEDCOLOR;
		}

		for (Enumeration<Element> e = _subelems.elements(); e.hasMoreElements();) {
			Object te = e.nextElement();
			if (te instanceof PaintElement) {
				PaintElement se = (PaintElement) te;
				se.paint(g);
			}
		}

		// Fill background
		this.width = WIDTH;
		this.m_width = M_WIDTH;
		this.m_height = M_HEIGHT;
		this.height = HEIGHT;
		resize();
		g.setColor(bgcolor);

		g.fillRect(this.x, this.y, this.width, this.height);

		// Draw Image
		g.drawImage(_img, _imgrect.x, _imgrect.y, _imgrect.width,
				_imgrect.height, null, this._owner);

		if (this.name != null) {
			java.awt.FontMetrics fm = _owner.getFontMetrics(font);
			int tx = _txtrect.x + (_txtrect.width - fm.stringWidth(name)) / 2;
			int ty = _txtrect.y + 2 * _txtrect.height;
			if (this._iscurrent) {
				g.drawImage(_owner.getImageResource("current.gif"), _txtrect.x,
						_txtrect.y, _txtrect.width + 30, 10 + _txtrect.height,
						null, this._owner);
			} else {
				g.drawImage(_owner.getImageResource("background.gif"),
						_txtrect.x, _txtrect.y, _txtrect.width + 30,
						10 + _txtrect.height, null, this._owner);

			}
			g.setColor(Color.black);
			g.drawString(name, tx + 13 + this.name.length(), ty - 10);
		}

		// // 标记起点
		// java.awt.FontMetrics fm = _owner.getFontMetrics(font);
		// g.drawImage(_owner.getImageResource("start.gif"), _iconrect.x,
		// _iconrect.y, 10, 10, null, this._owner);

		// 恢复当前背景颜色
		this.bgcolor = old;
	}

	public void paintMobile(OGraphics g) {
		_img = _owner.getImageResource("start_m.gif");

		// Call All Sub Elements PAINT METHOD.
		// 保存当前背景颜色...
		Color old = this.bgcolor;
		if (_owner.isCurrentToEdit(this)) {
			bgcolor = DEF_CURREDITCOLOR;
		}

		if (_owner.isCurrentSelected(this)) {
			bgcolor = DEF_SELECTEDCOLOR;
		}

		for (Enumeration<Element> e = _subelems.elements(); e.hasMoreElements();) {
			Object te = e.nextElement();
			if (te instanceof PaintElement) {
				PaintElement se = (PaintElement) te;
				se.paintMobile(g);
			}
		}

		// Fill background
		this.width = WIDTH;
		this.m_width = M_WIDTH;
		this.m_height = M_HEIGHT;
		this.height = HEIGHT;
		resizeForMobile();
		g.setColor(bgcolor);
		g.fillRect(this.x, this.y, this.width, this.height);

		// Draw Image

		if (_iscurrent) {
			_img = _owner.getImageResource("current_m.gif");
		}
		g.drawImage(_img, _imgrect.x, _imgrect.y, _imgrect.width,
				_imgrect.height, null, this._owner);

		if (this.name != null) {
			g.setColor(Color.black);
			g.drawString(name, _txtrect.x + name.length(), _txtrect.y + 30);

		}

		// 恢复当前背景颜色
		this.bgcolor = old;
	}

}
