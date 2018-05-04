package OLink.bpm.core.dynaform.view.ejb.type;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.view.ejb.Column;
import OLink.bpm.core.dynaform.view.ejb.EditMode;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.dynaform.view.ejb.ViewType;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.cache.MemoryCacheUtil;
import OLink.bpm.core.dynaform.document.ejb.DocumentProcess;

public class TreeType extends AbstractType implements ViewType {

	/**
	 * 默认必须的字段
	 */
	public final static String[] DEFAULT_KEY_FIELDS = { "superior_Node", "current_Node", "name_Node" };

	/**
	 * 所有的字段
	 */
	public final static List<String> ALL_KEY_FIELDS = new ArrayList<String>();
	static {
		for (int i = 0; i < DEFAULT_KEY_FIELDS.length; i++)
			ALL_KEY_FIELDS.add(DEFAULT_KEY_FIELDS[i]);
	}

	public final static Map<String, String> DEFAULT_FIELDS = new LinkedHashMap<String, String>();
	public final static Map<String, String> ALL_FIELDS = new LinkedHashMap<String, String>();

	static {
		DEFAULT_FIELDS.put(DEFAULT_KEY_FIELDS[0], "{*[Superior]*}{*[Node]*}{*[ID]*}");
		DEFAULT_FIELDS.put(DEFAULT_KEY_FIELDS[1], "{*[Current]*}{*[Node]*}{*[ID]*}");
		DEFAULT_FIELDS.put(DEFAULT_KEY_FIELDS[2], "{*[Current]*}{*[Node]*}{*[Name]*}");
		ALL_FIELDS.putAll(DEFAULT_FIELDS);
	}

	public TreeType(View view) {
		super(view);
	}

	protected void addConditionToMode(EditMode editMode, WebUser user, ParamsTable params) throws Exception {
		DocumentProcess dp = (DocumentProcess) ProcessFactory.createRuntimeProcess(DocumentProcess.class,view.getApplicationid());

		// 获取参数
		String parentid = params.getParameterAsString("treedocid");
		Document doc = (Document) MemoryCacheUtil.getFromPrivateSpace(parentid, user);
		// 从数据库中获取Document
		if (doc == null) {
			doc = (Document) dp.doView(parentid);
		}

		Map<String, Column> mapFormFields = getColumnMapping();// 获取映射的表单字段集
		String value = doc != null ? doc.getValueByField(mapFormFields.get(DEFAULT_KEY_FIELDS[1])
				.getFieldName()) : "";

		// String value = doc != null ?
		// doc.getValueByField(view.getNodeValueField()) : "";
		// 添加关联条件
		editMode.addCondition(mapFormFields.get(DEFAULT_KEY_FIELDS[0]).getFieldName(), value);
		// editMode.addCondition(view.getTreeRelationField(), value);
	}

	public int intValue() {
		return View.VIEW_TYPE_TREE;
	}

	/**
	 * 添加视图字段
	 * 
	 * @param key_Field
	 *            字段(ID)
	 * @param name_Field
	 *            字段名称
	 * @return boolean
	 */
	public static boolean addField(String key_Field, String name_Field) {
		if (ALL_FIELDS.containsKey(key_Field))
			return false;
		ALL_FIELDS.putAll(DEFAULT_FIELDS);
		ALL_KEY_FIELDS.add(key_Field);
		ALL_FIELDS.put(key_Field, name_Field);
		return true;
	}

	/**
	 * 获取TREE视图关联的column集并映射为一个map key = mappingField value = name
	 */
	public Map<String, String> getColumnMapName() {
		Map<String, String> columnMapping = new LinkedHashMap<String, String>();
		if (view != null) {
			Iterator<Column> columns = view.getColumns().iterator();
			while (columns.hasNext()) {
				Column column = columns.next();
				columnMapping.put(column.getMappingField(), column.getName());
			}
		}
		return columnMapping;
	}

	/**
	 * 获取TREE视图关联的column集并映射为一个map key = mappingField value = fieldName
	 */
	public Map<String, Column> getColumnMapping() {
		Map<String, Column> columnMapping = new LinkedHashMap<String, Column>();
		if (view != null) {
			Iterator<Column> columns = view.getColumns().iterator();
			while (columns.hasNext()) {
				Column column = columns.next();
				columnMapping.put(column.getMappingField(), column);
			}
		}
		return columnMapping;
	}

	public DataPackage<Document> getViewDatas(ParamsTable params, int page, int lines, WebUser user, Document sdoc)
			throws Exception {
		EditMode editMode = view.getEditModeType();
		addConditionToMode(editMode, user, params);
		return editMode.getDataPackage(params, user, sdoc);
	}
}
