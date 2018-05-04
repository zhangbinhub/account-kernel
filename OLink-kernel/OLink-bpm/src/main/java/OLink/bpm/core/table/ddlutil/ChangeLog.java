package OLink.bpm.core.table.ddlutil;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.core.dynaform.document.dql.DQLASTUtil;
import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSource;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.form.ejb.FormField;
import OLink.bpm.core.dynaform.form.ejb.mapping.TableMapping;
import OLink.bpm.core.table.alteration.*;
import OLink.bpm.core.table.constants.FieldConstant;
import OLink.bpm.core.table.model.Column;
import OLink.bpm.core.table.model.DuplicateException;
import OLink.bpm.core.table.model.Table;
import OLink.bpm.util.DbTypeUtil;
import org.apache.log4j.Logger;

import OLink.bpm.core.table.alteration.AddTableChange;
import OLink.bpm.core.table.alteration.ColumnDataTypeChange;
import OLink.bpm.core.table.alteration.ColumnRenameChange;
import OLink.bpm.core.table.alteration.DropColumnChange;
import OLink.bpm.core.table.alteration.DropTableChange;
import OLink.bpm.core.table.alteration.ModelChange;
import OLink.bpm.core.table.alteration.TableRenameChange;

/**
 * @author nicholas
 */
public class ChangeLog {
	private final static Logger log = Logger.getLogger(ChangeLog.class);

	private Collection<ModelChange> changes = new ArrayList<ModelChange>();

	/**
	 * 新表格
	 */
	private Table _newTable;

	/**
	 * 旧表格
	 */
	private Table _oldTable;

	// private Table _logTable;

	/**
	 * 对比新旧两个表单(Form)的改变,如果oldForm为null则认为是新建table,如果newForm为null则认为是删除table
	 * 
	 * @param newForm
	 *            新表单
	 * @param oldForm
	 *            旧表单
	 * @throws Exception
	 */
	public void compare(Form newForm, Form oldForm) throws Exception {
		compare(newForm, oldForm, DQLASTUtil.TABEL_TYPE_CONTENT);
		filter();
	}

	/**
	 * 2.6新增
	 * 
	 * @param newForm
	 * @param oldForm
	 * @param dt
	 * @throws Exception
	 */
	public void compare(Form newForm, Form oldForm, DataSource dt)
			throws Exception {
		compare(newForm, oldForm, DQLASTUtil.TABEL_TYPE_CONTENT, dt);
		filter();
	}

	/**
	 * 过滤，将添加和删除(同一个字段时)等消
	 */
	public void filter() {
		Collection<ModelChange> rtn = new ArrayList<ModelChange>();
		outer: for (Iterator<ModelChange> iterator = changes.iterator(); iterator
				.hasNext();) {
			ModelChange change = iterator.next();
			if (change instanceof AddColumnChange) {
				String name = change.getTargetColumn()
						.getName();
				int typeCode = change.getTargetColumn()
						.getTypeCode();
				for (Iterator<ModelChange> iterator2 = changes.iterator(); iterator2
						.hasNext();) {
					ModelChange anChange = iterator2.next();
					if (anChange instanceof DropColumnChange) {
						String anName = ((DropColumnChange) anChange)
								.getSourceColumn().getName();
						int anTypeCode = ((DropColumnChange) anChange)
								.getSourceColumn().getTypeCode();
						if (name.equals(anName) && typeCode == anTypeCode) {
							continue outer;
						}
					}
				}
			}
			rtn.add(change);
		}
		changes = rtn;
	}

