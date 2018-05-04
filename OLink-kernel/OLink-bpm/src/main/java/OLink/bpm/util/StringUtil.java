package OLink.bpm.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UTFDataFormatException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * The string utility.
 */
public class StringUtil {
	private static Logger log = Logger.getLogger(StringUtil.class);

	public static String toString(int[] codePoints, int offset, int count) {
		if (offset < 0) {
			throw new StringIndexOutOfBoundsException(offset);
		}
		if (count < 0) {
			throw new StringIndexOutOfBoundsException(count);
		}
		// Note: offset or count might be near -1>>>1.
		if (offset > codePoints.length - count) {
			throw new StringIndexOutOfBoundsException(offset + count);
		}

		int expansion = 0;
		int margin = 1;
		char[] v = new char[count + margin];
		int x = offset;
		int j = 0;
		for (int i = 0; i < count; i++) {
			int c = codePoints[x++];
			if (c < 0) {
				throw new IllegalArgumentException();
			}
			if (margin <= 0 && (j + 1) >= v.length) {
				if (expansion == 0) {
					expansion = (((-margin + 1) * count) << 10) / i;
					expansion >>= 10;
					if (expansion <= 0) {
						expansion = 1;
					}
				} else {
					expansion *= 2;
				}
				int newLen = Math.min(v.length + expansion, count * 2);
				margin = (newLen - v.length) - (count - i);

				char[] copy = new char[newLen];
				System.arraycopy(v, 0, copy, 0, Math.min(v.length, newLen));
				v = copy;
			}
			if (c < 0x010000) {
				v[j++] = (char) c;
			} else if (c <= 0x10ffff) {

				// Character.toSurrogates(c, v, j);
				int charOffset = c - 0x010000;
				v[j + 1] = (char) ((charOffset & 0x3ff) + '\uDC00');
				v[j] = (char) ((charOffset >>> 10) + '\uD800');

				j += 2;
				margin--;
			} else {
				throw new IllegalArgumentException();
			}
		}

		return new String(v, 0, j);
	}

	/**
	 * Retrieve how many times is the substring in the larger string. Null
	 * returns 0.
	 * 
	 * @param s
	 *            the string to check
	 * @param sb
	 *            the substring to count
	 * @return the number of occurances, 0 if the string is null
	 */
	public static int countMatches(String s, String sb) {
		if (s == null || sb == null) {
			return 0;
		}
		int count = 0;
		int idx = 0;
		while ((idx = s.indexOf(sb, idx)) != -1) {
			count++;
			idx += sb.length();
		}
		return count;
	}

	/**
	 * Gets the leftmost n characters of a string. If n characters are not
	 * available, or the string is null, the string will be returned without an
	 * exception.
	 * 
	 * @param s
	 *            The string to get the leftmost characters from
	 * @param len
	 *            The length of the required string
	 * @return The leftmost characters
	 */
	public static String left(String s, int len) {
		if (len < 0)
			throw new IllegalArgumentException("Requested String length " + len + " is less than zero");
		return ((s == null) || (s.length() <= len)) ? s : s.substring(0, len);
	}

	/**
	 * Gets the rightmost n characters of a string. If n characters are not
	 * available, or the string is null, the string will be returned without an
	 * exception.
	 * 
	 * @param s
	 *            The string to get the rightmost characters from
	 * @param len
	 *            The length of the required string
	 * @return The leftmost characters
	 */
	public static String right(String s, int len) {
		if (len < 0)
			throw new IllegalArgumentException("Requested String length " + len + " is less than zero");
		return ((s == null) || (s.length() <= len)) ? s : s.substring(s.length() - len);
	}

	/**
	 * Repeat a string n times to form a new string.
	 * 
	 * @param s
	 *            String to repeat
	 * @param t
	 *            The times of the string to repeat
	 * @return The new string after repeat
	 */
	public static String repeat(String s, int t) {
		StringBuffer buffer = new StringBuffer(t * s.length());

		for (int i = 0; i < t; i++)
			buffer.append(s);

		return buffer.toString();
	}

