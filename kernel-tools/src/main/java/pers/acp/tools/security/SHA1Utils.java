package pers.acp.tools.security;

import pers.acp.tools.file.common.FileCommon;
import pers.acp.tools.utility.CommonUtility;
import org.apache.log4j.Logger;

import java.security.MessageDigest;

public class SHA1Utils {

    private static Logger log = Logger.getLogger(SHA1Utils.class);

    private static String encode = FileCommon.getDefaultCharset();

    /**
     * 加密
     *
     * @param plainText 待加密字符串
     * @return 密文
     */
    public static String encrypt(String plainText) {
        String encryptText;
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            byte[] byteArray = plainText.getBytes(encode);
            byte[] sha1Bytes = sha1.digest(byteArray);
            encryptText = CommonUtility.byte2hex(sha1Bytes);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            encryptText = "";
        }
        return encryptText;
    }
}
