package OLink.bpm.core.dynaform.dts.exp.columnmapping.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import OLink.bpm.base.action.BaseAction;
import OLink.bpm.core.deploy.module.ejb.ModuleProcess;
import OLink.bpm.core.deploy.module.ejb.ModuleVO;
import OLink.bpm.core.dynaform.dts.exp.columnmapping.ejb.ColumnMapping;
import OLink.bpm.core.dynaform.dts.exp.columnmapping.ejb.ColumnMappingProcess;
import OLink.bpm.core.dynaform.dts.exp.mappingconfig.ejb.MappingConfig;
import OLink.bpm.core.dynaform.dts.exp.mappingconfig.ejb.MappingConfigProcess;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.util.ProcessFactory;

/**
 * @author nicholas
 */
public class ColumnMappingAction extends BaseAction<ColumnMapping> {
	
	private static final long serialVersionUID = 5644807753339212649L;

	/**
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public ColumnMappingAction() throws ClassNotFoundException {
		super(ProcessFactory.createProcess(ColumnMappingProcess.class),
				new ColumnMapping());
	}

	private static Map<String, String> _TOTYPE = new TreeMap<String, String>();

	private static Map<String, String> _COLMAPTYPE = new TreeMap<String, String>();

	private String _moduleid;

	private String _formid;

	private String stringfieldnames = null;

	private String decimalfieldnames = null;

	private String datafieldnames = null;

	static {
		_COLMAPTYPE.put(ColumnMapping.COLUMN_TYPE_FIELD, "field");
		_COLMAPTYPE.put(ColumnMapping.COLUMN_TYPE_SCRIPT, "script");

	}

	String mappingid;

	static {
		_TOTYPE.put(ColumnMapping.DATA_TYPE_DATE, "date");
		_TOTYPE.put(ColumnMapping.DATA_TYPE_NUMBER, "decimal");
		_TOTYPE.put(ColumnMapping.DATA_TYPE_VARCHAR, "String");
	}

	/**
	 * @return the _TOTYPE
	 * @uml.property name="_TOTYPE"
	 */
	public static Map<String, String> get_TOTYPE() {
		return _TOTYPE;
	}

	public String doSave() {
		try {
			ColumnMapping colMap = (ColumnMapping) this.getContent();
			boolean flag = true;
			// 判断是否为field 如果是则field则一定要有value

			MappingConfigProcess cp = (MappingConfigProcess) ProcessFactory
					.createProcess(MappingConfigProcess.class);
			MappingConfig mappingConfigVO = (MappingConfig) cp
					.doView(getMappingid());

			colMap.setMappingConfig(mappingConfigVO);
			// 查找相同toname的记录
			String toName = colMap.getToName();

			Collection<ColumnMapping> repeteName = ((ColumnMappingProcess) process)
					.getColMapBytoName(toName, getApplication());
			// 判断是否也是同一个子mappingconfig，如果是则不能新建，否则能新建
			for (Iterator<ColumnMapping> iter = repeteName.iterator(); iter.hasNext();) {
				ColumnMapping temp = iter.next();
				if (temp.getId() != null
						&& temp.getMappingConfig().getId().equals(
								colMap.getMappingConfig().getId())
						&& !temp.getId().equals(colMap.getId())) {

					if (this.getFieldErrors().size() > 0) {
						this.getFieldErrors().clear();

					}
					this.addFieldError("6",
							"{*[core.dts.exp.columnmapping.tonameexist]*}");
					flag = false;

				}

			}

			if (colMap.getType().equals(ColumnMapping.COLUMN_TYPE_FIELD)) {
				if (colMap.getFromName().equals("")) {
					this.addFieldError("1",
							"{*[core.dts.exp.columnmapping.field]*}");
					flag = false;

				}
				// 判断是否为script 如果是则valuescript则一定要有value
			} else if (colMap.getType()
					.equals(ColumnMapping.COLUMN_TYPE_SCRIPT)) {

				if (colMap.getValuescript().equals("")) {
					this.addFieldError("2",
							"{*[core.dts.exp.columnmapping.valuescript]*}");
					flag = false;

				}
			}

			// 如果选择的是string类型则长度一定要填写
			if (colMap.getToType().equals(ColumnMapping.DATA_TYPE_VARCHAR)) {
				if (colMap.getLength().equals("")) {
					this.addFieldError("3",
							"{*[core.dts.exp.columnmapping.length]*}!");
					flag = false;
				}
			} else if (colMap.getToType()
					.equals(ColumnMapping.DATA_TYPE_NUMBER)) {
				// 如果选择的是decimal类型则长度和精确度一定要填写
				if (colMap.getLength().equals("")) {
					this.addFieldError("4",
							"{*[core.dts.exp.columnmapping.length]*}");
					flag = false;
				}

				if (colMap.getPrecision().equals("")) {
					this.addFieldError("5",
							"{*[core.dts.exp.columnmapping.precision]*}");
					flag = false;
				}
			}

			if (get_moduleid() != null && !get_moduleid().equals("")
					&& !get_moduleid().equals("none")) {
				ModuleProcess mp = (ModuleProcess) ProcessFactory
						.createProcess(ModuleProcess.class);
				ModuleVO mv = (ModuleVO) mp.doView(this.get_moduleid());
				colMap.setModule(mv);
			}

			if (_formid != null && _formid.trim().length() > 0
					&& !_formid.equals("none")) {
				FormProcess fp = (FormProcess) ProcessFactory
						.createProcess(FormProcess.class);
				Form fm = (Form) fp.doView(_formid);
				colMap.setForm(fm);
			}

			if (flag) {
				return super.doSave();
			} else {
				return SUCCESS;
			}
		} catch (Exception e) {
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}

	}

