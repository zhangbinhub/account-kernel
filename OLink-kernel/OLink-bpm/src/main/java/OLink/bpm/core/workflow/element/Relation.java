package OLink.bpm.core.workflow.element;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.Vector;

public class Relation extends PaintElement {
	// System
	public String state;

	public String startnodeid;

	public String endnodeid;

	public boolean ispassed;

	public boolean isreturn; // 是否是返回路径

	/**
	 * 线路显示条件, 在(FlowDiagram.getNextNode)中使用
	 */
	public String condition = null;

	public String filtercondition = null;

	public String editMode = null;

	public String processDescription = null;

	public String action = null;

	public String pointstack;

	static final int ARROW_LONG = 14;

	static final int ARROW_WIDTH = 4;

	public static final String EDITMODE_VIEW = "00";// 流程条件视图编辑模式

	public static final String EDITMODE_CODE = "01";// 流程条件代码编辑模式

	static final double PAI = 3.1415926525;

	private Point _startpoint;

	private Point _endpoint;

	private Node _startnode;

	private Node _endnode;

	private Point _mousepoint;

	private Point _movepoint = null; // 拖拉时鼠标的移动点

	private Point breakpoint = null; // 按下鼠标时的点

	protected Rectangle _txtrect;

	private Vector vector = null;

	private int changevector = -1; // 当鼠标按下时恰好是流程线原有折点时的折点位置

	private boolean currentselect = false;

	private boolean initstart = false; // 由于该流程图用xml文件保存，所以当装载流程图时要构造保存流程线的折点位置的vector

	public String validateScript = null;

	/**
	 * @param owner
	 * @roseuid 3E0428DB027D
	 */
	public Relation(FlowDiagram owner) {
		super(owner);
		vector = new Vector();
		this.initstart = true;

	}

	public void paintMobile(OGraphics graphics) {
		paint(graphics);
	}

