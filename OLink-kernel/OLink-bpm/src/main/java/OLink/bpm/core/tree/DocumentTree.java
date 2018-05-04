package OLink.bpm.core.tree;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.form.ejb.ValidateMessage;
import OLink.bpm.core.dynaform.view.ejb.Column;
import OLink.bpm.core.dynaform.view.ejb.EditMode;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.dynaform.view.ejb.type.TreeType;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.macro.runner.JavaScriptFactory;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.StringUtil;

/**
 * 文档树
 * 
 * @author nicholas zhen
 * 
 */
public class DocumentTree extends Tree<Document> {
	private View view;

	private ParamsTable params;

	private WebUser user;

	private Document searchDoc;

	private IRunner jsrun;

	public DocumentTree(View view, ParamsTable params, WebUser user,
			Document searchDoc) {
		this.view = view;
		this.params = params;
		this.user = user;
		this.searchDoc = searchDoc;

		jsrun = JavaScriptFactory.getInstance(params.getSessionid(), view
				.getApplicationid());
	}

	/**
	 * 解析文档集合，生成树节点
	 * 
	 * @param docs
	 */
	public void parse(Collection<Document> docs) {
		if (docs != null && !docs.isEmpty()) {
			try {
				Map<String, Column> columnMapFields = view.getViewTypeImpl()
						.getColumnMapping();
				String relationFieldName = (columnMapFields
						.get(TreeType.DEFAULT_KEY_FIELDS[0])).getFieldName();
				// String relationFieldName = view.getTreeRelationField();

				for (Iterator<Document> iterator = docs.iterator(); iterator
						.hasNext();) {
					EditMode editMode = view.getEditModeType();
					Document doc = iterator.next();

					Node node = createNode(doc);

					params.setParameter("parentid", doc.getId());
					user.putToTmpspace(doc.getId(), doc);

					String value = doc
							.getValueByField(columnMapFields
									.get(TreeType.DEFAULT_KEY_FIELDS[1])
									.getFieldName()); // 当前节点ID
					// String value =
					// doc.getValueByField(view.getNodeValueField()); // 当前节点ID
					editMode.addCondition(relationFieldName, value); // 树形关系
					if (editMode.count(params, user, searchDoc) > 0) { // 判断是否有子节点
						node.setState(Node.STATE_CLOSED);
					}
					childNodes.add(node);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 查找节点集合
	 */
	public void search() {
		try {
			Map<String, Column> columnMapFields = view.getViewTypeImpl()
					.getColumnMapping();
			String nodeNameField = columnMapFields
					.get(TreeType.DEFAULT_KEY_FIELDS[2]).getFieldName();// 树节点名称
			// String nodeNameField = view.getNodeNameField(); // 树节点名称
			EditMode editMode = view.getEditModeType();

			// 根据输入节点名称模糊查询节点
			editMode.addCondition(nodeNameField, "%"
					+ params.getParameterAsString(nodeNameField) + "%", "like");
			DataPackage<Document> dataPackage = editMode.getDataPackage(params,
					user, searchDoc);

			if (dataPackage.rowCount > 0) {
				for (Iterator<Document> iterator = dataPackage.datas.iterator(); iterator
						.hasNext();) {
					Document doc = iterator.next();
					deepSearchParent(doc);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 递归查找上级节点
	 * 
	 * @param child
	 *            子节点
	 * @return
	 * @throws Exception
	 */
	public void deepSearchParent(Document child) throws Exception {
		EditMode editMode = view.getEditModeType();
		Map<String, Column> columnMapFields = view.getViewTypeImpl()
				.getColumnMapping();
		String relationField = columnMapFields
				.get(TreeType.DEFAULT_KEY_FIELDS[0]).getFieldName(); // 上级树节点
		String valueField = columnMapFields
				.get(TreeType.DEFAULT_KEY_FIELDS[1]).getFieldName(); // 当前树节点ID
		// String relationField = view.getTreeRelationField(); // 树节点名称
		// String valueField = view.getNodeValueField(); // 树节点ID

		String value = child.getItemValueAsString(relationField); // 父节点ID值
		if (!StringUtil.isBlank(value)) {
			editMode.addCondition(valueField, value);

			DataPackage<Document> dataPackage = editMode.getDataPackage(params,
					user, searchDoc);
			if (dataPackage.rowCount > 0) {
				for (Iterator<Document> iterator = dataPackage.datas.iterator(); iterator
						.hasNext();) {
					Document doc = iterator.next();
					deepSearchParent(doc);

					searchNodes.add("#" + doc.getId());
				}
			}
		}
	}

	/**
	 * 创建树节点
	 * 
	 * @param doc
	 * @param jsrun
	 * @return
	 * @throws Exception
	 */
	protected Node createNode(Document doc) throws Exception {
		jsrun.initBSFManager(doc, params, user, new HashSet<ValidateMessage>());
		Collection<Column> columns = view.getColumns();
		StringBuffer valuesMap = new StringBuffer("{");
		Iterator<Column> it = columns.iterator();
		while (it.hasNext()) {
			Column key = it.next();
			Object value = key.getText(doc, jsrun, user);
			valuesMap.append("'").append(key.getId()).append("':'").append(
					StringUtil.encodeHTML(value.toString())).append("',");
		}
		valuesMap.setLength(valuesMap.length() - 1);
		valuesMap.append("}");

		Node node = new Node();
		node.setId(doc.getId());

		// 获取表单字段与tree字段映射的column关系图
		Map<String, Column> columnMapFields = view.getViewTypeImpl()
				.getColumnMapping();
		// 当前树节点名称
		String text = doc.getValueByField(columnMapFields
				.get(TreeType.DEFAULT_KEY_FIELDS[2]).getFieldName());
		// 当前树节点(ID)
		String value = doc.getValueByField(columnMapFields
				.get(TreeType.DEFAULT_KEY_FIELDS[1]).getFieldName());

		// String text = doc.getValueByField(view.getNodeNameField());
		// String value = doc.getValueByField(view.getNodeValueField());

		node.setData(text);// 节点名称
		if (!StringUtil.isBlank(doc.getFormid())) {
			node.addAttr("formid", doc.getFormid());
		}
		node.addAttr("nodeValue", value); // 节点值
		node.addAttr("valuesMap", valuesMap.toString());

		return node;
	}
}
