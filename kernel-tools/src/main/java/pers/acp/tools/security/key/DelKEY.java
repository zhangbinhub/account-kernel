package pers.acp.tools.security.key;

import pers.acp.tools.security.key.factory.IStorageFactory;
import org.apache.log4j.Logger;

/**
 * Created by zhang on 2016/7/19.
 * 删除临时密钥线程
 */
class DelKEY implements Runnable {

    private Logger log = Logger.getLogger(this.getClass());

    private long waittime;

    private String traitID;

    private DelKEY(String traitID, long waittime) {
        this.traitID = traitID;
        this.waittime = waittime;
    }

    /**
     * 删除过期密钥
     *
     * @param traitID  映射ID
     * @param waittime 删除等待时间
     */
    static void delskey(String traitID, long waittime) {
        Thread thread = new Thread(new DelKEY(traitID, waittime));
        thread.setDaemon(true);
        synchronized (KeyManagement.temporaryThread) {
            if (KeyManagement.temporaryThread.containsKey(traitID)) {
                KeyManagement.temporaryThread.get(traitID).interrupt();
                KeyManagement.temporaryThread.remove(traitID);
            }
            KeyManagement.temporaryThread.put(traitID, thread);
        }
        thread.start();
    }

    @Override
    public void run() {
        try {
            try {
                Thread.sleep(waittime);
            } catch (InterruptedException interrupte) {
                return;
            }
            doDelete();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 执行删除
     */
    private void doDelete() throws Exception {
        IStorageFactory storageFactory = KeyManagement.produceStorageFactory();
        storageFactory.deleteEntity(traitID);
        synchronized (KeyManagement.temporaryThread) {
            KeyManagement.temporaryThread.remove(traitID);
        }
    }

}
