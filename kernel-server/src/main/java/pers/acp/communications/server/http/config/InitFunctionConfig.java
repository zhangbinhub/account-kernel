package pers.acp.communications.server.http.config;

import pers.acp.tools.config.base.BaseConfig;
import pers.acp.tools.exceptions.ConfigException;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

@XStreamAlias("initFunctionConfig")
public final class InitFunctionConfig extends BaseConfig {

    public static InitFunctionConfig getInstance() throws ConfigException {
        return Load(InitFunctionConfig.class);
    }

    @XStreamImplicit(itemFieldName = "function")
    private List<Function> functions;

    public List<Function> getFunctions() {
        return functions;
    }

    public class Function {

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
        private List<Param> params;

        public String getName() {
            return name;
        }

        public String getClassname() {
            return classname;
        }

        public String getMethod() {
            return method;
        }

        public List<Param> getParams() {
            return params;
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
