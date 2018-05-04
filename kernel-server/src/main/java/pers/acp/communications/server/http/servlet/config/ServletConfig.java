package pers.acp.communications.server.http.servlet.config;

import pers.acp.tools.config.base.BaseConfig;
import pers.acp.tools.exceptions.ConfigException;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

@XStreamAlias("servlet-config")
public final class ServletConfig extends BaseConfig {

    public static ServletConfig getInstance() throws ConfigException {
        return Load(ServletConfig.class);
    }

    @XStreamImplicit(itemFieldName = "server")
    private List<Server> servers;

    public List<Server> getServers() {
        return servers;
    }

    public class Server {

        @XStreamAsAttribute
        @XStreamAlias("name")
        private String name;

        @XStreamAsAttribute
        @XStreamAlias("classname")
        private String classname;

        @XStreamAsAttribute
        @XStreamAlias("method")
        private String method;

        @XStreamImplicit(itemFieldName = "param")
        private List<Param> param;

        public String getName() {
            return name;
        }

        public String getClassname() {
            return classname;
        }

        public String getMethod() {
            return method;
        }

        public List<Param> getParam() {
            return param;
        }
    }

    public class Param {

        @XStreamAsAttribute
        @XStreamAlias("type")
        private String type;

        @XStreamAsAttribute
        @XStreamAlias("value")
        private String value;

        public String getType() {
            return type;
        }

        public String getValue() {
            return value;
        }
    }
}
