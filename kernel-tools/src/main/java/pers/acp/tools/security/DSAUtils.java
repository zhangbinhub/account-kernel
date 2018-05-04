package pers.acp.tools.security;

import pers.acp.tools.file.common.FileCommon;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by zhangbin on 2016/12/23.
 * DSA加密解密类
 */
public class DSAUtils {

    private static final String ALGORITHM = "DSA";

    private static String encode = FileCommon.getDefaultCharset();

    public static String sign(String data, DSAPrivateKey privateKey) throws InvalidKeySpecException, NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, SignatureException {
        byte[] keyBytes = privateKey.getEncoded();
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);
        Signature signature = Signature.getInstance(keyFactory.getAlgorithm());
        signature.initSign(priKey);
        signature.update(data.getBytes(encode));
        return Base64.encode(signature.sign());
    }

    public static boolean verify(String data, DSAPublicKey publicKey, String sign) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, UnsupportedEncodingException, SignatureException, Base64DecodingException {
        byte[] keyBytes = publicKey.getEncoded();
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        PublicKey pubKey = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(keyFactory.getAlgorithm());
        signature.initVerify(pubKey);
        signature.update(data.getBytes(encode));
        return signature.verify(Base64.decode(sign));
    }

}
