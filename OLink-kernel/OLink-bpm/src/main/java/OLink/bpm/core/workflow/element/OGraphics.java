package OLink.bpm.core.workflow.element;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.image.ImageObserver;

public class OGraphics {

	private Graphics g;

	private double compressRate = 1.0;

	private static int maxX = 0;

	private static int maxY = 0;

	static {
		maxX = 0;
		maxY = 0;
	}

	public double getCompressRate() {
		return compressRate;
	}

	public void setCompressRate(double rate) {
		this.compressRate = rate;
	}

	public int getMaxX() {
		return maxX;
	}
/*
	public void setMaxX(int maxX) {
		OGraphics.maxX = maxX;
	}
*/
	public int getMaxY() {
		return maxY;
	}
/*
	public void setMaxY(int maxY) {
		OGraphics.maxY = maxY;
	}
*/
	public OGraphics() {
		this.setCompressRate(1.0);

	}

	public void compareWithX(int compare) {
		if (compare > maxX) {
			maxX = compare;
		}
	}

	public void compareWithY(int compare) {
		if (compare > maxY) {
			maxY = compare;
		}
	}

	public OGraphics(Graphics g) {
		this.g = g;
		g.setFont(PaintElement.DEF_FONT);
	}

	public void drawLine(int x1, int y1, int x2, int y2) {
		if (g != null) {
			g.drawLine((int) (x1 * compressRate), (int) (y1 * compressRate),
					(int) (x2 * compressRate), (int) (y2 * compressRate));
		}

		compareWithX((int) (x1 * compressRate));
		compareWithX((int) (x2 * compressRate));
		compareWithY((int) (y1 * compressRate) + 2);
		compareWithY((int) (y2 * compressRate) + 2);
	}

	public void drawString(String name, int rx, int ry) {
		if (g != null) {
			g.drawString(name, (int) (rx * compressRate),
					(int) (ry * compressRate));
		}
		compareWithX((int) (rx * compressRate));
		compareWithX((int) (rx * compressRate + getStringWidth(getFont(), name) + 2));
		compareWithY((int) (ry * compressRate) + 2);
	}

	public void setColor(Color color) {
		if (g != null) {
			g.setColor(color);
		}
	}

	public void fillPolygon(Polygon p) {
		int[] x = p.xpoints;
		int[] y = p.ypoints;

		Polygon polygon = new Polygon();

		for (int i = 0; i < p.npoints; i++) {
			polygon.addPoint((int) (x[i] * compressRate),
					(int) (y[i] * compressRate));
			compareWithX((int) (x[i] * compressRate));
			compareWithY((int) (y[i] * compressRate));
		}
		if (g != null) {

			g.fillPolygon(polygon);
		}
	}

	public void fillRect(int x, int y, int width, int height) {
		if (g != null) {
			g
					.fillRect((int) (x * compressRate),
							(int) (y * compressRate),
							(int) (width * compressRate),
							(int) (height * compressRate));

		}
		// compareWithX((int) (x * compressRate));
		// compareWithY((int) (y * compressRate));
		// compareWithX((int) ((x + width) * compressRate));
		// compareWithY((int) ((y + height) * compressRate));
	}

	public void clearRect(int x, int y, int width, int height) {
		if (g != null) {
			g
					.clearRect((int) (x * compressRate),
							(int) (y * compressRate),
							(int) (width * compressRate),
							(int) (height * compressRate));
		}
	}

	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
			int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
		compareWithX((int) (dx1 * compressRate));
		compareWithY((int) (dy1 * compressRate));
		compareWithX((int) (dx2 * compressRate));
		compareWithY((int) (dy2 * compressRate));
		compareWithX((int) (sx1 * compressRate));
		compareWithY((int) (sy1 * compressRate));
		compareWithX((int) (sx2 * compressRate));
		compareWithY((int) (sy2 * compressRate));

		if (g != null) {
			return g.drawImage(img, (int) (dx1 * compressRate),
					(int) (dy1 * compressRate), (int) (dx2 * compressRate),
					(int) (dy2 * compressRate), (int) (sx1 * compressRate),
					(int) (sy1 * compressRate), (int) (sx2 * compressRate),
					(int) (sy2 * compressRate), observer);
		} else {
			return true;
		}
	}

	public boolean drawImage(Image img, int x, int y, Color bgcolor,
			ImageObserver observer) {
		compareWithX((int) (x * compressRate));
		compareWithY((int) (y * compressRate));
		if (g != null) {
			return g.drawImage(img, (int) (x * compressRate),
					(int) (y * compressRate), bgcolor, observer);
		} else {
			return true;
		}
	}

	public boolean drawImage(Image img, int x, int y, int width, int height,
			Color bgcolor, ImageObserver observer) {

		compareWithX((int) (x * compressRate));
		compareWithY((int) (y * compressRate));
		compareWithX((int) ((x + width) * compressRate));
		compareWithY((int) ((y + height) * compressRate));
		if (g != null) {
			return g.drawImage(img, (int) (x * compressRate),
					(int) (y * compressRate), (int) (width * compressRate),
					(int) (height * compressRate), bgcolor, observer);
		} else {
			return true;
		}
	}

	public void setFont(Font f) {
		if (g != null) {
			g.setFont(f);
		}
	}

	public Font getFont() {
		if (g != null) {
			return g.getFont();
		}
		return PaintElement.DEF_FONT;
	}

	/**
	 * 获取字符串所占的像素宽度
	 * 
	 * @param font
	 * @param str
	 * @return
	 */
	public int getStringWidth(Font font, String str) {

		return str.length() * font.getSize();
	}
}
