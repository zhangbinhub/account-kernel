package OLink.bpm.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.homepage.ejb.Reminder;
import OLink.bpm.core.homepage.ejb.ReminderProcess;
import OLink.bpm.core.dynaform.dts.excelimport.config.ejb.IMPMappingConfigProcess;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.view.ejb.ViewProcess;
import OLink.bpm.core.resource.ejb.ResourceProcess;
import OLink.bpm.core.style.repository.ejb.StyleRepositoryProcess;
import OLink.bpm.core.style.repository.ejb.StyleRepositoryVO;
import OLink.bpm.core.dynaform.dts.excelimport.config.ejb.IMPMappingConfigVO;
import OLink.bpm.core.permission.ejb.PermissionVO;
import OLink.bpm.core.resource.ejb.ResourceVO;

public class ElementResplaceUtil {

	Map<String, String> formMap = new HashMap<String, String>();

	Map<String, String> viewMap = new HashMap<String, String>();

	Map<String, String> flowMap = new HashMap<String, String>();

	Map<String, String> moduleMap = new HashMap<String, String>();

	Map<String, String> styleMap = new HashMap<String, String>();

	Map<String, String> resourceMap = new HashMap<String, String>();

	public ElementResplaceUtil(Map<String, String> formMap, Map<String, String> viewMap, Map<String, String> flowMap,
			Map<String, String> moduleMap, Map<String, String> styleMap, Map<String, String> resourceMap) throws Exception {
		this.formMap = formMap;
		this.viewMap = viewMap;
		this.flowMap = flowMap;
		this.moduleMap = moduleMap;
		this.styleMap = styleMap;
		this.resourceMap = resourceMap;
	}

	public void resplaceReminder(String application) throws Exception {
		ReminderProcess reminderProcess = (ReminderProcess) ProcessFactory
				.createProcess(ReminderProcess.class);
		Collection<Reminder> remindres = reminderProcess.doSimpleQuery(null, application);
		if (remindres != null && remindres.size() > 0) {
			for (Iterator<Reminder> iterator = remindres.iterator(); iterator.hasNext();) {
				Reminder rem = iterator.next();
				for (Iterator<Entry<String, String>> iter = moduleMap.entrySet().iterator(); iter
						.hasNext();) {
					Entry<String, String> entry = iter.next();
					String entryKey =  entry.getKey();
					if (entryKey.equals(rem.getModuleid())) {
						rem.setModuleid(entry.getValue());
					}
				}
				for (Iterator<Entry<String, String>> iterator2 = formMap.entrySet().iterator(); iterator2
						.hasNext();) {
					Entry<String, String> object =iterator2.next();
					String objKey = object.getKey();
					if (objKey.equals(rem.getFormId())) {
						rem.setFormId(object.getValue());
					}
				}
				reminderProcess.doUpdate(rem);
			}
		}
	}