	public String doSaveColumns() throws Exception {

		String sTypefield = null;
		String[] sTypefields = null;
		if (this.getStringfieldnames().length() > 0
				&& !this.getStringfieldnames().equals("")) {
			sTypefield = this.getStringfieldnames();
			sTypefields = sTypefield.split(";");
		}
		String decimalfield = null;
		String[] decimalfields = null;
		if (this.getDecimalfieldnames().length() > 0
				&& !this.getDecimalfieldnames().equals("")) {
			decimalfield = this.getDecimalfieldnames();
			decimalfields = decimalfield.split(";");
		}

		String datafield = null;
		String[] datafields = null;

		if (this.getDatafieldnames().length() > 0
				&& !this.getDatafieldnames().equals("")) {
			datafield = this.getDatafieldnames();
			datafields = datafield.split(";");
		}

		ColumnMapping colMap = ((ColumnMapping) getContent());

		// 判断是否为field 如果是则field则一定要有value

		MappingConfigProcess cp = (MappingConfigProcess) ProcessFactory
				.createProcess(MappingConfigProcess.class);
		MappingConfig mappingConfigVO = (MappingConfig) cp
				.doView(getMappingid());

		colMap.setMappingConfig(mappingConfigVO);

		if (get_moduleid() != null && !get_moduleid().equals("")
				&& !get_moduleid().equals("none")) {
			ModuleProcess mp = (ModuleProcess) ProcessFactory
					.createProcess(ModuleProcess.class);
			ModuleVO mv = (ModuleVO) mp.doView(this.get_moduleid());
			colMap.setModule(mv);
		}

		if (_formid != null && _formid.trim().length() > 0
				&& !_formid.equals("none")) {
			FormProcess fp = (FormProcess) ProcessFactory
					.createProcess(FormProcess.class);
			Form fm = (Form) fp.doView(_formid);
			colMap.setForm(fm);
		}
		List<ColumnMapping> col = new ArrayList<ColumnMapping>();
		if (sTypefields != null && sTypefields.length > 0) {
			for (int i = 0; i < sTypefields.length; i++) {
				ColumnMapping colMaps = new ColumnMapping();

				colMaps.setForm(colMap.getForm());
				colMaps.setLength("255");
				colMaps.setApplicationid(colMap.getApplicationid());
				colMaps.setFromName(sTypefields[i]);
				colMaps.setToType(ColumnMapping.DATA_TYPE_VARCHAR);
				colMaps.setModule(colMap.getModule());
				colMaps.setMappingConfig(colMap.getMappingConfig());
				colMaps.setType(colMap.getType());
				colMaps.setToName(sTypefields[i]);
				col.add(colMaps);
			}

		}
		if (decimalfields != null && decimalfields.length > 0) {
			for (int i = 0; i < decimalfields.length; i++) {
				ColumnMapping colMaps = new ColumnMapping();

				colMaps.setForm(colMap.getForm());
				colMaps.setLength("40");
				colMaps.setApplicationid(colMap.getApplicationid());
				colMaps.setFromName(decimalfields[i]);
				colMaps.setToType(ColumnMapping.DATA_TYPE_NUMBER);
				colMaps.setModule(colMap.getModule());
				colMaps.setMappingConfig(colMap.getMappingConfig());
				colMaps.setType(colMap.getType());
				colMaps.setToName(decimalfields[i]);
				colMaps.setPrecision("3");
				col.add(colMaps);
			}

		}
		if (datafields != null && datafields.length > 0
				&& !datafields[0].equals("")) {
			for (int i = 0; i < datafields.length; i++) {
				ColumnMapping colMaps = new ColumnMapping();

				colMaps.setForm(colMap.getForm());
				colMaps.setApplicationid(colMap.getApplicationid());
				colMaps.setFromName(datafields[i]);
				colMaps.setToType(ColumnMapping.DATA_TYPE_DATE);
				colMaps.setModule(colMap.getModule());
				colMaps.setMappingConfig(colMap.getMappingConfig());
				colMaps.setType(colMap.getType());
				colMaps.setToName(datafields[i]);
				col.add(colMaps);

			}

		}
		if (col != null && col.size() > 0) {
			for (int i = 0; i < col.size(); i++) {
				ColumnMapping colMaps = col.get(i);

				this.process.doCreate(colMaps);

			}
			return SUCCESS;
		} else {
			return INPUT;
		}

	}

