package pers.acp.tools.security;

import pers.acp.tools.file.common.FileCommon;
import pers.acp.tools.utility.CommonUtility;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public final class AESUtils {

    private static final String CRYPT_TYPE = "AES/ECB/PKCS5Padding";

    private static String encode = FileCommon.getDefaultCharset();

    /**
     * 加密
     *
     * @param plainText 待加密字符串
     * @param key       密钥
     * @return 密文
     */
    public static String encrypt(String plainText, Key key) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        return encrypt(plainText, key, CRYPT_TYPE);
    }

    /**
     * 加密 AES/ECB/PKCS5Padding
     *
     * @param plainText  待加密字符串
     * @param key        密钥
     * @param crypt_type 加密类型，默认 AES/ECB/PKCS5Padding
     * @return 明文
     */
    public static String encrypt(String plainText, Key key, String crypt_type) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        if (CommonUtility.isNullStr(crypt_type)) {
            crypt_type = CRYPT_TYPE;
        }
        String encryptText;
        byte[] encrypt;
        Cipher cipher = Cipher.getInstance(crypt_type);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        encrypt = cipher.doFinal(plainText.getBytes(encode));
        encryptText = Base64.encode(encrypt).trim();
        return encryptText;
    }

    /**
     * 解密 AES/ECB/PKCS5Padding
     *
     * @param encryptedText 加密字符串
     * @param key           密钥
     * @return 明文
     */
    public static String decrypt(String encryptedText, Key key) throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, Base64DecodingException {
        return decrypt(encryptedText, key, CRYPT_TYPE);
    }

    /**
     * 解密
     *
     * @param encryptedText 加密字符串
     * @param key           密钥
     * @param crypt_type    加密类型，默认 AES/ECB/PKCS5Padding
     * @return 明文
     */
    public static String decrypt(String encryptedText, Key key, String crypt_type) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, Base64DecodingException {
        if (CommonUtility.isNullStr(crypt_type)) {
            crypt_type = CRYPT_TYPE;
        }
        String decryptText;
        byte[] decrypt;
        Cipher cipher = Cipher.getInstance(crypt_type);
        cipher.init(Cipher.DECRYPT_MODE, key);
        decrypt = cipher.doFinal(Base64.decode(encryptedText));
        decryptText = new String(decrypt, encode).trim();
        return decryptText;
    }

}
