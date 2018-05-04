package OLink.bpm.core.googlemap;

import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.dynaform.document.ejb.Item;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.document.ejb.DocumentProcess;

public class GoogleMap {

	/**
	 * 获得地图的数据
	 * @param fieldId
	 * @return
	 */
	public String getMapData(String fieldId,String applicationid){
		try {
			DocumentProcess documentPross = (DocumentProcess) ProcessFactory.createRuntimeProcess(DocumentProcess.class,applicationid);
			Document doc =(Document)documentPross.doView(fieldId.split("_")[0]);
			if(doc!=null){
				Item item = doc.findItem(fieldId.split("_")[1]);
				if(item!=null && item.getValue()!=null){
					return item.getValue().toString();
				}else{
					return "";
				}
			}else{
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
}
