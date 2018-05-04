package OLink.bpm.core.networkdisk.action;

import OLink.bpm.core.networkdisk.ejb.NetDisk;
import OLink.bpm.core.networkdisk.ejb.NetDiskProcess;
import OLink.bpm.util.ProcessFactory;

public class NetworkDiskHelper {

	public String getNetDiskTotalSize(String id){
		try{
			NetDiskProcess netDiskProcess = (NetDiskProcess)ProcessFactory.createProcess(NetDiskProcess.class);
			if(netDiskProcess.doView(id)==null){
				return "0 B";
			}else{
				if(((NetDisk)netDiskProcess.doView(id)).getTotalSize()<1024){
					return ((NetDisk)netDiskProcess.doView(id)).getTotalSize()+" B";
				}else if(((NetDisk)netDiskProcess.doView(id)).getTotalSize()<(1024*1024) && ((NetDisk)netDiskProcess.doView(id)).getTotalSize()>=1024){
					return ((NetDisk)netDiskProcess.doView(id)).getTotalSize()/1024+" KB";
				}else{
					return ((NetDisk)netDiskProcess.doView(id)).getTotalSize()/(1024*1024)+" M";
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public String getNetDiskUploadSize(String id){
		try{
			NetDiskProcess netDiskProcess = (NetDiskProcess)ProcessFactory.createProcess(NetDiskProcess.class);
			if(netDiskProcess.doView(id)==null){
				return "0 B";
			}else{
				if(((NetDisk)netDiskProcess.doView(id)).getUploadSize()<1024){
					return ((NetDisk)netDiskProcess.doView(id)).getUploadSize()+" B";
				}else if(((NetDisk)netDiskProcess.doView(id)).getUploadSize()<(1024*1024) && ((NetDisk)netDiskProcess.doView(id)).getUploadSize()>=1024){
					return ((NetDisk)netDiskProcess.doView(id)).getUploadSize()/1024+" KB";
				}else{
					return ((NetDisk)netDiskProcess.doView(id)).getUploadSize()/(1024*1024)+" M";
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	
	public String getNetDiskHaveUseSize(String id){
		try{
			NetDiskProcess netDiskProcess = (NetDiskProcess)ProcessFactory.createProcess(NetDiskProcess.class);
			if(netDiskProcess.doView(id)==null){
				return "0 B";
			}else{
				if(((NetDisk)netDiskProcess.doView(id)).getHaveUseSize()==0){
					return "0 B";
				}else if(((NetDisk)netDiskProcess.doView(id)).getHaveUseSize()<1024 && ((NetDisk)netDiskProcess.doView(id)).getHaveUseSize()>0){
					return ((NetDisk)netDiskProcess.doView(id)).getHaveUseSize()+" B";
				}else if(((NetDisk)netDiskProcess.doView(id)).getHaveUseSize()<(1024*1024) && ((NetDisk)netDiskProcess.doView(id)).getHaveUseSize()>=1024){
					return ((NetDisk)netDiskProcess.doView(id)).getHaveUseSize()/1024+" KB";
				}else{
					return ((NetDisk)netDiskProcess.doView(id)).getHaveUseSize()/(1024*1024)+" M";
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public String getNetDiskPemission(String id){
		try{
			NetDiskProcess netDiskProcess = (NetDiskProcess)ProcessFactory.createProcess(NetDiskProcess.class);
			if(netDiskProcess.doView(id)==null){
				return "{*[Disable]*}";
			}else{
				if(((NetDisk)netDiskProcess.doView(id)).getPemission()!=null){
					if(((NetDisk)netDiskProcess.doView(id)).getPemission().equals("true")){
						return "{*[Enable]*}";
					}else{
						return "{*[Disable]*}";
					}
				}else{
					return "{*[Disable]*}";
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			return "{*[Disable]*}";
		}
	}
}
