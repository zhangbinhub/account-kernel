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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Window;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JWindow;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * List window implementation
 * 
 * @author Arnab Karmakar
 * @version 1.0
 */
class JSideListWindow extends JWindow
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 138418500483069008L;

	// application variables
	private JPanel panel;

	private JList list;

	private JScrollPane scrollPane;

	private String[] listData;

	private int listIndexToSet;

	/**
	 * Contructor
	 * 
	 * @param window Parent window, this is used for drawing list relative
	 *            to the parent window
	 * @param data Data for the list
	 */
	JSideListWindow(Window window, String[] data)
	{
		super(window);
		listData = data;

		initComponents();
		addListeners();
		layoutComponents();
	}

	private void initComponents()
	{
		panel = new JPanel();
		scrollPane = new JScrollPane();
		list = new JList();
		listIndexToSet = 0;

		if (listData != null && listData.length > 0)
		{
			list.setListData(listData);
			list.setCellRenderer(new JSideListCellRenderer());
			list.setSelectedIndex(listIndexToSet);
			list.ensureIndexIsVisible(listIndexToSet);
			if (listData.length == 1)
				list.setVisibleRowCount(2);
			else if (listData.length < 8)
				list.setVisibleRowCount(listData.length);
		}
		list.setBackground(Color.LIGHT_GRAY);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	private void addListeners()
	{
		list.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent arg0)
			{
				listIndexToSet = list.getSelectedIndex();
				list.ensureIndexIsVisible(listIndexToSet);
			}

		});

		list.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent arg0)
			{
				if (arg0.getClickCount() >= 2)
				{
					// creates enter event
					KeyEvent adapter = new KeyEvent(
							((JSideSuggestDialog) JSideListWindow.this
									.getParent()).getTxtInput(), 401,
							1143636549811L, 0, 10,
							(char) JSideSuggestDialog.ENTER);
					((JSideSuggestDialog) JSideListWindow.this.getParent())
							.getTxtInput().getKeyListeners()[0]
							.keyPressed(adapter);
				}
			}

		});
		list.addFocusListener(new FocusAdapter()
		{
			public void focusGained(FocusEvent arg0)
			{
				JSideListWindow.this.getParent().transferFocusUpCycle();
			}

		});
	}
	
	private void layoutComponents()
	{
		panel.setLayout(new BorderLayout());
		scrollPane.getViewport().setView(list);
		panel.add(scrollPane, BorderLayout.CENTER);
		this.setContentPane(panel);

		this.setLocation((int) this.getParent().getLocation().getX(),
				(int) this.getParent().getLocation().getY()
						+ this.getParent().getHeight());
		this.pack();
	}

	void setListIndexToSet(int listIndexToSet)
	{
		this.listIndexToSet = listIndexToSet;
	}

	String[] getList()
	{
		return listData;
	}

	JList getJList()
	{
		return list;
	}
}
