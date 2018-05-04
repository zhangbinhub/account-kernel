package pers.acp.tools.file.common;

import org.apache.log4j.Logger;

import java.io.File;

public final class FileDelete extends Thread {

    private static Logger log = Logger.getLogger(FileDelete.class);

    private final File file;

    /**
     * 删除文件等待时间，单位:毫秒，默认1200000毫秒（20分钟）
     */
    private long waitTime = 1200000;

    private FileDelete(final File file) {
        this.file = file;
        this.setDaemon(true);
    }

    private FileDelete(final File file, long waitTime) {
        this(file);
        this.waitTime = waitTime;
    }

    /**
     * 删除文件
     *
     * @param file   待删除的文件
     * @param isSync 是否异步删除
     */
    protected static void doDeleteFile(File file, boolean isSync) {
        doDeleteFile(file, isSync, 0);
    }

    /**
     * 删除文件
     *
     * @param file     待删除的文件
     * @param isSync   是否异步删除
     * @param waitTime 异步删除等待时间
     */
    protected static void doDeleteFile(File file, boolean isSync, long waitTime) {
        if (isSync) {
            if (waitTime == 0) {
                new FileDelete(file).start();
            } else {
                new FileDelete(file, waitTime).start();
            }
        } else {
            try {
                if (file.exists()) {
                    if (file.delete()) {
                        log.info("delete file [" + file.getAbsolutePath() + "] success!");
                    } else {
                        log.info("delete file [" + file.getAbsolutePath() + "] failed!");
                    }
                }
            } catch (Exception e) {
                log.error("delete file Exception:" + e.getMessage(), e);
            }
        }
    }

    /**
     * 删除文件夹
     *
     * @param dir 将要删除的文件目录
     * @return
     */
    protected static boolean doDeleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String aChildren : children) {
                    boolean success = doDeleteDir(new File(dir, aChildren));
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        return dir.delete();
    }

    @Override
    public void run() {
        try {
            log.info("ready delete file [" + file.getAbsolutePath() + "],waitting " + (waitTime) / 1000 + " seconds");
            FileDelete.sleep(waitTime);
            if (file.exists()) {
                if (file.delete()) {
                    log.info("delete file [" + file.getAbsolutePath() + "] success!");
                } else {
                    log.info("delete file [" + file.getAbsolutePath() + "] failed!");
                }
            }
        } catch (Exception e) {
            log.error("delete file Exception:" + e.getMessage(), e);
        }
    }
}