	public void resplaceFrom() throws Exception {
		FormProcess formProcess = (FormProcess) ProcessFactory
				.createProcess(FormProcess.class);
		if (formMap != null) {
			for (Iterator<Entry<String, String>> iterator = formMap.entrySet().iterator(); iterator
					.hasNext();) {
				Entry<String, String> entry = iterator.next();
				String formValue = entry.getValue();
				Form form = (Form) formProcess.doView(formValue);
				if (form != null) {
					String resplaceTemplate = form.getTemplatecontext();
					if (!StringUtil.isBlank(resplaceTemplate)) {
						// 替换form中的需要的view属性
						for (Iterator<Entry<String, String>> iter = viewMap.entrySet().iterator();iter!=null && iter.hasNext();) {
							Entry<String, String> viewEntry = iter.next();
							String viewKey = viewEntry.getKey();
							String viewValue = viewEntry.getValue();
							if (viewKey != null && viewValue != null) {
								resplaceTemplate = resplaceTemplate
									.replaceAll(viewKey, viewValue);
							}
						}
						// 替换module
						for (Iterator<Entry<String, String>> iterator2 = moduleMap.entrySet()
								.iterator(); iterator2.hasNext();) {
							Entry<String, String> moduleEntry = iterator2
									.next();
							String moduleKey = moduleEntry.getKey();
							String moduleValue = moduleEntry
									.getValue();
							resplaceTemplate = resplaceTemplate.replaceAll(
									moduleKey, moduleValue);
						}
						form.setTemplatecontext(resplaceTemplate);
					}

					String activity = form.getActivityXML();
					if (!StringUtil.isBlank(activity)) {
						for (Iterator<Entry<String, String>> flowiter = flowMap.entrySet().iterator(); flowiter
								.hasNext();) {
							Entry<String, String> viewEntry = flowiter.next();
							String flowKey = viewEntry.getKey();
							String flowValue = viewEntry.getValue();
							if (flowKey != null && flowValue != null) {
								activity = activity.replaceAll(flowKey,
										flowValue);
							}

						}
						form.setActivityXML(activity);
					}
					// replace style
					if (styleMap != null && styleMap.size() > 0) {
						StyleRepositoryProcess process = (StyleRepositoryProcess) ProcessFactory
								.createProcess(StyleRepositoryProcess.class);
						for (Iterator<Entry<String, String>> iterator2 = styleMap.entrySet()
								.iterator(); iterator2.hasNext();) {
							Entry<String, String> style = iterator2.next();
							if (form.getStyle().getId().equals(style.getKey())) {
								StyleRepositoryVO styleVO = (StyleRepositoryVO) process
										.doView(style.getValue());
								form.setStyle(styleVO);
							}
						}
					}
					formProcess.doUpdate(form);
				}
			}

		}
	}

	/**
	 * resplace view protype includes filterScript, searchform,form,resource
	 * 
	 * @throws Exception
	 */
	public void replaceView() throws Exception {
		ViewProcess viewProcess = (ViewProcess) ProcessFactory
				.createProcess(ViewProcess.class);
		for (Iterator<Entry<String, String>> iterator = viewMap.entrySet().iterator(); iterator
				.hasNext();) {
			Entry<String, String> entry = iterator.next();
			String viewValue = entry.getValue();
			View view = (View) viewProcess.doView(viewValue);
			if (view != null) {
				String activityXML = view.getActivityXML();
				String columnXML = view.getColumnXML();
				String filterScript = view.getFilterScript();
				String sqlScript = view.getSqlFilterScript();
				Form searchForm = null;
				for (Iterator<Entry<String, String>> iter = formMap.entrySet().iterator(); iter
						.hasNext();) {
					Entry<String, String> formEntry = iter.next();
					String formKey = formEntry.getKey();
					String formValue = formEntry.getValue();

					// 替换TAB Field所对应的表单
					updateFormTabMenu(formMap, formValue);
					updateFormDialog(viewMap, formValue);
					if (view.getActivityXML() != null) {
						activityXML = activityXML
								.replaceAll(formKey, formValue);
					}
					if (view.getColumnXML() != null) {
						columnXML = columnXML.replaceAll(formKey, formValue);
					}
					if (filterScript != null) {
						filterScript = getFilterScript(filterScript, formKey,
								formValue, sqlScript);
					}
					if (sqlScript != null && sqlScript.length() > 0) {
						sqlScript = getFilterScript(filterScript, formKey,
								formValue, sqlScript);
					}
					searchForm = getSearchForm(view.getSearchForm(), formKey,
							formValue);
				}
				// replace style
				if (styleMap != null && styleMap.size() > 0) {
					StyleRepositoryProcess process = (StyleRepositoryProcess) ProcessFactory
							.createProcess(StyleRepositoryProcess.class);
					for (Iterator<Entry<String, String>> iterator2 = styleMap.entrySet().iterator(); iterator2
							.hasNext();) {
						Entry<String, String> style = iterator2.next();
						if (view.getStyle().getId().equals(style.getKey())) {
							StyleRepositoryVO styleVO = (StyleRepositoryVO) process
									.doView(style.getValue());
							view.setStyle(styleVO);
						}
					}
				}
				String relatedResourceid = view.getRelatedResourceid();
				view.setActivitys(null);
				view.setRelatedResourceid(relatedResourceid);
				view.setSqlFilterScript(sqlScript);
				view.setActivityXML(activityXML);
				view.setColumns(null);
				view.setColumnXML(columnXML);
				view.setFilterScript(filterScript);
				view.setSearchForm(searchForm);
				viewProcess.doUpdate(view);

			}
		}
	}