	public void compare(Form newForm, Form oldForm, int tableType)
			throws Exception {
		if (isMappingForm(newForm == null ? oldForm : newForm, tableType)) { // 是否为映射
			return;
		}

		_newTable = createTableByType(newForm, tableType);// 新表单的表格对象

		Table changedTable = createTableByType(oldForm, tableType);

		if (_newTable != null) {
			Table dbTable = getDBTable(newForm, tableType);
			Table oldDbTable = getDBTable(oldForm, tableType);

			// 1.旧表格存在，且相同则不新建
			if (dbTable == null && oldDbTable == null) {
				// 2. 新增表格
				ModelChange addTableChange = new AddTableChange(_newTable);
				changes.add(addTableChange);

				log.info("Add table create change,the new table is: "
						+ _newTable.getName());
			} else {
				if (isTypeChanged(newForm, oldForm)) { // 类型改变
					_oldTable = getDBTable(newForm, tableType);
				} else {
					_oldTable = getDBTable(oldForm, tableType);
				}

				if (_oldTable != null) {
					// 1.表单名称，且数据库名称都不相同
					if (_oldTable != null
							&& !_newTable.getName().equals(_oldTable.getName())) {
						ModelChange tableRenameChange = new TableRenameChange(
								changedTable, _newTable);

						changes.add(tableRenameChange);
						log.info("Add table rename change,new name is: "
								+ _newTable.getName());
					}

					if (tableType != DQLASTUtil.TABLE_TYPE_AUTH) {
						compareFields(newForm, oldForm);
					}
				}
			}
		} else {
			_oldTable = getDBTable(oldForm, tableType); // 数据库中的表格对象
			if (_oldTable != null) {
				// Add DropTableChange
				ModelChange dropTableChange = new DropTableChange(changedTable);
				changes.add(dropTableChange);
				log.info("Add table drop change,the drop table is: "
						+ changedTable.getName());
			}
		}
	}

	/**
	 * 2.6新增
	 * 
	 * @param newForm
	 * @param oldForm
	 * @param tableType
	 * @param dt
	 * @throws Exception
	 */
	public void compare(Form newForm, Form oldForm, int tableType, DataSource dt)
			throws Exception {
		if (isMappingForm(newForm == null ? oldForm : newForm, tableType)) { // 是否为映射
			return;
		}

		_newTable = createTableByType(newForm, tableType);// 新表单的表格对象

		Table changedTable = createTableByType(oldForm, tableType);

		if (_newTable != null) {
			Table dbTable = getDBTable(newForm, dt, tableType);
			Table oldDbTable = getDBTable(oldForm, dt, tableType);

			// 1.旧表格存在，且相同则不新建
			if (dbTable == null && oldDbTable == null) {
				// 2. 新增表格
				ModelChange addTableChange = new AddTableChange(_newTable);
				changes.add(addTableChange);

				log.info("Add table create change,the new table is: "
						+ _newTable.getName());
			} else {
				if (isTypeChanged(newForm, oldForm)) { // 类型改变
					_oldTable = getDBTable(newForm, dt, tableType);
				} else {
					_oldTable = getDBTable(oldForm, dt, tableType);
				}

				if (_oldTable != null) {
					// 1.表单名称，且数据库名称都不相同
					if (_oldTable != null
							&& !_newTable.getName().equals(_oldTable.getName())) {
						ModelChange tableRenameChange = new TableRenameChange(
								changedTable, _newTable);

						changes.add(tableRenameChange);
						log.info("Add table rename change,new name is: "
								+ _newTable.getName());
					}

					if (tableType != DQLASTUtil.TABLE_TYPE_AUTH) {
						compareFields(newForm, oldForm);
					}
				}
			}
		} else {
			_oldTable = getDBTable(oldForm, dt, tableType); // 数据库中的表格对象
			if (_oldTable != null) {
				// Add DropTableChange
				ModelChange dropTableChange = new DropTableChange(changedTable);
				changes.add(dropTableChange);
				log.info("Add table drop change,the drop table is: "
						+ changedTable.getName());
			}
		}
	}

	/**
	 * 是否已改变类型
	 * 
	 * @param newForm
	 * @param oldForm
	 * @return
	 */
	private boolean isTypeChanged(Form newForm, Form oldForm) {
		if (newForm != null && oldForm != null) {
			return newForm.getType() != oldForm.getType();
		}

		return false;
	}

	private boolean isMappingForm(Form newForm, int tableType) {
		if (newForm != null) {
			return newForm.getType() == Form.FORM_TYPE_NORMAL_MAPPING
					&& tableType == DQLASTUtil.TABEL_TYPE_CONTENT;
		}

		return false;
	}

