package OLink.core.protection;

import eWAP.core.RSAUtil;
import eWAP.core.Tools;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import java.io.*;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

public class RSAImp implements RSAUtil {
    public KeyPair generateKeyPair(String KeyStore) {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA", new BouncyCastleProvider());
            int KEY_SIZE = 1024;
            keyPairGen.initialize(1024, new SecureRandom());
            KeyPair keyPair = keyPairGen.generateKeyPair();

            if (KeyStore != null) {
                FileOutputStream fos = new FileOutputStream(KeyStore);
                ObjectOutputStream oos = new ObjectOutputStream(fos);

                oos.writeObject(keyPair);
                oos.close();
                fos.close();
            }
            return keyPair;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public KeyPair getKeyPair(String KeyStore) {
        KeyPair kp = null;
        try {
            FileInputStream fis = new FileInputStream(KeyStore);
            ObjectInputStream oos = new ObjectInputStream(fis);
            kp = (KeyPair) oos.readObject();
            oos.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return kp;
    }

    public static RSAPublicKey generateRSAPublicKey(byte[] modulus, byte[] publicExponent)
            throws Exception {
        KeyFactory keyFac = null;
        try {
            keyFac = KeyFactory.getInstance("RSA", new BouncyCastleProvider());
        } catch (NoSuchAlgorithmException ex) {
            throw new Exception(ex.getMessage());
        }

        RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(
                new BigInteger(modulus), new BigInteger(publicExponent));
        try {
            return (RSAPublicKey) keyFac.generatePublic(pubKeySpec);
        } catch (InvalidKeySpecException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public static RSAPrivateKey generateRSAPrivateKey(byte[] modulus, byte[] privateExponent)
            throws Exception {
        KeyFactory keyFac = null;
        try {
            keyFac = KeyFactory.getInstance("RSA", new BouncyCastleProvider());
        } catch (NoSuchAlgorithmException ex) {
            throw new Exception(ex.getMessage());
        }

        RSAPrivateKeySpec priKeySpec = new RSAPrivateKeySpec(
                new BigInteger(modulus), new BigInteger(privateExponent));
        try {
            return (RSAPrivateKey) keyFac.generatePrivate(priKeySpec);
        } catch (InvalidKeySpecException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public byte[] encrypt(Key pk, byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(pk.getAlgorithm(), new BouncyCastleProvider());
            cipher.init(1, pk);
            int blockSize = cipher.getBlockSize();

            int outputSize = cipher.getOutputSize(data.length);
            int leavedSize = data.length % blockSize;
            int blocksSize = leavedSize != 0 ? data.length / blockSize + 1 :
                    data.length / blockSize;
            byte[] raw = new byte[outputSize * blocksSize];
            int i = 0;
            while (data.length - i * blockSize > 0) {
                if (data.length - i * blockSize > blockSize)
                    cipher.doFinal(data, i * blockSize, blockSize, raw, i *
                            outputSize);
                else {
                    cipher.doFinal(data, i * blockSize, data.length - i *
                            blockSize, raw, i * outputSize);
                }

                i++;
            }
            return raw;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] decrypt(Key pk, byte[] raw) {
        try {
            Cipher cipher = Cipher.getInstance(pk.getAlgorithm(), new BouncyCastleProvider());
            cipher.init(2, pk);
            int blockSize = cipher.getBlockSize();
            ByteArrayOutputStream bout = new ByteArrayOutputStream(64);
            int j = 0;
            int len = raw.length;
            while (len > 0) {
                len = raw.length - j * blockSize > blockSize ? blockSize : raw.length - j * blockSize;
                if (len <= 0) break;
                bout.write(cipher.doFinal(raw, j * blockSize, len));

                j++;
            }
            return bout.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String decrypt(Key pk, String src, boolean reverse) {
        byte[] en_result = Tools.hexStringToBytes(src);
        byte[] de_result = decrypt(pk, en_result);
        if (de_result == null) return null;
        StringBuffer sb = new StringBuffer();
        sb.append(new String(de_result));
        String pwd;
        if (reverse) pwd = sb.reverse().toString();
        else
            pwd = sb.toString();
        try {
            pwd = URLDecoder.decode(pwd, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        return pwd;
    }
}