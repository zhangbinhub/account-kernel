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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

/**
 * A text component that supports syntax highlighting, brace matching, line
 * numbers and line highlighting
 * 
 * @author Adeel Javed
 */
public class JSideTextPane extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9048998273905213813L;

	// application variables
	private JTextPane textPane;

	private JScrollPane scrollPane;

	private LineNumbers lineNumber;

	private CodeDocument codeDocument;

	private Properties preferences;

	/**
	 * Constructor
	 */
	public JSideTextPane() {
		initComponents();
		layoutComponents();
		addListeners();
	}

	/**
	 * Adds listeners
	 */
	private void addListeners() {
		// empty block
	}

	/**
	 * Initializes components
	 */
	private void initComponents() {
		// load preferences from file
		try {
			preferences = new Properties();

			// preferences.loadFromXML(new FileInputStream(this.getClass()
			// .getResource("preferences.xml").getFile()));

			preferences.load(this.getClass().getResourceAsStream("preferences.xml"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		// turn line wrapping off
		textPane = new JTextPane() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -7728501786812688270L;

			public void setSize(Dimension d) {
				if (d.width < getParent().getSize().width)
					d.width = getParent().getSize().width;

				super.setSize(d);
			}

			public boolean getScrollableTracksViewportWidth() {
				return false;
			}
		};

		// set font of the component
		String fontType = preferences.getProperty("font.type");
		int fontSize = Integer.parseInt(preferences.getProperty("font.size"));
		Font font = new Font(fontType, Font.PLAIN, fontSize);
		textPane.setFont(font);

		scrollPane = new JScrollPane(textPane);

		initLineNumbers();
		initCodeStyling();
		initLineHighlighter();
	}

	/**
	 * Layout components
	 */
	private void layoutComponents() {
		this.setLayout(new BorderLayout());

		scrollPane.setOpaque(true);
		scrollPane.setBackground(textPane.getBackground());

		this.add(scrollPane, BorderLayout.CENTER);
	}

	/**
	 * Initializes line numbers
	 */
	private void initLineNumbers() {
		boolean lineNumbers = Boolean.valueOf(preferences.getProperty("lineNumbers.enable")).booleanValue();
		if (lineNumbers) {
			int r = 0, g = 0, b = 0;
			r = Integer.parseInt(preferences.getProperty("lineNumbers.bgcolor.r"));
			g = Integer.parseInt(preferences.getProperty("lineNumbers.bgcolor.g"));
			b = Integer.parseInt(preferences.getProperty("lineNumbers.bgcolor.b"));
			Color bgColor = new Color(r, g, b);
			r = Integer.parseInt(preferences.getProperty("lineNumbers.fgcolor.r"));
			g = Integer.parseInt(preferences.getProperty("lineNumbers.fgcolor.g"));
			b = Integer.parseInt(preferences.getProperty("lineNumbers.fgcolor.b"));
			Color fgColor = new Color(r, g, b);

			lineNumber = new LineNumbers(textPane, bgColor, fgColor);
			lineNumber.setPreferredSize(99999);
			scrollPane.setRowHeaderView(lineNumber);
		}
	}

	/**
	 * Initializes code styling which includes syntax coloring and brace
	 * matching
	 */
	private void initCodeStyling() {
		boolean codeStyling = Boolean.valueOf(preferences.getProperty("codeStyling.enable")).booleanValue();
		HashMap<String, Color> colorsMap = new HashMap<String, Color>();
		Vector<String> keywords = null;

		// perform this only if code styling syntax coloring is enabled
		if (codeStyling) {
			int r = 0, g = 0, b = 0;

			// load colors from preferences file and store them in the
			// colors map
			r = Integer.parseInt(preferences.getProperty("normal.color.r"));
			g = Integer.parseInt(preferences.getProperty("normal.color.g"));
			b = Integer.parseInt(preferences.getProperty("normal.color.b"));
			colorsMap.put("normalColor", new Color(r, g, b));
			r = Integer.parseInt(preferences.getProperty("numbers.color.r"));
			g = Integer.parseInt(preferences.getProperty("numbers.color.g"));
			b = Integer.parseInt(preferences.getProperty("numbers.color.b"));
			colorsMap.put("numbersColor", new Color(r, g, b));
			r = Integer.parseInt(preferences.getProperty("string.color.r"));
			g = Integer.parseInt(preferences.getProperty("string.color.g"));
			b = Integer.parseInt(preferences.getProperty("string.color.b"));
			colorsMap.put("stringColor", new Color(r, g, b));
			r = Integer.parseInt(preferences.getProperty("keywords.color.r"));
			g = Integer.parseInt(preferences.getProperty("keywords.color.g"));
			b = Integer.parseInt(preferences.getProperty("keywords.color.b"));
			colorsMap.put("keywordsColor", new Color(r, g, b));
			r = Integer.parseInt(preferences.getProperty("comments.color.r"));
			g = Integer.parseInt(preferences.getProperty("comments.color.g"));
			b = Integer.parseInt(preferences.getProperty("comments.color.b"));
			colorsMap.put("commentsColor", new Color(r, g, b));
			r = Integer.parseInt(preferences.getProperty("braceMatching.color.r"));
			g = Integer.parseInt(preferences.getProperty("braceMatching.color.g"));
			b = Integer.parseInt(preferences.getProperty("braceMatching.color.b"));
			colorsMap.put("braceMatchingColor", new Color(r, g, b));

			// keywords are specified in the preferences file as comma
			// separated, so after getting them from the preferences file
			// the property is tokenized and added to a keywords vector
			keywords = new Vector<String>();
			StringTokenizer tokenizer = new StringTokenizer(preferences.getProperty("keywords"), ";");
			while (tokenizer.hasMoreTokens())
				keywords.addElement(tokenizer.nextToken());

			codeDocument = new CodeDocument(codeStyling, colorsMap, keywords);
			textPane.setDocument(codeDocument);
		}
	}

	/**
	 * Initializes line highlighter
	 */
	private void initLineHighlighter() {
		boolean lineHighlighting = Boolean.valueOf(preferences.getProperty("lineHighlighting.enable")).booleanValue();

		if (lineHighlighting) {
			int r = 0, g = 0, b = 0;
			r = Integer.parseInt(preferences.getProperty("lineHighlight.color.r"));
			g = Integer.parseInt(preferences.getProperty("lineHighlight.color.g"));
			b = Integer.parseInt(preferences.getProperty("lineHighlight.color.b"));
			new LineHighlighter(textPane, new Color(r, g, b));
		}
	}

	// ////////////////////////////////////////////////////////////////////////
	// User preferences and accessible methods
	// ////////////////////////////////////////////////////////////////////////

	public void setText(String text) {
		this.textPane.setText(text);
	}

	public String getText() {
		return this.textPane.getText();
	}

	public void cut() {
		this.textPane.cut();
	}

	public void copy() {
		this.textPane.copy();
	}

	public void paste() {
		this.textPane.paste();
	}

	public void selectAll() {
		this.textPane.selectAll();
	}
}
