package pers.acp.tools.security;

import pers.acp.tools.file.common.FileCommon;
import pers.acp.tools.utility.CommonUtility;
import org.apache.log4j.Logger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by zhangbin on 2016/9/22.
 * sha-256加密
 */
public class SHA256Utils {

    private static Logger log = Logger.getLogger(SHA1Utils.class);

    private static String encode = FileCommon.getDefaultCharset();

    /**
     * 加密
     *
     * @param plainText 待加密字符串
     * @param secret    密钥
     * @return 密文
     */
    public static String encrypt(String plainText, String secret) {
        String encryptText;
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(encode), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            encryptText = CommonUtility.byte2hex(sha256_HMAC.doFinal(plainText.getBytes(encode)));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            encryptText = "";
        }
        return encryptText;
    }

}
