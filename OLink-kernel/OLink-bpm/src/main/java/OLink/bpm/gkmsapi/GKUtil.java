package OLink.bpm.gkmsapi;
import java.io.*;  
import java.security.MessageDigest;

/**
 * Base64编码类
 * 实现base64编码的加密和解密
 */
public class GKUtil {  
	
	/**
	 * 去掉-字符的UUID
	 * @param s
	 * @return
	 */
	 public static String getUUID(String s){ 
	    //去掉“-”符号 
		if(s.indexOf("-")!=-1){
			String[] uuidParts = s.split("-");
			StringBuffer builder = new StringBuffer();
			builder.append(uuidParts[2]);
			//builder.append("-");
			builder.append(uuidParts[1]);
			//builder.append("-");
			builder.append(uuidParts[0]);
			//builder.append("-");
			builder.append(uuidParts[3]);
			//builder.append("-");
			builder.append(uuidParts[4]);

			return builder.toString();
		}else{
			return s;
		}
	} 
	 
	private static char[] base64EncodeChars = new char[] {  
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',  
		'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',  
		'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',  
		'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',  
		'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',  
		'o', 'p', 'q', 'r', 's', 't', 'u', 'v',  
		'w', 'x', 'y', 'z', '0', '1', '2', '3',  
		'4', '5', '6', '7', '8', '9', '+', '/' };  

	private static byte[] base64DecodeChars = new byte[] {  
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63,  
		52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1,  
		-1,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14,  
		15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1,  
		-1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,  
		41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1 };  

	/**
	 * base64加密操作
	 * @param data 需要加密的数据
	 * @return base64编码后的数据
	 */
	public static String encode(byte[] data) {  
		StringBuffer sb = new StringBuffer();  
		int len = data.length;  
		int i = 0;  
		int b1, b2, b3;  

		while (i < len) {  
			b1 = data[i++] & 0xff;  
			if (i == len) {  
				sb.append(base64EncodeChars[b1 >>> 2]);  
				sb.append(base64EncodeChars[(b1 & 0x3) << 4]);  
				sb.append("==");  
				break;  
			}  
			b2 = data[i++] & 0xff;  
			if (i == len) {  
				sb.append(base64EncodeChars[b1 >>> 2]);  
				sb.append(  
						base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);  
				sb.append(base64EncodeChars[(b2 & 0x0f) << 2]);  
				sb.append("=");  
				break;  
			}  
			b3 = data[i++] & 0xff;  
			sb.append(base64EncodeChars[b1 >>> 2]);  
			sb.append(  
					base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);  
			sb.append(  
					base64EncodeChars[((b2 & 0x0f) << 2) | ((b3 & 0xc0) >>> 6)]);  
			sb.append(base64EncodeChars[b3 & 0x3f]);  
		}  
		return sb.toString();  
	}  

	/**
	 * base64解码类
	 * @param str 需要解码的字符串
	 * @return base64解码的结果
	 */
	public static byte[] decode(String str) {  
		byte[] data = str.getBytes();  
		int len = data.length;  
		ByteArrayOutputStream buf = new ByteArrayOutputStream(len);  
		int i = 0;  
		int b1, b2, b3, b4;  

		while (i < len) {  

			/* b1 */  
			do {  
				b1 = base64DecodeChars[data[i++]];  
			} while (i < len && b1 == -1);  
			if (b1 == -1) {  
				break;  
			}  

			/* b2 */  
			do {  
				b2 = base64DecodeChars[data[i++]];  
			} while (i < len && b2 == -1);  
			if (b2 == -1) {  
				break;  
			}  
			buf.write((b1 << 2) | ((b2 & 0x30) >>> 4));

			/* b3 */  
			do {  
				b3 = data[i++];  
				if (b3 == 61) {  
					return buf.toByteArray();  
				}  
				b3 = base64DecodeChars[b3];  
			} while (i < len && b3 == -1);  
			if (b3 == -1) {  
				break;  
			}  
			buf.write(((b2 & 0x0f) << 4) | ((b3 & 0x3c) >>> 2));

			/* b4 */  
			do {  
				b4 = data[i++];  
				if (b4 == 61) {  
					return buf.toByteArray();  
				}  
				b4 = base64DecodeChars[b4];  
			} while (i < len && b4 == -1);  
			if (b4 == -1) {  
				break;  
			}  
			buf.write(((b3 & 0x03) << 6) | b4);
		}  
		return buf.toByteArray();  
	}  
	
	private final static String[] hexDigits = {
		  "0", "1", "2", "3", "4", "5", "6", "7",
		  "8", "9", "A", "B", "C", "D", "E", "F"
		};
		
		/**
		* 转换字节数组为16进制字串
		* @param b 字节数组
		* @return 16进制字串
		*/
		
