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
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public final class RSAUtils {

    private static final String CRYPT_TYPE = "RSA/ECB/PKCS1Padding";

    private static String encode = FileCommon.getDefaultCharset();

    /**
     * 公钥加密 RSA/ECB/PKCS1Padding
     *
     * @param data      待加密字符串
     * @param publicKey 公钥
     * @return 密文
     */
    public static String encryptByPublicKey(String data, RSAPublicKey publicKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        return encryptByPublicKey(data, publicKey, CRYPT_TYPE);
    }

    /**
     * 公钥加密
     *
     * @param data       待加密字符串
     * @param publicKey  公钥
     * @param crypt_type 加密类型，默认 RSA/ECB/PKCS1Padding
     * @return 密文
     */
    public static String encryptByPublicKey(String data, RSAPublicKey publicKey, String crypt_type) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        return doEncrypt(data, publicKey, crypt_type);
    }

    /**
     * 私钥解密 RSA/ECB/PKCS1Padding
     *
     * @param data       加密字符串
     * @param privateKey 私钥
     * @return 明文
     */
    public static String decryptByPrivateKey(String data, RSAPrivateKey privateKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, Base64DecodingException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
        return decryptByPrivateKey(data, privateKey, CRYPT_TYPE);
    }

    /**
     * 私钥解密
     *
     * @param data       加密字符串
     * @param privateKey 私钥
     * @param crypt_type 加密类型，默认 RSA/ECB/PKCS1Padding
     * @return 明文
     */
    public static String decryptByPrivateKey(String data, RSAPrivateKey privateKey, String crypt_type) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, Base64DecodingException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
        return doDecrypt(data, privateKey, crypt_type);
    }

    /**
     * 私钥加密 RSA/ECB/PKCS1Padding
     *
     * @param data       待加密字符串
     * @param privateKey 私钥
     * @return 密文
     */
    public static String encryptByPrivateKey(String data, RSAPrivateKey privateKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        return encryptByPrivateKey(data, privateKey, CRYPT_TYPE);
    }

    /**
     * 私钥加密
     *
     * @param data       待加密字符串
     * @param privateKey 私钥
     * @param crypt_type 加密类型，默认 RSA/ECB/PKCS1Padding
     * @return 密文
     */
    public static String encryptByPrivateKey(String data, RSAPrivateKey privateKey, String crypt_type) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        return doEncrypt(data, privateKey, crypt_type);
    }

    /**
     * 公钥解密 RSA/ECB/PKCS1Padding
     *
     * @param data      加密字符串
     * @param publicKey 公钥
     * @return 明文
     */
    public static String decryptByPublicKey(String data, RSAPublicKey publicKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, Base64DecodingException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
        return decryptByPublicKey(data, publicKey, CRYPT_TYPE);
    }

    /**
     * 公钥解密
     *
     * @param data       加密字符串
     * @param publicKey  公钥
     * @param crypt_type 加密类型，默认 RSA/ECB/PKCS1Padding
     * @return 明文
     */
    public static String decryptByPublicKey(String data, RSAPublicKey publicKey, String crypt_type) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, Base64DecodingException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
        return doDecrypt(data, publicKey, crypt_type);
    }

    private static String doEncrypt(String data, RSAKey key, String crypt_type) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException {
        if (CommonUtility.isNullStr(crypt_type)) {
            crypt_type = CRYPT_TYPE;
        }
        Cipher cipher = Cipher.getInstance(crypt_type);
        cipher.init(Cipher.ENCRYPT_MODE, (Key) key);
        // 模长
        int key_len = key.getModulus().bitLength() / 8;
        // 加密数据长度 <= 模长-11
        String[] datas = splitString(data, key_len - 11);
        String mi = "";
        // 如果明文长度大于模长-11则要分组加密
        for (String s : datas) {
            mi += Base64.encode(cipher.doFinal(s.getBytes(encode)));
        }
        return mi;
    }

    private static String doDecrypt(String data, RSAKey key, String crypt_type) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, Base64DecodingException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        if (CommonUtility.isNullStr(crypt_type)) {
            crypt_type = CRYPT_TYPE;
        }
        Cipher cipher = Cipher.getInstance(crypt_type);
        cipher.init(Cipher.DECRYPT_MODE, (Key) key);
        // 模长
        int key_len = key.getModulus().bitLength() / 8;
        byte[] bytes = data.getBytes();
        byte[] bcd = Base64.decode(bytes);
        // 如果密文长度大于模长则要分组解密
        String ming = "";
        byte[][] arrays = splitArray(bcd, key_len);
        for (byte[] arr : arrays) {
            ming += new String(cipher.doFinal(arr), encode);
        }
        return ming;
    }

    /**
     * 拆分字符串
     */
    private static String[] splitString(String string, int len) {
        int x = string.length() / len;
        int y = string.length() % len;
        int z = 0;
        if (y != 0) {
            z = 1;
        }
        String[] strings = new String[x + z];
        String str;
        for (int i = 0; i < x + z; i++) {
            if (i == x + z - 1 && y != 0) {
                str = string.substring(i * len, i * len + y);
            } else {
                str = string.substring(i * len, i * len + len);
            }
            strings[i] = str;
        }
        return strings;
    }

    /**
     * 拆分数组
     */
    private static byte[][] splitArray(byte[] data, int len) {
        int x = data.length / len;
        int y = data.length % len;
        int z = 0;
        if (y != 0) {
            z = 1;
        }
        byte[][] arrays = new byte[x + z][];
        byte[] arr;
        for (int i = 0; i < x + z; i++) {
            arr = new byte[len];
            if (i == x + z - 1 && y != 0) {
                System.arraycopy(data, i * len, arr, 0, y);
            } else {
                System.arraycopy(data, i * len, arr, 0, len);
            }
            arrays[i] = arr;
        }
        return arrays;
    }
}
