package pers.acp.tools.security.key;

import pers.acp.tools.file.common.FileCommon;
import pers.acp.tools.security.key.enums.KeyType;
import pers.acp.tools.security.key.enums.StorageMode;
import pers.acp.tools.security.key.factory.IStorageFactory;
import pers.acp.tools.utility.CommonUtility;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.*;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public final class KeyManagement {

    static final Map<String, Thread> temporaryThread = new ConcurrentHashMap<>();

    private static String encode = FileCommon.getDefaultCharset();

    public final static int RANDOM_STR = 0;

    public final static int RANDOM_CHAR = 1;

    public final static int RANDOM_NUMBER = 2;

    private static StorageMode storageMode = StorageMode.Memory;

    public static String storageParam;

    public static String tablename;

    public static String keyCol;

    public static String objCol;

    public static void initParams(StorageMode storageMode) {
        KeyManagement.storageMode = storageMode;
    }

    public static void initParams(StorageMode storageMode, String storageParam) {
        KeyManagement.storageMode = storageMode;
        KeyManagement.storageParam = storageParam;
    }

    public static void initParams(StorageMode storageMode, String storageParam, String tablename, String keyCol, String objCol) {
        KeyManagement.storageMode = storageMode;
        KeyManagement.storageParam = storageParam;
        KeyManagement.tablename = tablename;
        KeyManagement.keyCol = keyCol;
        KeyManagement.objCol = objCol;
    }

    /**
     * 生成密钥存储工厂
     *
     * @return 密钥存储工厂
     */
    static IStorageFactory produceStorageFactory() throws Exception {
        String fname = storageMode.getName();
        String classname = KeyManagement.class.getCanonicalName();
        Class<?> cls = Class.forName(classname.substring(0, classname.lastIndexOf(".")) + ".factory." + fname);
        return (IStorageFactory) cls.newInstance();
    }

    /**
     * 获取密钥实体
     *
     * @param keyType   密钥类型
     * @param traitid   申请者身份标识字符串
     * @param delaytime 密钥使用延迟时间
     * @param exptime   密钥过期时间
     * @param length    密钥长度（随机字符串密钥时有效）
     * @return 密钥实体
     */
    private static KeyEntity getEntity(KeyType keyType, String traitid, long delaytime, long exptime, int length) throws Exception {
        delaytime = delaytime > 0 ? delaytime : 0;
        exptime = exptime > 0 ? exptime : 0;
        IStorageFactory storageFactory = produceStorageFactory();
        KeyEntity entity = storageFactory.readEntity(traitid);
        long flag = entity == null ? -1 : entity.updateTime();
        if (flag == -1) {
            expiresKey(traitid);
            entity = KeyEntity.generateEntity(keyType, traitid, delaytime, exptime, length);
            storageFactory.savaEntity(entity);
            DelKEY.delskey(traitid, exptime);
        } else if (flag == 0) {
            storageFactory.savaEntity(entity);
        } else {
            storageFactory.savaEntity(entity);
            DelKEY.delskey(traitid, flag);
        }
        return entity;
    }

    private static int decodeUInt32(byte[] key, int start_index) {
        byte[] test = Arrays.copyOfRange(key, start_index, start_index + 4);
        return new BigInteger(test).intValue();
//      int int_24 = (key[start_index++] << 24) & 0xff;
//      int int_16 = (key[start_index++] << 16) & 0xff;
//      int int_8 = (key[start_index++] << 8) & 0xff;
//      int int_0 = key[start_index++] & 0xff;
//      return int_24 + int_16 + int_8 + int_0;
    }

    /**
     * 读取文件内容
     *
     * @param filePath 文件绝对路径
     * @return 字节数组
     */
    private static byte[] getFileContent(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException(filePath);
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
        int buf_size = 1024;
        byte[] buffer = new byte[buf_size];
        int len;
        while (-1 != (len = in.read(buffer, 0, buf_size))) {
            bos.write(buffer, 0, len);
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        bos.close();
        return bos.toByteArray();
    }

    /**
     * 获取临时AES密钥，过期时间内再次获取则取到相同的值，剩余时间小于延迟时间则延迟过期；过期时间之后取到新的值
     *
     * @param traitid   申请者身份标识字符串
     * @param delaytime 密钥使用延迟时间
     * @param exptime   密钥过期时间
     * @return 临时密钥
     */
    public static Key getTempAESKey(String traitid, long delaytime, long exptime) throws Exception {
        KeyEntity entity = getEntity(KeyType.AES, traitid, delaytime, exptime, 0);
        return entity.getAesKey();
    }

    /**
     * 获取临时RSA公私钥对，过期时间内再次获取则取到相同的值，剩余时间小于延迟时间则延迟过期；过期时间之后取到新的值
     *
     * @param traitid   申请者身份标识字符串
     * @param delaytime 密钥使用延迟时间
     * @param exptime   密钥过期时间
     * @return Object[] [0]=>RSAPublicKey,[1]=>RSAPrivateKey
     */
    public static Object[] getTempRSAKeys(String traitid, long delaytime, long exptime) throws Exception {
        KeyEntity entity = getEntity(KeyType.RSA, traitid, delaytime, exptime, 0);
        Object[] keys = new Object[2];
        keys[0] = entity.getRsaPublicKey();
        keys[1] = entity.getRsaPrivateKey();
        return keys;
    }

    /**
     * 获取临时DSA公私钥对，过期时间内再次获取则取到相同的值，剩余时间小于延迟时间则延迟过期；过期时间之后取到新的值
     *
     * @param traitid   申请者身份标识字符串
     * @param delaytime 密钥使用延迟时间
     * @param exptime   密钥过期时间
     * @return Object[] [0]=>DSAPublicKey,[1]=>DSAPrivateKey
     */
    public static Object[] getTempDSAKeys(String traitid, long delaytime, long exptime) throws Exception {
        KeyEntity entity = getEntity(KeyType.DSA, traitid, delaytime, exptime, 0);
        Object[] keys = new Object[2];
        keys[0] = entity.getRsaPublicKey();
        keys[1] = entity.getRsaPrivateKey();
        return keys;
    }

    /**
     * 获取临时随机字符串，过期时间内再次获取则取到相同的值，剩余时间小于延迟时间则延迟过期；过期时间之后取到新的值
     *
     * @param traitid   申请者身份标识字符串
     * @param delaytime 使用延迟时间
     * @param exptime   过期时间
     * @param flag      类型：RANDOM_STR | RANDOM_CHAR | RANDOM_NUMBER
     * @param length    随机字符串的长度
     * @return 临时随机字符串
     */
    public static String getTempRandomString(String traitid, long delaytime, long exptime, int flag, int length) throws Exception {
        KeyType keyType;
        if (flag == RANDOM_CHAR) {
            keyType = KeyType.RandomChar;
        } else if (flag == RANDOM_NUMBER) {
            keyType = KeyType.RandomNumber;
        } else {
            keyType = KeyType.RandomStr;
        }
        KeyEntity entity = getEntity(keyType, traitid, delaytime, exptime, length);
        return entity.getRandomString();
    }

    /**
     * 所有临时密钥立即过期
     */
    public static void expiresAllKeys() throws Exception {
        expiresKey(null);
    }

    /**
     * 指定的临时密钥立即过期
     *
     * @param traitid 唯一id，为空表示全部立即过期
     */
    public static void expiresKey(String traitid) throws Exception {
        IStorageFactory storageFactory = produceStorageFactory();
        if (CommonUtility.isNullStr(traitid)) {
            synchronized (temporaryThread) {
                temporaryThread.values().forEach(Thread::interrupt);
                temporaryThread.clear();
            }
            storageFactory.clear();
        } else {
            synchronized (temporaryThread) {
                Thread delThread = temporaryThread.get(traitid);
                if (delThread != null && !delThread.isInterrupted()) {
                    temporaryThread.get(traitid).interrupt();
                }
                temporaryThread.remove(traitid);
            }
            storageFactory.deleteEntity(traitid);
        }
    }

    /**
     * 生成AES密钥
     *
     * @param keyStr 长度不超过16位的字符串
     * @return 密钥
     */
    public static Key getAESKey(String keyStr) throws UnsupportedEncodingException {
        return new SecretKeySpec(keyStr.getBytes(encode), "AES");
    }

    /**
     * 生成RSA公钥和私钥
     *
     * @return Object[] [0]=>RSAPublicKey,[1]=>RSAPrivateKey
     */
    public static Object[] getRSAKeys() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Object[] keys = new Object[2];
        keys[0] = publicKey;
        keys[1] = privateKey;
        return keys;
    }

    /**
     * 生成DSA公钥和私钥
     *
     * @return Object[] [0]=>DSAPublicKey,[1]=>DSAPrivateKey
     */
    public static Object[] getDSAKeys() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("DSA");
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.setSeed(getRandomString(RANDOM_STR, 32).getBytes(encode));
        keyPairGen.initialize(1024, secureRandom);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        DSAPublicKey publicKey = (DSAPublicKey) keyPair.getPublic();
        DSAPrivateKey privateKey = (DSAPrivateKey) keyPair.getPrivate();
        Object[] keys = new Object[2];
        keys[0] = publicKey;
        keys[1] = privateKey;
        return keys;
    }

    /**
     * 使用模和指数生成RSA公钥
     * 注意：【此代码用了默认补位方式，为RSA/None/PKCS1Padding，不同JDK默认的补位方式可能不同，如Android默认是RSA/None/NoPadding】
     *
     * @param modulus  模
     * @param exponent 指数
     * @return 公钥
     */
    public static RSAPublicKey getRSAPublicKey(String modulus, String exponent) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return getRSAPublicKey(modulus, exponent, 10);
    }

    /**
     * 使用模和指数生成RSA公钥
     * 注意：【此代码用了默认补位方式，为RSA/None/PKCS1Padding，不同JDK默认的补位方式可能不同，如Android默认是RSA/None/NoPadding】
     *
     * @param modulus  模
     * @param exponent 指数
     * @param radix    基数 2，8，10，16
     * @return 公钥
     */
    public static RSAPublicKey getRSAPublicKey(String modulus, String exponent, int radix) throws NoSuchAlgorithmException, InvalidKeySpecException {
        BigInteger b1 = new BigInteger(modulus, radix);
        BigInteger b2 = new BigInteger(exponent, radix);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(b1, b2);
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    /**
     * 使用模和指数生成RSA私钥
     * 注意：【此代码用了默认补位方式，为RSA/None/PKCS1Padding，不同JDK默认的补位方式可能不同，如Android默认是RSA/None/NoPadding】
     *
     * @param modulus  模
     * @param exponent 指数
     * @return 私钥
     */
    public static RSAPrivateKey getRSAPrivateKey(String modulus, String exponent) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return getRSAPrivateKey(modulus, exponent, 10);
    }

    /**
     * 使用模和指数生成RSA私钥
     * 注意：【此代码用了默认补位方式，为RSA/None/PKCS1Padding，不同JDK默认的补位方式可能不同，如Android默认是RSA/None/NoPadding】
     *
     * @param modulus  模
     * @param exponent 指数
     * @param radix    基数 2，8，10，16
     * @return 私钥
     */
    public static RSAPrivateKey getRSAPrivateKey(String modulus, String exponent, int radix) throws NoSuchAlgorithmException, InvalidKeySpecException {
        BigInteger b1 = new BigInteger(modulus, radix);
        BigInteger b2 = new BigInteger(exponent, radix);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(b1, b2);
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    /**
     * 解析DER编码的公钥证书
     *
     * @param filePath 证书文件绝对路径
     * @return RSA公钥对象
     */
    public static RSAPublicKey getRSAPublicKeyForDER(String filePath) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
        byte[] key = getFileContent(filePath);
        KeySpec keySpec = new X509EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    /**
     * 解析DER编码的私钥证书
     *
     * @param filePath 证书文件绝对路径
     * @return RSA私钥对象
     */
    public static RSAPrivateKey getRSAPrivateKeyForDER(String filePath) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        byte[] key = getFileContent(filePath);
        KeySpec keySpec = new PKCS8EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    /**
     * 解析PEM编码的公钥证书
     *
     * @param filePath 证书文件绝对路径
     * @return RSA公钥对象
     */
    public static RSAPublicKey getRSAPublicKeyForPEM(String filePath) throws IOException {
        Security.addProvider(new BouncyCastleProvider());
        Reader rsaPublic = new FileReader(filePath);
        PEMParser publicPem = new PEMParser(rsaPublic);
        Object pubObject = publicPem.readObject();
        JcaPEMKeyConverter pemConverter = new JcaPEMKeyConverter();
        pemConverter.setProvider("BC");
        if (pubObject instanceof SubjectPublicKeyInfo) {
            return (RSAPublicKey) pemConverter.getPublicKey((SubjectPublicKeyInfo) pubObject);
        } else {
            return null;
        }
    }

    /**
     * 解析PEM编码的私钥证书
     *
     * @param filePath 证书文件绝对路径
     * @return RSA私钥对象
     */
    public static RSAPrivateKey getRSAPrivateKeyForPEM(String filePath) throws IOException {
        Security.addProvider(new BouncyCastleProvider());
        Reader rsaReader = new FileReader(filePath);
        PEMParser privatePem = new PEMParser(rsaReader);
        Object obj = privatePem.readObject();
        JcaPEMKeyConverter pemConverter = new JcaPEMKeyConverter();
        pemConverter.setProvider("BC");
        if (obj instanceof PEMKeyPair) {
            KeyPair keyPair = pemConverter.getKeyPair((PEMKeyPair) obj);
            return (RSAPrivateKey) keyPair.getPrivate();
        } else {
            return obj instanceof KeyPair ? (RSAPrivateKey) ((KeyPair) obj).getPrivate() : null;
        }
    }

    /**
     * 解析SSH生成的公钥证书
     *
     * @param keyStr 公钥字符串
     * @return RSA公钥对象
     */
    public static RSAPublicKey getRSAPublicKeyForSSH(String keyStr) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, Base64DecodingException {
        byte[] key = Base64.decode(keyStr);
        byte[] sshrsa = new byte[]{0, 0, 0, 7, 's', 's', 'h', '-', 'r', 's', 'a'};
        int start_index = sshrsa.length;
        /* Decode the public exponent */
        int len = decodeUInt32(key, start_index);
        start_index += 4;
        byte[] pe_b = new byte[len];
        for (int i = 0; i < len; i++) {
            pe_b[i] = key[start_index++];
        }
        BigInteger pe = new BigInteger(pe_b);
        /* Decode the modulus */
        len = decodeUInt32(key, start_index);
        start_index += 4;
        byte[] md_b = new byte[len];
        for (int i = 0; i < len; i++) {
            md_b[i] = key[start_index++];
        }
        BigInteger md = new BigInteger(md_b);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        KeySpec ks = new RSAPublicKeySpec(md, pe);
        return (RSAPublicKey) keyFactory.generatePublic(ks);
    }

    /**
     * 生成DSA公钥
     *
     * @param publickey y
     * @param prime     p
     * @param subprime  q
     * @param base      g
     * @return 公钥
     */
    public static DSAPublicKey getDSAPublicKey(String publickey, String prime, String subprime, String base) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return getDSAPublicKey(publickey, prime, subprime, base, 10);
    }

    /**
     * 生成DSA公钥
     *
     * @param publickey y
     * @param prime     p
     * @param subprime  q
     * @param base      g
     * @param radix     基数 2，8，10，16
     * @return 公钥
     */
    public static DSAPublicKey getDSAPublicKey(String publickey, String prime, String subprime, String base, int radix) throws NoSuchAlgorithmException, InvalidKeySpecException {
        BigInteger y = new BigInteger(publickey, radix);
        BigInteger p = new BigInteger(prime, radix);
        BigInteger q = new BigInteger(subprime, radix);
        BigInteger g = new BigInteger(base, radix);
        KeyFactory keyFactory = KeyFactory.getInstance("DSA");
        DSAPublicKeySpec keySpec = new DSAPublicKeySpec(y, p, q, g);
        return (DSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    /**
     * 生成DSA私钥
     *
     * @param privatekey x
     * @param prime      p
     * @param subprime   q
     * @param base       g
     * @return 私钥
     */
    public static DSAPrivateKey getDSAPrivateKey(String privatekey, String prime, String subprime, String base) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return getDSAPrivateKey(privatekey, prime, subprime, base, 10);
    }

    /**
     * 使用模和指数生成DSA私钥
     *
     * @param privatekey x
     * @param prime      p
     * @param subprime   q
     * @param base       g
     * @param radix      基数 2，8，10，16
     * @return 私钥
     */
    public static DSAPrivateKey getDSAPrivateKey(String privatekey, String prime, String subprime, String base, int radix) throws NoSuchAlgorithmException, InvalidKeySpecException {
        BigInteger x = new BigInteger(privatekey, radix);
        BigInteger p = new BigInteger(prime, radix);
        BigInteger q = new BigInteger(subprime, radix);
        BigInteger g = new BigInteger(base, radix);
        KeyFactory keyFactory = KeyFactory.getInstance("DSA");
        DSAPrivateKeySpec keySpec = new DSAPrivateKeySpec(x, p, q, g);
        return (DSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    /**
     * 解析DER编码的公钥证书
     *
     * @param filePath 证书文件绝对路径
     * @return DSA公钥对象
     */
    public static DSAPublicKey getDSAPublicKeyForDER(String filePath) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
        byte[] key = getFileContent(filePath);
        KeySpec keySpec = new X509EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance("DSA");
        return (DSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    /**
     * 解析DER编码的私钥证书
     *
     * @param filePath 证书文件绝对路径
     * @return DSA私钥对象
     */
    public static DSAPrivateKey getDSAPrivateKeyForDER(String filePath) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        byte[] key = getFileContent(filePath);
        KeySpec keySpec = new PKCS8EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance("DSA");
        return (DSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    /**
     * 解析PEM编码的公钥证书
     *
     * @param filePath 证书文件绝对路径
     * @return DSA公钥对象
     */
    public static DSAPublicKey getDSAPublicKeyForPEM(String filePath) throws IOException {
        Security.addProvider(new BouncyCastleProvider());
        Reader dsaPublic = new FileReader(filePath);
        PEMParser publicPem = new PEMParser(dsaPublic);
        Object pubObject = publicPem.readObject();
        JcaPEMKeyConverter pemConverter = new JcaPEMKeyConverter();
        pemConverter.setProvider("BC");
        if (pubObject instanceof SubjectPublicKeyInfo) {
            return (DSAPublicKey) pemConverter.getPublicKey((SubjectPublicKeyInfo) pubObject);
        } else {
            return null;
        }
    }

    /**
     * 解析PEM编码的私钥证书
     *
     * @param filePath 证书文件绝对路径
     * @return DSA私钥对象
     */
    public static DSAPrivateKey getDSAPrivateKeyForPEM(String filePath) throws IOException {
        Security.addProvider(new BouncyCastleProvider());
        Reader dsaReader = new FileReader(filePath);
        PEMParser privatePem = new PEMParser(dsaReader);
        Object obj = privatePem.readObject();
        JcaPEMKeyConverter pemConverter = new JcaPEMKeyConverter();
        pemConverter.setProvider("BC");
        if (obj instanceof PEMKeyPair) {
            KeyPair keyPair = pemConverter.getKeyPair((PEMKeyPair) obj);
            return (DSAPrivateKey) keyPair.getPrivate();
        } else {
            return obj instanceof KeyPair ? (DSAPrivateKey) ((KeyPair) obj).getPrivate() : null;
        }
    }

    /**
     * 解析SSH生成的公钥证书
     *
     * @param keyStr 公钥字符串
     * @return DSA公钥对象
     */
    public static DSAPublicKey getDSAPublicKeyForSSH(String keyStr) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, Base64DecodingException {
        byte[] key = Base64.decode(keyStr);
        byte[] sshdsa = new byte[]{0, 0, 0, 7, 's', 's', 'h', '-', 'd', 's', 's'};
        int start_index = sshdsa.length;
        /* Decode the p */
        int len = decodeUInt32(key, start_index);
        start_index += 4;
        byte[] p_b = new byte[len];
        for (int i = 0; i < len; i++) {
            p_b[i] = key[start_index++];
        }
        BigInteger p = new BigInteger(p_b);
        /* Decode the q */
        len = decodeUInt32(key, start_index);
        start_index += 4;
        byte[] q_b = new byte[len];
        for (int i = 0; i < len; i++) {
            q_b[i] = key[start_index++];
        }
        BigInteger q = new BigInteger(q_b);
        /* Decode the g */
        len = decodeUInt32(key, start_index);
        start_index += 4;
        byte[] g_b = new byte[len];
        for (int i = 0; i < len; i++) {
            g_b[i] = key[start_index++];
        }
        BigInteger g = new BigInteger(g_b);
        /* Decode the y */
        len = decodeUInt32(key, start_index);
        start_index += 4;
        byte[] y_b = new byte[len];
        for (int i = 0; i < len; i++) {
            y_b[i] = key[start_index++];
        }
        BigInteger y = new BigInteger(y_b);
        KeyFactory keyFactory = KeyFactory.getInstance("DSA");
        KeySpec ks = new DSAPublicKeySpec(y, p, q, g);
        return (DSAPublicKey) keyFactory.generatePublic(ks);
    }

    /**
     * 生成随机字符串
     *
     * @param flag   类型：RANDOM_STR | RANDOM_CHAR | RANDOM_NUMBER
     * @param length 长度
     * @return 随机字符串
     */
    public static String getRandomString(int flag, long length) {
        String[] chars;
        int count;
        if (flag == RANDOM_CHAR) {
            chars = new String[]{"a", "b", "c", "d", "e", "f", "g", "h",
                    "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
                    "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H",
                    "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
                    "U", "V", "W", "X", "Y", "Z"};
            count = 52;
        } else if (flag == RANDOM_NUMBER) {
            chars = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
            count = 10;
        } else if (flag == RANDOM_STR) {
            chars = new String[]{"a", "b", "c", "d", "e", "f", "g", "h",
                    "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
                    "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
                    "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H",
                    "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
                    "U", "V", "W", "X", "Y", "Z"};
            count = 62;
        } else {
            chars = new String[]{"a", "b", "c", "d", "e", "f", "g", "h",
                    "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
                    "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
                    "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H",
                    "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
                    "U", "V", "W", "X", "Y", "Z"};
            count = 62;
        }
        StringBuilder shortBuffer = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            shortBuffer.append(chars[random.nextInt(count)]);
        }
        return shortBuffer.toString();
    }

}