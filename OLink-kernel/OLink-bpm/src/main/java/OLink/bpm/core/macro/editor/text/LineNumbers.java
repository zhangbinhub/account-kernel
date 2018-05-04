/*
 * JSide is an Integrated Development Environment for JavaScript Copyright
 * (C) 2006 JSide Development Team
 * 
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */
package OLink.bpm.core.macro.editor.text;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JComponent;

/**
 * Adds line numbers to text pane
 * 
 * @author Adeel Javed
 */
public class LineNumbers extends JComponent
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8976729104882183705L;

	// application variables
	private final static int HEIGHT = Integer.MAX_VALUE - 1000000;

	private final static int MARGIN = 1;

	private int lineHeight;

	private int fontLineHeight;

	private int currentRowWidth;

	private FontMetrics fontMetrics;

	/**
	 * Convenience constructor for Text Components
	 */
	public LineNumbers(JComponent component, Color bgColor, Color fgColor)
	{
		setBackground(bgColor);
		setForeground(fgColor);
		super.setFont(component.getFont());
		fontMetrics = getFontMetrics(getFont());
		fontLineHeight = fontMetrics.getHeight();
		setFont(component.getFont());

		setPreferredSize(9999);
	}

	public void setPreferredSize(int row)
	{
		int width = fontMetrics.stringWidth(String.valueOf(row));

		if (currentRowWidth < width)
		{
			currentRowWidth = width;
			setPreferredSize(new Dimension(2 * MARGIN + width, HEIGHT));
		}
	}

	/**
	 * The line height defaults to the line height of the font for this
	 * component. The line height can be overridden by setting it to a
	 * positive non-zero value.
	 */
	public int getLineHeight()
	{
		if (lineHeight == 0)
			return fontLineHeight;
		else
			return lineHeight;
	}

	public void setLineHeight(int lineHeight)
	{
		if (lineHeight > 0)
			this.lineHeight = lineHeight;
	}

	public int getStartOffset()
	{
		return 4;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g)
	{
		int lineHeight = getLineHeight();
		int startOffset = getStartOffset();
		Rectangle drawHere = g.getClipBounds();

		// Paint the background
		g.setColor(getBackground());
		g.fillRect(drawHere.x, drawHere.y, drawHere.width, drawHere.height);

		// Determine the number of lines to draw in the foreground.
		g.setColor(getForeground());
		int startLineNumber = (drawHere.y / lineHeight) + 1;
		int endLineNumber = startLineNumber + (drawHere.height / lineHeight);

		int start = (drawHere.y / lineHeight) * lineHeight + lineHeight
				- startOffset;

		for (int i = startLineNumber; i <= endLineNumber; i++)
		{
			String lineNumber = String.valueOf(i);
			int width = fontMetrics.stringWidth(lineNumber);
			g.drawString(lineNumber, MARGIN + currentRowWidth - width, start);
			start += lineHeight;
		}

		setPreferredSize(endLineNumber);
	}
}
