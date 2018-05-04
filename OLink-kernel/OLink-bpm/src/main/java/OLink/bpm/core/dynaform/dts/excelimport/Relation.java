package OLink.bpm.core.dynaform.dts.excelimport;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.Vector;

/**
 * @author nicholas
 */
public class Relation extends Element {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3513388607755110685L;

	// System
	public String state;

	public String startnodeid;

	public String endnodeid;

	public boolean ispassed;

	public boolean isreturn; // 是否是返回路径

	public String condition = null;

	public String pointstack;

	static final int ARROW_LONG = 20;

	static final int ARROW_WIDTH = 4;

	static final double PAI = 3.1415926525;

	private Point _startpoint;

	private Point _endpoint;

	private Point _mousepoint;

	private Point _movepoint = null; // 拖拉时鼠标的移动点

	private Point breakpoint = null; // 按下鼠标时的点

	protected Rectangle _txtrect;

	private Vector<Object> vector = null;

	private int changevector = -1; // 当鼠标按下时恰好是流程线原有折点时的折点位置

	private boolean currentselect = false;

	private boolean initstart = false; // 由于该流程图用xml文件保存，所以当装载流程图时要构造保存流程线的折点位置的vector

	/**
	 * @param owner
	 * @roseuid 3E0428DB027D
	 */
	public Relation(ExcelMappingDiagram owner) {
		super(owner);
		vector = new Vector<Object>();
		this.initstart = true;

	}