	/**
	 * Right pad a String with spaces. Pad to a size of n.
	 * 
	 * @param s
	 *            String to repeat
	 * @param z
	 *            int number of times to repeat
	 * @return right padded String
	 */
	public static String rightPad(String s, int z) {
		return rightPad(s, z, " ");
	}

	/**
	 * Right pad a String with a specified string. Pad to a size of n.
	 * 
	 * @param s
	 *            The string to pad out
	 * @param z
	 *            The size to pad to
	 * @param d
	 *            The string to pad with
	 * @return The right padded String
	 */
	public static String rightPad(String s, int z, String d) {
		z = (z - s.length()) / d.length();
		if (z > 0)
			s += repeat(d, z);
		return s;
	}

	/**
	 * Left pad a String with spaces. Pad to a size of n.
	 * 
	 * @param s
	 *            The String to pad out
	 * @param z
	 *            The size to pad to
	 * @return The left padded String
	 */
	public static String leftPad(String s, int z) {
		return leftPad(s, z, " ");
	}

	/**
	 * Left pad a String with a specified string. Pad to a size of n.
	 * 
	 * @param s
	 *            The String to pad out
	 * @param z
	 *            The size to pad to
	 * @param d
	 *            String to pad with
	 * @return left padded String
	 */
	public static String leftPad(String s, int z, String d) {
		z = (z - s.length()) / d.length();
		if (z > 0)
			s = repeat(d, z) + s;
		return s;
	}

	/**
	 * Replace a string with another string inside a larger string, once.
	 * 
	 * @see #replace(String text, String repl, String with, int max)
	 * @param text
	 *            text to search and replace in
	 * @param repl
	 *            The string to search for
	 * @param with
	 *            The String to replace with
	 * @return The text with any replacements processed
	 */
	public static String replaceOnce(String text, String repl, String with) {
		return replace(text, repl, with, 1);
	}

	/**
	 * Replace all occurances of a string within another string.
	 * 
	 * @see #replace(String text, String repl, String with, int max)
	 * @param text
	 *            The text to search and replace in
	 * @param repl
	 *            The String to search for
	 * @param with
	 *            THE String to replace with
	 * @return The text with any replacements processed
	 */
	public static String replace(String text, String repl, String with) {
		return replace(text, repl, with, -1);
	}

	/**
	 * Replace a string with another string inside a larger string, for the
	 * first <code>max</code> values of the search string. A <code>null</code>
	 * reference is passed to this method is a no-op.
	 * 
	 * @param text
	 *            The text to search and replace in
	 * @param repl
	 *            The String to search for
	 * @param with
	 *            The String to replace with
	 * @param max
	 *            The maximum number of values to replace, or <code>-1</code>
	 *            if no maximum
	 * @return The text with any replacements processed
	 */
	public static String replace(String text, String repl, String with, int max) {
		if (text == null)
			return null;

		StringBuffer buf = new StringBuffer(text.length());
		int start = 0, end = 0;

		while ((end = text.indexOf(repl, start)) != -1) {
			buf.append(text.substring(start, end)).append(with);
			start = end + repl.length();

			if (--max == 0) {
				break;
			}
		}

		buf.append(text.substring(start));
		return buf.toString();
	}

	/**
	 * Splits the provided text into a list, using whitespace as the separator.
	 * The separator is not included in the returned String array.
	 * 
	 * @param str
	 *            The string to parse
	 * @return An array of parsed Strings
	 * @see #split(String, String, int)
	 */
	public static String[] split(String str) {
		return split(str, null, -1);
	}

	public static String[] splitString(String str, String separator) {
		return str.split(separator);//Modify XGY
//		return str.split(separator,-1);
	}

