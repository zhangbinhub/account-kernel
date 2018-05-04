package pers.acp.tools.security.key.factory;

import pers.acp.tools.file.common.FileCommon;
import pers.acp.tools.security.key.KeyEntity;
import pers.acp.tools.security.key.KeyManagement;
import pers.acp.tools.utility.CommonUtility;

import java.io.*;

/**
 * Created by zhang on 2016/7/19.
 * 文件存储
 */
public class FileFactory implements IStorageFactory {

    @Override
    public KeyEntity readEntity(String traitid) throws Exception {
        File fold = getFold();
        File file = new File(fold.getAbsoluteFile() + File.separator + traitid);
        if (file.exists() && !file.isDirectory()) {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            KeyEntity entity = (KeyEntity) ois.readObject();
            ois.close();
            return entity;
        } else {
            return null;
        }
    }

    @Override
    public void savaEntity(KeyEntity entity) throws Exception {
        File fold = getFold();
        File file = new File(fold.getAbsoluteFile() + File.separator + entity.getTraitId());
        ObjectOutputStream oo = new ObjectOutputStream(new FileOutputStream(file));
        oo.writeObject(entity);
        oo.close();
    }

    @Override
    public void deleteEntity(String traitid) throws Exception {
        File fold = getFold();
        File file = new File(fold.getAbsoluteFile() + File.separator + traitid);
        if (file.exists() && !file.isDirectory()) {
            file.delete();
        }
    }

    @Override
    public void clear() throws Exception {
        File fold = getFold();
        File[] files = fold.listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
    }

    private File getFold() throws Exception {
        if (!CommonUtility.isNullStr(KeyManagement.storageParam)) {
            String foldPath = FileCommon.getAbsPath(KeyManagement.storageParam);
            File fold = new File(foldPath);
            if ((!fold.exists() || !fold.isDirectory()) && !fold.mkdirs()) {
                throw new SecurityException("con't find storage path!");
            }
            return fold;
        } else {
            throw new SecurityException("the storageParam is null or undefined!");
        }
    }
}
