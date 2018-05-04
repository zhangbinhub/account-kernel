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
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.View;

/**
 * Performs line highlighting
 * 
 * @author Adeel Javed
 */
public class LineHighlighter extends DefaultHighlighter.DefaultHighlightPainter
		implements CaretListener, MouseListener, MouseMotionListener,
		KeyListener
{
	// application variables
	private JTextComponent component;

	private DefaultHighlighter highlighter;

	private Object lastHighlight;

	/**
	 * Constructor
	 * 
	 * @param component A text component
	 * @param color The background color
	 */
	public LineHighlighter(JTextComponent component, Color color)
	{

		super(color);
		this.component = component;

		highlighter = (DefaultHighlighter) component.getHighlighter();
		highlighter.setDrawsLayeredHighlights(true);

		// add listener so we know when to change highlighting
		component.addKeyListener(this);
		component.addCaretListener(this);
		component.addMouseListener(this);
		component.addMouseMotionListener(this);

		// initially highlight the first line
		addHighlight(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.text.LayeredHighlighter$LayerPainter#paintLayer(java.awt.Graphics,
	 *      int, int, java.awt.Shape, javax.swing.text.JTextComponent,
	 *      javax.swing.text.View)
	 */
	public Shape paintLayer(Graphics g, int offs0, int offs1, Shape bounds,
			JTextComponent c, View view)
	{
		try
		{

			// Only use the first offset to get the line to highlight
			Rectangle r = c.modelToView(offs0);
			r.x = 0;
			r.width = c.getSize().width;

			// --- render ---
			g.setColor(getColor());
			g.fillRect(r.x, r.y, r.width, r.height);
			return r;
		}
		catch (BadLocationException e)
		{
			return null;
		}
	}

	/**
	 * Remove/add the highlight to make sure it gets repainted
	 */
	private void resetHighlight()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				highlighter.removeHighlight(lastHighlight);

				Element root = component.getDocument().getDefaultRootElement();
				int line = root.getElementIndex(component.getCaretPosition());
				Element lineElement = root.getElement(line);
				int start = lineElement.getStartOffset();
				addHighlight(start);
			}
		});
	}

	/**
	 * Add highlighting
	 * 
	 * @param offset From where highlighting can be added
	 */
	private void addHighlight(int offset)
	{
		try
		{
			lastHighlight = highlighter.addHighlight(offset, offset + 1, this);
		}
		catch (BadLocationException ble)
		{
			System.err.println(ble.getMessage());
		}
	}

	/**
	 * Removes private highlights
	 */
	public void removeHighlight()
	{
		highlighter.removeHighlight(lastHighlight);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.event.CaretListener#caretUpdate(javax.swing.event.CaretEvent)
	 */
	public void caretUpdate(CaretEvent e)
	{
		resetHighlight();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e)
	{
		resetHighlight();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e)
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent e)
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent e)
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e)
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved(MouseEvent e)
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	public void mouseDragged(MouseEvent e)
	{
		removeHighlight();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased(KeyEvent e)
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed(KeyEvent e)
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped(KeyEvent e)
	{
	}
}