	public void paint(OGraphics g) {

		Color old = this.color; // 保存当前颜色

		Point startPoint = this.getStartPoint();
		Point endPoint = this.getEndPoint();
		if (this.initstart) { // 如果流程图是第一次画，则要从原有xml文件中读取折点的坐标
			this.initVector(this.pointstack); // pointstack是与xml文件打交道的用于存储流程线的折点坐标的public
			// String型变量
		}

		drawSelfCycle(startPoint, endPoint);// 画自循环线

		this.initstart = false;
		if (startPoint != null && endPoint != null) {

			mergPoint();// 合并折点

			// int x1 = 0, y1 = 0;
			// int x2 = 0, y2 = 0;
			int d2 = 0, h2 = 0;

			// x1 = endPoint.x;
			// y1 = endPoint.y;

			// x2 = startPoint.x;
			// y2 = startPoint.y;
			if (this.ispassed) {
				this.color = Color.green;
			} else if (this.currentselect) {
				this.color = DEF_SELECTEDCOLOR;
				this.currentselect = false;
			} else if (_owner.isCurrentToEdit(this)) {
				this.color = DEF_CURREDITCOLOR;
			} else {
				this.color = DEF_COLOR;
			}
			if (this.vector.size() < 2) {
				d2 = 0;
				h2 = 0;
			} else {

				Node node = this.getEndnode();

				d2 = node._imgrect.width;
				h2 = node._imgrect.height;
			}

			g.setColor(this.color);

			Point sPoint = null;

			sPoint = this.getArrowhead(new Point(endPoint.x, endPoint.y), new Point(startPoint.x, startPoint.y), d2, h2); // 得到流程线箭头的坐标

			Point ePoint = null;

			ePoint = this.getArrowhead(new Point(startPoint.x, startPoint.y), new Point(endPoint.x, endPoint.y), d2, h2); // 得到流程线箭头的坐标

			if (this.vector.size() < 3) { // 鼠标从开始结点拖拉到结尾结点的过程中，鼠标当前移动点作为暂时的尾结点
				if (this._movepoint != null) {

					sPoint = this.getArrowhead(new Point(this._movepoint.x, this._movepoint.y), new Point(startPoint.x,
							startPoint.y), d2, h2);

					g.drawLine(sPoint.x, sPoint.y, this._movepoint.x, this._movepoint.y);

					startPoint.x = this._movepoint.x;
					startPoint.y = this._movepoint.y;

					ePoint = this.getArrowhead(new Point(startPoint.x, startPoint.y), new Point(endPoint.x, endPoint.y), d2, h2);

					g.drawLine(this._movepoint.x, this._movepoint.y, ePoint.x, ePoint.y);

					this._movepoint = null;
				} else {
					g.drawLine(sPoint.x, sPoint.y, ePoint.x, ePoint.y);
				}
			} else {
				if (this._movepoint != null) { // 画流程线折点时拖拉鼠标的情况
					int whichLine = this.getWhichLine(this.getBreakpoint());
					for (int j = 0; j < this.vector.size() - 1; j++) {
						Point obj1 = (Point) this.vector.elementAt(j);
						Point obj2 = (Point) this.vector.elementAt(j + 1);

						startPoint.x = obj1.x;
						startPoint.y = obj1.y;

						if (j == 0) {
							obj1 = this.getStartPoint();
						}
						if (j == this.vector.size() - 2) {
							ePoint = this.getArrowhead(new Point(startPoint.x, startPoint.y), new Point(endPoint.x, endPoint.y),
									d2, h2);
							obj2 = ePoint;
						}
						if (j == whichLine) {
							g.drawLine(obj1.x, obj1.y, this._movepoint.x, this._movepoint.y);
							startPoint.x = this._movepoint.x;
							startPoint.y = this._movepoint.y;
							if (j == this.vector.size() - 2) {
								sPoint = this.getArrowhead(new Point(endPoint.x, endPoint.y), new Point(startPoint.x,
										startPoint.y), d2, h2);
								obj1 = sPoint;

								ePoint = this.getArrowhead(new Point(startPoint.x, startPoint.y), new Point(endPoint.x,
										endPoint.y), d2, h2);
								obj2 = ePoint;
							}
							g.drawLine(this._movepoint.x, this._movepoint.y, obj2.x, obj2.y);
						} else {
							g.drawLine(obj1.x, obj1.y, obj2.x, obj2.y);
						}
					}
					this._movepoint = null;
				} else { // 鼠标释放点设为新折点
					for (int k = 0; k < this.vector.size() - 1; k++) {
						Point obj3 = (Point) this.vector.elementAt(k);
						Point obj4 = (Point) this.vector.elementAt(k + 1);
						startPoint.x = obj3.x;
						startPoint.y = obj3.y;
						if (k == 0) {
							obj3 = this.getStartPoint();			
							obj3 = this.getArrowhead(new Point(obj4.x, obj4.y), new Point(obj3.x, obj3.y), d2, h2);
							// obj3 = sPoint;
						}
						if (k == this.vector.size() - 2) {
							ePoint = this.getArrowhead(new Point(startPoint.x, startPoint.y), new Point(endPoint.x, endPoint.y),
									d2, h2);
							obj4 = ePoint;
						}
						g.drawLine(obj3.x, obj3.y, obj4.x, obj4.y);

					}
				}
			}

			ePoint = this.getArrowhead(new Point(startPoint.x, startPoint.y), new Point(endPoint.x, endPoint.y), d2, h2);

			drawArrow(g, endPoint.x, endPoint.y, startPoint.x, startPoint.y, ePoint);// 画箭头

			g.setColor(Color.black);

			drawRelationText(g, endPoint.x, endPoint.y, startPoint.x, startPoint.y);// 画“名称”

		}
		this.color = old; // 恢复当前颜色
		this.setPointStack(this.vector); // 把vector里的折点坐标存储在pointstack里，跟xml文件打交道
	}

