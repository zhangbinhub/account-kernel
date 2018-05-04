package OLink.bpm.core.dynaform.component.ejb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.dynaform.component.dao.ComponentDAO;
import OLink.bpm.core.dynaform.form.action.ImpropriateException;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import OLink.bpm.core.dynaform.form.ejb.Form;
import org.apache.commons.beanutils.PropertyUtils;

import eWAP.core.Tools;

import OLink.bpm.core.dynaform.form.ejb.BaseFormProcessBean;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.dynaform.form.ejb.FormTableProcessBean;

public class ComponentProcessBean extends BaseFormProcessBean<Component> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7810692021443657365L;

	protected IDesignTimeDAO<Component> getDAO() throws Exception {
		return (ComponentDAO) DAOFactory.getDefaultDAO(Component.class.getName());
	}

	/**
	 * 创建组件
	 */
	public void doCreate(ValueObject vo) throws Exception {
		ParamsTable params = new ParamsTable();
		params.setParameter("s_name", ((Component) vo).getName());
		params.setParameter("s_applicationid", vo.getApplicationid());

		Collection<Component> colls = this.doSimpleQuery(params);

		if (colls != null && colls.size() > 0) {
			throw new ImpropriateException("{*[Exist.same.name]*} (" + ((Form) vo).getName() + "),{*[please.choose.another]*}!");
		} else {
			ComponentDAO dao = ((ComponentDAO) getDAO());
			try {
				PersistenceUtils.beginTransaction();

				if (StringUtil.isBlank(vo.getId())) {
					vo.setId(Tools.getSequence());
				}

				if (StringUtil.isBlank(vo.getSortId())) {
					vo.setSortId(Tools.getTimeSequence());
				}

				dao.create(vo);

				PersistenceUtils.commitTransaction();
			} catch (Exception e) {
				PersistenceUtils.rollbackTransaction();
				e.printStackTrace();
				throw e;
			}
		}
	}

	public void doRemove(String pk) throws Exception {
		Component component = (Component) getDAO().find(pk);
		doRemove(component);
	}

	public void doRemove(String[] pks) throws Exception {
		try {
			PersistenceUtils.beginTransaction();
			if (pks != null) {
				for (int i = 0; i < pks.length; i++) {
					doRemove(pks[i]);
				}
			}
			PersistenceUtils.commitTransaction();
		} catch (Exception e) {
			PersistenceUtils.rollbackTransaction();
			throw e;
		}
	}

	public void doRemove(ValueObject obj) throws Exception {
		try {
			PersistenceUtils.beginTransaction();

			Collection<Form> forms = getReferenceForms(obj);
			if (forms.isEmpty()) {
				getDAO().remove(obj);
			} else {
				StringBuffer formNames = new StringBuffer();
				for (Iterator<Form> iterator = forms.iterator(); iterator.hasNext();) {
					Form form = iterator.next();
					formNames.append(form.getName()).append(",");

				}
				formNames.deleteCharAt(formNames.lastIndexOf(","));
				throw new Exception("{*[component.reference.by]*}(" + formNames.toString() + ")");
			}

			PersistenceUtils.commitTransaction();
		} catch (Exception e) {
			PersistenceUtils.rollbackTransaction();
			throw e;
		}
	}

	/**
	 * 获取引用组件的所有表单
	 * 
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	private Collection<Form> getReferenceForms(ValueObject obj) throws Exception {
		Collection<Form> rtn = new ArrayList<Form>();

		FormProcess formProcess = (FormProcess) ProcessFactory.createProcess(FormProcess.class);

		Collection<Form> forms = formProcess.doSimpleQuery(null, obj.getApplicationid());
		for (Iterator<Form> iterator = forms.iterator(); iterator.hasNext();) {
			Form form = iterator.next();
			if (form.findComponent(obj.getId()) != null) {
				rtn.add(form);
			}

		}
		return rtn;
	}

	public void doUpdate(ValueObject vo) throws Exception {
		FormTableProcessBean tableProcess = new FormTableProcessBean(vo.getApplicationid());

		try {
			PersistenceUtils.beginTransaction();
			tableProcess.beginTransaction();

			Component component = (Component) vo;

			// 1. 查找引用组件的表单
			Collection<Form> referenceForms = getReferenceForms(vo);
			for (Iterator<Form> iterator = referenceForms.iterator(); iterator.hasNext();) {
				Form form = iterator.next();

				Component oldComponent = form.findComponent(component.getId());
				if (oldComponent != null) {
					form.removeAllField(oldComponent.getAllFields());
					form.addAllField(component.getAllFields());
				}
			}

			// 2. 更新所有引用此组件的表单
			updateAllDynaTables(tableProcess, referenceForms);

			// 3. 更新组件
			Component oldCcomponent = (Component) getDAO().find(vo.getId());
			if (oldCcomponent != null) {
				PropertyUtils.copyProperties(oldCcomponent, component);
				getDAO().update(oldCcomponent);
			} else {
				getDAO().update(component);
			}

			tableProcess.commitTransaction();
			PersistenceUtils.commitTransaction();
		} catch (ImpropriateException e) {
			tableProcess.rollbackTransaction();
			PersistenceUtils.rollbackTransaction();
			throw e;
		} catch (Exception e) {
			tableProcess.rollbackTransaction();
			PersistenceUtils.rollbackTransaction();
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * @SuppressWarnings 工厂方法无法使用泛型
	 */
	@SuppressWarnings("unchecked")
	public void updateAllDynaTables(FormTableProcessBean tableProcess, Collection newForms) throws Exception {
		FormProcess formProcess = (FormProcess) ProcessFactory.createProcess(FormProcess.class);

		for (Iterator<Form> iter = newForms.iterator(); iter.hasNext();) {
			Form newForm = iter.next();
			Form oldForm = (Form) formProcess.doView(newForm.getId());
			tableProcess.createOrUpdateDynaTable(newForm, oldForm);
		}
	}

	public Collection<Component> getTemplateFormsByModule(String moduleid,
			String application) throws Exception {
		return null;
	}
}
