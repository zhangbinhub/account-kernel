package OLink.bpm.core.email.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeUtility;
import javax.mail.internet.ParseException;

public class Utility {
	
	private final static char trChars[] = {'\u0131', '\u0130', '\u015F', '\u015E', '\u011F', '\u011E', 
											'\u00fd', '\u00dd', '\u00fe', '\u00de', '\u00f0', '\u00d0', 
											'\u00E7', '\u00C7', '\u00FC', '\u00DC', '\u00F6', '\u00D6'};
	private final static char enChars[] = {'i', 'I', 's', 'S', 'g', 'G', 'i', 'I', 's', 'S', 
											'g', 'G', 'c', 'C', 'u', 'U', 'o', 'O'};
	private final static char enLowerCaseChars[] = {'i', 'i', 's', 's', 'g', 'g', 'i', 'i', 's', 's', 
											'g', 'g', 'c', 'c', 'u', 'u', 'o', 'o'};
	private final static String trUnicodes[] = {"&#305;", "&#304;", "&#351;", "&#350;", "&#287;", "&#286;", 
											"&#305;", "&#304;", "&#351;", "&#350;", "&#287;", "&#286;", 
											"&#231;", "&#199;", "&#252;", "&#220;", "&#246;", "&#214;"};
	
	private final static char trDirtyChars[] = { '\u00fd', '\u00dd', '\u00fe', '\u00de', '\u00f0', '\u00d0' };
	private final static char trCleanChars[] = { '\u0131', '\u0130', '\u015F', '\u015E', '\u011F', '\u011E' };

	/**
	 * 
	 * @param str
	 * @param from
	 * @param to
	 * @return
	 */
	public static String replaceAllOccurances(String str, String from, String to) {
		if (str == null || str.length() == 0) {
			return str;
		} else if (str.length() == 1 && str.equals(from)) {
			return to;
		} else if (str.length() == 1 && !str.equals(from)) {
			return str;
		}
		int j = -1;
		while ((j = str.indexOf(from)) >= 0) {
			str = str.substring(0, j) + (char)5 + str.substring(j + from.length());
		}

		int i = -1;
		while ((i = str.indexOf((char)5)) >= 0) {
			str = str.substring(0, i) + to + str.substring(i + 1);
		}

		return str;
	}

	/**
	 * 
	 * @param str
	 * @param trimStr
	 * @return
	 */
	public static String extendedTrim(String str, String trimStr) {
		if (str == null || str.length() == 0)
			return str;
		for (str = str.trim(); str.startsWith(trimStr); str = str.substring(trimStr.length()).trim());
		for (; str.endsWith(trimStr); str = str.substring(0, str.length() - trimStr.length()).trim());
		return str;
	}