	private void drawRelationText(OGraphics g, int x1, int y1, int x2, int y2) {
		int mx;
		int my;
		if (this.name != null) {

			mx = (x2 + x1) / 2;
			my = (y2 + y1) / 2;

			java.awt.FontMetrics fm = _owner.getFontMetrics(font);

			int rx = mx - 10;
			int ry = my + fm.getHeight(); // 比线段中间位低

			g.setColor(Color.blue);

			g.drawString(name, rx, ry);

			g.setColor(Color.black);
		}
	}

	private void drawArrow(OGraphics g, int x1, int y1, int x2, int y2, Point arrowhead) {
		int hx;
		int hy;
		double sina;
		double cosa;
		sina = Math.abs(Math.sqrt((y2 - y1) * (y2 - y1)) / Math.sqrt(((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1))));
		cosa = Math.abs(Math.sqrt((x2 - x1) * (x2 - x1)) / Math.sqrt(((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1))));
		// 第一象限
		Point arrowstart1 = new Point();
		Point arrowstart2 = new Point();
		if (x2 < arrowhead.x && y2 < arrowhead.y) { // 求箭头线的开始点，有两个，分别在流程线的两侧
			arrowstart1.x = arrowhead.x + (int) (ARROW_WIDTH * sina - ARROW_LONG * cosa);
			arrowstart1.y = arrowhead.y - (int) (ARROW_LONG * sina + ARROW_WIDTH * cosa);
			arrowstart2.x = arrowhead.x - (int) (ARROW_LONG * cosa + ARROW_WIDTH * sina);
			arrowstart2.y = arrowhead.y + (int) (ARROW_WIDTH * cosa - ARROW_LONG * sina);
		} else if (x2 == arrowhead.x && y2 < arrowhead.y) {
			arrowstart1.x = arrowhead.x + ARROW_WIDTH;
			arrowstart1.y = arrowhead.y - ARROW_LONG;
			arrowstart2.x = arrowhead.x - ARROW_WIDTH;
			arrowstart2.y = arrowhead.y - ARROW_LONG;
		} else if (x2 > arrowhead.x && y2 < arrowhead.y) {
			arrowstart1.x = arrowhead.x + (int) (ARROW_LONG * cosa + ARROW_WIDTH * sina);
			arrowstart1.y = arrowhead.y + (int) (ARROW_WIDTH * cosa - ARROW_LONG * sina);
			arrowstart2.x = arrowhead.x + (int) (ARROW_LONG * cosa - ARROW_WIDTH * sina);
			arrowstart2.y = arrowhead.y - (int) (ARROW_LONG * sina + ARROW_WIDTH * cosa);
		} else if (x2 > arrowhead.x && y2 == arrowhead.y) {
			arrowstart1.x = arrowhead.x + ARROW_LONG;
			arrowstart1.y = arrowhead.y + ARROW_WIDTH;
			arrowstart2.x = arrowhead.x + ARROW_LONG;
			arrowstart2.y = arrowhead.y - ARROW_WIDTH;
		} else if (x2 > arrowhead.x && y2 > arrowhead.y) {
			arrowstart1.x = arrowhead.x + (int) (ARROW_LONG * cosa - ARROW_WIDTH * sina);
			arrowstart1.y = arrowhead.y + (int) (ARROW_LONG * sina + ARROW_WIDTH * cosa);
			arrowstart2.x = arrowhead.x + (int) (ARROW_LONG * cosa + ARROW_WIDTH * sina);
			arrowstart2.y = arrowhead.y + (int) (ARROW_LONG * sina - ARROW_WIDTH * cosa);
		} else if (x2 == arrowhead.x && y2 > arrowhead.y) {
			arrowstart1.x = arrowhead.x - ARROW_WIDTH;
			arrowstart1.y = arrowhead.y + ARROW_LONG;
			arrowstart2.x = arrowhead.x + ARROW_WIDTH;
			arrowstart2.y = arrowhead.y + ARROW_LONG;
		} else if (x2 < arrowhead.x && y2 > arrowhead.y) {
			arrowstart1.x = arrowhead.x - (int) (ARROW_LONG * cosa + ARROW_WIDTH * sina);
			arrowstart1.y = arrowhead.y + (int) (ARROW_LONG * sina - ARROW_WIDTH * cosa);
			arrowstart2.x = arrowhead.x + (int) (ARROW_WIDTH * sina - ARROW_LONG * cosa);
			arrowstart2.y = arrowhead.y + (int) (ARROW_LONG * sina + ARROW_WIDTH * cosa);
		} else {
			arrowstart1.x = arrowhead.x - ARROW_LONG;
			arrowstart1.y = arrowhead.y - ARROW_WIDTH;
			arrowstart2.x = arrowhead.x - ARROW_LONG;
			arrowstart2.y = arrowhead.y + ARROW_WIDTH;
		}
		hx = arrowhead.x;

		hy = arrowhead.y;
		// g.setColor(Color.red);
		Polygon arrow = new Polygon();
		arrow.addPoint(hx, hy);
		arrow.addPoint(arrowstart1.x, arrowstart1.y);
		arrow.addPoint(arrowstart2.x, arrowstart2.y);

		// g.drawLine(arrowstart1.x, arrowstart1.y, hx, hy);
		// g.drawLine(arrowstart2.x, arrowstart2.y, hx, hy);
		g.fillPolygon(arrow);
	}

