package pers.acp.tools.config.instance;

import pers.acp.tools.config.base.BaseConfig;
import pers.acp.tools.exceptions.ConfigException;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

@XStreamAlias("system-config")
public final class SystemConfig extends BaseConfig {

    public static SystemConfig getInstance() throws ConfigException {
        return Load(SystemConfig.class);
    }

    @XStreamAlias("defaultCharset")
    private String defaultCharset;

    @XStreamAlias("DeleteWaitTime")
    private long deleteWaitTime;

    @XStreamAlias("DataBaseConfig")
    private DataBaseConfig dataBaseConfig;

    @XStreamAlias("Security")
    private Security security;

    @XStreamAlias("ThreadPool")
    private ThreadPool threadPool;

    public String getDefaultCharset() {
        return defaultCharset;
    }

    public long getDeleteWaitTime() {
        return deleteWaitTime;
    }

    public DataBaseConfig getDataBaseConfig() {
        return dataBaseConfig;
    }

    public Security getSecurity() {
        return security;
    }

    public ThreadPool getThreadPool() {
        return threadPool;
    }

    public class DataBaseConfig {

        @XStreamImplicit(itemFieldName = "connection")
        private List<Connection> connection;

        public List<Connection> getConnection() {
            return connection;
        }

        public class Connection {

            @XStreamAsAttribute
            @XStreamAlias("connectionNo")
            private int connectionNo;

            @XStreamAsAttribute
            @XStreamAlias("isDefault")
            private boolean isDefault;

            @XStreamAsAttribute
            @XStreamAlias("name")
            private String name;

            @XStreamAsAttribute
            @XStreamAlias("className")
            private String className;

            @XStreamAsAttribute
            @XStreamAlias("resourceName")
            private String resourceName;

            @XStreamAsAttribute
            @XStreamAlias("dbtype")
            private String dbtype;

            @XStreamAsAttribute
            @XStreamAlias("poolName")
            private String poolName;

            public int getConnectionNo() {
                return connectionNo;
            }

            public String getClassName() {
                return className;
            }

            public String getResourceName() {
                return resourceName;
            }

            public String getDbtype() {
                return dbtype;
            }

            public String getPoolName() {
                return poolName;
            }

            public String getName() {
                return name;
            }

            public boolean isDefault() {
                return isDefault;
            }
        }
    }

    public class Security {

        @XStreamAsAttribute
        @XStreamAlias("keyDelayTime")
        private long keyDelayTime;

        @XStreamAsAttribute
        @XStreamAlias("expirationTime")
        private long expirationTime;

        @XStreamAsAttribute
        @XStreamAlias("storageMode")
        private int storageMode;

        @XStreamAsAttribute
        @XStreamAlias("storageParam")
        private String storageParam;

        @XStreamAsAttribute
        @XStreamAlias("tablename")
        private String tablename;

        @XStreamAsAttribute
        @XStreamAlias("keyCol")
        private String keyCol;

        @XStreamAsAttribute
        @XStreamAlias("objCol")
        private String objCol;

        public long getKeyDelayTime() {
            return keyDelayTime;
        }

        public long getExpirationTime() {
            return expirationTime;
        }

        public int getStorageMode() {
            return storageMode;
        }

        public String getStorageParam() {
            return storageParam;
        }

        public String getTablename() {
            return tablename;
        }

        public String getKeyCol() {
            return keyCol;
        }

        public String getObjCol() {
            return objCol;
        }

    }

    public class ThreadPool {

        @XStreamAsAttribute
        @XStreamAlias("enabled")
        private boolean enabled;

        @XStreamAsAttribute
        @XStreamAlias("spacingTime")
        private long spacingTime;

        @XStreamAsAttribute
        @XStreamAlias("maxThreadNumber")
        private int maxThreadNumber;

        public long getSpacingTime() {
            return spacingTime;
        }

        public int getMaxThreadNumber() {
            return maxThreadNumber;
        }

        public boolean isEnabled() {
            return enabled;
        }
    }

}