	/**
	 * 
	 * @param number
	 * @return
	 */
	public static Object checkDecimalFormat(Object number) {
		String str = "-";
		try {
			str = number.toString();
			int posDot = str.indexOf(".");
			int posComma = str.indexOf(",");

			if (posComma > posDot) {
				str = Utility.replaceAllOccurances(str, ".", "");
				str = Utility.replaceAllOccurances(str, ",", ".");
			} else if (posComma == -1 && posDot > 0) {
				int lastPosDot = str.lastIndexOf(".");
				if (posDot != lastPosDot) {
					str = Utility.replaceAllOccurances(str, ".", "");
				}
			}
		} catch (Exception e) {
			str = "-";
		}
		return str;
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	public static String doCharsetCorrections(String str) {
		if (str == null) return "";
		
		String extraChars[] = {"\u00FD","\u00DD","\u00FE","\u00DE","\u00F0","\u00D0"};
		String unicode[] = {"\u0131", "\u0130", "\u015F", "\u015E", "\u011F", "\u011E"};
		for (int i=0;i<extraChars.length;i++) {
			while (str.indexOf(extraChars[i]) != -1) {
				String tmp = str;
				str = tmp.substring(0, tmp.indexOf(extraChars[i])) 
					+ unicode[i] + tmp.substring (tmp.indexOf(extraChars[i])+1, tmp.length());
			}
		}
		return str;
	}

	/**
	 * 
	 * @param input
	 * @return
	 */
	public static String htmlSpecialChars(String input) {
		StringBuffer filtered;
		try {
			filtered = new StringBuffer(input.length());
			char c;
			for (int i = 0; i < input.length(); i++) {
				c = input.charAt(i);
				if (c == '<') {
					filtered.append("&lt;");
				} else if (c == '>') {
					filtered.append("&gt;");
				} else if (c == '"') {
					filtered.append("&quot;");
				} else if (c == '&') {
					filtered.append("&amp;");
				} else {
					filtered.append(c);
				}
			}
		} catch (Exception e) {
			return input;
		}
		return (filtered.toString());
	}

	/**
	 * 
	 * @param a
	 * @return
	 */
	public static String convertTRCharsToHtmlSafe(String str) {
		if ((str == null) || (str.length() == 0))
			return "";

		int pos = -1;
		for (int i = 0; i < trChars.length; i++) {
			while ((pos = str.indexOf(trChars[i])) != -1) {
				str = str.substring(0, pos)
				+ trUnicodes[i] 
				+ str.substring(pos+1, str.length());
			}
		}
		return str;
	}

	/**
	 * 
	 * @param a
	 * @return
	 */
	public static String updateTRChars(String str) {
		if ((str == null) || (str.length() == 0))
			return "";
		String ret = "";
		try{
			ret = MimeUtility.decodeText(str);
		}
		catch(Exception e){
		}
		String strLowerCase = ret.toLowerCase(new Locale("en", "US"));
		if(strLowerCase.startsWith("=?iso-8859-9?q?")) {
			ret = ret.substring(15);
			if(strLowerCase.endsWith("?=")) {
				ret = ret.substring(0, ret.length()-2);
			}
			else
			{
				int pos = -1;
				while ((pos = ret.indexOf("?=")) != -1) {
					ret = ret.substring(0, pos) 
						+ ret.substring(pos+2, ret.length());
				}
			}
			try {
				ret = ret.replace('=', '%');
				ret = URLDecoder.decode(ret, "iso-8859-9");
			} catch(Exception ex) { }
		}
		for (int i = 0; i < trDirtyChars.length; i++) {
			int pos = -1;
			while ((pos = ret.indexOf(trDirtyChars[i])) != -1) {
				ret = ret.substring(0, pos)
					+ trCleanChars[i] 
					+ ret.substring(pos+1, ret.length());
			}
		}
		return ret;
	}
	
	/**
	 * 
	 * @param a
	 * @return
	 */
	public static String convertTRCharsToENChars(String str) {
		if ((str == null) || (str.length() == 0))
			return "";

		int pos = -1;
		for (int i = 0; i < trChars.length; i++) {
			while ((pos = str.indexOf(trChars[i])) != -1) {
				str = str.substring(0, pos)
				+ enChars[i] 
				+ str.substring(pos+1, str.length());
			}
		}
		return str;
	}
	
	/**
	 * 
	 * @param a
	 * @return
	 */
	public static String convertTRCharsToENLowerCaseChars(String str) {
		if ((str == null) || (str.length() == 0))
			return "";

		int pos = -1;
		for (int i = 0; i < trChars.length; i++) {
			while ((pos = str.indexOf(trChars[i])) != -1) {
				str = str.substring(0, pos)
				+ enLowerCaseChars[i] 
				+ str.substring(pos+1, str.length());
			}
		}
		return str;
	}
	
	/**
	 * 
	 * @param addr
	 * @return
	 */
	public static String[] addressArrToStringArr(Address[] addr) {
		if (addr != null && addr.length > 0) {
			String[] str = new String[addr.length];
			for (int j = 0; j < addr.length; j++) {
				InternetAddress add = (InternetAddress) addr[j];
				String personal = Utility.doCharsetCorrections(add.getPersonal());
				String address = Utility.doCharsetCorrections(add.getAddress());

				if (personal != null && personal.length() > 0) {
					if (address != null && address.length() > 0) {
						str[j] = personal + " <" + address + ">";
					} else {
						str[j] = personal;
					}
				} else {
					if (address != null && address.length() > 0) {
						str[j] = address;
					} else {
						str[j] = "";
					}
				}
			}
			return str;
		}
		return null;
	}

	/**
	 * 
	 * @param addr
	 * @return
	 */
	public static String addressArrToString(Address[] addr) {
		StringBuffer addrStr = new StringBuffer();
		String str[] = addressArrToStringArr(addr);
		if (str != null && str.length > 0) {
			for (int i = 0; i < str.length; i++) {
				addrStr.append(str[i]).append(", ");
			}
		}
		String msg = Utility.extendedTrim(addrStr.toString(), ",");
		return Utility.doCharsetCorrections(msg);
	}

	/**
	 * 
	 * @param addr
	 * @return
	 */
	public static String[] addressArrToStringArrShort(Address[] addr) {
		if (addr != null && addr.length > 0) {
			String[] str = new String[addr.length];
			for (int j = 0; j < addr.length; j++) {
				InternetAddress add = (InternetAddress) addr[j];
				String personal = Utility.doCharsetCorrections(add.getPersonal());
				String address = Utility.doCharsetCorrections(add.getAddress());

				if (personal != null && personal.length() > 0) {
					str[j] = personal;
				} else if (address != null && address.length() > 0) { 
					str[j] = address;
				} else {
					str[j] = "Unknown";
				}
			}
			return str;
		}
		return null;
	}
	
	/**
	 * 
	 * @param addr
	 * @return
	 */
	public static String addressArrToStringShort(Address[] addr) {
		StringBuffer addrStr = new StringBuffer();
		String str[] = addressArrToStringArrShort(addr);
		if (str != null && str.length > 0) {
			for (int i = 0; i < str.length; i++) {
				addrStr.append(str[i] + ", ");
			}
		}
		String msg = Utility.extendedTrim(addrStr.toString(), ",");
		msg =  Utility.doCharsetCorrections(msg);
		return msg;
	}

	/**
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static Address[] stringToAddressArray(String str) throws Exception {
		if (str == null || str.trim().length() == 0) {
			return null;
		}
		str = Utility.extendedTrim(str, ",");
		StringTokenizer token = new StringTokenizer(str, ",");
		if (token.countTokens() != 0) {
			Address[] outAddr = new Address[token.countTokens()];
			int counter = 0;
			while (token.hasMoreTokens()) {
				String addr = token.nextToken().trim();
				addr = Utility.replaceAllOccurances(addr, "&lt;", "<");
				addr = Utility.replaceAllOccurances(addr, "&gt;", ">");
				String fullname = "";
				String email = "";
				int j;
				try {
					if ((j = addr.indexOf("<")) > 0) {
						fullname = Utility.extendedTrim(addr.substring(0, j).trim(), "\"");
						email = Utility.extendedTrim(Utility.extendedTrim(addr.substring(j + 1), ">"), "\"").trim();
						String charset = EmailConfig.getString("charset", "UTF-8");
						outAddr[counter] = new InternetAddress(email, fullname, charset);
					} else {
						outAddr[counter] = new InternetAddress(addr);
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (AddressException e) {
					e.printStackTrace();
				}
				counter++;
			}
			return outAddr;
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @param size
	 * @return
	 */
	public static String sizeToHumanReadable(long size) {
		String sizeString = String.valueOf(Math.round((float)size / 1024));
		if (sizeString.equals("0")) {
			sizeString = size + "B";
		} else {
			StringBuffer temp = new StringBuffer();
			char[] chars = sizeString.toCharArray();
			int count = 0;
			for (int i = chars.length - 1; i >= 0; i --) {
				temp.insert(0, chars[i]);
				count ++;
				if (count % 3 == 0 && temp.length() < chars.length) {
					temp.insert(0, ',');
					count = 0;
				}
			}
			temp.append("k");
			sizeString = temp.toString();
		}
		return sizeString;
	}

	/**
	 * 
	 * @param message
	 * @return
	 */
	public static String stripHTMLTags(String message) {
		StringBuffer returnMessage = new StringBuffer(message);
		try {
			int startPosition = message.indexOf("<"); // encountered the first opening brace
			int endPosition = message.indexOf(">"); // encountered the first closing braces
			while (startPosition != -1) {
				returnMessage.delete(startPosition, endPosition + 1); // remove the tag
				returnMessage.insert(startPosition, " ");
				startPosition = (returnMessage.toString()).indexOf("<"); // look for the next opening brace
				endPosition = (returnMessage.toString()).indexOf(">"); // look for the next closing brace
			}
		} catch (Throwable e) {
			// do nothing sier
		}
		return returnMessage.toString();
	}
	
	public static boolean isBlank(String str) {
		return str == null || str.trim().length() == 0;
	}
	
	public static boolean checkEmailAddress(String address) {
		if (!Utility.isBlank(address)) {
			// ^(([a-zA-Z0-9_]){1,20}@([a-zA-Z0-9_])+(\.([a-zA-Z0-9_])+)+){0,1}$
			return address.matches("^(([a-zA-Z0-9_.-]){1,20}@([a-zA-Z0-9_-])+(\\.([a-zA-Z0-9_-])+)+){0,1}$");
		}
		return false;
	}
	
	public static String getDateToString() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(new Date());
	}
	
	public static String getEmailAddress(String account) {
		if (account.indexOf('@') >= 0) {
			return account;
		} else {
			return account + "@" + EmailConfig.getEmailDomain();
		}
	}
	
	public static String getDateToString(Date date) {
		if (date == null) {
			return "";
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(date);
	}
	
	public static String getDateToString(Date date, String formatString) {
		SimpleDateFormat format = new SimpleDateFormat(formatString);
		return format.format(date);
	}
	
	public static void main(String[] args) {
		System.out.println(Utility.sizeToHumanReadable(10240000L));
	}
	
	private static Pattern encodePattern = Pattern.compile("=\\?(.+)\\?(B|Q)\\?(.+)\\?=", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	
	public static String decodeText(String text) {
		try {
			if (!Utility.isBlank(text)) {
				String result = MimeUtility.decodeText(text);
				if (!Utility.isBlank(result)) {
					Matcher matcher = encodePattern.matcher(result);
					if (matcher.find()) {
						return MimeUtility.decodeWord(result);
					} else {
						return result;
					}
				}
			}
		} catch (UnsupportedEncodingException e) {
			//e.printStackTrace();
		} catch (ParseException e) {
			//e.printStackTrace();
		}
		return text;
	}
	
}