	private void mergPoint() {
		if (this.vector.size() >= 3) {
			while (true) { // 把相邻的两个距离小于10的折点合并为一个点
				if (this.vector.size() >= 3) {
					int distance = -1;
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

						distance = this.getDistance(obj1, obj2);

						if (distance <= 10) { // 若两点相邻且距离小于10，则删去其中一个点
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
	}

	private void drawSelfCycle(Point startPoint, Point endPoint) {
		// 如果开始坐标与结束坐标点重合，则生成自连接线
		if (!this.getCurrentselect() && this.getStartnode() == this.getEndnode()) {
			if (startPoint != null) {
				vector.clear();
				vector.add(startPoint);
				Point point = new Point(startPoint.x + 30, startPoint.y);
				vector.add(point);
				point = new Point(point.x, point.y - 50);
				vector.add(point);
				point = new Point(point.x - 30, point.y);
				vector.add(point);
				vector.add(endPoint);
			}
		}
	}

	/**
	 * Access method for the Startnode property.
	 * 
	 * @return the current value of the Startnode property
	 * @roseuid 3E0A6E1B0318
	 */
	public int getChangevector() {
		return this.changevector;
	}

	public void setChangevector(int change) {
		this.changevector = change;
	}

	public void setPointStack(Vector vector) {
		String strTemp = "";
		if (this.vector != null) {
			for (int i = 0; i < this.vector.size(); i++) {
				Point point = (Point) this.vector.elementAt(i);
				strTemp = strTemp + point.x + ";" + point.y;
				if (i < this.vector.size() - 1) {
					strTemp = strTemp + ";"; // 例如： "123；234；456；444"形式
				}
			}
		}
		this.pointstack = strTemp;
	}

	/**
	 * 把pointstack里的x,y坐标转换成vector里的Point对象
	 * 
	 * @param pointstack
	 */
	public void initVector(String pointstack) {
		String str = new String();
		str = pointstack;
		int length = 0;
		if (str == null || str.equalsIgnoreCase("")) {

		} else {
			int x = 0;
			int y = 0;
			int position = 0;
			length = str.length();
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
				}
			}
		}
	}

	public void setCurrentselect(boolean curSelect) {
		this.currentselect = curSelect;
	}

	public boolean getCurrentselect() {
		return this.currentselect;
	}

	public Point getBreakpoint() {
		return this.breakpoint;
	}

	public void setBreakpoint(Point point) {
		this.breakpoint = point;
	}

	/**
	 * 得到箭头坐标
	 * 
	 * @param p1
	 * @param p2
	 * @param d2
	 * @param h2
	 * @return
	 */
	public Point getArrowhead(Point p1, Point p2, int d2, int h2) {
		double k = Math.abs((double) (p2.y - p1.y) / (p2.x - p1.x));
		double k2 = (double) h2 / d2;
		Point arrowhead = new Point();
		if (p2.y > p1.y && p2.x > p1.x) {
			if (k2 >= k) {
				arrowhead.x = p2.x - d2 / 2;
				arrowhead.y = p2.y - (int) (k * d2 / 2);
			} else {
				arrowhead.x = p2.x - (int) (h2 / 2 / k);
				arrowhead.y = p2.y - h2 / 2;

			}
		} else if (p2.y == p1.y && p2.x > p1.x) {
			arrowhead.x = p2.x - d2 / 2;
			arrowhead.y = p2.y;
		} else if (p2.y < p1.y && p2.x > p1.x) {
			if (k2 >= k) {
				arrowhead.x = p2.x - h2 / 2;
				arrowhead.y = p2.y + (int) (d2 / 2 * k);

			} else {
				arrowhead.x = p2.x - (int) (h2 / 2 / k);
				arrowhead.y = p2.y + h2 / 2;

			}
		} else if (p2.y < p1.y && p2.x == p1.x) {
			arrowhead.x = p2.x;
			arrowhead.y = p2.y + h2 / 2;
		} else if (p2.y < p1.y && p2.x < p1.x) {
			if (k2 >= k) {
				arrowhead.x = p2.x + d2 / 2;
				arrowhead.y = p2.y + (int) (k * d2 / 2);

			} else {
				arrowhead.x = p2.x + (int) (h2 / 2 / k);
				arrowhead.y = p2.y + h2 / 2;

			}
		} else if (p2.y == p1.y && p2.x < p1.x) {
			arrowhead.x = p2.x + d2 / 2;
			arrowhead.y = p2.y;
		} else if (p2.y > p1.y && p2.x < p1.x) {
			if (k2 >= k) {
				arrowhead.x = p2.x + d2 / 2;
				arrowhead.y = p2.y - (int) (d2 * k / 2);
			} else {
				arrowhead.x = p2.x + (int) (h2 / 2 / k);
				arrowhead.y = p2.y - h2 / 2;
			}
		} else {
			arrowhead.x = p2.x;
			arrowhead.y = p2.y - h2 / 2;
		}

		return arrowhead;
	}

	public Vector getVector() {
		return this.vector;
	}

	public void addVector(Object obj) {
		if (this.vector.size() < 2) {
			this.vector.addElement(obj);
		} else { // 把新折点插入vector
			int i = this.getWhichLine(this.getBreakpoint());
			this.vector.insertElementAt(obj, i + 1);
		}

		mergPoint();

	}

	/**
	 * 检查点到点的距离是否小于一个给定常数
	 * 
	 * @param point
	 * @return
	 */
	public boolean checkDistance(Point point) {
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

			if (obj1.x == obj2.x) {
				// selected = Math.abs(x - obj1.x) < 5
				// && ((y > obj1.y && y < obj2.y) || (y > obj2.y && y <
				// obj1.y));
				return Math.abs(obj1.y - obj2.y) < 15;
			} else {

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

	/**
	 * 检查新折点应该插入哪条折线之间
	 * 
	 * @param point
	 * @return
	 */
	public int getWhichLine(Point point) {
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
		if (endnodeid != null && !getStartnode().isSelected(x, y) && !getEndnode().isSelected(x, y)) {
			for (i = 0; i < this.vector.size() - 1; i++) {
				Point obj1 = (Point) this.vector.elementAt(i);
				Point obj2 = (Point) this.vector.elementAt(i + 1);
				if (i == 0) {
					obj1 = this.getStartPoint();
				}
				if (i == this.vector.size() - 2) {
					obj2 = this.getEndPoint();
				}

				if (obj1.x == obj2.x) {
					break;
				} else {
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
						if ((ly <= y && y <= hy) && ((x - px) >= -5 && (x - px) <= 5)) {

							break;
						} else {
						}
					} else {
						if ((lx <= x && x <= hx) && ((y - py) >= -5 && (y - py) <= 5)) {
							break;
						} else {
						}
					}

				}
			}

		}
		return i;
	}

	/**
	 * 返回开始结点
	 * 
	 * @return
	 */
	public Node getStartnode() {
		if (startnodeid != null && startnodeid.trim().length() > 0) {
			if (_startnode == null) {
				Element sn = _owner.getElementByID(startnodeid);
				_startnode = (Node) sn;
			}

			return _startnode;
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
	}

	/**
	 * Access method for the Endnode property.
	 * 
	 * @return the current value of the Endnode property
	 * @roseuid 3E0A6E1B0336
	 */
	public Node getEndnode() {
		if (endnodeid != null && endnodeid.trim().length() > 0) {
			if (_endnode == null) {
				Element en = _owner.getElementByID(endnodeid);
				_endnode = (Node) en;
			}

			return _endnode;

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

		Node startNode = getStartnode();
		Node endNode = getEndnode();

		if (endnodeid != null && startNode != null && endNode != null && !startNode.isSelected(x, y) && !endNode.isSelected(x, y)) {
			for (int i = 0; i < this.vector.size() - 1; i++) {
				Point obj1 = (Point) this.vector.elementAt(i);
				Point obj2 = (Point) this.vector.elementAt(i + 1);
				if (i == 0) {
					obj1 = this.getStartPoint();
				}
				if (i == this.vector.size() - 2) {
					obj2 = this.getEndPoint();
				}

				if (obj1.x == obj2.x) {
					selected = Math.abs(x - obj1.x) < 5 && ((y > obj1.y && y < obj2.y) || (y > obj2.y && y < obj1.y));

					break;

				} else {
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
						if ((ly <= y && y <= hy) && ((x - px) >= -5 && (x - px) <= 5)) {
							selected = true;
							break;
						} else {
							selected = false;
						}
					} else {
						if ((lx <= x && x <= hx) && ((y - py) >= -5 && (y - py) <= 5)) {

							selected = true;
							break;
						} else {
							selected = false;
						}
					}
				}
			}
		}
		if (selected) {
			if (this._owner.get_statues() == 0x00000001) {
			} else {
				this._owner.setCursor(new Cursor(Cursor.HAND_CURSOR));
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

	/**
	 * 检查新折点与哪个原有折点距离最近
	 * 
	 * @param point
	 * @return
	 */
	public int checkWhichpoint(Point point) {
		int d = 0;
		int i = 0;
		int position = -1;
		for (i = 0; i < this.vector.size(); i++) {
			Point obj = (Point) this.vector.elementAt(i);
			d = Math.abs((int) (Math.sqrt((point.y - obj.y) * (point.y - obj.y) + (point.x - obj.x) * (point.x - obj.x))));
			if (d <= 10) {
				position = i;
				this.changevector = i;
				break;
			}
		}

		return position;
	}

	/**
	 * 得到点到点之间的距离
	 * 
	 * @param point1
	 * @param point2
	 * @return
	 */
	public int getDistance(Point point1, Point point2) {
		int d = -1;
		d = Math.abs((int) Math.sqrt((point2.y - point1.y) * (point2.y - point1.y) + (point2.x - point1.x)
				* (point2.x - point1.x)));
		return d;
	}

	/**
	 * 检查线与线的夹角是否小于一个给定值
	 * 
	 * @param point1
	 * @param point2
	 * @param point3
	 * @return
	 */
	public boolean lineTolineAngle(Point point1, Point point2, Point point3) {
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

	/**
	 * 返回开始结点的坐标
	 * 
	 * @return
	 */
	public Point getStartPoint() {
		Node nd = this.getStartnode();
		if (nd != null) {
			Point p = new Point(nd.x + nd._imgrect.width / 2, nd.y + nd._imgrect.height / 2);

			_startpoint = p;
			return p;
		}
		return this._startpoint;
	}

	/**
	 * 返回结束结点的坐标
	 * 
	 * @return java.awt.Point
	 * @roseuid 3E0A6E1B03CC
	 */
	public Point getEndPoint() {
		Node nd = this.getEndnode();
		if (nd != null) {
			Point p = new Point(nd.x + nd._imgrect.width / 2, nd.y + nd._imgrect.height / 2);

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

}
