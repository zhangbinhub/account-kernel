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
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextField;

/**
 * A dialog box that provides a suggestion list according to the input
 * provided
 * 
 * @author Arnab Karmakar
 * @version 1.0
 */
public class JSideSuggestDialog extends JDialog
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3454815448018300472L;

	// constants
	public static final int ESC = 27;

	public static final int ENTER = 10;

	public static final int BACKSPACE = 8;

	public static final int DELETE = 127;

	public static final int UP = 38;

	public static final int DOWN = 40;

	// application variables
	private JTextField txtInput;

	private StringBuffer stringToSearch;

	private JSideListWindow listWindow;

	private JSideListLoader listLoader;

	private String[] searchableList;

	private String selectedItem;

	/**
	 * Constructor
	 * 
	 * @param parent A parent component
	 * @param title Title of the dialog box
	 * @param searchableList A complete list of items, that can be
	 *            searched/suggested
	 * @throws HeadlessException
	 */
	public JSideSuggestDialog(JFrame parent, String title,
			String[] searchableList) throws HeadlessException
	{
		super(parent, title, true);
		this.searchableList = searchableList;

		initComponents();
		addListeners();
		layoutComponents();
	}

	private void initComponents()
	{
		txtInput = new JTextField(30);
		txtInput.setBackground(Color.LIGHT_GRAY);
		stringToSearch = new StringBuffer();
		listLoader = new JSideListLoader(searchableList);
	}

	private void addListeners()
	{
		txtInput.addKeyListener(new java.awt.event.KeyAdapter()
		{
			public void keyPressed(java.awt.event.KeyEvent e)
			{
				int key = (int) e.getKeyChar();
				int code = e.getKeyCode();
				switch (key)
				{
				case ESC:
					if (listWindow != null)
						listWindow.dispose();
					break;
				case ENTER:
					if (listWindow != null && listWindow.getList() != null)
					{
						int selectedIndex = listWindow.getJList()
								.getSelectedIndex();
						selectedItem = listWindow.getList()[selectedIndex];
						listWindow.dispose();
						JSideSuggestDialog.this.dispose();
					}
					break;
				case BACKSPACE:
					if (stringToSearch != null && stringToSearch.length() > 0)
					{
						stringToSearch
								.deleteCharAt(stringToSearch.length() - 1);
						manipulateString();
					}
					break;
				case DELETE:
					if (stringToSearch != null && stringToSearch.length() > 0)
					{
						stringToSearch
								.deleteCharAt(txtInput.getCaretPosition());
						manipulateString();
					}
					break;
				default:
					if (Character.isDefined((char) key))
					{
						stringToSearch.append((char) key);
						manipulateString();
					}
					break;
				}

				switch (code)
				{
				case UP:
					if (listWindow != null && listWindow.getJList() != null)
					{
						int index = 0;
						if (listWindow.getJList().getSelectedIndex() > 0)
						{
							index = listWindow.getJList().getSelectedIndex() - 1;
						}
						else
						{
							index = listWindow.getList().length - 1;
						}
						listWindow.getJList().setSelectedIndex(index);
					}
					break;

				case DOWN:
					if (listWindow != null && listWindow.getJList() != null
							&& listWindow.getList() != null)
					{
						int index = 0;
						if (listWindow.getJList().getSelectedIndex() < listWindow
								.getList().length - 1)
						{
							index = listWindow.getJList().getSelectedIndex() + 1;
						}
						else
						{
							index = 0;
						}
						listWindow.getJList().setSelectedIndex(index);
					}
					break;

				default:
					break;
				}
			}
		});

		this.addComponentListener(new ComponentAdapter()
		{
			public void componentMoved(ComponentEvent arg0)
			{
				if (listWindow != null)
				{
					Point point = JSideSuggestDialog.this.getLocation();
					listWindow.setLocation(point.x, point.y
							+ getTxtInput().getHeight() + 5);
				}
			}
		});
	}

	private void layoutComponents()
	{
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(txtInput, BorderLayout.CENTER);
		this.setSize(200, 50);
		Point positionParent = this.getParent().getLocation();
		this.setLocation((int) positionParent.getX() + 300,
				(int) positionParent.getY() + 180);
	}

	private void manipulateString()
	{
		String list[] = listLoader.searchList(stringToSearch.toString());

		if (list != null && list.length > 0)
		{
			txtInput.setForeground(Color.BLACK);

			if (listWindow != null)
				listWindow.dispose();
			initializeFileWindow(list, 0);
		}
		else
		{
			txtInput.setForeground(Color.RED);
			if (listWindow != null)
				listWindow.dispose();
		}
		if (stringToSearch.length() == 0)
			txtInput.setForeground(Color.BLACK);
	}

	private void initializeFileWindow(String[] list, int listIndexToSet)
	{
		listWindow = new JSideListWindow(JSideSuggestDialog.this, list);
		listWindow.setListIndexToSet(listIndexToSet);
		listWindow.setVisible(true);
		listWindow.getParent().transferFocusUpCycle();
	}

	JTextField getTxtInput()
	{
		return txtInput;
	}

	public String start()
	{
		this.setVisible(true);
		return selectedItem;
	}
}
