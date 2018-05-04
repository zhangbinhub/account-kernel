package pers.acp.gateway.server;

import pers.acp.tools.config.base.BaseConfig;
import pers.acp.tools.exceptions.ConfigException;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XStreamAlias("servers")
public class GateWayServerConfig extends BaseConfig {

    public static GateWayServerConfig getInstance() throws ConfigException {
        return Load(GateWayServerConfig.class);
    }

    @XStreamImplicit(itemFieldName = "server")
    private List<Server> servers;

    public List<Server> getServers() {
        return servers;
    }

    public class Server {

        @XStreamAsAttribute
        @XStreamAlias("serverName")
        private String serverName;

        @XStreamAsAttribute
        @XStreamAlias("serverClass")
        private String serverClass;

        @XStreamAsAttribute
        @XStreamAlias("orderClass")
        private String orderClass;

        @XStreamAsAttribute
        @XStreamAlias("notifyUrl")
        private String notifyUrl;

        @XStreamAlias("params")
        private Param params;

        public String getServerName() {
            return serverName;
        }

        public String getServerClass() {
            return serverClass;
        }

        public String getNotifyUrl() {
            return notifyUrl;
        }

        public Map<String, String> getParams() {
            Map<String, String> mp = new HashMap<String, String>();
            List<KeyItem> keyItem = params.getKeyItem();
            for (int i = 0; i < keyItem.size(); i++) {
                mp.put(keyItem.get(i).getKey(), keyItem.get(i).getValue());
            }
            return mp;
        }

        public String getOrderClass() {
            return orderClass;
        }

    }

    public class Param {

        @XStreamImplicit(itemFieldName = "key-item")
        private List<KeyItem> keyItem;

        public List<KeyItem> getKeyItem() {
            return keyItem;
        }

    }

    public class KeyItem {

        @XStreamAsAttribute
        @XStreamAlias("key")
        private String key;

        @XStreamAsAttribute
        @XStreamAlias("value")
        private String value;

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

    }

}
