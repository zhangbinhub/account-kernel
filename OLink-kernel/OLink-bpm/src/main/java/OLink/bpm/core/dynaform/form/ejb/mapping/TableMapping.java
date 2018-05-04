package OLink.bpm.core.dynaform.form.ejb.mapping;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import OLink.bpm.core.dynaform.document.dql.DQLASTUtil;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.form.ejb.FormField;
import OLink.bpm.core.dynaform.form.ejb.ValueStoreField;
import OLink.bpm.util.StringUtil;
import OLink.bpm.util.json.JsonUtil;

/**
 * 数据库表格映射类
 * 
 * @author Administrator
 * 
 */
public class TableMapping implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1437434521272759850L;

	public final static String MAPPINGID = "MAPPINGID";

	/**
	 * 表单名称
	 */
	private String formName;

	/**
	 * 数据库表格名称
	 */
	private String tableName;

	/**
	 * 应用ID
	 */
	private String applicationId;

	/**
	 * 表单类型
	 */
	private int formType = Form.FORM_TYPE_NORMAL;

	/**
	 * 字段映射集合
	 */
	private Map<String, ColumnMapping> columnMappingMap = new LinkedHashMap<String, ColumnMapping>();

	private ColumnMapping primaryKeyMapping = null;

	public TableMapping(Form form) {
		this.setFormName(form.getName());
		this.setApplicationId(form.getApplicationid());
		this.setFormType(form.getType());

		switch (form.getType()) {
		case Form.FORM_TYPE_NORMAL_MAPPING: // 带映射的表单
			if (!StringUtil.isBlank(form.getMappingStr())) {
				Map<?, ?> map = JsonUtil.toMap(form.getMappingStr());

				this.setTableName((String) map.get("tableName"));

				Object[] columnMappings = (Object[]) map.get("columnMappings");

				if (columnMappings != null) {
					for (int i = 0; i < columnMappings.length; i++) {
						Map<?, ?> columnMap = (Map<?, ?>) columnMappings[i];
						String mappingFieldName = (String) columnMap.get("fieldName");

						ColumnMapping columnMapping = new ColumnMapping();
						if (TableMapping.MAPPINGID.equals(mappingFieldName)) { // 非主键
							columnMapping.setPrimaryKey(true);
						}

						// 3. 设置数据字段名称
						columnMapping.setColumnName(((String) columnMap.get("columnName")).toUpperCase());
						columnMapping.setFieldName(mappingFieldName);
						this.addColumnMapping(columnMapping);
					}
				}

				// 4. 添加主键映射
				if (columnMappings != null) {
					for (int i = 0; i < columnMappings.length; i++) {
						Map<?, ?> columnMap = (Map<?, ?>) columnMappings[i];
						String mappingFieldName = (String) columnMap.get("fieldName");
						if (TableMapping.MAPPINGID.equals(mappingFieldName)) { // 主键
							ColumnMapping pkMapping = new ColumnMapping();
							pkMapping.setFieldName(mappingFieldName);
							pkMapping.setColumnName(((String) columnMap.get("columnName")).toUpperCase());

							this.setPrimaryKeyMapping(pkMapping);
						}
					}
				}
			}

			break;
		case Form.FORM_TYPE_SUBFORM:
		case Form.FORM_TYPE_HOMEPAGE:
		case Form.FORM_TYPE_SEARCHFORM:
		case Form.FORM_TYPE_NORMAL: // 不带映射的表单
			this.setTableName(DQLASTUtil.TBL_PREFIX + form.getName());

			for (Iterator<FormField> iterator = form.getAllFields().iterator(); iterator.hasNext();) {
				FormField field = iterator.next();

				if (!(field instanceof ValueStoreField)) {
					continue;
				}

				ColumnMapping columnMapping = new ColumnMapping();
				columnMapping.setColumnName((DQLASTUtil.ITEM_FIELD_PREFIX + field.getName()).toUpperCase());
				columnMapping.setFieldName(field.getName()); // 2.设置表单字段名称
				this.addColumnMapping(columnMapping);
			}
			break;

		default:
			break;
		}
	}

	public String getTableName() {
		return getTableName(DQLASTUtil.TABEL_TYPE_CONTENT);
	}

	public String getTableName(int tableType) {
		if (tableName != null) {
			if (tableName.startsWith(DQLASTUtil.TBL_PREFIX) && tableType != DQLASTUtil.TABEL_TYPE_CONTENT) {
				return DQLASTUtil.getItemTblName(tableName.substring(DQLASTUtil.TBL_PREFIX.length()), tableType);
			}
			return DQLASTUtil.getItemTblName(tableName, tableType);
		}
		return getFormName();
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void addColumnMapping(ColumnMapping columnMapping) {
		columnMappingMap.put(columnMapping.getFieldName(), columnMapping);
	}

	public Collection<ColumnMapping> getColumnMappings() {
		return columnMappingMap.values();
	}

	/**
	 * 根据表单字段名称，获取数据库字段名称
	 * 
	 * @param fieldName
	 *            表单字段名称
	 * @return
	 */
	public String getColumnName(String fieldName) {
		ColumnMapping columnMapping = columnMappingMap.get(fieldName);
		if (columnMapping != null) {
			return columnMapping.getColumnName();
		}

		if (!StringUtil.isBlank(fieldName)) {
			return DQLASTUtil.ITEM_FIELD_PREFIX + fieldName;
		}

		return "";
	}

	/**
	 * 根据表单字段名称，获取数据库字段名称
	 * 
	 * @param fieldName
	 *            表单字段名称
	 * @return
	 */
	public String getFieldName(String columnName) {
		Set<?> entrySet = columnMappingMap.entrySet();
		for (Iterator<?> iterator = entrySet.iterator(); iterator.hasNext();) {
			Entry<?, ?> entry = (Entry<?, ?>) iterator.next();

			ColumnMapping columnMapping = (ColumnMapping) entry.getValue();
			if (columnName.equalsIgnoreCase(columnMapping.getColumnName())) {
				return columnMapping.getFieldName();
			}
		}

		return "";
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public String getPrimaryKeyName() {
		ColumnMapping mapping = getPrimaryKeyMapping();
		if (mapping != null && !StringUtil.isBlank(mapping.getColumnName())) {
			return mapping.getColumnName().toUpperCase();
		}
		return "ID";
	}

	/**
	 * 获取主键字段
	 * 
	 * @return 主键
	 */
	public ColumnMapping getPrimaryKeyMapping() {
		return primaryKeyMapping;
	}

	public void setPrimaryKeyMapping(ColumnMapping primaryKeyMapping) {
		this.primaryKeyMapping = primaryKeyMapping;
	}

	/**
	 * 获取数据库字段列表字符串，以","分隔
	 * 
	 * @return
	 */
	public String getColumnListString() {
		StringBuffer buffer = new StringBuffer();
		if (columnMappingMap != null && !columnMappingMap.isEmpty()) {
			Set<?> entrySet = columnMappingMap.entrySet();
			for (Iterator<?> iterator = entrySet.iterator(); iterator.hasNext();) {
				Entry<?, ?> entry = (Entry<?, ?>) iterator.next();
				ColumnMapping columnMapping = (ColumnMapping) entry.getValue();
				if (!columnMapping.isPrimaryKey()) {
					buffer.append(columnMapping.getColumnName());
					buffer.append(",");
				}
			}

			if (!buffer.toString().equals("")) {
				buffer.deleteCharAt(buffer.lastIndexOf(","));
			}
		}

		return buffer.toString();
	}

	public String toString() {
		if (getColumnMappings() != null) {
			return getColumnMappings().toString();
		}

		return super.toString();
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public int getFormType() {
		return formType;
	}

	public void setFormType(int formType) {
		this.formType = formType;
	}

}
