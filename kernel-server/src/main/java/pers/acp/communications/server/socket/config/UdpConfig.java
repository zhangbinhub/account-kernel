package pers.acp.communications.server.socket.config;

import pers.acp.tools.config.base.BaseConfig;
import pers.acp.tools.exceptions.ConfigException;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

@XStreamAlias("udp-config")
public class UdpConfig extends BaseConfig {

    public static UdpConfig getInstance() throws ConfigException {
        return Load(UdpConfig.class);
    }

    @XStreamImplicit(itemFieldName = "listen")
    private List<ListenConfig> listen;

    public List<ListenConfig> getListen() {
        return listen;
    }

}