	/**
	 * 对比新旧两个表单中域(Field)的改变,每个域等同于数据库中表的一列
	 * 
	 * @param newForm
	 * @param oldForm
	 * @throws Exception
	 */
	private void compareFields(Form newForm, Form oldForm) throws Exception {
		Collection<FormField> newFields = newForm.getValueStoreFields();
		Collection<FormField> oldFields = oldForm.getValueStoreFields();

		compareFieldsToAddOrModify(newFields, oldForm);
		compareFieldsToDrop(oldFields, newForm);

		compareFixedFieldsToAdd();
	}

	/**
	 * 对比固有字段
	 * 
	 * @param oldTable
	 *            旧表格
	 * @throws DuplicateException
	 */
	private void compareFixedFieldsToAdd() throws DuplicateException {
		Collection<Column> fixedColumns = getFixedColumns();
		for (Iterator<Column> iterator = fixedColumns.iterator(); iterator
				.hasNext();) {
			Column newCol = iterator.next();
			Column oldCol = _oldTable.findColumn(newCol.getName());
			// 旧表单中不存在固定字段则添加
			if (oldCol == null) {
				ModelChange addColumnChange = new AddColumnChange(_newTable,
						newCol);
				changes.add(addColumnChange);
			}
		}
	}

	/**
	 * 以新表单中的域为基准,与旧表单的域做比较,查出需要新增或者修改的地方
	 * 
	 * @param newFields
	 *            新表单域的集合
	 * @param oldForm
	 *            旧表单
	 * @throws Exception
	 */
	private void compareFieldsToAddOrModify(Collection<FormField> newFields,
			Form oldForm) throws Exception {

		for (Iterator<FormField> iter = newFields.iterator(); iter.hasNext();) {
			FormField newfield = iter.next();
			FormField oldfield = oldForm.findField(newfield.getId());

			Column targetColumn = createColumn(newfield); // 新表单映射字段
			Column orgColumn = createColumn(oldfield); // 旧表单映射字段
			Column dataBaseColumn = _oldTable
					.findColumn(targetColumn.getName()); // 数据库映射字段

			if (orgColumn == null) { // 数据库字段不存在
				// 1.Add AddColumnChange, if the oldfield not found
				if (dataBaseColumn == null) {
					ModelChange addColumnChange = new AddColumnChange(
							_newTable, targetColumn);
					changes.add(addColumnChange);
					log.info("Add column add change, new column is: "
							+ targetColumn.getName());
				}
			} else {
				// 1.Add ColumnDataTypeChange
				if (orgColumn != null
						&& targetColumn.getTypeCode() != orgColumn
								.getTypeCode()) {
					ModelChange columnDataTypeChange = new ColumnDataTypeChange(
							_newTable, orgColumn, targetColumn);
					changes.add(columnDataTypeChange);
					log.info("Add column data type change , new column is: "
							+ targetColumn.getName());

					// if (oldfield != null) {
					// oldfield.setName(targetColumn.getName());
					// }
				}

				// 2.Add ColumnRenameChange, if the name of two fields not the
				// same
				if (orgColumn != null
						&& !targetColumn.getName().equalsIgnoreCase(
								orgColumn.getName())) {
					ModelChange columnRenameChange = new ColumnRenameChange(
							_newTable, orgColumn, targetColumn);
					changes.add(columnRenameChange);
					log.info("Add column rename change, new name is: "
							+ targetColumn.getName());
				}
			}
		}
	}

	/**
	 * 在数据库中以field的类型及名称判断其唯一性
	 * 
	 * @param field
	 *            字段
	 * @param anField
	 *            另一个字段
	 * @return 是否相等
	 */
	public boolean isEqualField(FormField field, FormField anField) {
		if (field != null && anField != null) {
			return field.getName().equals(anField.getName())
					&& field.getFieldtype().equals(anField.getFieldtype());
		}

		return false;
	}

