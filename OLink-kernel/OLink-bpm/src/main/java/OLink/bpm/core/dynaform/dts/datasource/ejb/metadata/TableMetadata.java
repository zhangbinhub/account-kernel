package OLink.bpm.core.dynaform.dts.datasource.ejb.metadata;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSource;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.form.ejb.FormField;
import OLink.bpm.core.dynaform.form.ejb.IncludeField;
import OLink.bpm.core.dynaform.form.ejb.ValueStoreField;
import OLink.bpm.core.dynaform.form.ejb.mapping.TableMapping;
import OLink.bpm.core.table.model.Column;
import OLink.bpm.core.table.model.Table;
import OLink.bpm.util.DbTypeUtil;
import OLink.bpm.util.StringUtil;

/**
 * 2.6新增
 * 
 * @author Administrator
 * 
 */
public class TableMetadata implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2498649312385458136L;

	private DataSource datasource;
	private Form form;
	private String table;
	private Integer type;
	private String description;
	private Set<ColumnMetadata> columnMetadatas = new HashSet<ColumnMetadata>();

	public DataSource getDatasource() {
		return datasource;
	}

	public void setDatasource(DataSource datasource) {
		this.datasource = datasource;
	}

	public Form getForm() {
		return form;
	}

	public void setForm(Form form) {
		this.form = form;
	}

	public String getTable() {
		return this.table;
	}

	public String get_table() {
		if (!StringUtil.isBlank(getTable())) {
			return getTable();
		} else {
			return getDefaultTable();
		}
	}

	private String getDefaultTable() {
		if (this.form != null) {
			TableMapping tableMapping = new TableMapping(this.form);
			return tableMapping.getTableName();
		}
		return null;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<ColumnMetadata> getColumnMetadatas() {
		return columnMetadatas;
	}

	public DataPackage<ColumnMetadata> get_columnMetadatas() {
		if (getColumnMetadatas() != null && getColumnMetadatas().size() > 0) {
			DataPackage<ColumnMetadata> datas = new DataPackage<ColumnMetadata>();
			datas.setDatas(getColumnMetadatas());
			return datas;
		} else {
			DataPackage<ColumnMetadata> datas = new DataPackage<ColumnMetadata>();
			Collection<ColumnMetadata> fieldInfos = new ArrayList<ColumnMetadata>();
			if (this.form != null) {
				Collection<String> columns = new ArrayList<String>();
				try {
					columns = getAllColumn();
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				TableMapping tableMapping = new TableMapping(this.form);
				Collection<FormField> formFields = this.form.getFields();
				Iterator<FormField> it = formFields.iterator();
				while (it.hasNext()) {
					FormField field = it.next();
					if (field != null) {
						ColumnMetadata columnMetadata = new ColumnMetadata();
						if (field instanceof ValueStoreField) {
							columnMetadata.setColumn(columns
									.contains(tableMapping.getColumnName(field
											.getName())) ? tableMapping
									.getColumnName(field.getName()) : "");
						} else {
							columnMetadata.setColumn("/");
						}
						columnMetadata
								.setDes(field.getDiscript() != null ? StringUtil
										.dencodeHTML(field.getDiscript())
										: "");
						columnMetadata.setField(field.getName() != null ? field
								.getName() : "");
						String type = field.getTagName();
						if (!StringUtil.isBlank(type)) {
							type = "{*[" + type + "]*}";
						}
						if (field instanceof IncludeField) {// 包含元素
							String includeType = ((IncludeField) field)
									.getIncludeType();
							if (!StringUtil.isBlank(type)
									&& IncludeField.INCLUDE_TYPE_VIEW
											.equals(includeType)) {
								type = type + "({*[View]*})";
							} else if (!StringUtil.isBlank(type)
									&& IncludeField.INCLUDE_TYPE_PAGE
											.equals(includeType)) {
								type = type + "({*[HomePage]*})";
							}
						}
						String col_type = field.getFieldtype();
						if (!StringUtil.isBlank(col_type)) {
							columnMetadata.setFieldType("{*[" + col_type
									+ "]*}");
						}
						columnMetadata.setType(type);

						fieldInfos.add(columnMetadata);
					}
				}
			}
			datas.setDatas(fieldInfos);
			return datas;
		}
	}

	private Collection<String> getAllColumn() throws SQLException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		Collection<String> columns = new ArrayList<String>();
		if (this.form != null && this.datasource != null) {
			TableMapping tableMapping = new TableMapping(this.form);
			Table table = null;
			try {
				table = DbTypeUtil.getTable(tableMapping.getTableName(),
						this.datasource.getDbTypeName(), this.datasource
								.getConnection());
			} catch (SQLException e) {
				throw e;
			} catch (InstantiationException e) {
				throw e;
			} catch (IllegalAccessException e) {
				throw e;
			} catch (ClassNotFoundException e) {
				throw e;
			}
			if (table != null) {
				Collection<Column> cs = table.getColumns();
				Iterator<Column> it = cs.iterator();
				while (it.hasNext()) {
					columns.add(it.next().getName());
				}
			}
		}
		return columns;
	}

	public void setColumnMetadatas(Set<ColumnMetadata> columnMetadatas) {
		this.columnMetadatas = columnMetadatas;
	}

}
