package pers.acp.communications.server.socket.config;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public class ListenConfig {

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
	@XStreamAlias("isHex")
	private boolean isHex;

	@XStreamAsAttribute
	@XStreamAlias("responseClass")
	private String responseClass;

	@XStreamAsAttribute
	@XStreamAlias("charset")
	private String charset;

	public String getName() {
		return name;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public int getPort() {
		return port;
	}

	public boolean isHex() {
		return isHex;
	}

	public String getResponseClass() {
		return responseClass;
	}

	public String getCharset() {
		return charset;
	}
	
}