	/**
	 * 以旧表单中的域为基准,与新表单的域做比较,查出需要删除的域
	 * 
	 * @param oldFields
	 *            旧表单域的集合
	 * @param newForm
	 *            新表单
	 * @throws DuplicateException
	 */
	public void compareFieldsToDrop(Collection<FormField> oldFields,
			Form newForm) throws DuplicateException {
		if (_newTable == null) {
			return;
		}

		for (Iterator<FormField> iter = oldFields.iterator(); iter.hasNext();) {
			FormField oldField = iter.next();
			FormField newField = newForm.findField(oldField.getId());
			Column sourceColumn = createColumn(oldField);
			Column targetColumn = _newTable.findColumn(sourceColumn.getName());

			if (newField == null && targetColumn == null) {
				ModelChange dropColumnChange = new DropColumnChange(_newTable,
						sourceColumn);
				changes.add(dropColumnChange);
				log.info("Add column drop change, drop column is: "
						+ sourceColumn.getName());
			}
		}
	}

	/**
	 * @return the changes
	 * @uml.property name="changes"
	 */
	public Collection<ModelChange> getChanges() {
		return changes;
	}

	/**
	 * 获取table基本column
	 * 
	 * @return
	 */
	private Collection<Column> getFixedColumns() {
		Collection<Column> rtn = new ArrayList<Column>();
		// table column ID,primary key
		// Column id = new Column("", "ID", Types.VARCHAR);

		Column parent = new Column("", "PARENT", Types.VARCHAR);
		Column lastmodifield = new Column("", "LASTMODIFIED", Types.TIMESTAMP);
		Column formname = new Column("", "FORMNAME", Types.VARCHAR);
		Column owner = new Column("", "OWNER", Types.VARCHAR);
		Column state = new Column("", "STATE", Types.VARCHAR);
		Column audituser = new Column("", "AUDITUSER", Types.VARCHAR);
		Column auditdate = new Column("", "AUDITDATE", Types.TIMESTAMP);
		Column author = new Column("", "AUTHOR", Types.VARCHAR);
		Column authorDeptIndex = new Column("", "AUTHOR_DEPT_INDEX", Types.VARCHAR);
		Column created = new Column("", "CREATED", Types.TIMESTAMP);
		Column issubdoc = new Column("", "ISSUBDOC", Types.BIT);
		Column formid = new Column("", "FORMID", Types.VARCHAR);
		Column istmp = new Column("", "ISTMP", Types.BIT);
		Column flowid = new Column("", "FLOWID", Types.VARCHAR);
		Column versions = new Column("", "VERSIONS", Types.INTEGER);
		Column sortid = new Column("", "SORTID", Types.VARCHAR);
		Column applicationid = new Column("", "APPLICATIONID", Types.VARCHAR);
		Column stateint = new Column("", "STATEINT", Types.INTEGER);
		Column statelabel = new Column("", "STATELABEL", Types.VARCHAR);
		Column auditorNames = new Column("", "AUDITORNAMES", Types.CLOB);
		Column lastFlowOperation = new Column("", "LASTFLOWOPERATION",
				Types.VARCHAR);
		Column lastModifier = new Column("", "LASTMODIFIER", Types.VARCHAR);
		Column domainid = new Column("", "DOMAINID", Types.VARCHAR);
		Column auditorList = new Column("", "AUDITORLIST", Types.CLOB);
		

		rtn.add(parent);
		rtn.add(lastmodifield);
		rtn.add(formname);
		rtn.add(owner);
		rtn.add(state);
		rtn.add(audituser);
		rtn.add(auditdate);
		rtn.add(author);
		rtn.add(authorDeptIndex);
		rtn.add(created);
		rtn.add(issubdoc);
		rtn.add(formid);
		rtn.add(istmp);
		rtn.add(flowid);
		rtn.add(versions);
		rtn.add(sortid);
		rtn.add(applicationid);
		rtn.add(stateint);
		rtn.add(statelabel);
		rtn.add(auditorNames);
		rtn.add(lastFlowOperation);
		rtn.add(lastModifier);
		rtn.add(domainid);
		rtn.add(auditorList);
		return rtn;
	}