	/**
	 * Splits the provided text into a list, using whitespace as the separator.
	 * The separator is not included in the returned String array.
	 * 
	 * @param text
	 *            The string to parse
	 * @param separator
	 *            The Characters used as the delimiters.
	 * @return An array of parsed Strings
	 * @see #split(String, String, int)
	 */
	public static String[] split(String text, String separator) {
		return split(text, separator, -1);
	}

	/**
	 * Splits the provided text into a list, using whitespace as the separator.
	 * The separator is not included in the returned String array.
	 * 
	 * @param text
	 *            The string to parse
	 * @param separator
	 *            The Characters used as the delimiters.
	 * @return An array of parsed Strings
	 * @see #split(String, String, int)
	 */
	public static String[] split(String text, char separator) {
		return split(text, String.valueOf(separator));
	}

	/**
	 * Splits the provided text into a list, based on a given separator. The
	 * separator is not included in the returned String array. The maximum
	 * number of splits to perfom can be controlled. A null separator will cause
	 * parsing to be on whitespace.
	 * <p>
	 * This is useful for quickly splitting a string directly into an array of
	 * tokens, instead of an enum1eration of tokens (as
	 * <code>StringTokenizer</code> does).
	 * 
	 * @param str
	 *            The string to parse.
	 * @param separator
	 *            Characters used as the delimiters. If <code>null</code>,
	 *            splits on whitespace.
	 * @param max
	 *            The maximum number of elements to include in the list. A zero
	 *            or negative value implies no limit.
	 * @return an array of parsed Strings
	 */
	public static String[] split(String str, String separator, int max) {
		StringTokenizer tok = null;
		if (separator == null) {
			tok = new StringTokenizer(str);
		} else {
			tok = new StringTokenizer(str, separator);
		}

		int listSize = tok.countTokens();
		if (max > 0 && listSize > max) {
			listSize = max;
		}

		String[] list = new String[listSize];
		int i = 0;
		int lastTokenBegin = 0;
		int lastTokenEnd = 0;

		while (tok.hasMoreTokens()) {
			if (max > 0 && i == listSize - 1) {
				String endToken = tok.nextToken();
				lastTokenBegin = str.indexOf(endToken, lastTokenEnd);
				list[i] = str.substring(lastTokenBegin);
				break;
			} else {
				list[i] = tok.nextToken();
				lastTokenBegin = str.indexOf(list[i], lastTokenEnd);
				lastTokenEnd = lastTokenBegin + list[i].length();
			}
			i++;
		}
		return list;
	}

	/**
	 * Checks whether the String a valid Java Boolean. Null and blank string
	 * will return false.
	 * 
	 * @param s
	 *            the string to check
	 * @return true if the string is a correctly Boolean
	 */
	static public boolean isBoolean(String s) {
		if (s == null || s.trim().length() == 0)
			return false;
		return Boolean.valueOf(s.trim()).booleanValue();
	}

