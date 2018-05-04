package OLink.bpm.util;

/**
 * The html encode utility.
 */
public class HtmlEncoder {

    private static final String[] htmlCode = new String[256];

    static {
        for (int i = 0; i < 10; i++) {
            htmlCode[i] = "&#00" + i + ";";
        }

        for (int i = 10; i < 32; i++) {
            htmlCode[i] = "&#0" + i + ";";
        }

        for (int i = 32; i < 128; i++) {
            htmlCode[i] = String.valueOf((char) i);
        }

        htmlCode['\"'] = "&quot;";
        htmlCode['&'] = "&amp;";
        htmlCode['<'] = "&lt;";
        htmlCode['>'] = "&gt;";
//        htmlCode[' '] = "&nbsp;";

        for (int i = 128; i < 256; i++) {
            htmlCode[i] = "&#" + i + ";";
        }
    }

    /**
     * Encode the given text into html.
     * @param s The text to encode
     * @return The encoded string
     */
    public static String encode(String s) {
    	if(!StringUtil.isBlank(s)){
	        int n = s.length();
	        char character;
	        StringBuffer buffer = new StringBuffer();
	
	        for (int i = 0; i < n; i++) {
	            character = s.charAt(i);
	            try {
	                buffer.append(htmlCode[character]);
	            } catch (ArrayIndexOutOfBoundsException aioobe) {
	                buffer.append(character);
	            }
	        }
	        return buffer.toString();
    	}else{
    		return s;
    	}
    }
}