	/**
	 * 根据form filed的属性创建一个column对象
	 * 
	 * @param field
	 *            表单域
	 * @return
	 */
	public Column createColumn(FormField field) {
		if (field != null) {
			TableMapping tableMapping = field.get_form().getTableMapping();
			String columnName;
			if (field.get_form().getType() != Form.FORM_TYPE_NORMAL_MAPPING) {
				columnName = tableMapping.getColumnName(field.getName());
			} else {
				columnName = DQLASTUtil.ITEM_FIELD_PREFIX + field.getName();
			}

			String id = field.getId();
			int typeCode = FieldConstant.getTypeCode(field.getFieldtype());

			Column column = new Column(id, columnName, typeCode);
			column.setFieldName(field.getName());

			return column;
		}
		return null;
	}

	/**
	 * 根据form的属性创建一个table对象
	 * 
	 * @param form
	 *            表单
	 * @return
	 */
	public Table createTable(Form form) {
		if (form != null) {

			return createTableByType(form, DQLASTUtil.TABEL_TYPE_CONTENT);
		}
		return null;
	}

	/**
	 * 根据form的属性创建一个table对象
	 * 
	 * @param form
	 *            表单
	 * @return
	 */
	public Table createTableByType(Form form, int tableType) {
		if (form != null) {
			Table table = new Table(getTableName(form, tableType));
			table.setFormName(form.getName());

			if (tableType == DQLASTUtil.TABLE_TYPE_AUTH) {
				Column id = new Column("", "ID", Types.VARCHAR);
				id.setPrimaryKey(true);// 设为主键
				Column docId = new Column("", "DOC_ID", Types.VARCHAR);
				Column value = new Column("", "VALUE", Types.VARCHAR);

				table.getColumns().add(id);
				table.getColumns().add(docId);
				table.getColumns().add(value);

			} else {
				TableMapping tableMapping = form.getTableMapping();
				table.getColumns().addAll(getFixedColumns());

				if (tableType == DQLASTUtil.TABEL_TYPE_LOG) {
					Column docid = new Column("", "DOC_ID", Types.VARCHAR);
					table.getColumns().add(docid);
				}

				for (Iterator<FormField> iter = form.getValueStoreFields()
						.iterator(); iter.hasNext();) {
					FormField field = iter.next();
					table.getColumns().add(createColumn(field));
				}

				// 添加数据库表主键
				Column primaryKey = new Column("", tableMapping
						.getPrimaryKeyName(), Types.VARCHAR);
				primaryKey.setPrimaryKey(true);
				table.getColumns().add(primaryKey);

			}
			return table;
		}
		return null;
	}

	/**
	 * 获取表格名称
	 * 
	 * @param form
	 *            表单
	 * @param tableType
	 *            数据库表类型
	 * @return
	 */
	private String getTableName(Form form, int tableType) {
		TableMapping tableMapping = form.getTableMapping();
		return tableMapping.getTableName(tableType);
	}

	/**
	 * 获取数据库表
	 * 
	 * @param form
	 * @param tableType
	 * @return
	 * @throws Exception
	 */
	public Table getDBTable(Form form, int tableType) throws Exception {
		if (form != null) {
			String tableName = getTableName(form, tableType);
			Table table = DbTypeUtil.getTable(tableName, form
					.getApplicationid());
			if (table != null) {
				table.setFormName(form.getName());
				return table;
			}
		}

		return null;
	}

	/**
	 * 2.6新增
	 * 
	 * 根据给定数据源,获取数据库表
	 * 
	 * @param form
	 * @param tableType
	 * @return
	 * @throws Exception
	 */
	public Table getDBTable(Form form, DataSource dt, int tableType)
			throws Exception {
		if (form != null) {
			String tableName = getTableName(form, tableType);
			Table table = DbTypeUtil.getTable(tableName, dt);
			if (table != null) {
				table.setFormName(form.getName());
				return table;
			}
		}

		return null;
	}
}
