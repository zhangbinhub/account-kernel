package pers.acp.communications.server.file.config;

import pers.acp.tools.config.base.BaseConfig;
import pers.acp.tools.exceptions.ConfigException;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

/**
 * Created by zhangbin on 2016/12/21.
 * SFTP服务配置
 */
@XStreamAlias("sftp-config")
public class SFTPConfig extends BaseConfig {

    public static SFTPConfig getInstance() throws ConfigException {
        return Load(SFTPConfig.class);
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

        public String getHostKeyPath() {
            return hostKeyPath;
        }

        public boolean isPasswordAuth() {
            return passwordAuth;
        }

        public boolean isPublicKeyAuth() {
            return publicKeyAuth;
        }

        public String getKeyAuthType() {
            return keyAuthType.toLowerCase();
        }

        public String getKeyAuthMode() {
            return keyAuthMode.toUpperCase();
        }

        public String getDefaultHomeDirectory() {
            return defaultHomeDirectory;
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
        @XStreamAlias("hostKeyPath")
        private String hostKeyPath;

        @XStreamAsAttribute
        @XStreamAlias("passwordAuth")
        private boolean passwordAuth;

        @XStreamAsAttribute
        @XStreamAlias("publicKeyAuth")
        private boolean publicKeyAuth;

        @XStreamAsAttribute
        @XStreamAlias("keyAuthType")
        private String keyAuthType;

        @XStreamAsAttribute
        @XStreamAlias("keyAuthMode")
        private String keyAuthMode;

        @XStreamAsAttribute
        @XStreamAlias("defaultHomeDirectory")
        private String defaultHomeDirectory;

        @XStreamAsAttribute
        @XStreamAlias("userFactoryClass")
        private String userFactoryClass;

    }

}