	public void paint(Graphics g) {
		boolean dashed = true;

		Color old = this.color; // 保存当前颜色

		Point sp = this.getStartPoint();
		Point ep = this.getEndPoint();
		if (this.initstart) { // 如果流程图是第一次画，则要从原有xml文件中读取折点的坐标
			this.initVector(this.pointstack); // pointstack是与xml文件打交道的用于存储流程线的折点坐标的public
			// String型变量
		}
		this.initstart = false;
		if (sp != null && ep != null) {
			if (this.vector.size() >= 3) {
				while (true) { // 把相邻的两个距离小于10的折点合并为一个点
					if (this.vector.size() >= 3) {
						int d = -1;
						int m = 0;
						int size = this.vector.size() - 1;
						for (m = 0; m < size; m++) {
							Point obj1 = (Point) this.vector.elementAt(m);
							Point obj2 = (Point) this.vector.elementAt(m + 1);
							if (m == 0) {
								obj1 = this.getStartPoint();
							}
							if (m == this.vector.size() - 2) {
								obj2 = this.getEndPoint();
							}

							d = this.getDistance(obj1, obj2);
							if (d <= 10) { // 若两点相邻且距离小于10，则删去其中一个点
								if (m == this.vector.size() - 2) {
									this.vector.removeElementAt(m);
								} else {
									this.vector.removeElementAt(m + 1);
								}
								break;
							}
						}
						if (m == size) {
							break;
						}
					} else {
						break;
					}
				}
				while (true) { // 把相邻的两条夹角小于5度的直线合并为一条直线
					if (this.vector.size() >= 3) {
						boolean remove = false;
						int n = 0;
						int size = this.vector.size() - 2;
						for (n = 0; n < size; n++) {
							Point obj1 = (Point) this.vector.elementAt(n);
							Point obj2 = (Point) this.vector.elementAt(n + 1);
							Point obj3 = (Point) this.vector.elementAt(n + 2);
							if (n == 0) {
								obj1 = this.getStartPoint();
							}
							if (n == this.vector.size() - 3) {
								obj3 = this.getEndPoint();
							}

							remove = this.lineTolineAngle(obj1, obj2, obj3); // 判断两线夹角是否小于5度
							if (remove) {
								this.vector.removeElementAt(n + 1);
								break;
							}
						}
						if (n == size) {
							break;
						}

					} else {
						break;
					}
				}
			}
			int x1 = 0, y1 = 0, x2 = 0, y2 = 0, d2 = 0, h2 = 0;
			int mx = 0, my = 0;
			/*
			 * i = 0, 
			int hx = 0, hy = 0, ex1 = 0, ey1 = 0, ex2 = 0, ey2 = 0;
			double k1 = 0, k2 = 0;

			double sina = 0, cosa = 0;
			double sinb = 0, cosb = 0;

			double tx = 0, ty = 0;
			boolean moveline = false;
			*/
			x1 = ep.x;
			y1 = ep.y;

			x2 = sp.x;
			y2 = sp.y;
			if (this.ispassed) {
				g.setColor(Color.green);
			} else if (this.currentselect) {
				this.color = DEF_SELECTEDCOLOR;
				g.setColor(this.color);
				this.currentselect = false;
			} else if (isCurrentToEdit()) {
				this.color = DEF_CURREDITCOLOR;
				g.setColor(this.color);
			} else {
				this.color = DEF_COLOR;
				g.setColor(this.color);
			}
			if (this.vector.size() < 2) {
				d2 = 0;
				h2 = 0;
			} else {

				Node node = this.getEndnode();

				d2 = node.getRect().width;
				h2 = node.getRect().height;
			}
			Point arrowhead = null;
			arrowhead = this.getArrowhead(new Point(x2, y2), new Point(x1, y1),
					d2, h2); // 得到流程线箭头的坐标
			if (this.vector.size() < 3) { // 鼠标从开始结点拖拉到结尾结点的过程中，鼠标当前移动点作为暂时的尾结点
				if (this._movepoint != null) {
					drawLine(g, x2, y2, this._movepoint.x, this._movepoint.y,
							dashed);
					x2 = this._movepoint.x;
					y2 = this._movepoint.y;
					arrowhead = this.getArrowhead(new Point(x2, y2), new Point(
							x1, y1), d2, h2);
					drawLine(g, this._movepoint.x, this._movepoint.y,
							arrowhead.x, arrowhead.y, dashed);
					this._movepoint = null;
				} else {
					drawLine(g, x2, y2, arrowhead.x, arrowhead.y, dashed);
				}
			} else {
				if (this._movepoint != null) { // 画流程线折点时拖拉鼠标的情况
					int whichLine = this.getWhichLine(this.getBreakpoint());
					for (int j = 0; j < this.vector.size() - 1; j++) {
						Point obj1 = (Point) this.vector.elementAt(j);
						Point obj2 = (Point) this.vector.elementAt(j + 1);
						x2 = obj1.x;
						y2 = obj1.y;
						if (j == 0) {
							obj1 = this.getStartPoint();
						}
						if (j == this.vector.size() - 2) {
							arrowhead = this.getArrowhead(new Point(x2, y2),
									new Point(x1, y1), d2, h2);
							obj2 = arrowhead;
						}
						if (j == whichLine) {
							g.drawLine(obj1.x, obj1.y, this._movepoint.x,
									this._movepoint.y);
							x2 = this._movepoint.x;
							y2 = this._movepoint.y;
							if (j == this.vector.size() - 2) {
								arrowhead = this.getArrowhead(
										new Point(x2, y2), new Point(x1, y1),
										d2, h2);
								obj2 = arrowhead;
							}
							drawLine(g, this._movepoint.x, this._movepoint.y,
									obj2.x, obj2.y, dashed);
						} else {
							g.drawLine(obj1.x, obj1.y, obj2.x, obj2.y);
						}
					}
					this._movepoint = null;
				} else { // 鼠标释放点设为新折点
					for (int k = 0; k < this.vector.size() - 1; k++) {
						Point obj3 = (Point) this.vector.elementAt(k);
						Point obj4 = (Point) this.vector.elementAt(k + 1);
						x2 = obj3.x;
						y2 = obj3.y;
						if (k == 0) {
							obj3 = this.getStartPoint();
						}
						if (k == this.vector.size() - 2) {
							arrowhead = this.getArrowhead(new Point(x2, y2),
									new Point(x1, y1), d2, h2);
							obj4 = arrowhead;
						}
						drawLine(g, obj3.x, obj3.y, obj4.x, obj4.y, dashed);

					}
				}
			}
			mx = (x2 + x1) / 2;
			my = (y2 + y1) / 2;
			arrowhead = this.getArrowhead(new Point(x2, y2), new Point(x1, y1),
					d2, h2);
//			sina = Math.abs((double) Math.sqrt((y2 - y1) * (y2 - y1))
//					/ Math
//							.sqrt(((x2 - x1) * (x2 - x1) + (y2 - y1)
//									* (y2 - y1))));
//			cosa = Math.abs((double) Math.sqrt((x2 - x1) * (x2 - x1))
//					/ Math
//							.sqrt(((x2 - x1) * (x2 - x1) + (y2 - y1)
//									* (y2 - y1))));
			// 第一象限
			/*
			 * Point arrowstart1 = new Point(); Point arrowstart2 = new Point();
			 * if (x2 < arrowhead.x && y2 < arrowhead.y) {
			 * //求箭头线的开始点，有两个，分别在流程线的两侧 arrowstart1.x = arrowhead.x + (int)
			 * (ARROW_WIDTH * sina - ARROW_LONG * cosa); arrowstart1.y =
			 * arrowhead.y - (int) (ARROW_LONG * sina + ARROW_WIDTH * cosa);
			 * arrowstart2.x = arrowhead.x - (int) (ARROW_LONG * cosa +
			 * ARROW_WIDTH * sina); arrowstart2.y = arrowhead.y + (int)
			 * (ARROW_WIDTH * cosa - ARROW_LONG * sina); } else if (x2 ==
			 * arrowhead.x && y2 < arrowhead.y) { arrowstart1.x = arrowhead.x +
			 * ARROW_WIDTH; arrowstart1.y = arrowhead.y; arrowstart2.x =
			 * arrowhead.x - ARROW_WIDTH; arrowstart2.y = arrowhead.y; } else if
			 * (x2 > arrowhead.x && y2 < arrowhead.y) { arrowstart1.x =
			 * arrowhead.x + (int) (ARROW_LONG * cosa + ARROW_WIDTH * sina);
			 * arrowstart1.y = arrowhead.y + (int) (ARROW_WIDTH * cosa -
			 * ARROW_LONG * sina); arrowstart2.x = arrowhead.x + (int)
			 * (ARROW_LONG * cosa - ARROW_WIDTH * sina); arrowstart2.y =
			 * arrowhead.y - (int) (ARROW_LONG * sina + ARROW_WIDTH * cosa); }
			 * else if (x2 > arrowhead.x && y2 == arrowhead.y) { arrowstart1.x =
			 * arrowhead.x + ARROW_LONG; arrowstart1.y = arrowhead.y +
			 * ARROW_WIDTH; arrowstart2.x = arrowhead.x + ARROW_LONG;
			 * arrowstart2.y = arrowhead.y - ARROW_WIDTH; } else if (x2 >
			 * arrowhead.x && y2 > arrowhead.y) { arrowstart1.x = arrowhead.x +
			 * (int) (ARROW_LONG * cosa - ARROW_WIDTH * sina); arrowstart1.y =
			 * arrowhead.y + (int) (ARROW_LONG * sina + ARROW_WIDTH * cosa);
			 * arrowstart2.x = arrowhead.x + (int) (ARROW_LONG * cosa +
			 * ARROW_WIDTH * sina); arrowstart2.y = arrowhead.y + (int)
			 * (ARROW_LONG * sina - ARROW_WIDTH * cosa); } else if (x2 ==
			 * arrowhead.x && y2 > arrowhead.y) { arrowstart1.x = arrowhead.x -
			 * ARROW_WIDTH; arrowstart1.y = arrowhead.y + ARROW_LONG;
			 * arrowstart2.x = arrowhead.x + ARROW_WIDTH; arrowstart2.y =
			 * arrowhead.y + ARROW_LONG; } else if (x2 < arrowhead.x && y2 >
			 * arrowhead.y) { arrowstart1.x = arrowhead.x - (int) (ARROW_LONG *
			 * cosa + ARROW_WIDTH * sina); arrowstart1.y = arrowhead.y + (int)
			 * (ARROW_LONG * sina - ARROW_WIDTH * cosa); arrowstart2.x =
			 * arrowhead.x + (int) (ARROW_WIDTH * sina - ARROW_LONG * cosa);
			 * arrowstart2.y = arrowhead.y + (int) (ARROW_LONG * sina +
			 * ARROW_WIDTH * cosa); } else { arrowstart1.x = arrowhead.x -
			 * ARROW_LONG; arrowstart1.y = arrowhead.y - ARROW_WIDTH;
			 * arrowstart2.x = arrowhead.x - ARROW_LONG; arrowstart2.y =
			 * arrowhead.y + ARROW_WIDTH; } hx = arrowhead.x;
			 * 
			 * hy = arrowhead.y; g.setColor(Color.red);
			 * g.drawLine(arrowstart1.x, arrowstart1.y, hx, hy);
			 * g.drawLine(arrowstart2.x, arrowstart2.y, hx, hy);
			 */
			g.setColor(Color.black);
			if (this.name != null) {
				java.awt.FontMetrics fm = _owner.getFontMetrics(font);

				int rx = mx - 10;
				int ry = my + fm.getHeight(); // 比线段中间位低

				g.setColor(Color.blue);
				g.drawString(name, rx, ry);

				g.setColor(Color.black);
			}

		}
		this.color = old; // 恢复当前颜色
		this.setPointStack(this.vector); // 把vector里的折点坐标存储在pointstack里，跟xml文件打交道
	}

