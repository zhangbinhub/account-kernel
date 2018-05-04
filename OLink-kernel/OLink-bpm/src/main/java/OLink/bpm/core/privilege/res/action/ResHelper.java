package OLink.bpm.core.privilege.res.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.dynaform.form.ejb.FormField;
import OLink.bpm.core.privilege.res.ejb.ResProcess;
import OLink.bpm.core.privilege.res.ejb.ResVO;
import OLink.bpm.util.ProcessFactory;
import org.apache.log4j.Logger;

import OLink.bpm.base.action.BaseHelper;

public class ResHelper extends BaseHelper<ResVO> {
	static Logger logger = Logger.getLogger(ResHelper.class);

	/**
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public ResHelper() throws ClassNotFoundException {
		super(ProcessFactory.createProcess(ResProcess.class));
	}

	public Collection<ResVO> getResources() {
		Collection<ResVO> rtn = new ArrayList<ResVO>();
		try {
			Collection<ResVO> colls = process.doSimpleQuery(null, null);
			if (colls != null && colls.size() > 0) {
				rtn = colls;
			}
		} catch (Exception e) {
			logger.error("Create instance select error");
			e.printStackTrace();
		}
		return rtn;
	}
	
	public Collection<ResVO> getInnerResources(String resourceid, int resourcetype) throws Exception{
		FormProcess formProcess = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
		Collection<ResVO> rtn = new ArrayList<ResVO>();
		
		if (resourcetype == ResVO.FORM_TYPE){
			Form form = (Form) formProcess.doView(resourceid);
			Collection<FormField> fields = form.getAllFields();
			for (Iterator<FormField> iterator = fields.iterator(); iterator.hasNext();) {
				FormField formField = iterator.next();
				ResVO resource = new ResVO();
				resource.setId(formField.getId());
				resource.setName(formField.getName());
				resource.setType(ResVO.FORM_FIELD_TYPE);
				resource.setApplicationid(form.getApplicationid());
				
				rtn.add(resource);
			}
		}
		
		return rtn;
	}
}
