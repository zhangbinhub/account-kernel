package pers.acp.communications.server.soap.webservice.config;

import pers.acp.tools.config.base.BaseConfig;
import pers.acp.tools.exceptions.ConfigException;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

@XStreamAlias("webservice-config")
public class WSConfig extends BaseConfig {

    public static WSConfig getInstance() throws ConfigException {
        return Load(WSConfig.class);
    }

    @XStreamImplicit(itemFieldName = "server")
    private List<Server> server;

    public List<Server> getServer() {
        return server;
    }

    public class Server {

        @XStreamAsAttribute
        @XStreamAlias("class")
        private String className;

        @XStreamAsAttribute
        @XStreamAlias("href")
        private String href;

        public String getClassName() {
            return className;
        }

        public String getHref() {
            return href;
        }
    }

}