	/**
	 * Access method for the Startnode property.
	 * 
	 * @return the current value of the Startnode property
	 * @roseuid 3E0A6E1B0318
	 * @uml.property name="changevector"
	 */
	public int getChangevector() {
		return this.changevector;
	}

	/**
	 * @param changevector
	 *            the changevector to set
	 * @uml.property name="changevector"
	 */
	public void setChangevector(int change) {
		this.changevector = change;
	}

	public void setPointStack(Vector<Object> vector) {
		StringBuffer strTemp = new StringBuffer();
		if (this.vector != null) {
			for (int i = 0; i < this.vector.size(); i++) {
				Point point = (Point) this.vector.elementAt(i);
				strTemp.append(point.x + ";" + point.y);
				if (i < this.vector.size() - 1) {
					strTemp.append(";"); // 例如： "123；234；456；444"形式
				}

			}
		}
		this.pointstack = strTemp.toString();
	}

	public void initVector(String pointstack) { // 把pointstack里的x,y坐标转换成vector里的Point对象
		String str = "";
		str = pointstack;
		//int length = 0;
		if (str == null || str.equalsIgnoreCase("")) {

		} else {
			int x = 0;
			int y = 0;
			int position = 0;
			//length = str.length();
			String strTemp = "";
			while (true) {
				try {
					position = str.indexOf(";");
					if (position <= 0) {
						break;
					}
					strTemp = str.substring(0, position);
					x = Integer.parseInt(strTemp);
					str = str.substring(position + 1, str.length());
					position = str.indexOf(";");
					if (position <= 0) {
						strTemp = str;
						y = Integer.parseInt(strTemp);
						this.vector.addElement(new Point(x, y));
						break;
					}
					strTemp = str.substring(0, position);
					y = Integer.parseInt(strTemp);
					this.vector.addElement(new Point(x, y));
					str = str.substring(position + 1, str.length());
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		}

	}

	/**
	 * @param currentselect
	 *            the currentselect to set
	 * @uml.property name="currentselect"
	 */
	public void setCurrentselect(boolean curSelect) {
		this.currentselect = curSelect;
	}

	/**
	 * @return the currentselect
	 * @uml.property name="currentselect"
	 */
	public boolean getCurrentselect() {
		return this.currentselect;
	}

	/**
	 * @return the breakpoint
	 * @uml.property name="breakpoint"
	 */
	public Point getBreakpoint() {
		return this.breakpoint;
	}

	/**
	 * @param breakpoint
	 *            the breakpoint to set
	 * @uml.property name="breakpoint"
	 */
	public void setBreakpoint(Point point) {
		this.breakpoint = point;
	}

	public Point getArrowhead(Point p1, Point p2, int d2, int h2) { // 得到箭头坐标
		double k = Math.abs((double) (p2.y - p1.y) / (p2.x - p1.x));
		double k2 = (double) h2 / d2;
		Point arrowhead = new Point();
		if (p2.y > p1.y && p2.x > p1.x) {
			if (k2 >= k) {
				arrowhead.x = p2.x - (int) (d2 / (double)2);
				arrowhead.y = p2.y - (int) (k * d2 / (double)2);
			} else {
				arrowhead.x = p2.x - (int) (h2 / (double)2 / k);
				arrowhead.y = p2.y - (int) (h2 / (double)2);

			}
		} else if (p2.y == p1.y && p2.x > p1.x) {
			arrowhead.x = p2.x - (int) (d2 / (double)2);
			arrowhead.y = p2.y;
		} else if (p2.y < p1.y && p2.x > p1.x) {
			if (k2 >= k) {
				arrowhead.x = p2.x - (int) (h2 / (double)2);
				arrowhead.y = p2.y + (int) (d2 / (double)2 * k);

			} else {
				arrowhead.x = p2.x - (int) (h2 / (double)2 / k);
				arrowhead.y = p2.y + (int) (h2 / (double)2);

			}
		} else if (p2.y < p1.y && p2.x == p1.x) {
			arrowhead.x = p2.x;
			arrowhead.y = p2.y + (int) (h2 / (double)2);
		} else if (p2.y < p1.y && p2.x < p1.x) {
			if (k2 >= k) {
				arrowhead.x = p2.x + (int) (d2 / (double)2);
				arrowhead.y = p2.y + (int) (k * d2 / (double)2);

			} else {
				arrowhead.x = p2.x + (int) (h2 / (double)2 / k);
				arrowhead.y = p2.y + (int) (h2 / (double)2);

			}
		} else if (p2.y == p1.y && p2.x < p1.x) {
			arrowhead.x = p2.x + (int) (d2 / (double)2);
			arrowhead.y = p2.y;
		} else if (p2.y > p1.y && p2.x < p1.x) {
			if (k2 >= k) {
				arrowhead.x = p2.x + (int) (d2 / (double)2);
				arrowhead.y = p2.y - (int) (d2 * k / 2);
			} else {
				arrowhead.x = p2.x + (int) (h2 / (double)2 / k);
				arrowhead.y = p2.y - (int) (h2 / (double)2);
			}
		} else {
			arrowhead.x = p2.x;
			arrowhead.y = p2.y - (int) (h2 / (double)2);
		}

		return arrowhead;
	}

	/**
	 * @return the vector
	 * @uml.property name="vector"
	 */
	public Vector<Object> getVector() {
		return this.vector;
	}

	public void addVector(Object obj) {
		if (this.vector.size() < 2) {
			this.vector.addElement(obj);
		} else { // 把新折点插入vector
			int i = this.getWhichLine(this.getBreakpoint());
			this.vector.insertElementAt(obj, i + 1);
		}

	}

	public boolean checkDistance(Point point) { // 检查点到点的距离是否小于一个给定常数
		int x = point.x;
		int y = point.y;
		int lx = 0;
		int ly = 0;
		int hx = 0;
		int hy = 0;
		int i = this.getWhichLine(this.getBreakpoint());
		if (i >= 0) {
			Point obj1 = (Point) this.vector.elementAt(i);
			Point obj2 = (Point) this.vector.elementAt(i + 1);
			if (i == 0) {
				obj1 = this.getStartPoint();
			}
			if (i == this.vector.size() - 2) {
				obj2 = this.getEndPoint();
			}

			if (obj1.x < obj2.x) {
				lx = obj1.x;
				hx = obj2.x;
			} else {
				hx = obj1.x;
				lx = obj2.x;
			}
			if (obj1.y < obj2.y) {
				ly = obj1.y;
				hy = obj2.y;
			} else {
				hy = obj1.y;
				ly = obj2.y;
			}
			double k = (double) (obj2.y - obj1.y) / (obj2.x - obj1.x);

			double z = obj1.y - k * obj1.x;
			int py = (int) (k * x + z);
			int px = (int) ((y - z) / k);
			if (k > 1 || k < -1) {
				if ((ly <= y && y <= hy) && ((x - px) >= -15 && (x - px) <= 15)) {

					return true;
				} else {
				}
			} else {
				if ((lx <= x && x <= hx) && ((y - py) >= -15 && (y - py) <= 15)) {

					return true;
				} else {
				}
			}

		}
		return false;
	}

	public void changeVector(Point point) {
		int pos = this.getChangevector();

		if (pos != -1) {
			this.vector.setElementAt(point, pos);

		}
	}

	public int getWhichLine(Point point) { // 检查新折点应该插入哪条折线之间
		if (point == null) {
			return -1;
		} else {
		}
		int x = point.x;
		int y = point.y;
		int lx = 0;
		int ly = 0;
		int hx = 0;
		int hy = 0;
		int i = 0;
		if (endnodeid != null && !getStartnode().isSelected(x, y)
				&& !getEndnode().isSelected(x, y)) {
			for (i = 0; i < this.vector.size() - 1; i++) {
				Point obj1 = (Point) this.vector.elementAt(i);
				Point obj2 = (Point) this.vector.elementAt(i + 1);
				if (i == 0) {
					obj1 = this.getStartPoint();
				}
				if (i == this.vector.size() - 2) {
					obj2 = this.getEndPoint();
				}
				if (obj1.x < obj2.x) {
					lx = obj1.x;
					hx = obj2.x;
				} else {
					hx = obj1.x;
					lx = obj2.x;
				}
				if (obj1.y < obj2.y) {
					ly = obj1.y;
					hy = obj2.y;
				} else {
					hy = obj1.y;
					ly = obj2.y;
				}
				double k = (double) (obj2.y - obj1.y) / (obj2.x - obj1.x);
				double z = obj1.y - k * obj1.x;
				int py = (int) (k * x + z);
				int px = (int) ((y - z) / k);
				if (k > 1 || k < -1) {
					if ((ly <= y && y <= hy)
							&& ((x - px) >= -5 && (x - px) <= 5)) {

						break;
					} else {
					}
				} else {
					if ((lx <= x && x <= hx)
							&& ((y - py) >= -5 && (y - py) <= 5)) {
						break;
					} else {
					}
				}
			}

		}
		return i;
	}

	public Node getStartnode() {
		if (startnodeid != null && startnodeid.trim().length() > 0) {
			Element sn = _owner.getElementByID(startnodeid);
			if (sn instanceof Node) {
				return (Node) sn;
			}
		}
		return null;
	}

	/**
	 * Sets the value of the Startnode property.
	 * 
	 * @param aStartnode
	 *            the new value of the Startnode property@param nd
	 * @roseuid 3E0A6E1B0322
	 */

	public void setStartnode(Node nd) {
		startnodeid = nd.id;
		// nd.appendElement(this);
	}

	/**
	 * Access method for the Endnode property.
	 * 
	 * @return the current value of the Endnode property
	 * @roseuid 3E0A6E1B0336
	 */
	public Node getEndnode() {
		if (endnodeid != null && endnodeid.trim().length() > 0) {
			Element en = _owner.getElementByID(endnodeid);

			if (en instanceof Node) {
				return (Node) en;
			}
		}
		return null;
	}

	/**
	 * Sets the value of the Endnode property.
	 * 
	 * @param aEndnode
	 *            the new value of the Endnode property@param nd
	 * @roseuid 3E0A6E1B034A
	 */
	public void setEndnode(Node nd) {
		endnodeid = nd.id;
	}

	/**
	 * @param x
	 * @param y
	 * @roseuid 3E0A6E1B035E
	 */
	public void moveTo(int x, int y) {
		if (_mousepoint == null) {
			_mousepoint = new Point(x, y);
		} else {
			_mousepoint.move(x, y);

		}
		if (_startpoint == null) {
			_startpoint = _mousepoint;
		}

		if (_endpoint == null) {
			_endpoint = _mousepoint;
		}
	}

	/**
	 * @param x
	 * @param y
	 * @return boolean
	 * @roseuid 3E0A6E1B037C
	 */
	public boolean isSelected(int x, int y) {
		boolean selected = false;
		int lx = 0;
		int ly = 0;
		int hx = 0;
		int hy = 0;
		if (endnodeid != null && getStartnode() != null && getEndnode() != null
				&& !getStartnode().isSelected(x, y)
				&& !getEndnode().isSelected(x, y)) {
			for (int i = 0; i < this.vector.size() - 1; i++) {
				Point obj1 = (Point) this.vector.elementAt(i);
				Point obj2 = (Point) this.vector.elementAt(i + 1);
				if (i == 0) {
					obj1 = this.getStartPoint();
				}
				if (i == this.vector.size() - 2) {
					obj2 = this.getEndPoint();
				}
				if (obj1.x < obj2.x) {
					lx = obj1.x;
					hx = obj2.x;
				} else {
					hx = obj1.x;
					lx = obj2.x;
				}
				if (obj1.y < obj2.y) {
					ly = obj1.y;
					hy = obj2.y;
				} else {
					hy = obj1.y;
					ly = obj2.y;
				}
				double k = (double) (obj2.y - obj1.y) / (obj2.x - obj1.x);
				double z = obj1.y - k * obj1.x;

				// 斜率大于1时比较x方向的差距,否则比较y方向的差距

				int py = (int) (k * x + z);
				int px = (int) ((y - z) / k);
				if (k > 1 || k < -1) {
					if ((ly <= y && y <= hy)
							&& ((x - px) >= -5 && (x - px) <= 5)) {
						selected = true;
						break;
					} else {
						selected = false;
					}
				} else {
					if ((lx <= x && x <= hx)
							&& ((y - py) >= -5 && (y - py) <= 5)) {

						selected = true;
						break;
					} else {
						selected = false;
					}
				}
			}
		}
		if (selected) {
			if (this._owner.get_statues() == 0x00000001) {
			} else {
				this._owner.setCursor(new Cursor(Cursor.SW_RESIZE_CURSOR));
			}
		} else {
			if (this._owner.get_statues() == 0x00000001) {
			} else {
				this._owner.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		}
		return selected;
	}

	/**
	 * @return java.awt.Rectangle
	 * @roseuid 3E0A6E1B039A
	 */
	public Rectangle getRepaintRect() {
		Rectangle rct = new Rectangle();
		return rct;
	}

	public int checkWhichpoint(Point point) { // 检查新折点与哪个原有折点距离最近
		int d = 0;
		int i = 0;
		int position = -1;
		for (i = 0; i < this.vector.size(); i++) {
			Point obj = (Point) this.vector.elementAt(i);
			d = Math.abs((int) (Math.sqrt((point.y - obj.y) * (point.y - obj.y)
					+ (point.x - obj.x) * (point.x - obj.x))));
			if (d <= 10) {
				position = i;
				this.changevector = i;
				break;
			}
		}

		return position;
	}

	public int getDistance(Point point1, Point point2) { // 得到点到点之间的距离
		int d = -1;
		d = Math.abs((int) Math.sqrt((point2.y - point1.y)
				* (point2.y - point1.y) + (point2.x - point1.x)
				* (point2.x - point1.x)));
		return d;
	}

	public boolean lineTolineAngle(Point point1, Point point2, Point point3) { // 检查线与线的夹角是否小于一个给定值
		double k1 = 0;
		double k2 = 0;
		double a = 0;
		if (point2.x == point1.x && point3.x == point2.x) {
			return true;
		} else if (point2.x == point1.x) {
			k1 = 0;
			k2 = (double) (point3.y - point2.y) / (point3.x - point2.x);
			a = Math.abs((k2 - k1) / (1 + k1 * k2));
			if (a >= Math.tan((double) 85 / 180 * Math.PI)) {
				return true;
			} else {
			}

		} else {
			if (point3.x == point2.x) {
				k2 = 0;
				k1 = (double) (point2.y - point1.y) / (point2.x - point1.x);
				a = Math.abs((k2 - k1) / (1 + k1 * k2));
				if (a >= Math.tan((double) 85 / 180 * Math.PI)) {
					return true;
				} else {
				}

			} else {
				k1 = (double) (point2.y - point1.y) / (point2.x - point1.x);
				k2 = (double) (point3.y - point2.y) / (point3.x - point2.x);
				a = Math.abs((k2 - k1) / (1 + k1 * k2));
				if (a <= Math.tan((double) 5 / 180 * Math.PI)) {
					return true;
				} else {
				}

			}
		}
		return false;

	}

	/**
	 * @return java.awt.Point
	 * @roseuid 3E0A6E1B03B8
	 */
	public Point getMovepoint() {
		return this._movepoint;
	}

	public void setMovepoint(Point p) {
		this._movepoint = p;
	}

	public Point getStartPoint() {
		Node nd = this.getStartnode();
		if (nd != null) {
			Point p = new Point(nd.x + nd.getRect().width / 2,
					nd.y + nd.getRect().height / 2);

			_startpoint = p;
			return p;
		}
		return this._startpoint;
	}

	/**
	 * @return java.awt.Point
	 * @roseuid 3E0A6E1B03CC
	 */
	public Point getEndPoint() {
		Node nd = this.getEndnode();
		if (nd != null) {
			Point p = new Point(nd.x + nd.getRect().width / 2,
					nd.y + nd.getRect().height / 2);

			_endpoint = p;
			return p;
		} else {
			return this._endpoint;
		}
	}

	public boolean removeSubElement(String id) {
		return false;
	}

	public void removeAllSubElement() {

	}

	/**
	 * @param e
	 * @roseuid 3E0A6F9A0047
	 */
	public void onMouseClicked(MouseEvent e) {

	}

	/**
	 * @param e
	 * @roseuid 3E0A6F9A0098
	 */
	public void onMouseDragged(MouseEvent e) {

	}

	/**
	 * @param e
	 * @roseuid 3E0A6F9A00F2
	 */
	public void onMouseMoved(MouseEvent e) {

	}

	/**
	 * @param e
	 * @roseuid 3E0A6F9A014C
	 */
	public void onMousePressed(MouseEvent e) {
	}

	/**
	 * @param e
	 * @roseuid 3E0A6F9A019C
	 */
	public void onMouseReleased(MouseEvent e) {

	}

	public void drawLine(Graphics g, int x1, int y1, int x2, int y2,
			boolean dashed) {
		/*
		if (false) {
			double sina = Math.abs((double) Math.sqrt((y2 - y1) * (y2 - y1))
					/ Math
							.sqrt(((x2 - x1) * (x2 - x1) + (y2 - y1)
									* (y2 - y1))));
			double cosa = Math.abs((double) Math.sqrt((x2 - x1) * (x2 - x1))
					/ Math
							.sqrt(((x2 - x1) * (x2 - x1) + (y2 - y1)
									* (y2 - y1))));
			int len = 20, blen = 30;
			int lencosa = (int) (len * cosa);
			int lensina = (int) (len * sina);

			int blencosa = (int) (blen * cosa);
			int blensina = (int) (blen * sina);

			int tx1 = 0, ty1 = 0, tx2 = 0, ty2 = 0;
			tx1 = x1;
			ty1 = y1;

			while ((tx2 - x1) * (tx2 - x1) + (ty2 - y1) * (ty2 - y1) <= (y2 - y1)
					* (y2 - y1) + (x2 - x1) * (x2 - x1)) {
				tx2 = (int) (tx1 + ((x2 - x1) >= 0 ? 1 : -1) * lencosa);
				ty2 = (int) (ty1 + ((y2 - y1) >= 0 ? 1 : -1) * lensina);
				g.drawLine(tx1, ty1, tx2, ty2);
				tx1 = (int) (tx1 + ((x2 - x1) >= 0 ? 1 : -1) * blencosa);
				ty1 = (int) (ty1 + ((y2 - y1) >= 0 ? 1 : -1) * blensina);
			}

		} else {
		*/
			g.drawLine(x1, y1, x2, y2);
		//}
	}

	public Node getAnotherEndNode(Node node) {
		if (node == null || node.id == null)
			return null;
		Node startNode = getStartnode();
		Node endNode = getEndnode();
		if (startNode != null && startNode.id != null) {
			if (startNode.id.equals(node.id)) {
				return endNode;
			}
		}

		if (endNode != null && endNode.id != null) {
			if (endNode.id.equals(node.id)) {
				return startNode;
			}
		}

		return null;

	}

	public boolean isLinkageKeyRelation() {
		Node startNode = getStartnode();
		Node endNode = getEndnode();
		return startNode != null && startNode instanceof Column && endNode != null
				&& endNode instanceof Column;
	}

	public LinkageKey getLinkageKey() {
		Node startNode = getStartnode();
		Node endNode = getEndnode();
		if (startNode != null && startNode instanceof Column && endNode != null
				&& endNode instanceof Column) {
			LinkageKey lks = new LinkageKey();

			AbstractSheet sheet = ((Column) startNode).getSheet();
			if (sheet instanceof MasterSheet) {
				lks.masterSheet = (MasterSheet) sheet;
				lks.masterSheetKeyColumn = (Column) startNode;
			} else if (sheet instanceof DetailSheet) {
				lks.detailSheet = (DetailSheet) sheet;
				lks.detailSheetKeyColumn = (Column) startNode;
			}

			sheet = ((Column) endNode).getSheet();
			if (sheet instanceof MasterSheet) {
				lks.masterSheet = (MasterSheet) sheet;
				lks.masterSheetKeyColumn = (Column) endNode;
			} else if (sheet instanceof DetailSheet) {
				lks.detailSheet = (DetailSheet) sheet;
				lks.detailSheetKeyColumn = (Column) endNode;
			}

			return lks;
		} else {
			return null;
		}
	}

	public boolean isCurrentToEdit() {
		return _owner.isCurrentToEdit(this);
	}
}
