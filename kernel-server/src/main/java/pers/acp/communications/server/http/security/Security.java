package pers.acp.communications.server.http.security;

import pers.acp.communications.server.http.servlet.base.BaseServletHandle;
import pers.acp.communications.server.http.servlet.handle.HttpServletRequestAcp;
import pers.acp.communications.server.http.servlet.handle.HttpServletResponseAcp;
import pers.acp.tools.config.instance.SystemConfig;
import pers.acp.tools.exceptions.ConfigException;
import pers.acp.tools.security.AESUtils;
import pers.acp.tools.security.RSAUtils;
import pers.acp.tools.security.key.KeyManagement;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * 前端加密解密类
 *
 * @author zb
 */
public final class Security extends BaseServletHandle {

    private static Logger log = Logger.getLogger(Security.class);// 日志对象

    /**
     * 密钥使用延迟时间，默认2分钟
     */
    private static long KEY_DELAYTIME = 120000;

    /**
     * 密钥过期时间，默认30分钟
     */
    private static long KEY_EXPTIME = 1800000;

    static {
        try {
            SystemConfig.Security seConf = SystemConfig.getInstance().getSecurity();
            if (seConf != null) {
                long delaytime = seConf.getKeyDelayTime();
                if (delaytime > 0) {
                    KEY_DELAYTIME = delaytime;
                }
                long exptime = seConf.getExpirationTime();
                if (exptime > 0) {
                    KEY_EXPTIME = exptime;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public Security(HttpServletRequestAcp request, HttpServletResponseAcp response) throws ConfigException {
        super(request, response);
    }

    /**
     * 获取证书，返回前端
     */
    public void getPublicKey() {
        try {
            JSONObject result = new JSONObject();
            String traitID = request.getParameter("traitid");
            RSAPublicKey publicKey = (RSAPublicKey) KeyManagement.getTempRSAKeys(traitID, KEY_DELAYTIME, KEY_EXPTIME)[0];
            String modulus = Hex.encodeHexString(publicKey.getModulus().toByteArray());
            String exponent = Hex.encodeHexString(publicKey.getPublicExponent().toByteArray());
            result.put("modulus", modulus);
            result.put("exponent", exponent);
            response.doReturn(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
            response.doReturnError("get certificate faild!");
        }
    }

    /**
     * 解密前端密文
     *
     * @param jsonObj json对象
     * @return 明文
     */
    public static String doDecrypt(JSONObject jsonObj) {
        String result;
        try {
            String traitID = jsonObj.getString("traitid");
            String encryptkey = jsonObj.getString("key");
            String encryptedstr = jsonObj.getString("encryptedstr");
            RSAPrivateKey privateKey = (RSAPrivateKey) KeyManagement.getTempRSAKeys(traitID, KEY_DELAYTIME, KEY_EXPTIME)[1];
            String keyStr = RSAUtils.decryptByPrivateKey(encryptkey, privateKey);// RSA解密
            result = AESUtils.decrypt(encryptedstr, KeyManagement.getAESKey(keyStr));// AES解密
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result = "";
        }
        return result;
    }
}
