package OLink.bpm.core.formula;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import OLink.bpm.base.action.BaseHelper;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.form.ejb.FormField;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.web.DWRHtmlUtils;

public class FormulaTreeHelper extends BaseHelper<FormulaTree> {
	public FormulaTreeHelper() {
		super(null);
	}

	private static Map<String, String> RELATION_SYMBOL = new LinkedHashMap<String, String>();

	private static Map<String, String> OPERATOR_SYMBOL = new LinkedHashMap<String, String>();

	private static Map<String, String> COMPARE_SYMBOL = new LinkedHashMap<String, String>();

	private static Map<String, String> ALL_SYMBOL = new LinkedHashMap<String, String>();

	static {
		RELATION_SYMBOL.put("AND", "AND");
		RELATION_SYMBOL.put("OR", "OR");

		OPERATOR_SYMBOL.put("+", "+");
		OPERATOR_SYMBOL.put("-", "-");
		OPERATOR_SYMBOL.put("*", "*");
		OPERATOR_SYMBOL.put("/", "/");

		COMPARE_SYMBOL.put("LIKE", "LIKE");
		COMPARE_SYMBOL.put(">", ">");
		COMPARE_SYMBOL.put(">=", ">=");
		COMPARE_SYMBOL.put("<", "<");
		COMPARE_SYMBOL.put("<=", "<=");
		COMPARE_SYMBOL.put("=", "=");
		COMPARE_SYMBOL.put("IN", "IN");
		COMPARE_SYMBOL.put("NOT IN", "NOT IN");

		ALL_SYMBOL.putAll(RELATION_SYMBOL);
		ALL_SYMBOL.putAll(OPERATOR_SYMBOL);
		ALL_SYMBOL.putAll(COMPARE_SYMBOL);
	}

	public static Map<String, String> getCOMPARE_SYMBOL() {
		return COMPARE_SYMBOL;
	}

	public static Map<String, String> getOPERATOR_SYMBOL() {
		return OPERATOR_SYMBOL;
	}

	public static Map<String, String> getRELATION_SYMBOL() {
		return RELATION_SYMBOL;
	}

	public static Map<String, String> getALL_SYMBOL() {
		return ALL_SYMBOL;
	}

	private Map<String, String> getFormsByModule(String moduleid, String application)
			throws Exception {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("", "Please Choose");

		FormProcess fp = (FormProcess) ProcessFactory
				.createProcess(FormProcess.class);
		Collection<Form> colls = fp.getFormsByModule(moduleid, application);
		for (Iterator<Form> iter = colls.iterator(); iter.hasNext();) {
			Form form = iter.next();
			map.put(form.getId(), form.getName());
		}

		return map;
	}

	private Map<String, String> getFieldsByForm(String formid) throws Exception {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("", "Please Choose");

		FormProcess fp = (FormProcess) ProcessFactory
				.createProcess(FormProcess.class);
		Form form = (Form) fp.doView(formid);

		if (form != null) {
			Collection<FormField> colls = form.getFields();
			for (Iterator<FormField> iter = colls.iterator(); iter.hasNext();) {
				FormField field = iter.next();
				map.put(field.getName(), field.getName());
			}
			map.put("$formname", "$formname");
			map.put("$lastmodified", "$lastmodified");
			map.put("$state.state", "$state.state");
		}
		return map;
	}

	public String createForm(String selectFieldName, String moduleid,
			String def, String application) throws Exception {
		Map<String, String> map = getFormsByModule(moduleid, application);
		return DWRHtmlUtils.createOptions(map, selectFieldName, def);
	}

	public String createFiled(String selectFieldName, String formid, String def)
			throws Exception {
		Map<String, String> map = getFieldsByForm(formid);

		return DWRHtmlUtils.createOptions(map, selectFieldName, def);
	}

	public String getFieldtype(String selectFieldName, String formid)
			throws Exception {
		FormProcess fp = (FormProcess) ProcessFactory
				.createProcess(FormProcess.class);
		Form form = (Form) fp.doView(formid);
		if (form != null) {
			Collection<FormField> colls = form.getFields();
			for (Iterator<FormField> iter = colls.iterator(); iter.hasNext();) {
				FormField field = iter.next();
				if (selectFieldName.equals(field.getName())) {
					return field.getFieldtype();
				}
			}
		}
		if (selectFieldName.indexOf("$") != -1) {
			return "VALUE_TYPE_PROPERTY";
		}
		return "";
	}

}