	/**
	 * updateFormDialog
	 * 
	 * @param map
	 * @param formid
	 * @throws Exception
	 */
	public void updateFormDialog(Map<String, String> map, String formid) throws Exception {
		FormProcess formProcess = (FormProcess) ProcessFactory
				.createProcess(FormProcess.class);
		Form form = (Form) formProcess.doView(formid);
		String template = form.getTemplatecontext();
		for (Iterator<Entry<String, String>> iterator = map.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, String> entry = iterator.next();
			if (form.getTemplatecontext() != null) {
				template = template.replaceAll(entry.getKey(), entry.getValue());
			}
		}
		form.setTemplatecontext(template);
		formProcess.doUpdate(form);
	}

	/**
	 * resplase from is tabMenu
	 * 
	 * @param map
	 * @param formId
	 * @throws Exception
	 */
	public void updateFormTabMenu(Map<String, String> map, String formId) throws Exception {
		FormProcess formProcess = (FormProcess) ProcessFactory
				.createProcess(FormProcess.class);
		Form form = (Form) formProcess.doView(formId);
		String template = form.getTemplatecontext();
		for (Iterator<Entry<String, String>> iterator = map.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, String> resourceEntry = iterator.next();
			String formIdKey = resourceEntry.getKey();
			String formIdValue = resourceEntry.getValue();
			if (form.getTemplatecontext() != null) {
				template = template.replaceAll(formIdKey, formIdValue);
			}
		}
		form.setTemplatecontext(template);
		formProcess.doUpdate(form);
	}

	/**
	 * resplace view filterScript
	 * 
	 * @param filterscript
	 * @param key
	 * @param value
	 * @param sqlScript
	 * @return
	 * @throws Exception
	 */
	public String getFilterScript(String filterscript, String key,
			String value, String sqlScript) throws Exception {
		FormProcess formProcess = (FormProcess) ProcessFactory
				.createProcess(FormProcess.class);
		Form oldForm = (Form) formProcess.doView(key);
		Form newForm = (Form) formProcess.doView(value);
		if (oldForm != null && newForm != null && filterscript != null) {
			return replaceFormName(oldForm, newForm, filterscript, sqlScript);
		}
		return filterscript;
	}

