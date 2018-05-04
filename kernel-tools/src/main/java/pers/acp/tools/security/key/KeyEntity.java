package pers.acp.tools.security.key;

import pers.acp.tools.security.key.enums.KeyType;
import pers.acp.tools.utility.CommonUtility;

import java.io.Serializable;
import java.security.Key;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;

public class KeyEntity implements Serializable {

    private KeyType keyType;

    private String traitId;

    private Date generateTime;

    private Date lastuseTime;

    private long delaytime;

    private long exptime;

    private Key aesKey;

    private RSAPublicKey rsaPublicKey;

    private RSAPrivateKey rsaPrivateKey;

    private DSAPublicKey dsaPublicKey;

    private DSAPrivateKey dsaPrivateKey;

    private String randomString;

    public String getTraitId() {
        return traitId;
    }

    private void setTraitId(String traitId) {
        this.traitId = traitId;
    }

    private void setGenerateTime(Date generateTime) {
        this.generateTime = generateTime;
    }

    private void setLastuseTime(Date lastuseTime) {
        this.lastuseTime = lastuseTime;
    }

    private void setDelaytime(long delaytime) {
        this.delaytime = delaytime;
    }

    private void setExptime(long exptime) {
        this.exptime = exptime;
    }

    Key getAesKey() {
        return aesKey;
    }

    private void setAesKey(Key aesKey) {
        this.aesKey = aesKey;
    }

    RSAPublicKey getRsaPublicKey() {
        return rsaPublicKey;
    }

    private void setRsaPublicKey(RSAPublicKey rsaPublicKey) {
        this.rsaPublicKey = rsaPublicKey;
    }

    RSAPrivateKey getRsaPrivateKey() {
        return rsaPrivateKey;
    }

    private void setRsaPrivateKey(RSAPrivateKey rsaPrivateKey) {
        this.rsaPrivateKey = rsaPrivateKey;
    }

    public DSAPrivateKey getDsaPrivateKey() {
        return dsaPrivateKey;
    }

    public void setDsaPrivateKey(DSAPrivateKey dsaPrivateKey) {
        this.dsaPrivateKey = dsaPrivateKey;
    }

    public DSAPublicKey getDsaPublicKey() {
        return dsaPublicKey;
    }

    public void setDsaPublicKey(DSAPublicKey dsaPublicKey) {
        this.dsaPublicKey = dsaPublicKey;
    }

    String getRandomString() {
        return randomString;
    }

    private void setRandomString(String randomString) {
        this.randomString = randomString;
    }

    private KeyEntity(KeyType keyType) {
        this.keyType = keyType;
    }

    /**
     * 更新密钥实体时间
     *
     * @return 0-不重启删除线程，>0重启删除线程的删除延迟时间，-1密钥已作废
     */
    long updateTime() throws Exception {
        if (keyType == null) {
            throw new SecurityException("key type is null,first use generateEntity function!");
        }
        long generatetime = generateTime.getTime();
        lastuseTime = new Date();
        long nowtiime = lastuseTime.getTime();
        if (generatetime + exptime < nowtiime) {
            return -1;
        } else if (generatetime + exptime < nowtiime + delaytime) {
            return delaytime;
        }
        return 0;
    }

    /**
     * 生成密钥实体
     *
     * @param keyType   密钥类型
     * @param traitid   申请者身份标识字符串
     * @param delaytime 密钥使用延迟时间
     * @param exptime   密钥过期时间
     * @param length    密钥长度（随机字符串密钥时有效）
     * @return 密钥实体
     */
    static KeyEntity generateEntity(KeyType keyType, String traitid, long delaytime, long exptime, int length) throws Exception {
        KeyEntity entity = new KeyEntity(keyType);
        switch (keyType) {
            case AES:
                String keyStr = CommonUtility.getUuid16();
                Key key = KeyManagement.getAESKey(keyStr);
                entity.setAesKey(key);
                break;
            case RSA:
                Object[] rsakeys = KeyManagement.getRSAKeys();
                entity.setRsaPublicKey((RSAPublicKey) rsakeys[0]);
                entity.setRsaPrivateKey((RSAPrivateKey) rsakeys[1]);
                break;
            case DSA:
                Object[] dsakeys = KeyManagement.getDSAKeys();
                entity.setDsaPublicKey((DSAPublicKey) dsakeys[0]);
                entity.setDsaPrivateKey((DSAPrivateKey) dsakeys[1]);
                break;
            case RandomStr:
                entity.setRandomString(KeyManagement.getRandomString(KeyManagement.RANDOM_STR, length));
                break;
            case RandomNumber:
                entity.setRandomString(KeyManagement.getRandomString(KeyManagement.RANDOM_NUMBER, length));
                break;
            case RandomChar:
                entity.setRandomString(KeyManagement.getRandomString(KeyManagement.RANDOM_CHAR, length));
                break;
        }
        entity.setTraitId(traitid);
        Date now = new Date();
        entity.setGenerateTime(now);
        entity.setLastuseTime(now);
        entity.setDelaytime(delaytime);
        entity.setExptime(exptime);
        return entity;
    }

}
