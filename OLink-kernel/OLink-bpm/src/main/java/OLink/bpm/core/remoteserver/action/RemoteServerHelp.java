package OLink.bpm.core.remoteserver.action;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import OLink.bpm.core.remoteserver.ejb.RemoteServerProcess;
import OLink.bpm.core.remoteserver.ejb.RemoteServerVO;
import OLink.bpm.util.ProcessFactory;

public class RemoteServerHelp {
	
	public static Map<String, String> getAllRemoServer(String application)throws Exception
	{
		RemoteServerProcess rp = (RemoteServerProcess) ProcessFactory.createProcess((RemoteServerProcess.class));
        Map<String, String> returns = new HashMap<String, String>();
        Collection<RemoteServerVO> col =  rp.doSimpleQuery(null,application);
        
        for (Iterator<RemoteServerVO> iter = col.iterator(); iter.hasNext();) {
			RemoteServerVO vo = iter.next();
			returns.put(vo.getId(),vo.getUrl());
		}

        return returns;
	}

}