	/**
	 * Check the string is the date format.
	 * 
	 * @param s
	 *            The string to check
	 * @return true if the string is a correctly date
	 */
	public static boolean isDate(String s) {
		if (s == null || s.trim().length() == 0)
			return false;

		try {
			DateUtil.parseDate(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Check the string is the date time format.
	 * 
	 * @param s
	 *            The string to check
	 * @return true if the string is a correctly date
	 */
	public static boolean isDateTime(String s) {
		if (s == null || s.trim().length() == 0)
			return false;

		try {
			DateUtil.parseDateTime(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Check the string is the time format.
	 * 
	 * @param s
	 *            The string to check
	 * @return true if the string is a correctly date
	 */
	public static boolean isTime(String s) {
		if (s == null || s.trim().length() == 0) {
			return false;
		}

		try {
			DateUtil.parseTime(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Checks whether the string a valid number fromat. Valid numbers include
	 * hexadecimal marked with the "0x" qualifier, scientific notation and
	 * numbers marked with a type qualifier (e.g. 123L). Null and blank string
	 * will return false.
	 * 
	 * @param s
	 *            The string to check
	 * @return true if the string is a correctly formatted number, false
	 *         otherwise.
	 */
	public static boolean isNumber(String s) {
		if ((s == null) || (s.length() == 0))
			return false;

		char[] chars = s.toCharArray();
		int sz = chars.length;
		boolean hasExp = false;
		boolean hasDecPoint = false;
		boolean allowSigns = false;
		boolean foundDigit = false;
		int start = (chars[0] == '-') ? 1 : 0;

		if (sz > start + 1) {
			if (chars[start] == '0' && chars[start + 1] == 'x') {
				int i = start + 2;
				if (i == sz) {
					return false;
				}
				for (; i < chars.length; i++) {
					if ((chars[i] < '0' || chars[i] > '9') && (chars[i] < 'a' || chars[i] > 'f')
							&& (chars[i] < 'A' || chars[i] > 'F')) {
						return false;
					}
				}
				return true;
			}
		}
		sz--;
		int i = start;
		while (i < sz || (i < sz + 1 && allowSigns && !foundDigit)) {
			if (chars[i] >= '0' && chars[i] <= '9') {
				foundDigit = true;
				allowSigns = false;

			} else if (chars[i] == '.') {
				if (hasDecPoint || hasExp) {
					return false;
				}
				hasDecPoint = true;
			} else if (chars[i] == 'e' || chars[i] == 'E') {
				if (hasExp) {
					return false;
				}
				if (!foundDigit) {
					return false;
				}
				hasExp = true;
				allowSigns = true;
			} else if (chars[i] == '+' || chars[i] == '-') {
				if (!allowSigns) {
					return false;
				}
				allowSigns = false;
				foundDigit = false;
			} else {
				return false;
			}
			i++;
		}
		if (i < chars.length) {
			if (chars[i] >= '0' && chars[i] <= '9') {
				return true;
			}
			if (chars[i] == 'e' || chars[i] == 'E') {
				return false;
			}
			if (!allowSigns && (chars[i] == 'd' || chars[i] == 'D' || chars[i] == 'f' || chars[i] == 'F')) {
				return foundDigit;
			}
			if (chars[i] == 'l' || chars[i] == 'L') {
				return foundDigit && !hasExp;
			}
		}

		return allowSigns && !foundDigit;
	}

	/**
	 * 判断是否为空对象或空字符串
	 * 
	 * @param s
	 *            字符串
	 * @return 是否为空
	 */
	public static boolean isBlank(String s) {
		return StringUtils.isBlank(s);
	}

	/**
	 * Unite the string array into one string.
	 * 
	 * @param arr
	 *            The string array
	 * @param sp
	 *            The split tag.
	 * @return The string after unite.
	 */
	public static String unite(String[] arr, String sp) {
		if (arr == null)
			return null;

		if (arr.length == 0)
			return "";

		int i;

		StringBuffer buff = new StringBuffer();
		for (i = 0; i < arr.length; i++) {
			if (isBlank(arr[i])) {
				continue;
			}
			buff = buff.append(arr[i]).append(sp);
		}

		if (buff.lastIndexOf(sp) > 0)
			buff = buff.deleteCharAt(buff.lastIndexOf(sp));

		return buff.toString();
	}

	/**
	 * Unite the string arrary into one string with ";".
	 * 
	 * @param arr
	 *            The string arrary
	 * @returnThe string after unite.
	 */
	public static String unite(String[] arr) {
		return unite(arr, ";");
	}

	/**
	 * Encode the url to UTF format.
	 * 
	 * @param s
	 *            The target URL
	 * @param sp
	 *            the separate taf.
	 * @return The url after encode.
	 */
	public static String chineseURLToUTF(String s, char sp) {
		String filename = (new File(s)).getName();
		String path = (new File(s)).getParent();

		path = path.replace('\\', sp);
		try {
			filename = URLEncoder.encode(filename, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		path = path + "/" + filename;
		return path;
	}

	/**
	 * Encode the string to GB2312
	 * 
	 * @param s
	 *            The Target string
	 * @return The string after encode.
	 */
	public static String toGB2312(String s) throws Exception {
		if (s != null && s.length() > 0) {
			byte[] byteTmp = s.getBytes("ISO8859_1");
			s = new String(byteTmp, "GB2312");
		}
		return s;
	}

	public static String toUTF8(String s, String encoding) throws Exception {
		if (s != null && s.length() > 0) {
			byte[] byteTmp = s.getBytes(encoding);
			s = new String(byteTmp, "UTF-8");
		}
		return s;
	}

	/**
	 * Encode the string to Big5
	 * 
	 * @param s
	 *            The Target string
	 * @return The string after encode.
	 */
	public static String toBig5(String s) throws Exception {
		if (s != null && s.length() > 0) {
			byte[] byteTmp = s.getBytes("BIG5");
			s = new String(byteTmp, "GBK");
		}
		return s;
	}

	/**
	 * Encode the string to Iso 8859.
	 * 
	 * @param s
	 *            The Target string
	 * @return The string after encode.
	 */
	public static String to8859(String s) throws Exception {
		if (s != null && s.length() > 0) {
			byte[] byteTmp = s.getBytes("GB2312");
			s = new String(byteTmp, "8859_1");
		}
		return s;
	}

	/**
	 * Encode the string in html format.
	 * 
	 * @param s
	 *            The string.
	 * @return The string in html format.
	 */
	public static String getHTMLString(String s) {
		if (s == null)
			return ("");
		if (s.equals(""))
			return ("");

		StringBuffer buf = new StringBuffer();
		char ch1 = '\n';
		char ch2 = '\n';

		for (int i = 0; i < s.length(); i++) {
			ch1 = s.charAt(i);

			if ((ch1 == ' ') && ((i + 1) < s.length())) {
				ch2 = s.charAt(i + 1);
				if (ch2 == ' ') {
					buf.append("¡¡");
					i++;
				} else {
					buf.append(ch1);
				}
			} else if (ch1 == '\n') {
				buf.append("<br>");
			} else if (ch1 == '\t') {
				buf.append("¡¡¡¡");
			} else {
				buf.append(ch1);
			}
		}

		return buf.toString();
	}

	/**
	 * Encode the string in html format.
	 * 
	 * @param t
	 *            The target text
	 * @return the string after encode.
	 */
	public static String encodeHTML(String t) {
		if (t != null) {
			t = t.replaceAll("#", "%23");
			t = t.replaceAll("&", "@amp;");
			t = t.replaceAll("\"", "@quot;");
			t = t.replaceAll("<", "@lt;");
			t = t.replaceAll(">", "@gt;");
			t = t.replaceAll("'", "@#146;");
			t = t.replaceAll(" ", "@nbsp;");
			t = t.replaceAll("\r", "&#10;");
			t = t.replaceAll("\n", "&#13;");
		}
		return t;
	}

	/**
	 * Decode the html format.
	 * 
	 * @param t
	 *            The target format.
	 * @return The string after decode.
	 */
	public static String dencodeHTML(String t) {
		if (t != null) {
			t = t.replaceAll("&amp;", "&");
			t = t.replaceAll("@amp;", "&");
			t = t.replaceAll("&quot;", "\"");
			t = t.replaceAll("@quot;", "\"");
			t = t.replaceAll("&lt;", "<");
			t = t.replaceAll("@lt;", "<");
			t = t.replaceAll("&gt;", ">");
			t = t.replaceAll("@gt;", ">");
			t = t.replaceAll("&#146;", "'");
			t = t.replaceAll("@#146;", "'");
			t = t.replaceAll("&nbsp;", " ");
			t = t.replaceAll("@nbsp;", " ");
			t = t.replaceAll("&#10;", "\r");
			t = t.replaceAll("&#13;", "\n");
			t = t.replaceAll("&#9;", " ");
			t = t.replaceAll("%23", "#");

			t = t.replaceAll("\n\r", "\n");
			t = t.replaceAll("\r\n", "\n");
		}
		return t;
	}

	/**
	 * Check whether the speical string include the chinese word.
	 * 
	 * @param str
	 *            The speical string
	 * @return true for include , false otherwise.
	 * @throws Exception
	 */
	public static boolean haveChinesewords(String str) throws Exception {
		return !toGB2312(str).equals(str);
	}

	/**
	 * Get the same sub string between two string.
	 * 
	 * @param s1
	 *            The first string.
	 * @param s2
	 *            The second string.
	 * @return The same sub string
	 */
	public static String getSameString(String s1, String s2) {
		StringBuffer s = new StringBuffer();
		if (s1 == null || s1.trim().length() <= 0 || s2 == null || s2.trim().length() <= 0)
			return s.toString();

		int len = s1.length() > s2.length() ? s2.length() : s1.length();
		char[] c1 = s1.toCharArray();
		char[] c2 = s2.toCharArray();
		for (int i = 0; i < len; i++) {
			if (c1[i] == c2[i]) {
				s.append(c1[i]);
				continue;
			} else {
				break;
			}
		}
		return s.toString();
	}

	public static String getFixLengthString(String str, int length) throws Exception { // ***************
		if (str == null || str.trim().length() < 0)
			return getBlankString(length);
		String reStr = "";
		// 去掉回车
		str = str.replaceAll("\r", "");
		str = str.replaceAll("\n", "");
		str = new String(str.getBytes(), "8859_1");
		if (str.length() >= length) {
			reStr = str.substring(0, length);
		} else {
			reStr = str + getBlankString(length - str.length());
		}
		byte[] bytesStr = reStr.getBytes("8859_1");
		reStr = new String(bytesStr, "gb2312");
		return reStr;
	}

	public static String getBlankString(int count) {
		StringBuffer str = new StringBuffer(0);
		for (int i = 0; i < count; i++) {
			str.append(" ");
		}
		return str.toString();
	}

	/**
	 * 获取String from file
	 * 
	 * @param file
	 * @return
	 * @throws DocumentException
	 * @throws IOException
	 */
	public static String getXmlFileContent(File file) throws DocumentException, IOException {
		SAXReader reader = new SAXReader();
		Document doc = reader.read(file);
		StringWriter writer = new StringWriter();
		XMLWriter xmlWriter = new XMLWriter(writer);
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding(doc.getXMLEncoding());
		xmlWriter.write(doc);

		log.info("Encoding-->" + doc.getXMLEncoding());

		String xmlStr = writer.toString();
		return xmlStr;
	}

	public static String toUTFBody(String str) throws IOException {
		int strlen = str.length();
		int utflen = 0;
		int c, count = 0;

		/* use charAt instead of copying String to char array */
		for (int i = 0; i < strlen; i++) {
			c = str.charAt(i);
			if ((c >= 0x0001) && (c <= 0x007F)) {
				utflen++;
			} else if (c > 0x07FF) {
				utflen += 3;
			} else {
				utflen += 2;
			}
		}

		if (utflen > 65535)
			throw new UTFDataFormatException("encoded string too long: " + utflen + " bytes");

		byte[] bytearr = new byte[utflen];

		// bytearr[count++] = (byte) ((utflen >>> 8) & 0xFF);
		// bytearr[count++] = (byte) ((utflen >>> 0) & 0xFF);

		int i = 0;
		for (i = 0; i < strlen; i++) {
			c = str.charAt(i);
			if (!((c >= 0x0001) && (c <= 0x007F)))
				break;
			bytearr[count++] = (byte) c;
		}

		for (; i < strlen; i++) {
			c = str.charAt(i);
			if ((c >= 0x0001) && (c <= 0x007F)) {
				bytearr[count++] = (byte) c;

			} else if (c > 0x07FF) {
				bytearr[count++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
				bytearr[count++] = (byte) (0x80 | ((c >> 6) & 0x3F));
				bytearr[count++] = (byte) (0x80 | ((c >> 0) & 0x3F));
			} else {
				bytearr[count++] = (byte) (0xC0 | ((c >> 6) & 0x1F));
				bytearr[count++] = (byte) (0x80 | ((c >> 0) & 0x3F));
			}
		}
		// out.write(bytearr, 0, utflen + 2);
		// return utflen + 2;
		return new String(bytearr, "UTF-8");
	}

	/**
	 * @param s
	 * @param enc
	 * @throws UnsupportedEncodingException
	 */
	public static String URLEncode(String s, String enc) throws UnsupportedEncodingException {
		return URLEncoder.encode(s, enc);
	}

	/**
	 * @param s
	 * @param enc
	 * @throws UnsupportedEncodingException
	 */
	public static String URLDecode(String s, String enc) throws UnsupportedEncodingException {
		return URLDecoder.decode(s, enc);
	}

	private static void writeToFile(String fileName, String content) throws IOException {
		BufferedOutputStream outStream = null;
		OutputStreamWriter writer = null;
		try {
			String dirPath = "";
			if (fileName.lastIndexOf("/") != -1) {
				dirPath = fileName.substring(0, fileName.lastIndexOf("/"));
			}
			File dir = new File(dirPath);
			if (!dir.exists()) {
				if(!dir.mkdirs())
					throw new IOException("create directory '" + dirPath + "' failed!");
			}

			outStream = new BufferedOutputStream(new FileOutputStream(fileName, true));
			writer = new OutputStreamWriter(outStream);
			writer.write(content);
		} catch (IOException e) {
			throw e;
		} finally {
			if (writer != null) {
				writer.close();
			}
			if (outStream != null) {
				outStream.close();
			}
		}

	}

	public static void printToFile(String fileName, String content) throws IOException {
		writeToFile(fileName, content);
	}

	public static void printlnToFile(String fileName, String content) throws IOException {
		writeToFile(fileName, content + "\n");
	}

	public static void main(String[] args) {
		try {
			// URLDecoder.decode("%E8%A1%A8%E5%8D%95", "UTF-8"));
			int index = 12;
			int flag = 0;
			StringBuffer buffer = new StringBuffer();

			for (int i = 0; i < index; i++) {
				if (flag == 0) {
					buffer.append("<tr>");
				}
				buffer.append("<td>" + i + "</td>");
				if (flag == 1) {
					buffer.append("</tr>\n");
					flag = 0;
					continue;
				}
				flag++;
			}
			if (index % 2 != 0) {
				buffer.append("<td>&nbsp</td>");
				buffer.append("</tr>\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String replaceBlank(String str) {
		if (str != null) {
			Pattern p = Pattern.compile("\t|\r|\n");
			Matcher m = p.matcher(str);
			return m.replaceAll("").trim();
		}

		return null;
	}
	
	public static int parseInt(String text){
		return Integer.parseInt(text);
	}
	
	public static long parseLong(String text){
		return Long.parseLong(text);
	}
	
	public static double parseDouble(String text){
		return Double.parseDouble(text);
	}
	
	/**
	 * 检查字符串是否包含特殊符号：<br>
	 * [`~!@#$^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——|{}【】‘；：”“'。，、？]
	 * @param value
	 * @return
	 */
	public static boolean specialSymbols(String value) {
		if (isBlank(value)) {
			return false;
		}
		String regex = "[`~!@#$^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——|{}【】‘；：”“'。，、？]";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(value);
		return matcher.find();
	}
	
}