		private static String byteArrayToHexString(byte[] b) 
		{
			StringBuffer resultSb = new StringBuffer();
			for (int i = 0; i < b.length; i++) {
			  resultSb.append(byteToHexString(b[i]));
			}
			return resultSb.toString();
		}
		
		private static String byteToHexString(byte b){
			int n=b;
			if(n<0){
				n+=256;
			}		
			return hexDigits[n/16]+hexDigits[n%16];
		}
		
		/**
		 * 生成字符串的MD5编码
		 * @param  orignal 原字符串
		 * @return 源字符串的MD5编码
		 */
		public static String str2MD5(String orignal){
			String resultString=null;
			try{
				resultString=new String(orignal);
				MessageDigest md = MessageDigest.getInstance("MD5");
				resultString=byteArrayToHexString(md.digest(resultString.getBytes())).toLowerCase();
			}catch(Exception ex){			
			}
			return resultString;
		}
		
		/**
		 * XML转义字符处理
		 * @param xml 待处理的XML
		 * @return 转义字符处理后的结果
		 */
		public static String codeXml(String xml){
			xml.replaceAll("&", "&amp;");
			xml.replaceAll("<", "&lt;");
			xml.replaceAll(">", "&gt;");
			xml.replaceAll("\'", "&apos;");
			xml.replaceAll("\"", "&quot;");
			return xml;
		}
		
}

/**
 * Base64编码类
 * 实现base64编码的加密和解密
 */
class Base64 {  
	private static char[] base64EncodeChars = new char[] {  
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',  
		'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',  
		'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',  
		'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',  
		'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',  
		'o', 'p', 'q', 'r', 's', 't', 'u', 'v',  
		'w', 'x', 'y', 'z', '0', '1', '2', '3',  
		'4', '5', '6', '7', '8', '9', '+', '/' };  

	private static byte[] base64DecodeChars = new byte[] {  
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63,  
		52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1,  
		-1,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14,  
		15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1,  
		-1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,  
		41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1 };  

	private Base64() {}  

	/**
	 * base64加密操作
	 * @param data 需要加密的数据
	 * @return base64编码后的数据
	 */
	public static String encode(byte[] data) {  
		StringBuffer sb = new StringBuffer();  
		int len = data.length;  
		int i = 0;  
		int b1, b2, b3;  

		while (i < len) {  
			b1 = data[i++] & 0xff;  
			if (i == len) {  
				sb.append(base64EncodeChars[b1 >>> 2]);  
				sb.append(base64EncodeChars[(b1 & 0x3) << 4]);  
				sb.append("==");  
				break;  
			}  
			b2 = data[i++] & 0xff;  
			if (i == len) {  
				sb.append(base64EncodeChars[b1 >>> 2]);  
				sb.append(  
						base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);  
				sb.append(base64EncodeChars[(b2 & 0x0f) << 2]);  
				sb.append("=");  
				break;  
			}  
			b3 = data[i++] & 0xff;  
			sb.append(base64EncodeChars[b1 >>> 2]);  
			sb.append(  
					base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);  
			sb.append(  
					base64EncodeChars[((b2 & 0x0f) << 2) | ((b3 & 0xc0) >>> 6)]);  
			sb.append(base64EncodeChars[b3 & 0x3f]);  
		}  
		return sb.toString();  
	}  

	/**
	 * base64解码类
	 * @param str 需要解码的字符串
	 * @return base64解码的结果
	 */
	public static byte[] decode(String str) {  
		byte[] data = str.getBytes();  
		int len = data.length;  
		ByteArrayOutputStream buf = new ByteArrayOutputStream(len);  
		int i = 0;  
		int b1, b2, b3, b4;  

		while (i < len) {  

			/* b1 */  
			do {  
				b1 = base64DecodeChars[data[i++]];  
			} while (i < len && b1 == -1);  
			if (b1 == -1) {  
				break;  
			}  

			/* b2 */  
			do {  
				b2 = base64DecodeChars[data[i++]];  
			} while (i < len && b2 == -1);  
			if (b2 == -1) {  
				break;  
			}  
			buf.write((b1 << 2) | ((b2 & 0x30) >>> 4));

			/* b3 */  
			do {  
				b3 = data[i++];  
				if (b3 == 61) {  
					return buf.toByteArray();  
				}  
				b3 = base64DecodeChars[b3];  
			} while (i < len && b3 == -1);  
			if (b3 == -1) {  
				break;  
			}  
			buf.write(((b2 & 0x0f) << 4) | ((b3 & 0x3c) >>> 2));

			/* b4 */  
			do {  
				b4 = data[i++];  
				if (b4 == 61) {  
					return buf.toByteArray();  
				}  
				b4 = base64DecodeChars[b4];  
			} while (i < len && b4 == -1);  
			if (b4 == -1) {  
				break;  
			}  
			buf.write(((b3 & 0x03) << 6) | b4);
		}  
		return buf.toByteArray();  
	}  
}