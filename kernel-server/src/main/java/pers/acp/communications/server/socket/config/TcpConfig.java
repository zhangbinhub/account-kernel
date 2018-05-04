package pers.acp.communications.server.socket.config;

import pers.acp.tools.config.base.BaseConfig;
import pers.acp.tools.exceptions.ConfigException;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

@XStreamAlias("tcp-config")
public class TcpConfig extends BaseConfig {

    public static TcpConfig getInstance() throws ConfigException {
        return Load(TcpConfig.class);
    }

    @XStreamImplicit(itemFieldName = "listen")
    private List<ListenConfig> listen;

    public List<ListenConfig> getListen() {
        return listen;
    }

}
