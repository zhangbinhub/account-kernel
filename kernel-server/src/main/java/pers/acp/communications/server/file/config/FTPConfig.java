package pers.acp.communications.server.file.config;

import pers.acp.tools.config.base.BaseConfig;
import pers.acp.tools.exceptions.ConfigException;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

/**
 * Created by zhangbin on 2016/12/20.
 * FTP服务配置
 */
@XStreamAlias("ftp-config")
public class FTPConfig extends BaseConfig {

    public static FTPConfig getInstance() throws ConfigException {
        return Load(FTPConfig.class);
    }

    public List<Listen> getListens() {
        return listens;
    }

    @XStreamImplicit(itemFieldName = "listen")
    private List<Listen> listens;

    public class Listen {

        public String getName() {
            return name;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public int getPort() {
            return port;
        }

        public boolean isAnonymousLoginEnabled() {
            return anonymousLoginEnabled;
        }

        public String getPwdEncryptMode() {
            return pwdEncryptMode.toUpperCase();
        }

        public int getLoginFailureDelay() {
            return loginFailureDelay;
        }

        public int getMaxLoginFailures() {
            return maxLoginFailures;
        }

        public int getMaxLogins() {
            return maxLogins;
        }

        public int getMaxAnonymousLogins() {
            return maxAnonymousLogins;
        }

        public int getMaxThreads() {
            return maxThreads;
        }

        public String getDefaultHomeDirectory() {
            return defaultHomeDirectory;
        }

        public boolean isAnonymousWritePermission() {
            return anonymousWritePermission;
        }

        public String getUserFactoryClass() {
            return userFactoryClass;
        }

        @XStreamAsAttribute
        @XStreamAlias("name")
        private String name;

        @XStreamAsAttribute
        @XStreamAlias("enabled")
        private boolean enabled;

        @XStreamAsAttribute
        @XStreamAlias("port")
        private int port;

        @XStreamAsAttribute
        @XStreamAlias("anonymousLoginEnabled")
        private boolean anonymousLoginEnabled;

        @XStreamAsAttribute
        @XStreamAlias("pwdEncryptMode")
        private String pwdEncryptMode;

        @XStreamAsAttribute
        @XStreamAlias("loginFailureDelay")
        private int loginFailureDelay;

        @XStreamAsAttribute
        @XStreamAlias("maxLoginFailures")
        private int maxLoginFailures;

        @XStreamAsAttribute
        @XStreamAlias("maxLogins")
        private int maxLogins;

        @XStreamAsAttribute
        @XStreamAlias("maxAnonymousLogins")
        private int maxAnonymousLogins;

        @XStreamAsAttribute
        @XStreamAlias("maxThreads")
        private int maxThreads;

        @XStreamAsAttribute
        @XStreamAlias("defaultHomeDirectory")
        private String defaultHomeDirectory;

        @XStreamAsAttribute
        @XStreamAlias("anonymousWritePermission")
        private boolean anonymousWritePermission;

        @XStreamAsAttribute
        @XStreamAlias("userFactoryClass")
        private String userFactoryClass;

    }

}
