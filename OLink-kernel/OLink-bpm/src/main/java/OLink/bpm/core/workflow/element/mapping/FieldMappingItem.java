package OLink.bpm.core.workflow.element.mapping;


/**
 * 字段映射项
 * @author Happy
 *
 */
public class FieldMappingItem implements Comparable<FieldMappingItem>{
	
	/**
	 * 父流程表单字段
	 */
	private String parentField;
	
	/**
	 * 子流程表单字段
	 */
	private String subField;
	
	/**
	 * 子流程表单字段的初始化脚本
	 */
	private String script;

	public String getParentField() {
		return parentField;
	}

	public void setParentField(String parentField) {
		this.parentField = parentField;
	}

	public String getSubField() {
		return subField;
	}

	public void setSubField(String subField) {
		this.subField = subField;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public int compareTo(FieldMappingItem o) {
		if (o != null) {
			if(this.subField == o.subField && this.parentField == o.parentField && this.script == o.script) return 0;
		}
		return -1;	
	}
	
	
	
}
