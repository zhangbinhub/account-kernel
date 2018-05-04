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
import java.util.HashMap;
import java.util.Vector;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

/**
 * A styled document that performs syntax coloring and brace matching on
 * the text pane
 * 
 * @author Adeel
 */
public class CodeDocument extends DefaultStyledDocument
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6970995237896186047L;

	// syntax highlighting variables
	private String word;

	private SimpleAttributeSet keyword;

	private SimpleAttributeSet string;

	private SimpleAttributeSet normal;

	private SimpleAttributeSet number;

	private SimpleAttributeSet comments;

	private int currentPos = 0;

	private Vector<String> keywords;

	public static final int STRING_MODE = 10;

	public static final int TEXT_MODE = 11;

	public static final int NUMBER_MODE = 12;

	public static final int COMMENT_MODE = 13;

	private static int mode = TEXT_MODE;

	// brace matching variables
	private int braceIndex1;

	private int braceIndex2;

	private Style braceStyle;

	private Style normalStyle;

	private static final char[] BRACES = { '(', ')', '{', '}', '[', ']' };

	/**
	 * Constructor
	 */
	public CodeDocument(boolean codeStyling, HashMap<String, Color> colorsMap, Vector<String> keywords)
	{
		if (codeStyling)
		{
			initSyntaxColoring(colorsMap, keywords);
			initBraceMatching(colorsMap);
		}
	}

	/**
	 * Initializes variables and sets syntax coloring style attributes
	 */
	private void initSyntaxColoring(HashMap<String, Color> colorsMap, Vector<String> keywords)
	{
		// initialize variables
		this.word = "";
		this.keyword = new SimpleAttributeSet();
		this.string = new SimpleAttributeSet();
		this.normal = new SimpleAttributeSet();
		this.number = new SimpleAttributeSet();
		this.comments = new SimpleAttributeSet();
		this.keywords = keywords;

		// set syntax highlighting style attributes
		StyleConstants.setForeground(normal, colorsMap
				.get("normalColor"));
		StyleConstants.setBold(keyword, true);
		StyleConstants.setForeground(keyword, colorsMap
				.get("keywordsColor"));
		StyleConstants.setForeground(string, colorsMap
				.get("stringColor"));
		StyleConstants.setForeground(number, colorsMap
				.get("numbersColor"));
		StyleConstants.setForeground(comments, colorsMap
				.get("commentsColor"));
		StyleConstants.setItalic(comments, true);
	}

	/**
	 * Initializes variables and sets brace matchin style attributes
	 */
	private void initBraceMatching(HashMap<String, Color> colorsMap)
	{
		// set brace matching style attributes
		normalStyle = addStyle("normal", null);
		StyleConstants.setForeground(normalStyle, Color.BLACK);
		braceStyle = addStyle("bracket-highlight", null);
		StyleConstants.setForeground(braceStyle, Color.BLUE.darker());
		StyleConstants.setBackground(braceStyle, colorsMap
				.get("braceMatchingColor"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.text.Document#insertString(int, java.lang.String,
	 *      javax.swing.text.AttributeSet)
	 */
	public void insertString(int offs, String str, AttributeSet a)
			throws BadLocationException
	{
		resetBracePosition();
		super.insertString(offs, str, normal);
		updateBraces(offs + 1);

		int strLen = str.length();
		int endpos = offs + strLen;
		int strpos;
		for (int i = offs; i < endpos; i++)
		{
			currentPos = i;
			strpos = i - offs;
			processChar(str.charAt(strpos));
		}
		currentPos = offs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.text.Document#remove(int, int)
	 */
	public void remove(int offset, int length) throws BadLocationException
	{
		resetBracePosition();
		super.remove(offset, length);
		if (offset > 0)
		{
			updateBraces(offset);
		}
	}

	// ////////////////////////////////////////////////////////////////////////
	// Syntax highlighting specific methods
	// ////////////////////////////////////////////////////////////////////////

	private void insertKeyword(String str, int pos)
	{
		try
		{
			// remove the old word and formatting
			this.remove(pos - str.length(), str.length());
			// replace it with the same word, but new formatting we MUST
			// call the super class insertString method here, otherwise we
			// would end up in an infinite loop
			super.insertString(pos - str.length(), str, keyword);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private void insertTextString(String str, int pos)
	{
		try
		{
			// remove the old word and formatting
			this.remove(pos, str.length());
			super.insertString(pos, str, string);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private void insertNumberString(String str, int pos)
	{
		try
		{
			// remove the old word and formatting
			this.remove(pos, str.length());
			super.insertString(pos, str, number);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private void insertCommentString(String str, int pos)
	{
		try
		{
			// remove the old word and formatting
			this.remove(pos, str.length());
			super.insertString(pos, str, comments);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private void checkForString()
	{
		int offs = this.currentPos;
		Element element = this.getParagraphElement(offs);
		String elementText = "";
		try
		{
			// this gets our chuck of current text for the element
			// we're on
			elementText = this.getText(element.getStartOffset(), element
					.getEndOffset()
					- element.getStartOffset());
		}
		catch (Exception ex)
		{
			 ex.printStackTrace();
		}
		int strLen = elementText.length();
		if (strLen == 0)
		{
			return;
		}
		int i = 0;

		if (element.getStartOffset() > 0)
		{
			// translates backward if neccessary
			offs = offs - element.getStartOffset();
		}
		int quoteCount = 0;
		if ((offs >= 0) && (offs <= strLen - 1))
		{
			i = offs;
			while (i > 0)
			{
				// the while loop walks back until we hit a delimiter

				char charAt = elementText.charAt(i);
				if ((charAt == '"'))
				{
					quoteCount++;
				}
				i--;
			}
			int rem = quoteCount % 2;
			mode = (rem == 0) ? TEXT_MODE : STRING_MODE;
		}
	}

	private void checkForKeyword()
	{
		if (mode != TEXT_MODE)
		{
			return;
		}
		int offs = this.currentPos;
		Element element = this.getParagraphElement(offs);
		String elementText = "";
		try
		{
			// this gets our chuck of current text for the element
			// we're on
			elementText = this.getText(element.getStartOffset(), element
					.getEndOffset()
					- element.getStartOffset());
		}
		catch (Exception ex)
		{
			System.err.println("no text");
		}
		int strLen = elementText.length();
		if (strLen == 0)
		{
			return;
		}
		int i = 0;

		if (element.getStartOffset() > 0)
		{
			// translates backward if neccessary
			offs = offs - element.getStartOffset();
		}
		if ((offs >= 0) && (offs <= strLen - 1))
		{
			i = offs;
			while (i > 0)
			{
				// the while loop walks back until we hit a delimiter
				i--;
				char charAt = elementText.charAt(i);
				if ((charAt == ' ') | (i == 0) | (charAt == '(')
						| (charAt == ')') | (charAt == '{') | (charAt == '}'))
				{ // if i == 0 then we're at the begininng
					if (i != 0)
					{
						i++;
					}
					word = elementText.substring(i, offs);// skip the
					// period

					String s = word.trim().toLowerCase();
					// this is what actually checks for a matching
					// keyword
					if (keywords.contains(s))
					{
						insertKeyword(word, currentPos);
					}
					break;
				}
			}
		}
	}

	private void checkForNumber()
	{
		int offs = this.currentPos;
		Element element = this.getParagraphElement(offs);
		String elementText = "";
		try
		{
			// this gets our chuck of current text for the element
			// we're on
			elementText = this.getText(element.getStartOffset(), element
					.getEndOffset()
					- element.getStartOffset());
		}
		catch (Exception ex)
		{
			System.err.println("no text");
		}
		int strLen = elementText.length();
		if (strLen == 0)
		{
			return;
		}
		int i = 0;

		if (element.getStartOffset() > 0)
		{
			// translates backward if neccessary
			offs = offs - element.getStartOffset();
		}
		mode = TEXT_MODE;
		if ((offs >= 0) && (offs <= strLen - 1))
		{
			i = offs;
			while (i > 0)
			{
				// the while loop walks back until we hit a delimiter
				char charAt = elementText.charAt(i);
				if ((charAt == ' ') | (i == 0) | (charAt == '(')
						| (charAt == ')') | (charAt == '{') | (charAt == '}') /* | */)
				{ // if i == 0 then we're at the begininng
					if (i != 0)
					{
						i++;
					}
					mode = NUMBER_MODE;
					break;
				}
				else if (!(charAt >= '0' & charAt <= '9' | charAt == '.'
						| charAt == '+' | charAt == '-' | charAt == '/'
						| charAt == '*' | charAt == '%' | charAt == '='))
				{
					mode = TEXT_MODE;
					break;
				}
				i--;
			}
		}
	}

	private void checkForComment()
	{
		int offs = this.currentPos;
		Element element = this.getParagraphElement(offs);
		String elementText = "";
		try
		{
			// this gets our chuck of current text for the element
			// we're on
			elementText = this.getText(element.getStartOffset(), element
					.getEndOffset()
					- element.getStartOffset());
		}
		catch (Exception ex)
		{
			System.err.println("no text");
		}
		int strLen = elementText.length();
		if (strLen == 0)
		{
			return;
		}
		int i = 0;

		if (element.getStartOffset() > 0)
		{
			// translates backward if neccessary
			offs = offs - element.getStartOffset();
		}
		if ((offs >= 1) && (offs <= strLen - 1))
		{
			i = offs;
			char commentStartChar1 = elementText.charAt(i - 1);
			char commentStartChar2 = elementText.charAt(i);
			if (commentStartChar1 == '/' && commentStartChar2 == '*')
			{
				mode = COMMENT_MODE;
				this.insertCommentString("/*", currentPos - 1);
			}
			else if (commentStartChar1 == '*' && commentStartChar2 == '/')
			{
				mode = TEXT_MODE;
				this.insertCommentString("*/", currentPos - 1);
			}
		}
	}

	private void processChar(String str)
	{
		char strChar = str.charAt(0);
		if (mode != COMMENT_MODE)
		{
			mode = TEXT_MODE;
		}
		switch (strChar)
		{
		case ('{'):
		case ('}'):
		case (' '):
		case ('\n'):
		case ('('):
		case (')'):
		case (';'):
		case ('.'):
		{
			checkForKeyword();
			if (mode == STRING_MODE && strChar == '\n')
			{
				mode = TEXT_MODE;
			}
		}
			break;
		case ('"'):
		{
			insertTextString(str, currentPos);
			this.checkForString();
		}
			break;
		case ('0'):
		case ('1'):
		case ('2'):
		case ('3'):
		case ('4'):
		case ('5'):
		case ('6'):
		case ('7'):
		case ('8'):
		case ('9'):
		{
			checkForNumber();
		}
			break;
		case ('*'):
		case ('/'):
		{
			checkForComment();
		}
			break;
		}
		if (mode == TEXT_MODE)
		{
			this.checkForString();
		}
		if (mode == STRING_MODE)
		{
			insertTextString(str, this.currentPos);
		}
		else if (mode == NUMBER_MODE)
		{
			insertNumberString(str, this.currentPos);
		}
		else if (mode == COMMENT_MODE)
		{
			insertCommentString(str, this.currentPos);
		}

	}

	private void processChar(char strChar)
	{
		char[] chrstr = new char[1];
		chrstr[0] = strChar;
		String str = String.valueOf(chrstr);
		processChar(str);
	}

	// ////////////////////////////////////////////////////////////////////////
	// Brace matching specific methods
	// ////////////////////////////////////////////////////////////////////////

	private void resetBracePosition()
	{
		if (braceIndex1 == -1 && braceIndex2 == -1)
		{
			return;
		}
		applyBraceHiglight(false, braceIndex1, braceIndex2);
		braceIndex1 = -1;
		braceIndex2 = -1;
	}

	private void applyBraceHiglight(boolean apply, int index1, int index2)
	{
		Style style = apply ? braceStyle : normalStyle;
		if (index1 != -1)
		{
			setCharacterAttributes(index1, 1, style, !apply);
		}
		if (index2 != -1)
		{
			setCharacterAttributes(index2, 1, style, !apply);
		}
	}

	private void updateBraces(int offset)
	{
		try
		{
			int length = getLength();
			String text = getText(0, length);
			char charAtOffset = 0;
			char charBeforeOffset = 0;

			if (offset > 0)
			{
				charBeforeOffset = text.charAt(offset - 1);
			}

			if (offset < length)
			{
				charAtOffset = text.charAt(offset);
			}

			int matchOffset = -1;
			if (isBrace(charAtOffset))
			{
				matchOffset = getMatchingBraceOffset(offset, charAtOffset, text);
			}
			else if (isBrace(charBeforeOffset))
			{
				offset--;
				matchOffset = getMatchingBraceOffset(offset, charBeforeOffset,
						text);
			}

			if (matchOffset != -1)
			{
				braceIndex1 = offset;
				braceIndex2 = matchOffset;
				applyBraceHiglight(true, offset, matchOffset);
			}

		}
		catch (BadLocationException e)
		{
			throw new Error(e);
		}
	}

	private int getMatchingBraceOffset(int offset, char brace, String text)
	{
		int thisBraceCount = 0;
		int matchingBraceCount = 0;
		char braceMatch = getMatchingBrace(brace);
		char[] chars = text.toCharArray();

		if (isOpenBrace(brace))
		{

			for (int i = offset; i < chars.length; i++)
			{
				if (chars[i] == brace)
				{
					thisBraceCount++;
				}
				else if (chars[i] == braceMatch)
				{
					matchingBraceCount++;
				}

				if (thisBraceCount == matchingBraceCount)
				{
					return i;
				}
			}

		}
		else
		{

			for (int i = offset; i >= 0; i--)
			{
				if (chars[i] == brace)
				{
					thisBraceCount++;
				}
				else if (chars[i] == braceMatch)
				{
					matchingBraceCount++;
				}

				if (thisBraceCount == matchingBraceCount)
				{
					return i;
				}
			}

		}
		return -1;
	}

	private char getMatchingBrace(char brace)
	{
		switch (brace)
		{
		case '(':
			return ')';
		case ')':
			return '(';
		case '[':
			return ']';
		case ']':
			return '[';
		case '{':
			return '}';
		case '}':
			return '{';
		default:
			return 0;
		}
	}

	private boolean isOpenBrace(char brace)
	{
		switch (brace)
		{
		case '(':
		case '[':
		case '{':
			return true;
		}
		return false;
	}

	private boolean isBrace(char charAt)
	{
		for (int i = 0; i < BRACES.length; i++)
		{
			if (charAt == BRACES[i])
			{
				return true;
			}
		}
		return false;
	}
}
