package pers.acp.tools.security.key.factory;

import pers.acp.tools.security.key.KeyEntity;

/**
 * Created by zhang on 2016/7/19.
 * 存储接口
 */
public interface IStorageFactory {

    /**
     * 读取密钥实体
     *
     * @param traitid 申请者身份标识字符串
     * @return 密钥实体
     */
    KeyEntity readEntity(String traitid) throws Exception;

    /**
     * 存储密钥实体
     *
     * @param entity 密钥实体
     */
    void savaEntity(KeyEntity entity) throws Exception;

    /**
     * 删除密钥实体
     *
     * @param traitid 申请者身份标识字符串
     */
    void deleteEntity(String traitid) throws Exception;

    /**
     * 清理存储中所有密钥实体
     */
    void clear() throws Exception;

}
