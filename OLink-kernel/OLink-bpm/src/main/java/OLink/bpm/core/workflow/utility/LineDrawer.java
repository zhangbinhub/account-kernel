package OLink.bpm.core.workflow.utility;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;

public class LineDrawer {
	private int startx;
	private int starty;

	int x;
	int y;

	/***************************************************************************
	 * * *
	 * 
	 * @param startx
	 *            ： Storing the start x coordinate of the line.
	 * @param starty
	 *            ： Storing the start y coordinate of the line. *
	 * @param endx
	 *            ： Storing the end x coordinate of the line. *
	 * @param endy
	 *            ： Storing the end y coordinate of the line.
	 */
	public LineDrawer(int startx, int starty, int endx, int endy) {
		this.startx = startx;
		this.starty = starty;
		x = endx;
		y = endy;
	}

	void draw(Graphics2D g2, float stroke) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setStroke(new BasicStroke(stroke));
		g2.draw(new Line2D.Double(startx, starty, x, y));
		g2.setPaint(Color.black); //
		g2.setColor(Color.black);
		drawArrow(g2, startx, starty, x, y);
	}

	/** * Drawing the arrow.... * * * */
	public void drawArrow(Graphics2D g2, int x1, int y1, int x2, int y2) {
		g2.drawPolygon(getArrow(x1, y1, x2, y2, 18, 0, 0.5)); //
		g2.fillPolygon(getArrow(x1, y1, x2, y2, 18, 0, 0.5));
	}

	public Polygon getArrow(int x1, int y1, int x2, int y2, int headsize,
			int difference, double factor) {
		int[] crosslinebase = getArrowHeadLine(x1, y1, x2, y2, headsize);
		int[] headbase = getArrowHeadLine(x1, y1, x2, y2, headsize - difference);
		int[] crossline = getArrowHeadCrossLine(crosslinebase[0],
				crosslinebase[1], x2, y2, factor);
		Polygon head = new Polygon();
		head.addPoint(headbase[0], headbase[1]);
		head.addPoint(crossline[0], crossline[1]);
		head.addPoint(x2, y2);
		head.addPoint(crossline[2], crossline[3]);
		head.addPoint(headbase[0], headbase[1]);
		head.addPoint(x1, y1);
		return head;
	}

	public int[] getArrowHeadLine(int xsource, int ysource, int xdest,
			int ydest, int distance) {
		int[] arrowhead = new int[2];
		int headsize = distance;
		double stretchfactor = 0;
		stretchfactor = 1 - (headsize / (Math
				.sqrt(((xdest - xsource) * (xdest - xsource))
						+ ((ydest - ysource) * (ydest - ysource)))));
		arrowhead[0] = (int) (stretchfactor * (xdest - xsource)) + xsource;
		arrowhead[1] = (int) (stretchfactor * (ydest - ysource)) + ysource;
		return arrowhead;
	}

	public int[] getArrowHeadCrossLine(int x1, int x2, int b1, int b2,
			double factor) {
		int[] crossline = new int[4];
		int xdest = (int) (((b1 - x1) * factor) + x1);
		int ydest = (int) (((b2 - x2) * factor) + x2);
		crossline[0] = (x1 + x2 - ydest);
		crossline[1] = (x2 + xdest - x1);
		crossline[2] = crossline[0] + (x1 - crossline[0]) * 2;
		crossline[3] = crossline[1] + (x2 - crossline[1]) * 2;
		return crossline;
	}
}
