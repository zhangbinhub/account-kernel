package OLink.bpm.core.dynaform.form.ejb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.dynaform.form.dao.FormDAO;
import OLink.bpm.util.StringUtil;

/**
 * 详细请查看BaseFormProcessBean
 * 
 * @author Nicholas
 * 
 */
public class FormProcessBean extends BaseFormProcessBean<Form> implements
		FormProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4531185835706797003L;

	/**
	 * 根据表单名以及应用标识查询查询,返回表单对象.
	 * 
	 * @param formName
	 *            表单名
	 * @param application
	 *            应用标识
	 * @return 表单对象
	 */
	public Form doViewByFormName(String formName, String application)
			throws Exception {
		return ((FormDAO<Form>) getDAO()).findByFormName(formName,
				application);
	}

	public ValueObject doView(String pk) throws Exception {
		return super.doView(pk);
	}

	public void doRemove(Collection<Form> formList) throws Exception {
		if (formList != null) {
			List<String> ids = new ArrayList<String>();
			for (Iterator<Form> it = formList.iterator(); it.hasNext();) {
				String id = it.next().getId();
				if (!StringUtil.isBlank(id)) {
					ids.add(id);
				}
			}
			this.doRemove(ids.toArray(new String[ids.size()]));
		}
	}

	public Collection<Form> getTemplateFormsByModule(String moduleid,String application) throws Exception {
		
		return ((FormDAO<Form>) getDAO()).queryTemplateFormsByModule(moduleid, application);
	}
	
}
