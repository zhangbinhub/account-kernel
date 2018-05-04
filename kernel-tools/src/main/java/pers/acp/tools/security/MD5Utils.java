package pers.acp.tools.security;

import pers.acp.tools.file.common.FileCommon;
import pers.acp.tools.utility.CommonUtility;
import org.apache.log4j.Logger;

import java.security.MessageDigest;

public class MD5Utils {

    private static Logger log = Logger.getLogger(MD5Utils.class);

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
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] byteArray = plainText.getBytes(encode);
            byte[] md5Bytes = md5.digest(byteArray);
            encryptText = CommonUtility.byte2hex(md5Bytes);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            encryptText = "";
        }
        return encryptText;
    }
}
