package pers.acp.communications.server.http.config;

import pers.acp.tools.config.base.BaseConfig;
import pers.acp.tools.exceptions.ConfigException;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

@XStreamAlias("http-config")
public class HttpConfig extends BaseConfig {

    public static HttpConfig getInstance() throws ConfigException {
        return Load(HttpConfig.class);
    }

    @XStreamAlias("appIdName")
    private String appIdName;

    @XStreamAlias("dbnoName")
    private String dbnoName;

    @XStreamAlias("operatorIdName")
    private String operatorIdName;

    @XStreamAlias("OnlineServer")
    private OnlineServer onlineServer;

    @XStreamAlias("noFilters")
    private NoFilters noFilters;

    public String getOperatorIdName() {
        return operatorIdName;
    }

    public OnlineServer getOnlineServer() {
        return onlineServer;
    }

    public NoFilters getNoFilters() {
        return noFilters;
    }

    public String getDbnoName() {
        return dbnoName;
    }

    public String getAppIdName() {
        return appIdName;
    }

    public class OnlineServer {

        @XStreamAsAttribute
        @XStreamAlias("enabled")
        private boolean enabled;

        @XStreamAsAttribute
        @XStreamAlias("spacingTime")
        private long spacingTime;

        @XStreamAsAttribute
        @XStreamAlias("onlineTimeout")
        private long onlineTimeout;

        @XStreamAsAttribute
        @XStreamAlias("tablename")
        private String tablename;

        @XStreamAsAttribute
        @XStreamAlias("colname")
        private String colname;

        public boolean isEnabled() {
            return enabled;
        }

        public long getSpacingTime() {
            return spacingTime;
        }

        public long getOnlineTimeout() {
            return onlineTimeout;
        }

        public String getTablename() {
            return tablename;
        }

        public String getColname() {
            return colname;
        }
    }

    public class NoFilters {

        @XStreamImplicit(itemFieldName = "urlKeyWord")
        private List<UrlKeyWord> urlKeyWords;

        public List<UrlKeyWord> getUrlKeyWords() {
            return urlKeyWords;
        }

    }

    public class UrlKeyWord {

        @XStreamAsAttribute
        @XStreamAlias("value")
        private String value;

        public String getValue() {
            return value;
        }

    }

}
