package pers.acp.tools.file.common;

import pers.acp.tools.config.instance.SystemConfig;
import pers.acp.tools.exceptions.ConfigException;
import pers.acp.tools.utility.CommonUtility;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileCommon {

    private static String DEFAULT_CHARSET = "utf-8";

    private static Logger log = Logger.getLogger(FileCommon.class);

    private static Properties pps;

    /**
     * 获取系统默认字符集
     *
     * @return 字符集
     */
    public static String getDefaultCharset() {
        String charset = DEFAULT_CHARSET;
        SystemConfig systemConfig;
        try {
            systemConfig = SystemConfig.getInstance();
        } catch (ConfigException e) {
            systemConfig = null;
        }
        if (systemConfig != null) {
            String charset_tmp = systemConfig.getDefaultCharset();
            if (!CommonUtility.isNullStr(charset_tmp)) {
                charset = charset_tmp;
            }
        }
        return charset;
    }

    /**
     * 初始化系统配置文件
     */
    public static void initSystemProperties() {
        try {
            if (pps == null) {
                pps = new Properties();
            }
            pps.load(FileCommon.class.getClassLoader().getResourceAsStream("acp.properties"));
            log.info("load acp.properties successfull!");
        } catch (Exception e) {
            log.error("load acp.properties failed!");
        }
    }

    /**
     * 获取配置信息
     *
     * @param key 键
     * @return 值
     */
    public static String getProperties(String key) {
        if (pps == null) {
            initSystemProperties();
        }
        return pps.getProperty(key);
    }

    /**
     * 获取配置信息
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 值
     */
    public static String getProperties(String key, String defaultValue) {
        if (pps == null) {
            initSystemProperties();
        }
        return pps.getProperty(key, defaultValue);
    }

    /**
     * 判断路径是否是绝对路径
     *
     * @param path 路径
     * @return 是否是绝对路径
     */
    public static boolean isAbsPath(String path) {
        String prefix = getProperties("abspath.prefix", "abs:");
        String prefixu = getProperties("userpath.prefix", "user:");
        return path.startsWith(prefix) || !(path.startsWith(prefixu) || path.startsWith("/") || path.startsWith("\\") || path.startsWith(File.separator));
    }

    /**
     * 获取绝对路径
     *
     * @param srcPath 路径
     * @return 绝对路径
     */
    public static String getAbsPath(String srcPath) {
        String path = srcPath.replace("\\", File.separator).replace("/", File.separator);
        if (isAbsPath(path)) {
            String prefix = getProperties("abspath.prefix", "abs:");
            if (path.startsWith(prefix)) {
                return path.substring(prefix.length());
            } else {
                return path;
            }
        } else {
            String prefix = getProperties("userpath.prefix", "user:");
            if (path.startsWith(prefix)) {
                return System.getProperty("user.home") + path.substring(prefix.length());
            } else {
                return getWebRootAbsPath() + path;
            }
        }
    }

    /**
     * 表达式变量替换
     *
     * @param varFormula 变量表达式:格式“${变量名}” 或带有变量格式的字符串
     * @param data       数据集
     * @return 目标字符串
     */
    public static String replaceVar(String varFormula, Map<String, String> data) {
        String tmpvar = varFormula;
        if (tmpvar.contains("${")) {
            if (tmpvar.contains("}")) {
                int begin = tmpvar.indexOf("${");
                while (begin != -1) {
                    tmpvar = tmpvar.substring(begin + 2);
                    if (tmpvar.contains("}")) {
                        String var = tmpvar.substring(0, tmpvar.indexOf("}"));
                        if (data.containsKey(var)) {
                            varFormula = varFormula.replace("${" + var + "}", data.get(var));
                        }
                        tmpvar = tmpvar.substring(tmpvar.indexOf("}") + 1);
                        begin = tmpvar.indexOf("${");
                    } else {
                        begin = -1;
                    }
                }
            }
            return varFormula;
        } else {
            return varFormula;
        }
    }

    /**
     * 获取webroot绝对路径
     *
     * @return webroot绝对路径
     */
    public static String getWebRootAbsPath() {
        try {
            String classPath = URLDecoder.decode(FileCommon.class.getResource("/").getPath(), DEFAULT_CHARSET);
            int indexWEB_INF = classPath.indexOf("WEB-INF");
            if (indexWEB_INF == -1) {
                indexWEB_INF = classPath.indexOf("bin");
            }
            String webrootpath = classPath;
            if (indexWEB_INF != -1) {
                webrootpath = webrootpath.substring(0, indexWEB_INF);
            }
            if (webrootpath.startsWith("jar")) {
                webrootpath = webrootpath.substring(10);
            } else if (webrootpath.startsWith("file")) {
                webrootpath = webrootpath.substring(6);
            } else {
                ClassLoader classLoader = FileCommon.class.getClassLoader();
                URL url = classLoader.getResource("/");
                if (url == null) {
                    log.error("webRootAbsPath=\"\"");
                    return "";
                }
                classPath = url.getPath();
                indexWEB_INF = classPath.indexOf("WEB-INF");
                if (indexWEB_INF == -1) {
                    indexWEB_INF = classPath.indexOf("bin");
                }
                if (indexWEB_INF != -1) {
                    webrootpath = classPath.substring(0, indexWEB_INF);
                } else {
                    webrootpath = classPath;
                }
            }
            if (webrootpath.endsWith("/")) {
                webrootpath = webrootpath.substring(0, webrootpath.length() - 1);
            }
            webrootpath = webrootpath.replace("/", File.separator);
            if (webrootpath.startsWith("\\")) {
                webrootpath = webrootpath.substring(1);
            }
            return webrootpath;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("webRootAbsPath=\"\"");
            return "";
        }
    }

    /**
     * 获取文件基路径
     *
     * @param filePath 文件绝对路径
     * @return 基路径
     */
    public static String getFileBaseURL(String filePath) {
        try {
            if (CommonUtility.isNullStr(filePath)) {
                filePath = getWebRootAbsPath();
            }
            return new File(filePath).toURI().toURL().toString();
        } catch (Exception e) {
            log.error("Get file baseURL is failed");
            return null;
        }
    }

    /**
     * 获取文件中的内容
     *
     * @param filePath 文件绝对路径
     * @return 内容
     */
    public static String getFileContent(String filePath) {
        FileInputStream fis = null;
        FileChannel channel = null;
        try {
            StringBuilder buff = new StringBuilder("");
            fis = new FileInputStream(filePath);
            channel = fis.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while (true) {
                int size = channel.read(buffer);
                if (size == -1) {
                    break;
                }
                byte[] bt = buffer.array();
                buff.append(new String(bt, 0, size, SystemConfig.getInstance().getDefaultCharset()));
                buffer.clear();
            }
            return buff.toString();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        } finally {
            try {
                if (channel != null) {
                    channel.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 获取文件扩展名
     *
     * @param fileName 文件名称
     * @return 扩展名（小写）
     */

    public static String getFileExt(String fileName) {
        if (fileName.lastIndexOf(".") > -1) {
            return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        } else {
            return "";
        }
    }

    /**
     * 十六进制制字符串转图片文件
     *
     * @param HexString      十六进字符串
     * @param FileName       文件名
     * @param ExtensionsName 扩展名
     * @param PathFlag       生成图片文件路径标志:0-相对于WebRoot；1-自定义
     * @param ResultPathFlag 返回文件路径标志:0-相对于WebRoot；1-绝对路径
     * @param ParentPath     生成图片所在目录
     * @param isDelete       是否异步删除临时图片
     * @return 临时图片路径
     */
    public static String HexToImage(String HexString, String FileName, String ExtensionsName, int PathFlag, int ResultPathFlag, String ParentPath, boolean isDelete) {
        String fileAbsPath;
        File tmpFile = null;
        FileOutputStream out = null;
        String webRootAbsPath = getWebRootAbsPath();
        try {
            if (PathFlag == 0) {
                tmpFile = new File(webRootAbsPath + File.separator + "files" + File.separator + "tmp" + File.separator + FileName + "." + ExtensionsName);
            } else {
                tmpFile = new File(ParentPath + File.separator + FileName + "." + ExtensionsName);
            }
            byte[] bytes = CommonUtility.hex2byte(HexString);
            out = new FileOutputStream(tmpFile);
            out.write(bytes);
            out.flush();
            out.close();
            if (ResultPathFlag == 0) {
                fileAbsPath = tmpFile.getAbsolutePath().replace(webRootAbsPath, "").replace(File.separator, "/");
            } else {
                fileAbsPath = tmpFile.getAbsolutePath().replace(File.separator, "/");
            }
            if (isDelete) {
                SystemConfig systemConfig = null;
                try {
                    systemConfig = SystemConfig.getInstance();
                } catch (ConfigException e) {
                    log.error(e.getMessage(), e);
                }
                if (systemConfig == null) {
                    FileDelete.doDeleteFile(tmpFile, true);
                } else {
                    FileDelete.doDeleteFile(tmpFile, true, systemConfig.getDeleteWaitTime());
                }
            }
        } catch (Exception e) {
            log.error("generate image faild:" + e.getMessage(), e);
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            } finally {
                if (tmpFile != null && tmpFile.exists()) {
                    tmpFile.delete();
                }
            }
            fileAbsPath = "";
        }
        return fileAbsPath;
    }

    /**
     * 生成临时文件夹
     *
     * @return 临时文件夹绝对路径
     */
    public static String buildTmpDir() {
        String webRootAdsPath = getWebRootAbsPath();
        File file = new File(webRootAdsPath + File.separator + "files"
                + File.separator + "tmp");
        if (!file.exists()) {
            file.mkdir();
        }
        return file.getAbsolutePath();
    }

    /**
     * 获取模板文件夹
     *
     * @return 模板文件夹绝对路径
     */
    public static String buildTemplateDir() {
        String webRootAdsPath = getWebRootAbsPath();
        File file = new File(webRootAdsPath + File.separator + "files"
                + File.separator + "template");
        if (!file.exists()) {
            file.mkdir();
        }
        return file.getAbsolutePath();
    }

    /**
     * 压缩文件
     *
     * @param fileNames      需要压缩的文件路径数组，可以是全路径也可以是相对于webroot的路径
     * @param resultFileName 生成的目标文件全路径
     * @param isDeleteFile   压缩完后是否删除原文件
     * @return 目标文件绝对路径
     */
    public static String filesToZIP(String[] fileNames, String resultFileName, boolean isDeleteFile) {
        byte[] buf = new byte[1024];
        FileInputStream in = null;
        ZipOutputStream out = null;
        try {
            out = new ZipOutputStream(new FileOutputStream(resultFileName));
            for (String fileName : fileNames) {
                String filename = fileName.replace("\\", File.separator).replace("/", File.separator);
                File srcfile = new File(filename);
                in = new FileInputStream(filename);
                out.putNextEntry(new ZipEntry(srcfile.getName()));
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.closeEntry();
                in.close();
                if (isDeleteFile) {
                    srcfile.delete();
                }
            }
            out.close();
            log.info("compress success");
            return resultFileName;
        } catch (Exception e) {
            try {
                if (out != null) {
                    out.closeEntry();
                }
                if (in != null) {
                    in.close();
                }
                new File(resultFileName).delete();
            } catch (Exception ex) {
                ex.printStackTrace();
                log.error("file compress Exception:" + ex.getMessage(), ex);
            }
            log.error("file compress Exception:" + e.getMessage(), e);
            return "";
        }
    }

    /**
     * 解压缩文件
     *
     * @param zipFileName  zip压缩文件名
     * @param parentFold   解压目标文件夹
     * @param isDeleteFile 解压完成是否删除压缩文件
     */
    public static void ZIPToFiles(String zipFileName, String parentFold, boolean isDeleteFile) {
        try {
            ZipInputStream zin = new ZipInputStream(new FileInputStream(zipFileName));
            BufferedInputStream bin = new BufferedInputStream(zin);
            File Fout;
            ZipEntry entry;
            while ((entry = zin.getNextEntry()) != null && !entry.isDirectory()) {
                Fout = new File(parentFold, entry.getName());
                if (!Fout.exists()) {
                    (new File(Fout.getParent())).mkdirs();
                }
                FileOutputStream out = new FileOutputStream(Fout);
                BufferedOutputStream Bout = new BufferedOutputStream(out);
                int b;
                while ((b = bin.read()) != -1) {
                    Bout.write(b);
                }
                Bout.close();
                out.close();
                System.out.println(Fout + "解压成功");
            }
            bin.close();
            zin.close();
            if (isDeleteFile) {
                new File(zipFileName).delete();
            }
            log.info("decompress success");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("file Exception:" + e.getMessage(), e);
        }
    }

    /**
     * 删除文件
     *
     * @param file   待删除文件
     * @param isSync 是否异步删除
     */
    public static void doDeleteFile(final File file, boolean isSync) {
        SystemConfig systemConfig = null;
        try {
            systemConfig = SystemConfig.getInstance();
        } catch (ConfigException e) {
            log.error(e.getMessage(), e);
        }
        if (systemConfig == null) {
            FileDelete.doDeleteFile(file, isSync);
        } else {
            long waittime = systemConfig.getDeleteWaitTime();
            if (waittime > 0) {
                FileDelete.doDeleteFile(file, isSync, waittime);
            } else {
                FileDelete.doDeleteFile(file, isSync);
            }

        }
    }

    /**
     * 删除文件
     *
     * @param file     待删除文件
     * @param isSync   是否异步删除
     * @param waitTime 异步删除等待时间
     */
    public static void doDeleteFile(final File file, boolean isSync, long waitTime) {
        FileDelete.doDeleteFile(file, isSync, waitTime);
    }

    /**
     * 删除文件夹
     *
     * @param dir 将要删除的文件目录
     */
    public static void doDeleteDir(File dir) {
        if (dir.exists()) {
            boolean result = FileDelete.doDeleteDir(dir);
            if (result) {
                log.info("delete fold [" + dir.getAbsolutePath() + "] success!");
            } else {
                log.info("delete fold [" + dir.getAbsolutePath() + "] failed!");
            }
        }
    }

}
