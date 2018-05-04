package OLink.bpm.version.transfer;

import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.core.dynaform.form.ejb.FormProcessBean;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import org.apache.log4j.Logger;

import OLink.bpm.base.action.ParamsTable;

public class FormTransfer extends BaseTransfer {
	private final static Logger LOG = Logger.getLogger(FormTransfer.class);
	

	public void to2_5SP4() {
		updateDynaFormTable();
	}
	
	private void updateDynaFormTable() {
		LOG.info("#######################begin update dynamic form#######################");
		FormProcess process = new FormProcessBean();
		try {
			ParamsTable params = new ParamsTable();
			params.setParameter("t_type", 0x0000001);
			Collection<Form> list = process.doSimpleQuery(params);
			for(Iterator<Form> iter = list.iterator();iter.hasNext();){
				Form form = iter.next();
				process.doUpdate(form);
			}
			LOG.info("#######################update dynamic form success#######################");
		} catch (Exception e) {
			LOG.info("#######################update dynamic form failed#######################");
			e.printStackTrace();
		}
		
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new FormTransfer().to2_5SP4();

	}

}
