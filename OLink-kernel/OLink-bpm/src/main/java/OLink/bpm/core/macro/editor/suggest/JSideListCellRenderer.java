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
package OLink.bpm.core.macro.editor.suggest;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * ListCellRenderer implementation
 * 
 * @author Arnab Karmakar
 * @version 1.0
 */
class JSideListCellRenderer extends JLabel implements ListCellRenderer
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4684059173914006048L;

	/**
	 * Constructor
	 */
	JSideListCellRenderer()
	{
		setOpaque(true);
	}

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus)
	{
		setText(value.toString());
		setBackground(isSelected ? Color.BLUE : Color.LIGHT_GRAY);
		setForeground(isSelected ? Color.white : Color.black);
		return this;
	}
}