	public String replaceFormName(Form oldForm, Form newForm,
			String filterscript, String sqlScript) {
		int index = filterscript.toLowerCase().indexOf("$formname");
		if (index != -1) {
			try {
				String regex = "\\$formname[ ]*=[ ]*'((/*([\\u4e00-\\u9fa5a-zA-Z0-9_]+))*)'[^and]";
				Pattern pattern = Pattern.compile(regex,
						Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(filterscript);
				if (matcher.find()) {
					String fullName = matcher.group(1);
					String formname = matcher.group(3);
					if (formname.equalsIgnoreCase(oldForm.getName())) {
						filterscript = filterscript.replaceAll(fullName,
								newForm.getName());
					}
				}
			} catch (RuntimeException e) {
				e.printStackTrace();
			}

			return filterscript;
		} else if (sqlScript != null && sqlScript.length() > 0) {
			if (oldForm.getName() != null && newForm.getName() != null) {
				sqlScript = sqlScript.replaceAll(oldForm.getName(), newForm
						.getName());
			}
			return sqlScript;
		}
		return "";
	}

	/**
	 * get serchform
	 * 
	 * @param form
	 * @param formKey
	 * @param formValue
	 * @return
	 * @throws Exception
	 */
	public Form getSearchForm(Form form, String formKey, String formValue)
			throws Exception {
		FormProcess formProcess = (FormProcess) ProcessFactory
				.createProcess(FormProcess.class);
		if (formKey != null && formValue != null) {
			Form newForm = (Form) formProcess.doView(formValue);
			if (newForm.equals(form)) {
				return newForm.getType() == 256 ? newForm : null;
			}
		}
		return null;
	}

	/**
	 * resplace module view form
	 * 
	 * @param application
	 * @throws Exception
	 */
	public void resplace(String application) throws Exception {
		replaceView();
		resplaceFrom();
		resplaceExcelMapping(application);
	}

	/**
	 * 替换excel的导入的配置的XML，只是替换表单，function中的表单名，字段名不需要替换,
	 * 
	 * @param application
	 * @throws Exception
	 */
	public void resplaceExcelMapping(String application) throws Exception {
		IMPMappingConfigProcess process = (IMPMappingConfigProcess) ProcessFactory
				.createProcess(IMPMappingConfigProcess.class);
		FormProcess formProcess = (FormProcess) ProcessFactory
				.createProcess(FormProcess.class);
		Collection<IMPMappingConfigVO> mappings = process.getAllMappingConfig(application);
		if (mappings != null && mappings.size() > 0) {
			for (Iterator<IMPMappingConfigVO> iterator = mappings.iterator(); iterator.hasNext();) {
				IMPMappingConfigVO vo = iterator.next();
				for (Iterator<Entry<String, String>> iter = formMap.entrySet().iterator(); iter
						.hasNext();) {
					Entry<String, String> obj = iter.next();
					Form keyForm = (Form) formProcess.doView(obj.getKey());
					Form valueForm = (Form) formProcess.doView(obj.getValue());
					String xml = vo.getXml();
					String regex = "<name>([u9fa5a-zA-Z0-9_ ]+)</name>"; // 用正则来对表名进行修改
					Pattern pattern = Pattern.compile(regex,
							Pattern.CASE_INSENSITIVE);
					Matcher matcher = pattern.matcher(xml);
					if (matcher.find()) {
						String group1 = matcher.group(1);
						if (group1.equals(keyForm.toString())) {
							xml = xml.replaceAll(group1, valueForm.getName());// 替换form的名字
						}
					}
					xml = this.replaceFormName(keyForm, valueForm, xml, null);
					vo.setXml(xml);
				}
				process.doUpdate(vo);
			}
		}
	}

	/**
	 * resplace resouce superior
	 * 
	 * @param application
	 * @throws Exception
	 */
	public void resplaceResource(String application) throws Exception {
		ResourceProcess respocess = (ResourceProcess) ProcessFactory
				.createProcess(ResourceProcess.class);
		//PermissionProcess process = (PermissionProcess) ProcessFactory
				//.createProcess(PermissionProcess.class);
		Collection<ResourceVO> reslist = respocess.doSimpleQuery(null, application);
		if (reslist != null && reslist.size() > 0) {
			for (Iterator<ResourceVO> iterator = reslist.iterator(); iterator.hasNext();) {
				ResourceVO resource = iterator.next();
				// superior
				for (Iterator<Entry<String, String>> iterator2 = resourceMap.entrySet().iterator(); iterator2
						.hasNext();) {
					Entry<String, String> entry = iterator2.next();
					String key = entry.getKey();
					String value =  entry.getValue();
					if (resource.getSuperior() != null) {
						if (key.equals(resource.getSuperior().getId())) {
							resource.setSuperior((ResourceVO) respocess
									.doView(value));
						}
					}
				}
				if (resource.getType().equals("00")) {
					if ((moduleMap != null && moduleMap.size() > 0)
							|| (formMap != null && formMap.size() > 0)) {
						for (Iterator<Entry<String, String>> iter = moduleMap.entrySet().iterator(); iter
								.hasNext();) {
							Entry<String, String> entry =  iter.next();
							if (entry.getKey().equals(resource.getModule())) {
								resource.setModule(entry.getValue());
							}
						}
						for (Iterator<Entry<String, String>> iterator2 = viewMap.entrySet().iterator(); iterator2
								.hasNext();) {
							Entry<String, String> object = iterator2
									.next();
							if (object.getKey().equals(
									resource.getDisplayView())) {
								resource.setDisplayView(object.getValue());
							}
						}
					}
				}
				Set<PermissionVO> coll = new HashSet<PermissionVO>();
				coll.addAll(resource.getRelatedPermissions());
				resource.setRelatedPermissions(coll);
				respocess.doUpdate(resource);
			}
		}
	}
}
