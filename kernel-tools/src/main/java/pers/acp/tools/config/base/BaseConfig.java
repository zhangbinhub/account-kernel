package pers.acp.tools.config.base;

import pers.acp.tools.exceptions.ConfigException;
import pers.acp.tools.file.common.FileCommon;
import pers.acp.tools.utility.CommonUtility;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseConfig {

    private static Logger log = Logger.getLogger(BaseConfig.class);// 日志对象

    private static final Map<String, BaseConfig> instanceMap = new ConcurrentHashMap<>();

    private long lastModified = 0;

    private String configFileName = "";

    /**
     * 序列化xml文件为java对象
     *
     * @param cls 序列化后转换的java类
     * @return 实例对象
     */
    @SuppressWarnings("unchecked")
    protected static <T> T Load(Class<T> cls) throws ConfigException {
        String fileName = null;
        try {
            fileName = FileCommon.getProperties(cls.getCanonicalName());
            if (!CommonUtility.isNullStr(fileName)) {
                File file = new File(FileCommon.getAbsPath(fileName));
                BaseConfig instance = instanceMap.get(file.getName());
                if (!instanceMap.containsKey(file.getName()) || file.lastModified() > instance.lastModified) {
                    synchronized (instanceMap) {
                        XStream xstream = new XStream(new DomDriver());
                        xstream.processAnnotations(cls);
                        T obj = (T) xstream.fromXML(new FileInputStream(file));
                        if (obj == null) {
                            log.error("load config faild:[" + fileName + "]");
                            instanceMap.remove(file.getName());
                        } else {
                            ((BaseConfig) obj).configFileName = file.getName();
                            ((BaseConfig) obj).lastModified = file.lastModified();
                            instanceMap.put(file.getName(), (BaseConfig) obj);
                            log.info("load [" + fileName + "] success => " + cls.getCanonicalName());
                        }
                        instanceMap.notifyAll();
                        return obj;
                    }
                } else {
                    return (T) instance;
                }
            } else {
                throw new ConfigException("load config faild: need specify XML file for " + cls.getCanonicalName());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ConfigException("load config faild:[" + fileName + "]");
        }
    }

    /**
     * 将java对象信息写入xml文件
     */
    public void writeToXml() throws ConfigException {
        try {
            if (!CommonUtility.isNullStr(this.configFileName)) {
                synchronized (this) {
                    File file = new File(FileCommon.getAbsPath(this.configFileName));
                    String encode = FileCommon.getDefaultCharset();
                    XStream xstream = new XStream(new DomDriver());
                    PrintWriter pw = new PrintWriter(file, encode);
                    xstream.toXML(this, pw);
                    this.lastModified = file.lastModified();
                    instanceMap.put(this.configFileName, this);
                }
                log.info("write [" + this.configFileName + "] success => " + this.getClass().getCanonicalName());
            } else {
                throw new ConfigException("write config faild: need specify XML file for " + this.getClass().getCanonicalName());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ConfigException("write config faild:[" + this.configFileName + "]");
        }
    }

}
