package pers.acp.gateway.client;

import pers.acp.tools.config.base.BaseConfig;
import pers.acp.tools.exceptions.ConfigException;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

@XStreamAlias("client")
public class GateWayClientConfig extends BaseConfig {

    public static GateWayClientConfig getInstance() throws ConfigException {
        return Load(GateWayClientConfig.class);
    }

    @XStreamImplicit(itemFieldName = "server")
    private List<Server> servers;

    public List<Server> getServers() {
        return servers;
    }

    public class Server {

        @XStreamAsAttribute
        @XStreamAlias("serverNo")
        private int serverNo;

        @XStreamAsAttribute
        @XStreamAlias("timeout")
        private int timeout;

        @XStreamAsAttribute
        @XStreamAlias("url")
        private String url;

        @XStreamAsAttribute
        @XStreamAlias("tradeKey")
        private String tradeKey;

        public int getServerNo() {
            return serverNo;
        }

        public int getTimeout() {
            return timeout;
        }

        public String getUrl() {
            return url;
        }

        public String getTradeKey() {
            return tradeKey;
        }

    }

}
