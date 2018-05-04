package OLink.bpm.core.dynaform.component.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.core.dynaform.component.ejb.Component;
import OLink.bpm.core.dynaform.form.action.FormAction;
import OLink.bpm.core.dynaform.form.ejb.FormField;
import OLink.bpm.util.text.TemplateContext;
import com.opensymphony.xwork.Action;

/**
 * @author nicholas
 */
public class ComponentAction extends FormAction<Component> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1395964549692071142L;

	/**
	 * @uml.property name="fieldList"
	 */
	private Collection<FormField> fieldList;

	public ComponentAction() throws ClassNotFoundException {
		super(new Component());
	}

	public String doSelectList() throws Exception {
		return super.doList();
	}

	public String doFieldList() throws Exception {
		String id = (String) getParams().getParameter("id");

		Component comp = (Component) process.doView(id);
		if (comp != null) {
			Collection<FormField> fieldList = new ArrayList<FormField>();
			for (Iterator<FormField> iter = comp.getFields().iterator(); iter.hasNext();) {
				FormField field = iter.next();
				TemplateContext context = TemplateContext.parse(field.getName());
				String newName = context.toText();
				if (field.getName().equals(newName)){
					field.setName(newName);
					fieldList.add(field);
				}
			}
			setFieldList(fieldList);
		} else {
			addFieldError("", "{*[core.component.notexist]*}");
		}

		return Action.SUCCESS;
	}

	public String get_applicationid() {
		Component content = (Component) getContent();
		return content.getApplicationid();
	}

	public void set_applicationid(String _applicationid) throws Exception {
		Component content = (Component) getContent();

		content.setApplicationid(_applicationid);
	}

	/**
	 * @return the fieldList
	 * @uml.property name="fieldList"
	 */
	public Collection<FormField> getFieldList() {
		return fieldList;
	}

	/**
	 * @param fieldList
	 *            the fieldList to set
	 * @uml.property name="fieldList"
	 */
	public void setFieldList(Collection<FormField> fieldList) {
		this.fieldList = fieldList;
	}
}
