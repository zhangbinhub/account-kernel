package OLink.bpm.core.xmpp.service;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;


public abstract class OBPMService extends IQ {
	protected MethodInvoker invoker;
	
	
	public MethodInvoker getInvoker() {
		return invoker;
	}


	public void setInvoker(MethodInvoker invoker) {
		this.invoker = invoker;
	}


	public abstract Packet createResultPacket();
}
