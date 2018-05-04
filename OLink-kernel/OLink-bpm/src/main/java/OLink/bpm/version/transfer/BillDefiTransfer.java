package OLink.bpm.version.transfer;

import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.core.workflow.element.*;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiProcess;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.workflow.element.Element;
import org.apache.log4j.Logger;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.workflow.element.ManualNode;

public class BillDefiTransfer extends BaseTransfer {

	private final static Logger LOG = Logger.getLogger(BillDefiTransfer.class);
	/* (non-Javadoc)
	 * @see ITransfer#to2_4()
	 */
	public void to2_4() {
		try {
			LOG.info("---->begin to transfer bill data!");
			transfer3to4();
			LOG.info("---->transfer successfully!");
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("---->transfer failed!");
		}

	}
	
	
	/**
	 * 2.3之前的版本迁移到2.4
	 * @throws Exception
	 */
	public void transfer3to4() throws Exception{
		BillDefiProcess process = (BillDefiProcess) ProcessFactory.createProcess(BillDefiProcess.class);
		Collection<?> dataList = process.doSimpleQuery(new ParamsTable());
		for(Iterator<?> iterator = dataList.iterator(); iterator.hasNext();){
			BillDefiVO flowVO = (BillDefiVO)iterator.next();
			FlowDiagram fd = flowVO.toFlowDiagram();
			Collection<?> allElements = fd.getAllElements();
			boolean flg = false;
			for(Iterator<?> iter = allElements.iterator(); iter.hasNext();){
				Element subElment = (Element) iter.next();
				if (subElment instanceof ManualNode) {
					if(((ManualNode)subElment).retracementScript == null){
						fd.delElement(((ManualNode)subElment).id);
						flg = true;
						
						((ManualNode)subElment).cBack = true;
						((ManualNode)subElment).cRetracement =false;
						((ManualNode)subElment).backType = 0;
						((ManualNode)subElment).retracementEditMode = 0;
						((ManualNode)subElment).retracementScript ="";
						
						fd.appendElement(subElment);
					}
				}
			}
			if(flg){
				flowVO.setFlow(fd.toXML());
				process.doUpdate(flowVO);
			}
			
			
			
		}
		
		
		
	}
	
	public static void main(String[] args){
		new BillDefiTransfer().to2_4();
	}
}
