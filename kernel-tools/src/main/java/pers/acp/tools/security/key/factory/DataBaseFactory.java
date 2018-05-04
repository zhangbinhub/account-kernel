package pers.acp.tools.security.key.factory;

import pers.acp.tools.dbconnection.ConnectionFactory;
import pers.acp.tools.security.key.KeyEntity;
import pers.acp.tools.security.key.KeyManagement;
import pers.acp.tools.utility.CommonUtility;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhang on 2016/7/19.
 * 数据库存储
 */
public class DataBaseFactory implements IStorageFactory {

    @Override
    public KeyEntity readEntity(String traitid) throws Exception {
        ConnectionFactory dbcon = getTool();
        Map<String, Object> param = new HashMap<>();
        param.put(KeyManagement.keyCol, traitid);
        InputStream in = dbcon.doQueryLOB(KeyManagement.tablename, param, KeyManagement.objCol);
        if (in != null) {
            ObjectInputStream ois = new ObjectInputStream(in);
            KeyEntity entity = (KeyEntity) ois.readObject();
            ois.close();
            in.close();
            return entity;
        } else {
            return null;
        }
    }

    @Override
    public void savaEntity(KeyEntity entity) throws Exception {
        ConnectionFactory dbcon = getTool();
        Map<String, Object> param = new HashMap<>();
        param.put(KeyManagement.keyCol, entity.getTraitId());
        dbcon.beginTranslist();
        InputStream in = dbcon.doQueryLOB(KeyManagement.tablename, param, KeyManagement.objCol);
        if (in != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream objOuts = new ObjectOutputStream(out);
            objOuts.writeObject(entity);
            final byte[] objBytes = out.toByteArray();
            in = new ByteArrayInputStream(objBytes);
            dbcon.doUpdateLOB(KeyManagement.tablename, param, KeyManagement.objCol, in);
        } else {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream objOuts = new ObjectOutputStream(out);
            objOuts.writeObject(entity);
            final byte[] objBytes = out.toByteArray();
            in = new ByteArrayInputStream(objBytes);
            dbcon.doInsertLOB(KeyManagement.tablename, param, KeyManagement.objCol, in);
        }
        dbcon.commitTranslist();
    }

    @Override
    public void deleteEntity(String traitid) throws Exception {
        ConnectionFactory dbcon = getTool();
        Object[] pas = new Object[1];
        pas[0] = traitid;
        dbcon.doUpdate("delete from " + KeyManagement.tablename + " where " + KeyManagement.keyCol + "=?", pas);
    }

    @Override
    public void clear() throws Exception {
        ConnectionFactory dbcon = getTool();
        dbcon.doUpdate("delete from " + KeyManagement.tablename);
    }

    private ConnectionFactory getTool() {
        if (CommonUtility.isNullStr(KeyManagement.tablename)) {
            throw new SecurityException("the tablename is null or undefined!");
        }
        if (CommonUtility.isNullStr(KeyManagement.keyCol)) {
            throw new SecurityException("the keyCol is null or undefined!");
        }
        if (CommonUtility.isNullStr(KeyManagement.objCol)) {
            throw new SecurityException("the objCol is null or undefined!");
        }
        ConnectionFactory dbcon;
        if (!CommonUtility.isNullStr(KeyManagement.storageParam)) {
            dbcon = new ConnectionFactory(Integer.valueOf(KeyManagement.storageParam));
        } else {
            dbcon = new ConnectionFactory();
        }
        return dbcon;
    }
}
