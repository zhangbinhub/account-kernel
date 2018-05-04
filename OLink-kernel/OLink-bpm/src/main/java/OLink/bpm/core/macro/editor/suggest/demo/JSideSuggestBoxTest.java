package OLink.bpm.core.macro.editor.suggest.demo;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;



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

/**
 * JSideSuggestBox test class
 * 
 * @author Arnab Karmakar
 * @version 1.0
 */
public class JSideSuggestBoxTest extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8825790310588795967L;
	String[] list = { "Adeel Javed", "Arnab Karmakar", "Jatin Naik",
			"Atul Kumar Singh", "Suman Mazumder", "Magnus Poromaa",
			"Jaber Hashemi Asl", "Aguimar Ribeiro Jr", "Greg Simons" };

	public JSideSuggestBoxTest()
	{
		super("JSideSuggestBox Test");

		JButton btnShow = new JButton("Show Suggest Box");
		btnShow.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
//				JSideSuggestDialog suggestBox = new JSideSuggestDialog(
//						JSideSuggestBoxTest.this, "JSide Developers List", list);
			}
		});
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(btnShow, BorderLayout.CENTER);

		this.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent arg0)
			{
				super.windowClosing(arg0);
				System.exit(0);
			}
		});

		this.pack();
		this.setVisible(true);
	}

	/**
	 * Execution begins from here
	 * 
	 * @param args Arguments passed by JVM
	 */
	public static void main(String[] args)
	{
		new JSideSuggestBoxTest();
	}
}