	/**
	 * @return the mappingid
	 * @uml.property name="mappingid"
	 */
	public String getMappingid() {
		return mappingid;
	}

	/**
	 * @param mappingid
	 *            the mappingid to set
	 * @uml.property name="mappingid"
	 */
	public void setMappingid(String mappingid) {
		this.mappingid = mappingid;
	}

	/**
	 * @return the _COLMAPTYPE
	 * @uml.property name="_COLMAPTYPE"
	 */
	public static Map<String, String> get_COLMAPTYPE() {
		return _COLMAPTYPE;
	}

	/**
	 * @return the _moduleid
	 * @uml.property name="_moduleid"
	 */
	public String get_moduleid() {
		ColumnMapping colMap = (ColumnMapping) this.getContent();
		if (colMap.getModule() != null) {
			_moduleid = colMap.getModule().getId();
		}
		return _moduleid;
	}

	/**
	 * @param _moduleid
	 *            the _moduleid to set
	 * @uml.property name="_moduleid"
	 */
	public void set_moduleid(String _moduleid) {
		this._moduleid = _moduleid;
	}

	/**
	 * @return the _formid
	 * @uml.property name="_formid"
	 */
	public String get_formid() {
		ColumnMapping colMap = (ColumnMapping) this.getContent();
		if (colMap.getForm() != null) {
			_formid = colMap.getForm().getId();
		}
		return _formid;
	}

	/**
	 * @param _formid
	 *            the _formid to set
	 * @uml.property name="_formid"
	 */
	public void set_formid(String _formid) {
		this._formid = _formid;
	}

	/**
	 * @return the datafieldnames
	 * @uml.property name="datafieldnames"
	 */
	public String getDatafieldnames() {
		return datafieldnames;
	}

	/**
	 * @param datafieldnames
	 *            the datafieldnames to set
	 * @uml.property name="datafieldnames"
	 */
	public void setDatafieldnames(String datafieldnames) {
		this.datafieldnames = datafieldnames;
	}

	/**
	 * @return the decimalfieldnames
	 * @uml.property name="decimalfieldnames"
	 */
	public String getDecimalfieldnames() {
		return decimalfieldnames;
	}

	/**
	 * @param decimalfieldnames
	 *            the decimalfieldnames to set
	 * @uml.property name="decimalfieldnames"
	 */
	public void setDecimalfieldnames(String decimalfieldnames) {
		this.decimalfieldnames = decimalfieldnames;
	}

	/**
	 * @return the stringfieldnames
	 * @uml.property name="stringfieldnames"
	 */
	public String getStringfieldnames() {
		return stringfieldnames;
	}

	/**
	 * @param stringfieldnames
	 *            the stringfieldnames to set
	 * @uml.property name="stringfieldnames"
	 */
	public void setStringfieldnames(String stringfieldnames) {
		this.stringfieldnames = stringfieldnames;
	}

}
