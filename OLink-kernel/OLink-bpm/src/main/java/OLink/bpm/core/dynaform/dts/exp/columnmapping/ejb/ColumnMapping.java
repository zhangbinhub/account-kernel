package OLink.bpm.core.dynaform.dts.exp.columnmapping.ejb;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.deploy.module.ejb.ModuleVO;
import OLink.bpm.core.dynaform.dts.exp.mappingconfig.ejb.MappingConfig;
import OLink.bpm.core.dynaform.form.ejb.Form;


/**
 * @hibernate.class  table="T_COLUMNMAPPING"
 */
public class ColumnMapping extends ValueObject {
	
	private static final long serialVersionUID = -337271272128548692L;

	public static final String DATA_TYPE_VARCHAR = "VARCHAR";

	public static final String DATA_TYPE_NUMBER = "DECIMAL";

	public static final String DATA_TYPE_DATE = "Date";
	
	public static final String COLUMN_TYPE_SCRIPT = "COLUMNMEPPING_TYPE_SCRIPT";

	public static final String COLUMN_TYPE_FIELD = "COLUMNMAPPING_TYPE_FIELD";
	
	private String id;
	
	private String fromName;

	private String valuescript;

    private String toName;
    
    private String toType;
    
    private String length;
    
    private MappingConfig mappingConfig;
    
    private Form form;
    
	/*
	 * 所属Module
	 */
	private ModuleVO module;
    
    private String type = COLUMN_TYPE_FIELD;
    
    private String precision;
    
    /**
	 * @hibernate.property  column="TYPE"
	 * @return
	 * @uml.property  name="type"
	 */
    
    public String getType() {
		return type;
	}

	/**
	 * @param type  the type to set
	 * @uml.property  name="type"
	 */
	public void setType(String type) {
		this.type = type;
	}



	/**
	 * @return  Form
	 * @hibernate.many-to-one  class="Form"  column="FORMID"
	 * @uml.property  name="form"
	 */
	
	public Form getForm() {
		return form;
	}

	/**
	 * @param form  the form to set
	 * @uml.property  name="form"
	 */
	public void setForm(Form form) {
		this.form = form;
	}

	/**
	 * @return  ModuleVO
	 * @hibernate.many-to-one  class="ModuleVO"  column="MODULE"
	 * @uml.property  name="module"
	 */
	public ModuleVO getModule() {
		return module;
	}

	/**
	 * @param module  the module to set
	 * @uml.property  name="module"
	 */
	public void setModule(ModuleVO module) {
		this.module = module;
	}
	/**
	 * @hibernate.property  column="FROMNAME"
	 * @uml.property  name="fromName"
	 */
	public String getFromName() {
		return fromName;
	}

	/**
	 * @param fromName  The fromName to set.
	 * @uml.property  name="fromName"
	 */
	public void setFromName(String fromName) {
		this.fromName = fromName;
	}


	/**
	 * @hibernate.property  column="TONAME"
	 * @uml.property  name="toName"
	 */
	public String getToName() {
		return toName;
	}

	/**
	 * @param toName  The toName to set.
	 * @uml.property  name="toName"
	 */
	public void setToName(String toName) {
		this.toName = toName;
	}

	/**
	 * @hibernate.property  column="VALUESCRIPT" type = "text"
	 * @uml.property  name="valuescript"
	 */
	public String getValuescript() {
		return valuescript;
	}

	/**
	 * @param valuescript  The valuescript to set.
	 * @uml.property  name="valuescript"
	 */
	public void setValuescript(String valuescript) {
		this.valuescript = valuescript;
	}

	/**
	 * @hibernate.id  column="ID" generator-class="assigned"
	 * @uml.property  name="id"
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id  The id to set.
	 * @uml.property  name="id"
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @hibernate.property  column="TOTYPE"
	 * @uml.property  name="toType"
	 */
	public String getToType() {
		return toType;
	}

	/**
	 * @param toType  The toType to set.
	 * @uml.property  name="toType"
	 */
	public void setToType(String toType) {
		this.toType = toType;
	}

	/**
	 * @hibernate.many-to-one  column="MAPPINGCONFIG"  class="MappingConfig"
	 * @uml.property  name="mappingConfig"
	 */
	public MappingConfig getMappingConfig() {
		return mappingConfig;
	}

	/**
	 * @param mappingConfig  The mappingConfig to set.
	 * @uml.property  name="mappingConfig"
	 */
	public void setMappingConfig(MappingConfig mappingConfig) {
		this.mappingConfig = mappingConfig;
	}

	/**
	 * @hibernate.property  column="LENGTH"
	 * @uml.property  name="length"
	 */
	public String getLength() {
		return length;
	}

	/**
	 * @param length  The length to set.
	 * @uml.property  name="length"
	 */
	public void setLength(String length) {
		this.length = length;
	}

	/**
	 * @hibernate.property  column="PRECISIONS"
	 * @uml.property  name="precision"
	 */
	public String getPrecision() {
		return precision;
	}

	/**
	 * @param precision  The precision to set.
	 * @uml.property  name="precision"
	 */
	public void setPrecision(String precision) {
		this.precision = precision;
	}


}
