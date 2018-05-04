package OLink.bpm.version.transfer;

import java.util.Collection;

import OLink.bpm.core.resource.ejb.ResourceProcess;
import OLink.bpm.core.links.ejb.LinkVO;
import OLink.bpm.core.resource.ejb.ResourceVO;
import OLink.bpm.util.ProcessFactory;
import eWAP.core.Tools;

public class MenuTransfer extends BaseTransfer {

	public void to2_4() {

	}
	
	public void to2_5() {
		try {
			transfer4to5();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	public void transfer4to5() throws Exception {
		ResourceProcess rp = (ResourceProcess) ProcessFactory.createProcess(ResourceProcess.class);
		String sql = "select * from t_resource where linkid in(select  linkid from t_resource group by  linkid having count(*) >1)";
		Collection<ResourceVO> memus = rp.doQueryBySQL(sql);
		for(ResourceVO menu : memus){
			if(menu.getLink()!=null){
				LinkVO link = menu.getLink();
				link.setId(Tools.getSequence());
				menu.setLink(link);
				rp.doUpdate(menu);
			}
		}
		
	}
	
	
	public static void main(String[] args) {
		new MenuTransfer().to2_5();
	}

}
